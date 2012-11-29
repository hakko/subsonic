package net.sourceforge.subsonic.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.service.MusicBrainzService;

/**
 * Controller for the MusicBrainz missing albums page.
 */
public class MissingAlbumsController extends ParameterizableViewController {

    private MusicBrainzService musicBrainzService;
    
	private static final Logger LOG = Logger.getLogger(MissingAlbumsController.class);

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        
        boolean isIndexBeingCreated = musicBrainzService.isIndexBeingCreated();
        
        if (!isIndexBeingCreated && "artists".equals(request.getParameter("update"))) {
        	isIndexBeingCreated = true;
        	updateDiscography();
        }
        
        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("isIndexBeingCreated", isIndexBeingCreated);
        if (isIndexBeingCreated) {
        	map.put("progressDescription", musicBrainzService.getProgressDescription());
        } else {
            map.put("missingAndOutdatedCount", musicBrainzService.getMissingAndOutdatedArtistsCount());
        	map.put("hasDiscography", musicBrainzService.hasDiscography());
        }

        result.addObject("model", map);
        return result;
    }
    
    private void updateDiscography() {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					LOG.debug("Update MusicBrainz discography.");
					musicBrainzService.updateDiscography();
					LOG.debug("MusicBrainz discography updated.");
				} catch (Throwable t) {
					LOG.warn("Couldn't update MusicBrainz discography!", t);
				}
			}
		});

    }

	public void setMusicBrainzService(MusicBrainzService musicBrainzService) {
		this.musicBrainzService = musicBrainzService;
	}

}