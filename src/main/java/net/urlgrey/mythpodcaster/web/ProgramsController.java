/*
 * ProgramsController.java
 *
 * Created: 2009-06-26 16:20
 *
 * Copyright (C) 2009 Scott Kidder
 * 
 * This file is part of MythPodcaster.
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

/**
 * 
 */
package net.urlgrey.mythpodcaster.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.urlgrey.mythpodcaster.dao.MythRecordingsDAO;
import net.urlgrey.mythpodcaster.dao.SubscriptionsDAO;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;
import net.urlgrey.mythpodcaster.dto.FeedSubscriptionItem;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author scott
 * 
 */
public class ProgramsController implements Controller {

	private static final Logger LOG = Logger.getLogger(ProgramsController.class);
	private String successView = null;
	private MythRecordingsDAO recordingsDao;
	private SubscriptionsDAO subscriptionsDao;
	private String applicationURL;
	private String feedFileExtension;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		LOG.debug("Retrieving list of recorded progams");
		final List<RecordedSeries> series = this.recordingsDao.findAllRecordedSeries();
		if (series != null) {
			LOG.debug("Series List size: " + series.size());
		} else {
			LOG.debug("Series List is empty");
		}

		// remove subscribed-to series from the global list of series
		final List<FeedSubscriptionItem> subscriptions = this.subscriptionsDao.findSubscriptions();
		final ArrayList<RecordedSeries> displaySeries = new ArrayList<RecordedSeries>(series);
		final List<FeedSubscriptionItem> displaySubscriptions = new ArrayList<FeedSubscriptionItem>();
		for (FeedSubscriptionItem item : subscriptions) {
			if (item.isActive()) {
				displaySubscriptions.add(item);
			}
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("applicationURL", this.applicationURL);
		model.put("feedFileExtension", this.feedFileExtension);
		model.put("mythpodcaster_series", displaySeries);
		model.put("mythpodcaster_series_subscriptions", displaySubscriptions);
		return new ModelAndView(this.successView, model);
	}

	@Required
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	@Required
	public void setRecordingsDao(MythRecordingsDAO recordingsDao) {
		this.recordingsDao = recordingsDao;
	}

	@Required
	public void setSubscriptionsDao(SubscriptionsDAO subscriptionsDao) {
		this.subscriptionsDao = subscriptionsDao;
	}

	@Required
	public void setApplicationURL(String applicationURL) {
		this.applicationURL = applicationURL;
	}

	@Required
	public void setFeedFileExtension(String feedFileExtension) {
		this.feedFileExtension = feedFileExtension;
	}
}
