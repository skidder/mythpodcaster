/*
 * TranscodeAndCleanupJob.java
 *
 * Created: Oct 8, 2009 12:41:45 PM
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.urlgrey.mythpodcaster.dao.MythRecordingsDAO;
import net.urlgrey.mythpodcaster.dao.SubscriptionsDAO;
import net.urlgrey.mythpodcaster.domain.Channel;
import net.urlgrey.mythpodcaster.domain.RecordedProgram;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;
import net.urlgrey.mythpodcaster.dto.FeedSubscriptionItem;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author scott
 *
 */
public class TranscodeAndCleanupJob {

	private static final Logger LOGGER = Logger.getLogger(TranscodeAndCleanupJob.class);
	private SubscriptionsDAO subscriptionsDao;
	private MythRecordingsDAO recordingsDao;
	private FeedFileAccessor feedFileAccessor;
	private String feedFilePath;
	private Comparator<SyndEntry> entryComparator = new FeedEntryComparator();
	private String feedFileExtension;


	public void execute() {
		LOGGER.debug("Job process starting");

		try {
			doWork();
		} catch (Exception e) {
			LOGGER.error("Error occurred during job", e);
		}
		LOGGER.debug("Job process finished");
	}

	private void doWork() {
		final List <FeedSubscriptionItem> purgeList = new ArrayList<FeedSubscriptionItem>();

		// retrieve series subscriptions
		final List<FeedSubscriptionItem> subscriptions = subscriptionsDao.findSubscriptions();

		// iterate over each series
		for (FeedSubscriptionItem subscription : subscriptions) {
			LOGGER.debug("Processing feed subscription for recordId[" + subscription.getSeriesId() + "]");
			boolean feedUpdated = false;

			// parse the XML-encoded RSS Feed into a Java object
			SyndFeed feed;
			try {
				feed = feedFileAccessor.readFeed(subscription.getSeriesId(), subscription.getTitle());
			} catch (IOException e) {
				LOGGER.error("Continuing, error reading feed for recordId[" + subscription.getSeriesId() + "]", e);
				continue;
			}
			final RecordedSeries seriesInfo = recordingsDao.findRecordedSeries(subscription.getSeriesId());

			if (seriesInfo == null) {
				// if not occurences of the recorded series are found, then continue
				LOGGER.debug("No recordings found for recordId[" + subscription.getSeriesId() + "]");
			}

			if (subscription.isActive() == false) {
				// if the series is inactive, then delete the feed, it's transcoded files, and the subscription entry
				feedFileAccessor.purgeFeed(subscription.getSeriesId(), feed);
				purgeList.add(subscription);
			} else {
				// identify series recordings not represented in the RSS Feed (transcode)
				final List entries = feed.getEntries();
				if (seriesInfo != null) {
				    for (RecordedProgram program : seriesInfo.getRecordedPrograms()) {
						if (program.getEndTime() == null || program.getEndTime().after(new Date())) {
						    LOGGER.debug("Skipping recorded program, end-time is in future (still recording): programId[" + program.getProgramId() + "]");
						    continue;
						}
						
						boolean found = false;
						LOGGER.debug("Locating program in existing feed entries: programId[" + program.getProgramId() + "], key[" + program.getKey() + "]");
						if (entries != null && entries.size() > 0) {
							final Iterator it = entries.iterator();
							while (it.hasNext()) {
								final SyndEntry entry = (SyndEntry) it.next();
								if (entry.getUri() == null) {
									continue;
								}
		
								String entryKey = entry.getUri();
								if (program.getKey().equalsIgnoreCase(entryKey)) {
									found = true;
									break;
								}
							}
						}
	
						if (found) {
							LOGGER.debug("Program was found in feed, continuing");
						} else {
							final Channel channel = this.recordingsDao.findChannel(program.getChannelId());
							feedFileAccessor.addProgramToFeed(program, channel, feed, subscription.getTranscodeProfile());
							feedUpdated = true;
						}
				    }	
				}

				// identify RSS Feed entries no longer in the database (delete)
				if (entries != null && entries.size() > 0) {
					LOGGER.debug("Identifying series recordings no longer in database but still in feed, recordId[" + subscription.getSeriesId() + "]");
					Set <SyndEntry> entryRemovalSet = new HashSet<SyndEntry>();
					final Iterator it = entries.iterator();
					while (it.hasNext()) {
						final SyndEntry entry = (SyndEntry) it.next();
						if (entry.getUri() == null) {
							feedUpdated = true;
							entryRemovalSet.add(entry);
							LOGGER.debug("Feed entry has null URI (GUID), removing because it cannot be identified");
							continue;
						}
						
						// locate the feed entry in the list of recorded programs 
						String episodeKey = entry.getUri();
						boolean found = false;
						if (seriesInfo != null) {
						    for (RecordedProgram program : seriesInfo.getRecordedPrograms()) {
								if (program.getKey().equalsIgnoreCase(episodeKey)) {
									found = true;
									break;
								}
						    }
						}

						// if the feed entry is no longer in the list of recorded programs, then remove
						if (found) {
							LOGGER.debug("Feed entry is current, continuing: uid[" + entry.getUri() + "]");
						} else {
							LOGGER.debug("Feed entry is invalid, deleting: uid[" + entry.getUri() + "]");
							feedUpdated = true;
							entryRemovalSet.add(entry);

							final List enclosures = entry.getEnclosures();
							if (enclosures.size() > 0) {
								final SyndEnclosure enclosure = (SyndEnclosure) enclosures.get(0);

								// delete the file 
								String encodingFileName = enclosure.getUrl();
								encodingFileName = encodingFileName.substring(encodingFileName.lastIndexOf('/')+1);
								final File encodingFile = new File(this.feedFilePath, encodingFileName);
								if (encodingFile.canWrite()) {
									encodingFile.delete();
									LOGGER.debug("Deleted encoding file no longer found in MythTV Database: " + encodingFile.getPath());
								} else {
									LOGGER.debug("Unable to delete file, not found on filesystem: " + encodingFile.getAbsolutePath());
								}
							} else {
								LOGGER.info("No enclosures specified in the entry, removing from feed and continuing");
							}
						}
					}

					// remove all of the entries flagged for removal
					entries.removeAll(entryRemovalSet);
				}			

				if (feedUpdated) {
					// write the updated RSS feed for the series
					File feedFile = new File(feedFilePath, subscription.getSeriesId() + feedFileExtension);
					LOGGER.debug("Changes made to feed, updating feed file: path[" + feedFile.getAbsolutePath() + "]");

					// sort the feed entries by published-date
					Collections.sort(entries, entryComparator);
					
			         try {
			 			FileWriter writer = new FileWriter(feedFile); 
						SyndFeedOutput output = new SyndFeedOutput();
						output.output(feed, writer);
					} catch (IOException e) {
						LOGGER.error("Error rendering feed", e);
						if (feedFile.canWrite()) {
							feedFile.delete();
						}
					} catch (FeedException e) {
						LOGGER.error("Error rendering feed", e);
						if (feedFile.canWrite()) {
							feedFile.delete();
						}
					}
				}
			}
		}
		
		subscriptionsDao.purge(purgeList);
	}

	private class FeedEntryComparator implements Comparator<SyndEntry> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SyndEntry entry1, SyndEntry entry2) {
			if (entry1 == null || entry1.getPublishedDate() == null) {
				return 1;
			}

			if (entry2 == null) {
				return -1;
			}
			return ((-1) * entry1.getPublishedDate().compareTo(entry2.getPublishedDate()));
		}
	}

	@Required
	public void setSubscriptionsDao(SubscriptionsDAO subscriptionsDao) {
		this.subscriptionsDao = subscriptionsDao;
	}

	@Required
	public void setFeedFilePath(String feedFilePath) {
		this.feedFilePath = feedFilePath;
	}

	@Required
	public void setRecordingsDao(MythRecordingsDAO recordingsDao) {
		this.recordingsDao = recordingsDao;
	}

	@Required
	public void setFeedFileAccessor(FeedFileAccessor feedAccessor) {
		this.feedFileAccessor = feedAccessor;
	}

	@Required
	public void setFeedFileExtension(String feedFileExtension) {
		this.feedFileExtension = feedFileExtension;
	}

}
