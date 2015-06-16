<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

	<%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<%@ include file="toggleStar.jspf" %>


<div style="padding: 15px;">

<h1>
<a href="#" onclick="toggleStar('art', ${model.artistUri}, '#starImage${sub:jqesc(model.artistUri)}'); return false;">
	<c:choose>
		<c:when test="${model.artistStarred}">
			<img id="starImage${model.artistUri}" src="<spring:theme code="ratingOnImage"/>" alt="" class="starred">
		</c:when>
		<c:otherwise>
			<img id="starImage${model.artistUri}" src="<spring:theme code="ratingOffImage"/>" alt="" class="starred">
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
						<a href="artistGenres.view?id=${model.artistUri}">&raquo;</a>
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
			<c:if test="${album.uri != -1}"> <a href="artist.view?id=${model.artistUri}&albumId=${album.uri}">&raquo;</a></c:if>
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
			<c:if test="${track.uri != -1 && model.user.streamRole}">
				<table>
					<tr>
						<td><a href="#" onclick="return onPlay([${sub:esc(track.uri)}], 'P');"><img src="<spring:theme code="playImage"/>" alt="Play" title="Play"></a></td>
						<td><a href="#" onclick="return onPlay([${sub:esc(track.uri)}], 'E');"><img src="<spring:theme code="enqueueImage"/>" alt="Enqueue" title="Enqueue"></a></td>
						<td><a href="#" onclick="return onPlay([${sub:esc(track.uri)}], 'A');"><img src="<spring:theme code="addImage"/>" alt="Add" title="Add"></a></td>
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
	<sub:param name="id" value="${model.artistUri}"/>
</sub:url>

<div style="padding-top:15px">

</div>
<div class="back"><a href="${artistUrl}"><fmt:message key="common.back"/></a></div>

</div>

<script type="text/javascript">
    function init() {
        dwr.engine.setErrorHandler(null);
  	}
    init();
</script>
</div>
