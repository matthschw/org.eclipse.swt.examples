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
 * <code>AlignableTab</code> is the abstract
 * superclass of example controls that can be
 * aligned.
 */
abstract class AlignableTab extends Tab {

	/* Alignment Controls */
	Button leftButton, rightButton, centerButton;

	/* Alignment Group */
	Group alignmentGroup;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	AlignableTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create the group */
		this.alignmentGroup = new Group (this.otherGroup, SWT.NONE);
		this.alignmentGroup.setLayout (new GridLayout ());
		this.alignmentGroup.setLayoutData (new GridData(GridData.HORIZONTAL_ALIGN_FILL |
			GridData.VERTICAL_ALIGN_FILL));
		this.alignmentGroup.setText (ControlExample.getResourceString("Alignment"));

		/* Create the controls */
		this.leftButton = new Button (this.alignmentGroup, SWT.RADIO);
		this.leftButton.setText (ControlExample.getResourceString("Left"));
		this.centerButton = new Button (this.alignmentGroup, SWT.RADIO);
		this.centerButton.setText(ControlExample.getResourceString("Center"));
		this.rightButton = new Button (this.alignmentGroup, SWT.RADIO);
		this.rightButton.setText (ControlExample.getResourceString("Right"));

		/* Add the listeners */
		final SelectionListener selectionListener = widgetSelectedAdapter(event -> {
			if (!((Button) event.widget).getSelection ()) {
        return;
      }
			this.setExampleWidgetAlignment ();
		});
		this.leftButton.addSelectionListener (selectionListener);
		this.centerButton.addSelectionListener (selectionListener);
		this.rightButton.addSelectionListener (selectionListener);
	}

	/**
	 * Sets the alignment of the "Example" widgets.
	 */
	abstract void setExampleWidgetAlignment ();

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		final Widget [] widgets = this.getExampleWidgets ();
		if (widgets.length != 0) {
			this.leftButton.setSelection ((widgets [0].getStyle () & SWT.LEFT) != 0);
			this.centerButton.setSelection ((widgets [0].getStyle () & SWT.CENTER) != 0);
			this.rightButton.setSelection ((widgets [0].getStyle () & SWT.RIGHT) != 0);
		}
	}
}
