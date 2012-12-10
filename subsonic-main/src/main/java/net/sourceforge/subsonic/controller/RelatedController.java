package net.sourceforge.subsonic.controller;

import static java.net.URLEncoder.encode;
import static net.sourceforge.subsonic.util.StringUtil.ENCODING_UTF8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.ArtistLink;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.service.ArtistRecommendationService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;

/**
 * Controller for the page displaying related artists.
 *
 * @author hakko / MusicCabinet
 */
public class RelatedController extends ParameterizableViewController {

    private ArtistRecommendationService recommendationService;
    private ArtistInfoService artistInfoService;
    private SecurityService securityService;
    private SettingsService settingsService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

        int id = NumberUtils.toInt(request.getParameter("id"));
        Map<String, Object> map = new HashMap<String, Object>();
        boolean onlyAlbumArtists = userSettings.isOnlyAlbumArtistRecommendations();

        ArtistInfo artistInfo = Util.square(artistInfoService.getArtistInfo(id));
        List<ArtistRecommendation> artistsInLibrary = Util.square(recommendationService.
                getRelatedArtistsInLibrary(id, userSettings.getRelatedArtists(), onlyAlbumArtists));
        List<String> namesNotInLibrary = recommendationService.
                getRelatedArtistsNotInLibrary(id, userSettings.getRecommendedArtists(), onlyAlbumArtists);

        List<ArtistLink> artistsNotInLibrary = new ArrayList<>();
        for (String name : namesNotInLibrary) {
            artistsNotInLibrary.add(new ArtistLink(name, encode(name, ENCODING_UTF8)));
        }

        map.put("id", id);
        map.put("artist", artistInfo.getArtist().getName());
        map.put("artistInfo", artistInfo);
        map.put("artists", artistsInLibrary);
        map.put("artistGridWidth", userSettings.getArtistGridWidth());
        map.put("artistsNotInLibrary", artistsNotInLibrary);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    // Spring setters
    
    public void setArtistRecommendationService(ArtistRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public void setArtistInfoService(ArtistInfoService artistInfoService) {
        this.artistInfoService = artistInfoService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

}