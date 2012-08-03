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
 * with the additions for MusicCabinet 0.6.54.
 *
 */
public class Schema46MusicCabinet0_6_54 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema46MusicCabinet0_6_54.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 23") == 0) {
            LOG.info("Updating database schema to version 23.");
            template.execute("insert into version values (23)");
        }

        if (columnExists(template, "path", "share_file")) {
            LOG.info("Database column 'share_file.path' found. Deleting it.");
            template.execute("alter table share_file drop path");
            LOG.info("Database column 'share_file.path' was deleted.");
        }
        
        if (!columnExists(template, "media_file_id", "share_file")) {
            LOG.info("Database column 'share_file.media_file_id' not found. Adding it.");
            template.execute("delete from share_file");
            template.execute("alter table share_file add media_file_id integer not null");
            LOG.info("Database column 'share_file.media_file_id' was added.");
        }

    }

}