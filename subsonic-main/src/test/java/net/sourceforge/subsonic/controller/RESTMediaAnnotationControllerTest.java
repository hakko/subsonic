package net.sourceforge.subsonic.controller;

import static net.sourceforge.subsonic.controller.RESTBrowseController.ALBUM_ID;
import static net.sourceforge.subsonic.controller.RESTBrowseController.ARTIST_ID;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import com.github.hakko.musiccabinet.service.StarService;
import com.github.hakko.musiccabinet.util.ResourceUtil;

public class RESTMediaAnnotationControllerTest {

    private RESTMediaAnnotationController mediaAnnotationController =
            new RESTMediaAnnotationController();

    private static final String STAR = "rest/mediaAnnotation/star.xml";
    private static final String UNSTAR = "rest/mediaAnnotation/unstar.xml";

    @Before
    public void setUp() {
        mediaAnnotationController.setPlayerService(mock(PlayerService.class));
        mediaAnnotationController.setSecurityService(mock(SecurityService.class));
        mediaAnnotationController.setSettingsService(mock(SettingsService.class));
        mediaAnnotationController.setStarService(mock(StarService.class));

        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void mediaAnnotation_star() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterValues("id")).thenReturn(new String[]{
                ARTIST_ID + "123", ALBUM_ID + "456"});
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        mediaAnnotationController.star(request, response);

        assertXMLEqual(new ResourceUtil(STAR).getContent(), sw.getBuffer().toString());
    }

    @Test
    public void mediaAnnotation_unstar() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterValues("id")).thenReturn(new String[]{"789"});
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        mediaAnnotationController.unstar(request, response);

        assertXMLEqual(new ResourceUtil(UNSTAR).getContent(), sw.getBuffer().toString());
    }

}
