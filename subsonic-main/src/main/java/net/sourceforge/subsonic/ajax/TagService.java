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
package net.sourceforge.subsonic.ajax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.service.metadata.MetaDataParser;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MetaData;
import net.sourceforge.subsonic.service.metadata.MetaDataParserFactory;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.github.hakko.musiccabinet.service.LibraryUpdateService;

/**
 * Provides AJAX-enabled services for editing tags in music files.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class TagService {

    private static final Logger LOG = Logger.getLogger(TagService.class);

    private MediaFileService mediaFileService;
    private MetaDataParserFactory metaDataParserFactory;
    private LibraryUpdateService libraryUpdateService;

    private List<Integer> updatedMusicFileIds = new ArrayList<>();
    
    /**
     * Updated tags for a given music file.
     *
     * @param path   The path of the music file.
     * @param track  The track number.
     * @param artist The artist name.
     * @param album  The album name.
     * @param title  The song title.
     * @param year   The release year.
     * @param genre   The musical genre.
     * @return "UPDATED" if the new tags were updated, "SKIPPED" if no update was necessary.
     *         Otherwise the error message is returned.
     */
    public String setTags(int id, String track, String artist, String albumArtist, String composer, String album, String title, String year, String genre) {

        track = StringUtils.trimToNull(track);
        artist = StringUtils.trimToNull(artist);
        albumArtist = StringUtils.trimToNull(albumArtist);
        composer = StringUtils.trimToNull(composer);
        album = StringUtils.trimToNull(album);
        title = StringUtils.trimToNull(title);
        year = StringUtils.trimToNull(year);
        genre = StringUtils.trimToNull(genre);

        Integer trackNumber = null;
        if (track != null) {
            try {
                trackNumber = new Integer(track);
            } catch (NumberFormatException x) {
                LOG.warn("Illegal track number: " + track, x);
            }
        }

        try {

            MediaFile file = mediaFileService.getMediaFile(id);
            MetaDataParser parser = metaDataParserFactory.getParser(file);

            if (!parser.isEditingSupported()) {
                return "Tag editing of " + StringUtil.getSuffix(file.getName()) + " files is not supported.";
            }

            MetaData existingMetaData = file.getMetaData();
            if (StringUtils.equals(artist, existingMetaData.getArtist()) &&
        		StringUtils.equals(albumArtist, existingMetaData.getAlbumArtist()) &&
        		StringUtils.equals(composer, existingMetaData.getComposer()) &&
                StringUtils.equals(album, existingMetaData.getAlbum()) &&
                StringUtils.equals(title, existingMetaData.getTitle()) &&
                StringUtils.equals(year, existingMetaData.getYear()) &&
                StringUtils.equals(genre, existingMetaData.getGenre()) &&
                ObjectUtils.equals(trackNumber, existingMetaData.getTrackNumber())) {
                return "SKIPPED";
            }

            MetaData newMetaData = new MetaData();
            newMetaData.setArtist(artist);
            newMetaData.setAlbumArtist(albumArtist);
            newMetaData.setComposer(composer);
            newMetaData.setAlbum(album);
            newMetaData.setTitle(title);
            newMetaData.setYear(year);
            newMetaData.setGenre(genre);
            newMetaData.setTrackNumber(trackNumber);
            parser.setMetaData(file, newMetaData);
            updatedMusicFileIds.add(id);
            return "UPDATED";

        } catch (Exception x) {
            LOG.warn("Failed to update tags for " + id, x);
            return x.getMessage();
        }
    }
    
    public void scanUpdatedFolders() {
    	Set<String> updatedDirectories = new HashSet<>();
    	for (int musicFileId : updatedMusicFileIds) {
    		updatedDirectories.add(mediaFileService.getMediaFile(musicFileId).getFile().getParent());
    	}
    	updatedMusicFileIds.clear(); // possible synchronization issue if multiple clients updates tags
    	LOG.debug("Updated directories: " + updatedDirectories);
    	try {
			libraryUpdateService.createSearchIndex(updatedDirectories, false, true, true);
		} catch (Throwable t) {
			LOG.warn("Could not complete scan after updating tags!", t);
		}
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setMetaDataParserFactory(MetaDataParserFactory metaDataParserFactory) {
        this.metaDataParserFactory = metaDataParserFactory;
    }

	public void setLibraryUpdateService(LibraryUpdateService libraryUpdateService) {
		this.libraryUpdateService = libraryUpdateService;
	}

}