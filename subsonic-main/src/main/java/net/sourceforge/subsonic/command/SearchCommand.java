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
package net.sourceforge.subsonic.command;

import java.util.List;

import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.User;

import com.github.hakko.musiccabinet.domain.model.music.Album;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.domain.model.music.Track;

/**
 * Command used in {@link SearchController}.
 *
 * @author Sindre Mehus
 */
public class SearchCommand {

    private String query;
    private List<Artist> artists;
    private List<Album> albums;
    private List<Track> songs;
    private boolean[] isTrackStarred;
    private boolean indexCreated;
    private User user;
    private boolean partyModeEnabled;
    private Player player;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isIndexCreated() {
        return indexCreated;
    }

    public void setIndexCreated(boolean indexCreated) {
        this.indexCreated = indexCreated;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Track> getSongs() {
        return songs;
    }

    public void setSongs(List<Track> songs) {
        this.songs = songs;
    }

    public boolean[] getIsTrackStarred() {
		return isTrackStarred;
	}

	public void setIsTrackStarred(boolean[] isTrackStarred) {
		this.isTrackStarred = isTrackStarred;
	}

	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPartyModeEnabled() {
        return partyModeEnabled;
    }

    public void setPartyModeEnabled(boolean partyModeEnabled) {
        this.partyModeEnabled = partyModeEnabled;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}