package net.sourceforge.subsonic.controller;

import static net.sourceforge.subsonic.domain.Playlist.ADD;

import java.io.File;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlaylistService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;
import net.sourceforge.subsonic.util.XMLBuilder.AttributeSet;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.github.hakko.musiccabinet.dao.util.URIUtil;

public class RESTPlaylistController extends RESTAbstractController {

    private RESTBrowseController restBrowseController;
    private SecurityService securityService;
    private PlaylistService playlistService;
    private MediaFileService mediaFileService;

    public void getPlaylists(HttpServletRequest request, HttpServletResponse response) throws Exception {
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("playlists", false);

        for (File playlist : playlistService.getSavedPlaylists()) {
            String id = FilenameUtils.getName(playlist.getName());
            String name = FilenameUtils.getBaseName(playlist.getName());
            builder.add("playlist", true,
                    new Attribute("id", id),
                    new Attribute("name", name),
                    new Attribute("owner", "admin"),
                    new Attribute("public", true));
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = playerService.getPlayer(request, response);

        XMLBuilder builder = createXMLBuilder(request, response, true);

        try {
            String id = ServletRequestUtils.getRequiredStringParameter(request, "id");
            File file = playlistService.getSavedPlaylist(id);
            if (file == null) {
                throw new Exception("Playlist not found.");
            }
            Playlist playlist = new Playlist();
            playlistService.loadPlaylist(playlist, id);

            builder.add("playlist", false,
                    new Attribute("id", playlist.getName()),
                    new Attribute("name", FilenameUtils.getBaseName(playlist.getName())),
                    new Attribute("owner", "admin"),
                    new Attribute("public", true));
            for (MediaFile mediaFile : playlist.getFiles()) {
                File coverArt = mediaFileService.getCoverArt(mediaFile);
                AttributeSet attributes = restBrowseController.createAttributesForMediaFile(player, coverArt, mediaFile);
                builder.add("entry", attributes, true);
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

    public void createPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request, true);

        User user = securityService.getCurrentUser(request);
        if (!user.isPlaylistRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to create playlists.");
            return;
        }

        try {

            String playlistId = request.getParameter("playlistId");
            String name = request.getParameter("name");
            if (playlistId == null && name == null) {
                error(request, response, ErrorCode.MISSING_PARAMETER, "Playlist ID or name must be specified.");
                return;
            }

            Playlist playlist = new Playlist();
            playlist.setName(playlistId != null ? StringUtil.utf8HexDecode(playlistId) : name);

            String[] ids = ServletRequestUtils.getStringParameters(request, "songId");
            for (String id : ids) {
                playlist.addFiles(Playlist.ADD, mediaFileService.getMediaFile(URIUtil.parseURI(id)));
            }
            playlistService.savePlaylist(playlist);

            XMLBuilder builder = createXMLBuilder(request, response, true);
            builder.endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void deletePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request, true);

        User user = securityService.getCurrentUser(request);
        if (!user.isPlaylistRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to delete playlists.");
            return;
        }

        try {
            String id = ServletRequestUtils.getRequiredStringParameter(request, "id");
            playlistService.deletePlaylist(id);

            XMLBuilder builder = createXMLBuilder(request, response, true);
            builder.endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void updatePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = ServletRequestUtils.getRequiredStringParameter(request, "playlistId");
            Playlist playlist = new Playlist();
            playlistService.loadPlaylist(playlist, id);

            String name = request.getParameter("name");
            if (name != null) {
                playlist.setName(name);
            }

            SortedSet<Integer> indexes = new TreeSet<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer i1, Integer i2) {
                    return i2.compareTo(i1);
                }
            });
            for (int songIndexToRemove : ServletRequestUtils.getIntParameters(request, "songIndexToRemove")) {
                indexes.add(songIndexToRemove);
            }
            for (int index : indexes) {
                playlist.removeFileAt(index);
            }

            for (String trackIdToAdd : ServletRequestUtils.getStringParameters(request, "songIdToAdd")) {
                playlist.addFiles(ADD, mediaFileService.getMediaFile(URIUtil.parseURI(trackIdToAdd)));
            }
            playlistService.savePlaylist(playlist);

            XMLBuilder builder = createXMLBuilder(request, response, true);
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

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

}
