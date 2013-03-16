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
package net.sourceforge.subsonic.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.subsonic.Logger;

import org.apache.commons.lang.StringUtils;

/**
 * A playlist is a list of music files that are associated to a remote player.
 *
 * @author Sindre Mehus
 */
public class Playlist {

    private List<MediaFile> files = new ArrayList<MediaFile>();
    private boolean repeatEnabled;
    private String name = "(unnamed)";
    private Status status = Status.PLAYING;

    public static final String PLAY = "P";
    public static final String ENQUEUE = "E";
    public static final String ADD = "A";

    private static final Logger LOG = Logger.getLogger(Playlist.class);

    /**
     * The index of the current song, or -1 is the end of the playlist is reached.
     * Note that both the index and the playlist size can be zero.
     */
    private int index = -1;

    /**
     * Used for undo functionality.
     */
    private List<MediaFile> filesBackup = new ArrayList<MediaFile>();
    private int indexBackup;

    public Playlist() {

    }

    /**
     * Returns the user-defined name of the playlist.
     *
     * @return The name of the playlist, or <code>null</code> if no name has been assigned.
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets the user-defined name of the playlist.
     *
     * @param name The name of the playlist.
     */
    public synchronized void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the current song in the playlist.
     *
     * @return The current song in the playlist, or <code>null</code> if no current song exists.
     */
    public synchronized MediaFile getCurrentFile() {
        if (index == -1 || index == 0 && size() == 0) {
            setStatus(Status.STOPPED);
            return null;
        } else {
        	StringBuilder sb = new StringBuilder();
        	for (MediaFile mf : files) {
        		sb.append(" " + mf.getName());
        	}
            MediaFile file = files.get(index);

            // Remove file from playlist if it doesn't exist.
            if (!file.exists()) {
                files.remove(index);
                index = Math.max(0, Math.min(index, size() - 1));
                return getCurrentFile();
            }

            return file;
        }
    }

    /**
     * Returns all music files in the playlist.
     *
     * @return All music files in the playlist.
     */
    public synchronized MediaFile[] getFiles() {
        return files.toArray(new MediaFile[files.size()]);
    }

    /**
     * Returns the music file at the given index.
     *
     * @param index The index.
     * @return The music file at the given index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public synchronized MediaFile getFile(int index) {
        return files.get(index);
    }

    /**
     * Skip to the next song in the playlist.
     */
    public synchronized void next() {
        index++;

        // Reached the end?
        if (index >= size()) {
            index = isRepeatEnabled() ? 0 : -1;
        }
    }

    /**
     * Returns the number of songs in the playlists.
     *
     * @return The number of songs in the playlists.
     */
    public synchronized int size() {
        return files.size();
    }

    /**
     * Returns whether the playlist is empty.
     *
     * @return Whether the playlist is empty.
     */
    public synchronized boolean isEmpty() {
        return files.isEmpty();
    }

    /**
     * Returns the index of the current song.
     *
     * @return The index of the current song, or -1 if the end of the playlist is reached.
     */
    public synchronized int getIndex() {
        return index;
    }

    /**
     * Sets the index of the current song.
     *
     * @param index The index of the current song.
     */
    public synchronized void setIndex(int index) {
        makeBackup();
        this.index = Math.max(0, Math.min(index, size() - 1));
        setStatus(Status.PLAYING);
    }

    public synchronized void setIndex(MediaFile mediaFile) {
    	this.index = files.indexOf(mediaFile);
    }

    /**
     * Adds one or more music file to the playlist.  If a given file is a directory, all its children
     * will be added recursively.
     *
     * @param append     Whether existing songs in the playlist should be kept.
     * @param mediaFiles The music files to add.
     * @throws IOException If an I/O error occurs.
     */
    public synchronized void addFiles(String mode, Iterable<MediaFile> mediaFiles) throws IOException {
        makeBackup();
        int insertIndex;
        if (PLAY.equals(mode)) {
            index = 0;
            insertIndex = 0;
            files.clear();
        } else if (ENQUEUE.equals(mode)) {
        	insertIndex = Math.min(index + 1, files.size());
        } else { // ADD
        	insertIndex = files.size();
        }
        List<MediaFile> list = new ArrayList<MediaFile>();
        for (MediaFile mediaFile : mediaFiles) {
            list.add(mediaFile); // TODO : used to be recursive
        }
        files.addAll(insertIndex, list);
        if (PLAY.equals(mode)) {
        	setStatus(Status.PLAYING);
        }
    }

    /**
     * Convenience method, equivalent to {@link #addFiles(boolean, Iterable)}.
     */
    public synchronized void addFiles(String mode, MediaFile... mediaFiles) throws IOException {
        addFiles(mode, Arrays.asList(mediaFiles));
    }

