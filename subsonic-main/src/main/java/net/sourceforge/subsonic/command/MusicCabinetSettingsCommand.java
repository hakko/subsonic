package net.sourceforge.subsonic.command;

import java.util.List;

import com.github.hakko.musiccabinet.domain.model.aggr.SearchIndexUpdateProgress;

import net.sourceforge.subsonic.controller.MusicCabinetSettingsController;

/**
 * Command used in {@link MusicCabinetSettingsController}.
 *
 * @author hakko / MusicCabinet
 */
public class MusicCabinetSettingsCommand {

	private boolean isDatabaseRunning;
	private boolean isPasswordCorrect;
	private boolean isPasswordAttemptWrong;
	private boolean isDatabaseUpdated;
	private boolean isSearchIndexBeingCreated;
	private boolean isSearchIndexCreated;
	private List<SearchIndexUpdateProgress> updateProgress;

	private String musicCabinetJDBCPassword;
	private String lastFMUsername;
	private boolean updateDatabase;
	private boolean updateSearchIndex;

	public boolean isDatabaseRunning() {
		return isDatabaseRunning;
	}
	
	public void setDatabaseRunning(boolean isDatabaseRunning) {
		this.isDatabaseRunning = isDatabaseRunning;
	}
	
	public boolean isPasswordCorrect() {
		return isPasswordCorrect;
	}

	public void setPasswordCorrect(boolean isPasswordCorrect) {
		this.isPasswordCorrect = isPasswordCorrect;
	}

	public boolean isPasswordAttemptWrong() {
		return isPasswordAttemptWrong;
	}

	public void setPasswordAttemptWrong(boolean isPasswordAttemptWrong) {
		this.isPasswordAttemptWrong = isPasswordAttemptWrong;
	}

	public boolean isDatabaseUpdated() {
		return isDatabaseUpdated;
	}
	
	public void setDatabaseUpdated(boolean isDatabaseUpdated) {
		this.isDatabaseUpdated = isDatabaseUpdated;
	}

	public boolean isSearchIndexBeingCreated() {
		return isSearchIndexBeingCreated;
	}

	public void setSearchIndexBeingCreated(boolean isSearchIndexBeingCreated) {
		this.isSearchIndexBeingCreated = isSearchIndexBeingCreated;
	}

	public List<SearchIndexUpdateProgress> getUpdateProgress() {
		return updateProgress;
	}

	public void setUpdateProgress(List<SearchIndexUpdateProgress> updateProgress) {
		this.updateProgress = updateProgress;
	}

	public boolean isSearchIndexCreated() {
		return isSearchIndexCreated;
	}

	public void setSearchIndexCreated(boolean isSearchIndexCreated) {
		this.isSearchIndexCreated = isSearchIndexCreated;
	}
	
	public String getMusicCabinetJDBCPassword() {
		return musicCabinetJDBCPassword;
	}

	public void setMusicCabinetJDBCPassword(String musicCabinetJDBCPassword) {
		this.musicCabinetJDBCPassword = musicCabinetJDBCPassword;
	}

	public String getLastFMUsername() {
		return lastFMUsername;
	}

	public void setLastFMUsername(String lastFMUsername) {
		this.lastFMUsername = lastFMUsername;
	}

	public boolean isUpdateDatabase() {
		return updateDatabase;
	}

	public void setUpdateDatabase(boolean updateDatabase) {
		this.updateDatabase = updateDatabase;
	}

	public boolean isUpdateSearchIndex() {
		return updateSearchIndex;
	}

	public void setUpdateSearchIndex(boolean updateSearchIndex) {
		this.updateSearchIndex = updateSearchIndex;
	}
	
}