<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

    <%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="password"/>
    <c:param name="restricted" value="true"/>
</c:import>

<c:choose>

    <c:when test="${command.ldapAuthenticated}">
        <p><fmt:message key="usersettings.passwordnotsupportedforldap"/></p>
    </c:when>

    <c:otherwise>
        <h2><fmt:message key="passwordsettings.title"><fmt:param>${command.username}</fmt:param></fmt:message></h2>
        <form:form method="post" action="passwordSettings.view" commandName="command">
            <table class="indent">
                <tr>
                    <td><fmt:message key="usersettings.newpassword"/></td>
                    <td><form:password path="password"/></td>
                    <td class="warning"><form:errors path="password"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.confirmpassword"/></td>
                    <td><form:password path="confirmPassword"/></td>
                    <td/>
                </tr>
                <tr>
                    <td colspan="3" style="padding-top:1.5em">
                        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
                        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
                    </td>
                </tr>

            </table>
        </form:form>
    </c:otherwise>
</c:choose>

</div>
