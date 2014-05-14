<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jspf" %>

<c:set var="categories" value="${param.restricted ? 'personal password player share' : 'mediaFolder general advanced personal user player share network transcoding internetRadio podcast search musicCabinet'}"/>
<h1>
    <img src="<spring:theme code="settingsImage"/>" alt=""/>
    <fmt:message key="settingsheader.title"/>
</h1>

<ul class="nav nav-tabs nav-justified">
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


