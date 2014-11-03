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
package net.sourceforge.subsonic.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * Represens a remote player.  A player has a unique ID, a user-defined name, a logged-on user,
 * miscellaneous identifiers, and an associated playlist.
 *
 * @author Sindre Mehus
 */
public class Player {

    private String id;
    private String name;
    private PlayerTechnology technology = PlayerTechnology.WEB;
    private String clientId;
    private String type;
    private String username;
    private String ipAddress;
    private boolean isDynamicIp = true;
    private boolean isAutoControlEnabled = true;
    private boolean spotifyEnabled = true;
    private Date lastSeen;
    private CoverArtScheme coverArtScheme = CoverArtScheme.MEDIUM;
    private TranscodeScheme transcodeScheme = TranscodeScheme.OFF;
    private Playlist playlist;

    /**
     * Returns the player ID.
     *
     * @return The player ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the player ID.
     *
     * @param id The player ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the user-defined player name.
     *
     * @return The user-defined player name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user-defined player name.
     *
     * @param name The user-defined player name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the player "technology", e.g., web, external or jukebox.
     *
     * @return The player technology.
     */
    public PlayerTechnology getTechnology() {
        return technology;
    }

    /**
     * Returns the third-party client ID (used if this player is managed over the
     * Subsonic REST API).
     *
     * @return The client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the third-party client ID (used if this player is managed over the
     * Subsonic REST API).
     *
     * @param clientId The client ID.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Sets the player "technology", e.g., web, external or jukebox.
     *
     * @param technology The player technology.
     */
    public void setTechnology(PlayerTechnology technology) {
        this.technology = technology;
    }

    public boolean isJukebox() {
        return technology == PlayerTechnology.JUKEBOX;
    }

    public boolean isExternal() {
        return technology == PlayerTechnology.EXTERNAL;
    }

    public boolean isExternalWithPlaylist() {
        return technology == PlayerTechnology.EXTERNAL_WITH_PLAYLIST;
    }

    public boolean isWeb() {
        return technology == PlayerTechnology.WEB;
    }

    /**
     * Returns the player type, e.g., WinAmp, iTunes.
     *
     * @return The player type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the player type, e.g., WinAmp, iTunes.
     *
     * @param type The player type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the logged-in user.
     *
     * @return The logged-in user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the logged-in username.
     *
     * @param username The logged-in username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns whether the player is automatically started.
     *
     * @return Whether the player is automatically started.
     */
    public boolean isAutoControlEnabled() {
        return isAutoControlEnabled;
    }

    /**
     * Sets whether the player is automatically started.
     *
     * @param isAutoControlEnabled Whether the player is automatically started.
     */
    public void setAutoControlEnabled(boolean isAutoControlEnabled) {
        this.isAutoControlEnabled = isAutoControlEnabled;
    }

    /**
     * Returns the time when the player was last seen.
     *
     * @return The time when the player was last seen.
     */
    public Date getLastSeen() {
        return lastSeen;
    }

    /**
     * Sets the time when the player was last seen.
     *
     * @param lastSeen The time when the player was last seen.
     */
    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    /**
     * Returns the cover art scheme.
     *
     * @return The cover art scheme.
     */
    public CoverArtScheme getCoverArtScheme() {
        return coverArtScheme;
    }

    /**
     * Sets the cover art scheme.
     *
     * @param coverArtScheme The cover art scheme.
     */
    public void setCoverArtScheme(CoverArtScheme coverArtScheme) {
        this.coverArtScheme = coverArtScheme;
    }

    /**
     * Returns the transcode scheme.
     *
     * @return The transcode scheme.
     */
    public TranscodeScheme getTranscodeScheme() {
        return transcodeScheme;
    }

    /**
     * Sets the transcode scheme.
     *
     * @param transcodeScheme The transcode scheme.
     */
    public void setTranscodeScheme(TranscodeScheme transcodeScheme) {
        this.transcodeScheme = transcodeScheme;
    }

    /**
     * Returns the IP address of the player.
     *
     * @return The IP address of the player.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address of the player.
     *
     * @param ipAddress The IP address of the player.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Returns whether this player has a dynamic IP address.
     *
     * @return Whether this player has a dynamic IP address.
     */
    public boolean isDynamicIp() {
        return isDynamicIp;
    }

    /**
     * Sets whether this player has a dynamic IP address.
     *
     * @param dynamicIp Whether this player has a dynamic IP address.
     */
    public void setDynamicIp(boolean dynamicIp) {
        isDynamicIp = dynamicIp;
    }

    /**
     * Returns the player's playlist.
     *
     * @return The player's playlist
     */
    public Playlist getPlaylist() {
        return playlist;
    }

    /**
     * Sets the player's playlist.
     *
     * @param playlist The player's playlist.
     */
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    /**
     * Returns a long description of the player, e.g., <code>Player 3 [admin]</code>
     *
     * @return A long description of the player.
     */
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        if (name != null) {
            builder.append(name);
        } else {
            builder.append("Player ").append(id);
        }

        builder.append(" [").append(username).append(']');
        return builder.toString();
    }

    /**
     * Returns a short description of the player, e.g., <code>Player 3</code>
     *
     * @return A short description of the player.
     */
    public String getShortDescription() {
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        return "Player " + id;
    }

    /**
     * Returns a string representation of the player.
     *
     * @return A string representation of the player.
     * @see #getDescription()
     */
    @Override
    public String toString() {
        return getDescription();
    }

	public boolean isSpotifyEnabled() {
		return spotifyEnabled;
	}

	public void setSpotifyEnabled(boolean spotifyEnabled) {
		this.spotifyEnabled = spotifyEnabled;
	}
}
