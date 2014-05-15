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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

/**
 * For API level >= 11. Show option menu ICS-style, using a popup menu anchored
 * to a given view.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class PopupMenuHelperHoneycomb extends PopupMenuHelperBase {

    @Override
    public void showMenu(final Activity activity, View anchor, int menuResource) {
        PopupMenu popup = new PopupMenu(activity, anchor);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return activity.onOptionsItemSelected(menuItem);
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuResource, popup.getMenu());

        activity.onPrepareOptionsMenu(popup.getMenu());
        popup.show();
    }

}
