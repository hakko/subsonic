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

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.Logger;
import net.sourceforge.subsonic.androidapp.util.ModalBackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Util;
import net.sourceforge.subsonic.androidapp.util.VideoPlayerType;

/**
 * @author Sindre Mehus
 */
public class SubsonicTabActivity extends Activity {

    private static final Logger LOG = new Logger(SubsonicTabActivity.class);
    private static ImageLoader IMAGE_LOADER;

    private boolean destroyed;
    private View homeButton;
    private View musicButton;
    private View playlistButton;
    private View nowPlayingButton;

    @Override
    protected void onCreate(Bundle bundle) {
        Util.setUncaughtExceptionHandler(this);
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        startService(new Intent(this, DownloadServiceImpl.class));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        homeButton = findViewById(R.id.button_bar_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubsonicTabActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
            }
        });

        musicButton = findViewById(R.id.button_bar_music);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubsonicTabActivity.this, SelectArtistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
            }
        });

        playlistButton = findViewById(R.id.button_bar_playlists);
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubsonicTabActivity.this, SelectPlaylistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, intent);
            }
        });

        nowPlayingButton = findViewById(R.id.button_bar_now_playing);
        nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivityWithoutTransition(SubsonicTabActivity.this, DownloadActivity.class);
            }
        });

        if (this instanceof MainActivity) {
            homeButton.setEnabled(false);
        } else if (this instanceof SelectAlbumActivity || this instanceof SelectArtistActivity) {
            musicButton.setEnabled(false);
        } else if (this instanceof SelectPlaylistActivity) {
            playlistButton.setEnabled(false);
        } else if (this instanceof DownloadActivity || this instanceof LyricsActivity) {
            nowPlayingButton.setEnabled(false);
        }

        updateButtonVisibility();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.registerMediaButtonEventReceiver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_exit:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constants.INTENT_EXTRA_NAME_EXIT, true);
                Util.startActivityWithoutTransition(this, intent);
                return true;

            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.menu_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
        getImageLoader().clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isVolumeDown = keyCode == KeyEvent.KEYCODE_VOLUME_DOWN;
        boolean isVolumeUp = keyCode == KeyEvent.KEYCODE_VOLUME_UP;
        boolean isVolumeAdjust = isVolumeDown || isVolumeUp;
        boolean isJukebox = getDownloadService() != null && getDownloadService().isJukeboxEnabled();

        if (isVolumeAdjust && isJukebox) {
            getDownloadService().adjustJukeboxVolume(isVolumeUp);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void finish() {
        super.finish();
        Util.disablePendingTransition(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        // Set the font of title in the action bar.
        TextView text = (TextView) findViewById(R.id.actionbar_title_text);
        text.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    private void updateButtonVisibility() {
        int visibility = Util.isOffline(this) ? View.GONE : View.VISIBLE;
        playlistButton.setVisibility(visibility);
    }

    public void setProgressVisible(boolean visible) {
        View view = findViewById(R.id.tab_progress);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void updateProgress(String message) {
        TextView view = (TextView) findViewById(R.id.tab_progress_message);
        if (view != null) {
            view.setText(message);
        }
    }

    public DownloadService getDownloadService() {
        // If service is not available, request it to start and wait for it.
        for (int i = 0; i < 5; i++) {
            DownloadService downloadService = DownloadServiceImpl.getInstance();
            if (downloadService != null) {
                return downloadService;
            }
            LOG.warn("DownloadService not running. Attempting to start it.");
            startService(new Intent(this, DownloadServiceImpl.class));
            Util.sleepQuietly(50L);
        }
        return DownloadServiceImpl.getInstance();
    }

    protected void warnIfNetworkOrStorageUnavailable() {
        if (!Util.isExternalStoragePresent()) {
            Util.toast(this, R.string.select_album_no_sdcard);
        } else if (!Util.isOffline(this) && !Util.isNetworkConnected(this)) {
            Util.toast(this, R.string.select_album_no_network);
        }
    }

    protected synchronized ImageLoader getImageLoader() {
        if (IMAGE_LOADER == null) {
            IMAGE_LOADER = new ImageLoader(this);
        }
        return IMAGE_LOADER;
    }


    protected void setBackAction(final Runnable runnable) {

        View backLayout = findViewById(R.id.actionbar_back_layout);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runnable.run();
            }
        });
        backLayout.setBackgroundResource(R.drawable.actionbar_button);

        findViewById(R.id.actionbar_back).setVisibility(View.VISIBLE);
    }

    protected void downloadRecursively(final String id, final boolean save, final boolean append, final boolean autoplay) {
        ModalBackgroundTask<List<MusicDirectory.Entry>> task = new ModalBackgroundTask<List<MusicDirectory.Entry>>(this, false) {

            private static final int MAX_SONGS = 500;

            @Override
            protected List<MusicDirectory.Entry> doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService(SubsonicTabActivity.this);
                MusicDirectory root = musicService.getMusicDirectory(id, false, SubsonicTabActivity.this, this);
                List<MusicDirectory.Entry> songs = new LinkedList<MusicDirectory.Entry>();
                getSongsRecursively(root, songs);
                return songs;
            }

            private void getSongsRecursively(MusicDirectory parent, List<MusicDirectory.Entry> songs) throws Exception {
                if (songs.size() > MAX_SONGS) {
                    return;
                }

                for (MusicDirectory.Entry song : parent.getChildren(false, true)) {
                    if (!song.isVideo()) {
                        songs.add(song);
                    }
                }
                for (MusicDirectory.Entry dir : parent.getChildren(true, false)) {
                    MusicService musicService = MusicServiceFactory.getMusicService(SubsonicTabActivity.this);
                    getSongsRecursively(musicService.getMusicDirectory(dir.getId(), false, SubsonicTabActivity.this, this), songs);
                }
            }

            @Override
            protected void done(List<MusicDirectory.Entry> songs) {
                DownloadService downloadService = getDownloadService();
                if (!songs.isEmpty() && downloadService != null) {
                    if (!append) {
                        downloadService.clear();
                    }
                    warnIfNetworkOrStorageUnavailable();
                    downloadService.download(songs, save, autoplay, false);
                    Util.startActivityWithoutTransition(SubsonicTabActivity.this, DownloadActivity.class);
                }
            }
        };

        task.execute();
    }

    protected void playVideo(MusicDirectory.Entry entry)  {
        if (!Util.isNetworkConnected(this)) {
            Util.toast(this, R.string.select_album_no_network);
            return;
        }

        VideoPlayerType player = Util.getVideoPlayerType(this);
        try {
            player.playVideo(this, entry);
        } catch (Exception e) {
            Util.toast(this, e.getMessage(), false);
        }
    }
}

