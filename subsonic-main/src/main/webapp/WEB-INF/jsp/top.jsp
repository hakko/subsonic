<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>

<%@ include file="include.jspf"%>

<nav class="navbar navbar-default bgcolor2 topframe" role="navigation">

	<fmt:message key="top.home" var="home" />
	<fmt:message key="top.now_playing" var="nowPlaying" />
	<fmt:message key="top.settings" var="settings" />
	<fmt:message key="top.status" var="status" />
	<fmt:message key="top.podcast" var="podcast" />
	<fmt:message key="top.help" var="help" />

	<!-- Brand and toggle get grouped for better mobile display -->
	<div class="navbar-header">
		<button type="button" class="navbar-toggle" data-toggle="collapse"
			data-target="#bs-example-navbar-collapse-1">
			<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span>
			<span class="icon-bar"></span> <span class="icon-bar"></span>
		</button>
		<a href="#/" onclick="return toggleLeft();" class="navbar-brand"><img
			src="<spring:theme code="logoImage"/>" title="${help}" alt=""></a>
	</div>
	<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
		<ul class="nav navbar-nav">
			<li class="text-center"><c:if
					test="${not model.mediaFoldersExist}">
					<p class="warning">
						<fmt:message key="top.missing" />
					</p>
				</c:if></li>
			<li class="text-center"><a href="#/home"><img
					src="<spring:theme code="homeImage"/>" title="${home}"
					alt="${home}">${home}</a></li>
			<li class="text-center"><a href="#/genres"><img
					src="<spring:theme code="genresImage"/>" title="Genres"
					alt="Genres">Genres</a></li>
			<li class="text-center"><a href="#/radio"><img
					src="<spring:theme code="radioImage"/>" title="Radio" alt="Radio">Radio</a></li>
			<li class="text-center"><a href="#/fileTree"><img
					src="<spring:theme code="fileTreeImage"/>" title="File tree"
					alt="File tree">File tree</a></li>
			<li class="text-center"><a href="#/podcastReceiver"><img
					src="<spring:theme code="podcastLargeImage"/>" title="${podcast}"
					alt="${podcast}">${podcast}</a></li>
			<li class="text-center"><a href="#/nowPlaying"><img
					src="<spring:theme code="nowPlayingImage"/>" title="${nowPlaying}"
					alt="${nowPlaying}">${nowPlaying}</a></li>
			<c:if test="${model.user.settingsRole}">
				<li class="text-center"><a href="#/settings"><img
						src="<spring:theme code="settingsImage"/>" title="${settings}"
						alt="${settings}">${settings}</a></li>
			</c:if>
			<li class="text-center"><a href="#/status"><img
					src="<spring:theme code="statusImage"/>" title="${status}"
					alt="${status}">${status}</a></li>
			<li class="text-center"><a href="#/help"><img
					src="<spring:theme code="helpImage"/>" title="${help}"
					alt="${help}">${help}</a></li>

		</ul>
		<form class="navbar-form navbar-left" role="search" method="POST"
			action="search.view" data-target="main" name="searchForm" onsubmit="return submitForm(this);">
			<div class="form-group">
				<input type="text" name="query" class="form-control"
					placeholder="Search">
			</div>
			<a href="#" onclick="return submitForm(this);"><img
				src="<spring:theme code="searchImage"/>" alt="${search}"
				title="${search}"></a>
		</form>
		<p class="navbar-text">
			<a href="j_spring_security_logout"><fmt:message
					key="top.logout">
					<fmt:param value="${model.user.username}" />
				</fmt:message></a>
			<c:if test="${not model.licensed}">
				<br>
				<a href="#/donate"><img
					src="<spring:theme code="donateSmallImage"/>" alt=""></a>
				<a href="#/donate"><fmt:message
						key="donate.title" /></a>
			</c:if>
		</p>
	</div>
</nav>
