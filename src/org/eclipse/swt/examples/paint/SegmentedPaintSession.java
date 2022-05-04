/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

/**
 * The superclass for paint tools that contruct objects from individually
 * picked segments.
 */
public abstract class SegmentedPaintSession extends BasicPaintSession {
	/**
	 * The set of control points making up the segmented selection
	 */
	private final List<Point> controlPoints = new ArrayList<>();

	/**
	 * The previous figure (so that we can abort with right-button)
	 */
	private Figure previousFigure = null;

	/**
	 * The current figure (so that we can abort with right-button)
	 */
	private Figure currentFigure = null;

	/**
	 * Constructs a PaintSession.
	 *
	 * @param paintSurface the drawing surface to use
	 */
	protected SegmentedPaintSession(final PaintSurface paintSurface) {
		super(paintSurface);
	}

	/**
	 * Activates the tool.
	 */
	@Override
	public void beginSession() {
		this.getPaintSurface().setStatusMessage(PaintExample.getResourceString(
			"session.SegmentedInteractivePaint.message.anchorMode"));
		this.previousFigure = null;
		this.currentFigure = null;
		this.controlPoints.clear();
	}

	/**
	 * Deactivates the tool.
	 */
	@Override
	public void endSession() {
		this.getPaintSurface().clearRubberbandSelection();
		if (this.previousFigure != null) {
      this.getPaintSurface().drawFigure(this.previousFigure);
    }
	}

	/**
	 * Resets the tool.
	 * Aborts any operation in progress.
	 */
	@Override
	public void resetSession() {
		this.getPaintSurface().clearRubberbandSelection();
		if (this.previousFigure != null) {
      this.getPaintSurface().drawFigure(this.previousFigure);
    }

		this.getPaintSurface().setStatusMessage(PaintExample.getResourceString(
			"session.SegmentedInteractivePaint.message.anchorMode"));
		this.previousFigure = null;
		this.currentFigure = null;
		this.controlPoints.clear();
	}

	/**
	 * Handles a mouseDown event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseDown(final MouseEvent event) {
		if (event.button != 1) {
      return;
    }

		this.getPaintSurface().setStatusMessage(PaintExample.getResourceString(
			"session.SegmentedInteractivePaint.message.interactiveMode"));
		this.previousFigure = this.currentFigure;

		if (this.controlPoints.size() > 0) {
			final Point lastPoint = this.controlPoints.get(this.controlPoints.size() - 1);
			if ((lastPoint.x == event.x) || (lastPoint.y == event.y))
       {
        return; // spurious event
      }
		}
		this.controlPoints.add(new Point(event.x, event.y));
	}

	/**
	 * Handles a mouseDoubleClick event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent event) {
		if (event.button != 1) {
      return;
    }
		if (this.controlPoints.size() >= 2) {
			this.getPaintSurface().clearRubberbandSelection();
			this.previousFigure = this.createFigure(
				this.controlPoints.toArray(new Point[this.controlPoints.size()]),
				this.controlPoints.size(), true);
		}
		this.resetSession();
	}

	/**
	 * Handles a mouseUp event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseUp(final MouseEvent event) {
		if (event.button != 1) {
			this.resetSession(); // abort if right or middle mouse button pressed
			return;
		}
	}

	/**
	 * Handles a mouseMove event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseMove(final MouseEvent event) {
		final PaintSurface ps = this.getPaintSurface();
		if (this.controlPoints.size() == 0) {
			ps.setStatusCoord(ps.getCurrentPosition());
			return; // spurious event
		}
		ps.setStatusCoordRange(this.controlPoints.get(this.controlPoints.size() - 1),
			ps.getCurrentPosition());
		ps.clearRubberbandSelection();
		final Point[] points = this.controlPoints.toArray(new Point[this.controlPoints.size() + 1]);
		points[this.controlPoints.size()] = ps.getCurrentPosition();
		this.currentFigure = this.createFigure(points, points.length, false);
		ps.addRubberbandSelection(this.currentFigure);
	}

	/**
	 * Template Method: Creates a Figure for drawing rubberband entities and the final product
	 *
	 * @param points the array of control points
	 * @param numPoints the number of valid points in the array (n >= 2)
	 * @param closed true if the user double-clicked on the final control point
	 */
	protected abstract Figure createFigure(Point[] points, int numPoints, boolean closed);
}
