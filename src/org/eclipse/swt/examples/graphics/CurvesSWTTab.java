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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;

/**
 * This tab presents cubic and quadratic curves that can be drawn. As a
 * demonstration, cubic and quadratic curves are used to spell out "SWT".
 * The user may reposition the cubic and quadratic handles.
 */
public class CurvesSWTTab extends GraphicsTab {
	/** These rectangles represent the handles on the curves. */
	private final Rectangle sRect1, sRect2, wRect1, wRect2, tTopRect1, tTopRect2,
			tBottomRect1, tBottomRect2;

	/** These values represent the positions of the curves. */
	private float sXPos, sYPos, wXPos, wYPos, topTXPos, topTYPos,
			botTXPos, botTYPos;

	/** These values represent the x and y displacement of each handle. */
	private float sDiffX1, sDiffY1, sDiffX2, sDiffY2;
	private float wDiffX1, wDiffY1, wDiffX2, wDiffY2;
	private float tTopDiffX1, tTopDiffY1, tTopDiffX2, tTopDiffY2;
	private float tBotDiffX1, tBotDiffY1, tBotDiffX2, tBotDiffY2;

	/** These are flags that indicate whether or not a handle has been moved. */
	private boolean sLeftPtMoved, sRightPtMoved, wPt1Moved, wPt2Moved,
			tTopPt1Moved, tTopPt2Moved, tBotPt1Moved, tBotPt2Moved;

	private MouseMoveListener mouseMoveListener;
	private MouseListener mouseListener;
	private Cursor cursor;

	/** true if hovering over a handle, false otherwise */
	private boolean hovering = false;

	/** true if left mouse button is held down, false otherwise */
	private boolean mouseDown = false;


public CurvesSWTTab(final GraphicsExample example) {
	super(example);
	this.sRect1 = new Rectangle(-75, 50, 5, 5);
	this.sRect2 = new Rectangle(75, 100, 5, 5);
	this.sDiffX1 = this.sDiffY1 = 0;
	this.sDiffX2 = this.sDiffY2 = 0;
	this.wRect1 = new Rectangle(80, 300, 5, 5);
	this.wRect2 = new Rectangle(120, 300, 5, 5);
	this.wDiffX1 = this.wDiffY1 = this.wDiffX2 = this.wDiffY2 = 0;
	this.tTopRect1 = new Rectangle(33, -20, 5, 5);
	this.tTopRect2 = new Rectangle(66, 20, 5, 5);
	this.tTopDiffX1 = this.tTopDiffY1 = this.tTopDiffX2 = this.tTopDiffY2 = 0;
	this.tBottomRect1 = new Rectangle(-33, 50, 5, 5);
	this.tBottomRect2 = new Rectangle(33, 100, 5, 5);
	this.tBotDiffX1 = this.tBotDiffY1 = this.tBotDiffX2 = this.tBotDiffY2 = 0;
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Curves"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("SWT"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("CurvesSWTDescription"); //$NON-NLS-1$
}

@Override
public boolean getDoubleBuffered() {
	return true;
}

@Override
public void dispose() {
	if (this.mouseListener != null) {
    this.example.canvas.removeMouseListener(this.mouseListener);
  }

	if (this.mouseMoveListener != null) {
    this.example.canvas.removeMouseMoveListener(this.mouseMoveListener);
  }

	this.cursor = null;
}

