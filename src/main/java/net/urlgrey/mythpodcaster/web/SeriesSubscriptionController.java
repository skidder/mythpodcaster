/*
 * SeriesSubscriptionController.java
 *
 * Created: Oct 8, 2009 9:40:27 AM
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
package net.urlgrey.mythpodcaster.web;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.urlgrey.mythpodcaster.dao.MythRecordingsDAO;
import net.urlgrey.mythpodcaster.dao.SubscriptionsDAO;
import net.urlgrey.mythpodcaster.dao.TranscodingProfilesDAO;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;
import net.urlgrey.mythpodcaster.dto.FeedSubscriptionItem;
import net.urlgrey.mythpodcaster.dto.TranscodingProfile;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author scott
 *
 */
public class SeriesSubscriptionController implements Controller {

	private static final Logger LOGGER = Logger.getLogger(SeriesSubscriptionController.class);
	private String successView = null;
	private SubscriptionsDAO subscriptionsDao;
	private MythRecordingsDAO recordingsDao;
	private TranscodingProfilesDAO transcodingProfilesDao;

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		final String seriesId = request.getParameter("seriesId");
		final String action = request.getParameter("action");
		if (seriesId == null) {
			LOGGER.error("Request submitted without a seriesId specified, aborting");
			return new ModelAndView(new RedirectView(successView));
		}

		if (action == null) {
			LOGGER.error("Request submitted without a action specified, aborting");
			return new ModelAndView(new RedirectView(successView));
		}
		LOGGER.debug("Handling Request: seriesId[" + seriesId + "], action[" + action + "]");

		try {
			String transcodeProfileId = request.getParameter("transcodeProfile");
			if ("subscribe".equalsIgnoreCase(action)) {
				final Map<String, TranscodingProfile> transcodeProfiles = transcodingProfilesDao.findAllProfiles();
				if(transcodeProfiles.size() > 1)
				{
					if( transcodeProfileId == null 
							|| !transcodeProfiles.containsKey(transcodeProfileId))
					{
						// identify and remove those transcoding profiles already in use for this series
						final List<FeedSubscriptionItem> currentSubscriptions = subscriptionsDao.findSubscriptions();
						for (FeedSubscriptionItem sub : currentSubscriptions) {
							if (sub.isActive() && sub.getSeriesId().equals(seriesId)) {
								transcodeProfiles.remove(sub.getTranscodeProfile());
							}
						}

						//Output a page
						final Map<String, Object> model = new HashMap <String, Object>();
						final ArrayList<TranscodingProfile> transList = new ArrayList<TranscodingProfile>(transcodeProfiles.values());
						Collections.sort(transList);
						model.put("", seriesId);
						model.put("action", action);
						model.put("transcoders", transList);
						return new ModelAndView("transcoder-selection", model);
					}
				}
				else if (transcodeProfiles.size() == 1)
				{
					// if only one transcoding profile exists, then use it automatically
					transcodeProfileId = transcodeProfiles.keySet().iterator().next();
				}

				final RecordedSeries seriesInfo = recordingsDao.findRecordedSeries(seriesId);
				if (seriesInfo == null) {
					LOGGER.error("Unable to find info for  [" + seriesId + "], aborting");
					return new ModelAndView(new RedirectView(successView));
				}

				final FeedSubscriptionItem item = new FeedSubscriptionItem();
				item.setDateAdded(new Date());
				item.setSeriesId(seriesId);
				item.setTitle(seriesInfo.getTitle());
				item.setTranscodeProfile(transcodeProfileId);

				subscriptionsDao.addSubscription(item);
			} else if ("unsubscribe".equalsIgnoreCase(action)) {
				subscriptionsDao.removeSubscription(seriesId, transcodeProfileId);
			}
		} catch (Exception e) {
			LOGGER.error("Error while processing subscription action", e);
		}

		return new ModelAndView(new RedirectView(successView));
	}

	@Required
	public void setSubscriptionsDao(SubscriptionsDAO subscriptionsDao) {
		this.subscriptionsDao = subscriptionsDao;
	}

	@Required
	public void setRecordingsDao(MythRecordingsDAO recordingsDao) {
		this.recordingsDao = recordingsDao;
	}

	@Required
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	@Required
	public void setTranscodingProfilesDao(
			TranscodingProfilesDAO transcodingProfilesDao) {
		this.transcodingProfilesDao = transcodingProfilesDao;
	}


}
