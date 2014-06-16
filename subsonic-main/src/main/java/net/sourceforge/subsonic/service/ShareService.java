/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.ShareDao;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Share;
import net.sourceforge.subsonic.domain.User;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.github.hakko.musiccabinet.configuration.Uri;

/**
 * Provides services for sharing media.
 *
 * @author Sindre Mehus
 * @see Share
 */
public class ShareService {

    private static final Logger LOG = Logger.getLogger(ShareService.class);

    private ShareDao shareDao;
    private SecurityService securityService;
    private SettingsService settingsService;
    private MediaFileService mediaFileService;

    public List<Share> getAllShares() {
        return shareDao.getAllShares();
    }

    public List<Share> getSharesForUser(User user) {
        List<Share> result = new ArrayList<Share>();
        for (Share share : getAllShares()) {
            if (user.isAdminRole() || ObjectUtils.equals(user.getUsername(), share.getUsername())) {
                result.add(share);
            }
        }
        return result;
    }

    public Share getShareById(int id) {
        return shareDao.getShareById(id);
    }

    public List<MediaFile> getSharedFiles(int id) {
        List<Uri> mediaFileIds = shareDao.getSharedFiles(id);
        mediaFileService.loadMediaFiles(mediaFileIds);
        return mediaFileService.getMediaFiles(mediaFileIds);
    }


    public Share createShare(HttpServletRequest request, List<MediaFile> files) throws Exception {

        Share share = new Share();
        share.setName(RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        share.setCreated(new Date());
        share.setUsername(securityService.getCurrentUsername(request));

        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.YEAR, 1);
        share.setExpires(expires.getTime());

        shareDao.createShare(share);
        for (MediaFile file : files) {
            shareDao.createSharedFiles(share.getId(), file.getUri().getId());
        }
        LOG.info("Created share '" + share.getName() + "' with " + files.size() + " file(s).");

        return share;
    }

    public void updateShare(Share share) {
        shareDao.updateShare(share);
    }

    public void deleteShare(int id) {
        shareDao.deleteShare(id);
    }

    public String getShareBaseUrl() {
    	String host;
    	if (StringUtils.isNotEmpty(settingsService.getShareUrlPrefix())) {
    		host = StringUtils.removeEnd(settingsService.getShareUrlPrefix(), "/");
    	} else if (settingsService.isUrlRedirectionEnabled()) {
    		host = "http://" + settingsService.getUrlRedirectFrom() + ".subsonic.org";
    	} else {
    		host = "http://" + getCurrentAddress() + ":" + settingsService.getPort();
    	}
        return host + "/share/";
    }
    
    private String getCurrentAddress() {
    	String host = "localhost";
    	try {
    		InetAddress localHost = InetAddress.getLocalHost();
    		host = localHost.getCanonicalHostName();
    	} catch (UnknownHostException e) {
    		LOG.warn("No host name found, are you connected to the internet?", e);
    	}
    	return host;
    }

    public String getShareUrl(Share share) {
        return getShareBaseUrl() + share.getName();
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setShareDao(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setmediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

}