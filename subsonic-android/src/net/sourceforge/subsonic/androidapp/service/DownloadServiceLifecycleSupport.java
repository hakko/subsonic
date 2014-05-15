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
package net.sourceforge.subsonic.androidapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import net.sourceforge.subsonic.androidapp.util.CacheCleaner;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.Logger;
import net.sourceforge.subsonic.androidapp.util.NotificationUtil;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Sindre Mehus
 */
public class DownloadServiceLifecycleSupport {

    private static final Logger LOG = new Logger(DownloadServiceLifecycleSupport.class);
    private static final String FILENAME_DOWNLOADS_SER = "downloadstate.ser";

    private final DownloadServiceImpl downloadService;
    private ScheduledExecutorService executorService;
    private BroadcastReceiver headsetEventReceiver;
    private BroadcastReceiver ejectEventReceiver;
    private PhoneStateListener phoneStateListener;
    private boolean externalStorageAvailable= true;

    /**
     * This receiver manages the intent that could come from other applications.
     */
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LOG.info("intentReceiver.onReceive: " + action);
            if (DownloadServiceImpl.CMD_PLAY.equals(action)) {
                downloadService.play();
            } else if (DownloadServiceImpl.CMD_NEXT.equals(action)) {
                downloadService.next();
            } else if (DownloadServiceImpl.CMD_PREVIOUS.equals(action)) {
                downloadService.previous();
            } else if (DownloadServiceImpl.CMD_TOGGLEPAUSE.equals(action)) {
                downloadService.togglePlayPause();
            } else if (DownloadServiceImpl.CMD_PAUSE.equals(action)) {
                downloadService.pause();
            } else if (DownloadServiceImpl.CMD_STOP.equals(action)) {
                downloadService.pause();
                downloadService.seekTo(0);
            }
        }
    };


    public DownloadServiceLifecycleSupport(DownloadServiceImpl downloadService) {
        this.downloadService = downloadService;
    }

    public void onCreate() {
        Runnable downloadChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    downloadService.checkDownloads();
                } catch (Throwable x) {
                    LOG.error("checkDownloads() failed.", x);
                }
            }
        };

        executorService = Executors.newScheduledThreadPool(2);
        executorService.scheduleWithFixedDelay(downloadChecker, 5, 5, TimeUnit.SECONDS);

        Runnable cleaner = new Runnable() {
            @Override
            public void run() {
                new CacheCleaner(downloadService, downloadService).clean();
            }
        };
        executorService.schedule(cleaner, 0L, TimeUnit.SECONDS);

        // Pause when headset is unplugged.
        headsetEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LOG.info("Headset event for: " + intent.getExtras().get("name"));
                if (intent.getExtras().getInt("state") == 0) {
                    downloadService.pause();
                }
            }
        };
        downloadService.registerReceiver(headsetEventReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        // Stop when SD card is ejected.
        ejectEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                externalStorageAvailable = Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction());
                if (!externalStorageAvailable) {
                    LOG.info("External media is ejecting. Stopping playback.");
                    downloadService.reset();
                } else {
                    LOG.info("External media is available.");
                }
            }
        };
        IntentFilter ejectFilter = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
        ejectFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        ejectFilter.addDataScheme("file");
        downloadService.registerReceiver(ejectEventReceiver, ejectFilter);

        // React to media buttons.
        Util.registerMediaButtonEventReceiver(downloadService);

        // Pause temporarily on incoming phone calls.
        phoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) downloadService.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // Register the handler for outside intents.
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(DownloadServiceImpl.CMD_PLAY);
        commandFilter.addAction(DownloadServiceImpl.CMD_TOGGLEPAUSE);
        commandFilter.addAction(DownloadServiceImpl.CMD_PAUSE);
        commandFilter.addAction(DownloadServiceImpl.CMD_STOP);
        commandFilter.addAction(DownloadServiceImpl.CMD_PREVIOUS);
        commandFilter.addAction(DownloadServiceImpl.CMD_NEXT);
        downloadService.registerReceiver(intentReceiver, commandFilter);

        deserializeDownloadQueue();
    }

    public void onStart(Intent intent) {
        if (intent == null) {
            return;
        }

        if (intent.getExtras() != null) {
            KeyEvent event = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (event != null) {
                handleKeyEvent(event);
            }
            if (intent.getBooleanExtra(Constants.INTENT_EXTRA_NAME_HIDE_NOTIFICATION, false)) {
                NotificationUtil.setNotificationHiddenByUser(downloadService, true);
                NotificationUtil.hideNotification(downloadService, new Handler());
            }
        }
    }

    public void onDestroy() {
        executorService.shutdown();
        serializeDownloadQueue();
        downloadService.clear(false);
        downloadService.unregisterReceiver(ejectEventReceiver);
        downloadService.unregisterReceiver(headsetEventReceiver);
        downloadService.unregisterReceiver(intentReceiver);

        TelephonyManager telephonyManager = (TelephonyManager) downloadService.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    public boolean isExternalStorageAvailable() {
        return externalStorageAvailable;
    }

    public void serializeDownloadQueue() {
        State state = new State();
        for (DownloadFile downloadFile : downloadService.getDownloads()) {
            state.songs.add(downloadFile.getSong());
        }
        state.currentPlayingIndex = downloadService.getCurrentPlayingIndex();
        state.currentPlayingPosition = downloadService.getPlayerPosition();

        LOG.info("Serialized currentPlayingIndex: " + state.currentPlayingIndex + ", currentPlayingPosition: " + state.currentPlayingPosition);
        FileUtil.serialize(downloadService, state, FILENAME_DOWNLOADS_SER);
    }

    private void deserializeDownloadQueue() {
       State state = FileUtil.deserialize(downloadService, FILENAME_DOWNLOADS_SER);
        if (state == null) {
            return;
        }
        LOG.info("Deserialized currentPlayingIndex: " + state.currentPlayingIndex + ", currentPlayingPosition: " + state.currentPlayingPosition);
        downloadService.restore(state.songs, state.currentPlayingIndex, state.currentPlayingPosition);

        // Work-around: Serialize again, as the restore() method creates a serialization without current playing info.
        serializeDownloadQueue();
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN || event.getRepeatCount() > 0) {
            return;
        }

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case 126: // KeyEvent.KEYCODE_MEDIA_PLAY
            case 127: // KeyEvent.KEYCODE_MEDIA_PAUSE
                downloadService.togglePlayPause();
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                downloadService.previous();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (downloadService.getCurrentPlayingIndex() < downloadService.size() - 1) {
                    downloadService.next();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                downloadService.reset();
                break;
            default:
                break;
        }
    }

    /**
     * Logic taken from packages/apps/Music.  Will pause when an incoming
     * call rings or if a call (incoming or outgoing) is connected.
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        private boolean resumeAfterCall;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (downloadService.getPlayerState() == PlayerState.STARTED) {
                        resumeAfterCall = true;
                        downloadService.pause();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (resumeAfterCall) {
                        resumeAfterCall = false;
                        downloadService.start();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static class State implements Serializable {
        private static final long serialVersionUID = -6346438781062572270L;

        private List<MusicDirectory.Entry> songs = new ArrayList<MusicDirectory.Entry>();
        private int currentPlayingIndex;
        private int currentPlayingPosition;
    }
}