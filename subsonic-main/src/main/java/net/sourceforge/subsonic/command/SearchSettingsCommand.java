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
package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.SearchSettingsController;

/**
 * Command used in {@link SearchSettingsController}.
 *
 * @author Sindre Mehus
 */
public class SearchSettingsCommand {
    private String interval;
    private String hour;
    private boolean isCreatingIndex;
    private boolean isDatabaseAvailable;
    private String brand;

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean isCreatingIndex() {
        return isCreatingIndex;
    }

    public void setCreatingIndex(boolean creatingIndex) {
        isCreatingIndex = creatingIndex;
    }

	public boolean isDatabaseAvailable() {
		return isDatabaseAvailable;
	}

	public void setDatabaseAvailable(boolean isDatabaseAvailable) {
		this.isDatabaseAvailable = isDatabaseAvailable;
	}

	public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
   
}