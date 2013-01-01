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
package net.sourceforge.subsonic.service.metadata;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MetaData;

/**
 * Parses meta data from media files.
 *
 * @author Sindre Mehus
 */
public abstract class MetaDataParser {

    /**
     * Parses meta data for the given music file.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MetaData getMetaData(MediaFile file) {

        MetaData metaData = getRawMetaData(file);
        String artist = defaultIfEmpty(metaData.getArtist(), "[unknown artist]");
        String album = defaultIfEmpty(metaData.getAlbum(), "[unknown album]");
        String title = defaultIfEmpty(metaData.getTitle(), file.getName());

        metaData.setArtist(artist);
        metaData.setAlbum(album);
        metaData.setTitle(title);

        return metaData;
    }

    /**
     * Parses meta data for the given music file. No guessing or reformatting is done.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public abstract MetaData getRawMetaData(MediaFile file);

    /**
     * Updates the given file with the given meta data.
     *
     * @param file     The music file to update.
     * @param metaData The new meta data.
     */
    public abstract void setMetaData(MediaFile file, MetaData metaData);

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public abstract boolean isApplicable(MediaFile file);

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Whether tag editing is supported.
     */
    public abstract boolean isEditingSupported();

    /**
     * Returns meta-data containg file size and format.
     *
     * @param file The music file.
     * @return Meta-data containg file size and format.
     */
    protected MetaData getBasicMetaData(MediaFile file) {
        MetaData metaData = new MetaData();
        metaData.setFileSize(file.length());
        metaData.setFormat(file.getSuffix());
        return metaData;
    }

}