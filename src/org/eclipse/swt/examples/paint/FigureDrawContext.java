/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class FigureDrawContext {
	/*
	 * <p>
	 * The GC must be set up as follows
	 * (it will be returned to this state upon completion of drawing operations)
	 * <ul>
	 *   <li>setXORMode(false)
	 * </ul>
	 * </p>
	 */
	public GC gc = null;
	public int xOffset = 0, yOffset = 0; // substract to get GC coords
	public int xScale = 1, yScale = 1;

	public Rectangle toClientRectangle(final int x1, final int y1, final int x2, final int y2) {
		return new Rectangle(
			(Math.min(x1, x2) * this.xScale) - this.xOffset,
			(Math.min(y1, y2) * this.yScale) - this.yOffset,
			(Math.abs(x2 - x1) + 1) * this.xScale,
			(Math.abs(y2 - y1) + 1) * this.yScale);
	}
	public Point toClientPoint(final int x, final int y) {
		return new Point((x * this.xScale) - this.xOffset, (y * this.yScale) - this.yOffset);
	}
}
