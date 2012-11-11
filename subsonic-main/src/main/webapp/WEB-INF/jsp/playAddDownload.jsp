<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jspf" %>
<%--
PARAMETERS
  id: id of media file(s).
  video: Whether the file is a video (default false).
  playEnabled: Whether the current user is allowed to play songs (default true).
  addEnabled: Whether the current user is allowed to add songs to the playlist (default true).
  downloadEnabled: Whether the current user is allowed to download songs (default false).
  downloadName: Preferred name of download zip (optional)
  asTable: Whether to put the images in td tags.
--%>

<sub:url value="/download.view" var="downloadUrl"><sub:param name="id" value="${param.id}"/><sub:param name="name" value="${param.downloadName}"/></sub:url>
<c:if test="${empty param.starDisabled}">
    <c:if test="${param.asTable}"><td></c:if>
    <a href="#" onclick="toggleStar('t', ${param.starId}, '#starImage${param.starId}'); return false;">
        <c:choose>
            <c:when test="${param.starred}">
                <img id="starImage${param.starId}" src="<spring:theme code="ratingOnImage"/>" alt="">
            </c:when>
            <c:otherwise>
                <img id="starImage${param.starId}" src="<spring:theme code="ratingOffImage"/>" alt="">
            </c:otherwise>
        </c:choose>
    </a>
    <c:if test="${param.asTable}"></td></c:if>
</c:if>
<c:if test="${param.asTable}"><td></c:if><c:if test="${empty param.playEnabled or param.playEnabled}">
	<c:choose>
		<c:when test="${param.video}">
			<sub:url value="/videoPlayer.view" var="videoUrl"><sub:param name="id" value="${param.id}"/></sub:url>
			<a href="${videoUrl}" target="main"><img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a>
		</c:when>
		<c:otherwise><a href="javascript:noop()" onclick="top.playlist.onPlay(${param.id}, 'P');"><img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a></c:otherwise>
	</c:choose>
</c:if><c:if test="${param.asTable}"></td></c:if>
<c:if test="${param.asTable}"><td></c:if><c:if test="${param.enqueueEnabled and not param.video}"><a href="javascript:noop()" onclick="top.playlist.onPlay(${param.id}, 'E');"><img src="<spring:theme code="enqueueImage"/>" alt="Enqueue" title="Enqueue"></a></c:if><c:if test="${param.asTable}"></td></c:if>
<c:if test="${param.asTable}"><td></c:if><c:if test="${(empty param.addEnabled or param.addEnabled) and not param.video}"><a href="javascript:noop()" onclick="top.playlist.onPlay(${param.id}, 'A');"><img src="<spring:theme code="addImage"/>" alt="Add last" title="Add last"></a></c:if><c:if test="${param.asTable}"></td></c:if>
<c:if test="${param.asTable}"><td></c:if><c:if test="${param.downloadEnabled}">|<a href="${downloadUrl}"><img src="<spring:theme code="downloadImage"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"></a></c:if><c:if test="${param.asTable}"></td></c:if>