/*
 * RemoteComposite.java
 *
 * Created: May 14, 2013
 *
 * Copyright (C) 2013 Scott Kidder
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
package net.urlgrey.mythpodcaster.client;

import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.client.service.UIControllerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author skidder
 * 
 */
public abstract class RemoteComposite extends Composite {

	protected String applicationUrl = null;

	protected void retrieveApplicationURL() {
		UIControllerServiceAsync service = (UIControllerServiceAsync) GWT
				.create(UIControllerService.class);
		try {
			service.retrieveApplicationUrl(new AsyncCallback<String>() {

				@Override
				public void onSuccess(String arg0) {
					applicationUrl = arg0;
				}

				@Override
				public void onFailure(Throwable arg0) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
