Subsonic
========

Subsonic is a web-based music streaming service, created by Sindre Mehus and released under the GPL.

This project is forked from Subsonic version 4.6, adding extensive last.fm integration through the MusicCabinet library. See https://github.com/hakko/musiccabinet for details.

There's a static demo of the features added at http://dilerium.se/musiccabinet/demo.htm.

Pre-requisites
--------------

To use this, you need a PostgreSQL database running on the same host as your Subsonic server.
There are pre-built binary packages available for most platforms at http://www.postgresql.org/download/.
During install:
* you'll be asked to create a database user. Stick to the default name, "postgres".
* you'll be asked to choose a port number. Stick to the default value, "5432".

Building
--------

The build process assumes that you have Java 1.6 or later and Maven installed, and that PostgreSQL is running.

    Clone https://github.com/hakko/musiccabinet to $workspace/musiccabinet
    cd $workspace/musiccabinet
    mvn exec:java -Dexec.mainClass=com.github.hakko.musiccabinet.service.DatabaseAdministrationService
    mvn install

    Clone https://github.com/hakko/subsonic to $workspace/subsonic.
    cd $workspace/subsonic/subsonic-main
    mvn package

Installation
------------

Installation assumes that you have previously installed Subsonic from http://subsonic.org.

1. Stop your Subsonic service
2. Make a backup of current settings, just in case
3. Use the subsonic.war file from $workspace/subsonic/subsonic-main/subsonic.war, and replace your current one.
4. Start your Subsonic service

Log in to Subsonic as usual and click the "Configure MusicCabinet" link (the header). It should be pretty self-explanatory from there.

Please note that the initial import of data from last.fm will take a while, roughly 30 minutes per 10.000 tracks. You can follow the progress meanwhile but MusicCabinet features won't work until it's finished.