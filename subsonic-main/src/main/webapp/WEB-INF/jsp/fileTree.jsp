<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>
    <%@ include file="head.jspf" %>
	<link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/uiStarService.js"/>"></script>
</head>
<body class="mainframe bgcolor1">

<%@ include file="toggleStar.jspf" %>

<script type="text/javascript">
<%@ include file="albumsHeader.jspf" %>
</script>

<h1>
    <img src="<spring:theme code="fileTreeImage"/>" alt="">
    File tree
</h1>

<c:choose>
<c:when test="${not empty model.directory}">

	<c:if test="${not empty model.addedDirectories}">
	<h2>New directories found:</h2>
	<ul>
	<c:forEach items="${model.addedDirectories}" var="directory">
		<li>${directory}</li>
	</c:forEach>
	</ul>
	<div class="forward"><a href="fileTree.view?id=${model.directoryId}&action=scan&from=subdir&type=offline">Offline scan</a></div>
	<div class="forward"><a href="fileTree.view?id=${model.directoryId}&action=scan&from=subdir&type=normal">Normal scan</a></div>
	<br>
	</c:if>

	<c:if test="${not empty model.addedFiles}">
	<h2>New files found:</h2>
	<ul>
	<c:forEach items="${model.addedFiles}" var="file">
		<li>${file.filename}</li>
	</c:forEach>
	</ul>
	<div class="forward"><a href="fileTree.view?id=${model.directoryId}&action=scan&from=dir&type=offline">Offline scan</a></div>
	<div class="forward"><a href="fileTree.view?id=${model.directoryId}&action=scan&from=dir&type=normal">Normal scan</a></div>
	<br>
	</c:if>

	<c:if test="${empty model.addedDirectories and empty model.addedFiles}">
	<p>Nothing new added to ${model.directory}.</p>
	</c:if>
	
	<sub:url value="fileTree.view" var="prevUrl">
		<sub:param name="id" value="${model.parentId}"/>
	</sub:url>
	<div class="back"><a href="${prevUrl}"><fmt:message key="common.back"/></a></div>
	
</c:when>
<c:otherwise>

	<ul style="list-style-type: none; margin-left: 5px; padding: 5px;">
	<c:if test="${not empty model.parentId}">
		<li><a href="fileTree.view?id=${model.parentId}">[..]</a></li>
	</c:if>
	<c:forEach items="${model.directories}" var="directory">
		<li>
		<c:if test="${model.user.adminRole}">
			<a href="fileTree.view?id=${directory.id}&action=add"><img src="<spring:theme code="plusImage"/>" alt="Look for added files" title="Look for added files"></a>
		</c:if>
		<a href="fileTree.view?id=${directory.id}">${directory.name}</a></li>
	</c:forEach>
	</ul>

	<c:if test="${not empty model.albums}">
		<%@ include file="albums.jspf" %>
	</c:if>

	<c:if test="${fn:length(model.videoFiles) > 0}">
		<ul style="list-style-type: none; margin-left: 5px; padding-left: 5px;">
			<c:forEach items="${model.videoFiles}" var="file">
			<li>
				<c:if test="${model.user.downloadRole}">
					<sub:url value="/download.view" var="downloadUrl"><sub:param name="id" value="[${file.id}]"/><sub:param name="name" value="${file.name}"/></sub:url>
					<a href="${downloadUrl}"><img src="<spring:theme code="downloadImage"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"></a>
				</c:if>
				<c:if test="${model.user.streamRole}">
					<sub:url value="/videoPlayer.view" var="videoUrl"><sub:param name="id" value="[${file.id}]"/></sub:url>
					<a href="${videoUrl}">
				</c:if>
				${file.name}
				<c:if test="${model.user.streamRole}"></a></c:if>
			</li>
			</c:forEach>
		</ul>
	</c:if>

	<c:if test="${fn:length(model.imageFiles) > 1}">
		<ul style="list-style-type: none; margin-left: 5px; padding-left: 5px;">
			<c:forEach items="${model.imageFiles}" var="file">
			<li>
				<sub:url value="coverArt.view" var="coverArtUrl">
					<sub:param name="path" value="${file.path}"/>
				</sub:url>
				<a href="${coverArtUrl}">${file.name}</a>
			</li>
			</c:forEach>
		</ul>
	</c:if>

</c:otherwise>
</c:choose>

</body></html>