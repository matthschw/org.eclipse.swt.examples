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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;

/**
 * This tab demonstrates line styles applicable to a line.  It shows the use of the
 * <code>GC.setLineStyle()</code> method.
 */
public class LineStyleTab extends GraphicsTab {

	Button colorButton;
	GraphicsBackground lineColor;
	Menu menu;
	Spinner lineWidthSpinner;

public LineStyleTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Lines"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("LineStyles"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("LineStylesDescription"); //$NON-NLS-1$
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

	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	new Label(comp, SWT.CENTER).setText(GraphicsExample
			.getResourceString("LineWidth")); //$NON-NLS-1$
	this.lineWidthSpinner = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.lineWidthSpinner.setSelection(10);
	this.lineWidthSpinner.setMinimum(1);
	this.lineWidthSpinner.setMaximum(30);
	this.lineWidthSpinner.addListener(SWT.Selection, event -> this.example.redraw());

	final ColorMenu cm = new ColorMenu();
	cm.setPatternItems(this.example.checkAdvancedGraphics());
	this.menu = cm.createMenu(parent.getParent(), gb -> {
		this.lineColor = gb;
		this.colorButton.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// create color button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());

	// initialize the foreground to the 5th item in the menu (blue)
	this.lineColor = (GraphicsBackground)this.menu.getItem(4).getData();

	// color button
	this.colorButton = new Button(comp, SWT.PUSH);
	this.colorButton.setText(GraphicsExample
			.getResourceString("Color")); //$NON-NLS-1$
	this.colorButton.setImage(this.lineColor.getThumbNail());
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

	Pattern pattern = null;
	if (this.lineColor.getBgColor1() != null) {
		gc.setForeground(this.lineColor.getBgColor1());
	} else if (this.lineColor.getBgImage() != null) {
		pattern = new Pattern(device, this.lineColor.getBgImage());
		gc.setForegroundPattern(pattern);
	}

	// set line width
	gc.setLineWidth(this.lineWidthSpinner.getSelection());

	// draw lines with caps
	gc.drawLine((3*width)/16, (1*height)/6, (13*width)/16, (1*height)/6);
	gc.setLineStyle(SWT.LINE_DASH);
	gc.drawLine((3*width)/16, (2*height)/6, (13*width)/16, (2*height)/6);
	gc.setLineStyle(SWT.LINE_DOT);
	gc.drawLine((3*width)/16, (3*height)/6, (13*width)/16, (3*height)/6);
	gc.setLineStyle(SWT.LINE_DASHDOT);
	gc.drawLine((3*width)/16, (4*height)/6, (13*width)/16, (4*height)/6);
	gc.setLineStyle(SWT.LINE_DASHDOTDOT);
	gc.drawLine((3*width)/16, (5*height)/6, (13*width)/16, (5*height)/6);

	// draw labels
	final Font font = new Font(device, getPlatformFont(), 20, SWT.NORMAL);
	gc.setFont(font);

	gc.setForeground(device.getSystemColor(SWT.COLOR_BLACK));

	String text = GraphicsExample.getResourceString("Solid"); //$NON-NLS-1$
	Point size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (1*height)/12, true);
	text = GraphicsExample.getResourceString("Dash"); //$NON-NLS-1$
	size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (3*height)/12, true);
	text = GraphicsExample.getResourceString("Dot"); //$NON-NLS-1$
	size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (5*height)/12, true);
	text = GraphicsExample.getResourceString("DashDot"); //$NON-NLS-1$
	size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (7*height)/12, true);
	text = GraphicsExample.getResourceString("DashDotDot"); //$NON-NLS-1$
	size = gc.stringExtent(text);
	gc.drawString(text, (width-size.x)/2, (9*height)/12, true);
	font.dispose();
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

