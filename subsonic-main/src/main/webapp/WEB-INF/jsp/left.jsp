<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head>
	<%@ include file="head.jspf" %>
	<script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/smooth-scroll.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>

    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/libraryStatusService.js"/>"></script>

	<script type="text/javascript">
    function init() {
        dwr.engine.setErrorHandler(null);
        dwr.engine.setActiveReverseAjax(true);
        dwr.engine.setNotifyServerOnPageUnload(true);
 
        $('#tag').change(function() {
        	window.location = $(this).val();
        });

    }
	</script>
	
	<meta http-equiv="Pragma" content="no-cache">
	
</head>
	
<body class="bgcolor2 leftframe" onload="init()">
<a name="top"></a>

<div style="padding-bottom:0.5em">
	<c:forEach items="${model.indexes}" var="index">
		<a href="#${index.key}" accesskey="${index.key}">${index.key}</a>
	</c:forEach>
</div>

<c:if test="${not empty model.statistics}">
	<div class="detail">
		<fmt:message key="left.statistics">
			<fmt:param value="${model.statistics.artistCount}"/>
			<fmt:param value="${model.statistics.albumCount}"/>
			<fmt:param value="${model.statistics.trackCount}"/>
			<fmt:param value="${model.statisticsBytes}"/>
			<fmt:param value="${model.statistics.totalLengthInHours}"/>
		</fmt:message>
	</div>
</c:if>

<c:if test="${not empty model.filebased}">
    <div style="padding:5px;padding-bottom:15px;border:1px solid #<spring:theme code="detailColor"/>">
		<img src="icons/error.png" alt=""/><b>File-based browsing.<br/></b>
		<c:choose>
		<c:when test="${not empty model.hasArtists}">
			Your library can be browsed by tags.			
			<a href="left.view?method=tag">Switch back.</a>
		</c:when>
		<c:otherwise>
			<c:if test="${model.indexing eq true}">Your library is being scanned.<br/></c:if>
			<c:if test="${model.indexing eq false}">Before you can use the full graphic interface, a library scan must be performed.<br/></c:if>
			<a target="main" href="musicCabinetSettings.view">Learn more.</a>
		</c:otherwise>
		</c:choose>
		<div id="leftMessage">
		</div>
	</div>
</c:if>

<c:if test="${not empty model.radios}">
	<h2 class="bgcolor1"><fmt:message key="left.radio"/></h2>
	<c:forEach items="${model.radios}" var="radio">
		<p class="dense">
			<a target="hidden" href="${radio.streamUrl}">
				<img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a>
			<c:choose>
				<c:when test="${empty radio.homepageUrl}">
					${radio.name}
				</c:when>
				<c:otherwise>
					<a target="main" href="${radio.homepageUrl}">${radio.name}</a>
				</c:otherwise>
			</c:choose>
		</p>
	</c:forEach>
</c:if>

<c:if test="${not empty model.indexes or not empty model.currentTag}">
	<h2 class="bgcolor1">Artists</h2>
	<c:if test="${not empty model.tags}">
		<select id="tag">
		<c:forEach items="${model.tags}" var="tag">
	        <sub:url value="left.view" var="leftUrl"><sub:param name="tag" value="${tag}"/></sub:url>
			<option value="${leftUrl}"<c:if test="${tag eq model.currentTag}"> selected</c:if>>${fn:escapeXml(tag)}</option>
		</c:forEach>
		</select>
		<c:if test="${not empty model.currentTag}">
			<br><a href="javascript:noop()" onclick="javascript:top.playlist.onPlayGenreRadio(new Array('${model.currentTag}'))">Play ${model.currentTag} radio</a>
		</c:if>
	</c:if>
	<div id="leftMessage"></div>

	<c:forEach items="${model.indexes}" var="index">
	<table class="bgcolor1" style="width:100%;padding:0;margin:1em 0 0 0;border:0">
		<tr style="padding:0;margin:0;border:0">
			<th style="text-align:left;padding:0;margin:0;border:0"><a name="${index.key}"></a>
				<h2 style="padding:0;margin:0;border:0"><c:if test="${model.reluctantArtistLoading}"><a href="left.view?indexLetter=${fn:replace(index.key,'#','0')}"></c:if>${index.key}<c:if test="${model.reluctantArtistLoading}"></a></c:if></h2>
			</th>
			<th style="text-align:right;">
				<a href="#top"><img src="<spring:theme code="upImage"/>" alt=""></a>
			</th>
		</tr>
	</table>

	<c:forEach items="${index.value}" var="artist">
		<p class="dense" style="padding-left:0.5em">
			<span title="${fn:escapeXml(artist.name)}">
				<sub:url value="artist.view" var="artistUrl"><sub:param name="id" value="${artist.id}"/></sub:url>
				<a target="main" href="${artistUrl}">${fn:escapeXml(artist.name)}</a>
			</span>
		</p>
	</c:forEach>
	</c:forEach>
	<a name="bottom"></a>
</c:if>

<c:if test="${not empty model.variousArtistsAlbums}">
	<h2 class="bgcolor1">Various Artists</h2>
	<c:forEach items="${model.variousArtistsAlbums}" var="album">
		<p class="dense" style="padding-left:0.5em">
			<sub:url value="artist.view" var="albumUrl"><sub:param name="id" value="${album.artist.id}"/><sub:param name="albumId" value="${album.id}"/></sub:url>
			<a target="main" href="${albumUrl}">${album.name}</a>
		</p>
	</c:forEach>
</c:if>

<c:if test="${not empty model.mediaFolders}">
	<h2 class="bgcolor1">Media folders</h2>
	<c:forEach items="${model.mediaFolders}" var="mediaFolder">
		<p class="dense" style="padding-left:0.5em">
			<sub:url value="main.view" var="mainUrl"><sub:param name="path" value="${mediaFolder.path}"/></sub:url>
			<a target="main" href="${mainUrl}">${mediaFolder.name}</a>
		</p>
	</c:forEach>
</c:if>

<c:if test="${empty model.filebased}">
	<div style="height:2em"></div><hr>
	<c:if test="${model.uploadRole}"><a target="main" href="more.view">Upload new music</a><br></c:if>
	<c:if test="${model.adminRole}"><a target="main" href="missingAlbums.view">Missing albums</a><br></c:if>
	<a href="left.view?method=file">File-based browsing</a>
</c:if>

<div style="height:5em"></div>

<div class="bgcolor2" style="opacity: 1.0; clear: both; position: fixed; bottom: 0; right: 0; left: 0;
	  padding: 0.25em 0.75em 0.25em 0.75em; border-top:1px solid black; max-width: 850px;">
	<c:forEach items="${model.indexes}" var="index">
		<a href="#${index.key}" accesskey="${index.key}">${index.key}</a>
	</c:forEach>
</div>

<c:if test="${not empty model.reluctantArtistLoading and not empty model.indexedLetter}">
<script type="text/javascript">
window.location.hash='${fn:replace(model.indexedLetter,'#','#bottom')}';
</script>
</c:if>

</body></html>