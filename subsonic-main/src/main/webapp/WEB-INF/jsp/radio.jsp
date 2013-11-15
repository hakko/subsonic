<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:form="http://www.springframework.org/tags/form"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:sub="http://subsonic.org/taglib/sub"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:str="http://jakarta.apache.org/taglibs/string-1.1" version="2.1">
	<jsp:directive.page session="false" />
	<jsp:directive.page contentType="text/html; charset=utf-8" />



	<div class="mainframe bgcolor1">

		<h1>
			<c:set var="radioImg">
				<spring:theme code="radioImage" />
			</c:set>
			<img alt="Radio" src="${radioImg}" /> Radio
		</h1>

		<c:choose>
			<c:when test="${empty model.topTags}">
				<p>
					Please configure which genres to use <a href="tagSettings.view">here</a>.
				</p>

			</c:when>
			<c:otherwise>
				<p>Choose one or more genres.</p>

				<c:forEach items="${model.topTags}" var="topTag">
					<span class="off" onclick="changeClass(this,'on','off');">
					${topTag}
					</span>
				</c:forEach>

				<div style="clear: both" />

					<input type="button" value="Play radio!"
						onClick="return playGenreRadio();" />
			</c:otherwise>
		</c:choose>

	</div>
</jsp:root>