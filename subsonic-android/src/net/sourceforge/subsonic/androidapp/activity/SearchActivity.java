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

package net.sourceforge.subsonic.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Artist;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.SearchCritera;
import net.sourceforge.subsonic.androidapp.domain.SearchResult;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.ArtistAdapter;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.EntryAdapter;
import net.sourceforge.subsonic.androidapp.util.MergeAdapter;
import net.sourceforge.subsonic.androidapp.util.PopupMenuHelper;
import net.sourceforge.subsonic.androidapp.util.ShareUtil;
import net.sourceforge.subsonic.androidapp.util.StarUtil;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Performs searches and displays the matching artists, albums and songs.
 *
 * @author Sindre Mehus
 */
public class SearchActivity extends SubsonicTabActivity {

    private static final int DEFAULT_ARTISTS = 3;
    private static final int DEFAULT_ALBUMS = 5;
    private static final int DEFAULT_SONGS = 10;

    private static final int MAX_ARTISTS = 10;
    private static final int MAX_ALBUMS = 20;
    private static final int MAX_SONGS = 25;
    private ListView list;

    private View artistsHeading;
    private View albumsHeading;
    private View songsHeading;
    private TextView noMatchTextView;
    private View moreArtistsButton;
    private View moreAlbumsButton;
    private View moreSongsButton;
    private SearchResult searchResult;
    private MergeAdapter mergeAdapter;
    private ArtistAdapter artistAdapter;
    private ListAdapter moreArtistsAdapter;
    private EntryAdapter albumAdapter;
    private ListAdapter moreAlbumsAdapter;
    private ListAdapter moreSongsAdapter;
    private EntryAdapter songAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        View buttons = LayoutInflater.from(this).inflate(R.layout.search_buttons, null);

        artistsHeading = buttons.findViewById(R.id.search_artists);
        albumsHeading = buttons.findViewById(R.id.search_albums);
        songsHeading = buttons.findViewById(R.id.search_songs);

        noMatchTextView = (TextView) buttons.findViewById(R.id.search_no_match);
        moreArtistsButton = buttons.findViewById(R.id.search_more_artists);
        moreAlbumsButton = buttons.findViewById(R.id.search_more_albums);
        moreSongsButton = buttons.findViewById(R.id.search_more_songs);