	/**
	 * This helper method determines whether or not the cursor is positioned
	 * over a handle.
	 *
	 * @param e
	 *            A MouseEvent
	 * @return true if cursor is positioned over a handle; false otherwise
	 */
private boolean isHovering(final MouseEvent e) {
	final Rectangle r1 = new Rectangle((this.sRect1.x + (int)this.sXPos) - 1, (this.sRect1.y + (int)this.sYPos) - 1, this.sRect1.width+2, this.sRect1.height+2);
	final Rectangle r2 = new Rectangle((this.sRect2.x + (int)this.sXPos) - 1, (this.sRect2.y + (int)this.sYPos) - 1, this.sRect2.width+2, this.sRect2.height+2);
	final Rectangle w1 = new Rectangle((this.wRect1.x + (int)this.wXPos) - 1, (this.wRect1.y + (int)this.wYPos) - 1, this.wRect1.width+2, this.wRect1.height+2);
	final Rectangle w2 = new Rectangle((this.wRect2.x + (int)this.wXPos) - 1, (this.wRect2.y + (int)this.wYPos) - 1, this.wRect2.width+2, this.wRect2.height+2);
	final Rectangle tTop1 = new Rectangle((this.tTopRect1.x + (int)this.topTXPos) - 1, (this.tTopRect1.y + (int)this.topTYPos) - 1, this.tTopRect1.width+2, this.tTopRect1.height+2);
	final Rectangle tTop2 = new Rectangle((this.tTopRect2.x + (int)this.topTXPos) - 1, (this.tTopRect2.y + (int)this.topTYPos) - 1, this.tTopRect2.width+2, this.tTopRect2.height+2);
	final Rectangle tBot1 = new Rectangle((this.tBottomRect1.x + (int)this.botTXPos) - 1, (this.tBottomRect1.y + (int)this.botTYPos) - 1, this.tBottomRect1.width+2, this.tBottomRect1.height+2);
	final Rectangle tBot2 = new Rectangle((this.tBottomRect2.x + (int)this.botTXPos) - 1, (this.tBottomRect2.y + (int)this.botTYPos) - 1, this.tBottomRect2.width+2, this.tBottomRect2.height+2);

	return ( r1.contains(e.x, e.y) || r2.contains(e.x, e.y)
		 || w1.contains(e.x, e.y) || w2.contains(e.x, e.y)
		 || tTop1.contains(e.x, e.y) || tTop2.contains(e.x, e.y)
		 || tBot1.contains(e.x, e.y) || tBot2.contains(e.x, e.y) );
}
/**
 * Creates the widgets used to control the drawing.
 */
@Override
public void createControlPanel(final Composite parent) {
	if (this.cursor == null) {
		this.cursor = parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND);
	}

	this.mouseMoveListener = e -> {
		if (this.hovering && this.mouseDown) {
			this.example.canvas.setCursor(this.cursor);
		} else if (this.isHovering(e)) {
			this.example.canvas.setCursor(this.cursor);
			this.hovering = true;
		} else {
			this.example.canvas.setCursor(null);
			this.hovering = false;
		}

		if (this.sLeftPtMoved) {
			this.sDiffX1 = (this.sDiffX1 + e.x) - (int)this.sXPos - this.sRect1.x;
			this.sDiffY1 = (this.sDiffY1 + e.y) - (int)this.sYPos - this.sRect1.y;
			this.sRect1.x = e.x - (int)this.sXPos;
			this.sRect1.y = e.y - (int)this.sYPos;
		} else if (this.sRightPtMoved) {
			this.sDiffX2 = (this.sDiffX2 + e.x) - (int)this.sXPos - this.sRect2.x;
			this.sDiffY2 = (this.sDiffY2 + e.y) - (int)this.sYPos - this.sRect2.y;
			this.sRect2.x = e.x - (int)this.sXPos;
			this.sRect2.y = e.y - (int)this.sYPos;
		} else if (this.wPt1Moved) {
			this.wDiffX1 = (this.wDiffX1 + e.x) - (int)this.wXPos - this.wRect1.x;
			this.wDiffY1 = (this.wDiffY1 + e.y) - (int)this.wYPos - this.wRect1.y;
			this.wRect1.x = e.x - (int)this.wXPos;
			this.wRect1.y = e.y - (int)this.wYPos;
		} else if (this.wPt2Moved) {
			this.wDiffX2 = (this.wDiffX2 + e.x) - (int)this.wXPos - this.wRect2.x;
			this.wDiffY2 = (this.wDiffY2 + e.y) - (int)this.wYPos - this.wRect2.y;
			this.wRect2.x = e.x - (int)this.wXPos;
			this.wRect2.y = e.y - (int)this.wYPos;
		} else if (this.tTopPt1Moved) {
			this.tTopDiffX1 = (this.tTopDiffX1 + e.x) - (int)this.topTXPos - this.tTopRect1.x;
			this.tTopDiffY1 = (this.tTopDiffY1 + e.y) - (int)this.topTYPos - this.tTopRect1.y;
			this.tTopRect1.x = e.x - (int)this.topTXPos;
			this.tTopRect1.y = e.y - (int)this.topTYPos;
		} else if (this.tTopPt2Moved) {
			this.tTopDiffX2 = (this.tTopDiffX2 + e.x) - (int)this.topTXPos - this.tTopRect2.x;
			this.tTopDiffY2 = (this.tTopDiffY2 + e.y) - (int)this.topTYPos - this.tTopRect2.y;
			this.tTopRect2.x = e.x - (int)this.topTXPos;
			this.tTopRect2.y = e.y - (int)this.topTYPos;
		} else if (this.tBotPt1Moved) {
			this.tBotDiffX1 = (this.tBotDiffX1 + e.x) - (int)this.botTXPos - this.tBottomRect1.x;
			this.tBotDiffY1 = (this.tBotDiffY1 + e.y) - (int)this.botTYPos - this.tBottomRect1.y;
			this.tBottomRect1.x = e.x - (int)this.botTXPos;
			this.tBottomRect1.y = e.y - (int)this.botTYPos;
		} else if (this.tBotPt2Moved) {
			this.tBotDiffX2 = (this.tBotDiffX2 + e.x) - (int)this.botTXPos - this.tBottomRect2.x;
			this.tBotDiffY2 = (this.tBotDiffY2 + e.y) - (int)this.botTYPos - this.tBottomRect2.y;
			this.tBottomRect2.x = e.x - (int)this.botTXPos;
			this.tBottomRect2.y = e.y - (int)this.botTYPos;
		}
		this.example.redraw();
	};

