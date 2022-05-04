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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

class TableTab extends ScrollableTab {
	/* Example widgets and groups that contain them */
	Table table1;
	Group tableGroup;

	/* Size widgets added to the "Size" group */
	Button packColumnsButton;

	/* Style widgets added to the "Style" group */
	Button noScrollButton, checkButton, fullSelectionButton, hideSelectionButton;

	/* Other widgets added to the "Other" group */
	Button multipleColumns, moveableColumns, resizableColumns, headerVisibleButton, sortIndicatorButton, headerImagesButton, linesVisibleButton, subImagesButton;

	/* Controls and resources added to the "Colors and Fonts" group */
	static final int ITEM_FOREGROUND_COLOR = 3;
	static final int ITEM_BACKGROUND_COLOR = 4;
	static final int ITEM_FONT = 5;
	static final int CELL_FOREGROUND_COLOR = 6;
	static final int CELL_BACKGROUND_COLOR = 7;
	static final int CELL_FONT = 8;
	static final int HEADER_FOREGROUND_COLOR = 9;
	static final int HEADER_BACKGROUND_COLOR = 10;
	Color itemForegroundColor, itemBackgroundColor, cellForegroundColor, cellBackgroundColor, headerForegroundColor, headerBackgroundColor;
	Font itemFont, cellFont;

	static String [] columnTitles	= {ControlExample.getResourceString("TableTitle_0"),
									   ControlExample.getResourceString("TableTitle_1"),
									   ControlExample.getResourceString("TableTitle_2"),
									   ControlExample.getResourceString("TableTitle_3")};

	static String[][] tableData = {
		{ ControlExample.getResourceString("TableLine0_0"),
				ControlExample.getResourceString("TableLine0_1"),
				ControlExample.getResourceString("TableLine0_2"),
				ControlExample.getResourceString("TableLine0_3") },
		{ ControlExample.getResourceString("TableLine1_0"),
				ControlExample.getResourceString("TableLine1_1"),
				ControlExample.getResourceString("TableLine1_2"),
				ControlExample.getResourceString("TableLine1_3") },
		{ ControlExample.getResourceString("TableLine2_0"),
				ControlExample.getResourceString("TableLine2_1"),
				ControlExample.getResourceString("TableLine2_2"),
				ControlExample.getResourceString("TableLine2_3") } };

	Point menuMouseCoords;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	TableTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Colors and Fonts" group.
	 */
	@Override
	void createColorAndFontGroup () {
		super.createColorAndFontGroup();

		TableItem item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Foreground_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Background_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Font"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Cell_Foreground_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Cell_Background_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Cell_Font"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Header_Foreground_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Header_Background_Color"));

		this.shell.addDisposeListener(event -> {
			if (this.itemFont != null) {
        this.itemFont.dispose();
      }
			if (this.cellFont != null) {
        this.cellFont.dispose();
      }
			this.itemBackgroundColor = null;
			this.itemForegroundColor = null;
			this.itemFont = null;
			this.cellBackgroundColor = null;
			this.cellForegroundColor = null;
			this.cellFont = null;
			this.headerForegroundColor = null;
			this.headerBackgroundColor = null;
		});
	}

