/*
 * FeedSubscriptionItem.java
 *
 * Created: Oct 7, 2009 7:19:15 PM
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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author scott
 *
 */
@XmlRootElement(name="feed-subscription-item")
@XmlAccessorType(XmlAccessType.FIELD)
public class FeedSubscriptionItem implements Comparable<FeedSubscriptionItem> {

	private String title;
	private String seriesId;
	private Date dateAdded;
	private boolean active = true;
	private String transcodeProfile;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSeriesId() {
		return seriesId;
	}
	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}
	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getTranscodeProfile() {
		return transcodeProfile;
	}
	public void setTranscodeProfile(String transcodeProfile) {
		this.transcodeProfile = transcodeProfile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seriesId == null) ? 0 : seriesId.hashCode());
		result = prime
				* result
				+ ((transcodeProfile == null) ? 0 : transcodeProfile.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedSubscriptionItem other = (FeedSubscriptionItem) obj;
		if (seriesId == null) {
			if (other.seriesId != null)
				return false;
		} else if (!seriesId.equals(other.seriesId))
			return false;
		if (transcodeProfile == null) {
			if (other.transcodeProfile != null)
				return false;
		} else if (!transcodeProfile.equals(other.transcodeProfile))
			return false;
		return true;
	}

	@Override
	public int compareTo(FeedSubscriptionItem o) {
		int titleComparison = this.getTitle().compareTo(o.getTitle());
		if (titleComparison != 0)
			return titleComparison;
		
		int transcodingProfileComparison = this.getTranscodeProfile().compareTo(o.getTranscodeProfile());
		if (transcodingProfileComparison != 0)
			return transcodingProfileComparison;
		
		return 0;
	}
	
	
}
