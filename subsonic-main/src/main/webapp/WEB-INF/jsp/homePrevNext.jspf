<sub:url value="home.view" var="prevUrl">
	<sub:param name="listType" value="${model.listType}"/>
	<sub:param name="listGroup" value="${model.listGroup}"/>
	<sub:param name="query" value="${model.query}"/>
	<sub:param name="page" value="${model.page - 1}"/>
</sub:url>
<sub:url value="home.view" var="nextUrl">
	<sub:param name="listType" value="${model.listType}"/>
	<sub:param name="listGroup" value="${model.listGroup}"/>
	<sub:param name="query" value="${model.query}"/>
	<sub:param name="page" value="${model.page + 1}"/>
</sub:url>

<div style="padding-top:15px"/>

<c:if test="${model.page > 0}"><div class="back"><a href="${prevUrl}"><fmt:message key="common.previous"/></a></div></c:if>
<c:if test="${not empty model.morePages}"><div class="forward"><a href="${nextUrl}"><fmt:message key="common.next"/></a></div></c:if>
