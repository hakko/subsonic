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
package net.sourceforge.subsonic.util;

import net.sourceforge.subsonic.Logger;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Inet4Address;
import java.util.Enumeration;
import java.util.List;

/**
 * Miscellaneous general utility methods.
 *
 * @author Sindre Mehus
 */
public final class Util {

    private static final Logger LOG = Logger.getLogger(Util.class);

    /**
     * Disallow external instantiation.
     */
    private Util() {
    }

    public static String getDefaultMediaFolder() {
        String def = isWindows() ? "c:\\music" : "/var/music";
        return System.getProperty("subsonic.defaultMediaFolder", def);
    }

    public static String getDefaultPodcastFolder() {
        String def = isWindows() ? "c:\\music\\Podcast" : "/var/music/Podcast";
        return System.getProperty("subsonic.defaultPodcastFolder", def);
    }

    public static String getDefaultPlaylistFolder() {
        String def = isWindows() ? "c:\\playlists" : "/var/playlists";
        return System.getProperty("subsonic.defaultPlaylistFolder", def);
    }

    public static boolean isWindows() {
        return System.getProperty("os.name", "Windows").toLowerCase().startsWith("windows");
    }

    public static boolean isWindowsInstall() {
        return "true".equals(System.getProperty("subsonic.windowsInstall"));
    }

    /**
     * Similar to {@link ServletResponse#setContentLength(int)}, but this
     * method supports lengths bigger than 2GB.
     * <p/>
     * See http://blogger.ziesemer.com/2008/03/suns-version-of-640k-2gb.html
     *
     * @param response The HTTP response.
     * @param length   The content length.
     */
    public static void setContentLength(HttpServletResponse response, long length) {
    	
   	
    	if(length <= 0) {
    		return;
    	}
    	
        if (length <= Integer.MAX_VALUE) {
            response.setContentLength((int) length);
        } else {
            response.setHeader("Content-Length", String.valueOf(length));
        }
    }

    /**
     * Returns the local IP address.
     * @return The local IP, or the loopback address (127.0.0.1) if not found.
     */
    public static String getLocalIpAddress() {
        try {

            // Try the simple way first.
            InetAddress address = InetAddress.getLocalHost();
            if (!address.isLoopbackAddress()) {
                return address.getHostAddress();
            }

            // Iterate through all network interfaces, looking for a suitable IP.
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }

        } catch (Throwable x) {
            LOG.warn("Failed to resolve local IP address.", x);
        }

        return "127.0.0.1";
    }
    
    public static String square(String imageUrl) {
    	return StringUtils.replaceOnce(imageUrl, "/126/", "/126s/");
    }
    
    /*
     * Changes the artist image urls to square size.
     */
    public static List<ArtistRecommendation> square(List<ArtistRecommendation> artistRecommendations) {
		for (ArtistRecommendation ar : artistRecommendations) {
			ar.setImageUrl(square(ar.getImageUrl()));
		}
		return artistRecommendations;
    }
    
    /*
     * Changes the artist image url to square size.
     */
    public static ArtistInfo square(ArtistInfo artistInfo) {
    	artistInfo.setLargeImageUrl(square(artistInfo.getLargeImageUrl()));
    	return artistInfo;
    }
}