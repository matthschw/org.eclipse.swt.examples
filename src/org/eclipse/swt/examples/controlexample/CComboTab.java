/*******************************************************************************
 * Copyright (c) 2000, 2019 IBM Corporation and others.
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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

class CComboTab extends AlignableTab {

	/* Example widgets and groups that contain them */
	CCombo combo1;
	Group comboGroup;

	/* Style widgets added to the "Style" group */
	Button flatButton, readOnlyButton;

	static String [] ListData = {ControlExample.getResourceString("ListData1_0"),
								 ControlExample.getResourceString("ListData1_1"),
								 ControlExample.getResourceString("ListData1_2"),
								 ControlExample.getResourceString("ListData1_3"),
								 ControlExample.getResourceString("ListData1_4"),
								 ControlExample.getResourceString("ListData1_5"),
								 ControlExample.getResourceString("ListData1_6"),
								 ControlExample.getResourceString("ListData1_7"),
								 ControlExample.getResourceString("ListData1_8")};

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	CComboTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the combo box */
		this.comboGroup = new Group (this.exampleGroup, SWT.NONE);
		this.comboGroup.setLayout (new GridLayout ());
		this.comboGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.comboGroup.setText (ControlExample.getResourceString("Custom_Combo"));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.flatButton.getSelection ()) {
      style |= SWT.FLAT;
    }
		if (this.readOnlyButton.getSelection ()) {
      style |= SWT.READ_ONLY;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }
		if (this.leftButton.getSelection ()) {
      style |= SWT.LEFT;
    }
		if (this.centerButton.getSelection ()) {
      style |= SWT.CENTER;
    }
		if (this.rightButton.getSelection ()) {
      style |= SWT.RIGHT;
    }

		/* Create the example widgets */
		this.combo1 = new CCombo (this.comboGroup, style);
		this.combo1.setItems (ListData);
		if (ListData.length >= 3) {
			this.combo1.setText(ListData [2]);
		}
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup () {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.readOnlyButton = new Button (this.styleGroup, SWT.CHECK);
		this.readOnlyButton.setText ("SWT.READ_ONLY");
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
		this.flatButton = new Button (this.styleGroup, SWT.CHECK);
		this.flatButton.setText ("SWT.FLAT");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.combo1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Editable", "Items", "Selection", "Text", "TextLimit", "ToolTipText", "VisibleItemCount"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "CCombo";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.flatButton.setSelection ((this.combo1.getStyle () & SWT.FLAT) != 0);
		this.readOnlyButton.setSelection ((this.combo1.getStyle () & SWT.READ_ONLY) != 0);
		this.borderButton.setSelection ((this.combo1.getStyle () & SWT.BORDER) != 0);
		this.leftButton.setSelection ((this.combo1.getStyle () & SWT.LEFT) != 0);
		this.centerButton.setSelection ((this.combo1.getStyle () & SWT.CENTER) != 0);
		this.rightButton.setSelection ((this.combo1.getStyle () & SWT.RIGHT) != 0);
	}

	@Override
	void setExampleWidgetAlignment() {
		int alignment = 0;
		if (this.leftButton.getSelection ()) {
      alignment = SWT.LEFT;
    }
		if (this.centerButton.getSelection ()) {
      alignment = SWT.CENTER;
    }
		if (this.rightButton.getSelection ()) {
      alignment = SWT.RIGHT;
    }
		this.combo1.setAlignment (alignment);
	}
}
