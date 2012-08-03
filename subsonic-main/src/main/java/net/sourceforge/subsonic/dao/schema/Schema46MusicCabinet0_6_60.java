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
package net.sourceforge.subsonic.dao.schema;

import org.springframework.jdbc.core.JdbcTemplate;

import net.sourceforge.subsonic.Logger;

/**
 * Used for creating and evolving the database schema.
 * This class implements the database schema for Subsonic version 4.6,
 * with the additions for MusicCabinet 0.6.60.
 *
 */
public class Schema46MusicCabinet0_6_60 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema46MusicCabinet0_6_60.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 24") == 0) {
            LOG.info("Updating database schema to version 24.");
            template.execute("insert into version values (24)");
        }

        if (columnExists(template, "last_fm_password", "user_settings")) {
            LOG.info("Database column 'user_settings.last_fm_password' found. Deleting it.");
            template.execute("alter table user_settings drop column user_settings.last_fm_password");
            LOG.info("Database column 'user_settings.last_fm_password' was deleted.");
            
            LOG.info("Reset all last.fm user names.");
            template.execute("update user_settings set last_fm_enabled = false, last_fm_username = null");
            LOG.info("Last.fm user names cleared.");
        }
        
        if (columnExists(template, "selected_music_folder_id", "user_settings")) {
            LOG.info("Database column 'user_settings.selected_music_folder_id' found. Deleting it.");
            template.execute("alter table user_settings drop column user_settings.selected_music_folder_id");
            LOG.info("Database column 'user_settings.selected_music_folder_id' was deleted.");
        }

    }

}