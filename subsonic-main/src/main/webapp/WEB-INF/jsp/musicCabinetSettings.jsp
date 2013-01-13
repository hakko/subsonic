<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jspf" %>
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
            <td>Artist radio, max songs per artist</td>
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
            <td>Genre radio, max songs per artist</td>
            <td>
             <form:input path="genreRadioArtistCount" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="genreradioartistcount"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Radio, minimum song length (seconds)</td>
            <td>
             <form:input path="radioMinimumSongLength" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="radiominimumsonglength"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Radio, maximum song length (seconds)</td>
            <td>
             <form:input path="radioMaximumSongLength" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="radiomaximumsonglength"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Related artists sampler, songs per artist</td>
            <td>
             <form:input path="relatedArtistsSamplerArtistCount" size="5"/>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="relatedartistssamplerartistcount"/></c:import>
            </td>
           </tr>
           <tr>
            <td>Prefer last.fm artwork</td>
            <td>
               <form:select path="preferLastFmArtwork">
                <form:option value="true" label="Yes"/>
                <form:option value="false" label="No"/>
               </form:select>
             <c:import url="helpToolTip.jsp"><c:param name="topic" value="preferlastfmartwork"/></c:import>
            </td>
           </tr>
          </table>
          <input type="submit" value="Save" style="margin-right:0.3em"/>
          <br/><br/><br/>
          <p><a href="tagSettings.view">Tag settings</a> | <a href="groupSettings.view">Group settings</a></p>

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
		   <p>MusicCabinet is now ready to scan your library, and fetch meta-data from <a href="http://last.fm">last.fm</a>. Depending on the size of your library, this could take a while, as they have some regulations on data traffic. A rough estimate is 30 minutes per 10.000 tracks.</p>
		   <p>You can use the simplified file-based interface to access your music during the import.</p>
           <c:if test="${fn:length(command.mediaFolderNames) > 0}">
			<p>These folders will be scanned:</p>
			<ul>
			<c:forEach items="${command.mediaFolderNames}" var="mediaFolderName" varStatus="loopStatus">
				<li>${mediaFolderName}</li>
			</c:forEach>			
		   </ul>
		  </c:if>
          </div>
          <input type="hidden" name="updateSearchIndex" value="true"/>
          <input type="submit" value="Update search index now" style="margin-right:0.3em" id="button"/>
         </c:otherwise>
        </c:choose>
       </c:otherwise>
      </c:choose>
     </c:when>
     <c:otherwise>
      <p>It seems like you recently installed or upgraded MusicCabinet. Press 'Upgrade database' to load the latest version.</p>
      <input type="hidden" name="updateDatabase" value="true"/>
      <input type="submit" value="Upgrade database" style="margin-right:0.3em" id="button"/>
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