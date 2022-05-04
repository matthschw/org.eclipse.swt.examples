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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

class SashTab extends Tab {
	/* Example widgets and groups that contain them */
	Sash hSash, vSash;
	Composite sashComp;
	Group sashGroup;
	List list1, list2, list3;
	Text text;
	Button smoothButton;

	static String [] ListData0 = {ControlExample.getResourceString("ListData0_0"),
								  ControlExample.getResourceString("ListData0_1"),
								  ControlExample.getResourceString("ListData0_2"),
								  ControlExample.getResourceString("ListData0_3"),
								  ControlExample.getResourceString("ListData0_4"),
								  ControlExample.getResourceString("ListData0_5"),
								  ControlExample.getResourceString("ListData0_6"),
								  ControlExample.getResourceString("ListData0_7"),
								  ControlExample.getResourceString("ListData0_8")};

	static String [] ListData1 = {ControlExample.getResourceString("ListData1_0"),
								  ControlExample.getResourceString("ListData1_1"),
								  ControlExample.getResourceString("ListData1_2"),
								  ControlExample.getResourceString("ListData1_3"),
								  ControlExample.getResourceString("ListData1_4"),
								  ControlExample.getResourceString("ListData1_5"),
								  ControlExample.getResourceString("ListData1_6"),
								  ControlExample.getResourceString("ListData1_7"),
								  ControlExample.getResourceString("ListData1_8")};

	/* Constants */
	static final int SASH_WIDTH = 3;
	static final int SASH_LIMIT = 20;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	SashTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();
		this.exampleGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.exampleGroup.setLayout(new FillLayout());

		/* Create a group for the sash widgets */
		this.sashGroup = new Group (this.exampleGroup, SWT.NONE);
		final FillLayout layout = new FillLayout();
		layout.marginHeight = layout.marginWidth = 5;
		this.sashGroup.setLayout(layout);
		this.sashGroup.setText ("Sash");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {
		/*
		 * Create the page.  This example does not use layouts.
		 */
		int style = this.getDefaultStyle();
		this.sashComp = new Composite(this.sashGroup, SWT.BORDER | style);

		/* Create the list and text widgets */
		this.list1 = new List (this.sashComp, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		this.list1.setItems (ListData0);
		this.list2 = new List (this.sashComp, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		this.list2.setItems (ListData1);
		this.text = new Text (this.sashComp, SWT.MULTI | SWT.BORDER);
		this.text.setText (ControlExample.getResourceString("Multi_line"));

		/* Create the sashes */
		style = this.smoothButton.getSelection() ? SWT.SMOOTH : SWT.NONE;
		this.vSash = new Sash (this.sashComp, SWT.VERTICAL | style);
		this.hSash = new Sash (this.sashComp, SWT.HORIZONTAL | style);

		/* Add the listeners */
		this.hSash.addSelectionListener (widgetSelectedAdapter(event -> {
			final Rectangle rect = this.vSash.getParent().getClientArea();
			event.y = Math.min (Math.max (event.y, SASH_LIMIT), rect.height - SASH_LIMIT);
			if (event.detail != SWT.DRAG) {
				this.hSash.setBounds (event.x, event.y, event.width, event.height);
				this.layout ();
			}
		}));
		this.vSash.addSelectionListener (widgetSelectedAdapter(event -> {
			final Rectangle rect = this.vSash.getParent().getClientArea();
			event.x = Math.min (Math.max (event.x, SASH_LIMIT), rect.width - SASH_LIMIT);
			if (event.detail != SWT.DRAG) {
				this.vSash.setBounds (event.x, event.y, event.width, event.height);
				this.layout ();
			}
		}));
		this.sashComp.addControlListener (ControlListener.controlResizedAdapter(e ->	this.resized ()));
	}

	/**
	 * Creates the "Size" group.  The "Size" group contains
	 * controls that allow the user to change the size of
	 * the example widgets.
	 */
	@Override
	void createSizeGroup () {
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.smoothButton = new Button (this.styleGroup, SWT.CHECK);
		this.smoothButton.setText("SWT.SMOOTH");
	}

	@Override
	void disposeExampleWidgets () {
		this.sashComp.dispose();
		this.sashComp = null;
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.hSash, this.vSash};
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
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Sash";
	}

	/**
	 * Layout the list and text widgets according to the new
	 * positions of the sashes..events.SelectionEvent
	 */
	void layout () {

		final Rectangle clientArea = this.sashComp.getClientArea ();
		final Rectangle hSashBounds = this.hSash.getBounds ();
		final Rectangle vSashBounds = this.vSash.getBounds ();

		this.list1.setBounds (0, 0, vSashBounds.x, hSashBounds.y);
		this.list2.setBounds (vSashBounds.x + vSashBounds.width, 0, clientArea.width - (vSashBounds.x + vSashBounds.width), hSashBounds.y);
		this.text.setBounds (0, hSashBounds.y + hSashBounds.height, clientArea.width, clientArea.height - (hSashBounds.y + hSashBounds.height));

		/**
		* If the horizontal sash has been moved then the vertical
		* sash is either too long or too short and its size must
		* be adjusted.
		*/
		vSashBounds.height = hSashBounds.y;
		this.vSash.setBounds (vSashBounds);
	}
	/**
	 * Sets the size of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetSize () {
		this.sashGroup.layout (true);
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.smoothButton.setSelection ((this.hSash.getStyle () & SWT.SMOOTH) != 0);
	}

	/**
	 * Handle the shell resized event.
	 */
	void resized () {

		/* Get the client area for the shell */
		final Rectangle clientArea = this.sashComp.getClientArea ();

		/*
		* Make list 1 half the width and half the height of the tab leaving room for the sash.
		* Place list 1 in the top left quadrant of the tab.
		*/
		final Rectangle list1Bounds = new Rectangle (0, 0, (clientArea.width - SASH_WIDTH) / 2, (clientArea.height - SASH_WIDTH) / 2);
		this.list1.setBounds (list1Bounds);

		/*
		* Make list 2 half the width and half the height of the tab leaving room for the sash.
		* Place list 2 in the top right quadrant of the tab.
		*/
		this.list2.setBounds (list1Bounds.width + SASH_WIDTH, 0, clientArea.width - (list1Bounds.width + SASH_WIDTH), list1Bounds.height);

		/*
		* Make the text area the full width and half the height of the tab leaving room for the sash.
		* Place the text area in the bottom half of the tab.
		*/
		this.text.setBounds (0, list1Bounds.height + SASH_WIDTH, clientArea.width, clientArea.height - (list1Bounds.height + SASH_WIDTH));

		/* Position the sashes */
		this.vSash.setBounds (list1Bounds.width, 0, SASH_WIDTH, list1Bounds.height);
		this.hSash.setBounds (0, list1Bounds.height, clientArea.width, SASH_WIDTH);
	}
}
