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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.domain.model.library.LastFmGroup;
import com.github.hakko.musiccabinet.service.LastFmService;
import com.github.hakko.musiccabinet.service.lastfm.GroupWeeklyArtistChartService;

/**
 * Controller for the page used to administrate last.fm group subscriptions.
 */
public class GroupSettingsController extends ParameterizableViewController {

    private LastFmService lastFmService;
    private GroupWeeklyArtistChartService artistChartService;
    
	private static final Logger LOG = Logger.getLogger(GroupSettingsController.class);

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        
        String[] groups = request.getParameterValues("group");
        if (groups != null) {
        	updateGroups(groups);
        }
        
        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("lastFmGroups", lastFmService.getLastFmGroups());

        result.addObject("model", map);
        return result;

    }

    /*
     * Store selected groups, and asynchronously fetch group artists from last.fm.
     */
    private void updateGroups(String[] groups) {
    	List<LastFmGroup> lastFmGroups = new ArrayList<>();
    	for (String group : groups) {
    		if (StringUtils.trimToNull(group) != null) {
    			lastFmGroups.add(new LastFmGroup(group));
    		}
    	}
		lastFmService.setLastFmGroups(lastFmGroups);

		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					LOG.debug("Update artist chart.");
					artistChartService.updateSearchIndex();
					LOG.debug("Artist chart updated.");
				} catch (Throwable t) {
					LOG.warn("Couldn't update group artists!", t);
				}
			}
		});
    }
    
	public void setLastFmService(LastFmService lastFmService) {
		this.lastFmService = lastFmService;
	}

    public void setGroupWeeklyArtistChartService(GroupWeeklyArtistChartService artistChartService) {
		this.artistChartService = artistChartService;
	}

}