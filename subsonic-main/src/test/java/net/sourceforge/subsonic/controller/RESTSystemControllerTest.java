package net.sourceforge.subsonic.controller;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import com.github.hakko.musiccabinet.util.ResourceUtil;

public class RESTSystemControllerTest {

    private RESTSystemController systemController = new RESTSystemController();

    private static final String PING = "rest/system/ping.xml";
    private static final String LICENSE = "rest/system/getLicense.xml";

    @Before
    public void setUp() {
        PlayerService playerService = mock(PlayerService.class);
        systemController.setPlayerService(playerService);

        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void system_ping() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        systemController.ping(request, response);

        assertXMLEqual(new ResourceUtil(PING).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void system_getLicense() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        SettingsService settingsService = mock(SettingsService.class);
        when(settingsService.isLicenseValid()).thenReturn(true);
        when(settingsService.getLicenseEmail()).thenReturn("foo@bar.com");
        when(settingsService.getLicenseCode()).thenReturn("ABC123DEF");
        when(settingsService.getLicenseDate()).thenReturn(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2009-09-03 14:46:43"));
        systemController.setSettingsService(settingsService);

        systemController.getLicense(request, response);

        assertXMLEqual(new ResourceUtil(LICENSE).getContent(), sw.getBuffer().toString());
    }

}
