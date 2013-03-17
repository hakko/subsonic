package net.sourceforge.subsonic.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.AttributeSet;

public class RESTSystemController extends RESTAbstractController {

    private SettingsService settingsService;

    public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
        response.getWriter().print(builder);
    }

    public void getLicense(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        String email = settingsService.getLicenseEmail();
        String key = settingsService.getLicenseCode();
        Date date = settingsService.getLicenseDate();
        boolean valid = settingsService.isLicenseValid();

        AttributeSet attributes = new AttributeSet();
        attributes.add("valid", true);
        if (valid) {
            attributes.add("email", email);
            attributes.add("key", key);
            attributes.add("date", StringUtil.toISO8601(date));
        }

        builder.add("license", attributes, true);
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

}
