/*******************************************************************************
 * Copyright (c) 2006, 2013 IBM Corporation and others.
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
import org.eclipse.swt.graphics.Transform;

/**
 * This tab draws 3D shapes (in 2D) using various line styles.
 */
public class ShapesTab extends AnimatedGraphicsTab {

	int upDownValue;
	int inc = 1;

public ShapesTab(final GraphicsExample example) {
	super(example);
	this.upDownValue = 0;
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Lines"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("Shapes"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("ShapesDescription"); //$NON-NLS-1$
}

@Override
public void next(final int width, final int height) {
	this.upDownValue += this.inc;

	if (this.upDownValue > 5) {
    this.inc = -1;
  }
	if (this.upDownValue < -5) {
    this.inc = 1;
  }
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	final int size = 100;

	gc.setLineWidth(2);

	// ----- cube -----

	Transform transform = new Transform(device);
	transform.translate((width/4) - size, (height/4) + -this.upDownValue);
	gc.setTransform(transform);

	gc.setLineStyle(SWT.LINE_DOT);

	// fill in left face
	gc.setBackground(device.getSystemColor(SWT.COLOR_RED));
	gc.fillPolygon(new int [] {0, 0, size/3, -size/2, size/3, size/2, 0, size});

	gc.setLineStyle(SWT.LINE_SOLID);

	// square
	gc.drawRectangle(0, 0, size, size);

	// next 3 solid lines
	gc.drawLine(0, 0, size/3, -size/2);			// left
	gc.drawLine(size, 0, (4*size)/3, -size/2);	// middle
	gc.drawLine(size, size, (4*size)/3, size/2);	// right

	// 2 furthest solid lines
	gc.drawLine(size/3, -size/2, (4*size)/3, -size/2);  	// horizontal
	gc.drawLine((4*size)/3, size/2, (4*size)/3, -size/2);	// vertical

	// 3 dotted lines
	gc.setLineStyle(SWT.LINE_DOT);
	gc.drawLine(0, size, size/3, size/2);
	gc.drawLine(size/3, -size/2, size/3, size/2);
	gc.drawLine((4*size)/3, size/2, size/3, size/2);

	// fill right side of cube
	gc.setBackground(device.getSystemColor(SWT.COLOR_GRAY));
	gc.fillPolygon(new int [] {size, 0, (4*size)/3, -size/2, (4*size)/3, size/2, size, size});

	transform.dispose();

	// ----- pyramid -----

	transform = new Transform(device);
	transform.translate((width/2) + (size/2), (height/4) + size + this.upDownValue);
	gc.setTransform(transform);

	// fill back of pyramid
	gc.fillPolygon(new int [] {size/3, -size/2, (6*size)/10, (-5*size)/4, (4*size)/3, -size/2});

	// fill left side of pyramid
	gc.setBackground(device.getSystemColor(SWT.COLOR_GREEN));
	gc.fillPolygon(new int [] {0, 0, (6*size)/10, (-5*size)/4, size/3, -size/2});

	// select solid line style
	gc.setLineStyle(SWT.LINE_SOLID);

	// 2 solid lines of base
	gc.drawLine(0, 0, size, 0);
	gc.drawLine(size, 0, (4*size)/3, -size/2);

	// 3 solid lines of pyramid
	gc.drawLine(0, 0, (6*size)/10, (-5*size)/4);
	gc.drawLine(size, 0, (6*size)/10, (-5*size)/4);
	gc.drawLine((4*size)/3, -size/2, (6*size)/10, (-5*size)/4);

	// select dot line style
	gc.setLineStyle(SWT.LINE_DOT);

	// 3 dotted lines
	gc.drawLine(0, 0, size/3, -size/2);					// left
	gc.drawLine(size/3, -size/2, (6*size)/10, (-5*size)/4); // to top of pyramid
	gc.drawLine((4*size)/3, -size/2, size/3, -size/2);	// right

	transform.dispose();

	// ----- rectangular prism -----

	transform = new Transform(device);
	transform.translate((width/2) + this.upDownValue, (height/2) + size);
	gc.setTransform(transform);

	// fill bottom
	gc.setBackground(device.getSystemColor(SWT.COLOR_BLUE));
	gc.fillPolygon(new int [] {0, size, size/3, size/2, (7*size)/3, size/2, 2*size, size});

	// select solid line style
	gc.setLineStyle(SWT.LINE_SOLID);

	gc.drawRectangle(0, 0, 2*size, size);

	// next 3 solid lines
	gc.drawLine(0, 0, size/3, -size/2);			// left
	gc.drawLine(2*size, 0, (7*size)/3, -size/2);	// middle
	gc.drawLine(2*size, size, (7*size)/3, size/2);	// right

	// 2 furthest solid lines
	gc.drawLine(size/3, -size/2, (7*size)/3, -size/2);  	// horizontal
	gc.drawLine((7*size)/3, size/2, (7*size)/3, -size/2);	// vertical

	// 3 dotted lines
	gc.setLineStyle(SWT.LINE_DASHDOTDOT);
	gc.drawLine(0, size, size/3, size/2);
	gc.drawLine(size/3, -size/2, size/3, size/2);
	gc.drawLine((7*size)/3, size/2, size/3, size/2);

	// fill top
	gc.setBackground(device.getSystemColor(SWT.COLOR_GRAY));
	gc.fillPolygon(new int [] {0, 0, size/3, -size/2, (7*size)/3, -size/2, 2*size, 0});

	transform.dispose();

	// ----- triangular shape -----
	transform = new Transform(device);
	transform.translate((width/4) - size - this.upDownValue, (height/2) + size + this.upDownValue);
	gc.setTransform(transform);

	// fill back of shape (top left)
	gc.setBackground(device.getSystemColor(SWT.COLOR_YELLOW));
	gc.fillPolygon(new int [] {0, 0, size/2, -size, size/2, -size/3});

	// fill back of shape (bottom right)
	gc.fillPolygon(new int [] {size, 0, size/2, size, size/2, -size/3});

	// select solid line style
	gc.setLineStyle(SWT.LINE_SOLID);

	// solid lines of bottom triangle
	gc.drawLine(0, 0, size/2, size);
	gc.drawLine(size, 0, size/2, size);

	// solid lines of top triangle
	gc.drawLine(0, 0, size/2, -size);
	gc.drawLine(size, 0, size/2, -size);

	// solid lines on top
	gc.drawLine(0, 0, size/2, -size/3);
	gc.drawLine(size, 0, size/2, -size/3);
	gc.drawLine(size/2, -size/3, size/2, size);
	gc.drawLine(size/2, -size/3, size/2, -size);

	transform.dispose();
}

}

