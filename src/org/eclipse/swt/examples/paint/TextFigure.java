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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;

/**
 * 2D Rectangle object
 */
public class TextFigure extends Figure {
	private final Color  color;
	private final Font   font;
	private final String text;
	private final int x, y;
	/**
	 * Constructs a TextFigure
	 *
	 * @param color the color for this object
	 * @param font  the font for this object
	 * @param text  the text to draw, tab and new-line expansion is performed
	 * @param x     the virtual X coordinate of the top-left corner of the text bounding box
	 * @param y     the virtual Y coordinate of the top-left corner of the text bounding box
	 */
	public TextFigure(final Color color, final Font font, final String text, final int x, final int y) {
		this.color = color; this.font = font; this.text = text; this.x = x; this.y = y;
	}
	@Override
	public void draw(final FigureDrawContext fdc) {
		final Point p = fdc.toClientPoint(this.x, this.y);
		fdc.gc.setFont(this.font);
		fdc.gc.setForeground(this.color);
		fdc.gc.drawText(this.text, p.x, p.y, true);
	}
	@Override
	public void addDamagedRegion(final FigureDrawContext fdc, final Region region) {
		final Font oldFont = fdc.gc.getFont();
		fdc.gc.setFont(this.font);
		final Point textExtent = fdc.gc.textExtent(this.text);
		fdc.gc.setFont(oldFont);
		region.add(fdc.toClientRectangle(this.x, this.y, this.x + textExtent.x, this.y + textExtent.y));
	}
}
