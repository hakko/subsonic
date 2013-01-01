package net.sourceforge.subsonic.domain;

import java.io.Serializable;

/**
 * Contains meta-data (song title, artist, album etc) for a music file.
 */
public class MetaData implements Serializable {

	private static final long serialVersionUID = -7838952173604977486L;

	private Integer discNumber;
	private Integer trackNumber;
	private String title;
	private String artist;
	private int artistId;
	private String albumArtist;
	private String album;
	private int albumId;
	private String composer;
	private String genre;
	private String year;
	private boolean hasLyrics;
	private Integer bitRate;
	private Boolean variableBitRate;
	private Integer duration;
	private String format;
	private Long fileSize;
	private Integer width;
	private Integer height;

	public Integer getDiscNumber() {
		return discNumber;
	}

	public void setDiscNumber(Integer discNumber) {
		this.discNumber = discNumber;
	}

	public Integer getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(Integer trackNumber) {
		this.trackNumber = trackNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getArtistId() {
		return artistId;
	}

	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getYear() {
		return (year == null || year.length() < 4) ?  year : year.substring(0, 4);
	}

	public Integer getYearAsInteger() {
		if (year == null || year.length() < 4) {
			return null;
		}
		try {
			return new Integer(year.substring(0, 4));
		} catch (NumberFormatException x) {
			return null;
		}
	}

	public void setYear(String year) {
		this.year = year;
	}

	public boolean hasLyrics() {
		return hasLyrics;
	}

	public void setHasLyrics(boolean hasLyrics) {
		this.hasLyrics = hasLyrics;
	}

	public Integer getBitRate() {
		return bitRate;
	}

	public void setBitRate(Integer bitRate) {
		this.bitRate = bitRate;
	}

	public Boolean getVariableBitRate() {
		return variableBitRate;
	}

	public void setVariableBitRate(Boolean variableBitRate) {
		this.variableBitRate = variableBitRate;
	}

	public Integer getDuration() {
		return duration;
	}

	public String getDurationAsString() {
		if (duration == null) {
			return null;
		}

		StringBuffer result = new StringBuffer(8);

		int seconds = duration;

		int hours = seconds / 3600;
		seconds -= hours * 3600;

		int minutes = seconds / 60;
		seconds -= minutes * 60;

		if (hours > 0) {
			result.append(hours).append(':');
			if (minutes < 10) {
				result.append('0');
			}
		}

		result.append(minutes).append(':');
		if (seconds < 10) {
			result.append('0');
		}
		result.append(seconds);

		return result.toString();
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

}