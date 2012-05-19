<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="musicCabinet"/>
</c:import>

<p style="padding-top:1em"><b>MusicCabinet configuration</b></p>

<form:form method="post" action="musicCabinetSettings.view" commandName="command" onsubmit="document.getElementById('button').disabled = 1;">

<c:choose>
 <c:when test="${command.databaseRunning}">
  <c:choose>
   <c:when test="${command.passwordCorrect}">
    <c:choose>
     <c:when test="${command.databaseUpdated}">
      <c:choose>
       <c:when test="${command.searchIndexBeingCreated}">
        <div style="width:60%">
         <p>The search index is being created. Depending on the size of your music library, this could take a while.</p>
         <p>Progress:</p>
         <ul>
         <c:forEach items="${command.updateProgress}" var="updateProgress">
          <li>${updateProgress}</li>
         </c:forEach>
         </ul>
         <input type="submit" value="Refresh" style="margin-right:0.3em"/>
        </div>
       </c:when>
       <c:otherwise>
        <c:choose>
         <c:when test="${command.searchIndexCreated}">
          <p>MusicCabinet is up-to-date and running normally.</p>
          <table style="white-space:nowrap" class="indent">
           <tr>
            <td>Last.fm username</td>
            <td>
             <form:input path="lastFMUsername" size="25"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="lastfmusername"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Artist radio, number of songs</td>
            <td>
             <form:input path="artistRadioTotalCount" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="artistradiototalcount"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Artist radio, max songs for each artist</td>
            <td>
             <form:input path="artistRadioArtistCount" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="artistradioartistcount"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Artist top tracks, number of songs</td>
            <td>
             <form:input path="artistTopTracksTotalCount" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="artisttoptrackstotalcount"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Genre radio, number of songs</td>
            <td>
             <form:input path="genreRadioTotalCount" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="genreradiototalcount"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Genre radio, max songs for each artist</td>
            <td>
             <form:input path="genreRadioArtistCount" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="genreradioartistcount"/></c:import>
            </td>
           </tr>
          </table>
          <input type="submit" value="Save" style="margin-right:0.3em"/>
          <br/><br/><br/>
          <p>Tags are configured <a href="tagSettings.view">here</a>.</p>

		<script type="text/javascript" language="javascript">
		['artistRadioTotalCount', 'artistRadioArtistCount', 'artistTopTracksTotalCount',
		 'genreRadioTotalCount', 'genreRadioArtistCount'].each(function(item) {
			Event.observe(item, 'keyup', function(e) {
				this.value = this.value.replace(/[^0-9]/g,'');
				return true;
			})
		});
		</script>

         </c:when>
         <c:otherwise>
          <div style="width:60%">
           <p>MusicCabinet is a tool that:
           <ul>
            <li>generates smart playlists, based on an artist or a musical genre
            <li>helps you find new music, by displaying related artists to what's in your library
            <li>allows you to browse your library by musical genre
            <li>adds artist biographies and artwork to Subsonic
           </ul>
           <p>This is made possible by fetching all that information from <a href="http://last.fm">last.fm</a>. However, depending on the size of your library, this could take a while, as they have some regulations on data traffic. Press the button below, and be patient. Normal usage of Subsonic will work meanwhile, you'll just have to wait for the MusicCabinet extended features.</p>
          </div>
          <input type="hidden" name="updateSearchIndex" value="true"/>
          <input type="submit" value="Start importing data" style="margin-right:0.3em" id="button"/>
         </c:otherwise>
        </c:choose>
       </c:otherwise>
      </c:choose>
     </c:when>
     <c:otherwise>
      <p>It seems like you recently installed or upgraded MusicCabinet. Press 'Update database' to continue.</p>
      <input type="hidden" name="updateDatabase" value="true"/>
      <input type="submit" value="Update database" style="margin-right:0.3em" id="button"/>
     </c:otherwise>
    </c:choose>
   </c:when>
   <c:otherwise>
    <p>You need to supply your password for user 'postgres', created during database setup.</p>
    <form:password path="musicCabinetJDBCPassword" size="70"/>
    <input type="submit" value="OK" style="margin-right:0.3em"/>
    <c:if test="${command.passwordAttemptWrong}">
     <p><b>Wrong password!</b></p>
    </c:if>
   </c:otherwise>
  </c:choose>
 </c:when>
 <c:otherwise>
  <p>MusicCabinet could not connect to a PostgreSQL database. Make sure it is installed, and up and running as a service! It is expected to be found at localhost:5432.</p>
  <input type="submit" value="Try again" style="margin-right:0.3em"/>
 </c:otherwise>
</c:choose>

</form:form>

</body></html>