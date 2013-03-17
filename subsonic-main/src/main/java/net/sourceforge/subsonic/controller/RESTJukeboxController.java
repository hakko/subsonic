package net.sourceforge.subsonic.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.ajax.PlaylistService;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.JukeboxService;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;
import net.sourceforge.subsonic.util.XMLBuilder.AttributeSet;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

public class RESTJukeboxController extends RESTAbstractController {

    private RESTBrowseController restBrowseController;
    private SecurityService securityService;
    private PlaylistService playlistControlService;
    private JukeboxService jukeboxService;
    private MediaFileService mediaFileService;

    public void jukeboxControl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request, true);

        User user = securityService.getCurrentUser(request);
        if (!user.isJukeboxRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to use jukebox.");
            return;
        }

        try {
            boolean returnPlaylist = false;
            String action = ServletRequestUtils.getRequiredStringParameter(request, "action");
            if ("start".equals(action)) {
                playlistControlService.doStart(request, response);
            } else if ("stop".equals(action)) {
                playlistControlService.doStop(request, response);
            } else if ("skip".equals(action)) {
                int index = ServletRequestUtils.getRequiredIntParameter(request, "index");
                int offset = ServletRequestUtils.getIntParameter(request, "offset", 0);
                playlistControlService.doSkip(request, response, index, offset);
            } else if ("add".equals(action)) {
                String[] ids = ServletRequestUtils.getStringParameters(request, "id");
                List<Integer> mediaFileIds = new ArrayList<Integer>(ids.length);
                for (String id : ids) {
                    mediaFileIds.add(NumberUtils.toInt(id));
                }
                playlistControlService.doAdd(request, response, mediaFileIds);
            } else if ("set".equals(action)) {
                String[] ids = ServletRequestUtils.getStringParameters(request, "id");
                List<Integer> mediaFileIds = new ArrayList<Integer>(ids.length);
                for (String id : ids) {
                    mediaFileIds.add(NumberUtils.toInt(id));
                }
                playlistControlService.doSet(request, response, mediaFileIds);
            } else if ("clear".equals(action)) {
                playlistControlService.doClear(request, response);
            } else if ("remove".equals(action)) {
                int index = ServletRequestUtils.getRequiredIntParameter(request, "index");
                playlistControlService.doRemove(request, response, index);
            } else if ("shuffle".equals(action)) {
                playlistControlService.doShuffle(request, response);
            } else if ("setGain".equals(action)) {
                float gain = ServletRequestUtils.getRequiredFloatParameter(request, "gain");
                jukeboxService.setGain(gain);
            } else if ("get".equals(action)) {
                returnPlaylist = true;
            } else if ("status".equals(action)) {
                // No action necessary.
            } else {
                throw new Exception("Unknown jukebox action: '" + action + "'.");
            }

            XMLBuilder builder = createXMLBuilder(request, response, true);

            Player player = playerService.getPlayer(request, response);
            Player jukeboxPlayer = jukeboxService.getPlayer();
            boolean controlsJukebox = jukeboxPlayer != null && jukeboxPlayer.getId().equals(player.getId());
            Playlist playlist = player.getPlaylist();

            List<Attribute> attrs = new ArrayList<Attribute>(Arrays.asList(
                    new Attribute("currentIndex", controlsJukebox && !playlist.isEmpty() ? playlist.getIndex() : -1),
                    new Attribute("playing", controlsJukebox && !playlist.isEmpty() && playlist.getStatus() == Playlist.Status.PLAYING),
                    new Attribute("gain", jukeboxService.getGain()),
                    new Attribute("position", controlsJukebox && !playlist.isEmpty() ? jukeboxService.getPosition() : 0)));

            if (returnPlaylist) {
                builder.add("jukeboxPlaylist", attrs, false);
                for (MediaFile mediaFile : playlist.getFiles()) {
                    File coverArt = mediaFileService.getCoverArt(mediaFile);
                    AttributeSet attributes = restBrowseController.createAttributesForMediaFile(player, coverArt, mediaFile);
                    builder.add("entry", attributes, true);
                }
            } else {
                builder.add("jukeboxStatus", attrs, false);
            }

            builder.endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void setRestBrowseController(RESTBrowseController restBrowseController) {
        this.restBrowseController = restBrowseController;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlaylistControlService(PlaylistService playlistControlService) {
        this.playlistControlService = playlistControlService;
    }

    public void setJukeboxService(JukeboxService jukeboxService) {
        this.jukeboxService = jukeboxService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

}
