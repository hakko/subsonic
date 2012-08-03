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
package net.sourceforge.subsonic.dao.schema;

public class Schema46MusicCabinet0_6_01 {

	// This is just a reminder. As of MusicCabinet 0.6.1, the Subsonic tradition
	// of storing password in clear-text is broken. From now on, they are stored
	// as salted hash sums.
	
	// To keep track of whether the password column has been updated, table Version
	// is simultaneously updated to version 21. @see SecurityService.
	
	// Further schema updates should start at version 22.
	
	// This class should not be added to schemas.
	
}