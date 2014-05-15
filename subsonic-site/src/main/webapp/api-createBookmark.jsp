<h2 class="div"><a name="createBookmark"></a>createBookmark</h2>
<p>
    <code>http://your-server/rest/createBookmark.view</code>
    <br>Since <a href="#versions">1.9.0</a>
</p>
<p>
    Creates or updates a bookmark (a position within a media file). Bookmarks are personal and not visible to other users.

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
        <td>ID of the media file to bookmark. If a bookmark already exists for this file it will be overwritten.</td>
    </tr>
    <tr>
        <td><code>position</code></td>
        <td>Yes</td>
        <td></td>
        <td>The position (in milliseconds) within the media file.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>comment</code></td>
        <td>No</td>
        <td></td>
        <td>A user-defined comment.</td>
    </tr>
</table>
<p>
    Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
</p>
