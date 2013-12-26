package net.sourceforge.subsonic.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.github.hakko.musiccabinet.domain.model.music.Album;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.INameSearchService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;

public class RESTSearchController extends RESTAbstractController {

    private RESTBrowseController restBrowseController;
    private INameSearchService nameSearchService;
    private LibraryBrowserService libraryBrowserService;

    @Deprecated
    public void search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("searchResult", true,
                new Attribute("offset", 0),
                new Attribute("totalHits", 0));
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void search2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        addSearch(request, response, "search2");
    }

    public void search3(HttpServletRequest request, HttpServletResponse response) throws Exception {
        addSearch(request, response, "search3");
    }

    private void addSearch(HttpServletRequest request, HttpServletResponse response, String nodeName) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        Player player = playerService.getPlayer(request, response);

        builder.add(nodeName, false);

        String query = request.getParameter("query");
        int artistCount = ServletRequestUtils.getIntParameter(request, "artistCount", 20);
        int artistOffset = ServletRequestUtils.getIntParameter(request, "artistOffset", 0);
        List<Artist> artists = nameSearchService.getArtists(StringUtils.trimToEmpty(query),
                artistOffset, artistCount).getResults();
        restBrowseController.addArtists(builder, artists);

        int albumCount = ServletRequestUtils.getIntParameter(request, "albumCount", 20);
        int albumOffset = ServletRequestUtils.getIntParameter(request, "albumOffset", 0);
        List<Album> albums = nameSearchService.getAlbums(StringUtils.trimToEmpty(query),
                albumOffset, albumCount).getResults();
        restBrowseController.addAlbums(builder, albums);

        int songCount = ServletRequestUtils.getIntParameter(request, "songCount", 20);
        int songOffset = ServletRequestUtils.getIntParameter(request, "songOffset", 0);
        List<Track> tracks = nameSearchService.getTracks(StringUtils.trimToEmpty(query),
                songOffset, songCount).getResults();
        List<Integer> mediaFileIds = restBrowseController.getMediaFileIds(tracks);
        restBrowseController.addTracks(builder, libraryBrowserService.getTracks(mediaFileIds), null, player, "song");

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void setRestBrowseController(RESTBrowseController restBrowseController) {
        this.restBrowseController = restBrowseController;
    }

    public void setNameSearchService(INameSearchService nameSearchService) {
        this.nameSearchService = nameSearchService;
    }

    public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
        this.libraryBrowserService = libraryBrowserService;
    }

}
