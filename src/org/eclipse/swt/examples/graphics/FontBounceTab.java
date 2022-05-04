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
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;

/**
 * This tab is an animated graphic. It presents the word SWT in different fonts
 * as it bounces around the screen.
 */
public class FontBounceTab extends AnimatedGraphicsTab {

	float x, y;
	float incX = 10.0f;				// units by which to move the word along X axis
	float incY = 5.0f;				// units by which to move the word along Y axis
	int textWidth, textHeight;		// width and height of the word SWT
	String text = GraphicsExample.getResourceString("SWT");
	int fontSize = 100;
	int fontFace = 0;
	int foreGrdColor, fillColor;	// font colors
	int fontStyle;				// represents various style attributes applicable to a Font

public FontBounceTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Font"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("Bounce"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("FontBounceDescription"); //$NON-NLS-1$
}

@Override
public void next(final int width, final int height) {
	this.x += this.incX;
	this.y += this.incY;
	final float random = (float)Math.random();

	// collision with right side of screen
	if ((this.x + this.textWidth) > width) {
		this.x = width - this.textWidth;
		this.incX = ((random * -width) / 16) - 1;
		this.fontFace = 0;
		this.fontSize = 125;
		this.fillColor = SWT.COLOR_DARK_BLUE;
		this.foreGrdColor = SWT.COLOR_YELLOW;
		this.fontStyle = SWT.ITALIC;
	}
	// collision with left side of screen
	if (this.x < 0) {
		this.x = 0;
		this.incX = ((random * width) / 16) + 1;
		this.fontFace = 1;
		this.fontSize = 80;
		this.fillColor = SWT.COLOR_DARK_MAGENTA;
		this.foreGrdColor = SWT.COLOR_CYAN;
		this.fontStyle = SWT.NONE;
	}
	// collision with bottom side of screen
	if ((this.y + this.textHeight) > height) {
		this.y = (height - this.textHeight)- 2;
		this.incY = ((random * -height) / 16) - 1;
		this.fontFace = 2;
		this.fontSize = 100;
		this.fillColor = SWT.COLOR_YELLOW;
		this.foreGrdColor = SWT.COLOR_BLACK;
		this.fontStyle = SWT.BOLD;
	}
	// collision with top side of screen
	if (this.y < 0) {
		this.y = 0;
		this.incY = ((random * height) / 16) + 1;
		this.fontFace = 3;
		this.fontSize = 120;
		this.fillColor = SWT.COLOR_GREEN;
		this.foreGrdColor = SWT.COLOR_GRAY;
		this.fontStyle = SWT.NONE;
	}
}


@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	final Font font = new Font(device, getPlatformFontFace(this.fontFace), this.fontSize, this.fontStyle);
	gc.setFont(font);

	final Point size = gc.stringExtent(this.text);
	this.textWidth = size.x;
	this.textHeight = size.y;

	final Path path = new Path(device);
	path.addString(this.text, this.x, this.y, font);

	gc.setForeground(device.getSystemColor(this.foreGrdColor));
	gc.setBackground(device.getSystemColor(this.fillColor));

	gc.fillPath(path);
	gc.drawPath(path);
	font.dispose();
	path.dispose();
}

/**
 * Returns the name of the font using the specified index.
 * This method takes into account the resident platform.
 *
 * @param index
 * 			The index of the font to be used
 */
static String getPlatformFontFace(final int index) {
	if(SWT.getPlatform() == "win32") {
		return new String [] {"Arial", "Impact", "Times", "Verdana"} [index];
	} else if (SWT.getPlatform() == "gtk") {
		return new String [] {"URW Chancery L", "Baekmuk Batang", "Baekmuk Headline", "KacsTitleL"} [index];
	} else {
		return new String [] {"Arial", "Impact", "Times", "Verdana"} [index];
	}
}
}
