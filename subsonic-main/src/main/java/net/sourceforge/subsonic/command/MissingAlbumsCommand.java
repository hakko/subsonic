package net.sourceforge.subsonic.command;

import java.util.List;

import net.sourceforge.subsonic.controller.MissingAlbumsController;

/**
 * Command used in {@link MissingAlbumsController}.
 *
 * @author hakko / MusicCabinet
 */
public class MissingAlbumsCommand {

	private String artistName;
	private List<Integer> albumTypes;
	private int playedLastDays;
	
	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public List<Integer> getAlbumTypes() {
		return albumTypes;
	}

	public void setAlbumTypes(List<Integer> albumTypes) {
		this.albumTypes = albumTypes;
	}

	public int getPlayedLastDays() {
		return playedLastDays;
	}

	public void setPlayedLastDays(int playedLastDays) {
		this.playedLastDays = playedLastDays;
	}
	
}