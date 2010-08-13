/*
 * TranscodingProfileSubscriptionsPanel.java
 *
 * Created: Jun 23, 2010
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
import java.util.List;

import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.client.service.UIControllerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 *
 */
public class TranscodingProfileSubscriptionsPanel extends Composite {

	private RecordingsPanel parent;
	private VerticalPanel panel = new VerticalPanel();
	private VerticalPanel subscriptionsPanel = new VerticalPanel();
	private Label subscriptionsLabel = new Label("Subscriptions:");
	private Grid subscriptionsTable = new Grid();
	private Button addSubscriptionButton = new Button("Add Subscription");
	private String applicationUrl = null;
	private String seriesId = null;
	private String seriesTitle = null;

	/**
	 * 
	 */
	public TranscodingProfileSubscriptionsPanel(RecordingsPanel parent) {
		this.parent = parent;
		subscriptionsTable.resizeColumns(4);
		subscriptionsTable.setStyleName("mythpodcaster-SubscriptionsTable");

		subscriptionsPanel.add(subscriptionsLabel);
		subscriptionsPanel.add(subscriptionsTable);
		subscriptionsLabel.setVisible(false);
		subscriptionsTable.setVisible(false);

		subscriptionsPanel.setStyleName("mythpodcaster-SubscriptionsPanel");
		addSubscriptionButton.addClickHandler(new AddSubscriptionHandler());

		panel.add(subscriptionsPanel);
		panel.add(addSubscriptionButton);
		initWidget(panel);
	}

	public void update(String seriesId, String seriesTitle) {
		this.seriesId = seriesId;
		this.seriesTitle = seriesTitle;

		refreshData();
	}

	public void refreshData() {
		if (this.applicationUrl == null) {
			UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
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

		AsyncCallback<List<FeedSubscriptionItemDTO>> callback = new AsyncCallback<List<FeedSubscriptionItemDTO>>() {


			@Override
			public void onSuccess(List<FeedSubscriptionItemDTO> subscriptions) {
				subscriptionsTable.resizeRows(subscriptions.size());

				int i = 0;
				for (FeedSubscriptionItemDTO item : subscriptions) {
					subscriptionsTable.setText(i, 0, item.getTranscodeProfileDisplayName());
					subscriptionsTable.setWidget(i, 1, new HTML("<a target=_blank href=\"" + applicationUrl + "/" + item.getTranscodeProfile() + "/" + seriesId + ".rss\">Feed</a>"));
					subscriptionsTable.setWidget(i, 2, new HTML("<a target=_blank href=\"" + applicationUrl + "/" + item.getTranscodeProfile() + "/" + seriesId + ".html\">HTML</a>"));
					final Button unsubscribeButton = new Button("Unsubscribe");
					unsubscribeButton.addClickHandler(new UnsubscribeHandler(item.getTranscodeProfile()));
					subscriptionsTable.setWidget(i++, 3, unsubscribeButton);
				}

				if (subscriptions.size() > 0) {
					subscriptionsLabel.setVisible(true);
				}
				subscriptionsTable.setVisible(true);
			}

			@Override
			public void onFailure(Throwable arg0) {

			}
		};

		subscriptionsTable.clear();
		subscriptionsLabel.setVisible(false);
		subscriptionsTable.setVisible(false);

		UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
		try {
			service.findSubscriptionsForSeries(seriesId, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class UnsubscribeHandler implements ClickHandler {
		private String transcodingProfile;

		public UnsubscribeHandler(String transcodingProfile) {
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

	private class AddSubscriptionHandler implements ClickHandler {

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
}
