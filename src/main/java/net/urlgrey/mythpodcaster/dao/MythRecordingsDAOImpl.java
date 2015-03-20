/*
 * MythRecordingsDAOImpl.java
 * 
 * Created: 2009-06-26 16:20
 * 
 * Copyright (C) 2009 Scott Kidder
 * 
 * This file is part of MythPodcaster.
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.urlgrey.mythpodcaster.domain.Channel;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class MythRecordingsDAOImpl implements MythRecordingsDAO {

  private static final String RECORDING_DIRECTORIES_CACHE_LABEL = "recording.directories";

  protected static final Logger LOGGER = Logger.getLogger(MythRecordingsDAOImpl.class);

  @PersistenceContext
  private EntityManager entityManager;

  private CacheWrapper<String, List<String>> cache;


  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findAllRecordedSeries()
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional(readOnly = true)
  public List<RecordedSeries> findAllRecordedSeries() {
    final Query namedQuery =
        entityManager.createNamedQuery("MYTH_RECORDINGS.findAllRecordedSeries");

    final List<RecordedSeries> series = namedQuery.getResultList();
    return series;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findRecordedSeries(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional(readOnly = true)
  public RecordedSeries findRecordedSeries(String seriesId) {
    final Query namedQuery = entityManager.createNamedQuery("MYTH_RECORDINGS.findRecordedSeries");
    namedQuery.setParameter("seriesId", seriesId);

    final List<RecordedSeries> seriesResultsList = namedQuery.getResultList();
    if (seriesResultsList != null && seriesResultsList.size() == 1) {
      final Query seriesProgramsQuery =
          entityManager.createNamedQuery("MYTH_RECORDINGS.findRecordedProgramsForSeries");
      seriesProgramsQuery.setParameter("seriesId", seriesId);

      final RecordedSeries series = seriesResultsList.get(0);
      series.setRecordedPrograms(seriesProgramsQuery.getResultList());
      return series;
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findRecordedSeries(java.lang.String, int)
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional(readOnly = true)
  public RecordedSeries findRecordedSeries(String seriesId, int numberOfMostRecentRecordings) {
    final Query namedQuery = entityManager.createNamedQuery("MYTH_RECORDINGS.findRecordedSeries");
    namedQuery.setParameter("seriesId", seriesId);

    final List<RecordedSeries> seriesResultsList = namedQuery.getResultList();
    if (seriesResultsList != null && seriesResultsList.size() == 1) {
      final Query seriesProgramsQuery =
          entityManager.createNamedQuery("MYTH_RECORDINGS.findRecordedProgramsForSeries");
      seriesProgramsQuery.setParameter("seriesId", seriesId);
      seriesProgramsQuery.setMaxResults(numberOfMostRecentRecordings);

      final RecordedSeries series = seriesResultsList.get(0);
      series.setRecordedPrograms(seriesProgramsQuery.getResultList());
      return series;
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findChannel(int)
   */
  @Override
  @Transactional(readOnly = true)
  public Channel findChannel(int channelId) {
    return entityManager.find(Channel.class, Integer.valueOf(channelId));
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findRecordingDirectories()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<String> findRecordingDirectories() {
    List<String> resultsList;
    if ((resultsList = cache.get(RECORDING_DIRECTORIES_CACHE_LABEL)) == null) {
      final Query nativeQuery =
          entityManager.createNativeQuery("SELECT DISTINCT dirname FROM storagegroup");
      nativeQuery.setHint("org.hibernate.comment", "MythPodcaster: findRecordingDirectories");

      resultsList = nativeQuery.getResultList();
      cache.put(RECORDING_DIRECTORIES_CACHE_LABEL, resultsList);
    }
    return resultsList;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @Required
  public void setCache(CacheWrapper<String, List<String>> cache) {
    this.cache = cache;
  }
}
