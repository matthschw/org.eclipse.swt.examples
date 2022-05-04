/*******************************************************************************
 * Copyright (c) 2008, 2017 IBM Corporation and others.
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
package org.eclipse.swt.examples.accessibility;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Instances of this class represent an accessible, focusable, user interface object
 * that has a shape and a color. From an accessibility point of view, they imitate a
 * "label" or "static text" whose name is its color and shape.
 */
public class Shape extends Canvas {
	static final int SQUARE = 0;
	static final int CIRCLE = 1;
	static final int TRIANGLE = 2;
	static ResourceBundle bundle = ResourceBundle.getBundle("examples_accessibility");
	int color = SWT.COLOR_BLUE;
	int shape = SQUARE;

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 *
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 */
	public Shape(final Composite parent, final int style) {
		super (parent, style);

		this.addListeners();
	}

	void addListeners() {
		this.addPaintListener(e -> {
			final GC gc = e.gc;
			final Display display = this.getDisplay();
			final Color c = display.getSystemColor(this.color);
			gc.setBackground(c);
			final Rectangle rect = this.getClientArea();
			final int length = Math.min(rect.width, rect.height);
			if (this.shape == CIRCLE) {
				gc.fillOval(0, 0, length, length);
			} else {
				gc.fillRectangle(0, 0, length, length);
			}
			if (this.isFocusControl()) {
        gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
      }
		});

		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				Shape.this.redraw();
			}
			@Override
			public void focusLost(final FocusEvent e) {
				Shape.this.redraw();
			}
		});

		this.addMouseListener(MouseListener.mouseDownAdapter(e -> {
			if (this.getClientArea().contains(e.x, e.y)) {
				this.setFocus();
			}
		}));

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				// key listener enables traversal out
			}
		});

		this.addTraverseListener(e -> {
			switch (e.detail) {
				case SWT.TRAVERSE_TAB_NEXT:
				case SWT.TRAVERSE_TAB_PREVIOUS:
					e.doit = true;
					break;
			}
		});

		this.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(final AccessibleEvent e) {
				final MessageFormat formatter = new MessageFormat("");
				formatter.applyPattern(bundle.getString("name"));  //$NON_NLS$
				final String colorName = bundle.getString("color" + Shape.this.color); //$NON_NLS$
				final String shapeName = bundle.getString("shape" + Shape.this.shape); //$NON_NLS$
				e.result = formatter.format(new String [] {colorName, shapeName}); //$NON_NLS$
			}
		});

		this.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getRole(final AccessibleControlEvent e) {
				e.detail = ACC.ROLE_GRAPHIC;
			}
			@Override
			public void getState(final AccessibleControlEvent e) {
				e.detail = ACC.STATE_FOCUSABLE;
				if (Shape.this.isFocusControl()) {
          e.detail |= ACC.STATE_FOCUSED;
        }
			}
		});
	}

	/**
	 * Return the receiver's color.
	 * The default color is SWT.COLOR_BLUE.
	 *
	 * @return the color, which may be any of the SWT.COLOR_ constants
	 */
	public int getColor () {
		return this.color;
	}

	/**
	 * Return the receiver's shape.
	 * The default shape is SQUARE.
	 *
	 * @return the shape, which may be either CIRCLE or SQUARE
	 */
	public int getShape () {
		return this.shape;
	}

	/**
	 * Set the receiver's color to the specified color.
	 * The default color is SWT.COLOR_BLUE.
	 *
	 * @param color any of the SWT.COLOR_ constants
	 */
	public void setColor (final int color) {
		this.color = color;
	}

	/**
	 * Set the receiver's shape to the specified shape.
	 * The default shape is SQUARE.
	 *
	 * @param shape an int that can be either CIRCLE or SQUARE
	 */
	public void setShape (final int shape) {
		this.shape = shape;
	}
}
