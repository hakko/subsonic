/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.ajax;

import static org.apache.commons.lang.StringUtils.trimToEmpty;

import java.util.List;

import com.github.hakko.musiccabinet.configuration.Uri;

/**
 * The playlist of a player.
 *
 * @author Sindre Mehus
 */
public class PlaylistInfo {

    private final List<Entry> entries;
    private final int index;
    private final boolean stopEnabled;
    private final boolean repeatEnabled;
    private final boolean sendM3U;
    private final float gain;
    
    public PlaylistInfo(List<Entry> entries, int index, boolean stopEnabled, boolean repeatEnabled, boolean sendM3U, float gain) {
        this.entries = entries;
        this.index = index;
        this.stopEnabled = stopEnabled;
        this.repeatEnabled = repeatEnabled;
        this.sendM3U = sendM3U;
        this.gain = gain;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public int getIndex() {
        return index;
    }

    public boolean isStopEnabled() {
        return stopEnabled;
    }

    public boolean isSendM3U() {
        return sendM3U;
    }

    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    public float getGain() {
        return gain;
    }

    public static class Entry {
        private final Short trackNumber;
        private final String title;
        private final String artist;
        private final Uri artistUri;
        private final String album;
        private final Uri albumUri;
        private final String composer;
        private final String genre;
        private final String year;
        private final String bitRate;
        private final Short duration;
        private final String durationAsString;
        private final String format;
        private final String contentType;
        private final String fileSize;
        private final String streamUrl;

        public Entry(Short trackNumber, String title, String artist, Uri artistUri, String album, Uri albumUri,
        		String composer, String genre, String year, String bitRate, Short duration, String durationAsString, 
        		String format, String contentType, String fileSize, String streamUrl) {
            this.trackNumber = trackNumber;
            this.title = title;
            this.artist = artist;
            this.artistUri = artistUri;
            this.album = album;
            this.albumUri = albumUri;
            this.composer = trimToEmpty(composer);
            this.genre = genre;
            this.year = year;
            this.bitRate = bitRate;
            this.duration = duration;
            this.durationAsString = durationAsString;
            this.format = format;
            this.contentType = contentType;
            this.fileSize = fileSize;
            this.streamUrl = streamUrl;
         }

        public Short getTrackNumber() {
            return trackNumber;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public Uri getArtistUri() {
			return artistUri;
		}

		public String getAlbum() {
            return album;
        }

        public Uri getAlbumUri() {
			return albumUri;
		}

		public String getComposer() {
			return composer;
		}

		public String getGenre() {
            return genre;
        }

        public String getYear() {
            return year;
        }

        public String getBitRate() {
            return bitRate;
        }

        public String getDurationAsString() {
            return durationAsString;
        }

        public Short getDuration() {
            return duration;
        }

        public String getFormat() {
            return format;
        }

        public String getContentType() {
            return contentType;
        }

        public String getFileSize() {
            return fileSize;
        }

        public String getStreamUrl() {
            return streamUrl;
        }
    }

}