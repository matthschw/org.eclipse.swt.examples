/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTableCellEvent;
import org.eclipse.swt.accessibility.AccessibleTableCellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class represent a selectable user interface object
 * that represents an item in a table.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#table">Table, TableItem, TableColumn snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CTableItem extends Item {
	CTable parent;
	int index = -1;
	boolean checked, grayed, cached;

	String[] texts;
	int[] textWidths = new int [1];	/* cached string measurements */
	int customWidth = -1;				/* width specified by Measure callback */
	int fontHeight;						/* cached item font height */
	int[] fontHeights;
	int imageIndent;
	Image[] images;
	Color foreground, background;
	String[] displayTexts;
	Accessible[] accessibles;
	Color[] cellForegrounds, cellBackgrounds;
	Font font;
	Font[] cellFonts;
	Display display;

	static final int MARGIN_TEXT = 3;			/* the left and right margins within the text's space */

/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Table</code>) and a style value
 * describing its behavior and appearance. The item is added
 * to the end of the items maintained by its parent.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a composite control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public CTableItem (final CTable parent, final int style) {
	this (parent, style, checkNull (parent).itemsCount);
}
/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Table</code>), a style value
 * describing its behavior and appearance, and the index
 * at which to place it in the items maintained by its parent.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a composite control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 * @param index the zero-relative index to store the receiver in its parent
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public CTableItem (final CTable parent, final int style, final int index) {
	this (parent, style, index, true);
}
CTableItem (final CTable parent, final int style, final int index, final boolean notifyParent) {
	super (parent, style);
	final int validItemIndex = parent.itemsCount;
	if (!((0 <= index) && (index <= validItemIndex))) {
    SWT.error (SWT.ERROR_INVALID_RANGE);
  }
	this.parent = parent;
	this.index = index;
	this.display = parent.getDisplay ();
	final int columnCount = parent.columns.length;
	this.accessibles = new Accessible [columnCount > 0 ? columnCount : 1];
	if (columnCount > 0) {
		this.displayTexts = new String [columnCount];
		if (columnCount > 1) {
			this.texts = new String [columnCount];
			this.textWidths = new int [columnCount];
			this.images = new Image [columnCount];
		}
	}
	if (notifyParent) {
    parent.createItem (this);
  }
}
/*
 * Updates internal structures in the receiver and its child items to handle the creation of a new column.
 */
