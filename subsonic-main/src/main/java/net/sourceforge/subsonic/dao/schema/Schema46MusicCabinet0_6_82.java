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

import net.sourceforge.subsonic.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implements the database schema for Subsonic version 4.6,
 * with the additions for MusicCabinet 0.6.82.
 *
 */
public class Schema46MusicCabinet0_6_82 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema46MusicCabinet0_6_82.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 26") == 0) {
            LOG.info("Updating database schema to version 26.");
            template.execute("insert into version values (26)");

            if (!columnExists(template, "default_home_artists", "user_settings")) {
                LOG.info("Database column 'user_settings.default_home_artists' not found. Creating it.");
                template.execute("alter table user_settings add default_home_artists smallint default 20 not null");
                LOG.info("Database column 'user_settings.default_home_artists' was added successfully.");
            }

            if (!columnExists(template, "default_home_albums", "user_settings")) {
                LOG.info("Database column 'user_settings.default_home_albums' not found. Creating it.");
                template.execute("alter table user_settings add default_home_albums smallint default 10 not null");
                LOG.info("Database column 'user_settings.default_home_albums' was added successfully.");
            }

            if (!columnExists(template, "default_home_songs", "user_settings")) {
                LOG.info("Database column 'user_settings.default_home_songs' not found. Creating it.");
                template.execute("alter table user_settings add default_home_songs smallint default 50 not null");
                LOG.info("Database column 'user_settings.default_home_songs' was added successfully.");
            }

            if (!columnExists(template, "artist_grid_width", "user_settings")) {
                LOG.info("Database column 'user_settings.artist_grid_width' not found. Creating it.");
                template.execute("alter table user_settings add artist_grid_width smallint default 5 not null");
                LOG.info("Database column 'user_settings.artist_grid_width' was added successfully.");
            }

            if (!columnExists(template, "related_artists", "user_settings")) {
                LOG.info("Database column 'user_settings.related_artists' not found. Creating it.");
                template.execute("alter table user_settings add related_artists smallint default 15 not null");
                LOG.info("Database column 'user_settings.related_artists' was added successfully.");
            }

            if (!columnExists(template, "recommended_artists", "user_settings")) {
                LOG.info("Database column 'user_settings.recommended_artists' not found. Creating it.");
                template.execute("alter table user_settings add recommended_artists smallint default 5 not null");
                LOG.info("Database column 'user_settings.recommended_artists' was added successfully.");
            }

        }

    }

}