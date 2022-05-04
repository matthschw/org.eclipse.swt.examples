/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
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

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Manages a simple drawing surface.
 */
public class PaintSurface {
	private Point currentPosition = new Point(0, 0);
	private Canvas paintCanvas;

	private PaintSession paintSession;
	private Image image;
	private Image paintImage; // buffer for refresh blits
	private final int   imageWidth, imageHeight;
	private int   visibleWidth, visibleHeight;

	private FigureDrawContext displayFDC = new FigureDrawContext();
	private FigureDrawContext imageFDC = new FigureDrawContext();
	private FigureDrawContext paintFDC = new FigureDrawContext();

	/* Rubberband */
	private ContainerFigure rubberband = new ContainerFigure();
		// the active rubberband selection
	private int rubberbandHiddenNestingCount = 0;
		// always >= 0, if > 0 rubberband has been hidden

	/* Status */
	private Text statusText;
	private String statusActionInfo, statusMessageInfo, statusCoordInfo;

	/**
	 * Constructs a PaintSurface.
	 * <p>
	 * paintCanvas must have SWT.NO_REDRAW_RESIZE and SWT.NO_BACKGROUND styles,
	 *     and may have SWT.V_SCROLL and/or SWT.H_SCROLL.
	 * </p>
	 * @param paintCanvas the Canvas object in which to render
	 * @param paintStatus the PaintStatus object to use for providing user feedback
	 * @param fillColor the color to fill the canvas with initially
	 */
	public PaintSurface(final Canvas paintCanvas, final Text statusText, final Color fillColor) {
		this.paintCanvas = paintCanvas;
		this.statusText = statusText;
		this.clearStatus();

		/* Set up the drawing surface */
		final Rectangle displayRect = paintCanvas.getDisplay().getClientArea();
		this.imageWidth = displayRect.width;
		this.imageHeight = displayRect.height;
		this.image = new Image(paintCanvas.getDisplay(), this.imageWidth, this.imageHeight);

		this.imageFDC.gc = new GC(this.image);
		this.imageFDC.gc.setBackground(fillColor);
		this.imageFDC.gc.fillRectangle(0, 0, this.imageWidth, this.imageHeight);
		this.displayFDC.gc = new GC(paintCanvas);

		/* Initialize the session */
		this.setPaintSession(null);

		/* Add our listeners */
		paintCanvas.addDisposeListener(e -> this.displayFDC.gc.dispose());
		paintCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent event) {
				PaintSurface.this.processMouseEventCoordinates(event);
				if (PaintSurface.this.paintSession != null) {
          PaintSurface.this.paintSession.mouseDown(event);
        }
			}
			@Override
			public void mouseUp(final MouseEvent event) {
				PaintSurface.this.processMouseEventCoordinates(event);
				if (PaintSurface.this.paintSession != null) {
          PaintSurface.this.paintSession.mouseUp(event);
        }
			}
			@Override
			public void mouseDoubleClick(final MouseEvent event) {
				PaintSurface.this.processMouseEventCoordinates(event);
				if (PaintSurface.this.paintSession != null) {
          PaintSurface.this.paintSession.mouseDoubleClick(event);
        }
			}
		});
		paintCanvas.addMouseMoveListener(event -> {
			this.processMouseEventCoordinates(event);
			if (this.paintSession != null) {
        this.paintSession.mouseMove(event);
      }
		});
		paintCanvas.addPaintListener(event -> {
			if (this.rubberband.isEmpty()) {
				// Nothing to merge, so we just refresh
				event.gc.drawImage(this.image,
					this.displayFDC.xOffset + event.x, this.displayFDC.yOffset + event.y, event.width, event.height,
					event.x, event.y, event.width, event.height);
			} else {
				/*
				 * Avoid flicker when merging overlayed objects by constructing the image on
				 * a backbuffer first, then blitting it to the screen.
				 */
				// Check that the backbuffer is large enough
				if (this.paintImage != null) {
					final Rectangle rect1 = this.paintImage.getBounds();
					if (((event.width + event.x) > rect1.width) ||
						((event.height + event.y) > rect1.height)) {
						this.paintFDC.gc.dispose();
						this.paintImage.dispose();
						this.paintImage = null;
					}
				}
				if (this.paintImage == null) {
					final Display display = this.getDisplay();
					final Rectangle rect2 = display.getClientArea();
					this.paintImage = new Image(display,
						Math.max(rect2.width, event.width + event.x),
						Math.max(rect2.height, event.height + event.y));
					this.paintFDC.gc = new GC(this.paintImage);
				}
				// Setup clipping and the FDC
				final Region clipRegion = new Region();
				event.gc.getClipping(clipRegion);
				this.paintFDC.gc.setClipping(clipRegion);
				clipRegion.dispose();

				this.paintFDC.xOffset = this.displayFDC.xOffset;
				this.paintFDC.yOffset = this.displayFDC.yOffset;
				this.paintFDC.xScale = this.displayFDC.xScale;
				this.paintFDC.yScale = this.displayFDC.yScale;

				// Merge the overlayed objects into the image, then blit
				this.paintFDC.gc.drawImage(this.image,
					this.displayFDC.xOffset + event.x, this.displayFDC.yOffset + event.y, event.width, event.height,
					event.x, event.y, event.width, event.height);
				this.rubberband.draw(this.paintFDC);
				event.gc.drawImage(this.paintImage,
					event.x, event.y, event.width, event.height,
					event.x, event.y, event.width, event.height);
			}
		});
		paintCanvas.addControlListener(ControlListener.controlResizedAdapter(e -> this.handleResize()));

		/* Set up the paint canvas scroll bars */
		final ScrollBar horizontal = paintCanvas.getHorizontalBar();
		horizontal.setVisible(true);
		horizontal.addSelectionListener(widgetSelectedAdapter(event -> this.scrollHorizontally((ScrollBar)event.widget)));
		final ScrollBar vertical = paintCanvas.getVerticalBar();
		vertical.setVisible(true);
		vertical.addSelectionListener(widgetSelectedAdapter(event -> this.scrollVertically((ScrollBar)event.widget)));
		this.handleResize();
	}

	/**
	 * Disposes of the PaintSurface's resources.
	 */
	public void dispose() {
		this.imageFDC.gc.dispose();
		this.image.dispose();
		if (this.paintImage != null) {
			this.paintImage.dispose();
			this.paintFDC.gc.dispose();
		}

		this.currentPosition = null;
		this.paintCanvas = null;
		this.paintSession = null;
		this.image = null;
		this.paintImage = null;
		this.displayFDC = null;
		this.imageFDC = null;
		this.paintFDC = null;
		this.rubberband = null;
		this.statusText = null;
		this.statusActionInfo = null;
		this.statusMessageInfo = null;
		this.statusCoordInfo = null;
	}

	/**
	 * Called when we must grab focus.
	 */
	public void setFocus()  {
		this.paintCanvas.setFocus();
	}

	/**
	 * Returns the Display on which the PaintSurface resides.
	 * @return the Display
	 */
	public Display getDisplay() {
		return this.paintCanvas.getDisplay();
	}

	/**
	 * Returns the Shell in which the PaintSurface resides.
	 * @return the Shell
	 */
	public Shell getShell() {
		return this.paintCanvas.getShell();
	}

	/**
	 * Sets the current paint session.
	 * <p>
	 * If oldPaintSession != paintSession calls oldPaintSession.end()
	 * and paintSession.begin()
	 * </p>
	 *
	 * @param paintSession the paint session to activate; null to disable all sessions
	 */
	public void setPaintSession(final PaintSession paintSession) {
		if (this.paintSession != null) {
			if (this.paintSession == paintSession) {
        return;
      }
			this.paintSession.endSession();
		}
		this.paintSession = paintSession;
		this.clearStatus();
		if (paintSession != null) {
			this.setStatusAction(paintSession.getDisplayName());
			paintSession.beginSession();
		} else {
			this.setStatusAction(PaintExample.getResourceString("tool.Null.label"));
			this.setStatusMessage(PaintExample.getResourceString("session.Null.message"));
		}
	}

	/**
	 * Returns the current paint session.
	 *
	 * @return the current paint session, null if none is active
	 */
	public PaintSession getPaintSession() {
		return this.paintSession;
	}

	/**
	 * Returns the current paint tool.
	 *
	 * @return the current paint tool, null if none is active (though some other session
	 *         might be)
	 */
	public PaintTool getPaintTool() {
		return ((this.paintSession != null) && (this.paintSession instanceof PaintTool)) ?
			(PaintTool)this.paintSession : null;
	}

	/**
	 * Returns the current position in an interactive operation.
	 *
	 * @return the last known position of the pointer
	 */
	public Point getCurrentPosition() {
		return this.currentPosition;
	}

	/**
	 * Draws a Figure object to the screen and to the backing store permanently.
	 *
	 * @param object the object to draw onscreen
	 */
	public void drawFigure(final Figure object) {
		object.draw(this.imageFDC);
		object.draw(this.displayFDC);
	}

	/**
	 * Adds a Figure object to the active rubberband selection.
	 * <p>
	 * This object will be drawn to the screen as a preview and refreshed appropriately
	 * until the selection is either cleared or committed.
	 * </p>
	 *
	 * @param object the object to add to the selection
	 */
	public void addRubberbandSelection(final Figure object) {
		this.rubberband.add(object);
		if (! this.isRubberbandHidden()) {
      object.draw(this.displayFDC);
    }
	}

	/**
	 * Clears the active rubberband selection.
	 * <p>
	 * Erases any rubberband objects on the screen then clears the selection.
	 * </p>
	 */
	public void clearRubberbandSelection() {
		if (! this.isRubberbandHidden()) {
			final Region region = new Region();
			this.rubberband.addDamagedRegion(this.displayFDC, region);
			final Rectangle r = region.getBounds();
			this.paintCanvas.redraw(r.x, r.y, r.width, r.height, true);
			region.dispose();
		}
		this.rubberband.clear();

	}

	/**
	 * Commits the active rubberband selection.
	 * <p>
	 * Redraws any rubberband objects on the screen as permanent objects then clears the selection.
	 * </p>
	 */
	public void commitRubberbandSelection() {
		this.rubberband.draw(this.imageFDC);
		if (this.isRubberbandHidden()) {
      this.rubberband.draw(this.displayFDC);
    }
		this.rubberband.clear();
	}

	/**
	 * Hides the rubberband (but does not eliminate it).
	 * <p>
	 * Increments by one the rubberband "hide" nesting count.  The rubberband
	 * is hidden from view (but remains active) if it wasn't already hidden.
	 * </p>
	 */
	public void hideRubberband() {
		if (this.rubberbandHiddenNestingCount++ <= 0) {
			final Region region = new Region();
			this.rubberband.addDamagedRegion(this.displayFDC, region);
			final Rectangle r = region.getBounds();
			this.paintCanvas.redraw(r.x, r.y, r.width, r.height, true);
			region.dispose();
		}
	}

	/**
	 * Shows (un-hides) the rubberband.
	 * <p>
	 * Decrements by one the rubberband "hide" nesting count.  The rubberband
	 * is only made visible when showRubberband() has been called once for each
	 * previous hideRubberband().  It is not permitted to call showRubberband() if
	 * the rubber band is not presently hidden.
	 * </p>
	 */
	public void showRubberband() {
		if (this.rubberbandHiddenNestingCount <= 0) {
      throw new IllegalStateException("rubberbandHiddenNestingCount > 0");
    }
		if (--this.rubberbandHiddenNestingCount == 0) {
			this.rubberband.draw(this.displayFDC);
		}
	}

	/**
	 * Determines if the rubberband is hidden.
	 *
	 * @return true iff the rubber is hidden
	 */
	public boolean isRubberbandHidden() {
		return this.rubberbandHiddenNestingCount > 0;
	}

	/**
	 * Handles a horizontal scroll event
	 *
	 * @param scrollbar the horizontal scroll bar that posted this event
	 */
	public void scrollHorizontally(final ScrollBar scrollBar) {
		if (this.image == null) {
      return;
    }
		if (this.imageWidth > this.visibleWidth) {
			final int oldOffset = this.displayFDC.xOffset;
			final int newOffset = Math.min(scrollBar.getSelection(), this.imageWidth - this.visibleWidth);
			if (oldOffset != newOffset) {
				this.paintCanvas.update();
				this.displayFDC.xOffset = newOffset;
				this.paintCanvas.scroll(Math.max(oldOffset - newOffset, 0), 0, Math.max(newOffset - oldOffset, 0), 0,
					this.visibleWidth, this.visibleHeight, false);
			}
		}
	}

	/**
	 * Handles a vertical scroll event
	 *
	 * @param scrollbar the vertical scroll bar that posted this event
	 */
	public void scrollVertically(final ScrollBar scrollBar) {
		if (this.image == null) {
      return;
    }
		if (this.imageHeight > this.visibleHeight) {
			final int oldOffset = this.displayFDC.yOffset;
			final int newOffset = Math.min(scrollBar.getSelection(), this.imageHeight - this.visibleHeight);
			if (oldOffset != newOffset) {
				this.paintCanvas.update();
				this.displayFDC.yOffset = newOffset;
				this.paintCanvas.scroll(0, Math.max(oldOffset - newOffset, 0), 0, Math.max(newOffset - oldOffset, 0),
					this.visibleWidth, this.visibleHeight, false);
			}
		}
	}

	/**
	 * Handles resize events
	 */
	private void handleResize() {
		this.paintCanvas.update();

		final Rectangle visibleRect = this.paintCanvas.getClientArea();
		this.visibleWidth = visibleRect.width;
		this.visibleHeight = visibleRect.height;

		final ScrollBar horizontal = this.paintCanvas.getHorizontalBar();
		if (horizontal != null) {
			this.displayFDC.xOffset = Math.min(horizontal.getSelection(), this.imageWidth - this.visibleWidth);
			if (this.imageWidth <= this.visibleWidth) {
				horizontal.setEnabled(false);
				horizontal.setSelection(0);
			} else {
				horizontal.setEnabled(true);
				horizontal.setValues(this.displayFDC.xOffset, 0, this.imageWidth, this.visibleWidth,
					8, this.visibleWidth);
			}
		}

		final ScrollBar vertical = this.paintCanvas.getVerticalBar();
		if (vertical != null) {
			this.displayFDC.yOffset = Math.min(vertical.getSelection(), this.imageHeight - this.visibleHeight);
			if (this.imageHeight <= this.visibleHeight) {
				vertical.setEnabled(false);
				vertical.setSelection(0);
			} else {
				vertical.setEnabled(true);
				vertical.setValues(this.displayFDC.yOffset, 0, this.imageHeight, this.visibleHeight,
					8, this.visibleHeight);
			}
		}
	}

	/**
	 * Virtualizes MouseEvent coordinates and stores the current position.
	 */
	private void processMouseEventCoordinates(final MouseEvent event) {
		this.currentPosition.x = event.x =
			Math.min(Math.max(event.x, 0), this.visibleWidth - 1) + this.displayFDC.xOffset;
		this.currentPosition.y = event.y =
			Math.min(Math.max(event.y, 0), this.visibleHeight - 1) + this.displayFDC.yOffset;
	}

	/**
	 * Clears the status bar.
	 */
	public void clearStatus() {
		this.statusActionInfo = "";
		this.statusMessageInfo = "";
		this.statusCoordInfo = "";
		this.updateStatus();
	}

	/**
	 * Sets the status bar action text.
	 *
	 * @param action the action in progress, null to clear
	 */
	public void setStatusAction(final String action) {
		this.statusActionInfo = (action != null) ? action : "";
		this.updateStatus();
	}

	/**
	 * Sets the status bar message text.
	 *
	 * @param message the message to display, null to clear
	 */
	public void setStatusMessage(final String message) {
		this.statusMessageInfo = (message != null) ? message : "";
		this.updateStatus();
	}

	/**
	 * Sets the coordinates in the status bar.
	 *
	 * @param coord the coordinates to display, null to clear
	 */
	public void setStatusCoord(final Point coord) {
		this.statusCoordInfo = (coord != null) ? PaintExample.getResourceString("status.Coord.format", new Object[]
			{ Integer.valueOf(coord.x), Integer.valueOf(coord.y)}) : "";
		this.updateStatus();
	}

	/**
	 * Sets the coordinate range in the status bar.
	 *
	 * @param a the "from" coordinate, must not be null
	 * @param b the "to" coordinate, must not be null
	 */
	public void setStatusCoordRange(final Point a, final Point b) {
		this.statusCoordInfo = PaintExample.getResourceString("status.CoordRange.format", new Object[]
			{ Integer.valueOf(a.x), Integer.valueOf(a.y), Integer.valueOf(b.x), Integer.valueOf(b.y)});
		this.updateStatus();
	}

	/**
	 * Updates the display.
	 */
	private void updateStatus() {
		this.statusText.setText(
			PaintExample.getResourceString("status.Bar.format", new Object[]
			{ this.statusActionInfo, this.statusMessageInfo, this.statusCoordInfo }));
	}
}