	@Override
	void changeFontOrColor(final int index) {
		switch (index) {
		case ITEM_FOREGROUND_COLOR: {
			Color oldColor = this.itemForegroundColor;
			if (oldColor == null) {
        oldColor = this.table1.getItem (0).getForeground ();
      }
			this.colorDialog.setRGB(oldColor.getRGB());
			final RGB rgb = this.colorDialog.open();
			if (rgb == null) {
        return;
      }
			this.itemForegroundColor = new Color (rgb);
			this.setItemForeground ();
		}
		break;
		case ITEM_BACKGROUND_COLOR: {
			Color oldColor = this.itemBackgroundColor;
			if (oldColor == null) {
        oldColor = this.table1.getItem (0).getBackground ();
      }
			this.colorDialog.setRGB(oldColor.getRGB());
			final RGB rgb = this.colorDialog.open();
			if (rgb == null) {
        return;
      }
			this.itemBackgroundColor = new Color (rgb);
			this.setItemBackground ();
		}
		break;
		case ITEM_FONT: {
			Font oldFont = this.itemFont;
			if (oldFont == null) {
        oldFont = this.table1.getItem (0).getFont ();
      }
			this.fontDialog.setFontList(oldFont.getFontData());
			final FontData fontData = this.fontDialog.open ();
			if (fontData == null) {
        return;
      }
			oldFont = this.itemFont;
			this.itemFont = new Font (this.display, fontData);
			this.setItemFont ();
			this.setExampleWidgetSize ();
			if (oldFont != null) {
        oldFont.dispose ();
      }
		}
		break;
		case CELL_FOREGROUND_COLOR: {
			Color oldColor = this.cellForegroundColor;
			if (oldColor == null) {
        oldColor = this.table1.getItem (0).getForeground (1);
      }
			this.colorDialog.setRGB(oldColor.getRGB());
			final RGB rgb = this.colorDialog.open();
			if (rgb == null) {
        return;
      }
			this.cellForegroundColor = new Color (rgb);
			this.setCellForeground ();
		}
		break;
		case CELL_BACKGROUND_COLOR: {
			Color oldColor = this.cellBackgroundColor;
			if (oldColor == null) {
        oldColor = this.table1.getItem (0).getBackground (1);
      }
			this.colorDialog.setRGB(oldColor.getRGB());
			final RGB rgb = this.colorDialog.open();
			if (rgb == null) {
        return;
      }
			this.cellBackgroundColor = new Color (rgb);
			this.setCellBackground ();
		}
		break;
		case CELL_FONT: {
			Font oldFont = this.cellFont;
			if (oldFont == null) {
        oldFont = this.table1.getItem (0).getFont (1);
      }
			this.fontDialog.setFontList(oldFont.getFontData());
			final FontData fontData = this.fontDialog.open ();
			if (fontData == null) {
        return;
      }
			oldFont = this.cellFont;
			this.cellFont = new Font (this.display, fontData);
			this.setCellFont ();
			this.setExampleWidgetSize ();
			if (oldFont != null) {
        oldFont.dispose ();
      }
		}
		break;
		case HEADER_FOREGROUND_COLOR: {
			Color oldColor = this.headerForegroundColor;
			if (oldColor == null) {
        oldColor = this.table1.getHeaderForeground();
      }
			this.colorDialog.setRGB(oldColor.getRGB());
			final RGB rgb = this.colorDialog.open();
			if (rgb == null) {
        return;
      }
			this.headerForegroundColor = new Color (rgb);
			this.setHeaderForeground ();
		}
		break;
		case HEADER_BACKGROUND_COLOR: {
			Color oldColor = this.headerBackgroundColor;
			if (oldColor == null) {
        oldColor = this.table1.getHeaderBackground();
      }
			this.colorDialog.setRGB(oldColor.getRGB());
			final RGB rgb = this.colorDialog.open();
			if (rgb == null) {
        return;
      }
			this.headerBackgroundColor = new Color (rgb);
			this.setHeaderBackground ();
		}
		break;
		default:
			super.changeFontOrColor(index);
	}
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		this.linesVisibleButton = new Button (this.otherGroup, SWT.CHECK);
		this.linesVisibleButton.setText (ControlExample.getResourceString("Lines_Visible"));
		this.multipleColumns = new Button (this.otherGroup, SWT.CHECK);
		this.multipleColumns.setText (ControlExample.getResourceString("Multiple_Columns"));
		this.multipleColumns.setSelection(true);
		this.headerVisibleButton = new Button (this.otherGroup, SWT.CHECK);
		this.headerVisibleButton.setText (ControlExample.getResourceString("Header_Visible"));
		this.sortIndicatorButton = new Button (this.otherGroup, SWT.CHECK);
		this.sortIndicatorButton.setText (ControlExample.getResourceString("Sort_Indicator"));
		this.moveableColumns = new Button (this.otherGroup, SWT.CHECK);
		this.moveableColumns.setText (ControlExample.getResourceString("Moveable_Columns"));
		this.resizableColumns = new Button (this.otherGroup, SWT.CHECK);
		this.resizableColumns.setText (ControlExample.getResourceString("Resizable_Columns"));
		this.headerImagesButton = new Button (this.otherGroup, SWT.CHECK);
		this.headerImagesButton.setText (ControlExample.getResourceString("Header_Images"));
		this.subImagesButton = new Button (this.otherGroup, SWT.CHECK);
		this.subImagesButton.setText (ControlExample.getResourceString("Sub_Images"));

		/* Add the listeners */
		this.linesVisibleButton.addSelectionListener (widgetSelectedAdapter(event -> this.setWidgetLinesVisible ()));
		this.multipleColumns.addSelectionListener (widgetSelectedAdapter(event -> this.recreateExampleWidgets ()));
		this.headerVisibleButton.addSelectionListener (widgetSelectedAdapter(event -> this.setWidgetHeaderVisible ()));
		this.sortIndicatorButton.addSelectionListener (widgetSelectedAdapter(event -> this.setWidgetSortIndicator ()));
		this.moveableColumns.addSelectionListener (widgetSelectedAdapter(event -> this.setColumnsMoveable ()));
		this.resizableColumns.addSelectionListener (widgetSelectedAdapter(event -> this.setColumnsResizable ()));
		this.headerImagesButton.addSelectionListener (widgetSelectedAdapter(event -> this.recreateExampleWidgets ()));
		this.subImagesButton.addSelectionListener (widgetSelectedAdapter(event -> this.recreateExampleWidgets ()));
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the table */
		this.tableGroup = new Group (this.exampleGroup, SWT.NONE);
		this.tableGroup.setLayout (new GridLayout ());
		this.tableGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.tableGroup.setText ("Table");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {
		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.singleButton.getSelection ()) {
      style |= SWT.SINGLE;
    }
		if (this.multiButton.getSelection ()) {
      style |= SWT.MULTI;
    }
		if (this.verticalButton.getSelection ()) {
      style |= SWT.V_SCROLL;
    }
		if (this.horizontalButton.getSelection ()) {
      style |= SWT.H_SCROLL;
    }
		if (this.noScrollButton.getSelection ()) {
      style |= SWT.NO_SCROLL;
    }
		if (this.checkButton.getSelection ()) {
      style |= SWT.CHECK;
    }
		if (this.fullSelectionButton.getSelection ()) {
      style |= SWT.FULL_SELECTION;
    }
		if (this.hideSelectionButton.getSelection ()) {
      style |= SWT.HIDE_SELECTION;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }

		/* Create the table widget */
		this.table1 = new Table (this.tableGroup, style);

		/* Fill the table with data */
		final boolean multiColumn = this.multipleColumns.getSelection();
		if (multiColumn) {
			for (int i = 0; i < columnTitles.length; i++) {
				final TableColumn tableColumn = new TableColumn(this.table1, SWT.NONE);
				tableColumn.setText(columnTitles[i]);
				tableColumn.setToolTipText(ControlExample.getResourceString("Tooltip", columnTitles[i]));
				if (this.headerImagesButton.getSelection()) {
          tableColumn.setImage(this.instance.images [i % 3]);
        }
			}
			this.table1.setSortColumn(this.table1.getColumn(0));
		}
		for (int i=0; i<16; i++) {
			final TableItem item = new TableItem (this.table1, SWT.NONE);
			if (multiColumn && this.subImagesButton.getSelection()) {
				for (int j = 0; j < columnTitles.length; j++) {
					item.setImage(j, this.instance.images [i % 3]);
				}
			} else {
				item.setImage(this.instance.images [i % 3]);
			}
			this.setItemText (item, i, ControlExample.getResourceString("Index") + i);
		}
		this.packColumns();
	}