void addColumn (final CTableColumn column) {
	final int index = column.getIndex ();
	final int columnCount = this.parent.columns.length;

	if (columnCount > 1) {
		if (columnCount == 2) {
			this.texts = new String [2];
		} else {
			final String[] newTexts = new String [columnCount];
			System.arraycopy (this.texts, 0, newTexts, 0, index);
			System.arraycopy (this.texts, index, newTexts, index + 1, columnCount - index - 1);
			this.texts = newTexts;
		}
		if (index == 0) {
			this.texts [1] = super.getText ();
			super.setText ("");	//$NON-NLS-1$
		}

		if (columnCount == 2) {
			this.images = new Image [2];
		} else {
			final Image[] newImages = new Image [columnCount];
			System.arraycopy (this.images, 0, newImages, 0, index);
			System.arraycopy (this.images, index, newImages, index + 1, columnCount - index - 1);
			this.images = newImages;
		}
		if (index == 0) {
			this.images [1] = super.getImage ();
			super.setImage (null);
		}

		final int[] newTextWidths = new int [columnCount];
		System.arraycopy (this.textWidths, 0, newTextWidths, 0, index);
		System.arraycopy (this.textWidths, index, newTextWidths, index + 1, columnCount - index - 1);
		this.textWidths = newTextWidths;
	} else {
		this.customWidth = -1;		/* columnCount == 1 */
	}

	/*
	 * The length of displayTexts always matches the parent's column count, unless this
	 * count is zero, in which case displayTexts is null.
	 */
	final String[] newDisplayTexts = new String [columnCount];
	if (columnCount > 1) {
		System.arraycopy (this.displayTexts, 0, newDisplayTexts, 0, index);
		System.arraycopy (this.displayTexts, index, newDisplayTexts, index + 1, columnCount - index - 1);
	}
	this.displayTexts = newDisplayTexts;

	if (this.cellBackgrounds != null) {
		final Color[] newCellBackgrounds = new Color [columnCount];
		System.arraycopy (this.cellBackgrounds, 0, newCellBackgrounds, 0, index);
		System.arraycopy (this.cellBackgrounds, index, newCellBackgrounds, index + 1, columnCount - index - 1);
		this.cellBackgrounds = newCellBackgrounds;
	}
	if (this.cellForegrounds != null) {
		final Color[] newCellForegrounds = new Color [columnCount];
		System.arraycopy (this.cellForegrounds, 0, newCellForegrounds, 0, index);
		System.arraycopy (this.cellForegrounds, index, newCellForegrounds, index + 1, columnCount - index - 1);
		this.cellForegrounds = newCellForegrounds;
	}
	if (this.cellFonts != null) {
		final Font[] newCellFonts = new Font [columnCount];
		System.arraycopy (this.cellFonts, 0, newCellFonts, 0, index);
		System.arraycopy (this.cellFonts, index, newCellFonts, index + 1, columnCount - index - 1);
		this.cellFonts = newCellFonts;

		final int[] newFontHeights = new int [columnCount];
		System.arraycopy (this.fontHeights, 0, newFontHeights, 0, index);
		System.arraycopy (this.fontHeights, index, newFontHeights, index + 1, columnCount - index - 1);
		this.fontHeights = newFontHeights;
	}

	if (columnCount > this.accessibles.length) {
		final Accessible[] newAccessibles = new Accessible [columnCount];
		System.arraycopy (this.accessibles, 0, newAccessibles, 0, index);
		System.arraycopy (this.accessibles, index, newAccessibles, index + 1, columnCount - index - 1);
		this.accessibles = newAccessibles;
	}

	if ((index == 0) && (columnCount > 1)) {
		/*
		 * The new second column may have more width available to it than it did when it was
		 * the first column if checkboxes are being shown, so recompute its displayText if needed.
		 */
		if ((this.parent.getStyle () & SWT.CHECK) != 0) {
			final GC gc = new GC (this.parent);
			gc.setFont (this.getFont (1, false));
			this.computeDisplayText (1, gc);
			gc.dispose ();
		}
	}
}
static CTable checkNull (final CTable table) {
	if (table == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	return table;
}
void clear () {
	this.checked = this.grayed = false;
	this.texts = null;
	this.textWidths = new int [1];
	this.fontHeight = 0;
	this.fontHeights = null;
	this.images = null;
	this.foreground = this.background = null;
	this.displayTexts = null;
	this.cellForegrounds = this.cellBackgrounds = null;
	this.font = null;
	this.cellFonts = null;
	this.cached = false;
	super.setText ("");
	super.setImage (null);

	final int columnCount = this.parent.columns.length;
	this.disposeAccessibles();
	this.accessibles = new Accessible [columnCount > 0 ? columnCount : 1];
	if (columnCount > 0) {
		this.displayTexts = new String [columnCount];
		if (columnCount > 1) {
			this.texts = new String [columnCount];
			this.textWidths = new int [columnCount];
			this.images = new Image [columnCount];
		}
	}
}
void computeDisplayText (final int columnIndex, final GC gc) {
	if (((this.parent.getStyle () & SWT.VIRTUAL) != 0) && !this.cached) {
    return;	/* nothing to do */
  }

	final int columnCount = this.parent.columns.length;
	if (columnCount == 0) {
		final String text = this.getText (0, false);
		this.textWidths [columnIndex] = gc.stringExtent (text).x;
		return;
	}

	final CTableColumn column = this.parent.columns [columnIndex];
	int availableWidth = column.width - (2 * this.parent.getCellPadding ()) - (2 * MARGIN_TEXT);
	if (columnIndex == 0) {
		availableWidth -= this.parent.col0ImageWidth;
		if (this.parent.col0ImageWidth > 0) {
      availableWidth -= CTable.MARGIN_IMAGE;
    }
		if ((this.parent.getStyle () & SWT.CHECK) != 0) {
			availableWidth -= this.parent.checkboxBounds.width;
			availableWidth -= CTable.MARGIN_IMAGE;
		}
	} else {
		final Image image = this.getImage (columnIndex, false);
		if (image != null) {
			availableWidth -= image.getBounds ().width;
			availableWidth -= CTable.MARGIN_IMAGE;
		}
	}

	String text = this.getText (columnIndex, false);
	int textWidth = gc.stringExtent (text).x;
	if (textWidth <= availableWidth) {
		this.displayTexts [columnIndex] = text;
		this.textWidths [columnIndex] = textWidth;
		return;
	}

	/* Ellipsis will be needed, so subtract their width from the available text width */
	final int ellipsisWidth = gc.stringExtent (CTable.ELLIPSIS).x;
	availableWidth -= ellipsisWidth;
	if (availableWidth <= 0) {
		this.displayTexts [columnIndex] = CTable.ELLIPSIS;
		this.textWidths [columnIndex] = ellipsisWidth;
		return;
	}

	/* Make initial guess. */
	int index = (int) Math.min (availableWidth / gc.getFontMetrics ().getAverageCharacterWidth (), text.length ());
	textWidth = gc.stringExtent (text.substring (0, index)).x;

	/* Initial guess is correct. */
	if (availableWidth == textWidth) {
		this.displayTexts [columnIndex] = text.substring (0, index) + CTable.ELLIPSIS;
		this.textWidths [columnIndex] = textWidth + ellipsisWidth;
		return;
	}

	/* Initial guess is too high, so reduce until fit is found. */
	if (availableWidth < textWidth) {
		do {
			index--;
			if (index < 0) {
				this.displayTexts [columnIndex] = CTable.ELLIPSIS;
				this.textWidths [columnIndex] = ellipsisWidth;
				return;
			}
			text = text.substring (0, index);
			textWidth = gc.stringExtent (text).x;
		} while (availableWidth < textWidth);
		this.displayTexts [columnIndex] = text + CTable.ELLIPSIS;
		this.textWidths [columnIndex] = textWidth + ellipsisWidth;
		return;
	}

	/* Initial guess is too low, so increase until overrun is found. */
	int previousWidth = 0;
	while (textWidth < availableWidth) {
		index++;
		previousWidth = textWidth;
		textWidth = gc.stringExtent (text.substring (0, index)).x;
	}
	this.displayTexts [columnIndex] = text.substring (0, index - 1) + CTable.ELLIPSIS;
	this.textWidths [columnIndex] = previousWidth + ellipsisWidth;
}
void computeDisplayTexts (final GC gc) {
	if (((this.parent.getStyle () & SWT.VIRTUAL) != 0) && !this.cached) {
    return;	/* nothing to do */
  }

	final int columnCount = this.parent.columns.length;
	if (columnCount == 0) {
    return;
  }

	for (int i = 0; i < columnCount; i++) {
		gc.setFont (this.getFont (i, false));
		this.computeDisplayText (i, gc);
	}
}
/*
 * Computes the cached text widths.
 */
void computeTextWidths (final GC gc) {
	if (((this.parent.getStyle () & SWT.VIRTUAL) != 0) && !this.cached) {
    return;	/* nothing to do */
  }

	final int validColumnCount = Math.max (1, this.parent.columns.length);
	this.textWidths = new int [validColumnCount];
	for (int i = 0; i < this.textWidths.length; i++) {
		final String value = this.getDisplayText (i);
		if (value != null) {
			gc.setFont (this.getFont (i, false));
			this.textWidths [i] = gc.stringExtent (value).x;
		}
	}
}
@Override
public void dispose () {
	if (this.isDisposed ()) {
    return;
  }
	final CTable parent = this.parent;
	final int startIndex = this.index;
	final int endIndex = parent.itemsCount - 1;
	this.dispose (true);
	parent.redrawItems (startIndex, endIndex, false);
}
void dispose (final boolean notifyParent) {
	if (this.isDisposed ()) {
    return;
  }
	if (notifyParent) {
    this.parent.destroyItem (this);
  }
	super.dispose ();	/* super is intentional here */
	this.background = this.foreground = null;
	this.cellBackgrounds = this.cellForegrounds = null;
	this.font = null;
	this.cellFonts = null;
	this.images = null;
	this.texts = this.displayTexts = null;
	this.textWidths = this.fontHeights = null;
	this.disposeAccessibles();
	this.parent = null;
}
void disposeAccessibles() {
	if (this.accessibles != null) {
		for (final Accessible accessible : this.accessibles) {
			if (accessible != null) {
				accessible.dispose();
			}
		}
		this.accessibles = null;
	}
}
/* Returns the cell accessible for the specified column index in the receiver. */
Accessible getAccessible(final Accessible accessibleTable, final int columnIndex) {
	if (this.accessibles [columnIndex] == null) {
		final Accessible accessible = new Accessible(accessibleTable);
		accessible.addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(final AccessibleEvent e) {
				e.result = CTableItem.this.getText(columnIndex);
				System.out.println("tableItem getName = " + e.result);
			}
		});
		accessible.addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getChild(final AccessibleControlEvent e) {
				/* CTable cells do not have children, so just return the index in parent. */
				switch (e.childID) {
					case ACC.CHILDID_CHILD_INDEX:
						e.detail = (CTableItem.this.index * Math.max(1, CTableItem.this.parent.getColumnCount())) + columnIndex + CTableItem.this.parent.getColumnCount();
						break;
				}
			}
			@Override
			public void getChildAtPoint(final AccessibleControlEvent e) {
				final Point point = CTableItem.this.parent.toControl(e.x, e.y);
				if (CTableItem.this.getBounds(columnIndex).contains(point)) {
					e.childID = ACC.CHILDID_SELF;
				} else {
					e.childID = ACC.CHILDID_NONE;
				}
			}
			@Override
			public void getFocus(final AccessibleControlEvent e) {
				e.childID = ((CTableItem.this.parent.focusItem == CTableItem.this) && CTableItem.this.parent.isFocusControl()) ?
						ACC.CHILDID_SELF : ACC.CHILDID_NONE;
			}
			@Override
			public void getLocation(final AccessibleControlEvent e) {
				final Rectangle location = CTableItem.this.getBounds(columnIndex);
				final Point pt = CTableItem.this.parent.toDisplay(location.x, location.y);
				e.x = pt.x;
				e.y = pt.y;
				e.width = location.width;
				e.height = location.height;
			}
			@Override
			public void getRole(final AccessibleControlEvent e) {
				e.detail = ACC.ROLE_TABLECELL;
			}
			@Override
			public void getValue(final AccessibleControlEvent e) {
				e.result = CTableItem.this.getText(columnIndex);
			}
		});
		accessible.addAccessibleTableCellListener(new AccessibleTableCellListener() {
			@Override
			public void getColumnHeaders(final AccessibleTableCellEvent e) {
				if (CTableItem.this.parent.columns.length == 0) {
					/* The CTable is being used as a list, and there are no headers. */
					e.accessibles = null;
				} else {
					/* CTable cells only occupy one column. */
					final CTableColumn column = CTableItem.this.parent.columns [columnIndex];
					e.accessibles = new Accessible[] {column.getAccessible (accessibleTable)};
				}
			}
			@Override
			public void getColumnIndex(final AccessibleTableCellEvent e) {
				e.index = columnIndex;
			}
			@Override
			public void getColumnSpan(final AccessibleTableCellEvent e) {
				/* CTable cells only occupy one column. */
				e.count = 1;
			}
			@Override
			public void getRowHeaders(final AccessibleTableCellEvent e) {
				/* CTable does not support row headers. */
			}
			@Override
			public void getRowIndex(final AccessibleTableCellEvent e) {
				e.index = CTableItem.this.index;
			}
			@Override
			public void getRowSpan(final AccessibleTableCellEvent e) {
				/* CTable cells only occupy one row. */
				e.count = 1;
			}
			@Override
			public void getTable(final AccessibleTableCellEvent e) {
				e.accessible = accessibleTable;
			}
			@Override
			public void isSelected(final AccessibleTableCellEvent e) {
				e.isSelected = CTableItem.this.isSelected();
			}
		});
		this.accessibles [columnIndex] = accessible;
	}
	return this.accessibles [columnIndex];
}
/**
 * Returns the receiver's background color.
 *
 * @return the background color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 2.0
 */
public Color getBackground () {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	if (this.background != null) {
    return this.background;
  }
	return this.parent.getBackground ();
}
/**
 * Returns the background color at the given column index in the receiver.
 *
 * @param index the column index
 * @return the background color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public Color getBackground (final int columnIndex) {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount)) || (this.cellBackgrounds == null) || (this.cellBackgrounds [columnIndex] == null)) {
    return this.getBackground ();
  }
	return this.cellBackgrounds [columnIndex];
}
/**
 * Returns a rectangle describing the receiver's size and location
 * relative to its parent.
 *
 * @return the receiver's bounding rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.2
 */
