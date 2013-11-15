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

		<c:import url="settingsHeader.jsp">
			<c:param name="cat" value="share" />
			<c:param name="restricted" value="${not model.user.adminRole}" />
		</c:import>

		<form method="post" action="shareSettings.view" class="form"
			role="form" onsubmit="return submitForm(this);">

			<div class="table-responsive">
				<table class="table table-striped table-hover table-condensed">
					<tr>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.name" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.owner" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.description" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.expires" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.lastvisited" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.visits" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.files" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="sharesettings.expirein" /></th>
						<th style="padding-left: 1em"><fmt:message
								key="common.delete" /></th>
					</tr>

					<c:forEach items="${model.shareInfos}" var="shareInfo"
						varStatus="loopStatus">
						<c:set var="share" value="${shareInfo.share}" />

						<sub:url value="main.view" var="albumUrl">
							<sub:param name="path" value="${shareInfo.dir.path}" />
						</sub:url>

						<tr>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em"><a
								href="${model.shareBaseUrl}${share.name}" target="_blank">${share.name}</a></td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em">${share.username}</td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em"><input type="text"
								name="description[${share.id}]" size="40"
								value="${share.description}" /></td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em"><fmt:formatDate
									value="${share.expires}" type="date" dateStyle="medium" /></td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em"><fmt:formatDate
									value="${share.lastVisited}" type="date" dateStyle="medium" /></td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em; text-align: right">${share.visitCount}</td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em"><a href="${albumUrl}"
								title="${shareInfo.dir.name}"><str:truncateNicely upper="30">${fn:escapeXml(shareInfo.dir.name)}</str:truncateNicely></a></td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em"><label><input
									type="radio" name="expireIn[${share.id}]" value="7" /> <fmt:message
										key="sharesettings.expirein.week" /></label> <label><input
									type="radio" name="expireIn[${share.id}]" value="30" /> <fmt:message
										key="sharesettings.expirein.month" /></label> <label><input
									type="radio" name="expireIn[${share.id}]" value="365" /> <fmt:message
										key="sharesettings.expirein.year" /></label> <label><input
									type="radio" name="expireIn[${share.id}]" value="0" /> <fmt:message
										key="sharesettings.expirein.never" /></label></td>
							<td class='${loopStatus.count % 2 == 1 ? "bgcolor2" : ""}'
								style="padding-left: 1em" align="center"><input
								type="checkbox" name="delete[${share.id}]" class="checkbox" /></td>
						</tr>
					</c:forEach>

				</table>
			</div>
			<button type="submit" style="margin-right: 0.3em" class="btn btn-primary">
				<fmt:message key="common.save" />
			</button>
			<button type="button" onclick="return loadInFrame(this, 'nowPlaying.view');"  class="btn btn-default">
				<fmt:message key="common.cancel" />
			</button>
		</form>
	</div>
</jsp:root>