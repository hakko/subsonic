
	var musicFiles = [[]<c:forEach items="${model.albums}" var="a">,[[]<c:forEach items="${a.trackIds}" var="t">,${t}</c:forEach>]</c:forEach>];
	
	function toggleAlbum(index) {
		a = '#albart' + index;
		$(a).attr('width', 174 + 87 - $(a).attr('width'));
		$(a).attr('height', 174 + 87 - $(a).attr('height'));

		$('#albcmd' + index).toggle();
		$('#albtit' + index).toggle();
		$('#albson' + index).toggle();

		if ($('#albson' + index).children().size() == 0) {
			$('#albson' + index).load('album.view?view' + musicFiles[index].join('&mf=') <c:if test="${not empty model.trackId}">+ '&trackId=${model.trackId}'</c:if>);
		}
	}
