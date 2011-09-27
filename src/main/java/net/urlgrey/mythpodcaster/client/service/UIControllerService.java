/*
 * UIControllerService.java
 *
 * Created: Jun 22, 2010
 *
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
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
package net.urlgrey.mythpodcaster.client.service;

import java.util.List;

import net.urlgrey.mythpodcaster.client.FeedSubscriptionItemDTO;
import net.urlgrey.mythpodcaster.client.RecordedSeriesDTO;
import net.urlgrey.mythpodcaster.client.StatusDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author scottkidder
 *
 */
@RemoteServiceRelativePath("uiController.rpc")
public interface UIControllerService extends RemoteService {
	static final String UNRECOGNIZED_PROFILE_LABEL = "Unrecognized Profile";

	List<FeedSubscriptionItemDTO> findSubscriptions();

	List<RecordedSeriesDTO> findAllRecordedSeries();

	List<FeedSubscriptionItemDTO> findSubscriptionsForSeries(String seriesId);

	boolean removeSubscription(String seriesId, String transcodingProfile);

	boolean addSubscription(FeedSubscriptionItemDTO item);

	List<String[]> findAvailableTranscodingProfilesForSeries(
			String seriesId);

	String retrieveApplicationUrl();

	StatusDTO retrieveStatus();

	List<String[]> listRecordingsForSeries(String seriesId);

	FeedSubscriptionItemDTO retrieveSubscriptionDetails(String seriesId,
			String transcodingProfile);
}
