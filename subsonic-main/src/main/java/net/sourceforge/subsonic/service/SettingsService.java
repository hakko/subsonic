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
package net.sourceforge.subsonic.service;

import static net.sourceforge.subsonic.util.StringUtil.utf8HexDecode;
import static net.sourceforge.subsonic.util.StringUtil.utf8HexEncode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.AvatarDao;
import net.sourceforge.subsonic.dao.InternetRadioDao;
import net.sourceforge.subsonic.dao.UserDao;
import net.sourceforge.subsonic.domain.Avatar;
import net.sourceforge.subsonic.domain.InternetRadio;
import net.sourceforge.subsonic.domain.Theme;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import com.github.hakko.musiccabinet.service.spotify.SpotifySettingsService;

/**
 * Provides persistent storage of application settings and preferences.
 *
 * @author Sindre Mehus
 */
public class SettingsService {

	// Subsonic home directory.
	private static final File SUBSONIC_HOME_WINDOWS = new File("c:/subsonic");
	private static final File SUBSONIC_HOME_OTHER = new File("/var/subsonic");

	// Global settings.
	private static final String KEY_INDEX_STRING = "IndexString";
	private static final String KEY_IGNORED_ARTICLES = "IgnoredArticles";
	private static final String KEY_PLAYLIST_FOLDER = "PlaylistFolder";
	private static final String KEY_MUSIC_FILE_TYPES = "MediaFileTypes";
	private static final String KEY_VIDEO_FILE_TYPES = "VideoFileTypes";
	private static final String KEY_IMAGE_FILE_TYPES = "ImageFileTypes";
	private static final String KEY_COVER_ART_LIMIT = "CoverArtLimit";
	private static final String KEY_WELCOME_TITLE = "WelcomeTitle";
	private static final String KEY_WELCOME_SUBTITLE = "WelcomeSubtitle";
	private static final String KEY_WELCOME_MESSAGE = "WelcomeMessage2";
	private static final String KEY_LOGIN_MESSAGE = "LoginMessage";
	private static final String KEY_SHARE_URL_PREFIX = "ShareUrlPrefix";
	private static final String KEY_LYRICS_URL = "LyricsUrl";
	private static final String KEY_REST_ALBUM_NAME = "RestAlbumName";
	private static final String KEY_LOCALE_LANGUAGE = "LocaleLanguage";
	private static final String KEY_LOCALE_COUNTRY = "LocaleCountry";
	private static final String KEY_LOCALE_VARIANT = "LocaleVariant";
	private static final String KEY_THEME_ID = "Theme";
	private static final String KEY_INDEX_CREATION_INTERVAL = "IndexCreationInterval";
	private static final String KEY_INDEX_CREATION_HOUR = "IndexCreationHour";
	private static final String KEY_PODCAST_UPDATE_INTERVAL = "PodcastUpdateInterval";
	private static final String KEY_PODCAST_FOLDER = "PodcastFolder";
	private static final String KEY_PODCAST_EPISODE_RETENTION_COUNT = "PodcastEpisodeRetentionCount";
	private static final String KEY_PODCAST_EPISODE_DOWNLOAD_COUNT = "PodcastEpisodeDownloadCount";
	private static final String KEY_DOWNLOAD_BITRATE_LIMIT = "DownloadBitrateLimit";
	private static final String KEY_UPLOAD_BITRATE_LIMIT = "UploadBitrateLimit";
	private static final String KEY_STREAM_PORT = "StreamPort";
	private static final String KEY_LICENSE_EMAIL = "LicenseEmail";
	private static final String KEY_LICENSE_CODE = "LicenseCode";
	private static final String KEY_LICENSE_DATE = "LicenseDate";
	private static final String KEY_DOWNSAMPLING_COMMAND = "DownsamplingCommand2";
	private static final String KEY_HLS_COMMAND = "HlsCommand2";
	private static final String KEY_JUKEBOX_COMMAND = "JukeboxCommand";
	private static final String KEY_REWRITE_URL = "RewriteUrl";
	private static final String KEY_LDAP_ENABLED = "LdapEnabled";
	private static final String KEY_LDAP_URL = "LdapUrl";
	private static final String KEY_LDAP_MANAGER_DN = "LdapManagerDn";
	private static final String KEY_LDAP_MANAGER_PASSWORD = "LdapManagerPassword";
	private static final String KEY_LDAP_SEARCH_FILTER = "LdapSearchFilter";
	private static final String KEY_LDAP_AUTO_SHADOWING = "LdapAutoShadowing";
	private static final String KEY_GETTING_STARTED_ENABLED = "GettingStartedEnabled";
	private static final String KEY_PORT_FORWARDING_ENABLED = "PortForwardingEnabled";
	private static final String KEY_PORT = "Port";
	private static final String KEY_HTTPS_PORT = "HttpsPort";
	private static final String KEY_URL_REDIRECTION_ENABLED = "UrlRedirectionEnabled";
	private static final String KEY_URL_REDIRECT_FROM = "UrlRedirectFrom";
	private static final String KEY_URL_REDIRECT_CONTEXT_PATH = "UrlRedirectContextPath";
	private static final String KEY_SERVER_ID = "ServerId";
	private static final String KEY_SETTINGS_CHANGED = "SettingsChanged";
	private static final String KEY_SPOTIFY_CACHE = "SpotifyCache";
	private static final String KEY_SPOTIFY_KEY = "SpotifyKey";
	private static final String KEY_SPOTIFY_USERNAME = "SpotifyUsername";
	private static final String KEY_SPOTIFY_PASSWORD = "SpotifyPassword";


	private static final String KEY_MUSICCABINET_LASTFM_USERNAME = "MusicCabinetLastFMUsername";
	private static final String KEY_MUSICCABINET_JDBC_PASSWORD = "MusicCabinetJDBCPassword";
	private static final String KEY_MUSICCABINET_FIRST_INVOCATION = "MusicCabinetFirstInvocation";
	private static final String KEY_MUSICCABINET_ARTIST_RADIO_ARTIST_COUNT = "MusicCabinetArtistRadioArtistCount";
	private static final String KEY_MUSICCABINET_ARTIST_RADIO_TOTAL_COUNT = "MusicCabinetArtistRadioTotalCount";
	private static final String KEY_MUSICCABINET_ARTIST_TOP_TRACKS_TOTAL_COUNT = "MusicCabinetArtistTopTracksTotalCount";
	private static final String KEY_MUSICCABINET_GENRE_RADIO_ARTIST_COUNT = "MusicCabinetGenreRadioArtistCount";
	private static final String KEY_MUSICCABINET_GENRE_RADIO_TOTAL_COUNT = "MusicCabinetGenreRadioTotalCount";
	private static final String KEY_MUSICCABINET_RELATED_ARTISTS_SAMPLER_ARTIST_COUNT = "MusicCabinetRelatedArtistsSamplerArtistCount";
	private static final String KEY_MUSICCABINET_RANDOM_SONG_COUNT = "MusicCabinetRandomSongCount";
	private static final String KEY_MUSICCABINET_PREFER_LAST_FM_ARTWORK = "MusicCabinetPreferLastFmArtwork";
	private static final String KEY_MUSICCABINET_RADIO_MINIMUM_SONG_LENGTH = "MusicCabinetRadioMinimumSongLength";
	private static final String KEY_MUSICCABINET_RADIO_MAXIMUM_SONG_LENGTH = "MusicCabinetRadioMaximumSongLength";
	private static final String KEY_MUSICCABINET_LAST_FM_LANGUAGE = "MusicCabinetLastFmLanguage";
	private static final String KEY_MUSICCABINET_SYNC_STARRED_AND_LOVED_TRACKS = "MusicCabinetSyncStarredAndLovedTracks";
	private static final String KEY_MUSICCABINET_PREFER_LOCAL_GENRES = "MusicCabinetPreferLocalGenres";

