<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--@elvariable id="model" type="java.util.Map"--%>

<html><head>
	<%@ include file="head.jspf" %>
	<link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/uiStarService.js"/>"></script>
</head><body class="mainframe bgcolor1" onload="init()">

<%@ include file="toggleStar.jspf" %>

<script type="text/javascript" language="javascript">
    function init() {
        dwr.engine.setErrorHandler(null);
	}
</script>

<div style="padding: 15px;">

<h1>
<a href="#" onclick="toggleStar('art', ${model.artistId}, '#starImage${model.artistId}'); return false;">
	<c:choose>
		<c:when test="${model.artistStarred}">
			<img id="starImage${model.artistId}" src="<spring:theme code="ratingOnImage"/>" alt="">
		</c:when>
		<c:otherwise>
			<img id="starImage${model.artistId}" src="<spring:theme code="ratingOffImage"/>" alt="">
		</c:otherwise>
	</c:choose>
</a>
${model.artistName}
</h1>

<c:if test="${not empty model.artistInfo}">
	<table>
		<tr>
			<td style="vertical-align:top">
				<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
					<img id="bioArt" width="174" height="174" src="${model.artistInfo.largeImageUrl}" alt="">
				</div></div></div></div>
			</td>
			<td style="vertical-align:top">
				<div style="width:525px; white-space: pre-line;">
					${model.artistInfo.bioContent}
				</div>
				<div style="line-height: 1.8">
					<c:forEach items="${model.topTags}" var="topTag" varStatus="i">
						<sub:url var="url" value="genres.view">
							<sub:param name="genre" value="${topTag.name}"/>
						</sub:url>
						<a href="${url}">${topTag.name}</a><c:if test="${i.count < fn:length(model.topTags)}">, </c:if>
					</c:forEach>
					<c:if test="${fn:length(model.topTags) > 0}">
						<a href="artistGenres.view?id=${model.artistId}">&raquo;</a>
					</c:if>
				</div>
			</td>
		</tr>
	</table>
</c:if>

<div style="padding-top:20px">
<h1>Discography</h1>
<c:forEach items="${model.albums}" var="album" varStatus="i">
	<c:choose>
		<c:when test="${not empty album.coverArtUrl}"><c:set var="coverArtUrl">${album.coverArtUrl}</c:set></c:when>
		<c:otherwise>
			<sub:url value="coverArt.view" var="coverArtUrl">
				<sub:param name="size" value="174"/>
				<c:if test="${not empty album.coverArtPath}"><sub:param name="path" value="${album.coverArtPath}"/></c:if>
			</sub:url>
		</c:otherwise>
	</c:choose>
	<div style="padding-top:5px">
		<div style="float:left">
			<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
				<img width="34" height="34" src="${coverArtUrl}" alt="">
			</div></div></div></div>
		</div>
		<div style="float:left">
			<b>${album.title}</b><c:if test="${album.year > 0}"> <em>(${album.year})</em></c:if>
			<c:if test="${album.id != -1}"> <a href="artist.view?id=${model.artistId}&albumId=${album.id}">&raquo;</a></c:if>
		</div>
		<div style="clear:both;"></div>
	</div>
</c:forEach>
</div>

<div style="padding-top:20px">
<h1>Top tracks</h1>
<table>
<c:forEach items="${model.topTracks}" var="track" varStatus="i">
	<tr>
		<td>
			<c:if test="${track.id != -1 && model.user.streamRole}">
				<table>
					<tr>
						<td><a href="javascript:noop()" onclick="top.playlist.onPlay([${track.id}], 'P');"><img src="<spring:theme code="playImage"/>" alt="Play" title="Play"></a></td>
						<td><a href="javascript:noop()" onclick="top.playlist.onPlay([${track.id}], 'E');"><img src="<spring:theme code="enqueueImage"/>" alt="Enqueue" title="Enqueue"></a></td>
						<td><a href="javascript:noop()" onclick="top.playlist.onPlay([${track.id}], 'A');"><img src="<spring:theme code="addImage"/>" alt="Add" title="Add"></a></td>
					</tr>
				</table>
			</c:if>
		</td>
		<td>
			${track.name}
		</td>
	</tr>
</c:forEach>
</table>
</div>

<sub:url value="artist.view" var="artistUrl">
	<sub:param name="id" value="${model.artistId}"/>
</sub:url>

<div style="padding-top:15px"/>
<div class="back"><a href="${artistUrl}"><fmt:message key="common.back"/></a></div>

</div>

</body>
</html>
