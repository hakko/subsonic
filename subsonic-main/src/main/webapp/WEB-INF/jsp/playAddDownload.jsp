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
<div class="btn-group">
<sub:url value="/download.view" var="downloadUrl"><sub:param name="id" value="${param.uri}"/><sub:param name="name" value="${param.downloadName}"/></sub:url>
<c:if test="${empty param.starDisabled}">
    <c:if test="${param.asTable}"><td class="play-add-download btn-group"></c:if>
    <a href="#" class="btn btn-default btn-star" onclick="toggleStar('t', ${sub:esc(param.starId)}, '${"#starImage".concat(sub:jqesc(param.starId))}'); return false;">
        <c:choose>
            <c:when test="${param.starred}">
                <img id="starImage${param.starId}" src="<spring:theme code="ratingOnImage"/>" alt="">
            </c:when>
            <c:otherwise>
                <img id="starImage${param.starId}" src="<spring:theme code="ratingOffImage"/>" alt="">
            </c:otherwise>
        </c:choose>
    </a>
</c:if>
<c:if test="${empty param.playEnabled or param.playEnabled}">
	<c:choose>
		<c:when test="${param.video}">
			<sub:url value="/videoPlayer.view" var="videoUrl"><sub:param name="id" value="${param.uri}"/></sub:url>
			<a class="btn btn-play-dd btn-default" href="${videoUrl}"><img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a>
		</c:when>
		<c:otherwise><a href="#" class="btn btn-play-dd btn-default" onclick="return onPlay(${param.uri}, 'P');"><img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a></c:otherwise>
	</c:choose>
                <button type="button" class="btn btn-play-dd btn-default dropdown-toggle" data-toggle="dropdown">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>           
	
</c:if>
<ul class="dropdown-menu" role="menu">
<c:if test="${empty param.playEnabled or param.playEnabled}">
    <c:choose>
        <c:when test="${param.video}">
            <sub:url value="/videoPlayer.view" var="videoUrl"><sub:param name="id" value="${param.uri}"/></sub:url>
            <li><a href="${videoUrl}"><img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"><fmt:message key="common.play"/></a></li>
        </c:when>
        <c:otherwise><li><a href="#" onclick="return onPlay(${param.uri}, 'P');"><img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"><fmt:message key="common.play"/></a></li></c:otherwise>
    </c:choose>

</c:if>
<c:if test="${param.enqueueEnabled and not param.video}"><li><a href="#" onclick="return onPlay(${param.uri}, 'E');"><img src="<spring:theme code="enqueueImage"/>" alt="Enqueue" title="Enqueue">Enqueue</a></li></c:if>
<c:if test="${(empty param.addEnabled or param.addEnabled) and not param.video}"><li><a href="#" onclick="return onPlay(${param.uri}, 'A');"><img src="<spring:theme code="addImage"/>" alt="Add last" title="Add last">Add last</a></li></c:if>
<c:if test="${param.downloadEnabled}"><li><a href="${downloadUrl}"><img src="<spring:theme code="downloadImage"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"><fmt:message key="common.download"/></a></li></c:if>
</ul>
</div>