package net.sourceforge.subsonic.controller;

import static java.lang.Short.MAX_VALUE;
import static java.util.Arrays.asList;
import static net.sourceforge.subsonic.controller.RESTBrowseController.ALBUM_ID;
import static net.sourceforge.subsonic.controller.RESTBrowseController.ARTIST_ID;
import static net.sourceforge.subsonic.service.MediaFileService.getMediaFile;
import static net.sourceforge.subsonic.service.SettingsService.DEFAULT_IGNORED_ARTICLES;
import static net.sourceforge.subsonic.service.SettingsService.DEFAULT_INDEX_STRING;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MediaFolder;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.ArtistIndexService;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.MediaFolderService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.TranscodingService;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.hakko.musiccabinet.configuration.SubsonicUri;
import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.library.MetaData;
import com.github.hakko.musiccabinet.domain.model.library.MetaData.Mediatype;
import com.github.hakko.musiccabinet.domain.model.music.Album;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;
import com.github.hakko.musiccabinet.util.ResourceUtil;

public class RESTBrowseControllerTest {

    private RESTBrowseController browseController = new RESTBrowseController();

    private Artist artist;
    private Album album;
    private Track track;
    private int artistId, albumId, trackId;
    private List trackUris = asList(new SubsonicUri(trackId));
    private String user = "username", lastFmUser = "lastFmUsername";

    private static final String ARTISTS = "rest/browse/getArtists.xml";
    private static final String ARTIST = "rest/browse/getArtist.xml";
    private static final String ALBUM = "rest/browse/getAlbum.xml";
    private static final String SONG = "rest/browse/getSong.xml";
    private static final String GET_STARRED = "rest/browse/getStarred.xml";
    private static final String GET_VIDEOS = "rest/browse/getVideos.xml";

