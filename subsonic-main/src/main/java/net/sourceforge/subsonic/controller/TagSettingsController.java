package net.sourceforge.subsonic.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.command.TagSettingsCommand;
import net.sourceforge.subsonic.service.SettingsService;

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
        Set<String> topTags = new HashSet<>();
        for (TagOccurrence to : command.getAvailableTags()) {
        	if (to.isUse() && to.getCorrectedTag().isEmpty()) {
        		topTags.add(to.getTag());
        	}
        	if (!to.getCorrectedTag().isEmpty()) {
            	tagCorrections.put(to.getTag(), to.getCorrectedTag().toLowerCase());
        		topTags.add(to.getCorrectedTag());
        	}
        }
        
        tagService.createTagCorrections(tagCorrections);
        tagService.setTopTags(new ArrayList<>(topTags));
        settingsService.setSettingsChanged(); // forces a cache timeout for left.view
        
        command.setAvailableTags(tagService.getAvailableTags());
    }
    
    // Spring setters

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

}