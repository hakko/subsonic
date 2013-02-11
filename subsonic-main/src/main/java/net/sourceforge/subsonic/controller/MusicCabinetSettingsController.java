package net.sourceforge.subsonic.controller;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.command.MusicCabinetSettingsCommand;
import net.sourceforge.subsonic.domain.MediaFolder;
import net.sourceforge.subsonic.service.MediaFolderService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.github.hakko.musiccabinet.service.DatabaseAdministrationService;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;
import com.github.hakko.musiccabinet.service.PlaylistGeneratorService;
import com.github.hakko.musiccabinet.service.lastfm.LastFmSettingsService;
import com.github.hakko.musiccabinet.service.lastfm.WebserviceHistoryService;

/**
 * Controller for the MusicCabinet settings page.
 *
 * @author hakko / MusicCabinet
 */
public class MusicCabinetSettingsController extends SimpleFormController implements InitializingBean {

	private Logger LOG = Logger.getLogger(this.getClass());
	
    private DatabaseAdministrationService dbAdmService;
    private PlaylistGeneratorService playlistService;
    
    private SettingsService settingsService;
    private LastFmSettingsService lastFmSettingsService;
    private SearchService searchService;
    private MediaFolderService mediaFolderService;
    private LibraryUpdateService libraryUpdateService;
    private WebserviceHistoryService webserviceHistoryService;
    
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        MusicCabinetSettingsCommand command = new MusicCabinetSettingsCommand();
        
        command.setDatabaseRunning(dbAdmService.isRDBMSRunning());
        command.setPasswordCorrect(dbAdmService.isPasswordCorrect(
        		settingsService.getMusicCabinetJDBCPassword()));
        command.setDatabaseUpdated(dbAdmService.isDatabaseUpdated());
        command.setSearchIndexBeingCreated(libraryUpdateService.isIndexBeingCreated());
        if (command.isSearchIndexBeingCreated()) {
        	command.setUpdateProgress(libraryUpdateService.getSearchIndexUpdateProgress());
        }
        if (command.isDatabaseRunning() && command.isDatabaseUpdated()) {
        	if (playlistService.isSearchIndexCreated()) {
        		command.setSearchIndexCreated(true);
        	} else {
        		command.setMediaFolderNames(getMediaFolderNames()); 
        	}
        }
        
        command.setLastFMUsername(settingsService.getMusicCabinetLastFMUsername());
        command.setArtistRadioArtistCount(settingsService.getArtistRadioArtistCount());
        command.setArtistRadioTotalCount(settingsService.getArtistRadioTotalCount());
        command.setArtistTopTracksTotalCount(settingsService.getArtistTopTracksTotalCount());
        command.setGenreRadioArtistCount(settingsService.getGenreRadioArtistCount());
        command.setGenreRadioTotalCount(settingsService.getGenreRadioTotalCount());
        command.setRadioMinimumSongLength(settingsService.getRadioMinimumSongLength());
        command.setRadioMaximumSongLength(settingsService.getRadioMaximumSongLength());
        command.setRelatedArtistsSamplerArtistCount(settingsService.getRelatedArtistsSamplerArtistCount());
        command.setPreferLastFmArtwork(settingsService.isPreferLastFmArtwork());
        command.setLastFmLanguage(settingsService.getLastFmLanguage());
        command.setAvailableLanguages(getAvailableLanguages());
        command.setClearLanguageSpecificContent(false);
        command.setSyncStarredAndLovedTracks(settingsService.isSyncStarredAndLovedTracks());
        