public Rectangle getBounds () {
	this.checkWidget ();
	return this.getBounds (true);
}
Rectangle getBounds (final boolean checkData) {
	if (checkData && !this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final int x = this.getTextX (0);
	int width = this.textWidths [0] + (2 * MARGIN_TEXT);
	if (this.parent.columns.length > 0) {
		final CTableColumn column = this.parent.columns [0];
		final int right = column.getX () + column.width;
		if ((x + width) > right) {
			width = Math.max (0, right - x);
		}
	}
	return new Rectangle (x, this.parent.getItemY (this), width, this.parent.itemHeight);
}
/**
 * Returns a rectangle describing the receiver's size and location
 * relative to its parent at a column in the table.
 *
 * @param index the index that specifies the column
 * @return the receiver's bounding column rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Rectangle getBounds (final int columnIndex) {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final CTableColumn[] columns = this.parent.columns;
	final int columnCount = columns.length;
	final int validColumnCount = Math.max (1, columnCount);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
		return new Rectangle (0, 0, 0, 0);
	}
	/*
	 * If there are no columns then this is the bounds of the receiver's content.
	 */
	if (columnCount == 0) {
		final int width = this.getContentWidth (0);
		return new Rectangle (
			this.getContentX (0),
			this.parent.getItemY (this),
			width,
			this.parent.itemHeight - 1);
	}

	final CTableColumn column = columns [columnIndex];
	if (columnIndex == 0) {
		/*
		 * For column 0 this is bounds from the beginning of the content to the
		 * end of the column.
		 */
		final int x = this.getContentX (0);
		final int offset = x - column.getX ();
		final int width = Math.max (0, column.width - offset - 1);		/* max is for columns with small widths */
		return new Rectangle (x, this.parent.getItemY (this) + 1, width, this.parent.itemHeight - 1);
	}
	/*
	 * For columns > 0 this is the bounds of the table cell.
	 */
	return new Rectangle (column.getX (), this.parent.getItemY (this) + 1, column.width, this.parent.itemHeight - 1);
}
/*
 * Returns the full bounds of a cell in a table, regardless of its content.
 */
Rectangle getCellBounds (final int columnIndex) {
	final int y = this.parent.getItemY (this);
	if (this.parent.columns.length == 0) {
		int width;
		if (this.customWidth != -1) {
			width = this.getContentX (0) + this.customWidth + this.parent.horizontalOffset;
		} else {
			final int textPaintWidth = this.textWidths [0] + (2 * MARGIN_TEXT);
			width = this.getTextX (0) + textPaintWidth + this.parent.horizontalOffset;
		}
		return new Rectangle (-this.parent.horizontalOffset, y, width, this.parent.itemHeight);
	}
	final CTableColumn column = this.parent.columns [columnIndex];
	return new Rectangle (column.getX (), y, column.width, this.parent.itemHeight);
}
/*
 * Returns the bounds of the receiver's checkbox, or null if the parent's style does not
 * include SWT.CHECK.
 */
Rectangle getCheckboxBounds () {
	if ((this.parent.getStyle () & SWT.CHECK) == 0) {
    return null;
  }
	final Rectangle result = this.parent.checkboxBounds;
	if (this.parent.columns.length == 0) {
		result.x = this.parent.getCellPadding () - this.parent.horizontalOffset;
	} else {
		result.x = this.parent.columns [0].getX () + this.parent.getCellPadding ();
	}
	result.y = this.parent.getItemY (this) + ((this.parent.itemHeight - result.height) / 2);
	return result;
}
/**
 * Returns <code>true</code> if the receiver is checked,
 * and false otherwise.  When the parent does not have
 * the <code>CHECK</code> style, return false.
 *
 * @return the checked state of the checkbox
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getChecked () {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	return this.checked;
}
int getContentWidth (final int columnIndex) {
	int width = this.textWidths [columnIndex] + (2 * MARGIN_TEXT);
	if (columnIndex == 0) {
		width += this.parent.col0ImageWidth;
		if (this.parent.col0ImageWidth > 0) {
      width += CTable.MARGIN_IMAGE;
    }
	} else {
		final Image image = this.getImage (columnIndex, false);
		if (image != null) {
			width += image.getBounds ().width + CTable.MARGIN_IMAGE;
		}
	}
	return width;
}
/*
 * Returns the x value where the receiver's content (ie.- its image or text) begins
 * for the specified column.
 */
int getContentX (final int columnIndex) {
	int minX = this.parent.getCellPadding ();
	if (columnIndex == 0) {
		final Rectangle checkboxBounds = this.getCheckboxBounds ();
		if (checkboxBounds != null) {
			minX += checkboxBounds.width + CTable.MARGIN_IMAGE;
		}
	}

	if (this.parent.columns.length == 0) {
    return minX - this.parent.horizontalOffset;	/* free first column */
  }

	final CTableColumn column = this.parent.columns [columnIndex];
	final int columnX = column.getX ();
	if ((column.getStyle () & SWT.LEFT) != 0) {
    return columnX + minX;
  }

	/* column is not left-aligned */
	final int contentWidth = this.getContentWidth (columnIndex);
	int contentX = 0;
	if ((column.getStyle () & SWT.RIGHT) != 0) {
		contentX = column.width - this.parent.getCellPadding () - contentWidth;
	} else {	/* SWT.CENTER */
		contentX = (column.width - contentWidth) / 2;
	}
	return Math.max (columnX + minX, columnX + contentX);
}
String getDisplayText (final int columnIndex) {
	if (this.parent.columns.length == 0) {
    return this.getText (0, false);
  }
	final String result = this.displayTexts [columnIndex];
	return result != null ? result : "";	//$NON-NLS-1$
}
/*
 * Returns the bounds that should be used for drawing a focus rectangle on the receiver
 */
Rectangle getFocusBounds () {
	int x = 0;
	final CTableColumn[] columns = this.parent.columns;
	final int[] columnOrder = this.parent.getColumnOrder ();
	if ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0) {
		final int col0index = columnOrder.length == 0 ? 0 : columnOrder [0];
		if (col0index == 0) {
			x = this.getTextX (0);
		} else {
			x = -this.parent.horizontalOffset;
		}
	} else {
		x = this.getTextX (0);
	}

	if (columns.length > 0) {
		/* ensure that the focus x does not start beyond the right bound of column 0 */
		final int rightX = columns [0].getX () + columns [0].width;
		x = Math.min (x, rightX - 1);
	}

	int width;
	if (columns.length == 0) {
		if (this.customWidth != -1) {
			width = this.customWidth;
		} else {
			width = this.textWidths [0] + (2 * MARGIN_TEXT);
		}
	} else {
		CTableColumn column;
		if ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0) {
			column = columns [columnOrder [columnOrder.length - 1]];
		} else {
			column = columns [0];
		}
		width = (column.getX () + column.width) - x - 1;
	}
	return new Rectangle (
		x,
		this.parent.getItemY (this) + (this.parent.linesVisible ? 1 : 0),
		width,
		this.parent.itemHeight - (this.parent.linesVisible ? 1 : 0));
}
/**
 * Returns the font that the receiver will use to paint textual information for this item.
 *
 * @return the receiver's font
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public Font getFont () {
	this.checkWidget ();
	return this.getFont (true);
}
Font getFont (final boolean checkData) {
	if (checkData && !this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	if (this.font != null) {
    return this.font;
  }
	return this.parent.getFont ();
}
/**
 * Returns the font that the receiver will use to paint textual information
 * for the specified cell in this item.
 *
 * @param index the column index
 * @return the receiver's font
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public Font getFont (final int columnIndex) {
	this.checkWidget ();
	return this.getFont (columnIndex, true);
}
Font getFont (final int columnIndex, final boolean checkData) {
	if (checkData && !this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount)) || (this.cellFonts == null) || (this.cellFonts [columnIndex] == null)) {
    return this.getFont (checkData);
  }
	return this.cellFonts [columnIndex];
}
int getFontHeight () {
	if (this.fontHeight != 0) {
    return this.fontHeight;
  }
	return this.parent.fontHeight;
}
int getFontHeight (final int columnIndex) {
	if ((this.fontHeights == null) || (this.fontHeights [columnIndex] == 0)) {
    return this.getFontHeight ();
  }
	return this.fontHeights [columnIndex];
}
/**
 * Returns the foreground color that the receiver will use to draw.
 *
 * @return the receiver's foreground color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 2.0
 */
