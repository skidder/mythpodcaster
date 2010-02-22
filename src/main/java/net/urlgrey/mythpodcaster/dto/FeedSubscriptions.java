/*
 * FeedSubscriptions.java
 *
 * Created: Oct 7, 2009 7:24:11 PM
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
package net.urlgrey.mythpodcaster.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author scott
 *
 */
@XmlRootElement(name="feed-subscriptions")
@XmlAccessorType(XmlAccessType.FIELD)
public class FeedSubscriptions {

	private List <FeedSubscriptionItem> subscriptions = new ArrayList<FeedSubscriptionItem>();

	public List<FeedSubscriptionItem> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<FeedSubscriptionItem> subscriptions) {
		this.subscriptions = subscriptions;
	}
	
	
}
