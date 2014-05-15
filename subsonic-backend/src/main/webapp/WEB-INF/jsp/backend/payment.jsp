<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="../head.jsp" %>
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/reset/reset.css">
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/fonts/fonts.css">
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/grid/grid.css">
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/base/base.css">
    <meta http-equiv="refresh" content="300">
</head>
<body>

<div style="margin-left: auto; margin-right: auto;">

    <table>
        <c:forEach items="${model.payments}" var="payment">
            <tr>
                <td><fmt:formatDate type="date" dateStyle="long" value="${payment.key}"/></td>
                <td>${payment.value}</td>
            </tr>
        </c:forEach>
        <tr><td><b>Average</b></td><td><b>${model.average}</b></td></tr>
    </table>

</div>
</body>
</html>