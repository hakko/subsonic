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

import static java.net.URLEncoder.encode;
import static net.sourceforge.subsonic.util.StringUtil.ENCODING_UTF8;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.math.NumberUtils.toInt;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Album;
import net.sourceforge.subsonic.domain.ArtistLink;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.library.LastFmUser;
import com.github.hakko.musiccabinet.domain.model.library.Period;
import com.github.hakko.musiccabinet.service.ArtistRecommendationService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;
import com.github.hakko.musiccabinet.service.StarService;
import com.github.hakko.musiccabinet.service.lastfm.UserTopArtistsService;

/**
 * Controller for the home page.
 */
public class HomeController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(HomeController.class);

    private SettingsService settingsService;
    private SecurityService securityService;
    private UserTopArtistsService userTopArtistsService;
    private ArtistRecommendationService artistRecommendationService;
    private LibraryUpdateService libraryUpdateService;
    private LibraryBrowserService libraryBrowserService;
    private MediaFileService mediaFileService;
    private StarService starService;
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
        Map<String, Object> map = new HashMap<String, Object>();

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        if (user.isAdminRole() && settingsService.isGettingStartedEnabled()) {
            return new ModelAndView(new RedirectView("gettingStarted.view"));
        }

        if (!libraryBrowserService.hasArtists()) {
        	return new ModelAndView(new RedirectView("settings.view"));
        }
        
        String listType = request.getParameter("listType");
        String listGroup = request.getParameter("listGroup");
    	String query = StringUtils.trimToNull(request.getParameter("query"));
    	int page = "random".equals(listType) ? 0 : toInt(request.getParameter("page"), 0);

    	if (listType == null) {
            String defaultHomeView = userSettings.getDefaultHomeView();
            if (defaultHomeView != null) {
            	int index;
            	if ((index = defaultHomeView.indexOf('+')) != -1) {
            		listType = defaultHomeView.substring(0, index);
            		listGroup = defaultHomeView.substring(index + 1);
            	} else {
            		listType = defaultHomeView;
            	}
            } else {
                listType = "newest";
            }
        }
        if (listGroup == null) {
        	listGroup = "topartists".equals(listType) ? "3month" : "Albums";
        }

        if ("topartists".equals(listType) || "recommended".equals(listType) || "Artists".equals(listGroup)) {
        	setArtists(listType, listGroup, query, page, userSettings, map);
        } else if ("newest".equals(listType) || "Albums".equals(listGroup)) {
        	setAlbums(listType, query, page, userSettings, map);
        } else if ("Songs".equals(listGroup)) {
        	setSongs(listType, query, page, userSettings, map);
        }

        map.put("welcomeTitle", settingsService.getWelcomeTitle());
        map.put("welcomeSubtitle", settingsService.getWelcomeSubtitle());
        map.put("welcomeMessage", settingsService.getWelcomeMessage());
        map.put("isIndexCreated", libraryUpdateService.isIndexCreated());
        map.put("listType", listType);
        map.put("listGroup", listGroup);
        map.put("user", user);
        
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        
        return result;
    }

    private void setArtists(String listType, String listGroup, String query, int page, UserSettings userSettings, Map<String, Object> map) {
    	String lastFmUsername = userSettings.getLastFmUsername();
    	if ("topartists".equals(listType)) {
    		Period period = Period.THREE_MONTHS;
    		for (Period p : Period.values()) {
    			if (p.getDescription().equals(listGroup)) {
    				period = p;
    			}
    		}
    		if (!isEmpty(lastFmUsername)) {
    			List<ArtistRecommendation> topArtists = Util.square(userTopArtistsService.getUserTopArtists(
    					new LastFmUser(lastFmUsername), period, 0, 25));
    			map.put("lastFmUser", lastFmUsername);
    			map.put("artists", topArtists);
    		}
    	} else {
        	final int ARTISTS = userSettings.getDefaultHomeArtists();
        	int offset = page * ARTISTS, limit = ARTISTS + 1;

        	List<ArtistRecommendation> artists = Util.square(
        			getArtists(listType, lastFmUsername, offset, limit, query, userSettings));
        	
        	if (artists.size() > ARTISTS) {
        		map.put("morePages", true);
        		artists.remove(ARTISTS);
        	}
        	map.put("page", page);
    		map.put("artists", artists);
    		
    		if ("recommended".equals(listType)) {
                map.put("artistsNotInLibrary", getRecommendedArtistsNotInLibrary(lastFmUsername, userSettings));
    		}
    	}
		map.put("artistGridWidth", userSettings.getArtistGridWidth());
    }
    
    private List<ArtistLink> getRecommendedArtistsNotInLibrary(String lastFmUsername, UserSettings userSettings) {
    	List<ArtistLink> artistsNotInLibrary = new ArrayList<>();
    	short amount = userSettings.getRecommendedArtists(); 
		boolean onlyAlbumArtists = userSettings.isOnlyAlbumArtistRecommendations();
		List<String> namesNotInLibrary = artistRecommendationService.
				getRecommendedArtistsNotInLibrary(lastFmUsername, amount, onlyAlbumArtists);
    	for (String name : namesNotInLibrary) {
    		artistsNotInLibrary.add(new ArtistLink(name, getURLEncodedName(name)));
    	}
    	return artistsNotInLibrary;
    }
    
    private String getURLEncodedName(String name) {
    	try {
    		return encode(name, ENCODING_UTF8);
    	} catch (UnsupportedEncodingException e) {
    		return name;
    	}
    }

    private List<ArtistRecommendation> getArtists(String listType, String lastFmUsername, 
    		int offset, int limit, String query, UserSettings userSettings) {
    	boolean onlyAlbumArtists = userSettings.isOnlyAlbumArtistRecommendations();
    	switch (listType) {
    	case "recent": return libraryBrowserService.getRecentlyPlayedArtists(lastFmUsername, onlyAlbumArtists, offset, limit, query);
    	case "frequent": return libraryBrowserService.getMostPlayedArtists(lastFmUsername, offset, limit, query);
    	case "starred": return libraryBrowserService.getStarredArtists(lastFmUsername, offset, limit, query);
    	case "random": return libraryBrowserService.getRandomArtists(onlyAlbumArtists, limit);
    	case "recommended": return artistRecommendationService.getRecommendedArtistsInLibrary(
    			lastFmUsername, offset, limit, userSettings.isOnlyAlbumArtistRecommendations());
    	}
    	return null;
    }
    
    private void setAlbums(String listType, String query, int page, UserSettings userSettings, Map<String, Object> map) {
    	String lastFmUsername = userSettings.getLastFmUsername();
    	final int ALBUMS = userSettings.getDefaultHomeAlbums();
    	int offset = page * ALBUMS, limit = ALBUMS + 1;

    	List<Album> albums = mediaFileService.getAlbums(
    			getAlbums(listType, query, offset, limit, lastFmUsername));

    	if (albums.size() > ALBUMS) {
    		map.put("morePages", true);
    		albums.remove(ALBUMS);
    	}
    	map.put("page", page);
    	map.put("albums", albums);
        map.put("isAlbumStarred", starService.getStarredAlbumsMask(lastFmUsername, getAlbumIds(albums)));
		map.put("artistGridWidth", userSettings.getArtistGridWidth());
		map.put("albumGridLayout", userSettings.isAlbumGridLayout());
    }

    private List<Integer> getAlbumIds(List<Album> albums) {
    	List<Integer> albumIds = new ArrayList<>();
    	for (Album album : albums) {
    		albumIds.add(album.getId());
    	}
    	return albumIds;
    }

    public List<com.github.hakko.musiccabinet.domain.model.music.Album> getAlbums(
    		String listType, String query, int offset, int limit, String lastFmUsername) {
    	switch (listType) {
		case "newest": return libraryBrowserService.getRecentlyAddedAlbums(offset, limit, query);
		case "recent": return libraryBrowserService.getRecentlyPlayedAlbums(lastFmUsername, offset, limit, query);
		case "frequent": return libraryBrowserService.getMostPlayedAlbums(lastFmUsername, offset, limit, query);
		case "starred": return libraryBrowserService.getStarredAlbums(lastFmUsername, offset, limit, query);
		case "random": return libraryBrowserService.getRandomAlbums(limit);
		}
    	return null;
    }

    private void setSongs(String listType, String query, int page, UserSettings userSettings, Map<String, Object> map) {
    	String lastFmUsername = userSettings.getLastFmUsername();
    	final int SONGS = userSettings.getDefaultHomeSongs();
    	int offset = page * SONGS, limit = SONGS + 1;

    	List<Integer> mediaFileIds = getMediaFileIds(listType, query, offset, limit, lastFmUsername);
    	mediaFileService.loadMediaFiles(mediaFileIds);
    	List<MediaFile> mediaFiles = mediaFileService.getMediaFiles(mediaFileIds);
    	
    	if (mediaFiles.size() > SONGS) {
    		map.put("morePages", true);
    		mediaFileIds.remove(SONGS);
    		mediaFiles.remove(SONGS);
    	}
    	map.put("page", page);
    	map.put("trackIds", mediaFileIds);
    	map.put("mediaFiles", mediaFiles);
    	map.put("multipleArtists", true);
    	map.put("visibility", userSettings.getHomeVisibility());
        map.put("isTrackStarred", starService.getStarredTracksMask(lastFmUsername, getTrackIds(mediaFiles)));
    }

    private List<Integer> getTrackIds(List<MediaFile> mediaFiles) {
    	List<Integer> trackIds = new ArrayList<>();
    	for (MediaFile mediaFile : mediaFiles) {
    		trackIds.add(mediaFile.getId());
    	}
    	return trackIds;
    }
    
    private List<Integer> getMediaFileIds(String listType, String query, int offset, int limit, String lastFmUsername) {
    	switch (listType) {
		case "recent": return libraryBrowserService.getRecentlyPlayedTrackIds(lastFmUsername, offset, limit, query);
		case "frequent": return libraryBrowserService.getMostPlayedTrackIds(lastFmUsername, offset, limit, query);
		case "starred": return libraryBrowserService.getStarredTrackIds(lastFmUsername, offset, limit, query);
		case "random": return libraryBrowserService.getRandomTrackIds(limit);
    	}
		return null;
	}

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setUserTopArtistsService(UserTopArtistsService userTopArtistsService) {
		this.userTopArtistsService = userTopArtistsService;
	}

	public void setArtistRecommendationService(ArtistRecommendationService artistRecommendationService) {
		this.artistRecommendationService = artistRecommendationService;
	}

	public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
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