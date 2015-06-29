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
import android.view.View;

/**
 * Fallback for API level < 11. Open the options menu rather than displaying the popup menu.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class PopupMenuHelperBase extends PopupMenuHelper {

    @Override
    public void showMenu(Activity activity, View anchor, int menuResource) {
        activity.openOptionsMenu();
    }
}
