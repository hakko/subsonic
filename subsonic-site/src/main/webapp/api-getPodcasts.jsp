<h2 class="div"><a name="getPodcasts"></a>getPodcasts</h2>

<p>
    <code>http://your-server/rest/getPodcasts.view</code>
    <br>Since <a href="#versions">1.6.0</a>
</p>

<p>
    Returns all Podcast channels the server subscribes to, and (optionally) their episodes. This method can also be used to return
    details for only one channel - refer to the <code>id</code> parameter. A typical use case for this method would be to first retrieve
    all channels without episodes, and then retrieve all episodes for the single channel the user selects.
</p>
<table width="100%" class="bottomspace">
    <tr>
        <th class="param-heading">Parameter</th>
        <th class="param-heading">Required</th>
        <th class="param-heading">Default</th>
        <th class="param-heading">Comment</th>
    </tr>
    <tr class="table-altrow">
        <td><code>includeEpisodes</code></td>
        <td>No</td>
        <td>true</td>
        <td>(Since <a href="#versions">1.9.0</a>) Whether to include Podcast episodes in the returned result.</td>
    </tr>
    <tr>
        <td><code>id</code></td>
        <td>No</td>
        <td></td>
        <td>(Since <a href="#versions">1.9.0</a>) If specified, only return the Podcast channel with this ID.</td>
    </tr>
</table>
<p>
    Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;podcasts&gt;</code>
    element on success. <a href="inc/api/examples/podcasts_example_1.xml">Example</a>.
</p>

