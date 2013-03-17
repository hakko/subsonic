package net.sourceforge.subsonic.controller;

import static java.util.Arrays.asList;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import com.github.hakko.musiccabinet.util.ResourceUtil;

public class RESTUserControllerTest {

    private RESTUserController userController = new RESTUserController();

    private static final String GET_USERS = "rest/user/getUsers.xml";

    @Before
    public void setUp() {
        userController.setPlayerService(mock(PlayerService.class));

        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void user_getUsers() throws Exception {
        StringWriter sw = new StringWriter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        SecurityService securityService = mock(SecurityService.class);
        when(securityService.getAllUsers()).thenReturn(asList(
                getUser("admin", null, true),
                getUser("sindre", "sindre@activeobjects.no", false)));
        userController.setSecurityService(securityService);

        SettingsService settingsService = mock(SettingsService.class);
        when(settingsService.getUserSettings("admin")).thenReturn(getUserSettings("admin", true));
        when(settingsService.getUserSettings("sindre")).thenReturn(getUserSettings("sindre", false));
        userController.setSettingsService(settingsService);

        userController.getUsers(request, response);

        assertXMLEqual(new ResourceUtil(GET_USERS).getContent(), sw.getBuffer().toString());
    }

    private User getUser(String username, String email, boolean adminRole) {
        User user = new User(username, null, email);
        user.setAdminRole(adminRole);
        user.setSettingsRole(true);
        user.setDownloadRole(adminRole);
        user.setUploadRole(adminRole);
        user.setPlaylistRole(true);
        user.setCoverArtRole(adminRole);
        user.setCommentRole(adminRole);
        user.setPodcastRole(adminRole);
        user.setStreamRole(true);
        user.setJukeboxRole(adminRole);
        user.setShareRole(adminRole);

        return user;
    }

    private UserSettings getUserSettings(String username, boolean lastFmEnabled) {
        UserSettings userSettings = new UserSettings(username);
        userSettings.setLastFmEnabled(lastFmEnabled);

        return userSettings;
    }

}
