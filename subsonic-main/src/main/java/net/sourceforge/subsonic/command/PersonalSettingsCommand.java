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

import net.sourceforge.subsonic.controller.PersonalSettingsController;
import net.sourceforge.subsonic.domain.Avatar;
import net.sourceforge.subsonic.domain.Theme;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;

import java.util.List;

/**
 * Command used in {@link PersonalSettingsController}.
 *
 * @author Sindre Mehus
 */
public class PersonalSettingsCommand {
    private User user;
    private String localeIndex;
    private String[] locales;
    private String themeIndex;
    private Theme[] themes;
    private int avatarId;
    private List<Avatar> avatars;
    private Avatar customAvatar;
    private UserSettings.Visibility mainVisibility;
    private UserSettings.Visibility playlistVisibility;
    private UserSettings.Visibility homeVisibility;
    private boolean partyModeEnabled;
    private boolean showNowPlayingEnabled;
    private boolean showChatEnabled;
    private boolean nowPlayingAllowed;
    private boolean lastFmEnabled;
    private String lastFmUsername;
    private boolean albumOrderAscending;
    private boolean albumOrderByYear;
    private String defaultHomeView;
    private short defaultHomeArtists;
    private short defaultHomeAlbums;
    private short defaultHomeSongs;
    private short artistGridWidth;
    private boolean albumGridLayout;
    private short relatedArtists;
    private short recommendedArtists;
    private boolean reluctantArtistLoading;
    private boolean onlyAlbumArtistRecommendations;
    private boolean useVariousArtistsShortlist;
    private boolean viewStatsForAllUsers;
    private boolean isReloadNeeded;
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLocaleIndex() {
        return localeIndex;
    }

    public void setLocaleIndex(String localeIndex) {
        this.localeIndex = localeIndex;
    }

    public String[] getLocales() {
        return locales;
    }

    public void setLocales(String[] locales) {
        this.locales = locales;
    }

    public String getThemeIndex() {
        return themeIndex;
    }

    public void setThemeIndex(String themeIndex) {
        this.themeIndex = themeIndex;
    }

    public Theme[] getThemes() {
        return themes;
    }

    public void setThemes(Theme[] themes) {
        this.themes = themes;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public List<Avatar> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<Avatar> avatars) {
        this.avatars = avatars;
    }

    public Avatar getCustomAvatar() {
        return customAvatar;
    }

    public void setCustomAvatar(Avatar customAvatar) {
        this.customAvatar = customAvatar;
    }

    public UserSettings.Visibility getMainVisibility() {
        return mainVisibility;
    }

    public void setMainVisibility(UserSettings.Visibility mainVisibility) {
        this.mainVisibility = mainVisibility;
    }

    public UserSettings.Visibility getPlaylistVisibility() {
        return playlistVisibility;
    }

    public void setPlaylistVisibility(UserSettings.Visibility playlistVisibility) {
        this.playlistVisibility = playlistVisibility;
    }

    public UserSettings.Visibility getHomeVisibility() {
		return homeVisibility;
	}

	public void setHomeVisibility(UserSettings.Visibility homeVisibility) {
		this.homeVisibility = homeVisibility;
	}

	public boolean isPartyModeEnabled() {
        return partyModeEnabled;
    }

    public void setPartyModeEnabled(boolean partyModeEnabled) {
        this.partyModeEnabled = partyModeEnabled;
    }

    public boolean isShowNowPlayingEnabled() {
        return showNowPlayingEnabled;
    }

    public void setShowNowPlayingEnabled(boolean showNowPlayingEnabled) {
        this.showNowPlayingEnabled = showNowPlayingEnabled;
    }

    public boolean isShowChatEnabled() {
        return showChatEnabled;
    }

    public void setShowChatEnabled(boolean showChatEnabled) {
        this.showChatEnabled = showChatEnabled;
    }

    public boolean isNowPlayingAllowed() {
        return nowPlayingAllowed;
    }

