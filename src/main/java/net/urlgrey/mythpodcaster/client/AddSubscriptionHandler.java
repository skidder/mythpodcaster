/**
 * AddSubscriptionHandler.java
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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.client.service.UIControllerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 *
 */
public class AddSubscriptionHandler implements ClickHandler {

	private static final String SCOPE_ALL = "ALL";
	private static final String SCOPE_MOST_RECENT = "MOST_RECENT";
	private static final String SCOPE_SPECIFIC_RECORDINGS = "SPECIFIC_RECORDINGS";

	private RecordingsPanel parent;
	private String seriesId;
	private String seriesTitle;
	
	public AddSubscriptionHandler(RecordingsPanel parent, String seriesId,
			String seriesTitle) {
		this.parent = parent;
		this.seriesId = seriesId;
		this.seriesTitle = seriesTitle;
	}
	
	@Override
	public void onClick(ClickEvent arg0) {
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("Add Transcoding Profile Subscription");

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		// Add some text to the top of the dialog
		HorizontalPanel listBoxPanel = new HorizontalPanel();
		listBoxPanel.add(new HTML("Transcoding Profile:&nbsp;"));
		final ListBox profileListBox = new ListBox();
		listBoxPanel.add(profileListBox);
		dialogContents.add(listBoxPanel);

		final HorizontalPanel mostRecentPanel = new HorizontalPanel();
		mostRecentPanel.add(new HTML("Number of most recent to transcode:&nbsp;"));
	    final ListBox mostRecentListBox = new ListBox();
	    mostRecentListBox.addItem("1");
	    mostRecentListBox.addItem("2");
	    mostRecentListBox.addItem("3");
	    mostRecentListBox.addItem("4");
	    mostRecentListBox.addItem("5");
	    mostRecentListBox.addItem("6");
	    mostRecentListBox.addItem("7");
	    mostRecentListBox.addItem("8");
	    mostRecentListBox.addItem("9");
	    mostRecentListBox.addItem("10");
	    mostRecentPanel.add(mostRecentListBox);
		mostRecentPanel.setVisible(false);

		final HorizontalPanel specificRecordingsPanel = new HorizontalPanel();
		specificRecordingsPanel.add(new HTML("Specific Recordings:&nbsp;"));
	    final ListBox recordingsListBox = new ListBox(true);
	    recordingsListBox.setVisibleItemCount(5);
	    specificRecordingsPanel.add(recordingsListBox);
		specificRecordingsPanel.setVisible(false);

		HorizontalPanel scopePanel = new HorizontalPanel();
		scopePanel.add(new HTML("Scope:&nbsp;"));
		final ListBox scopeListBox = new ListBox();
		scopeListBox.addItem("All recordings", SCOPE_ALL);
		scopeListBox.addItem("Number of Most Recent Recordings", SCOPE_MOST_RECENT);
		scopeListBox.addItem("Specific Recordings", SCOPE_SPECIFIC_RECORDINGS);
		scopePanel.add(scopeListBox);

		scopeListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent arg0) {
				final String selectedScopeValue = scopeListBox.getValue(scopeListBox.getSelectedIndex());
				if (SCOPE_ALL.equals(selectedScopeValue)) {
					mostRecentPanel.setVisible(false);
					specificRecordingsPanel.setVisible(false);
				}
				else if (SCOPE_MOST_RECENT.equals(selectedScopeValue)) {
					mostRecentPanel.setVisible(true);
					specificRecordingsPanel.setVisible(false);
				}
				else if (SCOPE_SPECIFIC_RECORDINGS.equals(selectedScopeValue)) {
					mostRecentPanel.setVisible(false);
					UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);

					try {
						service.listRecordingsForSeries(seriesId, new AsyncCallback<List<String[]>>() {

							@Override
							public void onFailure(Throwable arg0) {
							}

							@Override
							public void onSuccess(List<String[]> recordings) {
								recordingsListBox.clear();
								final DateTimeFormat format = DateTimeFormat.getMediumDateTimeFormat();
								for (String[] recording : recordings) {
									Date d = new Date(Long.valueOf(recording[2]));
									final String recordingTitle = (recording[1] != null && recording[1].trim().length() > 0) ? recording[1] : seriesTitle;
									final String label = "[" + format.format(d) + "] " + recordingTitle;
									recordingsListBox.addItem(label, recording[0]);
								}
								specificRecordingsPanel.setVisible(true);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		dialogContents.add(scopePanel);
		dialogContents.add(mostRecentPanel);
		dialogContents.add(specificRecordingsPanel);

		dialogContents.setCellHorizontalAlignment(listBoxPanel,
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
					final FeedSubscriptionItemDTO item = new FeedSubscriptionItemDTO();
					item.setDateAdded(new Date());
					item.setSeriesId(seriesId);
					item.setTitle(seriesTitle);
					item.setTranscodeProfile(profileListBox.getValue(profileListBox.getSelectedIndex()));
					item.setScope(scopeListBox.getValue(scopeListBox.getSelectedIndex()));
					if (SCOPE_MOST_RECENT.equals(item.getScope())) {
						item.setNumberOfMostRecentToKeep(
								Integer.parseInt(
										mostRecentListBox.getValue(mostRecentListBox.getSelectedIndex())));
					} else if (SCOPE_SPECIFIC_RECORDINGS.equals(item.getScope())) {
						final Set <String> selectedRecordings = new HashSet<String>();
						final int recordingCount = recordingsListBox.getItemCount();
						for (int i=0; i < recordingCount; i++) {
							if (recordingsListBox.isItemSelected(i)) {
								selectedRecordings.add(recordingsListBox.getValue(i));
							}
						}

						final String[] result = selectedRecordings.toArray(new String[0]);
						item.setRecordedProgramKeys(result);
					}

					service.addSubscription(item, new AsyncCallback<Boolean>() {

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

		UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
		try {
			service.findAvailableTranscodingProfilesForSeries(seriesId, new AsyncCallback<List<String[]>>() {

				@Override
				public void onFailure(Throwable arg0) {

				}

				@Override
				public void onSuccess(List<String[]> profiles) {
					for (String[] profile: profiles) {
						profileListBox.addItem(profile[1], profile[0]);
					}

					dialogBox.center();
					dialogBox.show();
				}
			});
		} catch (Exception e) {
		}

	}
}
