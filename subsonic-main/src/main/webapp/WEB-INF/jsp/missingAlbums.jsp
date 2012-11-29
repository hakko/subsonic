<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jspf" %>
	<style type="text/css">
		table td {
		  border-right:solid 20px transparent;
		}	
	</style>
</head>
<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>

<script type="text/javascript">
	function search(page) {
		$('#albums').load('missingAlbumsSearch.view?page=' + page + '&' + $('form').serialize());
		window.scrollTo(0, 0);
	}
</script>

<div style="padding:15px; width:80%">

<c:choose>
	<c:when test="${model.isIndexBeingCreated}">
		<p><i>Fetching information from MusicBrainz. ${model.progressDescription}</i>
		<a href="missingAlbums.view">(refresh)</a></p>
	</c:when>
	
	<c:otherwise>
		<c:if test="${model.missingAndOutdatedCount > 0}">
			<p>We're missing MusicBrainz information for ${model.missingAndOutdatedCount} artists.
			<div class="forward"><a href="missingAlbums.view?update=artists">Update now</a></div>
		</c:if>
		<c:if test="${model.missingAndOutdatedCount > 0 && model.hasDiscography}">
			<div style="padding-top: 30px; padding-bottom: 30px"><hr width="10%"></div>
		</c:if>
		<c:if test="${model.hasDiscography}">
			<h1>MusicBrainz discography</h1>
			Find official studio albums, missing in your local library:

			<form id="form" onsubmit="search(); return false;">
			<table style="padding-top:10px;">
				<tr>
					<td>Artist name</td>
					<td><input type="text" name="artistName" size="60"></td>
				</tr>
				<tr>
					<td>Type(s)</td>
					<td><input type="checkbox" name="album" checked="checked">Album
						<input type="checkbox" name="ep" checked="checked">EP
						<input type="checkbox" name="single">Single
					</td>
				</tr>
				<tr>
					<td>Recently played</td>
					<td><select name="playedWithinLastDays">
						<option value=""></option>
						<% for (int i = 1; i < 10; i++) { %>
						<option value="<%=i%>">last <%=i%> days</option>
						<% } %>
						<option value="14">last two weeks</option>
						<option value="21">last three weeks</option>
						<option value="30">last month</option>
						<option value="90">last three months</option>
						<option value="180">last six months</option>
						<option value="365">last year</option>
					</td>
				</tr>
			</table>
			
			<input type="button" value="search" onClick="search(0)">

			<div id="albums" style="padding-top:10px"></div>

			</form>
		</c:if>
	</c:otherwise>
</c:choose>

</div>

</body></html>