/**
 * UnsubscribeHandler.java
 *
 * Created: Oct 8, 2010
 *
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 *
 */
public class UnsubscribeHandler implements ClickHandler {
	private String transcodingProfile;
	private RecordingsPanel parent;
	private String seriesId;

	
	public UnsubscribeHandler(RecordingsPanel parent, String seriesId,
			String transcodingProfile) {
		this.parent = parent;
		this.seriesId = seriesId;
		this.transcodingProfile = transcodingProfile;
	}


	@Override
	public void onClick(ClickEvent event) {
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("Unsubscribe?");

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		// Add some text to the top of the dialog
		HTML details = new HTML("Are you sure you want to unsubscribe?");
		dialogContents.add(details);
		dialogContents.setCellHorizontalAlignment(details,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Add a cancel button at the bottom of the dialog
		final Button cancelButton = new Button("Cancel",
				new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		// Add a cancel button at the bottom of the dialog
		final Button okButton = new Button("OK",
				new ClickHandler() {
			public void onClick(ClickEvent event) {
				UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
				try {
					service.removeSubscription(seriesId, transcodingProfile, new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable arg0) {
							parent.refreshData();
						}

						@Override
						public void onSuccess(Boolean arg0) {
							parent.refreshData();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				dialogBox.hide();
			}
		});

		final SimplePanel buttonTopSpacer = new SimplePanel();
		buttonTopSpacer.setHeight("20px");

		final SimplePanel buttonSpacer = new SimplePanel();
		buttonSpacer.setWidth("30px");

		HorizontalPanel buttonRow = new HorizontalPanel();
		buttonRow.add(cancelButton);
		buttonRow.add(buttonSpacer);
		buttonRow.add(okButton);
		dialogContents.add(buttonTopSpacer);
		dialogContents.add(buttonRow);
		dialogContents.setCellHorizontalAlignment(buttonRow,
				HasHorizontalAlignment.ALIGN_CENTER);
		if (LocaleInfo.getCurrentLocale().isRTL()) {
			dialogContents.setCellHorizontalAlignment(cancelButton,
					HasHorizontalAlignment.ALIGN_LEFT);

		} else {
			dialogContents.setCellHorizontalAlignment(cancelButton,
					HasHorizontalAlignment.ALIGN_RIGHT);
		}

		dialogBox.center();
		dialogBox.show();
	}
}
