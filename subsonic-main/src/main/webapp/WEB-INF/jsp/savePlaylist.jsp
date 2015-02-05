<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

    <%@ include file="include.jspf" %>
<div class="mainframe bgcolor1">

<h1><fmt:message key="playlist.save.title"/></h1>
<form:form commandName="command" method="post" action="savePlaylist.view" class="form" role="form" onsubmit="return submitForm(this, 'Playlist saved.');">
  <div class="statusMessage"></div>
  <div class="form-group">
    <label for="name"><fmt:message key="playlist.save.name"/></label>
    <form:input path="name" size="30" class="form-control" />
  </div>
  <div class="form-group">
    <label for="suffix"><fmt:message key="playlist.save.format"/></label>
    <form:select path="suffix" class="form-control">
      <c:forEach items="${command.formats}" var="format" varStatus="loopStatus">
        <form:option value="${format}" label="${format}"/>
      </c:forEach>
    </form:select>
  </div>
  <button type="submit" class="btn btn-default"><fmt:message key="playlist.save.save"/></button>
</form:form>
</div>
