<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jspf" %>

<script type="text/javascript" language="javascript">
    var songs = null;
    var currentArtistUri = null;
    var currentAlbumUri = null;
    var currentStreamUrl = null;
    var startPlayer = false;
    var repeatEnabled = false;
    var slider = null;
</script>


<div class="bgcolor2 playlistframe">


<div class="bgcolor2" style="top:0; width:100%;padding-top:0.5em">
  <div class="row form form-inline">
            <c:if test="${model.user.settingsRole}">
                <div class="form-group">
                  <select name="player" onchange="loadInFrame(this, 'playlist.view?player=' + options[selectedIndex].value);" class="form-control">
                    <c:forEach items="${model.players}" var="player">
                        <option ${player.id eq model.player.id ? "selected" : ""} value="${player.id}">${player.shortDescription}</option>
                    </c:forEach>
                  </select>
                </div>
            </c:if>
            <c:if test="${model.player.web}">
                <div class="form-group"><div id="mediaplayer">
                    <p>Loading the player ...</p>
                </div></div>
            </c:if>

            <c:if test="${model.user.streamRole and not model.player.web}">
                <div class="form-group">
                  <div id="stop"><b><a href="#" onclick="return onStop()"><fmt:message key="playlist.stop"/></a></b></div>
                  <div id="start"><b><a href="#" onclick="return onStart()"><fmt:message key="playlist.start"/></a></b></div>
                </div>
            </c:if>

            <c:if test="${model.player.jukebox}">
                <div class="form-group">
                  <span class="fa fa-volume-up"></span>
                </div>
                <div class="form-group">
                  <div class="slider bgcolor2" id="slider-1" style="width:90px">
                      <input class="slider-input" id="slider-input-1" name="slider-input-1" />
                  </div>
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
            </c:if>

            <div class="btn-group form-group">
            <c:if test="${model.player.web}">
                <button type="button" class="btn btn-default btn-sm" onclick="return onPrevious()"><span class="fa fa-step-backward"></span></button>
                <button type="button" class="btn btn-default btn-sm" onclick="return onNext(false)"><span class="fa fa-step-forward"></span></button>
            </c:if>

            <button type="button" class="btn btn-default btn-sm" onclick="return onClear()" title="<fmt:message key="playlist.clear"/>"><span class="fa fa-ban"></span></button>
            <button type="button" class="btn btn-default btn-sm" onclick="return onShuffle()" title="<fmt:message key="playlist.shuffle"/>"><span class="fa fa-random"></span></button>

            <c:if test="${model.player.web or model.player.jukebox or model.player.external}">
                <button type="button" class="btn btn-default btn-sm" data-toggle="button" onclick="return onToggleRepeat()" title="<fmt:message key="playlist.repeat_on"/>" id="toggleRepeat"><span class="fa fa-repeat"></span></button>
            </c:if>

            <button type="button" class="btn btn-default btn-sm" onclick="return onUndo()" title="<fmt:message key="playlist.undo"/>"><span class="fa fa-undo"></span></button>

            <c:if test="${model.user.settingsRole}">
                <button onclick="jQuery('.main').load('playerSettings.view?id=${model.player.id}');" class="btn btn-default btn-sm" title="<fmt:message key="playlist.settings"/>"><span class="fa fa-cogs"></span></button>
            </c:if>
            </div>

            <div class="form-group">
              <select id="moreActions" onchange="actionSelected(this.options[selectedIndex].id)" class="form-control">
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
            </div>
  </div>

</div>

<div class="alert alert-warning" id="empty"><fmt:message key="playlist.empty"/></div>

