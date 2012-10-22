<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>
    <%@ include file="head.jspf" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
</head>
<body class="mainframe bgcolor1">

<div style="line-height: 1.5; padding: 15px;">

<c:if test="${not empty model.title}">
	<h1 style="text-transform:capitalize">${model.title}<c:if test="${model.page > 0}"> (page ${model.page + 1})</c:if></h1>
	<c:if test="${model.page == 0}">
		<c:if test="${not empty model.genreDescription}"><div style="width:650px">${model.genreDescription}</div></c:if>
		<c:choose><c:when test="${not empty model.genre}">
			<a href="javascript:noop()" onclick="javascript:top.playlist.onPlayGenreRadio(new Array('${model.genre}'))">Play ${model.title} radio</a>
		</c:when><c:otherwise>
			<a href="javascript:noop()" onclick="javascript:top.playlist.onPlayGroupRadio('${model.group}')">Play group radio</a>
		</c:otherwise></c:choose>
		| <a href="${model.url}">Browse last.fm</a>
		<h1 style="margin-top: 15px">Top artists</h1>
	</c:if>

	<%@ include file="artists.jspf" %>
 	
    <sub:url value="genres.view" var="prevUrl">
        <sub:param name="genre" value="${model.genre}"/>
        <sub:param name="group" value="${model.group}"/>
        <sub:param name="page" value="${model.page - 1}"/>
    </sub:url>
    <sub:url value="genres.view" var="nextUrl">
        <sub:param name="genre" value="${model.genre}"/>
        <sub:param name="group" value="${model.group}"/>
        <sub:param name="page" value="${model.page + 1}"/>
    </sub:url>

	<br/>
	<c:if test="${model.page > 0}"><div class="back"><a href="${prevUrl}"><fmt:message key="common.previous"/></a></div></c:if>
	<c:if test="${not empty model.morePages}"><div class="forward"><a href="${nextUrl}"><fmt:message key="common.next"/></a></div></c:if>
 	
	<%@ include file="artistRecommendation.jspf" %>
</c:if>

<c:if test="${empty model.title}">
	<c:forEach items="${model.topTagsOccurrences}" var="topTagOccurrence">
	<span style="font-size: ${topTagOccurrence.occurrence}px;">
		<sub:url var="url" value="genres.view">
			<sub:param name="genre" value="${topTagOccurrence.tag}"/>
		</sub:url>
		<a href="${url}">${topTagOccurrence.tag}</a>
	</span>
	</c:forEach>
	<c:if test="${not empty model.lastFmGroups}"><hr width="10%"></c:if>
	<c:forEach items="${model.lastFmGroups}" var="group" varStatus="i">
		<sub:url var="url" value="genres.view">
			<sub:param name="group" value="${group.name}"/>
		</sub:url>
		<c:if test="${i.count > 1}">|</c:if>
		<a href="${url}">${group.name}</a>
	</c:forEach>
	<c:if test="${empty model.topTagsOccurrences and empty model.lastFmGroups}">
		<p>Please configure which genres to use <a href="tagSettings.view">here</a>.
	</c:if>
</c:if>

</div>

</body></html>