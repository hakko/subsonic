package net.sourceforge.subsonic.controller;

import static java.lang.Math.max;
import static net.sourceforge.subsonic.util.StringUtil.utf8HexEncode;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.springframework.web.bind.ServletRequestUtils.getRequiredStringParameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MediaFolder;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.ArtistIndexService;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.MediaFolderService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.StatusService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;
import net.sourceforge.subsonic.util.XMLBuilder.AttributeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;
import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.library.Directory;
import com.github.hakko.musiccabinet.domain.model.library.MetaData;
import com.github.hakko.musiccabinet.domain.model.music.Album;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.ArtistRecommendationService;
import com.github.hakko.musiccabinet.service.DirectoryBrowserService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.PlaylistGeneratorService;
import com.github.hakko.musiccabinet.service.TagService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;

public class RESTBrowseController extends RESTAbstractController {

    private SettingsService settingsService;
    private SecurityService securityService;
    private MediaFolderService mediaFolderService;
    private MediaFileService mediaFileService;
    private TranscodingService transcodingService;
    private LeftController leftController;
    private HomeController homeController;
    private StatusService statusService;
    private LibraryBrowserService libraryBrowserService;
    private ArtistIndexService artistIndexService;
    private ArtistInfoService artistInfoService;
    private TagService tagService;
    private PlaylistGeneratorService playlistGeneratorService;
    private ArtistRecommendationService artistRecommendationService;
    private DirectoryBrowserService directoryBrowserService;

    private final Comparator<Track> trackComparator = new Comparator<Track>() {
        @Override
        public int compare(Track t1, Track t2) {
            return Integer.compare(getTrackNr(t1.getMetaData().getTrackNr()),
                    getTrackNr(t2.getMetaData().getTrackNr()));
        }

        private int getTrackNr(Short nr) {
            return nr == null ? 0 : nr.intValue();
        }
    };

    protected static final String GENRE_RADIO_ID = "GR_";
    protected static final String GENRE_RADIO_NAME = "Genre Radio";

    protected static final String ARTIST_RADIO_ID = "AR_";
    protected static final String ARTIST_RADIO_NAME = "Artist Radio";

    protected static final String TOP_TRACKS_ID = "TT_";
    protected static final String TOP_TRACKS_NAME = "Top Tracks";

    protected static final String RELATED_ARTISTS_ID = "RA_";
    protected static final String RELATED_ARTISTS_NAME = "Related Artists";

    protected static final String MEDIA_FOLDERS_ID = "MF_";
    protected static final String MEDIA_FOLDERS_NAME = "Media Folders";

    protected static final String ARTIST_ID = "A";
    protected static final String ALBUM_ID = "-";

    public void getMusicFolders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("musicFolders", false);

