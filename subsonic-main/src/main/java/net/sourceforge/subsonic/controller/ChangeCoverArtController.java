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

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.service.MediaFileService;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for saving playlists.
 *
 * @author Sindre Mehus
 */
public class ChangeCoverArtController extends ParameterizableViewController {

    private MediaFileService mediaFileService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int mediaFileId = NumberUtils.toInt(request.getParameter("id"));
        MediaFile mediaFile = mediaFileService.getMediaFile(mediaFileId);

        String artist = mediaFile.getMetaData().getArtist();
        String album = mediaFile.getMetaData().getAlbum();
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("artist", artist);
        map.put("album", album);
        map.put("id", mediaFileId);
        map.put("artistId", request.getParameter("artistId"));
        map.put("albumId", request.getParameter("albumId"));

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);

        return result;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}
