<div id="logo"><a href="index.jsp"><img src="inc/img/subsonic.png" alt="Subsonic"/></a></div>

<div class="hide">
</div>

<div id="search">
    <table><tr>
        <form method="post" action="search.jsp" name="searchForm">
            <td><input type="text" name="query" id="query" size="18" value="Search" onclick="document.searchForm.query.select();"/></td>
            <td><a href="javascript:document.searchForm.submit()"><img src="inc/img/search.png" alt="Search" title="Search"/></a></td>
        </form>
    </tr></table>
</div>

<hr/>
<div id="nav">
    <ul>
        <li id="menu-home"><a href="index.jsp" id="a-home"><span>Home</span></a></li>
        <li><a href="download.jsp" id="a-download"><span>Download</span></a></li>
        <li><a href="apps.jsp" id="a-apps"><span>Apps</span></a></li>
        <li id="menu-premium"><a href="premium.jsp" id="a-premium"><span>Subsonic Premium</span></a></li>
        <li><a href="documentation.jsp" id="a-documentation"><span>Documentation</span></a></li>
        <li><a href="features.jsp" id="a-features"><span>Features</span></a></li>
        <li><a href="screenshots.jsp" id="a-screenshots"><span>Screenshots</span></a></li>
        <li><a href="demo.jsp" id="a-demo"><span>Demo</span></a></li>
        <li><a href="forum.jsp" id="a-forum"><span>Forum</span></a></li>
        <li><a href="api.jsp" id="a-api"><span>API</span></a></li>
    </ul>

    <script type="text/javascript">
        document.getElementById("a-${param.current}").className = "open"
    </script>
</div>
<hr/>

