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
package org.eclipse.swt.examples.controlexample;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Widget;

class CanvasTab extends Tab {
	static final int colors [] = {
		SWT.COLOR_RED,
		SWT.COLOR_GREEN,
		SWT.COLOR_BLUE,
		SWT.COLOR_MAGENTA,
		SWT.COLOR_YELLOW,
		SWT.COLOR_CYAN,
		SWT.COLOR_DARK_RED,
		SWT.COLOR_DARK_GREEN,
		SWT.COLOR_DARK_BLUE,
		SWT.COLOR_DARK_MAGENTA,
		SWT.COLOR_DARK_YELLOW,
		SWT.COLOR_DARK_CYAN
	};
	static final String canvasString = "Canvas"; //$NON-NLS-1$

	/* Example widgets and groups that contain them */
	Canvas canvas;
	Group canvasGroup;

	/* Style widgets added to the "Style" group */
	Button horizontalButton, verticalButton, noBackgroundButton, noFocusButton,
	noMergePaintsButton, noRedrawResizeButton, doubleBufferedButton;

	/* Other widgets added to the "Other" group */
	Button caretButton, fillDamageButton;

	int paintCount;
	int cx, cy;
	int maxX, maxY;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	CanvasTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		this.caretButton = new Button (this.otherGroup, SWT.CHECK);
		this.caretButton.setText (ControlExample.getResourceString("Caret"));
		this.fillDamageButton = new Button (this.otherGroup, SWT.CHECK);
		this.fillDamageButton.setText (ControlExample.getResourceString("FillDamage"));

