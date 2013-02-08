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

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang.StringUtils.trimToNull;
import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.domain.*;

import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.apache.commons.lang.StringUtils;

import com.github.hakko.musiccabinet.service.DatabaseAdministrationService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * Controller for the page used to administrate the set of media folders.
 *
 * @author Sindre Mehus
 */
public class MediaFolderSettingsController extends ParameterizableViewController {

    private SettingsService settingsService;
    private SearchService searchService;
    private MediaFolderService mediaFolderService;
    private LibraryBrowserService libraryBrowserService;
    private LibraryUpdateService libraryUpdateService;
    private DatabaseAdministrationService dbAdmService;
    
    private static final Logger LOG = Logger.getLogger(MediaFolderSettingsController.class);
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isDeleteMediaFolder(request) && libraryUpdateService.isIndexBeingCreated()) {
            return new ModelAndView("musicCabinetUnavailable"); // not informative, but very rare
        }
        
        if (isFormSubmission(request)) {
        	if (!arePathParametersValid(request)) {
                map.put("error", "mediaFoldersettings.nopath");
        	} else {
        		updateMediaFoldersFromRequest(request);
        		addNewMediaFolderFromRequest(request);
                settingsService.setSettingsChanged();
            	map.put("hasArtists", libraryBrowserService.hasArtists());
                map.put("reload", true);
        	}
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("mediaFolders", mediaFolderService.getAllMediaFolders());
        map.put("indexBeingCreated", libraryUpdateService.isIndexBeingCreated());
        map.put("filesMissingMetadata", libraryBrowserService.getFilesMissingMetadata());
        map.put("databaseAvailable", dbAdmService.isRDBMSRunning()
				&& dbAdmService.isPasswordCorrect(settingsService.getMusicCabinetJDBCPassword())
				&& dbAdmService.isDatabaseUpdated());

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

    private boolean isDeleteMediaFolder(HttpServletRequest request) {
        for (MediaFolder mediaFolder : mediaFolderService.getAllMediaFolders()) {
            if (getParameter(request, "delete", mediaFolder.getId()) != null) {
            	return true;
            }
        }
        return false;
    }
    
    private boolean arePathParametersValid(HttpServletRequest request) {
        for (MediaFolder mediaFolder : mediaFolderService.getAllMediaFolders()) {
            Integer id = mediaFolder.getId();
            if (getParameter(request, "path", id) == null) {
            	return false;
            }
        }

        String name = trimToNull(request.getParameter("name"));
        String path = trimToNull(request.getParameter("path"));
        if (name != null && path == null) {
        	return false;
        }
        
        return true;
    }
    
    private void updateMediaFoldersFromRequest(HttpServletRequest request) {
    	Set<String> deletedPaths = new HashSet<>();
    	
        for (MediaFolder mediaFolder : mediaFolderService.getAllMediaFolders()) {
            Integer id = mediaFolder.getId();

            if (getParameter(request, "delete", id) != null) {
            	mediaFolderService.deleteMediaFolder(id);
            	String path = mediaFolder.getPath().getAbsolutePath();
                if (mediaFolder.isIndexed() && !hasIndexedParentFolder(path)) {
                	deletedPaths.add(path);
                }
            } else {
                String path = getParameter(request, "path", id);
                String name = getParameter(request, "name", id);
                boolean indexed = getParameter(request, "indexed", id) != null;
                
            	if (mediaFolder.isIndexed() && !indexed) {
    	        	deletedPaths.add(path);
            	}
                File file = new File(path);
                mediaFolderService.updateMediaFolder(new MediaFolder(mediaFolder.getId(), 
                		file, defaultIfEmpty(name, file.getName()), indexed, new Date()));
            }
        }

        LOG.debug("deleted paths: " + deletedPaths);
        if (!deletedPaths.isEmpty() && libraryBrowserService.hasArtists()) {
        	setChildFoldersToNonIndexed(deletedPaths);
        	searchService.deleteMediaFolders(deletedPaths);
        }
    }

    private boolean hasIndexedParentFolder(String folder) {
    	return mediaFolderService.hasIndexedParentFolder(folder);
    }
    
    private void setChildFoldersToNonIndexed(Set<String> deletedPaths) {
    	mediaFolderService.setChildFoldersToNonIndexed(deletedPaths);
    }

    private void addNewMediaFolderFromRequest(HttpServletRequest request) {
        String name = trimToNull(request.getParameter("name"));
        String path = trimToNull(request.getParameter("path"));
        boolean indexed = trimToNull(request.getParameter("indexed")) != null;

        if (name != null || path != null) {
            File file = new File(path);
            mediaFolderService.createMediaFolder(
            		new MediaFolder(file, defaultIfEmpty(name, file.getName()), indexed, new Date()));
        }
    }
    
    private String getParameter(HttpServletRequest request, String name, Integer id) {
        return StringUtils.trimToNull(request.getParameter(name + "[" + id + "]"));
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

	public void setMediaFolderService(MediaFolderService mediaFolderService) {
		this.mediaFolderService = mediaFolderService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

	public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}

	public void setDatabaseAdministrationService(DatabaseAdministrationService dbAdmService) {
		this.dbAdmService = dbAdmService;
	}

}