	void setItemText(final TableItem item, final int i, final String node) {
		final int index = i % 3;
		if (this.multipleColumns.getSelection()) {
			tableData [index][0] = node;
			item.setText (tableData [index]);
		} else {
			item.setText (node);
		}
	}

	/**
	 * Creates the "Size" group.  The "Size" group contains
	 * controls that allow the user to change the size of
	 * the example widgets.
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

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup () {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.noScrollButton = new Button (this.styleGroup, SWT.CHECK);
		this.noScrollButton.setText ("SWT.NO_SCROLL");
		this.noScrollButton.moveAbove(this.borderButton);
		this.checkButton = new Button (this.styleGroup, SWT.CHECK);
		this.checkButton.setText ("SWT.CHECK");
		this.fullSelectionButton = new Button (this.styleGroup, SWT.CHECK);
		this.fullSelectionButton.setText ("SWT.FULL_SELECTION");
		this.hideSelectionButton = new Button (this.styleGroup, SWT.CHECK);
		this.hideSelectionButton.setText ("SWT.HIDE_SELECTION");
	}

	/**
	 * Gets the "Example" widget children's items, if any.
	 *
	 * @return an array containing the example widget children's items
	 */
	@Override
	Item [] getExampleWidgetItems () {
		final Item [] columns = this.table1.getColumns();
		final Item [] items = this.table1.getItems();
		final Item [] allItems = new Item [columns.length + items.length];
		System.arraycopy(columns, 0, allItems, 0, columns.length);
		System.arraycopy(items, 0, allItems, columns.length, items.length);
		return allItems;
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.table1};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"ColumnOrder", "ItemCount", "Selection", "SelectionIndex", "ToolTipText", "TopIndex"};
	}

	@Override
	String setMethodName(final String methodRoot) {
		/* Override to handle special case of int getSelectionIndex()/setSelection(int) */
		return (methodRoot.equals("SelectionIndex")) ? "setSelection" : "set" + methodRoot;
	}

	void packColumns () {
		final int columnCount = this.table1.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			final TableColumn tableColumn = this.table1.getColumn(i);
			tableColumn.pack();
		}
	}

	@Override
	Object[] parameterForType(final String typeName, final String value, final Widget widget) {
		if (value.isEmpty())
     {
      return new Object[] {new TableItem[0]}; // bug in Table?
    }
		if (typeName.equals("org.eclipse.swt.widgets.TableItem")) {
			final TableItem item = this.findItem(value, ((Table) widget).getItems());
			if (item != null) {
        return new Object[] {item};
      }
		}
		if (typeName.equals("[Lorg.eclipse.swt.widgets.TableItem;")) {
			final String[] values = this.split(value, ',');
			final TableItem[] items = new TableItem[values.length];
			for (int i = 0; i < values.length; i++) {
				items[i] = this.findItem(values[i], ((Table) widget).getItems());
			}
			return new Object[] {items};
		}
		return super.parameterForType(typeName, value, widget);
	}

	TableItem findItem(final String value, final TableItem[] items) {
		for (final TableItem item : items) {
			if (item.getText().equals(value)) {
        return item;
      }
		}
		return null;
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Table";
	}

	/**
	 * Sets the foreground color, background color, and font
	 * of the "Example" widgets to their default settings.
	 * Also sets foreground and background color of TableItem [0]
	 * to default settings.
	 */
	@Override
	void resetColorsAndFonts () {
		super.resetColorsAndFonts ();
		this.itemForegroundColor = null;
		this.setItemForeground ();
		this.itemBackgroundColor = null;
		this.setItemBackground ();
		Font oldFont = this.font;
		this.itemFont = null;
		this.setItemFont ();
		if (oldFont != null) {
      oldFont.dispose();
    }
		this.cellForegroundColor = null;
		this.setCellForeground ();
		this.cellBackgroundColor = null;
		this.setCellBackground ();
		oldFont = this.font;
		this.cellFont = null;
		this.setCellFont ();
		if (oldFont != null) {
      oldFont.dispose();
    }
		this.headerBackgroundColor = null;
		this.setHeaderBackground ();
		this.headerForegroundColor = null;
		this.setHeaderForeground ();
	}

	/**
	 * Sets the background color of the Row 0 TableItem in column 1.
	 */
	void setCellBackground () {
		if (!this.instance.startup) {
			this.table1.getItem (0).setBackground (1, this.cellBackgroundColor);
		}
		/* Set the background color item's image to match the background color of the cell. */
		Color color = this.cellBackgroundColor;
		if (color == null) {
      color = this.table1.getItem (0).getBackground (1);
    }
		final TableItem item = this.colorAndFontTable.getItem(CELL_BACKGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the foreground color of the Row 0 TableItem in column 1.
	 */
	void setCellForeground () {
		if (!this.instance.startup) {
			this.table1.getItem (0).setForeground (1, this.cellForegroundColor);
		}
		/* Set the foreground color item's image to match the foreground color of the cell. */
		Color color = this.cellForegroundColor;
		if (color == null) {
      color = this.table1.getItem (0).getForeground (1);
    }
		final TableItem item = this.colorAndFontTable.getItem(CELL_FOREGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the font of the Row 0 TableItem in column 1.
	 */
	void setCellFont () {
		if (!this.instance.startup) {
			this.table1.getItem (0).setFont (1, this.cellFont);
		}
		/* Set the font item's image to match the font of the item. */
		Font ft = this.cellFont;
		if (ft == null) {
      ft = this.table1.getItem (0).getFont (1);
    }
		final TableItem item = this.colorAndFontTable.getItem(CELL_FONT);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.fontImage(ft));
		item.setFont(ft);
		this.colorAndFontTable.layout ();
	}

	/**
	 * Sets the background color of TableItem [0].
	 */
	void setItemBackground () {
		if (!this.instance.startup) {
			this.table1.getItem (0).setBackground (this.itemBackgroundColor);
		}
		/* Set the background color item's image to match the background color of the item. */
		Color color = this.itemBackgroundColor;
		if (color == null) {
      color = this.table1.getItem (0).getBackground ();
    }
		final TableItem item = this.colorAndFontTable.getItem(ITEM_BACKGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the foreground color of TableItem [0].
	 */
	void setItemForeground () {
		if (!this.instance.startup) {
			this.table1.getItem (0).setForeground (this.itemForegroundColor);
		}
		/* Set the foreground color item's image to match the foreground color of the item. */
		Color color = this.itemForegroundColor;
		if (color == null) {
      color = this.table1.getItem (0).getForeground ();
    }
		final TableItem item = this.colorAndFontTable.getItem(ITEM_FOREGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the font of TableItem 0.
	 */
	void setItemFont () {
		if (!this.instance.startup) {
			this.table1.getItem (0).setFont (this.itemFont);
		}
		/* Set the font item's image to match the font of the item. */
		Font ft = this.itemFont;
		if (ft == null) {
      ft = this.table1.getItem (0).getFont ();
    }
		final TableItem item = this.colorAndFontTable.getItem(ITEM_FONT);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.fontImage(ft));
		item.setFont(ft);
		this.colorAndFontTable.layout ();
	}

	void setHeaderBackground () {
		if (!this.instance.startup) {
			this.table1.setHeaderBackground (this.headerBackgroundColor);
		}
		/* Set the header background color item's image to match the header background color. */
		Color color = this.headerBackgroundColor;
		if (color == null) {
      color = this.table1.getHeaderBackground();
    }
		final TableItem item = this.colorAndFontTable.getItem(HEADER_BACKGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	void setHeaderForeground () {
		if (!this.instance.startup) {
			this.table1.setHeaderForeground (this.headerForegroundColor);
		}
		/* Set the header foreground color item's image to match the header foreground color. */
		Color color = this.headerForegroundColor;
		if (color == null) {
      color = this.table1.getHeaderForeground();
    }
		final TableItem item = this.colorAndFontTable.getItem(HEADER_FOREGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the moveable columns state of the "Example" widgets.
	 */
	void setColumnsMoveable () {
		final boolean selection = this.moveableColumns.getSelection();
		final TableColumn[] columns = this.table1.getColumns();
		for (final TableColumn column : columns) {
			column.setMoveable(selection);
		}
	}

	/**
	 * Sets the resizable columns state of the "Example" widgets.
	 */
	void setColumnsResizable () {
		final boolean selection = this.resizableColumns.getSelection();
		final TableColumn[] columns = this.table1.getColumns();
		for (final TableColumn column : columns) {
			column.setResizable(selection);
		}
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		this.setItemBackground ();
		this.setItemForeground ();
		this.setItemFont ();
		this.setCellBackground ();
		this.setCellForeground ();
		this.setCellFont ();
		this.setHeaderBackground ();
		this.setHeaderForeground ();
		if (!this.instance.startup) {
			this.setColumnsMoveable ();
			this.setColumnsResizable ();
			this.setWidgetHeaderVisible ();
			this.setWidgetSortIndicator ();
			this.setWidgetLinesVisible ();
		}
		super.setExampleWidgetState ();
		this.noScrollButton.setSelection ((this.table1.getStyle () & SWT.NO_SCROLL) != 0);
		this.checkButton.setSelection ((this.table1.getStyle () & SWT.CHECK) != 0);
		this.fullSelectionButton.setSelection ((this.table1.getStyle () & SWT.FULL_SELECTION) != 0);
		this.hideSelectionButton.setSelection ((this.table1.getStyle () & SWT.HIDE_SELECTION) != 0);
		try {
			final TableColumn column = this.table1.getColumn(0);
			this.moveableColumns.setSelection (column.getMoveable());
			this.resizableColumns.setSelection (column.getResizable());
		} catch (final IllegalArgumentException ex) {}
		this.headerVisibleButton.setSelection (this.table1.getHeaderVisible());
		this.linesVisibleButton.setSelection (this.table1.getLinesVisible());
	}

	/**
	 * Sets the header visible state of the "Example" widgets.
	 */
	void setWidgetHeaderVisible () {
		this.table1.setHeaderVisible (this.headerVisibleButton.getSelection ());
	}

	/**
	 * Sets the sort indicator state of the "Example" widgets.
	 */
	void setWidgetSortIndicator () {
		final TableColumn [] columns = this.table1.getColumns();
		if (this.sortIndicatorButton.getSelection ()) {
			/* Reset to known state: 'down' on column 0. */
			this.table1.setSortDirection (SWT.DOWN);
			for (int i = 0; i < columns.length; i++) {
				final TableColumn column = columns[i];
				if (i == 0) {
          this.table1.setSortColumn(column);
        }
				final SelectionListener listener = widgetSelectedAdapter(e -> {
					int sortDirection = SWT.DOWN;
					if (e.widget == this.table1.getSortColumn()) {
						/* If the sort column hasn't changed, cycle down -> up -> none. */
						switch (this.table1.getSortDirection ()) {
						case SWT.DOWN: sortDirection = SWT.UP; break;
						case SWT.UP: sortDirection = SWT.NONE; break;
						}
					} else {
						this.table1.setSortColumn((TableColumn)e.widget);
					}
					this.table1.setSortDirection (sortDirection);
				});
				column.addSelectionListener(listener);
				column.setData("SortListener", listener);	//$NON-NLS-1$
			}
		} else {
			this.table1.setSortDirection (SWT.NONE);
			for (final TableColumn column : columns) {
				final SelectionListener listener = (SelectionListener)column.getData("SortListener");	//$NON-NLS-1$
				if (listener != null) {
          column.removeSelectionListener(listener);
        }
			}
		}
	}

	/**
	 * Sets the lines visible state of the "Example" widgets.
	 */
	void setWidgetLinesVisible () {
		this.table1.setLinesVisible (this.linesVisibleButton.getSelection ());
	}

	@Override
	protected void specialPopupMenuItems(final Menu menu, final Event event) {
		final MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("getItem(Point) on mouse coordinates");
		this.menuMouseCoords = this.table1.toControl(new Point(event.x, event.y));
		item.addSelectionListener(widgetSelectedAdapter(e -> {
			this.eventConsole.append ("getItem(Point(" + this.menuMouseCoords + ")) returned: " + this.table1.getItem(this.menuMouseCoords));
			this.eventConsole.append ("\n");
		}));
	}
}
