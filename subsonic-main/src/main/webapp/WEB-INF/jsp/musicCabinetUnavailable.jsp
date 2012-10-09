<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jspf" %>
</head>

<body class="mainframe bgcolor1">

<div style="padding: 15px;">

<h1>
    <img src="<spring:theme code="errorImage"/>" alt=""/>
    Not available
</h1>

<p>
    Sorry, but that isn't currently available. Please check the <a href="musicCabinetSettings.view">MusicCabinet configuration</a>.
</p>

<div class="back"><a href="javascript:history.go(-1)"><fmt:message key="common.back"/></a></div>

</div>

</body>
</html>