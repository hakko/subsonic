<table style="border-collapse:collapse;white-space:nowrap">
	<c:set var="cutoff" value="${model.visibility.captionCutoff}"/>
<c:forEach items="${model.mediaFiles}" var="mediaFile" varStatus="loopStatus" >

<tr style="margin:0;padding:0;border:0">
	<c:import url="playAddDownload.jsp">
		<c:param name="id" value="[${mediaFile.id}]"/>
		<c:param name="starred" value="${model.isTrackStarred[loopStatus.index]}"/>
		<c:param name="starId" value="${mediaFile.id}"/>
		<c:param name="starEnabled" value="${model.isTrackStarred[loopStatus.index]}"/>
		<c:param name="playEnabled" value="${model.user.streamRole}"/>
		<c:param name="enqueueEnabled" value="${model.user.streamRole}"/>
		<c:param name="addEnabled" value="${model.user.streamRole}"/>
		<c:param name="asTable" value="true"/>
	</c:import>

	<c:if test="${model.visibility.trackNumberVisible}"><td style="padding-right:0.5em;text-align:right"><span class="detail">${mediaFile.metaData.trackNumber}</span></td></c:if>
	<td style="padding-right:1.25em;white-space:nowrap"><span title="${mediaFile.title}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(mediaFile.title)}</str:truncateNicely></span></td>
	<c:if test="${model.visibility.albumVisible}"><td style="padding-right:1.25em;white-space:nowrap"><span class="detail" title="${mediaFile.metaData.album}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(mediaFile.metaData.album)}</str:truncateNicely></span></td></c:if>
	<c:if test="${model.visibility.artistVisible and model.multipleArtists}"><td style="padding-right:1.25em;white-space:nowrap"><span class="detail" title="${mediaFile.metaData.artist}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(mediaFile.metaData.artist)}</str:truncateNicely></span></td></c:if>
	<c:if test="${model.visibility.genreVisible}"><td style="padding-right:1.25em;white-space:nowrap"><span class="detail">${mediaFile.metaData.genre}</span></td></c:if>
	<c:if test="${model.visibility.yearVisible}"><td style="padding-right:1.25em"><span class="detail">${mediaFile.metaData.year}</span></td></c:if>
	<c:if test="${model.visibility.formatVisible}"><td style="padding-right:1.25em"><span class="detail">${fn:toLowerCase(mediaFile.metaData.format)}</span></td></c:if>
	<c:if test="${model.visibility.fileSizeVisible}"><td style="padding-right:1.25em;text-align:right"><span class="detail"><sub:formatBytes bytes="${mediaFile.metaData.fileSize}"/></span></td></c:if>
	<c:if test="${model.visibility.durationVisible}"><td style="padding-right:1.25em;text-align:right"><span class="detail">${mediaFile.metaData.durationAsString}</span></td></c:if>
	<c:if test="${model.visibility.bitRateVisible}"><td style="padding-right:0.25em"><span class="detail"><c:if test="${not empty mediaFile.metaData.bitRate}">${mediaFile.metaData.bitRate} Kbps ${mediaFile.metaData.variableBitRate ? "vbr" : ""}</c:if>
	<c:if test="${mediaFile.video and not empty mediaFile.metaData.width and not empty mediaFile.metaData.height}">(${mediaFile.metaData.width}x${mediaFile.metaData.height})</c:if></span></td></c:if>
</tr>
</c:forEach>
</table>
