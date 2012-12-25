package net.sourceforge.subsonic.controller;

import static org.apache.commons.lang.StringUtils.defaultString;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.service.MediaFolderService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.service.NameSearchService;
import com.github.hakko.musiccabinet.service.TagService;

/**
 * Controller for the advanced search page.
 */
public class AdvancedSearchController extends ParameterizableViewController {

	private NameSearchService nameSearchService;
	private TagService tagService;
	private MediaFolderService mediaFolderService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        ModelAndView result = super.handleRequestInternal(request, response);

        map.put("searchQuery", defaultString(request.getParameter("searchQuery")));
        map.put("fileTypes", nameSearchService.getFileTypes());
        map.put("trackGenres", tagService.getFileTags());
        map.put("topTags", tagService.getTopTags());
        map.put("mediaFolders", mediaFolderService.getIndexedMediaFolders());

        result.addObject("model", map);
        return result;
    }
    
    // Spring setter(s)

	public void setNameSearchService(NameSearchService nameSearchService) {
		this.nameSearchService = nameSearchService;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	public void setMediaFolderService(MediaFolderService mediaFolderService) {
		this.mediaFolderService = mediaFolderService;
	}

}