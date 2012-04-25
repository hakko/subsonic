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

import java.io.File;

/**
 * Details about what a user is currently listening to.
 *
 * @author Sindre Mehus
 */
public class NowPlayingInfo {

	private final File file;
    private final String username;
    private final String artist;
    private final String title;
    private final String tooltip;
    private final String streamUrl;
    private final String albumUrl;
    private final String lyricsUrl;
    private final String coverArtUrl;
    private final String coverArtZoomUrl;
    private final String avatarUrl;
    private final long firstRegistered;

    public NowPlayingInfo(File file, String user, String artist, String title, String tooltip, String streamUrl, String albumUrl,
                          String lyricsUrl, String coverArtUrl, String coverArtZoomUrl, String avatarUrl) {
    	this.file = file;
        this.username = user;
        this.artist = artist;
        this.title = title;
        this.tooltip = tooltip;
        this.streamUrl = streamUrl;
        this.albumUrl = albumUrl;
        this.lyricsUrl = lyricsUrl;
        this.coverArtUrl = coverArtUrl;
        this.coverArtZoomUrl = coverArtZoomUrl;
        this.avatarUrl = avatarUrl;
        this.firstRegistered = System.currentTimeMillis();
    }

    public File getFile() {
    	return file;
    }
    
    public String getUsername() {
        return username;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public String getLyricsUrl() {
        return lyricsUrl;
    }

    public String getCoverArtUrl() {
        return coverArtUrl;
    }

    public String getCoverArtZoomUrl() {
        return coverArtZoomUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getMinutesAgo() {
        return (int) ((System.currentTimeMillis() - firstRegistered) / 1000L / 60L);
    }
}
