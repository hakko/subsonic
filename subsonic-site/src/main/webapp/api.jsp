<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
<jsp:include page="menu.jsp">
    <jsp:param name="current" value="api"/>
</jsp:include>

<div id="content">
<div id="main-col">
<h1>Subsonic API</h1>

<p>
    The Subsonic API allows anyone to build their own programs using Subsonic as the media server, whether they're
    on the web, the desktop or on mobile devices. All the Subsonic <a href="apps.jsp">apps</a> are built using the
    Subsonic API.
</p>
<p>
    Feel free to join the <a href="http://groups.google.com/group/subsonic-app-developers">Subsonic App Developers</a> group
    for discussions, suggestions and questions.
</p>

<h2 class="div">Introduction</h2>

<p>
    The Subsonic API allows you to call methods that respond in <a
        href="http://en.wikipedia.org/wiki/Representational_State_Transfer">REST</a> style xml.
    Individual methods are detailed below.
</p>

<p>
    Please note that all methods take the following parameters:
</p>

<table width="100%" class="bottomspace">
    <tr>
        <th class="param-heading">Parameter</th>
        <th class="param-heading">Required</th>
        <th class="param-heading">Default</th>
        <th class="param-heading">Comment</th>
    </tr>
    <tr class="table-altrow">
        <td><code>u</code></td>
        <td>Yes</td>
        <td></td>
        <td>The username.</td>
    </tr>
    <tr>
        <td><code>p</code></td>
        <td>Yes</td>
        <td></td>
        <td>The password, either in clear text or hex-encoded with a "enc:" prefix.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>v</code></td>
        <td>Yes</td>
        <td></td>
        <td>The protocol version implemented by the client, i.e., the version of the
            <code>subsonic-rest-api.xsd</code> schema used (see below).</td>
    </tr>
    <tr>
        <td><code>c</code></td>
        <td>Yes</td>
        <td></td>
        <td>A unique string identifying the client application.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>f</code></td>
        <td>No</td>
        <td>xml</td>
        <td>Request data to be returned in this format. Supported values are "xml", "json" (since <a href="#versions">1.4.0</a>)
            and "jsonp" (since <a href="#versions">1.6.0</a>). If using jsonp, specify name of javascript callback function using
            a <code>callback</code> parameter.</td>
    </tr>
</table>

<p>
    For example:
</p>

<p>
    <code>http://your-server/rest/getIndexes.view?u=joe&amp;p=sesame&amp;v=1.1.0&amp;c=myapp</code>, or<br/>
    <code>http://your-server/rest/getIndexes.view?u=joe&amp;p=enc:736573616d65&amp;v=1.1.0&amp;c=myapp</code>
</p>

<p>
    Starting with API version <a href="#versions">1.2.0</a> it is no longer necessary to send the username and password as part of the URL.
    Instead, HTTP <a href="http://en.wikipedia.org/wiki/Basic_access_authentication">Basic</a> authentication could be
    used.
    (Only <em>preemptive</em> authentication is supported, meaning that the credentials should be supplied by the client
    without being challenged for it.)
</p>

<p>
    Note that UTF-8 should be used when sending parameters to API methods. The XML returned
    will also be encoded with UTF-8.
</p>

<p>
    All methods (except those that return binary data) returns XML documents conforming to the
    <code>subsonic-rest-api.xsd</code> schema.
</p>

<h2 class="div">Error handling</h2>

<p>
    If a method fails it will return an error code and message in an <code>&lt;error&gt;</code> element.
    In addition, the <code>status</code> attribute of the <code>&lt;subsonic-response&gt;</code> root element
    will be set to <code>failed</code> instead of <code>ok</code>. For example:
</p>

            <pre>
   &lt;?xml version="1.0" encoding="UTF-8"?&gt;
   &lt;subsonic-response xmlns="http://subsonic.org/restapi"
                      status="failed" version="1.1.0"&gt;
       &lt;error code="40" message="Wrong username or password"/&gt;
   &lt;/subsonic-response&gt;
            </pre>

<p>
    The following error codes are defined:
