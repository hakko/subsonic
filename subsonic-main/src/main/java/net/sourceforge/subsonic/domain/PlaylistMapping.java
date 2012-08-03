package net.sourceforge.subsonic.domain;

import java.util.List;

public class PlaylistMapping {

	private final String name;
	private final List<Integer> mediaFileIds;

	public PlaylistMapping(String name, List<Integer> mediaFileIds) {
		this.name = name;
		this.mediaFileIds = mediaFileIds;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getMediaFileIds() {
		return mediaFileIds;
	}

}
