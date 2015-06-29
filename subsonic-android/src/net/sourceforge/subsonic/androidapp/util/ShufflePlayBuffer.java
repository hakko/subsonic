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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;

/**
 * @author Sindre Mehus
 * @version $Id: ShufflePlayBuffer.java 3539 2013-10-30 21:16:25Z sindre_mehus $
 */
public class ShufflePlayBuffer {

    private static final Logger LOG = new Logger(ShufflePlayBuffer.class);
    private static final int CAPACITY = 50;
    private static final int REFILL_THRESHOLD = 40;

    private final ScheduledExecutorService executorService;
    private final List<MusicDirectory.Entry> buffer = new ArrayList<MusicDirectory.Entry>();
    private Context context;
    private int currentServer;

    public ShufflePlayBuffer(Context context) {
        this.context = context;
        executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                refill();
            }
        };
        executorService.scheduleWithFixedDelay(runnable, 1, 10, TimeUnit.SECONDS);
    }

    public List<MusicDirectory.Entry> get(int size) {
        clearBufferIfNecessary();

        List<MusicDirectory.Entry> result = new ArrayList<MusicDirectory.Entry>(size);
        synchronized (buffer) {
            while (!buffer.isEmpty() && result.size() < size) {
                result.add(buffer.remove(buffer.size() - 1));
            }
        }
        LOG.info("Taking " + result.size() + " songs from shuffle play buffer. " + buffer.size() + " remaining.");
        return result;
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private void refill() {

        // Check if active server has changed.
        clearBufferIfNecessary();

        if (buffer.size() > REFILL_THRESHOLD || (!Util.isNetworkConnected(context) && !Util.isOffline(context))) {
            return;
        }

        try {
            MusicService service = MusicServiceFactory.getMusicService(context);
            int n = CAPACITY - buffer.size();
            MusicDirectory songs = service.getRandomSongs(n, context, null);

            synchronized (buffer) {
                buffer.addAll(songs.getChildren());
                LOG.info("Refilled shuffle play buffer with " + songs.getChildren().size() + " songs.");
            }
        } catch (Exception x) {
            LOG.warn("Failed to refill shuffle play buffer.", x);
        }
    }

    private void clearBufferIfNecessary() {
        synchronized (buffer) {
            int activeServer = Util.getActiveServer(context).getId();
            if (currentServer != activeServer) {
                currentServer = activeServer;
                buffer.clear();
            }
        }
    }

}
