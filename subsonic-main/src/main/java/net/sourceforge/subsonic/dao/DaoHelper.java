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

import java.io.File;

import javax.sql.DataSource;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.schema.Schema;
import net.sourceforge.subsonic.dao.schema.Schema25;
import net.sourceforge.subsonic.dao.schema.Schema26;
import net.sourceforge.subsonic.dao.schema.Schema27;
import net.sourceforge.subsonic.dao.schema.Schema28;
import net.sourceforge.subsonic.dao.schema.Schema29;
import net.sourceforge.subsonic.dao.schema.Schema30;
import net.sourceforge.subsonic.dao.schema.Schema31;
import net.sourceforge.subsonic.dao.schema.Schema32;
import net.sourceforge.subsonic.dao.schema.Schema33;
import net.sourceforge.subsonic.dao.schema.Schema34;
import net.sourceforge.subsonic.dao.schema.Schema35;
import net.sourceforge.subsonic.dao.schema.Schema36;
import net.sourceforge.subsonic.dao.schema.Schema37;
import net.sourceforge.subsonic.dao.schema.Schema38;
import net.sourceforge.subsonic.dao.schema.Schema40;
import net.sourceforge.subsonic.dao.schema.Schema43;
import net.sourceforge.subsonic.dao.schema.Schema45;
import net.sourceforge.subsonic.dao.schema.Schema46;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_5_35;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_6_12;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_6_54;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_6_60;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_6_80;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_6_82;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_6_85;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_04;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_08;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_12;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_13;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_16;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_19;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_20;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_21;
import net.sourceforge.subsonic.dao.schema.Schema46MusicCabinet0_7_22;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * DAO helper class which creates the data source, and updates the database schema.
 *
 * @author Sindre Mehus
 */
public class DaoHelper {
	
	private static final Logger LOG = Logger.getLogger(DaoHelper.class);

    private Schema[] schemas = {new Schema25(), new Schema26(), new Schema27(), new Schema28(), new Schema29(),
                                new Schema30(), new Schema31(), new Schema32(), new Schema33(), new Schema34(),
                                new Schema35(), new Schema36(), new Schema37(), new Schema38(), new Schema40(),
                                new Schema43(), new Schema45(), new Schema46(),
                                new Schema46MusicCabinet(),
                                new Schema46MusicCabinet0_5_35(),
                                new Schema46MusicCabinet0_6_12(),
                                new Schema46MusicCabinet0_6_54(),
                                new Schema46MusicCabinet0_6_60(),
                                new Schema46MusicCabinet0_6_80(),
                                new Schema46MusicCabinet0_6_82(),
                                new Schema46MusicCabinet0_6_85(),
                                new Schema46MusicCabinet0_7_04(),
                                new Schema46MusicCabinet0_7_08(),
                                new Schema46MusicCabinet0_7_12(),
                                new Schema46MusicCabinet0_7_13(),
                                new Schema46MusicCabinet0_7_16(),
                                new Schema46MusicCabinet0_7_19(),
                                new Schema46MusicCabinet0_7_20(),
                                new Schema46MusicCabinet0_7_21(),
                                new Schema46MusicCabinet0_7_22()
                                };
    
    private DataSource dataSource;
    private static boolean shutdownHookAdded;

    public DaoHelper() {
    	LOG.debug("DAO Helper constructor.");
        dataSource = createDataSource();
        checkDatabase();
        addShutdownHook();
    }

    private void addShutdownHook() {
        if (shutdownHookAdded) {
            return;
        }
        shutdownHookAdded = true;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("Shutting down database.");
                getJdbcTemplate().execute("shutdown");
                System.err.println("Done.");
            }
        });
    }

    /**
     * Returns a JDBC template for performing database operations.
     *
     * @return A JDBC template.
     */
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    private DataSource createDataSource() {
        File subsonicHome = SettingsService.getSubsonicHome();
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:file:" + subsonicHome.getPath() + "/db/subsonic");
        ds.setUsername("sa");
        ds.setPassword("");

        LOG.debug("Data source URL: " + ds.getUrl());
        
        return ds;
    }

    private void checkDatabase() {
        LOG.info("Checking database schema.");
        try {
            for (Schema schema : schemas) {
                schema.execute(getJdbcTemplate());
            }
            LOG.info("Done checking database schema.");
        } catch (Exception x) {
            LOG.error("Failed to initialize database.", x);
        }
    }

}