	// Default values.
	public static final String DEFAULT_INDEX_STRING = "A B C D E F G H I J K L M N O P Q R S T U V W X-Z(XYZ)";
	public static final String DEFAULT_IGNORED_ARTICLES = "The El La Los Las Le Les";
	private static final String DEFAULT_PLAYLIST_FOLDER = Util
			.getDefaultPlaylistFolder();
	private static final String DEFAULT_MUSIC_FILE_TYPES = "mp3 ogg oga aac m4a flac wav wma aif aiff ape mpc shn";
	private static final String DEFAULT_VIDEO_FILE_TYPES = "flv avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts";
	private static final String DEFAULT_IMAGE_FILE_TYPES = "jpg jpeg png tiff gif";
	private static final int DEFAULT_COVER_ART_LIMIT = 30;
	private static final String DEFAULT_WELCOME_TITLE = "Welcome to Subsonic!";
	private static final String DEFAULT_WELCOME_SUBTITLE = null;
	private static final String DEFAULT_WELCOME_MESSAGE = "__Welcome to Subsonic!__\n"
			+ "\\\\ \\\\\n"
			+ "Subsonic is a free, web-based media streamer, providing ubiquitous access to your music. \n"
			+ "\\\\ \\\\\n"
			+ "Use it to share your music with friends, or to listen to your own music while at work. You can stream to multiple "
			+ "players simultaneously, for instance to one player in your kitchen and another in your living room.\n"
			+ "\\\\ \\\\\n"
			+ "To change or remove this message, log in with administrator rights and go to {link:Settings > General|generalSettings.view}.";
	private static final String DEFAULT_LOGIN_MESSAGE = null;
	private static final String DEFAULT_LYRICS_URL = "http://google.com/search?q=\"$(artist)\"+\"$(song)\"+lyrics";
	private static final String DEFAULT_REST_ALBUM_NAME = "($(year)) $(album)";
	private static final String DEFAULT_LOCALE_LANGUAGE = "en";
	private static final String DEFAULT_LOCALE_COUNTRY = "";
	private static final String DEFAULT_LOCALE_VARIANT = "";
	private static final String DEFAULT_THEME_ID = "default";
	private static final int DEFAULT_INDEX_CREATION_INTERVAL = 1;
	private static final int DEFAULT_INDEX_CREATION_HOUR = 3;
	private static final int DEFAULT_PODCAST_UPDATE_INTERVAL = 24;
	private static final String DEFAULT_PODCAST_FOLDER = Util
			.getDefaultPodcastFolder();
	private static final int DEFAULT_PODCAST_EPISODE_RETENTION_COUNT = 10;
	private static final int DEFAULT_PODCAST_EPISODE_DOWNLOAD_COUNT = 1;
	private static final long DEFAULT_DOWNLOAD_BITRATE_LIMIT = 0;
	private static final long DEFAULT_UPLOAD_BITRATE_LIMIT = 0;
	private static final long DEFAULT_STREAM_PORT = 0;
	private static final String DEFAULT_LICENSE_EMAIL = null;
	private static final String DEFAULT_LICENSE_CODE = null;
	private static final String DEFAULT_LICENSE_DATE = null;
	private static final String DEFAULT_DOWNSAMPLING_COMMAND = "lame -S -h --resample 44.1 -b %b --tt %t --ta %a --tl %l %s -";
	private static final String DEFAULT_HLS_COMMAND = "ffmpeg -ss %o -t %d -i %s -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f mpegts -vcodec libx264 -preset superfast -acodec libmp3lame -threads 0 -";
	private static final String DEFAULT_JUKEBOX_COMMAND = "ffmpeg -ss %o -i %s -v 0 -f au -";
	private static final boolean DEFAULT_REWRITE_URL = true;
	private static final boolean DEFAULT_LDAP_ENABLED = false;
	private static final String DEFAULT_LDAP_URL = "ldap://host.domain.com:389/cn=Users,dc=domain,dc=com";
	private static final String DEFAULT_LDAP_MANAGER_DN = null;
	private static final String DEFAULT_LDAP_MANAGER_PASSWORD = null;
	private static final String DEFAULT_LDAP_SEARCH_FILTER = "(sAMAccountName={0})";
	private static final boolean DEFAULT_LDAP_AUTO_SHADOWING = false;
	private static final boolean DEFAULT_PORT_FORWARDING_ENABLED = false;
	private static final boolean DEFAULT_GETTING_STARTED_ENABLED = true;
	private static final int DEFAULT_PORT = 80;
	private static final int DEFAULT_HTTPS_PORT = 0;
	private static final boolean DEFAULT_URL_REDIRECTION_ENABLED = false;
	private static final String DEFAULT_URL_REDIRECT_FROM = "yourname";
	private static final String DEFAULT_URL_REDIRECT_CONTEXT_PATH = null;
	private static final String DEFAULT_SERVER_ID = null;
	private static final long DEFAULT_SETTINGS_CHANGED = 0L;
	private static final String DEFAULT_SPOTIFY_CACHE = "libspotify";
	private static final String DEFAULT_SPOTIFY_KEY = "spotify_appkey.key";

	private static final int DEFAULT_MUSICCABINET_ARTIST_RADIO_ARTIST_COUNT = 3;
	private static final int DEFAULT_MUSICCABINET_ARTIST_RADIO_TOTAL_COUNT = 20;
	private static final int DEFAULT_MUSICCABINET_ARTIST_TOP_TRACKS_TOTAL_COUNT = 20;
	private static final int DEFAULT_MUSICCABINET_GENRE_RADIO_ARTIST_COUNT = 1;
	private static final int DEFAULT_MUSICCABINET_GENRE_RADIO_TOTAL_COUNT = 25;
	private static final int DEFAULT_MUSICCABINET_RELATED_ARTISTS_SAMPLER_ARTIST_COUNT = 3;
	private static final int DEFAULT_MUSICCABINET_RANDOM_SONG_COUNT = 10;
	private static final boolean DEFAULT_MUSICCABINET_PREFER_LAST_FM_ARTWORK = true;
	private static final int DEFAULT_MUSICCABINET_RADIO_MINIMUM_SONG_LENGTH = 45;
	private static final int DEFAULT_MUSICCABINET_RADIO_MAXIMUM_SONG_LENGTH = 600;
	private static final String DEFAULT_MUSICCABINET_LAST_FM_LANGUAGE = Locale.ENGLISH
			.getLanguage();
	private static final boolean DEFAULT_MUSICCABINET_SYNC_STARRED_AND_LOVED_TRACKS = true;
	private static final boolean DEFAULT_MUSICCABINET_PREFER_LOCAL_GENRES = false;

