<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="iso-8859-1"%>

<%@ include file="include.jspf"%>
<div class="mainframe bgcolor1">

	<c:import url="settingsHeader.jsp">
		<c:param name="cat" value="transcoding" />
	</c:import>

	<form method="post" action="transcodingSettings.view"
		onsubmit="return submitForm(this, 'Transcode settings saved.')">
		<div class="panel panel-default">
			<div class="panel-heading">Transcode settings</div>
			<div class="panel-body">

				<div class="table-responsive">
					<table class="table table-condensed">
						<tr>
							<th><fmt:message key="transcodingsettings.name" /></th>
							<th><fmt:message key="transcodingsettings.sourceformat" /></th>
							<th><fmt:message key="transcodingsettings.targetformat" /></th>
							<th><fmt:message key="transcodingsettings.step1" /></th>
							<th><fmt:message key="transcodingsettings.step2" /></th>
							<th style="padding-left: 1em"><fmt:message
									key="common.delete" /></th>
						</tr>

						<c:forEach items="${model.transcodings}" var="transcoding">
							<tr>
								<td><input style="font-family: monospace" type="text"
									name="name[${transcoding.id}]" size="10"
									value="${transcoding.name}" /></td>
								<td><input style="font-family: monospace" type="text"
									name="sourceFormats[${transcoding.id}]" size="36"
									value="${transcoding.sourceFormats}" /></td>
								<td><input style="font-family: monospace" type="text"
									name="targetFormat[${transcoding.id}]" size="10"
									value="${transcoding.targetFormat}" /></td>
								<td><input style="font-family: monospace" type="text"
									name="step1[${transcoding.id}]" size="60"
									value="${transcoding.step1}" /></td>
								<td><input style="font-family: monospace" type="text"
									name="step2[${transcoding.id}]" size="22"
									value="${transcoding.step2}" /></td>
								<td align="center" style="padding-left: 1em"><input
									type="checkbox" name="delete[${transcoding.id}]"
									class="checkbox" /></td>
							</tr>
						</c:forEach>

						<tr>
							<th colspan="6" align="left" style="padding-top: 1em"><fmt:message
									key="transcodingsettings.add" /></th>
						</tr>

						<tr>
							<td><input style="font-family: monospace" type="text"
								name="name" size="10" value="${model.newTranscoding.name}" /></td>
							<td><input style="font-family: monospace" type="text"
								name="sourceFormats" size="36"
								value="${model.newTranscoding.sourceFormats}" /></td>
							<td><input style="font-family: monospace" type="text"
								name="targetFormat" size="10"
								value="${model.newTranscoding.targetFormat}" /></td>
							<td><input style="font-family: monospace" type="text"
								name="step1" size="60" value="${model.newTranscoding.step1}" /></td>
							<td><input style="font-family: monospace" type="text"
								name="step2" size="22" value="${model.newTranscoding.step2}" /></td>
							<td />
						</tr>

						<tr>
							<td colspan="6" style="padding-top: 0.1em"><input
								type="checkbox" id="defaultActive" name="defaultActive"
								class="checkbox" checked /> <label for="defaultActive"><fmt:message
										key="transcodingsettings.defaultactive" /></label></td>
						</tr>
					</table>
				</div>

				<div class="form-group">
					<label for="downsampleCommand"> <fmt:message
							key="advancedsettings.downsamplecommand" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="downsamplecommand" />
						</c:import>
					</label> <input style="font-family: monospace" type="text"
						name="downsampleCommand" size="100" class="form-control"
						value="${model.downsampleCommand}" />
				</div>
				<div class="form-group">
					<label for="hlsCommand"> <fmt:message
							key="advancedsettings.hlscommand" /> <c:import
							url="helpToolTip.jsp">
							<c:param name="topic" value="hlscommand" />
						</c:import>
					</label> <input style="font-family: monospace" type="text"
						name="hlsCommand" size="100" value="${model.hlsCommand}"
						class="form-control" />
				</div>
			</div>
			<div class="panel-footer">

				<input type="submit" value="<fmt:message key="common.save"/>"
					style="margin-right: 0.3em"  class="btn btn-primary" > 
					<input type="button"  class="btn btn-default"
					value="<fmt:message key="common.cancel"/>"
					onclick="location.href='nowPlaying.view'"
					style="margin-right: 1.3em"> <a
					href="http://www.subsonic.org/pages/transcoding.jsp"
					target="_blank"><fmt:message
						key="transcodingsettings.recommended" /></a>
			</div>
		</div>

	</form>

	<c:if test="${not empty model.error}">
		<p class="warning">
			<fmt:message key="${model.error}" />
		</p>
	</c:if>

	<div style="width: 60%">
		<fmt:message key="transcodingsettings.info">
			<fmt:param value="${model.transcodeDirectory}" />
			<fmt:param value="${model.brand}" />
		</fmt:message>
	</div>
</div>
