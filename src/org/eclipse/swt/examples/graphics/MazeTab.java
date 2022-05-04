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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * This tab shows three circles, each following a different path in a maze.
 * Only one of the three circles follows the correct path.
 */
public class MazeTab extends AnimatedGraphicsTab {

	int nextIndex, nextIndex2, nextIndex3;
	int xcoord, ycoord, xcoord2, ycoord2, xcoord3, ycoord3;
	List<Integer> nextCoord, nextCoord2, nextCoord3;
	boolean isDone, isDone2, isDone3;
	Image image;

/**
 * Constructor
 * @param example A GraphicsExample
 */
public MazeTab(final GraphicsExample example) {
	super(example);

	// correct path
	this.nextCoord = new ArrayList<>();
	this.nextCoord.addAll(this.moveDown(20, -50, 20, 110, 10));
	this.nextCoord.addAll(this.moveRight(30, 110, 130, 110, 10));
	this.nextCoord.addAll(this.moveUp(135, 100, 135, 15, 10));
	this.nextCoord.addAll(this.moveRight(140, 15, 210, 15, 10));
	this.nextCoord.addAll(this.moveDown(210, 25, 210, 75, 10));
	this.nextCoord.addAll(this.moveRight(220, 75, 320, 75, 10));
	this.nextCoord.addAll(this.moveUp(320, 65, 320, 55, 10));
	this.nextCoord.addAll(this.moveRight(330, 50, 475, 50, 10));
	this.nextCoord.addAll(this.moveDown(475, 60, 475, 225, 10));
	this.nextCoord.addAll(this.moveLeft(465, 225, 200, 225, 10));
	this.nextCoord.addAll(this.moveUp(200, 215, 200, 180, 10));
	this.nextCoord.addAll(this.moveLeft(190, 180, 120, 180, 10));
	this.nextCoord.addAll(this.moveDown(120, 190, 120, 320, 10));
	this.nextCoord.addAll(this.moveRight(130, 320, 475, 320, 10));
	this.nextCoord.addAll(this.moveDown(475, 330, 475, 435, 10));
	this.nextCoord.addAll(this.moveLeft(465, 435, 20, 435, 10));
	this.nextCoord.addAll(this.moveDown(20, 445, 20, 495, 10));

	this.nextIndex = 0;
	this.xcoord = this.nextCoord.get(this.nextIndex).intValue();
	this.ycoord = this.nextCoord.get(this.nextIndex+1).intValue();

	// wrong path 1
	this.nextCoord2 = new ArrayList<>();
	this.nextCoord2.addAll(this.moveDown(20, -25, 20, 110, 10));
	this.nextCoord2.addAll(this.moveRight(30, 110, 130, 110, 10));
	this.nextCoord2.addAll(this.moveUp(135, 100, 135, 15, 10));
	this.nextCoord2.addAll(this.moveRight(140, 15, 520, 15, 10));
	this.nextCoord2.addAll(this.moveDown(525, 15, 525, 480, 10));
	this.nextCoord2.addAll(this.moveLeft(515, 480, 70, 480, 10));

	this.nextIndex2 = 0;
	this.xcoord2 = this.nextCoord2.get(this.nextIndex2).intValue();
	this.ycoord2 = this.nextCoord2.get(this.nextIndex2+1).intValue();

	// wrong path 2
	this.nextCoord3 = new ArrayList<>();
	this.nextCoord3.addAll(this.moveDown(20, 0, 20, 110, 10));
	this.nextCoord3.addAll(this.moveRight(30, 110, 130, 110, 10));
	this.nextCoord3.addAll(this.moveUp(135, 100, 135, 15, 10));
	this.nextCoord3.addAll(this.moveRight(140, 15, 210, 15, 10));
	this.nextCoord3.addAll(this.moveDown(210, 25, 210, 75, 10));
	this.nextCoord3.addAll(this.moveRight(220, 75, 320, 75, 10));
	this.nextCoord3.addAll(this.moveUp(320, 65, 320, 55, 10));
	this.nextCoord3.addAll(this.moveRight(330, 50, 475, 50, 10));
	this.nextCoord3.addAll(this.moveDown(475, 60, 475, 225, 10));
	this.nextCoord3.addAll(this.moveLeft(465, 225, 425, 225, 10));
	this.nextCoord3.addAll(this.moveUp(420, 225, 420, 150, 10));
	this.nextCoord3.addAll(this.moveLeft(420, 145, 70, 145, 10));
	this.nextCoord3.addAll(this.moveDown(70, 150, 70, 320, 10));

	this.nextIndex3 = 0;
	this.xcoord3 = this.nextCoord3.get(this.nextIndex3).intValue();
	this.ycoord3 = this.nextCoord3.get(this.nextIndex3+1).intValue();

	this.isDone = this.isDone2 = this.isDone3 = false;
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Misc"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("Maze"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("MazeDescription"); //$NON-NLS-1$
}

@Override
public int getInitialAnimationTime() {
	return 50;
}

@Override
public void dispose() {
	if (this.image != null) {
		this.image.dispose();
		this.image = null;
	}
}

@Override
public void createControlPanel(final Composite parent) {
	super.createControlPanel(parent);

	// add selection listener to reset nextNumber after
	// the sequence has completed
	this.playItem.addListener(SWT.Selection, event -> {
		if (this.isDone){
			this.nextIndex = this.nextIndex2 = this.nextIndex3 = 0;
			this.isDone = this.isDone2 = this.isDone3 = false;
		}
	});
}

@Override
public void next(final int width, final int height) {

	if ((this.nextIndex+2) < this.nextCoord.size()) {
		this.nextIndex = (this.nextIndex+2)%this.nextCoord.size();
		this.xcoord = this.nextCoord.get(this.nextIndex).intValue();
		this.ycoord = this.nextCoord.get(this.nextIndex+1).intValue();
	} else {
		// stop animation
		this.setAnimation(false);
		this.isDone = true;
	}

	if ((this.nextIndex2+2) < this.nextCoord2.size()) {
		this.nextIndex2 = (this.nextIndex2+2)%this.nextCoord2.size();
		this.xcoord2 = this.nextCoord2.get(this.nextIndex2).intValue();
		this.ycoord2 = this.nextCoord2.get(this.nextIndex2+1).intValue();
	} else {
		this.isDone2 = true;
	}

	if ((this.nextIndex3+2) < this.nextCoord3.size()) {
		this.nextIndex3 = (this.nextIndex3+2)%this.nextCoord3.size();
		this.xcoord3 = this.nextCoord3.get(this.nextIndex3).intValue();
		this.ycoord3 = this.nextCoord3.get(this.nextIndex3+1).intValue();
	} else {
		this.isDone3 = true;
	}
}

@Override
public void paint(final GC gc, final int width, final int height) {
	final Device device = gc.getDevice();

	if (this.image == null) {
		this.image = this.example.loadImage(device, "maze.bmp");
	}
	// draw maze
	final Rectangle bounds = this.image.getBounds();
	final int x = (width - bounds.width) / 2;
	final int y = (height - bounds.height) / 2;
	gc.drawImage(this.image, x, y);

	// draw correct oval
	gc.setBackground(device.getSystemColor(SWT.COLOR_RED));
	gc.fillOval(x + this.xcoord, y + this.ycoord, 16, 16);
	gc.drawOval(x + this.xcoord, y + this.ycoord, 15, 15);

	// draw wrong oval 1
	gc.setBackground(device.getSystemColor(SWT.COLOR_BLUE));
	gc.fillOval(x + this.xcoord2, y + this.ycoord2, 16, 16);
	gc.drawOval(x + this.xcoord2, y + this.ycoord2, 15, 15);

	// draw wrong oval 2
	gc.setBackground(device.getSystemColor(SWT.COLOR_GREEN));
	gc.fillOval(x + this.xcoord3, y + this.ycoord3, 16, 16);
	gc.drawOval(x + this.xcoord3, y + this.ycoord3, 15, 15);

	if (this.isDone2) {
		final Image helpImg = this.example.loadImage(device, "help.gif");
		gc.drawImage(helpImg, x + this.xcoord2 + 16, (y + this.ycoord2) - 16);
		helpImg.dispose();
	}

	if (this.isDone3) {
		final Image helpImg = this.example.loadImage(device, "help.gif");
		gc.drawImage(helpImg, x + this.xcoord3 + 16, (y + this.ycoord3) - 16);
		helpImg.dispose();
	}
}

/**
 * Returns a list of coordinates moving in a "left-moving" fashion from the start
 * point to the end point inclusively.
 *
 * @param x1
 *            X component of the start point
 * @param y1
 *            Y component of the start point
 * @param x2
 *            X component of the end point
 * @param y2
 *            Y component of the end point
 * @param stepsize
 *            The number of pixels that separate each coordinate
 */
private List<Integer> moveLeft(int x1, final int y1, final int x2, final int y2, final int stepsize) {
	final List<Integer> coords = new ArrayList<>();
	coords.add(Integer.valueOf(x1));
	coords.add(Integer.valueOf(y1));
	while((x1 - stepsize) > x2) {
		x1 = x1 - stepsize;
		coords.add(Integer.valueOf(x1));
		coords.add(Integer.valueOf(y1));
	}
	coords.add(Integer.valueOf(x2));
	coords.add(Integer.valueOf(y2));
	return coords;
}

/**
 * Returns a list of coordinates moving in a "right-moving" fashion from the start
 * point to the end point inclusively.
 *
 * @param x1
 *            X component of the start point
 * @param y1
 *            Y component of the start point
 * @param x2
 *            X component of the end point
 * @param y2
 *            Y component of the end point
 * @param stepsize
 *            The number of pixels that separate each coordinate
 */
private List<Integer> moveRight(int x1, final int y1, final int x2, final int y2, final int stepsize) {
	final List<Integer> coords = new ArrayList<>();
	coords.add(Integer.valueOf(x1));
	coords.add(Integer.valueOf(y1));
	while((x1 + stepsize) < x2) {
		x1 = x1 + stepsize;
		coords.add(Integer.valueOf(x1));
		coords.add(Integer.valueOf(y1));
	}
	coords.add(Integer.valueOf(x2));
	coords.add(Integer.valueOf(y2));
	return coords;
}

/**
 * Returns a list of coordinates moving in an upward fashion from the start
 * point to the end point inclusively.
 *
 * @param x1
 *            X component of the start point
 * @param y1
 *            Y component of the start point
 * @param x2
 *            X component of the end point
 * @param y2
 *            Y component of the end point
 * @param stepsize
 *            The number of pixels that separate each coordinate
 */
private List<Integer> moveUp(final int x1, int y1, final int x2, final int y2, final int stepsize) {
	final List<Integer> coords = new ArrayList<>();
	coords.add(Integer.valueOf(x1));
	coords.add(Integer.valueOf(y1));
	while((y1 - stepsize) > y2) {
		y1 = y1 - stepsize;
		coords.add(Integer.valueOf(x1));
		coords.add(Integer.valueOf(y1));
	}
	coords.add(Integer.valueOf(x2));
	coords.add(Integer.valueOf(y2));
	return coords;
}

/**
 * Returns a list of coordinates moving in a downward fashion from the start
 * point to the end point inclusively.
 *
 * @param x1
 *            X component of the start point
 * @param y1
 *            Y component of the start point
 * @param x2
 *            X component of the end point
 * @param y2
 *            Y component of the end point
 * @param stepsize
 *            The number of pixels that separate each coordinate
 */
private List<Integer> moveDown(final int x1, int y1, final int x2, final int y2, final int stepsize) {
	final List<Integer> coords = new ArrayList<>();
	coords.add(Integer.valueOf(x1));
	coords.add(Integer.valueOf(y1));
	while((y1 + stepsize) < y2) {
		y1 = y1 + stepsize;
		coords.add(Integer.valueOf(x1));
		coords.add(Integer.valueOf(y1));
	}
	coords.add(Integer.valueOf(x2));
	coords.add(Integer.valueOf(y2));
	return coords;
}

}
