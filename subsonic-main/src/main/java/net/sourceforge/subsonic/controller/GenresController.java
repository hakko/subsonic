package net.sourceforge.subsonic.controller;

import static java.net.URLEncoder.encode;
import static net.sourceforge.subsonic.util.StringUtil.ENCODING_UTF8;
import static net.sourceforge.subsonic.util.Util.square;

import java.io.UnsupportedEncodingException;
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

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.service.ArtistRecommendationService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.lastfm.TagInfoService;
import com.github.hakko.musiccabinet.service.TagService;

/**
 * Controller for the genres browser / tag cloud.
 *
 * @author hakko / MusicCabinet
 */
public class GenresController extends ParameterizableViewController {

    private SettingsService settingsService;
    private SecurityService securityService;
    private TagService tagService;
    private TagInfoService tagInfoService;
    private ArtistRecommendationService recService;
    private LibraryBrowserService libraryBrowserService;
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    	Map<String, Object> map = new HashMap<String, Object>();

    	String genre = request.getParameter("genre");
    	int page = NumberUtils.toInt(request.getParameter("page"), 0);

        if (!libraryBrowserService.hasArtists()) {
            return new ModelAndView("musicCabinetUnavailable");
        } else if (genre != null) {
            User user = securityService.getCurrentUser(request);
            UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
            boolean albumArtists = userSettings.isOnlyAlbumArtistRecommendations();

            final int ARTISTS = userSettings.getDefaultHomeArtists();
    		List<ArtistRecommendation> ars = square(recService.getGenreArtistsInLibrary(
    				genre, page * ARTISTS, ARTISTS + 1, albumArtists));
    		if (page == 0) {
    			map.put("artistsNotInLibrary", getRecommendedArtists(
    					genre, userSettings.getRecommendedArtists(), albumArtists));
    		}
    		if (ars.size() > ARTISTS) {
    			map.put("morePages", true);
    			ars.remove(ARTISTS);
    		}
    		String genreDescription = tagInfoService.getTagInfo(genre);
    		map.put("page", page);
    		map.put("genre", genre);
    		map.put("genreDescription", genreDescription);
    		map.put("artists", ars);
    		map.put("artistGridWidth", userSettings.getArtistGridWidth());
    	} else {
    		map.put("topTagsOccurrences", tagService.getTopTagsOccurrence());
    	}

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    private List<ArtistLink> getRecommendedArtists(String genre, int amount, boolean onlyAlbumArtists) throws UnsupportedEncodingException {
    	List<String> namesNotInLibrary = recService.getGenreArtistsNotInLibrary(genre, amount, onlyAlbumArtists);

    	List<ArtistLink> artistsNotInLibrary = new ArrayList<>();
    	for (String name : namesNotInLibrary) {
    		artistsNotInLibrary.add(new ArtistLink(name, encode(name, ENCODING_UTF8)));
    	}
    	
    	return artistsNotInLibrary;
    }
    
    // Spring setters

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setTagInfoService(TagInfoService tagInfoService) {
		this.tagInfoService = tagInfoService;
	}
	
	public void setArtistRecommendationService(ArtistRecommendationService recService) {
		this.recService = recService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

}