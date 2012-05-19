<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--@elvariable id="model" type="java.util.Map"--%>

<html><head>
	<%@ include file="head.jsp" %>
	<link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
 	<script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
 	<script type="text/javascript" src="<c:url value="/script/scriptaculous.js?load=effects"/>"></script>
 	<script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
 	<script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoom.js"/>"></script>
 	<script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoomHTML.js"/>"></script>
</head><body class="mainframe bgcolor1" onload="init();">

<script type="text/javascript" language="javascript">
	function init() {
		setupZoom('<c:url value="/"/>');
	}

	function playMode() {
		return $('togglePlayAdd').className.substring(0, 1);
	}
	
	function togglePlayAdd() {
		var t = $('togglePlayAdd');
		if (t.className == 'Play') {
			t.className = 'Enqueue';
		} else if (t.className == 'Enqueue') {
			t.className = 'Add';
		} else {
			t.className = 'Play';
		}
		var ids = ['all','top_tracks','artist_radio','random'];
		for (var i=0; i<ids.length; i++) {
			if ($(ids[i])) {
				$(ids[i]).innerHTML = t.className + ' ' + ids[i].replace(/_/g, ' ');
			}
		}
	}

	function toggleAlbum(index) {
		a = 'art' + index;
		$(a).width = 174 + 87 - $(a).width;
		$(a).height = 174 + 87 - $(a).height;

		$('b' + index).toggle();
		$('c' + index).toggle();
		$('d' + index).toggle();
		$('e' + index).toggle();
	}

	function toggleArtist() {
		$('bioArt').width = 126 + 63 - $('bioArt').width;
		$('bioArt').height = 126 + 63 - $('bioArt').height;

		$('bio0').toggle();
		$('bio1').toggle();
	}

	function toggleComment() {
		$('commentForm').toggle();
		$('comment').toggle();
	}

	document.observe("dom:loaded", function() {
		<c:forEach items="${model.albums}" var="album" varStatus="i">
			<c:if test="${album.selected}">
				toggleAlbum('${i.count}');
				window.location.hash='alb${i.count}';
			</c:if>
		</c:forEach>
	});
</script>

<c:if test="${model.updateNowPlaying}">
	<script type="text/javascript" language="javascript">
		// Variable used by javascript in playlist.jsp
		var updateNowPlaying = true;
	</script>
</c:if>

<div style="padding: 15px;">

<h1>${model.artist}</h1>
<table>
	<c:if test="${not empty model.artistInfo}">
		<tr>
			<td style="vertical-align:top">
				<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
					<a href="javascript:noop()" onclick="toggleArtist()">
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
						${model.artistInfoFirstSentence}&nbsp;<a href="javascript:noop()" onclick="toggleArtist()">(...)</a>
					</div>
				</div>
			</td>
		</tr>
	</c:if>
</table>

<h2>
	<c:set var="path">
		<sub:escapeJavaScript string="${model.dir.path}"/>
	</c:set>

	<a id="all" href="javascript:noop()" onclick="top.playlist.onPlay('${path}', playMode());"><fmt:message key="main.playall"/></a> |
	<c:if test="${not empty model.isMusicCabinetReady and not empty model.artistInfo}">
		<a id="top_tracks" href="javascript:noop()" onclick="top.playlist.onPlayTopTracks('${path}', playMode());">Play top tracks</a> |
		<a id="artist_radio" href="javascript:noop()" onclick="top.playlist.onPlayArtistRadio('${path}', playMode());">Play artist radio</a> |
	</c:if>
	<a id="random" href="javascript:noop()" onclick="top.playlist.onPlayRandom('${path}', 10, playMode());"><fmt:message key="main.playrandom"/></a>
	<br/>
	<a id="togglePlayAdd" class="Play" href="javascript:togglePlayAdd()" title="Toggle if new tracks replace the current playlist, are played next, or are appended last to it.">Play/enqueue/add</a>
	<c:if test="${not empty model.isMusicCabinetReady and not empty model.artistInfo}">
		<sub:url value="related.view" var="relatedUrl"><sub:param name="path" value="${model.dir.path}"/></sub:url>
		| <a href="${relatedUrl}">Related artists</a>
	</c:if>

	<c:if test="${model.user.commentRole}">
		| <a href="javascript:toggleComment()"><fmt:message key="main.comment"/></a>
	</c:if>
	<c:if test="${model.user.coverArtRole and not empty model.artistInfo}">
		<sub:url value="/editArtist.view" var="editArtistUrl"><sub:param name="path" value="${model.dir.path}"/><sub:param name="artist" value="${model.artist}"/></sub:url>
		| <a href="${editArtistUrl}">Edit</a>
	</c:if>

