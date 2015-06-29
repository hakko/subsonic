<h2 class="div"><a name="getSongsByGenre"></a>getSongsByGenre</h2>

<p>
    <code>http://your-server/rest/getSongsByGenre.view</code>
    <br>Since <a href="#versions">1.9.0</a>
</p>

<p>
    Returns songs in a given genre.
</p>
<table width="100%" class="bottomspace">
    <tr>
        <th class="param-heading">Parameter</th>
        <th class="param-heading">Required</th>
        <th class="param-heading">Default</th>
        <th class="param-heading">Comment</th>
    </tr>
    <tr class="table-altrow">
        <td><code>genre</code></td>
        <td>Yes</td>
        <td></td>
        <td>The genre, as returned by <code>getGenres</code>.</td>
    </tr>
    <tr>
        <td><code>count</code></td>
        <td>No</td>
        <td>10</td>
        <td>The maximum number of songs to return. Max 500.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>offset</code></td>
        <td>No</td>
        <td>0</td>
        <td>The offset. Useful if you want to page through the songs in a genre.</td>
    </tr>
</table>
<p>
    Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;songsByGenre&gt;</code>
    element on success. <a href="inc/api/examples/songsByGenre_example_1.xml">Example</a>.
</p>
