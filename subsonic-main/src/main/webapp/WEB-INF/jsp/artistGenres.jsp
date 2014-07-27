<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
	<%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<%@ include file="toggleStar.jspf" %>


<div style="padding: 15px;">

<h1>
<a href="#" onclick="toggleStar('art', ${sub:esc(model.artistId)}, '#starImage${sub:jqesc(model.artistId)}'); return false;">
	<c:choose>
		<c:when test="${model.artistStarred}">
			<img id="starImage${model.artistId}" src="<spring:theme code="ratingOnImage"/>" alt="">
		</c:when>
		<c:otherwise>
			<img id="starImage${model.artistId}" src="<spring:theme code="ratingOffImage"/>" alt="">
		</c:otherwise>
	</c:choose>
</a>
${model.artistName}
</h1>

<c:if test="${not empty model.artistInfo}">
	<table>
		<tr>
			<td style="vertical-align:top">
				<div class="outerpair1"><div class="outerpair2"><div class="shadowbox"><div class="innerbox">
					<img id="bioArt" width="${model.artistInfoImageSize}" height="${model.artistInfoImageSize}" src="${model.artistInfo.largeImageUrl}" alt="">
				</div></div></div></div>
			</td>
			<td style="vertical-align:top">
				<div style="width:525px;">
					<div id="bio0">${model.artistInfo.bioSummary}</div>
				</div>
			</td>
		</tr>
	</table>
</c:if>

<div id="inp" style="padding-top: 15px; padding-bottom: 25px"></div>

<div style="padding-bottom: 25px">
	<img id="add" src="<spring:theme code="plusImage"/>" alt="Add">
	<select id="genres">
		<c:forEach items="${model.tags}" var="tag">
			<option>${tag}</option>
		</c:forEach>
	</select>
</div>

<sub:url value="artist.view" var="backUrl">
	<sub:param name="id" value="${model.artistId}"/>
</sub:url>
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>

<script type="text/javascript">

    function init() {
        dwr.engine.setErrorHandler(dwrErrorHandler);
	}

	function add(name, count) {
		jQuery("#inp").append('<div style="display: block">\
			<img class="dec" src="<spring:theme code="removeImage"/>" alt="Decrease" style="float: left; padding-top: 4px">\
			<div class="popularity" style="float: left"><div class="bar bgcolor3" style="width: ' + count + '%"></div><div class="genre">' + name + '</div></div>\
			<img class="inc" src="<spring:theme code="plusImage"/>" alt="Increase" style="float: left; padding-top: 4px">\
			<div style="clear:both;"></div></div>');
		
		jQuery(".dec:last").click(function() {
			set_width(jQuery(this), -25);
		});

		jQuery(".inc:last").click(function() {
			set_width(jQuery(this), 25);
		});

	}

	function set_width(element, diff) {
		var bar = element.parent().find(".bar");
		var width = bar.width();
		var genre = element.parent().find(".genre").text();
		width = width + diff;
		if (width > 500) { width = 500 };
		if (width <=  0) {
			element.parent().remove();
			jQuery("#genres").append("<option>" + genre + "</option>");
		} else {
			bar.width(width);
		}
		uiTagService.tagArtist('${model.artistId}', "${model.artistName}", genre, Math.round(width / 5), diff > 0);
	}

	jQuery(function() {

		<c:forEach items="${model.topTags}" var="tag">
			add("${tag.name}", ${tag.count} - (${tag.count} % 5));
		</c:forEach>
		
		jQuery("#add").click(function() {
			if (jQuery("#genres option").length > 0) {
				add(jQuery("#genres").val(), 50);
				jQuery(this).parent().find("option:selected").remove();
			}
		});
	});

  init();
</script>


</div>
</div>