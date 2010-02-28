/*
 * AbstractFileBasedDAO.java
 *
 * Created: Feb 17, 2010
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
package net.urlgrey.mythpodcaster.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

/**
 * @author scottkidder
 *
 */
public class AbstractFileBasedDAO {

	private static final Logger LOGGER = Logger.getLogger(AbstractFileBasedDAO.class);

	protected void storeDocument(String filePath, JAXBContext jaxbContext, Object document) {
		final File subscriptionsFile = new File(filePath);
		try {
			final Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			FileOutputStream os = new FileOutputStream(subscriptionsFile);
			marshaller.marshal(document, os);
		} catch (FileNotFoundException e) {
			LOGGER.error("Unable to write object to file: " + filePath, e);
			return;
		} catch (JAXBException e) {
			LOGGER.error("Unable to write object to file: " + filePath, e);
			return;
		}
	}

}
