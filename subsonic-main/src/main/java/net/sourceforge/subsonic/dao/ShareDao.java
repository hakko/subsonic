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

import net.sourceforge.subsonic.domain.Share;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.github.hakko.musiccabinet.configuration.SubsonicUri;
import com.github.hakko.musiccabinet.configuration.Uri;

/**
 * Provides database services for shared media.
 *
 * @author Sindre Mehus
 */
public class ShareDao extends AbstractDao {

    private static final String COLUMNS = "id, name, description, username, created, expires, last_visited, visit_count";

    private ShareRowMapper shareRowMapper = new ShareRowMapper();
    private ShareFileRowMapper shareFileRowMapper = new ShareFileRowMapper();

    /**
     * Creates a new share.
     *
     * @param share The share to create.  The ID of the share will be set by this method.
     */
    public synchronized void createShare(Share share) {
        String sql = "insert into share (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")";
        update(sql, null, share.getName(), share.getDescription(), share.getUsername(), share.getCreated(),
                share.getExpires(), share.getLastVisited(), share.getVisitCount());

        int id = getJdbcTemplate().queryForInt("select max(id) from share");
        share.setId(id);
    }

    /**
     * Returns all shares.
     *
     * @return Possibly empty list of all shares.
     */
    public List<Share> getAllShares() {
        String sql = "select " + COLUMNS + " from share";
        return query(sql, shareRowMapper);
    }

    public Share getShareByName(String shareName) {
        String sql = "select " + COLUMNS + " from share where name=?";
        return queryOne(sql, shareRowMapper, shareName);
    }

    public Share getShareById(int id) {
        String sql = "select " + COLUMNS + " from share where id=?";
        return queryOne(sql, shareRowMapper, id);
    }

    /**
     * Updates the given share.
     *
     * @param share The share to update.
     */
    public void updateShare(Share share) {
        String sql = "update share set name=?, description=?, username=?, created=?, expires=?, last_visited=?, visit_count=? where id=?";
        update(sql, share.getName(), share.getDescription(), share.getUsername(), share.getCreated(), share.getExpires(),
                share.getLastVisited(), share.getVisitCount(), share.getId());
    }

    /**
     * Creates shared files.
     *
     * @param shareId The share ID.
     * @param paths   Paths of the files to share.
     */
    public void createSharedFiles(int shareId, int... mediaFileIds) {
        String sql = "insert into share_file (share_id, media_file_id) values (?, ?)";
        for (int mediaFileId : mediaFileIds) {
            update(sql, shareId, mediaFileId);
        }
    }

    /**
     * Returns files for a share.
     *
     * @param shareId The ID of the share.
     * @return The paths of the shared files.
     */
    public List<Uri> getSharedFiles(int shareId) {
        return query("select media_file_id from share_file where share_id=?", shareFileRowMapper, shareId);
    }

    /**
     * Deletes the share with the given ID.
     *
     * @param id The ID of the share to delete.
     */
    public void deleteShare(Integer id) {
        update("delete from share where id=?", id);
    }

    private static class ShareRowMapper implements ParameterizedRowMapper<Share> {
        public Share mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Share(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5),
                    rs.getTimestamp(6), rs.getTimestamp(7), rs.getInt(8));
        }
    }

    private static class ShareFileRowMapper implements ParameterizedRowMapper<Uri> {
        public Uri mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SubsonicUri(rs.getInt(1));
        }

    }
}
