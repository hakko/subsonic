<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html>
 <head>
  <%@ include file="head.jspf" %>
 </head>
<body class="mainframe bgcolor1">

<sub:url value="radio.view" var="backUrl"></sub:url>
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>

<h1>
    <img src="<spring:theme code="radioImage"/>" alt="">
    Radio settings
</h1>

</body></html>