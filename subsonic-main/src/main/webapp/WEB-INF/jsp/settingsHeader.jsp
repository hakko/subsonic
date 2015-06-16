<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jspf" %>

<c:set var="categories" value="${param.restricted ? 'personal password player share' : 'mediaFolder general advanced personal user player share network transcoding internetRadio podcast tag search musicCabinet'}"/>

<ul class="nav nav-tabs nav-justified <spring:theme code="tabs.primary" />">
<c:forTokens items="${categories}" delims=" " var="cat" varStatus="loopStatus">

    <c:url var="url" value="#/${cat}Settings"/>

    <c:choose>
        <c:when test="${param.cat eq cat}">
            <li class="active"><a href="${url}"><fmt:message key="settingsheader.${cat}"/></a></li>
        </c:when>
        <c:otherwise>
            <li><a href="${url}"><fmt:message key="settingsheader.${cat}"/></a></li>
        </c:otherwise>
    </c:choose>

</c:forTokens>
</ul>


