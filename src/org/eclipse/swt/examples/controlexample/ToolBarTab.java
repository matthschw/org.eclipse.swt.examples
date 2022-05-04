/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

class ToolBarTab extends Tab {
	/* Example widgets and groups that contain them */
	ToolBar imageToolBar, textToolBar, imageTextToolBar;
	Group imageToolBarGroup, textToolBarGroup, imageTextToolBarGroup;

	/* Style widgets added to the "Style" group */
	Button horizontalButton, verticalButton, flatButton, shadowOutButton, wrapButton, rightButton;

	/* Other widgets added to the "Other" group */
	Button comboChildButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ToolBarTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the image tool bar */
		this.imageToolBarGroup = new Group (this.exampleGroup, SWT.NONE);
		this.imageToolBarGroup.setLayout (new GridLayout ());
		this.imageToolBarGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.imageToolBarGroup.setText (ControlExample.getResourceString("Image_ToolBar"));

		/* Create a group for the text tool bar */
		this.textToolBarGroup = new Group (this.exampleGroup, SWT.NONE);
		this.textToolBarGroup.setLayout (new GridLayout ());
		this.textToolBarGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.textToolBarGroup.setText (ControlExample.getResourceString("Text_ToolBar"));

		/* Create a group for the image and text tool bar */
		this.imageTextToolBarGroup = new Group (this.exampleGroup, SWT.NONE);
		this.imageTextToolBarGroup.setLayout (new GridLayout ());
		this.imageTextToolBarGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.imageTextToolBarGroup.setText (ControlExample.getResourceString("ImageText_ToolBar"));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.horizontalButton.getSelection()) {
      style |= SWT.HORIZONTAL;
    }
		if (this.verticalButton.getSelection()) {
      style |= SWT.VERTICAL;
    }
		if (this.flatButton.getSelection()) {
      style |= SWT.FLAT;
    }
		if (this.wrapButton.getSelection()) {
      style |= SWT.WRAP;
    }
		if (this.borderButton.getSelection()) {
      style |= SWT.BORDER;
    }
		if (this.shadowOutButton.getSelection()) {
      style |= SWT.SHADOW_OUT;
    }
		if (this.rightButton.getSelection()) {
      style |= SWT.RIGHT;
    }

		/*
		* Create the example widgets.
		*
		* A tool bar must consist of all image tool
		* items or all text tool items but not both.
		*/

		/* Create the image tool bar */
		this.imageToolBar = new ToolBar (this.imageToolBarGroup, style);
		ToolItem item = new ToolItem (this.imageToolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText("SWT.PUSH");
		item = new ToolItem (this.imageToolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.PUSH");
		item = new ToolItem (this.imageToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setToolTipText ("SWT.RADIO");
		item = new ToolItem (this.imageToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setToolTipText ("SWT.RADIO");
		item = new ToolItem (this.imageToolBar, SWT.CHECK);
		item.setImage (this.instance.images[ControlExample.ciTarget]);
		item.setToolTipText ("SWT.CHECK");
		item = new ToolItem (this.imageToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.RADIO");
		item = new ToolItem (this.imageToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.RADIO");
		item = new ToolItem (this.imageToolBar, SWT.SEPARATOR);
		item.setToolTipText("SWT.SEPARATOR");
		if (this.comboChildButton.getSelection ()) {
			final Combo combo = new Combo (this.imageToolBar, SWT.NONE);
			combo.setItems ("250", "500", "750");
			combo.setText (combo.getItem (0));
			combo.pack ();
			item.setWidth (combo.getSize ().x);
			item.setControl (combo);
		}
		item = new ToolItem (this.imageToolBar, SWT.DROP_DOWN);
		item.setImage (this.instance.images[ControlExample.ciTarget]);
		item.setToolTipText ("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());

		/* Create the text tool bar */
		this.textToolBar = new ToolBar (this.textToolBarGroup, style);
		item = new ToolItem (this.textToolBar, SWT.PUSH);
		item.setText (ControlExample.getResourceString("Push"));
		item.setToolTipText("SWT.PUSH");
		item = new ToolItem (this.textToolBar, SWT.PUSH);
		item.setText (ControlExample.getResourceString("Push"));
		item.setToolTipText("SWT.PUSH");
		item = new ToolItem (this.textToolBar, SWT.RADIO);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.textToolBar, SWT.RADIO);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.textToolBar, SWT.CHECK);
		item.setText (ControlExample.getResourceString("Check"));
		item.setToolTipText("SWT.CHECK");
		item = new ToolItem (this.textToolBar, SWT.RADIO);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.textToolBar, SWT.RADIO);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.textToolBar, SWT.SEPARATOR);
		item.setToolTipText("SWT.SEPARATOR");
		if (this.comboChildButton.getSelection ()) {
			final Combo combo = new Combo (this.textToolBar, SWT.NONE);
			combo.setItems ("250", "500", "750");
			combo.setText (combo.getItem (0));
			combo.pack ();
			item.setWidth (combo.getSize ().x);
			item.setControl (combo);
		}
		item = new ToolItem (this.textToolBar, SWT.DROP_DOWN);
		item.setText (ControlExample.getResourceString("Drop_Down"));
		item.setToolTipText("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());

		/* Create the image and text tool bar */
		this.imageTextToolBar = new ToolBar (this.imageTextToolBarGroup, style);
		item = new ToolItem (this.imageTextToolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setText (ControlExample.getResourceString("Push"));
		item.setToolTipText("SWT.PUSH");
		item = new ToolItem (this.imageTextToolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setText (ControlExample.getResourceString("Push"));
		item.setToolTipText("SWT.PUSH");
		item = new ToolItem (this.imageTextToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.imageTextToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.imageTextToolBar, SWT.CHECK);
		item.setImage (this.instance.images[ControlExample.ciTarget]);
		item.setText (ControlExample.getResourceString("Check"));
		item.setToolTipText("SWT.CHECK");
		item = new ToolItem (this.imageTextToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.imageTextToolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setText (ControlExample.getResourceString("Radio"));
		item.setToolTipText("SWT.RADIO");
		item = new ToolItem (this.imageTextToolBar, SWT.SEPARATOR);
		item.setToolTipText("SWT.SEPARATOR");
		if (this.comboChildButton.getSelection ()) {
			final Combo combo = new Combo (this.imageTextToolBar, SWT.NONE);
			combo.setItems ("250", "500", "750");
			combo.setText (combo.getItem (0));
			combo.pack ();
			item.setWidth (combo.getSize ().x);
			item.setControl (combo);
		}
		item = new ToolItem (this.imageTextToolBar, SWT.DROP_DOWN);
		item.setImage (this.instance.images[ControlExample.ciTarget]);
		item.setText (ControlExample.getResourceString("Drop_Down"));
		item.setToolTipText("SWT.DROP_DOWN");
		item.addSelectionListener(new DropDownSelectionListener());

		/*
		* Do not add the selection event for this drop down
		* tool item.  Without hooking the event, the drop down
		* widget does nothing special when the drop down area
		* is selected.
		*/
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		this.comboChildButton = new Button (this.otherGroup, SWT.CHECK);
		this.comboChildButton.setText (ControlExample.getResourceString("Combo_child"));

		/* Add the listeners */
		this.comboChildButton.addSelectionListener (widgetSelectedAdapter(event -> this.recreateExampleWidgets ()));
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
		this.verticalButton = new Button (this.styleGroup, SWT.RADIO);
		this.verticalButton.setText ("SWT.VERTICAL");
		this.flatButton = new Button (this.styleGroup, SWT.CHECK);
		this.flatButton.setText ("SWT.FLAT");
		this.shadowOutButton = new Button (this.styleGroup, SWT.CHECK);
		this.shadowOutButton.setText ("SWT.SHADOW_OUT");
		this.wrapButton = new Button (this.styleGroup, SWT.CHECK);
		this.wrapButton.setText ("SWT.WRAP");
		this.rightButton = new Button (this.styleGroup, SWT.CHECK);
		this.rightButton.setText ("SWT.RIGHT");
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
	}

	@Override
	void disposeExampleWidgets () {
		super.disposeExampleWidgets ();
	}

	/**
	 * Gets the "Example" widget children's items, if any.
	 *
	 * @return an array containing the example widget children's items
	 */
	@Override
	Item [] getExampleWidgetItems () {
		final Item [] imageToolBarItems = this.imageToolBar.getItems();
		final Item [] textToolBarItems = this.textToolBar.getItems();
		final Item [] imageTextToolBarItems = this.imageTextToolBar.getItems();
		final Item [] allItems = new Item [imageToolBarItems.length + textToolBarItems.length + imageTextToolBarItems.length];
		System.arraycopy(imageToolBarItems, 0, allItems, 0, imageToolBarItems.length);
		System.arraycopy(textToolBarItems, 0, allItems, imageToolBarItems.length, textToolBarItems.length);
		System.arraycopy(imageTextToolBarItems, 0, allItems, imageToolBarItems.length + textToolBarItems.length, imageTextToolBarItems.length);
		return allItems;
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.imageToolBar, this.textToolBar, this.imageTextToolBar};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"ToolTipText"};
	}

	/**
	 * Gets the short text for the tab folder item.
	 */
	@Override
	String getShortTabText() {
		return "TB";
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "ToolBar";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.horizontalButton.setSelection ((this.imageToolBar.getStyle () & SWT.HORIZONTAL) != 0);
		this.verticalButton.setSelection ((this.imageToolBar.getStyle () & SWT.VERTICAL) != 0);
		this.flatButton.setSelection ((this.imageToolBar.getStyle () & SWT.FLAT) != 0);
		this.wrapButton.setSelection ((this.imageToolBar.getStyle () & SWT.WRAP) != 0);
		this.shadowOutButton.setSelection ((this.imageToolBar.getStyle () & SWT.SHADOW_OUT) != 0);
		this.rightButton.setSelection ((this.imageToolBar.getStyle () & SWT.RIGHT) != 0);
		this.borderButton.setSelection ((this.imageToolBar.getStyle () & SWT.BORDER) != 0);
	}

	/**
	 * Listens to widgetSelected() events on SWT.DROP_DOWN type ToolItems
	 * and opens/closes a menu when appropriate.
	 */
	class DropDownSelectionListener extends SelectionAdapter {
		private Menu    menu = null;

		@Override
		public void widgetSelected(final SelectionEvent event) {
			// Create the menu if it has not already been created
			if (this.menu == null) {
				// Lazy create the menu.
				final ToolBar toolbar = ((ToolItem) event.widget).getParent();
				final int style = toolbar.getStyle() & (SWT.RIGHT_TO_LEFT | SWT.LEFT_TO_RIGHT);
				this.menu = new Menu(ToolBarTab.this.shell, style | SWT.POP_UP);
				for (int i = 0; i < 9; ++i) {
					final String text = ControlExample.getResourceString("DropDownData_" + i);
					if (text.length() != 0) {
						final MenuItem menuItem = new MenuItem(this.menu, SWT.NONE);
						menuItem.setText(text);
					} else {
						new MenuItem(this.menu, SWT.SEPARATOR);
					}
				}
			}

			/**
			 * A selection event will be fired when a drop down tool
			 * item is selected in the main area and in the drop
			 * down arrow.  Examine the event detail to determine
			 * where the widget was selected.
			 */
			if (event.detail == SWT.ARROW) {
				/*
				 * The drop down arrow was selected.
				 */
				// Position the menu below and vertically aligned with the the drop down tool button.
				final ToolItem toolItem = (ToolItem) event.widget;
				final ToolBar  toolBar = toolItem.getParent();

				final Point point = toolBar.toDisplay(new Point(event.x, event.y));
				this.menu.setLocation(point.x, point.y);
				this.menu.setVisible(true);
			}
		}
	}
}
