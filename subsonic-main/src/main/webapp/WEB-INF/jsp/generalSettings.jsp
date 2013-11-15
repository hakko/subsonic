<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>
<%@ include file="include.jspf"%>

<div class="mainframe bgcolor1">

	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="general" />
	</c:import>

	<form:form method="post" action="generalSettings.view"
		commandName="command"
		onsubmit="return submitForm(this, 'Settings saved.');">
		<div class="statusMessage"></div>

		<div class="panel panel-default">
			<div class="panel-heading">General</div>
			<div class="panel-body">


				<div class="form-group">
					<label for="playlistFolder"><fmt:message
							key="generalsettings.playlistfolder" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="playlistfolder" />
						</c:import></label>
					<form:input class="form-control" path="playlistFolder" size="70" />
				</div>

				<div class="form-group">
					<label for="musicFileTypes"><fmt:message
							key="generalsettings.musicmask" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="musicmask" />
						</c:import> </label>
					<form:input class="form-control" path="musicFileTypes" size="70" />
				</div>

				<div class="form-group">
					<label for="videoFileTypes"><fmt:message
							key="generalsettings.videomask" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="videomask" />
						</c:import> </label>

					<form:input class="form-control" path="videoFileTypes" size="70" />
				</div>

				<div class="form-group">
					<label for="imageFileTypes"> <fmt:message
							key="generalsettings.imagemask" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="imagemask" />
						</c:import>
					</label>
					<form:input class="form-control" path="imageFileTypes" size="70" />

				</div>

				<div class="form-group">
					<label for="index"> <fmt:message
							key="generalsettings.index" /> <c:import url="helpToolTip.jsp">
							<c:param name="topic" value="index" />
						</c:import>
					</label>
					<form:input class="form-control" path="index" size="70" />
				</div>

				<div class="form-group">
					<label for="ignoredArticles"> <fmt:message
							key="generalsettings.ignoredarticles" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="ignoredarticles" />
						</c:import>
					</label>

					<form:input class="form-control" path="ignoredArticles" size="70" />
				</div>

				<div class="form-group">
					<label for="localeIndex"> <fmt:message
							key="generalsettings.language" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="language" />
						</c:import>

					</label>
					<form:select class="form-control" path="localeIndex"
						cssStyle="width:15em">
						<c:forEach items="${command.locales}" var="locale"
							varStatus="loopStatus">
							<form:option value="${loopStatus.count - 1}" label="${locale}" />
						</c:forEach>
					</form:select>
				</div>

				<div class="form-group">
					<label for="themeIndex"> <fmt:message
							key="generalsettings.theme" /> <c:import url="helpToolTip.jsp">
							<c:param name="topic" value="theme" />
						</c:import>
					</label>
					<form:select class="form-control" path="themeIndex"
						cssStyle="width:15em">
						<c:forEach items="${command.themes}" var="theme"
							varStatus="loopStatus">
							<form:option value="${loopStatus.count - 1}"
								label="${theme.name}" />
						</c:forEach>
					</form:select>
				</div>

				<div class="checkbox">
					<label for="gettingStartedEnabled"><fmt:message
							key="generalsettings.showgettingstarted" /> <form:checkbox
							path="gettingStartedEnabled" id="gettingStartedEnabled" /> </label>

				</div>
				<div class="form-group">
					<label for="welcomeTitle"><fmt:message
							key="generalsettings.welcometitle" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="welcomemessage" />
						</c:import> </label>
					<form:input class="form-control" path="welcomeTitle" size="70" />
				</div>
				<div class="form-group">
					<label for="welcomeSubtitle"> <fmt:message
							key="generalsettings.welcomesubtitle" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="welcomemessage" />
						</c:import>
					</label>
					<form:input class="form-control" path="welcomeSubtitle" size="70" />
				</div>
				<div class="form-group">
					<label for="welcomeMessage"> <fmt:message
							key="generalsettings.welcomemessage" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="welcomemessage" />
						</c:import>
					</label>
					<form:textarea class="form-control" path="welcomeMessage" rows="5"
						cols="70" />
				</div>
				<div class="form-group">
					<label for="loginMessage"> <fmt:message
							key="generalsettings.loginmessage" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="loginmessage" />
						</c:import>

					</label>
					<form:textarea class="form-control" path="loginMessage" rows="5"
						cols="70" />
					<fmt:message key="main.wiki" />
				</div>
				<div class="form-group">
					<label for="shareUrlPrefix"> <fmt:message
							key="generalsettings.shareurlprefix" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="shareurlprefix" />
						</c:import>

					</label>
					<form:input class="form-control" path="shareUrlPrefix" size="70" />
				</div>
				<div class="form-group">
					<label for="lyricsUrl"> <fmt:message
							key="generalsettings.lyricsurl" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="lyricsurl" />
						</c:import>
					</label>
					<form:input class="form-control" path="lyricsUrl" size="70" />
				</div>
				<div class="form-group">
					<label for="restAlbumName"> <fmt:message
							key="generalsettings.restalbumname" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="restalbumname" />
						</c:import>

					</label>
					<form:input class="form-control" path="restAlbumName" size="70" />
				</div>
			</div>
			<div class="panel-footer">
				<input class="btn btn-primary" type="submit"
					value="<fmt:message key="common.save"/>"
					style="margin-right: 0.3em"> <input class="btn btn-default"
					type="button" value="<fmt:message key="common.cancel"/>"
					onclick="location.href='nowPlaying.view'">
			</div>
		</div>


	</form:form>

	<c:if test="${command.reloadNeeded}">
		<script language="javascript" type="text/javascript">
      jQuery('.left').load("left.view?");
      jQuery('.playlist').load("playlist.view?");
    </script>
	</c:if>

</div>