	this.mouseListener = new MouseListener() {

		@Override
		public void mouseDoubleClick(final MouseEvent e) {}

		/**
		 * Sent when a mouse button is pressed.
		 *
		 * @param e an event containing information about the mouse button press
		 */
		@Override
		public void mouseDown(final MouseEvent e) {
			final Rectangle r1 = new Rectangle((CurvesSWTTab.this.sRect1.x + (int)CurvesSWTTab.this.sXPos) - 1, (CurvesSWTTab.this.sRect1.y + (int)CurvesSWTTab.this.sYPos) - 1, CurvesSWTTab.this.sRect1.width+2, CurvesSWTTab.this.sRect1.height+2);
			final Rectangle r2 = new Rectangle((CurvesSWTTab.this.sRect2.x + (int)CurvesSWTTab.this.sXPos) - 1, (CurvesSWTTab.this.sRect2.y + (int)CurvesSWTTab.this.sYPos) - 1, CurvesSWTTab.this.sRect2.width+2, CurvesSWTTab.this.sRect2.height+2);
			final Rectangle w1 = new Rectangle((CurvesSWTTab.this.wRect1.x + (int)CurvesSWTTab.this.wXPos) - 1, (CurvesSWTTab.this.wRect1.y + (int)CurvesSWTTab.this.wYPos) - 1, CurvesSWTTab.this.wRect1.width+2, CurvesSWTTab.this.wRect1.height+2);
			final Rectangle w2 = new Rectangle((CurvesSWTTab.this.wRect2.x + (int)CurvesSWTTab.this.wXPos) - 1, (CurvesSWTTab.this.wRect2.y + (int)CurvesSWTTab.this.wYPos) - 1, CurvesSWTTab.this.wRect2.width+2, CurvesSWTTab.this.wRect2.height+2);
			final Rectangle tTop1 = new Rectangle((CurvesSWTTab.this.tTopRect1.x + (int)CurvesSWTTab.this.topTXPos) - 1, (CurvesSWTTab.this.tTopRect1.y + (int)CurvesSWTTab.this.topTYPos) - 1, CurvesSWTTab.this.tTopRect1.width+2, CurvesSWTTab.this.tTopRect1.height+2);
			final Rectangle tTop2 = new Rectangle((CurvesSWTTab.this.tTopRect2.x + (int)CurvesSWTTab.this.topTXPos) - 1, (CurvesSWTTab.this.tTopRect2.y + (int)CurvesSWTTab.this.topTYPos) - 1, CurvesSWTTab.this.tTopRect2.width+2, CurvesSWTTab.this.tTopRect2.height+2);
			final Rectangle tBot1 = new Rectangle((CurvesSWTTab.this.tBottomRect1.x + (int)CurvesSWTTab.this.botTXPos) - 1, (CurvesSWTTab.this.tBottomRect1.y + (int)CurvesSWTTab.this.botTYPos) - 1, CurvesSWTTab.this.tBottomRect1.width+2, CurvesSWTTab.this.tBottomRect1.height+2);
			final Rectangle tBot2 = new Rectangle((CurvesSWTTab.this.tBottomRect2.x + (int)CurvesSWTTab.this.botTXPos) - 1, (CurvesSWTTab.this.tBottomRect2.y + (int)CurvesSWTTab.this.botTYPos) - 1, CurvesSWTTab.this.tBottomRect2.width+2, CurvesSWTTab.this.tBottomRect2.height+2);

			if (r1.contains(e.x, e.y)) {
				CurvesSWTTab.this.sLeftPtMoved = true;
				CurvesSWTTab.this.mouseDown = true;
			} else if (r2.contains(e.x, e.y)) {
				CurvesSWTTab.this.sRightPtMoved = true;
				CurvesSWTTab.this.mouseDown = true;
			} else if (w1.contains(e.x, e.y)) {
				CurvesSWTTab.this.wPt1Moved = true;
				CurvesSWTTab.this.mouseDown = true;
			} else if (w2.contains(e.x, e.y)) {
				CurvesSWTTab.this.wPt2Moved = true;
				CurvesSWTTab.this.mouseDown = true;
			} else if (tTop1.contains(e.x, e.y)) {
				CurvesSWTTab.this.tTopPt1Moved = true;
				CurvesSWTTab.this.mouseDown = true;
			} else if (tTop2.contains(e.x, e.y)) {
				CurvesSWTTab.this.tTopPt2Moved = true;
				CurvesSWTTab.this.mouseDown = true;
			} else if (tBot1.contains(e.x, e.y)) {
				CurvesSWTTab.this.tBotPt1Moved = true;
				CurvesSWTTab.this.mouseDown = true;
			} else if (tBot2.contains(e.x, e.y)) {
				CurvesSWTTab.this.tBotPt2Moved = true;
				CurvesSWTTab.this.mouseDown = true;
			}
		}

		/**
		 * Sent when a mouse button is released.
		 *
		 * @param e an event containing information about the mouse button release
		 */
		@Override
		public void mouseUp(final MouseEvent e) {
			CurvesSWTTab.this.mouseDown = false;
			if (CurvesSWTTab.this.isHovering(e)) {
				CurvesSWTTab.this.example.canvas.setCursor(CurvesSWTTab.this.cursor);
			} else {
				CurvesSWTTab.this.example.canvas.setCursor(null);
			}
			if (CurvesSWTTab.this.sLeftPtMoved) {
        CurvesSWTTab.this.sLeftPtMoved = false;
      }
			if (CurvesSWTTab.this.sRightPtMoved) {
        CurvesSWTTab.this.sRightPtMoved = false;
      }
			if (CurvesSWTTab.this.wPt1Moved) {
        CurvesSWTTab.this.wPt1Moved = false;
      }
			if (CurvesSWTTab.this.wPt2Moved) {
        CurvesSWTTab.this.wPt2Moved = false;
      }
			if (CurvesSWTTab.this.tTopPt1Moved) {
        CurvesSWTTab.this.tTopPt1Moved = false;
      }
			if (CurvesSWTTab.this.tTopPt2Moved) {
        CurvesSWTTab.this.tTopPt2Moved = false;
      }
			if (CurvesSWTTab.this.tBotPt1Moved) {
        CurvesSWTTab.this.tBotPt1Moved = false;
      }
			if (CurvesSWTTab.this.tBotPt2Moved) {
        CurvesSWTTab.this.tBotPt2Moved = false;
      }

			CurvesSWTTab.this.example.redraw();
		}
	};
	this.example.canvas.addMouseMoveListener(this.mouseMoveListener);
	this.example.canvas.addMouseListener(this.mouseListener);
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	final Font font = new Font(device, getPlatformFont(), 16, SWT.ITALIC);
	gc.setFont(font);
	gc.setLineWidth(2);

