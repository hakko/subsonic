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

 Copyright 2010 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.androidapp.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.activity.DownloadActivity;
import net.sourceforge.subsonic.androidapp.activity.MainActivity;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Logger;

/**
 * Simple widget to show currently playing album art along
 * with play/pause and next/prev track buttons.
 * <p/>
 * Based on source code from the stock Android Music app.
 *
 * @author Sindre Mehus
 */
public class SubsonicAppWidgetProvider extends AppWidgetProvider {

    private static SubsonicAppWidgetProvider instance;
    private static final Logger LOG = new Logger(SubsonicAppWidgetProvider.class);

    public static synchronized SubsonicAppWidgetProvider getInstance() {
        if (instance == null) {
            instance = new SubsonicAppWidgetProvider();
        }
        return instance;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        defaultAppWidget(context, appWidgetIds);
    }

    /**
     * Initialize given widgets to default state, where we launch Subsonic on default click
     * and hide actions if service not running.
     */
    private void defaultAppWidget(Context context, int[] appWidgetIds) {
        final Resources res = context.getResources();
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        views.setTextViewText(R.id.widget_artist, res.getText(R.string.widget_initial_text));

        linkButtons(context, views, false);
        pushUpdate(context, appWidgetIds, views);
    }

    private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        // Update specific list of appWidgetIds if given, otherwise default to all
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            manager.updateAppWidget(appWidgetIds, views);
        } else {
            manager.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }

    /**
     * Handle a change notification coming over from {@link DownloadService}
     */
    public void notifyChange(Context context, DownloadService service, boolean playing) {
        if (hasInstances(context)) {
            performUpdate(context, service, null, playing);
        }
    }

    /**
     * Check against {@link AppWidgetManager} if there are any instances of this widget.
     */
    private boolean hasInstances(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, getClass()));
        return (appWidgetIds.length > 0);
    }

    /**
     * Update all active widget instances by pushing changes
     */
    private void performUpdate(Context context, DownloadService service, int[] appWidgetIds, boolean playing) {
        final Resources res = context.getResources();
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        MusicDirectory.Entry currentPlaying = service.getCurrentPlaying() == null ? null : service.getCurrentPlaying().getSong();
        String title = currentPlaying == null ? null : currentPlaying.getTitle();
        CharSequence artist = currentPlaying == null ? null : currentPlaying.getArtist();
        CharSequence errorText = null;

        // Show error message?
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_SHARED) || status.equals(Environment.MEDIA_UNMOUNTED)) {
            errorText = res.getText(R.string.widget_sdcard_busy);
        } else if (status.equals(Environment.MEDIA_REMOVED)) {
            errorText = res.getText(R.string.widget_sdcard_missing);
        } else if (currentPlaying == null) {
            errorText = res.getText(R.string.widget_initial_text);
        }

        if (errorText != null) {
            // Show error state to user
            views.setTextViewText(R.id.widget_title, null);
            views.setTextViewText(R.id.widget_artist, errorText);
            views.setViewVisibility(R.id.widget_text_separator, View.GONE);
            views.setImageViewResource(R.id.widget_albumart, R.drawable.appwidget_art_unknown);
        } else {
            // No error, so show normal titles
            views.setTextViewText(R.id.widget_title, title);
            views.setTextViewText(R.id.widget_artist, artist);
            views.setViewVisibility(R.id.widget_text_separator, title != null && artist != null ? View.VISIBLE : View.GONE);
        }

        // Set correct visibility for pause and play buttons.
        views.setViewVisibility(R.id.widget_play, playing ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.widget_pause, playing ? View.VISIBLE : View.GONE);

        // Set the cover art
        try {
            int size = context.getResources().getDimensionPixelSize(R.dimen.widget_album_art_size);
            Bitmap bitmap = currentPlaying == null ? null : FileUtil.getAlbumArtBitmap(context, currentPlaying, size);

            if (bitmap == null) {
                // Set default cover art
                views.setImageViewResource(R.id.widget_albumart, R.drawable.appwidget_art_unknown);
            } else {
                views.setImageViewBitmap(R.id.widget_albumart, bitmap);
            }
        } catch (Exception x) {
            LOG.error("Failed to load cover art", x);
            views.setImageViewResource(R.id.widget_albumart, R.drawable.appwidget_art_unknown);
        }

        // Link actions buttons to intents
        linkButtons(context, views, currentPlaying != null);

        pushUpdate(context, appWidgetIds, views);
    }

    /**
     * Link up various button actions using {@link PendingIntent}.
     *
     * @param playerActive True if player is active in background, which means
     *                     widget click will launch {@link DownloadActivity},
     *                     otherwise we launch {@link MainActivity}.
     */
    private void linkButtons(Context context, RemoteViews views, boolean playerActive) {

        Intent intent = new Intent(context, playerActive ? DownloadActivity.class : MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_albumart, pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_text_layout, pendingIntent);

        // Emulate media button clicks.
        intent = new Intent("1");
        intent.setComponent(new ComponentName(context, DownloadServiceImpl.class));
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
        pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_play, pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_pause, pendingIntent);

        intent = new Intent("2");  // Use a unique action name to ensure a different PendingIntent to be created.
        intent.setComponent(new ComponentName(context, DownloadServiceImpl.class));
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
        pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_next, pendingIntent);

        intent = new Intent("3");  // Use a unique action name to ensure a different PendingIntent to be created.
        intent.setComponent(new ComponentName(context, DownloadServiceImpl.class));
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
        pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_prev, pendingIntent);
    }
}
