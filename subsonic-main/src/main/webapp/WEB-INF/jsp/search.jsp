<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.SearchCommand"--%>

<html><head>
    <%@ include file="head.jspf" %>
	<script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value='/script/scripts.js'/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/uiStarService.js"/>"></script>

    <script type="text/javascript">
        function more(rowSelector, moreId) {
            $(rowSelector).show();
            $(moreId).hide();
        }

    function init() {
        dwr.engine.setErrorHandler(null);
	}
	</script>

</head>
<body class="mainframe bgcolor1" onload="init()">

<%@ include file="toggleStar.jspf" %>

<h1>
    <img src="<spring:theme code="searchImage"/>" alt=""/>
    <fmt:message key="search.title"/>
</h1>

<form:form commandName="command" method="post" action="search.view" name="searchForm">
    <table>
        <tr>
            <td><fmt:message key="search.query"/></td>
            <td style="padding-left:0.25em"><form:input path="query" size="35"/></td>
            <td style="padding-left:0.25em"><input type="submit" onclick="search(0)" value="<fmt:message key="search.search"/>"/></td>
        </tr>
    </table>

</form:form>

<c:if test="${not command.indexCreated}">
    <p class="warning"><fmt:message key="search.index"/></p>
</c:if>

<c:if test="${command.indexCreated and empty command.artists and empty command.albums and empty command.songs}">
    <p class="warning"><fmt:message key="search.hits.none"/></p>
</c:if>

<c:if test="${not empty command.artists}">
    <h2><fmt:message key="search.hits.artists"/></h2>
    <table style="border-collapse:collapse">
        <c:forEach items="${command.artists}" var="artist" varStatus="loopStatus">

            <sub:url value="/artist.view" var="artistUrl">
                <sub:param name="id" value="${artist.id}"/>
            </sub:url>

            <tr class="artistRow" ${loopStatus.count > 5 ? "style='display:none'" : ""}>
                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
                    <a href="${artistUrl}">${artist.name}</a>
                </td>
            </tr>

            </c:forEach>
    </table>
    <c:if test="${fn:length(command.artists) gt 5}">
        <div id="moreArtists" class="forward"><a href="javascript:noop()" onclick="more('.artistRow', '#moreArtists')"><fmt:message key="search.hits.more"/></a></div>
    </c:if>
</c:if>

<c:if test="${not empty command.albums}">
    <h2><fmt:message key="search.hits.albums"/></h2>
    <table style="border-collapse:collapse">
        <c:forEach items="${command.albums}" var="album" varStatus="loopStatus">

            <sub:url value="/artist.view" var="albumUrl">
                <sub:param name="id" value="${album.artist.id}"/>
                <sub:param name="albumId" value="${album.id}"/>
            </sub:url>

            <tr class="albumRow" ${loopStatus.count > 5 ? "style='display:none'" : ""}>
                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
                    <a href="${albumUrl}">${album.name}</a>
                </td>

                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
                    <span class="detail">${album.artist.name}</span>
                </td>
            </tr>

            </c:forEach>
    </table>
    <c:if test="${fn:length(command.albums) gt 5}">
        <div id="moreAlbums" class="forward"><a href="javascript:noop()" onclick="more('.albumRow', '#moreAlbums')"><fmt:message key="search.hits.more"/></a></div>
    </c:if>
</c:if>


<c:if test="${not empty command.songs}">
    <h2><fmt:message key="search.hits.songs"/></h2>
    <table style="border-collapse:collapse">
        <c:forEach items="${command.songs}" var="track" varStatus="loopStatus">

            <sub:url value="/artist.view" var="albumUrl">
                <sub:param name="id" value="${track.metaData.artistId}"/>
                <sub:param name="albumId" value="${track.metaData.albumId}"/>
            </sub:url>

            <tr class="songRow" ${loopStatus.count > 15 ? "style='display:none'" : ""}>
                <c:import url="playAddDownload.jsp">
                    <c:param name="id" value="[${track.id}]"/>
					<c:param name="starred" value="${command.isTrackStarred[loopStatus.index]}"/>
					<c:param name="starId" value="${track.id}"/>
                    <c:param name="playEnabled" value="${command.user.streamRole and not command.partyModeEnabled}"/>
                    <c:param name="enqueueEnabled" value="${command.user.streamRole}"/>
                    <c:param name="addEnabled" value="${command.user.streamRole}"/>
                    <c:param name="downloadEnabled" value="${command.user.downloadRole and not command.partyModeEnabled}"/>
                    <c:param name="asTable" value="true"/>
                </c:import>

                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
                        ${track.name}
                </td>

                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
                    <a href="${albumUrl}"><span class="detail">${track.metaData.album}</span></a>
                </td>

                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
                    <span class="detail">${track.metaData.artist}</span>
                </td>
            </tr>

            </c:forEach>
    </table>
<c:if test="${fn:length(command.songs) gt 15}">
    <div id="moreSongs" class="forward"><a href="javascript:noop()" onclick="more('.songRow', '#moreSongs')"><fmt:message key="search.hits.more"/></a></div>
</c:if>
</c:if>

</body></html>