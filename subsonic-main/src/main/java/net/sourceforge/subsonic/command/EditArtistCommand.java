package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.EditArtistController;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;

/**
 * Command used in {@link EditArtistController}.
 *
 * @author hakko / MusicCabinet
 */
public class EditArtistCommand {

	private Uri uri;
	private String artist;
	private ArtistInfo artistInfo;
	private String bioSummary;
	
	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
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