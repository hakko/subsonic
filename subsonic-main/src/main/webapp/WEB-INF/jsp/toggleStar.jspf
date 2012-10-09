<script type="text/javascript">
function toggleStar(type, id, imageId) {
	if ($(imageId).attr("src").indexOf("<spring:theme code="ratingOnImage"/>") != -1) {
		$(imageId).attr("src", "<spring:theme code="ratingOffImage"/>");
		if (type === 't') {
			uiStarService.unstarTrack(id);
		} else if (type === 'alb') {
			uiStarService.unstarAlbum(id);
		} else if (type === 'art') {
			uiStarService.unstarArtist(id);
		}
	}
	else if ($(imageId).attr("src").indexOf("<spring:theme code="ratingOffImage"/>") != -1) {
		$(imageId).attr("src", "<spring:theme code="ratingOnImage"/>");
		if (type === 't') {
			uiStarService.starTrack(id);
		} else if (type === 'alb') {
			uiStarService.starAlbum(id);
		} else if (type === 'art') {
			uiStarService.starArtist(id);
		}
	}
}
</script>