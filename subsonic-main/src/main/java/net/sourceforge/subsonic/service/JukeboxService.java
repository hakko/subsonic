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
package net.sourceforge.subsonic.service;

import static net.sourceforge.subsonic.service.jukebox.AudioPlayer.State.EOM;
import jahspotify.JahSpotify.PlayerStatus;
import jahspotify.PlaybackListener;
import jahspotify.media.Link;
import jahspotify.services.MediaPlayer;

import java.io.InputStream;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MediaFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.domain.Transcoding;
import net.sourceforge.subsonic.domain.TransferStatus;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.VideoTranscodingSettings;
import net.sourceforge.subsonic.service.jukebox.AudioPlayer;

import org.apache.commons.io.IOUtils;

import com.github.hakko.musiccabinet.dao.util.URIUtil;
import com.github.hakko.musiccabinet.service.spotify.SpotifyService;

/**
 * Plays music on the local audio device.
 *
 * @author Sindre Mehus
 */
public class JukeboxService implements AudioPlayer.Listener, PlaybackListener {

    private static final Logger LOG = Logger.getLogger(JukeboxService.class);

    private AudioPlayer audioPlayer;
    private TranscodingService transcodingService;
    private AudioScrobblerService audioScrobblerService;
    private StatusService statusService;
    private SettingsService settingsService;
    private SecurityService securityService;
    private SpotifyService spotifyService;

    private Player player;
    private TransferStatus status;
    private MediaFile currentPlayingFile;
    private float gain = 0.5f;
    private int offset;
    private boolean spotifyListening = false;

    /**
     * Updates the jukebox by starting or pausing playback on the local audio device.
     *
     * @param player The player in question.
     * @param offset Start playing after this many seconds into the track.
     */
    public synchronized void updateJukebox(Player player, int offset) throws Exception {
        User user = securityService.getUserByName(player.getUsername());
        if (!user.isJukeboxRole()) {
            LOG.warn(user.getUsername() + " is not authorized for jukebox playback.");
            return;
        }

        if (player.getPlaylist().getStatus() == Playlist.Status.PLAYING) {
            this.player = player;
            play(player.getPlaylist().getCurrentFile(), offset);
        } else {
        	if(this.currentPlayingFile != null && this.currentPlayingFile.isSpotify()) {
        		try {
        			if(spotifyService.lock()) {
        				spotifyService.getSpotify().pause();        				
        			}
        		} finally {
        			spotifyService.unlock();
        		}
        	}
        	else if (audioPlayer != null) {
                audioPlayer.pause();
            }
        }
    }

    private synchronized void play(MediaFile file, int offset) {
        InputStream in = null;
        try {

            // Resume if possible.
            boolean sameFile = file != null && file.equals(currentPlayingFile);
            boolean paused = false;
            if(currentPlayingFile != null && currentPlayingFile.isSpotify()) {
            	paused = spotifyService.getStatus().equals(PlayerStatus.PAUSED);
            } else {
            	paused = audioPlayer != null && audioPlayer.getState() == AudioPlayer.State.PAUSED;
            }
            if (sameFile && paused && offset == 0) {
            	if(currentPlayingFile.isSpotify()) {
            		try {
            			if(spotifyService.lock()) {
            				spotifyService.getSpotify().resume();        				
            			}
            		} finally {
            			spotifyService.unlock();
            		}
            	} else {
            		audioPlayer.play();
            	}
            } else {
                this.offset = offset;
                if (audioPlayer != null) {
                    audioPlayer.close();
                    if (currentPlayingFile != null) {
                        onSongEnd(currentPlayingFile);
                    }
                }

                if (file != null && file.isSpotify()) {
            		try {
            			if(spotifyService.lock()) {
                        	if(!spotifyListening) {
                        		spotifyService.getSpotify().addPlaybackListener(this);
                        		spotifyListening = true;
                        	}
                        	
                        	spotifyService.getSpotify().play(URIUtil.getSpotifyLink(file.getName()));
            			}
            		} finally {
            			spotifyService.unlock();
            		}
                	
                }
                else if (file != null) {
                    int duration = file.getMetaData().getDuration() == null ? 0 : file.getMetaData().getDuration() - offset;
                    TranscodingService.Parameters parameters = new TranscodingService.Parameters(file, new VideoTranscodingSettings(0, 0, offset, duration, false));
                    String command = settingsService.getJukeboxCommand();
                    parameters.setTranscoding(new Transcoding(null, null, null, null, command, null, null, false));
                    in = transcodingService.getTranscodedInputStream(parameters);
                    audioPlayer = new AudioPlayer(in, this);
                    audioPlayer.setGain(gain);
                    audioPlayer.play();
                    onSongStart(file);
                }
            }

            currentPlayingFile = file;

        } catch (Exception x) {
            LOG.error("Error in jukebox: " + x, x);
            IOUtils.closeQuietly(in);
        }
    }

