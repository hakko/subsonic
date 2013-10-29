<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

    <%@ include file="include.jspf" %>

	<c:set var="backUrl"><%= request.getHeader("referer") %></c:set>
	
    <sub:url value="videoPlayer.view" var="baseUrl"><sub:param name="id" value="${model.video.id}"/></sub:url>

    <sub:url value="/stream" var="streamUrl">
        <sub:param name="mfId" value="${model.video.id}"/>
    </sub:url>


<div class="mainframe bgcolor1" style="padding-bottom:0.5em">
<c:if test="${not model.popout}">
    <h1>${model.video.title}</h1>
</c:if>


<div id="wrapper" style="padding-top:1em">
    <div id="placeholder1"><a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a></div>
</div>

<div style="padding-top:0.7em;padding-bottom:0.7em">

    <span id="position" style="padding-right:0.5em">0:00</span>
    <select id="timeOffset" onchange="changeTimeOffset();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
        <c:forEach items="${model.skipOffsets}" var="skipOffset">
            <c:choose>
                <c:when test="${skipOffset.value - skipOffset.value mod 60 eq model.timeOffset - model.timeOffset mod 60}">
                    <option selected="selected" value="${skipOffset.value}">${skipOffset.key}</option>
                </c:when>
                <c:otherwise>
                    <option value="${skipOffset.value}">${skipOffset.key}</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>

    <select id="maxBitRate" onchange="changeBitRate();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
        <c:forEach items="${model.bitRates}" var="bitRate">
            <c:choose>
                <c:when test="${bitRate eq model.maxBitRate}">
                    <option selected="selected" value="${bitRate}">${bitRate} Kbps</option>
                </c:when>
                <c:otherwise>
                    <option value="${bitRate}">${bitRate} Kbps</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>
</div>

<c:choose>
    <c:when test="${model.popout}">
        <div class="back"><a href="javascript:popin();"><fmt:message key="common.back"/></a></div>
    </c:when>
    <c:otherwise>
        <div class="back" style="float:left;padding-right:2em"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>
        <div class="forward" style="float:left;"><a href="javascript:popout();"><fmt:message key="videoPlayer.popout"/></a></div>
    </c:otherwise>
</c:choose>

    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" language="javascript">

        var player;
        var position;
        var maxBitRate = ${model.maxBitRate};
        var timeOffset = ${model.timeOffset};

        function playerReady(thePlayer) {
            player = $("player1");
            player.addModelListener("TIME", "timeListener");

            play();
        }

        function play() {
            var list = new Array();
            list[0] = {
                file:"${streamUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + timeOffset + "&player=${model.player}",
                duration:${model.duration} - timeOffset,
                provider:"video"
            };
            player.sendEvent("LOAD", list);
            player.sendEvent("PLAY");
        }

        function timeListener(obj) {
            var newPosition = Math.round(obj.position);
            if (newPosition != position) {
                position = newPosition;
                updatePosition();
            }
        }

        function updatePosition() {
            var pos = getPosition();

            var minutes = Math.round(pos / 60);
            var seconds = pos % 60;

            var result = minutes + ":";
            if (seconds < 10) {
                result += "0";
            }
            result += seconds;
            $("position").innerHTML = result;
        }

        function changeTimeOffset() {
            timeOffset = $("timeOffset").getValue();
            play();
        }

        function changeBitRate() {
            maxBitRate = $("maxBitRate").getValue();
            timeOffset = getPosition();
            play();
        }

        function popout() {
            var url = "${baseUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + getPosition() + "&popout=true";
            popupSize(url, "video", 600, 400);
            window.location.href = "${backUrl}";
        }

        function popin() {
            window.close();
        }

        function getPosition() {
            return parseInt(timeOffset) + parseInt(position);
        }

            var flashvars = {
                id:"player1",
                skin:"<c:url value="/flash/whotube.zip"/>",
                screencolor:"000000",
                controlbar:"over",
                autostart:"false",
                bufferlength:3,
                backcolor:"<spring:theme code="backgroundColor"/>",
                frontcolor:"<spring:theme code="textColor"/>",
                provider:"video"
            };
            var params = {
                allowfullscreen:"true",
                allowscriptaccess:"always"
            };
            var attributes = {
                id:"player1",
                name:"player1"
            };

            var width = "${model.popout ? '100%' : '600'}";
            var height = "${model.popout ? '85%' : '360'}";
            swfobject.embedSWF("<c:url value="/flash/jw-player-5.10.swf"/>", "placeholder1", width, height, "9.0.0", false, flashvars, params, attributes);

    </script>


</div>
