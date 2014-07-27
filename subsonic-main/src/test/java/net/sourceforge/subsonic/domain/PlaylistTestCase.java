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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.sourceforge.subsonic.domain.Playlist.SortOrder;
import net.sourceforge.subsonic.domain.Playlist.Status;

import org.junit.Test;

import com.github.hakko.musiccabinet.domain.model.library.MetaData;

/**
 * Unit test of {@link Playlist}.
 *
 * @author Sindre Mehus
 */
public class PlaylistTestCase {

    @Test
    public void testEmpty() {
        Playlist playlist = new Playlist();
        assertEquals(0, playlist.size());
        assertTrue(playlist.isEmpty());
        assertEquals(0, playlist.getFiles().length);
        assertNull(playlist.getCurrentFile());
    }

    public void xtestStatus() throws Exception {
        Playlist playlist = new Playlist();
        assertEquals(Status.STOPPED, playlist.getStatus());

        playlist.addFiles(Playlist.PLAY, new TestMediaFile());
        assertEquals(Status.PLAYING, playlist.getStatus());

        playlist.clear();
        assertEquals(Status.STOPPED, playlist.getStatus());

        playlist.addFiles(Playlist.ADD, new TestMediaFile());
        assertEquals(Status.STOPPED, playlist.getStatus());

        playlist.addFiles(Playlist.ENQUEUE, new TestMediaFile());
        assertEquals(Status.STOPPED, playlist.getStatus());
    }

    @Test
    public void testMoveUp() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveUp(0);
        assertPlaylistEquals(playlist, 0, "A", "B", "C", "D");

        playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveUp(9999);
        assertPlaylistEquals(playlist, 0, "A", "B", "C", "D");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.moveUp(1);
        assertPlaylistEquals(playlist, 0, "B", "A", "C", "D");

