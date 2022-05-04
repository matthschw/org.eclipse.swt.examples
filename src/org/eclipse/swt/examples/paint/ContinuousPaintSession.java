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


import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * The superclass for paint tools that draw continuously along the path
 * traced by the mouse's movement while the button is depressed
 */
public abstract class ContinuousPaintSession extends BasicPaintSession {
	/**
	 * True if a click-drag is in progress.
	 */
	private boolean dragInProgress = false;

	/**
	 * A cached Point array for drawing.
	 */
	private final Point[] points = new Point[] { new Point(-1, -1), new Point(-1, -1) };

	/**
	 * The time to wait between retriggers in milliseconds.
	 */
	private int retriggerInterval = 0;

	/**
	 * The currently valid RetriggerHandler
	 */
	protected Runnable retriggerHandler = null;

	/**
	 * Constructs a ContinuousPaintSession.
	 *
	 * @param paintSurface the drawing surface to use
	 */
	protected ContinuousPaintSession(final PaintSurface paintSurface) {
		super(paintSurface);
	}

	/**
	 * Sets the retrigger timer.
	 * <p>
	 * After the timer elapses, if the mouse is still hovering over the same point with the
	 * drag button pressed, a new render order is issued and the timer is restarted.
	 * </p>
	 * @param interval the time in milliseconds to wait between retriggers, 0 to disable
	 */
	public void setRetriggerTimer(final int interval) {
		this.retriggerInterval = interval;
	}

	/**
	 * Activates the tool.
	 */
	@Override
	public void beginSession() {
		this.getPaintSurface().
			setStatusMessage(PaintExample.getResourceString("session.ContinuousPaint.message"));
		this.dragInProgress = false;
	}

	/**
	 * Deactivates the tool.
	 */
	@Override
	public void endSession() {
		this.abortRetrigger();
	}

	/**
	 * Aborts the current operation.
	 */
	@Override
	public void resetSession() {
		this.abortRetrigger();
	}

	/**
	 * Handles a mouseDown event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public final void mouseDown(final MouseEvent event) {
		if ((event.button != 1) || this.dragInProgress)
     {
      return; // spurious event
    }
		this.dragInProgress = true;

		this.points[0].x = event.x;
		this.points[0].y = event.y;
		this.render(this.points[0]);
		this.prepareRetrigger();
	}

	/**
	 * Handles a mouseDoubleClick event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public final void mouseDoubleClick(final MouseEvent event) {
	}

	/**
	 * Handles a mouseUp event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public final void mouseUp(final MouseEvent event) {
		if ((event.button != 1) || ! this.dragInProgress)
     {
      return; // spurious event
    }
		this.abortRetrigger();
		this.mouseSegmentFinished(event);
		this.dragInProgress = false;
	}

	/**
	 * Handles a mouseMove event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public final void mouseMove(final MouseEvent event) {
		final PaintSurface ps = this.getPaintSurface();
		ps.setStatusCoord(ps.getCurrentPosition());
		if (! this.dragInProgress) {
      return;
    }
		this.mouseSegmentFinished(event);
		this.prepareRetrigger();
	}

	/**
	 * Handle a rendering segment
	 *
	 * @param event the mouse event detail information
	 */
	private final void mouseSegmentFinished(final MouseEvent event) {
		if (this.points[0].x == -1)
     {
      return; // spurious event
    }
		if ((this.points[0].x != event.x) || (this.points[0].y != event.y)) {
			// draw new segment
			this.points[1].x = event.x;
			this.points[1].y = event.y;
			this.renderContinuousSegment();
		}
	}

	/**
	 * Draws a continuous segment from points[0] to points[1].
	 * Assumes points[0] has been drawn already.
	 *
	 * @post points[0] will refer to the same point as points[1]
	 */
	protected void renderContinuousSegment() {
		/* A lazy but effective line drawing algorithm */
		final int dX = this.points[1].x - this.points[0].x;
		final int dY = this.points[1].y - this.points[0].y;
		int absdX = Math.abs(dX);
		int absdY = Math.abs(dY);

		if ((dX == 0) && (dY == 0)) {
      return;
    }

		if (absdY > absdX) {
			final int incfpX = (dX << 16) / absdY;
			final int incY = (dY > 0) ? 1 : -1;
			int fpX = this.points[0].x << 16; // X in fixedpoint format

			while (--absdY >= 0) {
				this.points[0].y += incY;
				this.points[0].x = (fpX += incfpX) >> 16;
				this.render(this.points[0]);
			}
			if (this.points[0].x == this.points[1].x) {
        return;
      }
			this.points[0].x = this.points[1].x;
		} else {
			final int incfpY = (dY << 16) / absdX;
			final int incX = (dX > 0) ? 1 : -1;
			int fpY = this.points[0].y << 16; // Y in fixedpoint format

			while (--absdX >= 0) {
				this.points[0].x += incX;
				this.points[0].y = (fpY += incfpY) >> 16;
				this.render(this.points[0]);
			}
			if (this.points[0].y == this.points[1].y) {
        return;
      }
			this.points[0].y = this.points[1].y;
		}
		this.render(this.points[0]);
	}

	/**
	 * Prepare the retrigger timer
	 */
	private final void prepareRetrigger() {
		if (this.retriggerInterval > 0) {
			/*
			 * timerExec() provides a lightweight mechanism for running code at intervals from within
			 * the event loop when timing accuracy is not important.
			 *
			 * Since it is not possible to cancel a timerExec(), we remember the Runnable that is
			 * active in order to distinguish the valid one from the stale ones.  In practice,
			 * if the interval is 1/100th of a second, then creating a few hundred new RetriggerHandlers
			 * each second will not cause a significant performance hit.
			 */
			final Display display = this.getPaintSurface().getDisplay();
			this.retriggerHandler = new Runnable() {
				@Override
				public void run() {
					if (ContinuousPaintSession.this.retriggerHandler == this) {
						ContinuousPaintSession.this.render(ContinuousPaintSession.this.points[0]);
						ContinuousPaintSession.this.prepareRetrigger();
					}
				}
			};
			display.timerExec(this.retriggerInterval, this.retriggerHandler);
		}
	}

	/**
	 * Aborts the retrigger timer
	 */
	private final void abortRetrigger() {
		this.retriggerHandler = null;
	}

	/**
	 * Template method: Renders a point.
	 * @param point, the point to render
	 */
	protected abstract void render(Point point);
}