public Color getForeground () {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	if (this.foreground != null) {
    return this.foreground;
  }
	return this.parent.getForeground ();
}
/**
 *
 * Returns the foreground color at the given column index in the receiver.
 *
 * @param index the column index
 * @return the foreground color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public Color getForeground (final int columnIndex) {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount)) || (this.cellForegrounds == null) || (this.cellForegrounds [columnIndex] == null)) {
    return this.getForeground ();
  }
	return this.cellForegrounds [columnIndex];
}
/**
 * Returns <code>true</code> if the receiver is grayed,
 * and false otherwise. When the parent does not have
 * the <code>CHECK</code> style, return false.
 *
 * @return the grayed state of the checkbox
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getGrayed () {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	return this.grayed;
}
/*
 * Returns the bounds representing the clickable region that should select the receiver.
 */
Rectangle getHitBounds () {
	final int[] columnOrder = this.parent.getColumnOrder ();
	int contentX = 0;
	if ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0) {
		final int col0index = columnOrder.length == 0 ? 0 : columnOrder [0];
		if (col0index == 0) {
			contentX = this.getContentX (0);
		} else {
			contentX = 0;
		}
	} else {
		contentX = this.getContentX (0);
	}

	int width = 0;
	final CTableColumn[] columns = this.parent.columns;
	if (columns.length == 0) {
		width = this.getContentWidth (0);
	} else {
		/*
		 * If there are columns then this spans from the beginning of the receiver's column 0
		 * image or text to the end of either column 0 or the last column (FULL_SELECTION).
		 */
		CTableColumn column;
		if ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0) {
			column = columns [columnOrder [columnOrder.length - 1]];
		} else {
			column = columns [0];
		}
		width = (column.getX () + column.width) - contentX;
	}
	return new Rectangle (contentX, this.parent.getItemY (this), width, this.parent.itemHeight);
}
@Override
public Image getImage () {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	return super.getImage ();
}
/**
 * Returns the image stored at the given column index in the receiver,
 * or null if the image has not been set or if the column does not exist.
 *
 * @param index the column index
 * @return the image stored at the given column index in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Image getImage (final int columnIndex) {
	this.checkWidget ();
	return this.getImage (columnIndex, true);
}
Image getImage (final int columnIndex, final boolean checkData) {
	if (checkData && !this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
    return null;
  }
	if (columnIndex == 0) {
    return super.getImage ();		/* super is intentional here */
  }
	return this.images [columnIndex];
}
/**
 * Returns a rectangle describing the size and location
 * relative to its parent of an image at a column in the
 * table.  An empty rectangle is returned if index exceeds
 * the index of the table's last column.
 *
 * @param index the index that specifies the column
 * @return the receiver's bounding image rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Rectangle getImageBounds (final int columnIndex) {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
    return new Rectangle (0,0,0,0);
  }

	final int padding = this.parent.getCellPadding ();
	final int startX = this.getContentX (columnIndex);
	final int itemHeight = this.parent.itemHeight;
	final int imageSpaceY = itemHeight - (2 * padding);
	final int y = this.parent.getItemY (this);
	final Image image = this.getImage (columnIndex, false);
	int drawWidth = 0;
	if (columnIndex == 0) {
		/* for column 0 all images have the same width */
		drawWidth = this.parent.col0ImageWidth;
	} else if (image != null) {
    drawWidth = image.getBounds ().width;
  }
	return new Rectangle (startX, y + padding, drawWidth, imageSpaceY);
}
/**
 * Gets the image indent.
 *
 * @return the indent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getImageIndent () {
	this.checkWidget();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	return this.imageIndent;	// TODO
}
@Override
public String toString () {
	if (!this.isDisposed () && ((this.parent.getStyle () & SWT.VIRTUAL) != 0) && !this.cached) {
		return "CTableItem {*virtual*}"; //$NON-NLS-1$
	}
	return super.toString ();
}
/**
 * Returns the receiver's parent, which must be a <code>Table</code>.
 *
 * @return the receiver's parent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public CTable getParent () {
	this.checkWidget ();
	return this.parent;
}
/*
 * Returns the receiver's ideal width for the specified columnIndex.
 */
int getPreferredWidth (final int columnIndex) {
	int width = 0;
	final GC gc = new GC (this.parent);
	gc.setFont (this.getFont (columnIndex, false));
	width += gc.stringExtent (this.getText (columnIndex, false)).x + (2 * MARGIN_TEXT);
	if (columnIndex == 0) {
		if (this.parent.col0ImageWidth > 0) {
			width += this.parent.col0ImageWidth;
			width += CTable.MARGIN_IMAGE;
		}
	} else {
		final Image image = this.getImage (columnIndex, false);
		if (image != null) {
			width += image.getBounds ().width;
			width += CTable.MARGIN_IMAGE;
		}
	}

	if (this.parent.isListening (SWT.MeasureItem)) {
		final Event event = new Event ();
		event.item = this;
		event.gc = gc;
		event.index = columnIndex;
		event.x = this.getContentX (columnIndex);
		event.y = this.parent.getItemY (this);
		event.width = width;
		event.height = this.parent.itemHeight;
		this.parent.notifyListeners (SWT.MeasureItem, event);
		if (this.parent.itemHeight != event.height) {
			this.parent.customHeightSet = true;
			final boolean update = this.parent.setItemHeight (event.height + (2 * this.parent.getCellPadding ()));
			if (update) {
        this.parent.redraw ();
      }
		}
		width = event.width;
	}

	gc.dispose ();
	if ((columnIndex == 0) && ((this.parent.getStyle () & SWT.CHECK) != 0)) {
		width += this.parent.checkboxBounds.width;
		width += CTable.MARGIN_IMAGE;
	}
	return width + (2 * this.parent.getCellPadding ());
}
@Override
public String getText () {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	return super.getText ();
}
/**
 * Returns the text stored at the given column index in the receiver,
 * or empty string if the text has not been set.
 *
 * @param index the column index
 * @return the text stored at the given column index in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText (final int columnIndex) {
	this.checkWidget ();
	return this.getText (columnIndex, true);
}
String getText (final int columnIndex, final boolean checkData) {
	if (checkData && !this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount)))
   {
    return "";	//$NON-NLS-1$
  }
	if (columnIndex == 0) {
    return super.getText (); /* super is intentional here */
  }
	if (this.texts [columnIndex] == null)
   {
    return "";	//$NON-NLS-1$
  }
	return this.texts [columnIndex];
}
/**
 * Returns a rectangle describing the size and location
 * relative to its parent of the text at a column in the
 * table.  An empty rectangle is returned if index exceeds
 * the index of the table's last column.
 *
 * @param index the index that specifies the column
 * @return the receiver's bounding text rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.3
 */
public Rectangle getTextBounds (final int columnIndex) {
	this.checkWidget ();
	if (!this.parent.checkData (this, true)) {
    SWT.error (SWT.ERROR_WIDGET_DISPOSED);
  }
	final CTableColumn[] columns = this.parent.columns;
	final int columnCount = columns.length;
	final int validColumnCount = Math.max (1, columnCount);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
		return new Rectangle (0, 0, 0, 0);
	}
	/*
	 * If there are no columns then this is the bounds of the receiver's content,
	 * starting from the text.
	 */
	if (columnCount == 0) {
		final int x = this.getTextX (0) + MARGIN_TEXT;
		final int width = Math.max (0, (this.getContentX(0) + this.getContentWidth (0)) - x);
		return new Rectangle (
			x,
			this.parent.getItemY (this),
			width,
			this.parent.itemHeight - 1);
	}

	final CTableColumn column = columns [columnIndex];
	if (columnIndex == 0) {
		/*
		 * For column 0 this is bounds from the beginning of the content to the
		 * end of the column, starting from the text.
		 */
		final int x = this.getTextX (0) + MARGIN_TEXT;
		final int offset = x - column.getX ();
		final int width = Math.max (0, column.width - offset - 1);		/* max is for columns with small widths */
		return new Rectangle (x, this.parent.getItemY (this) + 1, width, this.parent.itemHeight - 1);
	}
	/*
	 * For columns > 0 this is the bounds of the table cell, starting from the text.
	 */
	final int x = this.getTextX (columnIndex) + MARGIN_TEXT;
	final int offset = x - column.getX ();
	final int width = Math.max (0, column.width - offset - MARGIN_TEXT);
	return new Rectangle (x, this.parent.getItemY (this) + 1, width, this.parent.itemHeight - 1);
}
/*
 * Returns the x value where the receiver's text begins.
 */
