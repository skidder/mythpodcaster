/*
 * MythRecordingsDAOImpl.java
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.urlgrey.mythpodcaster.domain.Channel;
import net.urlgrey.mythpodcaster.domain.RecordedProgram;
import net.urlgrey.mythpodcaster.domain.RecordedSeries;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

public class MythRecordingsDAOImpl implements MythRecordingsDAO {

    protected static final Logger LOGGER = Logger.getLogger(MythRecordingsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    
	/* (non-Javadoc)
	 * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findAllRecordedSeries()
	 */
	@Override
    @Transactional(readOnly=true)
	public List<RecordedSeries> findAllRecordedSeries() {
    	final Query namedQuery = entityManager.createNamedQuery("MYTH_RECORDINGS.findAllRecordedSeries");

		final List<RecordedSeries> series = namedQuery.getResultList();
		return series;
	}

	/* (non-Javadoc)
	 * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findRecordedSeries(java.lang.String)
	 */
	@Override
    @Transactional(readOnly=true)
	public RecordedSeries findRecordedSeries(String seriesId) {
    	final Query namedQuery = entityManager.createNamedQuery("MYTH_RECORDINGS.findRecordedSeries");
    	namedQuery.setParameter("seriesId", seriesId);

    	final List<RecordedSeries> resultsList = namedQuery.getResultList();
    	if (resultsList != null && resultsList.size() == 1) {
    		return resultsList.get(0);
    	}

		return null;
	}

	/* (non-Javadoc)
	 * @see net.urlgrey.mythpodcaster.dao.MythRecordingsDAO#findChannel(int)
	 */
	@Override
    @Transactional(readOnly=true)
	public Channel findChannel(int channelId) {
		return entityManager.find(Channel.class, Integer.valueOf(channelId));
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

}
