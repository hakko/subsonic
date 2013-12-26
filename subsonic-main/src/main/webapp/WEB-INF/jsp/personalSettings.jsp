<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.PersonalSettingsCommand"--%>

<%@ include file="include.jspf"%>

<div class="mainframe bgcolor1 personal-settings">

	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="personal" />
		<c:param name="restricted" value="${not command.user.adminRole}" />
	</c:import>

	<fmt:message key="common.default" var="defaultLabel" />
	<form:form method="post" action="personalSettings.view"
		commandName="command" class="form" role="form"
		onsubmit="return submitForm(this, 'Personal settings saved.');">
		<div class="statusMessage"></div>
		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="personalsettings.title">
					<fmt:param>${command.user.username}</fmt:param>
				</fmt:message>
			</div>
			<div class="panel-body">

				<div class="indent">

					<div class="form-group">
						<label for="localeIndex"><fmt:message
								key="personalsettings.language" /></label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="language" />
						</c:import>

						<form:select path="localeIndex" cssStyle="width:15em"
							class="form-control">
							<form:option value="-1" label="${defaultLabel}" />
							<c:forEach items="${command.locales}" var="locale"
								varStatus="loopStatus">
								<form:option value="${loopStatus.count - 1}" label="${locale}" />
							</c:forEach>
						</form:select>


					</div>

					<div class="form-group">
						<label for="themeIndex"><fmt:message
								key="personalsettings.theme" /></label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="theme" />
						</c:import>

						<form:select path="themeIndex" cssStyle="width:15em"
							class="form-control">
							<form:option value="-1" label="${defaultLabel}" />
							<c:forEach items="${command.themes}" var="theme"
								varStatus="loopStatus">
								<form:option value="${loopStatus.count - 1}"
									label="${theme.name}" />
							</c:forEach>
						</form:select>


					</div>

					<div class="form-group">
						<label for="albumOrderByYear">Album sorting</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="albumorderbyyear" />
						</c:import>

						<form:select path="albumOrderByYear" cssStyle="width:15em"
							class="form-control">
							<form:option value="true" label="By year" />
							<form:option value="false" label="By name" />
						</form:select>


					</div>

					<div class="form-group">
						<label for="albumOrderAscending">Album ordering</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="albumorderascending" />
						</c:import>

						<form:select path="albumOrderAscending" cssStyle="width:15em"
							class="form-control">
							<form:option value="true" label="Ascending" />
							<form:option value="false" label="Descending" />
						</form:select>


					</div>

					<div class="form-group">
						<label for="defaultHomeView">Default home view</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="defaulthomeview" />
						</c:import>

						<form:select path="defaultHomeView" cssStyle="width:15em"
							class="form-control">
							<form:option value="newest" label="Recently added" />
							<form:option value="recent+Artists"
								label="Recently played - Artists" />
							<form:option value="recent+Albums"
								label="Recently played - Albums" />
							<form:option value="recent+Songs" label="Recently played - Songs" />
							<form:option value="frequent+Artists"
								label="Frequently played - Artists" />
							<form:option value="frequent+Albums"
								label="Frequently played - Albums" />
							<form:option value="frequent+Songs"
								label="Frequently played - Songs" />
							<form:option value="starred+Artists" label="Starred - Artists" />
							<form:option value="starred+Albums" label="Starred - Albums" />
							<form:option value="starred+Songs" label="Starred - Songs" />
							<form:option value="topartists+3month"
								label="Top artists - Three months" />
							<form:option value="topartists+6month"
								label="Top artists - Six months" />
							<form:option value="topartists+12month"
								label="Top artists - Twelve months" />
							<form:option value="topartists+overall"
								label="Top artists - Overall" />
							<form:option value="random+Artists" label="Random - Artists" />
							<form:option value="random+Albums" label="Random - Albums" />
							<form:option value="random+Songs" label="Random - Songs" />
							<form:option value="recommended" label="Recommended" />
						</form:select>


					</div>

					<div class="form-group">
						<label for="viewStatsForAllUsers">Default home statistics</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="viewstatsforallusers" />
						</c:import>

						<form:select path="viewStatsForAllUsers" cssStyle="width:15em"
							class="form-control">
							<form:option value="false" label="Show only my activity" />
							<form:option value="true" label="Show activity for all users" />
						</form:select>


					</div>

					<div class="form-group">
						<label for="defaultHomeArtists">Default home artists</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="defaulthomeartists" />
						</c:import>

						<form:input path="defaultHomeArtists" size="3"
							class="form-control" cssStyle="width:15em" />


					</div>

					<div class="form-group">
						<label for="defaultHomeAlbums">Default home albums</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="defaulthomealbums" />
						</c:import>

						<form:input path="defaultHomeAlbums" size="3" class="form-control" />


					</div>

					<div class="form-group">
						<label for="defaultHomeSongs">Default home songs</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="defaulthomesongs" />
						</c:import>

						<form:input path="defaultHomeSongs" size="3" class="form-control" />


					</div>

					<div class="form-group">
						<label for="artistGridWidth">Artist grid width</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="artistgridwidth" />
						</c:import>

						<form:input path="artistGridWidth" size="3" class="form-control" />


					</div>

					<div class="form-group">
						<label for="albumGridLayout">Album grid layout</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="albumgridlayout" />
						</c:import>

						<form:select path="albumGridLayout" class="form-control">
							<form:option value="true" label="Yes" />
							<form:option value="false" label="No" />
						</form:select>


					</div>
					<div class="form-group">
						<label for="relatedArtists">Related artists</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="relatedartists" />
						</c:import>

						<form:select path="relatedArtists" class="form-control">
							<c:forTokens items="8 9 10 11 12 13 14 15 16 17 18 19 20 21"
								delims=" " var="i">
								<form:option value="${i}" label="${i}" />
							</c:forTokens>
						</form:select>


					</div>

					<div class="form-group">
						<label for="recommendedArtists">Recommended artists</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="recommendedartists" />
						</c:import>

						<form:select path="recommendedArtists" class="form-control">
							<c:forTokens items="3 4 5 6 7 8 9 10" delims=" " var="i">
								<form:option value="${i}" label="${i}" />
							</c:forTokens>
						</form:select>


					</div>

					<div class="form-group">
						<label for="reluctantArtistLoading">Reluctant artist
							loading</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="reluctantartistloading" />
						</c:import>

						<form:select path="reluctantArtistLoading" class="form-control">
							<form:option value="false" label="No" />
							<form:option value="true" label="Yes" />
						</form:select>


					</div>

					<div class="form-group">
						<label for="onlyAlbumArtistRecommendations">Album artist
							filter</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="onlyalbumartistrecommendations" />
						</c:import>

						<form:select path="onlyAlbumArtistRecommendations"
							class="form-control">
							<form:option value="false" label="No" />
							<form:option value="true" label="Yes" />
						</form:select>


					</div>

					<div class="form-group">
						<label for="useVariousArtistsShortlist">Use Various
							Artists shortlist</label>
						<c:import url="helpToolTip.jsp">
							<c:param name="topic" value="usevariousartistsshortlist" />
						</c:import>

						<form:select path="useVariousArtistsShortlist"
							class="form-control">
							<form:option value="false" label="No" />
							<form:option value="true" label="Yes" />
						</form:select>


					</div>
				</div>
			</div>

			<div class="table-responsive">
				<table class="table table-condensed">
					<tr>
						<th><fmt:message key="personalsettings.display" /></th>
						<th><fmt:message key="personalsettings.browse" /></th>
						<th><fmt:message key="personalsettings.playlist" /></th>
						<th>Home</th>
						<th><c:import url="helpToolTip.jsp">
								<c:param name="topic" value="visibility" />
							</c:import></th>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.tracknumber" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.trackNumberVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.trackNumberVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.trackNumberVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.artist" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.artistVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.artistVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.artistVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.album" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.albumVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.albumVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.albumVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.composer" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.composerVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.composerVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.composerVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.genre" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.genreVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.genreVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.genreVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.year" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.yearVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.yearVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.yearVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.bitrate" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.bitRateVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.bitRateVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.bitRateVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.duration" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.durationVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.durationVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.durationVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.format" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.formatVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.formatVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.formatVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.filesize" /></td>
						<td style="text-align: center"><form:checkbox
								path="mainVisibility.fileSizeVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="playlistVisibility.fileSizeVisible" cssClass="checkbox" /></td>
						<td style="text-align: center"><form:checkbox
								path="homeVisibility.fileSizeVisible" cssClass="checkbox" /></td>
					</tr>
					<tr>
						<td><fmt:message key="personalsettings.captioncutoff" /></td>
						<td style="text-align: center"><form:input
								path="mainVisibility.captionCutoff" size="3" /></td>
						<td style="text-align: center"><form:input
								path="playlistVisibility.captionCutoff" size="3" /></td>
						<td style="text-align: center"><form:input
								path="homeVisibility.captionCutoff" size="3" /></td>
					</tr>
				</table>

				<table class="table table-condensed">
					<tr>
						<td>
							<div class="checkbox">
								<label for="nowPlaying"><fmt:message
										key="personalsettings.shownowplaying" /> <form:checkbox
										path="showNowPlayingEnabled" id="nowPlaying"
										cssClass="checkbox" /></label>
							</div>
						</td>
						<td><div class="checkbox">
								<label for="chat"><fmt:message
										key="personalsettings.showchat" /> <form:checkbox
										path="showChatEnabled" id="chat" cssClass="checkbox" /></label>
							</div></td>
					</tr>
					<tr>
						<td>
							<div class="checkbox">
								<label for="nowPlayingAllowed"><fmt:message
										key="personalsettings.nowplayingallowed" /> <form:checkbox
										path="nowPlayingAllowed" id="nowPlayingAllowed"
										cssClass="checkbox" /> </label>
							</div>

						</td>
						<td>
							<div class="checkbox">
								<label for="partyModeEnabled"><fmt:message
										key="personalsettings.partymode" /> <c:import
										url="helpToolTip.jsp">
										<c:param name="topic" value="partymode" />
									</c:import> <form:checkbox path="partyModeEnabled" id="partyModeEnabled"
										cssClass="checkbox" /> </label>
							</div>
						</td>
					</tr>
				</table>
			</div>
			<div class="panel-footer">
				<input class="btn btn-primary" type="submit"
					value="<fmt:message key="common.save"/>"
					style="margin-right: 0.3em" class="btn btn-primary" /> <input
					class="btn btn-default" type="button"
					value="<fmt:message key="common.cancel"/>"
					onclick="location.href='nowPlaying.view'" class="btn btn-default">

			</div>
		</div>

		<div class="panel panel-default">
			<div class="panel-heading">last.fm</div>
			<div class="panel-body">


				<c:if test="${command.lastFmEnabled}">
					<div class="checkbox">
						<label for="lastFm">Scrobble tracks as user
							${command.lastFmUsername}. <form:checkbox path="lastFmEnabled"
								id="lastFm" cssClass="checkbox" />

						</label>
					</div>
				</c:if>
				<div id="lastFmLink" class="forward" style="padding-left: 2em">
					<a href="lastFmSettings.view">Configure last.fm scrobbling</a>
				</div>
			</div>
		</div>

		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="personalsettings.avatar.title" />
			</div>
			<div class="panel-body">

				<p style="padding-top: 1em">
					<c:forEach items="${command.avatars}" var="avatar">
						<c:url value="avatar.view" var="avatarUrl">
							<c:param name="id" value="${avatar.id}" />
						</c:url>
						<span style="white-space: nowrap;"> <form:radiobutton
								id="avatar-${avatar.id}" path="avatarId" value="${avatar.id}" />
							<label for="avatar-${avatar.id}"><img src="${avatarUrl}"
								alt="${avatar.name}" width="${avatar.width}"
								height="${avatar.height}"
								style="padding-right: 2em; padding-bottom: 1em" /></label>
						</span>
					</c:forEach>
				</p>
				<p>
					<form:radiobutton id="noAvatar" path="avatarId" value="-1" />
					<label for="noAvatar"><fmt:message
							key="personalsettings.avatar.none" /></label>
				</p>
				<p>
					<form:radiobutton id="customAvatar" path="avatarId" value="-2" />
					<label for="customAvatar"><fmt:message
							key="personalsettings.avatar.custom" /> <c:if
							test="${not empty command.customAvatar}">
							<sub:url value="avatar.view" var="avatarUrl">
								<sub:param name="username" value="${command.user.username}" />
							</sub:url>
							<img src="${avatarUrl}" alt="${command.customAvatar.name}"
								width="${command.customAvatar.width}"
								height="${command.customAvatar.height}"
								style="padding-right: 2em" />
						</c:if> </label>
				</p>

				<form method="post" enctype="multipart/form-data"
					action="avatarUpload.view" class="form" role="form"
					onsubmit="return submitForm(this);">

					<div class="form-group">
						<label for="file"><fmt:message
								key="personalsettings.avatar.changecustom" /></label> <input
							class="form-control" type="file" id="file" name="file" size="40" />
					</div>
					<p class="detail" style="text-align: right">
						<fmt:message key="personalsettings.avatar.courtesy" />
					</p>

				</form>

			</div>
			<div class="panel-footer">
				<input class="btn btn-primary" type="submit"
					value="<fmt:message key="personalsettings.avatar.upload"/>" />

			</div>
		</div>
	</form:form>


	<c:if test="${command.reloadNeeded}">
		<script language="javascript" type="text/javascript">
      //jQuery(".main").load("index.view?");
    </script>
	</c:if>

</div>
