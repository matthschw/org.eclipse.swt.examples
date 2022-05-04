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
import org.eclipse.swt.widgets.Widget;

class GroupTab extends Tab {
	Button titleButton;

	/* Example widgets and groups that contain them */
	Group group1;
	Group groupGroup;

	/* Style widgets added to the "Style" group */
	Button shadowEtchedInButton, shadowEtchedOutButton, shadowInButton, shadowOutButton, shadowNoneButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	GroupTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		this.titleButton = new Button (this.otherGroup, SWT.CHECK);
		this.titleButton.setText (ControlExample.getResourceString("Title_Text"));

		/* Add the listeners */
		this.titleButton.addSelectionListener (widgetSelectedAdapter(event -> this.setTitleText ()));
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the Group */
		this.groupGroup = new Group (this.exampleGroup, SWT.NONE);
		this.groupGroup.setLayout (new GridLayout ());
		this.groupGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.groupGroup.setText ("Group");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.shadowEtchedInButton.getSelection ()) {
      style |= SWT.SHADOW_ETCHED_IN;
    }
		if (this.shadowEtchedOutButton.getSelection ()) {
      style |= SWT.SHADOW_ETCHED_OUT;
    }
		if (this.shadowInButton.getSelection ()) {
      style |= SWT.SHADOW_IN;
    }
		if (this.shadowOutButton.getSelection ()) {
      style |= SWT.SHADOW_OUT;
    }
		if (this.shadowNoneButton.getSelection ()) {
      style |= SWT.SHADOW_NONE;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }

		/* Create the example widgets */
		this.group1 = new Group (this.groupGroup, style);
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.shadowEtchedInButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowEtchedInButton.setText ("SWT.SHADOW_ETCHED_IN");
		this.shadowEtchedInButton.setSelection(true);
		this.shadowEtchedOutButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowEtchedOutButton.setText ("SWT.SHADOW_ETCHED_OUT");
		this.shadowInButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowInButton.setText ("SWT.SHADOW_IN");
		this.shadowOutButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowOutButton.setText ("SWT.SHADOW_OUT");
		this.shadowNoneButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowNoneButton.setText ("SWT.SHADOW_NONE");
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.group1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Text", "ToolTipText"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Group";
	}

	/**
	 * Sets the title text of the "Example" widgets.
	 */
	void setTitleText () {
		if (this.titleButton.getSelection ()) {
			this.group1.setText (ControlExample.getResourceString("Title_Text"));
		} else {
			this.group1.setText ("");
		}
		this.setExampleWidgetSize ();
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.shadowEtchedInButton.setSelection ((this.group1.getStyle () & SWT.SHADOW_ETCHED_IN) != 0);
		this.shadowEtchedOutButton.setSelection ((this.group1.getStyle () & SWT.SHADOW_ETCHED_OUT) != 0);
		this.shadowInButton.setSelection ((this.group1.getStyle () & SWT.SHADOW_IN) != 0);
		this.shadowOutButton.setSelection ((this.group1.getStyle () & SWT.SHADOW_OUT) != 0);
		this.shadowNoneButton.setSelection ((this.group1.getStyle () & SWT.SHADOW_NONE) != 0);
		this.borderButton.setSelection ((this.group1.getStyle () & SWT.BORDER) != 0);
		if (!this.instance.startup) {
      this.setTitleText ();
    }
	}
}
