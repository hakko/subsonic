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

import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;
import static org.apache.commons.lang.StringUtils.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.Share;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.ShareService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;

/**
 * Controller for sharing music on Twitter, Facebook etc.
 *
 * @author Sindre Mehus
 */
public class ShareManagementController extends MultiActionController {

    private MediaFileService mediaFileService;
    private SettingsService settingsService;
    private ShareService shareService;
    private PlayerService playerService;
    private SecurityService securityService;

    private final static Logger LOG = Logger.getLogger(ShareManagementController.class);

    public ModelAndView createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {

    	List<MediaFile> files;
    	
        String playerId = request.getParameter("player");
        if (playerId != null) {
            Player player = playerService.getPlayerById(playerId);
            Playlist playlist = player.getPlaylist();
            files = Arrays.asList(playlist.getFiles());
        } else {
        	List<Uri> mediaFileUris = getMediaFileUris(request.getParameter("ids"));
	        files = mediaFileService.getMediaFiles(mediaFileUris);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlRedirectionEnabled", settingsService.isUrlRedirectionEnabled());
        map.put("user", securityService.getCurrentUser(request));
        Share share = shareService.createShare(request, files);
        map.put("playUrl", shareService.getShareUrl(share));

        return new ModelAndView("createShare", "model", map);
    }

    /*
     * given string "[x, y, z]", returns the integers x, y and z as a list.
     */
    private List<Uri> getMediaFileUris(String query) {
    	List<Uri> mediaFileIds = new ArrayList<>(); 
    	for (String s : split(removeEnd(removeStart(query, "["), "]"), ", ")) {
    		mediaFileIds.add(URIUtil.parseURI(s));
    	}
    	return mediaFileIds;
    }

// TODO : this used to be called, prob. when creating a share from playlist view
//    private List<MediaFile> getMediaFiles(HttpServletRequest request) throws IOException {
//        String playerId = request.getParameter("player");
//        Player player = playerService.getPlayerById(playerId);
//        Playlist playlist = player.getPlaylist();
//        Collections.addAll(result, playlist.getFiles());
//
//        return result;
//    }

    public void setmediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setShareService(ShareService shareService) {
        this.shareService = shareService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

}