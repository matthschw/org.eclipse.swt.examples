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


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;

/**
 * 2D Rectangle object
 */
public class RoundedRectangleFigure extends Figure {
	private final Color foregroundColor, backgroundColor;
	private final int lineStyle, x1, y1, x2, y2, diameter;
	/**
	 * Constructs a Rectangle
	 * These objects are defined by any two diametrically opposing corners.
	 *
	 * @param color the color for this object
	 * @param lineStyle the line style for this object
	 * @param x1 the virtual X coordinate of the first corner
	 * @param y1 the virtual Y coordinate of the first corner
	 * @param x2 the virtual X coordinate of the second corner
	 * @param y2 the virtual Y coordinate of the second corner
	 * @param diameter the diameter of curvature of all four corners
	 */
	public RoundedRectangleFigure(final Color foregroundColor, final Color backgroundColor, final int lineStyle, final int x1, final int y1, final int x2, final int y2, final int diameter) {
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
		this.lineStyle = lineStyle;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
		this.diameter = diameter;
	}
	@Override
	public void draw(final FigureDrawContext fdc) {
		final Rectangle r = fdc.toClientRectangle(this.x1, this.y1, this.x2, this.y2);
		fdc.gc.setForeground(this.foregroundColor);
		fdc.gc.setBackground(this.backgroundColor);
		fdc.gc.setLineStyle(this.lineStyle);
		fdc.gc.drawRoundRectangle(r.x, r.y, r.width - 1, r.height - 1, this.diameter, this.diameter);
		fdc.gc.setLineStyle(SWT.LINE_SOLID);
	}
	@Override
	public void addDamagedRegion(final FigureDrawContext fdc, final Region region) {
		region.add(fdc.toClientRectangle(this.x1, this.y1, this.x2, this.y2));
	}
}
