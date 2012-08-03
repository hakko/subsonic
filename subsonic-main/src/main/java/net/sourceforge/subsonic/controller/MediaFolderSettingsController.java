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
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;

import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.apache.commons.lang.StringUtils;

import com.github.hakko.musiccabinet.service.LibraryBrowserService;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Controller for the page used to administrate the set of music folders.
 *
 * @author Sindre Mehus
 */
public class MediaFolderSettingsController extends ParameterizableViewController {

    private SettingsService settingsService;
    private SearchService searchService;
    private LibraryBrowserService libraryBrowserService;
    
    private static final Logger LOG = Logger.getLogger(MediaFolderSettingsController.class);
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            String error = handleParameters(request);
            map.put("error", error);
            if (error == null) {
            	map.put("hasArtists", libraryBrowserService.hasArtists());
                map.put("reload", true);
            }
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("mediaFolders", settingsService.getAllMediaFolders(true));

        result.addObject("model", map);
        return result;
    }

    /**
     * Determine if the given request represents a form submission.
     * @param request current HTTP request
     * @return if the request represents a form submission
     */
    private boolean isFormSubmission(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    private String handleParameters(HttpServletRequest request) {

    	LOG.debug("handleParameters");
    	
    	Set<String> deletedPaths = new HashSet<>();
    	
        for (MediaFolder mediaFolder : settingsService.getAllMediaFolders(true)) {
            Integer id = mediaFolder.getId();

            String path = getParameter(request, "path", id);
            String name = getParameter(request, "name", id);
            boolean enabled = getParameter(request, "enabled", id) != null;
            boolean delete = getParameter(request, "delete", id) != null;

            if (delete) {
                settingsService.deleteMediaFolder(id);
                deletedPaths.add(path);
            } else if (path == null) {
                return "mediaFoldersettings.nopath";
            } else {
                File file = new File(path);
                if (name == null) {
                    name = file.getName();
                }
                mediaFolder.setName(name);
                mediaFolder.setPath(file);
                mediaFolder.setEnabled(enabled);
                mediaFolder.setChanged(new Date());
                settingsService.updateMediaFolder(mediaFolder);
            }
        }

        LOG.debug("deleted paths: " + deletedPaths);
        if (!deletedPaths.isEmpty() && libraryBrowserService.hasArtists()) {
        	searchService.deleteMediaFolders(deletedPaths);
        }
        
        String name = StringUtils.trimToNull(request.getParameter("name"));
        String path = StringUtils.trimToNull(request.getParameter("path"));
        boolean enabled = StringUtils.trimToNull(request.getParameter("enabled")) != null;

        if (name != null || path != null) {
            if (path == null) {
                return "mediaFoldersettings.nopath";
            }
            File file = new File(path);
            if (name == null) {
                name = file.getName();
            }
            settingsService.createMediaFolder(new MediaFolder(file, name, enabled, new Date()));
        }

        settingsService.setSettingsChanged();
        
        return null;
    }

    private String getParameter(HttpServletRequest request, String name, Integer id) {
        return StringUtils.trimToNull(request.getParameter(name + "[" + id + "]"));
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

}