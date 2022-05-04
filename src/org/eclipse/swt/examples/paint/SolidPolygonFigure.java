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
 * 2D Line object
 */
public class SolidPolygonFigure extends Figure {
	private final Color color;
	private final int[] points;
	/**
	 * Constructs a SolidPolygon
	 * These objects are defined by a sequence of vertices.
	 *
	 * @param color the color for this object
	 * @param vertices the array of vertices making up the polygon
	 * @param numPoint the number of valid points in the array (n >= 3)
	 */
	public SolidPolygonFigure(final Color color, final Point[] vertices, final int numPoints) {
		this.color = color;
		this.points = new int[numPoints * 2];
		for (int i = 0; i < numPoints; ++i) {
			this.points[i * 2] = vertices[i].x;
			this.points[(i * 2) + 1] = vertices[i].y;
		}
	}
	@Override
	public void draw(final FigureDrawContext fdc) {
		final int[] drawPoints = new int[this.points.length];
		for (int i = 0; i < this.points.length; i += 2) {
			drawPoints[i] = (this.points[i] * fdc.xScale) - fdc.xOffset;
			drawPoints[i + 1] = (this.points[i + 1] * fdc.yScale) - fdc.yOffset;
		}
		fdc.gc.setBackground(this.color);
		fdc.gc.fillPolygon(drawPoints);
	}
	@Override
	public void addDamagedRegion(final FigureDrawContext fdc, final Region region) {
		int xmin = Integer.MAX_VALUE, ymin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE, ymax = Integer.MIN_VALUE;

		for (int i = 0; i < this.points.length; i += 2) {
			if (this.points[i] < xmin) {
        xmin = this.points[i];
      }
			if (this.points[i] > xmax) {
        xmax = this.points[i];
      }
			if (this.points[i+1] < ymin) {
        ymin = this.points[i+1];
      }
			if (this.points[i+1] > ymax) {
        ymax = this.points[i+1];
      }
		}
		region.add(fdc.toClientRectangle(xmin, ymin, xmax, ymax));
	}
}
