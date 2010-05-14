/*
 * TranscodingControllerImpl.java
 *
 * Created: Feb 19, 2010
 *
 * Copyright (C) 2010 Scott Kidder
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
package net.urlgrey.mythpodcaster.transcode;

import java.io.File;
import java.io.IOException;
import java.rmi.server.UID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import net.urlgrey.mythpodcaster.dto.TranscoderConfigurationItem;
import net.urlgrey.mythpodcaster.dto.TranscodingProfile;

/**
 * @author scottkidder
 *
 */
public class TranscodingControllerImpl implements TranscodingController {

	private static final Logger LOGGER = Logger.getLogger(TranscodingControllerImpl.class);
	private Transcoder ffmpegTranscoder;
	private Transcoder segmentedVodTranscoder;
	private Transcoder fastStartVodTranscoder;


	@Override
	public void transcode(TranscodingProfile profile, File inputFile,
			File outputFile) throws Exception {
		
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
			encodeSegmented(profile, inputFile, outputFile);
			break;
		default:
			break;
		}
	}


	private void encodeFastStart(TranscodingProfile profile, File inputFile) throws Exception {
		LOGGER.info("Starting fast-start optimization of clip: inputFile[" + inputFile.getAbsolutePath() + "]");
		File workingDirectory = this.createTempDir();
		File tempOutputFile = File.createTempFile(new UID().toString(), "tmp");
		try {
			final TranscoderConfigurationItem config = profile.getTranscoderConfigurationItems().get(profile.getTranscoderConfigurationItems().size() - 1);
			fastStartVodTranscoder.transcode(workingDirectory, config, inputFile, tempOutputFile);
			
			// replace the input-file with the fast-start optimized version
			FileOperations.copy(tempOutputFile, inputFile);
		} catch (Exception e) {
			deleteDir(inputFile.getParentFile());
			throw e;
		} finally {
			deleteDir(workingDirectory);

			if (tempOutputFile.exists()) {
				tempOutputFile.delete();
			}
		}
	}


	private void encodeOnePass(TranscodingProfile profile, File inputFile,
			File outputFile) throws Exception {
		
		LOGGER.info("Starting 1-pass encoding: inputFile[" + inputFile.getAbsolutePath() + "]");
		File workingDirectory = this.createTempDir();

		try {
			ffmpegTranscoder.transcode(workingDirectory, profile.getTranscoderConfigurationItems().get(0), inputFile, outputFile);
		} finally {
			deleteDir(workingDirectory);
		}
	}

	
	private void encodeTwoPass(TranscodingProfile profile, File inputFile,
			File outputFile) throws Exception {

		LOGGER.info("Starting 2-pass encoding: inputFile[" + inputFile.getAbsolutePath() + "]");
		File workingDirectory = this.createTempDir();		
		try {
			final TranscoderConfigurationItem pass1Config = profile.getTranscoderConfigurationItems().get(0);
			ffmpegTranscoder.transcode(workingDirectory, pass1Config, inputFile, outputFile);
			
			final TranscoderConfigurationItem pass2Config = profile.getTranscoderConfigurationItems().get(1);
			ffmpegTranscoder.transcode(workingDirectory, pass2Config, inputFile, outputFile);
		} finally {
			deleteDir(workingDirectory);
		}
	}


	private void encodeSegmented(TranscodingProfile profile, File inputFile,
			File outputFile) throws Exception {

		LOGGER.info("Starting segmented vod encoding: inputFile[" + inputFile.getAbsolutePath() + "]");
		File workingDirectory = this.createTempDir();		
		File tempOutputFile = File.createTempFile(new UID().toString(), "tmp");
		
		try {
			final TranscoderConfigurationItem pass1Config = profile.getTranscoderConfigurationItems().get(0);
			ffmpegTranscoder.transcode(workingDirectory, pass1Config, inputFile, tempOutputFile);
			
			final TranscoderConfigurationItem segmentedVodConfig = profile.getTranscoderConfigurationItems().get(1);
			segmentedVodTranscoder.transcode(workingDirectory, segmentedVodConfig, tempOutputFile, outputFile);
		} catch (Exception e) {
			deleteDir(outputFile.getParentFile());
			throw e;
		} finally {
			deleteDir(workingDirectory);

			if (tempOutputFile.exists()) {
				tempOutputFile.delete();
			}
		}
	}


	/**
	 * @param directory
	 */
	private void deleteDir(File directory) {
		try {
			// delete files in the directory
			File[] files = directory.listFiles();
			for(int i=0;i<files.length;i++) {
			    File file = files[i];
			    file.delete();
			}
			directory.delete();
		} catch (Exception e) {
			// ignore
		}
	}


    /**
    * Creates a folder in "java.io.tmpdir" with a uniqe name.
    */
    private File createTempDir() throws IOException {
        UID uid = new UID();
        File dir = new File(System.getProperty("java.io.tmpdir"));
        File tmp = new File(dir, uid.toString());
        if(!tmp.mkdirs()) {
          throw new IOException("Cannot create tmp dir ["+tmp.toString()+"]");
        }
        return tmp;
    }

	@Required
	public void setFfmpegTranscoder(Transcoder ffmpegTranscoder) {
		this.ffmpegTranscoder = ffmpegTranscoder;
	}


	@Required
	public void setSegmentedVodTranscoder(
			Transcoder segmentedVodTranscoder) {
		this.segmentedVodTranscoder = segmentedVodTranscoder;
	}


	@Required
	public void setFastStartVodTranscoder(Transcoder fastStartTranscoder) {
		this.fastStartVodTranscoder = fastStartTranscoder;
	}
}
