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

 Copyright 2013 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.androidapp.util;

import android.util.Log;

public class Logger {

    private final String tag;

    public Logger(Class<?> clazz) {
        tag = "subsonic." + clazz.getSimpleName();
    }

    public void error(String message) {
        Log.e(tag, message);
    }

    public void error(String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }

    public void warn(String message) {
        Log.w(tag, message);
    }

    public void warn(String message, Throwable throwable) {
        Log.w(tag, message, throwable);
    }

    public void info(String message) {
        Log.i(tag, message);
    }

    public void info(String message, Throwable throwable) {
        Log.i(tag, message, throwable);
    }

    public void debug(String message) {
        Log.d(tag, message);
    }
}
