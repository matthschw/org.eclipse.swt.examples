/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

class MenuTab extends Tab {
	/* Widgets added to the "Menu Style", "MenuItem Style" and "Other" groups */
	Button barButton, dropDownButton, popUpButton, noRadioGroupButton, leftToRightButton, rightToLeftButton;
	Button checkButton, cascadeButton, pushButton, radioButton, separatorButton;
	Button imagesButton, acceleratorsButton, mnemonicsButton, subMenuButton, subSubMenuButton, tooltipButton;
	Button createButton, closeAllButton;
	Group menuItemStyleGroup;

	/* Variables used to track the open shells */
	int shellCount = 0;
	Shell [] shells = new Shell [4];

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	MenuTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Close all the example shells.
	 */
	void closeAllShells() {
		for (int i = 0; i<this.shellCount; i++) {
			if ((this.shells[i] != null) & !this.shells [i].isDisposed ()) {
				this.shells [i].dispose();
			}
		}
		this.shellCount = 0;
	}

	/**
	 * Handle the Create button selection event.
	 *
	 * @param event org.eclipse.swt.events.SelectionEvent
	 */
	public void createButtonSelected(final SelectionEvent event) {

		/*
		 * Remember the example shells so they
		 * can be disposed by the user.
		 */
		if (this.shellCount >= this.shells.length) {
			final Shell [] newShells = new Shell [this.shells.length + 4];
			System.arraycopy (this.shells, 0, newShells, 0, this.shells.length);
			this.shells = newShells;
		}

		int orientation = 0;
		if (this.leftToRightButton.getSelection()) {
      orientation |= SWT.LEFT_TO_RIGHT;
    }
		if (this.rightToLeftButton.getSelection()) {
      orientation |= SWT.RIGHT_TO_LEFT;
    }
		int radioBehavior = 0;
		if (this.noRadioGroupButton.getSelection()) {
      radioBehavior |= SWT.NO_RADIO_GROUP;
    }

		/* Create the shell and menu(s) */
		final Shell shell = new Shell (SWT.SHELL_TRIM | orientation);
		this.shells [this.shellCount] = shell;
		if (this.barButton.getSelection ()) {
			/* Create menu bar. */
			final Menu menuBar = new Menu(shell, SWT.BAR | radioBehavior);
			shell.setMenuBar(menuBar);
			this.hookListeners(menuBar);

			if (this.dropDownButton.getSelection() && this.cascadeButton.getSelection()) {
				/* Create cascade button and drop-down menu in menu bar. */
				final MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
				item.setText(this.getMenuItemText("Cascade"));
				if (this.imagesButton.getSelection()) {
          item.setImage(this.instance.images[ControlExample.ciOpenFolder]);
        }
				if (this.tooltipButton.getSelection()) {
          item.setToolTipText(ControlExample.getResourceString("Tooltip", item.getText() ));
        }
				this.hookListeners(item);
				final Menu dropDownMenu = new Menu(shell, SWT.DROP_DOWN | radioBehavior);
				item.setMenu(dropDownMenu);
				this.hookListeners(dropDownMenu);

				/* Create various menu items, depending on selections. */
				this.createMenuItems(dropDownMenu, this.subMenuButton.getSelection(), this.subSubMenuButton.getSelection());
			}
		}

		if (this.popUpButton.getSelection()) {
			/* Create pop-up menu. */
			final Menu popUpMenu = new Menu(shell, SWT.POP_UP | radioBehavior);
			shell.setMenu(popUpMenu);
			this.hookListeners(popUpMenu);

			/* Create various menu items, depending on selections. */
			this.createMenuItems(popUpMenu, this.subMenuButton.getSelection(), this.subSubMenuButton.getSelection());
		}

		/* Set the size, title and open the shell. */
		shell.setSize (300, 100);
		shell.setText (ControlExample.getResourceString("Title") + this.shellCount);
		shell.addPaintListener(e -> e.gc.drawString(ControlExample.getResourceString("PopupMenuHere"), 20, 20));
		shell.open ();
		this.shellCount++;
	}

