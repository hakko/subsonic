<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jspf" %>

<div class="bgcolor2 playlistframe">


<div class="bgcolor2" style="top:0; width:100%;padding-top:0.5em">
    <table style="white-space:nowrap;">
        <tr style="white-space:nowrap;">
            <c:if test="${model.user.settingsRole}">
                <td><select name="player" onchange="location='playlist.view?player=' + options[selectedIndex].value;">
                    <c:forEach items="${model.players}" var="player">
                        <option ${player.id eq model.player.id ? "selected" : ""} value="${player.id}">${player.shortDescription}</option>
                    </c:forEach>
                </select></td>
            </c:if>
            <c:if test="${model.player.web}">
                <td style="width:340px; height:24px;padding-left:10px;padding-right:10px"><div id="mediaplayer">
                    <p>Loading the player ...</p>
                </div></td>
            </c:if>

            <c:if test="${model.user.streamRole and not model.player.web}">
                <td style="white-space:nowrap;" id="stop"><b><a href="#" onclick="return onStop()"><fmt:message key="playlist.stop"/></a></b> | </td>
                <td style="white-space:nowrap;" id="start"><b><a href="#" onclick="return onStart()"><fmt:message key="playlist.start"/></a></b> | </td>
            </c:if>

            <c:if test="${model.player.jukebox}">
                <td style="white-space:nowrap;">
                    <img src="<spring:theme code="volumeImage"/>" alt="">
                </td>
                <td style="white-space:nowrap;">
                    <div class="slider bgcolor2" id="slider-1" style="width:90px">
                        <input class="slider-input" id="slider-input-1" name="slider-input-1">
                    </div>
                    <script type="text/javascript">

                        var updateGainTimeoutId = 0;
                        slider = new Slider(document.getElementById("slider-1"), document.getElementById("slider-input-1"));
                        slider.onchange = function () {
                            clearTimeout(updateGainTimeoutId);
                            updateGainTimeoutId = setTimeout("updateGain()", 250);
                        };

                        function updateGain() {
                            var gain = slider.getValue() / 100.0;
                            onGain(gain);
                        }
                    </script>
                </td>
            </c:if>

            <c:if test="${model.player.web}">
                <td style="white-space:nowrap;"><a href="#" onclick="return onPrevious()"><b>&laquo;</b></a></td>
                <td style="white-space:nowrap;"><a href="#" onclick="return onNext(false)"><b>&raquo;</b></a> |</td>
            </c:if>

            <td style="white-space:nowrap;"><a href="#" onclick="return onClear()"><fmt:message key="playlist.clear"/></a> |</td>
            <td style="white-space:nowrap;"><a href="#" onclick="return onShuffle()"><fmt:message key="playlist.shuffle"/></a> |</td>

            <c:if test="${model.player.web or model.player.jukebox or model.player.external}">
                <td style="white-space:nowrap;"><a href="#" onclick="return onToggleRepeat()"><span id="toggleRepeat"><fmt:message key="playlist.repeat_on"/></span></a> |</td>
            </c:if>

            <td style="white-space:nowrap;"><a href="#" onclick="return onUndo()"><fmt:message key="playlist.undo"/></a> |</td>

            <c:if test="${model.user.settingsRole}">
                <td style="white-space:nowrap;"><a href="playerSettings.view?id=${model.player.id}" target="main"><fmt:message key="playlist.settings"/></a> |</td>
            </c:if>

            <td style="white-space:nowrap;"><select id="moreActions" onchange="actionSelected(this.options[selectedIndex].id)">
                <option id="top" selected="selected"><fmt:message key="playlist.more"/></option>
                <option style="color:blue;"><fmt:message key="playlist.more.playlist"/></option>
                <option id="loadPlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.load"/></option>
                <c:if test="${model.user.playlistRole}">
                    <option id="savePlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.save"/></option>
                </c:if>
                <c:if test="${model.user.downloadRole}">
                    <option id="downloadPlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="common.download"/></option>
                </c:if>
                <c:if test="${model.user.shareRole}">
                    <option id="sharePlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="main.more.share"/></option>
                </c:if>
                <option id="sortByTrack">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.sortbytrack"/></option>
                <option id="sortByAlbum">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.sortbyalbum"/></option>
                <option id="sortByArtist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.sortbyartist"/></option>
                <option style="color:blue;"><fmt:message key="playlist.more.selection"/></option>
                <option id="selectAll">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectall"/></option>
                <option id="selectNone">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectnone"/></option>
                <option id="removeSelected">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.remove"/></option>
                <c:if test="${model.user.downloadRole}">
                    <option id="download">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="common.download"/></option>
                </c:if>
                <c:if test="${model.user.playlistRole}">
                    <option id="appendPlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.append"/></option>
                </c:if>
            </select>
            </td>

        </tr></table>
