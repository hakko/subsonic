<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC>

<html><head>
    <%@ include file="head.jspf" %>
    <link rel="alternate" type="application/rss+xml" title="Subsonic Podcast" href="podcast.view?suffix=.rss">
</head>

<body class="bgcolor1">
  <div class="wrapper">
  <header class="upper" data-src="top.view?" data-target="upper">
  </header>
  <section class="lower">
    <div class="left" data-src="left.view?" data-target="left">
    </div>
    <div class="middle">
      <div>
        <div class="main" data-src="nowPlaying.view?" data-target="main">
        </div>
        <div class="right" data-src="right.view?" data-target="right">
        </div>
      </div>
      <div class="playlist" data-src="playlist.view?" data-target="playlist">
      </div>
    </div>
  </section>
  </div>


  <script type="text/javascript" src="<c:url value="/script/jquery-1.7.2.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>




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
  <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>


  <script type="text/javascript" src="<c:url value="/script/jwplayer.js"/>"></script>    
  <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/webfx/range.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/webfx/timer.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/webfx/slider.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoom.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoomHTML.js"/>"></script>






  <script text="text/javascript">
    (function($) {
    jQuery("[data-src]").each(function() {
      loadFrame(this);
    });
    jQuery("a").live("click.target", function() {
      var el = $(this);
      if(el.attr("href").indexOf("#") === 0) {
        return true;
      }
      loadInFrame(el, el.attr("href"));
      return false;
    })
    })(jQuery);
  </script>
</body>
</html>
