/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
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

package org.eclipse.swt.examples.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This class extends the GraphicsTab class to create animated graphics.
 * */
public abstract class AnimatedGraphicsTab extends GraphicsTab {

	ToolBar toolBar;
	ToolItem playItem, pauseItem;
	Spinner timerSpinner;		// to input the speed of the animation
	private boolean animate;	// flag that indicates whether or not to animate the graphic

	public AnimatedGraphicsTab(final GraphicsExample example) {
		super(example);
		this.animate = true;
	}

	/**
	 * Sets the layout of the composite to RowLayout and creates the toolbar.
	 *
	 * @see org.eclipse.swt.examples.graphics.GraphicsTab#createControlPanel(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControlPanel(final Composite parent) {

		// setup layout
		final RowLayout layout = new RowLayout();
		layout.wrap = true;
		layout.spacing = 8;
		parent.setLayout(layout);

		this.createToolBar(parent);
	}

	/**
	 * Creates the toolbar controls: play, pause and animation timer.
	 *
	 * @param parent A composite
	 */
	void createToolBar(final Composite parent) {
		final Display display = parent.getDisplay();

		this.toolBar = new ToolBar(parent, SWT.FLAT);
		final Listener toolBarListener = event -> {
			switch (event.type) {
				case SWT.Selection: {
					if (event.widget == this.playItem) {
						this.animate = true;
						this.playItem.setEnabled(!this.animate);
						this.pauseItem.setEnabled(this.animate);
					} else if (event.widget == this.pauseItem) {
						this.animate = false;
						this.playItem.setEnabled(!this.animate);
						this.pauseItem.setEnabled(this.animate);
					}
				}
				break;
			}
		};

		// play tool item
		this.playItem = new ToolItem(this.toolBar, SWT.PUSH);
		this.playItem.setText(GraphicsExample.getResourceString("Play")); //$NON-NLS-1$
		this.playItem.setImage(this.example.loadImage(display, "play.gif")); //$NON-NLS-1$
		this.playItem.addListener(SWT.Selection, toolBarListener);

		// pause tool item
		this.pauseItem = new ToolItem(this.toolBar, SWT.PUSH);
		this.pauseItem.setText(GraphicsExample.getResourceString("Pause")); //$NON-NLS-1$
		this.pauseItem.setImage(this.example.loadImage(display, "pause.gif")); //$NON-NLS-1$
		this.pauseItem.addListener(SWT.Selection, toolBarListener);

		// timer spinner
		final Composite comp = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout(2, false);
		comp.setLayout(gridLayout);

		final Label label = new Label(comp, SWT.CENTER);
		label.setText(GraphicsExample.getResourceString("Animation")); //$NON-NLS-1$
		this.timerSpinner = new Spinner(comp, SWT.BORDER | SWT.WRAP);
		this.timerSpinner.setMaximum(1000);

		this.playItem.setEnabled(false);
		this.animate = true;

		this.timerSpinner.setSelection(this.getInitialAnimationTime());
	}

	/**
	 *  Answer whether the receiver's drawing should be double bufferer.
	 */
	@Override
	public boolean getDoubleBuffered() {
		return true;
	}

	/**
	 * Gets the initial animation time to be used by the tab. Animation time:
	 * number of milliseconds between the current drawing and the next (the time
	 * interval between calls to the next method). Should be overridden to
	 * return a value that is more appropriate for the tab.
	 */
	public int getInitialAnimationTime() {
		return 30;
	}

	/**
	 * Gets the animation time that is selected in the spinner. Animation time:
	 * number of milliseconds between the current drawing and the next (the time
	 * interval between calls to the next method). Should be overridden to
	 * return a value that is more appropriate for the tab.
	 */
	public int getAnimationTime() {
		return this.timerSpinner.getSelection();
	}

	/**
	 * Returns the true if the tab is currently animated; false otherwise.
	 */
	public boolean getAnimation() {
		return this.animate;
	}

	/**
	 * Causes the animation to stop or start.
	 *
	 * @param flag
	 *            true starts the animation; false stops the animation.
	 */
	public void setAnimation(final boolean flag) {
		this.animate = flag;
		this.playItem.setEnabled(!flag);
		this.pauseItem.setEnabled(flag);
	}

	/**
	 * Advance the animation.
	 */
	public abstract void next(int width, int height);

}
