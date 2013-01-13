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

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.max;
import static net.sourceforge.subsonic.security.RESTRequestParameterProcessingFilter.decrypt;
import static net.sourceforge.subsonic.util.StringUtil.utf8HexEncode;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang.math.NumberUtils.toInt;
import static org.springframework.web.bind.ServletRequestUtils.getRequiredStringParameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.ajax.ChatService;
import net.sourceforge.subsonic.command.UserSettingsCommand;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MediaFolder;
import net.sourceforge.subsonic.domain.MetaData;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.PlayerTechnology;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.PodcastChannel;
import net.sourceforge.subsonic.domain.PodcastEpisode;
import net.sourceforge.subsonic.domain.Share;
import net.sourceforge.subsonic.domain.TranscodeScheme;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.ArtistIndexService;
import net.sourceforge.subsonic.service.AudioScrobblerService;
import net.sourceforge.subsonic.service.JukeboxService;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.MediaFolderService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.PlaylistService;
import net.sourceforge.subsonic.service.PodcastService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.ShareService;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.library.Directory;
import com.github.hakko.musiccabinet.domain.model.music.Album;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.ArtistRecommendationService;
import com.github.hakko.musiccabinet.service.DirectoryBrowserService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.NameSearchService;
import com.github.hakko.musiccabinet.service.PlaylistGeneratorService;
import com.github.hakko.musiccabinet.service.StarService;
import com.github.hakko.musiccabinet.service.TagService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;

/**
 * Multi-controller used for the REST API.
 * <p/>
 * For documentation, please refer to api.jsp.
 *
 * @author Sindre Mehus
 */
