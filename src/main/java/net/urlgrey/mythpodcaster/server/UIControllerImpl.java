/*
 * UIControllerImpl.java
 * 
 * Created: Jun 22, 2010
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
package net.urlgrey.mythpodcaster.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import net.urlgrey.mythpodcaster.client.FeedSubscriptionItemDTO;
import net.urlgrey.mythpodcaster.client.JobHistoryItemDTO;
import net.urlgrey.mythpodcaster.client.RecordedSeriesDTO;
import net.urlgrey.mythpodcaster.client.StatusDTO;
import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.dao.MythRecordingsDAO;
import net.urlgrey.mythpodcaster.dao.SubscriptionsDAO;
import net.urlgrey.mythpodcaster.dao.TranscodingProfilesDAO;
import net.urlgrey.mythpodcaster.domain.RecordedProgram;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;
import net.urlgrey.mythpodcaster.jobs.JobHistoryCollectionBean;
import net.urlgrey.mythpodcaster.jobs.JobHistoryItemBean;
import net.urlgrey.mythpodcaster.jobs.StatusBean;
import net.urlgrey.mythpodcaster.jobs.StatusBean.StatusMode;
import net.urlgrey.mythpodcaster.xml.FeedSubscriptionItem;
import net.urlgrey.mythpodcaster.xml.ScopeEnum;
import net.urlgrey.mythpodcaster.xml.TranscodingProfile;

/**
 * @author scottkidder
 * 
 */
public class UIControllerImpl implements UIControllerService {
  private static final Logger LOGGER = Logger.getLogger(UIControllerImpl.class);

  private SubscriptionsDAO subscriptionsDao;
  private MythRecordingsDAO recordingsDao;
  private TranscodingProfilesDAO transcodingProfilesDao;
  private String applicationUrl;
  private StatusBean status;
  private Scheduler scheduler;
  private String triggerName;
  private String triggerGroup;
  private JobHistoryCollectionBean jobHistory;

  @Override
  public List<FeedSubscriptionItemDTO> findSubscriptions() {
    final List<FeedSubscriptionItemDTO> results = new ArrayList<FeedSubscriptionItemDTO>();
    final Map<String, TranscodingProfile> profiles = transcodingProfilesDao.findAllProfiles();

    for (FeedSubscriptionItem item : subscriptionsDao.findSubscriptions()) {
      FeedSubscriptionItemDTO dto = new FeedSubscriptionItemDTO();
      dto.setTitle(item.getTitle());
      dto.setSeriesId(item.getSeriesId());
      dto.setTranscodeProfile(item.getTranscodeProfile());
      dto.setTranscodeProfileDisplayName(profiles.get(item.getTranscodeProfile()).getDisplayName());
      results.add(dto);
    }

    return results;
  }

