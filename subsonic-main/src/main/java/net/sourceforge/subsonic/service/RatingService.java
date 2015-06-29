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

import java.util.List;

import com.github.hakko.musiccabinet.configuration.Uri;
import com.github.hakko.musiccabinet.dao.RatingDao;
import com.github.hakko.musiccabinet.domain.model.music.Album;

/**
 * Provides services for user ratings.
 *
 * @author Sindre Mehus
 */
public class RatingService {

	private RatingDao ratingDao;

	/**
	 * Returns the highest rated albums.
	 *
	 * @param offset
	 *            Number of albums to skip.
	 * @param count
	 *            Maximum number of albums to return.
	 * @param musicFolders
	 *            Only return albums in these folders.
	 * @return The highest rated albums.
	 */
	public List<Album> getHighestRatedAlbums(int offset, int count) {
		return ratingDao.getHighestRatedAlbums(offset, count);
	}

	/**
	 * Sets the rating for a music file and a given user.
	 *
	 * @param username
	 *            The user name.
	 * @param mediaFile
	 *            The music file.
	 * @param rating
	 *            The rating between 1 and 5, or <code>null</code> to remove the
	 *            rating.
	 */
	public void setRatingForUser(String username, Uri uri, Integer rating) {
		ratingDao.setRatingForUser(username, uri, rating);
	}

	/**
	 * Returns the average rating for the given music file.
	 *
	 * @param mediaFile
	 *            The music file.
	 * @return The average rating, or <code>null</code> if no ratings are set.
	 */
	public Double getAverageRating(Uri uri) {
		return ratingDao.getAverageRating(uri);
	}

	/**
	 * Returns the rating for the given user and music file.
	 *
	 * @param username
	 *            The user name.
	 * @param mediaFile
	 *            The music file.
	 * @return The rating, or <code>null</code> if no rating is set.
	 */
	public Integer getRatingForUser(String username, Uri uri) {
		return ratingDao.getRatingForUser(username, uri);
	}

	public int getRatedAlbumCount(String username) {
		return ratingDao.getRatedAlbumCount(username);
	}

	public void setRatingDao(RatingDao ratingDao) {
		this.ratingDao = ratingDao;
	}

}
