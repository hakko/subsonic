<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Subsonic &raquo; Free Music Streamer</title>
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/reset/reset.css">
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/fonts/fonts.css">
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/grid/grid.css">
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/base/base.css">
    <style type="text/css">
        a { text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body style="padding-left:5em; padding-top:1em; width:42em">

<div>
    <img src="inc/img/subsonic.png" alt="Subsonic"/>
</div>
<h1>Trial period expired</h1>

<p>
    Sorry, the trial period for the web address <b><%= request.getParameter("redirectFrom")%>.subsonic.org</b> has expired.
</p>

<p>
    Upgrade to <a href="premium.jsp">Subsonic Premium</a> to continue using the address, as well other extra features:
</p>
<ul>
    <li>A personal web address for your Subsonic server (<em>yourname</em>.subsonic.org).</li>
    <li><a href="apps.jsp">Apps</a> for Android, iPhone, Windows Phone, BlackBerry, Roku, Mac, Chrome and more.</li>
    <li>Video streaming.</li>
    <li>Podcast receiver.</li>
    <li>No ads in the Subsonic web interface.</li>
    <li>Play your media on compatible DLNA/UPnP devices.</li>
    <li>Share your media on Facebook, Twitter, Google+.</li>
    <li>Other features to be released later.</li>
</ul>

<p style="font-size:1.2em"><b><a href="premium.jsp">&raquo; Get Subsonic Premium</a></b></p>

<p>
    For more information, please visit <a href="http://subsonic.org/">subsonic.org</a>
</p>
</body>
</html>
