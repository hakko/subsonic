<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<%@ include file="toggleStar.jspf" %>

<h1>
    <img src="<spring:theme code="searchImage"/>" alt=""/>
    <fmt:message key="search.title"/>
</h1>

<form:form role="form" commandName="command" method="post" action="search.view" name="searchForm" onSubmit="return submitForm(this)">
<div class="form-group">
<label for="query"><fmt:message key="search.query"/></label>

<form:input path="query" size="35" class="form-control"/>
</div>
<input class="btn btn-default" type="button" onclick="return submitForm(this);" value="<fmt:message key="search.search"/>"/>
</form:form>
<br />
<c:if test="${not command.indexCreated}">
    <div class="alert alert-warning"><fmt:message key="search.index"/></div>
</c:if>

<c:if test="${command.indexCreated and empty command.artists and empty command.albums and empty command.songs}">
    <div class="alert alert-warning"><fmt:message key="search.hits.none"/></div>
</c:if>

<c:if test="${not empty command.artists}">
    <table class="table table-bordered table-striped table-hover table-condensed">
    <thead>
    <tr>
    <td><fmt:message key="search.hits.artists"/></td>
    </tr>
    </thead>
        <c:forEach items="${command.artists}" var="artist" varStatus="loopStatus">

            <sub:url value="/artist.view" var="artistUrl">
                <sub:param name="id" value="${artist.uri}"/>
            </sub:url>

            <tr>
                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
                    <a href="${artistUrl}">${artist.name}</a>
                </td>
            </tr>

            </c:forEach>
    </table>
</c:if>

<c:if test="${not empty command.albums}">
    <table class="table table-bordered table-striped table-hover table-condensed">
    <thead>
    <tr>
    <td colspan="2"><fmt:message key="search.hits.albums"/></td>
    </tr>
    </thead>
        <c:forEach items="${command.albums}" var="album" varStatus="loopStatus">

			      <sub:url value="/artist.view" var="artistUrl">
                <sub:param name="id" value="${album.artist.uri}"/>
            </sub:url>

            <sub:url value="/artist.view" var="albumUrl">
                <sub:param name="id" value="${album.artist.uri}"/>
                <sub:param name="albumId" value="${album.uri}"/>
            </sub:url>

            <tr>
                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
                    <a href="${albumUrl}">${album.name}</a>
                </td>

                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
                    <a href="${artistUrl}">${album.artist.name}</a>
                </td>
            </tr>

            </c:forEach>
    </table>
</c:if>

<c:if test="${not empty command.songs}">
    <table class="table table-bordered table-striped table-hover table-condensed">
    <thead>
    <tr>
    <td colspan="4"><fmt:message key="search.hits.songs"/></td>
    </tr>
    </thead>
        <c:forEach items="${command.songs}" var="track" varStatus="loopStatus">

			<sub:url value="/artist.view" var="artistUrl">
                <sub:param name="id" value="${track.metaData.artistUri}"/>
            </sub:url>

            <sub:url value="/artist.view" var="albumUrl">
                <sub:param name="id" value="${track.metaData.artistUri}"/>
                <sub:param name="albumId" value="${track.metaData.albumUri}"/>
            </sub:url>

            <tr>
                <c:import url="playAddDownload.jsp">
                  <c:param name="id" value="[${track.uri}]"/>
					        <c:param name="starred" value="${command.isTrackStarred[loopStatus.index]}"/>
					        <c:param name="starId" value="${track.uri}"/>
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
                    <a href="${albumUrl}">${track.metaData.album}</a>
                </td>

                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
                    <a href="${artistUrl}">${track.metaData.artist}</a>
                </td>
            </tr>

            </c:forEach>
    </table>
</c:if>

<h1>
    <img src="<spring:theme code="searchImage"/>" alt=""/>
    Advanced search
</h1>
<sub:url value="/advancedSearch.view" var="advancedSearchUrl">
	<sub:param name="searchQuery" value="${command.query}"/>
</sub:url>
<div class="forward"><a href="${advancedSearchUrl}">Search for '${command.query}' in advanced mode</a></div>
  <script type="text/javascript">
    function init() {
        dwr.engine.setErrorHandler(null);
  	}
    init();
	</script>
</div>
