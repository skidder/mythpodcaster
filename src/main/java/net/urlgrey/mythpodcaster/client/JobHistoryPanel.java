/*
 * JobHistoryPanel.java
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

import java.util.List;

import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.client.service.UIControllerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author skidder
 * 
 */
public class JobHistoryPanel extends RemoteComposite {

	private VerticalPanel panel = new VerticalPanel();
	private CellTable<JobHistoryItemDTO> table = new CellTable<JobHistoryItemDTO>();

	/**
	 * 
	 */
	public JobHistoryPanel() {
		table.setStyleName("mythpodcaster-SubscriptionsTable");

		// initialize the table columns
		TextColumn<JobHistoryItemDTO> statusColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				return object.getStatus();
			}
		};
		table.addColumn(statusColumn, "Status");

		TextColumn<JobHistoryItemDTO> programNameColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				return object.getTranscodingProgramName();
			}
		};
		table.addColumn(programNameColumn, "Program Name");

		TextColumn<JobHistoryItemDTO> programIDColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				return object.getTranscodingProgramEpisodeName();
			}
		};
		table.addColumn(programIDColumn, "Program ID");

		TextColumn<JobHistoryItemDTO> transcodingProfileColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				return object.getTranscodingProfileName();
			}
		};
		table.addColumn(transcodingProfileColumn,
				"Transcoding Profile");

		TextColumn<JobHistoryItemDTO> startedAtColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				final DateTimeFormat dateFormatter = DateTimeFormat
						.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
				return dateFormatter.format(object.getStartedAt());
			}
		};
		table.addColumn(startedAtColumn, "Start-Time");

		TextColumn<JobHistoryItemDTO> finishedAtColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				final DateTimeFormat dateFormatter = DateTimeFormat
						.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
				return dateFormatter.format(object.getFinishedAt());
			}
		};
		table.addColumn(finishedAtColumn, "End-Time");

		// add the table to the panel
		panel.add(table);
		initWidget(panel);
	}

	public void refreshData() {
		if (this.applicationUrl == null) {
			retrieveApplicationURL();
		}

		AsyncCallback<List<JobHistoryItemDTO>> callback = new AsyncCallback<List<JobHistoryItemDTO>>() {

			@Override
			public void onSuccess(List<JobHistoryItemDTO> history) {


				// Set the total row count. This isn't strictly necessary, but
				// it affects
				// paging calculations, so its good habit to keep the row count
				// up to date.
				table.setRowCount(history.size(), true);

				// Push the data into the widget.
				table.setRowData(0, history);
				table.setVisible(true);
			}

			@Override
			public void onFailure(Throwable arg0) {

			}
		};

		table.setVisible(false);

		UIControllerServiceAsync service = (UIControllerServiceAsync) GWT
				.create(UIControllerService.class);
		try {
			service.retrieveJobHistory(callback);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
