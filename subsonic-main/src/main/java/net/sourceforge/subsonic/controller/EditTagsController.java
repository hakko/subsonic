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
package net.sourceforge.subsonic.controller;

import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;
import static org.apache.commons.lang.StringUtils.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MetaData;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.metadata.JaudiotaggerParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.github.hakko.musiccabinet.configuration.SubsonicUri;
import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;

/**
 * Controller for the page used to edit MP3 tags.
 *
 * @author Sindre Mehus
 */
public class EditTagsController extends ParameterizableViewController {

    private MediaFileService mediaFileService;
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    	List<? extends Uri> mediaFileIds = MediaFile.getMediaFileUris(request.getParameter("ids"));
        
        List<MediaFile> files = mediaFileService.getMediaFiles(mediaFileIds);

        Map<String, Object> map = new HashMap<String, Object>();
        if (!files.isEmpty()) {
            MetaData metaData = files.get(0).getMetaData();
            map.put("defaultArtist", metaData.getArtist());
            map.put("defaultAlbumArtist", metaData.getAlbumArtist());
            map.put("defaultComposer", metaData.getComposer());
            map.put("defaultAlbum", metaData.getAlbum());
            map.put("defaultYear", metaData.getYear());
            map.put("defaultGenre", metaData.getGenre());
        }
        map.put("allGenres", JaudiotaggerParser.getID3V1Genres());

        List<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < files.size(); i++) {
            songs.add(createSong(files.get(i), i));
        }
        
        map.put("songs", songs);
        map.put("artistId", request.getParameter("artistId"));
        map.put("albumId", request.getParameter("albumId"));

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private Song createSong(MediaFile mf, int index) {
        MetaData metaData = mf.getMetaData();

        Song song = new Song();
        song.setUri(mf.getUri());
        song.setFileName(mf.getName());
        song.setTrack(metaData.getTrackNumber());
        song.setSuggestedTrack(index + 1);
        song.setTitle(metaData.getTitle());
        song.setSuggestedTitle(mf.getNameWithoutSuffix());
        song.setArtist(metaData.getArtist());
        song.setAlbumArtist(metaData.getAlbumArtist());
        song.setComposer(metaData.getComposer());
        song.setAlbum(metaData.getAlbum());
        song.setYear(metaData.getYear());
        song.setGenre(metaData.getGenre());
        return song;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

	/**
     * Contains information about a single song.
     */
    public static class Song {
        private Uri uri;
        private String fileName;
        private Integer suggestedTrack;
        private Integer track;
        private String suggestedTitle;
        private String title;
        private String artist;
        private String albumArtist;
        private String composer;
        private String album;
        private String year;
        private String genre;

        @Deprecated
        public int getId() {
			return uri.getId();
		}

		public void setId(int id) {
			this.uri = new SubsonicUri(id);
		}

		public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getSuggestedTrack() {
            return suggestedTrack;
        }

        public void setSuggestedTrack(Integer suggestedTrack) {
            this.suggestedTrack = suggestedTrack;
        }

        public Integer getTrack() {
            return track;
        }

        public void setTrack(Integer track) {
            this.track = track;
        }

        public String getSuggestedTitle() {
            return suggestedTitle;
        }

        public void setSuggestedTitle(String suggestedTitle) {
            this.suggestedTitle = suggestedTitle;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

		public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbumArtist() {
			return albumArtist;
		}

		public void setAlbumArtist(String albumArtist) {
			this.albumArtist = albumArtist;
		}

		public String getComposer() {
			return composer;
		}

		public void setComposer(String composer) {
			this.composer = composer;
		}

		public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

		@Override
		public String toString() {
			return "Song [uri=" + uri + ", fileName=" + fileName
					+ ", suggestedTrack=" + suggestedTrack + ", track=" + track
					+ ", suggestedTitle=" + suggestedTitle + ", title=" + title
					+ ", artist=" + artist + ", album artist=" + albumArtist
					+ ", composer=" + composer + ", album=" + album + ", year="
					+ year + ", genre=" + genre + "]";
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}
		
		
        
    }

}