        playlist = createPlaylist(3, "A", "B", "C", "D");
        playlist.moveUp(3);
        assertPlaylistEquals(playlist, 2, "A", "B", "D", "C");
    }

    @Test
    public void testMoveDown() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveDown(0);
        assertPlaylistEquals(playlist, 1, "B", "A", "C", "D");

        playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.moveDown(9999);
        assertPlaylistEquals(playlist, 0, "A", "B", "C", "D");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.moveDown(1);
        assertPlaylistEquals(playlist, 2, "A", "C", "B", "D");

        playlist = createPlaylist(3, "A", "B", "C", "D");
        playlist.moveDown(3);
        assertPlaylistEquals(playlist, 3, "A", "B", "C", "D");
    }

    public void xtestRemove() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.removeFileAt(0);
        assertPlaylistEquals(playlist, -1, "B", "C", "D");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.removeFileAt(0);
        assertPlaylistEquals(playlist, 0, "B", "C", "D");

        playlist = createPlaylist(0, "A", "B", "C", "D");
        playlist.removeFileAt(3);
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist = createPlaylist(1, "A", "B", "C", "D");
        playlist.removeFileAt(1);
        assertPlaylistEquals(playlist, -1, "A", "C", "D");

        playlist = createPlaylist(3, "A", "B", "C", "D");
        playlist.removeFileAt(3);
        assertPlaylistEquals(playlist, -1, "A", "B", "C");

        playlist = createPlaylist(0, "A");
        playlist.removeFileAt(0);
        assertPlaylistEquals(playlist, -1);
    }

    @Test
    public void testNext() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C");
        assertFalse(playlist.isRepeatEnabled());
        playlist.next();
        assertPlaylistEquals(playlist, 1, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, 2, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, -1, "A", "B", "C");

        playlist = createPlaylist(0, "A", "B", "C");
        playlist.setRepeatEnabled(true);
        assertTrue(playlist.isRepeatEnabled());
        playlist.next();
        assertPlaylistEquals(playlist, 1, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, 2, "A", "B", "C");
        playlist.next();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");
    }

    public void xtestPlayAfterEndReached() throws Exception {
        Playlist playlist = createPlaylist(2, "A", "B", "C");
        playlist.setStatus(Status.PLAYING);
        playlist.next();
        assertNull(playlist.getCurrentFile());
        assertEquals(Status.STOPPED, playlist.getStatus());

        playlist.setStatus(Status.PLAYING);
        assertEquals(Status.PLAYING, playlist.getStatus());
        assertEquals(0, playlist.getIndex());
        assertEquals("A", playlist.getCurrentFile().getName());
    }

    @Test
    public void testAppend() throws Exception {
        Playlist playlist = createPlaylist(1, "A", "B", "C");

        playlist.addFiles(Playlist.ADD, new TestMediaFile("D"));
        assertPlaylistEquals(playlist, 1, "A", "B", "C", "D");

        playlist.addFiles(Playlist.PLAY, new TestMediaFile("E"));
        assertPlaylistEquals(playlist, 0, "E");
    }

    public void xtestEnqueue() throws Exception {
        Playlist playlist = new Playlist();
        playlist.addFiles(Playlist.ENQUEUE, new TestMediaFile("A"));
        assertPlaylistEquals(playlist, -1, "A");

        playlist.addFiles(Playlist.ENQUEUE, new TestMediaFile("B"));
        assertPlaylistEquals(playlist, -1, "B", "A");

        playlist.clear();

        playlist.addFiles(Playlist.PLAY, new TestMediaFile("A"), new TestMediaFile("B"));
        assertPlaylistEquals(playlist, 0, "A", "B");

        playlist.addFiles(Playlist.ENQUEUE, new TestMediaFile("C"));
        assertPlaylistEquals(playlist, 0, "A", "C", "B");

        playlist.setIndex(2);

        playlist.addFiles(Playlist.ENQUEUE, new TestMediaFile("D"));
        assertPlaylistEquals(playlist, 2, "A", "C", "B", "D");

        playlist.addFiles(Playlist.ENQUEUE, new TestMediaFile("E"));
        assertPlaylistEquals(playlist, 2, "A", "C", "B", "E", "D");
    }

    @Test
    public void testUndo() throws Exception {
        Playlist playlist = createPlaylist(0, "A", "B", "C");
        playlist.setIndex(2);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.removeFileAt(2);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.clear();
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.addFiles(Playlist.ADD, new TestMediaFile());
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.moveDown(1);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");

        playlist.moveUp(1);
        playlist.undo();
        assertPlaylistEquals(playlist, 0, "A", "B", "C");
    }

    @Test
    public void testOrder() throws IOException {
        Playlist playlist = new Playlist();
        playlist.addFiles(Playlist.ADD, new TestMediaFile((short)2, "Artist A", "Album B"));
        playlist.addFiles(Playlist.ADD, new TestMediaFile((short)1, "Artist C", "Album C"));
        playlist.addFiles(Playlist.ADD, new TestMediaFile((short)3, "Artist B", "Album A"));
        playlist.addFiles(Playlist.ADD, new TestMediaFile(null, "Artist D", "Album D"));
        playlist.setIndex(2);
        assertEquals("Error in sort.", new Short((short)3), playlist.getCurrentFile().getMetaData().getTrackNr());

        // Order by track.
        playlist.sort(SortOrder.TRACK);
        assertEquals("Error in sort().", null, playlist.getFile(0).getMetaData().getTrackNr());
        assertEquals("Error in sort().", new Short((short)1), playlist.getFile(1).getMetaData().getTrackNr());
        assertEquals("Error in sort().", new Short((short)2), playlist.getFile(2).getMetaData().getTrackNr());
        assertEquals("Error in sort().", new Short((short)3), playlist.getFile(3).getMetaData().getTrackNr());
        assertEquals("Error in sort().", new Short((short)3), playlist.getCurrentFile().getMetaData().getTrackNr());

        // Order by artist.
        playlist.sort(SortOrder.ARTIST);
        assertEquals("Error in sort().", "Artist A", playlist.getFile(0).getMetaData().getArtist());
        assertEquals("Error in sort().", "Artist B", playlist.getFile(1).getMetaData().getArtist());
        assertEquals("Error in sort().", "Artist C", playlist.getFile(2).getMetaData().getArtist());
        assertEquals("Error in sort().", "Artist D", playlist.getFile(3).getMetaData().getArtist());
        assertEquals("Error in sort().", new Short((short)3), playlist.getCurrentFile().getMetaData().getTrackNr());

        // Order by album.
        playlist.sort(SortOrder.ALBUM);
        assertEquals("Error in sort().", "Album A", playlist.getFile(0).getMetaData().getAlbum());
        assertEquals("Error in sort().", "Album B", playlist.getFile(1).getMetaData().getAlbum());
        assertEquals("Error in sort().", "Album C", playlist.getFile(2).getMetaData().getAlbum());
        assertEquals("Error in sort().", "Album D", playlist.getFile(3).getMetaData().getAlbum());
        assertEquals("Error in sort().", new Short((short)3), playlist.getCurrentFile().getMetaData().getTrackNr());
    }

    private void assertPlaylistEquals(Playlist playlist, int index, String... songs) {
        assertEquals(songs.length, playlist.size());
        for (int i = 0; i < songs.length; i++) {
            assertEquals(songs[i], playlist.getFiles()[i].getName());
        }

        if (index == -1) {
            assertNull(playlist.getCurrentFile());
        } else {
            assertEquals(songs[index], playlist.getCurrentFile().getName());
        }
    }

    private Playlist createPlaylist(int index, String... songs) throws Exception {
        Playlist playlist = new Playlist();
        for (String song : songs) {
            playlist.addFiles(Playlist.ADD, new TestMediaFile(song));
        }
        playlist.setIndex(index);
        return playlist;
    }

    private static class TestMediaFile extends MediaFile {

        private static final long serialVersionUID = 1L;
        private String name;
        private MetaData metaData;

        TestMediaFile() {}

        TestMediaFile(String name) {
            this.name = name;
        }

        TestMediaFile(Short track, String artist, String album) {
            metaData = new MetaData();
            if (track != null) {
                metaData.setTrackNr(track);
            }
            metaData.setArtist(artist);
            metaData.setAlbum(album);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return metaData;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }
    }
}