/*
 * TranscodingProfilesDAO.java
 * 
 * Created: Feb 17, 2010
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
package net.urlgrey.mythpodcaster.dao;

import java.io.IOException;
import java.util.Map;

import net.urlgrey.mythpodcaster.xml.TranscodingProfile;

/**
 * @author scottkidder
 * 
 */
public interface TranscodingProfilesDAO {

  /**
   * 
   * @return
   */
  Map<String, TranscodingProfile> findAllProfiles();

  /**
   * 
   * @param profile
   * @throws IOException
   */
  void addTranscodingProfile(TranscodingProfile profile) throws IOException;

  /**
   * @param profileId
   */
  void removeTranscodingProfile(String profileId);

}
