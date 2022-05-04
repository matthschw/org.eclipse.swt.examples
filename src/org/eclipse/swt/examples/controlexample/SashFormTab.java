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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

class SashFormTab extends Tab {
	/* Example widgets and groups that contain them */
	Group sashFormGroup;
	SashForm form;
	List list1, list2;
	Text text;

	/* Style widgets added to the "Style" group */
	Button horizontalButton, verticalButton, smoothButton;

	static String [] ListData0 = {ControlExample.getResourceString("ListData0_0"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData0_1"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData0_2"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData0_3"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData0_4"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData0_5"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData0_6"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData0_7")}; //$NON-NLS-1$

	static String [] ListData1 = {ControlExample.getResourceString("ListData1_0"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData1_1"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData1_2"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData1_3"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData1_4"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData1_5"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData1_6"), //$NON-NLS-1$
								  ControlExample.getResourceString("ListData1_7")}; //$NON-NLS-1$


	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	SashFormTab(final ControlExample instance) {
		super(instance);
	}
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the sashform widget */
		this.sashFormGroup = new Group (this.exampleGroup, SWT.NONE);
		this.sashFormGroup.setLayout (new GridLayout ());
		this.sashFormGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.sashFormGroup.setText ("SashForm");
	}
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.horizontalButton.getSelection ()) {
      style |= SWT.H_SCROLL;
    }
		if (this.verticalButton.getSelection ()) {
      style |= SWT.V_SCROLL;
    }
		if (this.smoothButton.getSelection ()) {
      style |= SWT.SMOOTH;
    }

		/* Create the example widgets */
		this.form = new SashForm (this.sashFormGroup, style);
		this.list1 = new List (this.form, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		this.list1.setItems (ListData0);
		this.list2 = new List (this.form, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		this.list2.setItems (ListData1);
		this.text = new Text (this.form, SWT.MULTI | SWT.BORDER);
		this.text.setText (ControlExample.getResourceString("Multi_line")); //$NON-NLS-1$
		this.form.setWeights(new int[] {1, 1, 1});
	}
	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup();

		/* Create the extra widgets */
		this.horizontalButton = new Button (this.styleGroup, SWT.RADIO);
		this.horizontalButton.setText ("SWT.HORIZONTAL");
		this.horizontalButton.setSelection(true);
		this.verticalButton = new Button (this.styleGroup, SWT.RADIO);
		this.verticalButton.setText ("SWT.VERTICAL");
		this.verticalButton.setSelection(false);
		this.smoothButton = new Button (this.styleGroup, SWT.CHECK);
		this.smoothButton.setText ("SWT.SMOOTH");
		this.smoothButton.setSelection(false);
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.form};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "SashForm"; //$NON-NLS-1$
	}

		/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.horizontalButton.setSelection ((this.form.getStyle () & SWT.H_SCROLL) != 0);
		this.verticalButton.setSelection ((this.form.getStyle () & SWT.V_SCROLL) != 0);
		this.smoothButton.setSelection ((this.form.getStyle () & SWT.SMOOTH) != 0);
	}
}
