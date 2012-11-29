package net.sourceforge.subsonic.controller;

import static com.github.hakko.musiccabinet.service.MusicBrainzService.TYPE_ALBUM;
import static com.github.hakko.musiccabinet.service.MusicBrainzService.TYPE_EP;
import static com.github.hakko.musiccabinet.service.MusicBrainzService.TYPE_SINGLE;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.music.MBAlbum;
import com.github.hakko.musiccabinet.service.MusicBrainzService;

/**
 * Controller for the MusicBrainz missing albums page.
 */
public class MissingAlbumsSearchController extends ParameterizableViewController {

    private MusicBrainzService musicBrainzService;
    private SecurityService securityService;
    private SettingsService settingsService;

	private static final int RESULTS_PER_PAGE = 100;
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
        Map<String, Object> map = new HashMap<String, Object>();
        ModelAndView result = super.handleRequestInternal(request, response);

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        String lastFmUsername = userSettings.getLastFmUsername();

        List<MBAlbum> missingAlbums = getMissingAlbums(request, lastFmUsername);
        
        if (missingAlbums.size() > RESULTS_PER_PAGE) {
    		map.put("morePages", true);
    		missingAlbums.remove(RESULTS_PER_PAGE);
        }
        
        map.put("missingAlbums", missingAlbums);
        map.put("page", toInt(request.getParameter("page")));
        
        result.addObject("model", map);
        return result;
    }

    private List<MBAlbum> getMissingAlbums(HttpServletRequest request, String lastFmUsername) {
    	String artistName = request.getParameter("artistName");
        List<Integer> albumTypes = new ArrayList<Integer>();
        if (isNotEmpty(request.getParameter("album"))) {
        	albumTypes.add(TYPE_ALBUM);
        }
        if (isNotEmpty(request.getParameter("ep"))) {
        	albumTypes.add(TYPE_EP);
        }
        if (isNotEmpty(request.getParameter("single"))) {
        	albumTypes.add(TYPE_SINGLE);
        }
        int playedWithinLastDays = toInt(request.getParameter("playedWithinLastDays"));
        int offset = toInt(request.getParameter("page")) * RESULTS_PER_PAGE;

        return musicBrainzService.getMissingAlbums(
        		artistName, albumTypes, lastFmUsername, playedWithinLastDays, offset);
    }
    
	public void setMusicBrainzService(MusicBrainzService musicBrainzService) {
		this.musicBrainzService = musicBrainzService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

}