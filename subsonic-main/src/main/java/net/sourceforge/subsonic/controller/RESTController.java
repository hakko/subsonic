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
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Multi-controller used for the REST API, see http://www.subsonic.org/pages/api.jsp
 *
 * This class is modeled according to the Facade pattern (GoF) and delegates tasks
 * to REST*Controller. Sub classes are named to roughly comply with api.jsp.
 */
public class RESTController extends MultiActionController implements REST_1_10_2 {

    private RESTSystemController restSystemController;
    private RESTBrowseController restBrowseController;
    private RESTSearchController restSearchController;
    private RESTPlaylistController restPlaylistController;
    private RESTMediaRetrievalController restMediaRetrievalController;
    private RESTMediaAnnotationController restMediaAnnotationController;
    private RESTShareController restShareController;
    private RESTPodcastController restPodcastController;
    private RESTJukeboxController restJukeboxController;
    private RESTChatController restChatController;
    private RESTUserController restUserController;

    @Override
    public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restSystemController.ping(request, response);
    }

    @Override
    public void getLicense(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restSystemController.getLicense(request, response);
    }

    @Override
    public void getMusicFolders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getMusicFolders(request, response);
    }

    @Override
    public void getIndexes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getIndexes(request, response);
    }

    @Override
    public void getMusicDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getMusicDirectory(request, response);
    }

    @Override
    public void getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getArtists(request, response);
    }

    @Override
    public void getArtist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getArtist(request, response);
    }

    @Override
    public void getAlbum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getAlbum(request, response);
    }

    @Override
    public void getSong(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getSong(request, response);
    }

    @Override
    public void getVideos(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getVideos(request, response);
    }

    @Override
    public void getAlbumList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getAlbumList(request, response);
    }

    @Override
    public void getAlbumList2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getAlbumList2(request, response);
    }

    @Override
    public void getRandomSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getRandomSongs(request, response);
    }

    @Override
    public void getNowPlaying(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getNowPlaying(request, response);
    }

    @Override
    public void getStarred(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getStarred(request, response);
    }

    @Override
    public void getStarred2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restBrowseController.getStarred2(request, response);
    }

    @Deprecated
    @Override
    public void search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restSearchController.search(request, response);
    }

    @Override
    public void search2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restSearchController.search2(request, response);
    }

    @Override
    public void search3(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restSearchController.search3(request, response);
    }

    @Override
    public void getPlaylists(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restPlaylistController.getPlaylists(request, response);
    }

    @Override
    public void getPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restPlaylistController.getPlaylist(request, response);
    }

    @Override
    public void createPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restPlaylistController.createPlaylist(request, response);
    }

    @Override
    public void deletePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restPlaylistController.deletePlaylist(request, response);
    }

    @Override
    public void updatePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restPlaylistController.updatePlaylist(request, response);
    }

    @Override
    public ModelAndView stream(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return restMediaRetrievalController.stream(request, response);
    }

    @Override
    public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return restMediaRetrievalController.download(request, response);
    }

    @Override
    public ModelAndView getCoverArt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return restMediaRetrievalController.getCoverArt(request, response);
    }

    @Override
    public void getLyrics(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restMediaRetrievalController.getLyrics(request, response);
    }

    @Override
    public ModelAndView getAvatar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restMediaRetrievalController.getAvatar(request, response);
        //FIXME
        return null;
    }

    @Override
    public void star(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restMediaAnnotationController.star(request, response);
    }

    @Override
    public void unstar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restMediaAnnotationController.unstar(request, response);
    }

    @Override
    public void setRating(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restMediaAnnotationController.setRating(request, response);
    }

    @Override
    public void scrobble(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restMediaAnnotationController.scrobble(request, response);
    }

    @Override
    public void getShares(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restShareController.getShares(request, response);
    }

    @Override
    public void createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restShareController.createShare(request, response);
    }

    @Override
    public void updateShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restShareController.updateShare(request, response);
    }

    @Override
    public void deleteShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restShareController.deleteShare(request, response);
    }

    @Override
    public void getPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restPodcastController.getPodcasts(request, response);
    }

    @Override
    public void jukeboxControl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restJukeboxController.jukeboxControl(request, response);
    }

    @Override
    public void getChatMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restChatController.getChatMessages(request, response);
    }

    @Override
    public void addChatMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restChatController.addChatMessage(request, response);
    }

    @Override
    public void getUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restUserController.getUser(request, response);
    }

    @Override
    public void getUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restUserController.getUsers(request, response);
    }

    @Override
    public void createUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restUserController.createUser(request, response);
    }

    @Override
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restUserController.deleteUser(request, response);
    }

    @Override
    public void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        restUserController.changePassword(request, response);
    }

    public void setRestSystemController(RESTSystemController restSystemController) {
        this.restSystemController = restSystemController;
    }

    public void setRestBrowseController(RESTBrowseController restBrowseController) {
        this.restBrowseController = restBrowseController;
    }

    public void setRestSearchController(RESTSearchController restSearchController) {
        this.restSearchController = restSearchController;
    }

    public void setRestPlaylistController(RESTPlaylistController restPlaylistController) {
        this.restPlaylistController = restPlaylistController;
    }

    public void setRestMediaRetrievalController(RESTMediaRetrievalController restMediaRetrievalController) {
        this.restMediaRetrievalController = restMediaRetrievalController;
    }

    public void setRestMediaAnnotationController(RESTMediaAnnotationController restMediaAnnotationController) {
        this.restMediaAnnotationController = restMediaAnnotationController;
    }

    public void setRestShareController(RESTShareController restShareController) {
        this.restShareController = restShareController;
    }

    public void setRestPodcastController(RESTPodcastController restPodcastController) {
        this.restPodcastController = restPodcastController;
    }

    public void setRestJukeboxController(RESTJukeboxController restJukeboxController) {
        this.restJukeboxController = restJukeboxController;
    }

    public void setRestChatController(RESTChatController restChatController) {
        this.restChatController = restChatController;
    }

    public void setRestUserController(RESTUserController restUserController) {
        this.restUserController = restUserController;
    }

	@Override
	public void getGenres(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		restBrowseController.getGenres(request, response);
	}

	@Override
	public void getSongsByGenre(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		restBrowseController.getSongsByGenre(request, response);
	}

	@Override
	public ModelAndView hls(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshPodcasts(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createPodcastChannel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePodcastChannel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePodcastEpisode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadPodcastEpisode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getInternetRadioStations(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getBookmarks(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createBookmark(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteBookmark(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ModelAndView videoPlayer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(HttpServletRequest request, HttpServletResponse response,
			ErrorCode code, String message) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
