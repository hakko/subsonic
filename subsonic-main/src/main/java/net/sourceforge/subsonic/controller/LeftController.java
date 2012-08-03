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

import static org.apache.commons.lang.StringUtils.defaultString;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.ajax.LibraryStatusService;
import net.sourceforge.subsonic.domain.InternetRadio;
import net.sourceforge.subsonic.domain.MediaFolder;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.ArtistIndexService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.github.hakko.musiccabinet.domain.model.aggr.LibraryStatistics;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;
import com.github.hakko.musiccabinet.service.TagService;

/**
 * Controller for the left index frame.
 *
 * @author Sindre Mehus
 */
public class LeftController extends ParameterizableViewController implements LastModified {

    private SettingsService settingsService;
    private SecurityService securityService;
    private PlayerService playerService;
    private ArtistIndexService artistIndexService;
    
    private LibraryBrowserService libraryBrowserService;
    private LibraryUpdateService libraryUpdateService;
    private LibraryStatusService libraryStatusService;
    private TagService tagService;
    
    private static final Logger LOG = Logger.getLogger(LeftController.class);
    
	/**
     * {@inheritDoc}
     */
    public long getLastModified(HttpServletRequest request) {
    	
        // When was settings last changed?
        long lastModified = settingsService.getSettingsChanged();

        // When was user settings last changed?
        UserSettings userSettings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
        lastModified = Math.max(lastModified, userSettings.getChanged().getTime());

        // When was internet radio table last changed?
        for (InternetRadio internetRadio : settingsService.getAllInternetRadios()) {
            lastModified = Math.max(lastModified, internetRadio.getChanged().getTime());
        }

        // When was the last scanning started/finished?
		lastModified = Math.max(lastModified, libraryStatusService.getLastModified());
    	
    	LOG.debug("return last modified as " + new Date(lastModified));
    	
        return lastModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        
        map.put("player", playerService.getPlayer(request, response));
        map.put("radios", settingsService.getAllInternetRadios());
        map.put("uploadRole", user.isUploadRole());
        String tag = defaultString(request.getParameter("tag"), "");
        boolean hasArtists = libraryBrowserService.hasArtists();
        String method = request.getParameter("method");

        LOG.debug("hasArtists = " + hasArtists + ", method = " + method);
        
        if (!"file".equals(method) && hasArtists) {
            List<Artist> artists;
            if (tag.isEmpty() && userSettings.isReluctantArtistLoading()) {
            	map.put("reluctantArtistLoading", true);
            	String indexLetter = request.getParameter("indexLetter");
            	if ("0".equals(indexLetter)) indexLetter = "#";
            	artists = indexLetter == null ? null : libraryBrowserService.getArtists(indexLetter.charAt(0));
            	map.put("indexedLetter", indexLetter);
            	map.put("indexes", artistIndexService.getIndexedArtists(
            			libraryBrowserService.getArtistIndexes(), indexLetter, artists));
            } else {
            	artists = tag.isEmpty() ? libraryBrowserService.getArtists() : 
                	libraryBrowserService.getArtists(tag, 25);
            	map.put("indexes", artistIndexService.getIndexedArtists(artists));
            }
            LibraryStatistics statistics = libraryBrowserService.getStatistics();
            Locale locale = RequestContextUtils.getLocale(request);
            List<String> tags = new ArrayList<>();
            tags.add("");
            tags.addAll(tagService.getTopTags());
            map.put("tags", tags);
            map.put("currentTag", tag);
            map.put("statistics", statistics);
            map.put("statisticsBytes", StringUtil.formatBytes(statistics.getTotalLengthInBytes(), locale));
        } else {
    		map.put("filebased", true);
        	List<MediaFolder> mediaFolders = settingsService.getAllMediaFolders();
        	if (mediaFolders.size() > 0) {
    			map.put("indexing", libraryUpdateService.isIndexBeingCreated());
        		map.put("mediaFolders", mediaFolders);
        	}
        	if (hasArtists) {
        		map.put("hasArtists", true);
        	}
        }
        
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

	public void setArtistIndexService(ArtistIndexService artistIndexService) {
		this.artistIndexService = artistIndexService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

    public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}

	public void setLibraryStatusService(LibraryStatusService libraryStatusService) {
		this.libraryStatusService = libraryStatusService;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

}