	// Array of obsolete keys. Used to clean property file.
	private static final List<String> OBSOLETE_KEYS = Arrays.asList(
			"PortForwardingPublicPort", "PortForwardingLocalPort",
			"DownsamplingCommand", "AutoCoverBatch", "MusicMask", "VideoMask",
			"CoverArtMask", "Shortcuts", "CoverArtFileTypes");

	private static final String LOCALES_FILE = "/net/sourceforge/subsonic/i18n/locales.txt";
	private static final String THEMES_FILE = "/net/sourceforge/subsonic/theme/themes.txt";

	private static final Logger LOG = Logger.getLogger(SettingsService.class);

	private Properties properties = new Properties();
	private List<Theme> themes;
	private List<Locale> locales;
	private InternetRadioDao internetRadioDao;
	private UserDao userDao;
	private AvatarDao avatarDao;
	private VersionService versionService;
	private SpotifySettingsService spotifySettingsService;

	private String[] cachedMusicFileTypesArray;
	private String[] cachedVideoFileTypesArray;
	private String[] cachedImageFileTypesArray;
	private Map<String, UserSettings> cachedUserSettings = new HashMap<>();

	private static File subsonicHome;

	private boolean licenseValidated = true;

	public SettingsService() {
		File propertyFile = getPropertyFile();

		if (propertyFile.exists()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(propertyFile);
				properties.load(in);
			} catch (Exception x) {
				LOG.error("Unable to read from property file.", x);
			} finally {
				IOUtils.closeQuietly(in);
			}

			// Remove obsolete properties.
			for (Iterator<Object> iterator = properties.keySet().iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				if (OBSOLETE_KEYS.contains(key)) {
					LOG.debug("Removing obsolete property [" + key + ']');
					iterator.remove();
				}
			}
		}

		save(false);

