/*
 * SubscriptionsDAOImpl.java
 *
 * Created: Oct 7, 2009 7:16:28 PM
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
package net.urlgrey.mythpodcaster.dao;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.urlgrey.mythpodcaster.transcode.FeedFileAccessor;
import net.urlgrey.mythpodcaster.xml.FeedSubscriptionItem;
import net.urlgrey.mythpodcaster.xml.FeedSubscriptions;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * @author scott
 *
 */
public class SubscriptionsDAOImpl extends AbstractFileBasedDAO implements SubscriptionsDAO {

	protected static final Logger LOGGER = Logger.getLogger(SubscriptionsDAOImpl.class);

	private static final String RSS_FILE_EXTENSION = ".rss";

	private String subscriptionsFilePath;
	private String feedFilePath;
	private FeedFileAccessor feedFileAccessor;
	private JAXBContext jaxbContext;

	/**
	 * 
	 */
	public SubscriptionsDAOImpl() {
		try {
			jaxbContext = JAXBContext.newInstance(new Class[] {FeedSubscriptionItem.class, FeedSubscriptions.class} );
		} catch (JAXBException e) {
			LOGGER.fatal("Unable to create JAXB Context", e);
			throw new IllegalStateException(e);
		}
	}


	/* (non-Javadoc)
	 * @see net.urlgrey.mythpodcaster.dao.SubscriptionsDAO#findSubscriptions()
	 */
	@Override
	public List<FeedSubscriptionItem> findSubscriptions() {
		final FeedSubscriptions subscriptionsDocument = loadSubscriptionDocument();

		if (subscriptionsDocument != null && subscriptionsDocument.getSubscriptions() != null) {
			LOGGER.debug("Subscriptions size: " + subscriptionsDocument.getSubscriptions().size());
			return subscriptionsDocument.getSubscriptions();
		}

		LOGGER.debug("Subscriptions were empty or null, returning null");
		return null;
	}

	public synchronized void addSubscription(FeedSubscriptionItem item) throws IOException {
		final String seriesId = item.getSeriesId();
		LOGGER.debug("Adding subscription: seriesId [" + seriesId + "], transcodeProfileId[" + item.getTranscodeProfile() + "]");

		final FeedSubscriptions subscriptionsDocument = loadSubscriptionDocument();
		final List<FeedSubscriptionItem> subscriptions = subscriptionsDocument.getSubscriptions();
		if (subscriptions.contains(item)) {
			final int index = subscriptions.indexOf(item);
			final FeedSubscriptionItem actualItem = subscriptions.get(index);
			actualItem.setActive(true);
		} else {
			subscriptions.add(item);
		}

		final File encodingDirectory = new File(feedFilePath, item.getTranscodeProfile());
		final File feedFile = new File(encodingDirectory, seriesId + RSS_FILE_EXTENSION);
		if (feedFile.exists() == false) {
			final String title = item.getTitle();
			SyndFeed feed = feedFileAccessor.createFeed(feedFile, seriesId, title, item.getTranscodeProfile());
			if (feed == null) {
				throw new IOException("Unable to create feed for new subscription");
			}
		}

		LOGGER.debug("Subscriptions size: " + subscriptions.size());
		storeSubscriptionDocument(subscriptionsDocument);
	}


	/**
	 * @param subscriptionsDocument
	 */
	private void storeSubscriptionDocument(FeedSubscriptions subscriptionsDocument) {
		Collections.sort(subscriptionsDocument.getSubscriptions());
		storeDocument(subscriptionsFilePath, jaxbContext, subscriptionsDocument);
	}


	/**
	 * @param subscriptionsFile
	 * @return
	 */
	private FeedSubscriptions loadSubscriptionDocument() {
		final File subscriptionsFile = new File(subscriptionsFilePath);
		FeedSubscriptions subscriptionsDocument = null;
		if (subscriptionsFile.exists()) {
			try {
				final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				subscriptionsDocument = (FeedSubscriptions) unmarshaller.unmarshal(subscriptionsFile);
			} catch (JAXBException e) {
				LOGGER.error("Unable to unmarshal subscriptions document from XML", e);
			}

			if (subscriptionsDocument == null) {
				subscriptionsDocument = new FeedSubscriptions();
			}
		} else {
			subscriptionsDocument = new FeedSubscriptions();
		}
		return subscriptionsDocument;
	}


	/* (non-Javadoc)
	 * @see net.urlgrey.mythpodcaster.dao.SubscriptionsDAO#removeSubscription(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeSubscription(String seriesId, String transcodeProfileId) {
		LOGGER.debug("Removing subscription: seriesId [" + seriesId + "], transcodeProfileId[" + transcodeProfileId + "]");
		FeedSubscriptions subscriptionsDocument = loadSubscriptionDocument();

		FeedSubscriptionItem item = new FeedSubscriptionItem();
		item.setSeriesId(seriesId);
		item.setTranscodeProfile(transcodeProfileId);

		final List<FeedSubscriptionItem> subscriptions = subscriptionsDocument.getSubscriptions();
		if (!subscriptions.contains(item)) {
			LOGGER.info("Item not found in subscriptions, returning");
			return;
		}

		final int index = subscriptions.indexOf(item);
		final FeedSubscriptionItem actualItem = subscriptions.get(index);
		actualItem.setActive(false);
		LOGGER.debug("Subscriptions size: " + subscriptions.size());
		storeSubscriptionDocument(subscriptionsDocument);
	}


	/* (non-Javadoc)
	 * @see net.urlgrey.mythpodcaster.dao.SubscriptionsDAO#purge(java.util.List)
	 */
	@Override
	public void purge(List<FeedSubscriptionItem> purgeList) {
		FeedSubscriptions subscriptionsDocument = loadSubscriptionDocument();
		subscriptionsDocument.getSubscriptions().removeAll(purgeList);
		this.storeSubscriptionDocument(subscriptionsDocument);
	}

	@Required
	public void setSubscriptionsFilePath(String subscriptionsFilePath) {
		this.subscriptionsFilePath = subscriptionsFilePath;
	}


	@Required
	public void setFeedFilePath(String feedFilePath) {
		this.feedFilePath = feedFilePath;
	}


	@Required
	public void setFeedFileAccessor(FeedFileAccessor feedAccessor) {
		this.feedFileAccessor = feedAccessor;
	}

}
