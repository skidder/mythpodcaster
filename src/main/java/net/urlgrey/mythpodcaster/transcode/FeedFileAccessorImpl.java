/*
 * FeedFileAccessorImpl.java
 *
 * Created: Oct 11, 2009 8:21:23 AM
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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.urlgrey.mythpodcaster.dao.TranscodingProfilesDAO;
import net.urlgrey.mythpodcaster.domain.Channel;
import net.urlgrey.mythpodcaster.domain.RecordedProgram;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;
import net.urlgrey.mythpodcaster.dto.TranscodingProfile;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.EntryInformationImpl;
import com.sun.syndication.feed.module.itunes.FeedInformation;
import com.sun.syndication.feed.module.itunes.FeedInformationImpl;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author scott
 *
 */
public class FeedFileAccessorImpl implements FeedFileAccessor {

	private static final String PNG_EXTENSION = ".png";

	private static final String PATH_SEPARATOR = "/";

	static final Logger LOGGER = Logger.getLogger(FeedFileAccessorImpl.class);

	private String feedFilePath;
	private URL applicationURL;
	private ClipLocator clipLocator;
	private String feedFileExtension;
	private TranscodingController transcodingController;
	private TranscodingProfilesDAO transcodingProfilesDao;

	/**
	 * @param feedFile
	 * @param seriesId
	 * @param title
	 * @param transcodingProfileId 
	 * @return 
	 */
	public SyndFeed createFeed(File feedFile, String seriesId,
			String title, String transcodingProfileId) {
		final SyndFeed defaultFeed = new SyndFeedImpl();
		defaultFeed.setFeedType("rss_2.0");
		defaultFeed.setTitle(title);
		defaultFeed.setLink(this.applicationURL + PATH_SEPARATOR + transcodingProfileId + PATH_SEPARATOR + seriesId + feedFileExtension);
		defaultFeed.setDescription("Feed for the MythTV recordings of this program");

		try {
			final File feedDirectory = feedFile.getParentFile();
			if (!feedDirectory.exists()) {
				feedDirectory.mkdirs();
				LOGGER.info("Created feed directory: " + feedDirectory.getPath());
			}

			FileWriter writer = new FileWriter(feedFile);
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(defaultFeed, writer);
			return defaultFeed;
		} catch (IOException e) {
			LOGGER.error("Error rendering feed", e);
			if (feedFile.canWrite()) {
				feedFile.delete();
			}
			return null;
		} catch (FeedException e) {
			LOGGER.error("Error rendering feed", e);
			if (feedFile.canWrite()) {
				feedFile.delete();
			}
			return null;
		}
	}

	/**
	 * @param seriesId
	 * @param transcodingProfileId
	 * @param feed 
	 */
	public void purgeFeed(String seriesId, String transcodingProfileId, SyndFeed feed) {
		final File encodingDirectory = new File(feedFilePath, transcodingProfileId);

		// delete feed
		final File feedFile = new File(encodingDirectory, seriesId + feedFileExtension);
		if (feedFile.canWrite()) {
			feedFile.delete();
		}

		// delete feed thumbnail
		final File feedThumbnail = new File(encodingDirectory, seriesId + PNG_EXTENSION);
		if (feedThumbnail.canWrite()) {
			feedThumbnail.delete();
		}

		if (feed != null) {
			final Iterator it = feed.getEntries().iterator();
			while (it.hasNext()) {
				SyndEntry entry = (SyndEntry) it.next();
				final List enclosures = entry.getEnclosures();
				if (enclosures.size() > 0) {
					Iterator enclosureIterator = enclosures.iterator();
					while (enclosureIterator.hasNext())
					{
						SyndEnclosure enclosure = (SyndEnclosure) enclosureIterator.next();
						String encUrl = enclosure.getUrl();
						encUrl = encUrl.substring(encUrl.lastIndexOf('/')+1);

						final TranscodingProfile profile = transcodingProfilesDao.findAllProfiles().get(transcodingProfileId);
						profile.deleteEncoding(this.feedFilePath, enclosure.getUrl(), entry.getUri());
					}
				} else {
					LOGGER.debug("No enclosures specified in the entry, continuing");
					continue;
				}
			}
		}

		// delete the encoding profile directory if it is empty following the purge
		if (encodingDirectory.list().length == 0) {
			LOGGER.info("Deleting empty encoding profile directory: " + encodingDirectory.getAbsolutePath());
			encodingDirectory.delete();
		}
	}

