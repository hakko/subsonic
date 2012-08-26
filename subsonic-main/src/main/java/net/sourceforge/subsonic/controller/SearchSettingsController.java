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

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.command.SearchSettingsCommand;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.github.hakko.musiccabinet.service.DatabaseAdministrationService;

/**
 * Controller for the page used to administrate the search index.
 */
public class SearchSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private SearchService searchService;
    private DatabaseAdministrationService dbAdmService;
    
    private static final Logger LOG = Logger.getLogger(SearchSettingsController.class);
    
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        SearchSettingsCommand command = new SearchSettingsCommand();
    
        command.setDatabaseAvailable(dbAdmService.isRDBMSRunning()
        				&& dbAdmService.isPasswordCorrect(settingsService.getMusicCabinetJDBCPassword())
        				&& dbAdmService.isDatabaseUpdated());

        String updateParam = request.getParameter("update");
        if (updateParam != null) {
        	boolean offlineScan = updateParam.equals("offline");
        	boolean onlyNewArtists = updateParam.equals("normal");
        	LOG.debug("update search index, scan type " + updateParam);
            searchService.createIndex(offlineScan, onlyNewArtists, true);
            command.setCreatingIndex(true);
        }

        command.setInterval("" + settingsService.getIndexCreationInterval());
        command.setHour("" + settingsService.getIndexCreationHour());
        command.setBrand(settingsService.getBrand());

        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        SearchSettingsCommand command = (SearchSettingsCommand) comm;

        settingsService.setIndexCreationInterval(Integer.parseInt(command.getInterval()));
        settingsService.setIndexCreationHour(Integer.parseInt(command.getHour()));
        settingsService.save();

        searchService.schedule();
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setDatabaseAdministrationService(DatabaseAdministrationService dbAdmService) {
        this.dbAdmService = dbAdmService;
    }

}