<h2 class="div"><a name="createPodcastChannel"></a>createPodcastChannel</h2>
<p>
    <code>http://your-server/rest/createPodcastChannel.view</code>
    <br>Since <a href="#versions">1.9.0</a>
</p>
<p>
    Adds a new Podcast channel.
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
        <td><code>url</code></td>
        <td>Yes</td>
        <td></td>
        <td>The URL of the Podcast to add.</td>
    </tr>
</table>
<p>
    Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
</p>
