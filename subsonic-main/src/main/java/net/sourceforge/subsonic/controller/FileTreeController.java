package net.sourceforge.subsonic.controller;

import static com.github.hakko.musiccabinet.service.library.LibraryUtil.set;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.Album;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.domain.model.aggr.DirectoryContent;
import com.github.hakko.musiccabinet.domain.model.library.Directory;
import com.github.hakko.musiccabinet.service.DirectoryBrowserService;
import com.github.hakko.musiccabinet.service.LibraryBrowserService;
import com.github.hakko.musiccabinet.service.StarService;

/**
 * Controller for the file tree page.
 *
 * @author hakko / MusicCabinet
 */
public class FileTreeController extends ParameterizableViewController {

	private SettingsService settingsService;
    private SecurityService securityService;
    private SearchService searchService;
    private MediaFileService mediaFileService;
    private StarService starService;
    private LibraryBrowserService libraryBrowserService;
    private DirectoryBrowserService directoryBrowserService;

    private static final Logger LOG = Logger.getLogger(FileTreeController.class);
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!libraryBrowserService.hasArtists()) {
            return new ModelAndView("musicCabinetUnavailable");
        }
        
        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

        Map<String, Object> map = new HashMap<String, Object>();

        int id = NumberUtils.toInt(request.getParameter("id"), -1);
        if ("add".equals(request.getParameter("action"))) {
        	addDirectoryDiff(map, id);
        } else if ("scan".equals(request.getParameter("action"))) {
        	boolean offlineScan = "offline".equals(request.getParameter("type"));
        	boolean subDir = "subdir".equals(request.getParameter("from"));
        	scan(id, offlineScan, subDir);
        	addDirectories(map, -1, userSettings);
        } else {
        	addDirectories(map, id, userSettings);
        }

        map.put("user", user);
        	
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private void scan(int directoryId, boolean offlineScan, boolean addSubDir) {
    	Set<String> paths;
    	if (addSubDir) {
			paths = directoryBrowserService.getDirectoryDiff(directoryId).getSubDirectories();
			for (String path : paths) {
				LOG.debug("Add " + path + " as child of " + directoryId);
				directoryBrowserService.addDirectory(path, directoryId);
			}
    	} else {
    		paths = set(directoryBrowserService.getDirectory(directoryId).getPath());
    	}
    	LOG.debug("About to scan " + paths);
    	searchService.createIndex(paths, false, offlineScan, true, true);
    }
    
    private void addDirectoryDiff(Map<String, Object> map, int id) {
    	DirectoryContent content = directoryBrowserService.getDirectoryDiff(id);
    	map.put("parentId", directoryBrowserService.getParentId(id));
    	map.put("directoryId", id);
    	map.put("directory", content.getDirectory());
    	map.put("addedDirectories", content.getSubDirectories());
    	map.put("addedFiles", content.getFiles());
    }
    
    private void addDirectories(Map<String, Object> map, int id, UserSettings userSettings) {
        Set<Directory> directories = id == -1 ?
            	directoryBrowserService.getRootDirectories() :
            	directoryBrowserService.getSubDirectories(id);

        if (id != -1) {
        	map.put("parentId", directoryBrowserService.getParentId(id));
        	
        	List<Album> albums = mediaFileService.getAlbums(directoryBrowserService.getAlbums(id, 
        			userSettings.isAlbumOrderByYear(), userSettings.isAlbumOrderAscending()));
        	if (albums.size() > 0) {
        		map.put("albums", albums);
        		map.put("isAlbumStarred", starService.getStarredAlbumsMask(
        				userSettings.getLastFmUsername(), getAlbumUris(albums)));
        		map.put("visibility", userSettings.getMainVisibility());
        	}

        	Directory dir = directoryBrowserService.getDirectory(id);
        	List<String> filenames = directoryBrowserService.getNonAudioFiles(id);
        	map.put("videoFiles", getMediaFiles(dir, filenames, settingsService.getVideoFileTypesAsArray()));
        	map.put("imageFiles", getMediaFiles(dir, filenames, settingsService.getImageFileTypesAsArray()));
        }
        map.put("directories", directories);
    }
    
    private List<MediaFile> getMediaFiles(Directory dir, List<String> filenames, String[] extensions) {
    	List<MediaFile> mediaFiles = new ArrayList<>();
    	
    	for (String filename : filenames) {
    		if (FilenameUtils.isExtension(filename, extensions)) {
    			mediaFiles.add(mediaFileService.getNonIndexedMediaFile(
    					dir.getPath() + File.separatorChar + filename));
    		}
    	}
    	
    	return mediaFiles;
    }
    
    private List<Uri> getAlbumUris(List<Album> albums) {
    	List<Uri> albumIds = new ArrayList<>();
    	for (Album album : albums) {
    		albumIds.add(album.getUri());
    	}
    	return albumIds;
    }

    // Spring setters

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setMediaFileService(MediaFileService mediaFileService) {
		this.mediaFileService = mediaFileService;
	}

	public void setStarService(StarService starService) {
		this.starService = starService;
	}

	public void setLibraryBrowserService(LibraryBrowserService libraryBrowserService) {
		this.libraryBrowserService = libraryBrowserService;
	}

	public void setDirectoryBrowserService(DirectoryBrowserService directoryBrowserService) {
		this.directoryBrowserService = directoryBrowserService;
	}

}