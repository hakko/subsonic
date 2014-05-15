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
import java.io.PrintWriter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;

/**
 * Logs the stack trace of uncaught exceptions to a file on the SD card.
 */
public class SubsonicUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = new Logger(SubsonicUncaughtExceptionHandler.class);

    private final Thread.UncaughtExceptionHandler defaultHandler;
    private final Context context;

    public SubsonicUncaughtExceptionHandler(Context context) {
        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        File file = null;
        PrintWriter printWriter = null;
        try {

            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("net.sourceforge.subsonic.androidapp", 0);
            file = new File(Environment.getExternalStorageDirectory(), "subsonic-stacktrace.txt");
            printWriter = new PrintWriter(file);
            printWriter.println("Android API level: " + Build.VERSION.SDK_INT);
            printWriter.println("Subsonic version name: " + packageInfo.versionName);
            printWriter.println("Subsonic version code: " + packageInfo.versionCode);
            printWriter.println();
            throwable.printStackTrace(printWriter);
            LOG.info("Stack trace written to " + file);
        } catch (Throwable x) {
            LOG.error("Failed to write stack trace to " + file, x);
        } finally {
            Util.close(printWriter);
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        }
    }
}
