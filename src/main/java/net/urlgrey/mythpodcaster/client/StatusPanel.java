/*
 * StatusPanel.java
 *
 * Created: Jul 15, 2010
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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 *
 */
public class StatusPanel extends Composite {

	private Label statusLabel;
	private Label statusValueLabel;
	private Label nextTriggerStartLabel;
	private Label nextTriggerStartValueLabel;
	private Label currentTriggerValueLabel;
	private Label currentTriggerLabel;
	private Label currentTranscodeStartLabel;
	private Label currentTranscodeStartValueLabel;
	private Label transcodingProfileLabel;
	private Label transcodingProfileValueLabel;
    private Label transcodingSeriesTitleLabel;
    private Label transcodingSeriesTitleValueLabel;
	private Label transcodingProgramNameLabel;
	private Label transcodingProgramNameValueLabel;
	private Label transcodingProgramEpisodeNameLabel;
	private Label transcodingProgramEpisodeNameValueLabel;
	private DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_LONG);
	private Timer timer;

	/**
	 * 
	 */
	public StatusPanel() {
		super();

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("mythpodcaster-StatusPanel");

		statusLabel = new Label("Status:");
		statusLabel.setStyleName("mythpodcaster-StatusPanelLabel");
		statusValueLabel = new Label();
		statusValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

		HorizontalPanel currentStatusRow = new HorizontalPanel();
		currentStatusRow.add(statusLabel);
		currentStatusRow.add(statusValueLabel);
		panel.add(currentStatusRow);

		nextTriggerStartLabel = new Label("Next Trigger Start-time:");
		nextTriggerStartLabel.setStyleName("mythpodcaster-StatusPanelLabel");
		nextTriggerStartValueLabel = new Label();
		nextTriggerStartValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

		HorizontalPanel nextTriggerRow = new HorizontalPanel();
		nextTriggerRow.add(nextTriggerStartLabel);
		nextTriggerRow.add(nextTriggerStartValueLabel);
		panel.add(nextTriggerRow);

		currentTriggerLabel = new Label("Current Trigger Start-time:");
		currentTriggerLabel.setStyleName("mythpodcaster-StatusPanelLabel");
		currentTriggerValueLabel = new Label();
		currentTriggerValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

		HorizontalPanel currentTriggerRow = new HorizontalPanel();
		currentTriggerRow.add(currentTriggerLabel);
		currentTriggerRow.add(currentTriggerValueLabel);
		panel.add(currentTriggerRow);

		currentTranscodeStartLabel = new Label("Current Transcode Start-time:");
		currentTranscodeStartLabel.setStyleName("mythpodcaster-StatusPanelLabel");
		currentTranscodeStartValueLabel = new Label();
		currentTranscodeStartValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

		HorizontalPanel currentTranscodeStartRow = new HorizontalPanel();
		currentTranscodeStartRow.add(currentTranscodeStartLabel);
		currentTranscodeStartRow.add(currentTranscodeStartValueLabel);
		panel.add(currentTranscodeStartRow);

		transcodingProfileLabel = new Label("Transcoding Profile:");
		transcodingProfileLabel.setStyleName("mythpodcaster-StatusPanelLabel");
		transcodingProfileValueLabel = new Label();
		transcodingProfileValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

		HorizontalPanel transcodingProfileRow = new HorizontalPanel();
		transcodingProfileRow.add(transcodingProfileLabel);
		transcodingProfileRow.add(transcodingProfileValueLabel);
		panel.add(transcodingProfileRow);

		transcodingSeriesTitleLabel = new Label("Series:");
		transcodingSeriesTitleLabel.setStyleName("mythpodcaster-StatusPanelLabel");
        transcodingSeriesTitleValueLabel = new Label();
        transcodingSeriesTitleValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

        HorizontalPanel transcodingSeriesTitleRow = new HorizontalPanel();
        transcodingSeriesTitleRow.add(transcodingSeriesTitleLabel);
        transcodingSeriesTitleRow.add(transcodingSeriesTitleValueLabel);
        panel.add(transcodingSeriesTitleRow);

        transcodingProgramNameLabel = new Label("Program Name:");
        transcodingProgramNameLabel.setStyleName("mythpodcaster-StatusPanelLabel");
        transcodingProgramNameValueLabel = new Label();
        transcodingProgramNameValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

        HorizontalPanel transcodingProgramNameRow = new HorizontalPanel();
        transcodingProgramNameRow.add(transcodingProgramNameLabel);
        transcodingProgramNameRow.add(transcodingProgramNameValueLabel);
        panel.add(transcodingProgramNameRow);

		transcodingProgramEpisodeNameLabel = new Label("Program Id:");
		transcodingProgramEpisodeNameLabel.setStyleName("mythpodcaster-StatusPanelLabel");
		transcodingProgramEpisodeNameValueLabel = new Label();
		transcodingProgramEpisodeNameValueLabel.setStyleName("mythpodcaster-StatusPanelValue");

		HorizontalPanel transcodingProgramEpisodeNameRow = new HorizontalPanel();
		transcodingProgramEpisodeNameRow.add(transcodingProgramEpisodeNameLabel);
		transcodingProgramEpisodeNameRow.add(transcodingProgramEpisodeNameValueLabel);
		panel.add(transcodingProgramEpisodeNameRow);

		timer = new Timer() {

			@Override
			public void run() {
				refreshData();
			}
		};

		timer.scheduleRepeating(30000);
		refreshData();
		this.initWidget(panel);
	}

	private void refreshData() {
		AsyncCallback<StatusDTO> callback = new AsyncCallback<StatusDTO>() {

            @Override
			public void onSuccess(StatusDTO status) {
				if ("IDLE".equalsIgnoreCase(status.getMode())) {
					statusValueLabel.setText("Idle");
					nextTriggerStartLabel.setVisible(true);
					nextTriggerStartValueLabel.setText(dateFormatter.format(status.getNextTriggerStart()));
					nextTriggerStartValueLabel.setVisible(true);
					currentTriggerLabel.setVisible(false);
					currentTriggerValueLabel.setVisible(false);
					currentTranscodeStartLabel.setVisible(false);
					currentTranscodeStartValueLabel.setVisible(false);
					transcodingProfileValueLabel.setVisible(false);
					transcodingProfileLabel.setVisible(false);
					transcodingProgramEpisodeNameLabel.setVisible(false);
					transcodingProgramEpisodeNameValueLabel.setVisible(false);
					transcodingProgramNameLabel.setVisible(false);
					transcodingProgramNameValueLabel.setVisible(false);
				} else if ("TRANSCODING".equalsIgnoreCase(status.getMode())) {
					statusValueLabel.setText("Transcoding");
					currentTriggerLabel.setVisible(true);
					currentTriggerValueLabel.setText(dateFormatter.format(status.getCurrentTriggerStart()));
					currentTriggerValueLabel.setVisible(true);
					nextTriggerStartLabel.setVisible(false);
					nextTriggerStartValueLabel.setVisible(false);
					if (status.getCurrentTranscodeStart() != null) {
						currentTranscodeStartLabel.setVisible(true);
						currentTranscodeStartValueLabel.setText(dateFormatter.format(status.getCurrentTranscodeStart()));
						currentTranscodeStartValueLabel.setVisible(true);
						transcodingProfileLabel.setVisible(true);
						transcodingProfileValueLabel.setText(status.getTranscodingProfileName());
						transcodingProfileValueLabel.setVisible(true);
						transcodingProgramEpisodeNameLabel.setVisible(true);
						transcodingProgramEpisodeNameValueLabel.setText(status.getTranscodingProgramEpisodeName());
						transcodingProgramEpisodeNameValueLabel.setVisible(true);
						transcodingProgramNameLabel.setVisible(true);
                        transcodingProgramNameValueLabel.setText(status.getTranscodingProgramName());
                        transcodingProgramNameValueLabel.setVisible(true);
                        transcodingSeriesTitleValueLabel.setText(status.getTranscodingSeriesTitle());
                        transcodingSeriesTitleValueLabel.setVisible(true);
					} else {
						currentTranscodeStartLabel.setVisible(false);
						currentTranscodeStartValueLabel.setVisible(false);
						transcodingProfileValueLabel.setVisible(false);
						transcodingProfileLabel.setVisible(false);
						transcodingProgramEpisodeNameLabel.setVisible(false);
						transcodingProgramEpisodeNameValueLabel.setVisible(false);
						transcodingProgramNameLabel.setVisible(false);
						transcodingProgramNameValueLabel.setVisible(false);
						transcodingSeriesTitleValueLabel.setVisible(false);
					}
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub

			}

		};

		UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
		try {
			service.retrieveStatus(callback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
