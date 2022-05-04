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

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

/**
 * This tab demonstrates various transformations, such as scaling, rotation, and
 * translation.
 */
public class CardsTab extends AnimatedGraphicsTab {

	float movClubX, movClubY, movDiamondX, movDiamondY, movHeart, movSpade;
	float inc_club = 5.0f;
	float inc_diamond = 5.0f;
	float inc_hearts = 5.0f;
	float inc_spade = 5.0f;
	float scale, scaleWidth;
	int rotationAngle = 0;
	float scaleArg = 0;
	float heartScale = 0.5f;
	float spadeScale = 0.333f;
	int clubWidth, diamondWidth, heartWidth, spadeHeight;

	Image ace_club, ace_spade, ace_diamond, ace_hearts;

/**
 * Constructor
 * @param example A GraphicsExample
 */
public CardsTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Transform"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("Cards"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("CardsDescription"); //$NON-NLS-1$
}

@Override
public void dispose() {
	if (this.ace_club != null) {
		this.ace_club.dispose();
		this.ace_club = null;
		this.ace_spade.dispose();
		this.ace_spade = null;
		this.ace_diamond.dispose();
		this.ace_diamond = null;
		this.ace_hearts.dispose();
		this.ace_hearts = null;
	}
}
@Override
public void next(final int width, final int height) {
	this.rotationAngle = (this.rotationAngle+10)%360;

	// scaleVal goes from 0 to 1, then 1 to 0, then starts over
	this.scaleArg = (float)((this.scaleArg == 1) ? this.scaleArg - 0.1 : this.scaleArg + 0.1);
	this.scale = (float)Math.cos(this.scaleArg);

	this.movClubX += this.inc_club;
	this.movDiamondX += this.inc_diamond;
	this.movHeart += this.inc_hearts;
	this.movSpade += this.inc_spade;

	this.scaleWidth = (float) (((this.movClubY/height)*0.35) + 0.15);
	this.movClubY = (((2*height)/5) * (float)Math.sin((0.01*this.movClubX) - 90)) + ((2*height)/5);
	this.movDiamondY = (((2*height)/5) * (float)Math.cos(0.01*this.movDiamondX)) + ((2*height)/5);

	if ((this.movClubX + (this.clubWidth*this.scaleWidth)) > width) {
		this.movClubX = width - (this.clubWidth*this.scaleWidth);
		this.inc_club = -this.inc_club;
	}
	if (this.movClubX < 0) {
		this.movClubX = 0;
		this.inc_club = -this.inc_club;
	}
	if ((this.movDiamondX + (this.diamondWidth*this.scaleWidth)) > width) {
		this.movDiamondX = width - (this.diamondWidth*this.scaleWidth);
		this.inc_diamond = -this.inc_diamond;
	}
	if (this.movDiamondX < 0) {
		this.movDiamondX = 0;
		this.inc_diamond = -this.inc_diamond;
	}
	if ((this.movHeart + (this.heartWidth*this.heartScale)) > width) {
		this.movHeart = width - (this.heartWidth*this.heartScale);
		this.inc_hearts = -this.inc_hearts;
	}
	if (this.movHeart < 0) {
		this.movHeart = 0;
		this.inc_hearts = -this.inc_hearts;
	}
	if ((this.movSpade + (this.spadeHeight*this.spadeScale)) > height) {
		this.movSpade = height - (this.spadeHeight*this.spadeScale);
		this.inc_spade = -this.inc_spade;
	}
	if (this.movSpade < 0) {
		this.movSpade = 0;
		this.inc_spade = -this.inc_spade;
	}
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	if (this.ace_club == null) {
		this.ace_club = GraphicsExample.loadImage(device, GraphicsExample.class, "ace_club.jpg");
		this.ace_spade = GraphicsExample.loadImage(device, GraphicsExample.class, "ace_spade.jpg");
		this.ace_diamond = GraphicsExample.loadImage(device, GraphicsExample.class, "ace_diamond.jpg");
		this.ace_hearts = GraphicsExample.loadImage(device, GraphicsExample.class, "ace_hearts.jpg");
	}

	this.clubWidth = this.ace_club.getBounds().width;
	this.diamondWidth = this.ace_diamond.getBounds().width;
	this.heartWidth = this.ace_hearts.getBounds().width;
	this.spadeHeight = this.ace_spade.getBounds().height;

	Transform transform;

	// ace of clubs
	transform = new Transform(device);
	transform.translate((int)this.movClubX, (int)this.movClubY);
	transform.scale(this.scaleWidth, this.scaleWidth);

	// rotate on center of image
	final Rectangle rect = this.ace_club.getBounds();
	transform.translate(rect.width/2, rect.height/2);
	transform.rotate(this.rotationAngle);
	transform.translate(-rect.width/2, -rect.height/2);

	gc.setTransform(transform);
	transform.dispose();
	gc.drawImage(this.ace_club, 0, 0);

	// ace of diamonds
	transform = new Transform(device);
	transform.translate((int)this.movDiamondX, (int)this.movDiamondY);
	transform.scale(this.scaleWidth, this.scaleWidth);
	gc.setTransform(transform);
	transform.dispose();
	gc.drawImage(this.ace_diamond, 0, 0);

	// ace of hearts
	transform = new Transform(device);
	transform.translate(this.movHeart, height/2);
	transform.scale(this.heartScale, 0.5f*this.scale);
	gc.setTransform(transform);
	transform.dispose();
	gc.drawImage(this.ace_hearts, 0, 0);

	// ace of spades
	transform = new Transform(device);
	transform.translate(this.movSpade, this.movSpade);
	transform.scale(0.5f*this.scale, this.spadeScale);
	gc.setTransform(transform);
	transform.dispose();
	gc.drawImage(this.ace_spade, 0, 0);
}
}

