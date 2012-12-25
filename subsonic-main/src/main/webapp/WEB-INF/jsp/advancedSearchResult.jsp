<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--@elvariable id="model" type="java.util.Map"--%>

<html><head>
	<%@ include file="head.jspf" %>
</head><body class="mainframe bgcolor1">

<h2>
<a href="javascript:noop()" onclick="top.playlist.onPlay(${model.trackIds}, 'P');">Play all</a> |
<a href="javascript:noop()" onclick="top.playlist.onPlay(${model.trackIds}, 'E');">Enqueue all</a> |
<a href="javascript:noop()" onclick="top.playlist.onPlay(${model.trackIds}, 'A');">Add all</a>
</h2>

<%@ include file="songs.jspf" %>

<c:if test="${empty model.mediaFiles}">(no results found)</c:if>

<c:if test="${model.page > 0}"><div class="back"><a href="javascript:void(0)" onclick="javascript:window.search(${model.page - 1}); return false;"><fmt:message key="common.previous"/></a></div></c:if>
<c:if test="${not empty model.morePages}"><div class="forward"><a href="javascript:void(0)" onclick="javascript:window.search(${model.page + 1}); return false;"><fmt:message key="common.next"/></a></div></c:if>

</body>
</html>