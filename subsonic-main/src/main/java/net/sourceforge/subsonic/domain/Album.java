package net.sourceforge.subsonic.domain;

import static com.github.hakko.musiccabinet.service.library.AudioTagService.UNKNOWN_ALBUM;

import java.util.ArrayList;
import java.util.List;

import com.github.hakko.musiccabinet.configuration.SubsonicUri;
import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.domain.model.music.AlbumInfo;

public class Album implements Comparable<Album> {

	private Uri artistUri;
	private String artistName;
	private Uri uri;
	private String title;
	private Integer year;
	private String coverArtPath;
	private String coverArtUrl;
	private String coverArtZoomUrl;
	private Uri spotifyUri;
	private Integer rating;

	private List<? extends Uri> trackUris;
	private List<MediaFile> mediaFiles = new ArrayList<>();
	private boolean isSelected;

	public Album() {
	}

	public Album(int id, String title, String coverArtPath) {
		this.uri = new SubsonicUri(id);
		this.title = title;
		this.coverArtPath = coverArtPath;
	}

	public Uri getArtistUri() {
		return artistUri;
	}

	public void setArtistUri(Uri artistUri) {
		this.artistUri = artistUri;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	@Deprecated
	public int getId() {
		return uri.getId();
	}

	public void setId(int id) {
		this.uri = new SubsonicUri(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getCoverArtPath() {
		return coverArtPath;
	}

	public void setCoverArtPath(String coverArtPath) {
		this.coverArtPath = coverArtPath;
	}

	public String getCoverArtUrl() {
		return coverArtUrl;
	}

	public void setCoverArtUrl(String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
	}

	public String getCoverArtZoomUrl() {
		return coverArtZoomUrl;
	}

	public void setCoverArtZoomUrl(AlbumInfo albumInfo) {
		if (albumInfo.getExtraLargeImageUrl() != null) {
			this.coverArtZoomUrl = albumInfo.getExtraLargeImageUrl();
		} else if (albumInfo.getLargeImageUrl() != null) {
			this.coverArtZoomUrl = albumInfo.getLargeImageUrl();
		}
	}

	public void addMediaFile(MediaFile musicFile) {
		mediaFiles.add(musicFile);
	}

	public List<? extends Uri> getTrackUris() {
		return trackUris;
	}

	public void setTrackIds(List<? extends Uri> trackUris) {
		this.trackUris = trackUris;
	}

	public List<MediaFile> getMediaFiles() {
		return mediaFiles;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public int compareTo(Album album) {
		if (UNKNOWN_ALBUM.equals(title)) {
			return 1;
		} else if (UNKNOWN_ALBUM.equals(album.title)) {
			return -1;
		} else if (year == null) {
			return 1;
		} else if (album.year == null) {
			return -1;
		}
		return year.compareTo(album.year);
	}
	
	public Uri getUri() {
		return uri;
	}
	
	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public Uri getSpotifyUri() {
		return spotifyUri;
	}

	public void setSpotifyUri(Uri spotifyUri) {
		this.spotifyUri = spotifyUri;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}
	

}