    public synchronized void stateChanged(AudioPlayer audioPlayer, AudioPlayer.State state) {
        if (state == EOM) {
            player.getPlaylist().next();
            play(player.getPlaylist().getCurrentFile(), 0);
        }
    }

    public synchronized float getGain() {
        return gain;
    }

    public synchronized int getPosition() {
    	if(this.currentPlayingFile != null && this.currentPlayingFile.isSpotify()) {
    		return MediaPlayer.getInstance().getPosition() / 1000;
    	}
        return audioPlayer == null ? 0 : offset + audioPlayer.getPosition();
    }

    /**
     * Returns the player which currently uses the jukebox.
     *
     * @return The player, may be {@code null}.
     */
    public Player getPlayer() {
        return player;
    }

    private void onSongStart(MediaFile file) {
        LOG.info(player.getUsername() + " starting jukebox for \"" + file.getName() + "\"");
        status = statusService.createStreamStatus(player);
        status.setMediaFileUri(file.getUri());
        status.setFile(file.getName());
        status.addBytesTransfered(file.length());
        scrobble(file, false);
    }

    private void onSongEnd(MediaFile file) {
        LOG.info(player.getUsername() + " stopping jukebox for \"" + file.getName() + "\"");
        if (status != null) {
            statusService.removeStreamStatus(status);
        }
        scrobble(file, true);
    }

    private void scrobble(MediaFile file, boolean submission) {
        if (player.getClientId() == null) {  // Don't scrobble REST players.
            audioScrobblerService.scrobble(player.getUsername(), file, submission);
        }
    }

    public synchronized void setGain(float gain) {
        this.gain = gain;
        if (audioPlayer != null) {
            audioPlayer.setGain(gain);
        }
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

	public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
		this.audioScrobblerService = audioScrobblerService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setSpotifyService(SpotifyService spotifyService) {
		this.spotifyService = spotifyService;
	}

	@Override
	public void trackStarted(Link link) {
		// NOOP
		LOG.debug("Track started: " + link.toString());
	}

	@Override
	public void trackEnded(Link link, boolean forcedEnd) {
		LOG.debug("Track ended: " + link.toString());
		stateChanged(audioPlayer, EOM);
	}

	@Override
	public Link nextTrackToPreload() {
		
		
		Playlist playlist = player.getPlaylist();
		int nextIndex = playlist.getIndex() + 1;
		MediaFile mediaFile = playlist.getFile(nextIndex);
		if(mediaFile.isSpotify()) {
			LOG.debug("Requesting preload of: " + mediaFile.getName());
			return URIUtil.getSpotifyLink(mediaFile.getName());
		}
		return null;
	}

	@Override
	public void playTokenLost() {
		// NOOP

	}

	@Override
	public void setAudioFormat(int rate, int channels) {
		// NOOP

	}

	@Override
	public int addToBuffer(byte[] buffer) {
		// NOOP
		return 0;
	}
}
