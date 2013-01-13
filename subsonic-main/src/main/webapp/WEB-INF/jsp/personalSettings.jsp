<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.PersonalSettingsCommand"--%>

<html><head>
    <%@ include file="head.jspf" %>
</head>

<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="personal"/>
    <c:param name="restricted" value="${not command.user.adminRole}"/>
</c:import>

<h2><fmt:message key="personalsettings.title"><fmt:param>${command.user.username}</fmt:param></fmt:message></h2>

<fmt:message key="common.default" var="defaultLabel"/>
<form:form method="post" action="personalSettings.view" commandName="command">

    <table style="white-space:nowrap" class="indent">

        <tr>
            <td><fmt:message key="personalsettings.language"/></td>
            <td>
                <form:select path="localeIndex" cssStyle="width:15em">
                    <form:option value="-1" label="${defaultLabel}"/>
                    <c:forEach items="${command.locales}" var="locale" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${locale}"/>
                    </c:forEach>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="language"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="personalsettings.theme"/></td>
            <td>
                <form:select path="themeIndex" cssStyle="width:15em">
                    <form:option value="-1" label="${defaultLabel}"/>
                    <c:forEach items="${command.themes}" var="theme" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${theme.name}"/>
                    </c:forEach>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="theme"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Album sorting</td>
        	<td>
        		<form:select path="albumOrderByYear" cssStyle="width:15em">
        			<form:option value="true" label="By year"/>
        			<form:option value="false" label="By name"/>
        		</form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="albumorderbyyear"/></c:import>
            </td>
        </tr>
        
        <tr>
        	<td>Album ordering</td>
        	<td>
        		<form:select path="albumOrderAscending" cssStyle="width:15em">
        			<form:option value="true" label="Ascending"/>
        			<form:option value="false" label="Descending"/>
        		</form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="albumorderascending"/></c:import>
            </td>
        </tr>
        
        <tr>
        	<td>Default home view</td>
        	<td>
        		<form:select path="defaultHomeView" cssStyle="width:15em">
        			<form:option value="newest" label="Recently added"/>
        			<form:option value="recent+Artists" label="Recently played - Artists"/>
        			<form:option value="recent+Albums" label="Recently played - Albums"/>
        			<form:option value="recent+Songs" label="Recently played - Songs"/>
        			<form:option value="frequent+Artists" label="Frequently played - Artists"/>
        			<form:option value="frequent+Albums" label="Frequently played - Albums"/>
        			<form:option value="frequent+Songs" label="Frequently played - Songs"/>
        			<form:option value="starred+Artists" label="Starred - Artists"/>
        			<form:option value="starred+Albums" label="Starred - Albums"/>
        			<form:option value="starred+Songs" label="Starred - Songs"/>
        			<form:option value="topartists+3month" label="Top artists - Three months"/>
        			<form:option value="topartists+6month" label="Top artists - Six months"/>
        			<form:option value="topartists+12month" label="Top artists - Twelve months"/>
        			<form:option value="topartists+overall" label="Top artists - Overall"/>
        			<form:option value="random+Artists" label="Random - Artists"/>
        			<form:option value="random+Albums" label="Random - Albums"/>
        			<form:option value="random+Songs" label="Random - Songs"/>
        			<form:option value="recommended" label="Recommended"/>
        		</form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="defaulthomeview"/></c:import>
            </td>
        </tr>
		
        <tr>
            <td>Default home statistics</td>
            <td>
                <form:select path="viewStatsForAllUsers" cssStyle="width:15em">
                    <form:option value="false" label="Show only my activity"/>
                    <form:option value="true" label="Show activity for all users"/>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="viewstatsforallusers"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Default home artists</td>
        	<td>
				<form:input path="defaultHomeArtists" size="3"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="defaulthomeartists"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Default home albums</td>
        	<td>
				<form:input path="defaultHomeAlbums" size="3"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="defaulthomealbums"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Default home songs</td>
        	<td>
				<form:input path="defaultHomeSongs" size="3"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="defaulthomesongs"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Artist grid width</td>
        	<td>
				<form:input path="artistGridWidth" size="3"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="artistgridwidth"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Album grid layout</td>
        	<td>
        		<form:select path="albumGridLayout">
        			<form:option value="true" label="Yes"/>
        			<form:option value="false" label="No"/>
        		</form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="albumgridlayout"/></c:import>
            </td>
        </tr>
        <tr>
        	<td>Related artists</td>
        	<td>
                <form:select path="relatedArtists">
                    <c:forTokens items="8 9 10 11 12 13 14 15 16 17 18 19 20 21" delims=" " var="i">
                        <form:option value="${i}" label="${i}"/>
                    </c:forTokens>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="relatedartists"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Recommended artists</td>
        	<td>
                <form:select path="recommendedArtists">
                    <c:forTokens items="3 4 5 6 7 8 9 10" delims=" " var="i">
                        <form:option value="${i}" label="${i}"/>
                    </c:forTokens>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="recommendedartists"/></c:import>
            </td>
        </tr>
		
        <tr>
        	<td>Reluctant artist loading</td>
        	<td>
        		<form:select path="reluctantArtistLoading">
        			<form:option value="false" label="No"/>
        			<form:option value="true" label="Yes"/>
        		</form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="reluctantartistloading"/></c:import>
            </td>
        </tr>

        <tr>
        	<td>Album artist filter</td>
        	<td>
        		<form:select path="onlyAlbumArtistRecommendations">
        			<form:option value="false" label="No"/>
        			<form:option value="true" label="Yes"/>
        		</form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="onlyalbumartistrecommendations"/></c:import>
            </td>
        </tr>
        
        <tr>
            <td>Use Various Artists shortlist</td>
            <td>
                <form:select path="useVariousArtistsShortlist">
                    <form:option value="false" label="No"/>
                    <form:option value="true" label="Yes"/>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="usevariousartistsshortlist"/></c:import>
            </td>
        </tr>
    </table>

    <table class="indent">
        <tr>
            <th style="padding:0 0.5em 0.5em 0;text-align:left;"><fmt:message key="personalsettings.display"/></th>
            <th style="padding:0 0.5em 0.5em 0.5em;text-align:center;"><fmt:message key="personalsettings.browse"/></th>
            <th style="padding:0 0 0.5em 0.5em;text-align:center;"><fmt:message key="personalsettings.playlist"/></th>
            <th style="padding:0 0 0.5em 0.5em;text-align:center;">Home</th>
            <th style="padding:0 0 0.5em 0.5em">
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="visibility"/></c:import>
            </th>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.tracknumber"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.trackNumberVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.trackNumberVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.trackNumberVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.artist"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.artistVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.artistVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.artistVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.album"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.albumVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.albumVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.albumVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.composer"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.composerVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.composerVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.composerVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.genre"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.genreVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.genreVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.genreVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.year"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.yearVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.yearVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.yearVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.bitrate"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.bitRateVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.bitRateVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.bitRateVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.duration"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.durationVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.durationVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.durationVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.format"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.formatVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.formatVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.formatVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.filesize"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.fileSizeVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.fileSizeVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="homeVisibility.fileSizeVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.captioncutoff"/></td>
            <td style="text-align:center"><form:input path="mainVisibility.captionCutoff" size="3"/></td>
            <td style="text-align:center"><form:input path="playlistVisibility.captionCutoff" size="3"/></td>
            <td style="text-align:center"><form:input path="homeVisibility.captionCutoff" size="3"/></td>
        </tr>
    </table>

    <table class="indent">
        <tr>
            <td><form:checkbox path="showNowPlayingEnabled" id="nowPlaying" cssClass="checkbox"/></td>
            <td><label for="nowPlaying"><fmt:message key="personalsettings.shownowplaying"/></label></td>
            <td style="padding-left:2em"><form:checkbox path="showChatEnabled" id="chat" cssClass="checkbox"/></td>
            <td><label for="chat"><fmt:message key="personalsettings.showchat"/></label></td>
        </tr>
        <tr>
            <td><form:checkbox path="nowPlayingAllowed" id="nowPlayingAllowed" cssClass="checkbox"/></td>
            <td><label for="nowPlayingAllowed"><fmt:message key="personalsettings.nowplayingallowed"/></label></td>
            <td style="padding-left:2em"><form:checkbox path="partyModeEnabled" id="partyModeEnabled" cssClass="checkbox"/></td>
            <td><label for="partyModeEnabled"><fmt:message key="personalsettings.partymode"/></label>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="partymode"/></c:import>
            </td>
        </tr>
    </table>

	<c:if test="${command.lastFmEnabled}">
		<table class="indent">
			<tr>
				<td><form:checkbox path="lastFmEnabled" id="lastFm" cssClass="checkbox" /></td>
				<td><label for="lastFm">Scrobble tracks as user ${command.lastFmUsername}.</label></td>
			</tr>
		</table>
	</c:if>
    <div id="lastFmLink" class="forward" style="padding-left:2em">
		<a href="lastFmSettings.view">Configure last.fm scrobbling</a>
	</div>

    <p style="padding-top:1em;padding-bottom:1em">
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em"/>
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
    </p>

    <h2><fmt:message key="personalsettings.avatar.title"/></h2>

    <p style="padding-top:1em">
        <c:forEach items="${command.avatars}" var="avatar">
            <c:url value="avatar.view" var="avatarUrl">
                <c:param name="id" value="${avatar.id}"/>
            </c:url>
            <span style="white-space:nowrap;">
                <form:radiobutton id="avatar-${avatar.id}" path="avatarId" value="${avatar.id}"/>
                <label for="avatar-${avatar.id}"><img src="${avatarUrl}" alt="${avatar.name}" width="${avatar.width}" height="${avatar.height}" style="padding-right:2em;padding-bottom:1em"/></label>
            </span>
        </c:forEach>
    </p>
    <p>
        <form:radiobutton id="noAvatar" path="avatarId" value="-1"/>
        <label for="noAvatar"><fmt:message key="personalsettings.avatar.none"/></label>
    </p>
    <p>
        <form:radiobutton id="customAvatar" path="avatarId" value="-2"/>
        <label for="customAvatar"><fmt:message key="personalsettings.avatar.custom"/>
            <c:if test="${not empty command.customAvatar}">
                <sub:url value="avatar.view" var="avatarUrl">
                    <sub:param name="username" value="${command.user.username}"/>
                </sub:url>
                <img src="${avatarUrl}" alt="${command.customAvatar.name}" width="${command.customAvatar.width}" height="${command.customAvatar.height}" style="padding-right:2em"/>
            </c:if>
        </label>
    </p>
</form:form>

<form method="post" enctype="multipart/form-data" action="avatarUpload.view">
    <table>
        <tr>
            <td style="padding-right:1em"><fmt:message key="personalsettings.avatar.changecustom"/></td>
            <td style="padding-right:1em"><input type="file" id="file" name="file" size="40"/></td>
            <td style="padding-right:1em"><input type="submit" value="<fmt:message key="personalsettings.avatar.upload"/>"/></td>
        </tr>
    </table>
</form>

<p class="detail" style="text-align:right">
    <fmt:message key="personalsettings.avatar.courtesy"/>
</p>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.location.href="index.view?";
    </script>
</c:if>

</body></html>