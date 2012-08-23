package net.sourceforge.subsonic.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.command.TagSettingsCommand;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.github.hakko.musiccabinet.domain.model.aggr.TagOccurrence;
import com.github.hakko.musiccabinet.service.TagService;

/**
 * Controller for tag configuration.
 *
 * @author hakko / MusicCabinet
 */
public class TagSettingsController extends SimpleFormController {

    private TagService tagService;
    private SettingsService settingsService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        TagSettingsCommand command = new TagSettingsCommand();
        
        command.setAvailableTags(tagService.getAvailableTags());
        
        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        TagSettingsCommand command = (TagSettingsCommand) comm;
        
        Map<String, String> tagCorrections = new HashMap<>();
        List<String> topTags = new ArrayList<>();
        for (TagOccurrence to : command.getAvailableTags()) {
        	tagCorrections.put(to.getTag(), to.getCorrectedTag().toLowerCase());
        	if (to.isUse() && StringUtils.trimToNull(to.getCorrectedTag()) == null) {
        		topTags.add(to.getTag());
        	}
        }
        
        tagService.createTagCorrections(tagCorrections);
        tagService.setTopTags(topTags);
        settingsService.setSettingsChanged(); // forces a cache timeout for left.view
    }
    
    // Spring setters

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

}