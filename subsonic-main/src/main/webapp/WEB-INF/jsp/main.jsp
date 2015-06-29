<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<%@ include file="include.jspf" %>

<div class="mainframe bgcolor1">

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
			<a href="#" onclick="return onPlay('${sub:esc(model.trackUris)}', 'P');">Play all</a> |
			<a href="#" onclick="return onPlay('${sub:esc(model.trackUris)}', 'E');">Enqueue all</a> |
			<a href="#" onclick="return onPlay('${sub:esc(model.trackUris)}', 'A');">Add all</a>
		</h2>
			<c:forEach items="${model.files}" var="file" varStatus="loopStatus">
				<div>
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
					${fn:escapeXml(file.title)}
				</div>
			</c:forEach>
		</table>
	</c:if>
</div>
