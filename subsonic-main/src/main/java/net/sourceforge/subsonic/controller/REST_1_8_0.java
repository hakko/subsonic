package net.sourceforge.subsonic.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface REST_1_8_0 {

    // System
    public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getLicense(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Browsing
    public void getMusicFolders(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getIndexes(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getMusicDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getArtist(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getAlbumList(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getAlbumList2(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getAlbum(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getSong(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getVideos(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getStarred(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getStarred2(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getRandomSongs(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getNowPlaying(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Search
    public void search(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void search2(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void search3(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Playlists
    public void getPlaylists(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void createPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void deletePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Media retrieval
    public ModelAndView stream(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public ModelAndView getCoverArt(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getLyrics(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getAvatar(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Media annotation
    public void setRating(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void scrobble(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void star(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void unstar(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Share
    public void getShares(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void createShare(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void updateShare(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void deleteShare(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Podcast
    public void getPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Jukebox
    public void jukeboxControl(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // Chat
    public void getChatMessages(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void addChatMessage(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // User
    public void getUser(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void getUsers(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void createUser(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception;
    public void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
