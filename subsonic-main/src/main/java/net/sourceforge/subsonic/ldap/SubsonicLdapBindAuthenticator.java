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
package net.sourceforge.subsonic.ldap;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

/**
 * LDAP authenticator which uses a delegate {@link BindAuthenticator}, and which
 * supports dynamically changing LDAP provider URL and search filter.
 *
 * @author Sindre Mehus
 */
public class SubsonicLdapBindAuthenticator implements LdapAuthenticator {

    private static final Logger LOG = Logger.getLogger(SubsonicLdapBindAuthenticator.class);

    private SecurityService securityService;
    private SettingsService settingsService;

    private long authenticatorTimestamp;
    private BindAuthenticator delegateAuthenticator;

    public DirContextOperations authenticate(Authentication authentication) {

        // LDAP authentication must be enabled on the system.
        if (!settingsService.isLdapEnabled()) {
            throw new BadCredentialsException("LDAP authentication disabled.");
        }

        String username = authentication.getName();
        LOG.info("Authentication principal: " + username);
        
        // User must be defined in Subsonic, unless auto-shadowing is enabled.
        User user = securityService.getUserByName(username);
        if (user == null && !settingsService.isLdapAutoShadowing()) {
            throw new BadCredentialsException("User does not exist.");
        }

        // LDAP authentication must be enabled for the given user.
        if (user != null && !user.isLdapAuthenticated()) {
            throw new BadCredentialsException("LDAP authentication disabled for user.");
        }

        try {
            createDelegate();
            DirContextOperations contextOperations = delegateAuthenticator.authenticate(authentication);
            if (contextOperations != null) {
                LOG.info("User '" + username + "' successfully authenticated in LDAP. DN: " + contextOperations.getDn());

                if (user == null) {
                    User newUser = new User(username, "", null, true, 0L, 0L, 0L);
                    newUser.setStreamRole(true);
                    newUser.setSettingsRole(true);
                    securityService.createUser(newUser);
                    LOG.info("Created local user '" + username + "' for DN " + contextOperations.getDn());
                }
            }

            return contextOperations;
        } catch (RuntimeException x) {
            LOG.info("Failed to authenticate user '" + username + "' in LDAP.", x);
            throw x;
        }
    }

    /**
     * Creates the delegate {@link BindAuthenticator}.
     */
    private synchronized void createDelegate() {

        // Only create it if necessary.
        if (delegateAuthenticator == null || authenticatorTimestamp < settingsService.getSettingsChanged()) {
        	
        	LdapContextSource contextSource = new LdapContextSource();
        	contextSource.setReferral("follow");
        	contextSource.setUrl(settingsService.getLdapUrl());

            String managerDn = settingsService.getLdapManagerDn();
            String managerPassword = settingsService.getLdapManagerPassword();
            if (StringUtils.isNotEmpty(managerDn) && StringUtils.isNotEmpty(managerPassword)) {
            	contextSource.setUserDn(managerDn);
            	contextSource.setPassword(managerPassword);
            }

            FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch("", settingsService.getLdapSearchFilter(), contextSource);
            userSearch.setSearchSubtree(true);
            userSearch.setDerefLinkFlag(true);

            delegateAuthenticator = new BindAuthenticator(contextSource);
            delegateAuthenticator.setUserSearch(userSearch);

            authenticatorTimestamp = settingsService.getSettingsChanged();
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

}
