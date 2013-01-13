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
	private List<String> mediaFolderNames;
	
	private String musicCabinetJDBCPassword;
	private String lastFMUsername;
	private boolean updateDatabase;
	private boolean updateSearchIndex;

	private int artistRadioArtistCount;
	private int artistRadioTotalCount;
	private int artistTopTracksTotalCount;
	private int genreRadioArtistCount;
	private int genreRadioTotalCount;
	private int radioMinimumSongLength;
	private int radioMaximumSongLength;
	private int relatedArtistsSamplerArtistCount;
	private boolean preferLastFmArtwork;
	
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

	public boolean isSearchIndexCreated() {
		return isSearchIndexCreated;
	}

	public void setSearchIndexCreated(boolean isSearchIndexCreated) {
		this.isSearchIndexCreated = isSearchIndexCreated;
	}

	public List<String> getMediaFolderNames() {
		return mediaFolderNames;
	}

	public void setMediaFolderNames(List<String> mediaFolderNames) {
		this.mediaFolderNames = mediaFolderNames;
	}

	public List<SearchIndexUpdateProgress> getUpdateProgress() {
		return updateProgress;
	}

	public void setUpdateProgress(List<SearchIndexUpdateProgress> updateProgress) {
		this.updateProgress = updateProgress;
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

	public int getArtistRadioArtistCount() {
		return artistRadioArtistCount;
	}

	public void setArtistRadioArtistCount(int artistRadioArtistCount) {
		this.artistRadioArtistCount = artistRadioArtistCount;
	}

	public int getArtistRadioTotalCount() {
		return artistRadioTotalCount;
	}

	public void setArtistRadioTotalCount(int artistRadioTotalCount) {
		this.artistRadioTotalCount = artistRadioTotalCount;
	}

	public int getArtistTopTracksTotalCount() {
		return artistTopTracksTotalCount;
	}

	public void setArtistTopTracksTotalCount(int artistTopTracksTotalCount) {
		this.artistTopTracksTotalCount = artistTopTracksTotalCount;
	}

	public int getGenreRadioArtistCount() {
		return genreRadioArtistCount;
	}

	public void setGenreRadioArtistCount(int genreRadioArtistCount) {
		this.genreRadioArtistCount = genreRadioArtistCount;
	}

	public int getGenreRadioTotalCount() {
		return genreRadioTotalCount;
	}

	public void setGenreRadioTotalCount(int genreRadioTotalCount) {
		this.genreRadioTotalCount = genreRadioTotalCount;
	}

	public int getRadioMinimumSongLength() {
		return radioMinimumSongLength;
	}

	public void setRadioMinimumSongLength(int radioMinimumSongLength) {
		this.radioMinimumSongLength = radioMinimumSongLength;
	}

	public int getRadioMaximumSongLength() {
		return radioMaximumSongLength;
	}

	public void setRadioMaximumSongLength(int radioMaximumSongLength) {
		this.radioMaximumSongLength = radioMaximumSongLength;
	}

	public int getRelatedArtistsSamplerArtistCount() {
		return relatedArtistsSamplerArtistCount;
	}

	public void setRelatedArtistsSamplerArtistCount(int relatedArtistsSamplerArtistCount) {
		this.relatedArtistsSamplerArtistCount = relatedArtistsSamplerArtistCount;
	}

	public boolean isPreferLastFmArtwork() {
		return preferLastFmArtwork;
	}

	public void setPreferLastFmArtwork(boolean preferLastFmArtwork) {
		this.preferLastFmArtwork = preferLastFmArtwork;
	}

}