        return command;
    }

    private Map<String, String> getAvailableLanguages() {
    	Map<String, String> languages = new TreeMap<>();
		for (Locale locale : Locale.getAvailableLocales()) {
			languages.put(locale.getDisplayLanguage(ENGLISH), locale.getLanguage());
		}
		return languages;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        MusicCabinetSettingsCommand command = (MusicCabinetSettingsCommand) comm;

        if (command.isUpdateDatabase()) {
        	dbAdmService.loadNewDatabaseUpdates();
        	command.setDatabaseUpdated(dbAdmService.isDatabaseUpdated());
        	if (playlistService.isSearchIndexCreated()) {
        		command.setSearchIndexCreated(true);
        	} else {
        		command.setMediaFolderNames(getMediaFolderNames());
        	}
        }
        
        if (command.isUpdateSearchIndex()) {
        	searchService.createIndex(false, false, true);
            command.setSearchIndexBeingCreated(true);
        }
        
        if (command.getLastFMUsername() != null) {
        	settingsService.setMusicCabinetLastFMUsername(command.getLastFMUsername());
        }
        
        if (command.getMusicCabinetJDBCPassword() != null) {
        	String password = command.getMusicCabinetJDBCPassword();
        	if (dbAdmService.isPasswordCorrect(password)) {
            	settingsService.setMusicCabinetJDBCPassword(password);
            	command.setPasswordCorrect(true);
            	dbAdmService.forcePasswordUpdate(password);
                command.setDatabaseUpdated(dbAdmService.isDatabaseUpdated());
                command.setSearchIndexCreated(playlistService.isSearchIndexCreated());
        	} else {
        		command.setPasswordAttemptWrong(true);
        	}
        }

        if (command.isClearLanguageSpecificContent()) {
        	LOG.debug("clear language specific content");
        	webserviceHistoryService.clearLanguageSpecificInvocations();
        }
        
        settingsService.setArtistRadioArtistCount(command.getArtistRadioArtistCount());
        settingsService.setArtistRadioTotalCount(command.getArtistRadioTotalCount());
        settingsService.setArtistTopTracksTotalCount(command.getArtistTopTracksTotalCount());
        settingsService.setGenreRadioArtistCount(command.getGenreRadioArtistCount());
        settingsService.setGenreRadioTotalCount(command.getGenreRadioTotalCount());
        settingsService.setRelatedArtistsSamplerArtistCount(command.getRelatedArtistsSamplerArtistCount());
        settingsService.setPreferLastFmArtwork(command.isPreferLastFmArtwork());
        settingsService.setRadioMinimumSongLength(command.getRadioMinimumSongLength());
        settingsService.setRadioMaximumSongLength(command.getRadioMaximumSongLength());
        settingsService.setLastFmLanguage(command.getLastFmLanguage());
        settingsService.setSyncStarredAndLovedTracks(command.isSyncStarredAndLovedTracks());
        settingsService.save();

        playlistService.setAllowedTrackLengthInterval(
				settingsService.getRadioMinimumSongLength(),
				settingsService.getRadioMaximumSongLength());
    }

    private List<String> getMediaFolderNames() {
		List<String> mediaFolderNames = new ArrayList<String>();
		for (MediaFolder mediaFolder : mediaFolderService.getIndexedMediaFolders()) {
			mediaFolderNames.add(mediaFolder.getName());
		}
		return mediaFolderNames;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.debug(format("afterPropertiesSet(), set interval (%d, %d)",
				settingsService.getRadioMinimumSongLength(),
				settingsService.getRadioMaximumSongLength()));
		playlistService.setAllowedTrackLengthInterval(
				settingsService.getRadioMinimumSongLength(),
				settingsService.getRadioMaximumSongLength());
		lastFmSettingsService.setSyncStarredAndLovedTracks(
				settingsService.isSyncStarredAndLovedTracks());
	}
    
    // Spring setters
    
    public void setDatabaseAdministrationService(DatabaseAdministrationService dbAdmService) {
        this.dbAdmService = dbAdmService;
    }
    
    public void setPlaylistGeneratorService(PlaylistGeneratorService playlistService) {
    	this.playlistService = playlistService;
    }

	public void setSettingsService(SettingsService settingsService) {
    	this.settingsService = settingsService;
    }
 
    public void setLastFmSettingsService(LastFmSettingsService lastFmSettingsService) {
		this.lastFmSettingsService = lastFmSettingsService;
	}

	public void setSearchService(SearchService searchService) {
    	this.searchService = searchService;
    }

	public void setMediaFolderService(MediaFolderService mediaFolderService) {
		this.mediaFolderService = mediaFolderService;
	}

	public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}

	public void setWebserviceHistoryService(WebserviceHistoryService webserviceHistoryService) {
		this.webserviceHistoryService = webserviceHistoryService;
	}
   
}