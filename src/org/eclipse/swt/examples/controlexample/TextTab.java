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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

class TextTab extends ScrollableTab {
	/* Example widgets and groups that contain them */
	Text text;
	Group textGroup;

	/* Style widgets added to the "Style" group */
	Button wrapButton, readOnlyButton, passwordButton, searchButton, iconCancelButton, iconSearchButton;
	Button leftButton, centerButton, rightButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	TextTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the text widget */
		this.textGroup = new Group (this.exampleGroup, SWT.NONE);
		this.textGroup.setLayout (new GridLayout ());
		this.textGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.textGroup.setText ("Text");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.singleButton.getSelection ()) {
      style |= SWT.SINGLE;
    }
		if (this.multiButton.getSelection ()) {
      style |= SWT.MULTI;
    }
		if (this.horizontalButton.getSelection ()) {
      style |= SWT.H_SCROLL;
    }
		if (this.verticalButton.getSelection ()) {
      style |= SWT.V_SCROLL;
    }
		if (this.wrapButton.getSelection ()) {
      style |= SWT.WRAP;
    }
		if (this.readOnlyButton.getSelection ()) {
      style |= SWT.READ_ONLY;
    }
		if (this.passwordButton.getSelection ()) {
      style |= SWT.PASSWORD;
    }
		if (this.searchButton.getSelection ()) {
      style |= SWT.SEARCH;
    }
		if (this.iconCancelButton.getSelection ()) {
      style |= SWT.ICON_CANCEL;
    }
		if (this.iconSearchButton.getSelection ()) {
      style |= SWT.ICON_SEARCH;
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
		this.text = new Text (this.textGroup, style);
		this.text.setText (ControlExample.getResourceString("Example_string") + Text.DELIMITER + ControlExample.getResourceString("One_Two_Three"));
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup();

		/* Create the extra widgets */
		this.wrapButton = new Button (this.styleGroup, SWT.CHECK);
		this.wrapButton.setText ("SWT.WRAP");
		this.readOnlyButton = new Button (this.styleGroup, SWT.CHECK);
		this.readOnlyButton.setText ("SWT.READ_ONLY");
		this.passwordButton = new Button (this.styleGroup, SWT.CHECK);
		this.passwordButton.setText ("SWT.PASSWORD");
		this.searchButton = new Button (this.styleGroup, SWT.CHECK);
		this.searchButton.setText ("SWT.SEARCH");
		this.iconCancelButton = new Button (this.styleGroup, SWT.CHECK);
		this.iconCancelButton.setText ("SWT.ICON_CANCEL");
		this.iconSearchButton = new Button (this.styleGroup, SWT.CHECK);
		this.iconSearchButton.setText ("SWT.ICON_SEARCH");

		final Composite alignmentGroup = new Composite (this.styleGroup, SWT.NONE);
		final GridLayout layout = new GridLayout ();
		layout.marginWidth = layout.marginHeight = 0;
		alignmentGroup.setLayout (layout);
		alignmentGroup.setLayoutData (new GridData (GridData.FILL_BOTH));
		this.leftButton = new Button (alignmentGroup, SWT.RADIO);
		this.leftButton.setText ("SWT.LEFT");
		this.centerButton = new Button (alignmentGroup, SWT.RADIO);
		this.centerButton.setText ("SWT.CENTER");
		this.rightButton = new Button (alignmentGroup, SWT.RADIO);
		this.rightButton.setText ("SWT.RIGHT");
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
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.text};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"DoubleClickEnabled", "EchoChar", "Editable", "Message", "Orientation", "Selection", "Tabs", "Text", "TextChars", "TextLimit", "ToolTipText", "TopIndex"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Text";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.wrapButton.setSelection ((this.text.getStyle () & SWT.WRAP) != 0);
		this.readOnlyButton.setSelection ((this.text.getStyle () & SWT.READ_ONLY) != 0);
		this.passwordButton.setSelection ((this.text.getStyle () & SWT.PASSWORD) != 0);
		this.searchButton.setSelection ((this.text.getStyle () & SWT.SEARCH) != 0);
		this.leftButton.setSelection ((this.text.getStyle () & SWT.LEFT) != 0);
		this.centerButton.setSelection ((this.text.getStyle () & SWT.CENTER) != 0);
		this.rightButton.setSelection ((this.text.getStyle () & SWT.RIGHT) != 0);

		/* Special case: ICON_CANCEL and H_SCROLL have the same value,
		 * and ICON_SEARCH and V_SCROLL have the same value,
		 * so to avoid confusion, only set CANCEL if SEARCH is set. */
		if ((this.text.getStyle () & SWT.SEARCH) != 0) {
			this.iconCancelButton.setSelection ((this.text.getStyle () & SWT.ICON_CANCEL) != 0);
			this.iconSearchButton.setSelection ((this.text.getStyle () & SWT.ICON_SEARCH) != 0);
			this.horizontalButton.setSelection (false);
			this.verticalButton.setSelection (false);
		} else {
			this.iconCancelButton.setSelection (false);
			this.iconSearchButton.setSelection (false);
			this.horizontalButton.setSelection ((this.text.getStyle () & SWT.H_SCROLL) != 0);
			this.verticalButton.setSelection ((this.text.getStyle () & SWT.V_SCROLL) != 0);
		}

		this.passwordButton.setEnabled ((this.text.getStyle () & SWT.SINGLE) != 0);
		this.searchButton.setEnabled ((this.text.getStyle () & SWT.SINGLE) != 0);
		this.iconCancelButton.setEnabled ((this.text.getStyle () & SWT.SEARCH) != 0);
		this.iconSearchButton.setEnabled ((this.text.getStyle () & SWT.SEARCH) != 0);
		this.wrapButton.setEnabled ((this.text.getStyle () & SWT.MULTI) != 0);
		this.horizontalButton.setEnabled ((this.text.getStyle () & SWT.MULTI) != 0);
		this.verticalButton.setEnabled ((this.text.getStyle () & SWT.MULTI) != 0);
	}
}
