<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jspf" %>
 	<script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
 	
</head>
<body class="mainframe bgcolor1">

<script type="text/javascript" language="javascript">
	function toggleCheckbox(h) {
		$('tagSettings').getInputs('checkbox').each(function(e) { if (e.name != 'toggle') e.checked = h });
	}
</script>

<p style="padding-top:1em"><b>Tag configuration</b></p>

<form:form method="post" action="tagSettings.view" commandName="command" id="tagSettings">

<div style="width:60%">

<c:choose>
 <c:when test="${empty command.availableTags}">
  <p>Sorry, but you don't have enough music for this to be meaningful. Please add some more artists.</p>
 </c:when>
 <c:otherwise>

<p>Tags for the artists in your library are fetched automatically from <a href="http://last.fm">last.fm</a>. You can browse your library by tags, or generate playlists based on them.</p>

<p>To give an idea of the popularity of a tag, the number of matching artists in your library is displayed next to each tag. Since the tags are manually entered by last.fm users, errors and duplicates may occur. You can indicate that a tag is wrongly spelled by adding the real tag name in "Corrected tag" column. The misspelled tag is removed for browsing, and associated artists are treated as merged into the corrected tag.</p>

<p><a href="javascript:toggleCheckbox(1)">Mark all</a> | <a href="javascript:toggleCheckbox(0)">Mark none</a></p>

<table>
<tr>
	<th>Tag</th>
	<th>Artists in library</th>
	<th>Corrected tag</th>
</tr>
<c:forEach items="${command.availableTags}" var="availableTag" varStatus="status">
	<tr>
		<td><form:checkbox path="availableTags[${status.index}].use" value="true" label="${availableTag.tag}"/></td>
		<td>${availableTag.occurrence}</td>	
		<td><form:input path="availableTags[${status.index}].correctedTag" size="20"/></td>
	</tr>
</c:forEach>
</table>

</div>

<input type="submit" value="Save" style="margin-right:0.3em"/>

 </c:otherwise>
</c:choose>

</form:form>

</body></html>