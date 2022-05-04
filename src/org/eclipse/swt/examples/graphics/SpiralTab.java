/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
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
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;

/**
 * This tab presents a spiral consisting of the number of petals specified.
 * */
public class SpiralTab extends AnimatedGraphicsTab {

	int angle;					// angle by which to rotate the petals
	Spinner petalSpinner;		// spinner to control number of petals
	Button colorButton;
	GraphicsBackground foreground;
	Menu menu;

public SpiralTab(final GraphicsExample example) {
	super(example);
	this.angle = -90;
}

@Override
public void dispose() {
	if (this.menu != null) {
		this.menu.dispose();
		this.menu = null;
	}
}
/**
 * This method creates a spinner for specifying the number of petals. The call to the
 * createControlPanel method in the super class create the controls that are
 * defined in the super class.
 *
 * @param parent The parent composite
 */
@Override
public void createControlPanel(final Composite parent) {
	super.createControlPanel(parent);

	// create spinner number of petals
	Composite comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	new Label(comp, SWT.CENTER).setText(GraphicsExample
			.getResourceString("Petals")); //$NON-NLS-1$
	this.petalSpinner = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.petalSpinner.setSelection(8);
	this.petalSpinner.setMinimum(3);
	this.petalSpinner.setMaximum(20);
	this.petalSpinner.addListener(SWT.Selection, event -> this.example.redraw());

	// create color button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());

	final ColorMenu cm = new ColorMenu();
	cm.setPatternItems(this.example.checkAdvancedGraphics());
	this.menu = cm.createMenu(parent.getParent(), gb -> {
		this.foreground = gb;
		this.colorButton.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the foreground to the 2nd item in the menu
	this.foreground = (GraphicsBackground)this.menu.getItem(1).getData();

	// color button
	this.colorButton = new Button(comp, SWT.PUSH);
	this.colorButton.setText(GraphicsExample
			.getResourceString("Color")); //$NON-NLS-1$
	this.colorButton.setImage(this.foreground.getThumbNail());
	this.colorButton.addListener(SWT.Selection, event -> {
		final Button button = (Button) event.widget;
		final Composite parent1 = button.getParent();
		final Rectangle bounds = button.getBounds();
		final Point point = parent1.toDisplay(new Point(bounds.x, bounds.y));
		this.menu.setLocation(point.x, point.y + bounds.height);
		this.menu.setVisible(true);
	});
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Misc"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("Spiral"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("SpiralDescription"); //$NON-NLS-1$
}

@Override
public int getInitialAnimationTime() {
	return 150;
}

@Override
public void next(final int width, final int height) {
	if (this.angle == 270) {
		this.angle = -90;
	}
	this.angle += 10;
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	// set line attributes
	gc.setLineWidth(20);
	gc.setLineCap(SWT.CAP_ROUND);	// round line ends
	gc.setAntialias(SWT.ON);	// smooth jagged edges

	Pattern pattern = null;
	if (this.foreground.getBgColor1() != null) {
		gc.setForeground(this.foreground.getBgColor1());
	} else if (this.foreground.getBgImage() != null) {
		pattern = new Pattern(device, this.foreground.getBgImage());
		gc.setForegroundPattern(pattern);
	}

	// draw petals for the spiral
	Transform transform;
	final int n = this.petalSpinner.getSelection();
	for (int i=0; i < n; i++) {
		transform = new Transform(device);
		transform.translate(width/2, height/2);
		transform.rotate(-(this.angle + ((360/n) * i)));
		gc.setTransform(transform);
		gc.drawArc(0, 0, width/3, height/6, 0, 180);
		transform.dispose();
	}

	if (pattern != null) {
    pattern.dispose();
  }
}
}
