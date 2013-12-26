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
package net.sourceforge.subsonic.service;

import static net.sourceforge.subsonic.ajax.LibraryStatusService.Message.SCAN_FINISHED;
import static net.sourceforge.subsonic.ajax.LibraryStatusService.Message.SCAN_STARTED;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.ajax.LibraryStatusService;
import net.sourceforge.subsonic.domain.MediaFolder;

import com.github.hakko.musiccabinet.domain.model.library.LastFmUser;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;
import com.github.hakko.musiccabinet.service.lastfm.LastFmSettingsService;
import com.github.hakko.musiccabinet.service.library.LibraryScannerService;
import com.github.hakko.musiccabinet.service.spotify.SpotifySettingsService;

public class SearchService {

    private static final Logger LOG = Logger.getLogger(SearchService.class);

    private Timer timer;
    private MediaFolderService mediaFolderService;
    private SettingsService settingsService;

    private LibraryUpdateService libraryUpdateService;
    private LibraryScannerService libraryScannerService;
    private LibraryStatusService libraryStatusService;
    private LastFmSettingsService lastFmSettingsService;
    private SpotifySettingsService spotifySettingsService;

    /**
     * Generates the search index.  If the index already exists it will be
     * overwritten.  The index is created asynchronously, i.e., this method returns
     * before the index is created.
     */
    public void createIndex(final boolean offlineScan, final boolean onlyNewArtists, final boolean notify) {
        /**
         * Generates the search index.  If the index already exists it will be
         * overwritten.  The index is created asynchronously, i.e., this method returns
         * before the index is created.
         */
		deleteOldIndexFiles(); // TODO : remove after reaching version 0.8 or so
		updateSearchServices();
		List<MediaFolder> mediaFolders = mediaFolderService.getIndexedMediaFolders();
		Set<String> paths = new HashSet<>();
		for (int i = 0; i < mediaFolders.size(); i++) {
			paths.add(mediaFolders.get(i).getPath().getPath());
		}
		createIndex(paths, true, offlineScan, onlyNewArtists, notify);
    }

    public void createIndex(final Set<String> paths, final boolean isRootPaths,
    		final boolean offlineScan, final boolean onlyNewArtists, final boolean notify) {
        Thread thread = new Thread("Search Index Generator") { // TODO : use concurrent scheduler?
            @Override
            public void run() {
            	try {
            		if (notify) libraryStatusService.notifyLibraryUpdate(SCAN_STARTED);
            		libraryUpdateService.createSearchIndex(paths, isRootPaths, offlineScan, onlyNewArtists);
            		if (notify) libraryStatusService.notifyLibraryUpdate(SCAN_FINISHED);
            	} catch (Throwable t) {
            		LOG.warn("Search index update failed!", t);
            	}
            }
        };

        thread.start();
    }

    public void deleteMediaFolders(final Set<String> deletedPaths) {
        Thread thread = new Thread("Search Index Generator") { // TODO : use concurrent scheduler?
            @Override
            public void run() {
            	try {
        			libraryStatusService.notifyLibraryUpdate(SCAN_STARTED);
            		libraryScannerService.delete(deletedPaths);
        			libraryStatusService.notifyLibraryUpdate(SCAN_FINISHED);
            	} catch (Throwable t) {
            		LOG.warn("Could not delete " + deletedPaths, t);
            	}
            }
        };

        thread.start();

    }

    /*
     * Prepare last.fm update services with configuration kept within Subsonic.
     */
    private void updateSearchServices() {
    	String lastFmUser = settingsService.getMusicCabinetLastFMUsername();
    	if (lastFmUser.length() > 0) {
    		lastFmSettingsService.setLastFmUsername(lastFmUser);
    	}
        List<LastFmUser> users = new ArrayList<LastFmUser>();
        for (String user : settingsService.getAllLastFmUsers()) {
            users.add(new LastFmUser(user));
        }
        lastFmSettingsService.setLastFmUsers(users);
		lastFmSettingsService.setLocale(Locale.forLanguageTag(
				settingsService.getLastFmLanguage()));
		
		spotifySettingsService.setSpotifyCache(settingsService.getSpotifyCache());
		spotifySettingsService.setSpotifyUserName(settingsService.getSpotifyUserName());
    }

    /**
     * Schedule background execution of index creation.
     */
    public synchronized void schedule() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                createIndex(false, false, false);
            }
        };

        long daysBetween = settingsService.getIndexCreationInterval();
        int hour = settingsService.getIndexCreationHour();

        if (daysBetween == -1) {
            LOG.info("Automatic index creation disabled.");
            return;
        }

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTime().before(now)) {
            cal.add(Calendar.DATE, 1);
        }

        Date firstTime = cal.getTime();
        long period = daysBetween * 24L * 3600L * 1000L;
        timer.schedule(task, firstTime, period);

        LOG.info("Automatic index creation scheduled to run every " + daysBetween + " day(s), starting at " + firstTime);
    }

    /**
     * Deletes old versions of the index file.
     */
    private void deleteOldIndexFiles() {
        for (int i = 2; i <= 15; i++) {
            File file = new File(SettingsService.getSubsonicHome(), "subsonic" + i + ".index");
            try {
                if (file.exists() && file.delete()) {
                	LOG.info("Deleted old index file: " + file.getPath());
                }
            } catch (Exception x) {
                LOG.warn("Failed to delete old index file: " + file.getPath(), x);
            }
        }
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

	public void setMediaFolderService(MediaFolderService mediaFolderService) {
		this.mediaFolderService = mediaFolderService;
	}

	public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}

	public void setLibraryScannerService(LibraryScannerService libraryScannerService) {
		this.libraryScannerService = libraryScannerService;
	}

	public void setLibraryStatusService(LibraryStatusService libraryStatusService) {
		this.libraryStatusService = libraryStatusService;
	}

	public void setLastFmSettingsService(LastFmSettingsService lastFmSettingsService) {
		this.lastFmSettingsService = lastFmSettingsService;
	}
	
	public void setSpotifySettingsService(SpotifySettingsService spotifySettingsService) {
		this.spotifySettingsService = spotifySettingsService;
	}	

}