    @Before
    public void setUp() {
        PlayerService playerService = mock(PlayerService.class);
        browseController.setPlayerService(playerService);

        MediaFolderService mediaFolderService = mock(MediaFolderService.class);
        when(mediaFolderService.getAllMediaFolders()).thenReturn(new ArrayList<MediaFolder>());
        browseController.setMediaFolderService(mediaFolderService);

        TranscodingService transcodingService = mock(TranscodingService.class);
        browseController.setTranscodingService(transcodingService);

        artist = new Artist(artistId = 5432, "AC/DC");

        album = new Album(artist, albumId = 11053, "High Voltage");
        album.setCoverArtPath("folder.jpg");

        MetaData metaData = new MetaData();
        metaData.setMediaType(Mediatype.MP3);
        metaData.setArtistUri(artist.getUri());
        metaData.setArtist(artist.getName());
        metaData.setAlbum(album.getName());
        metaData.setAlbumUri(album.getUri());
        metaData.setDuration((short) 352);
        metaData.setBitrate((short) 128);
        metaData.setSize((long) 5624132);
        metaData.setDiscNr((short) 1);
        metaData.setTrackNr((short) 1);
        metaData.setPath("/path/to/file.mp3");
        metaData.setYear(2013);
        track = new Track(trackId = 71463, "The Jack", metaData);

        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void browse_getArtists() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        List<Artist> artists = asList(new Artist(5449, "A-Ha"),
                new Artist(5421, "ABBA"), new Artist(5950, "Bob Marley"));

        LibraryBrowserService libraryBrowserService = mock(LibraryBrowserService.class);
        when(libraryBrowserService.getArtists()).thenReturn(artists);
        browseController.setLibraryBrowserService(libraryBrowserService);

        SettingsService settingsService = mock(SettingsService.class);
        when(settingsService.getIgnoredArticles()).thenReturn(DEFAULT_IGNORED_ARTICLES);
        when(settingsService.getIndexString()).thenReturn(DEFAULT_INDEX_STRING);
        ArtistIndexService artistIndexService = new ArtistIndexService();
        artistIndexService.setSettingsService(settingsService);
        browseController.setArtistIndexService(artistIndexService);

        browseController.getArtists(request, response);

        assertXMLEqual(new ResourceUtil(ARTISTS).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void browse_getArtist() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("id")).thenReturn(ARTIST_ID + artistId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        List<net.sourceforge.subsonic.domain.Album> albums = Arrays.asList(
                new net.sourceforge.subsonic.domain.Album(11047, "Back In Black", "1.jpg"),
                new net.sourceforge.subsonic.domain.Album(11048, "Black Ice", "2.jpg"));

        UserSettings userSettings = mock(UserSettings.class);
        when(userSettings.isAlbumOrderByYear()).thenReturn(true);
        when(userSettings.isAlbumOrderAscending()).thenReturn(true);
        SettingsService settingsService = mock(SettingsService.class);
        when(settingsService.getUserSettings(null)).thenReturn(userSettings);
        browseController.setSettingsService(settingsService);

        ArtistInfoService artistInfoService = mock(ArtistInfoService.class);
        when(artistInfoService.getArtistInfo(new SubsonicUri(artistId))).thenReturn(new ArtistInfo(artist));
        browseController.setArtistInfoService(artistInfoService);

        //LibraryBrowserService libraryBrowserService = mock(LibraryBrowserService.class);
        //when(libraryBrowserService.getAlbums(new ArrayList<Album>(), new SubsonicUri(artistId), true, true)).thenReturn(null);
        //browseController.setLibraryBrowserService(libraryBrowserService);

        MediaFileService mediaFileService = mock(MediaFileService.class);
        when(mediaFileService.getAlbums(null, true)).thenReturn(albums);
        browseController.setMediaFileService(mediaFileService);

        browseController.setSecurityService(mock(SecurityService.class));

        browseController.getArtist(request, response);

        assertXMLEqual(new ResourceUtil(ARTIST).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void browse_getAlbum() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("id")).thenReturn(ALBUM_ID + albumId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        MediaFileService mediaFileService = mock(MediaFileService.class);
        when(mediaFileService.getMediaFile(new SubsonicUri(trackId))).thenReturn(
                new MediaFile(0, new File("ACDC/High voltage/ACDC - The Jack.mp3")));
        browseController.setMediaFileService(mediaFileService);

        SettingsService settingsService = mock(SettingsService.class);
        when(settingsService.getRestAlbumName()).thenReturn("$(album)");
        browseController.setSettingsService(settingsService);

        LibraryBrowserService libraryBrowserService = mock(LibraryBrowserService.class);
        when(libraryBrowserService.getAlbum(new SubsonicUri(albumId))).thenReturn(album);
        when(libraryBrowserService.getTracks(album.getTrackUris())).thenReturn(asList(track));
        browseController.setLibraryBrowserService(libraryBrowserService);

        browseController.getAlbum(request, response);

        assertXMLEqual(new ResourceUtil(ALBUM).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void browse_getSong() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("id")).thenReturn("" + trackId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        MediaFileService mediaFileService = mock(MediaFileService.class);
        when(mediaFileService.getMediaFile(new SubsonicUri(trackId))).thenReturn(
                MediaFileService.getMediaFile(track));
        browseController.setMediaFileService(mediaFileService);

        browseController.getSong(request, response);

        assertXMLEqual(new ResourceUtil(SONG).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void browse_getStarred() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        SecurityService securityService = mock(SecurityService.class);
        when(securityService.getCurrentUsername(Mockito.any(HttpServletRequest.class))).thenReturn(user);
        browseController.setSecurityService(securityService);

        SettingsService settingsService = mock(SettingsService.class);
        when(settingsService.getLastFmUsername(user)).thenReturn(lastFmUser);
        browseController.setSettingsService(settingsService);

        LibraryBrowserService libraryBrowserService = mock(LibraryBrowserService.class);
        when(libraryBrowserService.getStarredArtists(lastFmUser, 0, MAX_VALUE, null)).thenReturn(
                asList(new ArtistRecommendation(artist.getName(), new SubsonicUri(artistId))));
        when(libraryBrowserService.getStarredAlbums(lastFmUser, 0, MAX_VALUE, null)).thenReturn(
                asList(album));
        when(libraryBrowserService.getStarredTrackUris(lastFmUser, 0, MAX_VALUE, null)).thenReturn(
                trackUris);
        when(libraryBrowserService.getTracks(trackUris)).thenReturn(asList(track));
        browseController.setLibraryBrowserService(libraryBrowserService);

        MediaFileService mediaFileService = mock(MediaFileService.class);
        when(mediaFileService.getMediaFile(new SubsonicUri(trackId))).thenReturn(getMediaFile(track));
        browseController.setMediaFileService(mediaFileService);

        browseController.getStarred(request, response);

        assertXMLEqual(new ResourceUtil(GET_STARRED).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void browse_getVideos() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        browseController.getVideos(request, response);

        assertXMLEqual(new ResourceUtil(GET_VIDEOS).getContent(), sw.getBuffer().toString());
    }

}
