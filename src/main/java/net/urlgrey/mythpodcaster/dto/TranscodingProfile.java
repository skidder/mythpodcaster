/*
 * TranscodingProfile.java
 *
 * Created: Feb 17, 2010
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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

/**
 * @author scottkidder
 *
 */
@XmlRootElement(name="transcoding-profile")
@XmlAccessorType(XmlAccessType.FIELD)
public class TranscodingProfile implements Comparable<TranscodingProfile> {

	private static final Logger LOGGER = Logger.getLogger(TranscodingProfile.class);
	
	private static final String PATH_SEPARATOR = "/";

	public enum TranscoderType{ ONE_PASS, ONE_PASS_FAST_START, TWO_PASS, TWO_PASS_FAST_START, HTTP_SEGMENTED_VOD, USER_DEFINED }

	private String id;
	private String displayName;
	private List<TranscoderConfigurationItem> transcoderConfigurationItems = new ArrayList<TranscoderConfigurationItem>();
	private TranscoderType mode;
	private String encodingFileExtension;
	private String encodingMimeType;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public List<TranscoderConfigurationItem> getTranscoderConfigurationItems() {
		return transcoderConfigurationItems;
	}
	public void setTranscoderConfigurationItems(
			List<TranscoderConfigurationItem> transcoderConfigurationItems) {
		this.transcoderConfigurationItems = transcoderConfigurationItems;
	}	
	public TranscoderType getMode() {
		return mode;
	}
	public void setMode(TranscoderType mode) {
		this.mode = mode;
	}
	public String getEncodingFileExtension() {
		return encodingFileExtension;
	}
	public void setEncodingFileExtension(String encodingFileExtension) {
		this.encodingFileExtension = encodingFileExtension;
	}
	public String getEncodingMimeType() {
		return encodingMimeType;
	}
	public void setEncodingMimeType(String encodingMimeType) {
		this.encodingMimeType = encodingMimeType;
	}
	
	@Override
	public int compareTo(TranscodingProfile o) {
		return this.getDisplayName().compareTo(o.getDisplayName());
	}

	public File generateOutputFilePath(String feedFilePath, String programKey) {
		final File outputFile;
		
		final File encodingDirectory = new File(feedFilePath, this.id);
		encodingDirectory.mkdirs();

		switch (mode) {
		case ONE_PASS:
		case ONE_PASS_FAST_START:
		case TWO_PASS:
		case TWO_PASS_FAST_START:
		case USER_DEFINED:
			outputFile = new File(encodingDirectory, programKey + this.encodingFileExtension);
			break;
		case HTTP_SEGMENTED_VOD:
			SegmenterTranscoderConfigurationItem segmenterConfig = (SegmenterTranscoderConfigurationItem) this.transcoderConfigurationItems.get(1);

			File outputDirectory = new File(encodingDirectory, programKey);
			outputDirectory.mkdirs();
			outputFile = new File(outputDirectory, segmenterConfig.getPlaylistFileName());
			break;
		default:
			outputFile = null;
		}

		return outputFile;
	}
	
	/**
	 * 
	 * @param applicationURL
	 * @param outputFile
	 * @return
	 */
	public String generateOutputFileURL(URL applicationURL, File outputFile) {
		final String link;

		if (mode == TranscoderType.HTTP_SEGMENTED_VOD) {
			link = applicationURL.toExternalForm() + PATH_SEPARATOR + this.id + PATH_SEPARATOR + outputFile.getParentFile().getName() + PATH_SEPARATOR + outputFile.getName();
		} else {
			link = applicationURL.toExternalForm() + PATH_SEPARATOR + this.id + PATH_SEPARATOR + outputFile.getName();
		}
		
		return link;
	}

	/**
	 * @param feedFilePath
	 * @param url
	 * @param uid
	 */
	public void deleteEncoding(String feedFilePath, String url, String uid) {
		if (mode == TranscoderType.HTTP_SEGMENTED_VOD) {
			final File encodingDir = new File(feedFilePath, this.id);
			final File entryDir = new File(encodingDir, uid);
			
			for (File child : entryDir.listFiles()) {
				child.delete();
			}
			entryDir.delete();
			LOGGER.debug("Deleted encoding directory: " + entryDir.getPath());
		} else {
			final String encodingFileName = url.substring(url.lastIndexOf(PATH_SEPARATOR)+1);
			final File encodingDir = new File(feedFilePath, this.id);
			final File encodingFile = new File(encodingDir, encodingFileName);
			encodingFile.delete();
			LOGGER.debug("Deleted encoding file: " + encodingFile.getPath());
		}
	}	
}
