/*
 * TranscodingProfilesDAOImpl.java
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
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import net.urlgrey.mythpodcaster.dto.FFMpegTranscoderConfigurationItem;
import net.urlgrey.mythpodcaster.dto.FastStartTranscoderConfigurationItem;
import net.urlgrey.mythpodcaster.dto.SegmenterTranscoderConfigurationItem;
import net.urlgrey.mythpodcaster.dto.TranscoderConfigurationItem;
import net.urlgrey.mythpodcaster.dto.TranscodingProfile;
import net.urlgrey.mythpodcaster.dto.TranscodingProfileGroup;

/**
 * @author scottkidder
 *
 */
public class TranscodingProfilesDAOImpl extends AbstractFileBasedDAO implements TranscodingProfilesDAO {

	static final Logger LOGGER = Logger.getLogger(TranscodingProfilesDAOImpl.class);
	private String transcodingProfilesFilePath;
	private JAXBContext jaxbContext;

	public TranscodingProfilesDAOImpl() {
		 try {
				jaxbContext = JAXBContext.newInstance(new Class[] {TranscodingProfile.class, TranscodingProfileGroup.class, FFMpegTranscoderConfigurationItem.class, TranscoderConfigurationItem.class, SegmenterTranscoderConfigurationItem.class, FastStartTranscoderConfigurationItem.class} );
			} catch (JAXBException e) {
				LOGGER.fatal("Unable to create JAXB Context", e);
				throw new IllegalStateException(e);
			}
	}

	@Override
	public void addTranscodingProfile(TranscodingProfile profile)
			throws IOException {
		final String profileId = profile.getId();
		LOGGER.debug("Adding encoding profile: profileId [" + profileId + "]");
		TranscodingProfileGroup profileGroup = loadTranscodingProfilesDocument();
		
		final List<TranscodingProfile> profiles = profileGroup.getProfiles();
		if (!profiles.contains(profile)) {
			profiles.add(profile);
		}

		storeTranscodingProfilesDocument(profileGroup);
	}

	@Override
	public Map<String, TranscodingProfile> findAllProfiles() {
		Map<String, TranscodingProfile> profiles = new HashMap<String, TranscodingProfile>();
		for (TranscodingProfile profile : loadTranscodingProfilesDocument().getProfiles()) {
			profiles.put(profile.getId(), profile);
		}
		
		return profiles;
	}

	@Override
	public void removeTranscodingProfile(String profileId) {
		LOGGER.debug("Removing encoding profile: profileId [" + profileId + "]");
		TranscodingProfileGroup profileGroup = loadTranscodingProfilesDocument();
		
		final List<TranscodingProfile> profiles = profileGroup.getProfiles();
		TranscodingProfile targetedProfile = null;
		for (TranscodingProfile profile : profiles) {
			if (profile.getId().equals(profileId)) {
				targetedProfile  = profile;
			}
		}
		
		if (targetedProfile != null ) {
			profiles.remove(targetedProfile);
			storeTranscodingProfilesDocument(profileGroup);
		}
	}

	private void storeTranscodingProfilesDocument(
			TranscodingProfileGroup profileGroup) {
		Collections.sort(profileGroup.getProfiles());
		storeDocument(transcodingProfilesFilePath, jaxbContext, profileGroup);
	}

	private TranscodingProfileGroup loadTranscodingProfilesDocument() {
		final File encodingProfilesFile = new File(transcodingProfilesFilePath);
		TranscodingProfileGroup encodingProfiles = null;
		if (encodingProfilesFile.exists()) {
			try {
				final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				encodingProfiles = (TranscodingProfileGroup) unmarshaller.unmarshal(encodingProfilesFile);
			} catch (JAXBException e) {
				LOGGER.error("Unable to unmarshal transcoding profiles document from XML", e);
			}

			if (encodingProfiles == null) {
				encodingProfiles = new TranscodingProfileGroup();
			}
		} else {
			encodingProfiles = new TranscodingProfileGroup();
		}
		return encodingProfiles;
	}

	@Required
	public void setTranscodingProfilesFilePath(String transcodingProfilesFilePath) {
		this.transcodingProfilesFilePath = transcodingProfilesFilePath;
	}

}
