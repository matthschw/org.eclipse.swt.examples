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


import static org.eclipse.swt.events.MenuListener.menuHiddenAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

class CoolBarTab extends Tab {
	/* Example widgets and group that contains them */
	CoolBar coolBar;
	CoolItem pushItem, dropDownItem, radioItem, checkItem, textItem;
	Group coolBarGroup;

	/* Style widgets added to the "Style" group */
	Button horizontalButton, verticalButton;
	Button dropDownButton, flatButton;

	/* Other widgets added to the "Other" group */
	Button lockedButton;

	Point[] sizes;
	int[] wrapIndices;
	int[] order;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	CoolBarTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		this.lockedButton = new Button (this.otherGroup, SWT.CHECK);
		this.lockedButton.setText (ControlExample.getResourceString("Locked"));

		/* Add the listeners */
		this.lockedButton.addSelectionListener (widgetSelectedAdapter(event -> this.setWidgetLocked ()));
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();
		this.coolBarGroup = new Group (this.exampleGroup, SWT.NONE);
		this.coolBarGroup.setLayout (new GridLayout ());
		this.coolBarGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.coolBarGroup.setText ("CoolBar");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {
		int style = this.getDefaultStyle(), itemStyle = 0;

		/* Compute the widget, item, and item toolBar styles */
		int toolBarStyle = SWT.FLAT;
		boolean vertical = false;
		if (this.horizontalButton.getSelection ()) {
			style |= SWT.HORIZONTAL;
			toolBarStyle |= SWT.HORIZONTAL;
		}
		if (this.verticalButton.getSelection ()) {
			style |= SWT.VERTICAL;
			toolBarStyle |= SWT.VERTICAL;
			vertical = true;
		}
		if (this.borderButton.getSelection()) {
      style |= SWT.BORDER;
    }
		if (this.flatButton.getSelection()) {
      style |= SWT.FLAT;
    }
		if (this.dropDownButton.getSelection()) {
      itemStyle |= SWT.DROP_DOWN;
    }

		/*
		* Create the example widgets.
		*/
		this.coolBar = new CoolBar (this.coolBarGroup, style);

		/* Create the push button toolbar cool item */
		ToolBar toolBar = new ToolBar (this.coolBar, toolBarStyle);
		ToolItem item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.PUSH");
		item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setToolTipText ("SWT.PUSH");
		item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciTarget]);
		item.setToolTipText ("SWT.PUSH");
		item = new ToolItem (toolBar, SWT.SEPARATOR);
		item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.PUSH");
		item = new ToolItem (toolBar, SWT.PUSH);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setToolTipText ("SWT.PUSH");
		this.pushItem = new CoolItem (this.coolBar, itemStyle);
		this.pushItem.setControl (toolBar);
		this.pushItem.addSelectionListener (new CoolItemSelectionListener());

		/* Create the dropdown toolbar cool item */
		toolBar = new ToolBar (this.coolBar, toolBarStyle);
		item = new ToolItem (toolBar, SWT.DROP_DOWN);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setToolTipText ("SWT.DROP_DOWN");
		item.addSelectionListener (new DropDownSelectionListener());
		item = new ToolItem (toolBar, SWT.DROP_DOWN);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.DROP_DOWN");
		item.addSelectionListener (new DropDownSelectionListener());
		this.dropDownItem = new CoolItem (this.coolBar, itemStyle);
		this.dropDownItem.setControl (toolBar);
		this.dropDownItem.addSelectionListener (new CoolItemSelectionListener());

		/* Create the radio button toolbar cool item */
		toolBar = new ToolBar (this.coolBar, toolBarStyle);
		item = new ToolItem (toolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.RADIO");
		item = new ToolItem (toolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.RADIO");
		item = new ToolItem (toolBar, SWT.RADIO);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.RADIO");
		this.radioItem = new CoolItem (this.coolBar, itemStyle);
		this.radioItem.setControl (toolBar);
		this.radioItem.addSelectionListener (new CoolItemSelectionListener());

		/* Create the check button toolbar cool item */
		toolBar = new ToolBar (this.coolBar, toolBarStyle);
		item = new ToolItem (toolBar, SWT.CHECK);
		item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		item.setToolTipText ("SWT.CHECK");
		item = new ToolItem (toolBar, SWT.CHECK);
		item.setImage (this.instance.images[ControlExample.ciTarget]);
		item.setToolTipText ("SWT.CHECK");
		item = new ToolItem (toolBar, SWT.CHECK);
		item.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		item.setToolTipText ("SWT.CHECK");
		item = new ToolItem (toolBar, SWT.CHECK);
		item.setImage (this.instance.images[ControlExample.ciTarget]);
		item.setToolTipText ("SWT.CHECK");
		this.checkItem = new CoolItem (this.coolBar, itemStyle);
		this.checkItem.setControl (toolBar);
		this.checkItem.addSelectionListener (new CoolItemSelectionListener());

		/* Create the text cool item */
		if (!vertical) {
			final Text text = new Text (this.coolBar, SWT.BORDER | SWT.SINGLE);
			this.textItem = new CoolItem (this.coolBar, itemStyle);
			this.textItem.setControl (text);
			this.textItem.addSelectionListener (new CoolItemSelectionListener());
			Point textSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			textSize = this.textItem.computeSize(textSize.x, textSize.y);
			this.textItem.setMinimumSize(textSize);
			this.textItem.setPreferredSize(textSize);
			this.textItem.setSize(textSize);
		}

		/* Set the sizes after adding all cool items */
		final CoolItem[] coolItems = this.coolBar.getItems();
		for (final CoolItem coolItem : coolItems) {
			final Control control = coolItem.getControl();
			final Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			final Point coolSize = coolItem.computeSize(size.x, size.y);
			if (control instanceof ToolBar) {
				final ToolBar bar = (ToolBar)control;
				if (bar.getItemCount() > 0) {
					if (vertical) {
						size.y = bar.getItem(0).getBounds().height;
					} else {
						size.x = bar.getItem(0).getWidth();
					}
				}
			}
			coolItem.setMinimumSize(size);
			coolItem.setPreferredSize(coolSize);
			coolItem.setSize(coolSize);
		}

		/* If we have saved state, restore it */
		if ((this.order != null) && (this.order.length == this.coolBar.getItemCount())) {
			this.coolBar.setItemLayout(this.order, this.wrapIndices, this.sizes);
		} else {
			this.coolBar.setWrapIndices(new int[] {1, 3});
		}

		/* Add a listener to resize the group box to match the coolbar */
		this.coolBar.addListener(SWT.Resize, event -> this.exampleGroup.layout());
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
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
		this.flatButton = new Button (this.styleGroup, SWT.CHECK);
		this.flatButton.setText ("SWT.FLAT");
		final Group itemGroup = new Group(this.styleGroup, SWT.NONE);
		itemGroup.setLayout (new GridLayout ());
		itemGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		itemGroup.setText(ControlExample.getResourceString("Item_Styles"));
		this.dropDownButton = new Button (itemGroup, SWT.CHECK);
		this.dropDownButton.setText ("SWT.DROP_DOWN");
	}

	/**
	 * Disposes the "Example" widgets.
	 */
	@Override
	void disposeExampleWidgets () {
		/* store the state of the toolbar if applicable */
		if (this.coolBar != null) {
			this.sizes = this.coolBar.getItemSizes();
			this.wrapIndices = this.coolBar.getWrapIndices();
			this.order = this.coolBar.getItemOrder();
		}
		super.disposeExampleWidgets();
	}

	/**
	 * Gets the "Example" widget children's items, if any.
	 *
	 * @return an array containing the example widget children's items
	 */
	@Override
	Item [] getExampleWidgetItems () {
		return this.coolBar.getItems();
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.coolBar};
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
		return "CB";
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "CoolBar";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.horizontalButton.setSelection ((this.coolBar.getStyle () & SWT.HORIZONTAL) != 0);
		this.verticalButton.setSelection ((this.coolBar.getStyle () & SWT.VERTICAL) != 0);
		this.borderButton.setSelection ((this.coolBar.getStyle () & SWT.BORDER) != 0);
		this.flatButton.setSelection ((this.coolBar.getStyle () & SWT.FLAT) != 0);
		this.dropDownButton.setSelection ((this.coolBar.getItem(0).getStyle () & SWT.DROP_DOWN) != 0);
		this.lockedButton.setSelection(this.coolBar.getLocked());
		if (!this.instance.startup) {
      this.setWidgetLocked ();
    }
	}

	/**
	 * Sets the header visible state of the "Example" widgets.
	 */
	void setWidgetLocked () {
		this.coolBar.setLocked (this.lockedButton.getSelection ());
	}

	/**
	 * Listens to widgetSelected() events on SWT.DROP_DOWN type ToolItems
	 * and opens/closes a menu when appropriate.
	 */
	class DropDownSelectionListener extends SelectionAdapter {
		private Menu menu = null;
		private boolean visible = false;

		@Override
		public void widgetSelected(final SelectionEvent event) {
			// Create the menu if it has not already been created
			if (this.menu == null) {
				// Lazy create the menu.
				this.menu = new Menu(CoolBarTab.this.shell, SWT.POP_UP | (CoolBarTab.this.coolBar.getStyle() & (SWT.RIGHT_TO_LEFT | SWT.LEFT_TO_RIGHT)));
				this.menu.addMenuListener(menuHiddenAdapter(e ->	this.visible = false));
				for (int i = 0; i < 9; ++i) {
					final String text = ControlExample.getResourceString("DropDownData_" + i);
					if (text.length() != 0) {
						final MenuItem menuItem = new MenuItem(this.menu, SWT.NONE);
						menuItem.setText(text);
						/*
						 * Add a menu selection listener so that the menu is hidden
						 * when the user selects an item from the drop down menu.
						 */
						menuItem.addSelectionListener(widgetSelectedAdapter(e -> this.setMenuVisible(false)));
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
				if (this.visible) {
					// Hide the menu to give the Arrow the appearance of being a toggle button.
					this.setMenuVisible(false);
				} else {
					// Position the menu below and vertically aligned with the the drop down tool button.
					final ToolItem toolItem = (ToolItem) event.widget;
					final ToolBar  toolBar = toolItem.getParent();

					final Rectangle toolItemBounds = toolItem.getBounds();
					final Point point = toolBar.toDisplay(new Point(toolItemBounds.x, toolItemBounds.y));
					this.menu.setLocation(point.x, point.y + toolItemBounds.height);
					this.setMenuVisible(true);
				}
			} else {
				/*
				 * Main area of drop down tool item selected.
				 * An application would invoke the code to perform the action for the tool item.
				 */
			}
		}
		private void setMenuVisible(final boolean visible) {
			this.menu.setVisible(visible);
			this.visible = visible;
		}
	}

	/**
	 * Listens to widgetSelected() events on SWT.DROP_DOWN type CoolItems
	 * and opens/closes a menu when appropriate.
	 */
	class CoolItemSelectionListener extends SelectionAdapter {
		private Menu menu = null;

		@Override
		public void widgetSelected(final SelectionEvent event) {
			/**
			 * A selection event will be fired when the cool item
			 * is selected by its gripper or if the drop down arrow
			 * (or 'chevron') is selected. Examine the event detail
			 * to determine where the widget was selected.
			 */
			if (event.detail == SWT.ARROW) {
				/* If the popup menu is already up (i.e. user pressed arrow twice),
				 * then dispose it.
				 */
				if (this.menu != null) {
					this.menu.dispose();
					this.menu = null;
					return;
				}

				/* Get the cool item and convert its bounds to display coordinates. */
				final CoolItem coolItem = (CoolItem) event.widget;
				final Rectangle itemBounds = coolItem.getBounds ();
				itemBounds.width = event.x - itemBounds.x;
				Point pt = CoolBarTab.this.coolBar.toDisplay(new Point (itemBounds.x, itemBounds.y));
				itemBounds.x = pt.x;
				itemBounds.y = pt.y;

				/* Get the toolbar from the cool item. */
				final ToolBar toolBar = (ToolBar) coolItem.getControl ();
				final ToolItem[] tools = toolBar.getItems ();
				final int toolCount = tools.length;

				/* Convert the bounds of each tool item to display coordinates,
				 * and determine which ones are past the bounds of the cool item.
				 */
				int i = 0;
				while (i < toolCount) {
					final Rectangle toolBounds = tools[i].getBounds ();
					pt = toolBar.toDisplay(new Point(toolBounds.x, toolBounds.y));
					toolBounds.x = pt.x;
					toolBounds.y = pt.y;
					final Rectangle intersection = itemBounds.intersection (toolBounds);
					if (!intersection.equals (toolBounds)) {
            break;
          }
					i++;
				}

				/* Create a pop-up menu with items for each of the hidden buttons. */
				this.menu = new Menu (CoolBarTab.this.shell, SWT.POP_UP | (CoolBarTab.this.coolBar.getStyle() & (SWT.RIGHT_TO_LEFT | SWT.LEFT_TO_RIGHT)));
				for (int j = i; j < toolCount; j++) {
					final ToolItem tool = tools[j];
					final Image image = tool.getImage();
					if (image == null) {
						new MenuItem (this.menu, SWT.SEPARATOR);
					} else if ((tool.getStyle() & SWT.DROP_DOWN) != 0) {
          	final MenuItem menuItem = new MenuItem (this.menu, SWT.CASCADE);
          	menuItem.setImage(image);
          	String text = tool.getToolTipText();
          	if (text != null) {
              menuItem.setText(text);
            }
          	final Menu m = new Menu(this.menu);
          	menuItem.setMenu(m);
          	for (int k = 0; k < 9; ++k) {
          		text = ControlExample.getResourceString("DropDownData_" + k);
          		if (text.length() != 0) {
          			final MenuItem mi = new MenuItem(m, SWT.NONE);
          			mi.setText(text);
          			/* Application code to perform the action for the submenu item would go here. */
          		} else {
          			new MenuItem(m, SWT.SEPARATOR);
          		}
          	}
          } else {
          	final MenuItem menuItem = new MenuItem (this.menu, SWT.NONE);
          	menuItem.setImage(image);
          	final String text = tool.getToolTipText();
          	if (text != null) {
              menuItem.setText(text);
            }
          }
          /* Application code to perform the action for the menu item would go here. */
				}

				/* Display the pop-up menu at the lower left corner of the arrow button.
				 * Dispose the menu when the user is done with it.
				 */
				pt = CoolBarTab.this.coolBar.toDisplay(new Point(event.x, event.y));
				this.menu.setLocation (pt.x, pt.y);
				this.menu.setVisible (true);
				while ((this.menu != null) && !this.menu.isDisposed() && this.menu.isVisible ()) {
					if (!CoolBarTab.this.display.readAndDispatch ()) {
            CoolBarTab.this.display.sleep ();
          }
				}
				if (this.menu != null) {
					this.menu.dispose ();
					this.menu = null;
				}
			}
		}
	}
}
