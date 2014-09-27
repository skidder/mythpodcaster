/*
 * RecordingsPanel.java
 * 
 * Created: Jun 23, 2010
 * 
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
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
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 * 
 */
public class RecordingsPanel extends Composite {

  private StyledListBox seriesListBox = new StyledListBox("mythpodcaster-ActiveRecordingOption");
  private TranscodingProfileSubscriptionsPanel transcodingProfileSubscriptionsPanel =
      new TranscodingProfileSubscriptionsPanel(this);

  /**
	 * 
	 */
  public RecordingsPanel() {
    super();

    VerticalPanel panel = new VerticalPanel();
    seriesListBox.setStyleName("mythpodcaster-SubscriptionsTable");

    // create change handler that updates the displayed transcoding profiles when program changes
    ChangeHandler seriesSelectionHandler = new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        handleProgramSeriesSelectionEvent();
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

    refreshData();

    initWidget(panel);
  }


  /**
   * Update the transcoding profile subscriptions panel when the listbox has been selected.
   */
  private void handleProgramSeriesSelectionEvent() {
    final int index = seriesListBox.getSelectedIndex();
    final String selectedSeries = seriesListBox.getValue(index);
    final String seriesTitle = seriesListBox.getItemText(index);
    transcodingProfileSubscriptionsPanel.update(selectedSeries, seriesTitle);
  }


  public void refreshData() {
    final int selectedSeriesIndex = seriesListBox.getSelectedIndex();

    AsyncCallback<List<RecordedSeriesDTO>> callback = new AsyncCallback<List<RecordedSeriesDTO>>() {

      @Override
      public void onSuccess(List<RecordedSeriesDTO> recordedSeriesList) {
        seriesListBox.clear();

        // add all of the recorded series to the program series listbox
        for (RecordedSeriesDTO item : recordedSeriesList) {
          if (item.isActive()) {
            seriesListBox.addItemWithStyle(item.getTitle(), item.getSeriesId());
          } else {
            seriesListBox.addItem(item.getTitle(), item.getSeriesId());
          }
        }

        if (seriesListBox.getItemCount() > 0) {
          if (selectedSeriesIndex == -1) {
            seriesListBox.setSelectedIndex(0);
          } else {
            seriesListBox.setSelectedIndex(selectedSeriesIndex);
          }

          handleProgramSeriesSelectionEvent();
        }

      }

      @Override
      public void onFailure(Throwable arg0) {
        seriesListBox.clear();
      }
    };

    // retrieve and display the list of program series
    UIControllerServiceAsync service =
        (UIControllerServiceAsync) GWT.create(UIControllerService.class);
    try {
      service.findAllRecordedSeries(callback);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