  @Override
  public List<RecordedSeriesDTO> findAllRecordedSeries() {
    final Set<RecordedSeriesDTO> results = new HashSet<RecordedSeriesDTO>();
    final List<FeedSubscriptionItem> subscriptions = subscriptionsDao.findSubscriptions();

    // begin with all of the current subscriptions
    for (FeedSubscriptionItem subscription : subscriptions) {
      final RecordedSeriesDTO dto = new RecordedSeriesDTO();
      dto.setSeriesId(subscription.getSeriesId());
      dto.setTitle(subscription.getTitle());
      dto.setActive(subscription.isActive());
      results.add(dto);
    }

    // add all remaining recorded series
    for (RecordedSeries item : recordingsDao.findAllRecordedSeries()) {
      // continue if the item is null or the series-id is null
      if ((null == item) || (null == item.getSeriesId())) {
        continue;
      }

      // add the item only if it's not already present in the
      // subscriptions set
      final RecordedSeriesDTO dto = new RecordedSeriesDTO(item.getSeriesId());
      if (results.contains(dto)) {
        continue;
      }
      dto.setTitle(item.getTitle());
      results.add(dto);
    }

    // sort the recorded series entries using the program title
    final ArrayList<RecordedSeriesDTO> resultList = new ArrayList<RecordedSeriesDTO>(results);
    Collections.sort(resultList, new Comparator<RecordedSeriesDTO>() {

      @Override
      public int compare(RecordedSeriesDTO o1, RecordedSeriesDTO o2) {
        if (o1 == null || o1.getSortTitle() == null) {
          return -1;
        }

        return o1.getSortTitle().compareTo(o2.getSortTitle());
      }
    });

    return resultList;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public List<FeedSubscriptionItemDTO> findSubscriptionsForSeries(String seriesId) {
    final List<FeedSubscriptionItemDTO> results = new ArrayList<FeedSubscriptionItemDTO>();
    final Map<String, TranscodingProfile> profiles = transcodingProfilesDao.findAllProfiles();

    for (FeedSubscriptionItem item : subscriptionsDao.findSubscriptions()) {
      if (item.getSeriesId().equals(seriesId) == false || item.isActive() == false) {
        continue;
      }

      FeedSubscriptionItemDTO dto = new FeedSubscriptionItemDTO();
      dto.setTitle(item.getTitle());
      dto.setSeriesId(item.getSeriesId());
      dto.setTranscodeProfile(item.getTranscodeProfile());
      final TranscodingProfile transcodingProfile = profiles.get(item.getTranscodeProfile());

      // handle the case where a subscription exists for a transcoding
      // profile that's not
      // found in the transcoding profiles configuration. By including the
      // subscription in the UI
      // we can allow for the subscription to be deleted.
      if (transcodingProfile != null) {
        dto.setTranscodeProfileDisplayName(transcodingProfile.getDisplayName());
      } else {
        dto.setTranscodeProfileDisplayName(UNRECOGNIZED_PROFILE_LABEL + " ("
            + item.getTranscodeProfile() + ")");
      }
      results.add(dto);
    }

    Collections.sort(results, new Comparator() {

      @Override
      public int compare(Object o1, Object o2) {
        FeedSubscriptionItemDTO p1 = (FeedSubscriptionItemDTO) o1;
        FeedSubscriptionItemDTO p2 = (FeedSubscriptionItemDTO) o2;

        return p1.getTranscodeProfileDisplayName().compareTo(p2.getTranscodeProfileDisplayName());
      }

    });
    return results;
  }

  @Override
  public boolean removeSubscription(String seriesId, String transcodingProfile) {
    this.subscriptionsDao.removeSubscription(seriesId, transcodingProfile);
    return true;
  }

  @Override
  public boolean addSubscription(FeedSubscriptionItemDTO dto) {
    FeedSubscriptionItem item = new FeedSubscriptionItem();
    item.setActive(true);
    item.setDateAdded(dto.getDateAdded());
    item.setSeriesId(dto.getSeriesId());
    item.setTitle(dto.getTitle());
    item.setTranscodeProfile(dto.getTranscodeProfile());
    item.setScope(ScopeEnum.valueOf(dto.getScope()));
    if (ScopeEnum.MOST_RECENT.equals(item.getScope())) {
      item.setNumberOfMostRecentToKeep(dto.getNumberOfMostRecentToKeep());
    } else if (ScopeEnum.SPECIFIC_RECORDINGS.equals(item.getScope())) {
      if (dto.getRecordedProgramKeys() != null && dto.getRecordedProgramKeys().length > 0) {
        for (String recording : dto.getRecordedProgramKeys()) {
          item.getRecordedProgramKeys().add(recording);
        }
      }
    }

    try {
      this.subscriptionsDao.addSubscription(item);
    } catch (IOException e) {
      return false;
    }

    return true;
  }

  @Override
  public List<String[]> findAvailableTranscodingProfilesForSeries(String seriesId) {
    final Set<String> activeProfiles = new HashSet<String>();
    final List<TranscodingProfile> availableProfiles = new ArrayList<TranscodingProfile>();
    final List<String[]> result = new ArrayList<String[]>();
    final Map<String, TranscodingProfile> allTranscodingProfiles =
        this.transcodingProfilesDao.findAllProfiles();
    final List<FeedSubscriptionItem> allSubscriptions = this.subscriptionsDao.findSubscriptions();

    // remove those profiles already in use
    for (FeedSubscriptionItem subscription : allSubscriptions) {
      if (subscription.getSeriesId().equals(seriesId) && subscription.isActive() == true) {
        activeProfiles.add(subscription.getTranscodeProfile());
      }
    }

    // add all profiles to the map
    for (TranscodingProfile profile : allTranscodingProfiles.values()) {
      if (activeProfiles.contains(profile.getId()) == false) {
        availableProfiles.add(profile);
      }
    }

    // sort the profiles by display name
    Collections.sort(availableProfiles, new Comparator<TranscodingProfile>() {

      @Override
      public int compare(TranscodingProfile o1, TranscodingProfile o2) {
        return o1.getDisplayName().compareTo(o2.getDisplayName());
      }
    });

    for (TranscodingProfile profile : availableProfiles) {
      result.add(new String[] {profile.getId(), profile.getDisplayName()});
    }

    return result;
  }

  @Override
  public StatusDTO retrieveStatus() {
    StatusDTO dto = new StatusDTO();
    dto.setMode(status.getMode().name());

    if (status.getMode() == StatusMode.IDLE) {
      try {
        Trigger trigger = scheduler.getTrigger(triggerName, triggerGroup);
        dto.setNextTriggerStart(trigger.getNextFireTime());
      } catch (SchedulerException e) {
        LOGGER.warn("Unable to find Quartz trigger for job", e);
        dto.setNextTriggerStart(null);
      }
    } else {
      dto.setCurrentTriggerStart(status.getCurrentTriggerStart());
      dto.setCurrentTranscodeStart(status.getCurrentTranscodeStart());
      dto.setTranscodingProfileName(status.getTranscodingProfileName());
      dto.setTranscodingProgramEpisodeName(status.getTranscodingProgramEpisodeName());
      dto.setTranscodingProgramName(status.getTranscodingProgramName());
      dto.setTranscodingSeriesTitle(status.getTranscodingSeriesTitle());
    }

    return dto;
  }

  @Override
  public List<JobHistoryItemDTO> retrieveJobHistory() {
    final List<JobHistoryItemDTO> result = new ArrayList<JobHistoryItemDTO>();

    for (JobHistoryItemBean historyItem : this.jobHistory.getJobs()) {
      final JobHistoryItemDTO dto = new JobHistoryItemDTO();

      if (historyItem.getStartedAt() != null)
        dto.setStartedAt(historyItem.getStartedAt().getTime());

      if (historyItem.getFinishedAt() != null)
        dto.setFinishedAt(historyItem.getFinishedAt().getTime());

      dto.setTranscodingProfileName(historyItem.getTranscodingProfileName());
      dto.setTranscodingProgramKey(historyItem.getTranscodingProgramKey());
      dto.setTranscodingProgramName(historyItem.getTranscodingProgramName());
      dto.setTranscodingSeriesTitle(historyItem.getTranscodingSeriesTitle());
      dto.setStatus(historyItem.getStatus().name());

      result.add(0, dto);
    }

    return result;
  }

  @Override
  public String retrieveApplicationUrl() {
    return this.applicationUrl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.client.service.UIControllerService#
   * listRecordingsForSeries(java.lang.String)
   */
  @Override
  public List<String[]> listRecordingsForSeries(String seriesId) {
    LOGGER.debug("Finding recordings for seriesId[" + seriesId + "]");

    final List<String[]> result = new ArrayList<String[]>();
    final RecordedSeries seriesInfo = recordingsDao.findRecordedSeries(seriesId);
    if (seriesInfo != null) {
      Collections.sort(seriesInfo.getRecordedPrograms());
      for (RecordedProgram program : seriesInfo.getRecordedPrograms()) {
        String[] recordingInfo = new String[3];
        recordingInfo[0] = program.getKey();
        recordingInfo[1] = program.getSubtitle();
        recordingInfo[2] = Long.toString(program.getStartTime().getTime());
        result.add(recordingInfo);
      }
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.client.service.UIControllerService#
   * retrieveSubscriptionDetails(java.lang.String, java.lang.String)
   */
  @Override
  public FeedSubscriptionItemDTO retrieveSubscriptionDetails(String seriesId,
      String transcodingProfile) {
    FeedSubscriptionItemDTO result = null;

    for (FeedSubscriptionItem item : this.subscriptionsDao.findSubscriptions()) {
      if (item.getSeriesId().equals(seriesId)
          && item.getTranscodeProfile().equals(transcodingProfile)) {
        result = new FeedSubscriptionItemDTO();
        result.setSeriesId(item.getSeriesId());
        result.setScope(item.getScope().name());
        result.setTitle(item.getTitle());
        result.setRecordedProgramKeys(item.getRecordedProgramKeys().toArray(new String[0]));
        result.setNumberOfMostRecentToKeep(item.getNumberOfMostRecentToKeep());

        break;
      }
    }

    return result;
  }

  public void setSubscriptionsDao(SubscriptionsDAO subscriptionsDao) {
    this.subscriptionsDao = subscriptionsDao;
  }

  public void setRecordingsDao(MythRecordingsDAO recordingsDao) {
    this.recordingsDao = recordingsDao;
  }

  public void setTranscodingProfilesDao(TranscodingProfilesDAO transcodingProfilesDao) {
    this.transcodingProfilesDao = transcodingProfilesDao;
  }

  public void setStatus(StatusBean status) {
    this.status = status;
  }

  public void setJobHistory(JobHistoryCollectionBean jobHistory) {
    this.jobHistory = jobHistory;
  }

  public void setApplicationUrl(String applicationUrl) {
    this.applicationUrl = applicationUrl;
  }

  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public void setTriggerName(String triggerName) {
    this.triggerName = triggerName;
  }

  public void setTriggerGroup(String triggerGroup) {
    this.triggerGroup = triggerGroup;
  }
}
