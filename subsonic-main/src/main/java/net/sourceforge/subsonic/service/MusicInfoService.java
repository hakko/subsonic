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

import net.sourceforge.subsonic.dao.*;
import net.sourceforge.subsonic.domain.*;

/**
 * Provides services for user rating and comments, as well
 * as details about how often and how recent albums have been played.
 *
 * @author Sindre Mehus
 */
public class MusicInfoService {

    private MusicFileInfoDao musicFileInfoDao;

    /**
     * Returns music file info for the given path.
     *
     * @return Music file info for the given path, or <code>null</code> if not found.
     */
    public MusicFileInfo getMusicFileInfoForPath(String path) {
        return musicFileInfoDao.getMusicFileInfoForPath(path);
    }

    /**
     * Creates a new music file info.
     *
     * @param info The music file info to create.
     */
    public void createMusicFileInfo(MusicFileInfo info) {
        musicFileInfoDao.createMusicFileInfo(info);
    }

    /**
     * Updates the given music file info.
     *
     * @param info The music file info to update.
     */
    public void updateMusicFileInfo(MusicFileInfo info) {
        musicFileInfoDao.updateMusicFileInfo(info);
    }

    public void setMusicFileInfoDao(MusicFileInfoDao musicFileInfoDao) {
        this.musicFileInfoDao = musicFileInfoDao;
    }
}
