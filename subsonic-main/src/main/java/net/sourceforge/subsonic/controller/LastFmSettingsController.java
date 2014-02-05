package net.sourceforge.subsonic.controller;

import java.net.*;
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

import org.apache.commons.lang.StringUtils;
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

            final String callbackUrl = getCallbackUrl(request);
            map.put("callbackUrl", callbackUrl);
            LOG.debug("callback url = " + callbackUrl);
        }
        
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;

    }

    /**
     * Determine a callback url for the callback URL.
     *
     * This uses the same approach as net.sourceforge.subsonic.service.ShareService#getShareBaseUrl
     * However, instead of trying to detect the name of a machine it uses the request url as fallback.
     *
     * @return the determined callback url
     */
    private String getCallbackUrl(HttpServletRequest request) {
        try{
            URL host;
            URL requestURI = new URL(request.getRequestURL().toString());

            // from getShareBaseUrl
            if (StringUtils.isNotEmpty(settingsService.getShareUrlPrefix())) {
                LOG.debug("Using share prefix");
                host = new URL(settingsService.getShareUrlPrefix());
            } else if (settingsService.isUrlRedirectionEnabled()) {
                LOG.debug("Using redirects");
                host = new URL("http", settingsService.getUrlRedirectFrom() + ".subsonic.org", "");
            } else {
                LOG.debug("Using current address of request");
                host = requestURI;
            }

            LOG.info("Callback url " + host.toString() + " + " + requestURI.getFile());

            // callback url is determined url of current page + 'file' part of URL
            return new URL(host, requestURI.getFile()).toString();
        } catch (MalformedURLException e){
            LOG.error("URL " + e.getMessage() + " was invalid");

            // part of the input was likely invalid. Do not try to re-parse the current URL
            return request.getRequestURL().toString();
        }
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