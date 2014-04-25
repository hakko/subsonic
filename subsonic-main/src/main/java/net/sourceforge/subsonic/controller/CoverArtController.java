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
package net.sourceforge.subsonic.controller;

import jahspotify.media.Image;
import jahspotify.media.Link;
import jahspotify.services.MediaHelper;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.LastModified;

import com.github.hakko.musiccabinet.configuration.CharSet;
import com.github.hakko.musiccabinet.dao.util.URIUtil;
import com.github.hakko.musiccabinet.exception.ApplicationException;
import com.github.hakko.musiccabinet.service.library.AudioTagService;
import com.github.hakko.musiccabinet.service.spotify.SpotifyService;

/**
 * Controller which produces cover art images.
 *
 * @author Sindre Mehus
 */
public class CoverArtController implements Controller, LastModified {

    private SecurityService securityService;
    private AudioTagService audioTagService;
	private SpotifyService spotifyService;

    private static final Logger LOG = Logger.getLogger(CoverArtController.class);

    public long getLastModified(HttpServletRequest request) {
    	String encodedPath = request.getParameter("pathUtf8Hex");
        if (StringUtils.trimToNull(encodedPath) != null) {
        	File file = new File(encodedPath);
        	if(!file.exists()) {
        		return -1;
        	}
        	return file.lastModified();
        }
    	
        String path = request.getParameter("path");
        if (StringUtils.trimToNull(path) == null) {
            return 0;
        }

        File file = new File(path);
        if (!file.exists()) {
            return -1;
        }

        return file.lastModified();
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String encodedPath = request.getParameter("pathUtf8Hex");
    	String path = (encodedPath == null || encodedPath.length() == 0) ? null : StringUtil.utf8HexDecode(encodedPath);
    	if(path == null) {
    		path = request.getParameter("path");
    	}
    	File file = null;
        Integer size = ServletRequestUtils.getIntParameter(request, "size");
        
        boolean isSpotify = false;
        if(URIUtil.isSpotify(path)) {
        	isSpotify = true;
        	Link link = Link.create(path);
        	if(link.isImageLink()) {
        		Image image = spotifyService.getSpotify().readImage(link);
        		file = new File(getSpotifyCacheDirectory(), DigestUtils.md5Hex(path) + ".jpg");
        		path = file.getAbsolutePath();
        		// check if the image exists, if not cache it
        		if(!file.exists()) {
        			if(MediaHelper.waitFor(image, 10)) {
       					Files.write(FileSystems.getDefault().getPath(path), image.getBytes());
        			} else {
        				file = null;
        			}
        		}
        	}
        }

        // Check access.
        file = (path == null || path.length() == 0) ? null : new File(path);
        if (file != null && !isSpotify && !securityService.isReadAllowed(file)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        // Optimize if no scaling is required.
        if (size == null) {
            sendUnscaled(file, response);
            return null;
        }

        // Send default image if no path is given. (No need to cache it, since it will be cached in browser.)
        if (file == null) {
            sendDefault(size, response);
            return null;
        }

        // Send cached image, creating it if necessary.
        File cachedImage = getCachedImage(file, size);
        sendImage(cachedImage, response);

        return null;
    }

    private void sendImage(File file, HttpServletResponse response) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void sendDefault(Integer size, HttpServletResponse response) throws IOException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("default_cover.jpg");
            BufferedImage image = ImageIO.read(in);
            image = scale(image, size, size);
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void sendUnscaled(File file, HttpServletResponse response) throws IOException, ApplicationException {
        InputStream in = null;
        try {
            in = getImageInputStream(file);
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private File getCachedImage(File file, int size) throws IOException {
        String md5 = DigestUtils.md5Hex(file.getPath());
        File cachedImage = new File(getImageCacheDirectory(size), md5 + ".jpeg");

        // Is cache missing or obsolete?
        if (!cachedImage.exists() || file.lastModified() > cachedImage.lastModified()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = getImageInputStream(file);
                out = new FileOutputStream(cachedImage);
                BufferedImage image = ImageIO.read(in);
                if (image == null) {
                    throw new Exception("Unable to decode image.");
                }

                image = scale(image, size, size);
                ImageIO.write(image, "jpeg", out);

            } catch (Throwable x) {
                // Delete corrupt (probably empty) thumbnail cache.
                LOG.warn("Failed to create thumbnail for " + file, x);
                IOUtils.closeQuietly(out);
                cachedImage.delete();
                throw new IOException("Failed to create thumbnail for " + file + ". " + x.getMessage());

            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
        return cachedImage;
    }

    /**
     * Returns an input stream to the image in the given file.  If the file is an audio file,
     * the embedded album art is returned.
     * @throws ApplicationException 
     * @throws IOException 
     */
    private InputStream getImageInputStream(File file) throws ApplicationException, IOException {
    	String extension = FilenameUtils.getExtension(file.getName());
    	if (audioTagService.isAudioFile(extension)) {
            return new ByteArrayInputStream(audioTagService.getArtwork(file).getBinaryData());
    	} else {
            return new FileInputStream(file);
        }
    }
    
    private synchronized File getSpotifyCacheDirectory() {
        File dir = new File(SettingsService.getSubsonicHome(), "thumbs");
        dir = new File(dir, "spotify");
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                LOG.info("Created spotify thumbnail cache " + dir);
            } else {
                LOG.error("Failed to create spotify thumbnail cache " + dir);
            }
        }

        return dir;
    }    

    private synchronized File getImageCacheDirectory(int size) {
        File dir = new File(SettingsService.getSubsonicHome(), "thumbs");
        dir = new File(dir, String.valueOf(size));
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                LOG.info("Created thumbnail cache " + dir);
            } else {
                LOG.error("Failed to create thumbnail cache " + dir);
            }
        }

        return dir;
    }

    public static BufferedImage scale(BufferedImage image, int width, int height) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage thumb = image;

        // For optimal results, use step by step bilinear resampling - halfing the size at each step.
        do {
            w /= 2;
            h /= 2;
            if (w < width) {
                w = width;
            }
            if (h < height) {
                h = height;
            }

            BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = temp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
            g2.dispose();

            thumb = temp;
        } while (w != width);

        return thumb;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

	public void setAudioTagService(AudioTagService audioTagService) {
		this.audioTagService = audioTagService;
	}
    
	public void setSpotifyService(SpotifyService spotifyService) {
		this.spotifyService = spotifyService;
	}
	
}
