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
package net.sourceforge.subsonic.androidapp.service.parser;

import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.util.Logger;
import net.sourceforge.subsonic.androidapp.util.ProgressListener;

/**
 * @author Sindre Mehus
 */
public class MusicDirectoryParser extends MusicDirectoryEntryParser {

    private static final Logger LOG = new Logger(MusicDirectoryParser.class);

    public MusicDirectoryParser(Context context) {
        super(context);
    }

    public MusicDirectory parse(Reader reader, ProgressListener progressListener) throws Exception {

        long t0 = System.currentTimeMillis();
        updateProgress(progressListener, R.string.parser_reading);
        init(reader);

        MusicDirectory dir = new MusicDirectory();
        int eventType;
        do {
            eventType = nextParseEvent();
            if (eventType == XmlPullParser.START_TAG) {
                String name = getElementName();
                if ("child".equals(name)) {
                    dir.addChild(parseEntry());
                } else if ("directory".equals(name)) {
                    dir.setId(get("id"));
                    dir.setName(get("name"));
                    dir.setParentId(get("parent"));
                    dir.setStarred(get("starred") != null);
                } else if ("error".equals(name)) {
                    handleError();
                }
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);

        validate();
        updateProgress(progressListener, R.string.parser_reading_done);

        long t1 = System.currentTimeMillis();
        LOG.debug("Got music directory in " + (t1 - t0) + "ms.");

        return dir;
    }
}