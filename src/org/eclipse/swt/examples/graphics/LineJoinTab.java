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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

/**
 * This tab demonstrates various line joins. It allows a user to choose from
 * bevel, miter and round.
 */
public class LineJoinTab extends GraphicsTab {

	private Combo joinCb;
	private Button colorButton;
	private GraphicsBackground shapeColor;
	private Menu menu;
	private final int [] joinValues = new int [] {SWT.JOIN_BEVEL, SWT.JOIN_MITER, SWT.JOIN_ROUND};

public LineJoinTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Lines"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("LineJoin"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("LineJoinDescription"); //$NON-NLS-1$
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

	// create drop down combo for choosing clipping
	Composite comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	new Label(comp, SWT.CENTER).setText(GraphicsExample
				.getResourceString("LineJoin")); //$NON-NLS-1$
	this.joinCb = new Combo(comp, SWT.DROP_DOWN);
	this.joinCb.add(GraphicsExample
			.getResourceString("bevel")); //$NON-NLS-1$
	this.joinCb.add(GraphicsExample
			.getResourceString("miter")); //$NON-NLS-1$
	this.joinCb.add(GraphicsExample
			.getResourceString("round")); //$NON-NLS-1$
	this.joinCb.select(1);
	this.joinCb.addListener(SWT.Selection, event -> this.example.redraw());

	// color menu
	final ColorMenu cm = new ColorMenu();
	cm.setPatternItems(this.example.checkAdvancedGraphics());
	this.menu = cm.createMenu(parent.getParent(), gb -> {
		this.shapeColor = gb;
		this.colorButton.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the shape color to the 4th item in the menu (green)
	this.shapeColor =(GraphicsBackground)this.menu.getItem(3).getData();

	// color button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	this.colorButton = new Button(comp, SWT.PUSH);
	this.colorButton.setText(GraphicsExample
			.getResourceString("Color")); //$NON-NLS-1$
	this.colorButton.setImage(this.shapeColor.getThumbNail());
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

	gc.setLineWidth(20);
	gc.setLineJoin(this.joinValues[this.joinCb.getSelectionIndex()]);

	// set the foreground color or pattern
	Pattern pattern = null;
	if (this.shapeColor.getBgColor1() != null) {
		gc.setForeground(this.shapeColor.getBgColor1());
	} else if (this.shapeColor.getBgImage() != null) {
		pattern = new Pattern(device, this.shapeColor.getBgImage());
		gc.setForegroundPattern(pattern);
	}

	// draw the shape
	final Path path = new Path(device);
	path.moveTo(width/2, 25);
	path.lineTo((2*width)/3, height/3);
	path.lineTo(width-25, height/2);
	path.lineTo((2*width)/3, (2*height)/3);
	path.lineTo(width/2, height-25);
	path.lineTo(width/3, (2*height)/3);
	path.lineTo(25, height/2);
	path.lineTo(width/3, height/3);
	path.lineTo(width/2, 25);
	path.close();
	gc.drawPath(path);
	path.dispose();

	if (pattern != null) {
    pattern.dispose();
  }
}

}


