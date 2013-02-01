<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
    <%@ include file="head.jspf" %>
    <script type="text/javascript" language="javascript">
        function hideGettingStarted() {
            alert("<fmt:message key="gettingStarted.hidealert"/>");
            location.href = "gettingStarted.view?hide";
        }
    </script>
</head>
<body class="mainframe bgcolor1">

<h1 style="padding-bottom:0.5em">
    <img src="<spring:theme code="homeImage"/>" alt="">
    <fmt:message key="gettingStarted.title"/>
</h1>

Welcome to your new Subsonic server, running the MusicCabinet plugin!<br>
Before you can use the new features, some configuration needs to be done.<br>

<table style="padding-top:1em;padding-bottom:2em;width:60%">
    <tr>
        <td style="font-size:26pt;padding:20pt">1</td>
        <td>
            <div style="font-size:14pt;padding-top:20pt"><a href="http://www.postgresql.org/download/" target="_new">Install PostgreSQL, version 9.1 or above &raquo;</a></div>
            <div style="padding-top:5pt">PostgreSQL is a database, used to store meta-data about your music library. During the installation:
            	<ul>
            		<li>create a user called <b>postgres</b>, when asked to.</li>
            		<li>use port 5432, when asked to.</li>
            		<li>select the default locale, when asked to.</li>
            	</ul>
            	Then make sure it is running as a service. Simple as that.
            </div>
        </td>
    </tr>
    <tr>
        <td style="font-size:26pt;padding:20pt">2</td>
        <td>
            <div style="font-size:14pt;padding-top:20pt"><a href="musicCabinetSettings.view">Configure MusicCabinet &raquo;</a></div>
            <div style="padding-top:5pt">Tell MusicCabinet the password you selected for user <b>postgres</b> from step 1, and then start the automatic import of meta-data about your library!<br><br>The process is pretty straight-forward, but the import might be slow, depending on the size of your library. (<a href="http://last.fm" target="_new">Last.fm</a> kindly supplies all the musical knowledge for free, but they have some limitations on data traffic.)<br><br>To give you an idea, a library of 10.000 tracks would take maybe 30 minutes to finish. About 50 MB of disk space would be used for storage.</div>
        </td>
    </tr>
    <tr>
        <td style="font-size:26pt;padding:20pt">3</td>
        <td>
            <div style="font-size:14pt;padding-top:20pt"><a href="lastFmSettings.view">Set up a last.fm account &raquo;</a></div>
            <div style="padding-top:5pt">MusicCabinet heavily depends on information fetched from last.fm. To get personal music recommendations, keep track of play statistics, starred songs and much more, you need a last.fm account. It's free and anonymous.</div>
        </td>
    </tr>

</table>

<div class="forward"><a href="javascript:hideGettingStarted()"><fmt:message key="gettingStarted.hide"/></a></div>

</body></html>