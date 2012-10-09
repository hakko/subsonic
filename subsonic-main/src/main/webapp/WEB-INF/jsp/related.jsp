<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>
    <%@ include file="head.jspf" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
</head>
<body class="mainframe bgcolor1">

<div style="padding: 15px;">

<sub:url value="artist.view" var="artistUrl"><sub:param name="id" value="${model.id}"/></sub:url>

<h1>${model.artist}</h1>
<table>
	<tr>
		<td style="vertical-align:top">
			<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
				<a href="${artistUrl}">
					<img width="126" height="126" src="${model.artistInfo.largeImageUrl}" alt="">
				</a>
			</div></div></div></div>
		</td>
		<td style="vertical-align:top">
			<div style="width:525px;">
				${model.artistInfo.bioSummary}
			</div>
		</td>
	</tr>
</table>

<br>
<h1><a href="javascript:noop()" onclick="top.playlist.onPlayRelatedArtistsSampler(${model.id}, ${fn:length(model.artists)});">
		<img src="<spring:theme code="playImage"/>" alt="Play related artists sampler" title="Play related artists sampler">
	</a>Related artists
</h1>

<c:if test="${empty model.artists}"><p>Not a single related artist found!</p></c:if>
<%@ include file="artists.jspf" %>

<%@ include file="artistRecommendation.jspf" %>

</div>

</body></html>