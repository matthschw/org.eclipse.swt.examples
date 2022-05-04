/*******************************************************************************
 * Copyright (c) 2000, 2016 Red Hat, Inc. and others.
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
 *     Ian Pun <ipun@redhat.com> - addition of Color tab
 *******************************************************************************/
package org.eclipse.swt.examples.controlexample;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

class ColorTab extends Tab {
	Table colors, cursors;
	Group colorsGroup, cursorsGroup;
	HashMap <Integer, String> hmap = new HashMap <> ();
	HashMap <Integer, String> cmap = new HashMap <> ();
	static final int namedColorEnd = 8;
	static String [] columnTitles	= {ControlExample.getResourceString("ColorTitle_0"),
				ControlExample.getResourceString("ColorTitle_1"),
				ControlExample.getResourceString("ColorTitle_2"),
				ControlExample.getResourceString("ColorTitle_3")};

	/* Size widgets added to the "Size" group */
	Button packColumnsButton;

	/**
	 * Creates the color tab within a given instance of ControlExample.
	 */
	ColorTab(final ControlExample instance) {
		super(instance);
		this.addTableElements();
	}

	void addTableElements () {
		this.hmap.put(SWT.COLOR_WHITE, "COLOR_WHITE");
		this.hmap.put(SWT.COLOR_BLACK, "COLOR_BLACK");
		this.hmap.put(SWT.COLOR_RED, "COLOR_RED");
		this.hmap.put(SWT.COLOR_DARK_RED, "COLOR_DARK_RED");
		this.hmap.put(SWT.COLOR_GREEN, "COLOR_GREEN");
		this.hmap.put(SWT.COLOR_DARK_GREEN, "COLOR_DARK_GREEN");
		this.hmap.put(SWT.COLOR_YELLOW, "COLOR_YELLOW");
		this.hmap.put(SWT.COLOR_DARK_YELLOW, "COLOR_DARK_YELLOW");
		this.hmap.put(SWT.COLOR_WIDGET_DARK_SHADOW, "COLOR_WIDGET_DARK_SHADOW");
		this.hmap.put(SWT.COLOR_WIDGET_NORMAL_SHADOW, "COLOR_WIDGET_NORMAL_SHADOW");
		this.hmap.put(SWT.COLOR_WIDGET_LIGHT_SHADOW, "COLOR_WIDGET_LIGHT_SHADOW");
		this.hmap.put(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW, "COLOR_WIDGET_HIGHLIGHT_SHADOW");
		this.hmap.put(SWT.COLOR_WIDGET_FOREGROUND, "COLOR_WIDGET_FOREGROUND");
		this.hmap.put(SWT.COLOR_WIDGET_BACKGROUND, "COLOR_WIDGET_BACKGROUND");
		this.hmap.put(SWT.COLOR_WIDGET_BORDER, "COLOR_WIDGET_BORDER");
		this.hmap.put(SWT.COLOR_LIST_FOREGROUND, "COLOR_LIST_FOREGROUND");
		this.hmap.put(SWT.COLOR_LIST_BACKGROUND, "COLOR_LIST_BACKGROUND");
		this.hmap.put(SWT.COLOR_LIST_SELECTION, "COLOR_LIST_SELECTION");
		this.hmap.put(SWT.COLOR_LIST_SELECTION_TEXT, "COLOR_LIST_SELECTION_TEXT");
		this.hmap.put(SWT.COLOR_INFO_FOREGROUND, "COLOR_INFO_FOREGROUND");
		this.hmap.put(SWT.COLOR_INFO_BACKGROUND, "COLOR_INFO_BACKGROUND");
		this.hmap.put(SWT.COLOR_TITLE_FOREGROUND, "COLOR_TITLE_FOREGROUND");
		this.hmap.put(SWT.COLOR_TITLE_BACKGROUND, "COLOR_TITLE_BACKGROUND");
		this.hmap.put(SWT.COLOR_TITLE_BACKGROUND_GRADIENT, "COLOR_TITLE_BACKGROUND_GRADIENT");
		this.hmap.put(SWT.COLOR_TITLE_INACTIVE_FOREGROUND, "COLOR_TITLE_INACTIVE_FOREGROUND");
		this.hmap.put(SWT.COLOR_TITLE_INACTIVE_BACKGROUND, "COLOR_TITLE_INACTIVE_BACKGROUND");
		this.hmap.put(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT, "COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT");
		this.hmap.put(SWT.COLOR_LINK_FOREGROUND, "COLOR_LINK_FOREGROUND");
		this.hmap.put(SWT.COLOR_WIDGET_DISABLED_FOREGROUND, "COLOR_WIDGET_DISABLED_FOREGROUND");
		this.hmap.put(SWT.COLOR_TEXT_DISABLED_BACKGROUND, "COLOR_TEXT_DISABLED_BACKGROUND");

		this.cmap.put(SWT.CURSOR_APPSTARTING, "CURSOR_APPSTARTING");
		this.cmap.put(SWT.CURSOR_ARROW, "CURSOR_ARROW");
		this.cmap.put(SWT.CURSOR_WAIT, "CURSOR_WAIT");
		this.cmap.put(SWT.CURSOR_CROSS, "CURSOR_CROSS");
		this.cmap.put(SWT.CURSOR_HAND, "CURSOR_HAND");
		this.cmap.put(SWT.CURSOR_HELP, "CURSOR_HELP");
		this.cmap.put(SWT.CURSOR_SIZEALL, "CURSOR_SIZEALL");
		this.cmap.put(SWT.CURSOR_SIZENESW, "CURSOR_SIZENESW");
		this.cmap.put(SWT.CURSOR_SIZENS, "CURSOR_SIZENS");
		this.cmap.put(SWT.CURSOR_SIZENWSE, "CURSOR_SIZENWSE");
		this.cmap.put(SWT.CURSOR_SIZEWE, "CURSOR_SIZEWE");
		this.cmap.put(SWT.CURSOR_SIZEN, "CURSOR_SIZEN");
		this.cmap.put(SWT.CURSOR_SIZES, "CURSOR_SIZES");
		this.cmap.put(SWT.CURSOR_SIZEE, "CURSOR_SIZEE");
		this.cmap.put(SWT.CURSOR_SIZEW, "CURSOR_SIZEW");
		this.cmap.put(SWT.CURSOR_SIZENE, "CURSOR_SIZENE");
		this.cmap.put(SWT.CURSOR_SIZESE, "CURSOR_SIZESE");
		this.cmap.put(SWT.CURSOR_SIZESW, "CURSOR_SIZESW");
		this.cmap.put(SWT.CURSOR_SIZENW, "CURSOR_SIZENW");
		this.cmap.put(SWT.CURSOR_UPARROW, "CURSOR_UPARROW");
		this.cmap.put(SWT.CURSOR_IBEAM, "CURSOR_IBEAM");
		this.cmap.put(SWT.CURSOR_NO, "CURSOR_NO");
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();
		this.exampleGroup.setLayout(new GridLayout(2, false));

		this.colorsGroup = new Group (this.exampleGroup, SWT.NONE);
		this.colorsGroup.setLayout (new GridLayout ());
		this.colorsGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, true));
		this.colorsGroup.setText ("Colors");

		this.cursorsGroup = new Group (this.exampleGroup, SWT.NONE);
		this.cursorsGroup.setLayout (new GridLayout ());
		this.cursorsGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, true));
		this.cursorsGroup.setText ("Cursors");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Create the color table widget */
		/* Compute the widget style */
		final int style = this.getDefaultStyle();
		this.colors = new Table (this.colorsGroup, style | SWT.V_SCROLL);
		this.colors.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, true));
		this.colors.setHeaderVisible(true);
		// fill in the table.
		for (final String columnTitle : columnTitles) {
			final TableColumn tableColumn = new TableColumn(this.colors, SWT.NONE);
			tableColumn.setText(columnTitle);
			tableColumn.setToolTipText(ControlExample.getResourceString("Tooltip", columnTitle));
		}
		// fill in the Data. Put an empty line inbetween "Named" and "SWT" colors.
		boolean emptyLineFlag=false;
		for (final Entry<Integer, String> entry : this.hmap.entrySet()) {
			final Integer key = entry.getKey();
			final String value = entry.getValue();
			if (!emptyLineFlag) {
				final TableItem item = new TableItem(this.colors, SWT.NONE);
				item.setText(value);
				item.setText(0, value);
				item.setText(1, "Named");
				item.setText(2, this.getRGBcolor(key));
				// the spaces will help the color cell be large enough to see
				item.setText(3, "            ");
				item.setBackground(3, this.display.getSystemColor(key));
				if (key == namedColorEnd) {
					final TableItem emptyItem = new TableItem(this.colors, SWT.NONE);
					emptyItem.setText("");
					emptyLineFlag = true;
				}
			} else {
				final TableItem item = new TableItem(this.colors, SWT.NONE);
				item.setText(value);
				item.setText(0, value + " ");
				item.setText(1, "System ");
				item.setText(2, this.getRGBcolor(key) + " ");
				// the spaces will help the color cell be large enough to see
				item.setText(3, "            ");
				item.setBackground(3, this.display.getSystemColor(key));
			}
		}
		for (int i = 0; i < columnTitles.length; i++) {
			this.colors.getColumn(i).pack();
		}

		/* Create the cursor table widget */
		this.cursors = new Table (this.cursorsGroup, style | SWT.V_SCROLL);
		this.cursors.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, true));
		this.cursors.setHeaderVisible(true);
		// fill in the table.
		final TableColumn tableColumn = new TableColumn(this.cursors, SWT.NONE);
		tableColumn.setText("Cursor");
		// fill in the Data. Put an empty line inbetween "Named" and "SWT" cursors.
		for (final Entry<Integer, String> entry : this.cmap.entrySet()) {
			final Integer key = entry.getKey();
			final String value = entry.getValue();
			final TableItem item = new TableItem(this.cursors, SWT.NONE);
			item.setText(value);
			item.setData(this.display.getSystemCursor(key));
		}
		tableColumn.pack();

		this.cursors.addListener(SWT.MouseMove, e -> {
			final TableItem item = this.cursors.getItem(new Point(e.x, e.y));
			final Cursor cursor = (item != null) ? (Cursor) item.getData() : null;
			this.cursors.setCursor(cursor);
		});
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.colors, this.cursors};
	}

	/**
	 * Gets the Tab name.
	 */
	@Override
	String getTabText () {
		return "Color";
	}

	/**
	 * Colors only needs Orientation, Size, and Other groups. Everything else will be removed.
	 */
	@Override
	void createSizeGroup () {
		super.createSizeGroup();

		this.packColumnsButton = new Button (this.sizeGroup, SWT.PUSH);
		this.packColumnsButton.setText (ControlExample.getResourceString("Pack_Columns"));
		this.packColumnsButton.addSelectionListener(widgetSelectedAdapter(event -> {
			this.packColumns ();
			this.setExampleWidgetSize ();
		}));
	}

	void packColumns () {
		final int columnCount = this.colors.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			final TableColumn tableColumn = this.colors.getColumn(i);
			tableColumn.pack();
		}
	}

	String getRGBcolor(final int id){
		final Color color = this.display.getSystemColor(id);
		return String.format("(%d,%d,%d,%d)", color.getRed(), color.getGreen(),
				color.getBlue(), color.getAlpha());
	}

	@Override
	boolean rtlSupport() {
		return false;
	}

	/**
	 * Override the "Control" group.  The "Control" group
	 * is typically the right hand column in the tab.
	 */
	@Override
	void createControlGroup () {
		this.controlGroup = new Group (this.tabFolderPage, SWT.NONE);
		this.controlGroup.setLayout (new GridLayout (2, true));
		this.controlGroup.setLayoutData (new GridData(SWT.FILL, SWT.FILL, false, false));
		this.controlGroup.setText (ControlExample.getResourceString("Parameters"));
		/* Create individual groups inside the "Control" group */
		this.createOtherGroup ();
		this.createSetGetGroup();
		this.createSizeGroup ();
		this.createOrientationGroup ();

		final SelectionListener selectionListener = widgetSelectedAdapter(event -> {
			if ((event.widget.getStyle () & SWT.RADIO) != 0) {
				if (!((Button) event.widget).getSelection ()) {
          return;
        }
			}
			if (!this.handleTextDirection (event.widget)) {
				this.recreateExampleWidgets ();
			}
		});
		// attach listeners to the Orientation buttons
		this.rtlButton.addSelectionListener (selectionListener);
		this.ltrButton.addSelectionListener (selectionListener);
		this.defaultOrietationButton.addSelectionListener (selectionListener);
	}
}
