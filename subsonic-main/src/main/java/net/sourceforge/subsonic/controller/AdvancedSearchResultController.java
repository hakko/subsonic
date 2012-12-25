package net.sourceforge.subsonic.controller;

import static java.lang.Boolean.valueOf;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.math.NumberUtils.toInt;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.music.SearchCriteria;
import com.github.hakko.musiccabinet.service.NameSearchService;
import com.github.hakko.musiccabinet.service.StarService;

/**
 * Controller for the MusicBrainz missing albums page.
 */
public class AdvancedSearchResultController extends ParameterizableViewController {

	private NameSearchService nameSearchService;
    private SecurityService securityService;
    private SettingsService settingsService;
    private MediaFileService mediaFileService;
    private StarService starService;
    
	private static final int RESULTS_PER_PAGE = 100;
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
        Map<String, Object> map = new HashMap<String, Object>();
        ModelAndView result = super.handleRequestInternal(request, response);

        int page = toInt(request.getParameter("page"));

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        String lastFmUsername = userSettings.getLastFmUsername();

        List<Integer> trackIds = getMatchingTrackIds(request, page, lastFmUsername);
        if (trackIds.size() > RESULTS_PER_PAGE) {
    		map.put("morePages", true);
    		trackIds.remove(RESULTS_PER_PAGE);
        }
        
        mediaFileService.loadMediaFiles(trackIds);
        List<MediaFile> mediaFiles = mediaFileService.getMediaFiles(trackIds);

        map.put("page", page);
        map.put("trackIds", trackIds);
        map.put("mediaFiles", mediaFiles);
    	map.put("multipleArtists", true);
        map.put("visibility", userSettings.getMainVisibility());
        map.put("user", user);
        map.put("isTrackStarred", starService.getStarredTracksMask(lastFmUsername, trackIds));
        
        result.addObject("model", map);
        return result;
    }
    
    private List<Integer> getMatchingTrackIds(HttpServletRequest request, int page, String lastFmUsername) {
        SearchCriteria criteria = new SearchCriteria();
        addFileTagCriteria(request, criteria);
        addFileHeaderCriteria(request, criteria);
        addFileCriteria(request, criteria);
        addExternalCriteria(request, criteria);
		criteria.setLastFmUsername(lastFmUsername);

		return nameSearchService.getTracks(criteria, page * RESULTS_PER_PAGE, RESULTS_PER_PAGE + 1);
    }

	private void addFileTagCriteria(HttpServletRequest request, SearchCriteria criteria) {
    	criteria.setArtist(nullIfEmpty(request.getParameter("artist")));
    	criteria.setAlbumArtist(nullIfEmpty(request.getParameter("albumArtist")));
    	criteria.setComposer(nullIfEmpty(request.getParameter("composer")));
    	criteria.setAlbum(nullIfEmpty(request.getParameter("album")));
    	criteria.setTitle(nullIfEmpty(request.getParameter("title")));
    	criteria.setTrackNrFrom(getShortParameter(request, "trackNrFrom"));
    	criteria.setTrackNrTo(getShortParameter(request, "trackNrTo"));
    	criteria.setDiscNrFrom(getShortParameter(request, "discNrFrom"));
    	criteria.setDiscNrTo(getShortParameter(request, "discNrTo"));
    	criteria.setYearFrom(getShortParameter(request, "yearFrom"));
    	criteria.setYearTo(getShortParameter(request, "yearTo"));
		criteria.setTrackGenre(nullIfEmpty(request.getParameter("trackGenre")));
	}

	@SuppressWarnings("unchecked")
	private void addFileHeaderCriteria(HttpServletRequest request, SearchCriteria criteria) {
    	criteria.setDurationFrom(getShortParameter(request, "durationFrom"));
    	criteria.setDurationTo(getShortParameter(request, "durationTo"));
    	Set<Integer> fileTypes = new HashSet<>();
    	for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
    		String parameter = e.nextElement();
    		if (parameter.startsWith("fileType")) {
    			fileTypes.add(NumberUtils.toInt(parameter.substring(8)));
    		}
    	}
    	criteria.setFiletypes(fileTypes);
	}

	private void addFileCriteria(HttpServletRequest request, SearchCriteria criteria) {
		criteria.setDirectory(nullIfEmpty(request.getParameter("directory")));
		criteria.setModifiedDays(getShortParameter(request, "modifiedDays"));
	}

	private void addExternalCriteria(HttpServletRequest request, SearchCriteria criteria) {
		criteria.setSearchQuery(nullIfEmpty(request.getParameter("searchQuery")));
		criteria.setArtistGenre(nullIfEmpty(request.getParameter("artistGenre")));
		criteria.setTopTrackRank(getShortParameter(request, "topTrackRank"));
		criteria.setOnlyStarredByUser(valueOf(request.getParameter("onlyStarredByUser")));
		criteria.setPlayedLastDays(getShortParameter(request, "playedLastDays"));
		criteria.setPlayCountFrom(getShortParameter(request, "playCountFrom"));
		criteria.setPlayCountTo(getShortParameter(request, "playCountTo"));
	}

    private Short getShortParameter(HttpServletRequest request, String parameterName) {
    	String parameterValue = request.getParameter(parameterName);
    	return isEmpty(parameterValue) ? null : NumberUtils.toShort(parameterValue);
    }
    
    private String nullIfEmpty(String parameter) {
    	return isEmpty(parameter) ? null : parameter;
    }

    // Spring setter(s)

	public void setNameSearchService(NameSearchService nameSearchService) {
		this.nameSearchService = nameSearchService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setMediaFileService(MediaFileService mediaFileService) {
		this.mediaFileService = mediaFileService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}

}