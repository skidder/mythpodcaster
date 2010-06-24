/*
 * RecordingsPanel.java
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

import java.util.List;

import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.client.service.UIControllerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 *
 */
public class RecordingsPanel extends Composite {

	private ListBox seriesListBox = new ListBox();
	private TranscodingProfileSubscriptionsPanel transcodingProfileSubscriptionsPanel = new TranscodingProfileSubscriptionsPanel();

	/**
	 * 
	 */
	public RecordingsPanel() {
		super();

		VerticalPanel panel = new VerticalPanel();
		seriesListBox.setStyleName("mythpodcaster-SubscriptionsTable");

		AsyncCallback<List<RecordedSeriesDTO>> callback = new AsyncCallback<List<RecordedSeriesDTO>>() {

			@Override
			public void onSuccess(List<RecordedSeriesDTO> recordedSeriesList) {
				for (RecordedSeriesDTO item : recordedSeriesList) {
					seriesListBox.addItem(item.getTitle(), item.getSeriesId());
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub

			}
		};


		UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
		try {
			service.findAllRecordedSeries(callback);
		} catch (Exception e) {
			e.printStackTrace();
		}


		ChangeHandler seriesSelectionHandler = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				final int index = seriesListBox.getSelectedIndex();
				final String selectedSeries = seriesListBox.getValue(index);
				final String seriesTitle = seriesListBox.getItemText(index);
				transcodingProfileSubscriptionsPanel.update(selectedSeries, seriesTitle);
			}
		};

		seriesListBox.addChangeHandler(seriesSelectionHandler);
		HorizontalPanel seriesSelectionPanel = new HorizontalPanel();
		final Label programSeriesLabel = new Label("Program Series:");
		programSeriesLabel.setStyleName("mythpodcaster-ListBox");
		seriesSelectionPanel.add(programSeriesLabel);
		seriesSelectionPanel.add(seriesListBox);
		panel.add(seriesSelectionPanel);
		panel.add(transcodingProfileSubscriptionsPanel);

		initWidget(panel);
	}

}
