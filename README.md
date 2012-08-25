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

The build process assumes that you have Java 7 or later and Maven installed, and that PostgreSQL is running.

    Clone git@github.com:hakko/musiccabinet.git to $workspace/musiccabinet
    Update your PostgreSQL password in $workspace/musiccabinet/musiccabinet-server/src/main/resources/local.jdbc.properties
    cd $workspace/musiccabinet/musiccabinet-server
    mvn compile
    mvn exec:java -Dexec.mainClass=com.github.hakko.musiccabinet.service.DatabaseAdministrationService
    mvn install

    Download http://dilerium.se/musiccabinet/dwr-3.0.0-rc1.jar (this is not available from public repos)
    mvn install:install-file -Dfile=dwr-3.0.0-rc1.jar -DgroupId=org.directwebremoting -DartifactId=dwr -Dversion=3.0.0-rc1 -Dpackaging=jar

    Clone git@github.com:hakko/subsonic.git to $workspace/subsonic.
    cd $workspace/subsonic/subsonic-main
    mvn package
    cd $workspace/subsonic/subsonic-booter
    mvn package


Installation
------------

Installation assumes that you have previously installed Subsonic from http://subsonic.org.

1. Stop your Subsonic service
2. Make a backup of current settings, just in case
3. Use the subsonic.war file from $workspace/subsonic/subsonic-main/target/subsonic.war, and replace your current one.
4. Use the subsonic-booter-jar-with-dependencies.jar file from $workspace/subsonic/subsonic-booter/target/subsonic-booter-jar-with-dependencies.jar, and replace your current one.
5. Start your Subsonic service

Log in to Subsonic as usual and click the "Configure MusicCabinet" link (the header). It should be pretty self-explanatory from there.

Please note that the initial import of data from last.fm will take a while, roughly 30 minutes per 10.000 tracks. You can follow the progress meanwhile but MusicCabinet features won't work until it's finished.