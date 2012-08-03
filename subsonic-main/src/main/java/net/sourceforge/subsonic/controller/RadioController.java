package net.sourceforge.subsonic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.TagService;

/**
 * Controller for the genre radio.
 *
 * @author hakko / MusicCabinet
 */
public class RadioController extends ParameterizableViewController {

    private TagService tagService;
    private LibraryBrowserService libraryBrowserService;
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (!libraryBrowserService.hasArtists()) {
            return new ModelAndView("musicCabinetUnavailable");
        } else {
        	List<String> topTags = tagService.getTopTags();
        	map.put("topTags", topTags);
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    // Spring setters

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

}