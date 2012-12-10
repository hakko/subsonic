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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Album;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.MusicBrainzService;
import com.github.hakko.musiccabinet.service.StarService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistTopTagsService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistTopTracksService;

/**
 * Controller for the artist page.
 */
public class ArtistDetailsController extends ParameterizableViewController {

	private ArtistInfoService artistInfoService;
	private ArtistTopTagsService artistTopTagsService;
	private MusicBrainzService musicBrainzService;
	private ArtistTopTracksService artistTopTracksService;
	private MediaFileService mediaFileService;
	private SecurityService securityService;
	private SettingsService settingsService;
	private StarService starService;
	
	private static final Logger LOG = Logger.getLogger(ArtistDetailsController.class);
	
	private static final String LICENSE = "User-contributed text is available under the Creative Commons By-SA License and may also be available under the GNU FDL.";
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        
        int artistId = NumberUtils.toInt(request.getParameter("id"), -1);

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

        ArtistInfo artistInfo = artistInfoService.getDetailedArtistInfo(artistId);
        artistInfo.setBioContent(StringUtils.replace(artistInfo.getBioContent(), LICENSE, ""));
        boolean artistStarred = starService.isArtistStarred(userSettings.getLastFmUsername(), artistId);
        List<Album> albums = mediaFileService.getAlbums(musicBrainzService.getDiscography(artistId,
        		userSettings.isAlbumOrderByYear(), userSettings.isAlbumOrderAscending()));
        List<Track> topTracks = artistTopTracksService.getTopTracks(artistId);

        map.put("artistId", artistId);
        map.put("artistName", artistInfo.getArtist().getName());
    	map.put("artistInfo", Util.square(artistInfo));
        map.put("artistStarred", artistStarred);
        map.put("topTags", artistTopTagsService.getTopTags(artistId, 3));
        map.put("albums", albums);
        map.put("topTracks", topTracks);
        map.put("user", user);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    // Spring setters

	public void setArtistInfoService(ArtistInfoService artistInfoService) {
		this.artistInfoService = artistInfoService;
	}

	public void setArtistTopTagsService(ArtistTopTagsService artistTopTagsService) {
		this.artistTopTagsService = artistTopTagsService;
	}

	public void setMusicBrainzService(MusicBrainzService musicBrainzService) {
		this.musicBrainzService = musicBrainzService;
	}

	public void setArtistTopTracksService(ArtistTopTracksService artistTopTracksService) {
		this.artistTopTracksService = artistTopTracksService;
	}

	public void setMediaFileService(MediaFileService mediaFileService) {
		this.mediaFileService = mediaFileService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}
   
}