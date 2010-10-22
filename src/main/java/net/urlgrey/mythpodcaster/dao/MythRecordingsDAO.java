/*
 * MythRecordingsDAO.java
 *
 * Created: 2009-06-26 16:20
 *
 * Copyright (C) 2009 Scott Kidder
 * 
 * This file is part of MythPodcaster.
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


package net.urlgrey.mythpodcaster.dao;

import java.util.List;

import net.urlgrey.mythpodcaster.domain.Channel;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;


public interface MythRecordingsDAO {

	List <RecordedSeries> findAllRecordedSeries();

	RecordedSeries findRecordedSeries(String seriesId);

	Channel findChannel(int channelId);

	List<String> findRecordingDirectories();

	RecordedSeries findRecordedSeries(String seriesId, int numberOfMostRecentRecordings);
}
