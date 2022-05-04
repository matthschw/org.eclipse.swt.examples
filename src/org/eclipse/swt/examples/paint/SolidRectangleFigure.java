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
package org.eclipse.swt.examples.paint;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;

/**
 * 2D SolidRectangle object
 */
public class SolidRectangleFigure extends Figure {
	private final Color color;
	private final int x1, y1, x2, y2;
	/**
	 * Constructs a SolidRectangle
	 * These objects are defined by any two diametrically opposing corners.
	 *
	 * @param color the color for this object
	 * @param x1 the virtual X coordinate of the first corner
	 * @param y1 the virtual Y coordinate of the first corner
	 * @param x2 the virtual X coordinate of the second corner
	 * @param y2 the virtual Y coordinate of the second corner
	 */
	public SolidRectangleFigure(final Color color, final int x1, final int y1, final int x2, final int y2) {
		this.color = color; this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}
	@Override
	public void draw(final FigureDrawContext fdc) {
		final Rectangle r = fdc.toClientRectangle(this.x1, this.y1, this.x2, this.y2);
		fdc.gc.setBackground(this.color);
		fdc.gc.fillRectangle(r.x, r.y, r.width, r.height);
	}
	@Override
	public void addDamagedRegion(final FigureDrawContext fdc, final Region region) {
		region.add(fdc.toClientRectangle(this.x1, this.y1, this.x2, this.y2));
	}
}
