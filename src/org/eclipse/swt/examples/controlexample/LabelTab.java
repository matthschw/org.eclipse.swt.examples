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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

class LabelTab extends AlignableTab {
	/* Example widgets and groups that contain them */
	Label label1, label2, label3, label4, label5, label6;
	Group textLabelGroup, imageLabelGroup;

	/* Style widgets added to the "Style" group */
	Button wrapButton, separatorButton, horizontalButton, verticalButton, shadowInButton, shadowOutButton, shadowNoneButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	LabelTab(final ControlExample instance) {
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
		GridLayout gridLayout = new GridLayout ();
		this.textLabelGroup.setLayout (gridLayout);
		gridLayout.numColumns = 3;
		this.textLabelGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.textLabelGroup.setText (ControlExample.getResourceString("Text_Labels"));

		/* Create a group for the image labels */
		this.imageLabelGroup = new Group (this.exampleGroup, SWT.SHADOW_NONE);
		gridLayout = new GridLayout ();
		this.imageLabelGroup.setLayout (gridLayout);
		gridLayout.numColumns = 3;
		this.imageLabelGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.imageLabelGroup.setText (ControlExample.getResourceString("Image_Labels"));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.wrapButton.getSelection ()) {
      style |= SWT.WRAP;
    }
		if (this.separatorButton.getSelection ()) {
      style |= SWT.SEPARATOR;
    }
		if (this.horizontalButton.getSelection ()) {
      style |= SWT.HORIZONTAL;
    }
		if (this.verticalButton.getSelection ()) {
      style |= SWT.VERTICAL;
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
		this.label1 = new Label (this.textLabelGroup, style);
		this.label1.setText(ControlExample.getResourceString("One"));
		this.label2 = new Label (this.textLabelGroup, style);
		this.label2.setText(ControlExample.getResourceString("Two"));
		this.label3 = new Label (this.textLabelGroup, style);
		if (this.wrapButton.getSelection ()) {
			this.label3.setText (ControlExample.getResourceString("Wrap_Text"));
		} else {
			this.label3.setText (ControlExample.getResourceString("Three"));
		}
		this.label4 = new Label (this.imageLabelGroup, style);
		this.label4.setImage (this.instance.images[ControlExample.ciClosedFolder]);
		this.label5 = new Label (this.imageLabelGroup, style);
		this.label5.setImage (this.instance.images[ControlExample.ciOpenFolder]);
		this.label6 = new Label(this.imageLabelGroup, style);
		this.label6.setImage (this.instance.images[ControlExample.ciTarget]);
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.wrapButton = new Button (this.styleGroup, SWT.CHECK);
		this.wrapButton.setText ("SWT.WRAP");
		this.separatorButton = new Button (this.styleGroup, SWT.CHECK);
		this.separatorButton.setText ("SWT.SEPARATOR");
		this.horizontalButton = new Button (this.styleGroup, SWT.RADIO);
		this.horizontalButton.setText ("SWT.HORIZONTAL");
		this.verticalButton = new Button (this.styleGroup, SWT.RADIO);
		this.verticalButton.setText ("SWT.VERTICAL");
		final Group styleSubGroup = new Group (this.styleGroup, SWT.NONE);
		styleSubGroup.setLayout (new GridLayout ());
		this.shadowInButton = new Button (styleSubGroup, SWT.RADIO);
		this.shadowInButton.setText ("SWT.SHADOW_IN");
		this.shadowOutButton = new Button (styleSubGroup, SWT.RADIO);
		this.shadowOutButton.setText ("SWT.SHADOW_OUT");
		this.shadowNoneButton = new Button (styleSubGroup, SWT.RADIO);
		this.shadowNoneButton.setText ("SWT.SHADOW_NONE");
		this.borderButton = new Button(this.styleGroup, SWT.CHECK);
		this.borderButton.setText("SWT.BORDER");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.label1, this.label2, this.label3, this.label4, this.label5, this.label6};
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
		return "Label";
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
		this.label4.setAlignment (alignment);
		this.label5.setAlignment (alignment);
		this.label6.setAlignment (alignment);
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		final boolean isSeparator = (this.label1.getStyle () & SWT.SEPARATOR) != 0;
		this.wrapButton.setSelection (!isSeparator && ((this.label1.getStyle () & SWT.WRAP) != 0));
		this.leftButton.setSelection (!isSeparator && ((this.label1.getStyle () & SWT.LEFT) != 0));
		this.centerButton.setSelection (!isSeparator && ((this.label1.getStyle () & SWT.CENTER) != 0));
		this.rightButton.setSelection (!isSeparator && ((this.label1.getStyle () & SWT.RIGHT) != 0));
		this.shadowInButton.setSelection (isSeparator && ((this.label1.getStyle () & SWT.SHADOW_IN) != 0));
		this.shadowOutButton.setSelection (isSeparator && ((this.label1.getStyle () & SWT.SHADOW_OUT) != 0));
		this.shadowNoneButton.setSelection (isSeparator && ((this.label1.getStyle () & SWT.SHADOW_NONE) != 0));
		this.horizontalButton.setSelection (isSeparator && ((this.label1.getStyle () & SWT.HORIZONTAL) != 0));
		this.verticalButton.setSelection (isSeparator && ((this.label1.getStyle () & SWT.VERTICAL) != 0));
		this.wrapButton.setEnabled (!isSeparator);
		this.leftButton.setEnabled (!isSeparator);
		this.centerButton.setEnabled (!isSeparator);
		this.rightButton.setEnabled (!isSeparator);
		this.shadowInButton.setEnabled (isSeparator);
		this.shadowOutButton.setEnabled (isSeparator);
		this.shadowNoneButton.setEnabled (isSeparator);
		this.horizontalButton.setEnabled (isSeparator);
		this.verticalButton.setEnabled (isSeparator);
	}
}
