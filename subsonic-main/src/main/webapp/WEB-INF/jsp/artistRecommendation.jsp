<c:if test="${not empty model.artistsNotInLibrary}">
<br>
<h1>You might also like:</h1>
<ul>
	<c:forEach items="${model.artistsNotInLibrary}" var="recommendedArtist" varStatus="loopStatus">
		<li><a href="http://last.fm/music/${recommendedArtist.artistURL}">${recommendedArtist.artistName}</a></li>
	</c:forEach>
<ul>
</c:if>
