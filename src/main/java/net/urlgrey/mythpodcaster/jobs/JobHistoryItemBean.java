/*
 * JobHistoryItemBean.java
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
package net.urlgrey.mythpodcaster.jobs;

import java.util.Calendar;

/**
 * @author skidder
 * 
 */
public class JobHistoryItemBean {
	public enum JobStatus {
		TRANSCODING, FINISHED, ERROR
	};

	private String transcodingProfileName;
	private String transcodingProgramEpisodeName;
    private String transcodingProgramName;
    private String transcodingSeriesTitle;
	private Calendar startedAt;
	private Calendar finishedAt;
	private JobStatus status;

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

	public String getTranscodingSeriesTitle() {
        return transcodingSeriesTitle;
    }

    public void setTranscodingSeriesTitle(String transcodingSeriesTitle) {
        this.transcodingSeriesTitle = transcodingSeriesTitle;
    }

    public Calendar getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Calendar startedAt) {
		this.startedAt = startedAt;
	}

	public Calendar getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Calendar finishedAt) {
		this.finishedAt = finishedAt;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((transcodingProfileName == null) ? 0
						: transcodingProfileName.hashCode());
		result = prime
				* result
				+ ((transcodingProgramEpisodeName == null) ? 0
						: transcodingProgramEpisodeName.hashCode());
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
		JobHistoryItemBean other = (JobHistoryItemBean) obj;
		if (transcodingProfileName == null) {
			if (other.transcodingProfileName != null)
				return false;
		} else if (!transcodingProfileName.equals(other.transcodingProfileName))
			return false;
		if (transcodingProgramEpisodeName == null) {
			if (other.transcodingProgramEpisodeName != null)
				return false;
		} else if (!transcodingProgramEpisodeName
				.equals(other.transcodingProgramEpisodeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JobHistoryItemBean [transcodingProfileName="
				+ transcodingProfileName + ", transcodingProgramEpisodeName="
				+ transcodingProgramEpisodeName + ", transcodingProgramName="
				+ transcodingProgramName + ", startedAt=" + startedAt
				+ ", finishedAt=" + finishedAt + ", status=" + status + "]";
	}

}
