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


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

class CLabelTab extends AlignableTab {
	/* Example widgets and groups that contain them */
	CLabel label1, label2, label3;
	Group textLabelGroup;

	/* Style widgets added to the "Style" group */
	Button shadowInButton, shadowOutButton, shadowNoneButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	CLabelTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the text labels */
		this.textLabelGroup = new Group(this.exampleGroup, SWT.NONE);
		final GridLayout gridLayout = new GridLayout ();
		this.textLabelGroup.setLayout (gridLayout);
		gridLayout.numColumns = 3;
		this.textLabelGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.textLabelGroup.setText (ControlExample.getResourceString("Custom_Labels"));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.shadowInButton.getSelection ()) {
      style |= SWT.SHADOW_IN;
    }
		if (this.shadowNoneButton.getSelection ()) {
      style |= SWT.SHADOW_NONE;
    }
		if (this.shadowOutButton.getSelection ()) {
      style |= SWT.SHADOW_OUT;
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
		this.label1 = new CLabel (this.textLabelGroup, style);
		this.label1.setText(ControlExample.getResourceString("One"));
		this.label1.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		this.label2 = new CLabel (this.textLabelGroup, style);
		this.label2.setImage (this.instance.images[ControlExample.ciTarget]);
		this.label3 = new CLabel (this.textLabelGroup, style);
		this.label3.setText(ControlExample.getResourceString("Example_string") + "\n" + ControlExample.getResourceString("One_Two_Three"));
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.shadowNoneButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowNoneButton.setText ("SWT.SHADOW_NONE");
		this.shadowInButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowInButton.setText ("SWT.SHADOW_IN");
		this.shadowOutButton = new Button (this.styleGroup, SWT.RADIO);
		this.shadowOutButton.setText ("SWT.SHADOW_OUT");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.label1, this.label2, this.label3};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"BottomMargin", "LeftMargin", "RightMargin", "Text", "ToolTipText", "TopMargin"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "CLabel";
	}

	/**
	 * Sets the alignment of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetAlignment () {
		int alignment = 0;
		if (this.leftButton.getSelection ()) {
      alignment = SWT.LEFT;
    }
		if (this.centerButton.getSelection ()) {
      alignment = SWT.CENTER;
    }
		if (this.rightButton.getSelection ()) {
      alignment = SWT.RIGHT;
    }
		this.label1.setAlignment (alignment);
		this.label2.setAlignment (alignment);
		this.label3.setAlignment (alignment);
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.leftButton.setSelection ((this.label1.getStyle () & SWT.LEFT) != 0);
		this.centerButton.setSelection ((this.label1.getStyle () & SWT.CENTER) != 0);
		this.rightButton.setSelection ((this.label1.getStyle () & SWT.RIGHT) != 0);
		this.shadowInButton.setSelection ((this.label1.getStyle () & SWT.SHADOW_IN) != 0);
		this.shadowOutButton.setSelection ((this.label1.getStyle () & SWT.SHADOW_OUT) != 0);
		this.shadowNoneButton.setSelection ((this.label1.getStyle () & (SWT.SHADOW_IN | SWT.SHADOW_OUT)) == 0);
	}
}
