package net.sourceforge.subsonic.command;

import java.util.List;

import com.github.hakko.musiccabinet.domain.model.aggr.TagOccurrence;

import net.sourceforge.subsonic.controller.TagSettingsController;

/**
 * Command used in {@link TagSettingsController}.
 *
 * @author hakko / MusicCabinet
 */
public class TagSettingsCommand {

	private List<TagOccurrence> availableTags;
	private List<String> topTags;

	public List<TagOccurrence> getAvailableTags() {
		return availableTags;
	}

	public void setAvailableTags(List<TagOccurrence> availableTags) {
		this.availableTags = availableTags;
	}

	public List<String> getTopTags() {
		return topTags;
	}

	public void setTopTags(List<String> topTags) {
		this.topTags = topTags;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Available tags: " + availableTags);
		sb.append("Top tags: " + topTags);
		return sb.toString();
	}
}