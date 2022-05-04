/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
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

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class IntroTab extends AnimatedGraphicsTab {

	Font font;
	Image image;
	Random random = new Random();
	float x, y;
	float incX = 10.0f;
	float incY = 5.0f;
	int textWidth, textHeight;
	String text = GraphicsExample.getResourceString("SWT");

public IntroTab(final GraphicsExample example) {
	super(example);
}

@Override
public void dispose() {
	if (this.image != null) {
    this.image.dispose();
  }
	this.image = null;
	if (this.font != null) {
    this.font.dispose();
  }
	this.font = null;
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Introduction"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("SWT"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("IntroductionDescription"); //$NON-NLS-1$
}

@Override
public void next(final int width, final int height) {
	this.x += this.incX;
	this.y += this.incY;
	final float random = (float)Math.random();
	if ((this.x + this.textWidth) > width) {
		this.x = width - this.textWidth;
		this.incX = ((random * -width) / 16) - 1;
	}
	if (this.x < 0) {
		this.x = 0;
		this.incX = ((random * width) / 16) + 1;
	}
	if ((this.y + this.textHeight) > height) {
		this.y = (height - this.textHeight)- 2;
		this.incY = ((random * -height) / 16) - 1;
	}
	if (this.y < 0) {
		this.y = 0;
		this.incY = ((random * height) / 16) + 1;
	}
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();
	if (this.image == null) {
		this.image = this.example.loadImage(device, "irmaos.jpg");
		final Rectangle rect = this.image.getBounds();
		final FontData fd = device.getSystemFont().getFontData()[0];
		this.font = new Font(device, fd.getName(), rect.height / 4, SWT.BOLD);
		gc.setFont(this.font);
		final Point size = gc.stringExtent(this.text);
		this.textWidth = size.x;
		this.textHeight = size.y;
	}
	final Path path = new Path(device);
	path.addString(this.text, this.x, this.y, this.font);
	gc.setClipping(path);
	final Rectangle rect = this.image.getBounds();
	gc.drawImage(this.image, 0, 0, rect.width, rect.height, 0, 0, width, height);
	gc.setClipping((Rectangle)null);
	gc.setForeground(device.getSystemColor(SWT.COLOR_BLUE));
	gc.drawPath(path);
	path.dispose();
}
}
