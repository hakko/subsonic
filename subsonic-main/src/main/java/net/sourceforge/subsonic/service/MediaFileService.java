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
package net.sourceforge.subsonic.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Album;
import net.sourceforge.subsonic.domain.MediaFile;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.domain.model.library.MetaData;
import com.github.hakko.musiccabinet.domain.model.music.Track;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;

/**
 * Provides services for instantiating and caching music files and cover art.
 *
 * @author Sindre Mehus
 */
public class MediaFileService {

    private Ehcache mediaFileCache;
    private Ehcache coverArtCache;

    private SettingsService settingsService;
    private SecurityService securityService;
    private LibraryBrowserService libraryBrowserService;
    
    private static final Logger LOG = Logger.getLogger(MediaFileService.class);
    
    public MediaFile getMediaFile(Uri trackUri) {
		if (!mediaFileCache.isElementInMemory(trackUri)) {
			LOG.debug("media file not in memory, load meta data from db!");
			loadMediaFiles(Arrays.asList(trackUri));
			if (!mediaFileCache.isElementInMemory(trackUri)) {
				// trackId might refer to a playing track/video that since have been removed
				return null;
			}
		}
		return (MediaFile) mediaFileCache.get(trackUri).getValue();
    }

    public List<MediaFile> getMediaFiles(List<? extends Uri> trackUris) {
    	List<MediaFile> mediaFiles = new ArrayList<>();
    	for (Uri trackUri : trackUris) {
    		MediaFile mediaFile = getMediaFile(trackUri);
    		if (mediaFile != null) {
    			mediaFiles.add(mediaFile);
    		}
    	}
    	return mediaFiles;
    }

    public void loadMediaFiles(List<? extends Uri> mediaFileIds) {
    	List<Uri> missingMediaFileUris = new ArrayList<>(mediaFileIds);
    	for (Iterator<Uri> it = missingMediaFileUris.iterator(); it.hasNext();) {
    		if (mediaFileCache.isElementInMemory(it.next())) {
    			it.remove();
    		}
    	}
    	if (missingMediaFileUris.size() > 0) {
    		List<Track> tracks = libraryBrowserService.getTracks(missingMediaFileUris);
        	for (Track track : tracks) {
        		mediaFileCache.put(new Element(track.getUri(), getMediaFile(track)));
        	}
    	}
    }
    
    public static MediaFile getMediaFile(Track track) {
    	MediaFile mediaFile = new MediaFile(track.getUri());
    	MetaData md = track.getMetaData();
    	mediaFile.getMetaData();
    	if(md.getPath() != null) {
    		mediaFile.setFile(new File(md.getPath()));
    	}
    	mediaFile.getMetaData().setAlbum(md.getAlbum());
    	mediaFile.getMetaData().setAlbumUri(md.getAlbumUri());
    	mediaFile.getMetaData().setArtist(md.getArtist());
    	mediaFile.getMetaData().setArtistUri(md.getArtistUri());
    	mediaFile.getMetaData().setAlbumArtist(md.getAlbumArtist());
    	mediaFile.getMetaData().setComposer(md.getComposer());
    	mediaFile.getMetaData().setBitRate((int) md.getBitrate());
    	mediaFile.getMetaData().setDiscNumber((int) md.getDiscNr());
    	mediaFile.getMetaData().setDuration((int) md.getDuration());
    	mediaFile.getMetaData().setFileSize((long) md.getSize());
    	if(md.getMediaType() != null) {
    		mediaFile.getMetaData().setFormat(md.getMediaType().getFilesuffix().toLowerCase());
    	}
    	mediaFile.getMetaData().setGenre(md.getGenre());
    	mediaFile.getMetaData().setTitle(track.getName());
    	mediaFile.getMetaData().setTrackNumber((int) md.getTrackNr());
    	mediaFile.getMetaData().setVariableBitRate(md.isVbr());
    	mediaFile.getMetaData().setYear(toYear(md.getYear()));
    	mediaFile.getMetaData().setHasLyrics(md.hasLyrics());
    	return mediaFile;
    }
    
    private static String toYear(int year) {
    	return year == 0 ? null : "" + year;
    }
    
