<?xml version="1.0" encoding="utf-8"?>
<%@ include file="include.jspf" %>
<%@ page language="java" contentType="text/xml; charset=utf-8" pageEncoding="iso-8859-1" %>

<playlist version="0" xmlns="http://xspf.org/ns/0/">
    <trackList>

<c:forEach var="song" items="${model.songs}">

    <sub:url value="/stream" var="streamUrl">
        <sub:param name="path" value="${song.mediaFile.path}"/>
    </sub:url>

    <sub:url value="coverArt.view" var="coverArtUrl">
        <sub:param name="size" value="200"/>
        <c:if test="${not empty song.coverArtFile}">
            <sub:param name="path" value="${song.coverArtFile.path}"/>
        </c:if>
    </sub:url>

        <track>
            <location>${streamUrl}</location>
            <image>${coverArtUrl}</image>
            <annotation>${song.mediaFile.metaData.artist} - ${song.mediaFile.title}</annotation>
        </track>

</c:forEach>

    </trackList>
</playlist>