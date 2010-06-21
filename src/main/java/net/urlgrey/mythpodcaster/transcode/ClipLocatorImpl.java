/*
 * ClipLocatorImpl.java
 *
 * Created: Oct 9, 2009 10:17:10 AM
 *
 * Copyright (C) 2009 Scott Kidder
 * 
 * This file is part of MythPodcaster
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.transcode;

import java.io.File;
import java.util.List;

import net.urlgrey.mythpodcaster.dao.MythRecordingsDAO;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author scott
 *
 */
public class ClipLocatorImpl implements ClipLocator {

	private static final String PNG_EXTENSION = ".png";
	private MythRecordingsDAO recordingsDao;

	/**
	 * @param filename
	 * @return
	 */
	public File locateOriginalClip(String filename) {

		final List<String> recordingDirectories = recordingsDao.findRecordingDirectories();
		if (recordingDirectories == null || recordingDirectories.size() == 0) {
			return null;
		}

		for (String directory : recordingDirectories) {
			File clipLocation = new File(directory, filename);
			if (clipLocation.canRead()) {
				return clipLocation;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.urlgrey.mythpodcaster.transcode.ClipLocator#locateThumbnailForOriginalClip(java.lang.String)
	 */
	@Override
	public File locateThumbnailForOriginalClip(String filename) {
		final File clipFile = this.locateOriginalClip(filename);
		if (clipFile != null) {
			final File clipThumbnailFile = new File(clipFile.getParentFile(), clipFile.getName() + PNG_EXTENSION);
			if (clipThumbnailFile.canRead()) {
				return clipThumbnailFile;
			}
		}

		return null;
	}

	@Required
	public void setRecordingsDao(MythRecordingsDAO recordingsDao) {
		this.recordingsDao = recordingsDao;
	}
}