</div>

<p id="empty"><em><fmt:message key="playlist.empty"/></em></p>

<table style="border-collapse:collapse;white-space:nowrap;">
    <tbody id="playlistBody">
        <tr id="playlist-tmpl" style="display:none;margin:0;padding:0;border:0">
            <td class="bgcolor2">
                <img id="removeSong" onclick="onRemove(this.id.substring(10) - 1)" src="<spring:theme code="removeImage"/>"
                     alt="<fmt:message key="playlist.remove"/>" title="<fmt:message key="playlist.remove"/>"></td>
            <td class="bgcolor2">
                <img id="up" onclick="onUp(this.id.substring(2) - 1)" src="<spring:theme code="upImage"/>"
                     alt="<fmt:message key="playlist.up"/>" title="<fmt:message key="playlist.up"/>"></td>
            <td class="bgcolor2">
                <img id="down" onclick="onDown(this.id.substring(4) - 1)" src="<spring:theme code="downImage"/>"
                     alt="<fmt:message key="playlist.down"/>" title="<fmt:message key="playlist.down"/>"></td>

            <td class="bgcolor2" style="padding-left: 0.1em"><input type="checkbox" class="checkbox" id="songIndex"></td>
            <td style="padding-right:0.25em"></td>

            <c:if test="${model.visibility.trackNumberVisible}">
                <td style="padding-right:0.5em;text-align:right"><span class="detail" id="trackNumber">1</span></td>
            </c:if>

            <td style="padding-right:1.25em">
                <img id="currentImage" src="<spring:theme code="currentImage"/>" alt="" style="display:none">
                <c:choose>
                    <c:when test="${model.player.externalWithPlaylist}">
                        <span id="title">Title</span>
                    </c:when>
                    <c:otherwise>
                        <a id="titleUrl" href="#">Title</a>
                    </c:otherwise>
                </c:choose>
            </td>

            <c:if test="${model.visibility.albumVisible}"><td style="padding-right:1.25em"><a id="albumUrl" target="main"><span id="album" class="detail">Album</span></a></td></c:if>
            <c:if test="${model.visibility.artistVisible}"><td style="padding-right:1.25em"><a id="artistUrl" target="main"><span id="artist" class="detail">Artist</span></a></td></c:if>
			<c:if test="${model.visibility.composerVisible}"><td style="padding-right:1.25em"><span id="composer" class="detail">Composer</span></td></c:if>
            <c:if test="${model.visibility.genreVisible}"><td style="padding-right:1.25em"><span id="genre" class="detail">Genre</span></td></c:if>
            <c:if test="${model.visibility.yearVisible}"><td style="padding-right:1.25em"><span id="year" class="detail">Year</span></td></c:if>
            <c:if test="${model.visibility.formatVisible}"><td style="padding-right:1.25em"><span id="format" class="detail">Format</span></td></c:if>
            <c:if test="${model.visibility.fileSizeVisible}"><td style="padding-right:1.25em;text-align:right;"><span id="fileSize" class="detail">Size</span></td></c:if>
            <c:if test="${model.visibility.durationVisible}"><td style="padding-right:1.25em;text-align:right;"><span id="duration" class="detail">Duration</span></td></c:if>
            <c:if test="${model.visibility.bitRateVisible}"><td style="padding-right:0.25em"><span id="bitRate" class="detail">Bit Rate</span></td></c:if>
        </tr>
    </tbody>
</table>
</div>


