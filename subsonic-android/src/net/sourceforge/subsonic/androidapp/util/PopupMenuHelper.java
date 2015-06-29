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

import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * Helper class for showing option menu as a popup menu on API level >= 11, and fallback to the
 * old style option menu on API level < 11.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class PopupMenuHelper {

    public void showMenu(Activity activity, View anchor, int menuResource) {
        PopupMenuHelper helper = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                new PopupMenuHelperHoneycomb() : new PopupMenuHelperBase();
        helper.showMenu(activity, anchor, menuResource);
    }
}
