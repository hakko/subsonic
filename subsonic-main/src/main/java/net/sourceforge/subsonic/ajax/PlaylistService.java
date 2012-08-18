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
package net.sourceforge.subsonic.ajax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.MetaData;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.service.JukeboxService;
import net.sourceforge.subsonic.service.MediaFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.util.StringUtil;

import org.directwebremoting.WebContextFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.github.hakko.musiccabinet.service.PlaylistGeneratorService;

/**
 * Provides AJAX-enabled services for manipulating the playlist of a player.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class PlaylistService {

    private static final Logger LOG = Logger.getLogger(PlaylistService.class);

    private PlayerService playerService;
    private MediaFileService mediaFileService;
    private JukeboxService jukeboxService;
    private TranscodingService transcodingService;
    private SettingsService settingsService;

    private PlaylistGeneratorService playlistService;

    /**
     * Returns the playlist for the player of the current user.
     *
     * @return The playlist.
     */
    public PlaylistInfo getPlaylist() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        return convert(request, player, false);
    }

    public PlaylistInfo start() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doStart(request, response);
    }

    public PlaylistInfo doStart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().setStatus(Playlist.Status.PLAYING);
        return convert(request, player, true);
    }

    public PlaylistInfo stop() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doStop(request, response);
    }

    public PlaylistInfo doStop(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().setStatus(Playlist.Status.STOPPED);
        return convert(request, player, true);
    }

    public PlaylistInfo skip(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doSkip(request, response, index, 0);
    }

    public PlaylistInfo doSkip(HttpServletRequest request, HttpServletResponse response, int index, int offset) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().setIndex(index);
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(request, player, serverSidePlaylist, offset);
    }

    public PlaylistInfo play(List<Integer> ids, String mode) throws Exception {
    	LOG.debug("starting play(" + ids + ", " + mode + ")");
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        
        Player player = getCurrentPlayer(request, response);
        List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
        for (int id : ids) {
        	mediaFiles.add(mediaFileService.getMediaFile(id));
        }
        if (player.isWeb()) {
            removeVideoFiles(mediaFiles);
        }
        player.getPlaylist().addFiles(mode, mediaFiles);
        return convert(request, player, true);
    }
    
    public PlaylistInfo playRandom(List<Integer> ids, int count, String mode) throws Exception {
    	LOG.debug("starting playRandom(" + ids + ", " + count + ", " + mode + ")");
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        List<MediaFile> mediaFiles = new ArrayList<>();
        Collections.shuffle(ids);
        for (int i = 0; i < count && i < ids.size(); i++) {
        	mediaFiles.add(mediaFileService.getMediaFile(ids.get(i)));
        }
        
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().addFiles(mode, mediaFiles);
        return convert(request, player, true);
    }

    public PlaylistInfo playArtistRadio(int artistId, String mode) throws Exception {
    	LOG.debug("starting playArtistRadio(" + artistId + ", " + mode + ")");

    	return getPlaylistInfo(mode, playlistService.getPlaylistForArtist(artistId, 
    			settingsService.getArtistRadioArtistCount(),
    			settingsService.getArtistRadioTotalCount()));
    }

    public PlaylistInfo playTopTracks(int artistId, String mode) throws Exception {
    	LOG.debug("starting playTopTracks(" + artistId + ", " + mode + ")");

    	return getPlaylistInfo(mode, playlistService.getTopTracksForArtist(artistId,
    			settingsService.getArtistTopTracksTotalCount()));
    }
    
    public PlaylistInfo playGenreRadio(String[] tags) throws Exception {
    	LOG.debug("starting playGenreRadio(" + Arrays.toString(tags) + ")");
    	
    	return getPlaylistInfo(Playlist.PLAY, playlistService.getTopTracksForTags(tags,
    			settingsService.getGenreRadioArtistCount(),
    			settingsService.getGenreRadioTotalCount()));
    }

    public PlaylistInfo playRelatedArtistsSampler(int artistId, int totalCount) throws Exception {
    	LOG.debug("starting playRelatedArtistsSampler(" + artistId + ", " + totalCount + ")");

    	return getPlaylistInfo(Playlist.PLAY, playlistService.getPlaylistForRelatedArtists(
    			artistId, settingsService.getRelatedArtistsSamplerArtistCount(), totalCount));
    }

    private PlaylistInfo getPlaylistInfo(String mode, List<Integer> trackIds) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        
        mediaFileService.loadMediaFiles(trackIds);
        List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
        for (int trackId : trackIds) {
        	mediaFiles.add(mediaFileService.getMediaFile(trackId));
        }
        
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().addFiles(mode, mediaFiles);
        return convert(request, player, true);
    	
    }

    public PlaylistInfo add(int mediaFileId) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doAdd(request, response, Arrays.asList(mediaFileId));
    }

    public PlaylistInfo doAdd(HttpServletRequest request, HttpServletResponse response, List<Integer> mediaFileIds) throws Exception {
        Player player = getCurrentPlayer(request, response);
        List<MediaFile> files = new ArrayList<MediaFile>(mediaFileIds.size());
        for (int mediaFileId : mediaFileIds) {
            files.add(mediaFileService.getMediaFile(mediaFileId));
        }
        if (player.isWeb()) {
            removeVideoFiles(files);
        }
        player.getPlaylist().addFiles(Playlist.ADD, files);
        return convert(request, player, false);
    }
    
    public PlaylistInfo doSet(HttpServletRequest request, HttpServletResponse response, List<Integer> mediaFileIds) throws Exception {
        Player player = getCurrentPlayer(request, response);
        Playlist playlist = player.getPlaylist();
        MediaFile currentFile = playlist.getCurrentFile();
        Playlist.Status status = playlist.getStatus();

        playlist.clear();
        PlaylistInfo result = doAdd(request, response, mediaFileIds);

        int index = currentFile == null ? -1 : Arrays.asList(playlist.getFiles()).indexOf(currentFile);
        playlist.setIndex(index);
        playlist.setStatus(status);
        return result;
    }

    public PlaylistInfo clear() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doClear(request, response);
    }

    public PlaylistInfo doClear(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().clear();
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(request, player, serverSidePlaylist);
    }

    public PlaylistInfo shuffle() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doShuffle(request, response);
    }

    public PlaylistInfo doShuffle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().shuffle();
        return convert(request, player, false);
    }

    public PlaylistInfo remove(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doRemove(request, response, index);
    }

    public PlaylistInfo doRemove(HttpServletRequest request, HttpServletResponse response, int index) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().removeFileAt(index);
        return convert(request, player, false);
    }

    public PlaylistInfo removeMany(int[] indexes) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        for (int i = indexes.length - 1; i >= 0; i--) {
            player.getPlaylist().removeFileAt(indexes[i]);
        }
        return convert(request, player, false);
    }

    public PlaylistInfo up(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().moveUp(index);
        return convert(request, player, false);
    }

    public PlaylistInfo down(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().moveDown(index);
        return convert(request, player, false);
    }

    public PlaylistInfo toggleRepeat() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().setRepeatEnabled(!player.getPlaylist().isRepeatEnabled());
        return convert(request, player, false);
    }

    public PlaylistInfo undo() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().undo();
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(request, player, serverSidePlaylist);
    }

    public PlaylistInfo sortByTrack() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().sort(Playlist.SortOrder.TRACK);
        return convert(request, player, false);
    }

    public PlaylistInfo sortByArtist() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().sort(Playlist.SortOrder.ARTIST);
        return convert(request, player, false);
    }

    public PlaylistInfo sortByAlbum() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlaylist().sort(Playlist.SortOrder.ALBUM);
        return convert(request, player, false);
    }

    public void setGain(float gain) {
        jukeboxService.setGain(gain);
    }

    private void removeVideoFiles(List<MediaFile> files) {
        Iterator<MediaFile> iterator = files.iterator();
        while (iterator.hasNext()) {
            MediaFile file = iterator.next();
            if (file.isVideo()) {
                iterator.remove();
            }
        }
    }

    private PlaylistInfo convert(HttpServletRequest request, Player player, boolean sendM3U) throws Exception {
		return convert(request, player, sendM3U, 0);
    }

    private PlaylistInfo convert(HttpServletRequest request, Player player, boolean sendM3U, int offset) throws Exception {
    	String url = request.getRequestURL().toString();

        if (sendM3U && player.isJukebox()) {
            jukeboxService.updateJukebox(player, offset);
        }
        boolean isCurrentPlayer = player.getIpAddress() != null && player.getIpAddress().equals(request.getRemoteAddr());

        boolean m3uSupported = player.isExternal() || player.isExternalWithPlaylist();
        sendM3U = player.isAutoControlEnabled() && m3uSupported && isCurrentPlayer && sendM3U;
        Locale locale = RequestContextUtils.getLocale(request);

        List<PlaylistInfo.Entry> entries = new ArrayList<PlaylistInfo.Entry>();
        Playlist playlist = player.getPlaylist();
        for (MediaFile file : playlist.getFiles()) {
            MetaData metaData = file.getMetaData();
            String albumUrl = url.replaceFirst("/dwr/.*", "/artist.view?id=" + metaData.getArtistId() 
            		+ "&albumId=" + metaData.getAlbumId());
            String streamUrl = url.replaceFirst("/dwr/.*", "/stream?player=" + player.getId() + "&mfId=" + file.getId());

            // Rewrite URLs in case we're behind a proxy.
            if (settingsService.isRewriteUrlEnabled()) {
                String referer = request.getHeader("referer");
                albumUrl = StringUtil.rewriteUrl(albumUrl, referer);
                streamUrl = StringUtil.rewriteUrl(streamUrl, referer);
            }

            String format = formatFormat(player, file);
            entries.add(new PlaylistInfo.Entry(metaData.getTrackNumber(), metaData.getTitle(), metaData.getArtist(),
                    metaData.getAlbum(), metaData.getGenre(), metaData.getYear(), formatBitRate(metaData),
                    metaData.getDuration(), metaData.getDurationAsString(), format, formatContentType(format),
                    formatFileSize(metaData.getFileSize(), locale), albumUrl, streamUrl));
        }
        boolean isStopEnabled = playlist.getStatus() == Playlist.Status.PLAYING && !player.isExternalWithPlaylist();
        float gain = jukeboxService.getGain();
        return new PlaylistInfo(entries, playlist.getIndex(), isStopEnabled, playlist.isRepeatEnabled(), sendM3U, gain);
    }

    private String formatFileSize(Long fileSize, Locale locale) {
        if (fileSize == null) {
            return null;
        }
        return StringUtil.formatBytes(fileSize, locale);
    }

    private String formatFormat(Player player, MediaFile file) {
        return transcodingService.getSuffix(player, file, null);
    }

    private String formatContentType(String format) {
        return StringUtil.getMimeType(format);
    }

    private String formatBitRate(MetaData metaData) {
        if (metaData.getBitRate() == null) {
            return null;
        }
        if (Boolean.TRUE.equals(metaData.getVariableBitRate())) {
            return metaData.getBitRate() + " Kbps vbr";
        }
        return metaData.getBitRate() + " Kbps";
    }

    private Player getCurrentPlayer(HttpServletRequest request, HttpServletResponse response) {
        return playerService.getPlayer(request, response);
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setJukeboxService(JukeboxService jukeboxService) {
        this.jukeboxService = jukeboxService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

	public void setPlaylistGeneratorService(PlaylistGeneratorService playlistService) {
		this.playlistService = playlistService;
	}
	
}