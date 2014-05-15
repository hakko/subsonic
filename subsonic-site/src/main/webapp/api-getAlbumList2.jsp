<h2 class="div"><a name="getAlbumList2"></a>getAlbumList2</h2>

<p>
    <code>http://your-server/rest/getAlbumList2.view</code>
    <br>Since <a href="#versions">1.8.0</a>
</p>

<p>
    Similar to <code>getAlbumList</code>, but organizes music according to ID3 tags.
</p>
<table width="100%" class="bottomspace">
    <tr>
        <th class="param-heading">Parameter</th>
        <th class="param-heading">Required</th>
        <th class="param-heading">Default</th>
        <th class="param-heading">Comment</th>
    </tr>
    <tr class="table-altrow">
        <td><code>type</code></td>
        <td>Yes</td>
        <td></td>
        <td>The list type. Must be one of the following: <code>random</code>, <code>newest</code>,
            <code>frequent</code>, <code>recent</code>, <code>starred</code>,
            <code>alphabeticalByName</code> or <code>alphabeticalByArtist</code>.
            Since <a href="#versions">1.10.1</a> you can use <code>byYear</code> and <code>byGenre</code> to list albums in
            a given year range or genre.
        </td>
    </tr>
    <tr>
        <td><code>size</code></td>
        <td>No</td>
        <td>10</td>
        <td>The number of albums to return. Max 500.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>offset</code></td>
        <td>No</td>
        <td>0</td>
        <td>The list offset. Useful if you for example want to page through the list of newest albums.</td>
    </tr>
    <tr>
        <td><code>fromYear</code></td>
        <td>Yes (if <code>type</code> is <code>byYear</code>)</td>
        <td></td>
        <td>The first year in the range.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>toYear</code></td>
        <td>Yes (if <code>type</code> is <code>byYear</code>)</td>
        <td></td>
        <td>The last year in the range.</td>
    </tr>
    <tr>
        <td><code>genre</code></td>
        <td>Yes (if <code>type</code> is <code>byGenre</code>)</td>
        <td></td>
        <td>The name of the genre, e.g., "Rock".</td>
    </tr>
</table>
<p>
    Returns a <code>&lt;subsonic-response&gt;</code> element with a nested <code>&lt;albumList2&gt;</code>
    element on success. <a href="inc/api/examples/albumList2_example_1.xml">Example</a>.
</p>
