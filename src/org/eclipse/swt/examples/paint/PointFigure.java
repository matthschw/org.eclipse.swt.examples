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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;

/**
 * 2D Point object
 */
public class PointFigure extends Figure {
	private final Color color;
	private final int x, y;
	/**
	 * Constructs a Point
	 *
	 * @param color the color for this object
	 * @param x the virtual X coordinate of the first end-point
	 * @param y the virtual Y coordinate of the first end-point
	 */
	public PointFigure(final Color color, final int x, final int y) {
		this.color = color; this.x = x; this.y = y;
	}
	@Override
	public void draw(final FigureDrawContext fdc) {
		final Point p = fdc.toClientPoint(this.x, this.y);
		fdc.gc.setBackground(this.color);
		fdc.gc.fillRectangle(p.x, p.y, 1, 1);
	}
	@Override
	public void addDamagedRegion(final FigureDrawContext fdc, final Region region) {
		region.add(fdc.toClientRectangle(this.x, this.y, this.x, this.y));
	}
}
