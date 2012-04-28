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
import net.sourceforge.subsonic.domain.AvatarScheme;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.StatusService;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.github.hakko.musiccabinet.domain.model.music.AlbumInfo;
import com.github.hakko.musiccabinet.service.AlbumInfoService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides AJAX-enabled services for retrieving the currently playing file and directory.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class NowPlayingService {

    private PlayerService playerService;
    private StatusService statusService;
    private MusicFileService musicFileService;
    private SettingsService settingsService;
    private AlbumInfoService albumInfoService;

    private Map<String, NowPlayingInfo> lastPlayingInfo = new HashMap<String, NowPlayingInfo>();
    
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
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String url = request.getRequestURL().toString();
        List<NowPlayingInfo> result = new ArrayList<NowPlayingInfo>();
        for (TransferStatus status : statuses) {

            Player player = status.getPlayer();
            File file = status.getFile();

            if (status.getMillisSinceLastUpdate() / 1000L / 60L < 60 && 
            		player != null && player.getUsername() != null && file != null) {
            	
                String username = player.getUsername();
                UserSettings userSettings = settingsService.getUserSettings(username);
                if (!userSettings.isNowPlayingAllowed()) {
                    continue;
                }

                NowPlayingInfo npi;
                if ((npi = lastPlayingInfo.get(player.getId())) == null ||
                		!npi.getFile().equals(file)) {

                	MusicFile musicFile = musicFileService.getMusicFile(file);
                	Artwork artwork = getArtwork(musicFile, url);
                	String coverArtUrl = artwork.imageUrl;
                	String coverArtZoomUrl = artwork.zoomImageUrl;

                	String artist = musicFile.getMetaData().getArtist();
                	String title = musicFile.getMetaData().getTitle();
                	String streamUrl = url.replaceFirst("/dwr/.*", "/stream?player=" + player.getId() + "&pathUtf8Hex=" +
                			StringUtil.utf8HexEncode(file.getPath()));
                	String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?pathUtf8Hex=" +
                			StringUtil.utf8HexEncode(musicFile.getParent().getPath()));
                	String lyricsUrl = url.replaceFirst("/dwr/.*", "/lyrics.view?artistUtf8Hex=" +
                			StringUtil.utf8HexEncode(musicFile.getMetaData().getArtist()) +
                			"&songUtf8Hex=" +
                			StringUtil.utf8HexEncode(musicFile.getMetaData().getTitle()));

                	String avatarUrl = null;
                	if (userSettings.getAvatarScheme() == AvatarScheme.SYSTEM) {
                		avatarUrl = url.replaceFirst("/dwr/.*", "/avatar.view?id=" + userSettings.getSystemAvatarId());
                	} else
                		if (userSettings.getAvatarScheme() == AvatarScheme.CUSTOM && settingsService.getCustomAvatar(username) != null) {
                			avatarUrl = url.replaceFirst("/dwr/.*", "/avatar.view?username=" + username);
                		}

                	// Rewrite URLs in case we're behind a proxy.
                	if (settingsService.isRewriteUrlEnabled()) {
                		String referer = request.getHeader("referer");
                		streamUrl = StringUtil.rewriteUrl(streamUrl, referer);
                		albumUrl = StringUtil.rewriteUrl(albumUrl, referer);
                		lyricsUrl = StringUtil.rewriteUrl(lyricsUrl, referer);
                		if (artwork.isLocal) {
                			coverArtUrl = StringUtil.rewriteUrl(coverArtUrl, referer);
                			coverArtZoomUrl = StringUtil.rewriteUrl(coverArtZoomUrl, referer);
                		}
                		avatarUrl = StringUtil.rewriteUrl(avatarUrl, referer);
                	}

                	String tooltip = StringUtil.toHtml(artist) + " &ndash; " + StringUtil.toHtml(title);

                	if (StringUtils.isNotBlank(player.getName())) {
                		username += "@" + player.getName();
                	}
                	artist = StringUtil.toHtml(StringUtils.abbreviate(artist, 25));
                	title = StringUtil.toHtml(StringUtils.abbreviate(title, 25));
                	username = StringUtil.toHtml(StringUtils.abbreviate(username, 25));

					lastPlayingInfo.put(player.getId(),
							npi = new NowPlayingInfo(file, username, artist, title,
									tooltip, streamUrl, albumUrl, lyricsUrl,
									coverArtUrl, coverArtZoomUrl, avatarUrl));
                }
                result.add(npi);
            }
        }

        return result;

    }

    private Artwork getArtwork(MusicFile musicFile, String url) throws IOException {
    	Artwork artwork = new Artwork();
    	Map<String, AlbumInfo> map = albumInfoService.getAlbumInfosForPaths(
    			asList(musicFile.getParent().getPath()));
		AlbumInfo albumInfo = map.get(musicFile.getParent().getPath());
		if (albumInfo != null) {
			artwork.imageUrl = albumInfo.getMediumImageUrl();
			artwork.zoomImageUrl = albumInfo.getExtraLargeImageUrl() != null ? 
				albumInfo.getExtraLargeImageUrl() : albumInfo.getLargeImageUrl();
		}
    	if (artwork.imageUrl == null) {
    		artwork.isLocal = true;
            File coverArt = musicFileService.getCoverArt(musicFile.getParent());
            artwork.imageUrl = coverArt == null ? null : url.replaceFirst("/dwr/.*",
                "/coverArt.view?size=64&pathUtf8Hex=" + StringUtil.utf8HexEncode(coverArt.getPath()));
            artwork.zoomImageUrl = coverArt == null ? null : url.replaceFirst("/dwr/.*",
                "/coverArt.view?pathUtf8Hex=" + StringUtil.utf8HexEncode(coverArt.getPath()));
    	}
    	
        return artwork;
    }
    
    private class Artwork {
    	protected String imageUrl;
    	protected String zoomImageUrl;
    	protected boolean isLocal;
    }
    
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public void setAlbumInfoService(AlbumInfoService albumInfoService) {
    	this.albumInfoService = albumInfoService;
    }
}
