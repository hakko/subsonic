<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<%@ include file="toggleStar.jspf" %>


<div style="padding: 15px;">

<h1>
<a href="#" onclick="toggleStar('art', '${model.artistUri}', '${sub:jqesc("#starImage".concat(model.artistUri))}'); return false;">
	<c:choose>
		<c:when test="${model.artistStarred}">
			<img id="starImage${model.artistUri}" src="<spring:theme code="ratingOnImage"/>" alt="">
		</c:when>
		<c:otherwise>
			<img id="starImage${model.artistUri}" src="<spring:theme code="ratingOffImage"/>" alt="">
		</c:otherwise>
	</c:choose>
</a>
${model.artistName}
</h1>

<c:if test="${not empty model.artistInfo}">
		<div class="clearfix">
			<div class="col-lg-3">
					<a href="#" onclick="return toggleArtist()">
						<img id="bioArt" class="img-responsive" width="${model.artistInfoImageSize}" height="${model.artistInfoImageSize}" src="${model.artistInfo.largeImageUrl}" alt="">
					</a>
			</div>
			<div class="col-lg-7">
				<div>
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
						<a href="${url}" class="btn btn-default btn-xs">${topTag.name}</a><c:if test="${i.count < fn:length(model.topTags)}"> </c:if>
					</c:forEach>
					<c:if test="${fn:length(model.topTags) > 0 and model.allowTopTagsEdit}">
                        <sub:url var="url" value="artistGenres.view">
                            <sub:param name="id" value="${model.artistUri}"/>
                        </sub:url>
					
						<a href="${url}">&raquo;</a>
					</c:if>
				</div>
			</div>
		</div>
</c:if>

<div class="btn-toolbar" role="toolbar">
  <div class="btn-group">
    <div class="btn-group">
      <button type="button" class="btn btn-default dropdown-toggle btn-xs" data-toggle="dropdown">
        Play
        <span class="caret"></span>
      </button>
      <ul class="dropdown-menu">
    	  <li><a class="btn btn-default btn-xs" id="all" href="#" onclick="return onPlay(${sub:esc(model.trackUris)}, playMode());"><fmt:message key="main.playall"/></a></li>
    	  <c:if test="${model.isInSearchIndex}">
      		<li><a class="btn btn-default btn-xs" id="top_tracks" href="#" onclick="return onPlayTopTracks('${model.artistUri}', playMode());">Top tracks</a></li>
      		<li><a class="btn btn-default btn-xs" id="artist_radio" href="#" onclick="return onPlayArtistRadio('${model.artistUri}', playMode());">Artist radio</a></li>
      	</c:if>
      	<li><a class="btn btn-default btn-xs" id="random" href="#" onclick="return onPlayRandom(${sub:esc(model.trackUris)}, playMode());"><fmt:message key="main.playrandom"/></a></li>
      </ul>
    </div>
  	<a class="btn btn-default btn-xs" id="togglePlayAdd" class="Play" href="#" onclick="return togglePlayAdd();" title="Toggle if new tracks replace the current playlist, are played next, or are appended last to it.">Play/enqueue/add</a>
  	<c:if test="${model.isInSearchIndex}">
  		<sub:url value="related.view" var="relatedUrl"><sub:param name="id" value="${model.artistUri}"/></sub:url>
  		<a class="btn btn-default btn-xs" href="${relatedUrl}">Related artists</a>
  		<sub:url value="artistDetails.view" var="detailsUrl"><sub:param name="id" value="${model.artistUri}"/></sub:url>
  		<a class="btn btn-default btn-xs" href="${detailsUrl}">Details</a>
  	</c:if>
  	<c:if test="${model.user.coverArtRole and not empty model.artistInfo}">
  		<sub:url value="/editArtist.view" var="editArtistUrl"><sub:param name="id" value="${model.artistUri}"/><sub:param name="artist" value="${model.artistName}"/></sub:url>
  		<a class="btn btn-default btn-xs" href="${editArtistUrl}">Edit</a>
  	</c:if>
  </div>
</div>

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
	   return $(function($) {
            $('#bioArt').attr('width', 126 + 63 - $('#bioArt').attr('width'));
            $('#bioArt').attr('height', 126 + 63 - $('#bioArt').attr('height'));

            $('#bio0').toggle();
            $('#bio1').toggle();
        return false;
       }(jQuery));
	}

		<c:forEach items="${model.albums}" var="album" varStatus="i">
			<c:if test="${album.selected}">
				toggleAlbum('${i.count}');
			</c:if>
		</c:forEach>
    init();
</script>
</div>
