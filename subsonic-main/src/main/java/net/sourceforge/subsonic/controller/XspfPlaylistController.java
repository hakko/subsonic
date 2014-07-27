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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for the creating the XSPF playlist, used be the Flash-based embedded player.
 *
 * @author Sindre Mehus
 */
public class XspfPlaylistController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(XspfPlaylistController.class);

    private PlayerService playerService;
    private MediaFileService mediaFileService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = playerService.getPlayer(request, response);
        Playlist playlist = player.getPlaylist();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("songs", getSongs(playlist));
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Song> getSongs(Playlist playlist) {
        List<Song> result = new ArrayList<Song>();

        MediaFile[] files = playlist.getFiles();
        for (MediaFile file : files) {
            Song song = new Song();
            song.setmediaFile(file);
            try {
                song.setCoverArtFile(mediaFileService.getCoverArt(file));
            } catch (IOException x) {
                LOG.warn("Failed to get cover art for " + file);
            }
            result.add(song);
        }
        return result;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setmediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    /**
     * Contains information about a single song in the playlist.
     */
    public static class Song {
        private MediaFile mediaFile;
        private File coverArtFile;

        public MediaFile getmediaFile() {
            return mediaFile;
        }

        public void setmediaFile(MediaFile mediaFile) {
            this.mediaFile = mediaFile;
        }

        public File getCoverArtFile() {
            return coverArtFile;
        }

        public void setCoverArtFile(File coverArtFile) {
            this.coverArtFile = coverArtFile;
        }
    }
}
