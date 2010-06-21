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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
			name = "MYTH_RECORDINGS.findRecordedPrograms",
			query = "SELECT recordedprogram FROM RecordedProgram AS recordedprogram ",
			hints = {@QueryHint(name="org.hibernate.comment", value="MythPodcaster: MYTH_RECORDINGS.findRecordedPrograms")}	)})
			public class RecordedProgram implements Comparable, Serializable {

	@Transient
	private String key;

	@Id
	@Column(name = "programid", updatable=false, unique=false, insertable=false)
	private String programId;

	@Column(name = "chanid", updatable=false, unique=false, insertable=false)
	private int channelId;

	@Column(name = "starttime", updatable=false, unique=false, insertable=false)
	private Timestamp startTime;

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

	@ManyToOne
	@JoinColumn(name="recordid")
	private RecordedSeries series;

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
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

	public RecordedSeries getSeries() {
		return series;
	}

	public void setSeries(RecordedSeries series) {
		this.series = series;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@PostLoad
	public void postQuery() {
		if (channelId >= 0 && startTime != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHmmss");
			key = Integer.toString(channelId) + "-" + formatter.format(startTime);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		if (this.startTime == null) {
			return 1;
		}

		return (-1) * this.startTime.compareTo(((RecordedProgram) o).getStartTime());
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
		final String TAB = "    ";

		String retValue = "";

		retValue = "RecordedProgram ( "
			+ super.toString() + TAB
			+ "key = " + this.key + TAB
			+ "programId = " + this.programId + TAB
			+ "channelId = " + this.channelId + TAB
			+ "startTime = " + this.startTime + TAB
			+ "endTime = " + this.endTime + TAB
			+ "title = " + this.title + TAB
			+ "subtitle = " + this.subtitle + TAB
			+ "description = " + this.description + TAB
			+ "filesize = " + this.filesize + TAB
			+ "filename = " + this.filename + TAB
			+ "series = " + this.series + TAB
			+ " )";

		return retValue;
	}


}
