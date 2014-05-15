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
package net.sourceforge.subsonic.androidapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.Logger;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * Request media button focus when connected to Bluetooth A2DP.
 *
 * @author Sindre Mehus
 */
public class BluetoothIntentReceiver extends BroadcastReceiver {

    private static final Logger LOG = new Logger(BluetoothIntentReceiver.class);

    // Same as constants in android.bluetooth.BluetoothProfile, which is API level 11.
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.info("GOT INTENT " + intent);

        if (isConnected(intent)) {
            LOG.info("Connected to Bluetooth A2DP, requesting media button focus.");
            Util.registerMediaButtonEventReceiver(context);
        } else if (isDisconnected(intent)) {
            LOG.info("Disconnected from Bluetooth A2DP, requesting pause.");
            context.sendBroadcast(new Intent(DownloadServiceImpl.CMD_PAUSE));
        }
    }

    private boolean isConnected(Intent intent) {
        if ("android.bluetooth.a2dp.action.SINK_STATE_CHANGED".equals(intent.getAction()) &&
                intent.getIntExtra("android.bluetooth.a2dp.extra.SINK_STATE", -1) == STATE_CONNECTED) {
            return true;
        }

        if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction()) &&
                intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1) == STATE_CONNECTED) {
            return true;
        }
        return false;
    }

    private boolean isDisconnected(Intent intent) {
        if ("android.bluetooth.a2dp.action.SINK_STATE_CHANGED".equals(intent.getAction()) &&
                intent.getIntExtra("android.bluetooth.a2dp.extra.SINK_STATE", -1) == STATE_DISCONNECTED) {
            return true;
        }

        if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction()) &&
                intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1) == STATE_DISCONNECTED) {
            return true;
        }
        return false;
    }

}