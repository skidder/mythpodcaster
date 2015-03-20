/*
 * TranscodingControllerImpl.java
 * 
 * Created: Feb 19, 2010
 * 
 * Copyright (C) 2010 Scott Kidder
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
package net.urlgrey.mythpodcaster.transcode;

import java.io.File;
import java.rmi.server.UID;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import net.urlgrey.mythpodcaster.jobs.JobHistoryCollectionBean;
import net.urlgrey.mythpodcaster.jobs.JobHistoryItemBean;
import net.urlgrey.mythpodcaster.jobs.JobHistoryItemBean.JobStatus;
import net.urlgrey.mythpodcaster.xml.GenericTranscoderConfigurationItem;
import net.urlgrey.mythpodcaster.xml.TranscodingProfile;

/**
 * @author scottkidder
 * 
 */
public class TranscodingControllerImpl implements TranscodingController {

  private static final Logger LOGGER = Logger.getLogger(TranscodingControllerImpl.class);
  private Transcoder ffmpegTranscoder;
  private Transcoder segmentedVodTranscoder;
  private Transcoder fastStartVodTranscoder;
  private Transcoder userDefinedTranscoder;
  private Transcoder symbolicLinkTranscoder;
  private JobHistoryCollectionBean jobHistory;

  @Override
  public void transcode(TranscodingProfile profile, String programKey, String seriesTitle,
      String programTitle, File inputFile, File outputFile) throws Exception {

    // construct a new Job History Item bean to represent the active job
    final JobHistoryItemBean jobHistoryItem = new JobHistoryItemBean();
    jobHistoryItem.setStartedAt(Calendar.getInstance());
    jobHistoryItem.setStatus(JobStatus.TRANSCODING);
    jobHistoryItem.setTranscodingProfileName(profile.getDisplayName());
    jobHistoryItem.setTranscodingProgramKey(programKey);
    jobHistoryItem.setTranscodingSeriesTitle(seriesTitle);
    jobHistoryItem.setTranscodingProgramName(programTitle);
    this.jobHistory.addJobHistoryItemBean(jobHistoryItem);

    try {
      switch (profile.getMode()) {
        case ONE_PASS:
          encodeOnePass(profile, inputFile, outputFile);
          break;
        case ONE_PASS_FAST_START:
          encodeOnePass(profile, inputFile, outputFile);
          encodeFastStart(profile, outputFile);
          break;
        case TWO_PASS:
          encodeTwoPass(profile, inputFile, outputFile);
          break;
        case TWO_PASS_FAST_START:
          encodeTwoPass(profile, inputFile, outputFile);
          encodeFastStart(profile, outputFile);
          break;
        case HTTP_SEGMENTED_VOD:
        case ONE_PASS_HTTP_SEGMENTED_VOD:
          encodeOnePassSegmented(profile, inputFile, outputFile);
          break;
        case TWO_PASS_HTTP_SEGMENTED_VOD:
          encodeTwoPassSegmented(profile, inputFile, outputFile);
          break;
        case USER_DEFINED:
          encodeUserDefined(profile, inputFile, outputFile);
          break;
        case SYMBOLIC_LINK:
          encodeSymbolicLink(profile, inputFile, outputFile);
          break;
        default:
          break;
      }

      jobHistoryItem.setStatus(JobStatus.FINISHED);
    } catch (Exception e) {
      jobHistoryItem.setStatus(JobStatus.ERROR);
    } finally {
      jobHistoryItem.setFinishedAt(Calendar.getInstance());
    }
  }

  private void encodeSymbolicLink(TranscodingProfile profile, File inputFile, File outputFile)
      throws Exception {
    LOGGER.info("Starting symbolic-link encoding: inputFile[" + inputFile.getAbsolutePath() + "]");

    final File workingDirectory = FileOperations.createTempDir();
    final GenericTranscoderConfigurationItem config;
    if (profile.getTranscoderConfigurationItems() == null
        || profile.getTranscoderConfigurationItems().size() == 0) {
      config = new GenericTranscoderConfigurationItem();
      config.setTimeout(60);
    } else {
      config = profile.getTranscoderConfigurationItems().get(0);
    }

    try {
      symbolicLinkTranscoder.transcode(workingDirectory, config, inputFile, outputFile);
    } finally {
      FileOperations.deleteDir(workingDirectory);
    }
  }

  private void encodeUserDefined(TranscodingProfile profile, File inputFile, File outputFile)
      throws Exception {

    LOGGER.info("Starting user-defined encoding: inputFile[" + inputFile.getAbsolutePath() + "]");
    final File workingDirectory = FileOperations.createTempDir();

    try {
      File tempInputFile = null;
      File tempOutputFile = null;
      final List<GenericTranscoderConfigurationItem> configItems =
          profile.getTranscoderConfigurationItems();
      for (GenericTranscoderConfigurationItem config : configItems) {
        if (tempOutputFile == null) {
          // first run, use the original input
          tempInputFile = inputFile;
        } else {
          // use the output from the previously executed command
          tempInputFile = tempOutputFile;
        }

        if (config.equals(configItems.get(configItems.size() - 1))) {
          tempOutputFile =
              File.createTempFile(new UID().toString(), profile.getEncodingFileExtension(),
                  workingDirectory);
        } else {
          tempOutputFile = File.createTempFile(new UID().toString(), "tmp", workingDirectory);
        }
        userDefinedTranscoder.transcode(workingDirectory, config, tempInputFile, tempOutputFile);
      }

      FileOperations.copy(tempOutputFile, outputFile);
    } finally {
      FileOperations.deleteDir(workingDirectory);
    }
  }

