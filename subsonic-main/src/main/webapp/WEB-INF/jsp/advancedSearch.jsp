<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

    <%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<script type="text/javascript">
	function addOption(id) {
		jQuery('#addOptions').append(jQuery('<option></option>').attr('value', id).text(id.replace(/_/g, ' ')));
	}

	jQuery(document).ready(function($) {
		$('.advanced-search').find('tr').filter(':hidden').each(function() {
			addOption($(this).attr('id'));
		});

		$('.advanced-search').find('tr').each(function() {
			$(this).prepend('<td><img class="rem" src="<spring:theme code="removeImage"/>" alt="Remove"></td>');
		});

		$('.advanced-search').find('.rem').click(function() {
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

	}(jQuery));
</script>

<div style="padding:15px; width:80%">
<h1>
  <img src="<spring:theme code="searchImage"/>" alt=""/>
	Advanced search
	</h1>

<form id="form" role="form" onsubmit="return search(this, 0);">
	<div class="panel panel-default">
	<div class="panel-heading">Search Criteria</div>
  <div class="panel-body">
	
	<table style="padding-top:10px;" class="advanced-search">
		<tr id="Name" class="form-group">
			<td><label for="searchQuery">Artist, album or song title</label></td>
			<td><input type="text" class="form-control" name="searchQuery" size="60" value="${model.searchQuery}"></td>
		</tr>
		<tr id="Artist" class="form-group" style="display:none">
			<td><label for="artist">Artist</label></td>
			<td><input type="text" class="form-control" name="artist" size="60"></td>
		</tr>
		<tr id="Album_artist" class="form-group" style="display:none">
			<td><label for="albumArtist">Album artist</a></td>
			<td><input type="text" class="form-control" name="albumArtist" size="60"></td>
		</tr>
		<tr id="Composer" class="form-group" style="display:none">
			<td><label for="composer">Composer</label></td>
			<td><input type="text" class="form-control" name="composer" size="60"></td>
		</tr>
		<tr id="Album" class="form-group" style="display:none">
			<td><label for="album">Album</label></td>
			<td><input type="text" class="form-control" name="album" size="60"></td>
		</tr>
		<tr id="Song_title" class="form-group" style="display:none">
			<td><label for="title">Song title</label></td>
			<td><input type="text" class="form-control" name="title" size="60"></td>
		</tr>
		<tr id="Track_number" class="form-group" style="display:none">
			<td><label for="trackNrFrom">Track number</label></td>
			<td>
				<select name="trackNrFrom" class="form-control">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="trackNrTo" class="form-control">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="Disc_number" class="form-group" style="display:none">
			<td><label for="discNrFrom">Disc number</label></td>
			<td>
				<select name="discNrFrom" class="form-control">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="discNrTo" class="form-control">
				<option value=""></option>
				<% for (int i = 1; i < 99; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="Year" class="form-group" style="display:none">
			<td><label for="yearTo">Year</label></td>
			<td>
				<% int year = new org.joda.time.DateTime().getYear(); %>
				<select name="yearFrom" class="form-control">
				<option value=""></option>
				<% for (int i = year; i >= 1900; i--) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="yearTo" class="form-control">
				<option value=""></option>
				<% for (int i = year; i >= 1900; i--) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="Track_genre" class="form-group" style="display:none">
			<td><label for="trackGenre">Track genre</label></td>
			<td>
				<select name="trackGenre" class="form-control"><option value=""></option>
					<c:forEach items="${model.trackGenres}" var="trackGenre"><option>${trackGenre}</option></c:forEach>
				</select>
			</td>
		</tr>
		<tr id="Duration" class="form-group" style="display:none">
			<td><label for="durationFrom">Duration</label></td>
			<td>
				<select name="durationFrom" class="form-control">
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
				<select name="durationTo" class="form-control">
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
		<tr id="Media_folder" class="form-group" style="display:none">
			<td><label for="directory">Media folder</label></td>
			<td><select name="directory" class="form-control"><option value=""></option>
				<c:forEach items="${model.mediaFolders}" var="folder"><option value="${folder.path.path}">${folder.name}</c:forEach>
			</td>
		</tr>
		<tr id="Recently_changed" class="form-group" style="display:none">
			<td><label for="modifiedDays">Recently changed</label></td>
			<td><select name="modifiedDays" class="form-control">
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
		<tr id="Artist_genre" class="form-group" style="display:none">
			<td><label for="artistGenre">Artist genre</label></td>
			<td>
				<select name="artistGenre" class="form-control"><option value=""></option>
					<c:forEach items="${model.topTags}" var="topTag"><option>${topTag}</option></c:forEach>
				</select>
			</td>
		</tr>
		<tr id="Top_track_rank" class="form-group" style="display:none">
			<td><label for="topTrackRank">Top track max rank</label></td>
			<td><select name="topTrackRank" class="form-control">
				<option value=""></option>
				<% for (int i = 1; i < 20; i++) { %><option><%=i%></option><% } %>
			</td>
		</tr>
		<tr id="Only_starred_tracks" style="display:none">
			<td><label for="onlyStarredByUser">Only starred tracks</label></td>
			<td>
				<select name="onlyStarredByUser" class="form-control">
					<option value="false">No</option>
					<option value="true">Yes</option>
				</select>
			</td>
		</tr>
		<tr id="Recently_played" class="form-group" style="display:none">
			<td><label for="playedLastDays">Recently played</label></td>
			<td><select name="playedLastDays" class="form-control">
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
		<tr id="Play_count" class="form-group" style="display:none">
			<td><label for="playCountFrom">Play count</label></td>
			<td>
				<select name="playCountFrom" class="form-control">
				<option value=""></option>
				<% for (int i = 1; i < 10; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				<% for (int i = 10; i <= 100; i+=10) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
				to
				<select name="playCountTo" class="form-control">
				<option value=""></option>
				<% for (int i = 1; i < 10; i++) { %><option value="<%=i%>"><%=i%></option><% } %>
				<% for (int i = 10; i <= 100; i+=10) { %><option value="<%=i%>"><%=i%></option><% } %>
				</select>
			</td>
		</tr>
		<tr id="File_type" class="form-group" style="display:none">
			<td><label>File type(s)</label></td>
			<td><c:forEach items="${model.fileTypes}" var="fileType" varStatus="i"><input class="form-control" type="checkbox" name="fileType${i.count - 1}">${fileType}<c:if test="${i.count == 5}"><br></c:if></c:forEach>
			</td>
		</tr>
	</table>
	</div>
	<div class="panel-footer">
  
    <label for="addOptions"><img id="add" src="<spring:theme code="plusImage"/>" alt="Add" />Add Criterion</label>
    <select id="addOptions" class="form-control">
    </select>
  
	
	</div>
	</div>
	
	

	<input type="submit" value="Search" class="btn btn-default">
	
	<div id="songs" style="padding-top:10px"></div>
	</form>
	


</div>
</div>