</h2>

<div id="comment" class="albumComment"><sub:wiki text="${model.comment}"/></div>
<div id="commentForm" style="display:none">
	<form method="post" action="setMusicFileInfo.view">
		<input type="hidden" name="action" value="comment">
		<input type="hidden" name="path" value="${model.dir.path}">
		<textarea name="comment" rows="6" cols="70">${model.comment}</textarea>
		<input type="submit" value="<fmt:message key="common.save"/>">
	</form>
	<fmt:message key="main.wiki"/>
</div>

<c:forEach items="${model.albums}" var="album" varStatus="i">
	<c:choose>
		<c:when test="${not empty album.coverArtUrl}"><c:set var="coverArtUrl">${album.coverArtUrl}</c:set></c:when>
		<c:otherwise>
			<sub:url value="/coverArt.view" var="coverArtUrl">
				<sub:param name="size" value="174"/>
				<c:if test="${not empty album.coverArtPath}"><sub:param name="path" value="${album.coverArtPath}"/></c:if>
			</sub:url>
		</c:otherwise>
	</c:choose>
	<div style="padding-top:5px" id="alb${i.count}">
		<div style="float:left">
			<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
				<a href="javascript:noop()" onclick="toggleAlbum(${i.count})">
					<img id="art${i.count}" width="${model.coverArtSize}" height="${model.coverArtSize}" src="${coverArtUrl}" alt="">
				</a>
			</div></div></div></div>
			<div id="b${i.count}" style="display:none;float:right;padding-right:10px;padding-bottom:15px;text-align:right" class="detail">
				<c:import url="playAddDownload.jsp">
					<c:param name="path" value="${album.path}"/>
					<c:param name="video" value="${musicFile.video and model.player.web}"/>
					<c:param name="playEnabled" value="${model.user.streamRole}"/>
					<c:param name="enqueueEnabled" value="${model.user.streamRole}"/>
					<c:param name="addEnabled" value="${model.user.streamRole}"/>
					<c:param name="downloadEnabled" value="${model.user.downloadRole}"/>
					<c:param name="asTable" value="false"/>
				</c:import>

				<c:choose>
					<c:when test="${not empty album.coverArtZoomUrl or not empty album.coverArtUrl}">| <a rel="zoom" href="${album.coverArtZoomUrl}">Zoom</a></c:when>
					<c:otherwise><c:choose>
						<c:when test="${not empty album.coverArtPath}">
							<sub:url value="/coverArt.view" var="zoomCoverArtUrl"><sub:param name="path" value="${album.coverArtPath}"/></sub:url>
							| <a rel="zoom" href="${zoomCoverArtUrl}">Zoom</a>
						</c:when>
						<c:otherwise>| Zoom</c:otherwise>						
					</c:choose></c:otherwise>
				</c:choose>

				<br/>

				<c:if test="${model.user.coverArtRole}">
					<sub:url value="/changeCoverArt.view" var="changeCoverArtUrl"><sub:param name="path" value="${album.path}"/></sub:url>
					<a href="${changeCoverArtUrl}">Art</a> |
				</c:if>

				<c:if test="${model.user.coverArtRole}">
					<sub:url value="editTags.view" var="editTagsUrl"><sub:param name="path" value="${album.path}"/></sub:url>
					<a href="${editTagsUrl}">Tags</a> |
				</c:if>
				
				<c:if test="${model.user.shareRole}">
					<sub:url value="createShare.view" var="shareUrl"><sub:param name="dir" value="${album.path}"/></sub:url>
					<a href="${shareUrl}">Share</a>
				</c:if>
				
			</div>
		</div>
		<div style="float:left;padding-left:10px;padding-bottom:15px">
			<div>
				<a href="javascript:noop()" onclick="toggleAlbum(${i.count})">
					<b>${album.title}</b> <em>(${album.year})</em><br>
				</a>
				<div id="e${i.count}" style="display:none">
			        <c:if test="${model.user.commentRole}"><c:import url="rating.jsp">
            			<c:param name="path" value="${album.path}"/>
            			<c:param name="readonly" value="false"/>
            			<c:param name="rating" value="${album.userRating}"/>
            		</c:import></c:if>
				</div>	
			</div>
			<div id="c${i.count}">
				<c:import url="playAddDownload.jsp">
					<c:param name="path" value="${album.path}"/>
					<c:param name="video" value="${musicFile.video and model.player.web}"/>
					<c:param name="playEnabled" value="${model.user.streamRole}"/>
					<c:param name="enqueueEnabled" value="${model.user.streamRole}"/>
					<c:param name="addEnabled" value="${model.user.streamRole}"/>
					<c:param name="downloadEnabled" value="${model.user.downloadRole}"/>
					<c:param name="asTable" value="false"/>
				</c:import>
			</div>
			<div id="d${i.count}" style="display:none">
			<table style="border-collapse:collapse;white-space:nowrap">
				<c:set var="cutoff" value="${model.visibility.captionCutoff}"/>