        list = (ListView) findViewById(R.id.search_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view == moreArtistsButton) {
                    expandArtists();
                } else if (view == moreAlbumsButton) {
                    expandAlbums();
                } else if (view == moreSongsButton) {
                    expandSongs();
                } else {
                    Object item = parent.getItemAtPosition(position);
                    if (item instanceof Artist) {
                        onArtistSelected((Artist) item);
                    } else if (item instanceof MusicDirectory.Entry) {
                        MusicDirectory.Entry entry = (MusicDirectory.Entry) item;
                        if (entry.isDirectory()) {
                            onAlbumSelected(entry, false);
                        } else if (entry.isVideo()) {
                            onVideoSelected(entry);
                        } else {
                            onSongSelected(entry, false, true, true, false);
                        }

                    }
                }
            }
        });
        registerForContextMenu(list);

		// Button 1: gone
		findViewById(R.id.action_button_1).setVisibility(View.GONE);

        // Button 2: search
        final ImageButton actionSearchButton = (ImageButton)findViewById(R.id.action_button_2);
        actionSearchButton.setImageResource(R.drawable.action_search);
        actionSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchRequested();
            }
        });

        // Button 3: overflow
        final View overflowButton = findViewById(R.id.action_button_3);
        overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PopupMenuHelper().showMenu(SearchActivity.this, overflowButton, R.menu.main);
            }
        });

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String query = intent.getStringExtra(Constants.INTENT_EXTRA_NAME_QUERY);
        boolean starred = intent.getBooleanExtra(Constants.INTENT_EXTRA_NAME_QUERY_STARRED, false);
        boolean autoplay = intent.getBooleanExtra(Constants.INTENT_EXTRA_NAME_AUTOPLAY, false);

        setTitle(starred ? R.string.search_title_starred : R.string.search_title);

        if (query == null && !starred) {
            populateList(false);
        } else {
            mergeAdapter = new MergeAdapter();
            list.setAdapter(mergeAdapter);
            if (starred) {
                getStarred();
            } else {
                search(query, autoplay);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Object selectedItem = list.getItemAtPosition(info.position);

        Artist artist = selectedItem instanceof Artist ? (Artist) selectedItem : null;
        MusicDirectory.Entry entry = selectedItem instanceof MusicDirectory.Entry ? (MusicDirectory.Entry) selectedItem : null;
        boolean offline = Util.isOffline(this);

        if (artist != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.select_artist_context, menu);
            menu.findItem(R.id.artist_menu_star).setVisible(!offline && !artist.isStarred());
            menu.findItem(R.id.artist_menu_unstar).setVisible(!offline && artist.isStarred());
        }
        else if (entry != null && entry.isDirectory()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.select_album_context, menu);
            menu.findItem(R.id.album_menu_star).setVisible(!offline && !entry.isStarred());
            menu.findItem(R.id.album_menu_unstar).setVisible(!offline && entry.isStarred());
            menu.findItem(R.id.album_menu_share).setVisible(!offline);
        }
        else if (entry != null && !entry.isDirectory() && !entry.isVideo()) {
            DownloadFile downloadFile = getDownloadService().forSong(entry);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.select_song_context, menu);
            menu.findItem(R.id.song_menu_pin).setVisible(!downloadFile.isSaved());
            menu.findItem(R.id.song_menu_unpin).setVisible(downloadFile.isSaved());
            menu.findItem(R.id.song_menu_star).setVisible(!offline && !entry.isStarred());
            menu.findItem(R.id.song_menu_unstar).setVisible(!offline && entry.isStarred());
            menu.findItem(R.id.song_menu_share).setVisible(!offline);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        Object selectedItem = list.getItemAtPosition(info.position);

        Artist artist = selectedItem instanceof Artist ? (Artist) selectedItem : null;
        MusicDirectory.Entry entry = selectedItem instanceof MusicDirectory.Entry ? (MusicDirectory.Entry) selectedItem : null;
        String id = artist != null ? artist.getId() : entry.getId();

        switch (menuItem.getItemId()) {
            case R.id.artist_menu_play_now:
                downloadRecursively(artist.getId(), false, false, true);
                break;
            case R.id.artist_menu_play_last:
                downloadRecursively(artist.getId(), false, true, false);
                break;
            case R.id.artist_menu_pin:
                downloadRecursively(artist.getId(), true, true, false);
                break;
            case R.id.artist_menu_star:
                StarUtil.starInBackground(this, artist, true);
                return true;
            case R.id.artist_menu_unstar:
                StarUtil.starInBackground(this, artist, false);
                artistAdapter.remove(artist);
                return true;
            case R.id.album_menu_play_now:
                downloadRecursively(id, false, false, true);
                break;
            case R.id.album_menu_play_last:
                downloadRecursively(id, false, true, false);
                break;
            case R.id.album_menu_pin:
                downloadRecursively(id, true, true, false);
                break;
            case R.id.album_menu_star:
                StarUtil.starInBackground(this, entry, true);
                return true;
            case R.id.album_menu_unstar:
                StarUtil.starInBackground(this, entry, false);
                albumAdapter.remove(entry);
                return true;
            case R.id.album_menu_share:
                ShareUtil.shareInBackground(this, entry);
                return true;
            case R.id.song_menu_play_now:
                onSongSelected(entry, false, false, true, false);
                break;
            case R.id.song_menu_play_next:
                onSongSelected(entry, false, true, false, true);
                break;
            case R.id.song_menu_play_last:
                onSongSelected(entry, false, true, false, false);
                break;
            case R.id.song_menu_pin:
                getDownloadService().pin(Arrays.asList(entry));
                break;
            case R.id.song_menu_unpin:
                getDownloadService().unpin(Arrays.asList(entry));
                break;
            case R.id.song_menu_star:
                StarUtil.starInBackground(this, entry, true);
                return true;
            case R.id.song_menu_unstar:
                StarUtil.starInBackground(this, entry, false);
                songAdapter.remove(entry);
                return true;
            case R.id.song_menu_share:
                ShareUtil.shareInBackground(this, entry);
                return true;
            default:
                return super.onContextItemSelected(menuItem);
        }

        return true;
    }

    private void search(final String query, final boolean autoplay) {
        BackgroundTask<SearchResult> task = new TabActivityBackgroundTask<SearchResult>(this) {
            @Override
            protected SearchResult doInBackground() throws Throwable {
                SearchCritera criteria = new SearchCritera(query, MAX_ARTISTS, MAX_ALBUMS, MAX_SONGS);
                MusicService service = MusicServiceFactory.getMusicService(SearchActivity.this);
                return service.search(criteria, SearchActivity.this, this);
            }

            @Override
            protected void done(SearchResult result) {
                searchResult = result;
                populateList(false);
                if (autoplay) {
                    autoplay();
                }

            }
        };
        task.execute();
    }

    private void getStarred() {
        BackgroundTask<SearchResult> task = new TabActivityBackgroundTask<SearchResult>(this) {
            @Override
            protected SearchResult doInBackground() throws Throwable {
                MusicService service = MusicServiceFactory.getMusicService(SearchActivity.this);
                return service.getStarred(SearchActivity.this, this);
            }

            @Override
            protected void done(SearchResult result) {
                searchResult = result;
                populateList(true);
            }
        };
        task.execute();
    }

    private void populateList(boolean star) {
        mergeAdapter = new MergeAdapter();

        if (searchResult != null) {
            List<Artist> artists = searchResult.getArtists();
            List<MusicDirectory.Entry> albums = searchResult.getAlbums();
            List<MusicDirectory.Entry> songs = searchResult.getSongs();

            boolean empty = artists.isEmpty() && albums.isEmpty() && songs.isEmpty();
            if (empty) {
                mergeAdapter.addView(noMatchTextView, true);
                noMatchTextView.setText(star ? R.string.search_no_starred : R.string.search_no_match);
            }

            if (!artists.isEmpty()) {
                mergeAdapter.addView(artistsHeading);
                List<Artist> displayedArtists = new ArrayList<Artist>(artists.subList(0, Math.min(DEFAULT_ARTISTS, artists.size())));
                artistAdapter = new ArtistAdapter(this, displayedArtists);
                mergeAdapter.addAdapter(artistAdapter);
                if (artists.size() > DEFAULT_ARTISTS) {
                    moreArtistsAdapter = mergeAdapter.addView(moreArtistsButton, true);
                }
            }

            if (!albums.isEmpty()) {
                mergeAdapter.addView(albumsHeading);
                List<MusicDirectory.Entry> displayedAlbums = new ArrayList<MusicDirectory.Entry>(albums.subList(0, Math.min(DEFAULT_ALBUMS, albums.size())));
                albumAdapter = new EntryAdapter(this, getImageLoader(), displayedAlbums, false);
                mergeAdapter.addAdapter(albumAdapter);
                if (albums.size() > DEFAULT_ALBUMS) {
                    moreAlbumsAdapter = mergeAdapter.addView(moreAlbumsButton, true);
                }
            }

            if (!songs.isEmpty()) {
                mergeAdapter.addView(songsHeading);
                List<MusicDirectory.Entry> displayedSongs = new ArrayList<MusicDirectory.Entry>(songs.subList(0, Math.min(DEFAULT_SONGS, songs.size())));
                songAdapter = new EntryAdapter(this, getImageLoader(), displayedSongs, false);
                mergeAdapter.addAdapter(songAdapter);
                if (songs.size() > DEFAULT_SONGS) {
                    moreSongsAdapter = mergeAdapter.addView(moreSongsButton, true);
                }
            }
        }

        list.setAdapter(mergeAdapter);
    }

    private void expandArtists() {
        artistAdapter.clear();
        for (Artist artist : searchResult.getArtists()) {
            artistAdapter.add(artist);
        }
        artistAdapter.notifyDataSetChanged();
        mergeAdapter.removeAdapter(moreArtistsAdapter);
        mergeAdapter.notifyDataSetChanged();
    }

    private void expandAlbums() {
        albumAdapter.clear();
        for (MusicDirectory.Entry album : searchResult.getAlbums()) {
            albumAdapter.add(album);
        }
        albumAdapter.notifyDataSetChanged();
        mergeAdapter.removeAdapter(moreAlbumsAdapter);
        mergeAdapter.notifyDataSetChanged();
    }

    private void expandSongs() {
        songAdapter.clear();
        for (MusicDirectory.Entry song : searchResult.getSongs()) {
            songAdapter.add(song);
        }
        songAdapter.notifyDataSetChanged();
        mergeAdapter.removeAdapter(moreSongsAdapter);
        mergeAdapter.notifyDataSetChanged();
    }

    private void onArtistSelected(Artist artist) {
        Intent intent = new Intent(this, SelectAlbumActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, artist.getId());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, artist.getName());
        Util.startActivityWithoutTransition(this, intent);
    }

    private void onAlbumSelected(MusicDirectory.Entry album, boolean autoplay) {
        Intent intent = new Intent(SearchActivity.this, SelectAlbumActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, album.getId());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, album.getTitle());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_AUTOPLAY, autoplay);
        Util.startActivityWithoutTransition(SearchActivity.this, intent);
    }

    private void onSongSelected(MusicDirectory.Entry song, boolean save, boolean append, boolean autoplay, boolean playNext) {
        DownloadService downloadService = getDownloadService();
        if (downloadService != null) {
            if (!append) {
                downloadService.clear();
            }
            downloadService.download(Arrays.asList(song), save, false, playNext);
            if (autoplay) {
                downloadService.play(downloadService.size() - 1);
            }

            Util.toast(SearchActivity.this, getResources().getQuantityString(R.plurals.select_album_n_songs_added, 1, 1));
        }
    }

    private void onVideoSelected(MusicDirectory.Entry entry) {
        playVideo(entry);
    }

    private void autoplay() {
        if (!searchResult.getSongs().isEmpty()) {
            onSongSelected(searchResult.getSongs().get(0), false, false, true, false);
        } else if (!searchResult.getAlbums().isEmpty()) {
            onAlbumSelected(searchResult.getAlbums().get(0), true);
        }
    }
}