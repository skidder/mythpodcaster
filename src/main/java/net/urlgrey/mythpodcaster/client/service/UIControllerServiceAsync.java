/*
 * UIControllerServiceAsync.java
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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author scottkidder
 *
 */
public interface UIControllerServiceAsync {

	void findSubscriptions(AsyncCallback<List<FeedSubscriptionItemDTO>> callback);

	void findAllRecordedSeries(AsyncCallback<List<RecordedSeriesDTO>> callback);

	void findSubscriptionsForSeries(String seriesId, AsyncCallback<List<FeedSubscriptionItemDTO>> callback);

	void removeSubscription(String seriesId, String transcodingProfile,
			AsyncCallback<Boolean> callback);

	void addSubscription(FeedSubscriptionItemDTO item,
			AsyncCallback<Boolean> asyncCallback);

	void findAvailableTranscodingProfilesForSeries(String seriesId,
			AsyncCallback<List<String[]>> asyncCallback);

	void retrieveApplicationUrl(AsyncCallback<String> callback);

	void retrieveStatus(AsyncCallback<StatusDTO> callback);

	void listRecordingsForSeries(String seriesId,
			AsyncCallback<List<String[]>> asyncCallback);

	void retrieveSubscriptionDetails(String seriesId,
			String transcodingProfile,
			AsyncCallback<FeedSubscriptionItemDTO> asyncCallback);

}
