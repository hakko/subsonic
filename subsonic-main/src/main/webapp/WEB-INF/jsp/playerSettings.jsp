<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.PlayerSettingsCommand"--%>

<%@ include file="include.jspf"%>
<div class="mainframe bgcolor1">

	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="player" />
		<c:param name="restricted" value="${not command.admin}" />
	</c:import>

	<fmt:message key="common.unknown" var="unknown" />

	<form:form commandName="command" method="post"
		action="playerSettings.view"
		onsubmit="return submitForm(this, 'Player settings saved.')"
		role="form" class="form">
		<form:hidden path="playerId" />
		<div class="panel panel-default">
			<div class="panel-heading">Player settings</div>
			<div class="panel-body">



				<c:choose>
					<c:when test="${empty command.players}">
						<p>
							<fmt:message key="playersettings.noplayers" />
						</p>
					</c:when>
					<c:otherwise>

						<c:url value="playerSettings.view" var="deleteUrl">
							<c:param name="delete" value="${command.playerId}" />
						</c:url>
						<c:url value="playerSettings.view" var="cloneUrl">
							<c:param name="clone" value="${command.playerId}" />
						</c:url>

						<div class="form-group">
							<label for="player"> <fmt:message
									key="playersettings.title" />
							</label> <select name="player"
								onchange="return loadInFrame(this, 'playerSettings.view?id=' + options[selectedIndex].value);">
								<c:forEach items="${command.players}" var="player">
									<option ${player.id eq command.playerId ? "selected" : ""}
										value="${player.id}">${player.description}</option>
								</c:forEach>
							</select>
						</div>

						<div class="forward">
							<a href="${deleteUrl}"><fmt:message
									key="playersettings.forget" /></a>
						</div>
						<div class="forward">
							<a href="${cloneUrl}"><fmt:message key="playersettings.clone" /></a>
						</div>

						<c:forEach items="${command.technologyHolders}"
							var="technologyHolder">
							<c:set var="technologyName">
								<fmt:message
									key="playersettings.technology.${fn:toLowerCase(technologyHolder.name)}.title" />
							</c:set>
							<div class="radio">
								<label> <form:radiobutton id="radio-${technologyName}"
										path="technologyName" value="${technologyHolder.name}" /> <label
									for="radio-${technologyName}">${technologyName}</label>
								</label>
								<p class="help-block">
									<fmt:message
										key="playersettings.technology.${fn:toLowerCase(technologyHolder.name)}.text" />
								</p>
							</div>
						</c:forEach>


						<div class="form-group">
							<label class="control-label"><fmt:message
									key="playersettings.type" /> </label>
							<div>
								<p class="form-control-static">
									<c:choose>
										<c:when test="${empty command.type}">${unknown}</c:when>
										<c:otherwise>${command.type}</c:otherwise>
									</c:choose>
								</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label"><fmt:message
									key="playersettings.lastseen" /></label>
							<div>
								<p class="form-control-static">
									<fmt:formatDate value="${command.lastSeen}" type="both"
										dateStyle="long" timeStyle="medium" />
								</p>
							</div>
						</div>

						<div class="form-group">
							<label for="name"><fmt:message key="playersettings.name" />
								<c:import url="helpToolTip.jsp">
									<c:param name="topic" value="playername" />
								</c:import></label>
							<form:input path="name" size="16" class="form-control" />
						</div>

						<tr>
							<td><fmt:message key="playersettings.coverartsize" /></td>
							<td><form:select path="coverArtSchemeName"
									cssStyle="width:8em">
									<c:forEach items="${command.coverArtSchemeHolders}"
										var="coverArtSchemeHolder">
										<c:set var="coverArtSchemeName">
											<fmt:message
												key="playersettings.coverart.${fn:toLowerCase(coverArtSchemeHolder.name)}" />
										</c:set>
										<form:option value="${coverArtSchemeHolder.name}"
											label="${coverArtSchemeName}" />
									</c:forEach>
								</form:select></td>
							<td colspan="2"><c:import url="helpToolTip.jsp">
									<c:param name="topic" value="cover" />
								</c:import></td>
						</tr>

						<tr>
							<td><fmt:message key="playersettings.maxbitrate" /></td>
							<td><form:select path="transcodeSchemeName"
									cssStyle="width:8em">
									<c:forEach items="${command.transcodeSchemeHolders}"
										var="transcodeSchemeHolder">
										<form:option value="${transcodeSchemeHolder.name}"
											label="${transcodeSchemeHolder.description}" />
									</c:forEach>
								</form:select></td>
							<td><c:import url="helpToolTip.jsp">
									<c:param name="topic" value="transcode" />
								</c:import></td>
							<td class="warning"><c:if
									test="${not command.transcodingSupported}">
									<fmt:message key="playersettings.nolame" />
								</c:if></td>
						</tr>

						</table>

						<table class="indent" style="border-spacing: 3pt">

							<tr>
								<td><form:checkbox path="dynamicIp" id="dynamicIp"
										cssClass="checkbox" /> <label for="dynamicIp"><fmt:message
											key="playersettings.dynamicip" /></label></td>
								<td><c:import url="helpToolTip.jsp">
										<c:param name="topic" value="dynamicip" />
									</c:import></td>
							</tr>

							<tr>
								<td><form:checkbox path="autoControlEnabled"
										id="autoControlEnabled" cssClass="checkbox" /> <label
									for="autoControlEnabled"><fmt:message
											key="playersettings.autocontrol" /></label></td>
								<td><c:import url="helpToolTip.jsp">
										<c:param name="topic" value="autocontrol" />
									</c:import></td>
							</tr>
						</table>

						<c:if test="${not empty command.allTranscodings}">
							<table class="indent">
								<tr>
									<td><b><fmt:message key="playersettings.transcodings" /></b></td>
								</tr>
								<c:forEach items="${command.allTranscodings}" var="transcoding"
									varStatus="loopStatus">
									<c:if test="${loopStatus.count % 3 == 1}">
										<tr>
									</c:if>
									<td style="padding-right: 2em"><form:checkbox
											path="activeTranscodingIds" id="transcoding${transcoding.id}"
											value="${transcoding.id}" cssClass="checkbox" /> <label
										for="transcoding${transcoding.id}">${transcoding.name}</label></td>
									<c:if
										test="${loopStatus.count % 3 == 0 or loopStatus.count eq fn:length(command.allTranscodings)}">
										</tr>
									</c:if>
								</c:forEach>
							</table>
						</c:if>
			</div>
			<div class="panel-footer">
				<button type="submit" class="btn btn-primary">
					<fmt:message key="common.save" />
				</button>
				<button type="button" class="btn btn-default"
					onclick="return loadInFrame(this, 'nowPlaying.view');">
					<fmt:message key="common.cancel" />
				</button>
			</div>


			</c:otherwise>
			</c:choose>
	</form:form>

	<c:if test="${command.reloadNeeded}">
		<script language="javascript" type="text/javascript">
      jQuery('.playlist').load("playlist.view?");
    </script>
	</c:if>
</div>
