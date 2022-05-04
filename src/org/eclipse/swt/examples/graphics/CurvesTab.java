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
 * This tab presents cubic and quadratic curves that can be drawn.
 * The user may reposition the cubic and quadratic handles.
 */
public class CurvesTab extends GraphicsTab {
	/** These rectangles represent the handles on the curves. */
	private final Rectangle quadHndl, quadEndHndl, cubHndl1, cubHndl2, cubEndHndl;

	/** These values represent the positions of the curves. */
	private float quadXPos, quadYPos, cubXPos, cubYPos;

	/** These values represent the x and y displacement of each handle. */
	private float quadDiffX, quadDiffY, quadEndDiffX, quadEndDiffY;
	private float cubDiffX1, cubDiffY1, cubDiffX2, cubDiffY2, cubEndDiffX, cubEndDiffY;

	/** These are flags that indicate whether or not a handle has been moved. */
	private boolean quadPtMoved, quadEndPtMoved, cubPt1Moved, cubPt2Moved, cubEndPtMoved;

	private MouseMoveListener mouseMoveListener;
	private MouseListener mouseListener;
	private Cursor cursor;

	/** true if hovering over a handle, false otherwise */
	private boolean hovering = false;

	/** true if left mouse button is held down, false otherwise */
	private boolean mouseDown = false;


public CurvesTab(final GraphicsExample example) {
	super(example);
	this.quadHndl = new Rectangle(200, 150, 5, 5);
	this.quadEndHndl = new Rectangle(400, 0, 5, 5);
	this.quadDiffX = this.quadDiffY = this.quadEndDiffX = this.quadEndDiffY = 0;
	this.cubHndl1 = new Rectangle(133, -60, 5, 5);
	this.cubHndl2 = new Rectangle(266, 60, 5, 5);
	this.cubDiffX1 = this.cubDiffY1 = this.cubDiffX2 = this.cubDiffY2 = 0;
	this.cubEndHndl = new Rectangle(400, 0, 5, 5);
	this.cubEndDiffX = this.cubEndDiffY = 0;
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Curves"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("Curves"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("CurvesDescription"); //$NON-NLS-1$
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
	final Rectangle quad = new Rectangle((this.quadHndl.x + (int)this.quadXPos) - 1, (this.quadHndl.y + (int)this.quadYPos) - 1, this.quadHndl.width+2, this.quadHndl.height+2);
	final Rectangle quadEnd = new Rectangle((this.quadEndHndl.x + (int)this.quadXPos) - 1, (this.quadEndHndl.y + (int)this.quadYPos) - 1, this.quadEndHndl.width+2, this.quadEndHndl.height+2);
	final Rectangle cub1 = new Rectangle((this.cubHndl1.x + (int)this.cubXPos) - 1, (this.cubHndl1.y + (int)this.cubYPos) - 1, this.cubHndl1.width+2, this.cubHndl1.height+2);
	final Rectangle cub2 = new Rectangle((this.cubHndl2.x + (int)this.cubXPos) - 1, (this.cubHndl2.y + (int)this.cubYPos) - 1, this.cubHndl2.width+2, this.cubHndl2.height+2);
	final Rectangle cubEnd = new Rectangle((this.cubEndHndl.x + (int)this.cubXPos) - 1, (this.cubEndHndl.y + (int)this.cubYPos) - 1, this.cubEndHndl.width+2, this.cubEndHndl.height+2);

	return ( quad.contains(e.x, e.y) || quadEnd.contains(e.x, e.y)
		 || cub1.contains(e.x, e.y) || cub2.contains(e.x, e.y)
		 || cubEnd.contains(e.x, e.y));
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

		if (this.quadPtMoved) {
			this.quadDiffX = (this.quadDiffX + e.x) - (int)this.quadXPos - this.quadHndl.x;
			this.quadDiffY = (this.quadDiffY + e.y) - (int)this.quadYPos - this.quadHndl.y;
			this.quadHndl.x = e.x - (int)this.quadXPos;
			this.quadHndl.y = e.y - (int)this.quadYPos;
		} else if (this.quadEndPtMoved) {
			this.quadEndDiffX = (this.quadEndDiffX + e.x) - (int)this.quadXPos - this.quadEndHndl.x;
			this.quadEndDiffY = (this.quadEndDiffY + e.y) - (int)this.quadYPos - this.quadEndHndl.y;
			this.quadEndHndl.x = e.x - (int)this.quadXPos;
			this.quadEndHndl.y = e.y - (int)this.quadYPos;
		} else if (this.cubPt1Moved) {
			this.cubDiffX1 = (this.cubDiffX1 + e.x) - (int)this.cubXPos - this.cubHndl1.x;
			this.cubDiffY1 = (this.cubDiffY1 + e.y) - (int)this.cubYPos - this.cubHndl1.y;
			this.cubHndl1.x = e.x - (int)this.cubXPos;
			this.cubHndl1.y = e.y - (int)this.cubYPos;
		} else if (this.cubPt2Moved) {
			this.cubDiffX2 = (this.cubDiffX2 + e.x) - (int)this.cubXPos - this.cubHndl2.x;
			this.cubDiffY2 = (this.cubDiffY2 + e.y) - (int)this.cubYPos - this.cubHndl2.y;
			this.cubHndl2.x = e.x - (int)this.cubXPos;
			this.cubHndl2.y = e.y - (int)this.cubYPos;
		} else if (this.cubEndPtMoved) {
			this.cubEndDiffX = (this.cubEndDiffX + e.x) - (int)this.cubXPos - this.cubEndHndl.x;
			this.cubEndDiffY = (this.cubEndDiffY + e.y) - (int)this.cubYPos - this.cubEndHndl.y;
			this.cubEndHndl.x = e.x - (int)this.cubXPos;
			this.cubEndHndl.y = e.y - (int)this.cubYPos;
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
			final Rectangle quad = new Rectangle((CurvesTab.this.quadHndl.x + (int)CurvesTab.this.quadXPos) - 1, (CurvesTab.this.quadHndl.y + (int)CurvesTab.this.quadYPos) - 1, CurvesTab.this.quadHndl.width+2, CurvesTab.this.quadHndl.height+2);
			final Rectangle quadEnd = new Rectangle((CurvesTab.this.quadEndHndl.x + (int)CurvesTab.this.quadXPos) - 1, (CurvesTab.this.quadEndHndl.y + (int)CurvesTab.this.quadYPos) - 1, CurvesTab.this.quadEndHndl.width+2, CurvesTab.this.quadEndHndl.height+2);
			final Rectangle cub1 = new Rectangle((CurvesTab.this.cubHndl1.x + (int)CurvesTab.this.cubXPos) - 1, (CurvesTab.this.cubHndl1.y + (int)CurvesTab.this.cubYPos) - 1, CurvesTab.this.cubHndl1.width+2, CurvesTab.this.cubHndl1.height+2);
			final Rectangle cub2 = new Rectangle((CurvesTab.this.cubHndl2.x + (int)CurvesTab.this.cubXPos) - 1, (CurvesTab.this.cubHndl2.y + (int)CurvesTab.this.cubYPos) - 1, CurvesTab.this.cubHndl2.width+2, CurvesTab.this.cubHndl2.height+2);
			final Rectangle cubEnd = new Rectangle((CurvesTab.this.cubEndHndl.x + (int)CurvesTab.this.cubXPos) - 1, (CurvesTab.this.cubEndHndl.y + (int)CurvesTab.this.cubYPos) - 1, CurvesTab.this.cubEndHndl.width+2, CurvesTab.this.cubEndHndl.height+2);

			if (quad.contains(e.x, e.y)) {
				CurvesTab.this.quadPtMoved = true;
				CurvesTab.this.mouseDown = true;
			} else if (quadEnd.contains(e.x, e.y)) {
				CurvesTab.this.quadEndPtMoved = true;
				CurvesTab.this.mouseDown = true;
			} else if (cub1.contains(e.x, e.y)) {
				CurvesTab.this.cubPt1Moved = true;
				CurvesTab.this.mouseDown = true;
			} else if (cub2.contains(e.x, e.y)) {
				CurvesTab.this.cubPt2Moved = true;
				CurvesTab.this.mouseDown = true;
			} else if (cubEnd.contains(e.x, e.y)) {
				CurvesTab.this.cubEndPtMoved = true;
				CurvesTab.this.mouseDown = true;
			}
		}

		/**
		 * Sent when a mouse button is released.
		 *
		 * @param e an event containing information about the mouse button release
		 */
		@Override
		public void mouseUp(final MouseEvent e) {
			CurvesTab.this.mouseDown = false;
			if (CurvesTab.this.isHovering(e)) {
				CurvesTab.this.example.canvas.setCursor(CurvesTab.this.cursor);
			} else {
				CurvesTab.this.example.canvas.setCursor(null);
			}

			if (CurvesTab.this.quadPtMoved) {
        CurvesTab.this.quadPtMoved = false;
      }
			if (CurvesTab.this.quadEndPtMoved) {
        CurvesTab.this.quadEndPtMoved = false;
      }
			if (CurvesTab.this.cubPt1Moved) {
        CurvesTab.this.cubPt1Moved = false;
      }
			if (CurvesTab.this.cubPt2Moved) {
        CurvesTab.this.cubPt2Moved = false;
      }
			if (CurvesTab.this.cubEndPtMoved) {
        CurvesTab.this.cubEndPtMoved = false;
      }

			CurvesTab.this.example.redraw();
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
	gc.setLineWidth(5);

	Transform transform;

	// ----- cubic curve -----
	this.cubXPos = width/5;
	this.cubYPos = height/3;

	transform = new Transform(device);
	transform.translate(this.cubXPos, this.cubYPos);
	gc.setTransform(transform);
	transform.dispose();

	gc.setForeground(device.getSystemColor(SWT.COLOR_RED));
	gc.drawString(GraphicsExample.getResourceString("Cubic"), 25, -70, true);

	Path path = new Path(device);
	path.cubicTo(133 + this.cubDiffX1, -60 + this.cubDiffY1, 266 + this.cubDiffX2, 60 + this.cubDiffY2, 400 + this.cubEndDiffX, 0 + this.cubEndDiffY);
	gc.drawPath(path);
	path.dispose();

	gc.setTransform(null);
	gc.setForeground(device.getSystemColor(SWT.COLOR_DARK_BLUE));
	gc.drawRectangle(this.cubHndl1.x + (int)this.cubXPos, this.cubHndl1.y + (int)this.cubYPos, this.cubHndl1.width, this.cubHndl1.height);
	gc.drawRectangle(this.cubHndl2.x + (int)this.cubXPos, this.cubHndl2.y + (int)this.cubYPos, this.cubHndl2.width, this.cubHndl2.height);
	gc.drawRectangle(this.cubEndHndl.x + (int)this.cubXPos, this.cubEndHndl.y + (int)this.cubYPos, this.cubEndHndl.width, this.cubEndHndl.height);

	// ----- quadratic curve -----
	this.quadXPos = width/5;
	this.quadYPos = (2*height)/3;

	transform = new Transform(device);
	transform.translate(this.quadXPos, this.quadYPos);
	gc.setTransform(transform);
	transform.dispose();

	gc.setForeground(device.getSystemColor(SWT.COLOR_GREEN));
	gc.drawString(GraphicsExample.getResourceString("Quadratic"), 0, -50, true);

	path = new Path(device);
	path.quadTo(200 + this.quadDiffX, 150 + this.quadDiffY, 400 + this.quadEndDiffX, 0 + this.quadEndDiffY);
	gc.drawPath(path);
	path.dispose();

	gc.setTransform(null);
	gc.setForeground(device.getSystemColor(SWT.COLOR_GRAY));
	gc.drawRectangle(this.quadHndl.x + (int)this.quadXPos, this.quadHndl.y + (int)this.quadYPos, this.quadHndl.width, this.quadHndl.height);
	gc.drawRectangle(this.quadEndHndl.x + (int)this.quadXPos, this.quadEndHndl.y + (int)this.quadYPos, this.quadEndHndl.width, this.quadEndHndl.height);

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

