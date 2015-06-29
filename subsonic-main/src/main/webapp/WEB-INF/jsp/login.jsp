<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

    <%@ include file="include.jspf" %>
    <script type="text/javascript">
        if (window != window.top) {
            top.location.href = location.href;
        }
    </script>

<div class="mainframe bgcolor1" onload="document.getElementById('j_username').focus()">

<form action="<c:url value="/j_spring_security_check"/>" method="POST">
    <div class="bgcolor2" align="center" style="border:1px solid black; padding:20px 50px 20px 50px; margin-top:100px">

        <div style="margin-bottom:1em;max-width:50em;text-align:left;"><sub:wiki text="${model.loginMessage}"/></div>

        <table>
        <tr>
            <td colspan="2" align="left" style="padding-bottom:10px">
                <img src="<spring:theme code="logoImage"/>" alt="">
            </td>
        </tr>
        <tr>
            <td align="left" style="padding-right:10px"><fmt:message key="login.username"/></td>
            <td align="left"><input type="text" id="j_username" name="j_username" style="width:12em" tabindex="1"></td>
        </tr>

        <tr>
            <td align="left" style="padding-bottom:10px"><fmt:message key="login.password"/></td>
            <td align="left" style="padding-bottom:10px"><input type="password" name="j_password" style="width:12em" tabindex="2"></td>
        </tr>

        <tr>
            <td align="left"><input name="submit" type="submit" value="<fmt:message key="login.login"/>" tabindex="4"></td>
            <td align="left" class="detail">
                <input type="checkbox" name="_spring_security_remember_me" id="remember" class="checkbox" tabindex="3">
                <label for="remember"><fmt:message key="login.remember"/></label>
            </td>
        </tr>
            <c:if test="${model.logout}">
                <tr><td colspan="2" style="padding-top:10px"><b><fmt:message key="login.logout"/></b></td></tr>
            </c:if>
            <c:if test="${model.error}">
                <tr><td colspan="2" style="padding-top:10px"><b class="warning"><fmt:message key="login.error"/></b></td></tr>
            </c:if>

        </table>

        <c:if test="${model.insecure}">
            <p><b class="warning"><fmt:message key="login.insecure"><fmt:param value="${model.brand}"/></fmt:message></b></p>
        </c:if>

    </div>
</form>
</div>
