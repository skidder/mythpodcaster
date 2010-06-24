/*
 * RecordedSeries.java
 *
 * Created: Oct 7, 2009 10:35:39 AM
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
package net.urlgrey.mythpodcaster.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * @author scott
 *
 */
@Entity
@Table(name = "recorded")
@NamedQueries({
	@NamedQuery(
			name = "MYTH_RECORDINGS.findAllRecordedSeries",
			query = "SELECT recordedseries FROM RecordedSeries AS recordedseries WHERE recordedseries.recordingGroup != 'LiveTV' group by recordedseries.seriesId order by recordedseries.title asc",
			hints = {@QueryHint(name="org.hibernate.comment", value="MythPodcaster: MYTH_RECORDINGS.findAllRecordedSeries")}	),
			@NamedQuery(
					name = "MYTH_RECORDINGS.findRecordedSeries",
					query = "SELECT recordedseries FROM RecordedSeries AS recordedseries WHERE recordedseries.recordingGroup != 'LiveTV' AND recordedseries.seriesId = :seriesId group by recordedseries.seriesId",
					hints = {@QueryHint(name="org.hibernate.comment", value="MythPodcaster: MYTH_RECORDINGS.findRecordedSeries")}	)})
					public class RecordedSeries implements Serializable {

	@Column(name = "title")
	private String title;

	@Id
	@Column(name = "recordid")
	private String seriesId;

	@Column(name = "recgroup")
	private String recordingGroup;

	@OneToMany(cascade = {CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name="recordid")
	@Sort(type=SortType.NATURAL)
	private List<RecordedProgram> recordedPrograms = new ArrayList<RecordedProgram>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<RecordedProgram> getRecordedPrograms() {
		return recordedPrograms;
	}

	public void setRecordedPrograms(List<RecordedProgram> recordedPrograms) {
		this.recordedPrograms = recordedPrograms;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}

	public String getRecordingGroup() {
		return recordingGroup;
	}

	public void setRecordingGroup(String recordingGroup) {
		this.recordingGroup = recordingGroup;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((seriesId == null) ? 0 : seriesId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordedSeries other = (RecordedSeries) obj;
		if (seriesId == null) {
			if (other.seriesId != null)
				return false;
		} else if (!seriesId.equals(other.seriesId))
			return false;
		return true;
	}
}