<div class="table-responsive" id="playlistWrapper">
<table class="table table-striped table-hover table-condensed">
    <tbody id="playlistBody">
        <tr id="playlist-tmpl" style="display:none;">
            <td class="bgcolor2 buttons">
                <div class="btn-group">
                <button class="btn btn-default btn-sm" id="removeSong" onclick="onRemove(this.id.substring(10) - 1)" 
                     alt="<fmt:message key="playlist.remove"/>" title="<fmt:message key="playlist.remove"/>">
                    <span class="fa fa-minus-circle"></span>
                </button>
                <button class="btn btn-default btn-sm" id="up" onclick="onUp(this.id.substring(2) - 1)"
                     alt="<fmt:message key="playlist.up"/>" title="<fmt:message key="playlist.up"/>">
                    <span class="fa fa-arrow-circle-up"></span>
                </button>
                <button class="btn btn-default btn-sm" id="down" onclick="onDown(this.id.substring(4) - 1)"
                     alt="<fmt:message key="playlist.down"/>" title="<fmt:message key="playlist.down"/>">
                     <span class="fa fa-arrow-circle-down"></span>
                </div>
            </td>

            <td class="selector" class="bgcolor2 form-group"><input type="checkbox" class="checkbox" id="songIndex" class="form-control"></td>

            <c:if test="${model.visibility.trackNumberVisible}">
                <td class="trackNumber text-right"><span class="detail" id="trackNumber">1</span></td>
            </c:if>

            <td class="title">
                <img id="currentImage" src="<spring:theme code="currentImage"/>" alt="" style="display:none">
                <c:choose>
                    <c:when test="${model.player.externalWithPlaylist}">
                        <span id="title" class="detail">Title</span>
                    </c:when>
                    <c:otherwise>
                        <a id="titleUrl" href="#" class="detail">Title</a>
                    </c:otherwise>
                </c:choose>
            </td>

            <c:if test="${model.visibility.albumVisible}"><td class="album"><a id="albumUrl"><span id="album" class="detail">Album</span></a></td></c:if>
            <c:if test="${model.visibility.artistVisible}"><td class="artist"><a id="artistUrl"><span id="artist" class="detail">Artist</span></a></td></c:if>
			<c:if test="${model.visibility.composerVisible}"><td class="composer"><span id="composer" class="detail">Composer</span></td></c:if>
            <c:if test="${model.visibility.genreVisible}"><td class="genre"><span id="genre" class="detail">Genre</span></td></c:if>
            <c:if test="${model.visibility.yearVisible}"><td class="year"><span id="year" class="detail">Year</span></td></c:if>
            <c:if test="${model.visibility.formatVisible}"><td class="form"><span id="format" class="detail">Format</span></td></c:if>
            <c:if test="${model.visibility.fileSizeVisible}"><td class="fileSize text-right"><span id="fileSize" class="detail">Size</span></td></c:if>
            <c:if test="${model.visibility.durationVisible}"><td class="duration text-right"><span id="duration" class="detail">Duration</span></td></c:if>
            <c:if test="${model.visibility.bitRateVisible}"><td  class="bitRate"><span id="bitRate" class="detail">Bit Rate</span></td></c:if>
        </tr>
    </tbody>
</table>
</div>
</div>


