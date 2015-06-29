<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <jsp:include page="menu.jsp">
        <jsp:param name="current" value="documentation"/>
    </jsp:include>

    <div id="content">
        <div id="main-col">
            <h1 class="bottomspace">Subsonic Change Log</h1>

            <a name="4.9"><h2 class="div">Subsonic 4.9 - Jan 25, 2014</h2></a>
            <ul>
                <li><span class="bugid">New: </span>Rename "Download" to "Download all"</li>
                <li><span class="bugid">New: </span>Rename "More actions..." to "Selected songs..."</li>
                <li><span class="bugid">New: </span>When playing a song, queue only later songs (not earlier).</li>
                <li><span class="bugid">New: </span>Added button for downloading single files in left frame.</li>
                <li><span class="bugid">New: </span>Updated Japanese translation, courtesy of Kenji Maekawa.</li>
                <li><span class="bugid">Bugfix: </span>Changing artist for files with ID3v1 tags was broken.</li>
                <li><span class="bugid">Bugfix: </span>Play/add buttons for single files in left frame didn't work.</li>
                <li><span class="bugid">Bugfix: </span>Thumbs in external player was sometimes missing.</li>
                <li><span class="bugid">Bugfix: </span>Fixed layout bug in podcast page.</li>
                <li><span class="bugid">Bugfix: </span>Trim podcast urls.</li>
            </ul>

            <a name="4.9.beta4"><h2 class="div">Subsonic 4.9.beta4 - Jan 04, 2014</h2></a>
            <ul>
                <li><span class="bugid">New: </span>Support playlist sharing on Facebook etc.</li>
                <li><span class="bugid">New: </span>Added "Play next" button to playlist view.</li>
                <li><span class="bugid">New: </span>Added "Play all" to starred songs.</li>
                <li><span class="bugid">New: </span>Added "Save as playlist" to starred songs.</li>
                <li><span class="bugid">New: </span>Show cover art for starred albums.</li>
                <li><span class="bugid">New: </span>Show album/song count in genre list.</li>
                <li><span class="bugid">New: </span>When playing a song from a playlist, queue other songs in that playlist.</li>
                <li><span class="bugid">New: </span>When clicking the play icon for a song, add whole album to play queue.</li>
                <li><span class="bugid">New: </span>Clicking on album thumb in "Now playing" now opens album page.</li>
                <li><span class="bugid">New: </span>Cache generated album art.</li>
                <li><span class="bugid">Bugfix: </span>Don't create zip file when downloading a single song.</li>
                <li><span class="bugid">Bugfix: </span>Show cover art in the right order.</li>
                <li><span class="bugid">Bugfix: </span>Hande genres names with special characters (e.g., "R&amp;B").</li>
                <li><span class="bugid">Bugfix: </span>Fixed character encoding problems in some translations.</li>
                <li><span class="bugid">Bugfix: </span>Use locale-specific names for automatically created playlists.</li>
                <li><span class="bugid">REST: </span>Added album/song count in getGenres.</li>
            </ul>

            <a name="4.9.beta3"><h2 class="div">Subsonic 4.9.beta3 - Dec 09, 2013</h2></a>
            <ul>
                <li><span class="bugid">New: </span>Modernized web interface. Nicer fonts, colors and layout. Larger album art.</li>
                <li><span class="bugid">New: </span>Show thumbnails for "siebling" albums.</li>
                <li><span class="bugid">New: </span>Generate automatic album art.</li>
                <li><span class="bugid">New: </span>Replace Download button with Play next.</li>
                <li><span class="bugid">New: </span>Use max gain 0 dB in jukebox mode to avoid distortion caused by clipping.</li>
                <li><span class="bugid">New: </span>When playing a song, queue rest of album.</li>
                <li><span class="bugid">New: </span>Added album lists "By decade" and "By genre".</li>
                <li><span class="bugid">New: </span>Support playlists in DLNA.</li>
                <li><span class="bugid">New: </span>DLNA now browses by folder, not tags.</li>
                <li><span class="bugid">New: </span>Updated Dutch translation, courtesy of Toolman.</li>
                <li><span class="bugid">Bugfix: </span>Fixed DLNA song duration and album art.</li>
                <li><span class="bugid">Bugfix: </span>Newly added media was sometimes not picked up when scanning libraries.</li>
                <li><span class="bugid">Bugfix: </span>Fixed font problem when using https.</li>
                <li><span class="bugid">Bugfix: </span>Validate input in Settings &gt; Internet TV/radio.</li>
                <li><span class="bugid">Bugfix: </span>Honor "subsonic.host" system property when resolving local IP address.</li>
                <li><span class="bugid">Bugfix: </span>Playlist management was broken on Tomcat 7.</li>
                <li><span class="bugid">Bugfix: </span>Settings &gt; Users was broken on Tomcat 7.</li>
                <li><span class="bugid">Bugfix: </span>Settings &gt; Personal was broken on Tomcat 7.</li>
                <li><span class="bugid">Bugfix: </span>When clicking "Create new playlist", automatically expand list of playlists.</li>
                <li><span class="bugid">REST: </span>Add year and genre to albums.</li>
                <li><span class="bugid">REST: </span>Added "byYear" and "byGenre" to getAlbumList and getAlbumList2.</li>
                <li><span class="bugid">REST: </span>Add "bookmarkPosition" to songs.</li>
                <li><span class="bugid">Performance: </span>Fixed slow queries on large media collections (&gt;100,000 files): get starred, get files in playlist, get genres.</li>
            </ul>

            <a name="4.9.beta2"><h2 class="div">Subsonic 4.9.beta2 - Nov 16, 2013</h2></a>
            <ul>
                <li><span class="bugid">New: </span>Use modern icons and fonts.</li>
                <li><span class="bugid">New: </span>Added light icons for dark themes.</li>
                <li><span class="bugid">New: </span>Added Estonian translation, courtesy of Olav M&auml;gi.</li>
                <li><span class="bugid">Bugfix: </span>Proper support for album artist.</li>
                <li><span class="bugid">Bugfix: </span>Remember DLNA settings when restarting server.</li>
                <li><span class="bugid">Bugfix: </span>Play next/last popup menu was unreadable in some themes.</li>
                <li><span class="bugid">Bugfix: </span>Created signed installer for Mac OS.</li>
                <li><span class="bugid">Bugfix: </span>Set ID3 title tag based on Podcast episode name.</li>
                <li><span class="bugid">REST: </span>Created updateUser method.</li>
                <li><span class="bugid">REST: </span>Added "starred" to "Directory" and "Artist" in xsd.</li>
            </ul>

            <a name="4.9.beta1"><h2 class="div">Subsonic 4.9.beta1 - Oct 12, 2013</h2></a>
            <ul>
                <li><span class="bugid">New: </span>Added a DLNA/UPnP Media Server. This feature is experimental and might not work with all DLNA clients.</li>
                <li><span class="bugid">New: </span>Added "Play next" option.</li>
                <li><span class="bugid">New: </span>Updated Estonian translation, courtesy of Olav M&auml;gi.</li>
                <li><span class="bugid">REST: </span>Escape special characters in XML for getGenres.</li>
                <li><span class="bugid">REST: </span>Added ignoredArticles to getIndexes.</li>
                <li><span class="bugid">Bugfix: </span>Improved lyrics search.</li>
                <li><span class="bugid">Bugfix: </span>Trim license key when registering.</li>
                <li><span class="bugid">Bugfix: </span>Fix download file names with special characters.</li>
                <li><span class="bugid">Bugfix: </span>Don't create a new player for each download.</li>
                <li><span class="bugid">Tech: </span>Make Subsonic work on Tomcat 7.</li>
                <li><span class="bugid">Tech: </span>Increased limit of internal database from 2 to 8 GB (fresh installs only).</li>
                <li><span class="bugid">Tech: </span>Set MIME type for cover arts.</li>
                <li><span class="bugid">Security: </span>Added CAPTCHA to password recovery page.</li>
            </ul>

            <a name="4.8"><h2 class="div">Subsonic 4.8 - Apr 20, 2013</h2></a>
            <ul>
                <li><span class="bugid">New: </span>Introduced <a href="premium.jsp">Subsonic Premium</a>. (Note: Existing licenses will remain valid)</li>
                <li><span class="bugid">New: </span>Re-import playlists if file timestamp has changed.</li>
                <li><span class="bugid">New: </span>Make playlist folder setting visible again.</li>
                <li><span class="bugid">New: </span>Changed bitrate to video resolution mapping.</li>
                <li><span class="bugid">New: </span>Added Norwegion Nynorsk translation, courtesy of Kevin Brubeck Unhammer.</li>
                <li><span class="bugid">New: </span>Updated Dutch translation, courtesy of W. van der Heijden.</li>
                <li><span class="bugid">New: </span>Updated German translation, courtesy of deejay2302.</li>
                <li><span class="bugid">New: </span>Updated French translation, courtesy of Yoann Spicher.</li>
                <li><span class="bugid">New: </span>Updated Simplified Chinese translation, courtesy of Zhenghao Zhu.</li>
                <li><span class="bugid">Bugfix: </span>Settings &gt; Network doesn't show error if a subsonic.org address is in use.</li>
                <li><span class="bugid">Bugfix: </span>Improved speed of tag editing.</li>
                <li><span class="bugid">Bugfix: </span>Ogg dates not always parsed properly.</li>
                <li><span class="bugid">Bugfix: </span>Sort songs by filename if track number is missing.</li>
                <li><span class="bugid">Bugfix: </span>Fix init exception in podcast bean.</li>
                <li><span class="bugid">Bugfix: </span>Links to minisub and apps icons doesn't honor context path.</li>
                <li><span class="bugid">Bugfix: </span>Less aggressive removal of track number from title.</li>
                <li><span class="bugid">Bugfix: </span>HLS broken with context path.</li>
                <li><span class="bugid">Bugfix: </span>Video player didn't require authentication.</li>
                <li><span class="bugid">Bugfix: </span>Download cover to replace in-metadata image results in renaming music file (".old").</li>
                <li><span class="bugid">REST: </span>Added Podcast methods.</li>
                <li><span class="bugid">REST: </span>Added bookmark methods.</li>
                <li><span class="bugid">REST: </span>Added getInternetRadioStations.</li>
                <li><span class="bugid">REST: </span>Added getGenres.</li>
                <li><span class="bugid">REST: </span>Added getSongsByGenre.</li>
                <li><span class="bugid">REST: </span>Added option to disable transcoding when streaming.</li>
                <li><span class="bugid">REST: </span>Fixed a bug in getAlbumList which caused it to return non-albums in some cases.</li>
                <li><span class="bugid">REST: </span>Support CORS.</li>
                <li><span class="bugid">REST: </span>Support "parent" attribute in getMusicDirectory.</li>
                <li><span class="bugid">Tech: </span>Install Java 7 rather than Java 6.</li>
            </ul>

            <%@ include file="changelog-older.jsp" %>


