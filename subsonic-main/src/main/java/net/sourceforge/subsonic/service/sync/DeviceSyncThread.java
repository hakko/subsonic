package net.sourceforge.subsonic.service.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.UserSettings;

import org.apache.commons.io.FileUtils;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.domain.model.aggr.ArtistRecommendation;
import com.github.hakko.musiccabinet.domain.model.music.Album;
import com.github.hakko.musiccabinet.domain.model.music.Artist;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;

public class DeviceSyncThread implements Runnable {
	private static final Logger LOG = Logger.getLogger(DeviceSyncThread.class);
	private final DeviceListenerService deviceListenerService;

	public DeviceSyncThread(DeviceListenerService deviceListenerService) {
		this.deviceListenerService = deviceListenerService;
		new Thread(this).start();
	}

	public void run() {

		while (true) {
			try {
				List<String> serialNumbers = deviceListenerService
						.getSerialNumbers();
				for (String serial : serialNumbers) {
					LOG.debug("Processing sync for " + serial + "\n");
					UserSettings userSettings = deviceListenerService
							.getUserSettings(serial);
					if(userSettings.getDeviceLastSync().getTime() > System.currentTimeMillis() + (1000 * 60 * 60 * 24)) {
						LOG.debug("Skipping sync for " + serial + "\n");
						continue;
					}
					
					String mountPath = userSettings.getDeviceMountPath();
					File mountDirectory = new File(mountPath);
					if (mountDirectory.exists() && mountDirectory.isDirectory()
							&& mountDirectory.canWrite()) {
						LOG.debug("Can write to " + mountPath);
						processSync(mountDirectory, userSettings);
					} else {
						LOG.debug("Can't write to "
								+ mountDirectory.getAbsolutePath());
						LOG.debug("Can't exists to " + mountDirectory.exists());
						LOG.debug("Can't directory to "
								+ mountDirectory.isDirectory());
						LOG.debug("Can't write to " + mountDirectory.canWrite());
					}
					userSettings.setDeviceLastSync(new Date());
					deviceListenerService.getSettingsService().updateUserSettings(userSettings);
					LOG.debug("Done with sync for " + serial + "\n");
				}
				
			} catch (Throwable e) {
				LOG.warn("Error when syncing: " + e.getMessage(), e);
			}
			// we probably only need to check every 5 seconds
			try {
				LOG.debug("Sleeping");
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	private void processSync(File mountDirectory, UserSettings userSettings)
			throws Exception {
		long size = userSettings.getDeviceSyncSize();
		size *= 1048576;
		LOG.debug("Looking for " + size + " bytes of music.");

		LibraryBrowserService libraryBrowserService = deviceListenerService
				.getLibraryBrowserService();

		List<Uri> tracks = new ArrayList<Uri>();
		List<Album> starredAlbums = libraryBrowserService.getStarredAlbums(
				userSettings.getLastFmUsername(), 0, 1000000, null);
		for (Album album : starredAlbums) {
			tracks.addAll(album.getTrackUris());
		}

		List<ArtistRecommendation> starredArtists = libraryBrowserService
				.getStarredArtists(userSettings.getLastFmUsername(), 0,
						1000000, null);
		for (ArtistRecommendation artist : starredArtists) {
			new ArrayList<Album>();
			List<Album> albums = libraryBrowserService.getAlbums(
					new Artist(artist.getArtistUri(), artist.getArtistName()), false);
			for (Album album : albums) {
				tracks.addAll(album.getTrackUris());
			}
		}
		
		List<Album> mostPlayedAlumbs = libraryBrowserService
				.getMostPlayedAlbums(userSettings.getLastFmUsername(), 0,
						1000000, null);
		for (Album album : mostPlayedAlumbs) {
			tracks.addAll(album.getTrackUris());
		}

		List<Track> starredTracks = libraryBrowserService
				.getTracks(libraryBrowserService.getStarredTrackUris(
						userSettings.getLastFmUsername(), 0, 1000000, null));
		for (Track track : starredTracks) {
			// this should probably have an exception for various artists
			Album album = libraryBrowserService.getAlbum(track.getMetaData()
					.getAlbumUri());
			tracks.addAll(album.getTrackUris());
		}


		List<Album> recentlyAdded = libraryBrowserService
				.getRecentlyAddedAlbums(0, 1000000, null);
		for (Album album : recentlyAdded) {
			tracks.addAll(album.getTrackUris());
		}

		List<String> alreadyMade = new ArrayList<String>();
		List<String> toSkip = new ArrayList<String>();
		Map<String, File> toCopy = new HashMap<String, File>();
		long batches = (tracks.size() / 100);
		for (int i = 0; i <= batches; i++) {
			// stop attempting to copy batches at 5MB
			if (size < 5000000) {
				break;
			}

			List<? extends Uri> subList = tracks.subList(i * 100,
					Math.min((i + 1) * 100, tracks.size()));
			List<Track> subTracks = libraryBrowserService.getTracks(subList);
			for (Track track : subTracks) {
				File mp3File = new File(track.getMetaData().getPath());
				if (mp3File.exists()) {

					if (size - mp3File.length() > 0) {
						String artist = track.getMetaData().getAlbumArtist();
						if(artist == null) {
							artist = track.getMetaData().getArtist();
						}
						// copy it
						String path = artist
								+ File.separator
								+ track.getMetaData().getAlbum();
						File toDirectory = new File(
								userSettings.getDeviceMountPath(), path);
						LOG.debug("Looking at making "
								+ toDirectory.getAbsolutePath());
						if (alreadyMade.contains(toDirectory.getAbsolutePath())
								|| toDirectory.exists() || toDirectory.mkdirs()) {
							alreadyMade.add(toDirectory.getAbsolutePath());

							File newFile = new File(toDirectory,
									mp3File.getName());
							if (!newFile.exists()) {
								LOG.debug("Adding missing file "
										+ newFile.getAbsolutePath());
								toCopy.put(track.getMetaData().getPath(),
										newFile);
								size -= mp3File.length();
							} else if (mp3File.length() != newFile.length()) {
								LOG.debug("Adding different sized file "
										+ newFile.getAbsolutePath());
								toCopy.put(track.getMetaData().getPath(),
										newFile);
								size -= mp3File.length();
							} else {
								toSkip.add(newFile.getAbsolutePath());
							}
						} else {
							LOG.error("Unable to create directory "
									+ toDirectory.getAbsolutePath());
						}
					}
				} else {
					LOG.error("Cannot find mp3file "
							+ mp3File.getAbsolutePath());
				}
			}
		}

		List<File> toDelete = new ArrayList<File>();
		Collection<File> existingFiles = FileUtils.listFiles(new File(
				userSettings.getDeviceMountPath()), deviceListenerService
				.getSettingsService().getMusicFileTypesAsArray(), true);

		for (File existing : existingFiles) {
			if (!toSkip.contains(existing.getAbsolutePath())) {
				toDelete.add(existing);
			}
		}

		for (File delete : toDelete) {
			LOG.debug("Deleting " + delete);
			delete.delete();
		}

		for (Entry<String, File> copy : toCopy.entrySet()) {
			LOG.debug("Copying " + copy.getKey());
			FileUtils.copyFile(new File(copy.getKey()), copy.getValue());
		}
	}
}
