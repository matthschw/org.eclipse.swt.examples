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
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

class SliderTab extends RangeTab {
	/* Example widgets and groups that contain them */
	Slider slider1;
	Group sliderGroup;

	/* Spinner widgets added to the "Control" group */
	Spinner incrementSpinner, pageIncrementSpinner, thumbSpinner;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	SliderTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Control" widget children.
	 */
	@Override
	void createControlWidgets () {
		super.createControlWidgets ();
		this.createThumbGroup ();
		this.createIncrementGroup ();
		this.createPageIncrementGroup ();
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the slider */
		this.sliderGroup = new Group (this.exampleGroup, SWT.NONE);
		this.sliderGroup.setLayout (new GridLayout ());
		this.sliderGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.sliderGroup.setText ("Slider");
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
		this.slider1 = new Slider(this.sliderGroup, style);
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
	 * Create a group of widgets to control the thumb
	 * attribute of the example widget.
	 */
	void createThumbGroup() {

		/* Create the group */
		final Group thumbGroup = new Group (this.controlGroup, SWT.NONE);
		thumbGroup.setLayout (new GridLayout ());
		thumbGroup.setText (ControlExample.getResourceString("Thumb"));
		thumbGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));

		/* Create the Spinner widget */
		this.thumbSpinner = new Spinner (thumbGroup, SWT.BORDER);
		this.thumbSpinner.setMaximum (100000);
		this.thumbSpinner.setSelection (this.getDefaultThumb());
		this.thumbSpinner.setPageIncrement (100);
		this.thumbSpinner.setIncrement (1);
		this.thumbSpinner.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));

		/* Add the listeners */
		this.thumbSpinner.addSelectionListener (widgetSelectedAdapter(event -> this.setWidgetThumb ()));
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.slider1};
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
		return "Slider";
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
			this.setWidgetThumb ();
		}
	}

	/**
	 * Gets the default maximum of the "Example" widgets.
	 */
	@Override
	int getDefaultMaximum () {
		return this.slider1.getMaximum();
	}

	/**
	 * Gets the default minimim of the "Example" widgets.
	 */
	@Override
	int getDefaultMinimum () {
		return this.slider1.getMinimum();
	}

	/**
	 * Gets the default selection of the "Example" widgets.
	 */
	@Override
	int getDefaultSelection () {
		return this.slider1.getSelection();
	}

	/**
	 * Gets the default increment of the "Example" widgets.
	 */
	int getDefaultIncrement () {
		return this.slider1.getIncrement();
	}

	/**
	 * Gets the default page increment of the "Example" widgets.
	 */
	int getDefaultPageIncrement () {
		return this.slider1.getPageIncrement();
	}

	/**
	 * Gets the default thumb of the "Example" widgets.
	 */
	int getDefaultThumb () {
		return this.slider1.getThumb();
	}

	/**
	 * Sets the increment of the "Example" widgets.
	 */
	void setWidgetIncrement () {
		this.slider1.setIncrement (this.incrementSpinner.getSelection ());
	}

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	@Override
	void setWidgetMaximum () {
		this.slider1.setMaximum (this.maximumSpinner.getSelection ());
	}

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	@Override
	void setWidgetMinimum () {
		this.slider1.setMinimum (this.minimumSpinner.getSelection ());
	}

	/**
	 * Sets the page increment of the "Example" widgets.
	 */
	void setWidgetPageIncrement () {
		this.slider1.setPageIncrement (this.pageIncrementSpinner.getSelection ());
	}

	/**
	 * Sets the selection of the "Example" widgets.
	 */
	@Override
	void setWidgetSelection () {
		this.slider1.setSelection (this.selectionSpinner.getSelection ());
	}

	/**
	 * Sets the thumb of the "Example" widgets.
	 */
	void setWidgetThumb () {
		this.slider1.setThumb (this.thumbSpinner.getSelection ());
	}
}
