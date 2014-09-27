/*
 * AbstractTranscoderImpl.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author scottkidder
 * 
 */
public class AbstractTranscoderImpl {

  /**
   * Reads the process output streams
   */
  class OutputMonitor implements Callable<List<String>> {

    private InputStream in;

    public OutputMonitor(InputStream in) {
      this.in = in;
    }

    public List<String> call() throws Exception {
      ArrayList<String> lines = new ArrayList<String>();
      BufferedReader br = null;

      try {
        br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = br.readLine()) != null) {
          lines.add(line);
        }
      } finally {
        try {
          if (br != null) {
            br.close();
          }
        } catch (IOException e) {
        }
      }
      return lines;
    }

  }

}
