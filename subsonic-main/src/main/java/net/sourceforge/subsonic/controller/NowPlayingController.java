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

import static java.util.Arrays.asList;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.StatusService;
import net.sourceforge.subsonic.util.StringUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.dao.util.URIUtil;

/**
 * Controller for showing what's currently playing.
 *
 * @author Sindre Mehus
 */
public class NowPlayingController extends ParameterizableViewController {

    private PlayerService playerService;
    private StatusService statusService;
    private MediaFileService mediaFileService;
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Player player = playerService.getPlayer(request, response);
        List<TransferStatus> statuses = statusService.getStreamStatusesForPlayer(player);

        MediaFile current = statuses.isEmpty() ? null : mediaFileService.getMediaFile(statuses.get(0).getMediaFileUri());
        
        ModelAndView result = super.handleRequestInternal(request, response);
        
        if (current != null) {
	        File childCoverArt = mediaFileService.getCoverArt(current);
	        if (childCoverArt != null) {
	            result.addObject("coverArt", StringUtil.utf8HexEncode(childCoverArt.getPath()));
	        }
        }
        result.addObject("current", current);
        return result;
        /*
        String url;
        if (current != null) {
            url = "artist.view?id=" + current.getMetaData().getArtistUri() +
                    "&albumId=" + current.getMetaData().getAlbumUri() +
                    "&trackId=" + current.getUri();
        } else {
            url = "home.view";
        }

        return new ModelAndView(new RedirectView(url));
        */
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setmediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
    
}