	Transform transform;

	// ----- letter s -----
	this.sXPos = (4*width)/16;
	this.sYPos = (height-150)/2;

	transform = new Transform(device);
	transform.translate(this.sXPos, this.sYPos);
	gc.setTransform(transform);
	transform.dispose();

	gc.setForeground(device.getSystemColor(SWT.COLOR_DARK_BLUE));
	gc.drawString(GraphicsExample.getResourceString("Cubic"), 0, 175, true);

	Path path = new Path(device);
	path.cubicTo(-200 + this.sDiffX1, 50 + this.sDiffY1, 200 + this.sDiffX2, 100 + this.sDiffY2, 0, 150);
	gc.drawPath(path);
	path.dispose();

	// draw the spline points
	gc.setTransform(null);
	gc.drawRectangle(this.sRect1.x + (int)this.sXPos, this.sRect1.y + (int)this.sYPos, this.sRect1.width, this.sRect1.height);
	gc.drawRectangle(this.sRect2.x + (int)this.sXPos, this.sRect2.y + (int)this.sYPos, this.sRect2.width, this.sRect2.height);

	// ----- letter w -----
	this.wXPos = (6*width)/16;
	this.wYPos = (height-150)/2;

	transform = new Transform(device);
	transform.translate(this.wXPos, this.wYPos);
	gc.setTransform(transform);
	transform.dispose();

