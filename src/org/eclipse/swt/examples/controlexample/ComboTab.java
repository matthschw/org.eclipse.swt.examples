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


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Widget;

class ComboTab extends Tab {

	/* Example widgets and groups that contain them */
	Combo combo1;
	Group comboGroup;

	/* Style widgets added to the "Style" group */
	Button dropDownButton, readOnlyButton, simpleButton;

	static String [] ListData = {ControlExample.getResourceString("ListData0_0"),
								 ControlExample.getResourceString("ListData0_1"),
								 ControlExample.getResourceString("ListData0_2"),
								 ControlExample.getResourceString("ListData0_3"),
								 ControlExample.getResourceString("ListData0_4"),
								 ControlExample.getResourceString("ListData0_5"),
								 ControlExample.getResourceString("ListData0_6"),
								 ControlExample.getResourceString("ListData0_7"),
								 ControlExample.getResourceString("ListData0_8")};

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ComboTab(final ControlExample instance) {
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
		this.comboGroup.setText ("Combo");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.dropDownButton.getSelection ()) {
      style |= SWT.DROP_DOWN;
    }
		if (this.readOnlyButton.getSelection ()) {
      style |= SWT.READ_ONLY;
    }
		if (this.simpleButton.getSelection ()) {
      style |= SWT.SIMPLE;
    }

		/* Create the example widgets */
		this.combo1 = new Combo (this.comboGroup, style);
		this.combo1.setItems (ListData);
		if (ListData.length >= 3) {
			this.combo1.setText(ListData [2]);
		}
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
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.dropDownButton = new Button (this.styleGroup, SWT.RADIO);
		this.dropDownButton.setText ("SWT.DROP_DOWN");
		this.simpleButton = new Button (this.styleGroup, SWT.RADIO);
		this.simpleButton.setText("SWT.SIMPLE");
		this.readOnlyButton = new Button (this.styleGroup, SWT.CHECK);
		this.readOnlyButton.setText ("SWT.READ_ONLY");
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
		return new String[] {"Items", "Orientation", "Selection", "Text", "TextLimit", "ToolTipText", "VisibleItemCount"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Combo";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.dropDownButton.setSelection ((this.combo1.getStyle () & SWT.DROP_DOWN) != 0);
		this.simpleButton.setSelection ((this.combo1.getStyle () & SWT.SIMPLE) != 0);
		this.readOnlyButton.setSelection ((this.combo1.getStyle () & SWT.READ_ONLY) != 0);
		this.readOnlyButton.setEnabled(!this.simpleButton.getSelection());
	}
}
