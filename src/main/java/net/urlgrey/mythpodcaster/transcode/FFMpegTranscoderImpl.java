/*
 * FFMpegTranscoderImpl.java
 *
 * Created: Oct 9, 2009 9:24:11 AM
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
package net.urlgrey.mythpodcaster.transcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.urlgrey.mythpodcaster.dto.FFMpegTranscoderConfigurationItem;
import net.urlgrey.mythpodcaster.dto.TranscoderConfigurationItem;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author scott
 *
 */
public class FFMpegTranscoderImpl extends AbstractTranscoderImpl implements Transcoder {

    static final Logger LOG = Logger.getLogger(FFMpegTranscoderImpl.class);
	private String ffmpegLocation;
    private String niceLocation = "nice";
	
    static final ExecutorService pool = Executors.newCachedThreadPool();

	public void transcode(File workingDirectory, TranscoderConfigurationItem genericConfig, File inputFile, File outputFile) throws Exception {
		LOG.info("transcode started: inputFile [" + inputFile.getAbsolutePath() + "], outputFile [" + outputFile.getAbsolutePath() + "]");

		FFMpegTranscoderConfigurationItem config = (FFMpegTranscoderConfigurationItem) genericConfig;
		List <String> commandList = new ArrayList<String>();
		commandList.add(niceLocation);
		commandList.add("-n");
		commandList.add(Integer.toString(config.getNiceness()));
		commandList.add(ffmpegLocation);
		commandList.add("-i");
		commandList.add(inputFile.getAbsolutePath());
		commandList.addAll(config.getParsedEncoderArguments());
		commandList.add(outputFile.getAbsolutePath());
		ProcessBuilder pb = new ProcessBuilder(commandList);

        // Needed for ffmpeg
        pb.environment().put("LD_LIBRARY_PATH", "/usr/local/lib:");
        pb.redirectErrorStream(true);
        pb.directory(workingDirectory);
        Process process = null;

        try {
            // Get the ffmpeg process
            process = pb.start();
            // We give a couple of secs to complete task if needed
            Future<List<String>> stdout = pool.submit(new OutputMonitor(process.getInputStream()));
            List<String> result = stdout.get(config.getTimeout(), TimeUnit.SECONDS);
            process.waitFor();
            final int exitValue = process.exitValue();
            LOG.debug("FFMPEG exit value: " + exitValue);
            if (exitValue != 0) {
                for (String line : result) {
                    LOG.debug(line);
                }
                throw new Exception("FFMpeg return code indicated failure: " + exitValue);
            }
        } catch (InterruptedException e) {
            throw new Exception("FFMpeg process interrupted by another thread",
                    e);
        } catch (ExecutionException ee) {
            throw new Exception("Something went wrong parsing FFMpeg output",
                    ee);
        } catch (TimeoutException te) {
            // We could not get the result before timeout
            throw new Exception("FFMpeg process timed out", te);
        } catch (RuntimeException re) {
            // Unexpected output from FFMpeg
            throw new Exception("Something went wrong parsing FFMpeg output",
                    re);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        
        LOG.debug("transcoding finished");
    }

    @Required
	public void setFfmpegLocation(String ffmpegLocation) {
		this.ffmpegLocation = ffmpegLocation;
	}

	public void setNiceLocation(String niceLocation) {
		this.niceLocation = niceLocation;
	}
}
