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

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.ModelAndView;

import com.github.hakko.musiccabinet.service.LibraryBrowserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for the lyrics popup.
 *
 * @author Sindre Mehus
 */
public class LyricsController extends ParameterizableViewController {

	private LibraryBrowserService libraryBrowserService;
	
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int mediaFileId = NumberUtils.toInt(request.getParameter("mfId"));
        String lyrics = libraryBrowserService.getLyricsForTrack(mediaFileId);
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lyrics", lyrics);
        
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}
    
}
