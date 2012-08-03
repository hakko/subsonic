package net.sourceforge.subsonic.domain;

public class ArtistLink {

	private final String artistName;
	private final String artistURL;
	
	public ArtistLink(String artistName, String artistURL) {
		this.artistName = artistName;
		this.artistURL = artistURL;
	}

	public String getArtistName() {
		return artistName;
	}

	public String getArtistURL() {
		return artistURL;
	}
	
}
