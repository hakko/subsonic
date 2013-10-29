<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

    <%@ include file="include.jspf" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
<div class="mainframe bgcolor1">

<%@ include file="toggleStar.jspf" %>


<h1>
    <img src="<spring:theme code="homeImage"/>" alt="">
    ${model.welcomeTitle}
</h1>

<c:if test="${not empty model.welcomeSubtitle}">
    <h2>${model.welcomeSubtitle}</h2>
</c:if>

<c:if test="${model.isIndexBeingCreated}">
    <p class="warning"><fmt:message key="home.scan"/></p>
</c:if>

<c:if test="${empty model.lastFmUser}">
	Last.fm scrobbling not configured! For full statistics and personal music recommendations, <a href="lastFmSettings.view">click here</a>.
</c:if>

<h2>
    <c:forTokens items="newest recent frequent starred topartists random recommended" delims=" " var="cat" varStatus="loopStatus">
        <c:if test="${loopStatus.count > 1}">&nbsp;|&nbsp;</c:if>
        <sub:url var="url" value="home.view">
            <sub:param name="listType" value="${cat}"/>
        </sub:url>

        <c:choose>
            <c:when test="${model.listType eq cat}">
                <span class="headerSelected"><fmt:message key="home.${cat}.title"/></span>
            </c:when>
            <c:otherwise>
                <a href="${url}"><fmt:message key="home.${cat}.title"/></a>
            </c:otherwise>
        </c:choose>

    </c:forTokens>
</h2>

<c:if test="${not model.listType eq 'topartists'}"><h2><fmt:message key="home.${model.listType}.text"/></h2></c:if>

<table width="100%">
    <tr>
        <td style="vertical-align:top;">

<c:if test="${model.listType eq 'newest'}"><%@ include file="homeQuery.jspf" %><%@ include file="homeAlbums.jspf" %></c:if>
<c:if test="${model.listType eq 'recent' or model.listType eq 'frequent' or model.listType eq 'starred' or model.listType eq 'random'}">
	<%@ include file="homeArtistAlbumSongMenu.jspf" %>
</c:if>
<c:if test="${model.listType eq 'topartists'}"><%@ include file="homeTopArtists.jspf" %></c:if>
<c:if test="${model.listType eq 'recommended'}"><%@ include file="homeArtists.jspf" %><%@ include file="artistRecommendation.jspf" %></c:if>

        </td>
            <c:if test="${not empty model.welcomeMessage}">
                <td style="vertical-align:top;width:20em">
                    <div style="padding:0 1em 0 1em;border-left:1px solid #<spring:theme code="detailColor"/>">
                        <sub:wiki text="${model.welcomeMessage}"/>
                    </div>
                </td>
            </c:if>
        </tr>
    </table>
<script type="text/javascript" language="javascript">
        dwr.engine.setErrorHandler(null);
</script>
</div>