int getTextX (final int columnIndex) {
	int textX = this.getContentX (columnIndex);
	if (columnIndex == 0) {
		textX += this.parent.col0ImageWidth;
		if (this.parent.col0ImageWidth > 0) {
      textX += CTable.MARGIN_IMAGE;
    }
	} else {
		final Image image = this.getImage (columnIndex, false);
		if (image != null) {
			textX += image.getBounds ().width + CTable.MARGIN_IMAGE;
		}
	}
	return textX;
}
/*
 * Answers a boolean indicating whether the receiver's y is within the current
 * viewport of the parent.
 */
boolean isInViewport () {
	final int topIndex = this.parent.topIndex;
	if (this.index < topIndex) {
    return false;
  }
	final int visibleCount = this.parent.clientArea.height / this.parent.itemHeight;
	return this.index <= (topIndex + visibleCount);
}
boolean isSelected () {
	return this.parent.getSelectionIndex (this) != -1;
}
/*
 * The backgroundOnly argument indicates whether the item should only
 * worry about painting its background color and selection.
 *
 * Returns a boolean indicating whether to abort drawing focus on the item.
 * If the receiver is not the current focus item then this value is irrelevant.
 */
boolean paint (final GC gc, final CTableColumn column, final boolean backgroundOnly) {
	if (!this.parent.checkData (this, true)) {
    return false;
  }
	int columnIndex = 0, x = 0;
	if (column != null) {
		columnIndex = column.getIndex ();
		x = column.getX ();
	}

	/*
	 * Capture GC attributes that will need to be restored later in the paint
	 * process to ensure that the item paints as intended without being affected
	 * by GC changes made in MeasureItem/EraseItem/PaintItem callbacks.
	 */
	final int oldAlpha = gc.getAlpha ();
	final boolean oldAdvanced = gc.getAdvanced ();
	final int oldAntialias = gc.getAntialias ();
	final Pattern oldBackgroundPattern = gc.getBackgroundPattern ();
	final Pattern oldForegroundPattern = gc.getForegroundPattern ();
	final int oldInterpolation = gc.getInterpolation ();
	final int oldTextAntialias = gc.getTextAntialias ();

	if (this.parent.isListening (SWT.MeasureItem)) {
		final int contentWidth = this.getContentWidth (columnIndex);
		final int contentX = this.getContentX (columnIndex);
		gc.setFont (this.getFont (columnIndex, false));
		final Event event = new Event ();
		event.item = this;
		event.gc = gc;
		event.index = columnIndex;
		event.x = contentX;
		event.y = this.parent.getItemY (this);
		event.width = contentWidth;
		event.height = this.parent.itemHeight;
		this.parent.notifyListeners (SWT.MeasureItem, event);
		event.gc = null;
		if (gc.isDisposed ()) {
      return false;
    }
		gc.setAlpha (oldAlpha);
		gc.setAntialias (oldAntialias);
		gc.setBackgroundPattern (oldBackgroundPattern);
		gc.setForegroundPattern (oldForegroundPattern);
		gc.setInterpolation (oldInterpolation);
		gc.setTextAntialias (oldTextAntialias);
		gc.setAdvanced (oldAdvanced);
		if (this.isDisposed ()) {
      return false;
    }
		if (this.parent.itemHeight != event.height) {
			this.parent.customHeightSet = true;
			final boolean update = this.parent.setItemHeight (event.height + (2 * this.parent.getCellPadding ()));
			if (update) {
        this.parent.redraw ();
      }
		}
		if (this.parent.columns.length == 0) {
			final int change = event.width - (this.customWidth != -1 ? this.customWidth : contentWidth);
			if ((event.width != contentWidth) || (this.customWidth != -1)) {
        this.customWidth = event.width;
      }
			if (change != 0) {	/* scrollbar may be affected since no columns */
				this.parent.updateHorizontalBar (contentX + event.width, change);
				// TODO what if clip is too small now?
			}
		}
	}

	/* if this cell is completely to the right of the client area then there's no need to paint it */
	final Rectangle clientArea = this.parent.clientArea;
	if ((clientArea.x + clientArea.width) < x) {
    return false;
  }

	final Rectangle cellBounds = this.getCellBounds (columnIndex);
	if (this.parent.linesVisible) {
		cellBounds.y++;
		cellBounds.height--;
	}
	int cellRightX = 0;
	if (column != null) {
		cellRightX = column.getX () + column.width;
	} else {
		cellRightX = cellBounds.x + cellBounds.width;
	}

	/* restrict the clipping region to the cell */
	gc.setClipping (x, cellBounds.y, clientArea.width - x, cellBounds.height);

	final int y = this.parent.getItemY (this);
	final int itemHeight = this.parent.itemHeight;

	/* draw the parent background color/image of this cell */
	if (column == null) {
		gc.fillRectangle (0, y, clientArea.width, itemHeight);
		//parent.drawBackground (gc, 0, y, clientArea.width, itemHeight);
	} else {
		int fillWidth = cellBounds.width;
		if (this.parent.linesVisible) {
      fillWidth--;
    }
		gc.fillRectangle (cellBounds.x, cellBounds.y, fillWidth, cellBounds.height);
		//parent.drawBackground (gc, cellBounds.x, cellBounds.y, fillWidth, cellBounds.height);
	}

	final boolean isSelected = this.isSelected ();
	final boolean isFocusItem = (this.parent.focusItem == this) && this.parent.isFocusControl ();
	boolean drawBackground = true;
	boolean drawForeground = true;
	boolean drawSelection = isSelected;
	boolean drawFocus = isFocusItem;
	if (this.parent.isListening (SWT.EraseItem)) {
		drawBackground = (this.background != null) || ((this.cellBackgrounds != null) && (this.cellBackgrounds [columnIndex] != null));
		gc.setFont (this.getFont (columnIndex, false));
		if (isSelected && ((columnIndex == 0) || ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0))) {
			gc.setForeground (this.display.getSystemColor (SWT.COLOR_LIST_SELECTION_TEXT));
			gc.setBackground (this.display.getSystemColor (SWT.COLOR_LIST_SELECTION));
		} else {
			gc.setForeground (this.getForeground (columnIndex));
			gc.setBackground (this.getBackground (columnIndex));
		}
		final Event event = new Event ();
		event.item = this;
		event.gc = gc;
		event.index = columnIndex;
		event.doit = true;
		event.detail = SWT.FOREGROUND;
		if (drawBackground) {
      event.detail |= SWT.BACKGROUND;
    }
		if (isSelected) {
      event.detail |= SWT.SELECTED;
    }
		if (isFocusItem) {
      event.detail |= SWT.FOCUSED;
    }
		event.x = cellBounds.x;
		event.y = cellBounds.y;
		event.width = cellBounds.width;
		event.height = cellBounds.height;
		gc.setClipping (cellBounds);
		this.parent.notifyListeners (SWT.EraseItem, event);
		event.gc = null;
		if (gc.isDisposed ()) {
      return false;
    }
		gc.setAlpha (oldAlpha);
		gc.setAntialias (oldAntialias);
		gc.setBackgroundPattern (oldBackgroundPattern);
		gc.setClipping (cellBounds);
		gc.setForegroundPattern (oldForegroundPattern);
		gc.setInterpolation (oldInterpolation);
		gc.setTextAntialias (oldTextAntialias);
		gc.setAdvanced (oldAdvanced);
		if (this.isDisposed ()) {
      return false;
    }
		if (!event.doit) {
			drawBackground = drawForeground = drawSelection = drawFocus = false;
		} else {
			drawBackground = drawBackground && ((event.detail & SWT.BACKGROUND) != 0);
			drawForeground = (event.detail & SWT.FOREGROUND) != 0;
			drawSelection = isSelected && ((event.detail & SWT.SELECTED) != 0);
			drawFocus = isFocusItem && ((event.detail & SWT.FOCUSED) != 0);
		}
	}

	/* draw the cell's set background if appropriate */
	if (drawBackground) {
		gc.setBackground (this.getBackground (columnIndex));
		if ((columnIndex == 0) && ((column == null) || (column.getOrderIndex () == 0))) {
			final Rectangle focusBounds = this.getFocusBounds ();
			int fillWidth = 0;
			if (column == null) {
				fillWidth = focusBounds.width;
			} else {
				fillWidth = column.width - focusBounds.x;
				if (this.parent.linesVisible) {
          fillWidth--;
        }
			}
			gc.fillRectangle (focusBounds.x, focusBounds.y, fillWidth, focusBounds.height);
		} else {
			final int fillWidth = cellBounds.width;
			gc.fillRectangle (cellBounds.x, cellBounds.y, fillWidth, cellBounds.height);
		}
	}

	/* draw the selection bar if the receiver is selected */
	if (drawSelection && ((columnIndex == 0) || ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0))) {
		if (this.parent.isFocusControl () || ((this.parent.getStyle () & SWT.HIDE_SELECTION) == 0)) {
			gc.setBackground (this.display.getSystemColor (SWT.COLOR_LIST_SELECTION));
			if (columnIndex == 0) {
				final Rectangle focusBounds = this.getFocusBounds ();
				int startX, fillWidth;
				if ((column == null) || (column.getOrderIndex () == 0) || ((this.parent.getStyle () & SWT.FULL_SELECTION) == 0)) {
					startX = focusBounds.x + 1;		/* space for left bound of focus rect */
				} else {
					startX = column.getX ();
				}
				if (column == null) {
					fillWidth = focusBounds.width - 2;
				} else {
					fillWidth = (column.getX () + column.width) - startX;
					if ((column.getOrderIndex () == (this.parent.columns.length - 1)) || ((this.parent.getStyle () & SWT.FULL_SELECTION) == 0)) {
						fillWidth -= 2;	/* space for right bound of focus rect */
					}
				}
				if (fillWidth > 0) {
					gc.fillRectangle (startX, focusBounds.y + 1, fillWidth, focusBounds.height - 2);
				}
			} else {
				int fillWidth = column.width;
				if (column.getOrderIndex () == 0) {
					fillWidth -= 1;
				}
				if (column.getOrderIndex () == (this.parent.columns.length - 1)) {
					fillWidth -= 2;		/* space for right bound of focus rect */
				}
				if (fillWidth > 0) {
					gc.fillRectangle (
						column.getX (),
						cellBounds.y + 1,
						fillWidth,
						cellBounds.height - 2);
				}
			}
		}
	}

	if (backgroundOnly) {
    return false;
  }

	/* Draw checkbox if drawing column 0 and parent has style SWT.CHECK */
	if ((columnIndex == 0) && ((this.parent.getStyle () & SWT.CHECK) != 0)) {
		final Image baseImage = this.grayed ? this.parent.getGrayUncheckedImage () : this.parent.getUncheckedImage ();
		final Rectangle checkboxBounds = this.getCheckboxBounds ();
		gc.drawImage (baseImage, checkboxBounds.x, checkboxBounds.y);
		/* Draw checkmark if item is checked */
		if (this.checked) {
			final Image checkmarkImage = this.parent.getCheckmarkImage ();
			final Rectangle checkmarkBounds = checkmarkImage.getBounds ();
			final int xInset = (checkboxBounds.width - checkmarkBounds.width) / 2;
			final int yInset = (checkboxBounds.height - checkmarkBounds.height) / 2;
			gc.drawImage (checkmarkImage, checkboxBounds.x + xInset, checkboxBounds.y + yInset);
		}
	}

	if (drawForeground) {
		final Image image = this.getImage (columnIndex, false);
		final String text = this.getDisplayText (columnIndex);
		final Rectangle imageArea = this.getImageBounds (columnIndex);
		final int startX = imageArea.x;

		/* while painting the cell's content restrict the clipping region */
		final int padding = this.parent.getCellPadding ();
		gc.setClipping (
			startX,
			(cellBounds.y + padding) - (this.parent.linesVisible ? 1 : 0),
			cellRightX - startX - padding,
			cellBounds.height - (2 * (padding - (this.parent.linesVisible ? 1 : 0))));

		/* draw the image */
		if (image != null) {
			final Rectangle imageBounds = image.getBounds ();
			gc.drawImage (
				image,
				0, 0,									/* source x, y */
				imageBounds.width, imageBounds.height,	/* source width, height */
				imageArea.x, imageArea.y,				/* dest x, y */
				imageArea.width, imageArea.height);		/* dest width, height */
		}

		/* draw the text */
		if (text.length () > 0) {
			gc.setFont (this.getFont (columnIndex, false));
			final int fontHeight = this.getFontHeight (columnIndex);
			if (drawSelection && ((columnIndex == 0) || ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0))) {
				if (this.parent.isFocusControl () || ((this.parent.getStyle () & SWT.HIDE_SELECTION) == 0)) {
					gc.setForeground (this.display.getSystemColor (SWT.COLOR_LIST_SELECTION_TEXT));
				}
			} else if (!isSelected || drawSelection) {
      	gc.setForeground (this.getForeground (columnIndex));
      }
			x = this.getTextX (columnIndex) + MARGIN_TEXT;
			gc.drawString (text, x, y + ((itemHeight - fontHeight) / 2), true);
		}
	}

	if (this.parent.isListening (SWT.PaintItem)) {
		final int contentWidth = this.getContentWidth (columnIndex);
		final int contentX = this.getContentX (columnIndex);
		gc.setFont (this.getFont (columnIndex, false));
		if (isSelected && ((columnIndex == 0) || ((this.parent.getStyle () & SWT.FULL_SELECTION) != 0))) {
			gc.setForeground (this.display.getSystemColor (SWT.COLOR_LIST_SELECTION_TEXT));
			gc.setBackground (this.display.getSystemColor (SWT.COLOR_LIST_SELECTION));
		} else {
			gc.setForeground (this.getForeground (columnIndex));
			gc.setBackground (this.getBackground (columnIndex));
		}
		final Event event = new Event ();
		event.item = this;
		event.gc = gc;
		event.index = columnIndex;
		if (isSelected) {
      event.detail |= SWT.SELECTED;
    }
		if (drawFocus) {
      event.detail |= SWT.FOCUSED;
    }
		event.x = contentX;
		event.y = cellBounds.y;
		event.width = contentWidth;
		event.height = cellBounds.height;
		gc.setClipping (cellBounds);
		this.parent.notifyListeners (SWT.PaintItem, event);
		event.gc = null;
		if (gc.isDisposed ()) {
      return false;
    }
		gc.setAlpha (oldAlpha);
		gc.setAntialias (oldAntialias);
		gc.setBackgroundPattern (oldBackgroundPattern);
		gc.setClipping (cellBounds);
		gc.setForegroundPattern (oldForegroundPattern);
		gc.setInterpolation (oldInterpolation);
		gc.setTextAntialias (oldTextAntialias);
		gc.setAdvanced (oldAdvanced);
		drawFocus = isFocusItem && ((event.detail & SWT.FOCUSED) != 0);
	}

	return isFocusItem && !drawFocus;
}
/*
 * Redraw part of the receiver.  If either EraseItem or PaintItem is hooked then
 * only full cells should be damaged, so adjust accordingly.  If neither of these
 * events are hooked then the exact bounds given for damaging can be used.
 */
