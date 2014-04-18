package net.sourceforge.subsonic.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Share;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.ShareService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.util.XMLBuilder;
import net.sourceforge.subsonic.util.XMLBuilder.Attribute;
import net.sourceforge.subsonic.util.XMLBuilder.AttributeSet;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.github.hakko.musiccabinet.dao.util.URIUtil;

public class RESTShareController extends RESTAbstractController {

    private RESTBrowseController restBrowseController;
    private SecurityService securityService;
    private ShareService shareService;
    private MediaFileService mediaFileService;

    public void getShares(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);

        User user = securityService.getCurrentUser(request);
        XMLBuilder builder = createXMLBuilder(request, response, true);

        builder.add("shares", false);
        for (Share share : shareService.getSharesForUser(user)) {
            builder.add("share", createAttributesForShare(share), false);

            for (MediaFile mediaFile : shareService.getSharedFiles(share.getId())) {
                File coverArt = mediaFileService.getCoverArt(mediaFile);
                AttributeSet attributes = restBrowseController.createAttributesForMediaFile(player, coverArt, mediaFile);
                builder.add("entry", attributes, true);
            }

            builder.end();
        }
        builder.endAll();
        response.getWriter().print(builder);
    }

    public void createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);

        User user = securityService.getCurrentUser(request);
        if (!user.isShareRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to share media.");
            return;
        }

        XMLBuilder builder = createXMLBuilder(request, response, true);

        try {

            List<MediaFile> files = new ArrayList<MediaFile>();
            for (String id : ServletRequestUtils.getRequiredStringParameters(request, "id")) {
                MediaFile file = mediaFileService.getMediaFile(URIUtil.parseURI(id));
                files.add(file);
            }

            // TODO: Update api.jsp

            Share share = shareService.createShare(request, files);
            share.setDescription(request.getParameter("description"));
            long expires = ServletRequestUtils.getLongParameter(request, "expires", 0L);
            if (expires != 0) {
                share.setExpires(new Date(expires));
            }
            shareService.updateShare(share);

            builder.add("shares", false);
            builder.add("share", createAttributesForShare(share), false);

            for (MediaFile mediaFile : shareService.getSharedFiles(share.getId())) {
                File coverArt = mediaFileService.getCoverArt(mediaFile);
                AttributeSet attributes = restBrowseController.createAttributesForMediaFile(player, coverArt, mediaFile);
                builder.add("entry", attributes, true);
            }

            builder.endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void deleteShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            request = wrapRequest(request);
            User user = securityService.getCurrentUser(request);
            int id = ServletRequestUtils.getRequiredIntParameter(request, "id");

            Share share = shareService.getShareById(id);
            if (share == null) {
                error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
                return;
            }
            if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
                error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to delete shared media.");
                return;
            }

            shareService.deleteShare(id);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    public void updateShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            request = wrapRequest(request);
            User user = securityService.getCurrentUser(request);
            int id = ServletRequestUtils.getRequiredIntParameter(request, "id");

            Share share = shareService.getShareById(id);
            if (share == null) {
                error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
                return;
            }
            if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
                error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to modify shared media.");
                return;
            }

            share.setDescription(request.getParameter("description"));
            String expiresString = request.getParameter("expires");
            if (expiresString != null) {
                long expires = Long.parseLong(expiresString);
                share.setExpires(expires == 0L ? null : new Date(expires));
            }
            shareService.updateShare(share);
            XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
            response.getWriter().print(builder);

        } catch (ServletRequestBindingException x) {
            error(request, response, ErrorCode.MISSING_PARAMETER, getErrorMessage(x));
        } catch (Exception x) {
            LOG.warn("Error in REST API.", x);
            error(request, response, ErrorCode.GENERIC, getErrorMessage(x));
        }
    }

    private List<Attribute> createAttributesForShare(Share share) {
        List<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute("id", share.getId()));
        attributes.add(new Attribute("url", shareService.getShareUrl(share)));
        attributes.add(new Attribute("username", share.getUsername()));
        attributes.add(new Attribute("created", StringUtil.toISO8601(share.getCreated())));
        attributes.add(new Attribute("visitCount", share.getVisitCount()));
        attributes.add(new Attribute("description", share.getDescription()));
        attributes.add(new Attribute("expires", StringUtil.toISO8601(share.getExpires())));
        attributes.add(new Attribute("lastVisited", StringUtil.toISO8601(share.getLastVisited())));

        return attributes;
    }

    public void setRestBrowseController(RESTBrowseController restBrowseController) {
        this.restBrowseController = restBrowseController;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setShareService(ShareService shareService) {
        this.shareService = shareService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

}
