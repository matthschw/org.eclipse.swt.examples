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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This tab draws an image consisting of gradients of two colors.
 * */
public class GradientTab extends GraphicsTab {

	ToolBar toolBar;
	ToolItem colorItem1, colorItem2;
	Menu menu1, menu2;
	GraphicsBackground colorGB1, colorGB2;


public GradientTab(final GraphicsExample example) {
	super(example);
}

/**
 * Dispose resources created by this tab.
 * */
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

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Gradient"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("GradImage"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("GradientImageDescription"); //$NON-NLS-1$
}

@Override
public void createControlPanel(final Composite parent) {
	final Display display = parent.getDisplay();

	this.toolBar = new ToolBar(parent, SWT.FLAT);

	final ColorMenu colorMenu = new ColorMenu();

	// menu for colorItem1
	this.menu1 = colorMenu.createMenu(parent.getParent(), gb -> {
		this.colorGB1 = gb;
		this.colorItem1.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the background to the 5th item in the menu (blue)
	this.colorGB1 = (GraphicsBackground)this.menu1.getItem(4).getData();

	// toolbar item for color1
	this.colorItem1 = new ToolItem(this.toolBar, SWT.PUSH);
	this.colorItem1.setText(GraphicsExample.getResourceString("GradientTabItem1"));
	this.colorItem1.setImage(this.colorGB1.getThumbNail());
	this.colorItem1.addListener(SWT.Selection, event -> {
		final ToolItem toolItem = (ToolItem) event.widget;
		final ToolBar  toolBar = toolItem.getParent();
		final Rectangle toolItemBounds = toolItem.getBounds();
		final Point point = toolBar.toDisplay(new Point(toolItemBounds.x, toolItemBounds.y));
		this.menu1.setLocation(point.x, point.y + toolItemBounds.height);
		this.menu1.setVisible(true);
	});

	// menu for colorItem2
	this.menu2 = colorMenu.createMenu(parent.getParent(), gb -> {
		this.colorGB2 = gb;
		this.colorItem2.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the background to the 3rd item in the menu (red)
	this.colorGB2 = (GraphicsBackground)this.menu2.getItem(2).getData();

	// toolbar item for color2
	this.colorItem2 = new ToolItem(this.toolBar, SWT.PUSH);
	this.colorItem2.setText(GraphicsExample.getResourceString("GradientTabItem2"));
	this.colorItem2.setImage(this.colorGB2.getThumbNail());
	this.colorItem2.addListener(SWT.Selection, event -> {
		final ToolItem toolItem = (ToolItem) event.widget;
		final ToolBar  toolBar = toolItem.getParent();
		final Rectangle toolItemBounds = toolItem.getBounds();
		final Point point = toolBar.toDisplay(new Point(toolItemBounds.x, toolItemBounds.y));
		this.menu2.setLocation(point.x, point.y + toolItemBounds.height);
		this.menu2.setVisible(true);
	});

	// toolbar item for swapping colors
	final ToolItem swapItem = new ToolItem(this.toolBar, SWT.PUSH);
	swapItem.setText(GraphicsExample.getResourceString("SwapColors")); //$NON-NLS-1$
	swapItem.setImage(this.example.loadImage(display, "swap.gif"));
	swapItem.addListener(SWT.Selection, event -> {
		final GraphicsBackground tmp = this.colorGB1;
		this.colorGB1 = this.colorGB2;
		this.colorGB2 = tmp;
		this.colorItem1.setImage(this.colorGB1.getThumbNail());
		this.colorItem2.setImage(this.colorGB2.getThumbNail());
		this.example.redraw();
	});
}

/**
 * This method draws the gradient patterns that make up the image. The image
 * consists of 4 rows, each consisting of 4 gradient patterns (total of 16).
 */
@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	final Image image = this.createImage(device, this.colorGB1.getBgColor1(), this.colorGB2.getBgColor1(), width, height);
	final Pattern p = new Pattern(device, image);
	gc.setBackgroundPattern(p);
	gc.fillRectangle(0, 0, width, height);

	p.dispose();
	image.dispose();
}


/**
 * Creates and returns an image made up of gradient patterns. The image takes up
 * a quarter of the area of the total drawing surface.
 *
 * @param device
 *            A Device
 * @param color1
 *            A Color
 * @param color2
 *            A Color
 * @param width
 *            Width of the drawing surface
 * @param height
 *            Height of the drawing surface
 */
Image createImage(final Device device, final Color color1, final Color color2, final int width, final int height) {
	final Image image = new Image(device, width/2, height/2);
	final GC gc = new GC(image);
	final Rectangle rect = image.getBounds();

	final Pattern pattern1 = new Pattern(device, rect.x, rect.y, rect.width/2f, rect.height/2f, color1, color2);
	gc.setBackgroundPattern(pattern1);
	Path path = new Path(device);
	path.addRectangle(0, 0, width/4f, height/4f);
	path.addRectangle(width/4f, height/4f, width/4f, height/4f);
	gc.fillPath(path);
	path.dispose();

	final Pattern pattern2 = new Pattern(device, rect.width, 0, rect.width/2f, rect.height/2f, color1, color2);
	gc.setBackgroundPattern(pattern2);
	path = new Path(device);
	path.addRectangle(width/4f, 0, width/4f, height/4f);
	path.addRectangle(0, height/4f, width/4f, height/4f);
	gc.fillPath(path);
	path.dispose();

	gc.dispose();
	pattern1.dispose();
	pattern2.dispose();
	return image;
}

}
