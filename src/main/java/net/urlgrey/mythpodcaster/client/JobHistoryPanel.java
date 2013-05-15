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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
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
	private HandlerRegistration columnSortHandlerRef;

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
				if (object.getStartedAt() != null) {
					final DateTimeFormat dateFormatter = DateTimeFormat
							.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
					return dateFormatter.format(object.getStartedAt());
				} else {
					return "";
				}
			}
		};
		table.addColumn(startedAtColumn, "Start-Time");

		TextColumn<JobHistoryItemDTO> finishedAtColumn = new TextColumn<JobHistoryItemDTO>() {
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
				// remove the existing handler, not sure if this is necessary, but the handler is initialized with the list, so...
				if (columnSortHandlerRef != null)
					columnSortHandlerRef.removeHandler();

				final ListHandler<JobHistoryItemDTO> listHandler = new ColumnSortEvent.ListHandler<JobHistoryItemDTO>(history);
				listHandler.setComparator(table.getColumn(4), new Comparator<JobHistoryItemDTO>() {
					
					@Override
					public int compare(JobHistoryItemDTO o1, JobHistoryItemDTO o2) {
						if (o1.getStartedAt() != null) {
							return (-1) * o1.getStartedAt().compareTo(o2.getStartedAt());
						}
						return -1;
					}
				});
				columnSortHandlerRef = table.addColumnSortHandler(listHandler);
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
