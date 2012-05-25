package net.sourceforge.subsonic.controller;

import static net.sourceforge.subsonic.util.Util.square;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.service.SearchService;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.service.ArtistRecommendationService;
import com.github.hakko.musiccabinet.service.TagInfoService;
import com.github.hakko.musiccabinet.service.TagService;

/**
 * Controller for the genres browser / tag cloud.
 *
 * @author hakko / MusicCabinet
 */
public class GenresController extends ParameterizableViewController {

    private TagService tagService;
    private TagInfoService tagInfoService;
    private ArtistRecommendationService recService;
    private SearchService searchService;
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    	Map<String, Object> map = new HashMap<String, Object>();

    	String genre = request.getParameter("genre");
        String pageParam = StringUtils.defaultIfEmpty(request.getParameter("page"), "0");
        int page = Integer.parseInt(pageParam);

        if (!searchService.hasMusicCabinetIndex()) {
            return new ModelAndView("musicCabinetUnavailable");
        } else if (request.getParameter("genre") != null) {
        	final int ARTISTS = 20;
    		List<ArtistRecommendation> ars = square(recService.getRecommendedArtistsFromGenre(
    				genre, page * ARTISTS, ARTISTS + 1));
    		if (ars.size() > ARTISTS) {
    			map.put("morePages", true);
    			ars.remove(ARTISTS);
    		}
    		String genreDescription = tagInfoService.getTagInfo(genre);
    		map.put("page", page);
    		map.put("genre", genre);
    		map.put("genreDescription", genreDescription);
    		map.put("artistRecommendations", ars);
    	} else {
    		map.put("topTagsOccurrences", tagService.getTopTagsOccurrence());
    	}

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    // Spring setters

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	public void setTagInfoService(TagInfoService tagInfoService) {
		this.tagInfoService = tagInfoService;
	}
	
	public void setArtistRecommendationService(ArtistRecommendationService recService) {
		this.recService = recService;
	}

    public void setSearchService(SearchService searchService) {
    	this.searchService = searchService;
    }

}