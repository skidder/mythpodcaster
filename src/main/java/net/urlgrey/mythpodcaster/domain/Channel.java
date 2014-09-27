/*
 * Channel.java
 * 
 * Created: Oct 12, 2009 4:04:45 PM
 * 
 * Copyright (C) 2009 Scott Kidder
 * 
 * This file is part of MythPodcaster
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author scott
 * 
 */
@Entity
@Table(name = "channel")
public class Channel {

  @Id
  @Column(name = "chanid")
  private int channelId;

  @Column(name = "name")
  private String name;

  public int getChannelId() {
    return channelId;
  }

  public void setChannelId(int channelId) {
    this.channelId = channelId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Constructs a <code>String</code> with all attributes in name = value format.
   * 
   * @return a <code>String</code> representation of this object.
   */
  public String toString() {
    final String TAB = "    ";

    String retValue = "";

    retValue =
        "Channel ( " + super.toString() + TAB + "channelId = " + this.channelId + TAB + "name = "
            + this.name + TAB + " )";

    return retValue;
  }


}
