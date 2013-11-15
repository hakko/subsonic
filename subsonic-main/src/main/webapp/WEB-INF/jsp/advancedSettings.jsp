<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>

<%@ include file="include.jspf"%>

<div class="mainframe bgcolor1">

	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="advanced" />
	</c:import>

	<form:form method="post" action="advancedSettings.view"
		commandName="command"
		onsubmit="return submitForm(this, 'Settings saved.');">
		<div class="statusMessage"></div>
		<div class="panel panel-default">
			<div class="panel-heading">Advanced</div>
			<div class="panel-body">
				<div class="form-group">
					<label for="downsampleCommand"> <fmt:message
							key="advancedsettings.downsamplecommand" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="downsamplecommand" />
						</c:import>
					</label>

					<form:input class="form-control" path="downsampleCommand" size="70" />


				</div>

				<div class="row">
					<div class="form-group col-xs-3">
						<label for="coverArtLimit"> <c:import
								url="helpToolTip.jsp">
								<c:param name="topic" value="coverartlimit" />
							</c:import> <fmt:message key="advancedsettings.coverartlimit" />
						</label>

						<form:input class="form-control" path="coverArtLimit" size="8" />

					</div>
				</div>

				<div class="row">
					<div class="form-group col-xs-3">
						<label for="downloadLimit"> <c:import
								url="helpToolTip.jsp">
								<c:param name="topic" value="downloadlimit" />
							</c:import> <fmt:message key="advancedsettings.downloadlimit" />
						</label>



						<form:input class="form-control" path="downloadLimit" size="8" />

					</div>
				</div>

				<div class="row">
					<div class="form-group col-xs-3">
						<label for="uploadLimit"> <c:import url="helpToolTip.jsp">
								<c:param name="topic" value="uploadlimit" />
							</c:import> <fmt:message key="advancedsettings.uploadlimit" />
						</label>

						<form:input class="form-control" path="uploadLimit" size="8" />

					</div>
				</div>

				<div class="row">
					<div class="form-group col-xs-3">
						<label for="streamPort"> <c:import url="helpToolTip.jsp">
								<c:param name="topic" value="streamport" />
							</c:import> <fmt:message key="advancedsettings.streamport" />
						</label>

						<form:input class="form-control" path="streamPort" size="8" />

					</div>
				</div>

				<div class="checkbox">
					<label for="ldap"> <form:checkbox path="ldapEnabled"
							id="ldap" cssClass="checkbox"
							onclick="javascript:enableLdapFields()" />
							<c:import
              url="helpToolTip.jsp">
              <c:param name="topic" value="ldap" />
            </c:import>
							 
							<fmt:message
							key="advancedsettings.ldapenabled" /> 
					</label>
				</div>

				<div class="panel panel-default" id="ldapTable">
					<div class="panel-heading">LDAP</div>
					<div class="panel-body">

						<div class="form-group">
							<label for="ldapUrl"> <fmt:message
									key="advancedsettings.ldapurl" /> <c:import
									url="helpToolTip.jsp">
									<c:param name="topic" value="ldapurl" />
								</c:import>
							</label>

							<form:input class="form-control" path="ldapUrl" size="70" />

						</div>

						<div class="form-group">
							<label for="ldapSearchFilter"> <fmt:message
									key="advancedsettings.ldapsearchfilter" /> <c:import
									url="helpToolTip.jsp">
									<c:param name="topic" value="ldapsearchfilter" />
								</c:import>

							</label>

							<form:input class="form-control" path="ldapSearchFilter"
								size="70" />

						</div>

						<div class="row">
							<div class="form-group col-xs-6">
								<label for="ldapManagerDn"> <fmt:message
										key="advancedsettings.ldapmanagerdn" />
								</label>

								<form:input class="form-control" path="ldapManagerDn" size="20" />
							</div>
							<div class="form-group col-xs-6">
								<label for="ldapManagerPassword"> <fmt:message
										key="advancedsettings.ldapmanagerpassword" /> <c:import
										url="helpToolTip.jsp">
										<c:param name="topic" value="ldapmanagerdn" />
									</c:import>

								</label>
								<form:password class="form-control" path="ldapManagerPassword"
									size="20" />

							</div>
						</div>

						<div class="checkbox">
							<label for="ldapAutoShadowing"> <c:import
									url="helpToolTip.jsp">
									<c:param name="topic" value="ldapautoshadowing" />
								</c:import> <fmt:message key="advancedsettings.ldapautoshadowing">
									<fmt:param value="${command.brand}" />
								</fmt:message> <form:checkbox path="ldapAutoShadowing" id="ldapAutoShadowing"
									cssClass="checkbox" />
							</label>



						</div>
					</div>
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
	<script type="text/javascript">
    function enableLdapFields() {
      var checkbox = $("ldap");
      var table = $("ldapTable");

      if (checkbox && checkbox.checked) {
        table.show();
      } else {
        table.hide();
      }
    }
    enableLdapFields();
  </script>


	<c:if test="${command.reloadNeeded}">
		<script language="javascript" type="text/javascript">
      parent.frames.left.location.href = "left.view?";
      parent.frames.playlist.location.href = "playlist.view?";
    </script>
	</c:if>

</div>
