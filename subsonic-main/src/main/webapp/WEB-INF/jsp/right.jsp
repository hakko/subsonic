<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="include.jspf" %>

<body class="bgcolor1 rightframe" style="padding-top:2em" onload="init()">

<script type="text/javascript">
    function init() {
        setupZoom('<c:url value="/"/>');
        dwr.engine.setErrorHandler(null);
    <c:if test="${model.showChat}">
        chatService.addMessage(null);
    </c:if>
    }
</script>

<c:if test="${model.showNowPlaying}">

    <!-- This script uses AJAX to periodically retrieve what all users are playing. -->
    <script type="text/javascript" language="javascript">

        startGetNowPlayingTimer();

        function startGetNowPlayingTimer() {
            nowPlayingService.getNowPlaying(getNowPlayingCallback);
            setTimeout("startGetNowPlayingTimer()", 10000);
        }

        function getNowPlayingCallback(nowPlaying) {
            var html = nowPlaying.length == 0 ? "" : "<h2><fmt:message key="main.nowplaying"/></h2><table>";
            for (var i = 0; i < nowPlaying.length; i++) {
                html += "<tr><td colspan='2' class='detail' style='padding-top:1em;white-space:nowrap'>";

                if (nowPlaying[i].avatarUrl != null) {
                    html += "<img src='" + nowPlaying[i].avatarUrl + "' style='padding-right:5pt'>";
                }
                html += "<b>" + nowPlaying[i].username + "</b></td></tr>"

                html += "<tr><td class='detail' style='padding-right:1em'>" +
                        "<a title='" + nowPlaying[i].tooltip + "' target='main' href='" + nowPlaying[i].albumUrl + "'><em>" +
                        nowPlaying[i].artist + "</em><br/>" + nowPlaying[i].title + "</a>";
				
				if (nowPlaying[i].lyricsUrl != null) {
					html += "<br/><span class='forward'><a href='" + nowPlaying[i].lyricsUrl + "' onclick=\"return popupSize(this, 'lyrics', 500, 550)\">" +
							"<fmt:message key="main.lyrics"/>" + "</a></span>";
				}
                html += "</td><td style='padding-top:1em'>";

                if (nowPlaying[i].coverArtUrl != null) {
                    html += "<a rel='zoom' href='" + nowPlaying[i].coverArtZoomUrl + "'>" +
                            "<img src='" + nowPlaying[i].coverArtUrl + "' width='64' height='64'></a>";
                }
                html += "</td></tr>";

                var minutesAgo = nowPlaying[i].minutesAgo;
                if (minutesAgo > 4) {
                    html += "<tr><td class='detail' colspan='2'>" + minutesAgo + " <fmt:message key="main.minutesago"/></td></tr>";
                }
            }
            html += "</table>";
            $('nowPlaying').innerHTML = html;
            prepZooms();
        }
    </script>

    <div id="nowPlaying">
    </div>

</c:if>

<c:if test="${model.showChat}">
    <script type="text/javascript">

        var revision = 0;
        startGetMessagesTimer();

        function startGetMessagesTimer() {
            chatService.getMessages(revision, getMessagesCallback);
            setTimeout("startGetMessagesTimer()", 10000);
        }

        function addMessage() {
            chatService.addMessage($("message").value);
            dwr.util.setValue("message", null);
            setTimeout("startGetMessagesTimer()", 500);
        }
        function clearMessages() {
            chatService.clearMessages();
            setTimeout("startGetMessagesTimer()", 500);
        }
        function getMessagesCallback(messages) {

            if (messages == null) {
                return;
            }
            revision = messages.revision;

            // Delete all the rows except for the "pattern" row
            dwr.util.removeAllRows("chatlog", { filter:function(div) {
                return (div.id != "chat-tmpl");
            }});

            // Create a new set cloned from the pattern row
            for (var i = 0; i < messages.messages.length; i++) {
                var message = messages.messages[i];
                var id = i + 1;
                dwr.util.cloneNode("chat-tmpl", { idSuffix:id });
                dwr.util.setValue("user" + id, message.username);
                dwr.util.setValue("date" + id, " [" + formatDate(message.date) + "]");
                dwr.util.setValue("content" + id, message.content);
                $("chat-tmpl" + id).show();
            }

            var clearDiv = $("clearDiv");
            if (clearDiv) {
                if (messages.messages.length == 0) {
                    clearDiv.hide();
                } else {
                    clearDiv.show();
                }
            }
        }
        function formatDate(date) {
            var hours = date.getHours();
            var minutes = date.getMinutes();
            var result = hours < 10 ? "0" : "";
            result += hours;
            result += ":";
            if (minutes < 10) {
                result += "0";
            }
            result += minutes;
            return result;
        }
    </script>

    <h2><fmt:message key="main.chat"/></h2>
    <div style="padding-top:0.3em;padding-bottom:0.3em">
        <input id="message" value=" <fmt:message key="main.message"/>" style="width:90%" onclick="dwr.util.setValue('message', null);" onkeypress="dwr.util.onReturn(event, addMessage)"/>
    </div>

    <table>
        <tbody id="chatlog">
        <tr id="chat-tmpl" style="display:none;margin:0;padding:0 0 0.15em 0;border:0"><td>
            <span id="user" class="detail" style="font-weight:bold"></span>&nbsp;<span id="date" class="detail"></span> <span id="content"></span></td>
        </tr>
        </tbody>
    </table>

    <c:if test="${model.user.adminRole}">
        <div id="clearDiv" style="display:none;" class="forward"><a href="#" onclick="clearMessages(); return false;"> <fmt:message key="main.clearchat"/></a></div>
    </c:if>
</c:if>

</body>
</html>
