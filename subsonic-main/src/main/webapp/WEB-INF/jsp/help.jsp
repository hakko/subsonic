<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
    <%@ include file="head.jspf" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>
<body class="mainframe bgcolor1">

<c:choose>
    <c:when test="${empty model.buildDate}">
        <fmt:message key="common.unknown" var="buildDateString"/>
    </c:when>
    <c:otherwise>
        <fmt:formatDate value="${model.buildDate}" dateStyle="long" var="buildDateString"/>
    </c:otherwise>
</c:choose>

<h1>
    <img src="<spring:theme code="helpImage"/>" alt="">
    <fmt:message key="help.title"><fmt:param value="${model.brand}"/></fmt:message>
</h1>

<table width="75%" class="ruleTable indent">
    <tr><td class="ruleTableHeader"><fmt:message key="help.version.title"/></td><td class="ruleTableCell">${model.brand}, originally written by Sindre Mehus. Built with MusicCabinet plugin, version ${model.buildNumber}, on ${buildDateString}.</td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.server.title"/></td><td class="ruleTableCell">${model.serverInfo} (<sub:formatBytes bytes="${model.usedMemory}"/> / <sub:formatBytes bytes="${model.totalMemory}"/>)</td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.license.title"/></td><td class="ruleTableCell">
        <a href="http://www.gnu.org/copyleft/gpl.html" target="_blank"><img style="float:right;margin-left: 10px" alt="GPL 3.0" src="<c:url value="/icons/gpl.png"/>"></a>
        <fmt:message key="help.license.text"><fmt:param value="${model.brand}"/></fmt:message></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.homepage.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://www.subsonic.org/">subsonic.org</a></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.forum.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://forum.subsonic.org/">forum.subsonic.org</a></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.contact.title"/></td><td class="ruleTableCell"><fmt:message key="help.contact.text"><fmt:param value="${model.brand}"/></fmt:message><p>MusicCabinet is a non-affiliated plug-in for ${model.brand}. The source code, together with ${model.brand} modifications, is available at <a href="https://github.com/hakko">Github</a>.</p></td></tr>
	<tr><td class="ruleTableHeader">Bug reports</td><td class="ruleTableCell">Please report encountered bugs here: <a target="_blank" href="http://forum.subsonic.org/forum/viewforum.php?f=11">MusicCabinet forum</a><br>To help solving problems, attach the contents of these files:<ul><li>${model.subsonicLogFile}</li><li>${model.musicCabinetLogFile}</li></ul>Try to make your bug report as re-producable as possible. The more details you can supply on what's going wrong, the more likely it is to get solved. Thanks!</td></tr>
</table>

<h2><img src="<spring:theme code="logImage"/>" alt="">&nbsp;<fmt:message key="help.log"/></h2>

<table cellpadding="2" class="log indent">
    <c:forEach items="${model.logEntries}" var="entry">
        <tr>
            <td>[<fmt:formatDate value="${entry.date}" dateStyle="short" timeStyle="long" type="both"/>]</td>
            <td>${entry.level}</td><td>${entry.category}</td><td>${entry.message}</td>
        </tr>
    </c:forEach>
</table>

<p><fmt:message key="help.logfile"><fmt:param value="${model.subsonicLogFile}"/></fmt:message> </p>

<div class="forward"><a href="help.view?"><fmt:message key="common.refresh"/></a></div>

</body></html>