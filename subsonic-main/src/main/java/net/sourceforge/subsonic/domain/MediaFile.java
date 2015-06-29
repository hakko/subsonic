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
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.MediaFolderService;
import net.sourceforge.subsonic.service.ServiceLocator;
import net.sourceforge.subsonic.service.metadata.MetaDataParser;
import net.sourceforge.subsonic.util.FileUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.json.JSONArray;
import org.json.JSONException;

import com.github.hakko.musiccabinet.configuration.SubsonicUri;
import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;
import com.github.hakko.musiccabinet.domain.model.library.MetaData;

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

	private Uri uri;
	private File file;
	private boolean isFile;
	private boolean isDirectory;
	private boolean isVideo;
	private long lastModified;
	private MetaData metaData;

	/**
	 * Preferred usage: {@link MediaFileService#getmediaFile}.
	 */
	public MediaFile(int id, File file) {
		this(new SubsonicUri(id), file);
	}

	public MediaFile(Uri uri, File file) {
		this.uri = uri;
		this.file = file;

		// Cache these values for performance.
		isFile = file.isFile();
		isDirectory = file.isDirectory();
		lastModified = file.lastModified();
		isVideo = isFile && isVideoFile(file);

		if (isFile) {
			getMetaData();
			LOG.debug("created file with uri " + uri + " from file " + file
					+ ", metadata = " + metaData);
		}
	}

	/**
	 * Empty constructor. Used for testing purposes only.
	 */
	protected MediaFile() {
		isFile = true;
	}

	@Deprecated
	public MediaFile(int id) {
		this.uri = new SubsonicUri(id);
		this.isFile = true;
	}

	public MediaFile(Uri uri) {
		this.uri = uri;
		this.isFile = true;
	}

	public Uri getUri() {
		return uri;
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

	public boolean isLocal() {
		return file != null;
	}

	/**
	 * Returns whether this music file is one of the root music folders.
	 * 
	 * @return Whether this music file is one of the root music folders.
	 */
	public boolean isRoot() {
		MediaFolderService mediaFolderSettings = ServiceLocator
				.getMediaFolderService();
		List<MediaFolder> folders = mediaFolderSettings.getAllMediaFolders();
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
		if (file == null) {
			return 0;
		}
		return file.length();
	}

	/**
	 * Returns whether this music file exists.
	 * 
	 * @return Whether this music file exists.
	 */
	public boolean exists() {

		if (isSpotify()) {
			return true;
		}
		if (URIUtil.isRemote(uri)) {
			return true;
		}
		return file.exists();
	}

	/**
	 * Returns the name of the music file. This is normally just the last name
	 * in the pathname's name sequence.
	 * 
	 * @return The name of the music file.
	 */
	public String getName() {
		if (file == null) {
			return getUriPath();
		}
		return file.getName();
	}

	public void setPath(String path) {
		this.file = new File(path);
	}

	private String getUriPath() {
		return this.getUri().getUri().toString();
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
		if (!isLocal()) {
			return "mp3";
		}
		return FilenameUtils.getExtension(getName());
	}

	/**
	 * Returns the full pathname as a string.
	 * 
	 * @return The full pathname as a string.
	 */
	public String getPath() {
		if (file == null) {
			return getUriPath();
		}
		return file.getPath();
	}

	public String getAbsolutePath() {
		if (file == null) {
			return getUriPath();
		}
		return file.getAbsolutePath();
	}

	public InputStream getInputStream() throws IOException {

		if (file != null) {
			return new FileInputStream(file);
		}
		return this.uri.getUri().toURL().openStream();
	}

	/**
	 * Returns meta data for this music file.
	 * 
	 * @return Meta data (artist, album, title etc) for this music file.
	 */
	public MetaData getMetaData() {
		if (metaData == null) {
			MetaDataParser parser = ServiceLocator.getMetaDataParserFactory()
					.getParser(this);
			metaData = (parser == null) ? null : parser.getMetaData(this);
		}
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
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
	public MediaFile getParent() {
		if (file == null) {
			return null;
		}
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
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public List<MediaFile> getChildren(FileFilter filter) throws IOException {
		if (file == null) {
			return new ArrayList<MediaFile>();
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

		Collections.sort(result);

		return result;
	}

	private MediaFile createMediaFile(File file) {
		return ServiceLocator.getMediaFileService()
				.getNonIndexedMediaFile(file);
	}

	private boolean acceptMedia(File file) throws IOException {

		if (isExcluded(file)) {
			return false;
		}

		if (file.isDirectory()) {
			return true;
		}

		return isMusicFile(file) || isVideoFile(file);
	}

	private static boolean isMusicFile(File file) {
		return FilenameUtils.isExtension(file.getName(), ServiceLocator
				.getSettingsService().getMusicFileTypesAsArray());
	}

	private static boolean isVideoFile(File file) {
		return FilenameUtils.isExtension(file.getName(), ServiceLocator
				.getSettingsService().getVideoFileTypesAsArray());
	}

	/**
	 * @param file
	 *            The child file in question.
	 * @return Whether the child file is excluded.
	 */
	private boolean isExcluded(File file) throws IOException {

		// Exclude all hidden files starting with a "." or "@eaDir" (thumbnail
		// dir created on Synology devices).
		return (file.getName().startsWith(".") || file.getName().startsWith(
				"@eaDir"));
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != getClass())
			return false;

		return uri == ((MediaFile) o).uri;
	}

	/**
	 * Returns the hash code of this music file.
	 * 
	 * @return The hash code of this music file.
	 */
	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	/**
	 * Equivalent to {@link #getPath}.
	 * 
	 * @return This music file as a string.
	 */
	@Override
	public String toString() {
		if (isSpotify()) {
			return file.getName();
		}
		return getPath();
	}

	public boolean isSpotify() {
		if (file != null && file.getParent() != null
				&& file.getParent().equals("spotify:")) {
			return true;
		}

		if (file.getPath().startsWith("spotify:")) {
			return true;
		}

		return false;
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
				.append(nvl(md1.getDiscNr(), 1), nvl(md2.getDiscNr(), 1))
				.append(nvl(md1.getTrackNr(), -1), nvl(md2.getTrackNr(), -1))
				.append(md1.getTitle(), md2.getTitle());

		return ctb.toComparison();
	}

	private int nvl(Short value, int defaultValue) {
		return value == null ? defaultValue : value;
	}

	/*
	 * given string "['x', 'y', 'z']", returns the integers x, y and z as a
	 * list.
	 */
	public static List<? extends Uri> getMediaFileUris(String query)
			throws JSONException {
		List<Uri> mediaFileIds = new ArrayList<>();

		JSONArray list = new JSONArray(query);
		for (int i = 0; i < list.length(); i++) {
			mediaFileIds.add(URIUtil.parseURI(list.getString(i)));

		}
		return mediaFileIds;
	}

	public static enum MediaType {
		MUSIC, PODCAST, AUDIOBOOK, VIDEO, DIRECTORY, ALBUM
	}

}