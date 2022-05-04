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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

/**
 * This tab demonstrates how to apply a region clipping and the effects of
 * applying one.
 */
public class RegionClippingTab extends GraphicsTab {

	private Combo clippingCb;
	private Button colorButton1, colorButton2;
	private Menu menu1, menu2;
	private GraphicsBackground colorGB1, colorGB2;

public RegionClippingTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Clipping"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("RegionClipping"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("RegionClippingDescription"); //$NON-NLS-1$
}

@Override
public void dispose() {
	if (this.menu1 != null) {
		this.menu1.dispose();
		this.menu1 = null;
	}
	if (this.menu2 != null) {
		this.menu2.dispose();
		this.menu2 = null;
	}
}

/**
 * Creates the widgets used to control the drawing.
 */
@Override
public void createControlPanel(final Composite parent) {
	// create drop down combo for choosing clipping
	Composite comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	new Label(comp, SWT.CENTER).setText(GraphicsExample
			.getResourceString("Clipping")); //$NON-NLS-1$
	this.clippingCb = new Combo(comp, SWT.DROP_DOWN);
	this.clippingCb.add(GraphicsExample.getResourceString("Region1")); //$NON-NLS-1$
	this.clippingCb.add(GraphicsExample.getResourceString("Region2")); //$NON-NLS-1$
	this.clippingCb.add(GraphicsExample.getResourceString("Add")); //$NON-NLS-1$
	this.clippingCb.add(GraphicsExample.getResourceString("Sub")); //$NON-NLS-1$
	this.clippingCb.add(GraphicsExample.getResourceString("Inter")); //$NON-NLS-1$
	this.clippingCb.select(0);
	this.clippingCb.addListener(SWT.Selection, event -> this.example.redraw());

	// color menu
	final ColorMenu cm = new ColorMenu();
	this.menu1 = cm.createMenu(parent.getParent(), gb -> {
		this.colorGB1 = gb;
		this.colorButton1.setImage(gb.getThumbNail());
		this.example.redraw();
	});
	this.menu2 = cm.createMenu(parent.getParent(), gb -> {
		this.colorGB2 = gb;
		this.colorButton2.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the color to blue
	this.colorGB1 = (GraphicsBackground)this.menu1.getItem(4).getData();
	// initialize the color to red
	this.colorGB2 = (GraphicsBackground)this.menu2.getItem(2).getData();

	// color button 1
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	this.colorButton1 = new Button(comp, SWT.PUSH);
	this.colorButton1.setText(GraphicsExample
			.getResourceString("Color1")); //$NON-NLS-1$
	this.colorButton1.setImage(this.colorGB1.getThumbNail());
	this.colorButton1.addListener(SWT.Selection, event -> {
		final Button button = (Button) event.widget;
		final Composite parent1 = button.getParent();
		final Rectangle bounds = button.getBounds();
		final Point point = parent1.toDisplay(new Point(bounds.x, bounds.y));
		this.menu1.setLocation(point.x, point.y + bounds.height);
		this.menu1.setVisible(true);
	});

	// color button 2
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	this.colorButton2 = new Button(comp, SWT.PUSH);
	this.colorButton2.setText(GraphicsExample
			.getResourceString("Color2")); //$NON-NLS-1$
	this.colorButton2.setImage(this.colorGB2.getThumbNail());
	this.colorButton2.addListener(SWT.Selection, event -> {
		final Button button = (Button) event.widget;
		final Composite parent1 = button.getParent();
		final Rectangle bounds = button.getBounds();
		final Point point = parent1.toDisplay(new Point(bounds.x, bounds.y));
		this.menu2.setLocation(point.x, point.y + bounds.height);
		this.menu2.setVisible(true);
	});
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	// array of coordinate points of polygon 1 (region 1)
	final int [] polygon1 = new int [] {10, height/2, (9*width)/16, 10, (9*width)/16, height-10};
	final Region region1 = new Region(device);
	region1.add(polygon1);

	// array of coordinate points of polygon 2 (region 2)
	final int [] polygon2 = new int [] {
			(9*width)/16, 10,
			(9*width)/16, height/8,
			(7*width)/16, (2*height)/8,
			(9*width)/16, (3*height)/8,
			(7*width)/16, (4*height)/8,
			(9*width)/16, (5*height)/8,
			(7*width)/16, (6*height)/8,
			(9*width)/16, (7*height)/8,
			(9*width)/16, height-10,
			width-10, height/2
	};
	final Region region2 = new Region(device);
	region2.add(polygon2);

	gc.setAlpha(127);

	final int clippingIndex = this.clippingCb.getSelectionIndex();

	switch (clippingIndex) {
	case 0:
		// region 1
		gc.setClipping(region1);
		gc.setBackground(this.colorGB1.getBgColor1());
		gc.fillPolygon(polygon1);
		break;
	case 1:
		// region 2
		gc.setClipping(region2);
		gc.setBackground(this.colorGB2.getBgColor1());
		gc.fillPolygon(polygon2);
		break;
	case 2:
		// add
		region1.add(region2);
		break;
	case 3:
		// sub
		region1.subtract(region2);
		break;
	case 4:
		// intersect
		region1.intersect(region2);
		break;
	}

	if (clippingIndex > 1) {
		gc.setClipping(region1);

		gc.setBackground(this.colorGB1.getBgColor1());
		gc.fillPolygon(polygon1);

		gc.setBackground(this.colorGB2.getBgColor1());
		gc.fillPolygon(polygon2);
	}

	region1.dispose();
	region2.dispose();
	}
}
