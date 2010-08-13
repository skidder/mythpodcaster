/*
 * FeedFileAccessor.java
 *
 * Created: Oct 11, 2009 8:14:49 AM
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
import java.io.IOException;

import net.urlgrey.mythpodcaster.domain.Channel;
import net.urlgrey.mythpodcaster.domain.RecordedProgram;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * @author scott
 *
 */
public interface FeedFileAccessor {

	/**
	 * 
	 * @param feedFile
	 * @param seriesId
	 * @param title
	 * @param transcodingProfileId
	 * @return
	 */
	SyndFeed createFeed(File feedFile, String seriesId, String title, String transcodingProfileId);

	/**
	 * @param series
	 * @param program
	 * @param channel 
	 * @param feed
	 * @param transcoderProfile
	 */
	void addProgramToFeed(RecordedSeries series, RecordedProgram program, Channel channel, SyndFeed feed, String transcodingProfileId);

	/**
	 * @param seriesId
	 * @param transcodingProfileId
	 * @param feed 
	 */
	void purgeFeed(String seriesId, String transcodingProfileId, SyndFeed feed);

	/**
	 * @param seriesId
	 * @param transcodingProfileId
	 * @param title
	 * @throws IOException 
	 */
	SyndFeed readFeed(String seriesId, String transcodingProfileId, String title) throws IOException;

	/**
	 * 
	 * @param feedFile
	 * @param feed
	 * @param seriesId
	 * @return
	 * @throws IOException
	 */
	public File generateTransformationFromFeed(File feedFile, SyndFeed feed, String seriesId)
	throws IOException;
}
