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
package net.sourceforge.subsonic.validator;

import java.io.File;

import net.sourceforge.subsonic.command.SavePlaylistCommand;
import net.sourceforge.subsonic.controller.SavePlaylistController;
import net.sourceforge.subsonic.service.PlaylistService;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for {@link SavePlaylistController}.
 *
 * @author Sindre Mehus
 */
public class SavePlaylistValidator implements Validator {
    private PlaylistService playlistService;

    public boolean supports(Class clazz) {
        return clazz.equals(SavePlaylistCommand.class);
    }

    public void validate(Object obj, Errors errors) {
        File playlistDirectory = playlistService.getPlaylistDirectory();
        if (!playlistDirectory.exists()) {
            errors.rejectValue("name", "playlist.save.missing_folder", new Object[] {playlistDirectory.getPath()}, null);
        }

        String name = ((SavePlaylistCommand) obj).getName();
        if (name == null || name.trim().length() == 0) {
            errors.rejectValue("name", "playlist.save.noname");
        }
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}
