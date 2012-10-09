<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jspf" %>
</head>
<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<script type="text/javascript">
	function add(group) {
		$("#inp").append('<span style="display: block"><img class="rem" src="<spring:theme code="removeImage"/>" alt="Remove"><input name="group" type="text" value="' + group + '" size="50"></span>');

		$(".rem").click(function() {
			$(this).parent().remove();
		});
	}

	$(document).ready(function() {
		$("#add").click(function() {
			add('');
		});

		<c:forEach items="${model.lastFmGroups}" var="group">
			add('${group.name}');
		</c:forEach>

		add('');

		$("#save").click(function() {
			location.href = '?' + ($(this).parent().serialize());
		});
		
	});
	
</script>

<p style="padding-top:1em"><b>Last.fm group subscriptions</b>
	<c:import url="helpToolTip.jsp"><c:param name="topic" value="groupsubscriptions"/></c:import>
</p>

<form method="post" action="groupSettings.view">

	<div id="inp"></div>

	<span style="display: block">
		<img id="add" src="<spring:theme code="plusImage"/>" alt="Add">
	</span>
	
	<input id="save" type="button" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
	<input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">

</form>

</body></html>