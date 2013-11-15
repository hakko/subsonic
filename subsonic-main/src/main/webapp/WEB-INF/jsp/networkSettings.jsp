<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.NetworkSettingsCommand"--%>

    <%@ include file="include.jspf" %>
<div class="mainframe bgcolor1" onload="init()">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="network"/>
</c:import>

<p style="padding-top:1em"><fmt:message key="networksettings.text"/></p>

<form:form commandName="command" action="networkSettings.view" method="post">
    <p style="padding-top:1em">
        <form:checkbox id="portForwardingEnabled" path="portForwardingEnabled"/>
        <label for="portForwardingEnabled"><fmt:message key="networksettings.portforwardingenabled"/></label>
    </p>

    <div style="padding-left:2em;max-width:60em">
        <p>
            <fmt:message key="networksettings.portforwardinghelp"><fmt:param>${command.port}</fmt:param></fmt:message>
        </p>

        <p class="detail">
            <fmt:message key="networksettings.status"/>
            <span id="portForwardingStatus" style="margin-left:0.25em"></span>
        </p>
    </div>

    <p style="padding-top:1em"><form:checkbox id="urlRedirectionEnabled" path="urlRedirectionEnabled"
                                              onclick="enableUrlRedirectionFields()"/>
        <label for="urlRedirectionEnabled"><fmt:message key="networksettings.urlredirectionenabled"/></label>
    </p>

    <div style="padding-left:2em">

        <p>http://<form:input id="urlRedirectFrom" path="urlRedirectFrom" size="16" cssStyle="margin-left:0.25em"/>.subsonic.org</p>

        <p class="detail">
            <fmt:message key="networksettings.status"/>
            <span id="urlRedirectionStatus" style="margin-left:0.25em"></span>
            <span id="urlRedirectionTestStatus" style="margin-left:0.25em"></span>
        </p>
    </div>

    <p style="padding-top:1em">
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
    </p>

</form:form>
</div>
        <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" language="javascript">
        function refreshStatus() {
            multiService.getNetworkStatus(updateStatus);
        }

        function updateStatus(networkStatus) {
            (function($) {
              $("#portForwardingStatus").html(networkStatus.portForwardingStatusText);
              $("#urlRedirectionStatus".html(networkStatus.urlRedirectionStatusText);
              window.setTimeout("refreshStatus()", 1000);
            }(jQuery));
        }

        function enableUrlRedirectionFields() {
            var checkbox = $("urlRedirectionEnabled");
            var field = $("urlRedirectFrom");

            if (checkbox && checkbox.checked) {
                field.enable();
            } else {
                field.disable();
            }
        }

            enableUrlRedirectionFields();
            refreshStatus();


    </script>
