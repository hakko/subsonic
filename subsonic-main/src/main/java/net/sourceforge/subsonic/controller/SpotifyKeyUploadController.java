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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Avatar;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller which receives uploaded avatar images.
 *
 * @author Sindre Mehus
 */
public class SpotifyKeyUploadController extends ParameterizableViewController {

	private static final Logger LOG = Logger
			.getLogger(AvatarUploadController.class);

	private SettingsService settingsService;
	private SecurityService securityService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String username = securityService.getCurrentUsername(request);

		// Check that we have a file upload request.
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new Exception("Illegal request.");
		}

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
					createKey(data, map);
				} else {
					map.put("error", new Exception("Missing file."));
					LOG.warn("Failed to upload spotify key. Empty file.");
				}
				break;
			}
		}

		map.put("username", username);
		ModelAndView result = super.handleRequestInternal(request, response);
		result.addObject("model", map);
		return result;
	}

	private void createKey(byte[] data, Map<String, Object> map)
			throws IOException {

		try {
			String keyFile = settingsService.getSpotifyKey();
			FileUtils.writeByteArrayToFile(new File(keyFile), data);

		} catch (Exception x) {
			LOG.warn("Failed to upload spotify key: " + x, x);
			map.put("error", x);
		}
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
}
