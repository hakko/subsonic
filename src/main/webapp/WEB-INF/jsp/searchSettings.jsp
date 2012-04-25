<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.SearchSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe bgcolor1">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="search"/>
</c:import>

<p style="padding-top:1em"><b>Search configuration</b></p>
<div style="width:60%">
Search index scans your music folders, looking for removed and added tracks.<br/>
Since you've installed the MusicCabinet plugin, it will also update metadata for your library. See <a href="musicCabinetSettings.view">MusicCabinet configuration</a> for more details.
</div>

<form:form commandName="command" action="searchSettings.view" method="post">

<table class="indent">
    <tr>
        <td><fmt:message key="searchsettings.auto"/></td>
        <td>
            <form:select path="interval">
                <fmt:message key="searchsettings.interval.never" var="never"/>
                <fmt:message key="searchsettings.interval.one" var="one"/>
                <form:option value="-1" label="${never}"/>
                <form:option value="1" label="${one}"/>

                <c:forTokens items="2 3 7 14 30 60" delims=" " var="interval">
                    <fmt:message key="searchsettings.interval.many" var="many"><fmt:param value="${interval}"/></fmt:message>
                    <form:option value="${interval}" label="${many}"/>
                </c:forTokens>
            </form:select>
        </td>

        <td>
            <form:select path="hour">
                <c:forEach begin="0" end="23" var="hour">
                    <fmt:message key="searchsettings.hour" var="hourLabel"><fmt:param value="${hour}"/></fmt:message>
                    <form:option value="${hour}" label="${hourLabel}"/>
                </c:forEach>
            </form:select>
        </td>
    </tr>

    <tr>
        <td colspan="3">
        	<c:choose>
        		<c:when test="${command.musicCabinetReady}">
		            <div class="forward"><a href="searchSettings.view?update"><fmt:message key="searchsettings.manual"/></a></div>
        		</c:when>
        		<c:otherwise>
        			<p>MusicCabinet configuration isn't completed. Please finish it <a href="musicCabinetSettings.view">here</a> before updating search index.</p>
        		</c:otherwise>
        	</c:choose>
        </td>
    </tr>

    <tr>
        <td colspan="3" style="padding-top:1.5em">
            <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
            <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
        </td>
    </tr>

</table>
</form:form>

<c:if test="${command.creatingIndex}">
    <p><b><fmt:message key="searchsettings.text"><fmt:param value="${command.brand}"/></fmt:message></b></p>
</c:if>

</body></html>