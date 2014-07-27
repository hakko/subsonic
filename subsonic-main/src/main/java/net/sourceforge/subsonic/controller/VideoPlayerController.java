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

import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;

/**
 * Controller for the page used to play videos.
 *
 * @author Sindre Mehus
 */
public class VideoPlayerController extends ParameterizableViewController {

    public static final int DEFAULT_BIT_RATE = 1000;
    public static final int[] BIT_RATES = {200, 300, 400, 500, 700, 1000, 1200, 1500, 2000, 3000, 5000};

    private static final Logger LOG = Logger.getLogger(VideoPlayerController.class);
    
    private MediaFileService mediaFileService;
    private SettingsService settingsService;
    private PlayerService playerService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    	LOG.debug("got request " + request.getParameter("id"));
    	
        Map<String, Object> map = new HashMap<String, Object>();
        Uri mediaFileUri = URIUtil.parseURI(removeEnd(removeStart(request.getParameter("id"), "["), "]"));
        
        LOG.debug("ask for id " + mediaFileUri);
        
        MediaFile file = mediaFileService.getMediaFile(mediaFileUri);

        int timeOffset = ServletRequestUtils.getIntParameter(request, "timeOffset", 0);
        timeOffset = Math.max(0, timeOffset);
        Short duration = file.getMetaData().getDuration();
        short durationSeconds = 0;
        if (duration != null) {
        	durationSeconds = duration.shortValue();
            map.put("skipOffsets", createSkipOffsets(duration));
            timeOffset = Math.min(duration, timeOffset);
            durationSeconds -= timeOffset;
        }

        map.put("video", file);
        map.put("player", playerService.getPlayer(request, response).getId());
        map.put("maxBitRate", ServletRequestUtils.getIntParameter(request, "maxBitRate", DEFAULT_BIT_RATE));
        map.put("popout", ServletRequestUtils.getBooleanParameter(request, "popout", false));
        map.put("duration", durationSeconds);
        map.put("timeOffset", timeOffset);
        map.put("bitRates", BIT_RATES);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public static Map<String, Integer> createSkipOffsets(int durationSeconds) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < durationSeconds; i += 60) {
            result.put(StringUtil.formatDuration(i), i);
        }
        return result;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
