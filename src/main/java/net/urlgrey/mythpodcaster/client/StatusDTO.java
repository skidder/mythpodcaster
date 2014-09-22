/*
 * StatusDTO.java
 *
 * Created: Jul 15, 2010
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
public class StatusDTO implements IsSerializable {

	private String mode = "IDLE";
	private String transcodingProfileName = "";
    private String transcodingSeriesTitle = "";
    private String transcodingProgramName = "";
	private String transcodingProgramEpisodeName = "";
	private Date currentTriggerStart = null;
	private Date nextTriggerStart = null;
	private Date currentTranscodeStart = null;

	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getTranscodingProfileName() {
		return transcodingProfileName;
	}
	public void setTranscodingProfileName(String transcodingProfileName) {
		this.transcodingProfileName = transcodingProfileName;
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
    public String getTranscodingProgramEpisodeName() {
		return transcodingProgramEpisodeName;
	}
	public void setTranscodingProgramEpisodeName(
			String transcodingProgramEpisodeName) {
		this.transcodingProgramEpisodeName = transcodingProgramEpisodeName;
	}
	public Date getCurrentTriggerStart() {
		return currentTriggerStart;
	}
	public void setCurrentTriggerStart(Date currentTriggerStart) {
		this.currentTriggerStart = currentTriggerStart;
	}
	public Date getCurrentTranscodeStart() {
		return currentTranscodeStart;
	}
	public void setCurrentTranscodeStart(Date currentTranscodeStart) {
		this.currentTranscodeStart = currentTranscodeStart;
	}
	public Date getNextTriggerStart() {
		return nextTriggerStart;
	}
	public void setNextTriggerStart(Date nextTriggerStart) {
		this.nextTriggerStart = nextTriggerStart;
	}
}
