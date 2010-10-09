/*
 * FeedSubscriptionItemDTO.java
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
package net.urlgrey.mythpodcaster.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author scottkidder
 *
 */
public class FeedSubscriptionItemDTO implements IsSerializable {
	private String title;
	private String seriesId;
	private Date dateAdded;
	private boolean active = true;
	private String transcodeProfile;
	private String transcodeProfileDisplayName;
	private String scope;
	private int numberOfMostRecentToKeep;
	private String[] recordedProgramKeys;

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
	public void setTranscodeProfileDisplayName(
			String transcodeProfileDisplayName) {
		this.transcodeProfileDisplayName = transcodeProfileDisplayName;
	}
	public String getTranscodeProfileDisplayName() {
		return transcodeProfileDisplayName;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public int getNumberOfMostRecentToKeep() {
		return numberOfMostRecentToKeep;
	}
	public void setNumberOfMostRecentToKeep(int numberOfMostRecentToKeep) {
		this.numberOfMostRecentToKeep = numberOfMostRecentToKeep;
	}
	public String[] getRecordedProgramKeys() {
		return recordedProgramKeys;
	}
	public void setRecordedProgramKeys(String[] recordedProgramKeys) {
		this.recordedProgramKeys = recordedProgramKeys;
	}
}
