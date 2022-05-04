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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Widget;

abstract class ScrollableTab extends Tab {
	/* Style widgets added to the "Style" group */
	Button singleButton, multiButton, horizontalButton, verticalButton;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ScrollableTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup () {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.singleButton = new Button (this.styleGroup, SWT.RADIO);
		this.singleButton.setText ("SWT.SINGLE");
		this.multiButton = new Button (this.styleGroup, SWT.RADIO);
		this.multiButton.setText ("SWT.MULTI");
		this.horizontalButton = new Button (this.styleGroup, SWT.CHECK);
		this.horizontalButton.setText ("SWT.H_SCROLL");
		this.horizontalButton.setSelection(true);
		this.verticalButton = new Button (this.styleGroup, SWT.CHECK);
		this.verticalButton.setText ("SWT.V_SCROLL");
		this.verticalButton.setSelection(true);
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		final Widget [] widgets = this.getExampleWidgets ();
		if (widgets.length != 0){
			this.singleButton.setSelection ((widgets [0].getStyle () & SWT.SINGLE) != 0);
			this.multiButton.setSelection ((widgets [0].getStyle () & SWT.MULTI) != 0);
			this.horizontalButton.setSelection ((widgets [0].getStyle () & SWT.H_SCROLL) != 0);
			this.verticalButton.setSelection ((widgets [0].getStyle () & SWT.V_SCROLL) != 0);
			this.borderButton.setSelection ((widgets [0].getStyle () & SWT.BORDER) != 0);
		}
	}
}
