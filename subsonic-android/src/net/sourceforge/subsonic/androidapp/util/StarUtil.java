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
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.Artist;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;

/**
 * @author Sindre Mehus
 * @version $Id: StarUtil.java 3553 2013-11-02 15:58:08Z sindre_mehus $
 */
public final class StarUtil {

    private StarUtil() {
    }

    public static void starInBackground(Activity activity, MusicDirectory.Entry song, boolean star) {
        starInBackground(activity, song.getId(), song.getTitle(), star);
        song.setStarred(star);
    }

    public static void starInBackground(Activity activity, MusicDirectory directory, boolean star) {
        starInBackground(activity, directory.getId(), directory.getName(), star);
        directory.setStarred(star);
    }

    public static void starInBackground(Activity activity, Artist artist, boolean star) {
        starInBackground(activity, artist.getId(), artist.getName(), star);
        artist.setStarred(star);
    }

    private static void starInBackground(final Activity activity, final String id, final String name, final boolean star) {
        new SilentBackgroundTask<Void>(activity) {
            @Override
            protected Void doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService(activity);
                musicService.star(id, star, activity, null);
                return null;
            }

            @Override
            protected void done(Void result) {
                Util.toast(activity, star ? R.string.star_star_succesful : R.string.star_unstar_succesful, true, name);
            }

            @Override
            protected void error(Throwable error) {
                Util.toast(activity, star ? R.string.star_star_failed : R.string.star_unstar_failed,
                        true, name, getErrorMessage(error));
            }
        }.execute();
    }
}
