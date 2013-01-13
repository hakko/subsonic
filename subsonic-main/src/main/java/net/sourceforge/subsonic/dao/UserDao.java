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
package net.sourceforge.subsonic.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.AvatarScheme;
import net.sourceforge.subsonic.domain.TranscodeScheme;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.domain.UserSettings.Visibility;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Provides user-related database services.
 *
 * @author Sindre Mehus
 */
public class UserDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(UserDao.class);
    private static final String USER_COLUMNS = "username, password, email, ldap_authenticated, bytes_streamed, bytes_downloaded, bytes_uploaded";
    private static final String USER_SETTINGS_COLUMNS = "username, locale, theme_id, " +
            "last_fm_enabled, last_fm_username, transcode_scheme, show_now_playing, " +
            "party_mode_enabled, now_playing_allowed, avatar_scheme, system_avatar_id, changed, show_chat, " +
            "album_order_ascending, album_order_by_year, default_home_view, default_home_artists, " +
            "default_home_albums, default_home_songs, artist_grid_width, album_grid_layout, related_artists, " +
            "recommended_artists, reluctant_artist_loading, only_album_artist_recommendations, " +
            "various_artists_shortlist, view_stats_for_all_users";
    private static final String USER_VISIBILITY_COLUMNS = "username, type, caption_cutoff, track_number, artist, " +
            "album, composer, genre, year, bit_rate, duration, format, file_size";

    private static final Integer ROLE_ID_ADMIN = 1;
    private static final Integer ROLE_ID_DOWNLOAD = 2;
    private static final Integer ROLE_ID_UPLOAD = 3;
    private static final Integer ROLE_ID_PLAYLIST = 4;
    private static final Integer ROLE_ID_COVER_ART = 5;
    private static final Integer ROLE_ID_COMMENT = 6;
    private static final Integer ROLE_ID_PODCAST = 7;
    private static final Integer ROLE_ID_STREAM = 8;
    private static final Integer ROLE_ID_SETTINGS = 9;
    private static final Integer ROLE_ID_JUKEBOX = 10;
    private static final Integer ROLE_ID_SHARE = 11;
    
    private UserRowMapper userRowMapper = new UserRowMapper();
    private UserSettingsRowMapper userSettingsRowMapper = new UserSettingsRowMapper();
    private UserVisibilityRowMapper userVisibilityRowMapper = new UserVisibilityRowMapper();

    /**
     * Returns the user with the given username.
     *
     * @param username The username used when logging in.
     * @return The user, or <code>null</code> if not found.
     */
    public User getUserByName(String username) {
        String sql = "select " + USER_COLUMNS + " from user where username=?";
        return queryOne(sql, userRowMapper, username);
    }

    /**
     * Returns all users.
     *
     * @return Possibly empty array of all users.
     */
    public List<User> getAllUsers() {
        String sql = "select " + USER_COLUMNS + " from user";
        return query(sql, userRowMapper);
    }

    public List<String> getAllLastFmUsers() {
    	String sql = "select distinct LAST_FM_USERNAME from user_settings where LAST_FM_USERNAME is not null";
    	return getJdbcTemplate().queryForList(sql, String.class);
    }
    
    /**
     * Creates a new user.
     *
     * @param user The user to create.
     */
    public void createUser(User user) {
        String sql = "insert into user (" + USER_COLUMNS + ") values (" + questionMarks(USER_COLUMNS) + ')';
        update(sql, user.getUsername(), hexEncode(user.getPassword()), user.getEmail(), user.isLdapAuthenticated(),
                user.getBytesStreamed(), user.getBytesDownloaded(), user.getBytesUploaded());
        writeRoles(user);
    }

    /**
     * Deletes the user with the given username.
     *
     * @param username The username.
     */
    public void deleteUser(String username) {
        if (User.USERNAME_ADMIN.equals(username)) {
            throw new IllegalArgumentException("Can't delete admin user.");
        }

        String sql = "delete from user_role where username=?";
        update(sql, username);

        sql = "delete from user where username=?";
        update(sql, username);
    }

    /**
     * Updates the given user.
     *
     * @param user The user to update.
     */
    public void updateUser(User user) {
        String sql = "update user set password=?, email=?, ldap_authenticated=?, bytes_streamed=?, bytes_downloaded=?, bytes_uploaded=? " +
                "where username=?";
        getJdbcTemplate().update(sql, new Object[]{hexEncode(user.getPassword()), user.getEmail(), user.isLdapAuthenticated(),
                user.getBytesStreamed(), user.getBytesDownloaded(), user.getBytesUploaded(),
                user.getUsername()});
        writeRoles(user);
    }

    /**
     * Returns the name of the roles for the given user.
     *
     * @param username The user name.
     * @return Roles the user is granted.
     */
    public String[] getRolesForUser(String username) {
        String sql = "select r.name from role r, user_role ur " +
                "where ur.username=? and ur.role_id=r.id";
        List<?> roles = getJdbcTemplate().queryForList(sql, new Object[]{username}, String.class);
        String[] result = new String[roles.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (String) roles.get(i);
        }
        return result;
    }

    /**
     * Returns settings for the given user.
     *
     * @param username The username.
     * @return User-specific settings, or <code>null</code> if no such settings exist.
     */
    public UserSettings getUserSettings(String username) {
        String sql = "select " + USER_SETTINGS_COLUMNS + " from user_settings where username=?";
        UserSettings userSettings = queryOne(sql, userSettingsRowMapper, username);
        if (userSettings != null) {
        	sql = "select " + USER_VISIBILITY_COLUMNS + " from user_visibility where username=? and type=?";
        	userSettings.setMainVisibility(queryOne(sql, userVisibilityRowMapper, username, 0));
        	userSettings.setPlaylistVisibility(queryOne(sql, userVisibilityRowMapper, username, 1));
        	userSettings.setHomeVisibility(queryOne(sql, userVisibilityRowMapper, username, 2));
        }
        return userSettings;	
    }

    /**
     * Updates settings for the given username, creating it if necessary.
     *
     * @param settings The user-specific settings.
     */
    public void updateUserSettings(UserSettings settings) {
    	JdbcTemplate template = getJdbcTemplate();
        template.update("delete from user_settings where username=?", new Object[]{settings.getUsername()});

        String sql = "insert into user_settings (" + USER_SETTINGS_COLUMNS + ") values (" + questionMarks(USER_SETTINGS_COLUMNS) + ')';
        String locale = settings.getLocale() == null ? null : settings.getLocale().toString();
        template.update(sql, new Object[]{settings.getUsername(), locale, settings.getThemeId(),
                settings.isLastFmEnabled(), settings.getLastFmUsername(),
                settings.getTranscodeScheme().name(), settings.isShowNowPlayingEnabled(),
                settings.isPartyModeEnabled(), settings.isNowPlayingAllowed(),
                settings.getAvatarScheme().name(), settings.getSystemAvatarId(), settings.getChanged(),
                settings.isShowChatEnabled(), settings.isAlbumOrderAscending(), settings.isAlbumOrderByYear(),
                settings.getDefaultHomeView(), settings.getDefaultHomeArtists(),
                settings.getDefaultHomeAlbums(), settings.getDefaultHomeSongs(),
                settings.getArtistGridWidth(), settings.isAlbumGridLayout(), settings.getRelatedArtists(), 
                settings.getRecommendedArtists(), settings.isReluctantArtistLoading(), 
                settings.isOnlyAlbumArtistRecommendations(), settings.isUseVariousArtistShortlist(),
                settings.isViewStatsForAllUsers()});
        
        template.update("delete from user_visibility where username=?", new Object[]{settings.getUsername()});

        sql = "insert into user_visibility (" + USER_VISIBILITY_COLUMNS + ") values (" + questionMarks(USER_VISIBILITY_COLUMNS) + ')';
        Visibility[] visibilities = new Visibility[]{settings.getMainVisibility(), 
        	settings.getPlaylistVisibility(), settings.getHomeVisibility()};
        for (int i = 0; i < 3; i++) {
        	Visibility v = visibilities[i];
            template.update(sql, new Object[]{settings.getUsername(), i, v.getCaptionCutoff(), 
            		v.isTrackNumberVisible(), v.isArtistVisible(), v.isAlbumVisible(), v.isComposerVisible(),
            		v.isGenreVisible(), v.isYearVisible(), v.isBitRateVisible(), 
            		v.isDurationVisible(), v.isFormatVisible(), v.isFileSizeVisible()});
        }
    }

    private static String hexEncode(String s) {
        if (s == null) {
            return null;
        }
        try {
            return "enc:" + StringUtil.utf8HexEncode(s);
        } catch (Exception e) {
            return s;
        }
    }

    private static String hexDecode(String s) {
        if (s == null) {
            return null;
        }
        if (!s.startsWith("enc:")) {
            return s;
        }
        try {
            return StringUtil.utf8HexDecode(s.substring(4));
        } catch (Exception e) {
            return s;
        }
    }

    private void readRoles(User user) {
        synchronized (user.getUsername().intern()) {
            String sql = "select role_id from user_role where username=?";
            List<?> roles = getJdbcTemplate().queryForList(sql, new Object[]{user.getUsername()}, Integer.class);
            for (Object role : roles) {
                if (ROLE_ID_ADMIN.equals(role)) {
                    user.setAdminRole(true);
                } else if (ROLE_ID_DOWNLOAD.equals(role)) {
                    user.setDownloadRole(true);
                } else if (ROLE_ID_UPLOAD.equals(role)) {
                    user.setUploadRole(true);
                } else if (ROLE_ID_PLAYLIST.equals(role)) {
                    user.setPlaylistRole(true);
                } else if (ROLE_ID_COVER_ART.equals(role)) {
                    user.setCoverArtRole(true);
                } else if (ROLE_ID_COMMENT.equals(role)) {
                    user.setCommentRole(true);
                } else if (ROLE_ID_PODCAST.equals(role)) {
                    user.setPodcastRole(true);
                } else if (ROLE_ID_STREAM.equals(role)) {
                    user.setStreamRole(true);
                } else if (ROLE_ID_SETTINGS.equals(role)) {
                    user.setSettingsRole(true);
                } else if (ROLE_ID_JUKEBOX.equals(role)) {
                    user.setJukeboxRole(true);
                } else if (ROLE_ID_SHARE.equals(role)) {
                    user.setShareRole(true);
                } else {
                    LOG.warn("Unknown role: '" + role + '\'');
                }
            }
        }
    }

    private void writeRoles(User user) {
        synchronized (user.getUsername().intern()) {
            String sql = "delete from user_role where username=?";
            getJdbcTemplate().update(sql, new Object[]{user.getUsername()});
            sql = "insert into user_role (username, role_id) values(?, ?)";
            if (user.isAdminRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_ADMIN});
            }
            if (user.isDownloadRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_DOWNLOAD});
            }
            if (user.isUploadRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_UPLOAD});
            }
            if (user.isPlaylistRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_PLAYLIST});
            }
            if (user.isCoverArtRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_COVER_ART});
            }
            if (user.isCommentRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_COMMENT});
            }
            if (user.isPodcastRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_PODCAST});
            }
            if (user.isStreamRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_STREAM});
            }
            if (user.isJukeboxRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_JUKEBOX});
            }
            if (user.isSettingsRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_SETTINGS});
            }
            if (user.isShareRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_SHARE});
            }
        }
    }

    private class UserRowMapper implements ParameterizedRowMapper<User> {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User(rs.getString(1), hexDecode(rs.getString(2)), rs.getString(3), rs.getBoolean(4),
                                 rs.getLong(5), rs.getLong(6), rs.getLong(7));
            readRoles(user);
            return user;
        }
    }

    private static class UserSettingsRowMapper implements ParameterizedRowMapper<UserSettings> {
        public UserSettings mapRow(ResultSet rs, int rowNum) throws SQLException {
            int col = 1;
            UserSettings settings = new UserSettings(rs.getString(col++));
            settings.setLocale(StringUtil.parseLocale(rs.getString(col++)));
            settings.setThemeId(rs.getString(col++));

            settings.setLastFmEnabled(rs.getBoolean(col++));
            settings.setLastFmUsername(rs.getString(col++));

            settings.setTranscodeScheme(TranscodeScheme.valueOf(rs.getString(col++)));
            settings.setShowNowPlayingEnabled(rs.getBoolean(col++));
            settings.setPartyModeEnabled(rs.getBoolean(col++));
            settings.setNowPlayingAllowed(rs.getBoolean(col++));
            settings.setAvatarScheme(AvatarScheme.valueOf(rs.getString(col++)));
            settings.setSystemAvatarId((Integer) rs.getObject(col++));
            settings.setChanged(rs.getTimestamp(col++));
            settings.setShowChatEnabled(rs.getBoolean(col++));
            settings.setAlbumOrderAscending(rs.getBoolean(col++));
            settings.setAlbumOrderByYear(rs.getBoolean(col++));
            settings.setDefaultHomeView(rs.getString(col++));
            settings.setDefaultHomeArtists(rs.getShort(col++));
            settings.setDefaultHomeAlbums(rs.getShort(col++));
            settings.setDefaultHomeSongs(rs.getShort(col++));
            settings.setArtistGridWidth(rs.getShort(col++));
            settings.setAlbumGridLayout(rs.getBoolean(col++));
            settings.setRelatedArtists(rs.getShort(col++));
            settings.setRecommendedArtists(rs.getShort(col++));
            settings.setReluctantArtistLoading(rs.getBoolean(col++));
            settings.setOnlyAlbumArtistRecommendations(rs.getBoolean(col++));
            settings.setUseVariousArtistShortlist(rs.getBoolean(col++));
            settings.setViewStatsForAllUsers(rs.getBoolean(col++));
            
            return settings;
        }
    }

    private static class UserVisibilityRowMapper implements ParameterizedRowMapper<UserSettings.Visibility> {
        public UserSettings.Visibility mapRow(ResultSet rs, int rowNum) throws SQLException {
            int col = 3; // skip username + type
            
            UserSettings.Visibility visibility = new UserSettings.Visibility();
            visibility.setCaptionCutoff(rs.getInt(col++));
            visibility.setTrackNumberVisible(rs.getBoolean(col++));
            visibility.setArtistVisible(rs.getBoolean(col++));
            visibility.setAlbumVisible(rs.getBoolean(col++));
            visibility.setComposerVisible(rs.getBoolean(col++));
            visibility.setGenreVisible(rs.getBoolean(col++));
            visibility.setYearVisible(rs.getBoolean(col++));
            visibility.setBitRateVisible(rs.getBoolean(col++));
            visibility.setDurationVisible(rs.getBoolean(col++));
            visibility.setFormatVisible(rs.getBoolean(col++));
            visibility.setFileSizeVisible(rs.getBoolean(col++));

            return visibility;
        }
    }
}