    public File getCoverArt(MediaFile mediaFile) throws IOException {
        String coverArtFile = null;
    	Element element = coverArtCache.get(mediaFile.getUri());
        if (element == null) {
        	coverArtFile = libraryBrowserService.getCoverArtFileForTrack(mediaFile.getUri());
        	coverArtCache.put(new Element(mediaFile.getUri(), coverArtFile));
        } else {
        	coverArtFile = (String) element.getObjectValue();
        }
    	return coverArtFile == null ? null : new File(coverArtFile);
    }

    public List<Album> getAlbums(List<com.github.hakko.musiccabinet.domain.model.music.Album> alb) {
    	return getAlbums(alb, false);
    }

    public List<Album> getAlbums(List<com.github.hakko.musiccabinet.domain.model.music.Album> alb, boolean onlyLocalArtwork) {
    	List<Album> albums = new ArrayList<>();
        List<Uri> trackIds = new ArrayList<>();

        boolean preferLastFmArtwork = settingsService.isPreferLastFmArtwork();
    	for (com.github.hakko.musiccabinet.domain.model.music.Album a : alb) {
        	Album album = new Album();
        	album.setArtistUri(a.getArtist().getUri());
        	album.setArtistName(a.getArtist().getName());
        	album.setUri(a.getUri());
        	album.setTitle(a.getName());
        	album.setYear(a.getYear());
        	album.setCoverArtPath(a.getCoverArtPath());
        	album.setCoverArtUrl(a.getCoverArtURL());
        	album.setTrackIds(a.getTrackUris());
        	if (album.getCoverArtPath() != null && album.getCoverArtUrl() != null) {
        		if (preferLastFmArtwork && !onlyLocalArtwork) {
        			album.setCoverArtPath(null);
        		} else {
        			album.setCoverArtUrl(null);
        		}
        	}
        	trackIds.addAll(a.getTrackUris());
        	albums.add(album);
        }
        loadMediaFiles(trackIds);
        return albums;
    }

    /*
     * Inefficient MediaFile instantiation. Only to be used when we don't have an id,
     * like when loading files from a saved playlist.
     */
    public MediaFile getMediaFile(String absolutePath) {
    	Uri trackId = libraryBrowserService.getTrackUri(absolutePath);
    	return getMediaFile(trackId);
    }

    /*
     * Instantiate MediaFile by path name. Only to be used by file based browser.
     */
    public MediaFile getNonIndexedMediaFile(String pathName) {
        return getNonIndexedMediaFile(new File(pathName));
    }
    
    /*
     * Instantiate MediaFile by path name. Only to be used by file based browser.
     */
    public MediaFile getNonIndexedMediaFile(File file) {
    	
    	int fileId = -Math.abs(file.getAbsolutePath().hashCode());

    	LOG.debug("request for non indexed media file " + file.getAbsolutePath() + ", cache as " + fileId);
    	
    	Element element = mediaFileCache.get(fileId);
        if (element != null) {

            // Check if cache is up-to-date.
            MediaFile cachedMediaFile = (MediaFile) element.getObjectValue();
            if (cachedMediaFile.lastModified() >= file.lastModified()) {
                return cachedMediaFile;
            }
        }

        if (!securityService.isReadAllowed(file)) {
            throw new SecurityException("Access denied to file " + file);
        }

        MediaFile mediaFile = new MediaFile(fileId, file);
        mediaFileCache.put(new Element(fileId, mediaFile));
        
        return mediaFile;
    }
   
    /**
     * Register in service locator so that non-Spring objects can access me.
     * This method is invoked automatically by Spring.
     */
    public void init() {
        ServiceLocator.setMediaFileService(this);
    }

    public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

	public void setMediaFileCache(Ehcache mediaFileCache) {
        this.mediaFileCache = mediaFileCache;
    	mediaFileCache.removeAll(); // TODO : remove in version 0.8 or so, just clearing traces from 4.6 
   }

    public void setChildDirCache(Ehcache childDirCache) {
    	childDirCache.removeAll(); // TODO : remove in version 0.8 or so, just clearing traces from 4.6
    }

    public void setCoverArtCache(Ehcache coverArtCache) {
        this.coverArtCache = coverArtCache;
    }

}