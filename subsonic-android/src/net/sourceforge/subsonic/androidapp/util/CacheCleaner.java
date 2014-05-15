package net.sourceforge.subsonic.androidapp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.StatFs;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;

/**
 * @author Sindre Mehus
 * @version $Id: CacheCleaner.java 3978 2014-04-06 13:06:21Z sindre_mehus $
 */
public class CacheCleaner {

    private static final Logger LOG = new Logger(CacheCleaner.class);
    private static final double MAX_FILE_SYSTEM_USAGE = 0.95;

    private final Context context;
    private final DownloadService downloadService;

    public CacheCleaner(Context context, DownloadService downloadService) {
        this.context = context;
        this.downloadService = downloadService;
    }

    public void clean() {

        LOG.info("Starting cache cleaning.");

        if (downloadService == null) {
            LOG.error("DownloadService not set. Aborting cache cleaning.");
            return;
        }

        try {

            List<File> files = new ArrayList<File>();
            List<File> dirs = new ArrayList<File>();

            findCandidatesForDeletion(FileUtil.getMusicDirectory(context), files, dirs);
            sortByAscendingModificationTime(files);

            Set<File> undeletable = findUndeletableFiles();

            deleteFiles(files, undeletable);
            deleteEmptyDirs(dirs, undeletable);
            LOG.info("Completed cache cleaning.");

        } catch (RuntimeException x) {
            LOG.error("Error in cache cleaning.", x);
        }
    }

    private void deleteEmptyDirs(List<File> dirs, Set<File> undeletable) {
        for (File dir : dirs) {
            if (undeletable.contains(dir)) {
                continue;
            }

            File[] children = dir.listFiles();

            // Delete empty directory and associated album artwork.
            if (children.length == 0) {
                Util.delete(dir);
                Util.delete(FileUtil.getAlbumArtFile(context, dir));
            }
        }
    }

    private void deleteFiles(List<File> files, Set<File> undeletable) {

        if (files.isEmpty()) {
            return;
        }

        long cacheSizeBytes = Util.getCacheSizeMB(context) * 1024L * 1024L;

        long bytesUsedBySubsonic = 0L;
        for (File file : files) {
            bytesUsedBySubsonic += file.length();
        }

        // Ensure that file system is not more than 95% full.
        StatFs stat = new StatFs(files.get(0).getPath());
        long bytesTotalFs = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        long bytesAvailableFs = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        long bytesUsedFs = bytesTotalFs - bytesAvailableFs;
        long minFsAvailability = Math.round(MAX_FILE_SYSTEM_USAGE * (double) bytesTotalFs);

        long bytesToDeleteCacheLimit = Math.max(bytesUsedBySubsonic - cacheSizeBytes, 0L);
        long bytesToDeleteFsLimit = Math.max(bytesUsedFs - minFsAvailability, 0L);
        long bytesToDelete = Math.max(bytesToDeleteCacheLimit, bytesToDeleteFsLimit);

        LOG.info("File system       : " + Util.formatBytes(bytesAvailableFs) + " of " + Util.formatBytes(bytesTotalFs) + " available");
        LOG.info("Cache limit       : " + Util.formatBytes(cacheSizeBytes));
        LOG.info("Cache size before : " + Util.formatBytes(bytesUsedBySubsonic));
        LOG.info("Minimum to delete : " + Util.formatBytes(bytesToDelete));

        long bytesDeleted = 0L;
        for (File file : files) {

            if (file.getName().equals(Constants.ALBUM_ART_FILE)) {
                // Move artwork to new folder.
                file.renameTo(FileUtil.getAlbumArtFile(context, file.getParentFile()));

            } else if (bytesToDelete > bytesDeleted || file.getName().endsWith(".partial") || file.getName().contains(".partial.")) {
                if (!undeletable.contains(file)) {
                    long size = file.length();
                    if (Util.delete(file)) {
                        bytesDeleted += size;
                    }
                }
            }
        }

        LOG.info("Deleted           : " + Util.formatBytes(bytesDeleted));
        LOG.info("Cache size after  : " + Util.formatBytes(bytesUsedBySubsonic - bytesDeleted));
    }

    private void findCandidatesForDeletion(File file, List<File> files, List<File> dirs) {
        if (file.isFile()) {
            String name = file.getName();
            boolean isCacheFile = name.endsWith(".partial") || name.contains(".partial.") || name.endsWith(".complete") || name.contains(".complete.");
            boolean isAlbumArtFile = name.equals(Constants.ALBUM_ART_FILE);
            if (isCacheFile || isAlbumArtFile) {
                files.add(file);
            }
        } else {
            // Depth-first
            for (File child : FileUtil.listFiles(file)) {
                findCandidatesForDeletion(child, files, dirs);
            }
            dirs.add(file);
        }
    }

    private void sortByAscendingModificationTime(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File a, File b) {
                if (a.lastModified() < b.lastModified()) {
                    return -1;
                }
                if (a.lastModified() > b.lastModified()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private Set<File> findUndeletableFiles() {
        Set<File> undeletable = new HashSet<File>(5);

        for (DownloadFile downloadFile : downloadService.getDownloads()) {
            undeletable.add(downloadFile.getPartialFile());
            undeletable.add(downloadFile.getCompleteFile());
        }

        undeletable.add(FileUtil.getMusicDirectory(context));
        return undeletable;
    }
}
