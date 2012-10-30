<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jspf" %>

</head>
<body class="mainframe bgcolor1">

<div style="padding-left:0.75em; white-space: pre-line;">
${model.lyrics}
</div>

<hr/>
<p style="text-align:center">
    <a href="javascript:self.close()">[<fmt:message key="common.close"/>]</a>
</p>

</body>
</html>