    public void setNowPlayingAllowed(boolean nowPlayingAllowed) {
        this.nowPlayingAllowed = nowPlayingAllowed;
    }

    public boolean isLastFmEnabled() {
        return lastFmEnabled;
    }

    public void setLastFmEnabled(boolean lastFmEnabled) {
        this.lastFmEnabled = lastFmEnabled;
    }

    public String getLastFmUsername() {
        return lastFmUsername;
    }

    public void setLastFmUsername(String lastFmUsername) {
        this.lastFmUsername = lastFmUsername;
    }

    public boolean isAlbumOrderAscending() {
		return albumOrderAscending;
	}

	public void setAlbumOrderAscending(boolean albumOrderingAscending) {
		this.albumOrderAscending = albumOrderingAscending;
	}

	public boolean isAlbumOrderByYear() {
		return albumOrderByYear;
	}

	public void setAlbumOrderByYear(boolean albumOrderByYear) {
		this.albumOrderByYear = albumOrderByYear;
	}

	public String getDefaultHomeView() {
		return defaultHomeView;
	}

	public void setDefaultHomeView(String defaultHomeView) {
		this.defaultHomeView = defaultHomeView;
	}

	public short getDefaultHomeArtists() {
		return defaultHomeArtists;
	}

	public void setDefaultHomeArtists(short defaultHomeArtists) {
		this.defaultHomeArtists = defaultHomeArtists;
	}

	public short getDefaultHomeAlbums() {
		return defaultHomeAlbums;
	}

	public void setDefaultHomeAlbums(short defaultHomeAlbums) {
		this.defaultHomeAlbums = defaultHomeAlbums;
	}

	public short getDefaultHomeSongs() {
		return defaultHomeSongs;
	}

	public void setDefaultHomeSongs(short defaultHomeSongs) {
		this.defaultHomeSongs = defaultHomeSongs;
	}

	public short getArtistGridWidth() {
		return artistGridWidth;
	}

	public void setArtistGridWidth(short artistGridWidth) {
		this.artistGridWidth = artistGridWidth;
	}

	public boolean isAlbumGridLayout() {
		return albumGridLayout;
	}

	public void setAlbumGridLayout(boolean albumGridLayout) {
		this.albumGridLayout = albumGridLayout;
	}

	public short getRelatedArtists() {
		return relatedArtists;
	}

	public void setRelatedArtists(short relatedArtists) {
		this.relatedArtists = relatedArtists;
	}

	public short getRecommendedArtists() {
		return recommendedArtists;
	}

	public void setRecommendedArtists(short recommendedArtists) {
		this.recommendedArtists = recommendedArtists;
	}

	public boolean isReluctantArtistLoading() {
		return reluctantArtistLoading;
	}

	public void setReluctantArtistLoading(boolean reluctantArtistLoading) {
		this.reluctantArtistLoading = reluctantArtistLoading;
	}

	public boolean isOnlyAlbumArtistRecommendations() {
		return onlyAlbumArtistRecommendations;
	}

	public void setOnlyAlbumArtistRecommendations(boolean onlyAlbumArtistRecommendations) {
		this.onlyAlbumArtistRecommendations = onlyAlbumArtistRecommendations;
	}

	public boolean isUseVariousArtistsShortlist() {
		return useVariousArtistsShortlist;
	}

	public void setUseVariousArtistsShortlist(boolean useVariousArtistsShortlist) {
		this.useVariousArtistsShortlist = useVariousArtistsShortlist;
	}

	public boolean isViewStatsForAllUsers() {
		return viewStatsForAllUsers;
	}

	public void setViewStatsForAllUsers(boolean viewStatsForAllUsers) {
		this.viewStatsForAllUsers = viewStatsForAllUsers;
	}

	public boolean isReloadNeeded() {
        return isReloadNeeded;
    }

    public void setReloadNeeded(boolean reloadNeeded) {
        isReloadNeeded = reloadNeeded;
    }
}
