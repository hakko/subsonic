<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.GeneralSettingsCommand"--%>

<html><head>
    <%@ include file="head.jspf" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>

<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="general"/>
</c:import>

<form:form method="post" action="generalSettings.view" commandName="command">

    <table style="white-space:nowrap" class="indent">
        <tr>
            <td><fmt:message key="generalsettings.playlistfolder"/></td>
            <td>
                <form:input path="playlistFolder" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="playlistfolder"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.musicmask"/></td>
            <td>
                <form:input path="musicFileTypes" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="musicmask"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.videomask"/></td>
            <td>
                <form:input path="videoFileTypes" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="videomask"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.imagemask"/></td>
            <td>
                <form:input path="imageFileTypes" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="imagemask"/></c:import>
            </td>
        </tr>

        <tr><td colspan="2">&nbsp;</td></tr>

        <tr>
            <td><fmt:message key="generalsettings.index"/></td>
            <td>
                <form:input path="index" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="index"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.ignoredarticles"/></td>
            <td>
                <form:input path="ignoredArticles" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="ignoredarticles"/></c:import>
            </td>
        </tr>

        <tr><td colspan="2">&nbsp;</td></tr>

        <tr>
            <td><fmt:message key="generalsettings.language"/></td>
            <td>
                <form:select path="localeIndex" cssStyle="width:15em">
                    <c:forEach items="${command.locales}" var="locale" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${locale}"/>
                    </c:forEach>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="language"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.theme"/></td>
            <td>
                <form:select path="themeIndex" cssStyle="width:15em">
                    <c:forEach items="${command.themes}" var="theme" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${theme.name}"/>
                    </c:forEach>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="theme"/></c:import>
            </td>
        </tr>

        <tr><td colspan="2">&nbsp;</td></tr>

        <tr>
            <td>
            </td>
            <td>
                <form:checkbox path="gettingStartedEnabled" id="gettingStartedEnabled"/>
                <label for="gettingStartedEnabled"><fmt:message key="generalsettings.showgettingstarted"/></label>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="generalsettings.welcometitle"/></td>
            <td>
                <form:input path="welcomeTitle" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="welcomemessage"/></c:import>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="generalsettings.welcomesubtitle"/></td>
            <td>
                <form:input path="welcomeSubtitle" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="welcomemessage"/></c:import>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;"><fmt:message key="generalsettings.welcomemessage"/></td>
            <td>
                <form:textarea path="welcomeMessage" rows="5" cols="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="welcomemessage"/></c:import>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;"><fmt:message key="generalsettings.loginmessage"/></td>
            <td>
                <form:textarea path="loginMessage" rows="5" cols="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="loginmessage"/></c:import>
                <fmt:message key="main.wiki"/>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;"><fmt:message key="generalsettings.shareurlprefix"/></td>
            <td>
                <form:input path="shareUrlPrefix" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="shareurlprefix"/></c:import>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;"><fmt:message key="generalsettings.lyricsurl"/></td>
            <td>
                <form:input path="lyricsUrl" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="lyricsurl"/></c:import>
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;"><fmt:message key="generalsettings.restalbumname"/></td>
            <td>
                <form:input path="restAlbumName" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="restalbumname"/></c:import>
            </td>
        </tr>

        <tr>
            <td colspan="2" style="padding-top:1.5em">
                <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
                <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
            </td>
        </tr>

    </table>
</form:form>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.left.location.href="left.view?";
        parent.frames.playlist.location.href="playlist.view?";
    </script>
</c:if>

</body></html>