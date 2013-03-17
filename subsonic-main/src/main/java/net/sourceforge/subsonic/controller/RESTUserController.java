package net.sourceforge.subsonic.controller;

import static net.sourceforge.subsonic.security.RESTRequestParameterProcessingFilter.decrypt;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.command.UserSettingsCommand;
import net.sourceforge.subsonic.domain.TranscodeScheme;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

public class RESTUserController extends RESTAbstractController {

    private SettingsService settingsService;
    private SecurityService securityService;
    private UserSettingsController userSettingsController;

    public void getUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        String username;
        try {
            username = ServletRequestUtils.getRequiredStringParameter(request, "username");
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
            return;
        }

        User currentUser = securityService.getCurrentUser(request);
        if (!username.equals(currentUser.getUsername()) && !currentUser.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, currentUser.getUsername() + " is not authorized to get details for other users.");
            return;
        }

        User requestedUser = securityService.getUserByName(username);
        if (requestedUser == null) {
            error(request, response, ErrorCode.NOT_FOUND, "No such user: " + username);
            return;
        }

        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("user", getAttributes(requestedUser), true);
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void getUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        XMLBuilder builder = createXMLBuilder(request, response, true);
        builder.add("users", false);
        for (User user : securityService.getAllUsers()) {
            builder.add("user", getAttributes(user), true);
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void createUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to create new users.");
            return;
        }

        try {
            UserSettingsCommand command = new UserSettingsCommand();
            command.setUsername(ServletRequestUtils.getRequiredStringParameter(request, "username"));
            command.setPassword(decrypt(ServletRequestUtils.getRequiredStringParameter(request, "password")));
            command.setEmail(ServletRequestUtils.getRequiredStringParameter(request, "email"));
            command.setLdapAuthenticated(ServletRequestUtils.getBooleanParameter(request, "ldapAuthenticated", false));
            command.setAdminRole(ServletRequestUtils.getBooleanParameter(request, "adminRole", false));
            command.setCommentRole(ServletRequestUtils.getBooleanParameter(request, "commentRole", false));
            command.setCoverArtRole(ServletRequestUtils.getBooleanParameter(request, "coverArtRole", false));
            command.setDownloadRole(ServletRequestUtils.getBooleanParameter(request, "downloadRole", false));
            command.setStreamRole(ServletRequestUtils.getBooleanParameter(request, "streamRole", true));
            command.setUploadRole(ServletRequestUtils.getBooleanParameter(request, "uploadRole", false));
            command.setJukeboxRole(ServletRequestUtils.getBooleanParameter(request, "jukeboxRole", false));
            command.setPlaylistRole(ServletRequestUtils.getBooleanParameter(request, "playlistRole", false));
            command.setPodcastRole(ServletRequestUtils.getBooleanParameter(request, "podcastRole", false));
            command.setSettingsRole(ServletRequestUtils.getBooleanParameter(request, "settingsRole", true));
            command.setTranscodeSchemeName(ServletRequestUtils.getStringParameter(request, "transcodeScheme", TranscodeScheme.OFF.name()));

            userSettingsController.createUser(command);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to delete users.");
            return;
        }

        try {
            String username = ServletRequestUtils.getRequiredStringParameter(request, "username");
            securityService.deleteUser(username);

            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        try {

            String username = ServletRequestUtils.getRequiredStringParameter(request, "username");
            String password = decrypt(ServletRequestUtils.getRequiredStringParameter(request, "password"));

            User authUser = securityService.getCurrentUser(request);
            if (!authUser.isAdminRole() && !username.equals(authUser.getUsername())) {
                error(request, response, ErrorCode.NOT_AUTHORIZED, authUser.getUsername() + " is not authorized to change password for " + username);
                return;
            }

            User user = securityService.getUserByName(username);
            user.setPassword(password);
            securityService.setSecurePassword(user);
            securityService.updateUser(user);

            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);
        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    private List<Attribute> getAttributes(User user) {
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        return Arrays.asList(
                new Attribute("username", user.getUsername()),
                new Attribute("email", user.getEmail()),
                new Attribute("scrobblingEnabled", userSettings.isLastFmEnabled()),
                new Attribute("adminRole", user.isAdminRole()),
                new Attribute("settingsRole", user.isSettingsRole()),
                new Attribute("downloadRole", user.isDownloadRole()),
                new Attribute("uploadRole", user.isUploadRole()),
                new Attribute("playlistRole", user.isPlaylistRole()),
                new Attribute("coverArtRole", user.isCoverArtRole()),
                new Attribute("commentRole", user.isCommentRole()),
                new Attribute("podcastRole", user.isPodcastRole()),
                new Attribute("streamRole", user.isStreamRole()),
                new Attribute("jukeboxRole", user.isJukeboxRole()),
                new Attribute("shareRole", user.isShareRole())
                );

    }
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setUserSettingsController(
            UserSettingsController userSettingsController) {
        this.userSettingsController = userSettingsController;
    }

}
