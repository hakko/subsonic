package net.sourceforge.subsonic.controller;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.command.EditArtistCommand;
import net.sourceforge.subsonic.util.Util;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.util.URIUtil;
import com.github.hakko.musiccabinet.domain.model.music.ArtistInfo;
import com.github.hakko.musiccabinet.service.lastfm.ArtistInfoService;

/**
 * Controller for editing artists.
 *
 * @author hakko / MusicCabinet
 */
public class EditArtistController extends SimpleFormController {

    private ArtistInfoService artistInfoService;

    private static final Logger LOG = Logger.getLogger(EditArtistController.class);
    
    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        Uri uri = URIUtil.parseURI(request.getParameter("id"));
        String artist = request.getParameter("artist");
        
    	ArtistInfo artistInfo = Util.square(artistInfoService.getArtistInfo(uri));

    	EditArtistCommand command = new EditArtistCommand();

    	command.setUri(uri);
    	command.setArtist(artist);
    	command.setArtistInfo(artistInfo);
        
        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        EditArtistCommand command = (EditArtistCommand) comm;

        LOG.debug("id = " + command.getUri() + ", bio = " + command.getBioSummary());
        
        artistInfoService.setBioSummary(command.getUri(), command.getBioSummary());
        
        command.getArtistInfo().setBioSummary(command.getBioSummary());
    }
    
    // Spring setters

    public void setArtistInfoService(ArtistInfoService artistInfoService) {
    	this.artistInfoService = artistInfoService;
    }
    
}