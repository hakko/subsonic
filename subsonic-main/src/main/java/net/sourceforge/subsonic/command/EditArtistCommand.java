package net.sourceforge.subsonic.command;

import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;

import net.sourceforge.subsonic.controller.EditArtistController;

/**
 * Command used in {@link EditArtistController}.
 *
 * @author hakko / MusicCabinet
 */
public class EditArtistCommand {

	private int id;
	private String artist;
	private ArtistInfo artistInfo;
	private String bioSummary;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public ArtistInfo getArtistInfo() {
		return artistInfo;
	}

	public void setArtistInfo(ArtistInfo artistInfo) {
		this.artistInfo = artistInfo;
	}

	public String getBioSummary() {
		return bioSummary;
	}

	public void setBioSummary(String bioSummary) {
		this.bioSummary = bioSummary;
	}

}