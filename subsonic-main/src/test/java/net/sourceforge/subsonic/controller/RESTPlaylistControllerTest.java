package net.sourceforge.subsonic.controller;

import static org.apache.commons.io.FilenameUtils.getName;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.MediaFolderService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.PlaylistService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.TranscodingService;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import com.github.hakko.musiccabinet.domain.model.library.MetaData;
import com.github.hakko.musiccabinet.domain.model.library.MetaData.Mediatype;
import com.github.hakko.musiccabinet.domain.model.music.Album;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.util.ResourceUtil;

public class RESTPlaylistControllerTest {

    private RESTPlaylistController playlistController = new RESTPlaylistController();

    private String playlist1 = "/path/to/playlist1.m3u", playlist2 = "/path/to/playlist2.m3u",
            playlist1Id = getName(playlist1);

    private static final String GET_PLAYLISTS = "rest/playlist/getPlaylists.xml";
    private static final String GET_PLAYLIST = "rest/playlist/getPlaylist.xml";
    private static final String UPDATE_PLAYLIST = "rest/playlist/updatePlaylist.xml";

    @Before
    public void setUp() {
        playlistController.setPlayerService(mock(PlayerService.class));

        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void playlist_getPlaylists() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        PlaylistService playlistService = mock(PlaylistService.class);
        when(playlistService.getSavedPlaylists()).thenReturn(new File[]{
                new File(playlist1), new File(playlist2)});
        playlistController.setPlaylistService(playlistService);

        playlistController.getPlaylists(request, response);

        assertXMLEqual(new ResourceUtil(GET_PLAYLISTS).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void playlist_getPlaylist() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("id")).thenReturn(playlist1Id);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        playlistController.setSecurityService(mock(SecurityService.class));
        playlistController.setMediaFileService(mock(MediaFileService.class));
        RESTBrowseController browseController = new RESTBrowseController();
        browseController.setMediaFolderService(mock(MediaFolderService.class));
        browseController.setTranscodingService(mock(TranscodingService.class));
        playlistController.setRestBrowseController(browseController);
        playlistController.setPlaylistService(getPlaylistService());

        playlistController.getPlaylist(request, response);

        assertXMLEqual(new ResourceUtil(GET_PLAYLIST).getContent(), sw.getBuffer().toString());

    }

    @Test
    public void playlist_updatePlaylist() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("playlistId")).thenReturn(playlist1Id);
        when(request.getParameterValues("songIndexToRemove")).thenReturn(new String[]{
                "0", "0"});
        when(request.getParameterValues("songIdToAdd")).thenReturn(new String[]{
                "10", "1", "5", "10"});
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        playlistController.setMediaFileService(mock(MediaFileService.class));
        playlistController.setPlaylistService(getPlaylistService());

        playlistController.updatePlaylist(request, response);

        assertXMLEqual(new ResourceUtil(UPDATE_PLAYLIST).getContent(), sw.getBuffer().toString());
    }

    private PlaylistService getPlaylistService() {
        return new PlaylistService() {
            @Override
            public File getSavedPlaylist(String name) {
                return new File(playlist1);
            }

            @Override
            public void loadPlaylist(Playlist playlist, String name) throws IOException {
                playlist.setName(name);
                playlist.addFiles(Playlist.ADD, getMediaFile());
            }

            @Override
            public void savePlaylist(Playlist playlist) throws IOException {
                assertEquals(4, playlist.getFiles().length);
            }
        };
    }

    private MediaFile getMediaFile() {
        Artist artist = new Artist(5432, "AC/DC");

        Album album = new Album(artist, 11053, "High Voltage");
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
        Track track = new Track(71463, "The Jack", metaData);

        return MediaFileService.getMediaFile(track);
    }

}
