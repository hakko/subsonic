<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

    <%@ include file="include.jspf" %>
<div>
<c:forEach items="${model.reloadFrames}" var="reloadFrame">
    <script language="javascript" type="text/javascript">
      if('${reloadFrame.frame}' === 'main') {
        app.loadMain("${reloadFrame.view}");
      }
      else if('${reloadFrame.frame}' === 'playlist') {
        app.reloadPlaylist("${reloadFrame.view}");
      }
    </script>
</c:forEach>

</div>
