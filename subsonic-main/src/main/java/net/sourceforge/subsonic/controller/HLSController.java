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

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.util.StringUtil;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;

/**
 * Controller which produces the HLS (Http Live Streaming) playlist.
 *
 * @author Sindre Mehus
 */
public class HLSController implements Controller {

    private static final int SEGMENT_DURATION = 10;

    private PlayerService playerService;
    private MediaFileService mediaFileService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Uri uri = URIUtil.parseURI(ServletRequestUtils.getStringParameter(request, "id"));
        MediaFile mediaFile = mediaFileService.getMediaFile(uri);
        if (mediaFile == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Media file not found: " + uri);
            return null;
        }
        Short duration = mediaFile.getMetaData().getDuration();
        if (duration == null || duration == 0) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown duration for media file: " + uri);
            return null;
        }

        Player player = playerService.getPlayer(request, response);
        response.setContentType("application/vnd.apple.mpegurl");
        response.setCharacterEncoding(StringUtil.ENCODING_UTF8);
        int[] bitRates = ServletRequestUtils.getIntParameters(request, "bitRate");

        PrintWriter writer = response.getWriter();
        if (bitRates.length > 1) {
            generateVariantPlaylist(uri, player, bitRates, writer);
        } else {
            generateNormalPlaylist(uri, player, bitRates.length == 1 ? bitRates[0] : null, duration, writer);
        }

        return null;
    }

    private void generateVariantPlaylist(Uri uri, Player player, int[] bitRatesKbps, PrintWriter writer) {
        writer.println("#EXTM3U");
        writer.println("#EXT-X-VERSION:1");
//        writer.println("#EXT-X-TARGETDURATION:" + SEGMENT_DURATION);

        for (int bitRateKbps : bitRatesKbps) {
            writer.println("#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=" + bitRateKbps * 1000L);
            writer.println("/hls/hls.m3u8?id=" + uri + "&player=" + player.getId() + "&bitRate=" + bitRateKbps);
        }
//        writer.println("#EXT-X-ENDLIST");
    }

    private void generateNormalPlaylist(Uri uri, Player player, Integer bitRate, int totalDuration, PrintWriter writer) {
        writer.println("#EXTM3U");
        writer.println("#EXT-X-VERSION:1");
        writer.println("#EXT-X-TARGETDURATION:" + SEGMENT_DURATION);

        for (int i = 0; i < totalDuration / SEGMENT_DURATION; i++) {
            int offset = i * SEGMENT_DURATION;
            writer.println("#EXTINF:" + SEGMENT_DURATION + ",");
            writer.println(createStreamUrl(player, uri, offset, SEGMENT_DURATION, bitRate));
        }

        int remainder = totalDuration % SEGMENT_DURATION;
        if (remainder > 0) {
            writer.println("#EXTINF:" + remainder + ",");
            int offset = totalDuration - remainder;
            writer.println(createStreamUrl(player, uri, offset, remainder, bitRate));
        }
        writer.println("#EXT-X-ENDLIST");
    }

    private String createStreamUrl(Player player, Uri uri, int offset, int duration, Integer maxBitRate) {
        StringBuilder builder = new StringBuilder();
        builder.append("/stream/stream.ts?id=").append(uri).append("&hls=true&timeOffset=").append(offset).append("&player=")
                .append(player.getId()).append("&duration=").append(duration);
        if (maxBitRate != null) {
            builder.append("&maxBitRate=").append(maxBitRate);
        }
        return builder.toString();
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
