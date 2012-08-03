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

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.github.hakko.musiccabinet.service.StarService;

/**
 * Provides AJAX-enabled services for starring.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 */
public class UIStarService {

    private static final Logger LOG = Logger.getLogger(UIStarService.class);

    private SecurityService securityService;
    private SettingsService settingsService;
    private StarService starService;
    
    public void starArtist(int artistId) {
    	LOG.debug("star artist " + artistId);
    	starService.starArtist(getUser(), artistId);
    }

    public void unstarArtist(int artistId) {
    	LOG.debug("unstar artist " + artistId);
    	starService.unstarArtist(getUser(), artistId);
    }

    public void starAlbum(int albumId) {
    	LOG.debug("star album " + albumId);
    	starService.starAlbum(getUser(), albumId);
    }

    public void unstarAlbum(int albumId) {
    	LOG.debug("unstar album " + albumId);
    	starService.unstarAlbum(getUser(), albumId);
    }

    public void starTrack(int trackId) {
    	System.out.println("star track " + trackId);
    	starService.starTrack(getUser(), trackId);
    }

    public void unstarTrack(int trackId) {
    	System.out.println("unstar track " + trackId);
    	starService.unstarTrack(getUser(), trackId);
    }

    private String getUser() {
        WebContext webContext = WebContextFactory.get();
        UserSettings userSettings = settingsService.getUserSettings(
        		securityService.getCurrentUsername(webContext.getHttpServletRequest()));
        return userSettings.getLastFmUsername();
    }

    // Spring setters
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}

}