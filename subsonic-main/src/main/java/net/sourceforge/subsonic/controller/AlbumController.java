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

import static org.apache.commons.lang.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.service.StarService;

/**
 * Controller for the album page.
 */
public class AlbumController extends ParameterizableViewController {

    private SecurityService securityService;
    private SettingsService settingsService;
	private MediaFileService mediaFileService;
	private StarService starService;
	
	private static final Logger LOG = Logger.getLogger(AlbumController.class);
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Set<Integer> artistIds = new HashSet<>();

        MediaFile mediaFile;
        List<MediaFile> mediaFiles = new ArrayList<>();
        for (String param : request.getParameterValues("mf")) {
        	mediaFiles.add(mediaFile = mediaFileService.getMediaFile(toInt(param)));
        	artistIds.add(mediaFile.getMetaData().getArtistId());
        }

        map.put("mediaFiles", mediaFiles);
    	map.put("multipleArtists", artistIds.size() > 1);
        String userName = securityService.getCurrentUsername(request);
        UserSettings userSettings = settingsService.getUserSettings(userName);
        map.put("visibility", userSettings.getMainVisibility());
        map.put("user", securityService.getCurrentUser(request));
        map.put("isTrackStarred", starService.getStarredTracksMask(userSettings.getLastFmUsername(), 
        		getTrackIds(mediaFiles)));
        map.put("trackId", request.getParameter("trackId"));
        map.put("albumView", true);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Integer> getTrackIds(List<MediaFile> mediaFiles) {
    	List<Integer> trackIds = new ArrayList<>();
    	for (MediaFile mediaFile : mediaFiles) {
    		trackIds.add(mediaFile.getId());
    	}
    	return trackIds;
    }
    
    // Spring setters

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setMediaFileService(MediaFileService mediaFileService) {
		this.mediaFileService = mediaFileService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}
    
}