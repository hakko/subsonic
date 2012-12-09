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
package net.sourceforge.subsonic.controller;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.ArrayUtils.isEmpty;
import static org.apache.commons.lang.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.upperCase;
import static org.apache.commons.lang.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Album;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.exception.ApplicationException;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.StarService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistTopTagsService;

/**
 * Controller for the artist page.
 */
public class ArtistController extends ParameterizableViewController {

    private SecurityService securityService;
    private PlayerService playerService;
    private SettingsService settingsService;
	private ArtistInfoService artistInfoService;
	private ArtistTopTagsService artistTopTagsService;
	private LibraryBrowserService libraryBrowserService;
	private MediaFileService mediaFileService;
	private StarService starService;
	
	private static final Logger LOG = Logger.getLogger(ArtistController.class);
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Player player = playerService.getPlayer(request, response);
        
        int artistId = NumberUtils.toInt(request.getParameter("id"), -1);
        
        String[] albumIds = request.getParameterValues("albumId");

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

        boolean variousArtists = isVariousArtists(setArtistInfo(artistId, map));
        setAlbums(artistId, variousArtists, userSettings, map, albumIds);

        map.put("trackId", request.getParameter("trackId"));
        map.put("artistStarred", starService.isArtistStarred(userSettings.getLastFmUsername(), artistId));
        map.put("topTags", artistTopTagsService.getTopTags(artistId, 3));
        map.put("visibility", userSettings.getMainVisibility());
        map.put("player", player);
        map.put("user", user);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private void setAlbums(int artistId, boolean variousArtists, UserSettings userSettings, 
    		Map<String, Object> map, String[] selectedAlbumIds) {
        List<Album> albums = mediaFileService.getAlbums(
        		variousArtists && isNotEmpty(selectedAlbumIds) ?
        		asList(libraryBrowserService.getAlbum(toInt(selectedAlbumIds[0]))) :
        		libraryBrowserService.getAlbums(artistId,
        		userSettings.isAlbumOrderByYear(), userSettings.isAlbumOrderAscending()));
        List<Integer> albumIds = new ArrayList<>();
        List<Integer> trackIds = new ArrayList<>();
        
        for (Album album : albums) {
        	if (album.getArtistId() == artistId) {
        		album.setArtistName(null);
        	}
        	albumIds.add(album.getId());
        	trackIds.addAll(album.getTrackIds());
        }
        
        if (albums.size() > 3) {
        	map.put("artistInfoMinimized", true);
        	map.put("artistInfoImageSize", 63);
        } else {
        	if (albums.size() == 1) {
        		albums.get(0).setSelected(true);
        	}
        	map.put("artistInfoImageSize", 126);
        }
    	for (int i = 0; selectedAlbumIds != null && i < selectedAlbumIds.length; i++) {
    		for (Album album : albums) {
    			if (album.getId() == NumberUtils.toInt(selectedAlbumIds[i], -1)) {
    				album.setSelected(true);
    			}
    		}
        }

    	map.put("isAlbumStarred", starService.getStarredAlbumsMask(userSettings.getLastFmUsername(), albumIds));
        map.put("albums", albums);
        map.put("trackIds", trackIds);
        map.put("coverArtSize", 87);
	}

	private ArtistInfo setArtistInfo(int artistId, Map<String, Object> map) throws ApplicationException {
        ArtistInfo artistInfo = artistInfoService.getArtistInfo(artistId);
        map.put("artistId", artistId);
        map.put("artistName", artistInfo.getArtist().getName());
        map.put("isInSearchIndex", artistInfo.isInSearchIndex() && !isVariousArtists(artistInfo));
        if (artistInfo.getLargeImageUrl() != null && artistInfo.getBioSummary() != null) {
        	map.put("artistInfo", Util.square(artistInfo));
        	boolean insideTag = false;
        	String bio = artistInfo.getBioSummary();
        	for (int i = 0; i < bio.length(); i++) {
        		if (bio.charAt(i) == '<') {
        			insideTag = true;
        		} else if (bio.charAt(i) == '>') {
        			insideTag = false;
        		} else if (bio.charAt(i) == '.' && !insideTag) {
        			map.put("artistInfoFirstSentence", bio.substring(0, i+1));
        			break;
        		}
        	}
        }
        return artistInfo;
    }
	
	private boolean isVariousArtists(ArtistInfo artistInfo) {
		String artistName = upperCase(artistInfo.getArtist().getName());
		return "VARIOUS ARTISTS".equals(artistName) || "VA".equals(artistName);
	}
    
    // Spring setters

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setArtistInfoService(ArtistInfoService artistInfoService) {
		this.artistInfoService = artistInfoService;
	}

	public void setArtistTopTagsService(ArtistTopTagsService artistTopTagsService) {
		this.artistTopTagsService = artistTopTagsService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

	public void setMediaFileService(MediaFileService mediaFileService) {
		this.mediaFileService = mediaFileService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}
    
}