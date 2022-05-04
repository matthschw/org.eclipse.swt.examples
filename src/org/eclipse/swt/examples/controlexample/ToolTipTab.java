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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Widget;

class ToolTipTab extends Tab {

	/* Example widgets and groups that contain them */
	ToolTip toolTip1;
	Group toolTipGroup;

	/* Style widgets added to the "Style" group */
	Button balloonButton, iconErrorButton, iconInformationButton, iconWarningButton, noIconButton;

	/* Other widgets added to the "Other" group */
	Button autoHideButton, showInTrayButton;

	Tray tray;
	TrayItem trayItem;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ToolTipTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the tooltip visibility check box */
		this.toolTipGroup = new Group (this.exampleGroup, SWT.NONE);
		this.toolTipGroup.setLayout (new GridLayout ());
		this.toolTipGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.toolTipGroup.setText ("ToolTip");
		this.visibleButton = new Button(this.toolTipGroup, SWT.CHECK);
		this.visibleButton.setText(ControlExample.getResourceString("Visible"));
		this.visibleButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleWidgetVisibility ()));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.balloonButton.getSelection ()) {
      style |= SWT.BALLOON;
    }
		if (this.iconErrorButton.getSelection ()) {
      style |= SWT.ICON_ERROR;
    }
		if (this.iconInformationButton.getSelection ()) {
      style |= SWT.ICON_INFORMATION;
    }
		if (this.iconWarningButton.getSelection ()) {
      style |= SWT.ICON_WARNING;
    }

		/* Create the example widgets */
		this.toolTip1 = new ToolTip (this.shell, style);
		this.toolTip1.setText(ControlExample.getResourceString("ToolTip_Title"));
		this.toolTip1.setMessage(ControlExample.getResourceString("Example_string"));
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
		this.tabFolderPage.addControlListener(ControlListener.controlResizedAdapter(e -> this.setExampleWidgetSize ()));

		return this.tabFolderPage;
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup () {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.balloonButton = new Button (this.styleGroup, SWT.CHECK);
		this.balloonButton.setText ("SWT.BALLOON");
		this.iconErrorButton = new Button (this.styleGroup, SWT.RADIO);
		this.iconErrorButton.setText("SWT.ICON_ERROR");
		this.iconInformationButton = new Button (this.styleGroup, SWT.RADIO);
		this.iconInformationButton.setText("SWT.ICON_INFORMATION");
		this.iconWarningButton = new Button (this.styleGroup, SWT.RADIO);
		this.iconWarningButton.setText("SWT.ICON_WARNING");
		this.noIconButton = new Button (this.styleGroup, SWT.RADIO);
		this.noIconButton.setText(ControlExample.getResourceString("No_Icon"));
	}

	@Override
	void createColorAndFontGroup () {
		// ToolTip does not need a color and font group.
	}

	@Override
	void createOtherGroup () {
		/* Create the group */
		this.otherGroup = new Group (this.controlGroup, SWT.NONE);
		this.otherGroup.setLayout (new GridLayout ());
		this.otherGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.otherGroup.setText (ControlExample.getResourceString("Other"));

		/* Create the controls */
		this.autoHideButton = new Button(this.otherGroup, SWT.CHECK);
		this.autoHideButton.setText(ControlExample.getResourceString("AutoHide"));
		this.showInTrayButton = new Button(this.otherGroup, SWT.CHECK);
		this.showInTrayButton.setText(ControlExample.getResourceString("Show_In_Tray"));
		this.tray = this.display.getSystemTray();
		this.showInTrayButton.setEnabled(this.tray != null);

		/* Add the listeners */
		this.autoHideButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleWidgetAutoHide ()));
		this.showInTrayButton.addSelectionListener (widgetSelectedAdapter(event -> this.showExampleWidgetInTray ()));
		this.shell.addDisposeListener(event -> this.disposeTrayItem());

		/* Set the default state */
		this.autoHideButton.setSelection(true);
	}

	@Override
	void createSizeGroup () {
		// ToolTip does not need a size group.
	}

	@Override
	void createBackgroundModeGroup () {
		// ToolTip does not need a background mode group.
	}

	/**
	 * Disposes the "Example" widgets.
	 */
	@Override
	void disposeExampleWidgets () {
		this.disposeTrayItem();
		super.disposeExampleWidgets();
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	// Tab uses this for many things - widgets would only get set/get, listeners, and dispose.
	Widget[] getExampleWidgets () {
		return new Widget [] {this.toolTip1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Message", "Text"};
	}

	/**
	 * Gets the short text for the tab folder item.
	 */
	@Override
	String getShortTabText() {
		return "TT";
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "ToolTip";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		this.showExampleWidgetInTray ();
		this.setExampleWidgetAutoHide ();
		super.setExampleWidgetState ();
		this.balloonButton.setSelection ((this.toolTip1.getStyle () & SWT.BALLOON) != 0);
		this.iconErrorButton.setSelection ((this.toolTip1.getStyle () & SWT.ICON_ERROR) != 0);
		this.iconInformationButton.setSelection ((this.toolTip1.getStyle () & SWT.ICON_INFORMATION) != 0);
		this.iconWarningButton.setSelection ((this.toolTip1.getStyle () & SWT.ICON_WARNING) != 0);
		this.noIconButton.setSelection ((this.toolTip1.getStyle () & (SWT.ICON_ERROR | SWT.ICON_INFORMATION | SWT.ICON_WARNING)) == 0);
		this.autoHideButton.setSelection(this.toolTip1.getAutoHide());
	}

	/**
	 * Sets the visibility of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetVisibility () {
		this.toolTip1.setVisible (this.visibleButton.getSelection ());
	}

	/**
	 * Sets the autoHide state of the "Example" widgets.
	 */
	void setExampleWidgetAutoHide () {
		this.toolTip1.setAutoHide(this.autoHideButton.getSelection ());
	}

	void showExampleWidgetInTray () {
		if (this.showInTrayButton.getSelection ()) {
			this.createTrayItem();
			this.trayItem.setToolTip(this.toolTip1);
		} else {
			this.disposeTrayItem();
		}
	}

	void createTrayItem() {
		if (this.trayItem == null) {
			this.trayItem = new TrayItem(this.tray, SWT.NONE);
			this.trayItem.setImage(this.instance.images[ControlExample.ciTarget]);
		}
	}

	void disposeTrayItem() {
		if (this.trayItem != null) {
			this.trayItem.setToolTip(null);
			this.trayItem.dispose();
			this.trayItem = null;
		}
	}
}
