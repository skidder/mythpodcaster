/*
 * MainWindow.java
 *
 * Created: Jun 22, 2010
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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 *
 */
public class MainWindow extends Composite {
	final int RECORDINGS_TAB_INDEX = 0;
	final int JOB_HISTORY_TAB_INDEX = 1;

	private Label logoLabel = new Label();
	private TabPanel tabPanel = new TabPanel();
	private StatusPanel statusPanel = new StatusPanel();
	private JobHistoryPanel jobHistoryPanel = new JobHistoryPanel();
	private RecordingsPanel recordingsPanel = new RecordingsPanel();

	public MainWindow() {
		super();

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setStyleName("mythpodcaster-MainPanel");
		logoLabel.setText("MythPodcaster");
		logoLabel.setStyleName("mythpodcaster-Header");
		tabPanel.add(recordingsPanel, "Recordings");
		tabPanel.add(jobHistoryPanel, "Job History");
		tabPanel.selectTab(RECORDINGS_TAB_INDEX);
		tabPanel.setSize("100%", "100%");
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				switch (event.getSelectedItem()) {
				case RECORDINGS_TAB_INDEX:
					recordingsPanel.refreshData();
					break;
				case JOB_HISTORY_TAB_INDEX:
					jobHistoryPanel.refreshData();
					break;
				}
			}
		});
		
		mainPanel.add(logoLabel);
		mainPanel.add(statusPanel);
		mainPanel.add(tabPanel);
		initWidget(mainPanel);
	}
}
