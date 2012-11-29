<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--@elvariable id="model" type="java.util.Map"--%>

<html><head>
	<%@ include file="head.jspf" %>
</head><body class="mainframe bgcolor1">

<table>
<c:forEach items="${model.missingAlbums}" var="album" varStatus="i">
	<tr>
		<td>${album.artist.name}</td>
		<td>${album.title}</td>
		<td>${album.firstReleaseYear > 0 ? album.firstReleaseYear : ""}</td>
		<td>${album.albumTypeName}<c:if test="${not empty album.format}">, ${album.format}</c:if></td>
	</tr>
</c:forEach>
</table>

<c:if test="${model.page > 0}"><div class="back"><a href="javascript:void(0)" onclick="javascript:window.search(${model.page - 1}); return false;"><fmt:message key="common.previous"/></a></div></c:if>
<c:if test="${not empty model.morePages}"><div class="forward"><a href="javascript:void(0)" onclick="javascript:window.search(${model.page + 1}); return false;"><fmt:message key="common.next"/></a></div></c:if>

</body>
</html>