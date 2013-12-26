<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>
<%@ include file="include.jspf"%>


<div class="mainframe bgcolor1">

	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="user" />
	</c:import>

	<script type="text/javascript" language="javascript">
    function enablePasswordChangeFields() {
      var changePasswordCheckbox = jQuery("#passwordChange");
      var ldapCheckbox = jQuery("#ldapAuthenticated");
      var passwordChangeTable = jQuery("#passwordChangeTable");
      var passwordChangeCheckboxTable = jQuery("#passwordChangeCheckboxTable");

      if (changePasswordCheckbox.is(":checked")
          && (!ldapCheckbox.is(":checked"))) {
        passwordChangeTable.show();
      } else {
        passwordChangeTable.hide();
      }

      if (ldapCheckbox.is(":checked")) {
        passwordChangeCheckboxTable.hide();
      } else {
        passwordChangeCheckboxTable.show();
      }
    }
  </script>

	<form:form method="post" action="userSettings.view"
		commandName="command"
		onsubmit="return submitForm(this, 'User saved.')">
		<div class="statusMessage"></div>

		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="usersettings.title" />
			</div>
			<div class="panel-body">

				<select name="username"
					onchange="return loadInFrame(this, 'userSettings.view?userIndex=' + (selectedIndex - 1));">
					<option value="">
						--
						<fmt:message key="usersettings.newuser" />
						--
					</option>
					<c:forEach items="${command.users}" var="user">
						<option ${user.username eq command.username ? "selected" : ""}
							value="${user.username}">${user.username}</option>
					</c:forEach>
				</select>



				<c:if test="${not command.admin}">

					<div class="checkbox">
						<label for="admin"><fmt:message key="usersettings.admin" />
							<form:checkbox path="adminRole" id="admin" cssClass="checkbox" />
						</label>
					</div>


					<div class="checkbox">

						<label for="settings"><fmt:message
								key="usersettings.settings" /> <form:checkbox
								path="settingsRole" id="settings" cssClass="checkbox" /></label>
					</div>

					<div class="checkbox">
						<label for="stream"><fmt:message key="usersettings.stream" />
							<form:checkbox path="streamRole" id="stream" cssClass="checkbox" />
						</label>
					</div>


					<div class="checkbox">
						<label for="jukebox"><fmt:message
								key="usersettings.jukebox" /> <form:checkbox path="jukeboxRole"
								id="jukebox" cssClass="checkbox" /> </label>
					</div>
					<div class="checkbox">

						<label for="download"><fmt:message
								key="usersettings.download" /> <form:checkbox
								path="downloadRole" id="download" cssClass="checkbox" /></label>

					</div>
					<div class="checkbox">

						<label for="upload"><fmt:message key="usersettings.upload" />
							<form:checkbox path="uploadRole" id="upload" cssClass="checkbox" /></label>
					</div>
					<div class="checkbox">

						<label for="share"><fmt:message key="usersettings.share" />
							<form:checkbox path="shareRole" id="share" cssClass="checkbox" /></label>
					</div>
					<div class="checkbox">
						<label for="playlist"><fmt:message
								key="usersettings.playlist" /> <form:checkbox
								path="playlistRole" id="playlist" cssClass="checkbox" /></label>
					</div>
					<div class="checkbox">

						<label for="coverArt"><fmt:message
								key="usersettings.coverart" /> <form:checkbox
								path="coverArtRole" id="coverArt" cssClass="checkbox" /></label>
					</div>
					<div class="checkbox">


						<label for="comment"><fmt:message
								key="usersettings.comment" /> <form:checkbox path="commentRole"
								id="comment" cssClass="checkbox" /></label>
					</div>
					<div class="checkbox">

						<label for="podcast"><fmt:message
								key="usersettings.podcast" /> <form:checkbox path="podcastRole"
								id="podcast" cssClass="checkbox" /></label>
					</div>
				</c:if>

				<div class="form-group">
					<label for="transcodeSchemeName"> <fmt:message
							key="playersettings.maxbitrate" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="transcode" />
						</c:import>
					</label>
					<form:select path="transcodeSchemeName" cssStyle="width:8em">
						<c:forEach items="${command.transcodeSchemeHolders}"
							var="transcodeSchemeHolder">
							<form:option value="${transcodeSchemeHolder.name}"
								label="${transcodeSchemeHolder.description}" />
						</c:forEach>
					</form:select>
					<div class="alert alert-warning">
						<fmt:message key="playersettings.nolame" />
					</div>
				</div>

				<c:if test="${not command.newUser and not command.admin}">
					<div class="checkbox">

						<label for="delete"><fmt:message key="usersettings.delete" />
							<form:checkbox path="delete" id="delete" cssClass="checkbox" /></label>
					</div>


				</c:if>

				<c:if test="${command.ldapEnabled and not command.admin}">

					<div class="checkbox">
						<label for="ldapAuthenticated"><fmt:message
								key="usersettings.ldap" /> <c:import url="helpToolTip.jsp">
								<c:param name="topic" value="ldap" />
							</c:import> <form:checkbox path="ldapAuthenticated" id="ldapAuthenticated"
								cssClass="checkbox"
								onclick="javascript:enablePasswordChangeFields()" /> </label>
					</div>


				</c:if>

				<c:choose>
					<c:when test="${command.newUser}">

						<div class="form-group">
							<label for="username"> <fmt:message
									key="usersettings.username" />

							</label>
							<form:input path="username" class="form-control" />
							<div class="has-error">
								<form:errors path="username" />
							</div>
						</div>
						<div class="form-group">
							<label for="email"> <fmt:message key="usersettings.email" />

							</label>
							<form:input path="email" class="form-control" />
							<div class="has-error">
								<form:errors path="email" />
							</div>
						</div>
						<div class="form-group">
							<label for="password"> <fmt:message
									key="usersettings.password" />

							</label>
							<form:password path="password" class="form-control" />
							<div class="has-error">
								<form:errors path="password" />
							</div>
						</div>
						<div class="form-group">
							<label for="confirmPassword"> <fmt:message
									key="usersettings.confirmpassword" />

							</label>
							<form:password path="confirmPassword" class="form-control" />
							<div class="has-error">
								<form:errors path="confirmPassword" />
							</div>
						</div>
					</c:when>

					<c:otherwise>
						<div class="checkbox">
							<label for="passwordChange"><fmt:message
									key="usersettings.changepassword" /> <form:checkbox
									path="passwordChange" id="passwordChange"
									onclick="enablePasswordChangeFields();" cssClass="checkbox" />
							</label>

						</div>

						<div id="passwordChangeTable" style="display: none">

							<div class="form-group">
								<label for="password"><fmt:message
										key="usersettings.newpassword" /> </label>
								<form:password path="password" id="password"
									class="form-control" />

								<div class="has-error">
									<form:errors path="password" />
								</div>
							</div>

							<div class="form-group">
								<label for="confirmPassword"><fmt:message
										key="usersettings.confirmpassword" /> </label>
								<form:password path="confirmPassword" id="confirmPassword"
									class="form-control" />

								<div class="has-error">
									<form:errors path="confirmPassword" />
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="email"> <fmt:message key="usersettings.email" />
							</label>
							<form:input path="email" />
							<div class="has-error">
								<form:errors path="email" />
							</div>

						</div>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="panel-footer">
				<input class="btn btn-primary" type="submit"
					value="<fmt:message key="common.save"/>"> <input
					class="btn btn-default" type="button"
					value="<fmt:message key="common.cancel"/>"
					onclick="location.href='nowPlaying.view'">
			</div>
		</div>
	</form:form>
	<script type="text/javascript">
    enablePasswordChangeFields()
  </script>
</div>