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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

class ProgressBarTab extends RangeTab {
	/* Example widgets and groups that contain them */
	ProgressBar progressBar1;
	Group progressBarGroup;

	/* Style widgets added to the "Style" group */
	Button smoothButton;
	Button indeterminateButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ProgressBarTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup() {
		super.createExampleGroup ();

		/* Create a group for the progress bar */
		this.progressBarGroup = new Group (this.exampleGroup, SWT.NONE);
		this.progressBarGroup.setLayout (new GridLayout ());
		this.progressBarGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.progressBarGroup.setText ("ProgressBar");
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
		if (this.smoothButton.getSelection ()) {
      style |= SWT.SMOOTH;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }
		if (this.indeterminateButton.getSelection ()) {
      style |= SWT.INDETERMINATE;
    }

		/* Create the example widgets */
		this.progressBar1 = new ProgressBar (this.progressBarGroup, style);
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup () {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.smoothButton = new Button (this.styleGroup, SWT.CHECK);
		this.smoothButton.setText ("SWT.SMOOTH");
		this.indeterminateButton = new Button (this.styleGroup, SWT.CHECK);
		this.indeterminateButton.setText ("SWT.INDETERMINATE");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.progressBar1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Selection", "State", "ToolTipText"};
	}

	/**
	 * Gets the short text for the tab folder item.
	 */
	@Override
	String getShortTabText() {
		return "PB";
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "ProgressBar";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		if (this.indeterminateButton.getSelection ()) {
			this.selectionSpinner.setEnabled (false);
			this.minimumSpinner.setEnabled (false);
			this.maximumSpinner.setEnabled (false);
		} else {
			this.selectionSpinner.setEnabled (true);
			this.minimumSpinner.setEnabled (true);
			this.maximumSpinner.setEnabled (true);
		}
		this.smoothButton.setSelection ((this.progressBar1.getStyle () & SWT.SMOOTH) != 0);
		this.indeterminateButton.setSelection ((this.progressBar1.getStyle () & SWT.INDETERMINATE) != 0);
	}

	/**
	 * Gets the default maximum of the "Example" widgets.
	 */
	@Override
	int getDefaultMaximum () {
		return this.progressBar1.getMaximum();
	}

	/**
	 * Gets the default minimim of the "Example" widgets.
	 */
	@Override
	int getDefaultMinimum () {
		return this.progressBar1.getMinimum();
	}

	/**
	 * Gets the default selection of the "Example" widgets.
	 */
	@Override
	int getDefaultSelection () {
		return this.progressBar1.getSelection();
	}

	/**
	 * Sets the maximum of the "Example" widgets.
	 */
	@Override
	void setWidgetMaximum () {
		this.progressBar1.setMaximum (this.maximumSpinner.getSelection ());
		this.updateSpinners ();
	}

	/**
	 * Sets the minimim of the "Example" widgets.
	 */
	@Override
	void setWidgetMinimum () {
		this.progressBar1.setMinimum (this.minimumSpinner.getSelection ());
		this.updateSpinners ();
	}

	/**
	 * Sets the selection of the "Example" widgets.
	 */
	@Override
	void setWidgetSelection () {
		this.progressBar1.setSelection (this.selectionSpinner.getSelection ());
		this.updateSpinners ();
	}

	/**
	 * Update the Spinner widgets to reflect the actual value set
	 * on the "Example" widget.
	 */
	void updateSpinners () {
		this.updateSpinner (this.minimumSpinner, this.progressBar1.getMinimum ());
		this.updateSpinner (this.selectionSpinner, this.progressBar1.getSelection ());
		this.updateSpinner (this.maximumSpinner, this.progressBar1.getMaximum ());
	}

	void updateSpinner(final Spinner spinner, final int selection) {
		if (spinner.getSelection() != selection) {
      spinner.setSelection (selection);
    }
	}
}
