package net.sourceforge.subsonic.controller;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.command.EditArtistCommand;
import net.sourceforge.subsonic.util.Util;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.service.ArtistInfoService;

/**
 * Controller for editing artists.
 *
 * @author hakko / MusicCabinet
 */
public class EditArtistController extends SimpleFormController {

    private ArtistInfoService artistInfoService;

    private static final Logger LOG = Logger.getLogger(EditArtistController.class);
    
    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        String path = request.getParameter("path");
        String artist = request.getParameter("artist");
        
        LOG.debug("got path " + path);
        
    	ArtistInfo artistInfo = Util.square(artistInfoService.getArtistInfo(path));

    	EditArtistCommand command = new EditArtistCommand();

    	command.setPath(path);
    	command.setArtist(artist);
    	command.setArtistInfo(artistInfo);
        
        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        EditArtistCommand command = (EditArtistCommand) comm;

        LOG.debug("path = " + command.getPath() + ", bio = " + command.getBioSummary());
        
        artistInfoService.setBioSummary(command.getPath(), command.getBioSummary());
        
        command.getArtistInfo().setBioSummary(command.getBioSummary());
    }
    
    // Spring setters

    public void setArtistInfoService(ArtistInfoService artistInfoService) {
    	this.artistInfoService = artistInfoService;
    }
    
}