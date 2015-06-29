<h2 class="div"><a name="deletePodcastChannel"></a>deletePodcastChannel</h2>
<p>
    <code>http://your-server/rest/deletePodcastChannel.view</code>
    <br>Since <a href="#versions">1.9.0</a>
</p>
<p>
    Deletes a Podcast channel.
    Note: The user must be authorized for Podcast administration (see Settings &gt; Users &gt; User is allowed to administrate Podcasts).
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
        <td>The ID of the Podcast channel to delete.</td>
    </tr>
</table>
<p>
    Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
</p>
