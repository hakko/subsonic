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

import java.util.Collection;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * An {@link LdapAuthoritiesPopulator} that retrieves the roles from the
 * database using the {@link UserDetailsService} instead of retrieving the roles
 * from LDAP. An instance of this class can be configured for the
 * {@link LdapAuthenticationProvider} when
 * authentication should be done using LDAP and authorization using the
 * information stored in the database.
 *
 * @author Thomas M. Hofmann
 */
public class UserDetailsServiceBasedAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private UserDetailsService userDetailsService;

	public Collection<? extends GrantedAuthority> getGrantedAuthorities(
			DirContextOperations userData, String username) {
        UserDetails details = userDetailsService.loadUserByUsername(username);
        return details.getAuthorities();
	}

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

}