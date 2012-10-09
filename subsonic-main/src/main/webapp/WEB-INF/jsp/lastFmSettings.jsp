<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jspf" %>

</head>
<body class="mainframe bgcolor1">

<c:choose>
	<c:when test="${empty model.token}">
	<script type="text/javascript">
		window.location.replace('http://www.last.fm/api/auth/?api_key=${model.api_key}&cb=${model.callbackUrl}');
	</script>
	</c:when>
	<c:otherwise>
		<div style="padding: 15px;">
		<h1>Confirm last.fm scrobbling</h1>
		Press OK to have your tracks scrobbled to last.fm as <b>${model.lastFmUsername}</b>. This can be revoked at any time from your personal settings.
		<form>
			<input type="hidden" name="lastFmUsername" value="${model.lastFmUsername}"/>
			<input type="hidden" name="sessionKey" value="${model.sessionKey}"/>
			<input type="submit" value="OK"/>
		</form>
	</c:otherwise>
</c:choose>

</body></html>