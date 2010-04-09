/*
 * ClipLocator.java
 *
 * Created: Oct 9, 2009 10:19:23 AM
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

/**
 * @author scott
 *
 */
public interface ClipLocator {

	/**
	 * @param filename
	 * @return
	 */
	File locateOriginalClip(String filename);

	/**
	 * 
	 * @param filename
	 * @return
	 */
	File locateThumbnailForOriginalClip(String filename);
}