        int id = 0;
        builder.add("musicFolder", true,
                new Attribute("id", id), new Attribute("name", "All genres"));
        for (String tag : tagService.getTopTags()) {
            AttributeSet attributes = new AttributeSet();
            attributes.add("id", ++id);
            attributes.add("name", tag);
            builder.add("musicFolder", attributes, true);
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getIndexes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        long ifModifiedSince = ServletRequestUtils.getLongParameter(request, "ifModifiedSince", 0L);
        long lastModified = leftController.getLastModified(request);

        if (lastModified <= ifModifiedSince) {
            builder.endAll();
            response.getWriter().print(builder);
            return;
        }

        builder.add("indexes", "lastModified", lastModified, false);

        int genreId = NumberUtils.toInt(request.getParameter("musicFolderId"));
        LOG.debug("genreId = " + genreId);
        String genre = genreId == 0 ? null : tagService.getTopTags().get(genreId - 1);
        LOG.debug("genre = " + genre);

        builder.add("shortcut", true,
                new Attribute("name", MEDIA_FOLDERS_NAME),
                new Attribute("id", MEDIA_FOLDERS_ID + "-1"));

        if (libraryBrowserService.hasArtists()) {
            Map<String, List<Artist>> indexedArtists =
                    artistIndexService.getIndexedArtists(genre == null ?
                            libraryBrowserService.getArtists() :
                                libraryBrowserService.getArtists(genre, 25));

            if (genre != null) {
                builder.add("shortcut", true,
                        new Attribute("name", GENRE_RADIO_NAME),
                        new Attribute("id", GENRE_RADIO_ID + StringUtil.utf8HexEncode(genre)));
            }

            for (String index : indexedArtists.keySet()) {
                builder.add("index", "name", index, false);
                addArtists(builder, indexedArtists.get(index));
                builder.end();
            }
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getMusicDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id.startsWith(GENRE_RADIO_ID)) {
            String genre = StringUtil.utf8HexDecode(id.substring(GENRE_RADIO_ID.length()));
            LOG.debug("genre radio for " + genre);
            getGenreRadio(genre, request, response);
        } else if (id.startsWith(ARTIST_RADIO_ID)) {
            Uri artistUri = URIUtil.parseURI(id.substring(ARTIST_RADIO_ID.length()));
            LOG.debug("artist radio for " + artistUri);
            getArtistRadio(artistUri, request, response);
        } else if (id.startsWith(TOP_TRACKS_ID)) {
        	Uri artistUri = URIUtil.parseURI(id.substring(TOP_TRACKS_ID.length()));
            LOG.debug("top tracks for " + artistUri);
            getTopTracks(artistUri, request, response);
        } else if (id.startsWith(RELATED_ARTISTS_ID)) {
        	Uri artistUri = URIUtil.parseURI(id.substring(RELATED_ARTISTS_ID.length()));
            LOG.debug("related artists for " + artistUri);
            getRelatedArtists(artistUri, request, response);
        } else if (id.startsWith(MEDIA_FOLDERS_ID)) {
            int directoryId = NumberUtils.toInt(id.substring(MEDIA_FOLDERS_ID.length()));
            LOG.debug("directories for " + directoryId);
            addDirectories(request, response, directoryId);
        } else {
            if (id.startsWith(ARTIST_ID)) {
                getArtistDirectory(request, response);
            } else {
                getAlbumDirectory(request, response);
            }
        }
    }

    public void getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("artists", false);

        Map<String, List<Artist>> indexedArtists = artistIndexService.getIndexedArtists(
                libraryBrowserService.getArtists());
        for (String index : indexedArtists.keySet()) {
            builder.add("index", "name", index, false);
            addArtists(builder, indexedArtists.get(index));
            builder.end();
        }

