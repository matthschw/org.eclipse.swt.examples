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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * This tab demonstrates various line caps applicable to a line.
 */
public class LineCapTab extends GraphicsTab {

	Button colorButton;
	GraphicsBackground foreground;
	Menu menu;

public LineCapTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Lines"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("LineCap"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("LineCapDescription"); //$NON-NLS-1$
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

	// initialize the foreground to the 3rd item in the menu (red)
	this.foreground = (GraphicsBackground)this.menu.getItem(2).getData();

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
public void paint(final GC gc, final int width, final int height) {
	final Device device = gc.getDevice();

	// draw side lines
	gc.setLineWidth(1);
	gc.setLineStyle(SWT.LINE_DOT);
	gc.setForeground(device.getSystemColor(SWT.COLOR_BLACK));
	gc.drawLine((3*width)/16, height/6, (3*width)/16, (5*height)/6);
	gc.drawLine((13*width)/16, height/6, (13*width)/16, (5*height)/6);
	gc.setLineStyle(SWT.LINE_SOLID);

	// draw labels
	final Font font = new Font(device, getPlatformFont(), 20, SWT.NORMAL);
	gc.setFont(font);

	String text = GraphicsExample.getResourceString("Flat"); //$NON-NLS-1$
	Point size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (3*height)/12, true);
	text = GraphicsExample.getResourceString("Square"); //$NON-NLS-1$
	size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (5*height)/12, true);
	text = GraphicsExample.getResourceString("Round"); //$NON-NLS-1$
	size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (7*height)/12, true);
	font.dispose();

	Pattern pattern = null;
	if (this.foreground.getBgColor1() != null) {
		gc.setForeground(this.foreground.getBgColor1());
	} else if (this.foreground.getBgImage() != null) {
		pattern = new Pattern(device, this.foreground.getBgImage());
		gc.setForegroundPattern(pattern);
	}

	// draw lines with caps
	gc.setLineWidth(20);
	gc.setLineCap(SWT.CAP_FLAT);
	gc.drawLine((3*width)/16, (2*height)/6, (13*width)/16, (2*height)/6);
	gc.setLineCap(SWT.CAP_SQUARE);
	gc.drawLine((3*width)/16, (3*height)/6, (13*width)/16, (3*height)/6);
	gc.setLineCap(SWT.CAP_ROUND);
	gc.drawLine((3*width)/16, (4*height)/6, (13*width)/16, (4*height)/6);

	if (pattern != null) {
    pattern.dispose();
  }
}

/**
 * Returns the name of a valid font for the resident platform.
 */
static String getPlatformFont() {
	if(SWT.getPlatform() == "win32") {
		return "Arial";
	} else if (SWT.getPlatform() == "gtk") {
		return "Baekmuk Batang";
	} else {
		return "Verdana";
	}
}
}
