/*
 * JobHistoryCollectionBean.java
 * 
 * Created: May 14, 2013
 * 
 * Copyright (C) 2013 Scott Kidder
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
package net.urlgrey.mythpodcaster.jobs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skidder
 * 
 */
public class JobHistoryCollectionBean {

  final int collectionSize;
  List<JobHistoryItemBean> jobs = new ArrayList<JobHistoryItemBean>();

  /**
   * @param collectionSize
   */
  public JobHistoryCollectionBean(int collectionSize) {
    this.collectionSize = collectionSize;
  }

  public void addJobHistoryItemBean(JobHistoryItemBean item) {
    // remove oldest item from the collection if max size reached
    if (jobs.size() >= this.collectionSize) {
      this.jobs.remove(0);
    }

    this.jobs.add(item);
  }

  public List<JobHistoryItemBean> getJobs() {
    return jobs;
  }

  @Override
  public String toString() {
    return "JobHistoryCollectionBean [collectionSize=" + collectionSize + ", jobs=" + jobs + "]";
  }

}
