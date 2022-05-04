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


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

/**
 * <code>ButtonTab</code> is the class that
 * demonstrates SWT buttons.
 */
class ButtonTab extends AlignableTab {

	/* Example widgets and groups that contain them */
	Button button1, button2, button3, button4, button5, button6, button7, button8, button9;
	Group textButtonGroup, imageButtonGroup, imagetextButtonGroup;

	/* Alignment widgets added to the "Control" group */
	Button upButton, downButton;

	/* Style widgets added to the "Style" group */
	Button pushButton, checkButton, radioButton, toggleButton, arrowButton, flatButton, wrapButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ButtonTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Control" group.
	 */
	@Override
	void createControlGroup () {
		super.createControlGroup ();

		/* Create the controls */
		this.upButton = new Button (this.alignmentGroup, SWT.RADIO);
		this.upButton.setText (ControlExample.getResourceString("Up"));
		this.downButton = new Button (this.alignmentGroup, SWT.RADIO);
		this.downButton.setText (ControlExample.getResourceString("Down"));

		/* Add the listeners */
		final SelectionListener selectionListener = widgetSelectedAdapter(event -> {
			if (!((Button) event.widget).getSelection()) {
        return;
      }
			this.setExampleWidgetAlignment ();
		});
		this.upButton.addSelectionListener(selectionListener);
		this.downButton.addSelectionListener(selectionListener);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for text buttons */
		this.textButtonGroup = new Group(this.exampleGroup, SWT.NONE);
		GridLayout gridLayout = new GridLayout ();
		this.textButtonGroup.setLayout(gridLayout);
		gridLayout.numColumns = 3;
		this.textButtonGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.textButtonGroup.setText (ControlExample.getResourceString("Text_Buttons"));

		/* Create a group for the image buttons */
		this.imageButtonGroup = new Group(this.exampleGroup, SWT.NONE);
		gridLayout = new GridLayout();
		this.imageButtonGroup.setLayout(gridLayout);
		gridLayout.numColumns = 3;
		this.imageButtonGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.imageButtonGroup.setText (ControlExample.getResourceString("Image_Buttons"));

		/* Create a group for the image and text buttons */
		this.imagetextButtonGroup = new Group(this.exampleGroup, SWT.NONE);
		gridLayout = new GridLayout();
		this.imagetextButtonGroup.setLayout(gridLayout);
		gridLayout.numColumns = 3;
		this.imagetextButtonGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.imagetextButtonGroup.setText (ControlExample.getResourceString("Image_Text_Buttons"));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.pushButton.getSelection()) {
      style |= SWT.PUSH;
    }
		if (this.checkButton.getSelection()) {
      style |= SWT.CHECK;
    }
		if (this.radioButton.getSelection()) {
      style |= SWT.RADIO;
    }
		if (this.toggleButton.getSelection()) {
      style |= SWT.TOGGLE;
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
		if (this.leftButton.getSelection()) {
      style |= SWT.LEFT;
    }
		if (this.rightButton.getSelection()) {
      style |= SWT.RIGHT;
    }
		if (this.arrowButton.getSelection()) {
			style |= SWT.ARROW;
			if (this.upButton.getSelection()) {
        style |= SWT.UP;
      }
			if (this.downButton.getSelection()) {
        style |= SWT.DOWN;
      }
		} else if (this.centerButton.getSelection()) {
      style |= SWT.CENTER;
    }

		/* Create the example widgets */
		this.button1 = new Button(this.textButtonGroup, style);
		this.button1.setText(ControlExample.getResourceString("One"));
		this.button2 = new Button(this.textButtonGroup, style);
		this.button2.setText(ControlExample.getResourceString("Two"));
		this.button3 = new Button(this.textButtonGroup, style);
		if (this.wrapButton.getSelection ()) {
			this.button3.setText (ControlExample.getResourceString("Wrap_Text"));
		} else {
			this.button3.setText (ControlExample.getResourceString("Three"));
		}
		this.button4 = new Button(this.imageButtonGroup, style);
		this.button4.setImage(this.instance.images[ControlExample.ciClosedFolder]);
		this.button5 = new Button(this.imageButtonGroup, style);
		this.button5.setImage(this.instance.images[ControlExample.ciOpenFolder]);
		this.button6 = new Button(this.imageButtonGroup, style);
		this.button6.setImage(this.instance.images[ControlExample.ciTarget]);
		this.button7 = new Button(this.imagetextButtonGroup, style);
		this.button7.setText(ControlExample.getResourceString("One"));
		this.button7.setImage(this.instance.images[ControlExample.ciClosedFolder]);
		this.button8 = new Button(this.imagetextButtonGroup, style);
		this.button8.setText(ControlExample.getResourceString("Two"));
		this.button8.setImage(this.instance.images[ControlExample.ciOpenFolder]);
		this.button9 = new Button(this.imagetextButtonGroup, style);
		if (this.wrapButton.getSelection ()) {
			this.button9.setText (ControlExample.getResourceString("Wrap_Text"));
		} else {
			this.button9.setText (ControlExample.getResourceString("Three"));
		}
		this.button9.setImage(this.instance.images[ControlExample.ciTarget]);
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.pushButton = new Button (this.styleGroup, SWT.RADIO);
		this.pushButton.setText("SWT.PUSH");
		this.checkButton = new Button (this.styleGroup, SWT.RADIO);
		this.checkButton.setText ("SWT.CHECK");
		this.radioButton = new Button (this.styleGroup, SWT.RADIO);
		this.radioButton.setText ("SWT.RADIO");
		this.toggleButton = new Button (this.styleGroup, SWT.RADIO);
		this.toggleButton.setText ("SWT.TOGGLE");
		this.arrowButton = new Button (this.styleGroup, SWT.RADIO);
		this.arrowButton.setText ("SWT.ARROW");
		this.flatButton = new Button (this.styleGroup, SWT.CHECK);
		this.flatButton.setText ("SWT.FLAT");
		this.wrapButton = new Button (this.styleGroup, SWT.CHECK);
		this.wrapButton.setText ("SWT.WRAP");
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.button1, this.button2, this.button3, this.button4, this.button5, this.button6, this.button7, this.button8, this.button9};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Grayed", "Selection", "Text", "ToolTipText"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Button";
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
		if (this.upButton.getSelection ()) {
      alignment = SWT.UP;
    }
		if (this.downButton.getSelection ()) {
      alignment = SWT.DOWN;
    }
		this.button1.setAlignment (alignment);
		this.button2.setAlignment (alignment);
		this.button3.setAlignment (alignment);
		this.button4.setAlignment (alignment);
		this.button5.setAlignment (alignment);
		this.button6.setAlignment (alignment);
		this.button7.setAlignment (alignment);
		this.button8.setAlignment (alignment);
		this.button9.setAlignment (alignment);
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		if (this.arrowButton.getSelection ()) {
			this.upButton.setEnabled (true);
			this.centerButton.setEnabled (false);
			this.downButton.setEnabled (true);
		} else {
			this.upButton.setEnabled (false);
			this.centerButton.setEnabled (true);
			this.downButton.setEnabled (false);
		}
		this.upButton.setSelection ((this.button1.getStyle () & SWT.UP) != 0);
		this.downButton.setSelection ((this.button1.getStyle () & SWT.DOWN) != 0);
		this.pushButton.setSelection ((this.button1.getStyle () & SWT.PUSH) != 0);
		this.checkButton.setSelection ((this.button1.getStyle () & SWT.CHECK) != 0);
		this.radioButton.setSelection ((this.button1.getStyle () & SWT.RADIO) != 0);
		this.toggleButton.setSelection ((this.button1.getStyle () & SWT.TOGGLE) != 0);
		this.arrowButton.setSelection ((this.button1.getStyle () & SWT.ARROW) != 0);
		this.flatButton.setSelection ((this.button1.getStyle () & SWT.FLAT) != 0);
		this.wrapButton.setSelection ((this.button1.getStyle () & SWT.WRAP) != 0);
		this.borderButton.setSelection ((this.button1.getStyle () & SWT.BORDER) != 0);
	}
}
