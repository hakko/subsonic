/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.domain;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.ServiceLocator;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.metadata.MetaDataParser;
import net.sourceforge.subsonic.util.FileUtil;
import net.sourceforge.subsonic.util.StringUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.util.StringUtils;

/**
 * Represents a file or directory containing music. Media files can be put in a
 * {@link Playlist}, and may be streamed to remote players. All media files are
 * located in a configurable root music folder.
 * 
 * @author Sindre Mehus
 */
public class MediaFile implements Serializable, Comparable<MediaFile> {

	private static final long serialVersionUID = -3826007043440542822L;

	private static final Logger LOG = Logger.getLogger(MediaFile.class);

	private int id;
	private File file;
	private boolean isFile;
	private boolean isDirectory;
	private boolean isVideo;
	private long lastModified;
	private MetaData metaData;
	private Set<String> excludes;

	/**
	 * Preferred usage:
	 * {@link MediaFileService#getmediaFile}.
	 */
	public MediaFile(int id, File file) {
		this.id = id;
		this.file = file;

		// Cache these values for performance.
		isFile = file.isFile();
		isDirectory = file.isDirectory();
		lastModified = file.lastModified();
		String suffix = FilenameUtils.getExtension(file.getName())
				.toLowerCase();
		isVideo = isFile && isVideoFile(suffix);

		// added by MusicCabinet: force reading meta data.
		// we want that cached for faster generation of playlists.
		if (isFile) {
			getMetaData();
			LOG.debug("created file with id " + id + " from file " + file + ", metadata = " + metaData);
		}
	}

	/**
	 * Empty constructor. Used for testing purposes only.
	 */
	protected MediaFile() {
		isFile = true;
	}
	
	public MediaFile(int id) {
		this.id = id;
		this.isFile = true;
		this.metaData = new MetaData();
	}

