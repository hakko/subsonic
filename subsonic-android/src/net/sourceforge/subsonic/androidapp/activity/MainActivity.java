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

import java.util.Arrays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.MergeAdapter;
import net.sourceforge.subsonic.androidapp.util.PopupMenuHelper;
import net.sourceforge.subsonic.androidapp.util.ServerSettingsManager;
import net.sourceforge.subsonic.androidapp.util.Util;

public class MainActivity extends SubsonicTabActivity {

    private static final int MENU_GROUP_SERVER = 10;

    private static boolean infoDialogDisplayed;
    private TextView serverTextView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(Constants.INTENT_EXTRA_NAME_EXIT)) {
            exit();
        }
        setContentView(R.layout.main);

        loadSettings();

        View buttons = LayoutInflater.from(this).inflate(R.layout.main_buttons, null);

        final View serverButton = buttons.findViewById(R.id.main_select_server);
        serverTextView = (TextView) serverButton.findViewById(R.id.main_select_server_2);

        final TextView offlineButton = (TextView) buttons.findViewById(R.id.main_offline);
        offlineButton.setText(Util.isOffline(this) ? R.string.main_use_connected : R.string.main_use_offline);

        final TextView starredButton = (TextView) buttons.findViewById(R.id.main_starred);

        final View albumsTitle = buttons.findViewById(R.id.main_albums);
        final View albumsNewestButton = buttons.findViewById(R.id.main_albums_newest);
        final View albumsRandomButton = buttons.findViewById(R.id.main_albums_random);
        final View albumsHighestButton = buttons.findViewById(R.id.main_albums_highest);
        final View albumsRecentButton = buttons.findViewById(R.id.main_albums_recent);
        final View albumsFrequentButton = buttons.findViewById(R.id.main_albums_frequent);

        final View dummyView = findViewById(R.id.main_dummy);

        ListView list = (ListView) findViewById(R.id.main_list);

        MergeAdapter adapter = new MergeAdapter();

        adapter.addView(offlineButton, true);
        if (!Util.isOffline(this)) {
            adapter.addView(serverButton, true);
            adapter.addView(starredButton, true);
            adapter.addView(albumsTitle, false);
            adapter.addViews(Arrays.asList(albumsNewestButton, albumsRandomButton, albumsHighestButton, albumsRecentButton, albumsFrequentButton), true);
        }
        list.setAdapter(adapter);
        registerForContextMenu(dummyView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view == offlineButton) {
                    toggleOffline();
                } else if (view == serverButton) {
                    dummyView.showContextMenu();
                } else if (view == starredButton) {
                    showStarredMusic();
                } else if (view == albumsNewestButton) {
                    showAlbumList("newest");
                } else if (view == albumsRandomButton) {
                    showAlbumList("random");
                } else if (view == albumsHighestButton) {
                    showAlbumList("highest");
                } else if (view == albumsRecentButton) {
                    showAlbumList("recent");
                } else if (view == albumsFrequentButton) {
                    showAlbumList("frequent");
                }
            }
        });

        // Title: Subsonic
        setTitle(R.string.common_appname);

        // Button 1: gone
        ImageButton actionShuffleButton = (ImageButton) findViewById(R.id.action_button_1);
        actionShuffleButton.setImageResource(R.drawable.action_shuffle);
        actionShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShufflePlay();
            }
        });

        // Button 2: search
        ImageButton actionSearchButton = (ImageButton) findViewById(R.id.action_button_2);
        actionSearchButton.setImageResource(R.drawable.action_search);
        actionSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchRequested();
            }
        });
        actionSearchButton.setVisibility(Util.isOffline(this) ? View.GONE : View.VISIBLE);

        // Button 3: overflow
        final View overflowButton = findViewById(R.id.action_button_3);
        overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PopupMenuHelper().showMenu(MainActivity.this, overflowButton, R.menu.main);
            }
        });

        showInfoDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        serverTextView.setText(Util.getActiveServer(this).getName());
    }

    private void startShufflePlay() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.main_shuffle_confirm)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_NAME_SHUFFLE, true);
                        Util.startActivityWithoutTransition(MainActivity.this, intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void loadSettings() {
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences prefs = Util.getPreferences(this);
        if (!prefs.contains(Constants.PREFERENCES_KEY_OFFLINE)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.PREFERENCES_KEY_OFFLINE, false);
            editor.commit();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        ServerSettingsManager serverSettingsManager = new ServerSettingsManager(this);
        ServerSettingsManager.ServerSettings activeServer = serverSettingsManager.getActiveServer();

        for (ServerSettingsManager.ServerSettings server : serverSettingsManager.getAllServers()) {
            MenuItem menuItem = menu.add(MENU_GROUP_SERVER, server.getId(), server.getId(), server.getName());
            if (activeServer.getId() == server.getId()) {
                menuItem.setChecked(true);
            }
        }

        menu.setGroupCheckable(MENU_GROUP_SERVER, true, true);
        menu.setHeaderTitle(R.string.main_select_server);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        ServerSettingsManager serverSettingsManager = new ServerSettingsManager(this);
        if (menuItem.getItemId() == serverSettingsManager.getActiveServer().getId()) {
            return true;
        }

        serverSettingsManager.setActiveServerId(menuItem.getItemId());
        DownloadService service = getDownloadService();
        if (service != null) {
            service.clearIncomplete();
        }

        // Restart activity
        restart();
        return true;
    }

    private void toggleOffline() {
        Util.setOffline(this, !Util.isOffline(this));
        restart();
    }

    private void restart() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Util.startActivityWithoutTransition(this, intent);
    }

    private void exit() {
        stopService(new Intent(this, DownloadServiceImpl.class));
        finish();
    }

    private void showInfoDialog() {
        if (!infoDialogDisplayed) {
            infoDialogDisplayed = true;
            if (Util.getRestUrl(this, null).contains("demo.subsonic.org")) {
                Util.info(this, R.string.main_welcome_title, R.string.main_welcome_text);
            }
        }
    }

    private void showAlbumList(String type) {
        Intent intent = new Intent(this, SelectAlbumActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_TYPE, type);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_SIZE, 20);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_OFFSET, 0);
        Util.startActivityWithoutTransition(this, intent);
    }

    private void showStarredMusic() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_QUERY_STARRED, true);
        Util.startActivityWithoutTransition(this, intent);
    }
}