public class RESTController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(RESTController.class);

    private SettingsService settingsService;
    private SecurityService securityService;
    private MediaFolderService mediaFolderService;
    private PlayerService playerService;
    private MediaFileService mediaFileService;
    private TranscodingService transcodingService;
    private DownloadController downloadController;
    private CoverArtController coverArtController;
    private UserSettingsController userSettingsController;
    private LeftController leftController;
    private HomeController homeController;
    private StatusService statusService;
    private StreamController streamController;
    private HLSController hlsController;
    private ShareService shareService;
    private PlaylistService playlistService;
    private ChatService chatService;
    private net.sourceforge.subsonic.ajax.PlaylistService playlistControlService;
    private JukeboxService jukeboxService;
    private PodcastService podcastService;
    private NameSearchService nameSearchService;
    private LibraryBrowserService libraryBrowserService;
    private ArtistIndexService artistIndexService;
    private ArtistInfoService artistInfoService;
    private AudioScrobblerService audioScrobblerService;
    private TagService tagService;
    private PlaylistGeneratorService playlistGeneratorService;
    private ArtistRecommendationService artistRecommendationService;
    private DirectoryBrowserService directoryBrowserService;
    private StarService starService;
    
    private static final String GENRE_RADIO_ID = "GR_";
    private static final String GENRE_RADIO_NAME = "Genre Radio";

    private static final String ARTIST_RADIO_ID = "AR_";
    private static final String ARTIST_RADIO_NAME = "Artist Radio";

    private static final String TOP_TRACKS_ID = "TT_";
    private static final String TOP_TRACKS_NAME = "Top Tracks";

    private static final String RELATED_ARTISTS_ID = "RA_";
    private static final String RELATED_ARTISTS_NAME = "Related Artists";
    
    private static final String MEDIA_FOLDERS_ID = "MF_";
    private static final String MEDIA_FOLDERS_NAME = "Media Folders";

    private static final String ARTIST_ID = "A";
    private static final String ALBUM_ID = "-";
    
    private Comparator<Track> trackComparator = new Comparator<Track>() {
		@Override
		public int compare(Track t1, Track t2) {
			return Integer.compare(getTrackNr(t1.getMetaData().getTrackNr()), 
					getTrackNr(t2.getMetaData().getTrackNr()));
		}
		
		private int getTrackNr(Short nr) {
			return nr == null ? 0 : nr.intValue();
		}
	};
    
    public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
        response.getWriter().print(builder);
    }

    public void getLicense(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        String email = settingsService.getLicenseEmail();
        String key = settingsService.getLicenseCode();
        Date date = settingsService.getLicenseDate();
        boolean valid = settingsService.isLicenseValid();

        AttributeSet attributes = new AttributeSet();
        attributes.add("valid", true);
        if (valid) {
            attributes.add("email", email);
            attributes.add("key", key);
            attributes.add("date", StringUtil.toISO8601(date));
        }

        builder.add("license", attributes, true);
        builder.endAll();
        response.getWriter().print(builder);
    }

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
    		String artistId = id.substring(ARTIST_RADIO_ID.length());
    		LOG.debug("artist radio for " + artistId);
    		getArtistRadio(toInt(artistId), request, response);
    	} else if (id.startsWith(TOP_TRACKS_ID)) {
    		String artistId = id.substring(TOP_TRACKS_ID.length());
    		LOG.debug("top tracks for " + artistId);
    		getTopTracks(toInt(artistId), request, response);
    	} else if (id.startsWith(RELATED_ARTISTS_ID)) {
    		String artistId = id.substring(RELATED_ARTISTS_ID.length());
    		LOG.debug("related artists for " + artistId);
    		getRelatedArtists(toInt(artistId), request, response);
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

    public void addDirectories(HttpServletRequest request, HttpServletResponse response, int id) throws Exception {
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
        	UserSettings userSettings = settingsService.getUserSettings(request.getParameter("u"));
        	for (Album album : directoryBrowserService.getAlbums(id, 
        			userSettings.isAlbumOrderByYear(), userSettings.isAlbumOrderAscending())) {
        		List<Track> tracks = libraryBrowserService.getTracks(album.getTrackIds());
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
        int artistId;
        ArtistInfo artistInfo;
        List<net.sourceforge.subsonic.domain.Album> albums;

    	UserSettings userSettings = settingsService.getUserSettings(request.getParameter("u"));

        try {
            artistId = getId(request);
            artistInfo = artistInfoService.getArtistInfo(artistId);
            albums = mediaFileService.getAlbums(libraryBrowserService.getAlbums(artistId, 
            		userSettings.isAlbumOrderByYear(), userSettings.isAlbumOrderAscending()), true);
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
            return;
        }

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", ARTIST_ID + artistId),
                new Attribute("name", artistInfo.getArtist().getName()));

        for (net.sourceforge.subsonic.domain.Album album : albums) {
            builder.add("child", true,
        		new Attribute("id", ALBUM_ID + album.getId()),
        		new Attribute("parent", ARTIST_ID + artistId),
        		new Attribute("title", album.getTitle()),
        		new Attribute("isDir", true),
        		new Attribute("coverArt", StringUtil.utf8HexEncode(album.getCoverArtPath())));
        }
        
        builder.add("child", true, 
        		new Attribute("id", ARTIST_RADIO_ID + artistId),
        		new Attribute("parent", ARTIST_ID + artistId),
        		new Attribute("title", ARTIST_RADIO_NAME),
        		new Attribute("isDir", true));

        builder.add("child", true, 
        		new Attribute("id", TOP_TRACKS_ID + artistId),
        		new Attribute("parent", ARTIST_ID + artistId),
        		new Attribute("title", TOP_TRACKS_NAME),
        		new Attribute("isDir", true));

        builder.add("child", true, 
        		new Attribute("id", RELATED_ARTISTS_ID + artistId),
        		new Attribute("parent", ARTIST_ID + artistId),
        		new Attribute("title", RELATED_ARTISTS_NAME),
        		new Attribute("isDir", true));

        builder.endAll();
        response.getWriter().print(builder);

    }

    private void getRelatedArtists(int artistId, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	UserSettings userSettings = settingsService.getUserSettings(request.getParameter("u"));
    	
    	ArtistInfo artistInfo = artistInfoService.getArtistInfo(artistId);
    	List<ArtistRecommendation> relatedArtists = artistRecommendationService.getRelatedArtistsInLibrary(
    			artistId, userSettings.getRelatedArtists(), userSettings.isOnlyAlbumArtistRecommendations());
    	
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", RELATED_ARTISTS_ID + artistId),
                new Attribute("name", artistInfo.getArtist().getName()));

        for (ArtistRecommendation ar : relatedArtists) {
            builder.add("child", true,
        		new Attribute("id", ARTIST_ID + ar.getArtistId()),
        		new Attribute("parent", ARTIST_ID + artistId),
        		new Attribute("title", ar.getArtistName()),
        		new Attribute("isDir", true));
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    private void getAlbumDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int albumId;
        Album album;
        List<Track> tracks;

        Player player = playerService.getPlayer(request, response);
        
        try {
            albumId = getId(request);
            album = libraryBrowserService.getAlbum(albumId);
            tracks = libraryBrowserService.getTracks(album.getTrackIds());
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
            return;
        }

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", ALBUM_ID + albumId),
                new Attribute("name", album.getName()));

        Collections.sort(tracks, trackComparator);
        addTracks(builder, tracks, album, player);
        
        builder.endAll();
        response.getWriter().print(builder);

    }

    private void getGenreRadio(String genre, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	LOG.debug("getGenreRadio() for " + genre);

    	List<Integer> trackIds = playlistGeneratorService.getPlaylistForTags(new String[]{genre}, 
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

    private void getArtistRadio(int artistId, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	LOG.debug("getArtistRadio() for " + artistId);

    	List<Integer> trackIds = playlistGeneratorService.getPlaylistForArtist(artistId, 
    			settingsService.getArtistRadioArtistCount(), settingsService.getArtistRadioTotalCount());
        List<Track> tracks = trackIds.isEmpty() ? new ArrayList<Track>() : libraryBrowserService.getTracks(trackIds);
        Collections.shuffle(tracks);
        
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", ARTIST_RADIO_ID + artistId),
                new Attribute("name", ARTIST_RADIO_NAME));

        Player player = playerService.getPlayer(request, response);
        addTracks(builder, tracks, null, player);
        
        builder.endAll();
        response.getWriter().print(builder);

    }

    private void getTopTracks(int artistId, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	LOG.debug("getTopTracks() for " + artistId);

    	List<Integer> trackIds = playlistGeneratorService.getTopTracksForArtist(artistId, 
    			settingsService.getArtistTopTracksTotalCount());
        List<Track> tracks = trackIds.isEmpty() ? new ArrayList<Track>() : libraryBrowserService.getTracks(trackIds);
        
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("directory", false,
                new Attribute("id", TOP_TRACKS_ID + artistId),
                new Attribute("name", TOP_TRACKS_NAME));

        Player player = playerService.getPlayer(request, response);
        addTracks(builder, tracks, null, player);
        
        builder.endAll();
        response.getWriter().print(builder);

    }

    @Deprecated
    public void search(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	LOG.debug("search!");
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("searchResult", true,
                new Attribute("offset", 0),
                new Attribute("totalHits", 0));
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void search2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        Player player = playerService.getPlayer(request, response);

        builder.add("searchResult2", false);

        String query = request.getParameter("query");
        int artistCount = ServletRequestUtils.getIntParameter(request, "artistCount", 20);
        int artistOffset = ServletRequestUtils.getIntParameter(request, "artistOffset", 0);
        List<Artist> artists = nameSearchService.getArtists(StringUtils.trimToEmpty(query), 
        		artistOffset, artistCount).getResults();
        addArtists(builder, artists);

        int albumCount = ServletRequestUtils.getIntParameter(request, "albumCount", 20);
        int albumOffset = ServletRequestUtils.getIntParameter(request, "albumOffset", 0);
        List<Album> albums = nameSearchService.getAlbums(StringUtils.trimToEmpty(query),
        		albumOffset, albumCount).getResults();
        addAlbums(builder, albums);
        
        int songCount = ServletRequestUtils.getIntParameter(request, "songCount", 20);
        int songOffset = ServletRequestUtils.getIntParameter(request, "songOffset", 0);
        List<Track> tracks = nameSearchService.getTracks(StringUtils.trimToEmpty(query),
        		songOffset, songCount).getResults();
        List<Integer> mediaFileIds = getMediaFileIds(tracks);
        addTracks(builder, libraryBrowserService.getTracks(mediaFileIds), null, player, "song");

        builder.endAll();
        response.getWriter().print(builder);
    }
    
    private void addArtists(XMLBuilder builder, List<Artist> artists) throws IOException {
    	for (Artist artist : artists) {
    		builder.add("artist", true,
    				new Attribute("name", artist.getName()),
    				new Attribute("id", ARTIST_ID + artist.getId()));
    	}
    }
    
    private void addAlbums(XMLBuilder builder, List<Album> albums) throws IOException {
        for (Album album : albums) {
            builder.add("album", true,
                    new Attribute("id", ALBUM_ID + album.getId()),
                    new Attribute("parent", ARTIST_ID + album.getArtist().getId()),
                    new Attribute("title", album.getName()),
                    new Attribute("album", album.getName()),
                    new Attribute("artist", album.getArtist().getName()),
                    new Attribute("isDir", true),
                    new Attribute("coverArt", StringUtil.utf8HexEncode(album.getCoverArtPath())));
        }
    }

    private void addTracks(XMLBuilder builder, List<Track> tracks, Album album, Player player) throws IOException {
    	addTracks(builder, tracks, album, player, "child");
    }

    private void addTracks(XMLBuilder builder, List<Track> tracks, Album album, Player player, String nodeName) throws IOException {
    	String coverArtPath = album == null ? null : album.getCoverArtPath();
    	if (album == null || album.getCoverArtPath() == null) {
    		libraryBrowserService.addArtwork(tracks);
    	}
    	mediaFileService.loadMediaFiles(getMediaFileIds(tracks));
        for (Track track : tracks) {
        	String suffix = track.getMetaData().getMediaType().getFilesuffix().toLowerCase();
            MediaFile mediaFile = mediaFileService.getMediaFile(track.getId());
            
        	AttributeSet attributes = new AttributeSet();
            attributes.add(new Attribute("id", track.getId()));
            attributes.add(new Attribute("parent", ALBUM_ID + 
            		(album == null ? track.getMetaData().getAlbumId() : album.getId())));
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
            attributes.add(new Attribute("bitrate", track.getMetaData().getBitrate()));
            attributes.add(new Attribute("path", getRelativePath(mediaFile)));
            
            if (transcodingService.isTranscodingRequired(mediaFile, player)) {
            	String transcodingSuffix = transcodingService.getSuffix(player, mediaFile, null);
            	attributes.add("transcodedSuffix", transcodingSuffix);
            	attributes.add("transcodedContentType", StringUtil.getMimeType(transcodingSuffix));
            }
            
            builder.add(nodeName, attributes, true);
        }
    }

    private List<Integer> getMediaFileIds(List<Track> tracks) {
    	List<Integer> mediaFileIds = new ArrayList<Integer>();
    	for (Track track : tracks) {
    		mediaFileIds.add(track.getId());
    	}
    	return mediaFileIds;
    }
    
    private int getId(HttpServletRequest request) throws ServletRequestBindingException {
    	return toInt(getRequiredStringParameter(request, "id").substring(1));
    }
    
    private int getId(String prefixedId) {
    	return toInt(prefixedId.substring(1));
    }

    public void getPlaylists(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("playlists", false);

        for (File playlist : playlistService.getSavedPlaylists()) {
            String id = StringUtil.utf8HexEncode(playlist.getName());
            String name = FilenameUtils.getBaseName(playlist.getName());
            builder.add("playlist", true, new Attribute("id", id), new Attribute("name", name));
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);

        XMLBuilder builder = createXMLBuilder(request, response, true);

        try {
            String id = StringUtil.utf8HexDecode(ServletRequestUtils.getRequiredStringParameter(request, "id"));
            File file = playlistService.getSavedPlaylist(id);
            if (file == null) {
                throw new Exception("Playlist not found.");
            }
            Playlist playlist = new Playlist();
            playlistService.loadPlaylist(playlist, id);

            builder.add("playlist", false, new Attribute("id", StringUtil.utf8HexEncode(playlist.getName())),
                    new Attribute("name", FilenameUtils.getBaseName(playlist.getName())));
            for (MediaFile mediaFile : playlist.getFiles()) {
                File coverArt = mediaFileService.getCoverArt(mediaFile);
                AttributeSet attributes = createAttributesForMediaFile(player, coverArt, mediaFile);
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
                    AttributeSet attributes = createAttributesForMediaFile(player, coverArt, mediaFile);
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
                playlist.addFiles(Playlist.ADD, mediaFileService.getMediaFile(NumberUtils.toInt(id)));
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
            String id = StringUtil.utf8HexDecode(ServletRequestUtils.getRequiredStringParameter(request, "id"));
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

    public void getAlbumList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("albumList", false);

        try {
            int size = ServletRequestUtils.getIntParameter(request, "size", 10);
            int offset = ServletRequestUtils.getIntParameter(request, "offset", 0);

            size = Math.max(0, Math.min(size, 500));
            offset = Math.max(0, Math.min(offset, 5000));

            String type = ServletRequestUtils.getRequiredStringParameter(request, "type").toLowerCase();
            String username = ServletRequestUtils.getRequiredStringParameter(request, "u");
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
            		libraryBrowserService.getRandomTrackIds(size, fromYear, toYear, genre));
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
            File file = status.getFile();
            if (player != null && player.getUsername() != null && file != null) {

                String username = player.getUsername();
                UserSettings userSettings = settingsService.getUserSettings(username);
                if (!userSettings.isNowPlayingAllowed()) {
                    continue;
                }

                MediaFile mediaFile = mediaFileService.getMediaFile(status.getMediaFileId());
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

    private AttributeSet createAttributesForMediaFile(Player player, File coverArt, MediaFile mediaFile) throws IOException {
        AttributeSet attributes = new AttributeSet();
        attributes.add("id", mediaFile.getId());
        if (!mediaFile.isVideo()) {
            attributes.add("parent", ALBUM_ID + mediaFile.getMetaData().getAlbumId());
        }
        attributes.add("title", mediaFile.getTitle());
        attributes.add("isDir", false);

        if (mediaFile.isFile()) {
            MetaData metaData = mediaFile.getMetaData();
            Integer duration = metaData.getDuration();
            if (duration != null) {
                attributes.add("duration", duration);
            }
            Integer bitRate = metaData.getBitRate();
            if (bitRate != null) {
                attributes.add("bitRate", bitRate);
            }

            if (!mediaFile.isVideo()) {
                attributes.add("album", metaData.getAlbum());
                attributes.add("artist", metaData.getArtist());

                Integer track = metaData.getTrackNumber();
                if (track != null) {
                    attributes.add("track", track);
                }

                Integer year = metaData.getYearAsInteger();
                if (year != null) {
                    attributes.add("year", year);
                }

                String genre = metaData.getGenre();
                if (genre != null) {
                    attributes.add("genre", genre);
                }
            }

            attributes.add("size", mediaFile.length());
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

    private String resolveArtist(MediaFile file) throws IOException {
        return file.getMetaData().getArtist();
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

    public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        request = wrapRequest(request);
        User user = securityService.getCurrentUser(request);
        if (!user.isDownloadRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to download files.");
            return null;
        }

        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        long lastModified = downloadController.getLastModified(request);

        if (ifModifiedSince != -1 && lastModified != -1 && lastModified <= ifModifiedSince) {
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return null;
        }

        if (lastModified != -1) {
            response.setDateHeader("Last-Modified", lastModified);
        }

        return downloadController.handleRequest(request, response);
    }

    public ModelAndView stream(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        User user = securityService.getCurrentUser(request);
        if (!user.isStreamRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to play files.");
            return null;
        }

        LOG.debug("got request " + request + ", [id] = " + request.getParameter("id"));
        request.setAttribute("mfId", request.getParameter("id"));
        
        streamController.handleRequest(request, response);
        return null;
    }

    public ModelAndView hls(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        User user = securityService.getCurrentUser(request);
        if (!user.isStreamRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to play files.");
            return null;
        }
        hlsController.handleRequest(request, response);
        return null;
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
            int mediaFileId = NumberUtils.toInt(ServletRequestUtils.getRequiredStringParameter(request, "id"));
            file = mediaFileService.getMediaFile(mediaFileId);
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

    public void getPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);
        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("podcasts", false);

        for (PodcastChannel channel : podcastService.getAllChannels()) {
            AttributeSet channelAttrs = new AttributeSet();
            channelAttrs.add("id", channel.getId());
            channelAttrs.add("url", channel.getUrl());
            channelAttrs.add("status", channel.getStatus().toString().toLowerCase());
            if (channel.getTitle() != null) {
                channelAttrs.add("title", channel.getTitle());
            }
            if (channel.getDescription() != null) {
                channelAttrs.add("description", channel.getDescription());
            }
            if (channel.getErrorMessage() != null) {
                channelAttrs.add("errorMessage", channel.getErrorMessage());
            }
            builder.add("channel", channelAttrs, false);

            List<PodcastEpisode> episodes = podcastService.getEpisodes(channel.getId(), false);
            for (PodcastEpisode episode : episodes) {
                AttributeSet episodeAttrs = new AttributeSet();

                String path = episode.getPath();
                if (path != null) {
                    MediaFile mediaFile = mediaFileService.getNonIndexedMediaFile(path);
                    File coverArt = mediaFileService.getCoverArt(mediaFile);
                    episodeAttrs.addAll(createAttributesForMediaFile(player, coverArt, mediaFile));
                    episodeAttrs.add("streamId", StringUtil.utf8HexEncode(mediaFile.getPath()));
                }

                episodeAttrs.add("id", episode.getId());  // Overwrites the previous "id" attribute.
                episodeAttrs.add("status", episode.getStatus().toString().toLowerCase());

                if (episode.getTitle() != null) {
                    episodeAttrs.add("title", episode.getTitle());
                }
                if (episode.getDescription() != null) {
                    episodeAttrs.add("description", episode.getDescription());
                }
                if (episode.getPublishDate() != null) {
                    episodeAttrs.add("publishDate", episode.getPublishDate());
                }

                builder.add("episode", episodeAttrs, true);
            }

            builder.end(); // <channel>
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getShares(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);

        User user = securityService.getCurrentUser(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("shares", false);
        for (Share share : shareService.getSharesForUser(user)) {
            builder.add("share", createAttributesForShare(share), false);

            for (MediaFile mediaFile : shareService.getSharedFiles(share.getId())) {
                File coverArt = mediaFileService.getCoverArt(mediaFile);
                AttributeSet attributes = createAttributesForMediaFile(player, coverArt, mediaFile);
                builder.add("entry", attributes, true);
            }

            builder.end();
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);

        User user = securityService.getCurrentUser(request);
        if (!user.isShareRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to share media.");
            return;
        }

        XMLBuilder builder = createXMLBuilder(request, response, true);

        try {

            List<MediaFile> files = new ArrayList<MediaFile>();
            for (String id : ServletRequestUtils.getRequiredStringParameters(request, "id")) {
                MediaFile file = mediaFileService.getMediaFile(NumberUtils.toInt(id));
                files.add(file);
            }

            // TODO: Update api.jsp

            Share share = shareService.createShare(request, files);
            share.setDescription(request.getParameter("description"));
            long expires = ServletRequestUtils.getLongParameter(request, "expires", 0L);
            if (expires != 0) {
                share.setExpires(new Date(expires));
            }
            shareService.updateShare(share);

            builder.add("shares", false);
            builder.add("share", createAttributesForShare(share), false);

            for (MediaFile mediaFile : shareService.getSharedFiles(share.getId())) {
                File coverArt = mediaFileService.getCoverArt(mediaFile);
                AttributeSet attributes = createAttributesForMediaFile(player, coverArt, mediaFile);
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

    public void deleteShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            request = wrapRequest(request);
            User user = securityService.getCurrentUser(request);
            int id = ServletRequestUtils.getRequiredIntParameter(request, "id");

            Share share = shareService.getShareById(id);
            if (share == null) {
                error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
                return;
            }
            if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
                error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to delete shared media.");
                return;
            }

            shareService.deleteShare(id);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void updateShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            request = wrapRequest(request);
            User user = securityService.getCurrentUser(request);
            int id = ServletRequestUtils.getRequiredIntParameter(request, "id");

            Share share = shareService.getShareById(id);
            if (share == null) {
                error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
                return;
            }
            if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
                error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to modify shared media.");
                return;
            }

            share.setDescription(request.getParameter("description"));
            String expiresString = request.getParameter("expires");
            if (expiresString != null) {
                long expires = Long.parseLong(expiresString);
                share.setExpires(expires == 0L ? null : new Date(expires));
            }
            shareService.updateShare(share);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

            } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    private List<Attribute> createAttributesForShare(Share share) {
        List<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute("id", share.getId()));
        attributes.add(new Attribute("url", shareService.getShareUrl(share)));
        attributes.add(new Attribute("username", share.getUsername()));
        attributes.add(new Attribute("created", StringUtil.toISO8601(share.getCreated())));
        attributes.add(new Attribute("visitCount", share.getVisitCount()));
        attributes.add(new Attribute("description", share.getDescription()));
        attributes.add(new Attribute("expires", StringUtil.toISO8601(share.getExpires())));
        attributes.add(new Attribute("lastVisited", StringUtil.toISO8601(share.getLastVisited())));

        return attributes;
    }

    public ModelAndView videoPlayer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        Map<String, Object> map = new HashMap<String, Object>();
        int id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        MediaFile file = mediaFileService.getMediaFile(id);

        int timeOffset = ServletRequestUtils.getIntParameter(request, "timeOffset", 0);
        timeOffset = Math.max(0, timeOffset);
        Integer duration = file.getMetaData().getDuration();
        if (duration != null) {
            map.put("skipOffsets", VideoPlayerController.createSkipOffsets(duration));
            timeOffset = Math.min(duration, timeOffset);
            duration -= timeOffset;
        }

        map.put("id", request.getParameter("id"));
        map.put("u", request.getParameter("u"));
        map.put("p", request.getParameter("p"));
        map.put("c", request.getParameter("c"));
        map.put("v", request.getParameter("v"));
        map.put("video", file);
        map.put("maxBitRate", ServletRequestUtils.getIntParameter(request, "maxBitRate", VideoPlayerController.DEFAULT_BIT_RATE));
        map.put("duration", duration);
        map.put("timeOffset", timeOffset);
        map.put("bitRates", VideoPlayerController.BIT_RATES);
        map.put("autoplay", ServletRequestUtils.getBooleanParameter(request, "autoplay", true));

        ModelAndView result = new ModelAndView("rest/videoPlayer");
        result.addObject("model", map);
        return result;
    }

    public ModelAndView getCoverArt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        return coverArtController.handleRequest(request, response);
    }

    public void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        try {

            String username = ServletRequestUtils.getRequiredStringParameter(request, "username");
            String password = decrypt(ServletRequestUtils.getRequiredStringParameter(request, "password"));

            User authUser = securityService.getCurrentUser(request);
            if (!authUser.isAdminRole() && !username.equals(authUser.getUsername())) {
                error(request, response, ErrorCode.NOT_AUTHORIZED, authUser.getUsername() + " is not authorized to change password for " + username);
                return;
            }

            User user = securityService.getUserByName(username);
            user.setPassword(password);
            securityService.setSecurePassword(user);
            securityService.updateUser(user);

            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void getUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        String username;
        try {
            username = ServletRequestUtils.getRequiredStringParameter(request, "username");
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
            return;
        }

        User currentUser = securityService.getCurrentUser(request);
        if (!username.equals(currentUser.getUsername()) && !currentUser.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, currentUser.getUsername() + " is not authorized to get details for other users.");
            return;
        }

        User requestedUser = securityService.getUserByName(username);
        if (requestedUser == null) {
            error(request, response, ErrorCode.NOT_FOUND, "No such user: " + username);
            return;
        }

        UserSettings userSettings = settingsService.getUserSettings(username);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        List<Attribute> attributes = Arrays.asList(
                new Attribute("username", requestedUser.getUsername()),
                new Attribute("email", requestedUser.getEmail()),
                new Attribute("scrobblingEnabled", userSettings.isLastFmEnabled()),
                new Attribute("adminRole", requestedUser.isAdminRole()),
                new Attribute("settingsRole", requestedUser.isSettingsRole()),
                new Attribute("downloadRole", requestedUser.isDownloadRole()),
                new Attribute("uploadRole", requestedUser.isUploadRole()),
                new Attribute("playlistRole", requestedUser.isPlaylistRole()),
                new Attribute("coverArtRole", requestedUser.isCoverArtRole()),
                new Attribute("commentRole", requestedUser.isCommentRole()),
                new Attribute("podcastRole", requestedUser.isPodcastRole()),
                new Attribute("streamRole", requestedUser.isStreamRole()),
                new Attribute("jukeboxRole", requestedUser.isJukeboxRole()),
                new Attribute("shareRole", requestedUser.isShareRole())
        );

        builder.add("user", attributes, true);
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void createUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to create new users.");
            return;
        }

        try {
            UserSettingsCommand command = new UserSettingsCommand();
            command.setUsername(ServletRequestUtils.getRequiredStringParameter(request, "username"));
            command.setPassword(decrypt(ServletRequestUtils.getRequiredStringParameter(request, "password")));
            command.setEmail(ServletRequestUtils.getRequiredStringParameter(request, "email"));
            command.setLdapAuthenticated(ServletRequestUtils.getBooleanParameter(request, "ldapAuthenticated", false));
            command.setAdminRole(ServletRequestUtils.getBooleanParameter(request, "adminRole", false));
            command.setCommentRole(ServletRequestUtils.getBooleanParameter(request, "commentRole", false));
            command.setCoverArtRole(ServletRequestUtils.getBooleanParameter(request, "coverArtRole", false));
            command.setDownloadRole(ServletRequestUtils.getBooleanParameter(request, "downloadRole", false));
            command.setStreamRole(ServletRequestUtils.getBooleanParameter(request, "streamRole", true));
            command.setUploadRole(ServletRequestUtils.getBooleanParameter(request, "uploadRole", false));
            command.setJukeboxRole(ServletRequestUtils.getBooleanParameter(request, "jukeboxRole", false));
            command.setPlaylistRole(ServletRequestUtils.getBooleanParameter(request, "playlistRole", false));
            command.setPodcastRole(ServletRequestUtils.getBooleanParameter(request, "podcastRole", false));
            command.setSettingsRole(ServletRequestUtils.getBooleanParameter(request, "settingsRole", true));
            command.setTranscodeSchemeName(ServletRequestUtils.getStringParameter(request, "transcodeScheme", TranscodeScheme.OFF.name()));

            userSettingsController.createUser(command);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to delete users.");
            return;
        }

        try {
            String username = ServletRequestUtils.getRequiredStringParameter(request, "username");
            securityService.deleteUser(username);

            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void getChatMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        long since = ServletRequestUtils.getLongParameter(request, "since", 0L);

        builder.add("chatMessages", false);

        for (ChatService.Message message : chatService.getMessages(0L).getMessages()) {
            long time = message.getDate().getTime();
            if (time > since) {
                builder.add("chatMessage", true, new Attribute("username", message.getUsername()),
                        new Attribute("time", time), new Attribute("message", message.getContent()));
            }
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void addChatMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        try {
            chatService.doAddMessage(ServletRequestUtils.getRequiredStringParameter(request, "message"), request);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        }
    }

    public void getLyrics(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        String artist = request.getParameter("artist");
        String title = request.getParameter("title");
        // TODO : look up track id by artist + title
//        LyricsInfo lyrics = lyricsService.getLyrics(artist, title);

        XMLBuilder builder = createXMLBuilder(request, response, true);
        AttributeSet attributes = new AttributeSet();
//        if (lyrics.getArtist() != null) {
//            attributes.add("artist", lyrics.getArtist());
//        }
//        if (lyrics.getTitle() != null) {
//            attributes.add("title", lyrics.getTitle());
//        }
        builder.add("lyrics", attributes, null, true);

        builder.endAll();
        response.getWriter().print(builder);
    }

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

        String username = securityService.getCurrentUser(request).getUsername();
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
                	starService.starTrack(lastFmUsername, toInt(id));
                } else {
                	starService.unstarTrack(lastFmUsername, toInt(id));
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
        	addArtists(builder, starService.getStarredArtists(lastFmUsername));
        	addAlbums(builder, homeController.getAlbums("starred", null, 0, MAX_VALUE, lastFmUsername));
        	List<Track> tracks = libraryBrowserService.getTracks(
        			libraryBrowserService.getStarredTrackIds(lastFmUsername, 0, MAX_VALUE, null));
        	addTracks(builder, tracks, null, player, "song");
        }

        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getStarred2(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	getStarred(request, response);
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request) {
        return wrapRequest(request, false);
    }

    private HttpServletRequest wrapRequest(final HttpServletRequest request, boolean jukebox) {
        final String playerId = createPlayerIfNecessary(request, jukebox);
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {

                // Renames "id" request parameter to "path".
                if ("path".equals(name)) {
                    try {
                        return StringUtil.utf8HexDecode(request.getParameter("id"));
                    } catch (Exception e) {
                        return null;
                    }
                }

                // Returns the correct player to be used in PlayerService.getPlayer()
                else if ("player".equals(name)) {
                    return playerId;
                }

                return super.getParameter(name);
            }
        };
    }

    private String getErrorMessage(Throwable t) {
        if (t.getMessage() != null) {
            return t.getMessage();
        }
        return t.getClass().getSimpleName();
    }

    public static void error(HttpServletRequest request, HttpServletResponse response, ErrorCode code, String message) throws IOException {
        XMLBuilder builder = createXMLBuilder(request, response, false);
        builder.add("error", true,
                new XMLBuilder.Attribute("code", code.getCode()),
                new XMLBuilder.Attribute("message", message));
        builder.end();
        response.getWriter().print(builder);
    }

    private static XMLBuilder createXMLBuilder(HttpServletRequest request, HttpServletResponse response, boolean ok) throws IOException {
        String format = ServletRequestUtils.getStringParameter(request, "f", "xml");
        boolean json = "json".equals(format);
        boolean jsonp = "jsonp".equals(format);
        XMLBuilder builder;

        response.setCharacterEncoding(StringUtil.ENCODING_UTF8);

        if (json) {
            builder = XMLBuilder.createJSONBuilder();
            response.setContentType("application/json");
        } else if (jsonp) {
            builder = XMLBuilder.createJSONPBuilder(request.getParameter("callback"));
            response.setContentType("text/javascript");
        } else {
        	builder = XMLBuilder.createXMLBuilder();
            response.setContentType("text/xml");
        }
        
        builder.preamble(StringUtil.ENCODING_UTF8);
        builder.add("subsonic-response", false,
                    new Attribute("xmlns", "http://subsonic.org/restapi"),
                    new Attribute("status", ok ? "ok" : "failed"),
                    new Attribute("version", StringUtil.getRESTProtocolVersion()));
        return builder;
    }

    private String createPlayerIfNecessary(HttpServletRequest request, boolean jukebox) {
        String username = request.getRemoteUser();
        String clientId = request.getParameter("c");
        if (jukebox) {
            clientId += "-jukebox";
        }

        List<Player> players = playerService.getPlayersForUserAndClientId(username, clientId);

        // If not found, create it.
        if (players.isEmpty()) {
            Player player = new Player();
            player.setIpAddress(request.getRemoteAddr());
            player.setUsername(username);
            player.setClientId(clientId);
            player.setName(clientId);
            player.setTechnology(jukebox ? PlayerTechnology.JUKEBOX : PlayerTechnology.EXTERNAL_WITH_PLAYLIST);
            playerService.createPlayer(player);
            players = playerService.getPlayersForUserAndClientId(username, clientId);
        }

        // Return the player ID.
        return !players.isEmpty() ? players.get(0).getId() : null;
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

	public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setDownloadController(DownloadController downloadController) {
        this.downloadController = downloadController;
    }

    public void setCoverArtController(CoverArtController coverArtController) {
        this.coverArtController = coverArtController;
    }

    public void setUserSettingsController(UserSettingsController userSettingsController) {
        this.userSettingsController = userSettingsController;
    }

    public void setLeftController(LeftController leftController) {
        this.leftController = leftController;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setStreamController(StreamController streamController) {
        this.streamController = streamController;
    }

    public HLSController getHlsController() {
		return hlsController;
	}

	public void setHlsController(HLSController hlsController) {
		this.hlsController = hlsController;
	}

	public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setPlaylistControlService(net.sourceforge.subsonic.ajax.PlaylistService playlistControlService) {
        this.playlistControlService = playlistControlService;
    }

    public void setJukeboxService(JukeboxService jukeboxService) {
        this.jukeboxService = jukeboxService;
    }

    public void setPodcastService(PodcastService podcastService) {
        this.podcastService = podcastService;
    }

    public void setNameSearchService(NameSearchService nameSearchService) {
		this.nameSearchService = nameSearchService;
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

	public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
		this.audioScrobblerService = audioScrobblerService;
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

	public void setShareService(ShareService shareService) {
        this.shareService = shareService;
    }

    public void setDirectoryBrowserService(DirectoryBrowserService directoryBrowserService) {
		this.directoryBrowserService = directoryBrowserService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}

	public static enum ErrorCode {

        GENERIC(0, "A generic error."),
        MISSING_PARAMETER(10, "Required parameter is missing."),
        PROTOCOL_MISMATCH_CLIENT_TOO_OLD(20, "Incompatible Subsonic REST protocol version. Client must upgrade."),
        PROTOCOL_MISMATCH_SERVER_TOO_OLD(30, "Incompatible Subsonic REST protocol version. Server must upgrade."),
        NOT_AUTHENTICATED(40, "Wrong username or password."),
        NOT_AUTHORIZED(50, "User is not authorized for the given operation."),
        NOT_LICENSED(60, "The trial period for the Subsonic server is over. Please donate to get a license key. Visit subsonic.org for details."),
        NOT_FOUND(70, "Requested data was not found.");

        private final int code;
        private final String message;

        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