  private void encodeFastStart(TranscodingProfile profile, File inputFile) throws Exception {
    LOGGER.info("Starting fast-start optimization of clip: inputFile["
        + inputFile.getAbsolutePath() + "]");
    File workingDirectory = FileOperations.createTempDir();
    File tempOutputFile = File.createTempFile(new UID().toString(), "tmp");
    try {
      final GenericTranscoderConfigurationItem config =
          profile.getTranscoderConfigurationItems().get(
              profile.getTranscoderConfigurationItems().size() - 1);
      fastStartVodTranscoder.transcode(workingDirectory, config, inputFile, tempOutputFile);

      // replace the input-file with the fast-start optimized version
      FileOperations.copy(tempOutputFile, inputFile);
    } catch (Exception e) {
      FileOperations.deleteDir(inputFile.getParentFile());
      throw e;
    } finally {
      FileOperations.deleteDir(workingDirectory);

      if (tempOutputFile.exists()) {
        tempOutputFile.delete();
      }
    }
  }

  private void encodeOnePass(TranscodingProfile profile, File inputFile, File outputFile)
      throws Exception {

    LOGGER.info("Starting 1-pass encoding: inputFile[" + inputFile.getAbsolutePath() + "]");
    File workingDirectory = FileOperations.createTempDir();

    try {
      ffmpegTranscoder.transcode(workingDirectory,
          profile.getTranscoderConfigurationItems().get(0), inputFile, outputFile);
    } finally {
      FileOperations.deleteDir(workingDirectory);
    }
  }

  private void encodeTwoPass(TranscodingProfile profile, File inputFile, File outputFile)
      throws Exception {

    LOGGER.info("Starting 2-pass encoding: inputFile[" + inputFile.getAbsolutePath() + "]");
    File workingDirectory = FileOperations.createTempDir();
    try {
      final GenericTranscoderConfigurationItem pass1Config =
          profile.getTranscoderConfigurationItems().get(0);
      ffmpegTranscoder.transcode(workingDirectory, pass1Config, inputFile, outputFile);

      final GenericTranscoderConfigurationItem pass2Config =
          profile.getTranscoderConfigurationItems().get(1);
      ffmpegTranscoder.transcode(workingDirectory, pass2Config, inputFile, outputFile);
    } finally {
      FileOperations.deleteDir(workingDirectory);
    }
  }

  private void encodeOnePassSegmented(TranscodingProfile profile, File inputFile, File outputFile)
      throws Exception {

    LOGGER.info("Starting one-pass segmented vod encoding: inputFile["
        + inputFile.getAbsolutePath() + "]");
    File workingDirectory = FileOperations.createTempDir();
    File tempOutputFile = File.createTempFile(new UID().toString(), "tmp");

    try {
      final GenericTranscoderConfigurationItem pass1Config =
          profile.getTranscoderConfigurationItems().get(0);
      ffmpegTranscoder.transcode(workingDirectory, pass1Config, inputFile, tempOutputFile);

      final GenericTranscoderConfigurationItem segmentedVodConfig =
          profile.getTranscoderConfigurationItems().get(1);
      segmentedVodTranscoder.transcode(workingDirectory, segmentedVodConfig, tempOutputFile,
          outputFile);
    } catch (Exception e) {
      FileOperations.deleteDir(outputFile.getParentFile());
      throw e;
    } finally {
      FileOperations.deleteDir(workingDirectory);

      if (tempOutputFile.exists()) {
        tempOutputFile.delete();
      }
    }
  }

  private void encodeTwoPassSegmented(TranscodingProfile profile, File inputFile, File outputFile)
      throws Exception {

    LOGGER.info("Starting two-pass segmented vod encoding: inputFile["
        + inputFile.getAbsolutePath() + "]");
    File workingDirectory = FileOperations.createTempDir();
    File tempOutputFile = File.createTempFile(new UID().toString(), "tmp");

    try {
      final GenericTranscoderConfigurationItem pass1Config =
          profile.getTranscoderConfigurationItems().get(0);
      ffmpegTranscoder.transcode(workingDirectory, pass1Config, inputFile, tempOutputFile);

      final GenericTranscoderConfigurationItem pass2Config =
          profile.getTranscoderConfigurationItems().get(1);
      ffmpegTranscoder.transcode(workingDirectory, pass2Config, inputFile, tempOutputFile);

      final GenericTranscoderConfigurationItem segmentedVodConfig =
          profile.getTranscoderConfigurationItems().get(2);
      segmentedVodTranscoder.transcode(workingDirectory, segmentedVodConfig, tempOutputFile,
          outputFile);
    } catch (Exception e) {
      FileOperations.deleteDir(outputFile.getParentFile());
      throw e;
    } finally {
      FileOperations.deleteDir(workingDirectory);

      if (tempOutputFile.exists()) {
        tempOutputFile.delete();
      }
    }
  }

  @Required
  public void setFfmpegTranscoder(Transcoder ffmpegTranscoder) {
    this.ffmpegTranscoder = ffmpegTranscoder;
  }

  @Required
  public void setSegmentedVodTranscoder(Transcoder segmentedVodTranscoder) {
    this.segmentedVodTranscoder = segmentedVodTranscoder;
  }

  @Required
  public void setFastStartVodTranscoder(Transcoder fastStartTranscoder) {
    this.fastStartVodTranscoder = fastStartTranscoder;
  }

  @Required
  public void setUserDefinedTranscoder(Transcoder userDefinedTranscoder) {
    this.userDefinedTranscoder = userDefinedTranscoder;
  }

  @Required
  public void setSymbolicLinkTranscoder(Transcoder symbolicLinkTranscoder) {
    this.symbolicLinkTranscoder = symbolicLinkTranscoder;
  }

  @Required
  public void setJobHistory(JobHistoryCollectionBean jobHistory) {
    this.jobHistory = jobHistory;
  }
}