</p>
<table width="100%" class="bottomspace">
    <tr>
        <th class="param-heading">Code</th>
        <th class="param-heading">Description</th>
    </tr>
    <tr class="table-altrow">
        <td><code>0</code></td>
        <td>A generic error.</td>
    </tr>
    <tr>
        <td><code>10</code></td>
        <td>Required parameter is missing.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>20</code></td>
        <td>Incompatible Subsonic REST protocol version. Client must upgrade.</td>
    </tr>
    <tr>
        <td><code>30</code></td>
        <td>Incompatible Subsonic REST protocol version. Server must upgrade.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>40</code></td>
        <td>Wrong username or password.</td>
    </tr>
    <tr>
        <td><code>50</code></td>
        <td>User is not authorized for the given operation.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>60</code></td>
        <td>The trial period for the Subsonic server is over. Please upgrade to Subsonic Premium. Visit subsonic.org for details.</td>
    </tr>
    <tr>
        <td><code>70</code></td>
        <td>The requested data was not found.</td>
    </tr>
</table>

<h2 class="div"><a name="versions"></a>Versions</h2>

<p>
    This table shows the REST API version implemented in different Subsonic versions:
</p>
<table width="50%" class="bottomspace">
    <tr>
        <th class="param-heading">Subsonic version</th>
        <th class="param-heading">REST API version</th>
    </tr>
    <tr class="table-altrow">
        <td>4.9</td>
        <td><a href="https://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.9/subsonic-site/src/main/webapp/inc/api/subsonic-rest-api.xsd">1.10.2</a></td>
    </tr>
    <tr>
        <td>4.8</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.8/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.9.0</a></td>
    </tr>
    <tr class="table-altrow">
        <td>4.7</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.7/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.8.0</a></td>
    </tr>
    <tr>
        <td>4.6</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.6/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.7.0</a></td>
    </tr>
    <tr class="table-altrow">
        <td>4.5</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.5/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.6.0</a></td>
    </tr>
    <tr>
        <td>4.4</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.4/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.5.0</a></td>
    </tr>
    <tr class="table-altrow">
        <td>4.2</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.2/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.4.0</a></td>
    </tr>
    <tr>
        <td>4.1</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.1.beta1/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.3.0</a></td>
    </tr>
    <tr class="table-altrow">
        <td>4.0</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-4.0/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.2.0</a></td>
    </tr>
    <tr>
        <td>3.9</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-3.9/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.1.1</a></td>
    </tr>
    <tr class="table-altrow">
        <td>3.8</td>
        <td><a href="http://sourceforge.net/p/subsonic/code/HEAD/tree/tags/release-3.8/subsonic-main/src/main/webapp/xsd/subsonic-rest-api.xsd">1.1.0</a></td>
    </tr>
</table>
<p>
    Note that a Subsonic server is backward compatible with a REST client if and only if the major version is the same,
    and the minor version of the client is less than or equal to the server's. For example, if the server has
    REST API version 2.2, it supports client versions 2.0, 2.1 and 2.2, but not versions 1.x, 2.3+ or 3.x. The third
    part of the version number is not used to determine compatibility.
</p>

<h2 class="div">File structure vs ID3 tags</h2>

<p>
    Starting with version <a href="#versions">1.8.0</a>, the API provides methods for accessing the media collection
    organized according to ID3 tags, rather than file structure.
</p>
<p>
    For instance, browsing through the collection using ID3 tags should use the <code>getArtists</code>, <code>getArtist</code>
    and <code>getAlbum</code> methods.
    To browse using file structure you would use <code>getIndexes</code> and <code>getMusicDirectory</code>.
</p>
<p>
    Correspondingly, there are two sets of methods for searching, starring and album lists. Refer to the method
    documentation for details.
</p>

<h1>API method documentation</h1>