	/**
	 * Creates the "Control" group.
	 */
	@Override
	void createControlGroup () {
		/*
		 * Create the "Control" group.  This is the group on the
		 * right half of each example tab.  For MenuTab, it consists of
		 * the Menu style group, the MenuItem style group and the 'other' group.
		 */
		this.controlGroup = new Group (this.tabFolderPage, SWT.NONE);
		this.controlGroup.setLayout (new GridLayout (2, true));
		this.controlGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		this.controlGroup.setText (ControlExample.getResourceString("Parameters"));

		/* Create a group for the menu style controls */
		this.styleGroup = new Group (this.controlGroup, SWT.NONE);
		this.styleGroup.setLayout (new GridLayout ());
		this.styleGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		this.styleGroup.setText (ControlExample.getResourceString("Menu_Styles"));

		/* Create a group for the menu item style controls */
		this.menuItemStyleGroup = new Group (this.controlGroup, SWT.NONE);
		this.menuItemStyleGroup.setLayout (new GridLayout ());
		this.menuItemStyleGroup.setLayoutData (new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		this.menuItemStyleGroup.setText (ControlExample.getResourceString("MenuItem_Styles"));

		/* Create a group for the 'other' controls */
		this.otherGroup = new Group (this.controlGroup, SWT.NONE);
		this.otherGroup.setLayout (new GridLayout ());
		this.otherGroup.setLayoutData (new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		this.otherGroup.setText (ControlExample.getResourceString("Other"));
	}

	/**
	 * Creates the "Control" widget children.
	 */
	@Override
	void createControlWidgets () {

		/* Create the menu style buttons */
		this.barButton = new Button (this.styleGroup, SWT.CHECK);
		this.barButton.setText ("SWT.BAR");
		this.dropDownButton = new Button (this.styleGroup, SWT.CHECK);
		this.dropDownButton.setText ("SWT.DROP_DOWN");
		this.popUpButton = new Button (this.styleGroup, SWT.CHECK);
		this.popUpButton.setText ("SWT.POP_UP");
		this.noRadioGroupButton = new Button (this.styleGroup, SWT.CHECK);
		this.noRadioGroupButton.setText ("SWT.NO_RADIO_GROUP");
		this.leftToRightButton = new Button (this.styleGroup, SWT.RADIO);
		this.leftToRightButton.setText ("SWT.LEFT_TO_RIGHT");
		this.leftToRightButton.setSelection(true);
		this.rightToLeftButton = new Button (this.styleGroup, SWT.RADIO);
		this.rightToLeftButton.setText ("SWT.RIGHT_TO_LEFT");

		/* Create the menu item style buttons */
		this.cascadeButton = new Button (this.menuItemStyleGroup, SWT.CHECK);
		this.cascadeButton.setText ("SWT.CASCADE");
		this.checkButton = new Button (this.menuItemStyleGroup, SWT.CHECK);
		this.checkButton.setText ("SWT.CHECK");
		this.pushButton = new Button (this.menuItemStyleGroup, SWT.CHECK);
		this.pushButton.setText ("SWT.PUSH");
		this.radioButton = new Button (this.menuItemStyleGroup, SWT.CHECK);
		this.radioButton.setText ("SWT.RADIO");
		this.separatorButton = new Button (this.menuItemStyleGroup, SWT.CHECK);
		this.separatorButton.setText ("SWT.SEPARATOR");

		/* Create the 'other' buttons */
		this.enabledButton = new Button(this.otherGroup, SWT.CHECK);
		this.enabledButton.setText(ControlExample.getResourceString("Enabled"));
		this.enabledButton.setSelection(true);
		this.imagesButton = new Button (this.otherGroup, SWT.CHECK);
		this.imagesButton.setText (ControlExample.getResourceString("Images"));
		this.acceleratorsButton = new Button (this.otherGroup, SWT.CHECK);
		this.acceleratorsButton.setText (ControlExample.getResourceString("Accelerators"));
		this.mnemonicsButton = new Button (this.otherGroup, SWT.CHECK);
		this.mnemonicsButton.setText (ControlExample.getResourceString("Mnemonics"));
		this.subMenuButton = new Button (this.otherGroup, SWT.CHECK);
		this.subMenuButton.setText (ControlExample.getResourceString("SubMenu"));
		this.subSubMenuButton = new Button (this.otherGroup, SWT.CHECK);
		this.subSubMenuButton.setText (ControlExample.getResourceString("SubSubMenu"));
		this.tooltipButton = new Button (this.otherGroup, SWT.CHECK);
		this.tooltipButton.setText (ControlExample.getResourceString("Show_Tooltip"));

		/* Create the "create" and "closeAll" buttons (and a 'filler' label to place them) */
		new Label(this.controlGroup, SWT.NONE);
		this.createButton = new Button (this.controlGroup, SWT.NONE);
		this.createButton.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_END));
		this.createButton.setText (ControlExample.getResourceString("Create_Shell"));
		this.closeAllButton = new Button (this.controlGroup, SWT.NONE);
		this.closeAllButton.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING));
		this.closeAllButton.setText (ControlExample.getResourceString("Close_All_Shells"));

		/* Add the listeners */
		this.createButton.addSelectionListener(widgetSelectedAdapter(e -> this.createButtonSelected(e)));
		this.closeAllButton.addSelectionListener(widgetSelectedAdapter(e -> this.closeAllShells ()));
		this.subMenuButton.addSelectionListener(widgetSelectedAdapter(e -> this.subSubMenuButton.setEnabled (this.subMenuButton.getSelection ())));

		/* Set the default state */
		this.barButton.setSelection (true);
		this.dropDownButton.setSelection (true);
		this.popUpButton.setSelection (true);
		this.cascadeButton.setSelection (true);
		this.checkButton.setSelection (true);
		this.pushButton.setSelection (true);
		this.radioButton.setSelection (true);
		this.separatorButton.setSelection (true);
		this.subSubMenuButton.setEnabled (this.subMenuButton.getSelection ());
	}

	/* Create various menu items, depending on selections. */
	void createMenuItems(final Menu menu, final boolean createSubMenu, final boolean createSubSubMenu) {
		MenuItem item;
		if (this.pushButton.getSelection()) {
			item = new MenuItem(menu, SWT.PUSH);
			item.setText(this.getMenuItemText("Push"));
			if (this.acceleratorsButton.getSelection()) {
        item.setAccelerator(SWT.MOD1 + SWT.MOD2 + 'P');
      }
			if (this.imagesButton.getSelection()) {
        item.setImage(this.instance.images[ControlExample.ciClosedFolder]);
      }
			item.setEnabled(this.enabledButton.getSelection());
			if (this.tooltipButton.getSelection()) {
        item.setToolTipText(ControlExample.getResourceString("Tooltip", item.getText() ));
      }
			this.hookListeners(item);
		}

		if (this.separatorButton.getSelection()) {
			item = new MenuItem(menu, SWT.SEPARATOR);
			if (this.tooltipButton.getSelection()) {
        item.setToolTipText(ControlExample.getResourceString("Tooltip", item.getText() ));
      }
		}

		if (this.checkButton.getSelection()) {
			item = new MenuItem(menu, SWT.CHECK);
			item.setText(this.getMenuItemText("Check"));
			if (this.acceleratorsButton.getSelection()) {
        item.setAccelerator(SWT.MOD1 + SWT.MOD2 + 'C');
      }
			if (this.imagesButton.getSelection()) {
        item.setImage(this.instance.images[ControlExample.ciOpenFolder]);
      }
			item.setEnabled(this.enabledButton.getSelection());
			if (this.tooltipButton.getSelection()) {
        item.setToolTipText(ControlExample.getResourceString("Tooltip", item.getText() ));
      }
			this.hookListeners(item);
		}

		if (this.radioButton.getSelection()) {
			item = new MenuItem(menu, SWT.RADIO);
			item.setText(this.getMenuItemText("1Radio"));
			if (this.acceleratorsButton.getSelection()) {
        item.setAccelerator(SWT.MOD1 + SWT.MOD2 + '1');
      }
			if (this.imagesButton.getSelection()) {
        item.setImage(this.instance.images[ControlExample.ciTarget]);
      }
			item.setSelection(true);
			item.setEnabled(this.enabledButton.getSelection());
			if (this.tooltipButton.getSelection()) {
        item.setToolTipText(ControlExample.getResourceString("Tooltip", item.getText() ));
      }
			this.hookListeners(item);

			item = new MenuItem(menu, SWT.RADIO);
			item.setText(this.getMenuItemText("2Radio"));
			if (this.acceleratorsButton.getSelection()) {
        item.setAccelerator(SWT.MOD1 + SWT.MOD2 + '2');
      }
			if (this.imagesButton.getSelection()) {
        item.setImage(this.instance.images[ControlExample.ciTarget]);
      }
			item.setEnabled(this.enabledButton.getSelection());
			if (this.tooltipButton.getSelection()) {
        item.setToolTipText(ControlExample.getResourceString("Tooltip", item.getText()));
      }
			this.hookListeners(item);
		}

		if (createSubMenu && this.cascadeButton.getSelection()) {
			/* Create cascade button and drop-down menu for the sub-menu. */
			item = new MenuItem(menu, SWT.CASCADE);
			item.setText(this.getMenuItemText("Cascade"));
			if (this.imagesButton.getSelection()) {
        item.setImage(this.instance.images[ControlExample.ciOpenFolder]);
      }
			this.hookListeners(item);
			final Menu subMenu = new Menu(menu.getShell(), SWT.DROP_DOWN);
			item.setMenu(subMenu);
			item.setEnabled(this.enabledButton.getSelection());
			this.hookListeners(subMenu);
			if (this.tooltipButton.getSelection()) {
        item.setToolTipText(ControlExample.getResourceString("Tooltip", item.getText() ));
      }
			this.createMenuItems(subMenu, createSubSubMenu, false);
		}
	}

	String getMenuItemText(final String item) {
		final boolean cascade = item.equals("Cascade");
		final boolean mnemonic = this.mnemonicsButton.getSelection();
		final boolean accelerator = this.acceleratorsButton.getSelection();
		final char acceleratorKey = item.charAt(0);
		if (mnemonic && accelerator && !cascade) {
			return ControlExample.getResourceString(item + "WithMnemonic") + "\tCtrl+Shift+" + acceleratorKey;
		}
		if (accelerator && !cascade) {
			return ControlExample.getResourceString(item) + "\tCtrl+Shift+" + acceleratorKey;
		}
		if (mnemonic) {
			return ControlExample.getResourceString(item + "WithMnemonic");
		}
		return ControlExample.getResourceString(item);
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Menu";
	}
}
