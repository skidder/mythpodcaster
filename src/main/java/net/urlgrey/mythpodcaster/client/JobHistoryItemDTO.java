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
public class JobHistoryItemDTO implements IsSerializable, Comparable<JobHistoryItemDTO> {
	private String transcodingProfileName;
	private String transcodingProgramEpisodeName;
	private String transcodingProgramName;
	private Date startedAt;
	private Date finishedAt;
	private String status;

	public String getTranscodingProfileName() {
		return transcodingProfileName;
	}

	public void setTranscodingProfileName(String transcodingProfileName) {
		this.transcodingProfileName = transcodingProfileName;
	}

	public String getTranscodingProgramEpisodeName() {
		return transcodingProgramEpisodeName;
	}

	public void setTranscodingProgramEpisodeName(
			String transcodingProgramEpisodeName) {
		this.transcodingProgramEpisodeName = transcodingProgramEpisodeName;
	}

	public String getTranscodingProgramName() {
		return transcodingProgramName;
	}

	public void setTranscodingProgramName(String transcodingProgramName) {
		this.transcodingProgramName = transcodingProgramName;
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

	@Override
	public int compareTo(JobHistoryItemDTO arg0) {
		if (arg0 == null)
			return -1;
		
		if (this.startedAt != null) {
			final int startedAtComparisonResult = this.startedAt.compareTo(arg0.startedAt);
			if (startedAtComparisonResult == 0) {
				final int finishedAtComparisonResult = this.finishedAt.compareTo(arg0.finishedAt);
				if (finishedAtComparisonResult == 0) {
					return this.transcodingProgramName.compareTo(arg0.transcodingProgramName);
				} else {
					return (-1) * finishedAtComparisonResult;
				}
			} else {
				return (-1) * startedAtComparisonResult;
			}
		}

		return 1;
	}
}
