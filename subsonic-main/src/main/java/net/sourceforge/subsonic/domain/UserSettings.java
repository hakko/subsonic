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

import java.util.*;

/**
 * Represent user-specific settings.
 *
 * @author Sindre Mehus
 */
public class UserSettings {

    private String username;
    private Locale locale;
    private String themeId;
    private boolean showNowPlayingEnabled;
    private boolean showChatEnabled;
    private Visibility mainVisibility = new Visibility();
    private Visibility playlistVisibility = new Visibility();
    private Visibility homeVisibility = new Visibility();
    private boolean lastFmEnabled;
    private String lastFmUsername;
    private TranscodeScheme transcodeScheme = TranscodeScheme.OFF;
    private boolean partyModeEnabled;
    private boolean nowPlayingAllowed;
    private AvatarScheme avatarScheme = AvatarScheme.NONE;
    private Integer systemAvatarId;
    private boolean albumOrderAscending = true;
    private boolean albumOrderByYear = true;
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
    private boolean useVariousArtistShortlist;
    private boolean viewStatsForAllUsers;
    private Date changed = new Date();

    public UserSettings(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
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

    public Visibility getMainVisibility() {
        return mainVisibility;
    }

    public void setMainVisibility(Visibility mainVisibility) {
        this.mainVisibility = mainVisibility;
    }

    public Visibility getPlaylistVisibility() {
        return playlistVisibility;
    }

    public void setPlaylistVisibility(Visibility playlistVisibility) {
        this.playlistVisibility = playlistVisibility;
    }

    public Visibility getHomeVisibility() {
		return homeVisibility;
	}

	public void setHomeVisibility(Visibility homeVisibility) {
		this.homeVisibility = homeVisibility;
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

    public TranscodeScheme getTranscodeScheme() {
        return transcodeScheme;
    }

    public void setTranscodeScheme(TranscodeScheme transcodeScheme) {
        this.transcodeScheme = transcodeScheme;
    }

    public boolean isPartyModeEnabled() {
        return partyModeEnabled;
    }

    public void setPartyModeEnabled(boolean partyModeEnabled) {
        this.partyModeEnabled = partyModeEnabled;
    }

    public boolean isNowPlayingAllowed() {
        return nowPlayingAllowed;
    }

    public void setNowPlayingAllowed(boolean nowPlayingAllowed) {
        this.nowPlayingAllowed = nowPlayingAllowed;
    }

    public AvatarScheme getAvatarScheme() {
        return avatarScheme;
    }

    public void setAvatarScheme(AvatarScheme avatarScheme) {
        this.avatarScheme = avatarScheme;
    }

    public Integer getSystemAvatarId() {
        return systemAvatarId;
    }

    public void setSystemAvatarId(Integer systemAvatarId) {
        this.systemAvatarId = systemAvatarId;
    }

	public boolean isAlbumOrderAscending() {
		return albumOrderAscending;
	}

	public void setAlbumOrderAscending(boolean albumOrderAscending) {
		this.albumOrderAscending = albumOrderAscending;
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

	public boolean isUseVariousArtistShortlist() {
		return useVariousArtistShortlist;
	}

	public void setUseVariousArtistShortlist(boolean useVariousArtistShortlist) {
		this.useVariousArtistShortlist = useVariousArtistShortlist;
	}

	public boolean isViewStatsForAllUsers() {
		return viewStatsForAllUsers;
	}

	public void setViewStatsForAllUsers(boolean viewStatsForAllUsers) {
		this.viewStatsForAllUsers = viewStatsForAllUsers;
	}

	/**
     * Returns when the corresponding database entry was last changed.
     *
     * @return When the corresponding database entry was last changed.
     */
    public Date getChanged() {
        return changed;
    }

    /**
     * Sets when the corresponding database entry was last changed.
     *
     * @param changed When the corresponding database entry was last changed.
     */
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    /**
     * Configuration of what information to display about a song.
     */
    public static class Visibility {
        private int captionCutoff;
        private boolean isTrackNumberVisible;
        private boolean isAlbumVisible;
        private boolean isArtistVisible;
        private boolean isComposerVisible;
        private boolean isGenreVisible;
        private boolean isYearVisible;
        private boolean isBitRateVisible;
        private boolean isDurationVisible;
        private boolean isFormatVisible;
        private boolean isFileSizeVisible;

        public int getCaptionCutoff() {
            return captionCutoff;
        }

        public void setCaptionCutoff(int captionCutoff) {
            this.captionCutoff = captionCutoff;
        }

        public boolean isTrackNumberVisible() {
            return isTrackNumberVisible;
        }

        public void setTrackNumberVisible(boolean trackNumberVisible) {
            isTrackNumberVisible = trackNumberVisible;
        }

        public boolean isAlbumVisible() {
            return isAlbumVisible;
        }

        public void setAlbumVisible(boolean albumVisible) {
            isAlbumVisible = albumVisible;
        }

        public boolean isArtistVisible() {
            return isArtistVisible;
        }

        public void setArtistVisible(boolean artistVisible) {
            isArtistVisible = artistVisible;
        }
        
        public boolean isComposerVisible() {
			return isComposerVisible;
		}

		public void setComposerVisible(boolean isComposerVisible) {
			this.isComposerVisible = isComposerVisible;
		}

		public boolean isGenreVisible() {
            return isGenreVisible;
        }

        public void setGenreVisible(boolean genreVisible) {
            isGenreVisible = genreVisible;
        }

        public boolean isYearVisible() {
            return isYearVisible;
        }

        public void setYearVisible(boolean yearVisible) {
            isYearVisible = yearVisible;
        }

        public boolean isBitRateVisible() {
            return isBitRateVisible;
        }

        public void setBitRateVisible(boolean bitRateVisible) {
            isBitRateVisible = bitRateVisible;
        }

        public boolean isDurationVisible() {
            return isDurationVisible;
        }

        public void setDurationVisible(boolean durationVisible) {
            isDurationVisible = durationVisible;
        }

        public boolean isFormatVisible() {
            return isFormatVisible;
        }

        public void setFormatVisible(boolean formatVisible) {
            isFormatVisible = formatVisible;
        }

        public boolean isFileSizeVisible() {
            return isFileSizeVisible;
        }

        public void setFileSizeVisible(boolean fileSizeVisible) {
            isFileSizeVisible = fileSizeVisible;
        }
    }
}
