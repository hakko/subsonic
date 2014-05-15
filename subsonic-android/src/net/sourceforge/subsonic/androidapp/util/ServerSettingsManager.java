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
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Sindre Mehus
 * @version $Id: ServerSettingsManager.java 3485 2013-05-30 13:20:55Z sindre_mehus $
 */
public class ServerSettingsManager {

    /**
     * Key for property containing a list of server IDs, separated by whitespace, e.g., "0 1 3"
     */
    private static final String KEY_SERVERS = "servers";

    /**
     * Key for property containing containg the ID of the currently active server.
     */
    private static final String KEY_ACTIVE_SERVER = "activeServer";

    private final Context context;

    public ServerSettingsManager(Context context) {
        this.context = context;
        initDefault();
    }

    private void initDefault() {
        SharedPreferences prefs = Util.getPreferences(context);

        // If KEY_SERVERS is not set, migrate from old schema - or set default demo server.
        String servers = prefs.getString(KEY_SERVERS, null);
        if (servers == null) {
            boolean oldServerSettingsFound = false;

            for (int i = 1; i <= 3; i++) {
                String name = prefs.getString(Constants.PREFERENCES_KEY_SERVER_NAME + i, null);
                String url = prefs.getString(Constants.PREFERENCES_KEY_SERVER_URL + i, null);
                String username = prefs.getString(Constants.PREFERENCES_KEY_USERNAME + i, null);
                String password = prefs.getString(Constants.PREFERENCES_KEY_PASSWORD + i, null);

                if (name != null && url != null && username != null && password != null) {
                    oldServerSettingsFound = true;
                    int serverId = addServer(name, url, username, password);

                    // Restore existing active server.
                    if (i == prefs.getInt(Constants.PREFERENCES_KEY_SERVER_INSTANCE, 1)) {
                        setActiveServerId(serverId);
                    }
                }
            }

            if (!oldServerSettingsFound) {
                int serverId = addServer("Subsonic Demo", "http://demo.subsonic.org", "android-guest", "guest");
                setActiveServerId(serverId);
            }
        }
    }

    public List<ServerSettings> getAllServers() {
        List<ServerSettings> result = new ArrayList<ServerSettings>();
        for (Integer serverId : getServerIds()) {
            result.add(getServer(serverId));
        }
        return result;
    }

    public ServerSettings getServer(Integer serverId) {
        return new ServerSettings(context, serverId);
    }

    public ServerSettings getActiveServer() {
        SharedPreferences prefs = Util.getPreferences(context);
        int activeServerId = prefs.getInt(KEY_ACTIVE_SERVER, 0);
        return getServer(activeServerId);
    }

    public void setActiveServerId(int activeServerId) {
        SharedPreferences prefs = Util.getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_ACTIVE_SERVER, activeServerId);
        editor.commit();
    }

    public int addServer(String name, String url, String username, String password) {
        SortedSet<Integer> serverIds = getServerIds();
        int serverId = serverIds.isEmpty() ? 0 : serverIds.last() + 1;
        new ServerSettings(serverId, name, url, username, password).save(context);
        serverIds.add(serverId);
        saveServerIds(serverIds);
        return serverId;
    }

    private void saveServerIds(SortedSet<Integer> serverIds) {
        StringBuilder builder = new StringBuilder();

        Iterator<Integer> iterator = serverIds.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(" ");
            }
        }
        SharedPreferences prefs = Util.getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVERS, builder.toString());
        editor.commit();
    }

    private SortedSet<Integer> getServerIds() {
        SharedPreferences prefs = Util.getPreferences(context);
        SortedSet<Integer> result = new TreeSet<Integer>();
        String serverString = prefs.getString(KEY_SERVERS, null);

        if (serverString != null && serverString.length() > 0) {
            for (String serverId : serverString.split(" ")) {
                result.add(Integer.parseInt(serverId));
            }
        }
        return result;
    }

    public void deleteServer(int id) {
        SortedSet<Integer> serverIds = getServerIds();
        serverIds.remove(id);
        saveServerIds(serverIds);

        if (getActiveServer().getId() == id) {
            setActiveServerId(getServerIds().first());
        }
    }

    public static class ServerSettings {
        private final int id;
        private final String name;
        private final String url;
        private final String username;
        private final String password;

        public ServerSettings(int id, String name, String url, String username, String password) {
            this.id = id;
            this.name = name;
            this.url = url;
            this.username = username;
            this.password = password;
        }

        private ServerSettings(Context context, int id) {
            SharedPreferences prefs = Util.getPreferences(context);
            this.id = id;
            this.name = prefs.getString(getNameKey(), null);
            this.url = prefs.getString(getUrlKey(), null);
            this.username = prefs.getString(getUsernameKey(), null);
            this.password = prefs.getString(getPasswordKey(), null);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getNameKey() {
            return "server.name." + id;
        }

        public String getUrlKey() {
            return "server.url." + id;
        }

        public String getUsernameKey() {
            return "server.username." + id;
        }

        public String getPasswordKey() {
            return "server.password." + id;
        }

        public void save(Context context) {
            SharedPreferences prefs = Util.getPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getNameKey(), getName());
            editor.putString(getUrlKey(), getUrl());
            editor.putString(getUsernameKey(), getUsername());
            editor.putString(getPasswordKey(), getPassword());
            editor.commit();
        }
    }
}
