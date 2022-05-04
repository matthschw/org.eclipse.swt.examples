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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

abstract class RangeTab extends Tab {
	/* Style widgets added to the "Style" group */
	Button horizontalButton, verticalButton;
	boolean orientationButtons = true;

	/* Scale widgets added to the "Control" group */
	Spinner minimumSpinner, selectionSpinner, maximumSpinner;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	RangeTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Control" widget children.
	 */
	@Override
	void createControlWidgets () {
		/* Create controls specific to this example */
		this.createMinimumGroup ();
		this.createMaximumGroup ();
		this.createSelectionGroup ();
	}

	/**
	 * Create a group of widgets to control the maximum
	 * attribute of the example widget.
	 */
	void createMaximumGroup() {

		/* Create the group */
		final Group maximumGroup = new Group (this.controlGroup, SWT.NONE);
		maximumGroup.setLayout (new GridLayout ());
		maximumGroup.setText (ControlExample.getResourceString("Maximum"));
		maximumGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));

		/* Create a Spinner widget */
		this.maximumSpinner = new Spinner (maximumGroup, SWT.BORDER);
		this.maximumSpinner.setMaximum (100000);
		this.maximumSpinner.setSelection (this.getDefaultMaximum());
		this.maximumSpinner.setPageIncrement (100);
		this.maximumSpinner.setIncrement (1);
		this.maximumSpinner.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));

		/* Add the listeners */
		this.maximumSpinner.addSelectionListener(widgetSelectedAdapter(event -> this.setWidgetMaximum ()));
	}

	/**
	 * Create a group of widgets to control the minimum
	 * attribute of the example widget.
	 */
	void createMinimumGroup() {

		/* Create the group */
		final Group minimumGroup = new Group (this.controlGroup, SWT.NONE);
		minimumGroup.setLayout (new GridLayout ());
		minimumGroup.setText (ControlExample.getResourceString("Minimum"));
		minimumGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));

		/* Create a Spinner widget */
		this.minimumSpinner = new Spinner (minimumGroup, SWT.BORDER);
		this.minimumSpinner.setMaximum (100000);
		this.minimumSpinner.setSelection(this.getDefaultMinimum());
		this.minimumSpinner.setPageIncrement (100);
		this.minimumSpinner.setIncrement (1);
		this.minimumSpinner.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));

		/* Add the listeners */
		this.minimumSpinner.addSelectionListener (widgetSelectedAdapter(event -> this.setWidgetMinimum ()));

	}

	/**
	 * Create a group of widgets to control the selection
	 * attribute of the example widget.
	 */
	void createSelectionGroup() {

		/* Create the group */
		final Group selectionGroup = new Group(this.controlGroup, SWT.NONE);
		selectionGroup.setLayout(new GridLayout());
		final GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		selectionGroup.setLayoutData(gridData);
		selectionGroup.setText(ControlExample.getResourceString("Selection"));

		/* Create a Spinner widget */
		this.selectionSpinner = new Spinner (selectionGroup, SWT.BORDER);
		this.selectionSpinner.setMaximum (100000);
		this.selectionSpinner.setSelection (this.getDefaultSelection());
		this.selectionSpinner.setPageIncrement (100);
		this.selectionSpinner.setIncrement (1);
		this.selectionSpinner.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));

		/* Add the listeners */
		this.selectionSpinner.addSelectionListener(widgetSelectedAdapter(event -> this.setWidgetSelection ()));

	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup () {
		super.createStyleGroup ();

		/* Create the extra widgets */
		if (this.orientationButtons) {
			this.horizontalButton = new Button (this.styleGroup, SWT.RADIO);
			this.horizontalButton.setText ("SWT.HORIZONTAL");
			this.verticalButton = new Button (this.styleGroup, SWT.RADIO);
			this.verticalButton.setText ("SWT.VERTICAL");
		}
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		if (!this.instance.startup) {
			this.setWidgetMinimum ();
			this.setWidgetMaximum ();
			this.setWidgetSelection ();
		}
		final Widget [] widgets = this.getExampleWidgets ();
		if (widgets.length != 0) {
			if (this.orientationButtons) {
				this.horizontalButton.setSelection ((widgets [0].getStyle () & SWT.HORIZONTAL) != 0);
				this.verticalButton.setSelection ((widgets [0].getStyle () & SWT.VERTICAL) != 0);
			}
			this.borderButton.setSelection ((widgets [0].getStyle () & SWT.BORDER) != 0);
		}
	}

	/**
	 * Gets the default maximum of the "Example" widgets.
	 */
	abstract int getDefaultMaximum ();

	/**
	 * Gets the default minimim of the "Example" widgets.
	 */
	abstract int getDefaultMinimum ();

	/**
	 * Gets the default selection of the "Example" widgets.
	 */
	abstract int getDefaultSelection ();

	/**
	 * Sets the maximum of the "Example" widgets.
	 */
	abstract void setWidgetMaximum ();

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	abstract void setWidgetMinimum ();

	/**
	 * Sets the selection of the "Example" widgets.
	 */
	abstract void setWidgetSelection ();
}
