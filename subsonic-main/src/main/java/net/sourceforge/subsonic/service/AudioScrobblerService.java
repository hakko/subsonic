package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;

import com.github.hakko.musiccabinet.domain.model.library.MetaData;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.ScrobbleService;

public class AudioScrobblerService {

	private ScrobbleService scrobbleService;
	private SettingsService settingsService;
	
	private static final Logger LOG = Logger.getLogger(AudioScrobblerService.class);
	
	// TODO : check uppercase/lowercase behavior
	public void scrobble(String username, MediaFile mediaFile, boolean submission) {
		
		String lastFmUsername = settingsService.getLastFmUsername(username);

		if (mediaFile.isVideo()) {
			LOG.debug("Not scrobbling video files.");
		} else if (lastFmUsername != null) {
			try {
				MetaData metaData = new MetaData();
				metaData.setArtist(mediaFile.getMetaData().getArtist());
				metaData.setArtistUri(mediaFile.getMetaData().getArtistUri());
				metaData.setAlbum(mediaFile.getMetaData().getAlbum());
				metaData.setAlbumUri(mediaFile.getMetaData().getAlbumUri());
				metaData.setDuration((short) mediaFile.getMetaData().getDuration().intValue());
				Track track = new Track(mediaFile.getId(), mediaFile.getTitle(), metaData);

				scrobbleService.scrobble(lastFmUsername, track, submission);
			} catch (Throwable t) {
				LOG.debug("Scrobbling failed!", t);
			}
		} else {
			LOG.debug("Last.fm not enabled. Not scrobbling for " + username + ".");
		}
	}

	// Spring setters
	
	public void setScrobbleService(ScrobbleService scrobbleService) {
		this.scrobbleService = scrobbleService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}
	
}