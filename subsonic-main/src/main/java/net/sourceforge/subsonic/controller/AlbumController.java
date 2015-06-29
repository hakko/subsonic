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
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;
import com.github.hakko.musiccabinet.service.StarService;

/**
 * Controller for the album page.
 */
public class AlbumController extends ParameterizableViewController {

    private SecurityService securityService;
    private PlayerService playerService;
    private SettingsService settingsService;
	private MediaFileService mediaFileService;
	private StarService starService;
	
	private static final Logger LOG = Logger.getLogger(AlbumController.class);
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Set<Uri> artistIds = new HashSet<>();
        
        Player player = playerService.getPlayer(request, response);

        MediaFile mediaFile;
        List<MediaFile> mediaFiles = new ArrayList<>();
        for (String param : request.getParameterValues("mf[]")) {
        	mediaFiles.add(mediaFile = mediaFileService.getMediaFile(URIUtil.parseURI(param)));
        	artistIds.add(mediaFile.getMetaData().getArtistUri());
        }

        map.put("mediaFiles", mediaFiles);
    	map.put("multipleArtists", artistIds.size() > 1);
        String userName = securityService.getCurrentUsername(request);
        UserSettings userSettings = settingsService.getUserSettings(userName);
        map.put("visibility", userSettings.getMainVisibility());
        map.put("user", securityService.getCurrentUser(request));
        map.put("isTrackStarred", starService.getStarredTracksMask(userSettings.getLastFmUsername(), 
        		getTrackUris(mediaFiles)));
        map.put("trackId", request.getParameter("trackId"));
        map.put("albumView", true);
        map.put("player", player);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Uri> getTrackUris(List<MediaFile> mediaFiles) {
    	List<Uri> trackUris = new ArrayList<>();
    	for (MediaFile mediaFile : mediaFiles) {
    		trackUris.add(mediaFile.getUri());
    	}
    	return trackUris;
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

	public void setMediaFileService(MediaFileService mediaFileService) {
		this.mediaFileService = mediaFileService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}
    
}