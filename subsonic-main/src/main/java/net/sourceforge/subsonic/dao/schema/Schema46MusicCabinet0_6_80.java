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
 * with the additions for MusicCabinet 0.6.80.
 *
 */
public class Schema46MusicCabinet0_6_80 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema46MusicCabinet0_6_80.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 25") == 0) {
            LOG.info("Updating database schema to version 25.");
            template.execute("insert into version values (25)");

            LOG.info("Creating table 'user_visibility'.");
            template.execute("create table user_visibility ("
                    + "username varchar not null,"
            		+ "type smallint not null,"
                    + "caption_cutoff int default 35 not null,"
                    + "track_number boolean default true not null,"
                    + "artist boolean default true not null,"
                    + "album boolean default false not null,"
                    + "genre boolean default false not null,"
                    + "year boolean default false not null,"
                    + "bit_rate boolean default false not null,"
                    + "duration boolean default true not null,"
                    + "format boolean default false not null,"
                    + "file_size boolean default false not null)");

            LOG.info("Copy visibility settings from 'user_settings' to 'user_visibility'.");
            String insertSql = "insert into user_visibility (username, type,"
	           + "caption_cutoff, track_number, artist, album, genre, " 
	           + "year, bit_rate, duration, format, file_size) ";
            
            template.execute(insertSql + "select username, 0,"
            	+ "main_caption_cutoff, main_track_number, main_artist, main_album, main_genre, "
            	+ "main_year, main_bit_rate, main_duration, main_format, main_file_size from user_settings");

            template.execute(insertSql + "select username, 1,"
            	+ "playlist_caption_cutoff, playlist_track_number, playlist_artist, playlist_album, playlist_genre, "
                + "playlist_year, playlist_bit_rate, playlist_duration, playlist_format, playlist_file_size from user_settings");

            template.execute(insertSql + "select username, 2,"
                	+ "main_caption_cutoff, false, true, true, false, "
                    + "true, false, true, false, false from user_settings");

            LOG.info("Drop columns from 'user_settings'.");
    		String[] columns = ("main_caption_cutoff, main_track_number, main_artist, main_album, main_genre, "
        	+ "main_year, main_bit_rate, main_duration, main_format, main_file_size, "
        	+ "playlist_caption_cutoff, playlist_track_number, playlist_artist, playlist_album, playlist_genre, "
            + "playlist_year, playlist_bit_rate, playlist_duration, playlist_format, playlist_file_size").split(",");
            for (String column : columns) {
            	template.execute("alter table user_settings drop column " + column);
            }

            LOG.info("Table user_visibility created.");
        }

    }

}