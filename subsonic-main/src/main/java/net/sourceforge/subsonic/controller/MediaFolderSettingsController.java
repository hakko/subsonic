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
import jahspotify.AbstractConnectionListener;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFolder;
import net.sourceforge.subsonic.service.MediaFolderService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.service.DatabaseAdministrationService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;
import com.github.hakko.musiccabinet.service.spotify.SpotifyService;
import com.github.hakko.musiccabinet.util.BlockingRequest;

/**
 * Controller for the page used to administrate the set of media folders.
 * 
 * @author Sindre Mehus
 */
public class MediaFolderSettingsController extends
		ParameterizableViewController {

	private SettingsService settingsService;
	private SearchService searchService;
	private MediaFolderService mediaFolderService;
	private LibraryBrowserService libraryBrowserService;
	private LibraryUpdateService libraryUpdateService;
	private DatabaseAdministrationService dbAdmService;
	private SpotifyService spotifyService;
	private byte[] keyBytes;

	private static final Logger LOG = Logger
			.getLogger(MediaFolderSettingsController.class);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		

		Map<String, String> parameters = getParameters(request);

		if (isDeleteMediaFolder(parameters)
				&& libraryUpdateService.isIndexBeingCreated()) {
			return new ModelAndView("musicCabinetUnavailable"); // not
																// informative,
																// but very rare
		}

		if (isFormSubmission(request)) {
			if (!arePathParametersValid(parameters)) {
				map.put("error", "mediaFoldersettings.nopath");
			} else {
				updateSpotifyFromRequest(parameters, map);
				updateMediaFoldersFromRequest(parameters);
				addNewMediaFolderFromRequest(parameters);
				settingsService.setSettingsChanged();
				map.put("hasArtists", libraryBrowserService.hasArtists());
				map.put("reload", true);
			}
		}

		ModelAndView result = super.handleRequestInternal(request, response);
		map.put("mediaFolders", mediaFolderService.getAllMediaFolders());
		map.put("indexBeingCreated", libraryUpdateService.isIndexBeingCreated());
		map.put("filesMissingMetadata",
				libraryBrowserService.getFilesMissingMetadata());
		map.put("databaseAvailable",
				dbAdmService.isRDBMSRunning()
						&& dbAdmService.isPasswordCorrect(settingsService
								.getMusicCabinetJDBCPassword())
						&& dbAdmService.isDatabaseUpdated());

		map.put("spotifyAvailable", false);
		map.put("spotifyKeyExists",
				new File(settingsService.getSpotifyKey()).exists());
		if (spotifyService.isSpotifyAvailable()) {
			map.put("spotifyAvailable", true);
			map.put("spotifyLoggedIn", false);
			if (spotifyService.isLoggedIn()) {
				map.put("spotifyLoggedIn", true);
				map.put("spotifyUsername", spotifyService.getUser()
						.getDisplayName());
			}
		}

		result.addObject("model", map);
		return result;
	}

	/**
	 * Determine if the given request represents a form submission.
	 * 
	 * @param request
	 *            current HTTP request
	 * @return if the request represents a form submission
	 */
	private boolean isFormSubmission(HttpServletRequest request) {
		return "POST".equals(request.getMethod());
	}

	private boolean isDeleteMediaFolder(Map<String, String> parameters) {
		for (MediaFolder mediaFolder : mediaFolderService.getAllMediaFolders()) {
			if (getParameter(parameters, "delete", mediaFolder.getId()) != null) {
				return true;
			}
		}
		return false;
	}

	private boolean arePathParametersValid(Map<String, String> parameters) {
		for (MediaFolder mediaFolder : mediaFolderService.getAllMediaFolders()) {
			Integer id = mediaFolder.getId();
			if (getParameter(parameters, "path", id) == null) {
				return false;
			}
		}

		String name = trimToNull(parameters.get("name"));
		String path = trimToNull(parameters.get("path"));
		if (name != null && path == null) {
			return false;
		}

		return true;
	}

	private void updateMediaFoldersFromRequest(Map<String, String> parameters) {
		Set<String> deletedPaths = new HashSet<>();

		for (MediaFolder mediaFolder : mediaFolderService.getAllMediaFolders()) {
			Integer id = mediaFolder.getId();

			if (getParameter(parameters, "delete", id) != null) {
				mediaFolderService.deleteMediaFolder(id);
				String path = mediaFolder.getPath().getAbsolutePath();
				if (mediaFolder.isIndexed() && !hasIndexedParentFolder(path)) {
					deletedPaths.add(path);
				}
			} else {
				String path = getParameter(parameters, "path", id);
				String name = getParameter(parameters, "name", id);
				boolean indexed = getParameter(parameters, "indexed", id) != null;

				if (mediaFolder.isIndexed() && !indexed) {
					deletedPaths.add(path);
				}
				File file = new File(path);
				mediaFolderService.updateMediaFolder(new MediaFolder(
						mediaFolder.getId(), file, defaultIfEmpty(name,
								file.getName()), indexed, new Date()));
			}
		}

		LOG.debug("deleted paths: " + deletedPaths);
		if (!deletedPaths.isEmpty() && libraryBrowserService.hasArtists()) {
			setChildFoldersToNonIndexed(deletedPaths);
			searchService.deleteMediaFolders(deletedPaths);
		}
	}

	private void updateSpotifyFromRequest(Map<String, String> parameters,
			Map<String, Object> response) throws Exception {
		
		if (keyBytes != null) {
			createKey(keyBytes, response);
		}

		final String username = parameters.get("spotify_username");
		final String password = parameters.get("spotify_password");
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return;
		}
		settingsService.setSpotifyUserName(username);
		settingsService.setSpotifyPassword(password);
		settingsService.save();

		final BlockingRequest<Boolean> blockingRequest = new BlockingRequest<Boolean>() {
			@Override
			public void run() {
				spotifyService.login(username, password, null);
			}
		};
		AbstractConnectionListener loginListener = new AbstractConnectionListener() {
			@Override
			public void loggedIn(boolean success) {
				blockingRequest.finish(success);
			}
		};

		Boolean loggedIn = Boolean.FALSE;
		spotifyService.registerListener(loginListener);
		try {
			loggedIn = blockingRequest.start();
			if (loggedIn == null || !loggedIn) {
				response.put("error", "Invalid username or password.");
			}
		} finally {
			LOG.debug("Logged in: " + loggedIn);
			spotifyService.removeListener(loginListener);
		}
	}
	
	private Map<String, String> getParameters(HttpServletRequest request) throws FileUploadException {
		
		keyBytes = null;
		Map<String, String> parameters = new HashMap<String, String>();
		if (ServletFileUpload.isMultipartContent(request)) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<?> items = upload.parseRequest(request);

			// Look for file items.
			for (Object o : items) {
				FileItem item = (FileItem) o;

				if (!item.isFormField()) {
					byte[] data = item.get();

					if (data.length > 0) {
						keyBytes = data;
					}
					break;
				} else {
					parameters.put(item.getFieldName(), item.getString());
				}
			}
			
			

			return parameters;
		}
		
		for(Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			parameters.put(key, request.getParameter(key));
		}
		return parameters;

	}
	

	private void createKey(byte[] data, Map<String, Object> map) {

		try {
			String keyFile = settingsService.getSpotifyKey();
			FileUtils.writeByteArrayToFile(new File(keyFile), data);

		} catch (Exception x) {
			LOG.warn("Failed to upload spotify key: " + x, x);
			map.put("error", x);
		}
	}

	private boolean hasIndexedParentFolder(String folder) {
		return mediaFolderService.hasIndexedParentFolder(folder);
	}

	private void setChildFoldersToNonIndexed(Set<String> deletedPaths) {
		mediaFolderService.setChildFoldersToNonIndexed(deletedPaths);
	}

	private void addNewMediaFolderFromRequest(Map<String, String> parameters) {
		String name = trimToNull(parameters.get("name"));
		String path = trimToNull(parameters.get("path"));
		boolean indexed = trimToNull(parameters.get("indexed")) != null;

		if (name != null || path != null) {
			File file = new File(path);
			mediaFolderService.createMediaFolder(new MediaFolder(file,
					defaultIfEmpty(name, file.getName()), indexed, new Date()));
		}
	}

	private String getParameter(Map<String, String> parameters, String name,
			Integer id) {
		return StringUtils.trimToNull(parameters.get(name + "[" + id
				+ "]"));
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

	public void setLibraryBrowserService(
			LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

	public void setLibraryUpdateService(
			LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}

	public void setDatabaseAdministrationService(
			DatabaseAdministrationService dbAdmService) {
		this.dbAdmService = dbAdmService;
	}

	public void setSpotifyService(SpotifyService spotifyService) {
		this.spotifyService = spotifyService;
	}

}
