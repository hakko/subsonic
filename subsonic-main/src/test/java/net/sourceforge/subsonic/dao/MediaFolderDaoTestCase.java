package net.sourceforge.subsonic.dao;

import static java.io.File.separator;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

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

    public void testFindsIndexedParentFolder() {
    	final String SEP = separator;
    	String ROOT = System.getProperty("java.io.tmpdir");
    	if (!ROOT.endsWith(SEP)) ROOT = ROOT + SEP;
    	mediaFolderDao.createMediaFolder(new MediaFolder(new File(ROOT + "b"), "b", true, new Date()));
    	mediaFolderDao.createMediaFolder(new MediaFolder(new File(ROOT + "c"), "c", false, new Date()));
    	
    	assertTrue(mediaFolderDao.hasIndexedParentFolder(ROOT + "b" + SEP + "x"));
    	assertFalse(mediaFolderDao.hasIndexedParentFolder(ROOT + "b"));
    	assertFalse(mediaFolderDao.hasIndexedParentFolder(ROOT + "bbb"));
    	assertFalse(mediaFolderDao.hasIndexedParentFolder(ROOT + "c" + SEP + "y"));
    	assertFalse(mediaFolderDao.hasIndexedParentFolder(ROOT));
    }

    public void testFindsIndexedRootFolder() {
    	for (File file : File.listRoots()) {
        	mediaFolderDao.createMediaFolder(new MediaFolder(file, file.getName(), true, new Date()));
    	}
    	
    	assertTrue(mediaFolderDao.hasIndexedParentFolder(System.getProperty("user.home")));
    }
    
    public void testSetsChildrenFoldersToNonIndexed() {
    	final String SEP = separator;
    	String ROOT = System.getProperty("java.io.tmpdir");
    	if (!ROOT.endsWith(SEP)) ROOT = ROOT + SEP;
    	mediaFolderDao.createMediaFolder(new MediaFolder(new File(ROOT + "a" + SEP + "b"), "a", true, new Date()));
    	mediaFolderDao.createMediaFolder(new MediaFolder(new File(ROOT + "a" + SEP + "c"), "c", true, new Date()));
    	mediaFolderDao.createMediaFolder(new MediaFolder(new File(ROOT + "a" + SEP + "d"), "d", true, new Date()));
    	mediaFolderDao.createMediaFolder(new MediaFolder(new File(ROOT + "e"), "e", true, new Date()));
    	
    	assertEquals(4, mediaFolderDao.getIndexedMediaFolders().size());
    	assertEquals(0, mediaFolderDao.getNonIndexedMediaFolders().size());
    	
    	mediaFolderDao.setChildFoldersToNonIndexed(new HashSet<>(Arrays.asList(ROOT + "a")));

    	assertEquals(1, mediaFolderDao.getIndexedMediaFolders().size());
    	assertEquals(3, mediaFolderDao.getNonIndexedMediaFolders().size());
    	assertEquals("e", mediaFolderDao.getIndexedMediaFolders().get(0).getName());
    }

}