void redraw (final int x, final int y, final int width, final int height, final int columnIndex) {
	if (!this.parent.isListening (SWT.EraseItem) && !this.parent.isListening (SWT.PaintItem)) {
		this.parent.redraw (x, y, width, height, false);
		return;
	}
	final Rectangle cellBounds = this.getCellBounds (columnIndex);
	this.parent.redraw (cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height, false);
}
void redrawItem () {
	this.parent.redraw (0, this.parent.getItemY (this), this.parent.clientArea.width, this.parent.itemHeight, false);
}
/*
 * Updates internal structures in the receiver and its child items to handle the removal of a column.
 */
void removeColumn (final CTableColumn column, final int index) {
	final int columnCount = this.parent.columns.length;

	if (columnCount == 0) {
		/* reverts to normal table when last column disposed */
		this.cellBackgrounds = this.cellForegrounds = null;
		this.displayTexts = null;
		this.cellFonts = null;
		this.fontHeights = null;
		final GC gc = new GC (this.parent);
		this.computeTextWidths (gc);
		gc.dispose ();
		return;
	}

	final String[] newTexts = new String [columnCount];
	System.arraycopy (this.texts, 0, newTexts, 0, index);
	System.arraycopy (this.texts, index + 1, newTexts, index, columnCount - index);
	this.texts = newTexts;

	final Image[] newImages = new Image [columnCount];
	System.arraycopy (this.images, 0, newImages, 0, index);
	System.arraycopy (this.images, index + 1, newImages, index, columnCount - index);
	this.images = newImages;

	final int[] newTextWidths = new int [columnCount];
	System.arraycopy (this.textWidths, 0, newTextWidths, 0, index);
	System.arraycopy (this.textWidths, index + 1, newTextWidths, index, columnCount - index);
	this.textWidths = newTextWidths;

	final String[] newDisplayTexts = new String [columnCount];
	System.arraycopy (this.displayTexts, 0, newDisplayTexts, 0, index);
	System.arraycopy (this.displayTexts, index + 1, newDisplayTexts, index, columnCount - index);
	this.displayTexts = newDisplayTexts;

	if (columnCount > 1) {
		final Accessible[] newAccessibles = new Accessible [columnCount];
		System.arraycopy (this.accessibles, 0, newAccessibles, 0, index);
		System.arraycopy (this.accessibles, index + 1, newAccessibles, index, columnCount - index);
		this.accessibles = newAccessibles;
	}

	if (this.cellBackgrounds != null) {
		final Color[] newCellBackgrounds = new Color [columnCount];
		System.arraycopy (this.cellBackgrounds, 0, newCellBackgrounds, 0, index);
		System.arraycopy (this.cellBackgrounds, index + 1, newCellBackgrounds, index, columnCount - index);
		this.cellBackgrounds = newCellBackgrounds;
	}
	if (this.cellForegrounds != null) {
		final Color[] newCellForegrounds = new Color [columnCount];
		System.arraycopy (this.cellForegrounds, 0, newCellForegrounds, 0, index);
		System.arraycopy (this.cellForegrounds, index + 1, newCellForegrounds, index, columnCount - index);
		this.cellForegrounds = newCellForegrounds;
	}
	if (this.cellFonts != null) {
		final Font[] newCellFonts = new Font [columnCount];
		System.arraycopy (this.cellFonts, 0, newCellFonts, 0, index);
		System.arraycopy (this.cellFonts, index + 1, newCellFonts, index, columnCount - index);
		this.cellFonts = newCellFonts;

		final int[] newFontHeights = new int [columnCount];
		System.arraycopy (this.fontHeights, 0, newFontHeights, 0, index);
		System.arraycopy (this.fontHeights, index + 1, newFontHeights, index, columnCount - index);
		this.fontHeights = newFontHeights;
	}

	if (index == 0) {
		super.setText (this.texts [0] != null ? this.texts [0] : "");	//$NON-NLS-1$
		this.texts [0] = null;
		super.setImage(this.images [0]);
		this.images [0] = null;
		/*
		 * The new first column may not have as much width available to it as it did when it was
		 * the second column if checkboxes are being shown, so recompute its displayText if needed.
		 */
		if ((this.parent.getStyle () & SWT.CHECK) != 0) {
			final GC gc = new GC (this.parent);
			gc.setFont (this.getFont (0, false));
			this.computeDisplayText (0, gc);
			gc.dispose ();
		}
	}
	if (columnCount < 2) {
		this.texts = null;
		this.images = null;
	}
}
/**
 * Sets the receiver's background color to the color specified
 * by the argument, or to the default system color for the item
 * if the argument is null.
 *
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 2.0
 */
