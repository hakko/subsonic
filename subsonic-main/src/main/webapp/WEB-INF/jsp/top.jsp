<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>

<%@ include file="include.jspf"%>

<nav class="col-lg-12 navbar navbar-default <spring:theme code="navbar.theme" />  topframe" role="navigation">

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
			<li class="text-center"><a href="#/home"><i class="fa fa-home"></i><p>${home}</p></a></li>
			<li class="text-center"><a href="#/genres"><i class="fa fa-cloud"></i><br />Genres</a></li>
			<li class="text-center"><a href="#/radio"><i class="fa fa-signal"></i><br />Radio</a></li>
			<li class="text-center"><a href="#/podcastReceiver"><i class="fa fa-bullhorn"></i><br />${podcast}</a></li>
			<li class="text-center"><a href="#/nowPlaying"><i class="fa fa-play-circle"></i><br />${nowPlaying}</a></li>
			<c:if test="${model.user.settingsRole}">
				<li class="text-center"><a href="#/settings"><i class="fa fa-cog"></i><br />${settings}</a></li>
			</c:if>
			<li class="text-center"><a href="#/status"><i class="fa fa-bar-chart"></i><br />${status}</a></li>
			<li class="text-center"><a href="#/help"><i class="fa fa-info-circle"></i><br />${help}</a></li>

		</ul>
		<form class="navbar-form navbar-left" role="search" method="POST"
			action="search.view" data-target="main" name="searchForm" onsubmit="return submitForm(this);">
			<div class="form-group">
				<input type="text" name="query" class="form-control"
					placeholder="Search">
			</div>
			<a href="#" onclick="return submitForm(this);"><i class="fa fa-search"></i></a>
		</form>
		<p class="navbar-text">
			<a href="j_spring_security_logout"><fmt:message
					key="top.logout">
					<fmt:param value="${model.user.username}" />
				</fmt:message></a>
			<c:if test="${not model.licensed}">
				<br>
				<a href="#/donate"><i class="fa fa-heart"></i></a>
				<a href="#/donate"><fmt:message
						key="donate.title" /></a>
			</c:if>
		</p>
	</div>
</nav>