		if (getMusicCabinetJDBCPassword() != null) {
			System.setProperty("musiccabinet.jdbc.password",
					getMusicCabinetJDBCPassword());
		}
	}

	/**
	 * Register in service locator so that non-Spring objects can access me.
	 * This method is invoked automatically by Spring.
	 */
	public void init() {
		System.err.println("Initializing settings service.");
		ServiceLocator.setSettingsService(this);
		validateLicenseAsync();
		spotifySettingsService.setSpotifyCache(getSpotifyCache());
		spotifySettingsService.setSpotifyKey(new File(getSpotifyKey()));
		spotifySettingsService.setSpotifyUserName(getSpotifyUserName());
		spotifySettingsService.setSpotifyPassword(getSpotifyPassword());
	}

	public void save() {
		save(true);
	}

	private void save(boolean updateChangedDate) {
		if (updateChangedDate) {
			setProperty(KEY_SETTINGS_CHANGED,
					String.valueOf(System.currentTimeMillis()));
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(getPropertyFile());
			properties
					.store(out,
							"Subsonic preferences.  NOTE: This file is automatically generated.");
		} catch (Exception x) {
			LOG.error("Unable to write to property file.", x);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private File getPropertyFile() {
		return new File(getSubsonicHome(), "subsonic.properties");
	}
	
	public static synchronized File getSubsonicLog() {
        File subsonicHome = SettingsService.getSubsonicHome();
        
		String overrideLog = System.getProperty("subsonic.log");
		if (overrideLog != null) {
			return new File(overrideLog);
		}
        
        return new File(subsonicHome, "subsonic.log");
	}

	/**
	 * Returns the Subsonic home directory.
	 *
	 * @return The Subsonic home directory, if it exists.
	 * @throws RuntimeException
	 *             If directory doesn't exist.
	 */
	public static synchronized File getSubsonicHome() {

		if (subsonicHome != null) {
			return subsonicHome;
		}

		File home;

		String overrideHome = System.getProperty("subsonic.home");
		if (overrideHome != null) {
			home = new File(overrideHome);
		} else {
			boolean isWindows = System.getProperty("os.name", "Windows")
					.toLowerCase().startsWith("windows");
			home = isWindows ? SUBSONIC_HOME_WINDOWS : SUBSONIC_HOME_OTHER;
		}

		// Attempt to create home directory if it doesn't exist.
		if (!home.exists() || !home.isDirectory()) {
			boolean success = home.mkdirs();
			if (success) {
				subsonicHome = home;
			} else {
				String message = "The directory "
						+ home
						+ " does not exist. Please create it and make it writable. "
						+ "(You can override the directory location by specifying -Dsubsonic.home=... when "
						+ "starting the servlet container.)";
				System.err.println("ERROR: " + message);
			}
		} else {
			subsonicHome = home;
		}

		return home;
	}

	private int getInt(String key, int defaultValue) {
		return Integer.valueOf(properties.getProperty(key,
				String.valueOf(defaultValue)));
	}

	private void setInt(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	private boolean getBoolean(String key, boolean defaultValue) {
		return Boolean.valueOf(properties.getProperty(key,
				String.valueOf(defaultValue)));
	}

	private void setBoolean(String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}

	public String getIndexString() {
		return properties.getProperty(KEY_INDEX_STRING, DEFAULT_INDEX_STRING);
	}

	public void setIndexString(String indexString) {
		setProperty(KEY_INDEX_STRING, indexString);
	}

	public String getIgnoredArticles() {
		return properties.getProperty(KEY_IGNORED_ARTICLES,
				DEFAULT_IGNORED_ARTICLES);
	}

	public String[] getIgnoredArticlesAsArray() {
		return getIgnoredArticles().split("\\s+");
	}

	public void setIgnoredArticles(String ignoredArticles) {
		setProperty(KEY_IGNORED_ARTICLES, ignoredArticles);
	}

	public String getPlaylistFolder() {
		return properties.getProperty(KEY_PLAYLIST_FOLDER,
				DEFAULT_PLAYLIST_FOLDER);
	}

	public void setPlaylistFolder(String playlistFolder) {
		setProperty(KEY_PLAYLIST_FOLDER, playlistFolder);
	}

	public String getMusicFileTypes() {
		return properties.getProperty(KEY_MUSIC_FILE_TYPES,
				DEFAULT_MUSIC_FILE_TYPES);
	}

	public synchronized void setMediaFileTypes(String fileTypes) {
		setProperty(KEY_MUSIC_FILE_TYPES, fileTypes);
		cachedMusicFileTypesArray = null;
	}

	public synchronized String[] getMusicFileTypesAsArray() {
		if (cachedMusicFileTypesArray == null) {
			cachedMusicFileTypesArray = toStringArray(getMusicFileTypes());
		}
		return cachedMusicFileTypesArray;
	}

	public String getVideoFileTypes() {
		return properties.getProperty(KEY_VIDEO_FILE_TYPES,
				DEFAULT_VIDEO_FILE_TYPES);
	}

	public synchronized void setVideoFileTypes(String fileTypes) {
		setProperty(KEY_VIDEO_FILE_TYPES, fileTypes);
		cachedVideoFileTypesArray = null;
	}

	public synchronized String[] getVideoFileTypesAsArray() {
		if (cachedVideoFileTypesArray == null) {
			cachedVideoFileTypesArray = toStringArray(getVideoFileTypes());
		}
		return cachedVideoFileTypesArray;
	}

	public String getImageFileTypes() {
		return properties.getProperty(KEY_IMAGE_FILE_TYPES,
				DEFAULT_IMAGE_FILE_TYPES);
	}

	public synchronized void setImageFileTypes(String fileTypes) {
		setProperty(KEY_IMAGE_FILE_TYPES, fileTypes);
		cachedImageFileTypesArray = null;
	}

	public synchronized String[] getImageFileTypesAsArray() {
		if (cachedImageFileTypesArray == null) {
			cachedImageFileTypesArray = toStringArray(getImageFileTypes());
		}
		return cachedImageFileTypesArray;
	}

	public int getCoverArtLimit() {
		return Integer.parseInt(properties.getProperty(KEY_COVER_ART_LIMIT, ""
				+ DEFAULT_COVER_ART_LIMIT));
	}

	public void setCoverArtLimit(int limit) {
		setProperty(KEY_COVER_ART_LIMIT, "" + limit);
	}

	public String getWelcomeTitle() {
		return StringUtils.trimToNull(properties.getProperty(KEY_WELCOME_TITLE,
				DEFAULT_WELCOME_TITLE));
	}

	public void setWelcomeTitle(String title) {
		setProperty(KEY_WELCOME_TITLE, title);
	}

	public String getWelcomeSubtitle() {
		return StringUtils.trimToNull(properties.getProperty(
				KEY_WELCOME_SUBTITLE, DEFAULT_WELCOME_SUBTITLE));
	}

	public void setWelcomeSubtitle(String subtitle) {
		setProperty(KEY_WELCOME_SUBTITLE, subtitle);
	}

	public String getWelcomeMessage() {
		return StringUtils.trimToNull(properties.getProperty(
				KEY_WELCOME_MESSAGE, DEFAULT_WELCOME_MESSAGE));
	}

	public void setWelcomeMessage(String message) {
		setProperty(KEY_WELCOME_MESSAGE, message);
	}

	public String getLoginMessage() {
		return StringUtils.trimToNull(properties.getProperty(KEY_LOGIN_MESSAGE,
				DEFAULT_LOGIN_MESSAGE));
	}

	public void setLoginMessage(String message) {
		setProperty(KEY_LOGIN_MESSAGE, message);
	}

	public String getShareUrlPrefix() {
		return StringUtils.trimToEmpty(properties
				.getProperty(KEY_SHARE_URL_PREFIX));
	}

	public void setShareUrlPrefix(String shareUrlPrefix) {
		setProperty(KEY_SHARE_URL_PREFIX, shareUrlPrefix);
	}

	public String getLyricsUrl() {
		return properties.getProperty(KEY_LYRICS_URL, DEFAULT_LYRICS_URL);
	}

	public void setLyricsUrl(String lyricsUrl) {
		setProperty(KEY_LYRICS_URL, lyricsUrl);
	}

	public String getRestAlbumName() {
		return properties.getProperty(KEY_REST_ALBUM_NAME,
				DEFAULT_REST_ALBUM_NAME);
	}

	public void setRestAlbumName(String restAlbumName) {
		setProperty(KEY_REST_ALBUM_NAME, restAlbumName);
	}

	/**
	 * Returns the number of days between automatic index creation, of -1 if
	 * automatic index creation is disabled.
	 */
	public int getIndexCreationInterval() {
		return Integer.parseInt(properties.getProperty(
				KEY_INDEX_CREATION_INTERVAL, ""
						+ DEFAULT_INDEX_CREATION_INTERVAL));
	}

	/**
	 * Sets the number of days between automatic index creation, of -1 if
	 * automatic index creation is disabled.
	 */
	public void setIndexCreationInterval(int days) {
		setProperty(KEY_INDEX_CREATION_INTERVAL, String.valueOf(days));
	}

	/**
	 * Returns the hour of day (0 - 23) when automatic index creation should
	 * run.
	 */
	public int getIndexCreationHour() {
		return Integer.parseInt(properties.getProperty(KEY_INDEX_CREATION_HOUR,
				String.valueOf(DEFAULT_INDEX_CREATION_HOUR)));
	}

	/**
	 * Sets the hour of day (0 - 23) when automatic index creation should run.
	 */
	public void setIndexCreationHour(int hour) {
		setProperty(KEY_INDEX_CREATION_HOUR, String.valueOf(hour));
	}

	/**
	 * Returns the number of hours between Podcast updates, of -1 if automatic
	 * updates are disabled.
	 */
	public int getPodcastUpdateInterval() {
		return Integer.parseInt(properties.getProperty(
				KEY_PODCAST_UPDATE_INTERVAL,
				String.valueOf(DEFAULT_PODCAST_UPDATE_INTERVAL)));
	}

	/**
	 * Sets the number of hours between Podcast updates, of -1 if automatic
	 * updates are disabled.
	 */
	public void setPodcastUpdateInterval(int hours) {
		setProperty(KEY_PODCAST_UPDATE_INTERVAL, String.valueOf(hours));
	}

	/**
	 * Returns the number of Podcast episodes to keep (-1 to keep all).
	 */
	public int getPodcastEpisodeRetentionCount() {
		return Integer.parseInt(properties.getProperty(
				KEY_PODCAST_EPISODE_RETENTION_COUNT,
				String.valueOf(DEFAULT_PODCAST_EPISODE_RETENTION_COUNT)));
	}

	/**
	 * Sets the number of Podcast episodes to keep (-1 to keep all).
	 */
	public void setPodcastEpisodeRetentionCount(int count) {
		setProperty(KEY_PODCAST_EPISODE_RETENTION_COUNT, String.valueOf(count));
	}

	/**
	 * Returns the number of Podcast episodes to download (-1 to download all).
	 */
	public int getPodcastEpisodeDownloadCount() {
		return Integer.parseInt(properties.getProperty(
				KEY_PODCAST_EPISODE_DOWNLOAD_COUNT,
				String.valueOf(DEFAULT_PODCAST_EPISODE_DOWNLOAD_COUNT)));
	}

	/**
	 * Sets the number of Podcast episodes to download (-1 to download all).
	 */
	public void setPodcastEpisodeDownloadCount(int count) {
		setProperty(KEY_PODCAST_EPISODE_DOWNLOAD_COUNT, String.valueOf(count));
	}

	/**
	 * Returns the Podcast download folder.
	 */
	public String getPodcastFolder() {
		return properties.getProperty(KEY_PODCAST_FOLDER,
				DEFAULT_PODCAST_FOLDER);
	}

	/**
	 * Sets the Podcast download folder.
	 */
	public void setPodcastFolder(String folder) {
		setProperty(KEY_PODCAST_FOLDER, folder);
	}

	/**
	 * @return The download bitrate limit in Kbit/s. Zero if unlimited.
	 */
	public long getDownloadBitrateLimit() {
		return Long.parseLong(properties
				.getProperty(KEY_DOWNLOAD_BITRATE_LIMIT, ""
						+ DEFAULT_DOWNLOAD_BITRATE_LIMIT));
	}

	/**
	 * @param limit
	 *            The download bitrate limit in Kbit/s. Zero if unlimited.
	 */
	public void setDownloadBitrateLimit(long limit) {
		setProperty(KEY_DOWNLOAD_BITRATE_LIMIT, "" + limit);
	}

	/**
	 * @return The upload bitrate limit in Kbit/s. Zero if unlimited.
	 */
	public long getUploadBitrateLimit() {
		return Long.parseLong(properties.getProperty(KEY_UPLOAD_BITRATE_LIMIT,
				"" + DEFAULT_UPLOAD_BITRATE_LIMIT));
	}

	/**
	 * @param limit
	 *            The upload bitrate limit in Kbit/s. Zero if unlimited.
	 */
	public void setUploadBitrateLimit(long limit) {
		setProperty(KEY_UPLOAD_BITRATE_LIMIT, "" + limit);
	}

	/**
	 * @return The non-SSL stream port. Zero if disabled.
	 */
	public int getStreamPort() {
		return Integer.parseInt(properties.getProperty(KEY_STREAM_PORT, ""
				+ DEFAULT_STREAM_PORT));
	}

	/**
	 * @param port
	 *            The non-SSL stream port. Zero if disabled.
	 */
	public void setStreamPort(int port) {
		setProperty(KEY_STREAM_PORT, "" + port);
	}

	public String getLicenseEmail() {
		return properties.getProperty(KEY_LICENSE_EMAIL, DEFAULT_LICENSE_EMAIL);
	}

	public void setLicenseEmail(String email) {
		setProperty(KEY_LICENSE_EMAIL, email);
	}

	public String getLicenseCode() {
		return properties.getProperty(KEY_LICENSE_CODE, DEFAULT_LICENSE_CODE);
	}

	public void setLicenseCode(String code) {
		setProperty(KEY_LICENSE_CODE, code);
	}

	public Date getLicenseDate() {
		String value = properties.getProperty(KEY_LICENSE_DATE,
				DEFAULT_LICENSE_DATE);
		return value == null ? null : new Date(Long.parseLong(value));
	}

	public void setLicenseDate(Date date) {
		String value = (date == null ? null : String.valueOf(date.getTime()));
		setProperty(KEY_LICENSE_DATE, value);
	}

	public boolean isLicenseValid() {
		return isLicenseValid(getLicenseEmail(), getLicenseCode())
				&& licenseValidated;
	}

	public boolean isLicenseValid(String email, String license) {
		if (email == null || license == null) {
			return false;
		}
		return license.equalsIgnoreCase(StringUtil.md5Hex(email.toLowerCase()));
	}

	public String getDownsamplingCommand() {
		return properties.getProperty(KEY_DOWNSAMPLING_COMMAND,
				DEFAULT_DOWNSAMPLING_COMMAND);
	}

	public void setDownsamplingCommand(String command) {
		setProperty(KEY_DOWNSAMPLING_COMMAND, command);
	}

	public String getHlsCommand() {
		return properties.getProperty(KEY_HLS_COMMAND, DEFAULT_HLS_COMMAND);
	}

	public void setHlsCommand(String command) {
		setProperty(KEY_HLS_COMMAND, command);
	}

	public String getJukeboxCommand() {
		return properties.getProperty(KEY_JUKEBOX_COMMAND,
				DEFAULT_JUKEBOX_COMMAND);
	}

	public boolean isRewriteUrlEnabled() {
		return Boolean.valueOf(properties.getProperty(KEY_REWRITE_URL,
				String.valueOf(DEFAULT_REWRITE_URL)));
	}

	public void setRewriteUrlEnabled(boolean rewriteUrl) {
		properties.setProperty(KEY_REWRITE_URL, String.valueOf(rewriteUrl));
	}

	public boolean isLdapEnabled() {
		return Boolean.valueOf(properties.getProperty(KEY_LDAP_ENABLED,
				String.valueOf(DEFAULT_LDAP_ENABLED)));
	}

	public void setLdapEnabled(boolean ldapEnabled) {
		properties.setProperty(KEY_LDAP_ENABLED, String.valueOf(ldapEnabled));
	}

	public String getLdapUrl() {
		return properties.getProperty(KEY_LDAP_URL, DEFAULT_LDAP_URL);
	}

	public void setLdapUrl(String ldapUrl) {
		properties.setProperty(KEY_LDAP_URL, ldapUrl);
	}

	public String getLdapSearchFilter() {
		return properties.getProperty(KEY_LDAP_SEARCH_FILTER,
				DEFAULT_LDAP_SEARCH_FILTER);
	}

	public void setLdapSearchFilter(String ldapSearchFilter) {
		properties.setProperty(KEY_LDAP_SEARCH_FILTER, ldapSearchFilter);
	}

	public String getLdapManagerDn() {
		return properties.getProperty(KEY_LDAP_MANAGER_DN,
				DEFAULT_LDAP_MANAGER_DN);
	}

	public void setLdapManagerDn(String ldapManagerDn) {
		properties.setProperty(KEY_LDAP_MANAGER_DN, ldapManagerDn);
	}

	public String getLdapManagerPassword() {
		String s = properties.getProperty(KEY_LDAP_MANAGER_PASSWORD,
				DEFAULT_LDAP_MANAGER_PASSWORD);
		try {
			return StringUtil.utf8HexDecode(s);
		} catch (Exception x) {
			LOG.warn("Failed to decode LDAP manager password.", x);
			return s;
		}
	}

	public void setLdapManagerPassword(String ldapManagerPassword) {
		try {
			ldapManagerPassword = StringUtil.utf8HexEncode(ldapManagerPassword);
		} catch (Exception x) {
			LOG.warn("Failed to encode LDAP manager password.", x);
		}
		properties.setProperty(KEY_LDAP_MANAGER_PASSWORD, ldapManagerPassword);
	}

	public boolean isLdapAutoShadowing() {
		return Boolean.valueOf(properties.getProperty(KEY_LDAP_AUTO_SHADOWING,
				String.valueOf(DEFAULT_LDAP_AUTO_SHADOWING)));
	}

	public void setLdapAutoShadowing(boolean ldapAutoShadowing) {
		properties.setProperty(KEY_LDAP_AUTO_SHADOWING,
				String.valueOf(ldapAutoShadowing));
	}

	public boolean isGettingStartedEnabled() {
		return Boolean.valueOf(properties.getProperty(
				KEY_GETTING_STARTED_ENABLED,
				String.valueOf(DEFAULT_GETTING_STARTED_ENABLED)));
	}

	public void setGettingStartedEnabled(boolean isGettingStartedEnabled) {
		setProperty(KEY_GETTING_STARTED_ENABLED,
				String.valueOf(isGettingStartedEnabled));
	}

	public boolean isPortForwardingEnabled() {
		return Boolean.valueOf(properties.getProperty(
				KEY_PORT_FORWARDING_ENABLED,
				String.valueOf(DEFAULT_PORT_FORWARDING_ENABLED)));
	}

	public void setPortForwardingEnabled(boolean isPortForwardingEnabled) {
		setProperty(KEY_PORT_FORWARDING_ENABLED,
				String.valueOf(isPortForwardingEnabled));
	}

	public int getPort() {
		return Integer.valueOf(properties.getProperty(KEY_PORT,
				String.valueOf(DEFAULT_PORT)));
	}

	public void setPort(int port) {
		setProperty(KEY_PORT, String.valueOf(port));
	}

	public int getHttpsPort() {
		return Integer.valueOf(properties.getProperty(KEY_HTTPS_PORT,
				String.valueOf(DEFAULT_HTTPS_PORT)));
	}

	public void setHttpsPort(int httpsPort) {
		setProperty(KEY_HTTPS_PORT, String.valueOf(httpsPort));
	}

	public boolean isUrlRedirectionEnabled() {
		return Boolean.valueOf(properties.getProperty(
				KEY_URL_REDIRECTION_ENABLED,
				String.valueOf(DEFAULT_URL_REDIRECTION_ENABLED)));
	}

	public void setUrlRedirectionEnabled(boolean isUrlRedirectionEnabled) {
		setProperty(KEY_URL_REDIRECTION_ENABLED,
				String.valueOf(isUrlRedirectionEnabled));
	}

	public String getUrlRedirectFrom() {
		return properties.getProperty(KEY_URL_REDIRECT_FROM,
				DEFAULT_URL_REDIRECT_FROM);
	}

	public void setUrlRedirectFrom(String urlRedirectFrom) {
		properties.setProperty(KEY_URL_REDIRECT_FROM, urlRedirectFrom);
	}

	public String getUrlRedirectContextPath() {
		return properties.getProperty(KEY_URL_REDIRECT_CONTEXT_PATH,
				DEFAULT_URL_REDIRECT_CONTEXT_PATH);
	}

	public void setUrlRedirectContextPath(String contextPath) {
		properties.setProperty(KEY_URL_REDIRECT_CONTEXT_PATH, contextPath);
	}

	public String getServerId() {
		return properties.getProperty(KEY_SERVER_ID, DEFAULT_SERVER_ID);
	}

	public void setServerId(String serverId) {
		properties.setProperty(KEY_SERVER_ID, serverId);
	}

	public long getSettingsChanged() {
		return Long.parseLong(properties.getProperty(KEY_SETTINGS_CHANGED,
				String.valueOf(DEFAULT_SETTINGS_CHANGED)));
	}

	public void setSettingsChanged() {
		properties.setProperty(KEY_SETTINGS_CHANGED,
				String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * Returns the locale (for language, date format etc).
	 *
	 * @return The locale.
	 */
	public Locale getLocale() {
		String language = properties.getProperty(KEY_LOCALE_LANGUAGE,
				DEFAULT_LOCALE_LANGUAGE);
		String country = properties.getProperty(KEY_LOCALE_COUNTRY,
				DEFAULT_LOCALE_COUNTRY);
		String variant = properties.getProperty(KEY_LOCALE_VARIANT,
				DEFAULT_LOCALE_VARIANT);

		return new Locale(language, country, variant);
	}

	/**
	 * Sets the locale (for language, date format etc.)
	 *
	 * @param locale
	 *            The locale.
	 */
	public void setLocale(Locale locale) {
		setProperty(KEY_LOCALE_LANGUAGE, locale.getLanguage());
		setProperty(KEY_LOCALE_COUNTRY, locale.getCountry());
		setProperty(KEY_LOCALE_VARIANT, locale.getVariant());
	}

	/**
	 * Returns the ID of the theme to use.
	 *
	 * @return The theme ID.
	 */
	public String getThemeId() {
		return properties.getProperty(KEY_THEME_ID, DEFAULT_THEME_ID);
	}

	/**
	 * Sets the ID of the theme to use.
	 *
	 * @param themeId
	 *            The theme ID
	 */
	public void setThemeId(String themeId) {
		setProperty(KEY_THEME_ID, themeId);
	}

	/**
	 * Returns a list of available themes.
	 *
	 * @return A list of available themes.
	 */
	public synchronized Theme[] getAvailableThemes() {
		if (themes == null) {
			themes = new ArrayList<Theme>();
			try {
				InputStream in = SettingsService.class
						.getResourceAsStream(THEMES_FILE);
				String[] lines = StringUtil.readLines(in);
				for (String line : lines) {
					String[] elements = StringUtil.split(line);
					if (elements.length == 2) {
						themes.add(new Theme(elements[0], elements[1]));
					} else {
						LOG.warn("Failed to parse theme from line: [" + line
								+ "].");
					}
				}
			} catch (IOException x) {
				LOG.error("Failed to resolve list of themes.", x);
				themes.add(new Theme("default", "Subsonic default"));
			}
		}
		return themes.toArray(new Theme[themes.size()]);
	}

	/**
	 * Returns a list of available locales.
	 *
	 * @return A list of available locales.
	 */
	public synchronized Locale[] getAvailableLocales() {
		if (locales == null) {
			locales = new ArrayList<Locale>();
			try {
				InputStream in = SettingsService.class
						.getResourceAsStream(LOCALES_FILE);
				String[] lines = StringUtil.readLines(in);

				for (String line : lines) {
					locales.add(parseLocale(line));
				}

			} catch (IOException x) {
				LOG.error("Failed to resolve list of locales.", x);
				locales.add(Locale.ENGLISH);
			}
		}
		return locales.toArray(new Locale[locales.size()]);
	}

	private Locale parseLocale(String line) {
		String[] s = line.split("_");
		String language = s[0];
		String country = "";
		String variant = "";

		if (s.length > 1) {
			country = s[1];
		}
		if (s.length > 2) {
			variant = s[2];
		}
		return new Locale(language, country, variant);
	}

	/**
	 * Returns the "brand" name. Normally, this is just "Subsonic".
	 *
	 * @return The brand name.
	 */
	public String getBrand() {
		return "Subsonic";
	}

	public List<String> getAllLastFmUsers() {
		return userDao.getAllLastFmUsers();
	}
	
	public List<String> getAllSpotifyUsers() {
		return userDao.getAllSpotifyUsers();
	}

	public List<String> getAllUserDevices() {
		return userDao.getAllUserDevices();
	}

	public UserSettings getUserSettingsByDevice(String deviceName) {
		return userDao.getUserSettingsByDevice(deviceName);
	}

	/**
	 * Returns all internet radio stations. Disabled stations are not returned.
	 *
	 * @return Possibly empty list of all internet radio stations.
	 */
	public List<InternetRadio> getAllInternetRadios() {
		return getAllInternetRadios(false);
	}

	/**
	 * Returns the internet radio station with the given ID.
	 *
	 * @param id
	 *            The ID.
	 * @return The internet radio station with the given ID, or
	 *         <code>null</code> if not found.
	 */
	public InternetRadio getInternetRadioById(Integer id) {
		for (InternetRadio radio : getAllInternetRadios()) {
			if (id.equals(radio.getId())) {
				return radio;
			}
		}
		return null;
	}

	/**
	 * Returns all internet radio stations.
	 *
	 * @param includeAll
	 *            Whether disabled stations should be included.
	 * @return Possibly empty list of all internet radio stations.
	 */
	public List<InternetRadio> getAllInternetRadios(boolean includeAll) {
		List<InternetRadio> all = internetRadioDao.getAllInternetRadios();
		List<InternetRadio> result = new ArrayList<InternetRadio>(all.size());
		for (InternetRadio folder : all) {
			if (includeAll || folder.isEnabled()) {
				result.add(folder);
			}
		}
		return result;
	}

	/**
	 * Creates a new internet radio station.
	 *
	 * @param radio
	 *            The internet radio station to create.
	 */
	public void createInternetRadio(InternetRadio radio) {
		internetRadioDao.createInternetRadio(radio);
	}

	/**
	 * Deletes the internet radio station with the given ID.
	 *
	 * @param id
	 *            The internet radio station ID.
	 */
	public void deleteInternetRadio(Integer id) {
		internetRadioDao.deleteInternetRadio(id);
	}

	/**
	 * Updates the given internet radio station.
	 *
	 * @param radio
	 *            The internet radio station to update.
	 */
	public void updateInternetRadio(InternetRadio radio) {
		internetRadioDao.updateInternetRadio(radio);
	}

	/**
	 * Returns settings for the given user.
	 *
	 * @param username
	 *            The username.
	 * @return User-specific settings. Never <code>null</code>.
	 */
	public UserSettings getUserSettings(String username) {
		if (cachedUserSettings.containsKey(username)) {
			return cachedUserSettings.get(username);
		} else {
			UserSettings settings = userDao.getUserSettings(username);
			if (settings == null) {
				LOG.error("Creating default user settings.");
				settings = createDefaultUserSettings(username);
			}
			cachedUserSettings.put(username, settings);
			return settings;
		}
	}

	public String getLastFmUsername(String username) {
		UserSettings userSettings = getUserSettings(username);
		return userSettings.isLastFmEnabled() ? userSettings
				.getLastFmUsername() : null;
	}

	public void clearUserSettingsCache(String username) {
		cachedUserSettings.remove(username);
	}

	private UserSettings createDefaultUserSettings(String username) {
		UserSettings settings = new UserSettings(username);
		settings.setShowNowPlayingEnabled(true);
		settings.setShowChatEnabled(true);
		settings.setPartyModeEnabled(false);
		settings.setNowPlayingAllowed(true);
		settings.setLastFmEnabled(false);
		settings.setLastFmUsername(null);
		settings.setSpotifyUsername(null);
		settings.setDefaultHomeArtists((short) 20);
		settings.setDefaultHomeAlbums((short) 20);
		settings.setDefaultHomeSongs((short) 50);
		settings.setArtistGridWidth((short) 5);
		settings.setAlbumGridLayout(true);
		settings.setRelatedArtists((short) 15);
		settings.setRecommendedArtists((short) 5);
		settings.setReluctantArtistLoading(false);
		settings.setOnlyAlbumArtistRecommendations(true);
		settings.setViewStatsForAllUsers(false);
		settings.setChanged(new Date());

		UserSettings.Visibility main = settings.getMainVisibility();
		main.setCaptionCutoff(35);
		main.setTrackNumberVisible(true);
		main.setArtistVisible(true);
		main.setDurationVisible(true);

		UserSettings.Visibility playlist = settings.getPlaylistVisibility();
		playlist.setCaptionCutoff(35);
		playlist.setArtistVisible(true);
		playlist.setAlbumVisible(true);
		playlist.setYearVisible(true);
		playlist.setDurationVisible(true);
		playlist.setBitRateVisible(true);
		playlist.setFormatVisible(true);
		playlist.setFileSizeVisible(true);

		UserSettings.Visibility home = settings.getHomeVisibility();
		home.setCaptionCutoff(35);
		home.setArtistVisible(true);
		home.setAlbumVisible(true);
		home.setYearVisible(true);
		home.setDurationVisible(true);

		return settings;
	}

	public void setDefaultMusicCabinetUserSettings() {
		// Sonic white happens to be the only theme with icons for genre
		// browser/radio,
		// so change everybody's theme to it the first time we run MusicCabinet.
		if (properties.getProperty(KEY_MUSICCABINET_FIRST_INVOCATION) == null) {
			setThemeId("musiccabinet_white");
			for (User user : userDao.getAllUsers()) {
				UserSettings settings = getUserSettings(user.getUsername());
				settings.setThemeId("musiccabinet_white");
				settings.setChanged(new Date());
				updateUserSettings(settings);
			}
			properties.setProperty(KEY_MUSICCABINET_FIRST_INVOCATION, "false");
			properties.setProperty(KEY_GETTING_STARTED_ENABLED, "true");
			save(true);
		}
	}

	/**
	 * Updates settings for the given username.
	 *
	 * @param settings
	 *            The user-specific settings.
	 */
	public void updateUserSettings(UserSettings settings) {
		userDao.updateUserSettings(settings);
	}

	/**
	 * Returns all system avatars.
	 *
	 * @return All system avatars.
	 */
	public List<Avatar> getAllSystemAvatars() {
		return avatarDao.getAllSystemAvatars();
	}

	/**
	 * Returns the system avatar with the given ID.
	 *
	 * @param id
	 *            The system avatar ID.
	 * @return The avatar or <code>null</code> if not found.
	 */
	public Avatar getSystemAvatar(int id) {
		return avatarDao.getSystemAvatar(id);
	}

	/**
	 * Returns the custom avatar for the given user.
	 *
	 * @param username
	 *            The username.
	 * @return The avatar or <code>null</code> if not found.
	 */
	public Avatar getCustomAvatar(String username) {
		return avatarDao.getCustomAvatar(username);
	}

	/**
	 * Sets the custom avatar for the given user.
	 *
	 * @param avatar
	 *            The avatar, or <code>null</code> to remove the avatar.
	 * @param username
	 *            The username.
	 */
	public void setCustomAvatar(Avatar avatar, String username) {
		avatarDao.setCustomAvatar(avatar, username);
	}

	private void setProperty(String key, String value) {
		if (value == null) {
			properties.remove(key);
		} else {
			properties.setProperty(key, value);
		}
	}

	private String[] toStringArray(String s) {
		List<String> result = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(s, " ");
		while (tokenizer.hasMoreTokens()) {
			result.add(tokenizer.nextToken());
		}

		return result.toArray(new String[result.size()]);
	}

	private void validateLicense() {
		String email = getLicenseEmail();
		Date date = getLicenseDate();

		if (email == null || date == null) {
			licenseValidated = false;
			return;
		}

		licenseValidated = true;

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 120000);
		HttpConnectionParams.setSoTimeout(client.getParams(), 120000);
		HttpGet method = new HttpGet(
				"http://subsonic.org/backend/validateLicense.view" + "?email="
						+ StringUtil.urlEncode(email) + "&date="
						+ date.getTime() + "&version="
						+ versionService.getLocalVersion());
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String content = client.execute(method, responseHandler);
			licenseValidated = content != null && content.contains("true");
			if (!licenseValidated) {
				LOG.warn("License key is not valid.");
			}
		} catch (Throwable x) {
			LOG.warn("Failed to validate license.", x);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public void validateLicenseAsync() {
		new Thread() {
			@Override
			public void run() {
				validateLicense();
			}
		}.start();
	}

	public void setInternetRadioDao(InternetRadioDao internetRadioDao) {
		this.internetRadioDao = internetRadioDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setAvatarDao(AvatarDao avatarDao) {
		this.avatarDao = avatarDao;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	public String getMusicCabinetLastFMUsername() {
		return properties.getProperty(KEY_MUSICCABINET_LASTFM_USERNAME, "");
	}

	public void setMusicCabinetLastFMUsername(String username) {
		properties.setProperty(KEY_MUSICCABINET_LASTFM_USERNAME, username);
	}

	public String getMusicCabinetJDBCPassword() {
		String password = properties.getProperty(
				KEY_MUSICCABINET_JDBC_PASSWORD, "");
		try {
			if (password.length() > 0) {
				password = utf8HexDecode(password);
			}
		} catch (Exception e) {
			LOG.warn("UTF8 not supported? Could not read password", e);
		}
		if ("".equals(password)) {
			return System.getProperty("musiccabinet.jdbc.password");
		}
		return password;
	}

	public void setMusicCabinetJDBCPassword(String password) {
		// storing the password hex-encoded obviously isn't "safe", but better
		// than clear-text.
		properties.setProperty(KEY_MUSICCABINET_JDBC_PASSWORD,
				utf8HexEncode(password));
	}

	public int getArtistRadioArtistCount() {
		return getInt(KEY_MUSICCABINET_ARTIST_RADIO_ARTIST_COUNT,
				DEFAULT_MUSICCABINET_ARTIST_RADIO_ARTIST_COUNT);
	}

	public void setArtistRadioArtistCount(int artistCount) {
		setInt(KEY_MUSICCABINET_ARTIST_RADIO_ARTIST_COUNT, artistCount);
	}

	public int getArtistRadioTotalCount() {
		return getInt(KEY_MUSICCABINET_ARTIST_RADIO_TOTAL_COUNT,
				DEFAULT_MUSICCABINET_ARTIST_RADIO_TOTAL_COUNT);
	}

	public void setArtistRadioTotalCount(int totalCount) {
		setInt(KEY_MUSICCABINET_ARTIST_RADIO_TOTAL_COUNT, totalCount);
	}

	public int getArtistTopTracksTotalCount() {
		return getInt(KEY_MUSICCABINET_ARTIST_TOP_TRACKS_TOTAL_COUNT,
				DEFAULT_MUSICCABINET_ARTIST_TOP_TRACKS_TOTAL_COUNT);
	}

	public void setArtistTopTracksTotalCount(int totalCount) {
		setInt(KEY_MUSICCABINET_ARTIST_TOP_TRACKS_TOTAL_COUNT, totalCount);
	}

	public int getGenreRadioArtistCount() {
		return getInt(KEY_MUSICCABINET_GENRE_RADIO_ARTIST_COUNT,
				DEFAULT_MUSICCABINET_GENRE_RADIO_ARTIST_COUNT);
	}

	public void setGenreRadioArtistCount(int artistCount) {
		setInt(KEY_MUSICCABINET_GENRE_RADIO_ARTIST_COUNT, artistCount);
	}

	public int getGenreRadioTotalCount() {
		return getInt(KEY_MUSICCABINET_GENRE_RADIO_TOTAL_COUNT,
				DEFAULT_MUSICCABINET_GENRE_RADIO_TOTAL_COUNT);
	}

	public void setGenreRadioTotalCount(int totalCount) {
		setInt(KEY_MUSICCABINET_GENRE_RADIO_TOTAL_COUNT, totalCount);
	}

	public int getRelatedArtistsSamplerArtistCount() {
		return getInt(KEY_MUSICCABINET_RELATED_ARTISTS_SAMPLER_ARTIST_COUNT,
				DEFAULT_MUSICCABINET_RELATED_ARTISTS_SAMPLER_ARTIST_COUNT);
	}

	public void setRelatedArtistsSamplerArtistCount(int artistCount) {
		setInt(KEY_MUSICCABINET_RELATED_ARTISTS_SAMPLER_ARTIST_COUNT,
				artistCount);
	}

	public int getRandomSongCount() {
		return getInt(KEY_MUSICCABINET_RANDOM_SONG_COUNT,
				DEFAULT_MUSICCABINET_RANDOM_SONG_COUNT);
	}

	public void setRandomSongCount(int randomSongCount) {
		setInt(KEY_MUSICCABINET_RANDOM_SONG_COUNT, randomSongCount);
	}

	public boolean isPreferLastFmArtwork() {
		return getBoolean(KEY_MUSICCABINET_PREFER_LAST_FM_ARTWORK,
				DEFAULT_MUSICCABINET_PREFER_LAST_FM_ARTWORK);
	}

	public void setPreferLastFmArtwork(boolean preferLastFmArtwork) {
		setBoolean(KEY_MUSICCABINET_PREFER_LAST_FM_ARTWORK, preferLastFmArtwork);
	}

	public int getRadioMinimumSongLength() {
		return getInt(KEY_MUSICCABINET_RADIO_MINIMUM_SONG_LENGTH,
				DEFAULT_MUSICCABINET_RADIO_MINIMUM_SONG_LENGTH);
	}

	public void setRadioMinimumSongLength(int minLength) {
		setInt(KEY_MUSICCABINET_RADIO_MINIMUM_SONG_LENGTH, minLength);
	}

	public int getRadioMaximumSongLength() {
		return getInt(KEY_MUSICCABINET_RADIO_MAXIMUM_SONG_LENGTH,
				DEFAULT_MUSICCABINET_RADIO_MAXIMUM_SONG_LENGTH);
	}

	public void setRadioMaximumSongLength(int maxLength) {
		setInt(KEY_MUSICCABINET_RADIO_MAXIMUM_SONG_LENGTH, maxLength);
	}

	public String getLastFmLanguage() {
		return properties.getProperty(KEY_MUSICCABINET_LAST_FM_LANGUAGE,
				DEFAULT_MUSICCABINET_LAST_FM_LANGUAGE);
	}

	public void setLastFmLanguage(String language) {
		setProperty(KEY_MUSICCABINET_LAST_FM_LANGUAGE, language);
	}

	public boolean isSyncStarredAndLovedTracks() {
		return getBoolean(KEY_MUSICCABINET_SYNC_STARRED_AND_LOVED_TRACKS,
				DEFAULT_MUSICCABINET_SYNC_STARRED_AND_LOVED_TRACKS);
	}

	public void setSyncStarredAndLovedTracks(boolean syncStarredAndLovedTracks) {
		setBoolean(KEY_MUSICCABINET_SYNC_STARRED_AND_LOVED_TRACKS,
				syncStarredAndLovedTracks);
	}

	public boolean isPreferLocalGenres() {
		return getBoolean(KEY_MUSICCABINET_PREFER_LOCAL_GENRES,
				DEFAULT_MUSICCABINET_PREFER_LOCAL_GENRES);
	}

	public void setPreferLocalGenres(boolean preferLocalGenres) {
		setBoolean(KEY_MUSICCABINET_PREFER_LOCAL_GENRES, preferLocalGenres);
	}

	public String getSpotifyCache() {
		return properties.getProperty(KEY_SPOTIFY_CACHE, getSubsonicHome()
				+ File.separator + SettingsService.DEFAULT_SPOTIFY_CACHE);
	}

	public void setSpotifyCache(String spotifyCache) {
		setProperty(KEY_SPOTIFY_CACHE, spotifyCache);
	}
	
	public String getSpotifyKey() {
		return properties.getProperty(KEY_SPOTIFY_KEY, getSubsonicHome()
				+ File.separator + SettingsService.DEFAULT_SPOTIFY_KEY);
	}

	public void setSpotifyKey(String spotifyKey) {
		setProperty(KEY_SPOTIFY_KEY, spotifyKey);
	}
	

	public String getSpotifyUserName() {
		return properties.getProperty(KEY_SPOTIFY_USERNAME);
	}

	public void setSpotifyUserName(String spotifyUserName) {
		setProperty(KEY_SPOTIFY_USERNAME, spotifyUserName);
	}

	public String getSpotifyPassword() {
		return properties.getProperty(KEY_SPOTIFY_PASSWORD);
	}

	public void setSpotifyPassword(String spotifyPassword) {
		setProperty(KEY_SPOTIFY_PASSWORD, spotifyPassword);
	}

	public void setSpotifySettingsService(
			SpotifySettingsService spotifySettingsService) {
		this.spotifySettingsService = spotifySettingsService;
	}

}
