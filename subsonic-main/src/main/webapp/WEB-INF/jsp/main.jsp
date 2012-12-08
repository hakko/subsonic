<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--@elvariable id="model" type="java.util.Map"--%>

<html><head>
	<%@ include file="head.jspf" %>
</head><body class="mainframe bgcolor1">

<h1>${model.dir.name}</h1>

	<c:if test="${model.dir.root eq false}">
		<sub:url value="main.view" var="parentUrl"><sub:param name="path" value="${model.dir.parent.path}"/></sub:url>
		<p class="dense" style="padding-left:0.5em">
			<a href="${parentUrl}" title=".."><span style="white-space:nowrap;">..</span></a>
		</p>
	</c:if>

	<c:forEach items="${model.subDirectories}" var="subDirectory" varStatus="loopStatus">
		<sub:url value="main.view" var="subDirectoryUrl"><sub:param name="path" value="${subDirectory.path}"/></sub:url>
		<p class="dense" style="padding-left:0.5em">
			<a href="${subDirectoryUrl}" title="${subDirectory.name}">
				<span style="white-space:nowrap;">${fn:escapeXml(subDirectory.name)}</span>
			</a>
		</p>
	</c:forEach>
	
	<br/>

	<c:if test="${fn:length(model.files) > 0}">
		<h2>
			<a href="javascript:noop()" onclick="top.playlist.onPlay(${model.trackIds}, 'P');">Play all</a> |
			<a href="javascript:noop()" onclick="top.playlist.onPlay(${model.trackIds}, 'E');">Enqueue all</a> |
			<a href="javascript:noop()" onclick="top.playlist.onPlay(${model.trackIds}, 'A');">Add all</a>
		</h2>
		<table style="border-collapse:collapse;white-space:nowrap">
			<c:forEach items="${model.files}" var="file" varStatus="loopStatus">
				<tr>
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="[${file.id}]"/>
						<c:param name="video" value="${file.video and model.player.web}"/>
						<c:param name="starDisabled" value="true"/>
						<c:param name="playEnabled" value="${model.user.streamRole}"/>
						<c:param name="enqueueEnabled" value="${model.user.streamRole}"/>
						<c:param name="addEnabled" value="${model.user.streamRole}"/>
						<c:param name="downloadEnabled" value="${model.user.downloadRole}"/>
						<c:param name="asTable" value="true"/>
					</c:import>
					<td style="padding-right:1.25em;white-space:nowrap">${fn:escapeXml(file.title)}</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>

</body>
</html>