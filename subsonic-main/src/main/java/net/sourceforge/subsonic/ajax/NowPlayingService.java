/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.ajax;

import static java.util.Arrays.asList;
import static net.sourceforge.subsonic.domain.AvatarScheme.CUSTOM;
import static net.sourceforge.subsonic.util.StringUtil.toHtml;
import static net.sourceforge.subsonic.util.StringUtil.utf8HexEncode;
import static org.apache.commons.lang.StringUtils.abbreviate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.AvatarScheme;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MetaData;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.StatusService;
import net.sourceforge.subsonic.util.StringUtil;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.github.hakko.musiccabinet.domain.model.music.AlbumInfo;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.lastfm.AlbumInfoService;

/**
 * Provides AJAX-enabled services for retrieving the currently playing file and directory.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class NowPlayingService {

    private PlayerService playerService;
    private StatusService statusService;
    private MediaFileService mediaFileService;
    private SettingsService settingsService;

    private AlbumInfoService albumInfoService;
    private LibraryBrowserService libraryBrowserService;

    private Map<String, NowPlayingInfo> lastPlayingInfo = new HashMap<String, NowPlayingInfo>();
    
    private final static Logger LOG = Logger.getLogger(NowPlayingService.class);
    
    /**
     * Returns details about what the current player is playing.
     *
     * @return Details about what the current player is playing, or <code>null</code> if not playing anything.
     */
    public NowPlayingInfo getNowPlayingForCurrentPlayer() throws Exception {
        WebContext webContext = WebContextFactory.get();
        Player player = playerService.getPlayer(webContext.getHttpServletRequest(), webContext.getHttpServletResponse());
        List<TransferStatus> statuses = statusService.getStreamStatusesForPlayer(player);
        List<NowPlayingInfo> result = convert(statuses);

        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Returns details about what all users are currently playing.
     *
     * @return Details about what all users are currently playing.
     */
    public List<NowPlayingInfo> getNowPlaying() throws Exception {
    	return convert(statusService.getAllStreamStatuses());
    }

    private List<NowPlayingInfo> convert(List<TransferStatus> statuses) throws Exception {
        List<NowPlayingInfo> result = new ArrayList<NowPlayingInfo>();
        for (TransferStatus status : statuses) {

            Player player = status.getPlayer();
            int mediaFileId = status.getMediaFileId();

            if (status.getMillisSinceLastUpdate() / 1000L / 60L < 60 && 
            		player != null && player.getUsername() != null) {
            	
                String username = player.getUsername();
                UserSettings userSettings = settingsService.getUserSettings(username);
                if (!userSettings.isNowPlayingAllowed()) {
                    continue;
                }
                
                NowPlayingInfo npi;
                if ((npi = lastPlayingInfo.get(player.getId())) == null ||
                		npi.getMediaFileId() != mediaFileId) {
                	MediaFile mediaFile = mediaFileService.getMediaFile(mediaFileId);
                    Artwork artwork = getArtwork(mediaFile, settingsService.isPreferLastFmArtwork());
					lastPlayingInfo.put(player.getId(), npi = 
							getNowPlayingInfo(username, userSettings, mediaFile, artwork));
                }
                result.add(npi);
            }
        }

        return result;
    }

    private NowPlayingInfo getNowPlayingInfo(String username, UserSettings userSettings, MediaFile mediaFile, Artwork artwork) {
    	MetaData md = mediaFile.getMetaData();
    	String artist = md.getArtist();
    	String title = md.getTitle();
    	String albumUrl = "artist.view?id=" + md.getArtistId() + "&albumId=" + md.getAlbumId()
    			+ "&trackId=" + mediaFile.getId();
    	String lyricsUrl = "lyrics.view?artistUtf8Hex=" + utf8HexEncode(md.getArtist()) +
    			"&songUtf8Hex=" + utf8HexEncode(md.getTitle());

    	String avatarUrl = null;
    	if (userSettings.getAvatarScheme() == AvatarScheme.SYSTEM) {
    		avatarUrl = "avatar.view?id=" + userSettings.getSystemAvatarId();
    	} else if (userSettings.getAvatarScheme() == CUSTOM && settingsService.getCustomAvatar(username) != null) {
    		avatarUrl = "avatar.view?username=" + username;
    	}

    	String tooltip = StringUtil.toHtml(artist) + " &ndash; " + StringUtil.toHtml(title);
    	artist = toHtml(abbreviate(artist, 25));
    	title = toHtml(abbreviate(title, 25));
    	username = toHtml(abbreviate(username, 25));
    	return new NowPlayingInfo(mediaFile.getId(), username, artist, title, tooltip, 
    			albumUrl, lyricsUrl, artwork.imageUrl, artwork.zoomImageUrl, avatarUrl);
    }
    
    private Artwork getArtwork(MediaFile mediaFile, boolean preferLastFm) {
    	int albumId = mediaFile.getMetaData().getAlbumId();
    	if (preferLastFm) {
    		AlbumInfo albumInfo = albumInfoService.getAlbumInfosForAlbumIds(asList(albumId)).get(albumId);
    		if (albumInfo != null) {
    			return new Artwork(
    					albumInfo.getMediumImageUrl(), 
    					albumInfo.getExtraLargeImageUrl());
    		}
    	}
    	String path = libraryBrowserService.getCoverArtFileForTrack(mediaFile.getId());
    	if (path != null) {
            return new Artwork(
            		"coverArt.view?size=64&pathUtf8Hex=" + utf8HexEncode(path),
            		"coverArt.view?pathUtf8Hex=" + utf8HexEncode(path));
    	}
        return new Artwork();
    }
    
    final class Artwork {
    	protected String imageUrl;
    	protected String zoomImageUrl;

    	public Artwork(String imageUrl, String zoomImageUrl) {
			this.imageUrl = imageUrl;
			this.zoomImageUrl = zoomImageUrl;
		}
    	
    	public Artwork() {}
    }
    
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setmediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public void setAlbumInfoService(AlbumInfoService albumInfoService) {
    	this.albumInfoService = albumInfoService;
    }

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

}