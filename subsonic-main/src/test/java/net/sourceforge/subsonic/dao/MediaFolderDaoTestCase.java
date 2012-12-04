package net.sourceforge.subsonic.dao;

import java.io.File;
import java.util.Date;

import net.sourceforge.subsonic.domain.MediaFolder;

/**
 * Unit test of {@link MediaFolderDao}.
 *
 * @author Sindre Mehus
 */
public class MediaFolderDaoTestCase extends DaoTestCaseBase {

    @Override
    protected void setUp() throws Exception {
        getJdbcTemplate().execute("delete from music_folder");
    }

    public void testCreateMediaFolder() {
        MediaFolder mediaFolder = new MediaFolder(new File("path"), "name", true, new Date());
        mediaFolderDao.createMediaFolder(mediaFolder);

        MediaFolder newMediaFolder = mediaFolderDao.getAllMediaFolders().get(0);
        assertMediaFolderEquals(mediaFolder, newMediaFolder);
    }

    public void testUpdateMediaFolder() {
        MediaFolder mediaFolder = new MediaFolder(new File("path"), "name", true, new Date());
        mediaFolderDao.createMediaFolder(mediaFolder);
        mediaFolder = mediaFolderDao.getAllMediaFolders().get(0);

        mediaFolder.setPath(new File("newPath"));
        mediaFolder.setName("newName");
        mediaFolder.setIndexed(false);
        mediaFolder.setChanged(new Date(234234L));
        mediaFolderDao.updateMediaFolder(mediaFolder);

        MediaFolder newMediaFolder = mediaFolderDao.getAllMediaFolders().get(0);
        assertMediaFolderEquals(mediaFolder, newMediaFolder);
    }

    public void testDeleteMediaFolder() {
        assertEquals("Wrong number of music folders.", 0, mediaFolderDao.getAllMediaFolders().size());

        mediaFolderDao.createMediaFolder(new MediaFolder(new File("path"), "name", true, new Date()));
        assertEquals("Wrong number of music folders.", 1, mediaFolderDao.getAllMediaFolders().size());

        mediaFolderDao.createMediaFolder(new MediaFolder(new File("path"), "name", true, new Date()));
        assertEquals("Wrong number of music folders.", 2, mediaFolderDao.getAllMediaFolders().size());

        mediaFolderDao.deleteMediaFolder(mediaFolderDao.getAllMediaFolders().get(0).getId());
        assertEquals("Wrong number of music folders.", 1, mediaFolderDao.getAllMediaFolders().size());

        mediaFolderDao.deleteMediaFolder(mediaFolderDao.getAllMediaFolders().get(0).getId());
        assertEquals("Wrong number of music folders.", 0, mediaFolderDao.getAllMediaFolders().size());
    }

    private void assertMediaFolderEquals(MediaFolder expected, MediaFolder actual) {
        assertEquals("Wrong name.", expected.getName(), actual.getName());
        assertEquals("Wrong path.", expected.getPath(), actual.getPath());
        assertEquals("Wrong indexed state.", expected.isIndexed(), actual.isIndexed());
        assertEquals("Wrong changed date.", expected.getChanged(), actual.getChanged());
    }


}