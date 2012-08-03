<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>
    <%@ include file="head.jsp" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
</head>
<body class="mainframe bgcolor1">

<div style="line-height: 1.5; padding: 15px;">

<c:choose>
	<c:when test="${not empty model.genre}">
	<h1 style="text-transform:capitalize">${model.genre}<c:if test="${model.page > 0}"> (page ${model.page + 1})</c:if></h1>
	<c:if test="${model.page == 0}">
		<div style="width:650px">${model.genreDescription}</div>
		<a href="javascript:noop()" onclick="javascript:top.playlist.onPlayGenreRadio(new Array('${model.genre}'))">Play ${model.genre} radio</a> |
		<a href="http://www.last.fm/tag/${model.genre}">Browse last.fm</a>
		<h1 style="margin-top: 15px">Top artists</h1>
	</c:if>

<%@ include file="artists.jsp" %>
 	
    <sub:url value="genres.view" var="prevUrl">
        <sub:param name="genre" value="${model.genre}"/>
        <sub:param name="page" value="${model.page - 1}"/>
    </sub:url>
    <sub:url value="genres.view" var="nextUrl">
        <sub:param name="genre" value="${model.genre}"/>
        <sub:param name="page" value="${model.page + 1}"/>
    </sub:url>

	<br/>
	<c:if test="${model.page > 0}"><div class="back"><a href="${prevUrl}"><fmt:message key="common.previous"/></a></div></c:if>
	<c:if test="${not empty model.morePages}"><div class="forward"><a href="${nextUrl}"><fmt:message key="common.next"/></a></div></c:if>
 	
	</c:when>
    <c:when test="${empty model.topTagsOccurrences}">
    	<p>Please configure which genres to use <a href="tagSettings.view">here</a>.
    </c:when>
    <c:otherwise>
		<c:forEach items="${model.topTagsOccurrences}" var="topTagOccurrence">
		<span style="font-size: ${topTagOccurrence.occurrence}px;">
	        <sub:url var="url" value="genres.view">
    	        <sub:param name="genre" value="${topTagOccurrence.tag}"/>
	        </sub:url>
			<a href="${url}">${topTagOccurrence.tag}</a>
		</span>
		</c:forEach>
	</c:otherwise>
</c:choose>

</div>

</body></html>