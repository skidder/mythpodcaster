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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.urlgrey.mythpodcaster.dao.SubscriptionsDAO;
import net.urlgrey.mythpodcaster.dto.FeedSubscriptionItem;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * @author scott
 *
 */
public class TranscodeAndCleanupJob implements ApplicationContextAware {

	private static final Logger LOGGER = Logger.getLogger(TranscodeAndCleanupJob.class);
	private SubscriptionsDAO subscriptionsDao;
	private FeedFileAccessor feedFileAccessor;
	private ThreadPoolTaskExecutor executor;
    private ApplicationContext applicationContext;
	private int threadCompletionPollingFrequency;


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
		List<FeedSubscriptionItem> subscriptions = subscriptionsDao.findSubscriptions();

		// iterate over each series
		for (FeedSubscriptionItem subscription : subscriptions) {
			// parse the XML-encoded RSS Feed into a Java object
			final SyndFeed feed;
			try {
				feed = feedFileAccessor.readFeed(subscription.getSeriesId(), subscription.getTranscodeProfile(), subscription.getTitle());
			} catch (IOException e) {
				LOGGER.error("Continuing, error reading feed for recordId[" + subscription.getSeriesId() + "]", e);
				continue;
			}

			if (subscription.isActive() == false) {
				// if the series is inactive, then delete the feed, it's transcoded files, and the subscription entry
				feedFileAccessor.purgeFeed(subscription.getSeriesId(), subscription.getTranscodeProfile(), feed);
				purgeList.add(subscription);
			}			
		}

		// purge those subscriptions marked for deletion
		subscriptionsDao.purge(purgeList);

		// refresh the list of subscriptions
		subscriptions = subscriptionsDao.findSubscriptions();

		// iterate over each series to identify those requiring transcoding
		for (FeedSubscriptionItem subscription : subscriptions) {
			LOGGER.debug("Processing feed subscription for recordId[" + subscription.getSeriesId() + "]");

			// parse the XML-encoded RSS Feed into a Java object
			final SyndFeed feed;
			try {
				feed = feedFileAccessor.readFeed(subscription.getSeriesId(), subscription.getTranscodeProfile(), subscription.getTitle());
			} catch (IOException e) {
				LOGGER.error("Continuing, error reading feed for recordId[" + subscription.getSeriesId() + "]", e);
				continue;
			}

            Runnable task = (Runnable) this.applicationContext.getBean("feedTranscodingTask", new Object[]{ subscription, feed });
            executor.execute(task);
		}
		
		while (executor.getActiveCount() > 0) {
			try {
				Thread.sleep(threadCompletionPollingFrequency * 1000);
			} catch (InterruptedException e) {
				LOGGER.warn("Thread was interrupted while waiting for thread-pool to finish work");
			}
		}
	}

	@Required
	public void setSubscriptionsDao(SubscriptionsDAO subscriptionsDao) {
		this.subscriptionsDao = subscriptionsDao;
	}

	@Required
	public void setFeedFileAccessor(FeedFileAccessor feedAccessor) {
		this.feedFileAccessor = feedAccessor;
	}

    @Required
    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Required
    public void setThreadCompletionPollingFrequency(
			int threadCompletionPollingFrequency) {
		this.threadCompletionPollingFrequency = threadCompletionPollingFrequency;
	}

	@Required
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}
