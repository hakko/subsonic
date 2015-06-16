<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

    <%@ include file="include.jspf" %>
 <spring:theme code="panel.primary" var="themePanelPrimary" scope="page" />
<div class="mainframe bgcolor1 panel panel-primary ${themePanelPrimary}">

<div class="panel-heading">
  <i class="fa fa-cog"></i>
  Group Settings
</div>
<div class="panel-body">

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
</div>
</div>