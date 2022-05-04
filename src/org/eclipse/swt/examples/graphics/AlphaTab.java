/*******************************************************************************
 * Copyright (c) 2006, 2014 IBM Corporation and others.
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * This tab demonstrates alpha blending. It draws various shapes and images as
 * alpha values change.
 */
public class AlphaTab extends AnimatedGraphicsTab {
	/**
	 * Value used in setAlpha API call. Goes from 0 to 255 and then starts over.
	 */
	int alphaValue;

	/**
	 * Value used in setAlpha API call. Goes from 0 to 255, then from 255 to 0
	 * and then starts over.
	 */
	int alphaValue2;

	boolean reachedMax = false;
	int diameter;

	/** random numbers used for positioning "SWT" */
	int randX, randY;
	Image alphaImg1, alphaImg2;

	public AlphaTab(final GraphicsExample example) {
		super(example);
	}

	@Override
	public String getCategory() {
		return GraphicsExample.getResourceString("Alpha"); //$NON-NLS-1$
	}

	@Override
	public String getText() {
		return GraphicsExample.getResourceString("Alpha"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return GraphicsExample.getResourceString("AlphaDescription"); //$NON-NLS-1$
	}

	@Override
	public int getInitialAnimationTime() {
		return 20;
	}

	@Override
	public void dispose() {
		if (this.alphaImg1 != null) {
			this.alphaImg1.dispose();
			this.alphaImg1 = null;
		}
		if (this.alphaImg2 != null) {
			this.alphaImg2.dispose();
			this.alphaImg2 = null;
		}
	}

	@Override
	public void next(final int width, final int height) {
		this.alphaValue = (this.alphaValue+5)%255;

		this.alphaValue2 = this.reachedMax ? this.alphaValue2 - 5 : this.alphaValue2 + 5;

		if (this.alphaValue2 == 255) {
			this.reachedMax = true;
		} else if (this.alphaValue2 == 0) {
			this.reachedMax = false;
		}

		this.diameter = (this.diameter + 10)%(width > height ? width : height);
	}

	/**
	 * Paint the receiver into the specified GC.
	 */
	@Override
	public void paint(final GC gc, final int width, final int height) {
		if (!this.example.checkAdvancedGraphics()) {
      return;
    }
		final Device device = gc.getDevice();

		if (this.alphaImg1 == null) {
			this.alphaImg1 = GraphicsExample.loadImage(device, GraphicsExample.class, "alpha_img1.png");
			this.alphaImg2 = GraphicsExample.loadImage(device, GraphicsExample.class, "alpha_img2.png");
		}

		final Rectangle rect = this.alphaImg1.getBounds();

		gc.setAlpha(this.alphaValue);
		gc.drawImage(this.alphaImg1, rect.x, rect.y, rect.width, rect.height,
				width/2, height/2, width/4, height/4);

		gc.drawImage(this.alphaImg1, rect.x, rect.y, rect.width, rect.height,
				0, 0, width/4, height/4);

		gc.setAlpha(255-this.alphaValue);
		gc.drawImage(this.alphaImg2, rect.x, rect.y, rect.width, rect.height,
				width/2, 0, width/4, height/4);

		gc.drawImage(this.alphaImg2, rect.x, rect.y, rect.width, rect.height,
				0, (3*height)/4, width/4, height/4);

		// pentagon
		gc.setBackground(device.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		gc.fillPolygon(new int [] {width/10, height/2, (3*width)/10, (height/2)-(width/6), (5*width)/10, height/2,
				(4*width)/10, (height/2)+(width/6), (2*width)/10, (height/2)+(width/6)});

		gc.setBackground(device.getSystemColor(SWT.COLOR_RED));

		// square
		gc.setAlpha(this.alphaValue);
		gc.fillRectangle(width/2, height-75, 75, 75);

		// triangle
		gc.setAlpha(this.alphaValue + 15);
		gc.fillPolygon(new int[]{(width/2)+75, height-(2*75), (width/2)+75, height-75, (width/2)+(2*75), height-75});

		// triangle
		gc.setAlpha(this.alphaValue + 30);
		gc.fillPolygon(new int[]{(width/2)+80, height-(2*75), (width/2)+(2*75), height-(2*75), (width/2)+(2*75), height-80});

		// triangle
		gc.setAlpha(this.alphaValue + 45);
		gc.fillPolygon(new int[]{(width/2)+(2*75), height-(2*75), (width/2)+(3*75), height-(2*75), (width/2)+(3*75), height-(3*75)});

		// triangle
		gc.setAlpha(this.alphaValue + 60);
		gc.fillPolygon(new int[]{(width/2)+(2*75), height-((2*75)+5), (width/2)+(2*75), height-(3*75), (width/2)+((3*75)-5), height-(3*75)});

		// square
		gc.setAlpha(this.alphaValue + 75);
		gc.fillRectangle((width/2)+(3*75), height-(4*75), 75, 75);

		gc.setBackground(device.getSystemColor(SWT.COLOR_GREEN));

		// circle in top right corner
		gc.setAlpha(this.alphaValue2);
		gc.fillOval(width-100, 0, 100, 100);

		// triangle
		gc.setAlpha(this.alphaValue + 90);
		gc.fillPolygon(new int[]{width-300, 10, width-100, 10, width-275, 50});

		// triangle
		gc.setAlpha(this.alphaValue + 105);
		gc.fillPolygon(new int[]{width-10, 100, width-10, 300, width-50, 275});

		// quadrilateral shape
		gc.setAlpha(this.alphaValue + 120);
		gc.fillPolygon(new int[]{width-100, 100, width-200, 150, width-200, 200, width-150, 200});

		// blue circles
		gc.setBackground(device.getSystemColor(SWT.COLOR_BLUE));
		final int size = 50;
		int alpha = 20;
		for (int i = 0; i < 10; i++) {
			gc.setAlpha(this.alphaValue + alpha);
			if ((i % 2) > 0) {
				gc.fillOval(width-((i+1)*size), height-size, size, size);
			} else {
				gc.fillOval(width-((i+1)*size), height-((3*size)/2), size, size);
			}
			alpha = alpha + 20;
		}

		// SWT string appearing randomly
		gc.setAlpha(this.alphaValue2);
		final String text = GraphicsExample.getResourceString("SWT");
		final Font font = createFont(device, 100, SWT.NONE);
		gc.setFont(font);

		final Point textSize = gc.stringExtent(text);
		final int textWidth = textSize.x;
		final int textHeight = textSize.y;

		if (this.alphaValue2 == 0){
			this.randX = (int)(width*Math.random());
			this.randY = (int)(height*Math.random());
			this.randX = (this.randX > textWidth) ? this.randX - textWidth : this.randX;
			this.randY = (this.randY > textHeight) ? this.randY - textHeight : this.randY;
		}

		gc.drawString(text, this.randX, this.randY, true);
		font.dispose();

		// gray donut
		gc.setAlpha(100);
		final Path path = new Path(device);
		path.addArc((width-this.diameter)/2, (height-this.diameter)/2, this.diameter, this.diameter, 0, 360);
		path.close();
		path.addArc(((width-this.diameter)+25)/2, ((height-this.diameter)+25)/2, this.diameter-25, this.diameter-25, 0, 360);
		path.close();
		gc.setBackground(device.getSystemColor(SWT.COLOR_GRAY));
		gc.fillPath(path);
		gc.drawPath(path);
		path.dispose();
	}

	/**
	 * Creates a font using the specified arguments and returns it.
	 * This method takes into account the resident platform.
	 *
	 * @param face
	 * 			The name of the font
	 * @param points
	 * 			The size of the font in point
	 * @param style
	 * 			The style to be applied to the font
	 */
	static Font createFont(final Device device, final int points, final int style) {
		if(SWT.getPlatform() == "win32") {
			return new Font(device, "Verdana", points, style);
		} else if (SWT.getPlatform() == "gtk") {
			return new Font(device, "Baekmuk Batang", points, style);
		} else {
			return new Font(device, "Verdana", points, style);
		}
	}
}