</div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
            <div class="sidebox">
                <h2>Releases</h2>
                <ul class="list">
                    <li><a href="#4.9">Subsonic 4.9</a></li>
                    <li><a href="#4.9.beta4">Subsonic 4.9.beta4</a></li>
                    <li><a href="#4.9.beta3">Subsonic 4.9.beta3</a></li>
                    <li><a href="#4.9.beta2">Subsonic 4.9.beta2</a></li>
                    <li><a href="#4.9.beta1">Subsonic 4.9.beta1</a></li>
                    <li><a href="#4.8">Subsonic 4.8</a></li>
                    <li><a href="#4.7">Subsonic 4.7</a></li>
                    <li><a href="#4.7.beta3">Subsonic 4.7.beta3</a></li>
                    <li><a href="#4.7.beta2">Subsonic 4.7.beta2</a></li>
                    <li><a href="#4.7.beta1">Subsonic 4.7.beta1</a></li>
                    <li><a href="#4.6">Subsonic 4.6</a></li>
                    <li><a href="#4.6.beta2">Subsonic 4.6.beta2</a></li>
                    <li><a href="#4.6.beta1">Subsonic 4.6.beta1</a></li>
                    <li><a href="#4.5">Subsonic 4.5</a></li>
                    <li><a href="#4.5.beta2">Subsonic 4.5.beta2</a></li>
                    <li><a href="#4.5.beta1">Subsonic 4.5.beta1</a></li>
                    <li><a href="#4.4">Subsonic 4.4</a></li>
                    <li><a href="#4.4.beta1">Subsonic 4.4.beta1</a></li>
                    <li><a href="#4.3">Subsonic 4.3</a></li>
                    <li><a href="#4.3.beta1">Subsonic 4.3.beta1</a></li>
                    <li><a href="#4.2">Subsonic 4.2</a></li>
                    <li><a href="#4.2.beta1">Subsonic 4.2.beta1</a></li>
                    <li><a href="#4.1">Subsonic 4.1</a></li>
                    <li><a href="#4.1.beta1">Subsonic 4.1.beta1</a></li>
                    <li><a href="#4.0.1">Subsonic 4.0.1</a></li>
                    <li><a href="#4.0">Subsonic 4.0</a></li>
                    <li><a href="#4.0.beta2">Subsonic 4.0.beta2</a></li>
                    <li><a href="#4.0.beta1">Subsonic 4.0.beta1</a></li>
                    <li><a href="#3.9">Subsonic 3.9</a></li>
                    <li><a href="#3.9.beta1">Subsonic 3.9.beta1</a></li>
                    <li><a href="#3.8">Subsonic 3.8</a></li>
                    <li><a href="#3.8.beta1">Subsonic 3.8.beta1</a></li>
                    <li><a href="#3.7">Subsonic 3.7</a></li>
                    <li><a href="#3.7.beta1">Subsonic 3.7.beta1</a></li>
                    <li><a href="#3.6">Subsonic 3.6</a></li>
                    <li><a href="#3.6.beta2">Subsonic 3.6.beta2</a></li>
                    <li><a href="#3.6.beta1">Subsonic 3.6.beta1</a></li>
                    <li><a href="#3.5">Subsonic 3.5</a></li>
                    <li><a href="#3.5.beta2">Subsonic 3.5.beta2</a></li>
                    <li><a href="#3.5.beta1">Subsonic 3.5.beta1</a></li>
                    <li><a href="#3.4">Subsonic 3.4</a></li>
                    <li><a href="#3.4">Subsonic 3.4.beta1</a></li>
                    <li><a href="#3.3">Subsonic 3.3</a></li>
                    <li><a href="#3.3.beta1">Subsonic 3.3.beta1</a></li>
                    <li><a href="#3.2">Subsonic 3.2</a></li>
                    <li><a href="#3.2.beta1">Subsonic 3.2.beta1</a></li>
                    <li><a href="#3.1">Subsonic 3.1</a></li>
                    <li><a href="#3.1.beta2">Subsonic 3.1.beta2</a></li>
                    <li><a href="#3.1.beta1">Subsonic 3.1.beta1</a></li>
                    <li><a href="#3.0">Subsonic 3.0</a></li>
                    <li><a href="#3.0.beta2">Subsonic 3.0.beta2</a></li>
                    <li><a href="#3.0.beta1">Subsonic 3.0.beta1</a></li>
                    <li><a href="#2.9">Subsonic 2.9</a></li>
                    <li><a href="#2.9.beta1">Subsonic 2.9.beta1</a></li>
                    <li><a href="#2.8">Subsonic 2.8</a></li>
                    <li><a href="#2.8.beta1">Subsonic 2.8.beta1</a></li>
                    <li><a href="#2.7">Subsonic 2.7</a></li>
                    <li><a href="#2.6">Subsonic 2.6</a></li>
                    <li><a href="#2.5">Subsonic 2.5</a></li>
                    <li><a href="#2.4">Subsonic 2.4</a></li>
                    <li><a href="#2.3">Subsonic 2.3</a></li>
                    <li><a href="#2.2">Subsonic 2.2</a></li>
                    <li><a href="#2.1">Subsonic 2.1</a></li>
                    <li><a href="#2.0">Subsonic 2.0</a></li>
                    <li><a href="#1.0">Subsonic 1.0</a></li>
                    <li><a href="#0.1">Subsonic 0.1</a></li>
                </ul>
            </div>

            <%@ include file="premium-column.jsp" %>

        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
