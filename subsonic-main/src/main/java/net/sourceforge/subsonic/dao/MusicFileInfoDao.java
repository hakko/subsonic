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

import net.sourceforge.subsonic.domain.MusicFileInfo;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Provides database services for music file info.
 *
 * @author Sindre Mehus
 */
public class MusicFileInfoDao extends AbstractDao {

    private static final String COLUMNS = "id, path, comment, play_count, last_played, enabled";
    private MusicFileInfoRowMapper rowMapper = new MusicFileInfoRowMapper();

    /**
     * Returns music file info for the given path. Also disabled instances are returned.
     *
     * @return Music file info for the given path, or <code>null</code> if not found.
     */
    public MusicFileInfo getMusicFileInfoForPath(String path) {
        String sql = "select " + COLUMNS + " from music_file_info where path=?";
        return queryOne(sql, rowMapper, path);
    }

    /**
     * Creates a new music file info.
     *
     * @param info The music file info to create.
     */
    public void createMusicFileInfo(MusicFileInfo info) {
        String sql = "insert into music_file_info (" + COLUMNS + ") values (null, ?, ?, ?, ?, ?)";
        update(sql, info.getPath(), info.getComment(), info.getPlayCount(), info.getLastPlayed(), info.isEnabled());
    }

    /**
     * Updates the given music file info.
     *
     * @param info The music file info to update.
     */
    public void updateMusicFileInfo(MusicFileInfo info) {
        String sql = "update music_file_info set path=?, comment=?, play_count=?, last_played=?, enabled=? where id=?";
        update(sql, info.getPath(), info.getComment(), info.getPlayCount(), info.getLastPlayed(), info.isEnabled(), info.getId());
    }

    private static class MusicFileInfoRowMapper implements ParameterizedRowMapper<MusicFileInfo> {
        public MusicFileInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MusicFileInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getTimestamp(5), rs.getBoolean(6));
        }
    }

}