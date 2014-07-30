package net.sourceforge.subsonic.controller;

import static java.lang.Integer.MAX_VALUE;
import static net.sourceforge.subsonic.controller.RESTBrowseController.ALBUM_ID;
import static net.sourceforge.subsonic.controller.RESTBrowseController.ARTIST_ID;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.service.AudioScrobblerService;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.XMLBuilder;

import org.springframework.web.bind.ServletRequestUtils;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.StarService;

public class RESTMediaAnnotationController extends RESTAbstractController {

    private RESTBrowseController restBrowseController;
    private SettingsService settingsService;
    private SecurityService securityService;
    private StarService starService;
    private HomeController homeController;
    private LibraryBrowserService libraryBrowserService;
    private MediaFileService mediaFileService;
    private AudioScrobblerService audioScrobblerService;

    public void setRating(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
        response.getWriter().print(builder);
    }

    public void star(HttpServletRequest request, HttpServletResponse response) throws Exception {
        starOrUnstar(request, response, true);
    }

    public void unstar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        starOrUnstar(request, response, false);
    }

    private void starOrUnstar(HttpServletRequest request, HttpServletResponse response, boolean star) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        String username = securityService.getCurrentUsername(request);
        String lastFmUsername = settingsService.getLastFmUsername(username);

        for (String id : request.getParameterValues("id")) {
            if (id.startsWith(ARTIST_ID)) {
                LOG.debug("star artist: " + id + ", " + star);
                if (star) {
                    starService.starArtist(lastFmUsername, getId(id));
                } else {
                    starService.unstarArtist(lastFmUsername, getId(id));
                }
            } else if (id.startsWith(ALBUM_ID)) {
                LOG.debug("star album: " + id + ", " + star);
                if (star) {
                    starService.starAlbum(lastFmUsername, getId(id));
                } else {
                    starService.unstarAlbum(lastFmUsername, getId(id));
                }
            } else {
                LOG.debug("star track: " + id + ", " + star);
                if (star) {
                    starService.starTrack(lastFmUsername, URIUtil.parseURI(id));
                } else {
                    starService.unstarTrack(lastFmUsername, URIUtil.parseURI(id));
                }
            }
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getStarred(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);
        String username = securityService.getCurrentUsername(request);
        String lastFmUsername = settingsService.getLastFmUsername(username);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("starred", false);

        if (lastFmUsername != null) {
            restBrowseController.addArtists(builder, starService.getStarredArtists(lastFmUsername));
            restBrowseController.addAlbums(builder, homeController.getAlbums("starred", null, 0, MAX_VALUE, lastFmUsername, null, -1, -1));
            List<Track> tracks = libraryBrowserService.getTracks(
                    libraryBrowserService.getStarredTrackUris(lastFmUsername, 0, MAX_VALUE, null));
            restBrowseController.addTracks(builder, tracks, null, player, "song");
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getStarred2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        getStarred(request, response); // TODO : should return a nested list of <starred2>
    }

    public void scrobble(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        Player player = playerService.getPlayer(request, response);

        if (!settingsService.getUserSettings(player.getUsername()).isLastFmEnabled()) {
            error(request, response, ErrorCode.GENERIC, "Scrobbling is not enabled for " + player.getUsername() + ".");
            return;
        }

        MediaFile file;
        try {
            Uri mediaFileUri = URIUtil.parseURI(ServletRequestUtils.getRequiredStringParameter(request, "id"));
            file = mediaFileService.getMediaFile(mediaFileUri);
            boolean submission = ServletRequestUtils.getBooleanParameter(request, "submission", true);
            audioScrobblerService.scrobble(player.getUsername(), file, submission);
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
            return;
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    private Uri getId(String prefixedId) {
        return URIUtil.parseURI(prefixedId.substring(1));
    }

    public void setRestBrowseController(RESTBrowseController restBrowseController) {
        this.restBrowseController = restBrowseController;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setStarService(StarService starService) {
        this.starService = starService;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
        this.libraryBrowserService = libraryBrowserService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
        this.audioScrobblerService = audioScrobblerService;
    }

}
