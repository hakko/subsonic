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
package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.*;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.domain.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the page used to administrate per-user settings.
 *
 * @author Sindre Mehus
 */
public class PersonalSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private SecurityService securityService;

    private static final Logger LOG = Logger.getLogger(PersonalSettingsController.class);
    
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        PersonalSettingsCommand command = new PersonalSettingsCommand();

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

        command.setUser(user);
        command.setLocaleIndex("-1");
        command.setThemeIndex("-1");
        command.setAvatars(settingsService.getAllSystemAvatars());
        command.setCustomAvatar(settingsService.getCustomAvatar(user.getUsername()));
        command.setAvatarId(getAvatarId(userSettings));
        command.setPartyModeEnabled(userSettings.isPartyModeEnabled());
        command.setShowNowPlayingEnabled(userSettings.isShowNowPlayingEnabled());
        command.setShowChatEnabled(userSettings.isShowChatEnabled());
        command.setNowPlayingAllowed(userSettings.isNowPlayingAllowed());
        command.setMainVisibility(userSettings.getMainVisibility());
        command.setPlaylistVisibility(userSettings.getPlaylistVisibility());
        command.setHomeVisibility(userSettings.getHomeVisibility());
        command.setLastFmEnabled(userSettings.isLastFmEnabled());
        command.setLastFmUsername(userSettings.getLastFmUsername());
        command.setAlbumOrderAscending(userSettings.isAlbumOrderAscending());
        command.setAlbumOrderByYear(userSettings.isAlbumOrderByYear());
        command.setDefaultHomeView(userSettings.getDefaultHomeView());
        command.setDefaultHomeArtists(userSettings.getDefaultHomeArtists());
        command.setDefaultHomeAlbums(userSettings.getDefaultHomeAlbums());
        command.setDefaultHomeSongs(userSettings.getDefaultHomeSongs());
        command.setArtistGridWidth(userSettings.getArtistGridWidth());
        command.setAlbumGridLayout(userSettings.isAlbumGridLayout());
        command.setRelatedArtists(userSettings.getRelatedArtists());
        command.setRecommendedArtists(userSettings.getRecommendedArtists());
        command.setOnlyAlbumArtistRecommendations(userSettings.isOnlyAlbumArtistRecommendations());
        command.setReluctantArtistLoading(userSettings.isReluctantArtistLoading());
        command.setUseVariousArtistsShortlist(userSettings.isUseVariousArtistShortlist());
        command.setViewStatsForAllUsers(userSettings.isViewStatsForAllUsers());
        
        Locale currentLocale = userSettings.getLocale();
        Locale[] locales = settingsService.getAvailableLocales();
        String[] localeStrings = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            localeStrings[i] = locales[i].getDisplayName(locales[i]);
            if (locales[i].equals(currentLocale)) {
                command.setLocaleIndex(String.valueOf(i));
            }
        }
        command.setLocales(localeStrings);

        String currentThemeId = userSettings.getThemeId();
        Theme[] themes = settingsService.getAvailableThemes();
        command.setThemes(themes);
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].getId().equals(currentThemeId)) {
                command.setThemeIndex(String.valueOf(i));
                break;
            }
        }

        return command;
    }

    @Override
    protected void doSubmitAction(Object comm) throws Exception {
        PersonalSettingsCommand command = (PersonalSettingsCommand) comm;

        int localeIndex = Integer.parseInt(command.getLocaleIndex());
        Locale locale = null;
        if (localeIndex != -1) {
            locale = settingsService.getAvailableLocales()[localeIndex];
        }

        int themeIndex = Integer.parseInt(command.getThemeIndex());
        String themeId = null;
        if (themeIndex != -1) {
            themeId = settingsService.getAvailableThemes()[themeIndex].getId();
        }

        String username = command.getUser().getUsername();
        UserSettings settings = settingsService.getUserSettings(username);

        settings.setLocale(locale);
        settings.setThemeId(themeId);
        settings.setPartyModeEnabled(command.isPartyModeEnabled());
        settings.setShowNowPlayingEnabled(command.isShowNowPlayingEnabled());
        settings.setShowChatEnabled(command.isShowChatEnabled());
        settings.setNowPlayingAllowed(command.isNowPlayingAllowed());
        settings.setMainVisibility(command.getMainVisibility());
        settings.setPlaylistVisibility(command.getPlaylistVisibility());
        settings.setHomeVisibility(command.getHomeVisibility());
        settings.setLastFmEnabled(command.isLastFmEnabled());
        settings.setLastFmUsername(command.getLastFmUsername());
        settings.setSystemAvatarId(getSystemAvatarId(command));
        settings.setAvatarScheme(getAvatarScheme(command));
        settings.setAlbumOrderAscending(command.isAlbumOrderAscending());
        settings.setAlbumOrderByYear(command.isAlbumOrderByYear());
        settings.setDefaultHomeView(command.getDefaultHomeView());
        settings.setDefaultHomeArtists(command.getDefaultHomeArtists());
        settings.setDefaultHomeAlbums(command.getDefaultHomeAlbums());
        settings.setDefaultHomeSongs(command.getDefaultHomeSongs());
        settings.setArtistGridWidth(command.getArtistGridWidth());
        settings.setAlbumGridLayout(command.isAlbumGridLayout());
        settings.setRelatedArtists(command.getRelatedArtists());
        settings.setRecommendedArtists(command.getRecommendedArtists());
        settings.setReluctantArtistLoading(command.isReluctantArtistLoading());
        settings.setOnlyAlbumArtistRecommendations(command.isOnlyAlbumArtistRecommendations());
        settings.setUseVariousArtistShortlist(command.isUseVariousArtistsShortlist());
        settings.setViewStatsForAllUsers(command.isViewStatsForAllUsers());
        
        settings.setChanged(new Date());
        settingsService.updateUserSettings(settings);
        settingsService.clearUserSettingsCache(username);

        command.setReloadNeeded(true); // TODO : base on locale/themeid update
    }

    private int getAvatarId(UserSettings userSettings) {
        AvatarScheme avatarScheme = userSettings.getAvatarScheme();
        return avatarScheme == AvatarScheme.SYSTEM ? userSettings.getSystemAvatarId() : avatarScheme.getCode();
    }

    private AvatarScheme getAvatarScheme(PersonalSettingsCommand command) {
        if (command.getAvatarId() == AvatarScheme.NONE.getCode()) {
            return AvatarScheme.NONE;
        }
        if (command.getAvatarId() == AvatarScheme.CUSTOM.getCode()) {
            return AvatarScheme.CUSTOM;
        }
        return AvatarScheme.SYSTEM;
    }

    private Integer getSystemAvatarId(PersonalSettingsCommand command) {
        int avatarId = command.getAvatarId();
        if (avatarId == AvatarScheme.NONE.getCode() ||
            avatarId == AvatarScheme.CUSTOM.getCode()) {
            return null;
        }
        return avatarId;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

}