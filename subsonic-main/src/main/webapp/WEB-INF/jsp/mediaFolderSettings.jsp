<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>

<%@ include file="include.jspf"%>
<div class="mainframe bgcolor1">

	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="mediaFolder" />
	</c:import>

	<form method="post" action="mediaFolderSettings.view"
		onsubmit="return submitForm(this, 'Settings saved.');">
		<div class="statusMessage"></div>
		<div class="panel panel-default">
			<div class="panel-heading">Media Folders</div>
			<table class="table table-striped table-hover table-condensed">
				<tr>
					<th><fmt:message key="mediaFoldersettings.name" /></th>
					<th><fmt:message key="mediaFoldersettings.path" /></th>
					<th style="padding-left: 1em"><fmt:message
							key="mediaFoldersettings.indexed" /></th>
					<th style="padding-left: 1em"><fmt:message key="common.delete" /></th>
				</tr>

				<c:forEach items="${model.mediaFolders}" var="folder">
					<tr>
						<td><input type="text" name="name[${folder.id}]" size="20"
							value="${folder.name}" /></td>
						<td><input type="text" name="path[${folder.id}]" size="40"
							value="${folder.path.path}" /></td>
						<td align="center" style="padding-left: 1em"><input
							type="checkbox" ${folder.indexed ? "checked" : ""}
							name="indexed[${folder.id}]" class="checkbox" /></td>
						<td align="center" style="padding-left: 1em"><input
							type="checkbox" name="delete[${folder.id}]" class="checkbox"
							<c:if test="${model.indexBeingCreated}">disabled title="Media folders cannot be deleted during library scanning."</c:if> /></td>
					</tr>
					<tr>
						<td><input type="text" name="name" size="20" /></td>
						<td><input type="text" name="path" size="40" /></td>
						<td align="center" style="padding-left: 1em"><input
							name="indexed" checked type="checkbox" class="checkbox" /></td>
						<td />
					</tr>
				</c:forEach>
			</table>
			<div class="panel-footer">
				<button type="submit" class="btn btn-primary">
					<fmt:message key="common.save" />
				</button>
				<button type="button"
					onclick="jQuery('.main').load('nowPlaying.view')"
					class="btn btn-default">
					<fmt:message key="common.cancel" />
				</button>
			</div>
		</div>


		<c:if test="${not empty model.error}">
			<p class="warning">
				<fmt:message key="${model.error}" />
			</p>
		</c:if>

		<c:if test="${not model.databaseAvailable}">
			<p style="padding-top: 1em">
				<b>MusicCabinet configuration</b>
			</p>
			<p>
				MusicCabinet configuration isn't completed. Please finish it <a
					href="musicCabinetSettings.view">here</a> before scanning media
				folders.
			</p>
		</c:if>

		<c:if
			test="${model.databaseAvailable and not model.indexBeingCreated}">
			<div class="panel panel-default">
				<div class="panel-heading">
					Scan media folders
					<c:import url="helpToolTip.jsp">
						<c:param name="topic" value="searchsettingsscan" />
					</c:import>
				</div>
				<div class="panel-body">
					<div class="button-group">
						<a class="btn btn-default" 
							href="#/searchSettings/update/offline">Offline scan</a> <a
							class="btn btn-default" 
							href="#/searchSettings/update/normal">Normal scan</a> <a
							class="btn btn-default" 
							href="#/searchSettings/update/full">Full scan</a>
					</div>

				</div>
			</div>
		</c:if>

		<c:if test="${model.spotifyAvailable}">
			<div class="panel panel-default">
				<div class="panel-heading">Spotify</div>
				<div class="panel-body">
					<p>Spotify can only be played in JukeBox mode and requires a
						Spotify Premium account.</p>

					<c:choose>
						<c:when test="${not model.spotifyLoggedIn}">

							<div class="form-group">
								<label for="spotify_username"> <fmt:message
										key="spotifysettings.username" />

								</label> <input type="text" name="spotify_username" class="form-control" />
							</div>


							<div class="form-group">
								<label for="spotify_password"><fmt:message
										key="spotifysettings.password" /> </label> <input type="password"
									name="spotify_password" id="spotify_password"
									class="form-control" />
							</div>

							<button class="btn btn-primary">
								<fmt:message key="common.ok" />
							</button>
						</c:when>
						<c:otherwise>
							Logged into spotify as ${model.spotifyUsername}
							</c:otherwise>
					</c:choose>
				</div>
			</div>
		</c:if>
	</form>




	<c:if test="${fn:length(model.filesMissingMetadata) > 0}">
		<div class="panel panel-default">
			<div class="panel-heading">Files lacking tags</div>
			<div class="panel-body">
				These files lack either artist or track tag, and are ignored by
				MusicCabinet.
				<c:import url="helpToolTip.jsp">
					<c:param name="topic" value="filesmissingmetadata" />
				</c:import>
				<ul>
					<c:forEach items="${model.filesMissingMetadata}" var="file">
						<li>${file}</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</c:if>

	<c:if test="${model.reload}">
		<script type="text/javascript">
      jQuery('.upper').load("top.view?");

      <c:if test="${not model.hasArtists}">
      jQuery('.left').load("left.view?");
      </c:if>
    </script>
	</c:if>

</div>