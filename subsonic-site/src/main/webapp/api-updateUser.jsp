<h2 class="div"><a name="updateUser"></a>updateUser</h2>

<p>
    <code>http://your-server/rest/updateUser.view</code>
    <br>Since <a href="#versions">1.10.1</a>
</p>

<p>
    Modifies an existing Subsonic user, using the following parameters:
</p>
<table width="100%" class="bottomspace">
    <tr>
        <th class="param-heading">Parameter</th>
        <th class="param-heading">Required</th>
        <th class="param-heading">Default</th>
        <th class="param-heading">Comment</th>
    </tr>
    <tr class="table-altrow">
        <td><code>username</code></td>
        <td>Yes</td>
        <td></td>
        <td>The name of the user.</td>
    </tr>
    <tr>
        <td><code>password</code></td>
        <td>No</td>
        <td></td>
        <td>The password of the user, either in clear text of hex-encoded (see above).</td>
    </tr>
    <tr class="table-altrow">
        <td><code>email</code></td>
        <td>No</td>
        <td></td>
        <td>The email address of the user.</td>
    </tr>
    <tr>
        <td><code>ldapAuthenticated</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is authenicated in LDAP.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>adminRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is administrator.</td>
    </tr>
    <tr>
        <td><code>settingsRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to change settings and password.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>streamRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to play files.</td>
    </tr>
    <tr>
        <td><code>jukeboxRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to play files in jukebox mode.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>downloadRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to download files.</td>
    </tr>
    <tr>
        <td><code>uploadRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to upload files.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>coverArtRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to change cover art and tags.</td>
    </tr>
    <tr>
        <td><code>commentRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to create and edit comments and ratings.</td>
    </tr>
    <tr class="table-altrow">
        <td><code>podcastRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to administrate Podcasts.</td>
    </tr>
    <tr>
        <td><code>shareRole</code></td>
        <td>No</td>
        <td></td>
        <td>Whether the user is allowed to share files with anyone.</td>
    </tr>
</table>

<p>
    Returns an empty <code>&lt;subsonic-response&gt;</code> element on success.
</p>