	/**
	 * @param seriesId
	 * @throws IOException 
	 */
	public SyndFeed readFeed(String seriesId, String transcodingProfileId, String title) throws IOException {
		File encodingDirectory = new File(feedFilePath, transcodingProfileId);
		File feedFile = new File(encodingDirectory, seriesId + feedFileExtension);
		if (feedFile.exists() == false) {
			SyndFeed feed = this.createFeed(feedFile, seriesId, title, transcodingProfileId);
			if (feed == null) {
				throw new IOException("Unable to create feed for new subscription");
			}

			return feed;
		}

		SyndFeedInput input = new SyndFeedInput();
		try {
			return input.build(feedFile);
		} catch (IOException e) {
			LOGGER.error("Error rendering feed", e);
			feedFile.delete();
			throw new IOException("Unable to render feed");
		} catch (FeedException e) {
			LOGGER.error("Error rendering feed", e);
			feedFile.delete();
			throw new IOException("Unable to render feed");
		}
	}

	/**
	 * @param series
	 * @param program
	 * @param channel 
	 * @param feed
	 * @param transcoderProfile
	 */
	public void addProgramToFeed(RecordedSeries series, RecordedProgram program, Channel channel, SyndFeed feed, String transcodingProfileId) {
		LOGGER.info("Transcoding new feed entry: programId[" + program.getProgramId() + "], key[" +  program.getKey() + "], title[" + program.getTitle() + "], channel[" + (channel != null ? channel.getName() : "") + "], transcodingProfileId[" + transcodingProfileId + "]");
		final SyndEntryImpl entry = new SyndEntryImpl();
		entry.setUri(program.getKey());
		entry.setPublishedDate(program.getRecordedProgramKey().getStartTime());

		final EntryInformation itunesEntryMetadata = new EntryInformationImpl();
		itunesEntryMetadata.setDuration(new Duration(program.getEndTime().getTime() - program.getRecordedProgramKey().getStartTime().getTime()));
		itunesEntryMetadata.setSummary(program.getDescription());

		// set author info from the channel if available
		if (channel != null) {
			entry.setAuthor(channel.getName());
			itunesEntryMetadata.setAuthor(channel.getName());
		}

		// Use the sub-title if no title can be found for the program
		if (program.getSubtitle() != null && program.getSubtitle().trim().length() > 0) {
			entry.setTitle(program.getSubtitle());
		} else {
			entry.setTitle(program.getTitle());
		}

		final SyndContentImpl description = new SyndContentImpl();
		description.setType("text/plain");
		description.setValue(program.getDescription());
		entry.setDescription(description);

		// apply thumbnail for clip to the feed
		final File originalClipThumbnail = clipLocator.locateThumbnailForOriginalClip(program.getFilename());
		if (originalClipThumbnail != null) {
			final String seriesId = series.getSeriesId();
			final File encodingDirectory = new File(feedFilePath, transcodingProfileId);
			final File feedThumbnailFile = new File(encodingDirectory, seriesId + PNG_EXTENSION);

			try {
				FileOperations.copy(originalClipThumbnail, feedThumbnailFile);

				final SyndImageImpl feedImage = new SyndImageImpl();
				final String feedImageUrl = this.applicationURL + PATH_SEPARATOR + transcodingProfileId + PATH_SEPARATOR + seriesId + PNG_EXTENSION;
				feedImage.setUrl(feedImageUrl);
				feedImage.setTitle(series.getTitle());
				feed.setImage(feedImage);

				// include iTunes-specific metadata
				final Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
				final FeedInformation itunesFeedMetadata;
				if (module == null) {
					itunesFeedMetadata = new FeedInformationImpl();
					feed.getModules().add(itunesFeedMetadata);
				} else {
					itunesFeedMetadata = (FeedInformation) module;
				}

				itunesFeedMetadata.setImage(new URL(feedImageUrl));

				LOGGER.info("Applied clip thumbnail to feed: thumbnail[" + feedThumbnailFile.getAbsolutePath() + "], url[" + feedImage.getUrl() + "]");
			} catch (IOException e) {
				if (feedThumbnailFile.canWrite()) {
					feedThumbnailFile.delete();
					feed.setImage(null);
				}
			}
		}

		// transcode
		final File originalClip = clipLocator.locateOriginalClip(program.getFilename());
		if (originalClip !=null) {
			final TranscodingProfile profile = transcodingProfilesDao.findAllProfiles().get(transcodingProfileId);
			if (profile != null) {

				final File outputFile = profile.generateOutputFilePath(feedFilePath, program.getKey());
				try {

					LOGGER.info("Transcode STARTING: profile[" + profile.getId() + "]");
					transcodingController.transcode(profile, originalClip, outputFile);
					LOGGER.info("Transcode FINISHED: profile[" + profile.getId() + "]");

					if (outputFile.canRead()) {
						// get the file-size in bytes
						FileInputStream in = new FileInputStream(outputFile);
						final int fileSize = in.available();
						in.close();

						final String link = profile.generateOutputFileURL(this.applicationURL, outputFile);						
						final SyndEnclosure enclosure = new SyndEnclosureImpl();
						enclosure.setUrl(link);
						enclosure.setType(profile.getEncodingMimeType());
						enclosure.setLength(fileSize);
						final List enclosures = new ArrayList();
						enclosures.add(enclosure);
						entry.setEnclosures(enclosures);

						// include iTunes-specific metadata
						final Module module = entry.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
						if (module != null) {
							entry.getModules().remove(module);
						}
						entry.getModules().add(itunesEntryMetadata);
					} else {
						LOGGER.warn("Transcoded output file cannot be read, setting link to null: path[" + outputFile.getAbsolutePath() + "]");
						entry.setLink(null);
					}
				} catch (Exception e) {
					LOGGER.error("Error while transcoding, setting link to null", e);
					entry.setLink(null);
					if (outputFile != null && outputFile.canWrite()) {
						outputFile.delete();
					}
				}
			} else {
				final String msg = "Unable to locate transcoding profile with given id: ["
					+ transcodingProfileId + "]";
				LOGGER.error(msg);
			}
		} else {
			if (originalClip != null) {
				LOGGER.warn("Original clip does not exist or cannot be read: " + originalClip.getAbsolutePath());
			} else {
				LOGGER.warn("Original clip could not be found in content paths");
			}
			entry.setLink(null);
		}

		feed.getEntries().add(entry);
	}

	@Required
	public void setApplicationURL(URL applicationURL) {
		this.applicationURL = applicationURL;
	}

	@Required
	public void setFeedFilePath(String feedFilePath) {
		this.feedFilePath = feedFilePath;
	}

	@Required
	public void setClipLocator(ClipLocator clipLocator) {
		this.clipLocator = clipLocator;
	}

	@Required
	public void setFeedFileExtension(String feedFileExtension) {
		this.feedFileExtension = feedFileExtension;
	}

	@Required
	public void setTranscodingController(TranscodingController transcodingController) {
		this.transcodingController = transcodingController;
	}

	@Required
	public void setTranscodingProfilesDao(
			TranscodingProfilesDAO transcodingProfilesDao) {
		this.transcodingProfilesDao = transcodingProfilesDao;
	}
}
