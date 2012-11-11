package net.sourceforge.subsonic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.Util;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.domain.model.music.Tag;
import com.github.hakko.musiccabinet.service.StarService;
import com.github.hakko.musiccabinet.service.TagService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;
import com.github.hakko.musiccabinet.service.lastfm.ArtistTopTagsService;

/**
 * Controller for the artist genre configuration page.
 */
public class ArtistGenresController extends ParameterizableViewController {

    private SecurityService securityService;
    private SettingsService settingsService;
	private ArtistInfoService artistInfoService;
	private ArtistTopTagsService artistTopTagsService;
	private StarService starService;
	private TagService tagService;
	
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        
        int artistId = NumberUtils.toInt(request.getParameter("id"), -1);

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        String lastFmUsername = userSettings.getLastFmUsername();
        
        List<Tag> topTags = artistTopTagsService.getTopTags(artistId, 25);
        List<String> tags = tagService.getTopTags();
        for (Tag topTag : topTags) {
        	tags.remove(topTag.getName());
        }
        
        ArtistInfo artistInfo = artistInfoService.getArtistInfo(artistId);
        map.put("artistId", artistId);
        map.put("artistName", artistInfo.getArtist().getName());
        if (artistInfo.getLargeImageUrl() != null && artistInfo.getBioSummary() != null) {
        	map.put("artistInfo", Util.square(artistInfo));
        	map.put("artistInfoImageSize", 126);
        }
        map.put("topTags", topTags);
        map.put("tags", tags);
        map.put("lastFmUsername", lastFmUsername);
        map.put("artistStarred", starService.isArtistStarred(lastFmUsername, artistId));

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    // Spring setters

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setArtistInfoService(ArtistInfoService artistInfoService) {
		this.artistInfoService = artistInfoService;
	}

	public void setArtistTopTagsService(ArtistTopTagsService artistTopTagsService) {
		this.artistTopTagsService = artistTopTagsService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}
	
}