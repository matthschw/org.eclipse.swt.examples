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
import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Instances of this class represent a very simple accessible bar chart.
 * From an accessibility point of view, they present the data as a "list" with "list items".
 */
public class BarChart extends Canvas {
	static ResourceBundle bundle = ResourceBundle.getBundle("examples_accessibility");
	List<Object[]> data = new ArrayList<>();
	String title;
	int color = SWT.COLOR_RED;
	int selectedItem = -1;
	int valueMin = 0;
	int valueMax = 10;
	int valueIncrement = 1;
	static final int GAP = 4;
	static final int AXIS_WIDTH = 2;

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 *
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 */
	public BarChart(final Composite parent, final int style) {
		super (parent, style);

		this.addListeners();
	}

	void addListeners() {
		this.addPaintListener(e -> {
			final GC gc = e.gc;
			final Rectangle rect = this.getClientArea();
			final Display display = this.getDisplay();
			final int count = this.data.size();
			final Point valueSize = gc.stringExtent (Integer.toString(this.valueMax));
			final int leftX = rect.x + (2 * GAP) + valueSize.x;
			final int bottomY = (rect.y + rect.height) - (2 * GAP) - valueSize.y;
			final int unitWidth = ((rect.width - (4 * GAP) - valueSize.x - AXIS_WIDTH) / count) - GAP;
			final int unitHeight = (rect.height - (3 * GAP) - AXIS_WIDTH - (2 * valueSize.y)) / ((this.valueMax - this.valueMin) / this.valueIncrement);
			// draw the title
			final int titleWidth = gc.stringExtent (this.title).x;
			final int center = (Math.max(titleWidth, (count * (unitWidth + GAP)) + GAP) - titleWidth) / 2;
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.drawString(this.title, leftX + AXIS_WIDTH + center, rect.y + GAP);
			// draw the y axis and value labels
			gc.setLineWidth(AXIS_WIDTH);
			gc.drawLine(leftX, rect.y + GAP + valueSize.y, leftX, bottomY);
			for (int i1 = this.valueMin; i1 <= this.valueMax; i1+=this.valueIncrement) {
				final int y = bottomY - (i1 * unitHeight);
				gc.drawLine(leftX, y, leftX - GAP, y);
				gc.drawString(Integer.toString(i1), rect.x + GAP, y - valueSize.y);
			}
			// draw the x axis and item labels
			gc.drawLine(leftX, bottomY, (rect.x + rect.width) - GAP, bottomY);
			for (int i2 = 0; i2 < count; i2++) {
				final Object [] dataItem1 = this.data.get(i2);
				final String itemLabel = (String)dataItem1[0];
				final int x1 = leftX + AXIS_WIDTH + GAP + (i2 * (unitWidth + GAP));
				gc.drawString(itemLabel, x1, bottomY + GAP);
			}
			// draw the bars
			gc.setBackground(display.getSystemColor(this.color));
			for (int i3 = 0; i3 < count; i3++) {
				final Object [] dataItem2 = this.data.get(i3);
				final int itemValue1 = ((Integer)dataItem2[1]).intValue();
				final int x2 = leftX + AXIS_WIDTH + GAP + (i3 * (unitWidth + GAP));
				gc.fillRectangle(x2, bottomY - AXIS_WIDTH - (itemValue1 * unitHeight), unitWidth, itemValue1 * unitHeight);
			}
			if (this.isFocusControl()) {
				if (this.selectedItem == -1) {
					// draw the focus rectangle around the whole bar chart
					gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
				} else {
					// draw the focus rectangle around the selected item
					final Object [] dataItem3 = this.data.get(this.selectedItem);
					final int itemValue2 = ((Integer)dataItem3[1]).intValue();
					final int x3 = leftX + AXIS_WIDTH + GAP + (this.selectedItem * (unitWidth + GAP));
					gc.drawFocus(x3, bottomY - (itemValue2 * unitHeight) - AXIS_WIDTH, unitWidth, (itemValue2 * unitHeight) + AXIS_WIDTH + GAP + valueSize.y);
				}
			}
		});

		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				BarChart.this.redraw();
			}
			@Override
			public void focusLost(final FocusEvent e) {
				BarChart.this.redraw();
			}
		});

		this.addMouseListener(MouseListener.mouseDownAdapter(e -> {
			if (this.getClientArea().contains(e.x, e.y)) {
				this.setFocus();
				int item = -1;
				final int count = this.data.size();
				for (int i = 0; i < count; i++) {
					if (this.itemBounds(i).contains(e.x, e.y)) {
						item = i;
						break;
					}
				}
				if (item != this.selectedItem) {
					this.selectedItem = item;
					this.redraw();
					this.getAccessible().setFocus(item);
					this.getAccessible().selectionChanged();
				}
			}
		}));

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				boolean change = false;
				switch (e.keyCode) {
					case SWT.ARROW_DOWN:
					case SWT.ARROW_RIGHT:
						BarChart.this.selectedItem++;
						if (BarChart.this.selectedItem >= BarChart.this.data.size()) {
              BarChart.this.selectedItem = 0;
            }
						change = true;
						break;
					case SWT.ARROW_UP:
					case SWT.ARROW_LEFT:
						BarChart.this.selectedItem--;
						if (BarChart.this.selectedItem <= -1) {
              BarChart.this.selectedItem = BarChart.this.data.size() - 1;
            }
						change = true;
						break;
					case SWT.HOME:
						BarChart.this.selectedItem = 0;
						change = true;
						break;
					case SWT.END:
						BarChart.this.selectedItem = BarChart.this.data.size() - 1;
						change = true;
						break;
				}
				if (change) {
					BarChart.this.redraw();
					BarChart.this.getAccessible().setFocus(BarChart.this.selectedItem);
					BarChart.this.getAccessible().selectionChanged();
				}
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
				final MessageFormat formatter = new MessageFormat("");  //$NON_NLS$
				formatter.applyPattern(bundle.getString("name"));  //$NON_NLS$
				final int childID = e.childID;
				if (childID == ACC.CHILDID_SELF) {
					e.result = BarChart.this.title;
				} else {
					final Object [] item = BarChart.this.data.get(childID);
					e.result = formatter.format(item);
				}
			}
			@Override
			public void getDescription(final AccessibleEvent e) {
				final int childID = e.childID;
				if (childID != ACC.CHILDID_SELF) {
					final Object [] item = BarChart.this.data.get(childID);
					final String value = item[1].toString();
					final String colorName = bundle.getString("color" + BarChart.this.color); //$NON_NLS$
					final MessageFormat formatter = new MessageFormat("");  //$NON_NLS$
					formatter.applyPattern(bundle.getString("color_value"));  //$NON_NLS$
					e.result = formatter.format(new String [] {colorName, value});
				}
			}
		});

		this.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getRole(final AccessibleControlEvent e) {
				if (e.childID == ACC.CHILDID_SELF) {
					e.detail = ACC.ROLE_LIST;
				} else {
					e.detail = ACC.ROLE_LISTITEM;
				}
			}
			@Override
			public void getChildCount(final AccessibleControlEvent e) {
				e.detail = BarChart.this.data.size();
			}
			@Override
			public void getChildren(final AccessibleControlEvent e) {
				final int count = BarChart.this.data.size();
				final Object[] children = new Object[count];
				for (int i = 0; i < count; i++) {
					children[i] = Integer.valueOf(i);
				}
				e.children = children;
			}
			@Override
			public void getChildAtPoint(final AccessibleControlEvent e) {
				final Point testPoint = BarChart.this.toControl(e.x, e.y);
				int childID = ACC.CHILDID_NONE;
				if (BarChart.this.getClientArea().contains(testPoint)) {
					childID = ACC.CHILDID_SELF;
					final int count = BarChart.this.data.size();
					for (int i = 0; i < count; i++) {
						if (BarChart.this.itemBounds(i).contains(testPoint)) {
							childID = i;
							break;
						}
					}
				}
				e.childID = childID;
			}
			@Override
			public void getLocation(final AccessibleControlEvent e) {
				Rectangle location = null;
				Point pt = null;
				final int childID = e.childID;
				if (childID == ACC.CHILDID_SELF) {
					location = BarChart.this.getClientArea();
					pt = BarChart.this.getParent().toDisplay(location.x, location.y);
				} else {
					location = BarChart.this.itemBounds(childID);
					pt = BarChart.this.toDisplay(location.x, location.y);
				}
				e.x = pt.x;
				e.y = pt.y;
				e.width = location.width;
				e.height = location.height;
			}
			@Override
			public void getFocus(final AccessibleControlEvent e) {
				int childID = ACC.CHILDID_NONE;
				if (BarChart.this.isFocusControl()) {
					if (BarChart.this.selectedItem == -1) {
						childID = ACC.CHILDID_SELF;
					} else {
						childID = BarChart.this.selectedItem;
					}
				}
				e.childID = childID;

			}
			@Override
			public void getSelection(final AccessibleControlEvent e) {
				e.childID = (BarChart.this.selectedItem == -1) ? ACC.CHILDID_NONE : BarChart.this.selectedItem;
			}
			@Override
			public void getValue(final AccessibleControlEvent e) {
				final int childID = e.childID;
				if (childID != ACC.CHILDID_SELF) {
					final Object [] dataItem = BarChart.this.data.get(childID);
					e.result = ((Integer)dataItem[1]).toString();
				}
			}
			@Override
			public void getState(final AccessibleControlEvent e) {
				final int childID = e.childID;
				e.detail = ACC.STATE_FOCUSABLE;
				if (BarChart.this.isFocusControl()) {
          e.detail |= ACC.STATE_FOCUSED;
        }
				if (childID != ACC.CHILDID_SELF) {
					e.detail |= ACC.STATE_SELECTABLE;
					if (childID == BarChart.this.selectedItem) {
            e.detail |= ACC.STATE_SELECTED;
          }
				}
			}
		});
	}

	@Override
	public Point computeSize (final int wHint, final int hHint, final boolean changed) {
		this.checkWidget ();
		final int count = this.data.size();
		final GC gc = new GC (this);
		final int titleWidth = gc.stringExtent (this.title).x;
		final Point valueSize = gc.stringExtent (Integer.toString(this.valueMax));
		int itemWidth = 0;
		for (int i = 0; i < count; i++) {
			final Object [] dataItem = this.data.get(i);
			final String itemLabel = (String)dataItem[0];
			itemWidth = Math.max(itemWidth, gc.stringExtent (itemLabel).x);
		}
		gc.dispose();
		int width = Math.max(titleWidth, (count * (itemWidth + GAP)) + GAP) + (3 * GAP) + AXIS_WIDTH + valueSize.x;
		int height = (3 * GAP) + AXIS_WIDTH + (valueSize.y * (((this.valueMax - this.valueMin) / this.valueIncrement) + 3));
		if (wHint != SWT.DEFAULT) {
      width = wHint;
    }
		if (hHint != SWT.DEFAULT) {
      height = hHint;
    }
		final int border = this.getBorderWidth ();
		final Rectangle trim = this.computeTrim (0, 0, width + (border*2), height + (border*2));
		return new Point (trim.width, trim.height);
	}

	/**
	 * Add a labeled data value to the bar chart.
	 *
	 * @param label a string describing the value
	 * @param value the data value
	 */
	public void addData (final String label, final int value) {
		this.checkWidget ();
		this.data.add(new Object[] {label, Integer.valueOf(value)});
	}

	/**
	 * Set the title of the bar chart.
	 *
	 * @param title a string to display as the bar chart's title.
	 */
	public void setTitle (final String title) {
		this.checkWidget ();
		this.title = title;
	}

	/**
	 * Set the bar color to the specified color.
	 * The default color is SWT.COLOR_RED.
	 *
	 * @param color any of the SWT.COLOR_* constants
	 */
	public void setColor (final int color) {
		this.checkWidget ();
		this.color = color;
	}

	/**
	 * Set the minimum value for the y axis.
	 * The default minimum is 0.
	 *
	 * @param min the minimum value
	 */
	public void setValueMin (final int min) {
		this.checkWidget ();
		this.valueMin = min;
	}

	/**
	 * Set the maximum value for the y axis.
	 * The default maximum is 10.
	 *
	 * @param max the maximum value
	 */
	public void setValueMax (final int max) {
		this.checkWidget ();
		this.valueMax = max;
	}

	/**
	 * Set the increment value for the y axis.
	 * The default increment is 1.
	 *
	 * @param increment the increment value
	 */
	public void setValueIncrement (final int increment) {
		this.checkWidget ();
		this.valueIncrement = increment;
	}

	/* The bounds of the specified item in the coordinate system of the BarChart. */
	Rectangle itemBounds(final int index) {
		final Rectangle rect = this.getClientArea();
		final GC gc = new GC (BarChart.this);
		final Point valueSize = gc.stringExtent (Integer.toString(this.valueMax));
		gc.dispose();
		final int leftX = rect.x + (2 * GAP) + valueSize.x;
		final int bottomY = (rect.y + rect.height) - (2 * GAP) - valueSize.y;
		final int unitWidth = ((rect.width - (4 * GAP) - valueSize.x - AXIS_WIDTH) / this.data.size()) - GAP;
		final int unitHeight = (rect.height - (3 * GAP) - AXIS_WIDTH - (2 * valueSize.y)) / ((this.valueMax - this.valueMin) / this.valueIncrement);
		final Object [] dataItem = this.data.get(index);
		final int itemValue = ((Integer)dataItem[1]).intValue();
		final int x = leftX + AXIS_WIDTH + GAP + (index * (unitWidth + GAP));
		return new Rectangle(x, bottomY - (itemValue * unitHeight) - AXIS_WIDTH, unitWidth, (itemValue * unitHeight) + AXIS_WIDTH + GAP + valueSize.y);
	}
}