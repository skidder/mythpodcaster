/*
 * StyledListBox.java
 *
 * Created: Jul 2, 2010
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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author scottkidder
 *
 */
public class StyledListBox extends ListBox {

	private String styleName;

	/**
	 * 
	 */
	public StyledListBox(String styleName) {
		this.styleName = styleName;
	}

	/**
	 * @param isMultipleSelect
	 */
	public StyledListBox(String styleName, boolean isMultipleSelect) {
		super(isMultipleSelect);
		this.styleName = styleName;
	}

	/**
	 * @param element
	 */
	public StyledListBox(String styleName, Element element) {
		super(element);
		this.styleName = styleName;
	}

	public void addItemWithStyle(String item, String value) {
		final SelectElement select = getElement().cast();
		final OptionElement option = Document.get().createOptionElement();
		option.setText(item);
		option.setValue(value);
		option.setClassName(styleName);

		select.add(option, null);
	}
}
