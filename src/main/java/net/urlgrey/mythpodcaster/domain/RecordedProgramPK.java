/*
 * RecordedSeriesDTO.java
 * 
 * Created: Aug 12, 2010
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
package net.urlgrey.mythpodcaster.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author scottkidder
 * 
 */
@Embeddable
public class RecordedProgramPK implements Serializable {

  private static final long serialVersionUID = -2379768958766467182L;

  @Column(name = "chanid", updatable = false, unique = false, insertable = false, nullable = false)
  private int channelId;

  @Column(name = "starttime", updatable = false, unique = false, insertable = false,
      nullable = false)
  private Timestamp startTime;

  public int getChannelId() {
    return channelId;
  }

  public void setChannelId(int channelId) {
    this.channelId = channelId;
  }

  public Timestamp getStartTime() {
    return startTime;
  }

  public void setStartTime(Timestamp startTime) {
    this.startTime = startTime;
  }

  @Override
  public String toString() {
    return "RecordedProgramPK [channelId=" + channelId + ", startTime=" + startTime + "]";
  }
}
