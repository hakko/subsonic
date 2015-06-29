<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">




<c:choose>
<c:when test="${empty model.mediaFiles}">
  <div class="alert alert-warning"><fmt:message key="search.hits.none"/></div>
</c:when>
<c:otherwise>
  <div class="btn-group">
  
    <a class="btn btn-default" href="#" onclick="return onPlay(${sub:esc(model.trackUris)}, 'P');">Play all</a>
    <a class="btn btn-default" href="#" onclick="return onPlay(${sub:esc(model.trackUris)}, 'E');">Enqueue all</a>
    <a class="btn btn-default" href="#" onclick="return onPlay(${sub:esc(model.trackUris)}, 'A');">Add all</a>
  </div>
  <%@ include file="songs.jspf" %>
  
  <ul class="pager">
  <c:if test="${model.page > 0}"><div class="previous back"><a href="#" onclick="return window.search(this, ${model.page - 1});"><fmt:message key="common.previous"/></a></div></c:if>
  <c:if test="${not empty model.morePages}"><div class="next forward"><a href="#" onclick="return window.search(this, ${model.page + 1});"><fmt:message key="common.next"/></a></div></c:if>
  </ul>
  
</c:otherwise>
</c:choose>



</div>
