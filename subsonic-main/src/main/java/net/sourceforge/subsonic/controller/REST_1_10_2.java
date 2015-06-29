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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * Multi-controller used for the REST API.
 * <p/>
 * For documentation, please refer to api.jsp.
 * <p/>
 * Note: Exceptions thrown from the methods are intercepted by RESTFilter.
 * 
 * @author Sindre Mehus
 */
public interface REST_1_10_2 {

	public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception;

	public void getLicense(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getMusicFolders(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getIndexes(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getGenres(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getSongsByGenre(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getArtists(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getArtist(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getAlbum(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getSong(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void getMusicDirectory(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	@Deprecated
	public void search(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void search2(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void search3(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void getPlaylists(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getPlaylist(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void jukeboxControl(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void createPlaylist(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void updatePlaylist(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void deletePlaylist(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getAlbumList(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getAlbumList2(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getRandomSongs(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getVideos(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getNowPlaying(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ModelAndView download(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ModelAndView stream(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ModelAndView hls(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void scrobble(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void star(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void unstar(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void getStarred(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getStarred2(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getPodcasts(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void refreshPodcasts(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void createPodcastChannel(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void deletePodcastChannel(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void deletePodcastEpisode(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void downloadPodcastEpisode(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getInternetRadioStations(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getBookmarks(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void createBookmark(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void deleteBookmark(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getShares(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void createShare(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void deleteShare(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void updateShare(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ModelAndView videoPlayer(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ModelAndView getCoverArt(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ModelAndView getAvatar(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void changePassword(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getUser(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void getUsers(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void createUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void updateUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void deleteUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getChatMessages(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void addChatMessage(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void getLyrics(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void setRating(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void error(HttpServletRequest request, HttpServletResponse response,
			ErrorCode code, String message) throws Exception;

	public static enum ErrorCode {

		GENERIC(0, "A generic error."), MISSING_PARAMETER(10,
				"Required parameter is missing."), PROTOCOL_MISMATCH_CLIENT_TOO_OLD(
				20,
				"Incompatible Subsonic REST protocol version. Client must upgrade."), PROTOCOL_MISMATCH_SERVER_TOO_OLD(
				30,
				"Incompatible Subsonic REST protocol version. Server must upgrade."), NOT_AUTHENTICATED(
				40, "Wrong username or password."), NOT_AUTHORIZED(50,
				"User is not authorized for the given operation."), NOT_LICENSED(
				60,
				"The trial period for the Subsonic server is over. Please upgrade to Subsonic Premium. Visit subsonic.org for details."), NOT_FOUND(
				70, "Requested data was not found.");

		private final int code;
		private final String message;

		ErrorCode(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}
	}

}