    /**
     * Removes the music file at the given index.
     *
     * @param index The playlist index.
     */
    public synchronized void removeFileAt(int index) {
        makeBackup();
        index = Math.max(0, Math.min(index, size() - 1));
        if (this.index > index) {
            this.index--;
        }
        files.remove(index);

        if (index != -1) {
            this.index = Math.max(0, Math.min(this.index, size() - 1));
        }
    }

    /**
     * Clears the playlist.
     */
    public synchronized void clear() {
        makeBackup();
        files.clear();
        index = -1;
    }

    /**
     * Shuffles the playlist.
     */
    public synchronized void shuffle() {
        makeBackup();
        MediaFile currentFile = getCurrentFile();
        Collections.shuffle(files);
        if (currentFile != null) {
            index = files.indexOf(currentFile);
        }
    }

    /**
     * Sorts the playlist according to the given sort order.
     */
    public synchronized void sort(final SortOrder sortOrder) {
        makeBackup();
        MediaFile currentFile = getCurrentFile();

        Comparator<MediaFile> comparator = new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile a, MediaFile b) {
                switch (sortOrder) {
                    case TRACK:
                        Integer trackA = a.getMetaData().getTrackNumber();
                        Integer trackB = b.getMetaData().getTrackNumber();
                        if (trackA == null) {
                            trackA = 0;
                        }
                        if (trackB == null) {
                            trackB = 0;
                        }
                        return trackA.compareTo(trackB);

                    case ARTIST:
                        String artistA = StringUtils.trimToEmpty(a.getMetaData().getArtist());
                        String artistB = StringUtils.trimToEmpty(b.getMetaData().getArtist());
                        return artistA.compareTo(artistB);

                    case ALBUM:
                        String albumA = StringUtils.trimToEmpty(a.getMetaData().getAlbum());
                        String albumB = StringUtils.trimToEmpty(b.getMetaData().getAlbum());
                        return albumA.compareTo(albumB);
                    default:
                        return 0;
                }
            }
        };

        Collections.sort(files, comparator);
        if (currentFile != null) {
            index = files.indexOf(currentFile);
        }
    }

    /**
     * Moves the song at the given index one step up.
     *
     * @param index The playlist index.
     */
    public synchronized void moveUp(int index) {
        makeBackup();
        if (index <= 0 || index >= size()) {
            return;
        }
        Collections.swap(files, index, index - 1);

        if (this.index == index) {
            this.index--;
        } else if (this.index == index - 1) {
            this.index++;
        }
    }

    /**
     * Moves the song at the given index one step down.
     *
     * @param index The playlist index.
     */
    public synchronized void moveDown(int index) {
        makeBackup();
        if (index < 0 || index >= size() - 1) {
            return;
        }
        Collections.swap(files, index, index + 1);

        if (this.index == index) {
            this.index++;
        } else if (this.index == index + 1) {
            this.index--;
        }
    }

    /**
     * Returns whether the playlist is repeating.
     *
     * @return Whether the playlist is repeating.
     */
    public synchronized boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    /**
     * Sets whether the playlist is repeating.
     *
     * @param repeatEnabled Whether the playlist is repeating.
     */
    public synchronized void setRepeatEnabled(boolean repeatEnabled) {
        this.repeatEnabled = repeatEnabled;
    }

    /**
     * Revert the last operation.
     */
    public synchronized void undo() {
        List<MediaFile> filesTmp = new ArrayList<MediaFile>(files);
        int indexTmp = index;

        index = indexBackup;
        files = filesBackup;

        indexBackup = indexTmp;
        filesBackup = filesTmp;
    }

    /**
     * Returns the playlist status.
     *
     * @return The playlist status.
     */
    public synchronized Status getStatus() {
        return status;
    }

    /**
     * Sets the playlist status.
     *
     * @param status The playlist status.
     */
    public synchronized void setStatus(Status status) {
        this.status = status;
        if (index == -1) {
            index = Math.max(0, Math.min(index, size() - 1));
        }
    }

    /**
     * Returns the total length in bytes.
     *
     * @return The total length in bytes.
     */
    public synchronized long length() {
        long length = 0;
        for (MediaFile mediaFile : files) {
            length += mediaFile.length();
        }
        return length;
    }

    private void makeBackup() {
        filesBackup = new ArrayList<MediaFile>(files);
        indexBackup = index;
    }

    /**
     * Playlist status.
     */
    public enum Status {
        PLAYING,
        STOPPED
    }

    /**
     * Playlist sort order.
     */
    public enum SortOrder {
        TRACK,
        ARTIST,
        ALBUM
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Playlist " + hashCode() + ":");
    	for (MediaFile mf : files) {
    		sb.append(" " + mf.getName());
    	}
    	return sb.toString();
    }
}