public void setBackground (final Color color) {
	this.checkWidget ();
	if ((color != null) && color.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	final Color oldColor = this.background;
	if (oldColor == color) {
    return;
  }
	this.background = color;
	if ((oldColor != null) && oldColor.equals (color)) {
    return;
  }
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }
	this.redrawItem ();
}
/**
 * Sets the background color at the given column index in the receiver
 * to the color specified by the argument, or to the default system color for the item
 * if the argument is null.
 *
 * @param index the column index
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public void setBackground (final int columnIndex, final Color color) {
	this.checkWidget ();
	if ((color != null) && color.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
    return;
  }
	if (this.cellBackgrounds == null) {
		if (color == null) {
      return;
    }
		this.cellBackgrounds = new Color [validColumnCount];
	}
	final Color oldColor = this.cellBackgrounds [columnIndex];
	if (oldColor == color) {
    return;
  }
	this.cellBackgrounds [columnIndex] = color;
	if ((oldColor != null) && oldColor.equals (color)) {
    return;
  }
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	if (this.isInViewport ()) {
		final Rectangle bounds = this.getCellBounds (columnIndex);
		this.parent.redraw (bounds.x, bounds.y, bounds.width, bounds.height, false);
	}
}
/**
 * Sets the checked state of the checkbox for this item.  This state change
 * only applies if the Table was created with the SWT.CHECK style.
 *
 * @param checked the new checked state of the checkbox
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setChecked (final boolean value) {
	this.checkWidget ();
	if (((this.parent.getStyle () & SWT.CHECK) == 0) || (this.checked == value)) {
    return;
  }
	this.checked = value;
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	if (this.isInViewport ()) {
		if (this.parent.isListening (SWT.EraseItem) || this.parent.isListening (SWT.PaintItem)) {
			this.redrawItem ();
		} else {
			final Rectangle bounds = this.getCheckboxBounds ();
			this.parent.redraw (bounds.x, bounds.y, bounds.width, bounds.height, false);
		}
	}
}
/**
 * Sets the font that the receiver will use to paint textual information
 * for this item to the font specified by the argument, or to the default font
 * for that kind of control if the argument is null.
 *
 * @param font the new font (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public void setFont (final Font font) {
	this.checkWidget ();
	if ((font != null) && font.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	final Font oldFont = this.font;
	if (oldFont == font) {
    return;
  }
	this.font = font;
	if ((oldFont != null) && oldFont.equals (font)) {
    return;
  }

	Rectangle bounds = this.getBounds (false);
	final int oldRightX = bounds.x + bounds.width;
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	/* recompute cached values for string measurements */
	final GC gc = new GC (this.parent);
	gc.setFont (this.getFont (false));
	this.fontHeight = gc.getFontMetrics ().getHeight ();
	this.computeDisplayTexts (gc);
	this.computeTextWidths (gc);
	gc.dispose ();

	/* horizontal bar could be affected if table has no columns */
	if (this.parent.columns.length == 0) {
		bounds = this.getBounds (false);
		final int newRightX = bounds.x + bounds.width;
		this.parent.updateHorizontalBar (newRightX, newRightX - oldRightX);
	}
	this.redrawItem ();
}
/**
 * Sets the font that the receiver will use to paint textual information
 * for the specified cell in this item to the font specified by the
 * argument, or to the default font for that kind of control if the
 * argument is null.
 *
 * @param index the column index
 * @param font the new font (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public void setFont (final int columnIndex, final Font font) {
	this.checkWidget ();
	if ((font != null) && font.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}

	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
    return;
  }
	if (this.cellFonts == null) {
		if (font == null) {
      return;
    }
		this.cellFonts = new Font [validColumnCount];
	}
	final Font oldFont = this.cellFonts [columnIndex];
	if (oldFont == font) {
    return;
  }
	this.cellFonts [columnIndex] = font;
	if ((oldFont != null) && oldFont.equals (font)) {
    return;
  }
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	/* recompute cached values for string measurements */
	final GC gc = new GC (this.parent);
	gc.setFont (this.getFont (columnIndex, false));
	if (this.fontHeights == null) {
    this.fontHeights = new int [validColumnCount];
  }
	this.fontHeights [columnIndex] = gc.getFontMetrics ().getHeight ();
	this.computeDisplayText (columnIndex, gc);
	gc.dispose ();

	if (this.isInViewport ()) {
		final Rectangle bounds = this.getCellBounds (columnIndex);
		this.parent.redraw (bounds.x, bounds.y, bounds.width, bounds.height, false);
	}
}
/**
 * Sets the receiver's foreground color to the color specified
 * by the argument, or to the default system color for the item
 * if the argument is null.
 *
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 2.0
 */
