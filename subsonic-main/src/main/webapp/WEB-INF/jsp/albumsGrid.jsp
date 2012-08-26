<table>
	<c:forEach items="${model.albums}" var="album" varStatus="loopStatus">
        <c:if test="${loopStatus.count % model.artistGridWidth == 1}">
	        <tr>
   	    </c:if>
		<td style="vertical-align:top">
			<a href="artist.view?id=${album.artistId}&albumId=${album.id}">
				<c:choose>
					<c:when test="${not empty album.coverArtUrl}"><c:set var="coverArtUrl">${album.coverArtUrl}</c:set></c:when>
					<c:otherwise>
						<sub:url value="coverArt.view" var="coverArtUrl">
							<sub:param name="size" value="126"/>
							<c:if test="${not empty album.coverArtPath}"><sub:param name="path" value="${album.coverArtPath}"/></c:if>
						</sub:url>
					</c:otherwise>
				</c:choose>
				<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
					<img width="126" height="126" src="${coverArtUrl}" alt="" title="${album.artistName} - ${album.title}">
				</div></div></div></div>
				<div style="detail">
					<div style="width:108px;float:left">
						<div class="detail"><em><str:truncateNicely lower="14" upper="14">${album.artistName}</str:truncateNicely></em></div>
						<div class="detail"><str:truncateNicely lower="14" upper="14">${album.title}</str:truncateNicely></div>
					</div>
					<div style="width:18px;float:right">
						<a href="javascript:noop()" onclick="top.playlist.onPlay(${album.trackIds}, 'P');">
			                <img src="<spring:theme code="playImage"/>" alt="Play album" title="Play album">
						</a>
					</div>
				</div>
			</a>
		</td>
        <c:if test="${loopStatus.count % model.artistGridWidth == 0}">
            </tr>
        </c:if>
	</c:forEach>
</table>
