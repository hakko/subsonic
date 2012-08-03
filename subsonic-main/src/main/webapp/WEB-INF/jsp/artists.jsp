<table>
	<c:forEach items="${model.artists}" var="artistRecommendation" varStatus="loopStatus">
        <c:if test="${loopStatus.count % model.artistGridWidth == 1}">
	        <tr>
   	     </c:if>
		<td style="vertical-align:top">
			<a href="artist.view?id=${artistRecommendation.artistId}">
				<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
					<img width="126" height="126" src="${artistRecommendation.imageUrl}" alt="">
				</div></div></div></div>
				<div style="detail">
					<div style="width:108px;float:left">
						${artistRecommendation.artistName}
					</div>
					<div style="width:18px;float:right">
						<a href="javascript:noop()" onclick="top.playlist.onPlayTopTracks(${artistRecommendation.artistId}, 'P');">
			                <img src="<spring:theme code="playImage"/>" alt="Play top tracks" title="Play top tracks">
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
