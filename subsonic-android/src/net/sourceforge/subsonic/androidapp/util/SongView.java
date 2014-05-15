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
package net.sourceforge.subsonic.androidapp.util;

import java.io.File;
import java.util.WeakHashMap;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;

/**
 * Used to display songs in a {@code ListView}.
 *
 * @author Sindre Mehus
 */
public class SongView extends RelativeLayout implements Checkable {

    private static final Logger LOG = new Logger(SongView.class);
    private static final WeakHashMap<SongView, ?> INSTANCES = new WeakHashMap<SongView, Object>();
    private static Handler handler;

    private CheckedTextView checkedTextView;
    private TextView titleTextView;
    private TextView artistTextView;
    private TextView statusTextView;
    private ImageView downloadButton;
    private MusicDirectory.Entry song;

    public SongView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.song_list_item, this, true);

        checkedTextView = (CheckedTextView) findViewById(R.id.song_check);
        titleTextView = (TextView) findViewById(R.id.song_title);
        artistTextView = (TextView) findViewById(R.id.song_artist);
        statusTextView = (TextView) findViewById(R.id.song_status);
        downloadButton = (ImageView) findViewById(R.id.song_download_button);

        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SongView.this.showContextMenu();
            }
        });
        INSTANCES.put(this, null);
        int instanceCount = INSTANCES.size();
        if (instanceCount > 50) {
            LOG.warn(instanceCount + " live SongView instances");
        }
        startUpdater();
    }

    public void setSong(MusicDirectory.Entry song, boolean checkable) {
        this.song = song;
        StringBuilder artist = new StringBuilder(40);

        String bitRate = null;
        if (song.getBitRate() != null) {
            bitRate = String.format(getContext().getString(R.string.song_details_kbps), song.getBitRate());
        }

        VideoPlayerType videoPlayer = Util.getVideoPlayerType(getContext());
        String fileFormat;
        if (song.getTranscodedSuffix() == null || song.getTranscodedSuffix().equals(song.getSuffix())
                || (song.isVideo() && videoPlayer != VideoPlayerType.FLASH)) {
            fileFormat = song.getSuffix();
        } else {
            fileFormat = String.format("%s > %s", song.getSuffix(), song.getTranscodedSuffix());
        }

        if (song.getArtist() != null) {
            artist.append(song.getArtist()).append(" ");
        }
        artist.append("(")
                .append(String.format(getContext().getString(R.string.song_details_all), bitRate == null ? "" : bitRate, fileFormat))
                .append(")");

        titleTextView.setText(song.getTitle());
        artistTextView.setText(artist);
        statusTextView.setText(Util.formatDuration(song.getDuration()));
        checkedTextView.setVisibility(checkable && !song.isVideo() ? View.VISIBLE : View.GONE);

        update();
    }

    private void update() {
        DownloadService downloadService = DownloadServiceImpl.getInstance();
        if (downloadService == null) {
            return;
        }

        DownloadFile downloadFile = downloadService.forSong(song);
        File completeFile = downloadFile.getCompleteFile();
        File partialFile = downloadFile.getPartialFile();

        if (completeFile.exists()) {
            statusTextView.setText(Util.formatDuration(song.getDuration()));
            downloadButton.setImageResource(downloadFile.isSaved() ? R.drawable.download_pinned : R.drawable.download_cached);
        } else if (downloadFile.isDownloading() && !downloadFile.isDownloadCancelled() && partialFile.exists()) {
            statusTextView.setText(Util.formatLocalizedBytes(partialFile.length(), getContext()));
            downloadButton.setImageResource(R.drawable.download_streaming);
        } else {
            statusTextView.setText(Util.formatDuration(song.getDuration()));
            downloadButton.setImageResource(R.drawable.action_overflow_small);
        }

        boolean playing = downloadService.getCurrentPlaying() == downloadFile;
        if (playing) {
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.now_playing, 0);
        } else {
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private static synchronized void startUpdater() {
        if (handler != null) {
            return;
        }

        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateAll();
                handler.postDelayed(this, 1000L);
            }
        };
        handler.postDelayed(runnable, 1000L);
    }

    private static void updateAll() {
        try {
            for (SongView view : INSTANCES.keySet()) {
                if (view.isShown()) {
                    view.update();
                }
            }
        } catch (Throwable x) {
            LOG.warn("Error when updating song views.", x);
        }
    }

    @Override
    public void setChecked(boolean b) {
        checkedTextView.setChecked(b);
    }

    @Override
    public boolean isChecked() {
        return checkedTextView.isChecked();
    }

    @Override
    public void toggle() {
        checkedTextView.toggle();
    }
}
