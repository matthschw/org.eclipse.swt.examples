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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

/**
 * Miscellaneous tab that demonstrates emerging colors from layering other
 * colors.
 */
public class RGBTab extends AnimatedGraphicsTab {

	int translateX, translateY;
	float diagTranslateX1, diagTranslateX2, diagTranslateY1, diagTranslateY2;

/**
 * Constructor
 * @param example A GraphicsExample
 */
public RGBTab(final GraphicsExample example) {
	super(example);
	this.translateX = this.translateY = 0;
	this.diagTranslateX1 = this.diagTranslateX2 = this.diagTranslateY1 = this.diagTranslateY2 = 0;
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Misc"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("rgb"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("rgbDescription"); //$NON-NLS-1$
}

@Override
public void next(final int width, final int height) {

	final float h = height;
	final float w = width;

	this.translateX = (this.translateX+3)%width;
	this.translateY = (this.translateY+5)%height;

	this.diagTranslateX1 = (this.diagTranslateX1+6)%width;
	this.diagTranslateY1 = this.diagTranslateX1*(h/w);

	this.diagTranslateX2 = (this.diagTranslateX2+8)%width;
	this.diagTranslateY2 = (-this.diagTranslateX2*(h/w)) + h;
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	// horizontal rectangle
	Transform transform = new Transform(device);
	transform.translate(0, this.translateY);
	gc.setTransform(transform);
	transform.dispose();

	Path path = new Path(device);
	path.addRectangle(0, 0, width, 50);
	Pattern pattern = new Pattern(device, 0, 0, width, 50,
				device.getSystemColor(SWT.COLOR_BLUE), 0x7f,
				device.getSystemColor(SWT.COLOR_RED), 0x7f);
	gc.setBackgroundPattern(pattern);
	gc.fillPath(path);
	gc.drawPath(path);
	path.dispose();

	// vertical rectangle
	transform = new Transform(device);
	transform.translate(this.translateX, 0);
	gc.setTransform(transform);
	transform.dispose();

	path = new Path(device);
	path.addRectangle(0, 0, 50, height);
	pattern.dispose();
	pattern = new Pattern(device, 0, 0, 50, height,
				device.getSystemColor(SWT.COLOR_DARK_CYAN), 0x7f,
				device.getSystemColor(SWT.COLOR_WHITE), 0x7f);
	gc.setBackgroundPattern(pattern);
	gc.fillPath(path);
	gc.drawPath(path);
	path.dispose();

	// diagonal rectangle from bottom right corner
	final Rectangle rect = new Rectangle(0, 0, 50, height);
	transform = new Transform(device);
	transform.translate(width-this.diagTranslateX1, (height/2)-this.diagTranslateY1);

	// rotate on center of rectangle
	transform.translate(rect.width/2, rect.height/2);
	transform.rotate(45);
	transform.translate(-rect.width/2, -rect.height/2);
	gc.setTransform(transform);
	transform.dispose();

	path = new Path(device);
	path.addRectangle(rect.x, rect.y, rect.width, rect.height);
	pattern.dispose();
	pattern = new Pattern(device, rect.x, rect.y, rect.width, rect.height,
			device.getSystemColor(SWT.COLOR_DARK_GREEN), 0x7f,
			device.getSystemColor(SWT.COLOR_DARK_MAGENTA), 0x7f);
	gc.setBackgroundPattern(pattern);
	gc.fillPath(path);
	gc.drawPath(path);
	path.dispose();

	// diagonal rectangle from top right corner
	transform = new Transform(device);
	transform.translate(width-this.diagTranslateX2, (height/2)-this.diagTranslateY2);

	// rotate on center of rectangle
	transform.translate(rect.width/2, rect.height/2);
	transform.rotate(-45);
	transform.translate(-rect.width/2, -rect.height/2);
	gc.setTransform(transform);
	transform.dispose();

	path = new Path(device);
	path.addRectangle(rect.x, rect.y, rect.width, rect.height);
	pattern.dispose();
	pattern = new Pattern(device, rect.x, rect.y, rect.width, rect.height,
			device.getSystemColor(SWT.COLOR_DARK_RED), 0x7f,
			device.getSystemColor(SWT.COLOR_YELLOW), 0x7f);
	gc.setBackgroundPattern(pattern);
	gc.fillPath(path);
	gc.drawPath(path);
	pattern.dispose();
	path.dispose();
}

}