<table width="100%" class="bottomspace">
    <tr class="table-altrow">
        <td>System</td>
        <td>
            <code><a href="#ping">ping</a></code>,
            <code><a href="#getLicense">getLicense</a></code>
        </td>
    </tr>
    <tr>
        <td>Browsing</td>
        <td>
            <code><a href="#getMusicFolders">getMusicFolders</a></code>,
            <code><a href="#getIndexes">getIndexes</a></code>,
            <code><a href="#getMusicDirectory">getMusicDirectory</a></code>,
            <code><a href="#getGenres">getGenres</a></code>,
            <code><a href="#getArtists">getArtists</a></code>,
            <code><a href="#getArtist">getArtist</a></code>,
            <code><a href="#getAlbum">getAlbum</a></code>,
            <code><a href="#getSong">getSong</a></code>,
            <code><a href="#getVideos">getVideos</a></code>
        </td>
    </tr>
    <tr class="table-altrow">
        <td>Album/song lists</td>
        <td>
            <code><a href="#getAlbumList">getAlbumList</a></code>,
            <code><a href="#getAlbumList2">getAlbumList2</a></code>,
            <code><a href="#getRandomSongs">getRandomSongs</a></code>,
            <code><a href="#getSongsByGenre">getSongsByGenre</a></code>,
            <code><a href="#getNowPlaying">getNowPlaying</a></code>,
            <code><a href="#getStarred">getStarred</a></code>,
            <code><a href="#getStarred2">getStarred2</a></code>
        </td>
    </tr>
    <tr>
        <td>Searching</td>
        <td>
            <code><a href="#search">search</a></code>,
            <code><a href="#search2">search2</a></code>,
            <code><a href="#search3">search3</a></code>
        </td>
    </tr>
    <tr class="table-altrow">
        <td>Playlists</td>
        <td>
            <code><a href="#getPlaylists">getPlaylists</a></code>,
            <code><a href="#getPlaylist">getPlaylist</a></code>,
            <code><a href="#createPlaylist">createPlaylist</a></code>,
            <code><a href="#updatePlaylist">updatePlaylist</a></code>,
            <code><a href="#deletePlaylist">deletePlaylist</a></code>
        </td>
    </tr>
    <tr>
        <td>Media retrieval</td>
        <td>
            <code><a href="#stream">stream</a></code>,
            <code><a href="#download">download</a></code>,
            <code><a href="#hls">hls</a></code>,
            <code><a href="#getCoverArt">getCoverArt</a></code>,
            <code><a href="#getLyrics">getLyrics</a></code>,
            <code><a href="#getAvatar">getAvatar</a></code>
        </td>
    </tr>
    <tr class="table-altrow">
        <td>Media annotation</td>
        <td>
            <code><a href="#star">star</a></code>,
            <code><a href="#unstar">unstar</a></code>,
            <code><a href="#setRating">setRating</a></code>,
            <code><a href="#scrobble">scrobble</a></code>
        </td>
    </tr>
    <tr>
        <td>Sharing</td>
        <td>
            <code><a href="#getShares">getShares</a></code>,
            <code><a href="#createShare">createShare</a></code>,
            <code><a href="#updateShare">updateShare</a></code>,
            <code><a href="#deleteShare">deleteShare</a></code>
        </td>
    </tr>
    <tr class="table-altrow">
        <td>Podcast</td>
        <td>
            <code><a href="#getPodcasts">getPodcasts</a></code>
            <code><a href="#refreshPodcasts">refreshPodcasts</a></code>
            <code><a href="#createPodcastChannel">createPodcastChannel</a></code>
            <code><a href="#deletePodcastChannel">deletePodcastChannel</a></code>
            <code><a href="#deletePodcastEpisode">deletePodcastEpisode</a></code>
            <code><a href="#downloadPodcastEpisode">downloadPodcastEpisode</a></code>
        </td>
    </tr>
    <tr>
        <td>Jukebox</td>
        <td>
            <code><a href="#jukeboxControl">jukeboxControl</a></code>
        </td>
    </tr>
    <tr class="table-altrow">
        <td>Internet radio</td>
        <td>
            <code><a href="#getInternetRadioStations">getInternetRadioStations</a></code>
        </td>
    </tr>
    <tr>
        <td>Chat</td>
        <td>
            <code><a href="#getChatMessages">getChatMessages</a></code>,
            <code><a href="#addChatMessage">addChatMessage</a></code>
        </td>
    </tr>
    <tr class="table-altrow">
        <td style="white-space: nowrap; padding-right: 0.6em">User management</td>
        <td>
            <code><a href="#getUser">getUser</a></code>,
            <code><a href="#getUsers">getUsers</a></code>,
            <code><a href="#createUser">createUser</a></code>,
            <code><a href="#updateUser">updateUser</a></code>,
            <code><a href="#deleteUser">deleteUser</a></code>,
            <code><a href="#changePassword">changePassword</a></code>
        </td>
    </tr>
    <tr>
        <td>Bookmarks</td>
        <td>
            <code><a href="#getBookmarks">getBookmarks</a></code>,
            <code><a href="#createBookmark">createBookmark</a></code>,
            <code><a href="#deleteBookmark">deleteBookmark</a></code>,
        </td>
    </tr>
