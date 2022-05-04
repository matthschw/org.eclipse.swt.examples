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


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

class ScaleTab extends RangeTab {
	/* Example widgets and groups that contain them */
	Scale scale1;
	Group scaleGroup;

	/* Spinner widgets added to the "Control" group */
	Spinner incrementSpinner, pageIncrementSpinner;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ScaleTab(final ControlExample instance) {
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
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the scale */
		this.scaleGroup = new Group (this.exampleGroup, SWT.NONE);
		this.scaleGroup.setLayout (new GridLayout ());
		this.scaleGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.scaleGroup.setText ("Scale");

	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.horizontalButton.getSelection ()) {
      style |= SWT.HORIZONTAL;
    }
		if (this.verticalButton.getSelection ()) {
      style |= SWT.VERTICAL;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }

		/* Create the example widgets */
		this.scale1 = new Scale (this.scaleGroup, style);
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
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.scale1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Selection", "ToolTipText"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Scale";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		if (!this.instance.startup) {
			this.setWidgetIncrement ();
			this.setWidgetPageIncrement ();
		}
	}

	/**
	 * Gets the default maximum of the "Example" widgets.
	 */
	@Override
	int getDefaultMaximum () {
		return this.scale1.getMaximum();
	}

	/**
	 * Gets the default minimim of the "Example" widgets.
	 */
	@Override
	int getDefaultMinimum () {
		return this.scale1.getMinimum();
	}

	/**
	 * Gets the default selection of the "Example" widgets.
	 */
	@Override
	int getDefaultSelection () {
		return this.scale1.getSelection();
	}

	/**
	 * Gets the default increment of the "Example" widgets.
	 */
	int getDefaultIncrement () {
		return this.scale1.getIncrement();
	}

	/**
	 * Gets the default page increment of the "Example" widgets.
	 */
	int getDefaultPageIncrement () {
		return this.scale1.getPageIncrement();
	}

	/**
	 * Sets the increment of the "Example" widgets.
	 */
	void setWidgetIncrement () {
		this.scale1.setIncrement (this.incrementSpinner.getSelection ());
	}

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	@Override
	void setWidgetMaximum () {
		this.scale1.setMaximum (this.maximumSpinner.getSelection ());
	}

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	@Override
	void setWidgetMinimum () {
		this.scale1.setMinimum (this.minimumSpinner.getSelection ());
	}

	/**
	 * Sets the page increment of the "Example" widgets.
	 */
	void setWidgetPageIncrement () {
		this.scale1.setPageIncrement (this.pageIncrementSpinner.getSelection ());
	}

	/**
	 * Sets the selection of the "Example" widgets.
	 */
	@Override
	void setWidgetSelection () {
		this.scale1.setSelection (this.selectionSpinner.getSelection ());
	}
}
