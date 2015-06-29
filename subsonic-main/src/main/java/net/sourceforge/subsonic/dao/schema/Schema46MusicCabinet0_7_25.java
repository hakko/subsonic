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
 * Used for creating and evolving the database schema. This class implements the
 * database schema for Subsonic version 4.6, with the additions for MusicCabinet
 * 0.7.19.
 * 
 */
public class Schema46MusicCabinet0_7_25 extends Schema {

	private static final Logger LOG = Logger
			.getLogger(Schema46MusicCabinet0_7_25.class);

	@Override
	public void execute(JdbcTemplate template) {

		if (template
				.queryForInt("select count(*) from version where version = 40") == 0) {

			LOG.info("Altering Database column 'user_settings.song_notification'.");
			template.execute("alter table user_settings add column song_notification boolean default true");
			LOG.info("Database column 'user_settings.song_notification' was altered successfully.");

			LOG.info("Updating database schema to version 40.");
			template.execute("insert into version values (40)");
		}

	}

}
