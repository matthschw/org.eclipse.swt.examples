/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

class LinkTab extends Tab {
	/* Example widgets and groups that contain them */
	Link link1;
	Group linkGroup;

	/* Controls and resources added to the "Fonts" group */
	static final int LINK_FOREGROUND_COLOR = 3;
	Color linkForegroundColor;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	LinkTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the list */
		this.linkGroup = new Group (this.exampleGroup, SWT.NONE);
		this.linkGroup.setLayout (new GridLayout ());
		this.linkGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.linkGroup.setText ("Link");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }

		/* Create the example widgets */
		this.link1 = new Link (this.linkGroup, style);
		this.link1.setText (ControlExample.getResourceString("LinkText"));
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.borderButton = new Button(this.styleGroup, SWT.CHECK);
		this.borderButton.setText("SWT.BORDER");
	}

	@Override
	void createColorAndFontGroup () {
		super.createColorAndFontGroup();

		final TableItem item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Link_Foreground_Color"));

		this.shell.addDisposeListener(event -> {
			this.linkForegroundColor = null;
		});
	}

	@Override
	void changeFontOrColor(final int index) {
		switch (index) {
			case LINK_FOREGROUND_COLOR: {
				Color oldColor = this.linkForegroundColor;
				if (oldColor == null) {
          oldColor = this.link1.getLinkForeground();
        }
				this.colorDialog.setRGB(oldColor.getRGB());
				final RGB rgb = this.colorDialog.open();
				if (rgb == null) {
          return;
        }
				this.linkForegroundColor = new Color (rgb);
				this.setLinkForeground ();
			}
			break;
			default:
				super.changeFontOrColor(index);
		}
	}

	void setLinkForeground () {
		if (!this.instance.startup) {
			this.link1.setLinkForeground(this.linkForegroundColor);
		}
		Color color = this.linkForegroundColor;
		if (color == null) {
      color = this.link1.getLinkForeground ();
    }
		final TableItem item = this.colorAndFontTable.getItem(LINK_FOREGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	@Override
	void resetColorsAndFonts () {
		super.resetColorsAndFonts ();
		this.linkForegroundColor = null;
		this.setLinkForeground ();
	}

	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState();
		this.setLinkForeground ();
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.link1};
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
		return "Link";
	}

}