	public int getId() {
		return id;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public boolean isFile() {
		return isFile;
	}

	/**
	 * Returns whether this music file is a directory.
	 * 
	 * @return Whether this music file is a directory.
	 */
	public boolean isDirectory() {
		return isDirectory;
	}

	/**
	 * Returns whether this media file is a video.
	 * 
	 * @return Whether this media file is a video.
	 */
	public boolean isVideo() {
		return isVideo;
	}

	/**
	 * Returns whether this music file is one of the root music folders.
	 * 
	 * @return Whether this music file is one of the root music folders.
	 */
	public boolean isRoot() {
		SettingsService settings = ServiceLocator.getSettingsService();
		List<MediaFolder> folders = settings.getAllMediaFolders();
		for (MediaFolder folder : folders) {
			if (file.equals(folder.getPath())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the time this music file was last modified.
	 * 
	 * @return The time since this music file was last modified, in milliseconds
	 *         since the epoch.
	 */
	public long lastModified() {
		return lastModified;
	}

	/**
	 * Returns the length of the music file. The return value is unspecified if
	 * this music file is a directory.
	 * 
	 * @return The length, in bytes, of the music file, or or <code>0L</code> if
	 *         the file does not exist
	 */
	public long length() {
		return file.length();
	}

	/**
	 * Returns whether this music file exists.
	 * 
	 * @return Whether this music file exists.
	 */
	public boolean exists() {
		return file.exists();
	}

	/**
	 * Returns the name of the music file. This is normally just the last name
	 * in the pathname's name sequence.
	 * 
	 * @return The name of the music file.
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * Same as {@link #getName}, but without file suffix (unless this music file
	 * represents a directory).
	 * 
	 * @return The name of the file without the suffix
	 */
	public String getNameWithoutSuffix() {
		String name = getName();
		if (isDirectory()) {
			return name;
		}
		int i = name.lastIndexOf('.');
		return i == -1 ? name : name.substring(0, i);
	}

	/**
	 * Returns the file suffix, e.g., "mp3".
	 * 
	 * @return The file suffix.
	 */
	public String getSuffix() {
		return StringUtils.getFilenameExtension(getName());
	}

	/**
	 * Returns the full pathname as a string.
	 * 
	 * @return The full pathname as a string.
	 */
	public String getPath() {
		return file.getPath();
	}

	/**
	 * Returns meta data for this music file.
	 * 
	 * @return Meta data (artist, album, title etc) for this music file.
	 */
	public synchronized MetaData getMetaData() {
		if (metaData == null) {
			MetaDataParser parser = ServiceLocator.getMetaDataParserFactory()
					.getParser(this);
			metaData = (parser == null) ? null : parser.getMetaData(this);
		}
		return metaData;
	}

	/**
	 * Returns the title of the music file, by attempting to parse relevant
	 * meta-data embedded in the file, for instance ID3 tags in MP3 files.
	 * <p/>
	 * If this music file is a directory, or if no tags are found, this method
	 * is equivalent to {@link #getNameWithoutSuffix}.
	 * 
	 * @return The song title of this music file.
	 */
	public String getTitle() {
		return getMetaData() == null ? getNameWithoutSuffix() : getMetaData()
				.getTitle();
	}

	/**
	 * Returns the parent music file.
	 * 
	 * @return The parent music file, or <code>null</code> if no parent exists.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public MediaFile getParent() throws IOException {
		File parent = file.getParentFile();
		return parent == null ? null : createMediaFile(parent);
	}

	/**
	 * Returns all music files that are children of this music file.
	 * 
	 * @param includeFiles
	 *            Whether files should be included in the result.
	 * @param includeDirectories
	 *            Whether directories should be included in the result.
	 * @param sort
	 *            Whether to sort files in the same directory. @return All
	 *            children music files.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public List<MediaFile> getChildren(boolean includeFiles,
			boolean includeDirectories, boolean sort) throws IOException {

		FileFilter filter;
		if (includeFiles && includeDirectories) {
			filter = TrueFileFilter.INSTANCE;
		} else if (includeFiles) {
			filter = FileFileFilter.FILE;
		} else if (includeDirectories) {
			filter = DirectoryFileFilter.DIRECTORY;
		} else {
			filter = FalseFileFilter.INSTANCE;
		}

		File[] children = FileUtil.listFiles(file, filter);
		List<MediaFile> result = new ArrayList<MediaFile>(children.length);

		for (File child : children) {
			try {
				if (acceptMedia(child)) {
					result.add(createMediaFile(child));
				}
			} catch (SecurityException x) {
				LOG.warn("Failed to create mediaFile for " + child, x);
			}
		}
		
		if (sort) {
			Collections.sort(result);
		}

		return result;
	}

	private MediaFile createMediaFile(File file) {
		return ServiceLocator.getMediaFileService().getNonIndexedMediaFile(file);
	}

	private boolean acceptMedia(File file) throws IOException {

		if (isExcluded(file)) {
			return false;
		}

		if (file.isDirectory()) {
			return true;
		}

		String suffix = FilenameUtils.getExtension(file.getName())
				.toLowerCase();
		return isMediaFile(suffix) || isVideoFile(suffix);
	}

	private static boolean isMediaFile(String suffix) {
		for (String s : ServiceLocator.getSettingsService()
				.getMediaFileTypesAsArray()) {
			if (suffix.equals(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private static boolean isVideoFile(String suffix) {
		for (String s : ServiceLocator.getSettingsService()
				.getVideoFileTypesAsArray()) {
			if (suffix.equals(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given file is excluded, i.e., whether it is listed in
	 * 'subsonic_exclude.txt' in the current directory.
	 * 
	 * @param file
	 *            The child file in question.
	 * @return Whether the child file is excluded.
	 */
	public boolean isExcluded(File file) throws IOException {

		// Exclude all hidden files starting with a "." or "@eaDir" (thumbnail
		// dir created on Synology devices).
		if (file.getName().startsWith(".")
				|| file.getName().startsWith("@eaDir")) {
			return true;
		}

		if (excludes == null) {
			excludes = new HashSet<String>();
			File excludeFile = new File(this.file, "subsonic_exclude.txt");
			if (excludeFile.exists()) {
				String[] lines = StringUtil.readLines(new FileInputStream(
						excludeFile));
				for (String line : lines) {
					excludes.add(line.toLowerCase());
				}
			}
		}

		return excludes.contains(file.getName().toLowerCase());
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (o.getClass() != getClass()) return false;
		
		return id == ((MediaFile) o).id;
	}

	/**
	 * Returns the hash code of this music file.
	 * 
	 * @return The hash code of this music file.
	 */
	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * Equivalent to {@link #getPath}.
	 * 
	 * @return This music file as a string.
	 */
	@Override
	public String toString() {
		return getPath();
	}

	@Override
	public int compareTo(MediaFile mf) {

		if (!isDirectory && mf.isDirectory) {
			return 1;
		} else if (isDirectory && !mf.isDirectory) {
			return -1;
		} else if (isDirectory && mf.isDirectory) {
			return getName().compareTo(mf.getName());
		}
		
		MetaData md1 = getMetaData();
		MetaData md2 = mf.getMetaData();
		
		CompareToBuilder ctb = new CompareToBuilder()
		.append(nvl(md1.getDiscNumber(), 1), nvl(md2.getDiscNumber(), 1))
		.append(nvl(md1.getTrackNumber(), -1), nvl(md2.getTrackNumber(), -1))
		.append(md1.getTitle(), md2.getTitle());
		
		return ctb.toComparison();
	}
	
	private int nvl(Integer value, int defaultValue) {
		return value == null ? defaultValue : value;
	}

}