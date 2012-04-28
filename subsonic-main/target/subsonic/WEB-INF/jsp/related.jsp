<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>
    <%@ include file="head.jsp" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
</head>
<body class="mainframe bgcolor1">

<div style="padding: 15px;">

<sub:url value="main.view" var="mainUrl"><sub:param name="path" value="${model.path}"/></sub:url>
<!--<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>-->

<h1>${model.artist}</h1>
<table>
	<tr>
		<td style="vertical-align:top">
			<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
				<a href="${mainUrl}">
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
<h1>Related artists:</h1>

<c:if test="${empty model.artistsInLibrary}">Not a single related artist found!</c:if>
<table>
	<c:forEach items="${model.artistsInLibrary}" var="artistRecommendation" varStatus="loopStatus">
        <c:if test="${loopStatus.count % 5 == 1}">
	        <tr>
   	     </c:if>
        <sub:url var="url" value="main.view">
	        <sub:param name="path" value="${artistRecommendation.path}"/>
        </sub:url>
		<td style="vertical-align:top">
			<a href="${url}">
				<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
					<img width="126" height="126" src="${artistRecommendation.imageUrl}" alt="">
				</div></div></div></div>
				<div style="detail">
					<div style="width:108px;float:left">
						${artistRecommendation.artistName}
					</div>
					<div style="width:18px;float:right">
					    <c:set var="path">
					        <sub:escapeJavaScript string="${artistRecommendation.path}"/>
	 					</c:set>
						<a href="javascript:noop()" onclick="top.playlist.onPlayTopTracks('${path}', 'P');">
			                <img src="<spring:theme code="playImage"/>" alt="Play top tracks" title="Play top tracks">
						</a>
					</div>
				</div>
			</a>
		</td>
        <c:if test="${loopStatus.count % 5 == 0}">
            </tr>
        </c:if>
	</c:forEach>
</table>

<c:if test="${not empty model.artistsNotInLibrary}">
<br>
<h1>You might also like:</h1>
<ul>
	<c:forEach items="${model.artistsNotInLibrary}" var="recommendedArtist" varStatus="loopStatus">
		<li><a href="http://last.fm/music/${recommendedArtist.path}">${recommendedArtist.artistName}</a></li>
	</c:forEach>
<ul>
</c:if>

</div>

</body></html>