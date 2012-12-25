<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jspf" %>
</head>
<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>

<script type="text/javascript">
	function search(page) {
		$('#songs').load('advancedSearchResult.view?page=' + page + '&' + $('form').serialize());
		window.scrollTo(0, 0);
	}
	
	function addOption(id) {
		$('#addOptions').append($('<option></option>').attr('value', id).text(id.replace(/_/g, ' ')));
	}

	$(document).ready(function() {
		$('tr').filter(':hidden').each(function() {
			addOption($(this).attr('id'));
		});

		$('tr').each(function() {
			$(this).prepend('<td><img class="rem" src="<spring:theme code="removeImage"/>" alt="Remove"></td>');
		});

		$('.rem').click(function() {
			tr = $(this).parent().parent();
			addOption(tr.attr('id'));
			tr.find('select').val('');
			tr.find('input').val('');
			tr.toggle();
		});

		$('#add').click(function() {
			if ($('#addOptions option').length > 0) {
				sel = $('#addOptions').find(':selected');
				$('#' + sel.attr('value')).toggle();
				sel.remove();
			}
		});

	});
</script>

<div style="padding:15px; width:80%">

	<h1>Advanced search</h1>

	<span style="display: block">
		<img id="add" src="<spring:theme code="plusImage"/>" alt="Add">
		<select id="addOptions">
		</select>
	</span>
	
	<form id="form" onsubmit="search(); return false;">
	<table style="padding-top:10px;">
		<tr id="Name">
			<td>Artist, album or song title</td>
			<td><input type="text" name="searchQuery" size="60" value="${model.searchQuery}"></td>
		</tr>
		<tr id="Artist" style="display:none">
			<td>Artist</td>
			<td><input type="text" name="artist" size="60"></td>
		</tr>
		<tr id="Album_artist" style="display:none">
			<td>Album artist</td>
			<td><input type="text" name="albumArtist" size="60"></td>
		</tr>
		<tr id="Composer" style="display:none">
			<td>Composer</td>
			<td><input type="text" name="composer" size="60"></td>
		</tr>
		<tr id="Album" style="display:none">
			<td>Album</td>
			<td><input type="text" name="album" size="60"></td>
		</tr>
		<tr id="Song_title" style="display:none">
			<td>Song title</td>
			<td><input type="text" name="title" size="60"></td>
		</tr>
		<tr id="Track_number" style="display:none">
			<td>Track number</td>
			<td>
				<select name="trackNrFrom">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="trackNrTo">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="Disc_number" style="display:none">
			<td>Disc number</td>
			<td>
				<select name="discNrFrom">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="discNrTo">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="Year" style="display:none">
			<td>Year</td>
			<td>
				<% int year = new org.joda.time.DateTime().getYear(); %>
				<select name="yearFrom">
				<option value=""></option>
				<% for (int i = year; i >= 1900; i--) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="yearTo">
				<option value=""></option>
				<% for (int i = year; i >= 1900; i--) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="Track_genre" style="display:none">
			<td>Track genre</td>
			<td>
				<select name="trackGenre"><option value=""></option>
					<c:forEach items="${model.trackGenres}" var="trackGenre"><option>${trackGenre}</option></c:forEach>
				</select>
			</td>
		</tr>
		<tr id="Duration" style="display:none">
			<td>Duration</td>
			<td>
				<select name="durationFrom">
					<option value=""></option>
					<option value="5">5 sec</option>
					<option value="10">10 sec</option>
					<option value="15">15 sec</option>
					<option value="30">30 sec</option>
					<% for (int i = 1; i < 10; i++) { %>
					<option value="<%=i*60%>"><%=i%> min</option>
					<% } %>
				</select>
				to
				<select name="durationTo">
					<option value=""></option>
					<option value="5">5 sec</option>
					<option value="10">10 sec</option>
					<option value="15">15 sec</option>
					<option value="30">30 sec</option>
					<% for (int i = 1; i < 10; i++) { %>
					<option value="<%=i*60%>"><%=i%> min</option>
					<% } %>
				</select>
			</td>
		</tr>
		<tr id="Media_folder" style="display:none">
			<td>Media folder</td>
			<td><select name="directory"><option value=""></option>
				<c:forEach items="${model.mediaFolders}" var="folder"><option value="${folder.path.path}">${folder.name}</c:forEach>
			</td>
		</tr>
		<tr id="Recently_changed">
			<td>Recently changed</td>
			<td><select name="modifiedDays">
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
		<tr id="Artist_genre">
			<td>Artist genre</td>
			<td>
				<select name="artistGenre"><option value=""></option>
					<c:forEach items="${model.topTags}" var="topTag"><option>${topTag}</option></c:forEach>
				</select>
			</td>
		</tr>
		<tr id="Top_track_rank" style="display:none">
			<td>Top track max rank</td>
			<td><select name="topTrackRank">
				<option value=""></option>
				<% for (int i = 1; i < 20; i++) { %><option><%=i%></option><% } %>
			</td>
		</tr>
		<tr id="Only_starred_tracks" style="display:none">
			<td>Only starred tracks</td>
			<td>
				<select name="onlyStarredByUser">
					<option value="false">No</option>
					<option value="true">Yes</option>
				</select>
			</td>
		</tr>
		<tr id="Recently_played">
			<td>Recently played</td>
			<td><select name="playedLastDays">
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
		<tr id="Play_count" style="display:none" style="display:none">
			<td>Play count</td>
			<td>
				<select name="playCountFrom">
				<option value=""></option>
				<% for (int i = 1; i < 10; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				<% for (int i = 10; i <= 100; i+=10) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="playCountTo">
				<option value=""></option>
				<% for (int i = 1; i < 10; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				<% for (int i = 10; i <= 100; i+=10) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="File_type" style="display:none">
			<td>File type(s)</td>
			<td><c:forEach items="${model.fileTypes}" var="fileType" varStatus="i"><input type="checkbox" name="fileType${i.count - 1}">${fileType}<c:if test="${i.count == 5}"><br></c:if></c:forEach>
			</td>
		</tr>
	</table>

	<input type="button" value="search" onClick="search(0)">

	<div id="songs" style="padding-top:10px"></div>

	</form>

</div>

</body></html>