<script type="text/javascript" language="javascript">

    function init() {
        dwr.engine.setErrorHandler(dwrErrorHandler);
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
            if (currentArtistUri != nowPlayingInfo.artistUri && currentAlbumUri != nowPlayingInfo.albumUri && top.main.updateNowPlaying) {
                jQuery(".main").load("nowPlaying.view?");
                currentArtistUri = nowPlayingInfo.artistUri;
        				currentAlbumUri = nowPlayingInfo.albumUri;
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
        jwplayer('mediaplayer').onError(
            function(event) {
                window.console.log(event);
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
    function onPlayRelatedArtistsSampler(artistUri, artistCount) {
        if (verifyIfPartyMode()) {
	    	startPlayer = true;
			playlistService.playRelatedArtistsSampler(artistUri, artistCount, playlistCallback);
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
            if (jQuery("#songIndex" + index).has(":checked")) {
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
      (function($) {
        songs = playlist.entries;
        repeatEnabled = playlist.repeatEnabled;
        if ($("#start")) {
            if (playlist.stopEnabled) {
                $("#start").hide();
                $("#stop").show();
            } else {
                $("#start").show();
                $("#stop").hide();
            }
        }


        if ($("#toggleRepeat")) {
            var text = repeatEnabled ? "<fmt:message key="playlist.repeat_on"/>" : "<fmt:message key="playlist.repeat_off"/>";
            if(repeatEnabled) {
              $("#toggleRepeat").button("toggle");
            }
            $("#toggleRepeat").attr("title", text);
        }

        if (songs.length == 0) {
            $("#empty").show();
            $("#playlistWrapper").hide();
        } else {
            $("#empty").hide();
            $("#playlistWrapper").show();
        }


        // Delete all the rows except for the "playlist-tmpl" row
        dwr.util.removeAllRows("playlistBody", { filter:function(tr) {
            return (tr.id != "playlist-tmpl");
        }});

        // Create a new set cloned from the pattern row

        var newRows = [];
        for (var i = 0; i < songs.length; i++) {
            var song  = songs[i];
            var id = i + 1;

            dwr.util.cloneNode("playlist-tmpl", { idSuffix:id });
            if ($("#trackNumber" + id)) {
                $("#trackNumber" + id).html(song.trackNumber);
            }

            if ($("#currentImage" + id) && song.streamUrl == currentStreamUrl) {
                $("#currentImage" + id).show();
            }
            if ($("#title" + id)) {
                $("#title" + id).html(truncate(song.title));
                $("#title" + id).title = song.title;
            }
            if ($("#titleUrl" + id)) {
                $("#titleUrl" + id).html(truncate(song.title));
                $("#titleUrl" + id).title = song.title;
                $("#titleUrl" + id).onclick = function () {onSkip(this.id.substring(8) - 1)};
            }
            if ($("#album" + id)) {
                $("#album" + id).html(truncate(song.album));
                $("#album" + id).title = song.album;
                $("#albumUrl" + id).href = "artist.view?id=" + song.artistUri + "&albumId=" + song.albumUri;
            }
            if ($("#artist" + id)) {
                $("#artist" + id).html(truncate(song.artist));
                $("#artist" + id).title = song.artist;
                $("#artistUrl" + id).href = "artist.view?id=" + song.artistUri;
            }
            if ($("#composer" + id)) {
                $("#composer" + id).html(truncate(song.composer));
                $("#composer" + id).title = song.composer;
            }
            if ($("#genre" + id)) {
                $("#genre" + id).html(song.genre);
            }
            if ($("#year" + id)) {
                $("#year" + id).html(song.year);
            }
            if ($("#bitRate" + id)) {
                $("#bitRate" + id).html(song.bitRate);
            }
            if ($("#duration" + id)) {
                $("#duration" + id).html(song.durationAsString);
            }
            if ($("#format" + id)) {
                $("#format" + id).html(song.format);
            }
            if ($("#fileSize" + id)) {
                $("#fileSize" + id).html(song.fileSize);
            }
 
            var row = $("#playlist-tmpl" + id);
            row.className = (i % 2 == 0) ? "bgcolor1" : "bgcolor2";
            //row.show();
            newRows.push(row.get(0)); 
        }
        $(newRows).show();

        if (playlist.sendM3U) {
            $(".main").load("play.m3u?");
        }

        if (slider) {
            slider.setValue(playlist.gain * 100);
        }

    <c:if test="${model.player.web}">
        triggerPlayer();
    </c:if>
}(jQuery));
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
        if(!songs) {
          return -1;
        }
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
          jQuery(".main").load("loadPlaylist.view?");
        } else if (id == "savePlaylist") {
          jQuery(".main").load("savePlaylist.view?");
        } else if (id == "downloadPlaylist") {
          location.href = "download.view?player=${model.player.id}";
        } else if (id == "sharePlaylist") {
          jQuery(".main").load("createShare.view?player=${model.player.id}&" + getSelectedIndexes());
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
            jQuery(".main").load("appendPlaylist.view?player=${model.player.id}&" + getSelectedIndexes());
        }
        $("moreActions").selectedIndex = 0;
    }

    function getSelectedIndexes() {
        var result = "";
        if(!songs) {
          return result;
        }
        
        for (var i = 0; i < songs.length; i++) {
            if ($("songIndex" + (i + 1)).checked) {
                result += "i=" + i + "&";
            }
        }
        return result;
    }

    function selectAll(b) {
        if(!songs) {
          return;
        }
        for (var i = 0; i < songs.length; i++) {
            $("songIndex" + (i + 1)).checked = b;
        }
    }

    init();
</script>
