<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
	<%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<%@ include file="toggleStar.jspf" %>


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
					<a href="#" onclick="return toggleArtist()">
						<img id="bioArt" width="${model.artistInfoImageSize}" height="${model.artistInfoImageSize}" src="${model.artistInfo.largeImageUrl}" alt="">
					</a>
				</div></div></div></div>
			</td>
			<td style="vertical-align:top">
				<div style="width:525px;">
					<div id="bio0" <c:if test="${not empty model.artistInfoMinimized}">style="display:none"</c:if>>
						${model.artistInfo.bioSummary}
					</div>
					<div id="bio1" <c:if test="${empty model.artistInfoMinimized}">style="display:none"</c:if>>
						${model.artistInfoFirstSentence}&nbsp;<a href="#" onclick="return toggleArtist()">(...)</a>
					</div>
				</div>
				<div style="line-height: 1.8">
					<c:forEach items="${model.topTags}" var="topTag" varStatus="i">
						<sub:url var="url" value="genres.view">
							<sub:param name="genre" value="${topTag.name}"/>
						</sub:url>
						<a href="${url}">${topTag.name}</a><c:if test="${i.count < fn:length(model.topTags)}">, </c:if>
					</c:forEach>
					<c:if test="${fn:length(model.topTags) > 0 and model.allowTopTagsEdit}">
						<a href="artistGenres.view?id=${model.artistId}">&raquo;</a>
					</c:if>
				</div>
			</td>
		</tr>
	</table>
</c:if>

<h2>
	<a id="all" href="#" onclick="return onPlay(${model.trackIds}, playMode());"><fmt:message key="main.playall"/></a> |
	<c:if test="${model.isInSearchIndex}">
		<a id="top_tracks" href="#" onclick="return onPlayTopTracks(${model.artistId}, playMode());">Play top tracks</a> |
		<a id="artist_radio" href="#" onclick="return onPlayArtistRadio(${model.artistId}, playMode());">Play artist radio</a> |
	</c:if>
	<a id="random" href="#" onclick="return onPlayRandom(${model.trackIds}, playMode());"><fmt:message key="main.playrandom"/></a>
	<br/>
	<a id="togglePlayAdd" class="Play" href="javascript:togglePlayAdd()" title="Toggle if new tracks replace the current playlist, are played next, or are appended last to it.">Play/enqueue/add</a>
	<c:if test="${model.isInSearchIndex}">
		<sub:url value="related.view" var="relatedUrl"><sub:param name="id" value="${model.artistId}"/></sub:url>
		| <a href="${relatedUrl}">Related artists</a>
		<sub:url value="artistDetails.view" var="detailsUrl"><sub:param name="id" value="${model.artistId}"/></sub:url>
		| <a href="${detailsUrl}">Details</a>
	</c:if>

	<c:if test="${model.user.coverArtRole and not empty model.artistInfo}">
		<sub:url value="/editArtist.view" var="editArtistUrl"><sub:param name="id" value="${model.artistId}"/><sub:param name="artist" value="${model.artistName}"/></sub:url>
		| <a href="${editArtistUrl}">Edit</a>
	</c:if>
</h2>

<%@ include file="albums.jspf" %>

</div>

<script type="text/javascript" language="javascript">

    function init() {
        dwr.engine.setErrorHandler(null);
	}

	function playMode() {
		return $('#togglePlayAdd').attr('class').substring(0, 1);
	}
	
	function togglePlayAdd() {
		var t = $('#togglePlayAdd');
		if (t.attr('class') == 'Play') {
			t.attr('class', 'Enqueue');
		} else if (t.attr('class') == 'Enqueue') {
			t.attr('class', 'Add');
		} else {
			t.attr('class', 'Play');
		}
		var ids = ['all','top_tracks','artist_radio','random'];
		for (var i=0; i<ids.length; i++) {
			if ($('#'+ids[i])) {
				$('#'+ids[i]).html(t.attr('class') + ' ' + ids[i].replace(/_/g, ' '));
			}
		}
	}

<%@ include file="albumsHeader.jspf" %>

	function toggleArtist() {
		$('#bioArt').attr('width', 126 + 63 - $('#bioArt').attr('width'));
		$('#bioArt').attr('height', 126 + 63 - $('#bioArt').attr('height'));

		$('#bio0').toggle();
		$('#bio1').toggle();
    return false;
	}

		<c:forEach items="${model.albums}" var="album" varStatus="i">
			<c:if test="${album.selected}">
				toggleAlbum('${i.count}');
				window.location.hash='alb${i.count}';
			</c:if>
		</c:forEach>
    init();
</script>
</div>
