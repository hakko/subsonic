<h2 class="div"><a name="deleteBookmark"></a>deleteBookmark</h2>
<p>
    <code>http://your-server/rest/deleteBookmark.view</code>
    <br>Since <a href="#versions">1.9.0</a>
</p>
<p>
    Deletes the bookmark for a given file.

</p>
<table width="100%" class="bottomspace">
    <tr>
        <th class="param-heading">Parameter</th>
        <th class="param-heading">Required</th>
        <th class="param-heading">Default</th>
        <th class="param-heading">Comment</th>
    </tr>
    <tr class="table-altrow">
        <td><code>id</code></td>
        <td>Yes</td>
        <td></td>
        <td>ID of the media file for which to delete the bookmark. Other users' bookmarks are not affected.</td>
    </tr>
</table>
<p>
    Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
</p>
