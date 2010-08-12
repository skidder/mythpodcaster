/*
 * RecordedProgram.java
 *
 * Created: Oct 6, 2009 4:23:13 PM
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author scott
 *
 */
@Entity
@Table(name = "recorded")
@NamedQueries({
	@NamedQuery(
			name = "MYTH_RECORDINGS.findRecordedProgramsForSeries",
			query = "SELECT recordedprogram FROM RecordedProgram AS recordedprogram WHERE seriesId = :seriesId",
			hints = {@QueryHint(name="org.hibernate.comment", value="MythPodcaster: MYTH_RECORDINGS.findRecordedProgramsForSeries")}	)})
			public class RecordedProgram implements Comparable<RecordedProgram>, Serializable {

	private static final long serialVersionUID = 1972318377670024183L;

	@Transient
	private String key;

	@EmbeddedId
	private RecordedProgramPK recordedProgramKey;

	@Column(name = "recordid")
	private String seriesId;

	@Column(name = "programid", updatable=false, unique=false, insertable=false)
	private String programId;

	@Column(name = "endtime", updatable=false, unique=false, insertable=false)
	private Timestamp endTime;

	@Column(name = "title", updatable=false, unique=false, insertable=false)
	private String title;

	@Column(name = "subtitle", updatable=false, unique=false, insertable=false)
	private String subtitle;

	@Column(name = "description", updatable=false, unique=false, insertable=false)
	private String description;

	@Column(name = "filesize", updatable=false, unique=false, insertable=false)
	private long filesize;

	@Column(name = "basename", updatable=false, unique=false, insertable=false)
	private String filename;

	public RecordedProgramPK getRecordedProgramKey() {
		return recordedProgramKey;
	}

	public void setRecordedProgramKey(RecordedProgramPK recordedProgramKey) {
		this.recordedProgramKey = recordedProgramKey;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@PostLoad
	public void postQuery() {
		if (recordedProgramKey.getChannelId() >= 0 && recordedProgramKey.getStartTime() != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHmmss");
			key = Integer.toString(recordedProgramKey.getChannelId()) + "-" + formatter.format(recordedProgramKey.getStartTime());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RecordedProgram o) {
		if (this.recordedProgramKey.getStartTime() == null) {
			return 1;
		}

		return (-1) * this.recordedProgramKey.getStartTime().compareTo(o.recordedProgramKey.getStartTime());
	}

	@Override
	public String toString() {
		return "RecordedProgram [description=" + description + ", endTime="
		+ endTime + ", filename=" + filename + ", filesize=" + filesize
		+ ", key=" + key + ", programId=" + programId
		+ ", recordedProgramKey=" + recordedProgramKey + ", seriesId="
		+ seriesId + ", subtitle=" + subtitle + ", title=" + title
		+ "]";
	}
}
