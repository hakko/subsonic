<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.NetworkSettingsCommand"--%>

<%@ include file="include.jspf"%>
<spring:theme code="panel.primary" var="themePanelPrimary" scope="page" />
<div class="mainframe bgcolor1 panel panel-primary ${themePanelPrimary}" onload="init()">

<div class="panel-heading">
  <i class="fa fa-cog"></i>
  <fmt:message key="settingsheader.title"/>
</div>
<div class="panel-body">


	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="network" />
	</c:import>

	<form:form commandName="command" action="networkSettings.view"
		method="post"
		onsubmit="return submitForm(this, 'Network settings saved.')">
		<div class="statusMessage"></div>

		<div class="panel panel-default">
			<div class="panel-heading">Network settings</div>
			<div class="panel-body">
				<p>
					<fmt:message key="networksettings.text" />
				</p>

				<div class="checkbox">
					<label for="portForwardingEnabled"><fmt:message
							key="networksettings.portforwardingenabled" /> <form:checkbox
							id="portForwardingEnabled" path="portForwardingEnabled" /> </label>
				</div>

				<div>
					<p>
						<fmt:message key="networksettings.portforwardinghelp">
							<fmt:param>${command.port}</fmt:param>
						</fmt:message>
					</p>

					<p class="detail">
						<fmt:message key="networksettings.status" />
						<span id="portForwardingStatus" style="margin-left: 0.25em"></span>
					</p>
				</div>

				<div class="checkbox">
					<label for="urlRedirectionEnabled"><fmt:message
							key="networksettings.urlredirectionenabled" /> <form:checkbox
							id="urlRedirectionEnabled" path="urlRedirectionEnabled"
							onclick="enableUrlRedirectionFields()" /> </label>
				</div>

				<p>
					http://
					<form:input id="urlRedirectFrom" path="urlRedirectFrom" size="16"
						cssStyle="margin-left:0.25em" />
					.subsonic.org
				</p>

				<p class="detail">
					<fmt:message key="networksettings.status" />
					<span id="urlRedirectionStatus" style="margin-left: 0.25em"></span>
					<span id="urlRedirectionTestStatus" style="margin-left: 0.25em"></span>
				</p>

			</div>
			<div class="panel-footer">
				<input  class="btn btn-primary" type="submit" value="<fmt:message key="common.save"/>"
					style="margin-right: 0.3em"> <input  class="btn btn-default" type="button"
					value="<fmt:message key="common.cancel"/>"
					onclick="location.href='nowPlaying.view'">

			</div>
		</div>
	</form:form>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
<script type="text/javascript" language="javascript">
        function refreshStatus() {
            multiService.getNetworkStatus(updateStatus);
        }

        function updateStatus(networkStatus) {
            (function($) {
              $("#portForwardingStatus").html(networkStatus.portForwardingStatusText);
              $("#urlRedirectionStatus").html(networkStatus.urlRedirectionStatusText);
              window.setTimeout("refreshStatus()", 1000);
            }(jQuery));
        }

        function enableUrlRedirectionFields() {
          (function($) {
            var checkbox = $("#urlRedirectionEnabled");
            var field = $("#urlRedirectFrom");

            if (checkbox && checkbox.prop('checked')) {
            
                field.prop('disabled', false);
            } else {
                field.prop('disabled', true);
            }
          }(jQuery));
        }

            enableUrlRedirectionFields();
            refreshStatus();


    </script>
