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

import static java.io.File.separator;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFolder;

/**
 * Provides database services for media folders.
 *
 * @author Sindre Mehus
 */
public class MediaFolderDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(MediaFolderDao.class);
    private static final String COLUMNS = "id, path, name, indexed, changed";
    private final MediaFolderRowMapper rowMapper = new MediaFolderRowMapper();

    /**
     * Returns all media folders.
     *
     * @return Possibly empty list of all media folders.
     */
    public List<MediaFolder> getAllMediaFolders() {
        String sql = "select " + COLUMNS + " from music_folder order by upper(name)";
        return query(sql, rowMapper);
    }
    
    public List<MediaFolder> getIndexedMediaFolders() {
        String sql = "select " + COLUMNS + " from music_folder where indexed order by upper(name)";
        return query(sql, rowMapper);
    }

    public List<MediaFolder> getNonIndexedMediaFolders() {
        String sql = "select " + COLUMNS + " from music_folder where not indexed order by upper(name)";
        return query(sql, rowMapper);
    }

    /**
     * Creates a new media folder.
     *
     * @param mediaFolder The media folder to create.
     */
    public void createMediaFolder(MediaFolder mediaFolder) {
        String sql = "insert into music_folder (" + COLUMNS + ") values (null, ?, ?, ?, ?)";
        update(sql, mediaFolder.getPath(), mediaFolder.getName(), mediaFolder.isIndexed(), mediaFolder.getChanged());
        LOG.info("Created media folder " + mediaFolder.getPath());
    }

    /**
     * Updates the given media folder.
     *
     * @param mediaFolder The media folder to update.
     */
    public void updateMediaFolder(MediaFolder mediaFolder) {
        String sql = "update music_folder set path=?, name=?, indexed=?, changed=? where id=?";
        update(sql, mediaFolder.getPath().getPath(), mediaFolder.getName(),
                mediaFolder.isIndexed(), mediaFolder.getChanged(), mediaFolder.getId());
    }

    /**
     * Deletes the media folder with the given ID.
     *
     * @param id The media folder ID.
     */
    public void deleteMediaFolder(Integer id) {
        String sql = "delete from music_folder where id=?";
        update(sql, id);
        LOG.info("Deleted media folder with ID " + id);
    }

    public boolean hasIndexedParentFolder(String folder) {
    	List<String> paths = getJdbcTemplate().queryForList(
    			"select path from music_folder where indexed", String.class);
    	for (String path : paths) {
    		if ((path.endsWith(separator) && folder.startsWith(path)) ||
    			(!path.endsWith(separator) && folder.startsWith(path + separator))) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public void setChildFoldersToNonIndexed(Set<String> paths) {
    	for (String path : paths) {
        	update("update music_folder set indexed = false where indexed and path like ?", 
        			(path.endsWith(separator) ? path : (path + separator)) + "%");
    	}
    }

    private static class MediaFolderRowMapper implements ParameterizedRowMapper<MediaFolder> {
        public MediaFolder mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MediaFolder(rs.getInt(1), new File(rs.getString(2)), rs.getString(3), rs.getBoolean(4), rs.getTimestamp(5));
        }
    }

}