<script type="text/javascript" language="javascript">
    var songs = null;
    var currentArtistId = null;
    var currentAlbumId = null;
    var currentStreamUrl = null;
    var startPlayer = false;
    var repeatEnabled = false;
    var slider = null;

    function init() {
        dwr.engine.setErrorHandler(null);
        startTimer();

    <c:choose>
    <c:when test="${model.player.web}">
        createPlayer();
    </c:when>
    <c:otherwise>
        getPlaylist();
    </c:otherwise>
    </c:choose>
    }


    function startTimer() {
        <!-- Periodically check if the current song has changed. -->
        nowPlayingService.getNowPlayingForCurrentPlayer(nowPlayingCallback);
        setTimeout("startTimer()", 10000);
    }


    function nowPlayingCallback(nowPlayingInfo) {
        if (nowPlayingInfo != null && nowPlayingInfo.streamUrl != currentStreamUrl) {
            getPlaylist();
            if (currentArtistId != nowPlayingInfo.artistId && currentAlbumId != nowPlayingInfo.albumId && top.main.updateNowPlaying) {
                top.main.location.replace("nowPlaying.view?");
                currentArtistId = nowPlayingInfo.artistId;
				currentAlbumId = nowPlayingInfo.albumId;
            }
        <c:if test="${not model.player.web}">
            currentStreamUrl = nowPlayingInfo.streamUrl;
            updateCurrentImage();
        </c:if>
        }
    }
    
    function createPlayer() {
        jwplayer('mediaplayer').setup({
           width: 340,
           height: 24,
           id: 'player1',
           backcolor: "<spring:theme code="backgroundColor"/>",
           frontcolor: "<spring:theme code="textColor"/>",
           dock: false,
           'controlbar.position': 'bottom',
           'modes': [
                {type: 'flash', src: "<c:url value="/flash/jw-player-5.10.swf"/>"},
                {type: 'html5'},
                {type: 'download'}
            ]
        });
        jwplayer('mediaplayer').onReady(
            function(event) {
                getPlaylist();
            }
        );        
        jwplayer('mediaplayer').onComplete(
            function(event) {
                onNext(repeatEnabled);
            }
        );
    }

    function getPlaylist() {
        playlistService.getPlaylist(playlistCallback);
    }

	function verifyIfPartyMode() {
		var ok = true;
    <c:if test="${model.partyMode}">
        ok = confirm("<fmt:message key="playlist.confirmclear"/>");
    </c:if>
		return ok;
	}


    function onClear() {
        if (verifyIfPartyMode()) {
            playlistService.clear(playlistCallback);
        }
        return false;
    }
    function onStart() {
        playlistService.start(playlistCallback);
        return false;
    }
    function onStop() {
        playlistService.stop(playlistCallback);
        return false;
    }
    function onGain(gain) {
        playlistService.setGain(gain);
        return false;
    }
    function onSkip(index) {
    <c:choose>
    <c:when test="${model.player.web}">
        skip(index);
    </c:when>
    <c:otherwise>
        currentStreamUrl = songs[index].streamUrl;
        playlistService.skip(index, playlistCallback);
    </c:otherwise>
    </c:choose>
      return false;
    }
    function onNext(wrap) {
        var index = parseInt(getCurrentSongIndex()) + 1;
        if (wrap) {
            index = index % songs.length;
        }
        return skip(index);
    }
    function onPrevious() {
        return skip(parseInt(getCurrentSongIndex()) - 1);
    }
    function onPlay(path, mode) {
    	append = (mode != 'P');
        if (append || verifyIfPartyMode()) {
	        startPlayer = !append;
    	    playlistService.play(path, mode, playlistCallback);
    	}
      return false;
    }
    function onPlayRandom(path, mode) {
    	append = (mode != 'P');
        if (append || verifyIfPartyMode()) {
	        startPlayer = !append;
    	    playlistService.playRandom(path, mode, playlistCallback);
    	}
      return false;

    }
    
    function onPlayArtistRadio(path, mode) {
    	append = (mode != 'P');
        if (append || verifyIfPartyMode()) {
	    	startPlayer = !append;
    		playlistService.playArtistRadio(path, mode, playlistCallback);
    	}
      return false;

    }
    function onPlayTopTracks(path, mode) {
    	append = (mode != 'P');
        if (append || verifyIfPartyMode()) {
	    	startPlayer = !append;
    		playlistService.playTopTracks(path, mode, playlistCallback);
    	}
      return false;

    }
    function onPlayGenreRadio(genres) {
        if (verifyIfPartyMode()) {
	    	startPlayer = true;
    		playlistService.playGenreRadio(genres, playlistCallback);
    	}
       return false;

    }
    function onPlayGroupRadio(group) {
        if (verifyIfPartyMode()) {
	    	startPlayer = true;
    		playlistService.playGroupRadio(group, playlistCallback);
    	}
      return false;

    }
    function onPlayRelatedArtistsSampler(artistId, artistCount) {
        if (verifyIfPartyMode()) {
	    	startPlayer = true;
			playlistService.playRelatedArtistsSampler(artistId, artistCount, playlistCallback);
    	}
      return false;

	}

    function onShuffle() {
        playlistService.shuffle(playlistCallback);
        return false;

    }
    function onRemove(index) {
        playlistService.remove(index, playlistCallback);
        return false;
    }
    function onRemoveSelected() {
        var indexes = new Array();
        var counter = 0;
        for (var i = 0; i < songs.length; i++) {
            var index = i + 1;
            if ($("songIndex" + index).checked) {
                indexes[counter++] = i;
            }
        }
        playlistService.removeMany(indexes, playlistCallback);
    }

    function onUp(index) {
        playlistService.up(index, playlistCallback);
        return false;
    }
    function onDown(index) {
        playlistService.down(index, playlistCallback);
        return false;
    }
    function onToggleRepeat() {
        playlistService.toggleRepeat(playlistCallback);
        return false;
    }
    function onUndo() {
        playlistService.undo(playlistCallback);
        return false;
    }
    function onSortByTrack() {
        playlistService.sortByTrack(playlistCallback);
        return false;
    }
    function onSortByArtist() {
        playlistService.sortByArtist(playlistCallback);
        return false;
    }
    function onSortByAlbum() {
        playlistService.sortByAlbum(playlistCallback);
        return false;
    }


    function playlistCallback(playlist) {
try {
        songs = playlist.entries;
        repeatEnabled = playlist.repeatEnabled;
        if ($("start")) {
            if (playlist.stopEnabled) {
                $("start").hide();
                $("stop").show();
            } else {
                $("start").show();
                $("stop").hide();
            }
        }


        if ($("toggleRepeat")) {
            var text = repeatEnabled ? "<fmt:message key="playlist.repeat_on"/>" : "<fmt:message key="playlist.repeat_off"/>";
            dwr.util.setValue("toggleRepeat", text);
        }

        if (songs.length == 0) {
            $("empty").show();
        } else {
            $("empty").hide();
        }

        // Delete all the rows except for the "playlist-tmpl" row
        dwr.util.removeAllRows("playlistBody", { filter:function(tr) {
            return (tr.id != "playlist-tmpl");
        }});

        // Create a new set cloned from the pattern row
        for (var i = 0; i < songs.length; i++) {
            var song  = songs[i];
            var id = i + 1;

            dwr.util.cloneNode("playlist-tmpl", { idSuffix:id });
            if ($("trackNumber" + id)) {
                dwr.util.setValue("trackNumber" + id, song.trackNumber);
            }

            if ($("currentImage" + id) && song.streamUrl == currentStreamUrl) {
                $("currentImage" + id).show();
            }
            if ($("title" + id)) {
                dwr.util.setValue("title" + id, truncate(song.title));
                $("title" + id).title = song.title;
            }
            if ($("titleUrl" + id)) {
                dwr.util.setValue("titleUrl" + id, truncate(song.title));
                $("titleUrl" + id).title = song.title;
                $("titleUrl" + id).onclick = function () {onSkip(this.id.substring(8) - 1)};
            }
            if ($("album" + id)) {
                dwr.util.setValue("album" + id, truncate(song.album));
                $("album" + id).title = song.album;
                $("albumUrl" + id).href = "artist.view?id=" + song.artistId + "&albumId=" + song.albumId;
            }
            if ($("artist" + id)) {
                dwr.util.setValue("artist" + id, truncate(song.artist));
                $("artist" + id).title = song.artist;
                $("artistUrl" + id).href = "artist.view?id=" + song.artistId;
            }
            if ($("composer" + id)) {
                dwr.util.setValue("composer" + id, truncate(song.composer));
                $("composer" + id).title = song.composer;
            }
            if ($("genre" + id)) {
                dwr.util.setValue("genre" + id, song.genre);
            }
            if ($("year" + id)) {
                dwr.util.setValue("year" + id, song.year);
            }
            if ($("bitRate" + id)) {
                dwr.util.setValue("bitRate" + id, song.bitRate);
            }
            if ($("duration" + id)) {
                dwr.util.setValue("duration" + id, song.durationAsString);
            }
            if ($("format" + id)) {
                dwr.util.setValue("format" + id, song.format);
            }
            if ($("fileSize" + id)) {
                dwr.util.setValue("fileSize" + id, song.fileSize);
            }

            $("playlist-tmpl" + id).show();
            $("playlist-tmpl" + id).className = (i % 2 == 0) ? "bgcolor1" : "bgcolor2";
        }

        if (playlist.sendM3U) {
            parent.frames.main.location.href="play.m3u?";
        }

        if (slider) {
            slider.setValue(playlist.gain * 100);
        }

    <c:if test="${model.player.web}">
        triggerPlayer();
    </c:if>
} catch(e) {
window.console.log(e);
}
    }

    function triggerPlayer() {
        if (startPlayer) {
            startPlayer = false;
            if (songs.length > 0) {
                skip(0);
            }
        }
        updateCurrentImage();
        if (songs.length == 0) {
            jwplayer('mediaplayer').stop();
        }
    }

    function skip(index) {
        if (index < 0 || index >= songs.length) {
            return false;
        }

        var song = songs[index];
        currentStreamUrl = song.streamUrl;
        updateCurrentImage();
        
        var list = [{
            file: song.streamUrl,
            title: song.title,
            provider: "sound"        
        }];

        if (song.duration != null) {
            list[0].duration = song.duration;
        }
        
        if (song.format == "aac" || song.format == "m4a") {
            list[0].provider = "video";
        }        

        jwplayer('mediaplayer').load(list);
        jwplayer('mediaplayer').play(true);
        return false;
    }

    function updateCurrentImage() {
        for (var i = 0; i < songs.length; i++) {
            var song  = songs[i];
            var id = i + 1;
            var image = $("currentImage" + id);

            if (image) {
                if (song.streamUrl == currentStreamUrl) {
                    image.show();
                } else {
                    image.hide();
                }
            }
        }
    }

    function getCurrentSongIndex() {
        for (var i = 0; i < songs.length; i++) {
            if (songs[i].streamUrl == currentStreamUrl) {
                return i;
            }
        }
        return -1;
    }

    function truncate(s) {
        var cutoff = ${model.visibility.captionCutoff};

        if (s.length > cutoff) {
            return s.substring(0, cutoff) + "...";
        }
        return s;
    }

    <!-- actionSelected() is invoked when the users selects from the "More actions..." combo box. -->
    function actionSelected(id) {
        if (id == "top") {
            return;
        } else if (id == "loadPlaylist") {
            parent.frames.main.location.href = "loadPlaylist.view?";
        } else if (id == "savePlaylist") {
            parent.frames.main.location.href = "savePlaylist.view?";
        } else if (id == "downloadPlaylist") {
            location.href = "download.view?player=${model.player.id}";
        } else if (id == "sharePlaylist") {
            parent.frames.main.location.href = "createShare.view?player=${model.player.id}&" + getSelectedIndexes();
        } else if (id == "sortByTrack") {
            onSortByTrack();
        } else if (id == "sortByArtist") {
            onSortByArtist();
        } else if (id == "sortByAlbum") {
            onSortByAlbum();
        } else if (id == "selectAll") {
            selectAll(true);
        } else if (id == "selectNone") {
            selectAll(false);
        } else if (id == "removeSelected") {
            onRemoveSelected();
        } else if (id == "download") {
            location.href = "download.view?player=${model.player.id}&" + getSelectedIndexes();
        } else if (id == "appendPlaylist") {
            parent.frames.main.location.href = "appendPlaylist.view?player=${model.player.id}&" + getSelectedIndexes();
        }
        $("moreActions").selectedIndex = 0;
    }

    function getSelectedIndexes() {
        var result = "";
        for (var i = 0; i < songs.length; i++) {
            if ($("songIndex" + (i + 1)).checked) {
                result += "i=" + i + "&";
            }
        }
        return result;
    }

    function selectAll(b) {
        for (var i = 0; i < songs.length; i++) {
            $("songIndex" + (i + 1)).checked = b;
        }
    }

    init();
</script>