	gc.setForeground(device.getSystemColor(SWT.COLOR_GRAY));
	gc.drawString(GraphicsExample.getResourceString("Quadratic"), 0, -50, true);
	gc.drawString(GraphicsExample.getResourceString("Quadratic"), 110, -50, true);

	path = new Path(device);
	path.quadTo(100 + this.wDiffX1, 300 + this.wDiffY1, 100, 0);
	path.quadTo(100+this.wDiffX2, 300+this.wDiffY2, 200, 0);
	gc.drawPath(path);
	path.dispose();

	gc.setTransform(null);
	gc.drawRectangle(this.wRect1.x + (int)this.wXPos, this.wRect1.y + (int)this.wYPos, this.wRect1.width, this.wRect1.height);
	gc.drawRectangle(this.wRect2.x + (int)this.wXPos, this.wRect2.y + (int)this.wYPos, this.wRect2.width, this.wRect2.height);


	// ----- top of letter t -----
	this.topTXPos = (11*width)/16;
	this.topTYPos = (height-150)/2;

	transform = new Transform(device);
	transform.translate(this.topTXPos, this.topTYPos);
	gc.setTransform(transform);
	transform.dispose();

	gc.setForeground(device.getSystemColor(SWT.COLOR_YELLOW));
	gc.drawString(GraphicsExample.getResourceString("Cubic"), 25, -50, true);

	path = new Path(device);
	path.cubicTo(33 + this.tTopDiffX1, -20 + this.tTopDiffY1, 66 + this.tTopDiffX2, 20 + this.tTopDiffY2, 100, 0);
	gc.drawPath(path);
	path.dispose();

	gc.setTransform(null);
	gc.drawRectangle(this.tTopRect1.x + (int)this.topTXPos, this.tTopRect1.y + (int)this.topTYPos, this.tTopRect1.width, this.tTopRect1.height);
	gc.drawRectangle(this.tTopRect2.x + (int)this.topTXPos, this.tTopRect2.y + (int)this.topTYPos, this.tTopRect2.width, this.tTopRect2.height);


	// ----- vertical bar of letter t -----
	this.botTXPos = (12*width)/16;
	this.botTYPos = (height-150)/2;

	transform = new Transform(device);
	transform.translate(this.botTXPos, this.botTYPos);
	gc.setTransform(transform);
	transform.dispose();

	gc.setForeground(device.getSystemColor(SWT.COLOR_RED));
	gc.drawString(GraphicsExample.getResourceString("Cubic"), 0, 175, true);

	path = new Path(device);
	path.cubicTo(-33 + this.tBotDiffX1, 50 + this.tBotDiffY1, 33 + this.tBotDiffX2, 100 + this.tBotDiffY2, 0, 150);
	gc.drawPath(path);
	path.dispose();

	gc.setTransform(null);
	gc.drawRectangle(this.tBottomRect1.x + (int)this.botTXPos, this.tBottomRect1.y + (int)this.botTYPos, this.tBottomRect1.width, this.tBottomRect1.height);
	gc.drawRectangle(this.tBottomRect2.x + (int)this.botTXPos, this.tBottomRect2.y + (int)this.botTYPos, this.tBottomRect2.width, this.tBottomRect2.height);

	font.dispose();
}

/**
 * Returns the name of a valid font for the resident platform.
 */
static String getPlatformFont() {
	if(SWT.getPlatform() == "win32") {
		return "Arial";
	} else if (SWT.getPlatform() == "gtk") {
		return "Baekmuk Batang";
	} else {
		return "Verdana";
	}
}
}
