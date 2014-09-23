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

import java.util.Comparator;
import java.util.List;

import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.client.service.UIControllerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
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
    private TextColumn<JobHistoryItemDTO> startedAtColumn;
    private TextColumn<JobHistoryItemDTO> statusColumn;
    private TextColumn<JobHistoryItemDTO> seriesTitleColumn;
    private TextColumn<JobHistoryItemDTO> programNameColumn;
    private TextColumn<JobHistoryItemDTO> programIDColumn;
    private TextColumn<JobHistoryItemDTO> transcodingProfileColumn;
    private TextColumn<JobHistoryItemDTO> finishedAtColumn;

	/**
	 * 
	 */
	public JobHistoryPanel() {
		table.setStyleName("mythpodcaster-SubscriptionsTable");

		statusColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				return object.getStatus();
			}
		};
		statusColumn.setSortable(false);
		table.addColumn(statusColumn, "Status");

        seriesTitleColumn = new TextColumn<JobHistoryItemDTO>() {
            @Override
            public String getValue(JobHistoryItemDTO object) {
                return object.getTranscodingSeriesTitle();
            }
        };
        seriesTitleColumn.setSortable(false);
        table.addColumn(seriesTitleColumn, "Series");

        programNameColumn = new TextColumn<JobHistoryItemDTO>() {
            @Override
            public String getValue(JobHistoryItemDTO object) {
                return object.getTranscodingProgramName();
            }
        };
        programNameColumn.setSortable(false);
        table.addColumn(programNameColumn, "Program Name");

		programIDColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				return object.getTranscodingProgramKey();
			}
		};
		programIDColumn.setSortable(false);
		table.addColumn(programIDColumn, "Program ID");

		transcodingProfileColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				return object.getTranscodingProfileName();
			}
		};
		transcodingProfileColumn.setSortable(false);
		table.addColumn(transcodingProfileColumn,
				"Transcoding Profile");

		startedAtColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				if (object.getStartedAt() != null) {
					final DateTimeFormat dateFormatter = DateTimeFormat
							.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
					return dateFormatter.format(object.getStartedAt());
				} else {
					return "";
				}
			}
		};
		startedAtColumn.setSortable(false);
		table.addColumn(startedAtColumn, "Start-Time");

        finishedAtColumn = new TextColumn<JobHistoryItemDTO>() {
			@Override
			public String getValue(JobHistoryItemDTO object) {
				if (object.getFinishedAt() != null) {
					final DateTimeFormat dateFormatter = DateTimeFormat
							.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
					return dateFormatter.format(object.getFinishedAt());
				} else {
					return "";
				}
			}
		};
		finishedAtColumn.setSortable(false);
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
				table.setRowData(history);
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
