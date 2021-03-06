/*
 * StatusBean.java
 * 
 * Created: Jul 15, 2010
 * 
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.jobs;

import java.util.Date;

/**
 * @author scottkidder
 * 
 */
public class StatusBean {

  public enum StatusMode {
    IDLE, TRANSCODING
  };

  private StatusMode mode = StatusMode.IDLE;
  private String transcodingProfileName = "";
  private String transcodingSeriesTitle = "";
  private String transcodingProgramName = "";
  private String transcodingProgramEpisodeName = "";
  private Date currentTriggerStart = new Date();
  private Date currentTranscodeStart = null;

  public StatusMode getMode() {
    return mode;
  }

  public void setMode(StatusMode mode) {
    this.mode = mode;
  }

  public String getTranscodingProfileName() {
    return transcodingProfileName;
  }

  public void setTranscodingProfileName(String transcodingProfileName) {
    this.transcodingProfileName = transcodingProfileName;
  }

  public String getTranscodingSeriesTitle() {
    return transcodingSeriesTitle;
  }

  public void setTranscodingSeriesTitle(String transcodingSeriesTitle) {
    this.transcodingSeriesTitle = transcodingSeriesTitle;
  }

  public String getTranscodingProgramName() {
    return transcodingProgramName;
  }

  public void setTranscodingProgramName(String transcodingProgramName) {
    this.transcodingProgramName = transcodingProgramName;
  }

  public String getTranscodingProgramEpisodeName() {
    return transcodingProgramEpisodeName;
  }

  public void setTranscodingProgramEpisodeName(String transcodingProgramEpisodeName) {
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

  /**
   * Clear the values of all fields that are used in the web UI to display current status.
   */
  public void clearDisplayFields() {
    this.transcodingProfileName = "";
    this.transcodingProgramEpisodeName = "";
    this.transcodingProgramName = "";
    this.transcodingSeriesTitle = "";
  }
}
