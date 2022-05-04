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
import org.eclipse.swt.graphics.Path;
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
 * This tab demonstrates the use of alpha blending. It allows a user to specify
 * a custom alpha value.
 */
public class CustomAlphaTab extends AnimatedGraphicsTab {

	private Spinner alphaSpinner;
	private Button colorButton;
	private GraphicsBackground background;
	private Menu menu;
	private int angle;

public CustomAlphaTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Alpha"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("CustomAlpha"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("CustomAlphaDescription"); //$NON-NLS-1$
}

@Override
public void dispose() {
	if (this.menu != null) {
		this.menu.dispose();
		this.menu = null;
	}
}

/**
 * Creates the widgets used to control the drawing.
 */
@Override
public void createControlPanel(final Composite parent) {
	super.createControlPanel(parent);

	// create drop down combo for choosing clipping
	Composite comp;

	// create spinner for line width
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));
	new Label(comp, SWT.CENTER).setText(GraphicsExample
				.getResourceString("Alpha")); //$NON-NLS-1$
	this.alphaSpinner = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.alphaSpinner.setMinimum(0);
	this.alphaSpinner.setMaximum(255);
	this.alphaSpinner.setSelection(127);
	this.alphaSpinner.addListener(SWT.Selection, event -> this.example.redraw());

	// color menu
	final ColorMenu cm = new ColorMenu();
	cm.setPatternItems(this.example.checkAdvancedGraphics());
	this.menu = cm.createMenu(parent.getParent(), gb -> {
		this.background = gb;
		this.colorButton.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the background to the 5th item in the menu (blue)
	this.background = (GraphicsBackground)this.menu.getItem(4).getData();

	// color button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	this.colorButton = new Button(comp, SWT.PUSH);
	this.colorButton.setText(GraphicsExample
			.getResourceString("Color")); //$NON-NLS-1$
	this.colorButton.setImage(this.background.getThumbNail());
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
public void next(final int width, final int height) {

	this.angle = (this.angle+1)%360;
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	Pattern pattern = null;
	if (this.background.getBgColor1() != null) {
		gc.setBackground(this.background.getBgColor1());
	} else if (this.background.getBgImage() != null) {
		pattern = new Pattern(device, this.background.getBgImage());
		gc.setBackgroundPattern(pattern);
	}

	gc.setAntialias(SWT.ON);
	gc.setAlpha(this.alphaSpinner.getSelection());

	// rotate on center
	final Transform transform = new Transform(device);
	transform.translate(width/2, height/2);
	transform.rotate(-this.angle);
	transform.translate(-width/2, -height/2);
	gc.setTransform(transform);
	transform.dispose();

	// choose the smallest between height and width
	final int diameter = (height < width) ? height : width;

	final Path path = new Path(device);
	path.addArc((width-(diameter/5))/2, (height-(diameter/5))/2, diameter/5, diameter/5, 0, 360);
	path.addArc((5*(width-(diameter/8)))/12, (4*(height-(diameter/8)))/12, diameter/8, diameter/8, 0, 360);
	path.addArc((7*(width-(diameter/8)))/12, (8*(height-(diameter/8)))/12, diameter/8, diameter/8, 0, 360);
	path.addArc((6*(width-(diameter/12)))/12, (3*(height-(diameter/12)))/12, diameter/12, diameter/12, 0, 360);
	path.addArc((6*(width-(diameter/12)))/12, (9*(height-(diameter/12)))/12, diameter/12, diameter/12, 0, 360);
	path.addArc((11.5f*(width-(diameter/18)))/20, (5*(height-(diameter/18)))/18, diameter/18, diameter/18, 0, 360);
	path.addArc((8.5f*(width-(diameter/18)))/20, (13*(height-(diameter/18)))/18, diameter/18, diameter/18, 0, 360);
	path.addArc((62f*(width-(diameter/25)))/100, (32*(height-(diameter/25)))/100, diameter/25, diameter/25, 0, 360);
	path.addArc((39f*(width-(diameter/25)))/100, (67*(height-(diameter/25)))/100, diameter/25, diameter/25, 0, 360);

	gc.fillPath(path);
	path.dispose();

	if (pattern != null) {
    pattern.dispose();
  }
}
}
