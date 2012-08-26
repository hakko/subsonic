<script type="text/javascript">
<%@ include file="albumsHeader.jsp" %>
</script>

<c:choose>
<c:when test="${model.albumGridLayout}"><%@ include file="albumsGrid.jsp" %></c:when>
<c:otherwise><%@ include file="albums.jsp" %></c:otherwise>
</c:choose>

<%@ include file="homePrevNext.jsp" %>