        builder.endAll();
        response.getWriter().print(builder);
    }
    
    public void getGenres(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("genres", false);

        List<String> tags = tagService.getTopTags();
        for(String tag : tags) {
        	builder.add("genre", (Iterable<Attribute>)null, tag, true);
        }
        builder.endAll();
        response.getWriter().print(builder);
    }
    
    public void getSongsByGenre(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
//        request = wrapRequest(request);
//        Player player = playerService.getPlayer(request, response);
//        String username = securityService.getCurrentUsername(request);
//        String lastFmUsername = settingsService.getLastFmUsername(username);
//        
//        String genre = getRequiredStringParameter(request, "genre");
//        int count = Math.min(500, getIntParameter(request, "count", 10));
//        int offset = getIntParameter(request, "offset", 0);
//
//        XMLBuilder builder = createXMLBuilder(request, response, true);
//        builder.add("songsByGenre", false);
//        
//    	
//    	
//    	libraryBrowserService.getStarredTrackUris(lastFmUsername, offset, count, genre)
    }
    

    public void getArtist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        UserSettings userSettings = settingsService.getUserSettings(securityService.getCurrentUsername(request));

        Uri artistUri = getId(request);

        ArtistInfo artistInfo = artistInfoService.getArtistInfo(artistUri);
        List<net.sourceforge.subsonic.domain.Album> albums = mediaFileService.getAlbums(
                libraryBrowserService.getAlbums(new Artist(artistUri, artistInfo.getArtist().getName()), userSettings.isAlbumOrderByYear(),
                        userSettings.isAlbumOrderAscending()), true);

        builder.add("artist", false,
                new Attribute("id", ARTIST_ID + artistUri),
                new Attribute("name", artistInfo.getArtist().getName()));

        for (net.sourceforge.subsonic.domain.Album album : albums) {
            builder.add("album", true,
                    new Attribute("id", ALBUM_ID + album.getUri()),
                    new Attribute("name", album.getTitle()),
                    new Attribute("coverArt", StringUtil.utf8HexEncode(album.getCoverArtPath())),
                    new Attribute("artist", artistInfo.getArtist().getName()),
                    new Attribute("artistId", ARTIST_ID + artistUri));
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getAlbum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);

        Uri albumUri = getId(request);
        Album album = libraryBrowserService.getAlbum(albumUri);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("album", false,
                new Attribute("id", ALBUM_ID + albumUri),
                new Attribute("name", getAlbumName(album)),
                new Attribute("artist", album.getArtist().getName()),
                new Attribute("artistId", ARTIST_ID + album.getArtist().getUri()));

        List<Track> tracks = libraryBrowserService.getTracks(album.getTrackUris());
        Collections.sort(tracks, trackComparator);
        addTracks(builder, tracks, album, player, "song");

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getSong(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        Uri trackUri = URIUtil.parseURI(ServletRequestUtils.getRequiredStringParameter(request, "id"));
        MediaFile mediaFile = mediaFileService.getMediaFile(trackUri);

        builder.add("song", createAttributesForMediaFile(player, null, mediaFile), true);

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getVideos(HttpServletRequest request, HttpServletResponse response) throws Exception {
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("videos", true);
        builder.endAll();
        response.getWriter().print(builder);
    }

    private void addDirectories(HttpServletRequest request, HttpServletResponse response, int id) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        Player player = playerService.getPlayer(request, response);

        Directory dir = id == -1 ? new Directory(-1, "Media Folders") :
            directoryBrowserService.getDirectory(id);
        Set<Directory> subDirs = id == -1 ?
                directoryBrowserService.getRootDirectories() :
                    directoryBrowserService.getSubDirectories(id);

                builder.add("directory", false,
                        new Attribute("id", MEDIA_FOLDERS_ID + id),
                        new Attribute("name", dir.getName()));

                for (Directory subDir : subDirs) {
                    builder.add("child", true,
                            new Attribute("id", MEDIA_FOLDERS_ID + subDir.getId()),
                            new Attribute("parent", MEDIA_FOLDERS_ID + id),
                            new Attribute("title", subDir.getName()),
                            new Attribute("isDir", true));
                }

                if (id != -1) {
                    UserSettings userSettings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
                    for (Album album : directoryBrowserService.getAlbums(id,
                            userSettings.isAlbumOrderByYear(), userSettings.isAlbumOrderAscending())) {
                        List<Track> tracks = libraryBrowserService.getTracks(album.getTrackUris());
                        Collections.sort(tracks, trackComparator);
                        addTracks(builder, tracks, album, player);
                    }

                    List<String> filenames = directoryBrowserService.getNonAudioFiles(id);
                    String[] videoExtensions = settingsService.getVideoFileTypesAsArray();
                    for (String filename : filenames) {
                        if (FilenameUtils.isExtension(filename, videoExtensions)) {
                            MediaFile mediaFile = mediaFileService.getNonIndexedMediaFile(
                                    dir.getPath() + File.separatorChar + filename);
                            AttributeSet attributes = createAttributesForMediaFile(player, null, mediaFile);
                            builder.add("child", attributes, true);
                        }
                    }
                }

                builder.endAll();
                response.getWriter().print(builder);
    }

    private void getArtistDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Uri artistUri;
        ArtistInfo artistInfo;
        List<net.sourceforge.subsonic.domain.Album> albums;

        UserSettings userSettings = settingsService.getUserSettings(securityService.getCurrentUsername(request));

        try {
            artistUri = getId(request);
            artistInfo = artistInfoService.getArtistInfo(artistUri);
            albums = mediaFileService.getAlbums(libraryBrowserService.getAlbums(new Artist(artistUri, artistInfo.getArtist().getName()),
                    userSettings.isAlbumOrderByYear(), userSettings.isAlbumOrderAscending()), true);
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
            return;
        }

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", ARTIST_ID + artistUri),
                new Attribute("name", artistInfo.getArtist().getName()));

        for (net.sourceforge.subsonic.domain.Album album : albums) {
            builder.add("child", true,
                    new Attribute("id", ALBUM_ID + album.getUri()),
                    new Attribute("parent", ARTIST_ID + artistUri),
                    new Attribute("title", getAlbumName(album.getTitle(), album.getYear())),
                    new Attribute("isDir", true),
                    new Attribute("coverArt", StringUtil.utf8HexEncode(album.getCoverArtPath())));
        }

        builder.add("child", true,
                new Attribute("id", ARTIST_RADIO_ID + artistUri),
                new Attribute("parent", ARTIST_ID + artistUri),
                new Attribute("title", ARTIST_RADIO_NAME),
                new Attribute("isDir", true));

        builder.add("child", true,
                new Attribute("id", TOP_TRACKS_ID + artistUri),
                new Attribute("parent", ARTIST_ID + artistUri),
                new Attribute("title", TOP_TRACKS_NAME),
                new Attribute("isDir", true));

        builder.add("child", true,
                new Attribute("id", RELATED_ARTISTS_ID + artistUri),
                new Attribute("parent", ARTIST_ID + artistUri),
                new Attribute("title", RELATED_ARTISTS_NAME),
                new Attribute("isDir", true));

        builder.endAll();
        response.getWriter().print(builder);

    }

    private void getRelatedArtists(Uri artistUri, HttpServletRequest request, HttpServletResponse response) throws Exception {

        UserSettings userSettings = settingsService.getUserSettings(securityService.getCurrentUsername(request));

        ArtistInfo artistInfo = artistInfoService.getArtistInfo(artistUri);
        List<ArtistRecommendation> relatedArtists = artistRecommendationService.getRelatedArtistsInLibrary(
                artistUri, userSettings.getRelatedArtists(), userSettings.isOnlyAlbumArtistRecommendations());

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", RELATED_ARTISTS_ID + artistUri),
                new Attribute("name", artistInfo.getArtist().getName()));

        for (ArtistRecommendation ar : relatedArtists) {
            builder.add("child", true,
                    new Attribute("id", ARTIST_ID + ar.getArtistUri()),
                    new Attribute("parent", ARTIST_ID + artistUri),
                    new Attribute("title", ar.getArtistName()),
                    new Attribute("isDir", true));
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    private void getAlbumDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Uri albumUri;
        Album album;
        List<Track> tracks;

        Player player = playerService.getPlayer(request, response);

        try {
        	albumUri = getId(request);
            album = libraryBrowserService.getAlbum(albumUri);
            tracks = libraryBrowserService.getTracks(album.getTrackUris());
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
            return;
        }

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", ALBUM_ID + albumUri),
                new Attribute("name", getAlbumName(album)));

        Collections.sort(tracks, trackComparator);
        addTracks(builder, tracks, album, player);

        builder.endAll();
        response.getWriter().print(builder);

    }

    private void getGenreRadio(String genre, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("getGenreRadio() for " + genre);

        List<? extends Uri> trackIds = playlistGeneratorService.getPlaylistForTags(new String[]{genre},
                settingsService.getGenreRadioArtistCount(), settingsService.getGenreRadioTotalCount());
        List<Track> tracks = trackIds.isEmpty() ? new ArrayList<Track>() : libraryBrowserService.getTracks(trackIds);

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", GENRE_RADIO_ID + genre),
                new Attribute("name", GENRE_RADIO_NAME));

        Player player = playerService.getPlayer(request, response);
        addTracks(builder, tracks, null, player);

        builder.endAll();
        response.getWriter().print(builder);

    }

    private void getArtistRadio(Uri artistUri, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("getArtistRadio() for " + artistUri);

        List<? extends Uri> trackUris = playlistGeneratorService.getPlaylistForArtist(artistUri,
                settingsService.getArtistRadioArtistCount(), settingsService.getArtistRadioTotalCount());
        List<Track> tracks = trackUris.isEmpty() ? new ArrayList<Track>() : libraryBrowserService.getTracks(trackUris);
        Collections.shuffle(tracks);

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", ARTIST_RADIO_ID + artistUri),
                new Attribute("name", ARTIST_RADIO_NAME));

        Player player = playerService.getPlayer(request, response);
        addTracks(builder, tracks, null, player);

        builder.endAll();
        response.getWriter().print(builder);

    }

    private void getTopTracks(Uri artistUri, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("getTopTracks() for " + artistUri);

        List<? extends Uri> trackIds = playlistGeneratorService.getTopTracksForArtist(artistUri,
                settingsService.getArtistTopTracksTotalCount());
        List<Track> tracks = trackIds.isEmpty() ? new ArrayList<Track>() : libraryBrowserService.getTracks(trackIds);

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", TOP_TRACKS_ID + artistUri),
                new Attribute("name", TOP_TRACKS_NAME));

        Player player = playerService.getPlayer(request, response);
        addTracks(builder, tracks, null, player);

        builder.endAll();
        response.getWriter().print(builder);

    }

    protected void addArtists(XMLBuilder builder, List<Artist> artists) throws IOException {
        for (Artist artist : artists) {
            builder.add("artist", true,
                    new Attribute("name", artist.getName()),
                    new Attribute("id", ARTIST_ID + artist.getUri()));
        }
    }

    protected void addAlbums(XMLBuilder builder, List<Album> albums) throws IOException {
        for (Album album : albums) {
            builder.add("album", true,
                    new Attribute("id", ALBUM_ID + album.getUri()),
                    new Attribute("parent", ARTIST_ID + album.getArtist().getUri()),
                    new Attribute("title", album.getName()),
                    new Attribute("album", album.getName()),
                    new Attribute("artist", album.getArtist().getName()),
                    new Attribute("isDir", true),
                    new Attribute("coverArt", StringUtil.utf8HexEncode(album.getCoverArtPath())));
        }
    }

    protected void addTracks(XMLBuilder builder, List<Track> tracks, Album album, Player player) throws IOException {
        addTracks(builder, tracks, album, player, "child");
    }

    protected void addTracks(XMLBuilder builder, List<Track> tracks, Album album, Player player, String nodeName) throws IOException {
        String coverArtPath = album == null ? null : album.getCoverArtPath();
        if (album == null || album.getCoverArtPath() == null) {
            libraryBrowserService.addArtwork(tracks);
        }
        mediaFileService.loadMediaFiles(getMediaFileUris(tracks));
        for (Track track : tracks) {
            String suffix = track.getMetaData().getMediaType().getFilesuffix().toLowerCase();
            MediaFile mediaFile = mediaFileService.getMediaFile(track.getUri());

            AttributeSet attributes = new AttributeSet();
            attributes.add(new Attribute("id", track.getUri()));
            attributes.add(new Attribute("parent", ALBUM_ID +
                    (album == null ? track.getMetaData().getAlbumUri() : album.getUri())));
            attributes.add(new Attribute("title", track.getName()));
            attributes.add(new Attribute("album", track.getMetaData().getAlbum()));
            attributes.add(new Attribute("artist", track.getMetaData().getArtist()));
            attributes.add(new Attribute("isDir", false));
            attributes.add(new Attribute("track", track.getMetaData().getTrackNr()));
            attributes.add(new Attribute("discNumber", max(track.getMetaData().getDiscNr(), 1)));
            attributes.add(new Attribute("year", track.getMetaData().getYear()));
            attributes.add(new Attribute("coverArt", utf8HexEncode(
                    defaultIfEmpty(coverArtPath, track.getMetaData().getArtworkPath()))));
            attributes.add(new Attribute("size", track.getMetaData().getSize()));
            attributes.add(new Attribute("contentType", StringUtil.getMimeType(suffix)));
            attributes.add(new Attribute("suffix", suffix));
            attributes.add(new Attribute("duration", track.getMetaData().getDuration()));
            attributes.add(new Attribute("bitRate", track.getMetaData().getBitrate()));
            attributes.add(new Attribute("path", getRelativePath(mediaFile)));

            if (transcodingService.isTranscodingRequired(mediaFile, player)) {
                String transcodingSuffix = transcodingService.getSuffix(player, mediaFile, null);
                attributes.add("transcodedSuffix", transcodingSuffix);
                attributes.add("transcodedContentType", StringUtil.getMimeType(transcodingSuffix));
            }

            builder.add(nodeName, attributes, true);
        }
    }

    public void getAlbumList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        addAlbumList(request, response, "albumList");
    }

    public void getAlbumList2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        addAlbumList(request, response, "albumList2");
    }

    private void addAlbumList(HttpServletRequest request, HttpServletResponse response, String nodeName) throws Exception {
        request = wrapRequest(request);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add(nodeName, false);

        try {
            int size = ServletRequestUtils.getIntParameter(request, "size", 10);
            int offset = ServletRequestUtils.getIntParameter(request, "offset", 0);

            size = Math.max(0, Math.min(size, 500));
            offset = Math.max(0, Math.min(offset, 5000));

            String type = ServletRequestUtils.getRequiredStringParameter(request, "type").toLowerCase();
            String username = securityService.getCurrentUsername(request);
            String lastFmUsername = settingsService.getLastFmUsername(username);

            List<Album> albums = homeController.getAlbums(type, null, offset, size, lastFmUsername);

            if (albums != null) {
                addAlbums(builder, albums);
            }
            builder.endAll();
            response.getWriter().print(builder);
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Throwable t) {
            LOG.warn("Error in REST API.", t);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(t));
        }
    }

    public void getRandomSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("randomSongs", false);

        try {
            int size = ServletRequestUtils.getIntParameter(request, "size", 10);
            size = Math.max(0, Math.min(size, 500));
            //            String genre = ServletRequestUtils.getStringParameter(request, "genre");
            Integer fromYear = ServletRequestUtils.getIntParameter(request, "fromYear");
            Integer toYear = ServletRequestUtils.getIntParameter(request, "toYear");
            int genreId = NumberUtils.toInt(request.getParameter("musicFolderId"));
            LOG.debug("genreId = " + genreId);
            String genre = genreId == 0 ? null : tagService.getTopTags().get(genreId - 1);
            LOG.debug("genre = " + genre);

            List<Track> tracks = libraryBrowserService.getTracks(
                    libraryBrowserService.getRandomTrackUris(size, fromYear, toYear, genre));
            addTracks(builder, tracks, null, player, "song");

            builder.endAll();
            response.getWriter().print(builder);
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void getNowPlaying(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("nowPlaying", false);

        for (TransferStatus status : statusService.getAllStreamStatuses()) {

            Player player = status.getPlayer();
            String file = status.getFile();
            if (player != null && player.getUsername() != null && file != null) {

                String username = player.getUsername();
                UserSettings userSettings = settingsService.getUserSettings(username);
                if (!userSettings.isNowPlayingAllowed()) {
                    continue;
                }

                MediaFile mediaFile = mediaFileService.getMediaFile(status.getMediaFileUri());
                File coverArt = mediaFileService.getCoverArt(mediaFile);

                long minutesAgo = status.getMillisSinceLastUpdate() / 1000L / 60L;
                if (minutesAgo < 60) {
                    AttributeSet attributes = createAttributesForMediaFile(player, coverArt, mediaFile);
                    attributes.add("username", username);
                    attributes.add("playerId", player.getId());
                    if (player.getName() != null) {
                        attributes.add("playerName", player.getName());
                    }
                    attributes.add("minutesAgo", minutesAgo);
                    builder.add("entry", attributes, true);
                }
            }
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getStarred(HttpServletRequest request, HttpServletResponse response) throws Exception {
        addStarred(request, response, "starred");
    }

    public void getStarred2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        addStarred(request, response, "starred2");
    }

    private void addStarred(HttpServletRequest request, HttpServletResponse response, String nodeName) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);
        String username = securityService.getCurrentUsername(request);
        String lastFmUsername = settingsService.getLastFmUsername(username);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add(nodeName, false);
        for (ArtistRecommendation rec : libraryBrowserService.getStarredArtists(
                lastFmUsername, 0, Short.MAX_VALUE, null)) {
            builder.add("artist", true,
                    new Attribute("name", rec.getArtistName()),
                    new Attribute("id", ARTIST_ID + rec.getArtistUri()));
        }
        addAlbums(builder, libraryBrowserService.getStarredAlbums(
                lastFmUsername, 0, Short.MAX_VALUE, null));
        List<Track> tracks = libraryBrowserService.getTracks(libraryBrowserService
                .getStarredTrackUris(lastFmUsername, 0, Short.MAX_VALUE, null));
        addTracks(builder, tracks, null, player, "song");

        builder.endAll();
        response.getWriter().print(builder);
    }

    protected AttributeSet createAttributesForMediaFile(Player player, File coverArt, MediaFile mediaFile) throws IOException {
        AttributeSet attributes = new AttributeSet();
        attributes.add("id", mediaFile.getUri());
        if (!mediaFile.isVideo()) {
            attributes.add("parent", ALBUM_ID + mediaFile.getMetaData().getAlbumUri());
        }
        attributes.add("title", mediaFile.getTitle());
        attributes.add("isDir", false);

        if (mediaFile.isFile()) {
            MetaData metaData = mediaFile.getMetaData();
            Short duration = metaData.getDuration();
            if (duration != null) {
                attributes.add("duration", duration);
            }
            Short bitRate = metaData.getBitrate();
            if (bitRate != null) {
                attributes.add("bitRate", bitRate);
            }

            if (!mediaFile.isVideo()) {
                attributes.add("album", metaData.getAlbum());
                attributes.add("artist", metaData.getArtist());

                Short track = metaData.getTrackNr();
                if (track != null) {
                    attributes.add("track", track);
                }

                Integer year = metaData.getYear();
                if (year != null) {
                    attributes.add("year", year);
                }

                String genre = metaData.getGenre();
                if (genre != null) {
                    attributes.add("genre", genre);
                }
            }

            attributes.add("size", mediaFile.getMetaData().getSize());
            String suffix = mediaFile.getSuffix();
            attributes.add("suffix", suffix);
            attributes.add("contentType", StringUtil.getMimeType(suffix));
            attributes.add("isVideo", mediaFile.isVideo());

            if (coverArt != null) {
                attributes.add("coverArt", StringUtil.utf8HexEncode(coverArt.getPath()));
            }

            if (transcodingService.isTranscodingRequired(mediaFile, player)) {
                String transcodedSuffix = transcodingService.getSuffix(player, mediaFile, null);
                attributes.add("transcodedSuffix", transcodedSuffix);
                attributes.add("transcodedContentType", StringUtil.getMimeType(transcodedSuffix));
            }

            String path = getRelativePath(mediaFile);
            if (path != null) {
                attributes.add("path", path);
            }

        } else {

            File childCoverArt = mediaFileService.getCoverArt(mediaFile);
            if (childCoverArt != null) {
                attributes.add("coverArt", StringUtil.utf8HexEncode(childCoverArt.getPath()));
            }

            String artist = resolveArtist(mediaFile);
            if (artist != null) {
                attributes.add("artist", artist);
            }

        }
        return attributes;
    }

    protected List<Uri> getMediaFileUris(List<Track> tracks) {
        List<Uri> mediaFileIds = new ArrayList<>();
        for (Track track : tracks) {
            mediaFileIds.add(track.getUri());
        }
        return mediaFileIds;
    }

    private Uri getId(HttpServletRequest request) throws ServletRequestBindingException {
        return URIUtil.parseURI(getRequiredStringParameter(request, "id").substring(1));
    }

    private String getRelativePath(MediaFile mediaFile) {

        String filePath = mediaFile.getPath();

        // Convert slashes.
        filePath = filePath.replace('\\', '/');

        String filePathLower = filePath.toLowerCase();

        List<MediaFolder> mediaFolders = mediaFolderService.getIndexedMediaFolders();
        for (MediaFolder mediaFolder : mediaFolders) {
            String folderPath = mediaFolder.getPath().getPath();
            folderPath = folderPath.replace('\\', '/');
            String folderPathLower = folderPath.toLowerCase();

            if (filePathLower.startsWith(folderPathLower)) {
                String relativePath = filePath.substring(folderPath.length());
                return relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
            }
        }

        return filePath;
    }

    private String resolveArtist(MediaFile file) throws IOException {
        return file.getMetaData().getArtist();
    }

    private String getAlbumName(Album album) {
        return getAlbumName(album.getName(), album.getYear());
    }

    private String getAlbumName(String title, Integer year) {
        String albumName = settingsService.getRestAlbumName();
        albumName = StringUtils.replace(albumName, "$(album)", title);
        albumName = StringUtils.replace(albumName, "$(year)",
                year == null || year == 0 ? "" : "" + year);
        return albumName;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFolderService(MediaFolderService mediaFolderService) {
        this.mediaFolderService = mediaFolderService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setLeftController(LeftController leftController) {
        this.leftController = leftController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
        this.libraryBrowserService = libraryBrowserService;
    }

    public void setArtistIndexService(ArtistIndexService artistIndexService) {
        this.artistIndexService = artistIndexService;
    }

    public void setArtistInfoService(ArtistInfoService artistInfoService) {
        this.artistInfoService = artistInfoService;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    public void setPlaylistGeneratorService(PlaylistGeneratorService playlistGeneratorService) {
        this.playlistGeneratorService = playlistGeneratorService;
    }

    public void setArtistRecommendationService(ArtistRecommendationService artistRecommendationService) {
        this.artistRecommendationService = artistRecommendationService;
    }

    public void setDirectoryBrowserService(DirectoryBrowserService directoryBrowserService) {
        this.directoryBrowserService = directoryBrowserService;
    }

}
