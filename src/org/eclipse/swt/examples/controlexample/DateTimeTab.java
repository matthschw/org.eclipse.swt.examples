/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.examples.controlexample;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

class DateTimeTab extends Tab {
	/* Example widgets and groups that contain them */
	DateTime dateTime1;
	Group dateTimeGroup;

	/* Style widgets added to the "Style" group */
	Button dateButton, timeButton, calendarButton, shortButton, mediumButton, longButton, dropDownButton, weekNumbersButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	DateTimeTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the list */
		this.dateTimeGroup = new Group (this.exampleGroup, SWT.NONE);
		this.dateTimeGroup.setLayout (new GridLayout ());
		this.dateTimeGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.dateTimeGroup.setText ("DateTime");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.dateButton.getSelection ()) {
      style |= SWT.DATE;
    }
		if (this.timeButton.getSelection ()) {
      style |= SWT.TIME;
    }
		if (this.calendarButton.getSelection ()) {
      style |= SWT.CALENDAR;
    }
		if (this.shortButton.getSelection ()) {
      style |= SWT.SHORT;
    }
		if (this.mediumButton.getSelection ()) {
      style |= SWT.MEDIUM;
    }
		if (this.longButton.getSelection ()) {
      style |= SWT.LONG;
    }
		if (this.dropDownButton.getSelection ()) {
      style |= SWT.DROP_DOWN;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }
		if (this.weekNumbersButton.getSelection ()) {
      style |= SWT.CALENDAR_WEEKNUMBERS;
    }

		/* Create the example widgets */
		this.dateTime1 = new DateTime (this.dateTimeGroup, style);
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.dateButton = new Button(this.styleGroup, SWT.RADIO);
		this.dateButton.setText("SWT.DATE");
		this.timeButton = new Button(this.styleGroup, SWT.RADIO);
		this.timeButton.setText("SWT.TIME");
		this.calendarButton = new Button(this.styleGroup, SWT.RADIO);
		this.calendarButton.setText("SWT.CALENDAR");
		final Group formatGroup = new Group(this.styleGroup, SWT.NONE);
		formatGroup.setLayout(new GridLayout());
		this.shortButton = new Button(formatGroup, SWT.RADIO);
		this.shortButton.setText("SWT.SHORT");
		this.mediumButton = new Button(formatGroup, SWT.RADIO);
		this.mediumButton.setText("SWT.MEDIUM");
		this.longButton = new Button(formatGroup, SWT.RADIO);
		this.longButton.setText("SWT.LONG");
		this.dropDownButton = new Button(this.styleGroup, SWT.CHECK);
		this.dropDownButton.setText("SWT.DROP_DOWN");
		this.weekNumbersButton = new Button(this.styleGroup, SWT.CHECK);
		this.weekNumbersButton.setText("SWT.CALENDAR_WEEKNUMBERS");
		this.borderButton = new Button(this.styleGroup, SWT.CHECK);
		this.borderButton.setText("SWT.BORDER");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.dateTime1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Day", "Hours", "Minutes", "Month", "Seconds", "Year"};
	}

	/**
	 * Gets the short text for the tab folder item.
	 */
	@Override
	String getShortTabText() {
		return "DT";
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "DateTime";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.dateButton.setSelection ((this.dateTime1.getStyle () & SWT.DATE) != 0);
		this.timeButton.setSelection ((this.dateTime1.getStyle () & SWT.TIME) != 0);
		this.calendarButton.setSelection ((this.dateTime1.getStyle () & SWT.CALENDAR) != 0);
		this.shortButton.setSelection ((this.dateTime1.getStyle () & SWT.SHORT) != 0);
		this.mediumButton.setSelection ((this.dateTime1.getStyle () & SWT.MEDIUM) != 0);
		this.longButton.setSelection ((this.dateTime1.getStyle () & SWT.LONG) != 0);
		if ((this.dateTime1.getStyle() & SWT.DATE) != 0) {
			this.dropDownButton.setEnabled(true);
			this.dropDownButton.setSelection ((this.dateTime1.getStyle () & SWT.DROP_DOWN) != 0);
		} else {
			this.dropDownButton.setSelection(false);
			this.dropDownButton.setEnabled(false);
		}
		if (((this.dateTime1.getStyle() & SWT.CALENDAR) != 0) || ((this.dateTime1.getStyle() & SWT.DROP_DOWN) != 0)) {
			this.weekNumbersButton.setEnabled(true);
		} else {
			this.weekNumbersButton.setEnabled(false);
			this.weekNumbersButton.setSelection(false);
		}
		this.borderButton.setSelection ((this.dateTime1.getStyle () & SWT.BORDER) != 0);
	}
}
