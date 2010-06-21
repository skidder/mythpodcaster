/*
 * SegmenterTranscoderConfigurationItem.java
 *
 * Created: Feb 19, 2010
 *
 * Copyright (C) 2010 Scott Kidder
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
package net.urlgrey.mythpodcaster.dto;


/**
 * @author scottkidder
 *
 */
public class SegmenterTranscoderConfigurationItem extends TranscoderConfigurationItem {

	private String segmentDuration;
	private String segmentFilePrefix;
	private String playlistFileName;
	private String httpPrefix;

	public String getSegmentDuration() {
		return segmentDuration;
	}
	public void setSegmentDuration(String segmentDuration) {
		this.segmentDuration = segmentDuration;
	}
	public String getSegmentFilePrefix() {
		return segmentFilePrefix;
	}
	public void setSegmentFilePrefix(String segmentFilePrefix) {
		this.segmentFilePrefix = segmentFilePrefix;
	}
	public String getPlaylistFileName() {
		return playlistFileName;
	}
	public void setPlaylistFileName(String playlistFileName) {
		this.playlistFileName = playlistFileName;
	}
	public String getHttpPrefix() {
		return httpPrefix;
	}
	public void setHttpPrefix(String httpPrefix) {
		this.httpPrefix = httpPrefix;
	}
}
