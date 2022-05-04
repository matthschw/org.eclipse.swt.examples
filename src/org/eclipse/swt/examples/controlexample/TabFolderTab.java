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


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

class TabFolderTab extends Tab {
	/* Example widgets and groups that contain them */
	TabFolder tabFolder1;
	Group tabFolderGroup;

	/* Style widgets added to the "Style" group */
	Button topButton, bottomButton;

	static String [] TabItems1 = {ControlExample.getResourceString("TabItem1_0"),
								  ControlExample.getResourceString("TabItem1_1"),
								  ControlExample.getResourceString("TabItem1_2")};

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	TabFolderTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the TabFolder */
		this.tabFolderGroup = new Group (this.exampleGroup, SWT.NONE);
		this.tabFolderGroup.setLayout (new GridLayout ());
		this.tabFolderGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.tabFolderGroup.setText ("TabFolder");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.topButton.getSelection ()) {
      style |= SWT.TOP;
    }
		if (this.bottomButton.getSelection ()) {
      style |= SWT.BOTTOM;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }

		/* Create the example widgets */
		this.tabFolder1 = new TabFolder (this.tabFolderGroup, style);
		for (int i = 0; i < TabItems1.length; i++) {
			final TabItem item = new TabItem(this.tabFolder1, SWT.NONE);
			item.setText(TabItems1[i]);
			item.setToolTipText(ControlExample.getResourceString("Tooltip", TabItems1[i]));
			final Text content = new Text(this.tabFolder1, SWT.WRAP | SWT.MULTI);
			content.setText(ControlExample.getResourceString("TabItem_content") + ": " + i);
			item.setControl(content);
		}
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.topButton = new Button (this.styleGroup, SWT.RADIO);
		this.topButton.setText ("SWT.TOP");
		this.topButton.setSelection(true);
		this.bottomButton = new Button (this.styleGroup, SWT.RADIO);
		this.bottomButton.setText ("SWT.BOTTOM");
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
	}

	/**
	 * Gets the "Example" widget children's items, if any.
	 *
	 * @return an array containing the example widget children's items
	 */
	@Override
	Item [] getExampleWidgetItems () {
		return this.tabFolder1.getItems();
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.tabFolder1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Selection", "SelectionIndex", "ToolTipText"};
	}

	@Override
	String setMethodName(final String methodRoot) {
		/* Override to handle special case of int getSelectionIndex()/setSelection(int) */
		return (methodRoot.equals("SelectionIndex")) ? "setSelection" : "set" + methodRoot;
	}

	@Override
	Object[] parameterForType(final String typeName, final String value, final Widget widget) {
		if (value.isEmpty()) {
      return new Object[] {new TabItem[0]};
    }
		if (typeName.equals("org.eclipse.swt.widgets.TabItem")) {
			final TabItem item = this.findItem(value, ((TabFolder) widget).getItems());
			if (item != null) {
        return new Object[] {item};
      }
		}
		if (typeName.equals("[Lorg.eclipse.swt.widgets.TabItem;")) {
			final String[] values = this.split(value, ',');
			final TabItem[] items = new TabItem[values.length];
			for (int i = 0; i < values.length; i++) {
				items[i] = this.findItem(values[i], ((TabFolder) widget).getItems());
			}
			return new Object[] {items};
		}
		return super.parameterForType(typeName, value, widget);
	}

	TabItem findItem(final String value, final TabItem[] items) {
		for (final TabItem item : items) {
			if (item.getText().equals(value)) {
        return item;
      }
		}
		return null;
	}

	/**
	 * Gets the short text for the tab folder item.
	 */
	@Override
	String getShortTabText() {
		return "TF";
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "TabFolder";
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.topButton.setSelection ((this.tabFolder1.getStyle () & SWT.TOP) != 0);
		this.bottomButton.setSelection ((this.tabFolder1.getStyle () & SWT.BOTTOM) != 0);
		this.borderButton.setSelection ((this.tabFolder1.getStyle () & SWT.BORDER) != 0);
	}
}
