<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.SearchSettingsCommand"--%>

<html><head>
    <%@ include file="head.jspf" %>
</head>
<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:if test="${command.creatingIndex}">
	<script type="text/javascript">
		window.location.replace('musicCabinetSettings.view');
	</script>
</c:if>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="search"/>
</c:import>

<div style="width:60%">
<c:if test="${not command.databaseAvailable}">
	<p style="padding-top:1em"><b>Search configuration</b></p>
	<p>MusicCabinet configuration isn't completed. Please finish it <a href="musicCabinetSettings.view">here</a> before updating search index.</p>
</c:if>

<c:if test="${command.databaseAvailable and not command.creatingIndex}">

	<form:form commandName="command" action="searchSettings.view" method="post">
		<div style="padding-top:1em">
			<p><b>Nightly update</b></p>
			<p>It is recommended to have your library scanned nightly, to keep the information fetched from last.fm up-to-date.</p>
			<p><fmt:message key="searchsettings.auto"/>
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

            <form:select path="hour">
                <c:forEach begin="0" end="23" var="hour">
                    <fmt:message key="searchsettings.hour" var="hourLabel"><fmt:param value="${hour}"/></fmt:message>
                    <form:option value="${hour}" label="${hourLabel}"/>
                </c:forEach>
            </form:select></p>

			<p>
				<input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
				<input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
			</p>
		</div>
	</form:form>
</c:if>
</div>

</body></html>