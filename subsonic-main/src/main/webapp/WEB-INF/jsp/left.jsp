<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>
<%@ include file="include.jspf"%>



<div class="bgcolor2 leftframe">

	<ul class="nav nav-tabs">
		<li class="${empty model.filebased ? 'active' : ''}"><a
			href="left.view?method=media">Media</a></li>
		<li class="${not empty model.filebased ? 'active' : ''}"><a
			href="left.view?method=file">Files</a></li>
	</ul>

	<div>
		<c:forEach items="${model.indexes}" var="index">
			<a href="#${index.key}" accesskey="${index.key}">${index.key}</a>
		</c:forEach>
	</div>


	<c:if test="${not empty model.statistics}">
		<div class="detail">
			<fmt:message key="left.statistics">
				<fmt:param value="${model.statistics.artistCount}" />
				<fmt:param value="${model.statistics.albumCount}" />
				<fmt:param value="${model.statistics.trackCount}" />
				<fmt:param value="${model.statisticsBytes}" />
				<fmt:param value="${model.statistics.totalLengthInHours}" />
			</fmt:message>
		</div>
	</c:if>
	<div class="inner-scroll">
		<c:if test="${not empty model.filebased}">
			<c:if test="${model.indexing eq true}">Your library is being scanned.<br />
			</c:if>
			<c:if test="${model.indexing eq false}">Before you can use the full graphic interface, a library scan must be performed.<br />
			</c:if>
			<a href="musicCabinetSettings.view">Learn more.</a>
			<div id="leftMessage"></div>
	</div>
	</c:if>



	<c:if test="${not empty model.radios}">
		<h2 class="bgcolor1">
			<fmt:message key="left.radio" />
		</h2>
		<c:forEach items="${model.radios}" var="radio">
			<p class="dense">
				<a href="${radio.streamUrl}"> <img
					src="<spring:theme code="playImage"/>"
					alt="<fmt:message key="common.play"/>"
					title="<fmt:message key="common.play"/>"></a>
				<c:choose>
					<c:when test="${empty radio.homepageUrl}">
					${radio.name}
				</c:when>
					<c:otherwise>
						<a href="${radio.homepageUrl}">${radio.name}</a>
					</c:otherwise>
				</c:choose>
			</p>
		</c:forEach>
	</c:if>

	<c:if test="${not empty model.indexes or not empty model.currentTag}">
		<a name="left-top"><h2 class="bgcolor1">Artists</h2></a>
		<c:if test="${not empty model.tags}">
			<select id="tag">
				<c:forEach items="${model.tags}" var="tag">
					<sub:url value="left.view" var="leftUrl">
						<sub:param name="tag" value="${tag}" />
					</sub:url>
					<option value="${leftUrl}"
						<c:if test="${tag eq model.currentTag}"> selected</c:if>>${fn:escapeXml(tag)}</option>
				</c:forEach>
			</select>
			<c:if test="${not empty model.currentTag}">
				<br>
				<a href="#"
					onclick="return playlist.onPlayGenreRadio(new Array('${model.currentTag}'))">Play
					${model.currentTag} radio</a>
			</c:if>
		</c:if>
		<div id="leftMessage"></div>

		<c:forEach items="${model.indexes}" var="index">
			<table class="bgcolor1">
				<tr>
					<th><a name="${index.key}"></a>
						<h2>
							<c:if test="${model.reluctantArtistLoading}">
								<a href="left.view?indexLetter=${fn:replace(index.key,'#','0')}">
							</c:if>
							${index.key}
							<c:if test="${model.reluctantArtistLoading}">
								</a>
							</c:if>
						</h2></th>
					<th><a href="#left-top"><img
							src="<spring:theme code="upImage"/>" alt=""></a></th>
				</tr>
			</table>

			<c:forEach items="${index.value}" var="artist">
				<p class="dense">
					<span title="${fn:escapeXml(artist.name)}"> <sub:url
							value="artist.view" var="artistUrl">
							<sub:param name="id" value="${artist.uri}" />
						</sub:url> <a href="${artistUrl}">${fn:escapeXml(artist.name)}</a>
					</span>
				</p>
			</c:forEach>
		</c:forEach>
		<a name="bottom"></a>
	</c:if>

	<c:if test="${not empty model.variousArtistsAlbums}">
		<h2 class="bgcolor1">Various Artists</h2>
		<c:forEach items="${model.variousArtistsAlbums}" var="album">
			<p class="dense">
				<sub:url value="artist.view" var="albumUrl">
					<sub:param name="id" value="${album.artist.uri}" />
					<sub:param name="albumId" value="${album.uri}" />
				</sub:url>
				<a href="${albumUrl}">${album.name}</a>
			</p>
		</c:forEach>
	</c:if>

	<c:if test="${not empty model.mediaFolders}">
		<h2 class="bgcolor1">Media folders</h2>
		<c:forEach items="${model.mediaFolders}" var="mediaFolder">
			<p class="dense">
				<sub:url value="main.view" var="mainUrl">
					<sub:param name="path" value="${mediaFolder.path}" />
				</sub:url>
				<a href="${mainUrl}">${mediaFolder.name}</a>
			</p>
		</c:forEach>
	</c:if>

	<c:if test="${empty model.filebased}">
		<div></div>
		<hr>
		<c:if test="${model.uploadRole}">
			<a href="more.view">Upload new music</a>
			<br>
		</c:if>
		<c:if test="${model.adminRole}">
			<a href="missingAlbums.view">Missing albums</a>
			<br>
		</c:if>

	</c:if>

	<div></div>


	<c:if
		test="${not empty model.reluctantArtistLoading and not empty model.indexedLetter}">
		<script type="text/javascript">
window.location.hash='${fn:replace(model.indexedLetter,'#','#bottom')}';
</script>
	</c:if>

</div>


<div class="bgcolor2">
	<c:forEach items="${model.indexes}" var="index">
		<a href="#${index.key}" accesskey="${index.key}">${index.key}</a>
	</c:forEach>
</div>


<script type="text/javascript">
        (function($) {
            $('#tag').change(function() {
              $(".left").load($(this).val());
            });
        }(jQuery));
	</script>
</div>

