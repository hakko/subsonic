<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jspf" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
 	<script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
</head>
<body class="mainframe bgcolor1">

<form:form method="post" action="editArtist.view" commandName="command" id="editArtist">

<form:hidden path="id"/>
<form:hidden path="artist"/>

<div style="padding:15px; width:60%">

<h1>${command.artist}</h1>
<table>
	<tr>
		<td style="vertical-align:top">
			<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
				<img width="126" height="126" src="${command.artistInfo.largeImageUrl}" alt="">
			</div></div></div></div>
		</td>
		<td style="vertical-align:top">
			<div style="width:525px;">
				<form:textarea path="bioSummary" rows="10" cols="70"/>
				<script type="text/javascript" language="javascript">
					$('bioSummary').setValue('<sub:escapeJavaScript string="${command.artistInfo.bioSummary}"/>');
				</script>
			</div>
		</td>
	</tr>
</table>

</div>

<p style="padding-top:1em;padding-bottom:1em">
    <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em"/>
	<sub:url value="artist.view" var="artistUrl"><sub:param name="id" value="${command.id}"/></sub:url>
    <input type="button" value="<fmt:message key="common.back"/>" onclick="location.href='${artistUrl}'">
</p>

</form:form>

</body></html>