		/* Add the listeners */
		this.caretButton.addSelectionListener (widgetSelectedAdapter(event -> this.setCaret ()));
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the canvas widget */
		this.canvasGroup = new Group (this.exampleGroup, SWT.NONE);
		this.canvasGroup.setLayout (new GridLayout ());
		this.canvasGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.canvasGroup.setText ("Canvas");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.horizontalButton.getSelection ()) {
      style |= SWT.H_SCROLL;
    }
		if (this.verticalButton.getSelection ()) {
      style |= SWT.V_SCROLL;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }
		if (this.noBackgroundButton.getSelection ()) {
      style |= SWT.NO_BACKGROUND;
    }
		if (this.noFocusButton.getSelection ()) {
      style |= SWT.NO_FOCUS;
    }
		if (this.noMergePaintsButton.getSelection ()) {
      style |= SWT.NO_MERGE_PAINTS;
    }
		if (this.noRedrawResizeButton.getSelection ()) {
      style |= SWT.NO_REDRAW_RESIZE;
    }
		if (this.doubleBufferedButton.getSelection ()) {
      style |= SWT.DOUBLE_BUFFERED;
    }

		/* Create the example widgets */
		this.paintCount = 0; this.cx = 0; this.cy = 0;
		this.canvas = new Canvas (this.canvasGroup, style);
		this.canvas.addPaintListener(e -> {
			this.paintCount++;
			final GC gc = e.gc;
			if (this.fillDamageButton.getSelection ()) {
				final Color color = e.display.getSystemColor (colors [this.paintCount % colors.length]);
				gc.setBackground(color);
				gc.fillRectangle(e.x, e.y, e.width, e.height);
			}
			final Point size = this.canvas.getSize ();
			gc.drawArc(this.cx + 1, this.cy + 1, size.x - 2, size.y - 2, 0, 360);
			gc.drawRectangle(this.cx + ((size.x - 10) / 2), this.cy + ((size.y - 10) / 2), 10, 10);
			final Point extent = gc.textExtent(canvasString);
			gc.drawString(canvasString, this.cx + ((size.x - extent.x) / 2), (this.cy - extent.y) + ((size.y - 10) / 2), true);
		});
		this.canvas.addControlListener(ControlListener.controlResizedAdapter(e -> {
			final Point size = this.canvas.getSize();
			this.maxX = (size.x * 3) / 2;
			this.maxY = (size.y * 3) / 2;
			this.resizeScrollBars();
		}));
		ScrollBar bar = this.canvas.getHorizontalBar();
		if (bar != null) {
			this.hookListeners (bar);
			bar.addSelectionListener(widgetSelectedAdapter(event -> this.scrollHorizontal ((ScrollBar)event.widget)));
		}
		bar = this.canvas.getVerticalBar();
		if (bar != null) {
			this.hookListeners (bar);
			bar.addSelectionListener(widgetSelectedAdapter(event -> this.scrollVertical ((ScrollBar)event.widget)));
		}
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup();

		/* Create the extra widgets */
		this.horizontalButton = new Button (this.styleGroup, SWT.CHECK);
		this.horizontalButton.setText ("SWT.H_SCROLL");
		this.horizontalButton.setSelection(true);
		this.verticalButton = new Button (this.styleGroup, SWT.CHECK);
		this.verticalButton.setText ("SWT.V_SCROLL");
		this.verticalButton.setSelection(true);
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
		this.noBackgroundButton = new Button (this.styleGroup, SWT.CHECK);
		this.noBackgroundButton.setText ("SWT.NO_BACKGROUND");
		this.noFocusButton = new Button (this.styleGroup, SWT.CHECK);
		this.noFocusButton.setText ("SWT.NO_FOCUS");
		this.noMergePaintsButton = new Button (this.styleGroup, SWT.CHECK);
		this.noMergePaintsButton.setText ("SWT.NO_MERGE_PAINTS");
		this.noRedrawResizeButton = new Button (this.styleGroup, SWT.CHECK);
		this.noRedrawResizeButton.setText ("SWT.NO_REDRAW_RESIZE");
		this.doubleBufferedButton = new Button (this.styleGroup, SWT.CHECK);
		this.doubleBufferedButton.setText ("SWT.DOUBLE_BUFFERED");
	}

	/**
	 * Creates the tab folder page.
	 *
	 * @param tabFolder org.eclipse.swt.widgets.TabFolder
	 * @return the new page for the tab folder
	 */
	@Override
	Composite createTabFolderPage (final TabFolder tabFolder) {
		super.createTabFolderPage (tabFolder);

		/*
		 * Add a resize listener to the tabFolderPage so that
		 * if the user types into the example widget to change
		 * its preferred size, and then resizes the shell, we
		 * recalculate the preferred size correctly.
		 */
		this.tabFolderPage.addControlListener(ControlListener.controlResizedAdapter(e -> this.setExampleWidgetSize ()));

		return this.tabFolderPage;
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.canvas};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"ToolTipText"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Canvas";
	}

	/**
	 * Resizes the maximum and thumb of both scrollbars.
	 */
	void resizeScrollBars () {
		final Rectangle clientArea = this.canvas.getClientArea();
		ScrollBar bar = this.canvas.getHorizontalBar();
		if (bar != null) {
			bar.setMaximum(this.maxX);
			bar.setThumb(clientArea.width);
			bar.setPageIncrement(clientArea.width);
		}
		bar = this.canvas.getVerticalBar();
		if (bar != null) {
			bar.setMaximum(this.maxY);
			bar.setThumb(clientArea.height);
			bar.setPageIncrement(clientArea.height);
		}
	}

	/**
	 * Scrolls the canvas horizontally.
	 *
	 * @param scrollBar
	 */
	void scrollHorizontal (final ScrollBar scrollBar) {
		final Rectangle bounds = this.canvas.getClientArea();
		int x = -scrollBar.getSelection();
		if ((x + this.maxX) < bounds.width) {
			x = bounds.width - this.maxX;
		}
		this.canvas.scroll(x, this.cy, this.cx, this.cy, this.maxX, this.maxY, false);
		this.cx = x;
	}

	/**
	 * Scrolls the canvas vertically.
	 *
	 * @param scrollBar
	 */
	void scrollVertical (final ScrollBar scrollBar) {
		final Rectangle bounds = this.canvas.getClientArea();
		int y = -scrollBar.getSelection();
		if ((y + this.maxY) < bounds.height) {
			y = bounds.height - this.maxY;
		}
		this.canvas.scroll(this.cx, y, this.cx, this.cy, this.maxX, this.maxY, false);
		this.cy = y;
	}

	/**
	 * Sets or clears the caret in the "Example" widget.
	 */
	void setCaret () {
		final Caret oldCaret = this.canvas.getCaret ();
		if (this.caretButton.getSelection ()) {
			final Caret newCaret = new Caret(this.canvas, SWT.NONE);
			final Font font = this.canvas.getFont();
			newCaret.setFont(font);
			final GC gc = new GC(this.canvas);
			gc.setFont(font);
			newCaret.setBounds(1, 1, 1, gc.getFontMetrics().getHeight());
			gc.dispose();
			this.canvas.setCaret (newCaret);
			this.canvas.setFocus();
		} else {
			this.canvas.setCaret (null);
		}
		if (oldCaret != null) {
      oldCaret.dispose ();
    }
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.horizontalButton.setSelection ((this.canvas.getStyle () & SWT.H_SCROLL) != 0);
		this.verticalButton.setSelection ((this.canvas.getStyle () & SWT.V_SCROLL) != 0);
		this.borderButton.setSelection ((this.canvas.getStyle () & SWT.BORDER) != 0);
		this.noBackgroundButton.setSelection ((this.canvas.getStyle () & SWT.NO_BACKGROUND) != 0);
		this.noFocusButton.setSelection ((this.canvas.getStyle () & SWT.NO_FOCUS) != 0);
		this.noMergePaintsButton.setSelection ((this.canvas.getStyle () & SWT.NO_MERGE_PAINTS) != 0);
		this.noRedrawResizeButton.setSelection ((this.canvas.getStyle () & SWT.NO_REDRAW_RESIZE) != 0);
		this.doubleBufferedButton.setSelection ((this.canvas.getStyle () & SWT.DOUBLE_BUFFERED) != 0);
		if (!this.instance.startup) {
      this.setCaret ();
    }
	}
}
