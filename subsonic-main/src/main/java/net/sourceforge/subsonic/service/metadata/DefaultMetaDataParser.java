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

import net.sourceforge.subsonic.domain.MediaFile;

import com.github.hakko.musiccabinet.domain.model.library.MetaData;

/**
 * Parses meta data by guessing artist, album and song title based on the path of the file.
 *
 * @author Sindre Mehus
 */
public class DefaultMetaDataParser extends MetaDataParser {

    /**
     * Parses meta data for the given music file. No guessing or reformatting is done.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MetaData getRawMetaData(MediaFile file) {
        return getBasicMetaData(file);
    }

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Always false.
     */
    public boolean isEditingSupported() {
        return false;
    }

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public boolean isApplicable(MediaFile file) {
        return file.isFile();
    }
}