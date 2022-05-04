/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
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


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Widget;

class SpinnerTab extends RangeTab {

	/* Example widgets and groups that contain them */
	Spinner spinner1;
	Group spinnerGroup;

	/* Style widgets added to the "Style" group */
	Button readOnlyButton, wrapButton;

	/* Spinner widgets added to the "Control" group */
	Spinner incrementSpinner, pageIncrementSpinner, digitsSpinner;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	SpinnerTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Control" widget children.
	 */
	@Override
	void createControlWidgets () {
		super.createControlWidgets ();
		this.createIncrementGroup ();
		this.createPageIncrementGroup ();
		this.createDigitsGroup ();
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the spinner */
		this.spinnerGroup = new Group (this.exampleGroup, SWT.NONE);
		this.spinnerGroup.setLayout (new GridLayout ());
		this.spinnerGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.spinnerGroup.setText ("Spinner");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.readOnlyButton.getSelection ()) {
      style |= SWT.READ_ONLY;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }
		if (this.wrapButton.getSelection ()) {
      style |= SWT.WRAP;
    }

		/* Create the example widgets */
		this.spinner1 = new Spinner (this.spinnerGroup, style);
	}

	/**
	 * Create a group of widgets to control the increment
	 * attribute of the example widget.
	 */
	void createIncrementGroup() {

		/* Create the group */
		final Group incrementGroup = new Group (this.controlGroup, SWT.NONE);
		incrementGroup.setLayout (new GridLayout ());
		incrementGroup.setText (ControlExample.getResourceString("Increment"));
		incrementGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));

		/* Create the Spinner widget */
		this.incrementSpinner = new Spinner (incrementGroup, SWT.BORDER);
		this.incrementSpinner.setMaximum (100000);
		this.incrementSpinner.setSelection (this.getDefaultIncrement());
		this.incrementSpinner.setPageIncrement (100);
		this.incrementSpinner.setIncrement (1);
		this.incrementSpinner.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));

		/* Add the listeners */
		this.incrementSpinner.addSelectionListener (widgetSelectedAdapter(e -> this.setWidgetIncrement ()));
	}

	/**
	 * Create a group of widgets to control the page increment
	 * attribute of the example widget.
	 */
	void createPageIncrementGroup() {

		/* Create the group */
		final Group pageIncrementGroup = new Group (this.controlGroup, SWT.NONE);
		pageIncrementGroup.setLayout (new GridLayout ());
		pageIncrementGroup.setText (ControlExample.getResourceString("Page_Increment"));
		pageIncrementGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));

		/* Create the Spinner widget */
		this.pageIncrementSpinner = new Spinner (pageIncrementGroup, SWT.BORDER);
		this.pageIncrementSpinner.setMaximum (100000);
		this.pageIncrementSpinner.setSelection (this.getDefaultPageIncrement());
		this.pageIncrementSpinner.setPageIncrement (100);
		this.pageIncrementSpinner.setIncrement (1);
		this.pageIncrementSpinner.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));

		/* Add the listeners */
		this.pageIncrementSpinner.addSelectionListener (widgetSelectedAdapter(event -> this.setWidgetPageIncrement ()));
	}

	/**
	 * Create a group of widgets to control the digits
	 * attribute of the example widget.
	 */
	void createDigitsGroup() {

		/* Create the group */
		final Group digitsGroup = new Group (this.controlGroup, SWT.NONE);
		digitsGroup.setLayout (new GridLayout ());
		digitsGroup.setText (ControlExample.getResourceString("Digits"));
		digitsGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));

		/* Create the Spinner widget */
		this.digitsSpinner = new Spinner (digitsGroup, SWT.BORDER);
		this.digitsSpinner.setMaximum (100000);
		this.digitsSpinner.setSelection (this.getDefaultDigits());
		this.digitsSpinner.setPageIncrement (100);
		this.digitsSpinner.setIncrement (1);
		this.digitsSpinner.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));

		/* Add the listeners */
		this.digitsSpinner.addSelectionListener (widgetSelectedAdapter(e -> this.setWidgetDigits ()));
	}

	/**
	 * Creates the tab folder page.
	 *
	 * @param tabFolder org.eclipse.swt.widgets.TabFolder
	 * @return the new page for the tab folder
	 */
	@Override
	Composite createTabFolderPage (final TabFolder tabFolder) {
		super.createTabFolderPage (tabFolder);

		/*
		 * Add a resize listener to the tabFolderPage so that
		 * if the user types into the example widget to change
		 * its preferred size, and then resizes the shell, we
		 * recalculate the preferred size correctly.
		 */
		this.tabFolderPage.addControlListener(ControlListener.controlResizedAdapter(e ->	this.setExampleWidgetSize ()));

		return this.tabFolderPage;
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup () {
		this.orientationButtons = false;
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.readOnlyButton = new Button (this.styleGroup, SWT.CHECK);
		this.readOnlyButton.setText ("SWT.READ_ONLY");
		this.wrapButton = new Button (this.styleGroup, SWT.CHECK);
		this.wrapButton.setText ("SWT.WRAP");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.spinner1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Selection", "TextLimit", "ToolTipText"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Spinner";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.readOnlyButton.setSelection ((this.spinner1.getStyle () & SWT.READ_ONLY) != 0);
		this.wrapButton.setSelection ((this.spinner1.getStyle () & SWT.WRAP) != 0);
		if (!this.instance.startup) {
			this.setWidgetIncrement ();
			this.setWidgetPageIncrement ();
			this.setWidgetDigits ();
		}
	}

	/**
	 * Gets the default maximum of the "Example" widgets.
	 */
	@Override
	int getDefaultMaximum () {
		return this.spinner1.getMaximum();
	}

	/**
	 * Gets the default minimim of the "Example" widgets.
	 */
	@Override
	int getDefaultMinimum () {
		return this.spinner1.getMinimum();
	}

	/**
	 * Gets the default selection of the "Example" widgets.
	 */
	@Override
	int getDefaultSelection () {
		return this.spinner1.getSelection();
	}

	/**
	 * Gets the default increment of the "Example" widgets.
	 */
	int getDefaultIncrement () {
		return this.spinner1.getIncrement();
	}

	/**
	 * Gets the default page increment of the "Example" widgets.
	 */
	int getDefaultPageIncrement () {
		return this.spinner1.getPageIncrement();
	}

	/**
	 * Gets the default digits of the "Example" widgets.
	 */
	int getDefaultDigits () {
		return this.spinner1.getDigits();
	}

	/**
	 * Sets the increment of the "Example" widgets.
	 */
	void setWidgetIncrement () {
		this.spinner1.setIncrement (this.incrementSpinner.getSelection ());
	}

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	@Override
	void setWidgetMaximum () {
		this.spinner1.setMaximum (this.maximumSpinner.getSelection ());
	}

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	@Override
	void setWidgetMinimum () {
		this.spinner1.setMinimum (this.minimumSpinner.getSelection ());
	}

	/**
	 * Sets the page increment of the "Example" widgets.
	 */
	void setWidgetPageIncrement () {
		this.spinner1.setPageIncrement (this.pageIncrementSpinner.getSelection ());
	}

	/**
	 * Sets the digits of the "Example" widgets.
	 */
	void setWidgetDigits () {
		this.spinner1.setDigits (this.digitsSpinner.getSelection ());
	}

	/**
	 * Sets the selection of the "Example" widgets.
	 */
	@Override
	void setWidgetSelection () {
		this.spinner1.setSelection (this.selectionSpinner.getSelection ());
	}
}
