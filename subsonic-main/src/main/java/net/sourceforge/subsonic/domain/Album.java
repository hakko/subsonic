package net.sourceforge.subsonic.domain;

import static com.github.hakko.musiccabinet.service.library.AudioTagService.UNKNOWN_ALBUM;

import java.util.ArrayList;
import java.util.List;

import com.github.hakko.musiccabinet.domain.model.music.AlbumInfo;

public class Album implements Comparable<Album> {

	private int artistId;
	private String artistName;
	private int id;
	private String title;
	private Short year;
	private String coverArtPath;
	private String coverArtUrl;
	private String coverArtZoomUrl;

	private List<Integer> trackIds;
	private List<MediaFile> mediaFiles = new ArrayList<>();
	private boolean isSelected;

	public Album() {
	}

	public Album(int id, String title, String coverArtPath) {
		this.id = id;
		this.title = title;
		this.coverArtPath = coverArtPath;
	}

	public int getArtistId() {
		return artistId;
	}

	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Short getYear() {
		return year;
	}

	public void setYear(Short year) {
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

	public List<Integer> getTrackIds() {
		return trackIds;
	}

	public void setTrackIds(List<Integer> trackIds) {
		this.trackIds = trackIds;
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

}
