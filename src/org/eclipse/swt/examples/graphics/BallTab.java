/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
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

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Transform;

/**
 * This tab uses alpha blending to simulate "ghosting" of a ball in movement.
 */
public class BallTab extends AnimatedGraphicsTab {

	BallCollection[] bc;

	/**
	 * This inner class serves as a container for the data needed to display a
	 * collection of balls.
	 */
	static class BallCollection {
		float x, y; // position of ball
		float incX, incY; // values by which to move the ball
		int ball_size; // size (diameter) of the ball
		int capacity; // number of balls in the collection
		LinkedList<Float> prevx, prevy; // collection of previous x and y positions
								 // of ball
		Color[] colors; // colors used for this ball collection

		public BallCollection(final float x, final float y, final float incX, final float incY,
				final int ball_size, final int capacity, final Color[] colors) {
			this.x = x;
			this.y = y;
			this.incX = incX;
			this.incY = incY;
			this.ball_size = ball_size;
			this.capacity = capacity;
			this.prevx = new LinkedList<>();
			this.prevy = new LinkedList<>();
			this.colors = colors;
		}
	}

	@Override
	public void dispose() {
		this.bc[0] = this.bc[1] = this.bc[2] = this.bc[3] = this.bc[4] = null;
	}

	public BallTab(final GraphicsExample example) {
		super(example);
		this.bc = new BallCollection[5];
	}

	@Override
	public String getCategory() {
		return GraphicsExample.getResourceString("Alpha"); //$NON-NLS-1$
	}

	@Override
	public String getText() {
		return GraphicsExample.getResourceString("Ball"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return GraphicsExample.getResourceString("BallDescription"); //$NON-NLS-1$
	}

	@Override
	public int getInitialAnimationTime() {
		return 10;
	}

	@Override
	public void next(final int width, final int height) {
		for (int i = 0; i < this.bc.length; i++) {
			if (this.bc[i] == null) {
        return;
      }
			if (this.bc[i].prevx.isEmpty()) {
				this.bc[i].prevx.addLast(Float.valueOf(this.bc[i].x));
				this.bc[i].prevy.addLast(Float.valueOf(this.bc[i].y));
			} else if (this.bc[i].prevx.size() == this.bc[i].capacity) {
				this.bc[i].prevx.removeFirst();
				this.bc[i].prevy.removeFirst();
			}

			this.bc[i].x += this.bc[i].incX;
			this.bc[i].y += this.bc[i].incY;

			final float random = (float) Math.random();

			// right
			if ((this.bc[i].x + this.bc[i].ball_size) > width) {
				this.bc[i].x = width - this.bc[i].ball_size;
				this.bc[i].incX = ((random * -width) / 16) - 1;
			}
			// left
			if (this.bc[i].x < 0) {
				this.bc[i].x = 0;
				this.bc[i].incX = ((random * width) / 16) + 1;
			}
			// bottom
			if ((this.bc[i].y + this.bc[i].ball_size) > height) {
				this.bc[i].y = (height - this.bc[i].ball_size) - 2;
				this.bc[i].incY = ((random * -height) / 16) - 1;
			}
			// top
			if (this.bc[i].y < 0) {
				this.bc[i].y = 0;
				this.bc[i].incY = ((random * height) / 16) + 1;
			}
			this.bc[i].prevx.addLast(Float.valueOf(this.bc[i].x));
			this.bc[i].prevy.addLast(Float.valueOf(this.bc[i].y));
		}
	}

	@Override
	public void paint(final GC gc, final int width, final int height) {
		if (!this.example.checkAdvancedGraphics()) {
      return;
    }
		final Device device = gc.getDevice();

		if (this.bc[0] == null) {
			this.bc[0] = new BallCollection(0, 0, 5, 5, 20, 20, new Color[] { device
					.getSystemColor(SWT.COLOR_GREEN) });
			this.bc[1] = new BallCollection(50, 300, 10, -5, 50, 10,
					new Color[] { device.getSystemColor(SWT.COLOR_BLUE) });
			this.bc[2] = new BallCollection(250, 100, -5, 8, 25, 12,
					new Color[] { device.getSystemColor(SWT.COLOR_RED) });
			this.bc[3] = new BallCollection(150, 400, 5, 8, 35, 14,
					new Color[] { device.getSystemColor(SWT.COLOR_BLACK) });
			this.bc[4] = new BallCollection(100, 250, -5, -18, 100, 5,
					new Color[] { device.getSystemColor(SWT.COLOR_MAGENTA) });
		}

		for (final BallCollection ballCollection : this.bc) {
			for (int i = 0; i < ballCollection.prevx.size(); i++) {
				final Transform transform = new Transform(device);
				transform.translate(ballCollection.prevx.get(ballCollection.prevx.size()
						- (i + 1)).floatValue(), ballCollection.prevy
						.get(ballCollection.prevy.size() - (i + 1)).floatValue());
				gc.setTransform(transform);
				transform.dispose();

				final Path path = new Path(device);
				path.addArc(0, 0, ballCollection.ball_size, ballCollection.ball_size, 0, 360);
				gc.setAlpha(255 - (i * (255 / ballCollection.capacity)));
				gc.setBackground(ballCollection.colors[0]);
				gc.fillPath(path);
				gc.drawPath(path);
				path.dispose();
			}
		}
	}
}
