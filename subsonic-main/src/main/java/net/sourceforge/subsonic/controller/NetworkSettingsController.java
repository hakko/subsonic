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

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.subsonic.command.NetworkSettingsCommand;
import net.sourceforge.subsonic.service.NetworkService;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Controller for the page used to change the network settings.
 *
 * @author Sindre Mehus
 */
public class NetworkSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private NetworkService networkService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        NetworkSettingsCommand command = new NetworkSettingsCommand();
        command.setPortForwardingEnabled(settingsService.isPortForwardingEnabled());
        command.setUrlRedirectionEnabled(settingsService.isUrlRedirectionEnabled());
        command.setUrlRedirectFrom(settingsService.getUrlRedirectFrom());
        command.setPort(settingsService.getPort());

        return command;
    }

    protected void doSubmitAction(Object cmd) throws Exception {
        NetworkSettingsCommand command = (NetworkSettingsCommand) cmd;

        settingsService.setPortForwardingEnabled(command.isPortForwardingEnabled());
        settingsService.setUrlRedirectionEnabled(command.isUrlRedirectionEnabled());
        settingsService.setUrlRedirectFrom(StringUtils.lowerCase(command.getUrlRedirectFrom()));

        if (settingsService.getServerId() == null) {
            Random rand = new Random(System.currentTimeMillis());
            settingsService.setServerId(String.valueOf(Math.abs(rand.nextLong())));
        }

        settingsService.save();
        networkService.initPortForwarding();
        networkService.initUrlRedirection(true);
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }
}