public void setForeground (final Color color) {
	this.checkWidget ();
	if ((color != null) && color.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	final Color oldColor = this.foreground;
	if (oldColor == color) {
    return;
  }
	this.foreground = color;
	if ((oldColor != null) && oldColor.equals (color)) {
    return;
  }
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }
	this.redrawItem ();
}
/**
 * Sets the foreground color at the given column index in the receiver
 * to the color specified by the argument, or to the default system color for the item
 * if the argument is null.
 *
 * @param index the column index
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public void setForeground (final int columnIndex, final Color color) {
	this.checkWidget ();
	if ((color != null) && color.isDisposed ()) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
    return;
  }
	if (this.cellForegrounds == null) {
		if (color == null) {
      return;
    }
		this.cellForegrounds = new Color [validColumnCount];
	}
	final Color oldColor = this.cellForegrounds [columnIndex];
	if (oldColor == color) {
    return;
  }
	this.cellForegrounds [columnIndex] = color;
	if ((oldColor != null) && oldColor.equals (color)) {
    return;
  }
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	if (this.isInViewport ()) {
		this.redraw (
			this.getTextX (columnIndex),
			this.parent.getItemY (this),
			this.textWidths [columnIndex] + (2 * MARGIN_TEXT),
			this.parent.itemHeight,
			columnIndex);
	}
}
/**
 * Sets the grayed state of the checkbox for this item.  This state change
 * only applies if the Table was created with the SWT.CHECK style.
 *
 * @param grayed the new grayed state of the checkbox;
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setGrayed (final boolean value) {
	this.checkWidget ();
	if (((this.parent.getStyle () & SWT.CHECK) == 0) || (this.grayed == value)) {
    return;
  }
	this.grayed = value;
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	if (this.isInViewport ()) {
		final Rectangle bounds = this.getCheckboxBounds ();
		this.parent.redraw (bounds.x, bounds.y, bounds.width, bounds.height, false);
	}
}
@Override
public void setImage (final Image value) {
	this.checkWidget ();
	this.setImage (0, value);
}
/**
 * Sets the image for multiple columns in the table.
 *
 * @param images the array of new images
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the array of images is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if one of the images has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setImage (final Image[] value) {
	this.checkWidget ();
	if (value == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }

	// TODO make a smarter implementation of this
	for (int i = 0; i < value.length; i++) {
		if (value [i] != null) {
      this.setImage (i, value [i]);
    }
	}
}
/**
 * Sets the receiver's image at a column.
 *
 * @param index the column index
 * @param image the new image
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setImage (final int columnIndex, final Image value) {
	this.checkWidget ();
	if ((value != null) && value.isDisposed ()) {
    SWT.error (SWT.ERROR_INVALID_ARGUMENT);
  }

	final CTableColumn[] columns = this.parent.columns;
	final int validColumnCount = Math.max (1, columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount))) {
    return;
  }
	final Image image = this.getImage (columnIndex, false);
	if ((value == image) || ((value != null) && value.equals (image))) {
    return;
  }
	if (columnIndex == 0) {
		super.setImage (value);
	} else {
		this.images [columnIndex] = value;
	}
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	/*
	 * An image width change may affect the space available for the item text, so
	 * recompute the displayText if there are columns.
	 */
	if (columns.length > 0) {
		final GC gc = new GC (this.parent);
		gc.setFont (this.getFont (columnIndex, false));
		this.computeDisplayText (columnIndex, gc);
		gc.dispose ();
	}

	if (value == null) {
		this.redrawItem ();	// TODO why the whole item?
		return;
	}

	/*
	 * If this is the first image being put into the table then its item height
	 * may be adjusted, in which case a full redraw is needed.
	 */
	if (this.parent.imageHeight == 0) {
		final int oldItemHeight = this.parent.itemHeight;
		this.parent.setImageHeight (value.getBounds ().height);
		if (oldItemHeight != this.parent.itemHeight) {
			if (columnIndex == 0) {
				this.parent.col0ImageWidth = value.getBounds ().width;
				if (columns.length > 0) {
					/*
					 * All column 0 cells will now have less room available for their texts,
					 * so all items must now recompute their column 0 displayTexts.
					 */
					final GC gc = new GC (this.parent);
					final CTableItem[] rootItems = this.parent.items;
					for (int i = 0; i < this.parent.itemsCount; i++) {
						rootItems [i].updateColumnWidth (columns [0], gc);
					}
					gc.dispose ();
				}
			}
			this.parent.redraw ();
			return;
		}
	}

	/*
	 * If this is the first image being put into column 0 then all cells
	 * in the column should also indent accordingly.
	 */
	if ((columnIndex == 0) && (this.parent.col0ImageWidth == 0)) {
		this.parent.col0ImageWidth = value.getBounds ().width;
		/* redraw the column */
		if (columns.length == 0) {
			this.parent.redraw ();
		} else {
			/*
			 * All column 0 cells will now have less room available for their texts,
			 * so all items must now recompute their column 0 displayTexts.
			 */
			final GC gc = new GC (this.parent);
			final CTableItem[] rootItems = this.parent.items;
			for (int i = 0; i < this.parent.itemsCount; i++) {
				rootItems [i].updateColumnWidth (columns [0], gc);
			}
			gc.dispose ();
			this.parent.redraw (
				columns [0].getX (), 0,
				columns [0].width,
				this.parent.clientArea.height,
				false);
		}
		return;
	}
	this.redrawItem ();	// TODO why the whole item?
}
/**
 * Sets the indent of the first column's image, expressed in terms of the image's width.
 *
 * @param indent the new indent
 *
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @deprecated this functionality is not supported on most platforms
 */
@Deprecated
public void setImageIndent (final int indent) {
	this.checkWidget();
	if ((indent < 0) || (this.imageIndent == indent)) {
    return;
  }
	this.imageIndent = indent;
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }
}
/**
 * Sets the receiver's text at a column
 *
 * @param index the column index
 * @param string the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (final int columnIndex, final String value) {
	this.checkWidget ();
	if (value == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	final int validColumnCount = Math.max (1, this.parent.columns.length);
	if (!((0 <= columnIndex) && (columnIndex < validColumnCount)) || value.equals (this.getText (columnIndex, false))) {
    return;
  }
	if (columnIndex == 0) {
		super.setText (value);
	} else {
		this.texts [columnIndex] = value;
	}
	if ((this.parent.getStyle () & SWT.VIRTUAL) != 0) {
    this.cached = true;
  }

	final int oldWidth = this.textWidths [columnIndex];
	final GC gc = new GC (this.parent);
	gc.setFont (this.getFont (columnIndex, false));
	this.computeDisplayText (columnIndex, gc);
	gc.dispose ();

	if (this.parent.columns.length == 0) {
		final Rectangle bounds = this.getBounds (false);
		final int rightX = bounds.x + bounds.width;
		this.parent.updateHorizontalBar (rightX, this.textWidths [columnIndex] - oldWidth);
	}
	if (this.isInViewport ()) {
		this.redraw (
			this.getTextX (columnIndex),
			this.parent.getItemY (this),
			Math.max (oldWidth, this.textWidths [columnIndex]) + (2 * MARGIN_TEXT),
			this.parent.itemHeight,
			columnIndex);
	}
}
@Override
public void setText (final String value) {
	this.checkWidget ();
	Rectangle bounds = this.getBounds (false);
	final int oldRightX = bounds.x + bounds.width;
	this.setText (0, value);
	/* horizontal bar could be affected if table has no columns */
	if (this.parent.columns.length == 0) {
		bounds = this.getBounds (false);
		final int newRightX = bounds.x + bounds.width;
		this.parent.updateHorizontalBar (newRightX, newRightX - oldRightX);
	}
}
/**
 * Sets the text for multiple columns in the table.
 *
 * @param strings the array of new strings
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (final String[] value) {
	this.checkWidget ();
	if (value == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	Rectangle bounds = this.getBounds (false);
	final int oldRightX = bounds.x + bounds.width;
	// TODO make a smarter implementation of this
	for (int i = 0; i < value.length; i++) {
		if (value [i] != null) {
      this.setText (i, value [i]);
    }
	}
	/* horizontal bar could be affected if table has no columns */
	if (this.parent.columns.length == 0) {
		bounds = this.getBounds (false);
		final int newRightX = bounds.x + bounds.width;
		this.parent.updateHorizontalBar (newRightX, newRightX - oldRightX);
	}
}
/*
 * Perform any internal changes necessary to reflect a changed column width.
 */
void updateColumnWidth (final CTableColumn column, final GC gc) {
	final int columnIndex = column.getIndex ();
	gc.setFont (this.getFont (columnIndex, false));
	final String oldDisplayText = this.displayTexts [columnIndex];
	this.computeDisplayText (columnIndex, gc);

	/* the cell must be damaged if there is custom drawing being done or if the alignment is not LEFT */
	if (this.isInViewport ()) {
		final boolean columnIsLeft = (column.getStyle () & SWT.LEFT) != 0;
		if (!columnIsLeft || this.parent.isListening (SWT.EraseItem) || this.parent.isListening (SWT.PaintItem)) {
			final Rectangle cellBounds = this.getCellBounds (columnIndex);
			this.parent.redraw (cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height, false);
			return;
		}
		/* if the display text has changed then the cell text must be damaged in order to repaint */
		if ((oldDisplayText == null) || !oldDisplayText.equals (this.displayTexts [columnIndex])) {
			final Rectangle cellBounds = this.getCellBounds (columnIndex);
			final int textX = this.getTextX (columnIndex);
			this.parent.redraw (textX, cellBounds.y, (cellBounds.x + cellBounds.width) - textX, cellBounds.height, false);
		}
	}
}
/*
 * The parent's font has changed, so if this font was being used by the receiver then
 * recompute its cached text sizes using the gc argument.
 */
void updateFont (final GC gc) {
	if (this.font == null) {		/* receiver is using the Table's font */
		this.computeDisplayTexts (gc);
		this.computeTextWidths (gc);
	}
}
}
