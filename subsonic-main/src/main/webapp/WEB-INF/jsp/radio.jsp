<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>
    <%@ include file="head.jspf" %>

	<style type="text/css">
span.off {
    cursor: pointer;
    float:left;
    padding: 2px 6px;
    margin: 2px;
    background: #FFF;
    color: #000;
    -webkit-border-radius: 7px;
    -moz-border-radius: 7px;
    border-radius: 7px;
    border: solid 1px #CCC;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
    -webkit-transition-duration: 0.1s;
    -moz-transition-duration: 0.1s;
    transition-duration: 0.1s;
    -webkit-user-select:none;
    -moz-user-select:none;
    -ms-user-select:none;
    user-select:none;
    white-space: nowrap;
}

span.on {
    cursor: pointer;
    float:left;
    padding: 2px 6px;
    margin: 2px;
    background: #9E7;
    color: #000;
    -webkit-border-radius: 7px;
    -moz-border-radius: 7px;
    border-radius: 7px;
    border: solid 1px #999;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
    -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.6);
    -moz-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.6);
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.6);
    -webkit-transition-duration: 0.1s;
    -moz-transition-duration: 0.1s;
    transition-duration: 0.1s;
    -webkit-user-select:none;
    -moz-user-select:none;
    -ms-user-select:none;
    user-select:none;
    white-space: nowrap;
}

span.off:hover {
    background: #9E7;opacity:0.7;
    border: solid 1px #999;
    text-decoration: none;
}
	</style>
	
	<script type="text/javascript">

function changeClass(elem, className1,className2) {
    elem.className = (elem.className == className1)?className2:className1;
}
function playGenreRadio() {
	var genres = new Array();
	var e = document.getElementsByTagName("span");
	for (var i = 0; i < e.length; i++) {
		if (e[i].className == "on") {
			genres.push(e[i].firstChild.data);
		}
	}
	top.playlist.onPlayGenreRadio(genres);
}
	</script>

</head>
<body class="mainframe bgcolor1">

<h1>
    <img src="<spring:theme code="radioImage"/>" alt="">
    Radio
</h1>

<c:choose>
    <c:when test="${empty model.topTags}">
    	<p>Please configure which genres to use <a href="tagSettings.view">here</a>.
    </c:when>
    <c:otherwise>
		<p>Choose one or more genres.</p>

		<c:forEach items="${model.topTags}" var="topTag">
			<span class="off" onclick='changeClass(this,"on","off");'>${topTag}</span>
		</c:forEach>

		<div style="clear:both"/>

		<form>
			<input type="button" value="Play radio!" onClick="playGenreRadio();">
		</form> 
    </c:otherwise>
</c:choose>

</body></html>