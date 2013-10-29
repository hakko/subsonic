<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

    <%@ include file="include.jspf" %>

<div class="mainframe bgcolor1" onload="enableLdapFields()">
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="advanced"/>
</c:import>

<form:form method="post" action="advancedSettings.view" commandName="command">

    <table style="white-space:nowrap" class="indent">

        <tr>
            <td><fmt:message key="advancedsettings.downsamplecommand"/></td>
            <td>
                <form:input path="downsampleCommand" size="70"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="downsamplecommand"/></c:import>
            </td>
        </tr>

        <tr><td colspan="2">&nbsp;</td></tr>

        <tr>
            <td><fmt:message key="advancedsettings.coverartlimit"/></td>
            <td>
                <form:input path="coverArtLimit" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="coverartlimit"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.downloadlimit"/></td>
            <td>
                <form:input path="downloadLimit" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="downloadlimit"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.uploadlimit"/></td>
            <td>
                <form:input path="uploadLimit" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="uploadlimit"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.streamport"/></td>
            <td>
                <form:input path="streamPort" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="streamport"/></c:import>
            </td>
        </tr>

        <tr><td colspan="2">&nbsp;</td></tr>

        <tr>
            <td colspan="2">
                <form:checkbox path="ldapEnabled" id="ldap" cssClass="checkbox" onclick="javascript:enableLdapFields()"/>
                <label for="ldap"><fmt:message key="advancedsettings.ldapenabled"/></label>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldap"/></c:import>
            </td>
        </tr>

        <tr><td colspan="2">
            <table class="indent" id="ldapTable" style="padding-left:2em">
                <tr>
                    <td><fmt:message key="advancedsettings.ldapurl"/></td>
                    <td colspan="3">
                        <form:input path="ldapUrl" size="70"/>
                        <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapurl"/></c:import>
                    </td>
                </tr>

                <tr>
                    <td><fmt:message key="advancedsettings.ldapsearchfilter"/></td>
                    <td colspan="3">
                        <form:input path="ldapSearchFilter" size="70"/>
                        <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapsearchfilter"/></c:import>
                    </td>
                </tr>

                <tr>
                    <td><fmt:message key="advancedsettings.ldapmanagerdn"/></td>
                    <td>
                        <form:input path="ldapManagerDn" size="20"/>
                    </td>
                    <td><fmt:message key="advancedsettings.ldapmanagerpassword"/></td>
                    <td>
                        <form:password path="ldapManagerPassword" size="20"/>
                        <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapmanagerdn"/></c:import>
                    </td>
                </tr>

                <tr>
                    <td colspan="5">
                        <form:checkbox path="ldapAutoShadowing" id="ldapAutoShadowing" cssClass="checkbox"/>
                        <label for="ldapAutoShadowing"><fmt:message key="advancedsettings.ldapautoshadowing"><fmt:param value="${command.brand}"/></fmt:message></label>
                        <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapautoshadowing"/></c:import>
                    </td>
                </tr>
            </table>
        </td></tr>

        <tr>
            <td colspan="2" style="padding-top:1.5em">
                <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
                <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
            </td>
        </tr>

    </table>
</form:form>
    <script type="text/javascript" language="javascript">
        function enableLdapFields() {
            var checkbox = $("ldap");
            var table = $("ldapTable");

            if (checkbox && checkbox.checked) {
                table.show();
            } else {
                table.hide();
            }
        }
    </script>


<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.left.location.href="left.view?";
        parent.frames.playlist.location.href="playlist.view?";
    </script>
</c:if>

</div>
