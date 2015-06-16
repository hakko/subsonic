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
  
<spring:theme code="panel.primary" var="themePanelPrimary" scope="page" />
<div class="mainframe bgcolor1 panel panel-primary ${themePanelPrimary}">

<div class="panel-heading">
  <i class="fa fa-cog"></i>
  Now Playing
</div>
<div class="panel-body">

<c:choose>
<c:when test="${current != null}">
          <sub:url value="coverArt.view" var="coverArtUrl" hashurl="false">
              <sub:param name="pathUtfHex8" value="${coverArt}" />
          </sub:url>

          <span class="overlay">
          <img class="img-thumbnail" id="albart${i.count}" src="${coverArtUrl}" alt="" />
             <!--c:if test="${not model.player.web &amp;&amp; sub:isSpotify(current)}">
                   <img class="spotify-icon" src="icons/spotify.png" width="16" height="16" />
             </c:if-->
          </span>
          <h3>${current.metaData.title}</h3>
          <h5>${current.metaData.album}</h5>
          <h5>${current.metaData.artist}</h5>          
</c:when>
<c:otherwise>
Nothing playing.
</c:otherwise>
</c:choose>
</div>
</div>


</jsp:root>