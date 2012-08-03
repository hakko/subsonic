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
package net.sourceforge.subsonic.ajax;

import static java.util.Arrays.asList;
import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import com.github.hakko.musiccabinet.exception.ApplicationException;
import com.github.hakko.musiccabinet.service.LibraryUpdateService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;

/**
 * Provides AJAX-enabled services for changing cover art images.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class CoverArtService {

    private static final Logger LOG = Logger.getLogger(CoverArtService.class);

    private SecurityService securityService;
    private MediaFileService mediaFileService;
    private LibraryUpdateService libraryUpdateService;

    /**
     * Downloads and saves the cover art at the given URL.
     *
     * @param path The directory in which to save the image.
     * @param url  The image URL.
     * @return The error string if something goes wrong, <code>null</code> otherwise.
     */
    public String setCoverArtImage(int mediaFileId, String url) {
        try {
            saveCoverArt(mediaFileId, url);
            return null;
        } catch (Exception x) {
            LOG.warn("Failed to save cover art for " + mediaFileId, x);
            return x.toString();
        }
    }

    private void saveCoverArt(int mediaFileId, String url) throws Exception {
        InputStream input = null;
        HttpClient client = new DefaultHttpClient();

        MediaFile mediaFile = mediaFileService.getMediaFile(mediaFileId);
        String path = mediaFile.getFile().getParent();
        
        try {
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 20 * 1000); // 20 seconds
            HttpConnectionParams.setSoTimeout(client.getParams(), 20 * 1000); // 20 seconds
            HttpGet method = new HttpGet(url);

            HttpResponse response = client.execute(method);
            input = response.getEntity().getContent();

            // Attempt to resolve proper suffix.
            String suffix = "jpg";
            if (url.toLowerCase().endsWith(".gif")) {
                suffix = "gif";
            } else if (url.toLowerCase().endsWith(".png")) {
                suffix = "png";
            }

            // Check permissions.
            File newCoverFile = new File(path, "folder." + suffix);
            if (!securityService.isWriteAllowed(newCoverFile)) {
                throw new Exception("Permission denied: " + StringUtil.toHtml(newCoverFile.getPath()));
            }

            // If file exists, create a backup.
            backup(newCoverFile, new File(path, "folder.backup." + suffix));

            // Write file.
            IOUtils.copy(input, new FileOutputStream(newCoverFile));

            LOG.debug("Initialize scan of " + path);
        	try {
    			libraryUpdateService.createSearchIndex(new HashSet<>(asList(path)), true, false);
    		} catch (ApplicationException e) {
    			LOG.warn("Could not complete scan after updating tags!", e);
    		}


        } finally {
            IOUtils.closeQuietly(input);
            client.getConnectionManager().shutdown();
        }
    }

    private void backup(File newCoverFile, File backup) {
        if (newCoverFile.exists()) {
            if (backup.exists()) {
                backup.delete();
            }
            if (newCoverFile.renameTo(backup)) {
                LOG.info("Backed up old image file to " + backup);
            } else {
                LOG.warn("Failed to create image file backup " + backup);
            }
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setmediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

	public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}
    
}