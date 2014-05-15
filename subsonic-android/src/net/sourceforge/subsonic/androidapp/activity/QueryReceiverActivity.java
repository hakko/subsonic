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

package net.sourceforge.subsonic.androidapp.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * Receives search queries and forwards to SearchActivity or SelectAlbumActivity.
 *
 * @author Sindre Mehus
 */
public class QueryReceiverActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        // Handle the normal search query case
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }

        // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
        else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            showResult(intent.getDataString(), intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        }

        finish();
        Util.disablePendingTransition(this);
    }

    private void doSearch(String query) {
        if (query != null) {
            Intent intent = new Intent(QueryReceiverActivity.this, SearchActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_NAME_QUERY, query);
            Util.startActivityWithoutTransition(QueryReceiverActivity.this, intent);
        }
    }

    private void showResult(String albumId, String name) {
        if (albumId != null) {
            Intent intent = new Intent(this, SelectAlbumActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, albumId);
            if (name != null) {
                intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, name);
            }
            Util.startActivityWithoutTransition(this, intent);
        }
    }
}