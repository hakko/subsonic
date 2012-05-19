package net.sourceforge.subsonic.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.github.hakko.musiccabinet.domain.model.aggr.SearchIndexUpdateProgress;
import com.github.hakko.musiccabinet.service.DatabaseAdministrationService;
import com.github.hakko.musiccabinet.service.PlaylistGeneratorService;
import com.github.hakko.musiccabinet.service.SearchIndexUpdateService;

import net.sourceforge.subsonic.command.MusicCabinetSettingsCommand;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SettingsService;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the MusicCabinet settings page.
 *
 * @author hakko / MusicCabinet
 */
public class MusicCabinetSettingsController extends SimpleFormController {

    private DatabaseAdministrationService dbAdmService;
    private PlaylistGeneratorService playlistService;
    
    private SettingsService settingsService;
    private SearchService searchService;
    
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        MusicCabinetSettingsCommand command = new MusicCabinetSettingsCommand();
        
        command.setDatabaseRunning(dbAdmService.isRDBMSRunning());
        command.setPasswordCorrect(dbAdmService.isPasswordCorrect(
        		settingsService.getMusicCabinetJDBCPassword()));
        command.setDatabaseUpdated(dbAdmService.isDatabaseUpdated());
        command.setSearchIndexBeingCreated(searchService.isIndexBeingCreated());
        if (command.isSearchIndexBeingCreated()) {
        	command.setUpdateProgress(getSearchIndexUpdateProgress());
        }
        if (command.isDatabaseRunning() && command.isDatabaseUpdated()) {
        	command.setSearchIndexCreated(playlistService.isSearchIndexCreated());
        }
        
        command.setLastFMUsername(settingsService.getMusicCabinetLastFMUsername());
        command.setArtistRadioArtistCount(settingsService.getArtistRadioArtistCount());
        command.setArtistRadioTotalCount(settingsService.getArtistRadioTotalCount());
        command.setArtistTopTracksTotalCount(settingsService.getArtistTopTracksTotalCount());
        command.setGenreRadioArtistCount(settingsService.getGenreRadioArtistCount());
        command.setGenreRadioTotalCount(settingsService.getGenreRadioTotalCount());
        
        return command;
    }
    
    /*
     * Build a list of all ongoing system updates and their progress
     * (scanning/indexing library, fetching all kinds of data from last.fm, etc)
     */
    private List<SearchIndexUpdateProgress> getSearchIndexUpdateProgress() throws IOException {
    	List<SearchIndexUpdateProgress> updateProgress = 
    			new ArrayList<SearchIndexUpdateProgress>();
    	
    	SearchIndexUpdateProgress subsonicScanProgress = new SearchIndexUpdateProgress();
    	subsonicScanProgress.setFinishedOperations(searchService.getSubsonicScannedFiles());
    	subsonicScanProgress.setUpdateDescription("music files and folders scanned");
    	updateProgress.add(subsonicScanProgress);
    	
    	SearchIndexUpdateProgress musicCabinetScanProgress = new SearchIndexUpdateProgress();
    	musicCabinetScanProgress.setFinishedOperations(searchService.getMusicCabinetScannedFiles());
    	musicCabinetScanProgress.setUpdateDescription("music files added for indexing");
    	updateProgress.add(musicCabinetScanProgress);
    	
    	for (SearchIndexUpdateService updateService : 
    		searchService.getSearchIndexUpdateServices()) {
    		updateProgress.add(updateService.getProgress());
    	}
    	
    	return updateProgress;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        MusicCabinetSettingsCommand command = (MusicCabinetSettingsCommand) comm;

        if (command.isUpdateDatabase()) {
        	dbAdmService.loadNewDatabasUpdates();
        	command.setDatabaseUpdated(dbAdmService.isDatabaseUpdated());
        }
        
        if (command.isUpdateSearchIndex()) {
            searchService.createIndex();
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
        
        settingsService.setArtistRadioArtistCount(command.getArtistRadioArtistCount());
        settingsService.setArtistRadioTotalCount(command.getArtistRadioTotalCount());
        settingsService.setArtistTopTracksTotalCount(command.getArtistTopTracksTotalCount());
        settingsService.setGenreRadioArtistCount(command.getGenreRadioArtistCount());
        settingsService.setGenreRadioTotalCount(command.getGenreRadioTotalCount());
        settingsService.save();
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
 
    public void setSearchService(SearchService searchService) {
    	this.searchService = searchService;
    }
    
}