</table>

<%@ include file="api-ping.jsp" %>
<%@ include file="api-getLicense.jsp" %>

<%@ include file="api-getMusicFolders.jsp" %>
<%@ include file="api-getIndexes.jsp" %>
<%@ include file="api-getMusicDirectory.jsp" %>
<%@ include file="api-getGenres.jsp" %>
<%@ include file="api-getArtists.jsp" %>
<%@ include file="api-getArtist.jsp" %>
<%@ include file="api-getAlbum.jsp" %>
<%@ include file="api-getSong.jsp" %>
<%@ include file="api-getVideos.jsp" %>

<%@ include file="api-getAlbumList.jsp" %>
<%@ include file="api-getAlbumList2.jsp" %>
<%@ include file="api-getRandomSongs.jsp" %>
<%@ include file="api-getSongsByGenre.jsp" %>
<%@ include file="api-getNowPlaying.jsp" %>
<%@ include file="api-getStarred.jsp" %>
<%@ include file="api-getStarred2.jsp" %>

<%@ include file="api-search.jsp" %>
<%@ include file="api-search2.jsp" %>
<%@ include file="api-search3.jsp" %>

<%@ include file="api-getPlaylists.jsp" %>
<%@ include file="api-getPlaylist.jsp" %>
<%@ include file="api-createPlaylist.jsp" %>
<%@ include file="api-updatePlaylist.jsp" %>
<%@ include file="api-deletePlaylist.jsp" %>

<%@ include file="api-stream.jsp" %>
<%@ include file="api-download.jsp" %>
<%@ include file="api-hls.jsp" %>
<%@ include file="api-getCoverArt.jsp" %>
<%@ include file="api-getLyrics.jsp" %>
<%@ include file="api-getAvatar.jsp" %>

<%@ include file="api-star.jsp" %>
<%@ include file="api-unstar.jsp" %>
<%@ include file="api-setRating.jsp" %>
<%@ include file="api-scrobble.jsp" %>

<%@ include file="api-getShares.jsp" %>
<%@ include file="api-createShare.jsp" %>
<%@ include file="api-updateShare.jsp" %>
<%@ include file="api-deleteShare.jsp" %>

<%@ include file="api-getPodcasts.jsp" %>
<%@ include file="api-refreshPodcasts.jsp" %>
<%@ include file="api-createPodcastChannel.jsp" %>
<%@ include file="api-deletePodcastChannel.jsp" %>
<%@ include file="api-deletePodcastEpisode.jsp" %>
<%@ include file="api-downloadPodcastEpisode.jsp" %>

<%@ include file="api-jukeboxControl.jsp" %>

<%@ include file="api-getInternetRadioStations.jsp" %>

<%@ include file="api-getChatMessages.jsp" %>
<%@ include file="api-addChatMessage.jsp" %>

<%@ include file="api-getUser.jsp" %>
<%@ include file="api-getUsers.jsp" %>
<%@ include file="api-createUser.jsp" %>
<%@ include file="api-updateUser.jsp" %>
<%@ include file="api-deleteUser.jsp" %>
<%@ include file="api-changePassword.jsp" %>

<%@ include file="api-getBookmarks.jsp" %>
<%@ include file="api-createBookmark.jsp" %>
<%@ include file="api-deleteBookmark.jsp" %>

</div>

<div id="side-col">

    <%@ include file="google-translate.jsp" %>
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
