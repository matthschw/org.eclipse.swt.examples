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
import org.eclipse.swt.widgets.Menu;

/**
 * This tab demonstrates the use of paths. It allows the user to see the
 * differences between filling, drawing and closing paths.
 */
public class PathTab extends GraphicsTab {

	Button colorButton, fillButton, drawButton, closeButton;
	GraphicsBackground fillColor;
	Menu menu;

public PathTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Path"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("PathOper"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("PathOperDescription"); //$NON-NLS-1$
}

@Override
public void dispose() {
	if (this.menu != null) {
		this.menu.dispose();
		this.menu = null;
	}
}

@Override
public void createControlPanel(final Composite parent) {

	Composite comp;

	// create draw button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());

	this.drawButton = new Button(comp, SWT.TOGGLE);
	this.drawButton.setText(GraphicsExample.getResourceString("DrawPath")); //$NON-NLS-1$
	this.drawButton.addListener(SWT.Selection, event -> this.example.redraw());
	this.drawButton.setSelection(true);

	// create fill button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());

	this.fillButton = new Button(comp, SWT.TOGGLE);
	this.fillButton.setText(GraphicsExample.getResourceString("FillPath")); //$NON-NLS-1$
	this.fillButton.addListener(SWT.Selection, event -> this.example.redraw());

	// create close button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());

	this.closeButton = new Button(comp, SWT.TOGGLE);
	this.closeButton.setText(GraphicsExample.getResourceString("ClosePath")); //$NON-NLS-1$
	this.closeButton.addListener(SWT.Selection, event -> this.example.redraw());

	// create color button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());

	final ColorMenu cm = new ColorMenu();
	cm.setPatternItems(this.example.checkAdvancedGraphics());
	this.menu = cm.createMenu(parent.getParent(), gb -> {
		this.fillColor = gb;
		this.colorButton.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the foreground to the 5th item in the menu (green)
	this.fillColor = (GraphicsBackground)this.menu.getItem(3).getData();

	// color button
	this.colorButton = new Button(comp, SWT.PUSH);
	this.colorButton.setText(GraphicsExample.getResourceString("FillColor")); //$NON-NLS-1$
	this.colorButton.setImage(this.fillColor.getThumbNail());
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
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	Pattern pattern = null;
	if (this.fillColor.getBgColor1() != null) {
		gc.setBackground(this.fillColor.getBgColor1());
	} else if (this.fillColor.getBgImage() != null) {
		pattern = new Pattern(device, this.fillColor.getBgImage());
		gc.setBackgroundPattern(pattern);
	}

	gc.setLineWidth(5);
	gc.setForeground(device.getSystemColor(SWT.COLOR_BLACK));

	// arc
	Path path = new Path(device);
	path.addArc((width-250)/2, (height-400)/2, 500, 400, 90, 180);
	if (this.closeButton.getSelection()) {
    path.close();
  }
	if (this.fillButton.getSelection()) {
    gc.fillPath(path);
  }
	if (this.drawButton.getSelection()) {
    gc.drawPath(path);
  }
	path.dispose();

	// shape on left
	final Transform transform = new Transform(device);
	transform.translate((width-250)/4, (height/2)-150);
	gc.setTransform(transform);
	transform.dispose();
	path = new Path(device);
	path.cubicTo(-150, 100, 150, 200, 0, 300);
	if (this.closeButton.getSelection()) {
    path.close();
  }
	if (this.fillButton.getSelection()) {
    gc.fillPath(path);
  }
	if (this.drawButton.getSelection()) {
    gc.drawPath(path);
  }
	path.dispose();
	gc.setTransform(null);

	// shape on right
	path = new Path(device);
	path.moveTo((((3*(width-250))/4) - 25) + 250, height/2);
	path.lineTo(((3*(width-250))/4) + 50 + 250, (height/2) - 200);
	path.lineTo(((3*(width-250))/4) + 50 + 250, (height/2) + 50);
	path.lineTo((((3*(width-250))/4) - 25) + 250, (height/2) + 150);
	path.lineTo(((3*(width-250))/4) + 25 + 250, (height/2) + 50);
	if (this.closeButton.getSelection()) {
    path.close();
  }
	if (this.fillButton.getSelection()) {
    gc.fillPath(path);
  }
	if (this.drawButton.getSelection()) {
    gc.drawPath(path);
  }
	path.dispose();

	if (pattern != null) {
    pattern.dispose();
  }
}
}


