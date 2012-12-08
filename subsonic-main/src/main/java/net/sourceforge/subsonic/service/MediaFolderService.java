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

import java.util.List;
import java.util.Set;

import net.sourceforge.subsonic.dao.MediaFolderDao;
import net.sourceforge.subsonic.domain.MediaFolder;

/**
 * Service wrapping Media Folder DAO.
 * 
 */
public class MediaFolderService {

	private MediaFolderDao mediaFolderDao;

    public void init() {
        ServiceLocator.setMediaFolderService(this);
    }
    
    /**
     * Returns all media folders.
     */
    public List<MediaFolder> getAllMediaFolders() {
        return mediaFolderDao.getAllMediaFolders();
    }

    /**
     *	Returns all media folders that are configured as indexed.
     */
    public List<MediaFolder> getIndexedMediaFolders() {
        return mediaFolderDao.getIndexedMediaFolders();
    }

    /**
     *	Returns all media folders that are not configured as indexed.
     */
    public List<MediaFolder> getNonIndexedMediaFolders() {
        return mediaFolderDao.getNonIndexedMediaFolders();
    }
    
    /**
     * Creates a new media folder.
     *
     * @param mediaFolder The media folder to create.
     */
    public void createMediaFolder(MediaFolder mediaFolder) {
        mediaFolderDao.createMediaFolder(mediaFolder);
    }

    /**
     * Updates the given media folder.
     *
     * @param mediaFolder The media folder to update.
     */
    public void updateMediaFolder(MediaFolder mediaFolder) {
        mediaFolderDao.updateMediaFolder(mediaFolder);
    }

    /**
     * Deletes the media folder with the given ID.
     *
     * @param id The ID of the media folder to delete.
     */
    public void deleteMediaFolder(Integer id) {
        mediaFolderDao.deleteMediaFolder(id);
    }

    public boolean hasIndexedParentFolder(String folder) {
    	return mediaFolderDao.hasIndexedParentFolder(folder);
    }
    
    public void setChildFoldersToNonIndexed(Set<String> paths) {
    	mediaFolderDao.setChildFoldersToNonIndexed(paths);
    }

}