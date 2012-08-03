package net.sourceforge.subsonic.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import com.github.hakko.musiccabinet.domain.model.library.LastFmUser;
import com.github.hakko.musiccabinet.service.LastFmService;

public class LastFmSettingsController extends ParameterizableViewController {

	private static final Logger LOG = Logger.getLogger(LastFmSettingsController.class);

	private SecurityService securityService;
	private SettingsService settingsService;
	private LastFmService lastFmService;
	
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        
        String lastFmUsername = request.getParameter("lastFmUsername");
        String sessionKey = request.getParameter("sessionKey");
        LOG.debug("lastFmUsername = " + lastFmUsername + ", sessionKey = " + sessionKey);
        if (lastFmUsername != null && sessionKey != null) {
            User user = securityService.getCurrentUser(request);
            UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
            userSettings.setLastFmEnabled(true);
            userSettings.setLastFmUsername(lastFmUsername);
            userSettings.setChanged(new Date());
            settingsService.updateUserSettings(userSettings);
            lastFmService.createOrUpdateLastFmUser(new LastFmUser(lastFmUsername, sessionKey));
            return new ModelAndView(new RedirectView("personalSettings.view"));
        }

        String token = request.getParameter("token");
        LOG.debug("token = " + token);
        if (token != null) {
        	LastFmUser lastFmUser = lastFmService.identifyLastFmUser(token);
        	LOG.debug(lastFmUser.getLastFmUsername() + ", " + lastFmUser.getSessionKey());
        	
        	map.put("lastFmUsername", lastFmUser.getLastFmUsername());
        	map.put("sessionKey", lastFmUser.getSessionKey());
            map.put("token", token);
        }

        if (lastFmUsername == null && sessionKey == null && token == null) {
        	LOG.debug("Prepare data for last.fm auth.");
            map.put("api_key", LastFmService.API_KEY);
            map.put("callbackUrl", request.getRequestURL());
            LOG.debug("callback url = " + request.getRequestURL());
        }
        
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;

    }
    
    private String getCallbackUrl() {
    	return null; // TODO : verify callback URL on different network setups
    }

    // Spring setters
    
	public void setLastFmService(LastFmService lastFmService) {
		this.lastFmService = lastFmService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

}