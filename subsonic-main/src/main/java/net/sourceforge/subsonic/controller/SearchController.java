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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.command.SearchCommand;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;
import com.github.hakko.musiccabinet.service.NameSearchService;
import com.github.hakko.musiccabinet.service.StarService;

/**
 * Controller for the search page.
 *
 * @author Sindre Mehus
 */
public class SearchController extends SimpleFormController {

    private SecurityService securityService;
    private SettingsService settingsService;
    private PlayerService playerService;
    private LibraryUpdateService libraryUpdateService;
    private NameSearchService nameSearchService;
    private StarService starService;

    private static final int ARTIST_COUNT = 5;
    private static final int ALBUM_COUNT = 5;
    private static final int TRACK_COUNT = 15;
    
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return new SearchCommand();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object com, BindException errors)
            throws Exception {
        SearchCommand command = (SearchCommand) com;

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        command.setUser(user);
        command.setPartyModeEnabled(userSettings.isPartyModeEnabled());

        String any = StringUtils.trimToNull(command.getQuery());

        if (any == null) {
            return new ModelAndView(new RedirectView("advancedSearch.view"));
        } else {

            if (libraryUpdateService.isIndexCreated()) {
                command.setArtists(nameSearchService.getArtists(any, 0, ARTIST_COUNT).getResults());
                command.setAlbums(nameSearchService.getAlbums(any, 0, ALBUM_COUNT).getResults());
                command.setSongs(nameSearchService.getTracks(any, 0, TRACK_COUNT).getResults());
                command.setIsTrackStarred(starService.getStarredTracksMask(userSettings.getLastFmUsername(), 
                		getTrackIds(command.getSongs())));

                command.setPlayer(playerService.getPlayer(request, response));
                command.setIndexCreated(true);
            } else {
                command.setIndexCreated(false);
            }
        }

        return new ModelAndView(getSuccessView(), errors.getModel());
    }
    
    private List<Integer> getTrackIds(List<Track> tracks) {
    	List<Integer> trackIds = new ArrayList<>();
    	for (Track track : tracks) {
    		trackIds.add(track.getId());
    	}
    	return trackIds;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

	public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}

	public void setNameSearchService(NameSearchService nameSearchService) {
		this.nameSearchService = nameSearchService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}

}