<c:forEach items="${album.musicFiles}" var="musicFile" varStatus="loopStatus">

<tr style="margin:0;padding:0;border:0">
	<c:import url="playAddDownload.jsp">
		<c:param name="path" value="${musicFile.path}"/>
		<c:param name="video" value="${musicFile.video and model.player.web}"/>
		<c:param name="playEnabled" value="${model.user.streamRole}"/>
		<c:param name="enqueueEnabled" value="${model.user.streamRole}"/>
		<c:param name="addEnabled" value="${model.user.streamRole}"/>
		<c:param name="asTable" value="true"/>
	</c:import>

	<c:if test="${model.visibility.trackNumberVisible}"><td style="padding-right:0.5em;text-align:right"><span class="detail">${musicFile.metaData.trackNumber}</span></td></c:if>
	<td style="padding-right:1.25em;white-space:nowrap"><span title="${musicFile.title}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(musicFile.title)}</str:truncateNicely></span></td>
	<c:if test="${model.visibility.albumVisible}"><td style="padding-right:1.25em;white-space:nowrap"><span class="detail" title="${musicFile.metaData.album}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(musicFile.metaData.album)}</str:truncateNicely></span></td></c:if>
	<c:if test="${model.visibility.artistVisible and model.multipleArtists}"><td style="padding-right:1.25em;white-space:nowrap"><span class="detail" title="${musicFile.metaData.artist}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(musicFile.metaData.artist)}</str:truncateNicely></span></td></c:if>
	<c:if test="${model.visibility.genreVisible}"><td style="padding-right:1.25em;white-space:nowrap"><span class="detail">${musicFile.metaData.genre}</span></td></c:if>
	<c:if test="${model.visibility.yearVisible}"><td style="padding-right:1.25em"><span class="detail">${musicFile.metaData.year}</span></td></c:if>
	<c:if test="${model.visibility.formatVisible}"><td style="padding-right:1.25em"><span class="detail">${fn:toLowerCase(musicFile.metaData.format)}</span></td></c:if>
	<c:if test="${model.visibility.fileSizeVisible}"><td style="padding-right:1.25em;text-align:right"><span class="detail"><sub:formatBytes bytes="${musicFile.metaData.fileSize}"/></span></td></c:if>
	<c:if test="${model.visibility.durationVisible}"><td style="padding-right:1.25em;text-align:right"><span class="detail">${musicFile.metaData.durationAsString}</span></td></c:if>
	<c:if test="${model.visibility.bitRateVisible}"><td style="padding-right:0.25em"><span class="detail"><c:if test="${not empty musicFile.metaData.bitRate}">${musicFile.metaData.bitRate} Kbps ${musicFile.metaData.variableBitRate ? "vbr" : ""}</c:if>
	<c:if test="${musicFile.video and not empty musicFile.metaData.width and not empty musicFile.metaData.height}">(${musicFile.metaData.width}x${musicFile.metaData.height})</c:if></span></td></c:if>
</tr>
</c:forEach>
</table>
			</div>
		</div>
	</div>
	<div style="clear:both;"></div>
</c:forEach>		

</div>

</body>
</html>
