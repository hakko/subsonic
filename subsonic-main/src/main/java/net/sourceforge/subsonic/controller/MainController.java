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

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFileInfo;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicFileService.Album;
import net.sourceforge.subsonic.service.MusicInfoService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import com.github.hakko.musiccabinet.domain.model.music.AlbumInfo;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.exception.ApplicationException;
import com.github.hakko.musiccabinet.service.AlbumInfoService;
import com.github.hakko.musiccabinet.service.ArtistInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for the main page.
 *
 * @author Sindre Mehus
 */
public class MainController extends ParameterizableViewController {

    private SecurityService securityService;
    private PlayerService playerService;
    private SettingsService settingsService;
    private MusicInfoService musicInfoService;
    private MusicFileService musicFileService;

    private SearchService searchService;
	private ArtistInfoService artistInfoService;
	private AlbumInfoService albumInfoService;
	
	private static final Logger LOG = Logger.getLogger(MainController.class);
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Player player = playerService.getPlayer(request, response);
        String[] paths = request.getParameterValues("path");
        String path = paths[0];
        MusicFile dir = musicFileService.getMusicFile(path);
        while (!dir.getParent().isRoot()) {
        	dir = dir.getParent();
        }

        // Redirect if root directory.
        if (dir.isRoot()) {
            return new ModelAndView(new RedirectView("home.view?"));
        }

        String userName = securityService.getCurrentUsername(request);
        setArtistInfo(path, map);
        List<Album> albums = musicFileService.getAlbums(dir, paths);
        setAlbumArtwork(albums);
        setAlbumRating(albums, userName);
        
        UserSettings userSettings = settingsService.getUserSettings(userName);
        map.put("dir", dir);
        map.put("isArtist", dir.getParent().isRoot());
        map.put("artist", guessArtist(dir, albums));
        map.put("albums", albums);
        map.put("coverArtSize", 87);
        map.put("player", player);
        map.put("user", securityService.getCurrentUser(request));
        map.put("multipleArtists", isMultipleArtists(albums));
        map.put("visibility", userSettings.getMainVisibility());
        map.put("updateNowPlaying", request.getParameter("updateNowPlaying") != null);
        map.put("partyMode", userSettings.isPartyModeEnabled());
        if (albums.size() > 3) {
        	map.put("artistInfoMinimized", true);
        	map.put("artistInfoImageSize", 63);
        } else {
        	map.put("artistInfoImageSize", 126);
        }
        if (!userSettings.isAlbumOrderAscending()) {
        	Collections.reverse(albums);
        }

        MusicFileInfo musicInfo = musicInfoService.getMusicFileInfoForPath(dir.getPath());
        String comment = musicInfo == null ? null : musicInfo.getComment();
        map.put("comment", comment);

        if (searchService.hasMusicCabinetIndex()) {
        	map.put("isMusicCabinetReady", true);
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private void setArtistInfo(String path, Map<String, Object> map) throws ApplicationException {
        ArtistInfo artistInfo = artistInfoService.getArtistInfo(path);
        if (artistInfo != null) {
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
    }
    
    private void setAlbumArtwork(List<Album> albums) {
    	Map<String, Album> dirMap = new HashMap<String, Album>();
    	for (Album album : albums) {
    		dirMap.put(album.getDirectory().getPath(), album);
    	}
    	Map<String, AlbumInfo> infoMap = albumInfoService.getAlbumInfosForPaths(
    			new ArrayList<String>(dirMap.keySet()));
    	for (String path : infoMap.keySet()) {
    		dirMap.get(path).setCoverArtUrl(infoMap.get(path).getLargeImageUrl());
    		dirMap.get(path).setCoverArtZoomUrl(infoMap.get(path));
    	}
    	for (Album album : albums) {
    		if (album.getCoverArtUrl() == null) {
    			album.setCoverArt(musicFileService.getArtwork(
    					album.getDirectory(), album.getMusicFiles()));
    		}    		
    	}
    }
    
    private void setAlbumRating(List<Album> albums, String username) {
    	for (Album album : albums) {
            Integer userRating = musicInfoService.getRatingForUser(username, album.getDirectory());
            album.setUserRating(userRating == null ? 0 : 10 * userRating);
    	}
    }
    
    private String guessArtist(MusicFile dir, List<Album> albums) throws IOException {
    	for (Album album : albums) {
    		for (MusicFile musicFile : album.getMusicFiles()) {
    	        MusicFile.MetaData md = musicFile.getMetaData();
    	        if (md != null) {
    	        	return md.getAlbumArtist() != null ? md.getAlbumArtist() : md.getArtist();
    	        }
    		}
    	}
        return dir.getName();
    }

    private boolean isMultipleArtists(List<Album> albums) {
        // Collect unique artist names.
        Set<String> artists = new HashSet<String>();
        for (Album album : albums) {
        	for (MusicFile child : album.getMusicFiles()) {
        		MusicFile.MetaData metaData = child.getMetaData();
        		if (metaData != null && metaData.getArtist() != null) {
        			artists.add(metaData.getArtist().toLowerCase());
        		}
        	}
        }

        // If zero or one artist, it is definitely not multiple artists.
        if (artists.size() < 2) {
            return false;
        }

        // Fuzzily compare artist names, allowing for some differences in spelling, whitespace etc.
        List<String> artistList = new ArrayList<String>(artists);
        for (String artist : artistList) {
            if (StringUtils.getLevenshteinDistance(artist, artistList.get(0)) > 3) {
                return true;
            }
        }
        return false;
    }
    
    // Spring setters
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSearchService(SearchService searchService) {
    	this.searchService = searchService;
    }
    
    public void setArtistInfoService(ArtistInfoService artistInfoService) {
    	this.artistInfoService = artistInfoService;
    }
    
    public void setAlbumInfoService(AlbumInfoService albumInfoService) {
    	this.albumInfoService = albumInfoService;
    }
    
}
