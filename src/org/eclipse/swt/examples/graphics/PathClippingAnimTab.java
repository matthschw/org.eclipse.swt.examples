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
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * This is another tab that demonstrates the use of a path clipping.
 */
public class PathClippingAnimTab extends AnimatedGraphicsTab {

	private Button colorButton;
	private GraphicsBackground background;
	private Menu menu;
	private int rectWidth = 300;
	private int rectHeight = 300;
	private int incWidth = 5;
	private int incHeight = 5;
	private boolean vertical = false;
	private int angle;

	public PathClippingAnimTab(final GraphicsExample example) {
		super(example);
	}

	@Override
	public String getCategory() {
		return GraphicsExample.getResourceString("Clipping"); //$NON-NLS-1$
	}

	@Override
	public String getText() {
		return GraphicsExample.getResourceString("AnimPathClipping"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return GraphicsExample.getResourceString("AnimPathClippingDesc"); //$NON-NLS-1$
	}

	@Override
	public void dispose() {
		if (this.menu != null) {
			this.menu.dispose();
			this.menu = null;
		}
	}

	/**
	 * Creates the widgets used to control the drawing.
	 */
	@Override
	public void createControlPanel(final Composite parent) {
		super.createControlPanel(parent);

		// color menu
		final ColorMenu cm = new ColorMenu();
		cm.setPatternItems(this.example.checkAdvancedGraphics());
		this.menu = cm.createMenu(parent.getParent(), gb -> {
			this.background = gb;
			this.colorButton.setImage(gb.getThumbNail());
			this.example.redraw();
		});

		// initialize the background to the 5th item in the menu (blue)
		this.background = (GraphicsBackground) this.menu.getItem(4).getData();

		// color button
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));

		this.colorButton = new Button(comp, SWT.PUSH);
		this.colorButton.setText(GraphicsExample.getResourceString("Color")); //$NON-NLS-1$
		this.colorButton.setImage(this.background.getThumbNail());
		this.colorButton.addListener(SWT.Selection, event -> {
			final Button button = (Button) event.widget;
			final Composite parent1 = button.getParent();
			final Rectangle bounds = button.getBounds();
			final Point point = parent1.toDisplay(new Point(bounds.x, bounds.y));
			this.menu.setLocation(point.x, point.y + bounds.height);
			this.menu.setVisible(true);
		});
	}

	@Override
	public void next(final int width, final int height) {
		this.angle = (this.angle + 5)%360;
		if (this.vertical) {
			if (this.rectHeight <= 0) {
				this.incHeight = -this.incHeight;
			}
			if (this.rectHeight >= height) {
				this.incHeight = -this.incHeight;
				this.vertical = false;
			}
			this.rectHeight = this.rectHeight + this.incHeight;
		} else {
			if (this.rectWidth <= 0) {
				this.incWidth = -this.incWidth;
			}
			if (this.rectWidth >= width) {
				this.incWidth = -this.incWidth;
				this.vertical = true;
			}
			this.rectWidth = this.rectWidth + this.incWidth;
		}
	}

	@Override
	public void paint(final GC gc, final int width, final int height) {
		if (!this.example.checkAdvancedGraphics()) {
      return;
    }
		final Device device = gc.getDevice();

		// top triangle
		final Path path = new Path(device);
		path.moveTo(width/2, 0);
		path.lineTo((width/2)+100, 173);
		path.lineTo((width/2)-100, 173);
		path.lineTo(width/2, 0);

		// bottom triangle
		final Path path2 = new Path(device);
		path2.moveTo(width/2, height);
		path2.lineTo((width/2)+100, height-173);
		path2.lineTo((width/2)-100, height-173);
		path2.lineTo(width/2, height);

		// left triangle
		final Path path3 = new Path(device);
		path3.moveTo(0, height/2);
		path3.lineTo(173, (height/2)-100);
		path3.lineTo(173, (height/2)+100);
		path3.lineTo(0, height/2);

		// right triangle
		final Path path4 = new Path(device);
		path4.moveTo(width, height/2);
		path4.lineTo(width-173, (height/2)-100);
		path4.lineTo(width-173, (height/2)+100);
		path4.lineTo(width, height/2);

		// circle
		final Path path5 = new Path(device);
		path5.moveTo((width-200)/2, (height-200)/2);
		path5.addArc((width-200)/2, (height-200)/2, 200, 200, 0, 360);

		// top rectangle
		final Path path6 = new Path(device);
		path6.addRectangle((width-40)/2, 175, 40, ((height-200)/2)-177);

		// bottom rectangle
		final Path path7 = new Path(device);
		path7.addRectangle((width-40)/2, ((height-200)/2)+202, 40, (height-175)-(((height-200)/2)+202));

		// left rectangle
		final Path path8 = new Path(device);
		path8.addRectangle(175, (height-40)/2, ((width-200)/2)-177, 40);

		// right rectangle
		final Path path9 = new Path(device);
		path9.addRectangle(((width-200)/2)+202, (height-40)/2, (width-175)-(((width-200)/2)+202), 40);

		path.addPath(path2);
		path.addPath(path3);
		path.addPath(path4);
		path.addPath(path5);
		path.addPath(path6);
		path.addPath(path7);
		path.addPath(path8);
		path.addPath(path9);
		gc.setClipping(path);

		Pattern pattern = null;
		if (this.background.getBgColor1() != null) {
			gc.setBackground(this.background.getBgColor1());
		} else if (this.background.getBgImage() != null) {
			pattern = new Pattern(device, this.background.getBgImage());
			gc.setBackgroundPattern(pattern);
		}

		gc.setLineWidth(2);
		gc.fillRectangle((width-this.rectWidth)/2, (height-this.rectHeight)/2, this.rectWidth, this.rectHeight);
		gc.drawPath(path);

		if (pattern != null) {
      pattern.dispose();
    }

		path9.dispose();
		path8.dispose();
		path7.dispose();
		path6.dispose();
		path5.dispose();
		path4.dispose();
		path3.dispose();
		path2.dispose();
		path.dispose();
	}
}
