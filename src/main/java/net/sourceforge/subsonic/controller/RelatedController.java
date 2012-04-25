package net.sourceforge.subsonic.controller;

import static java.net.URLEncoder.encode;
import static net.sourceforge.subsonic.util.StringUtil.ENCODING_UTF8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.util.Util;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.exception.ApplicationException;
import com.github.hakko.musiccabinet.service.ArtistInfoService;
import com.github.hakko.musiccabinet.service.ArtistRecommendationService;

/**
 * Controller for the page displaying related artists.
 *
 * @author hakko / MusicCabinet
 */
public class RelatedController extends ParameterizableViewController {

	private ArtistRecommendationService recommendationService;
	private ArtistInfoService artistInfoService;
	private MusicFileService musicFileService;
	
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String path = request.getParameter("path");
        Map<String, Object> map = new HashMap<String, Object>();

        try {
        	ArtistInfo artistInfo = Util.square(artistInfoService.getArtistInfo(path));
        	List<ArtistRecommendation> artistsInLibrary = Util.square(recommendationService.getRelatedArtistsInLibrary(path, 15));
        	List<String> namesNotInLibrary = recommendationService.getRelatedArtistsNotInLibrary(path, 5);

        	List<ArtistRecommendation> artistsNotInLibrary = new ArrayList<ArtistRecommendation>();
        	for (String name : namesNotInLibrary) {
        		artistsNotInLibrary.add(new ArtistRecommendation(name, encode(name, ENCODING_UTF8)));
        	}

        	map.put("path", path);
            map.put("artist", getFirstArtistName(path));
            map.put("artistInfo", artistInfo);
            map.put("artistsInLibrary", artistsInLibrary);
            map.put("artistsNotInLibrary", artistsNotInLibrary);
        } catch (ApplicationException e) {
            return new ModelAndView("musicCabinetUnavailable");
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private String getFirstArtistName(String pathName) throws IOException {
    	List<MusicFile> musicFiles = musicFileService.getMusicFile(pathName).getDescendants(true, false);
    	for (MusicFile musicFile : musicFiles) {
    		if (musicFile.isFile()) {
    			return musicFile.getMetaData().getArtist();
    		}
    	}
    	return null;
    }
    
    // Spring setters
    
	public void setArtistRecommendationService(ArtistRecommendationService recommendationService) {
		this.recommendationService = recommendationService;
	}

	public void setArtistInfoService(ArtistInfoService artistInfoService) {
		this.artistInfoService = artistInfoService;
	}
	
	public void setMusicFileService(MusicFileService musicFileService) {
		this.musicFileService = musicFileService;
	}
	
}