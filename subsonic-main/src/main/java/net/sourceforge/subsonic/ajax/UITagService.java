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

import static java.lang.String.format;
import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.service.TagUpdateService;

/**
 * Provides AJAX-enabled services for updating artist tags.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 */
public class UITagService {

    private static final Logger LOG = Logger.getLogger(UITagService.class);

    private SecurityService securityService;
    private SettingsService settingsService;
    private TagUpdateService tagUpdateService;
    
    public void tagArtist(int artistId, String artistName, String tagName, int tagCount, boolean increase) {
    	Artist artist = new Artist(artistId, artistName);
    	String lastFmUsername = getLastFmUsername();
    	LOG.debug(format("Tag %s as %s (%d) for %s", artistName, tagName, tagCount, lastFmUsername));
    	try {
    		tagUpdateService.updateTag(artist, lastFmUsername, tagName, tagCount, increase);
    	} catch (Throwable e) {
    		LOG.debug("Uh-oh.", e);
    	}
    }

    private String getLastFmUsername() {
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

	public void setTagUpdateService(TagUpdateService tagUpdateService) {
		this.tagUpdateService = tagUpdateService;
	}

}