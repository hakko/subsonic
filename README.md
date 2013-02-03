Subsonic
========

Subsonic is a web-based music streaming service, created by Sindre Mehus and released under the GPL.

This project is forked from Subsonic version 4.6, adding extensive last.fm integration through the MusicCabinet library. See https://github.com/hakko/musiccabinet for details.

There's an introduction to the project at http://dilerium.se/musiccabinet.

Pre-requisites
--------------

To use this add-on, you need a PostgreSQL database running on the same host as your Subsonic server.
There are pre-built binary packages available for most platforms at http://www.postgresql.org/download/.
During install:
* you'll be asked to create a database user. Stick to the default name, "postgres".
* you'll be asked to choose a port number. Stick to the default value, "5432".

You also need Java 7. Uninstalling Java 6 is a good idea, unless you don't explicitly need it.

Building
--------

The build process assumes that you have Java 7 or later and Maven 3 installed, and that PostgreSQL is running.

    Clone git@github.com:hakko/musiccabinet.git to $workspace/musiccabinet
    Update your PostgreSQL password in $workspace/musiccabinet/musiccabinet-server/src/main/resources/local.jdbc.properties
    cd $workspace/musiccabinet/musiccabinet-server
    mvn compile
    mvn exec:java -Dexec.mainClass=com.github.hakko.musiccabinet.service.DatabaseAdministrationService
    mvn install

    Clone git@github.com:hakko/subsonic.git to $workspace/subsonic.
    cd $workspace/subsonic
    mvn install
    cd $workspace/subsonic/subsonic-booter
    mvn install

    cd $workspace/subsonic/subsonic-installer-standalone
    mvn package

To also build a .war file that runs on a Tomcat server, execute:

    cd $workspace/subsonic/subsonic-main
    mvn -P tomcat package

If a test case fails during build, please report and re-run with mvn -fn as a workaround.

Installation
------------

1. If you're already running Subsonic: stop your service, and backup your settings. (Just in case.)
2. Go to $workspace/subsonic/subsonic-installer-standalone/target.
3. Unzip subsonic-installer-standalone.zip to preferred install directory.
4. In the install directory, tweak and run the subsonic.sh script to start your new server.

Log in to Subsonic as usual (localhost:4040, as admin/admin) and click the "Configure MusicCabinet" link (the header). It should be pretty self-explanatory from there.

Please note that the initial import of data from last.fm will take a while, roughly 30 minutes per 10.000 tracks. You can follow the progress meanwhile but MusicCabinet features won't work until it's finished.
