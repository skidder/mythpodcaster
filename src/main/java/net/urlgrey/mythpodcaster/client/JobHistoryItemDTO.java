/*
 * JobHistoryItemDTO.java
 *
 * Created: May 14, 2013
 *
 * Copyright (C) 2013 Scott Kidder
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
package net.urlgrey.mythpodcaster.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author skidder
 *
 */
public class JobHistoryItemDTO implements IsSerializable {
	private String transcodingProfileName;
	private String transcodingProgramKey;
    private String transcodingProgramName;
    private String transcodingSeriesTitle;
	private Date startedAt;
	private Date finishedAt;
	private String status;

	public String getTranscodingProfileName() {
		return transcodingProfileName;
	}

	public void setTranscodingProfileName(String transcodingProfileName) {
		this.transcodingProfileName = transcodingProfileName;
	}

	public String getTranscodingProgramKey() {
		return transcodingProgramKey;
	}

	public void setTranscodingProgramKey(
			String transcodingProgramKey) {
		this.transcodingProgramKey = transcodingProgramKey;
	}

	public String getTranscodingProgramName() {
		return transcodingProgramName;
	}

	public void setTranscodingProgramName(String transcodingProgramName) {
		this.transcodingProgramName = transcodingProgramName;
	}

	public String getTranscodingSeriesTitle() {
        return transcodingSeriesTitle;
    }

    public void setTranscodingSeriesTitle(String transcodingSeriesTitle) {
        this.transcodingSeriesTitle = transcodingSeriesTitle;
    }

    public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
