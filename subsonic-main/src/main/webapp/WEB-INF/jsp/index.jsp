<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC>
<%@ include file="include.jspf" %>
<html>
<head>
  <title>Subsonic</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <!--[if lt IE 7.]>
  <script defer type="text/javascript" src="<c:url value="/script/pngfix.js"/>"></script>
  <![endif]-->
  <link href="<c:url value="/script/bower_components/bootstrap/dist/css/bootstrap.min.css"/>" rel="stylesheet" media="screen">
  <link href="<c:url value="/script/bower_components/bootstrap-material-design/dist/css/material-fullpalette.min.css"/>" rel="stylesheet" media="screen">
  <link href="<c:url value="/script/bower_components/font-awesome/css/font-awesome.min.css"/>" rel="stylesheet" media="screen">
  
  
  <link rel="stylesheet" href="<c:url value="/style/structure.css"/>" type="text/css" />
  <link type="text/css" rel="stylesheet" href="<c:url value="/script/webfx/luna.css"/>" />
  <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet" type="text/css" />
  <link href="<c:url value="/style/artistgenres.css"/>" rel="stylesheet" type="text/css" />

  <c:set var="styleSheet"><spring:theme code="styleSheet"/></c:set>
  <link rel="stylesheet" href="<c:url value="/${styleSheet}"/>" type="text/css" />
  <c:set var="faviconImage"><spring:theme code="faviconImage"/></c:set>
  <link rel="shortcut icon" href="<c:url value="/${faviconImage}"/>" type="text/css" />
  <link rel="alternate" type="application/rss+xml" title="Subsonic Podcast" href="podcast.view?suffix=.rss">
</head>

<c:set var="showRight" value="${model.showRight}" />
<body class="bgcolor1 container">
  <div class="upper row" data-src="top.view?" data-target="upper">
  </div>
  <div class="lower row">
    <div class="left col-lg-2" data-src="left.view?" data-target="left">
    </div>
    <div class="middle col-lg-12">
      <div class="row">
        <div class="main col-lg-8" data-src="nowPlaying.view?" data-target="main">
        </div>
        <div class="playlist col-lg-4" data-src="playlist.view?" data-target="playlist">
        </div>
      </div>
    </div>
  </div>


  <script type="text/javascript" src="<c:url value="/script/bower_components/jquery/dist/jquery.min.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/bower_components/bootstrap/dist/js/bootstrap.min.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/bower_components/bootstrap-material-design/dist/js/material.min.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/bower_components/mustache/mustache.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/bower_components/director/build/director.min.js"/>"></script>
  <!--script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script-->




	<script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/smooth-scroll.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>    
  <script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/libraryStatusService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/coverArtService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/chatService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/multiService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/uiStarService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/uiTagService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/transferService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/tagService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/deviceLocatorService.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>


  <script type="text/javascript" src="<c:url value="/script/jwplayer.js"/>"></script>    
  <script type="text/javascript" src="<c:url value="/script/webfx/range.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/webfx/timer.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/webfx/slider.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoom.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoomHTML.js"/>"></script>





  <script text="text/javascript">
    $.material.init();

    var router = Router(app.routes);
    router.init("/");
  </script>
</body>
</html>
