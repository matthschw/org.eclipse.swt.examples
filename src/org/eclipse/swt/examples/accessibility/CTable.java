/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
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
import org.eclipse.swt.accessibility.AccessibleTableAdapter;
import org.eclipse.swt.accessibility.AccessibleTableEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class implement a selectable user interface
 * object that displays a list of images and strings and issues
 * notification when selected.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>TableItem</code>.
 * </p><p>
 * Style <code>VIRTUAL</code> is used to create a <code>Table</code> whose
 * <code>TableItem</code>s are to be populated by the client on an on-demand basis
 * instead of up-front.  This can provide significant performance improvements for
 * tables that are very large or for which <code>TableItem</code> population is
 * expensive (for example, retrieving values from an external source).
 * </p><p>
 * Here is an example of using a <code>Table</code> with style <code>VIRTUAL</code>:
 * <code><pre>
 *  final Table table = new Table (parent, SWT.VIRTUAL | SWT.BORDER);
 *  table.setItemCount (1000000);
 *  table.addListener (SWT.SetData, new Listener () {
 *      public void handleEvent (Event event) {
 *          TableItem item = (TableItem) event.item;
 *          int index = table.indexOf (item);
 *          item.setText ("Item " + index);
 *          System.out.println (item.getText ());
 *      }
 *  });
 * </pre></code>
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not normally make sense to add <code>Control</code> children to
 * it, or set a layout on it, unless implementing something like a cell
 * editor.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK, FULL_SELECTION, HIDE_SELECTION, VIRTUAL, NO_SCROLL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, SetData, MeasureItem, EraseItem, PaintItem</dd>
 * </dl>
 * </p><p>
 * Note: Only one of the styles SINGLE, and MULTI may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#table">Table, TableItem, TableColumn snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example: ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CTable extends Composite {
	Canvas header;
	CTableColumn[] columns = new CTableColumn [0];
	CTableColumn[] orderedColumns;
	CTableItem[] items = new CTableItem [0];
	CTableItem[] selectedItems = new CTableItem [0];
	CTableItem focusItem, anchorItem, lastClickedItem;
	Event lastSelectionEvent;
	boolean linesVisible, ignoreKey, ignoreDispose, customHeightSet;
	int itemsCount = 0;
	int topIndex = 0, horizontalOffset = 0;
	int fontHeight = 0, imageHeight = 0, itemHeight = 0;
	int col0ImageWidth = 0;
	int headerImageHeight = 0;
	CTableColumn resizeColumn;
	int resizeColumnX = -1;
	int drawCount = 0;
	CTableColumn sortColumn;
	int sortDirection = SWT.NONE;

	/* column header tooltip */
	Listener toolTipListener;
	Shell toolTipShell;
	Label toolTipLabel;

	Rectangle arrowBounds, checkboxBounds, clientArea;

	static final int MARGIN_IMAGE = 3;
	static final int MARGIN_CELL = 1;
	static final int SIZE_HORIZONTALSCROLL = 5;
	static final int TOLLERANCE_COLUMNRESIZE = 2;
	static final int WIDTH_HEADER_SHADOW = 2;
	static final int WIDTH_CELL_HIGHLIGHT = 1;
	static final int [] toolTipEvents = new int[] {SWT.MouseExit, SWT.MouseHover, SWT.MouseMove, SWT.MouseDown};
	static final String ELLIPSIS = "...";						//$NON-NLS-1$
	static final String ID_UNCHECKED = "UNCHECKED";			//$NON-NLS-1$
	static final String ID_GRAYUNCHECKED = "GRAYUNCHECKED";	//$NON-NLS-1$
	static final String ID_CHECKMARK = "CHECKMARK";			//$NON-NLS-1$
	static final String ID_ARROWUP = "ARROWUP";				//$NON-NLS-1$
	static final String ID_ARROWDOWN = "ARROWDOWN";			//$NON-NLS-1$

	Display display;

//TEMPORARY CODE
boolean hasFocus;
@Override
public boolean isFocusControl() {
	return this.hasFocus;
}

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
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
 * @see SWT#SINGLE
 * @see SWT#MULTI
 * @see SWT#CHECK
 * @see SWT#FULL_SELECTION
 * @see SWT#HIDE_SELECTION
 * @see SWT#VIRTUAL
 * @see SWT#NO_SCROLL
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public CTable (final Composite parent, final int style) {
	super (parent, checkStyle (style));
	this.display = parent.getDisplay ();
	this.setForeground (null);	/* set foreground and background to chosen default colors */
	this.setBackground (null);
	final GC gc = new GC (this);
	this.fontHeight = gc.getFontMetrics ().getHeight ();
	gc.dispose ();
	this.itemHeight = this.fontHeight + (2 * this.getCellPadding ());
	initImages (this.display);
	this.checkboxBounds = this.getUncheckedImage ().getBounds ();
	this.arrowBounds = this.getArrowDownImage ().getBounds ();
	this.clientArea = this.getClientArea ();

	final Listener listener = event -> this.handleEvents (event);
	this.addListener (SWT.Paint, listener);
	this.addListener (SWT.MouseDown, listener);
	this.addListener (SWT.MouseUp, listener);
	this.addListener (SWT.MouseDoubleClick, listener);
	this.addListener (SWT.Dispose, listener);
	this.addListener (SWT.Resize, listener);
	this.addListener (SWT.KeyDown, listener);
	this.addListener (SWT.FocusOut, listener);
	this.addListener (SWT.FocusIn, listener);
	this.addListener (SWT.Traverse, listener);

	this.initAccessibility ();

	this.header = new Canvas (this, SWT.NO_REDRAW_RESIZE | SWT.NO_FOCUS);
	this.header.setVisible (false);
	this.header.setBounds (0, 0, 0, this.fontHeight + (2 * this.getHeaderPadding ()));
	this.header.addListener (SWT.Paint, listener);
	this.header.addListener (SWT.MouseDown, listener);
	this.header.addListener (SWT.MouseUp, listener);
	this.header.addListener (SWT.MouseHover, listener);
	this.header.addListener (SWT.MouseDoubleClick, listener);
	this.header.addListener (SWT.MouseMove, listener);
	this.header.addListener (SWT.MouseExit, listener);
	this.header.addListener (SWT.MenuDetect, listener);

	this.toolTipListener = event -> {
		switch (event.type) {
			case SWT.MouseHover:
			case SWT.MouseMove:
				if (this.headerUpdateToolTip (event.x)) {
          break;
        }
				// FALL THROUGH
			case SWT.MouseExit:
			case SWT.MouseDown:
				this.headerHideToolTip ();
				break;
		}
	};

	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar != null) {
		hBar.setValues (0, 0, 1, 1, 1, 1);
		hBar.setVisible (false);
		hBar.addListener (SWT.Selection, listener);
	}
	final ScrollBar vBar = this.getVerticalBar ();
	if (vBar != null) {
		vBar.setValues (0, 0, 1, 1, 1, 1);
		vBar.setVisible (false);
		vBar.addListener (SWT.Selection, listener);
	}
}
/**
 * Adds the listener to the collection of listeners who will
 * be notified when the user changes the receiver's selection, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * When <code>widgetSelected</code> is called, the item field of the event object is valid.
 * If the receiver has the <code>SWT.CHECK</code> style and the check selection changes,
 * the event object detail field contains the value <code>SWT.CHECK</code>.
 * <code>widgetDefaultSelected</code> is typically called when an item is double-clicked.
 * The item field of the event object is valid for default selection, but the detail field is not used.
 * </p>
 *
 * @param listener the listener which should be notified when the user changes the receiver's selection
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #removeSelectionListener
 * @see SelectionEvent
 */
public void addSelectionListener (final SelectionListener listener) {
	this.checkWidget ();
	if (listener == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	final TypedListener typedListener = new TypedListener (listener);
	this.addListener (SWT.Selection, typedListener);
	this.addListener (SWT.DefaultSelection, typedListener);
}
boolean checkData (final CTableItem item, final boolean redraw) {
	if (item.cached) {
    return true;
  }
	if ((this.getStyle () & SWT.VIRTUAL) != 0) {
		item.cached = true;
		final Event event = new Event ();
		event.item = item;
		event.index = this.indexOf (item);
		this.notifyListeners (SWT.SetData, event);
		if (this.isDisposed () || item.isDisposed ()) {
      return false;
    }
		if (redraw) {
      this.redrawItem (item.index, false);
    }
	}
	return true;
}
static int checkStyle (int style) {
	/*
	* Feature in Windows.  Even when WS_HSCROLL or
	* WS_VSCROLL is not specified, Windows creates
	* trees and tables with scroll bars.  The fix
	* is to set H_SCROLL and V_SCROLL.
	*
	* NOTE: This code appears on all platforms so that
	* applications have consistent scroll bar behavior.
	*/
	if ((style & SWT.NO_SCROLL) == 0) {
		style |= SWT.H_SCROLL | SWT.V_SCROLL;
	}
	style |= SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED;
	//TEMPORARY CODE
	style |= SWT.FULL_SELECTION;
	return checkBits (style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
}
static int checkBits (int style, final int int0, final int int1, final int int2, final int int3, final int int4, final int int5) {
	final int mask = int0 | int1 | int2 | int3 | int4 | int5;
	if ((style & mask) == 0) {
    style |= int0;
  }
	if ((style & int0) != 0) {
    style = (style & ~mask) | int0;
  }
	if ((style & int1) != 0) {
    style = (style & ~mask) | int1;
  }
	if ((style & int2) != 0) {
    style = (style & ~mask) | int2;
  }
	if ((style & int3) != 0) {
    style = (style & ~mask) | int3;
  }
	if ((style & int4) != 0) {
    style = (style & ~mask) | int4;
  }
	if ((style & int5) != 0) {
    style = (style & ~mask) | int5;
  }
	return style;
}
/**
 * Clears the item at the given zero-relative index in the receiver.
 * The text, icon and other attributes of the item are set to the default
 * value.  If the table was created with the <code>SWT.VIRTUAL</code> style,
 * these attributes are requested again as needed.
 *
 * @param index the index of the item to clear
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SWT#VIRTUAL
 * @see SWT#SetData
 *
 * @since 3.0
 */
public void clear (final int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    SWT.error (SWT.ERROR_INVALID_RANGE);
  }
	final Rectangle bounds = this.items [index].getBounds (false);
	final int oldRightX = bounds.x + bounds.width;
	this.items [index].clear ();
	if (this.columns.length == 0) {
    this.updateHorizontalBar (0, -oldRightX);
  }
	this.redrawItem (index, false);
}
/**
 * Removes the items from the receiver which are between the given
 * zero-relative start and end indices (inclusive).  The text, icon
 * and other attributes of the items are set to their default values.
 * If the table was created with the <code>SWT.VIRTUAL</code> style,
 * these attributes are requested again as needed.
 *
 * @param start the start index of the item to clear
 * @param end the end index of the item to clear
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SWT#VIRTUAL
 * @see SWT#SetData
 *
 * @since 3.0
 */
public void clear (final int start, final int end) {
	this.checkWidget ();
	if (start > end) {
    return;
  }
	if (!((0 <= start) && (start <= end) && (end < this.itemsCount))) {
		SWT.error (SWT.ERROR_INVALID_RANGE);
	}
	for (int i = start; i <= end; i++) {
		this.items [i].clear ();
	}
	this.updateHorizontalBar ();
	this.redrawItems (start, end, false);
}
/**
 * Clears the items at the given zero-relative indices in the receiver.
 * The text, icon and other attributes of the items are set to their default
 * values.  If the table was created with the <code>SWT.VIRTUAL</code> style,
 * these attributes are requested again as needed.
 *
 * @param indices the array of indices of the items
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 *    <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SWT#VIRTUAL
 * @see SWT#SetData
 *
 * @since 3.0
 */
public void clear (final int [] indices) {
	this.checkWidget ();
	if (indices == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (indices.length == 0) {
    return;
  }
	for (final int index : indices) {
		if (!((0 <= index) && (index < this.itemsCount))) {
			SWT.error (SWT.ERROR_INVALID_RANGE);
		}
	}

	for (final int index : indices) {
		this.items [index].clear ();
	}
	this.updateHorizontalBar ();
	for (final int index : indices) {
		this.redrawItem (index, false);
	}
}
/**
 * Clears all the items in the receiver. The text, icon and other
 * attributes of the items are set to their default values. If the
 * table was created with the <code>SWT.VIRTUAL</code> style, these
 * attributes are requested again as needed.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SWT#VIRTUAL
 * @see SWT#SetData
 *
 * @since 3.0
 */
public void clearAll () {
	this.checkWidget ();
	this.clear (0, this.itemsCount - 1);
}
/*
 * Returns the ORDERED index of the column that the specified x falls within,
 * or -1 if the x lies to the right of the last column.
 */
int computeColumnIntersect (final int x, final int startColumn) {
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	if ((orderedColumns.length - 1) < startColumn) {
    return -1;
  }
	int rightX = orderedColumns [startColumn].getX ();
	for (int i = startColumn; i < orderedColumns.length; i++) {
		rightX += orderedColumns [i].width;
		if (x < rightX) {
      return i;
    }
	}
	return -1;
}
@Override
public Point computeSize (final int wHint, final int hHint, final boolean changed) {
	this.checkWidget ();
	int width = 0, height = 0;
	if (wHint != SWT.DEFAULT) {
		width = wHint;
	} else if (this.columns.length == 0) {
  	for (int i = 0; i < this.itemsCount; i++) {
  		final Rectangle itemBounds = this.items [i].getBounds (false);
  		width = Math.max (width, itemBounds.x + itemBounds.width);
  	}
  } else {
  	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
  	final CTableColumn lastColumn = orderedColumns [orderedColumns.length - 1];
  	width = lastColumn.getX () + lastColumn.width;
  }
	if (hHint != SWT.DEFAULT) {
		height = hHint;
	} else {
		height = this.getHeaderHeight () + (this.itemsCount * this.itemHeight);
	}
	final Rectangle result = this.computeTrim (0, 0, width, height);
	return new Point (result.width, result.height);
}
void createItem (final CTableColumn column, final int index) {
	final CTableColumn[] newColumns = new CTableColumn [this.columns.length + 1];
	System.arraycopy (this.columns, 0, newColumns, 0, index);
	newColumns [index] = column;
	System.arraycopy (this.columns, index, newColumns, index + 1, this.columns.length - index);
	this.columns = newColumns;

	if (this.orderedColumns != null) {
		int insertIndex = 0;
		if (index > 0) {
			insertIndex = this.columns [index - 1].getOrderIndex () + 1;
		}
		final CTableColumn[] newOrderedColumns = new CTableColumn [this.orderedColumns.length + 1];
		System.arraycopy (this.orderedColumns, 0, newOrderedColumns, 0, insertIndex);
		newOrderedColumns [insertIndex] = column;
		System.arraycopy (
			this.orderedColumns,
			insertIndex,
			newOrderedColumns,
			insertIndex + 1,
			this.orderedColumns.length - insertIndex);
		this.orderedColumns = newOrderedColumns;
	}

	/* allow all items to update their internal structures accordingly */
	for (int i = 0; i < this.itemsCount; i++) {
		this.items [i].addColumn (column);
	}

	/* existing items become hidden when going from 0 to 1 column (0 width) */
	if ((this.columns.length == 1) && (this.itemsCount > 0)) {
		this.redrawFromItemDownwards (this.topIndex);
	} else /* checkboxes become hidden when creating a column with index == orderedIndex == 0 (0 width) */
  if ((this.itemsCount > 0) && ((this.getStyle () & SWT.CHECK) != 0) && (index == 0) && (column.getOrderIndex () == 0)) {
  	this.redrawFromItemDownwards (this.topIndex);
  }

	/* Columns were added, so notify the accessible. */
	final int[] eventData = new int[5];
	eventData[0] = ACC.INSERT;
	eventData[1] = 0;
	eventData[2] = 0;
	eventData[3] = index;
	eventData[4] = 1;
	this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);
}
void createItem (final CTableItem item) {
	final int index = item.index;
	if (this.itemsCount == this.items.length) {
		final int grow = this.drawCount <= 0 ? 4 : Math.max (4, (this.items.length * 3) / 2);
		final CTableItem[] newItems = new CTableItem [this.items.length + grow];
		System.arraycopy (this.items, 0, newItems, 0, this.items.length);
		this.items = newItems;
	}
	if (index != this.itemsCount) {
		/* new item is not at end of list, so shift other items right to create space for it */
		System.arraycopy (this.items, index, this.items, index + 1, this.itemsCount - index);
	}
	this.items [index] = item;
	this.itemsCount++;

	/* update the index for items bumped down by this new item */
	for (int i = index + 1; i < this.itemsCount; i++) {
		this.items [i].index = i;
	}

	/* Rows were added, so notify the accessible. */
	final int[] eventData = new int[5];
	eventData[0] = ACC.INSERT;
	eventData[1] = index;
	eventData[2] = 1;
	eventData[3] = 0;
	eventData[4] = 0;
	this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);

	/* update scrollbars */
	this.updateVerticalBar ();
	final Rectangle bounds = item.getBounds (false);
	final int rightX = bounds.x + bounds.width;
	this.updateHorizontalBar (rightX, rightX);
	/*
	 * If new item is above viewport then adjust topIndex and the vertical
	 * scrollbar so that the current viewport items will not change.
	 */
	if (item.index < this.topIndex) {
		this.topIndex++;
		final ScrollBar vBar = this.getVerticalBar ();
		if (vBar != null) {
      vBar.setSelection (this.topIndex);
    }
		return;
	}
	/*
	 * If this is the first item and the receiver has focus then its boundary
	 * focus ring must be removed.
	 */
	if ((this.itemsCount == 1) && this.isFocusControl ()) {
		this.focusItem = item;
		this.redraw ();
		return;
	}
	if (item.isInViewport ()) {
		this.redrawFromItemDownwards (index);
	}
}
/**
 * Deselects the item at the given zero-relative index in the receiver.
 * If the item at the index was already deselected, it remains
 * deselected. Indices that are out of range are ignored.
 *
 * @param index the index of the item to deselect
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (final int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    return;
  }
	final CTableItem item = this.items [index];
	final int selectIndex = this.getSelectionIndex (item);
	if (selectIndex == -1) {
    return;
  }

	final CTableItem[] newSelectedItems = new CTableItem [this.selectedItems.length - 1];
	System.arraycopy (this.selectedItems, 0, newSelectedItems, 0, selectIndex);
	System.arraycopy (this.selectedItems, selectIndex + 1, newSelectedItems, selectIndex, newSelectedItems.length - selectIndex);
	this.selectedItems = newSelectedItems;

	if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
		this.redrawItem (item.index, false);
	}
	this.getAccessible().selectionChanged();
}
/**
 * Deselects the items at the given zero-relative indices in the receiver.
 * If the item at the given zero-relative index in the receiver
 * is selected, it is deselected.  If the item at the index
 * was not selected, it remains deselected.  The range of the
 * indices is inclusive. Indices that are out of range are ignored.
 *
 * @param start the start index of the items to deselect
 * @param end the end index of the items to deselect
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (int start, int end) {
	this.checkWidget ();
	if ((start == 0) && (end == (this.itemsCount - 1))) {
		this.deselectAll ();
	} else {
		start = Math.max (start, 0);
		end = Math.min (end, this.itemsCount - 1);
		for (int i = start; i <= end; i++) {
			this.deselect (i);
		}
	}
}
/**
 * Deselects the items at the given zero-relative indices in the receiver.
 * If the item at the given zero-relative index in the receiver
 * is selected, it is deselected.  If the item at the index
 * was not selected, it remains deselected. Indices that are out
 * of range and duplicate indices are ignored.
 *
 * @param indices the array of indices for the items to deselect
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (final int [] indices) {
	this.checkWidget ();
	if (indices == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (indices.length == 0) {
    return;
  }
	for (final int index : indices) {
		this.deselect (index);
	}
}
/**
 * Deselects all selected items in the receiver.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselectAll () {
	this.checkWidget ();
	final CTableItem[] oldSelection = this.selectedItems;
	this.selectedItems = new CTableItem [0];
	if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
		for (final CTableItem element : oldSelection) {
			this.redrawItem (element.index, true);
		}
	}
	for (final CTableItem element : oldSelection) {
		element.getAccessible(this.getAccessible(), 0).selectionChanged();
	}
	if (oldSelection.length > 0) {
    this.getAccessible().selectionChanged();
  }
}
void deselectItem (final CTableItem item) {
	final int index = this.getSelectionIndex (item);
	if (index == -1) {
    return;
  }
	final CTableItem[] newSelectedItems = new CTableItem [this.selectedItems.length - 1];
	System.arraycopy (this.selectedItems, 0, newSelectedItems, 0, index);
	System.arraycopy (
		this.selectedItems,
		index + 1,
		newSelectedItems,
		index,
		newSelectedItems.length - index);
	this.selectedItems = newSelectedItems;
	item.getAccessible(this.getAccessible(), 0).selectionChanged();
}
void destroyItem (final CTableColumn column) {
	this.headerHideToolTip ();
	final int index = column.getIndex ();
	final int orderedIndex = column.getOrderIndex ();

	final CTableColumn[] newColumns = new CTableColumn [this.columns.length - 1];
	System.arraycopy (this.columns, 0, newColumns, 0, index);
	System.arraycopy (this.columns, index + 1, newColumns, index, newColumns.length - index);
	this.columns = newColumns;

	if (this.orderedColumns != null) {
		if (this.columns.length < 2) {
			this.orderedColumns = null;
		} else {
			final int removeIndex = column.getOrderIndex ();
			final CTableColumn[] newOrderedColumns = new CTableColumn [this.orderedColumns.length - 1];
			System.arraycopy (this.orderedColumns, 0, newOrderedColumns, 0, removeIndex);
			System.arraycopy (
				this.orderedColumns,
				removeIndex + 1,
				newOrderedColumns,
				removeIndex,
				newOrderedColumns.length - removeIndex);
			this.orderedColumns = newOrderedColumns;
		}
	}

	/* ensure that column 0 always has left-alignment */
	if ((index == 0) && (this.columns.length > 0)) {
		int style = this.columns [0].getStyle ();
		style |= SWT.LEFT;
		style &= ~(SWT.CENTER | SWT.RIGHT);
		this.columns [0].setStyle (style);
	}

	/* allow all items to update their internal structures accordingly */
	for (int i = 0; i < this.itemsCount; i++) {
		this.items [i].removeColumn (column, index);
	}

	/* update horizontal scrollbar */
	final int lastColumnIndex = this.columns.length - 1;
	if (lastColumnIndex < 0) {		/* no more columns */
		this.updateHorizontalBar ();
	} else {
		int newWidth = 0;
		for (final CTableColumn column2 : this.columns) {
			newWidth += column2.width;
		}
		final ScrollBar hBar = this.getHorizontalBar ();
		if (hBar != null) {
			hBar.setMaximum (newWidth);
			hBar.setVisible (this.clientArea.width < newWidth);
		}
		final int selection = hBar.getSelection ();
		if (selection != this.horizontalOffset) {
			this.horizontalOffset = selection;
			this.redraw ();
			if (this.header.isVisible () && (this.drawCount <= 0)) {
        this.header.redraw ();
      }
		}
	}
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	for (int i = orderedIndex; i < orderedColumns.length; i++) {
		if (!orderedColumns [i].isDisposed ()) {
			orderedColumns [i].notifyListeners (SWT.Move, new Event ());
		}
	}

	final int[] eventData = new int[5];
	eventData[0] = ACC.DELETE;
	eventData[1] = 0;
	eventData[2] = 0;
	eventData[3] = index;
	eventData[4] = 1;
	this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);

	if (this.sortColumn == column) {
		this.sortColumn = null;
	}
}
/*
 * Allows the Table to update internal structures it has that may contain the
 * item being destroyed.
 */
void destroyItem (final CTableItem item) {
	if (item == this.focusItem) {
    this.reassignFocus ();
  }

	final int index = item.index;
	final Rectangle bounds = item.getBounds (false);
	final int rightX = bounds.x + bounds.width;

	if (index != (this.itemsCount - 1)) {
		/* item is not at end of items list, so must shift items left to reclaim its slot */
		System.arraycopy (this.items, index + 1, this.items, index, this.itemsCount - index - 1);
		this.items [this.itemsCount - 1] = null;
	} else {
		this.items [index] = null;	/* last item, so no array copy needed */
	}
	this.itemsCount--;

	if ((this.drawCount <= 0) && ((this.items.length - this.itemsCount) == 4)) {
		/* shrink the items array */
		final CTableItem[] newItems = new CTableItem [this.itemsCount];
		System.arraycopy (this.items, 0, newItems, 0, newItems.length);
		this.items = newItems;
	}

	/* update the index on affected items */
	for (int i = index; i < this.itemsCount; i++) {
		this.items [i].index = i;
	}
	item.index = -1;

	final int oldTopIndex = this.topIndex;
	this.updateVerticalBar ();
	this.updateHorizontalBar (0, -rightX);
	/*
	 * If destroyed item is above viewport then adjust topIndex and the vertical
	 * scrollbar so that the current viewport items will not change.
	 */
	if (index < this.topIndex) {
		this.topIndex = oldTopIndex - 1;
		final ScrollBar vBar = this.getVerticalBar ();
		if (vBar != null) {
      vBar.setSelection (this.topIndex);
    }
	}

	/* selectedItems array */
	if (item.isSelected ()) {
		final int selectionIndex = this.getSelectionIndex (item);
		final CTableItem[] newSelectedItems = new CTableItem [this.selectedItems.length - 1];
		System.arraycopy (this.selectedItems, 0, newSelectedItems, 0, selectionIndex);
		System.arraycopy (
			this.selectedItems,
			selectionIndex + 1,
			newSelectedItems,
			selectionIndex,
			newSelectedItems.length - selectionIndex);
		this.selectedItems = newSelectedItems;
	}
	if (item == this.anchorItem) {
    this.anchorItem = null;
  }
	if (item == this.lastClickedItem) {
    this.lastClickedItem = null;
  }
	/*
	 * If this was the last item and the receiver has focus then its boundary
	 * focus ring must be redrawn.
	 */
	if ((this.itemsCount == 0) && this.isFocusControl ()) {
		this.redraw ();
	}

	final int[] eventData = new int[5];
	eventData[0] = ACC.DELETE;
	eventData[1] = index;
	eventData[2] = 1;
	eventData[3] = 0;
	eventData[4] = 0;
	this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);
}
Image getArrowDownImage () {
	return (Image) this.display.getData (ID_ARROWDOWN);
}
Image getArrowUpImage () {
	return (Image) this.display.getData (ID_ARROWUP);
}
int getCellPadding () {
	return MARGIN_CELL + WIDTH_CELL_HIGHLIGHT;
}
Image getCheckmarkImage () {
	return (Image) this.display.getData (ID_CHECKMARK);
}
@Override
public Control[] getChildren () {
	this.checkWidget ();
	final Control[] controls = super.getChildren ();
	if (this.header == null) {
    return controls;
  }
	final Control[] result = new Control [controls.length - 1];
	/* remove the Header from the returned set of children */
	int index = 0;
	for (final Control control : controls) {
		if (control != this.header) {
			result [index++] = control;
		}
	}
	return result;
}
/**
 * Returns the column at the given, zero-relative index in the
 * receiver. Throws an exception if the index is out of range.
 * Columns are returned in the order that they were created.
 * If no <code>TableColumn</code>s were created by the programmer,
 * this method will throw <code>ERROR_INVALID_RANGE</code> despite
 * the fact that a single column of data may be visible in the table.
 * This occurs when the programmer uses the table like a list, adding
 * items but never creating a column.
 *
 * @param index the index of the column to return
 * @return the column at the given index
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#getColumnOrder()
 * @see CTable#setColumnOrder(int[])
 * @see CTableColumn#getMoveable()
 * @see CTableColumn#setMoveable(boolean)
 * @see SWT#Move
 */
public CTableColumn getColumn (final int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.columns.length))) {
    SWT.error (SWT.ERROR_INVALID_RANGE);
  }
	return this.columns [index];
}
/**
 * Returns the number of columns contained in the receiver.
 * If no <code>TableColumn</code>s were created by the programmer,
 * this value is zero, despite the fact that visually, one column
 * of items may be visible. This occurs when the programmer uses
 * the table like a list, adding items but never creating a column.
 *
 * @return the number of columns
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getColumnCount () {
	this.checkWidget ();
	return this.columns.length;
}
/**
 * Returns an array of zero-relative integers that map
 * the creation order of the receiver's items to the
 * order in which they are currently being displayed.
 * <p>
 * Specifically, the indices of the returned array represent
 * the current visual order of the items, and the contents
 * of the array represent the creation order of the items.
 * </p><p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver.
 * </p>
 *
 * @return the current visual order of the receiver's items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#setColumnOrder(int[])
 * @see CTableColumn#getMoveable()
 * @see CTableColumn#setMoveable(boolean)
 * @see SWT#Move
 *
 * @since 3.1
 */
public int[] getColumnOrder () {
	this.checkWidget ();
	final int[] result = new int [this.columns.length];
	if (this.orderedColumns != null) {
		for (int i = 0; i < result.length; i++) {
			result [i] = this.orderedColumns [i].getIndex ();
		}
	} else {
		for (int i = 0; i < this.columns.length; i++) {
			result [i] = i;
		}
	}
	return result;
}
/**
 * Returns an array of <code>TableColumn</code>s which are the
 * columns in the receiver.  Columns are returned in the order
 * that they were created.  If no <code>TableColumn</code>s were
 * created by the programmer, the array is empty, despite the fact
 * that visually, one column of items may be visible. This occurs
 * when the programmer uses the table like a list, adding items but
 * never creating a column.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver.
 * </p>
 *
 * @return the items in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#getColumnOrder()
 * @see CTable#setColumnOrder(int[])
 * @see CTableColumn#getMoveable()
 * @see CTableColumn#setMoveable(boolean)
 * @see SWT#Move
 */
public CTableColumn[] getColumns () {
	this.checkWidget ();
	final CTableColumn[] result = new CTableColumn [this.columns.length];
	System.arraycopy (this.columns, 0, result, 0, this.columns.length);
	return result;
}
Image getGrayUncheckedImage () {
	return (Image) this.display.getData (ID_GRAYUNCHECKED);
}
/**
 * Returns the width in pixels of a grid line.
 *
 * @return the width of a grid line in pixels
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getGridLineWidth () {
	this.checkWidget ();
	return 1;
}
/**
 * Returns the height of the receiver's header
 *
 * @return the height of the header or zero if the header is not visible
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 2.0
 */
public int getHeaderHeight () {
	this.checkWidget ();
	if (!this.header.getVisible ()) {
    return 0;
  }
	return this.header.getSize ().y;
}
int getHeaderPadding () {
	return MARGIN_CELL + WIDTH_HEADER_SHADOW;
}
/**
 * Returns <code>true</code> if the receiver's header is visible,
 * and <code>false</code> otherwise.
 * <p>
 * If one of the receiver's ancestors is not visible or some
 * other condition makes the receiver not visible, this method
 * may still indicate that it is considered visible even though
 * it may not actually be showing.
 * </p>
 *
 * @return the receiver's header's visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getHeaderVisible () {
	this.checkWidget ();
	return this.header.getVisible ();
}
/**
 * Returns the item at the given, zero-relative index in the
 * receiver. Throws an exception if the index is out of range.
 *
 * @param index the index of the item to return
 * @return the item at the given index
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public CTableItem getItem (final int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    SWT.error (SWT.ERROR_INVALID_RANGE);
  }
	return this.items [index];
}
/**
 * Returns the item at the given point in the receiver
 * or null if no such item exists. The point is in the
 * coordinate system of the receiver.
 * <p>
 * The item that is returned represents an item that could be selected by the user.
 * For example, if selection only occurs in items in the first column, then null is
 * returned if the point is outside of the item.
 * Note that the SWT.FULL_SELECTION style hint, which specifies the selection policy,
 * determines the extent of the selection.
 * </p>
 *
 * @param point the point used to locate the item
 * @return the item at the given point, or null if the point is not in a selectable item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public CTableItem getItem (final Point point) {
	this.checkWidget ();
	if (point == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	final int index = ((point.y - this.getHeaderHeight ()) / this.itemHeight) + this.topIndex;
	if (!((0 <= index) && (index < this.itemsCount))) {
    return null;		/* below the last item */
  }
	final CTableItem result = this.items [index];
	if (!result.getHitBounds ().contains (point)) {
    return null;	/* considers the x value */
  }
	return result;
}
/**
 * Returns the number of items contained in the receiver.
 *
 * @return the number of items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getItemCount () {
	this.checkWidget ();
	return this.itemsCount;
}
/**
 * Returns the height of the area which would be used to
 * display <em>one</em> of the items in the receiver.
 *
 * @return the height of one item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getItemHeight () {
	this.checkWidget ();
	return this.itemHeight;
}
/**
 * Returns a (possibly empty) array of <code>TableItem</code>s which
 * are the items in the receiver.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver.
 * </p>
 *
 * @return the items in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public CTableItem[] getItems () {
	this.checkWidget ();
	final CTableItem[] result = new CTableItem [this.itemsCount];
	System.arraycopy (this.items, 0, result, 0, this.itemsCount);
	return result;
}
/*
 * Returns the current y-coordinate that the specified item should have.
 */
int getItemY (final CTableItem item) {
	return ((item.index - this.topIndex) * this.itemHeight) + this.getHeaderHeight ();
}
/**
 * Returns <code>true</code> if the receiver's lines are visible,
 * and <code>false</code> otherwise. Note that some platforms draw
 * grid lines while others may draw alternating row colors.
 * <p>
 * If one of the receiver's ancestors is not visible or some
 * other condition makes the receiver not visible, this method
 * may still indicate that it is considered visible even though
 * it may not actually be showing.
 * </p>
 *
 * @return the visibility state of the lines
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getLinesVisible () {
	this.checkWidget ();
	return this.linesVisible;
}
CTableColumn[] getOrderedColumns () {
	if (this.orderedColumns != null) {
    return this.orderedColumns;
  }
	return this.columns;
}
/**
 * Returns an array of <code>TableItem</code>s that are currently
 * selected in the receiver. The order of the items is unspecified.
 * An empty array indicates that no items are selected.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its selection, so modifying the array will
 * not affect the receiver.
 * </p>
 * @return an array representing the selection
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public CTableItem[] getSelection () {
	this.checkWidget ();
	final CTableItem[] result = new CTableItem [this.selectedItems.length];
	System.arraycopy (this.selectedItems, 0, result, 0, this.selectedItems.length);
	this.sortAscent (result);
	return result;
}
/**
 * Returns the number of selected items contained in the receiver.
 *
 * @return the number of selected items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getSelectionCount () {
	this.checkWidget ();
	return this.selectedItems.length;
}
/**
 * Returns the zero-relative index of the item which is currently
 * selected in the receiver, or -1 if no item is selected.
 *
 * @return the index of the selected item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getSelectionIndex () {
	this.checkWidget ();
	if (this.selectedItems.length == 0) {
    return -1;
  }
	return this.selectedItems [0].index;
}
/*
 * Returns the index of the argument in the receiver's array of currently-
 * selected items, or -1 if the item is not currently selected.
 */
int getSelectionIndex (final CTableItem item) {
	for (int i = 0; i < this.selectedItems.length; i++) {
		if (this.selectedItems [i] == item) {
      return i;
    }
	}
	return -1;
}
/**
 * Returns the zero-relative indices of the items which are currently
 * selected in the receiver. The order of the indices is unspecified.
 * The array is empty if no items are selected.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its selection, so modifying the array will
 * not affect the receiver.
 * </p>
 * @return the array of indices of the selected items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int [] getSelectionIndices () {
	this.checkWidget ();
	final int[] result = new int [this.selectedItems.length];
	for (int i = 0; i < this.selectedItems.length; i++) {
		result [i] = this.selectedItems [i].index;
	}
	this.sortAscent (result);
	return result;
}
/**
 * Returns the column which shows the sort indicator for
 * the receiver. The value may be null if no column shows
 * the sort indicator.
 *
 * @return the sort indicator
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setSortColumn(CTableColumn)
 *
 * @since 3.2
 */
public CTableColumn getSortColumn () {
	this.checkWidget ();
	return this.sortColumn;
}
/**
 * Returns the direction of the sort indicator for the receiver.
 * The value will be one of <code>UP</code>, <code>DOWN</code>
 * or <code>NONE</code>.
 *
 * @return the sort direction
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setSortDirection(int)
 *
 * @since 3.2
 */
public int getSortDirection () {
	this.checkWidget ();
	return this.sortDirection;
}
/**
 * Returns the zero-relative index of the item which is currently
 * at the top of the receiver. This index can change when items are
 * scrolled or new items are added or removed.
 *
 * @return the index of the top item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTopIndex () {
	this.checkWidget ();
	return this.topIndex;
}
Image getUncheckedImage () {
	return (Image) this.display.getData (ID_UNCHECKED);
}
void handleEvents (final Event event) {
	switch (event.type) {
		case SWT.Paint:
			if (event.widget == this.header) {
				this.headerOnPaint (event);
			} else {
				this.onPaint (event);
			}
			break;
		case SWT.MenuDetect: {
			this.notifyListeners (SWT.MenuDetect, event);
			break;
			}
		case SWT.MouseDown:
			if (event.widget == this.header) {
				this.headerOnMouseDown (event);
			} else {
				this.onMouseDown (event);
			}
			break;
		case SWT.MouseUp:
			if (event.widget == this.header) {
				this.headerOnMouseUp (event);
			} else {
				this.onMouseUp (event);
			}
			break;
		case SWT.MouseHover:
			this.headerOnMouseHover (event); break;
		case SWT.MouseMove:
			this.headerOnMouseMove (event); break;
		case SWT.MouseDoubleClick:
			if (event.widget == this.header) {
				this.headerOnMouseDoubleClick (event);
			} else {
				this.onMouseDoubleClick (event);
			}
			break;
		case SWT.MouseExit:
			this.headerOnMouseExit (); break;
		case SWT.Dispose:
			this.onDispose (event); break;
		case SWT.KeyDown:
			this.onKeyDown (event); break;
		case SWT.Resize:
			this.onResize (event); break;
		case SWT.Selection:
			if (event.widget == this.getHorizontalBar ()) {
				this.onScrollHorizontal (event);
			}
			if (event.widget == this.getVerticalBar ()) {
				this.onScrollVertical (event);
			}
			break;
		case SWT.FocusOut:
			this.onFocusOut (); break;
		case SWT.FocusIn:
			this.onFocusIn (); break;
		case SWT.Traverse:
			switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
				case SWT.TRAVERSE_RETURN:
				case SWT.TRAVERSE_TAB_NEXT:
				case SWT.TRAVERSE_TAB_PREVIOUS:
				case SWT.TRAVERSE_PAGE_NEXT:
				case SWT.TRAVERSE_PAGE_PREVIOUS:
					event.doit = true;
					break;
			}
			break;
	}
}
String headerGetToolTip (final int x) {
	if (this.resizeColumn != null) {
    return null;
  }
	final int orderedIndex = this.computeColumnIntersect (x, 0);
	if (orderedIndex == -1) {
    return null;
  }
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	final CTableColumn column = orderedColumns [orderedIndex];
	if (column.toolTipText == null) {
    return null;
  }

	/* no tooltip should appear if the hover is at a column resize opportunity */
	final int columnX = column.getX ();
	if ((orderedIndex > 0) && orderedColumns [orderedIndex - 1].resizable) {
		/* left column bound is resizable */
		if ((x - columnX) <= TOLLERANCE_COLUMNRESIZE) {
      return null;
    }
	}
	if (column.resizable) {
		/* right column bound is resizable */
		final int columnRightX = columnX + column.width;
		if ((columnRightX - x) <= TOLLERANCE_COLUMNRESIZE) {
      return null;
    }
	}
	return this.removeMnemonics (column.toolTipText);
}
void headerHideToolTip() {
	if (this.toolTipShell == null) {
    return;
  }
	for (final int toolTipEvent : toolTipEvents) {
		this.header.removeListener (toolTipEvent, this.toolTipListener);
	}
	this.toolTipShell.dispose ();
	this.toolTipShell = null;
	this.toolTipLabel = null;
}
void headerOnMouseDoubleClick (final Event event) {
	if (!this.isFocusControl ()) {
    this.setFocus ();
  }
	if (this.columns.length == 0) {
    return;
  }
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	int x = -this.horizontalOffset;
	for (int i = 0; i < orderedColumns.length; i++) {
		final CTableColumn column = orderedColumns [i];
		x += column.width;
		if (event.x < x) {
			/* found the clicked column */
			CTableColumn packColumn = null;
			if ((x - event.x) <= TOLLERANCE_COLUMNRESIZE) {
				/* clicked on column bound for this column */
				packColumn = column;
			} else if ((i > 0) && ((event.x - column.getX ()) <= TOLLERANCE_COLUMNRESIZE)) {
      	/* clicked on column bound that applies to previous column */
      	packColumn = orderedColumns [i - 1];
      }
			if (packColumn != null) {
				packColumn.pack ();
				this.resizeColumn = null;
				if (Math.abs ((packColumn.getX () + packColumn.width) - event.x) > TOLLERANCE_COLUMNRESIZE) {
					/* column separator has relocated away from pointer location */
					this.setCursor (null);
				}
				return;
			}
			/* did not click on column separator, so just fire column event */
			final Event newEvent = new Event ();
			newEvent.widget = column;
			column.notifyListeners (SWT.DefaultSelection, newEvent);
			return;
		}
	}
}
void headerOnMouseDown (final Event event) {
	if (event.button != 1) {
    return;
  }
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	int x = -this.horizontalOffset;
	for (final CTableColumn column : orderedColumns) {
		x += column.width;
		/* if close to a resizable column separator line then begin column resize */
		if (column.resizable && (Math.abs (x - event.x) <= TOLLERANCE_COLUMNRESIZE)) {
			this.resizeColumn = column;
			this.resizeColumnX = x;
			return;
		}
		/*
		 * If within column but not near separator line then start column drag
		 * if column is moveable, or just fire column Selection otherwise.
		 */
		if (event.x < x) {
			if (column.moveable) {
				/* open tracker on the dragged column's header cell */
				final int columnX = column.getX ();
				final int pointerOffset = event.x - columnX;
				this.headerHideToolTip ();
				final Tracker tracker = new Tracker (this, SWT.NONE);
				tracker.setRectangles (new Rectangle[] {
					new Rectangle (columnX, 0, column.width, this.getHeaderHeight ())
				});
				if (!tracker.open ()) {
          return;	/* cancelled */
        }
				/* determine which column was dragged onto */
				final Rectangle result = tracker.getRectangles () [0];
				final int pointerX = result.x + pointerOffset;
				if (pointerX < 0) {
          return;	/* dragged too far left */
        }
				x = -this.horizontalOffset;
				for (int destIndex = 0; destIndex < orderedColumns.length; destIndex++) {
					final CTableColumn destColumn = orderedColumns [destIndex];
					x += destColumn.width;
					if (pointerX < x) {
						final int oldIndex = column.getOrderIndex ();
						if (destIndex == oldIndex) {	/* dragged onto self */
							final Event newEvent = new Event ();
							newEvent.widget = column;
							column.notifyListeners (SWT.Selection, newEvent);
							return;
						}
						final int leftmostIndex = Math.min (destIndex, oldIndex);
						final int[] oldOrder = this.getColumnOrder ();
						final int[] newOrder = new int [oldOrder.length];
						System.arraycopy (oldOrder, 0, newOrder, 0, leftmostIndex);
						if (leftmostIndex == oldIndex) {
							/* column moving to the right */
							System.arraycopy (oldOrder, oldIndex + 1, newOrder, oldIndex, destIndex - oldIndex);
						} else {
							/* column moving to the left */
							System.arraycopy (oldOrder, destIndex, newOrder, destIndex + 1, oldIndex - destIndex);
						}
						newOrder [destIndex] = oldOrder [oldIndex];
						final int rightmostIndex = Math.max (destIndex, oldIndex);
						System.arraycopy (
							oldOrder,
							rightmostIndex + 1,
							newOrder,
							rightmostIndex + 1,
							newOrder.length - rightmostIndex - 1);
						this.setColumnOrder (newOrder);
						return;
					}
				}
				return;		/* dragged too far right */
			}
			/* column is not moveable */
			final Event newEvent = new Event ();
			newEvent.widget = column;
			column.notifyListeners (SWT.Selection, newEvent);
			return;
		}
	}
}
void headerOnMouseExit () {
	if (this.resizeColumn != null) {
    return;
  }
	this.setCursor (null);	/* ensure that a column resize cursor does not escape */
}
void headerOnMouseHover (final Event event) {
	this.headerShowToolTip (event.x);
}
void headerOnMouseMove (final Event event) {
	if (this.resizeColumn == null) {
		/* not currently resizing a column */
		for (final CTableColumn column : this.columns) {
			final int x = column.getX () + column.width;
			if (Math.abs (x - event.x) <= TOLLERANCE_COLUMNRESIZE) {
				if (column.resizable) {
					this.setCursor (this.display.getSystemCursor (SWT.CURSOR_SIZEWE));
				} else {
					this.setCursor (null);
				}
				return;
			}
		}
		this.setCursor (null);
		return;
	}

	/* currently resizing a column */

	/* don't allow the resize x to move left of the column's x position */
	if (event.x <= this.resizeColumn.getX ()) {
    return;
  }

	/* redraw the resizing line at its new location */
	final GC gc = new GC (this);
	gc.setForeground (this.display.getSystemColor (SWT.COLOR_BLACK));
	final int lineHeight = this.clientArea.height;
	this.redraw (this.resizeColumnX - 1, 0, 1, lineHeight, false);
	this.resizeColumnX = event.x;
	gc.drawLine (this.resizeColumnX - 1, 0, this.resizeColumnX - 1, lineHeight);
	gc.dispose ();
}
void headerOnMouseUp (final Event event) {
	if (this.resizeColumn == null) {
    return;	/* not resizing a column */
  }

	/* remove the resize line */
	final GC gc = new GC (this);
	this.redraw (this.resizeColumnX - 1, 0, 1, this.clientArea.height, false);
	gc.dispose ();

	final int newWidth = this.resizeColumnX - this.resizeColumn.getX ();
	if (newWidth != this.resizeColumn.width) {
		this.setCursor (null);
		this.updateColumnWidth (this.resizeColumn, newWidth);
	}
	this.resizeColumnX = -1;
	this.resizeColumn = null;
}
void headerOnPaint (final Event event) {
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	final int numColumns = orderedColumns.length;
	final GC gc = event.gc;
	final Rectangle clipping = gc.getClipping ();
	int startColumn = -1, endColumn = -1;
	if (numColumns > 0) {
		startColumn = this.computeColumnIntersect (clipping.x, 0);
		if (startColumn != -1) {	/* the clip x is within a column's bounds */
			endColumn = this.computeColumnIntersect (clipping.x + clipping.width, startColumn);
			if (endColumn == -1) {
        endColumn = numColumns - 1;
      }
		}
	} else {
		startColumn = endColumn = 0;
	}

	/* paint the column header shadow that spans the full header width */
	final Point headerSize = this.header.getSize ();
	this.headerPaintHShadows (gc, 0, 0, headerSize.x, headerSize.y);

	/* if all damage is to the right of the last column then finished */
	/* paint each of the column headers */
	if ((startColumn == -1) || (numColumns == 0)) {
    return;	/* no headers to paint */
  }
	for (int i = startColumn; i <= endColumn; i++) {
		this.headerPaintVShadows (gc, orderedColumns [i].getX (), 0, orderedColumns [i].width, headerSize.y);
		orderedColumns [i].paint (gc);
	}
}
void headerPaintHShadows (final GC gc, final int x, final int y, final int width, final int height) {
	gc.setClipping (x, y, width, height);
	final int endX = x + width;
	gc.setForeground (this.display.getSystemColor (SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
	gc.drawLine (x, y, endX, y);					/* highlight shadow */
	gc.setForeground (this.display.getSystemColor (SWT.COLOR_WIDGET_NORMAL_SHADOW));
	gc.drawLine (x, height - 2, endX, height - 2);	/* lowlight shadow */
	gc.setForeground (this.display.getSystemColor (SWT.COLOR_WIDGET_DARK_SHADOW));
	gc.drawLine (x, height - 1, endX, height - 1);	/* outer shadow */
}
void headerPaintVShadows (final GC gc, final int x, final int y, final int width, final int height) {
	gc.setClipping (x, y, width, height);
	final int endX = x + width;
	gc.setForeground (this.display.getSystemColor (SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
	gc.drawLine (x, y, x, (y + height) - 1);					/* highlight shadow */
	gc.setForeground (this.display.getSystemColor (SWT.COLOR_WIDGET_NORMAL_SHADOW));
	gc.drawLine (endX - 2, y + 1, endX - 2, height - 2);	/* light inner shadow */
	gc.setForeground (this.display.getSystemColor (SWT.COLOR_WIDGET_DARK_SHADOW));
	gc.drawLine (endX - 1, y, endX - 1, height - 1);		/* dark outer shadow */
}
void headerShowToolTip (final int x) {
	final String tooltip = this.headerGetToolTip (x);
	if ((tooltip == null) || (tooltip.length () == 0)) {
    return;
  }

	if (this.toolTipShell == null) {
		this.toolTipShell = new Shell (this.getShell (), SWT.ON_TOP | SWT.TOOL);
		this.toolTipLabel = new Label (this.toolTipShell, SWT.CENTER);
		final Display display = this.toolTipShell.getDisplay ();
		this.toolTipLabel.setForeground (display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
		this.toolTipLabel.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
		for (final int toolTipEvent : toolTipEvents) {
			this.header.addListener (toolTipEvent, this.toolTipListener);
		}
	}
	if (this.headerUpdateToolTip (x)) {
		this.toolTipShell.setVisible (true);
	} else {
		this.headerHideToolTip ();
	}
}
boolean headerUpdateToolTip (final int x) {
	final String tooltip = this.headerGetToolTip (x);
	if ((tooltip == null) || (tooltip.length () == 0)) {
    return false;
  }
	if (tooltip.equals (this.toolTipLabel.getText ())) {
    return true;
  }

	this.toolTipLabel.setText (tooltip);
	final CTableColumn column = this.getOrderedColumns () [this.computeColumnIntersect (x, 0)];
	this.toolTipShell.setData (Integer.valueOf (column.getIndex ()));
	final Point labelSize = this.toolTipLabel.computeSize (SWT.DEFAULT, SWT.DEFAULT, true);
	labelSize.x += 2; labelSize.y += 2;
	this.toolTipLabel.setSize (labelSize);
	this.toolTipShell.pack ();
	/*
	 * On some platforms, there is a minimum size for a shell
	 * which may be greater than the label size.
	 * To avoid having the background of the tip shell showing
	 * around the label, force the label to fill the entire client area.
	 */
	final Rectangle area = this.toolTipShell.getClientArea ();
	this.toolTipLabel.setSize (area.width, area.height);

	/* Position the tooltip and ensure it's not located off the screen */
	final Point cursorLocation = this.getDisplay ().getCursorLocation ();
	final int cursorHeight = 21;	/* assuming cursor is 21x21 */
	final Point size = this.toolTipShell.getSize ();
	final Rectangle rect = this.getMonitor ().getBounds ();
	final Point pt = new Point (cursorLocation.x, cursorLocation.y + cursorHeight + 2);
	pt.x = Math.max (pt.x, rect.x);
	if ((pt.x + size.x) > (rect.x + rect.width)) {
    pt.x = (rect.x + rect.width) - size.x;
  }
	if ((pt.y + size.y) > (rect.y + rect.height)) {
    pt.y = cursorLocation.y - 2 - size.y;
  }
	this.toolTipShell.setLocation (pt);
	return true;
}
/**
 * Searches the receiver's list starting at the first column
 * (index 0) until a column is found that is equal to the
 * argument, and returns the index of that column. If no column
 * is found, returns -1.
 *
 * @param column the search column
 * @return the index of the column
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the column is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (final CTableColumn column) {
	this.checkWidget ();
	if (column == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (column.parent != this) {
    return -1;
  }
	return column.getIndex ();
}
/**
 * Searches the receiver's list starting at the first item
 * (index 0) until an item is found that is equal to the
 * argument, and returns the index of that item. If no item
 * is found, returns -1.
 *
 * @param item the search item
 * @return the index of the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (final CTableItem item) {
	this.checkWidget ();
	if (item == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (item.parent != this) {
    return -1;
  }
	return item.index;
}

void initAccessibility () {
	// TODO: does this all work if CTable is virtual?
	final Accessible accessibleTable = this.getAccessible();
	accessibleTable.addAccessibleListener(new AccessibleAdapter() {
		@Override
		public void getName(final AccessibleEvent e) {
			/* CTables take their name from the preceding Label, if any. */
			final Control[] siblings = CTable.this.getParent().getChildren();
			for (int i = 0; i < siblings.length; i++) {
				if ((i != 0) && (siblings[i] == CTable.this)) {
					final Control control = siblings[i-1];
					if (control instanceof Label) {
						e.result = ((Label) control).getText();
					}
				}
			}
		}
		@Override
		public void getHelp(final AccessibleEvent e) {
			/* A CTable's toolTip text (if any) can be used as its help text. */
			e.result = CTable.this.getToolTipText();
		}
	});
	accessibleTable.addAccessibleControlListener(new AccessibleControlAdapter() {
		/* Child IDs are assigned as follows:
		 * - column header ids (if any) are numbered from 0 to columnCount - 1
		 * - cell ids are numbered in row-major order (starting from columnCount if there are columns)
		 * Accessibles are returned in getChild.
		 */
		@Override
		public void getChild(final AccessibleControlEvent e) {
			int childID = e.childID;
			if (childID == ACC.CHILDID_CHILD_AT_INDEX)
       {
        childID = e.detail; // childID == index
      }
			if ((CTable.this.columns.length > 0) && (0 <= childID) && (childID < CTable.this.columns.length)) { // header cell
				final CTableColumn column = CTable.this.columns [childID];
				e.accessible = column.getAccessible(accessibleTable);
			} else { // item cell
				final int columnCount = CTable.this.columns.length > 0 ? CTable.this.columns.length : 1;
				if (CTable.this.columns.length > 0) {
          childID -= columnCount;
        }
				if ((0 <= childID) && (childID < (CTable.this.itemsCount * columnCount))) {
					final int rowIndex = childID / columnCount;
					final int columnIndex = childID - (rowIndex * columnCount);
					e.accessible = CTable.this.items[rowIndex].getAccessible (accessibleTable, columnIndex);
				}
			}
		}
		@Override
		public void getChildAtPoint(final AccessibleControlEvent e) {
			final Point point = CTable.this.toControl(e.x, e.y);
			if ((CTable.this.columns.length > 0) && (point.y < CTable.this.getHeaderHeight ())) { // header cell
				final int columnIndex = CTable.this.computeColumnIntersect (point.x, 0);
				if (columnIndex != -1) {
					final CTableColumn column = CTable.this.columns [columnIndex];
					e.accessible = column.getAccessible (accessibleTable);
				}
			} else { // item cell
				final int columnIndex = CTable.this.columns.length > 0 ? CTable.this.computeColumnIntersect (point.x, 0) : 0;
				if (columnIndex != -1) {
					final int rowIndex = ((point.y - CTable.this.getHeaderHeight ()) / CTable.this.itemHeight) + CTable.this.topIndex;
					if ((0 <= rowIndex) && (rowIndex < CTable.this.itemsCount)) {
						if (CTable.this.items [rowIndex].getHitBounds ().contains (point)) {  /* considers the x value */
							e.accessible = CTable.this.items[rowIndex].getAccessible (accessibleTable, columnIndex);
						}
					}
				}
			}
		}
		@Override
		public void getChildCount(final AccessibleControlEvent e) {
			e.detail = CTable.this.columns.length > 0 ? CTable.this.columns.length + (CTable.this.itemsCount * CTable.this.columns.length) : CTable.this.itemsCount;
		}
		@Override
		public void getChildren(final AccessibleControlEvent e) {
			final int childIdCount = CTable.this.columns.length > 0 ? CTable.this.columns.length + (CTable.this.itemsCount * CTable.this.columns.length) : CTable.this.itemsCount;
			final Object[] children = new Object[childIdCount];
			for (int i = 0; i < childIdCount; i++) {
				children[i] = Integer.valueOf(i);
			}
			e.children = children;
		}
		@Override
		public void getFocus(final AccessibleControlEvent e) {
			e.childID = CTable.this.isFocusControl() ? ACC.CHILDID_SELF : ACC.CHILDID_NONE;
		}
		@Override
		public void getLocation(final AccessibleControlEvent e) {
			Rectangle location = null;
			Point pt = null;
			int childID = e.childID;
			if (childID == ACC.CHILDID_SELF) { // table
				location = CTable.this.getBounds();
				pt = CTable.this.getParent().toDisplay(location.x, location.y);
			} else if ((CTable.this.columns.length > 0) && (0 <= childID) && (childID < CTable.this.columns.length)) { // header cell
				final CTableColumn column = CTable.this.columns [childID];
				location = new Rectangle (column.getX (), 0, column.getWidth(), CTable.this.getHeaderHeight());
				pt = CTable.this.toDisplay(location.x, location.y);
			} else { // item cell
				final int columnCount = CTable.this.columns.length > 0 ? CTable.this.columns.length : 1;
				if (CTable.this.columns.length > 0) {
          childID -= columnCount;
        }
				if ((0 <= childID) && (childID < (CTable.this.itemsCount * columnCount))) {
					final int rowIndex = childID / columnCount;
					final int columnIndex = childID - (rowIndex * columnCount);
					location = CTable.this.items[rowIndex].getBounds(columnIndex);
					pt = CTable.this.toDisplay(location.x, location.y);
				}
			}
			if ((location != null) && (pt != null)) {
				e.x = pt.x;
				e.y = pt.y;
				e.width = location.width;
				e.height = location.height;
			}
		}
		@Override
		public void getRole(final AccessibleControlEvent e) {
			e.detail = e.childID == ACC.CHILDID_SELF ? ACC.ROLE_TABLE : ACC.ROLE_TABLECELL;
		}
		@Override
		public void getSelection(final AccessibleControlEvent e) {
			final int columnCount = CTable.this.columns.length > 0 ? CTable.this.columns.length : 1;
			final int [] selectionIndices = CTable.this.getSelectionIndices();
			final Object[] selectedChildren = new Object[selectionIndices.length * columnCount];
			for (int i = 0; i < selectionIndices.length; i++) {
				final int row = selectionIndices[i];
				for (int col = 0; col < columnCount; col++) {
					selectedChildren[i] = Integer.valueOf((row * columnCount) + col);
				}
			}
			e.children = selectedChildren;
		}
		@Override
		public void getState(final AccessibleControlEvent e) {
			int state = ACC.STATE_NORMAL;
			int childID = e.childID;
			if (childID == ACC.CHILDID_SELF) { // table
				state |= ACC.STATE_FOCUSABLE;
				if (CTable.this.isFocusControl()) {
					state |= ACC.STATE_FOCUSED;
				}
			} else if ((CTable.this.columns.length > 0) && (0 <= childID) && (childID < CTable.this.columns.length)) { // header cell
				/* CTable does not support header cell focus or selection. */
				state |= ACC.STATE_SIZEABLE;
			} else { // item cell
				final int columnCount = CTable.this.columns.length > 0 ? CTable.this.columns.length : 1;
				if (CTable.this.columns.length > 0) {
          childID -= columnCount;
        }
				if ((0 <= childID) && (childID < (CTable.this.itemsCount * columnCount))) {
					/* CTable does not support cell selection (only row selection). */
					final int rowIndex = childID / columnCount;
					state |= ACC.STATE_SELECTABLE;
					if (CTable.this.isFocusControl()) {
						state |= ACC.STATE_FOCUSABLE;
					}
					if (CTable.this.items[rowIndex].isSelected()) {
						state |= ACC.STATE_SELECTED;
						if (CTable.this.isFocusControl()) {
							state |= ACC.STATE_FOCUSED;
						}
					}
				}
			}
			e.detail = state;
		}
	});
	accessibleTable.addAccessibleTableListener(new AccessibleTableAdapter() {
		@Override
		public void deselectColumn(final AccessibleTableEvent e) {
			/* CTable does not support column selection. */
		}
		@Override
		public void deselectRow(final AccessibleTableEvent e) {
			CTable.this.deselect(e.row);
			e.result = ACC.OK;
		}
		@Override
		public void getCaption(final AccessibleTableEvent e) {
			// TODO: What is a caption? How does it differ from name? Should app supply?
			e.result = "This is the Custom Table's Test Caption";
		}
		@Override
		public void getCell(final AccessibleTableEvent e) {
			int index = e.row;
			if ((0 <= index) && (index < CTable.this.itemsCount)) {
				final CTableItem row = CTable.this.items [index];
				index = e.column;
				if ((CTable.this.columns.length == 0) || ((0 <= index) && (index < CTable.this.columns.length))) {
					e.accessible = row.getAccessible (accessibleTable, index);
				}
			}
		}
		@Override
		public void getColumnCount(final AccessibleTableEvent e) {
			final int columnCount = CTable.this.columns.length > 0 ? CTable.this.columns.length : 1;
			e.count = columnCount;
		}
		@Override
		public void getColumnDescription(final AccessibleTableEvent e) {
			// TODO: What is a description? How does it differ from name? Should app supply?
			e.result = "This is the Custom Table's Test Description for column " + e.column;
		}
//		public void getColumnHeader(AccessibleTableEvent e) {
//			e.accessible = header.getAccessible();
//		}
		@Override
		public void getColumnHeaderCells(final AccessibleTableEvent e) {
			if (CTable.this.columns.length == 0) {
				/* The CTable is being used as a list, and there are no headers. */
				e.accessibles = null;
			} else {
				final Accessible[] accessibles = new Accessible[CTable.this.columns.length];
				for (int i = 0; i < CTable.this.columns.length; i++) {
					final CTableColumn column = CTable.this.columns [i];
					accessibles[i] = column.getAccessible (accessibleTable);
				}
				e.accessibles = accessibles;
			}
		}
		@Override
		public void getRowCount(final AccessibleTableEvent e) {
			e.count = CTable.this.itemsCount;
		}
		@Override
		public void getRowDescription(final AccessibleTableEvent e) {
			// TODO: What is a description? How does it differ from name? Should app supply?
			e.result = "This is the Custom Table's Test Description for row " + e.row;
		}
		@Override
		public void getRowHeader(final AccessibleTableEvent e) {
			/* CTable does not support row headers. */
		}
		@Override
		public void getSelectedCellCount(final AccessibleTableEvent e) {
			final int columnCount = CTable.this.columns.length > 0 ? CTable.this.columns.length : 1;
			e.count = CTable.this.selectedItems.length * columnCount;
		}
		@Override
		public void getSelectedCells(final AccessibleTableEvent e) {
			final int columnCount = CTable.this.columns.length > 0 ? CTable.this.columns.length : 1;
			final Accessible[] accessibles = new Accessible[CTable.this.selectedItems.length * columnCount];
			for (int r = 0; r < CTable.this.selectedItems.length; r++) {
				final CTableItem row = CTable.this.selectedItems [r];
				for (int c = 0; c < columnCount; c++) {
          accessibles[r+c] = row.getAccessible (accessibleTable, c);
        }
			}
			e.accessibles = accessibles;
		}
		@Override
		public void getSelectedColumnCount(final AccessibleTableEvent e) {
			e.count = 0; /* CTable does not support column selection. */
		}
		@Override
		public void getSelectedColumns(final AccessibleTableEvent e) {
			/* CTable does not support column selection. */
		}
		@Override
		public void getSelectedRowCount(final AccessibleTableEvent e) {
			e.count = CTable.this.selectedItems.length;
		}
		@Override
		public void getSelectedRows(final AccessibleTableEvent e) {
			final int[] selectedIndices = new int[CTable.this.selectedItems.length];
			for (int i = 0; i < CTable.this.selectedItems.length; i++) {
				selectedIndices[i] = CTable.this.selectedItems [i].index;
			}
			e.selected = selectedIndices;
		}
		@Override
		public void getSummary(final AccessibleTableEvent e) {
			// TODO: What is a summary? How does it differ from name? Should app supply?
			e.result = "This is the Custom Table's Summary";
		}
		@Override
		public void isColumnSelected(final AccessibleTableEvent e) {
			e.isSelected = false; /* CTable does not support column selection. */
		}
		@Override
		public void isRowSelected(final AccessibleTableEvent e) {
			e.isSelected = CTable.this.isSelected(e.row);
		}
		@Override
		public void selectColumn(final AccessibleTableEvent e) {
			/* CTable does not support column selection. */
		}
		@Override
		public void selectRow(final AccessibleTableEvent e) {
			CTable.this.select(e.row);
			e.result = ACC.OK;
		}
		@Override
		public void setSelectedColumn(final AccessibleTableEvent e) {
			/* CTable does not support column selection. */
		}
		@Override
		public void setSelectedRow(final AccessibleTableEvent e) {
			CTable.this.setSelection(e.row);
			e.result = ACC.OK;
		}
	});
}

static void initImages (final Display display) {
	final PaletteData arrowPalette = new PaletteData (new RGB (0, 0, 0), new RGB (255, 255, 255));
	if (display.getData (ID_ARROWDOWN) == null) {
		final ImageData arrowDown = new ImageData (
			7, 4, 1,
			arrowPalette, 1,
			new byte[] {0x00, (byte)0x83, (byte)0xC7, (byte)0xEF});
		arrowDown.transparentPixel = 0x1;	/* use white for transparency */
		display.setData (ID_ARROWDOWN, new Image (display, arrowDown));
	}
	if (display.getData (ID_ARROWUP) == null) {
		final ImageData arrowUp = new ImageData (
			7, 4, 1,
			arrowPalette, 1,
			new byte[] {(byte)0xEF, (byte)0xC7, (byte)0x83, 0x00});
		arrowUp.transparentPixel = 0x1;		/* use white for transparency */
		display.setData (ID_ARROWUP, new Image (display, arrowUp));
	}

	final PaletteData checkMarkPalette = new PaletteData (new RGB (0, 0, 0), new RGB (252, 3, 251));
	final byte[] checkbox = new byte[] {0, 0, 127, -64, 127, -64, 127, -64, 127, -64, 127, -64, 127, -64, 127, -64, 127, -64, 127, -64, 0, 0};
	final ImageData checkmark = new ImageData (7, 7, 1, checkMarkPalette, 1, new byte[] {-4, -8, 112, 34, 6, -114, -34});
	checkmark.transparentPixel = 1;
	if (display.getData (ID_CHECKMARK) == null) {
		display.setData (ID_CHECKMARK, new Image (display, checkmark));
	}

	if (display.getData (ID_UNCHECKED) == null) {
		final PaletteData uncheckedPalette = new PaletteData (new RGB (128, 128, 128), new RGB (255, 255, 255));
		final ImageData unchecked = new ImageData (11, 11, 1, uncheckedPalette, 2, checkbox);
		display.setData (ID_UNCHECKED, new Image (display, unchecked));
	}

	if (display.getData (ID_GRAYUNCHECKED) == null) {
		final PaletteData grayUncheckedPalette = new PaletteData (new RGB (128, 128, 128), new RGB (192, 192, 192));
		final ImageData grayUnchecked = new ImageData (11, 11, 1, grayUncheckedPalette, 2, checkbox);
		display.setData (ID_GRAYUNCHECKED, new Image (display, grayUnchecked));
	}

	display.disposeExec (() -> {
		final Image unchecked = (Image) display.getData (ID_UNCHECKED);
		if (unchecked != null) {
      unchecked.dispose ();
    }
		final Image grayUnchecked = (Image) display.getData (ID_GRAYUNCHECKED);
		if (grayUnchecked != null) {
      grayUnchecked.dispose ();
    }
		final Image checkmark1 = (Image) display.getData (ID_CHECKMARK);
		if (checkmark1 != null) {
      checkmark1.dispose ();
    }
		final Image arrowDown = (Image) display.getData (ID_ARROWDOWN);
		if (arrowDown != null) {
      arrowDown.dispose ();
    }
		final Image arrowUp = (Image) display.getData (ID_ARROWUP);
		if (arrowUp != null) {
      arrowUp.dispose ();
    }

		display.setData (ID_UNCHECKED, null);
		display.setData (ID_GRAYUNCHECKED, null);
		display.setData (ID_CHECKMARK, null);
		display.setData (ID_ARROWDOWN, null);
		display.setData (ID_ARROWUP, null);
	});
}
/**
 * Returns <code>true</code> if the item is selected,
 * and <code>false</code> otherwise.  Indices out of
 * range are ignored.
 *
 * @param index the index of the item
 * @return the selection state of the item at the index
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean isSelected (final int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    return false;
  }
	return this.items [index].isSelected ();
}
@Override
public void notifyListeners (final int eventType, final Event event) {
	super.notifyListeners(eventType, event);
	if ((eventType == SWT.Selection) && (event.detail != SWT.CHECK)) {
    this.getAccessible().selectionChanged();
  }
}
void onArrowDown (final int stateMask) {
	if ((stateMask & (SWT.SHIFT | SWT.CTRL)) == 0) {
		/* Down Arrow with no modifiers */
		final int newFocusIndex = this.focusItem.index + 1;
		if (newFocusIndex == this.itemsCount) {
      return; 	/* at bottom */
    }
		this.selectItem (this.items [newFocusIndex], false);
		this.setFocusItem (this.items [newFocusIndex], true);
		this.redrawItem (newFocusIndex, true);
		this.showItem (this.items [newFocusIndex]);
		final Event newEvent = new Event ();
		newEvent.item = this.items [newFocusIndex];
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	if ((this.getStyle () & SWT.SINGLE) != 0) {
		if ((stateMask & SWT.CTRL) != 0) {
			/* CTRL+Down Arrow, CTRL+Shift+Down Arrow */
			final int visibleItemCount = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
			if (this.itemsCount <= (this.topIndex + visibleItemCount)) {
        return;	/* at bottom */
      }
			this.update ();
			this.topIndex++;
			final ScrollBar vBar = this.getVerticalBar ();
			if (vBar != null) {
        vBar.setSelection (this.topIndex);
      }
			final GC gc = new GC (this);
			gc.copyArea (
				0, 0,
				this.clientArea.width, this.clientArea.height,
				0, -this.itemHeight);
			gc.dispose ();
			return;
		}
		/* Shift+Down Arrow */
		final int newFocusIndex = this.focusItem.index + 1;
		if (newFocusIndex == this.itemsCount) {
      return; 	/* at bottom */
    }
		this.selectItem (this.items [newFocusIndex], false);
		this.setFocusItem (this.items [newFocusIndex], true);
		this.redrawItem (newFocusIndex, true);
		this.showItem (this.items [newFocusIndex]);
		final Event newEvent = new Event ();
		newEvent.item = this.items [newFocusIndex];
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	/* SWT.MULTI */
	if ((stateMask & SWT.CTRL) != 0) {
		if ((stateMask & SWT.SHIFT) != 0) {
			/* CTRL+Shift+Down Arrow */
			final int visibleItemCount = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
			if (this.itemsCount <= (this.topIndex + visibleItemCount)) {
        return;	/* at bottom */
      }
			this.update ();
			this.topIndex++;
			final ScrollBar vBar = this.getVerticalBar ();
			if (vBar != null) {
        vBar.setSelection (this.topIndex);
      }
			final GC gc = new GC (this);
			gc.copyArea (
				0, 0,
				this.clientArea.width, this.clientArea.height,
				0, -this.itemHeight);
			gc.dispose ();
			return;
		}
		/* CTRL+Down Arrow */
		final int focusIndex = this.focusItem.index;
		if (focusIndex == (this.itemsCount - 1)) {
      return;	/* at bottom */
    }
		final CTableItem newFocusItem = this.items [focusIndex + 1];
		this.setFocusItem (newFocusItem, true);
		this.redrawItem (newFocusItem.index, true);
		this.showItem (newFocusItem);
		return;
	}
	/* Shift+Down Arrow */
	final int newFocusIndex = this.focusItem.index + 1;
	if (newFocusIndex == this.itemsCount) {
    return; 	/* at bottom */
  }
	if (this.anchorItem == null) {
    this.anchorItem = this.focusItem;
  }
	if (this.focusItem.index < this.anchorItem.index) {
		this.deselectItem (this.focusItem);
		this.redrawItem (this.focusItem.index, true);
	}
	this.selectItem (this.items [newFocusIndex], true);
	this.setFocusItem (this.items [newFocusIndex], true);
	this.redrawItem (newFocusIndex, true);
	this.showItem (this.items [newFocusIndex]);
	final Event newEvent = new Event ();
	newEvent.item = this.items [newFocusIndex];
	this.notifyListeners (SWT.Selection, newEvent);
}
void onArrowLeft (final int stateMask) {
	if (this.horizontalOffset == 0) {
    return;
  }
	final int newSelection = Math.max (0, this.horizontalOffset - SIZE_HORIZONTALSCROLL);
	this.update ();
	GC gc = new GC (this);
	gc.copyArea (
		0, 0,
		this.clientArea.width, this.clientArea.height,
		this.horizontalOffset - newSelection, 0);
	gc.dispose ();
	if (this.header.getVisible ()) {
		this.header.update ();
		final Rectangle headerClientArea = this.header.getClientArea ();
		gc = new GC (this.header);
		gc.copyArea (
			0, 0,
			headerClientArea.width, headerClientArea.height,
			this.horizontalOffset - newSelection, 0);
		gc.dispose();
	}
	this.horizontalOffset = newSelection;
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar != null) {
    hBar.setSelection (this.horizontalOffset);
  }
}
void onArrowRight (final int stateMask) {
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar == null) {
    return;
  }
	final int maximum = hBar.getMaximum ();
	final int clientWidth = this.clientArea.width;
	if (((this.horizontalOffset + this.clientArea.width) == maximum) || (maximum <= clientWidth)) {
    return;
  }
	final int newSelection = Math.min (this.horizontalOffset + SIZE_HORIZONTALSCROLL, maximum - clientWidth);
	this.update ();
	GC gc = new GC (this);
	gc.copyArea (
		0, 0,
		this.clientArea.width, this.clientArea.height,
		this.horizontalOffset - newSelection, 0);
	gc.dispose ();
	if (this.header.getVisible ()) {
		final Rectangle headerClientArea = this.header.getClientArea ();
		this.header.update ();
		gc = new GC (this.header);
		gc.copyArea (
			0, 0,
			headerClientArea.width, headerClientArea.height,
			this.horizontalOffset - newSelection, 0);
		gc.dispose();
	}
	this.horizontalOffset = newSelection;
	hBar.setSelection (this.horizontalOffset);
}
void onArrowUp (final int stateMask) {
	if ((stateMask & (SWT.SHIFT | SWT.CTRL)) == 0) {
		/* Up Arrow with no modifiers */
		final int newFocusIndex = this.focusItem.index - 1;
		if (newFocusIndex < 0) {
      return; 		/* at top */
    }
		final CTableItem item = this.items [newFocusIndex];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.redrawItem (newFocusIndex, true);
		this.showItem (item);
		final Event newEvent = new Event ();
		newEvent.item = item;
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	if ((this.getStyle () & SWT.SINGLE) != 0) {
		if ((stateMask & SWT.CTRL) != 0) {
			/* CTRL+Up Arrow, CTRL+Shift+Up Arrow */
			if (this.topIndex == 0) {
        return;	/* at top */
      }
			this.update ();
			this.topIndex--;
			final ScrollBar vBar = this.getVerticalBar ();
			if (vBar != null) {
        vBar.setSelection (this.topIndex);
      }
			final GC gc = new GC (this);
			gc.copyArea (
				0, 0,
				this.clientArea.width, this.clientArea.height,
				0, this.itemHeight);
			gc.dispose ();
			return;
		}
		/* Shift+Up Arrow */
		final int newFocusIndex = this.focusItem.index - 1;
		if (newFocusIndex < 0) {
      return; 	/* at top */
    }
		final CTableItem item = this.items [newFocusIndex];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.redrawItem (newFocusIndex, true);
		this.showItem (item);
		final Event newEvent = new Event ();
		newEvent.item = item;
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	/* SWT.MULTI */
	if ((stateMask & SWT.CTRL) != 0) {
		if ((stateMask & SWT.SHIFT) != 0) {
			/* CTRL+Shift+Up Arrow */
			if (this.topIndex == 0) {
        return;	/* at top */
      }
			this.update ();
			this.topIndex--;
			final ScrollBar vBar = this.getVerticalBar ();
			if (vBar != null) {
        vBar.setSelection (this.topIndex);
      }
			final GC gc = new GC (this);
			gc.copyArea (
				0, 0,
				this.clientArea.width, this.clientArea.height,
				0, this.itemHeight);
			gc.dispose ();
			return;
		}
		/* CTRL+Up Arrow */
		final int focusIndex = this.focusItem.index;
		if (focusIndex == 0) {
      return;	/* at top */
    }
		final CTableItem newFocusItem = this.items [focusIndex - 1];
		this.setFocusItem (newFocusItem, true);
		this.showItem (newFocusItem);
		this.redrawItem (newFocusItem.index, true);
		return;
	}
	/* Shift+Up Arrow */
	final int newFocusIndex = this.focusItem.index - 1;
	if (newFocusIndex < 0) {
    return; 		/* at top */
  }
	if (this.anchorItem == null) {
    this.anchorItem = this.focusItem;
  }
	if (this.anchorItem.index < this.focusItem.index) {
		this.deselectItem (this.focusItem);
		this.redrawItem (this.focusItem.index, true);
	}
	final CTableItem item = this.items [newFocusIndex];
	this.selectItem (item, true);
	this.setFocusItem (item, true);
	this.redrawItem (newFocusIndex, true);
	this.showItem (item);
	final Event newEvent = new Event ();
	newEvent.item = item;
	this.notifyListeners (SWT.Selection, newEvent);
}
void onCR () {
	if (this.focusItem == null) {
    return;
  }
	final Event event = new Event ();
	event.item = this.focusItem;
	this.notifyListeners (SWT.DefaultSelection, event);
}
void onDispose (final Event event) {
	if (this.isDisposed () || this.ignoreDispose) {
    return;
  }
	this.ignoreDispose = true;
	this.notifyListeners(SWT.Dispose, event);
	event.type = SWT.None;
	for (int i = 0; i < this.itemsCount; i++) {
		this.items [i].dispose (false);
	}
	for (final CTableColumn column : this.columns) {
		column.dispose (false);
	}
	if (this.toolTipShell != null) {
		this.toolTipShell.dispose ();
		this.toolTipShell = null;
		this.toolTipLabel = null;
	}
	this.toolTipListener = null;
	this.itemsCount = this.topIndex = this.horizontalOffset = 0;
	this.items = this.selectedItems = null;
	this.columns = this.orderedColumns = null;
	this.focusItem = this.anchorItem = this.lastClickedItem = null;
	this.lastSelectionEvent = null;
	this.header = null;
	this.resizeColumn = this.sortColumn = null;
}
void onEnd (final int stateMask) {
	final int lastAvailableIndex = this.itemsCount - 1;
	if ((stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
		/* End with no modifiers */
		if (this.focusItem.index == lastAvailableIndex) {
      return; 	/* at bottom */
    }
		final CTableItem item = this.items [lastAvailableIndex];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.redrawItem (lastAvailableIndex, true);
		this.showItem (item);
		final Event newEvent = new Event ();
		newEvent.item = item;
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	if ((this.getStyle () & SWT.SINGLE) != 0) {
		if ((stateMask & SWT.CTRL) != 0) {
			/* CTRL+End, CTRL+Shift+End */
			final int visibleItemCount = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
			this.setTopIndex (this.itemsCount - visibleItemCount);
			return;
		}
		/* Shift+End */
		if (this.focusItem.index == lastAvailableIndex) {
      return; /* at bottom */
    }
		final CTableItem item = this.items [lastAvailableIndex];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.redrawItem (lastAvailableIndex, true);
		this.showItem (item);
		final Event newEvent = new Event ();
		newEvent.item = item;
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	/* SWT.MULTI */
	if ((stateMask & SWT.CTRL) != 0) {
		if ((stateMask & SWT.SHIFT) != 0) {
			/* CTRL+Shift+End */
			this.showItem (this.items [lastAvailableIndex]);
			return;
		}
		/* CTRL+End */
		if (this.focusItem.index == lastAvailableIndex) {
      return; /* at bottom */
    }
		final CTableItem item = this.items [lastAvailableIndex];
		this.setFocusItem (item, true);
		this.showItem (item);
		this.redrawItem (item.index, true);
		return;
	}
	/* Shift+End */
	if (this.anchorItem == null) {
    this.anchorItem = this.focusItem;
  }
	final CTableItem selectedItem = this.items [lastAvailableIndex];
	if ((selectedItem == this.focusItem) && selectedItem.isSelected ()) {
    return;
  }
	final int anchorIndex = this.anchorItem.index;
	final int selectIndex = selectedItem.index;
	final CTableItem[] newSelection = new CTableItem [(selectIndex - anchorIndex) + 1];
	int writeIndex = 0;
	for (int i = anchorIndex; i <= selectIndex; i++) {
		newSelection [writeIndex++] = this.items [i];
	}
	this.setSelection (newSelection, false);
	this.setFocusItem (selectedItem, true);
	this.redrawItems (anchorIndex, selectIndex, true);
	this.showItem (selectedItem);
	final Event newEvent = new Event ();
	newEvent.item = selectedItem;
	this.notifyListeners (SWT.Selection, newEvent);
}
void onFocusIn () {
	this.hasFocus = true;
	if (this.itemsCount == 0) {
		this.redraw ();
		return;
	}
	if ((this.getStyle () & (SWT.HIDE_SELECTION | SWT.MULTI)) == (SWT.HIDE_SELECTION | SWT.MULTI)) {
		for (final CTableItem selectedItem : this.selectedItems) {
			this.redrawItem (selectedItem.index, true);
		}
	}
	if (this.focusItem != null) {
		this.redrawItem (this.focusItem.index, true);
		return;
	}
	/* an initial focus item must be selected */
	CTableItem initialFocus;
	if (this.selectedItems.length > 0) {
		initialFocus = this.selectedItems [0];
	} else {
		initialFocus = this.items [this.topIndex];
	}
	this.setFocusItem (initialFocus, false);
	this.redrawItem (initialFocus.index, true);
	return;
}
void onFocusOut () {
	this.hasFocus = false;
	if (this.itemsCount == 0) {
		this.redraw ();
		return;
	}
	if (this.focusItem != null) {
		this.redrawItem (this.focusItem.index, true);
	}
	if ((this.getStyle () & (SWT.HIDE_SELECTION | SWT.MULTI)) == (SWT.HIDE_SELECTION | SWT.MULTI)) {
		for (final CTableItem selectedItem : this.selectedItems) {
			this.redrawItem (selectedItem.index, true);
		}
	}
}
void onHome (final int stateMask) {
	if ((stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
		/* Home with no modifiers */
		if (this.focusItem.index == 0) {
      return; 		/* at top */
    }
		final CTableItem item = this.items [0];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.redrawItem (0, true);
		this.showItem (item);
		final Event newEvent = new Event ();
		newEvent.item = item;
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	if ((this.getStyle () & SWT.SINGLE) != 0) {
		if ((stateMask & SWT.CTRL) != 0) {
			/* CTRL+Home, CTRL+Shift+Home */
			this.setTopIndex (0);
			return;
		}
		/* Shift+Home */
		if (this.focusItem.index == 0) {
      return; 		/* at top */
    }
		final CTableItem item = this.items [0];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.redrawItem (0, true);
		this.showItem (item);
		final Event newEvent = new Event ();
		newEvent.item = item;
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}
	/* SWT.MULTI */
	if ((stateMask & SWT.CTRL) != 0) {
		if ((stateMask & SWT.SHIFT) != 0) {
			/* CTRL+Shift+Home */
			this.setTopIndex (0);
			return;
		}
		/* CTRL+Home */
		if (this.focusItem.index == 0) {
      return; /* at top */
    }
		final CTableItem item = this.items [0];
		this.setFocusItem (item, true);
		this.showItem (item);
		this.redrawItem (item.index, true);
		return;
	}
	/* Shift+Home */
	if (this.anchorItem == null) {
    this.anchorItem = this.focusItem;
  }
	final CTableItem selectedItem = this.items [0];
	if ((selectedItem == this.focusItem) && selectedItem.isSelected ()) {
    return;
  }
	final int anchorIndex = this.anchorItem.index;
	final int selectIndex = selectedItem.index;
	final CTableItem[] newSelection = new CTableItem [anchorIndex + 1];
	int writeIndex = 0;
	for (int i = anchorIndex; i >= 0; i--) {
		newSelection [writeIndex++] = this.items [i];
	}
	this.setSelection (newSelection, false);
	this.setFocusItem (selectedItem, true);
	this.redrawItems (anchorIndex, selectIndex, true);
	this.showItem (selectedItem);
	final Event newEvent = new Event ();
	newEvent.item = selectedItem;
	this.notifyListeners (SWT.Selection, newEvent);
}
void onKeyDown (final Event event) {
	if (this.ignoreKey) {
		this.ignoreKey = false;
		return;
	}
	this.ignoreKey = true;
	this.notifyListeners (event.type, event);
	event.type = SWT.None;
	if (!event.doit || (this.focusItem == null)) {
    return;
  }
	if (((event.stateMask & SWT.SHIFT) == 0) && (event.keyCode != SWT.SHIFT)) {
		this.anchorItem = null;
	}
	switch (event.keyCode) {
		case SWT.ARROW_UP:
			this.onArrowUp (event.stateMask);
			return;
		case SWT.ARROW_DOWN:
			this.onArrowDown (event.stateMask);
			return;
		case SWT.ARROW_LEFT:
			this.onArrowLeft (event.stateMask);
			return;
		case SWT.ARROW_RIGHT:
			this.onArrowRight (event.stateMask);
			return;
		case SWT.PAGE_UP:
			this.onPageUp (event.stateMask);
			return;
		case SWT.PAGE_DOWN:
			this.onPageDown (event.stateMask);
			return;
		case SWT.HOME:
			this.onHome (event.stateMask);
			return;
		case SWT.END:
			this.onEnd (event.stateMask);
			return;
	}
	if (event.character == ' ') {
		this.onSpace ();
		return;
	}
	if (event.character == SWT.CR) {
		this.onCR ();
		return;
	}
	if ((event.stateMask & SWT.CTRL) != 0) {
    return;
  }

	final int initialIndex = this.focusItem.index;
	final char character = Character.toLowerCase (event.character);
	/* check available items from current focus item to bottom */
	for (int i = initialIndex + 1; i < this.itemsCount; i++) {
		final CTableItem item = this.items [i];
		final String text = item.getText (0, false);
		if (text.length () > 0) {
			if (Character.toLowerCase (text.charAt (0)) == character) {
				this.selectItem (item, false);
				this.setFocusItem (item, true);
				this.redrawItem (i, true);
				this.showItem (item);
				final Event newEvent = new Event ();
				newEvent.item = item;
				this.notifyListeners (SWT.Selection, newEvent);
				return;
			}
		}
	}
	/* check available items from top to current focus item */
	for (int i = 0; i < initialIndex; i++) {
		final CTableItem item = this.items [i];
		final String text = item.getText (0, false);
		if (text.length () > 0) {
			if (Character.toLowerCase (text.charAt (0)) == character) {
				this.selectItem (item, false);
				this.setFocusItem (item, true);
				this.redrawItem (i, true);
				this.showItem (item);
				final Event newEvent = new Event ();
				newEvent.item = item;
				this.notifyListeners (SWT.Selection, newEvent);
				return;
			}
		}
	}
}
void onMouseDoubleClick (final Event event) {
	if (!this.isFocusControl ()) {
    this.setFocus ();
  }
	final int index = ((event.y - this.getHeaderHeight ()) / this.itemHeight) + this.topIndex;
	if  (!((0 <= index) && (index < this.itemsCount))) {
    return;	/* not on an available item */
  }
	final CTableItem selectedItem = this.items [index];

	/*
	 * If the two clicks of the double click did not occur over the same item then do not
	 * consider this to be a default selection.
	 */
	if ((selectedItem != this.lastClickedItem) || !selectedItem.getHitBounds ().contains (event.x, event.y)) {
    return;	/* considers x */
  }

	final Event newEvent = new Event ();
	newEvent.item = selectedItem;
	this.notifyListeners (SWT.DefaultSelection, newEvent);
}
void onMouseDown (final Event event) {
	if (!this.isFocusControl ()) {
    this.forceFocus ();
  }
	final int index = ((event.y - this.getHeaderHeight ()) / this.itemHeight) + this.topIndex;
	if (!((0 <= index) && (index < this.itemsCount))) {
    return;	/* not on an available item */
  }
	final CTableItem selectedItem = this.items [index];

	/* if click was in checkbox */
	if (((this.getStyle () & SWT.CHECK) != 0) && selectedItem.getCheckboxBounds ().contains (event.x, event.y)) {
		if (event.button != 1) {
      return;
    }
		selectedItem.setChecked (!selectedItem.checked);
		final Event newEvent = new Event ();
		newEvent.item = selectedItem;
		newEvent.detail = SWT.CHECK;
		this.notifyListeners (SWT.Selection, newEvent);
		return;
	}

	if (!selectedItem.getHitBounds ().contains (event.x, event.y)) {
    return;
  }

	if (((event.stateMask & SWT.SHIFT) == 0) && (event.keyCode != SWT.SHIFT)) {
    this.anchorItem = null;
  }

	boolean sendSelection = true;
	/* Detect when this is the second click of a DefaultSelection and don't fire Selection */
	if ((this.lastSelectionEvent != null) && (this.lastSelectionEvent.item == selectedItem)) {
		if ((event.time - this.lastSelectionEvent.time) <= this.display.getDoubleClickTime ()) {
			sendSelection = false;
		} else {
			this.lastSelectionEvent = event;
			event.item = selectedItem;
		}
	} else {
		this.lastSelectionEvent = event;
		event.item = selectedItem;
	}

	if ((this.getStyle () & SWT.SINGLE) != 0) {
		if (!selectedItem.isSelected ()) {
			if ((event.button == 1) || ((event.stateMask & (SWT.CTRL | SWT.SHIFT)) == 0)) {
				this.selectItem (selectedItem, false);
				this.setFocusItem (selectedItem, true);
				this.redrawItem (selectedItem.index, true);
				if (sendSelection) {
					final Event newEvent = new Event ();
					newEvent.item = selectedItem;
					this.notifyListeners (SWT.Selection, newEvent);
				}
				return;
			}
		}
		/* item is selected */
		if (event.button == 1) {
			/* fire a selection event, though the selection did not change */
			if (sendSelection) {
				final Event newEvent = new Event ();
				newEvent.item = selectedItem;
				this.notifyListeners (SWT.Selection, newEvent);
			}
			return;
		}
	}
	/* SWT.MULTI */
	if (!selectedItem.isSelected ()) {
		if (event.button == 1) {
			if ((event.stateMask & (SWT.CTRL | SWT.SHIFT)) == SWT.SHIFT) {
				if (this.anchorItem == null) {
          this.anchorItem = this.focusItem;
        }
				final int anchorIndex = this.anchorItem.index;
				final int selectIndex = selectedItem.index;
				final CTableItem[] newSelection = new CTableItem [Math.abs (anchorIndex - selectIndex) + 1];
				final int step = anchorIndex < selectIndex ? 1 : -1;
				int writeIndex = 0;
				for (int i = anchorIndex; i != selectIndex; i += step) {
					newSelection [writeIndex++] = this.items [i];
				}
				newSelection [writeIndex] = this.items [selectIndex];
				this.setSelection (newSelection, false);
				this.setFocusItem (selectedItem, true);
				this.redrawItems (
					Math.min (anchorIndex, selectIndex),
					Math.max (anchorIndex, selectIndex),
					true);
				if (sendSelection) {
					final Event newEvent = new Event ();
					newEvent.item = selectedItem;
					this.notifyListeners (SWT.Selection, newEvent);
				}
				return;
			}
			this.selectItem (selectedItem, (event.stateMask & SWT.CTRL) != 0);
			this.setFocusItem (selectedItem, true);
			this.redrawItem (selectedItem.index, true);
			if (sendSelection) {
				final Event newEvent = new Event ();
				newEvent.item = selectedItem;
				this.notifyListeners (SWT.Selection, newEvent);
			}
			return;
		}
		/* button 3 */
		if ((event.stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
			this.selectItem (selectedItem, false);
			this.setFocusItem (selectedItem, true);
			this.redrawItem (selectedItem.index, true);
			if (sendSelection) {
				final Event newEvent = new Event ();
				newEvent.item = selectedItem;
				this.notifyListeners (SWT.Selection, newEvent);
			}
			return;
		}
	}
	/* item is selected */
	if (event.button != 1) {
    return;
  }
	if ((event.stateMask & SWT.CTRL) != 0) {
		this.removeSelectedItem (this.getSelectionIndex (selectedItem));
		this.setFocusItem (selectedItem, true);
		this.redrawItem (selectedItem.index, true);
		if (sendSelection) {
			final Event newEvent = new Event ();
			newEvent.item = selectedItem;
			this.notifyListeners (SWT.Selection, newEvent);
		}
		return;
	}
	if ((event.stateMask & SWT.SHIFT) != 0) {
		if (this.anchorItem == null) {
      this.anchorItem = this.focusItem;
    }
		final int anchorIndex = this.anchorItem.index;
		final int selectIndex = selectedItem.index;
		final CTableItem[] newSelection = new CTableItem [Math.abs (anchorIndex - selectIndex) + 1];
		final int step = anchorIndex < selectIndex ? 1 : -1;
		int writeIndex = 0;
		for (int i = anchorIndex; i != selectIndex; i += step) {
			newSelection [writeIndex++] = this.items [i];
		}
		newSelection [writeIndex] = this.items [selectIndex];
		this.setSelection (newSelection, false);
		this.setFocusItem (selectedItem, true);
		this.redrawItems (
			Math.min (anchorIndex, selectIndex),
			Math.max (anchorIndex, selectIndex),
			true);
		if (sendSelection) {
			final Event newEvent = new Event ();
			newEvent.item = selectedItem;
			this.notifyListeners (SWT.Selection, newEvent);
		}
		return;
	}
	this.selectItem (selectedItem, false);
	this.setFocusItem (selectedItem, true);
	this.redrawItem (selectedItem.index, true);
	if (sendSelection) {
		final Event newEvent = new Event ();
		newEvent.item = selectedItem;
		this.notifyListeners (SWT.Selection, newEvent);
	}
}
void onMouseUp (final Event event) {
	final int index = ((event.y - this.getHeaderHeight ()) / this.itemHeight) + this.topIndex;
	if (!((0 <= index) && (index < this.itemsCount))) {
    return;	/* not on an available item */
  }
	this.lastClickedItem = this.items [index];
}
void onPageDown (final int stateMask) {
	final int visibleItemCount = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
	if ((stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
		/* PageDown with no modifiers */
		int newFocusIndex = (this.focusItem.index + visibleItemCount) - 1;
		newFocusIndex = Math.min (newFocusIndex, this.itemsCount - 1);
		if (newFocusIndex == this.focusItem.index) {
      return;
    }
		final CTableItem item = this.items [newFocusIndex];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.showItem (item);
		this.redrawItem (item.index, true);
		return;
	}
	if ((stateMask & (SWT.CTRL | SWT.SHIFT)) == (SWT.CTRL | SWT.SHIFT)) {
		/* CTRL+Shift+PageDown */
		int newTopIndex = this.topIndex + visibleItemCount;
		newTopIndex = Math.min (newTopIndex, this.itemsCount - visibleItemCount);
		if (newTopIndex == this.topIndex) {
      return;
    }
		this.setTopIndex (newTopIndex);
		return;
	}
	if ((this.getStyle () & SWT.SINGLE) != 0) {
		if ((stateMask & SWT.SHIFT) != 0) {
			/* Shift+PageDown */
			int newFocusIndex = (this.focusItem.index + visibleItemCount) - 1;
			newFocusIndex = Math.min (newFocusIndex, this.itemsCount - 1);
			if (newFocusIndex == this.focusItem.index) {
        return;
      }
			final CTableItem item = this.items [newFocusIndex];
			this.selectItem (item, false);
			this.setFocusItem (item, true);
			this.showItem (item);
			this.redrawItem (item.index, true);
			return;
		}
		/* CTRL+PageDown */
		int newTopIndex = this.topIndex + visibleItemCount;
		newTopIndex = Math.min (newTopIndex, this.itemsCount - visibleItemCount);
		if (newTopIndex == this.topIndex) {
      return;
    }
		this.setTopIndex (newTopIndex);
		return;
	}
	/* SWT.MULTI */
	if ((stateMask & SWT.CTRL) != 0) {
		/* CTRL+PageDown */
		final int bottomIndex = Math.min ((this.topIndex + visibleItemCount) - 1, this.itemsCount - 1);
		if (this.focusItem.index != bottomIndex) {
			/* move focus to bottom item in viewport */
			this.setFocusItem (this.items [bottomIndex], true);
			this.redrawItem (bottomIndex, true);
		} else {
			/* at bottom of viewport, so set focus to bottom item one page down */
			final int newFocusIndex = Math.min (this.itemsCount - 1, bottomIndex + visibleItemCount);
			if (newFocusIndex == this.focusItem.index) {
        return;
      }
			this.setFocusItem (this.items [newFocusIndex], true);
			this.showItem (this.items [newFocusIndex]);
			this.redrawItem (newFocusIndex, true);
		}
		return;
	}
	/* Shift+PageDown */
	if (this.anchorItem == null) {
    this.anchorItem = this.focusItem;
  }
	final int anchorIndex = this.anchorItem.index;
	final int bottomIndex = Math.min ((this.topIndex + visibleItemCount) - 1, this.itemsCount - 1);
	int selectIndex;
	if (this.focusItem.index != bottomIndex) {
		/* select from focus to bottom item in viewport */
		selectIndex = bottomIndex;
	} else {
		/* already at bottom of viewport, so select to bottom of one page down */
		selectIndex = Math.min (this.itemsCount - 1, bottomIndex + visibleItemCount);
		if ((selectIndex == this.focusItem.index) && this.focusItem.isSelected ()) {
      return;
    }
	}
	final CTableItem selectedItem = this.items [selectIndex];
	final CTableItem[] newSelection = new CTableItem [Math.abs (anchorIndex - selectIndex) + 1];
	final int step = anchorIndex < selectIndex ? 1 : -1;
	int writeIndex = 0;
	for (int i = anchorIndex; i != selectIndex; i += step) {
		newSelection [writeIndex++] = this.items [i];
	}
	newSelection [writeIndex] = this.items [selectIndex];
	this.setSelection (newSelection, false);
	this.setFocusItem (selectedItem, true);
	this.showItem (selectedItem);
	final Event newEvent = new Event ();
	newEvent.item = selectedItem;
	this.notifyListeners (SWT.Selection, newEvent);
}
void onPageUp (final int stateMask) {
	final int visibleItemCount = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
	if ((stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
		/* PageUp with no modifiers */
		final int newFocusIndex = Math.max (0, (this.focusItem.index - visibleItemCount) + 1);
		if (newFocusIndex == this.focusItem.index) {
      return;
    }
		final CTableItem item = this.items [newFocusIndex];
		this.selectItem (item, false);
		this.setFocusItem (item, true);
		this.showItem (item);
		this.redrawItem (item.index, true);
		return;
	}
	if ((stateMask & (SWT.CTRL | SWT.SHIFT)) == (SWT.CTRL | SWT.SHIFT)) {
		/* CTRL+Shift+PageUp */
		final int newTopIndex = Math.max (0, this.topIndex - visibleItemCount);
		if (newTopIndex == this.topIndex) {
      return;
    }
		this.setTopIndex (newTopIndex);
		return;
	}
	if ((this.getStyle () & SWT.SINGLE) != 0) {
		if ((stateMask & SWT.SHIFT) != 0) {
			/* Shift+PageUp */
			final int newFocusIndex = Math.max (0, (this.focusItem.index - visibleItemCount) + 1);
			if (newFocusIndex == this.focusItem.index) {
        return;
      }
			final CTableItem item = this.items [newFocusIndex];
			this.selectItem (item, false);
			this.setFocusItem (item, true);
			this.showItem (item);
			this.redrawItem (item.index, true);
			return;
		}
		/* CTRL+PageUp */
		final int newTopIndex = Math.max (0, this.topIndex - visibleItemCount);
		if (newTopIndex == this.topIndex) {
      return;
    }
		this.setTopIndex (newTopIndex);
		return;
	}
	/* SWT.MULTI */
	if ((stateMask & SWT.CTRL) != 0) {
		/* CTRL+PageUp */
		if (this.focusItem.index != this.topIndex) {
			/* move focus to top item in viewport */
			this.setFocusItem (this.items [this.topIndex], true);
			this.redrawItem (this.topIndex, true);
		} else {
			/* at top of viewport, so set focus to top item one page up */
			final int newFocusIndex = Math.max (0, this.focusItem.index - visibleItemCount);
			if (newFocusIndex == this.focusItem.index) {
        return;
      }
			this.setFocusItem (this.items [newFocusIndex], true);
			this.showItem (this.items [newFocusIndex]);
			this.redrawItem (newFocusIndex, true);
		}
		return;
	}
	/* Shift+PageUp */
	if (this.anchorItem == null) {
    this.anchorItem = this.focusItem;
  }
	final int anchorIndex = this.anchorItem.index;
	int selectIndex;
	if (this.focusItem.index != this.topIndex) {
		/* select from focus to top item in viewport */
		selectIndex = this.topIndex;
	} else {
		/* already at top of viewport, so select to top of one page up */
		selectIndex = Math.max (0, this.topIndex - visibleItemCount);
		if ((selectIndex == this.focusItem.index) && this.focusItem.isSelected ()) {
      return;
    }
	}
	final CTableItem selectedItem = this.items [selectIndex];
	final CTableItem[] newSelection = new CTableItem [Math.abs (anchorIndex - selectIndex) + 1];
	final int step = anchorIndex < selectIndex ? 1 : -1;
	int writeIndex = 0;
	for (int i = anchorIndex; i != selectIndex; i += step) {
		newSelection [writeIndex++] = this.items [i];
	}
	newSelection [writeIndex] = this.items [selectIndex];
	this.setSelection (newSelection, false);
	this.setFocusItem (selectedItem, true);
	this.showItem (selectedItem);
	final Event newEvent = new Event ();
	newEvent.item = selectedItem;
	this.notifyListeners (SWT.Selection, newEvent);
}
void onPaint (final Event event) {
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	final GC gc = event.gc;
	final Rectangle clipping = gc.getClipping ();
	final int headerHeight = this.getHeaderHeight ();
	final int numColumns = orderedColumns.length;
	int startColumn = -1, endColumn = -1;
	if (numColumns > 0) {
		startColumn = this.computeColumnIntersect (clipping.x, 0);
		if (startColumn != -1) {	/* the clip x is within a column's bounds */
			endColumn = this.computeColumnIntersect (clipping.x + clipping.width, startColumn);
			if (endColumn == -1) {
        endColumn = numColumns - 1;
      }
		}
	} else {
		startColumn = endColumn = 0;
	}

	/* Determine the items to be painted */
	int startIndex = ((clipping.y - headerHeight) / this.itemHeight) + this.topIndex;
	int endIndex = -1;
	if (startIndex < this.itemsCount) {
		endIndex = startIndex + (int)Math.ceil((float)clipping.height / this.itemHeight);
	}
	startIndex = Math.max (0, startIndex);
	endIndex = Math.min (endIndex, this.itemsCount - 1);

	/* fill background not handled by items */
	gc.setBackground (this.getBackground ());
	gc.setClipping (clipping);
	int bottomY = endIndex >= 0 ? this.getItemY (this.items [endIndex]) + this.itemHeight : 0;
	final int fillHeight = Math.max (0, this.clientArea.height - bottomY);
	if (fillHeight > 0) {	/* space below bottom item */
		gc.fillRectangle (0, bottomY, this.clientArea.width, fillHeight);
		//drawBackground (gc, 0, bottomY, clientArea.width, fillHeight);
	}
	if (this.columns.length > 0) {
		final CTableColumn column = orderedColumns [orderedColumns.length - 1];	/* last column */
		final int rightX = column.getX () + column.width;
		if (rightX < this.clientArea.width) {
			gc.fillRectangle (rightX, 0, this.clientArea.width - rightX, this.clientArea.height - fillHeight);
			//drawBackground (gc, rightX, 0, clientArea.width - rightX, clientArea.height - fillHeight);
		}
	}

	/* paint the items */
	boolean noFocusDraw = false;
	final int[] lineDash = gc.getLineDash ();
	final int lineWidth = gc.getLineWidth ();
	for (int i = startIndex; i <= Math.min (endIndex, this.itemsCount - 1); i++) {
		final CTableItem item = this.items [i];
		if (!item.isDisposed ()) {	/* ensure that item was not disposed in a callback */
			if (startColumn == -1) {
				/* indicates that region to paint is to the right of the last column */
				noFocusDraw = item.paint (gc, null, true) || noFocusDraw;
			} else if (numColumns == 0) {
      	noFocusDraw = item.paint (gc, null, false) || noFocusDraw;
      } else {
      	for (int j = startColumn; j <= Math.min (endColumn, this.columns.length - 1); j++) {
      		if (!item.isDisposed ()) {	/* ensure that item was not disposed in a callback */
      			noFocusDraw = item.paint (gc, orderedColumns [j], false) || noFocusDraw;
      		}
      		if (this.isDisposed () || gc.isDisposed ()) {
            return;	/* ensure that receiver was not disposed in a callback */
          }
      	}
      }
		}
		if (this.isDisposed () || gc.isDisposed ()) {
      return;	/* ensure that receiver was not disposed in a callback */
    }
	}

	/* repaint grid lines */
	gc.setClipping(clipping);
	gc.setLineWidth (lineWidth);
	if (this.linesVisible) {
		gc.setForeground (this.display.getSystemColor (SWT.COLOR_WIDGET_LIGHT_SHADOW));
		gc.setLineDash (lineDash);
		if ((numColumns > 0) && (startColumn != -1)) {
			/* vertical column lines */
			for (int i = startColumn; i <= endColumn; i++) {
				final int x = (orderedColumns [i].getX () + orderedColumns [i].width) - 1;
				gc.drawLine (x, clipping.y, x, clipping.y + clipping.height);
			}
		}
		/* horizontal item lines */
		bottomY = clipping.y + clipping.height;
		final int rightX = clipping.x + clipping.width;
		int y = (((clipping.y - headerHeight) / this.itemHeight) * this.itemHeight) + headerHeight;
		while (y <= bottomY) {
			gc.drawLine (clipping.x, y, rightX, y);
			y += this.itemHeight;
		}
	}

	/* paint focus rectangle */
	if (!noFocusDraw && this.isFocusControl ()) {
		if (this.focusItem != null) {
			final Rectangle focusBounds = this.focusItem.getFocusBounds ();
			if (focusBounds.width > 0) {
				gc.setForeground (this.display.getSystemColor (SWT.COLOR_BLACK));
				gc.setClipping (focusBounds);
				if (this.focusItem.isSelected ()) {
					gc.setLineDash (new int[] {2, 2});
				} else {
					gc.setLineDash (new int[] {1, 1});
				}
				gc.drawFocus (focusBounds.x, focusBounds.y, focusBounds.width, focusBounds.height);
			}
		} else {
			/* no items, so draw focus border around Table */
			final int y = headerHeight + 1;
			final int width = Math.max (0, this.clientArea.width - 2);
			final int height = Math.max (0, this.clientArea.height - headerHeight - 2);
			gc.setForeground (this.display.getSystemColor (SWT.COLOR_BLACK));
			gc.setClipping (1, y, width, height);
			gc.setLineDash (new int[] {1, 1});
			gc.drawFocus (1, y, width, height);
		}
	}
}
void onResize (final Event event) {
	this.clientArea = this.getClientArea ();
	/* vertical scrollbar */
	final ScrollBar vBar = this.getVerticalBar ();
	if (vBar != null) {
		final int clientHeight = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
		final int thumb = Math.min (clientHeight, this.itemsCount);
		vBar.setThumb (thumb);
		vBar.setPageIncrement (thumb);
		final int index = vBar.getSelection ();
		if (index != this.topIndex) {
			this.topIndex = index;
			this.redraw ();
		}
		final boolean visible = clientHeight < this.itemsCount;
		if (visible != vBar.getVisible ()) {
			vBar.setVisible (visible);
			this.clientArea = this.getClientArea ();
		}
	}

	/* horizontal scrollbar */
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar != null) {
		final int hBarMaximum = hBar.getMaximum ();
		final int thumb = Math.min (this.clientArea.width, hBarMaximum);
		hBar.setThumb (thumb);
		hBar.setPageIncrement (thumb);
		this.horizontalOffset = hBar.getSelection ();
		final boolean visible = this.clientArea.width < hBarMaximum;
		if (visible != hBar.getVisible ()) {
			hBar.setVisible (visible);
			this.clientArea = this.getClientArea ();
		}
	}

	/* header */
	final int headerHeight = Math.max (this.fontHeight, this.headerImageHeight) + (2 * this.getHeaderPadding ());
	this.header.setSize (this.clientArea.width, headerHeight);

	/* if this is the focus control but there are no items then the boundary focus ring must be repainted */
	if ((this.itemsCount == 0) && this.isFocusControl ()) {
    this.redraw ();
  }
}
void onScrollHorizontal (final Event event) {
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar == null) {
    return;
  }
	final int newSelection = hBar.getSelection ();
	this.update ();
	if (this.itemsCount > 0) {
		final GC gc = new GC (this);
		gc.copyArea (
			0, 0,
			this.clientArea.width, this.clientArea.height,
			this.horizontalOffset - newSelection, 0);
		gc.dispose ();
	} else {
		this.redraw ();	/* ensure that static focus rectangle updates properly */
	}

	if ((this.drawCount <= 0) && this.header.isVisible ()) {
		this.header.update ();
		final Rectangle headerClientArea = this.header.getClientArea ();
		final GC gc = new GC (this.header);
		gc.copyArea (
			0, 0,
			headerClientArea.width, headerClientArea.height,
			this.horizontalOffset - newSelection, 0);
		gc.dispose ();
	}
	this.horizontalOffset = newSelection;
}
void onScrollVertical (final Event event) {
	final ScrollBar vBar = this.getVerticalBar ();
	if (vBar == null) {
    return;
  }
	final int newSelection = vBar.getSelection ();
	this.update ();
	final GC gc = new GC (this);
	gc.copyArea (
		0, 0,
		this.clientArea.width, this.clientArea.height,
		0, (this.topIndex - newSelection) * this.itemHeight);
	gc.dispose ();
	this.topIndex = newSelection;
}
void onSpace () {
	if (this.focusItem == null) {
    return;
  }
	if (!this.focusItem.isSelected ()) {
		this.selectItem (this.focusItem, (this.getStyle () & SWT.MULTI) != 0);
		this.redrawItem (this.focusItem.index, true);
	}
	if ((this.getStyle () & SWT.CHECK) != 0) {
		this.focusItem.setChecked (!this.focusItem.checked);
	}
	this.showItem (this.focusItem);
	Event event = new Event ();
	event.item = this.focusItem;
	this.notifyListeners (SWT.Selection, event);
	if ((this.getStyle () & SWT.CHECK) == 0) {
    return;
  }

	/* SWT.CHECK */
	event = new Event ();
	event.item = this.focusItem;
	event.detail = SWT.CHECK;
	this.notifyListeners (SWT.Selection, event);
}
/*
 * The current focus item is about to become unavailable, so reassign focus.
 */
void reassignFocus () {
	if (this.focusItem == null) {
    return;
  }

	/*
	 * reassign to the previous root-level item if there is one, or the next
	 * root-level item otherwise
	 */
	int index = this.focusItem.index;
	if (index != 0) {
		index--;
	} else {
		index++;
	}
	if (index < this.itemsCount) {
		final CTableItem item = this.items [index];
		this.setFocusItem (item, false);
		this.showItem (item);
	} else {
		this.setFocusItem (null, false);		/* no items left */
	}
}
@Override
public void redraw () {
	this.checkWidget ();
	if (this.drawCount <= 0) {
    super.redraw ();
  }
}
@Override
public void redraw (final int x, final int y, final int width, final int height, final boolean all) {
	this.checkWidget ();
	if (this.drawCount <= 0) {
    super.redraw (x, y, width, height, all);
  }
}
/*
 * Redraws from the specified index down to the last available item inclusive.  Note
 * that the redraw bounds do not extend beyond the current last item, so clients
 * that reduce the number of available items should use #redrawItems(int,int) instead
 * to ensure that redrawing extends down to the previous bottom item boundary.
 */
void redrawFromItemDownwards (final int index) {
	this.redrawItems (index, this.itemsCount - 1, false);
}
/*
 * Redraws the table item at the specified index.  It is valid for this index to reside
 * beyond the last available item.
 */
void redrawItem (final int itemIndex, final boolean focusBoundsOnly) {
	if ((itemIndex < this.itemsCount) && !this.items [itemIndex].isInViewport ()) {
    return;
  }
	this.redrawItems (itemIndex, itemIndex, focusBoundsOnly);
}
/*
 * Redraws the table between the start and end item indices inclusive.  It is valid
 * for the end index value to extend beyond the last available item.
 */
void redrawItems (final int startIndex, int endIndex, final boolean focusBoundsOnly) {
	if (this.drawCount > 0) {
    return;
  }

	final int startY = ((startIndex - this.topIndex) * this.itemHeight) + this.getHeaderHeight ();
	final int height = ((endIndex - startIndex) + 1) * this.itemHeight;
	if (focusBoundsOnly) {
		final boolean custom = this.isListening (SWT.EraseItem) || this.isListening (SWT.PaintItem);
		if (!custom && (this.columns.length > 0)) {
			CTableColumn lastColumn;
			if ((this.getStyle () & SWT.FULL_SELECTION) != 0) {
				final CTableColumn[] orderedColumns = this.getOrderedColumns ();
				lastColumn = orderedColumns [orderedColumns.length - 1];
			} else {
				lastColumn = this.columns [0];
			}
			final int rightX = lastColumn.getX () + lastColumn.getWidth ();
			if (rightX <= 0) {
        return;	/* focus column(s) not visible */
      }
		}
		endIndex = Math.min (endIndex, this.itemsCount - 1);
		for (int i = startIndex; i <= endIndex; i++) {
			final CTableItem item = this.items [i];
			if (item.isInViewport ()) {
				/* if custom painting is being done then repaint the full item */
				if (custom) {
					this.redraw (0, this.getItemY (item), this.clientArea.width, this.itemHeight, false);
				} else {
					/* repaint the item's focus bounds */
					final Rectangle bounds = item.getFocusBounds ();
					this.redraw (bounds.x, startY, bounds.width, height, false);
				}
			}
		}
	} else {
		this.redraw (0, startY, this.clientArea.width, height, false);
	}
}
/**
 * Removes the item from the receiver at the given
 * zero-relative index.
 *
 * @param index the index for the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void remove (final int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    SWT.error (SWT.ERROR_INVALID_RANGE);
  }
	this.items [index].dispose ();
	final int[] eventData = new int[5];
	eventData[0] = ACC.DELETE;
	eventData[1] = index;
	eventData[2] = 1;
	eventData[3] = 0;
	eventData[4] = 0;
	this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);
}
/**
 * Removes the items from the receiver which are
 * between the given zero-relative start and end
 * indices (inclusive).
 *
 * @param start the start of the range
 * @param end the end of the range
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void remove (final int start, final int end) {
	this.checkWidget ();
	if (start > end) {
    return;
  }
	if (!((0 <= start) && (start <= end) && (end < this.itemsCount))) {
		SWT.error (SWT.ERROR_INVALID_RANGE);
	}
	if ((start == 0) && (end == (this.itemsCount - 1))) {
		this.removeAll ();
	} else {
		for (int i = end; i >= start; i--) {
			this.items [i].dispose ();
		}

		final int[] eventData = new int[5];
		eventData[0] = ACC.DELETE;
		eventData[1] = start;
		eventData[2] = (end - start) + 1;
		eventData[3] = 0;
		eventData[4] = 0;
		this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);

	}
}
/**
 * Removes the items from the receiver's list at the given
 * zero-relative indices.
 *
 * @param indices the array of indices of the items
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 *    <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void remove (final int [] indices) {
	this.checkWidget ();
	if (indices == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (indices.length == 0) {
    return;
  }
	final int [] newIndices = new int [indices.length];
	System.arraycopy (indices, 0, newIndices, 0, indices.length);
	this.sortDescent (newIndices);
	final int start = newIndices [newIndices.length - 1], end = newIndices [0];
	if (!((0 <= start) && (start <= end) && (end < this.itemsCount))) {
		SWT.error (SWT.ERROR_INVALID_RANGE);
	}
	int lastRemovedIndex = -1;
	final int[] eventData = new int[5];
	for (final int newIndice : newIndices) {
		if (newIndice != lastRemovedIndex) {
			this.items [newIndice].dispose ();
			eventData[0] = ACC.DELETE;
			eventData[1] = newIndice;
			eventData[2] = 1;
			eventData[3] = 0;
			eventData[4] = 0;
			this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);
			lastRemovedIndex = newIndice;
		}
	}
}
/**
 * Removes all of the items from the receiver.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void removeAll () {
	this.checkWidget ();
	if (this.itemsCount == 0) {
    return;
  }
	this.setRedraw (false);

	this.setFocusItem (null, false);
	for (int i = 0; i < this.itemsCount; i++) {
		this.items [i].dispose (false);
	}
	this.items = new CTableItem [0];
	this.selectedItems = new CTableItem [0];
	final int oldCount = this.itemsCount;
	this.itemsCount = this.topIndex = 0;
	this.anchorItem = this.lastClickedItem = null;
	this.lastSelectionEvent = null;

	final int[] eventData = new int[5];
	eventData[0] = ACC.DELETE;
	eventData[1] = 0;
	eventData[2] = oldCount;
	eventData[3] = 0;
	eventData[4] = 0;
	this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);

	final ScrollBar vBar = this.getVerticalBar ();
	if (vBar != null) {
		vBar.setMaximum (1);
		vBar.setVisible (false);
	}
	if (this.columns.length == 0) {
		this.horizontalOffset = 0;
		final ScrollBar hBar = this.getHorizontalBar ();
		if (hBar != null) {
			hBar.setMaximum (1);
			hBar.setVisible (false);
		}
	}

	this.setRedraw (true);
}
String removeMnemonics (final String string) {
	/* removes single ampersands and preserves double-ampersands */
	final char [] chars = new char [string.length ()];
	string.getChars (0, chars.length, chars, 0);
	int i = 0, j = 0;
	for ( ; i < chars.length; i++, j++) {
		if (chars[i] == '&') {
			if (++i == chars.length) {
        break;
      }
			if (chars[i] == '&') {
				chars[j++] = chars[i - 1];
			}
		}
		chars[j] = chars[i];
	}
	if (i == j) {
    return string;
  }
	return new String (chars, 0, j);
}
void removeSelectedItem (final int index) {
	final CTableItem[] newSelectedItems = new CTableItem [this.selectedItems.length - 1];
	System.arraycopy (this.selectedItems, 0, newSelectedItems, 0, index);
	System.arraycopy (this.selectedItems, index + 1, newSelectedItems, index, newSelectedItems.length - index);
	this.selectedItems = newSelectedItems;
}
/**
 * Removes the listener from the collection of listeners who will
 * be notified when the user changes the receiver's selection.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #addSelectionListener(SelectionListener)
 */
public void removeSelectionListener (final SelectionListener listener) {
	this.checkWidget ();
	if (listener == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	this.removeListener (SWT.Selection, listener);
	this.removeListener (SWT.DefaultSelection, listener);
}
/**
 * Selects the item at the given zero-relative index in the receiver.
 * If the item at the index was already selected, it remains
 * selected. Indices that are out of range are ignored.
 *
 * @param index the index of the item to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void select (final int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    return;
  }
	this.selectItem (this.items [index], (this.getStyle () & SWT.MULTI) != 0);
	if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
		this.redrawItem (index, false);
	}
	this.getAccessible().selectionChanged();
}
/**
 * Selects the items in the range specified by the given zero-relative
 * indices in the receiver. The range of indices is inclusive.
 * The current selection is not cleared before the new items are selected.
 * <p>
 * If an item in the given range is not selected, it is selected.
 * If an item in the given range was already selected, it remains selected.
 * Indices that are out of range are ignored and no items will be selected
 * if start is greater than end.
 * If the receiver is single-select and there is more than one item in the
 * given range, then all indices are ignored.
 * </p>
 *
 * @param start the start of the range
 * @param end the end of the range
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#setSelection(int,int)
 */
public void select (int start, int end) {
	this.checkWidget ();
	if ((end < 0) || (start > end) || (((this.getStyle () & SWT.SINGLE) != 0) && (start != end))) {
    return;
  }
	if ((this.itemsCount == 0) || (start >= this.itemsCount)) {
    return;
  }
	start = Math.max (start, 0);
	end = Math.min (end, this.itemsCount - 1);
	for (int i = start; i <= end; i++) {
		this.selectItem (this.items [i], (this.getStyle () & SWT.MULTI) != 0);
	}
	if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
		this.redrawItems (start, end, false);
	}
	this.getAccessible().selectionChanged();
}
/**
 * Selects the items at the given zero-relative indices in the receiver.
 * The current selection is not cleared before the new items are selected.
 * <p>
 * If the item at a given index is not selected, it is selected.
 * If the item at a given index was already selected, it remains selected.
 * Indices that are out of range and duplicate indices are ignored.
 * If the receiver is single-select and multiple indices are specified,
 * then all indices are ignored.
 * </p>
 *
 * @param indices the array of indices for the items to select
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#setSelection(int[])
 */
public void select (final int [] indices) {
	this.checkWidget ();
	if (indices == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if ((indices.length == 0) || (((this.getStyle () & SWT.SINGLE) != 0) && (indices.length > 1))) {
    return;
  }
	for (final int index : indices) {
		if ((0 <= index) && (index < this.itemsCount)) {
			this.selectItem (this.items [index], (this.getStyle () & SWT.MULTI) != 0);
		}
	}
	if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
		for (final int index : indices) {
			if ((0 <= index) && (index < this.itemsCount)) {
				this.redrawItem (index, false);
			}
		}
	}
	this.getAccessible().selectionChanged();
}
/**
 * Selects all of the items in the receiver.
 * <p>
 * If the receiver is single-select, do nothing.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void selectAll () {
	this.checkWidget ();
	if ((this.getStyle () & SWT.SINGLE) != 0) {
    return;
  }
	this.selectedItems = new CTableItem [this.itemsCount];
	System.arraycopy (this.items, 0, this.selectedItems, 0, this.itemsCount);
	if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
		this.redraw ();
	}
	for (final CTableItem selectedItem : this.selectedItems) {
		selectedItem.getAccessible(this.getAccessible(), 0).selectionChanged();
	}
	this.getAccessible().selectionChanged();
}
void selectItem (final CTableItem item, final boolean addToSelection) {
	final CTableItem[] oldSelectedItems = this.selectedItems;
	if (!addToSelection || ((this.getStyle () & SWT.SINGLE) != 0)) {
		this.selectedItems = new CTableItem[] {item};
		if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
			for (final CTableItem oldSelectedItem : oldSelectedItems) {
				if (oldSelectedItem != item) {
					this.redrawItem (oldSelectedItem.index, true);
				}
			}
		}
		for (final CTableItem oldSelectedItem : oldSelectedItems) {
			oldSelectedItem.getAccessible(this.getAccessible(), 0).selectionChanged();
		}
	} else {
		if (item.isSelected ()) {
      return;
    }
		this.selectedItems = new CTableItem [this.selectedItems.length + 1];
		System.arraycopy (oldSelectedItems, 0, this.selectedItems, 0, oldSelectedItems.length);
		this.selectedItems [this.selectedItems.length - 1] = item;
	}

	item.getAccessible(this.getAccessible(), 0).selectionChanged();
	this.getAccessible().selectionChanged();
}
@Override
public void setBackground (Color color) {
	this.checkWidget ();
	if (color == null) {
    color = this.display.getSystemColor (SWT.COLOR_LIST_BACKGROUND);
  }
	super.setBackground (color);
}
@Override
public void setForeground (Color color) {
	this.checkWidget ();
	if (color == null) {
    color = this.display.getSystemColor (SWT.COLOR_LIST_FOREGROUND);
  }
	super.setForeground (color);
}
/**
 * Sets the order that the items in the receiver should
 * be displayed in to the given argument which is described
 * in terms of the zero-relative ordering of when the items
 * were added.
 *
 * @param order the new order to display the items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the item order is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the item order is not the same length as the number of items</li>
 * </ul>
 *
 * @see CTable#getColumnOrder()
 * @see CTableColumn#getMoveable()
 * @see CTableColumn#setMoveable(boolean)
 * @see SWT#Move
 *
 * @since 3.1
 */
public void setColumnOrder (final int [] order) {
	this.checkWidget ();
	if (order == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (this.columns.length == 0) {
		if (order.length != 0) {
      SWT.error (SWT.ERROR_INVALID_ARGUMENT);
    }
		return;
	}
	if (order.length != this.columns.length) {
    SWT.error (SWT.ERROR_INVALID_ARGUMENT);
  }
	boolean reorder = false;
	final boolean [] seen = new boolean [this.columns.length];
	final int[] oldOrder = this.getColumnOrder ();
	for (int i = 0; i < order.length; i++) {
		final int index = order [i];
		if ((index < 0) || (index >= this.columns.length)) {
      SWT.error (SWT.ERROR_INVALID_RANGE);
    }
		if (seen [index]) {
      SWT.error (SWT.ERROR_INVALID_ARGUMENT);
    }
		seen [index] = true;
		if (index != oldOrder [i]) {
      reorder = true;
    }
	}
	if (!reorder) {
    return;
  }

	this.headerHideToolTip ();
	final int[] oldX = new int [this.columns.length];
	for (int i = 0; i < this.columns.length; i++) {
		oldX [i] = this.columns [i].getX ();
	}
	this.orderedColumns = new CTableColumn [order.length];
	for (int i = 0; i < order.length; i++) {
		this.orderedColumns [i] = this.columns [order [i]];
	}
	for (final CTableColumn orderedColumn : this.orderedColumns) {
		final CTableColumn column = orderedColumn;
		if (!column.isDisposed () && (column.getX () != oldX [column.getIndex ()])) {
			column.notifyListeners (SWT.Move, new Event ());
		}
	}

	this.redraw ();
	if ((this.drawCount <= 0) && this.header.isVisible ()) {
    this.header.redraw ();
  }
}
void setFocusItem (final CTableItem item, final boolean redrawOldFocus) {
	if (item == this.focusItem) {
    return;
  }
	final CTableItem oldFocusItem = this.focusItem;
	if (oldFocusItem != null) {
    oldFocusItem.getAccessible(this.getAccessible(), 0).setFocus(ACC.CHILDID_SELF);
  }
	this.focusItem = item;
	if (redrawOldFocus && (oldFocusItem != null)) {
		this.redrawItem (oldFocusItem.index, true);
	}
	if (this.focusItem != null) {
    this.focusItem.getAccessible(this.getAccessible(), 0).setFocus(ACC.CHILDID_SELF);
  }
}
@Override
public void setFont (final Font value) {
	this.checkWidget ();
	final Font oldFont = this.getFont ();
	super.setFont (value);
	final Font font = this.getFont ();
	if (font.equals (oldFont)) {
    return;
  }

	final GC gc = new GC (this);

	/* recompute the receiver's cached font height and item height values */
	this.fontHeight = gc.getFontMetrics ().getHeight ();
	this.setItemHeight (Math.max (this.fontHeight, this.imageHeight) + (2 * this.getCellPadding ()));
	final Point headerSize = this.header.getSize ();
	final int newHeaderHeight = Math.max (this.fontHeight, this.headerImageHeight) + (2 * this.getHeaderPadding ());
	if (headerSize.y != newHeaderHeight) {
		this.header.setSize (headerSize.x, newHeaderHeight);
	}
	this.header.setFont (font);

	/*
	 * Notify all columns and items of the font change so that elements that
	 * use the receiver's font can recompute their cached string widths.
	 */
	for (final CTableColumn column : this.columns) {
		column.updateFont (gc);
	}
	for (int i = 0; i < this.itemsCount; i++) {
		this.items [i].updateFont (gc);
	}

	gc.dispose ();

	if ((this.drawCount <= 0) && this.header.isVisible ()) {
    this.header.redraw ();
  }

	/* update scrollbars */
	if (this.columns.length == 0) {
    this.updateHorizontalBar ();
  }
	final ScrollBar vBar = this.getVerticalBar ();
	if (vBar != null) {
		final int thumb = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
		vBar.setThumb (thumb);
		vBar.setPageIncrement (thumb);
		this.topIndex = vBar.getSelection ();
		vBar.setVisible (thumb < vBar.getMaximum ());
	}
	this.redraw ();
}
void setHeaderImageHeight (final int value) {
	this.headerImageHeight = value;
	final Point headerSize = this.header.getSize ();
	final int newHeaderHeight = Math.max (this.fontHeight, this.headerImageHeight) + (2 * this.getHeaderPadding ());
	if (headerSize.y != newHeaderHeight) {
		this.header.setSize (headerSize.x, newHeaderHeight);
	}
}
/**
 * Marks the receiver's header as visible if the argument is <code>true</code>,
 * and marks it invisible otherwise.
 * <p>
 * If one of the receiver's ancestors is not visible or some
 * other condition makes the receiver not visible, marking
 * it visible may not actually cause it to be displayed.
 * </p>
 *
 * @param show the new visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setHeaderVisible (final boolean value) {
	this.checkWidget ();
	if (this.header.getVisible () == value) {
    return;		/* no change */
  }
	this.headerHideToolTip ();
	this.header.setVisible (value);
	this.updateVerticalBar ();
	this.redraw ();
}
void setImageHeight (final int value) {
	this.imageHeight = value;
	this.setItemHeight (Math.max (this.fontHeight, this.imageHeight) + (2 * this.getCellPadding ()));
}
/**
 * Sets the number of items contained in the receiver.
 *
 * @param count the number of items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public void setItemCount (int count) {
	this.checkWidget ();
	count = Math.max (0, count);
	if (count == this.itemsCount) {
    return;
  }
	final int oldCount = this.itemsCount;
	int redrawStart, redrawEnd;

	/* if the new item count is less than the current count then remove all excess items from the end */
	if (count < this.itemsCount) {
		redrawStart = count;
		redrawEnd = this.itemsCount - 1;
		for (int i = count; i < this.itemsCount; i++) {
			this.items [i].dispose (false);
		}

		int newSelectedCount = 0;
		for (final CTableItem selectedItem : this.selectedItems) {
			if (!selectedItem.isDisposed ()) {
        newSelectedCount++;
      }
		}
		if (newSelectedCount != this.selectedItems.length) {
			/* one or more selected items have been disposed */
			final CTableItem[] newSelectedItems = new CTableItem [newSelectedCount];
			int pos = 0;
			for (final CTableItem selectedItem : this.selectedItems) {
				final CTableItem item = selectedItem;
				if (!item.isDisposed ()) {
					newSelectedItems [pos++] = item;
				}
			}
			this.selectedItems = newSelectedItems;
		}

		if ((this.anchorItem != null) && this.anchorItem.isDisposed ()) {
      this.anchorItem = null;
    }
		if ((this.lastClickedItem != null) && this.lastClickedItem.isDisposed ()) {
      this.lastClickedItem = null;
    }
		if ((this.focusItem != null) && this.focusItem.isDisposed ()) {
			final CTableItem newFocusItem = count > 0 ? this.items [count - 1] : null;
			this.setFocusItem (newFocusItem, false);
		}
		final int[] eventData = new int[5];
		eventData[0] = ACC.DELETE;
		eventData[1] = redrawStart;
		eventData[2] = redrawEnd - redrawStart;
		eventData[3] = 0;
		eventData[4] = 0;
		this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);

		this.itemsCount = count;
		if (this.columns.length == 0) {
      this.updateHorizontalBar ();
    }
	} else {
		redrawStart = this.itemsCount;
		redrawEnd = count - 1;
		final CTableItem[] newItems = new CTableItem [count];
		System.arraycopy (this.items, 0, newItems, 0, this.itemsCount);
		this.items = newItems;
		for (int i = this.itemsCount; i < count; i++) {
			this.items [i] = new CTableItem (this, SWT.NONE, i, false);
			this.itemsCount++;
		}

		final int[] eventData = new int[5];
		eventData[0] = ACC.INSERT;
		eventData[1] = redrawStart;
		eventData[2] = count;
		eventData[3] = 0;
		eventData[4] = 0;
		this.getAccessible().sendEvent(ACC.EVENT_TABLE_CHANGED, eventData);
		if (oldCount == 0) {
      this.focusItem = this.items [0];
    }
	}

	this.updateVerticalBar ();
	/*
	 * If this is the focus control and the item count is going from 0->!0 or !0->0 then the
	 * receiver must be redrawn to ensure that its boundary focus ring is updated.
	 */
	if (((oldCount == 0) || (this.itemsCount == 0)) && this.isFocusControl ()) {
		this.redraw ();
		return;
	}
	this.redrawItems (redrawStart, redrawEnd, false);
}
boolean setItemHeight (final int value) {
	final boolean update = !this.customHeightSet || (this.itemHeight < value);
	if (update) {
    this.itemHeight = value;
  }
	return update;
}
/**
 * Marks the receiver's lines as visible if the argument is <code>true</code>,
 * and marks it invisible otherwise. Note that some platforms draw grid lines
 * while others may draw alternating row colors.
 * <p>
 * If one of the receiver's ancestors is not visible or some
 * other condition makes the receiver not visible, marking
 * it visible may not actually cause it to be displayed.
 * </p>
 *
 * @param show the new visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setLinesVisible (final boolean value) {
	this.checkWidget ();
	if (this.linesVisible == value) {
    return;		/* no change */
  }
	this.linesVisible = value;
	this.redraw ();
}
@Override
public void setMenu (final Menu menu) {
	super.setMenu (menu);
	this.header.setMenu (menu);
}
@Override
public void setRedraw (final boolean value) {
	this.checkWidget();
	if (value) {
		if (--this.drawCount == 0) {
			if ((this.items.length - this.itemsCount) > 3) {
				final CTableItem[] newItems = new CTableItem [this.itemsCount];
				System.arraycopy (this.items, 0, newItems, 0, this.itemsCount);
				this.items = newItems;
			}
			this.updateVerticalBar ();
			this.updateHorizontalBar ();
		}
	} else {
		this.drawCount++;
	}
	super.setRedraw (value);
	this.header.setRedraw (value);
}
/**
 * Sets the receiver's selection to the given item.
 * The current selection is cleared before the new item is selected.
 * <p>
 * If the item is not in the receiver, then it is ignored.
 * </p>
 *
 * @param item the item to select
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.2
 */
public void setSelection (final CTableItem item) {
	this.checkWidget ();
	if (item == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	this.setSelection (new CTableItem[] {item}, true);
}
/**
 * Sets the receiver's selection to be the given array of items.
 * The current selection is cleared before the new items are selected.
 * <p>
 * Items that are not in the receiver are ignored.
 * If the receiver is single-select and multiple items are specified,
 * then all items are ignored.
 * </p>
 *
 * @param items the array of items
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if one of the items has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#deselectAll()
 * @see CTable#select(int[])
 * @see CTable#setSelection(int[])
 */
public void setSelection (final CTableItem[] items) {
	this.checkWidget ();
	if (items == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	this.setSelection (items, true);
}
void setSelection (final CTableItem[] items, final boolean updateViewport) {
	if ((items.length == 0) || (((this.getStyle () & SWT.SINGLE) != 0) && (items.length > 1))) {
		this.deselectAll ();
		return;
	}
	final CTableItem[] oldSelection = this.selectedItems;

	/* remove null and duplicate items */
	int index = 0;
	this.selectedItems = new CTableItem [items.length];	/* assume all valid items */
	for (final CTableItem item2 : items) {
		final CTableItem item = item2;
		if ((item != null) && (item.parent == this) && !item.isSelected ()) {
			this.selectedItems [index++] = item;
		}
	}
	if (index != items.length) {
		/* an invalid item was provided so resize the array accordingly */
		final CTableItem[] temp = new CTableItem [index];
		System.arraycopy (this.selectedItems, 0, temp, 0, index);
		this.selectedItems = temp;
	}
	if (this.selectedItems.length == 0) {	/* no valid items */
		this.deselectAll ();
		return;
	}

	boolean tableSelectionChanged = false;
	if (this.isFocusControl () || ((this.getStyle () & SWT.HIDE_SELECTION) == 0)) {
		for (final CTableItem item : oldSelection) {
			if (!item.isSelected ()) {
				this.redrawItem (item.index, true);
				item.getAccessible(this.getAccessible(), 0).selectionChanged();
				tableSelectionChanged = true;
			}
		}
		for (final CTableItem selectedItem : this.selectedItems) {
			this.redrawItem (selectedItem.index, true);
			selectedItem.getAccessible(this.getAccessible(), 0).selectionChanged();
			tableSelectionChanged = true;
		}
	}
	if (updateViewport) {
		this.showItem (this.selectedItems [0]);
		this.setFocusItem (this.selectedItems [0], true);
	}

	if (tableSelectionChanged) {
    this.getAccessible().selectionChanged();
  }
}
/**
 * Sets the column used by the sort indicator for the receiver. A null
 * value will clear the sort indicator.  The current sort column is cleared
 * before the new column is set.
 *
 * @param column the column used by the sort indicator or <code>null</code>
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the column is disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.2
 */
public void setSortColumn (final CTableColumn column) {
	this.checkWidget ();
	if ((column != null) && column.isDisposed ()) {
    SWT.error (SWT.ERROR_INVALID_ARGUMENT);
  }
	if (column == this.sortColumn) {
    return;
  }
	if ((this.sortColumn != null) && !this.sortColumn.isDisposed ()) {
		this.sortColumn.setSortDirection (SWT.NONE);
	}
	this.sortColumn = column;
	if (this.sortColumn != null) {
		this.sortColumn.setSortDirection (this.sortDirection);
	}
}
/**
 * Sets the direction of the sort indicator for the receiver. The value
 * can be one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
 *
 * @param direction the direction of the sort indicator
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.2
 */
public void setSortDirection (final int direction) {
	this.checkWidget ();
	if ((direction != SWT.UP) && (direction != SWT.DOWN) && (direction != SWT.NONE)) {
    return;
  }
	this.sortDirection = direction;
	if ((this.sortColumn == null) || this.sortColumn.isDisposed ()) {
    return;
  }
	this.sortColumn.setSortDirection (this.sortDirection);
}
/**
 * Selects the item at the given zero-relative index in the receiver.
 * The current selection is first cleared, then the new item is selected.
 *
 * @param index the index of the item to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#deselectAll()
 * @see CTable#select(int)
 */
public void setSelection (final int index) {
	this.checkWidget ();
	this.deselectAll ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    return;
  }
	this.selectItem (this.items [index], false);
	this.setFocusItem (this.items [index], true);
	this.redrawItem (index, true);
	this.showSelection ();
	this.getAccessible().selectionChanged();
}
/**
 * Selects the items in the range specified by the given zero-relative
 * indices in the receiver. The range of indices is inclusive.
 * The current selection is cleared before the new items are selected.
 * <p>
 * Indices that are out of range are ignored and no items will be selected
 * if start is greater than end.
 * If the receiver is single-select and there is more than one item in the
 * given range, then all indices are ignored.
 * </p>
 *
 * @param start the start index of the items to select
 * @param end the end index of the items to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#deselectAll()
 * @see CTable#select(int,int)
 */
public void setSelection (int start, int end) {
	this.checkWidget ();
	this.deselectAll ();
	if ((end < 0) || (start > end) || (((this.getStyle () & SWT.SINGLE) != 0) && (start != end))) {
    return;
  }
	if ((this.itemsCount == 0) || (start >= this.itemsCount)) {
    return;
  }
	start = Math.max (0, start);
	end = Math.min (end, this.itemsCount - 1);
	this.select (start, end);
	this.setFocusItem (this.items [start], true);
	this.showSelection ();
}
/**
 * Selects the items at the given zero-relative indices in the receiver.
 * The current selection is cleared before the new items are selected.
 * <p>
 * Indices that are out of range and duplicate indices are ignored.
 * If the receiver is single-select and multiple indices are specified,
 * then all indices are ignored.
 * </p>
 *
 * @param indices the indices of the items to select
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#deselectAll()
 * @see CTable#select(int[])
 */
public void setSelection (final int [] indices) {
	this.checkWidget ();
	if (indices == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	this.deselectAll ();
	final int length = indices.length;
	if ((length == 0) || (((this.getStyle () & SWT.SINGLE) != 0) && (length > 1))) {
    return;
  }
	this.select (indices);
	int focusIndex = -1;
	for (int i = 0; (i < indices.length) && (focusIndex == -1); i++) {
		if ((0 <= indices [i]) && (indices [i] < this.itemsCount)) {
			focusIndex = indices [i];
		}
	}
	if (focusIndex != -1) {
    this.setFocusItem (this.items [focusIndex], true);
  }
	this.showSelection ();
}
/**
 * Sets the zero-relative index of the item which is currently
 * at the top of the receiver. This index can change when items
 * are scrolled or new items are added and removed.
 *
 * @param index the index of the top item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setTopIndex (int index) {
	this.checkWidget ();
	if (!((0 <= index) && (index < this.itemsCount))) {
    return;
  }
	final int visibleItemCount = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
	if (this.itemsCount <= visibleItemCount) {
    return;
  }
	index = Math.min (index, this.itemsCount - visibleItemCount);
	if (index == this.topIndex) {
    return;
  }

	this.update ();
	final int change = this.topIndex - index;
	this.topIndex = index;
	final ScrollBar vBar = this.getVerticalBar ();
	if (vBar != null) {
    vBar.setSelection (this.topIndex);
  }
	if (this.drawCount <= 0) {
		final GC gc = new GC (this);
		gc.copyArea (0, 0, this.clientArea.width, this.clientArea.height, 0, change * this.itemHeight);
		gc.dispose ();
	}
}
/**
 * Shows the column.  If the column is already showing in the receiver,
 * this method simply returns.  Otherwise, the columns are scrolled until
 * the column is visible.
 *
 * @param column the column to be shown
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the column is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the column has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @since 3.0
 */
public void showColumn (final CTableColumn column) {
	this.checkWidget ();
	if (column == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (column.isDisposed ()) {
    SWT.error(SWT.ERROR_INVALID_ARGUMENT);
  }
	if (column.parent != this) {
    return;
  }

	final int x = column.getX ();
	final int rightX = x + column.width;
	if ((0 <= x) && (rightX <= this.clientArea.width)) {
    return;	 /* column is fully visible */
  }

	this.headerHideToolTip ();
	int absX = 0;	/* the X of the column irrespective of the horizontal scroll */
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	for (int i = 0; i < column.getOrderIndex (); i++) {
		absX += orderedColumns [i].width;
	}
	if (x < this.clientArea.x) { 	/* column is to left of viewport */
		this.horizontalOffset = absX;
	} else {
		this.horizontalOffset = (absX + column.width) - this.clientArea.width;
	}
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar != null) {
    hBar.setSelection (this.horizontalOffset);
  }
	this.redraw ();
	if ((this.drawCount <= 0) && this.header.isVisible ()) {
    this.header.redraw ();
  }
}
/**
 * Shows the item.  If the item is already showing in the receiver,
 * this method simply returns.  Otherwise, the items are scrolled until
 * the item is visible.
 *
 * @param item the item to be shown
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#showSelection()
 */
public void showItem (final CTableItem item) {
	this.checkWidget ();
	if (item == null) {
    SWT.error (SWT.ERROR_NULL_ARGUMENT);
  }
	if (item.isDisposed ()) {
    SWT.error (SWT.ERROR_INVALID_ARGUMENT);
  }
	if (item.parent != this) {
    return;
  }

	final int index = item.index;
	int visibleItemCount = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
	/* nothing to do if item is already in viewport */
	if ((this.topIndex <= index) && (index < (this.topIndex + visibleItemCount))) {
    return;
  }

	if (index <= this.topIndex) {
		/* item is above current viewport, so show on top */
		this.setTopIndex (item.index);
	} else {
		/* item is below current viewport, so show on bottom */
		visibleItemCount = Math.max (visibleItemCount, 1);	/* item to show should be top item */
		this.setTopIndex (Math.min ((index - visibleItemCount) + 1, this.itemsCount - 1));
	}
}
/**
 * Shows the selection.  If the selection is already showing in the receiver,
 * this method simply returns.  Otherwise, the items are scrolled until
 * the selection is visible.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see CTable#showItem(CTableItem)
 */
public void showSelection () {
	this.checkWidget ();
	if (this.selectedItems.length == 0) {
    return;
  }
	this.showItem (this.selectedItems [0]);
}
void sortDescent (final int [] items) {
	/* Shell Sort from K&R, pg 108 */
	final int length = items.length;
	for (int gap = length / 2; gap > 0; gap /= 2) {
		for (int i = gap; i < length; i++) {
			for (int j = i - gap; j >= 0; j -= gap) {
				if (items [j] <= items [j + gap]) {
					final int swap = items [j];
					items [j] = items [j + gap];
					items [j + gap] = swap;
				}
			}
		}
	}
}
void sortAscent (final int [] items) {
	/* Shell Sort from K&R, pg 108 */
	final int length = items.length;
	for (int gap = length / 2; gap > 0; gap /= 2) {
		for (int i = gap; i < length; i++) {
			for (int j = i - gap; j >= 0; j -= gap) {
				if (items [j] >= items [j + gap]) {
					final int swap = items [j];
					items [j] = items [j + gap];
					items [j + gap] = swap;
				}
			}
		}
	}
}
void sortAscent (final CTableItem [] items) {
	/* Shell Sort from K&R, pg 108 */
	final int length = items.length;
	for (int gap = length / 2; gap > 0; gap /= 2) {
		for (int i = gap; i < length; i++) {
			for (int j = i - gap; j >= 0; j -= gap) {
				if (items [j].index >= items [j + gap].index) {
					final CTableItem swap = items [j];
					items [j] = items [j + gap];
					items [j + gap] = swap;
				}
			}
		}
	}
}
void updateColumnWidth (final CTableColumn column, final int width) {
	this.headerHideToolTip ();
	final int oldWidth = column.width;
	final int columnX = column.getX ();
	int x = (columnX + oldWidth) - 1;	/* -1 ensures that grid line is included */

	this.update ();
	final GC gc = new GC (this);
	gc.copyArea (x, 0, this.clientArea.width - x, this.clientArea.height, (columnX + width) - 1, 0);	/* dest x -1 offsets x's -1 above */
	if (width > oldWidth) {
		/* column width grew */
		final int change = (width - oldWidth) + 1;	/* +1 offsets x's -1 above */
		/* -1/+1 below ensure that right bound of selection redraws correctly in column */
		this.redraw (x - 1, 0, change + 1, this.clientArea.height, false);
	} else {
		final int change = (oldWidth - width) + 1;	/* +1 offsets x's -1 above */
		this.redraw (this.clientArea.width - change, 0, change, this.clientArea.height, false);
	}
	/* the focus box must be repainted because its stipple may become shifted as a result of its new width */
	if (this.focusItem != null) {
    this.redrawItem (this.focusItem.index, true);
  }

	final GC headerGC = new GC (this.header);
	if ((this.drawCount <= 0) && this.header.getVisible ()) {
		final Rectangle headerBounds = this.header.getClientArea ();
		this.header.update ();
		x -= 1;	/* -1 ensures that full header column separator is included */
		headerGC.copyArea (x, 0, headerBounds.width - x, headerBounds.height, (columnX + width) - 2, 0);	/* dest x -2 offsets x's -1s above */
		if (width > oldWidth) {
			/* column width grew */
			final int change = (width - oldWidth) + 2;	/* +2 offsets x's -1s above */
			this.header.redraw (x, 0, change, headerBounds.height, false);
		} else {
			final int change = (oldWidth - width) + 2;	/* +2 offsets x's -1s above */
			this.header.redraw (headerBounds.width - change, 0, change, headerBounds.height, false);
		}
	}

	column.width = width;

	/*
	 * Notify column and all items of column width change so that display labels
	 * can be recomputed if needed.
	 */
	column.updateWidth (headerGC);
	headerGC.dispose ();
	for (int i = 0; i < this.itemsCount; i++) {
		this.items [i].updateColumnWidth (column, gc);
	}
	gc.dispose ();

	int maximum = 0;
	for (final CTableColumn column2 : this.columns) {
		maximum += column2.width;
	}
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar != null) {
		hBar.setMaximum (Math.max (1, maximum));	/* setting a value of 0 here is ignored */
		if (hBar.getThumb () != this.clientArea.width) {
			hBar.setThumb (this.clientArea.width);
			hBar.setPageIncrement (this.clientArea.width);
		}
		final int oldHorizontalOffset = this.horizontalOffset;	/* hBar.setVisible() can modify horizontalOffset */
		hBar.setVisible (this.clientArea.width < maximum);
		final int selection = hBar.getSelection ();
		if (selection != oldHorizontalOffset) {
			this.horizontalOffset = selection;
			this.redraw ();
			if ((this.drawCount <= 0) && this.header.getVisible ()) {
        this.header.redraw ();
      }
		}
	}

	column.notifyListeners (SWT.Resize, new Event ());
	final CTableColumn[] orderedColumns = this.getOrderedColumns ();
	for (int i = column.getOrderIndex () + 1; i < orderedColumns.length; i++) {
		if (!orderedColumns [i].isDisposed ()) {
			orderedColumns [i].notifyListeners (SWT.Move, new Event ());
		}
	}

	if (this.itemsCount == 0) {
    this.redraw ();	/* ensure that static focus rectangle updates properly */
  }
}
/*
 * This is a naive implementation that computes the value from scratch.
 */
void updateHorizontalBar () {
	if (this.drawCount > 0) {
    return;
  }
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar == null) {
    return;
  }

	int maxX = 0;
	if (this.columns.length > 0) {
		for (final CTableColumn column : this.columns) {
			maxX += column.width;
		}
	} else {
		for (int i = 0; i < this.itemsCount; i++) {
			final Rectangle itemBounds = this.items [i].getCellBounds (0);
			maxX = Math.max (maxX, itemBounds.x + itemBounds.width + this.horizontalOffset);
		}
	}

	final int clientWidth = this.clientArea.width;
	if (maxX != hBar.getMaximum ()) {
		hBar.setMaximum (Math.max (1, maxX));	/* setting a value of 0 here is ignored */
	}
	final int thumb = Math.min (clientWidth, maxX);
	if (thumb != hBar.getThumb ()) {
		hBar.setThumb (thumb);
		hBar.setPageIncrement (thumb);
	}
	hBar.setVisible (clientWidth < maxX);

	/* reclaim any space now left on the right */
	if (maxX < (this.horizontalOffset + thumb)) {
		this.horizontalOffset = maxX - thumb;
		hBar.setSelection (this.horizontalOffset);
		this.redraw ();
	} else {
		final int selection = hBar.getSelection ();
		if (selection != this.horizontalOffset) {
			this.horizontalOffset = selection;
			this.redraw ();
		}
	}
}
/*
 * Update the horizontal bar, if needed, in response to an item change (eg.- created,
 * disposed, expanded, etc.).  newRightX is the new rightmost X value of the item,
 * and rightXchange is the change that led to the item's rightmost X value becoming
 * newRightX (so oldRightX + rightXchange = newRightX)
 */
void updateHorizontalBar (int newRightX, final int rightXchange) {
	if (this.drawCount > 0) {
    return;
  }
	final ScrollBar hBar = this.getHorizontalBar ();
	if (hBar == null) {
    return;
  }

	newRightX += this.horizontalOffset;
	final int barMaximum = hBar.getMaximum ();
	if (newRightX > barMaximum) {	/* item has extended beyond previous maximum */
		hBar.setMaximum (newRightX);
		final int clientAreaWidth = this.clientArea.width;
		final int thumb = Math.min (newRightX, clientAreaWidth);
		hBar.setThumb (thumb);
		hBar.setPageIncrement (thumb);
		hBar.setVisible (clientAreaWidth <= newRightX);
		return;
	}

	final int previousRightX = newRightX - rightXchange;
	if (previousRightX != barMaximum) {
		/* this was not the rightmost item, so just check for client width change */
		final int clientAreaWidth = this.clientArea.width;
		final int thumb = Math.min (barMaximum, clientAreaWidth);
		hBar.setThumb (thumb);
		hBar.setPageIncrement (thumb);
		hBar.setVisible (clientAreaWidth <= barMaximum);
		return;
	}
	this.updateHorizontalBar ();		/* must search for the new rightmost item */
}
void updateVerticalBar () {
	if (this.drawCount > 0) {
    return;
  }
	final ScrollBar vBar = this.getVerticalBar ();
	if (vBar == null) {
    return;
  }

	final int pageSize = (this.clientArea.height - this.getHeaderHeight ()) / this.itemHeight;
	final int maximum = Math.max (1, this.itemsCount);	/* setting a value of 0 here is ignored */
	if (maximum != vBar.getMaximum ()) {
		vBar.setMaximum (maximum);
	}
	final int thumb = Math.min (pageSize, maximum);
	if (thumb != vBar.getThumb ()) {
		vBar.setThumb (thumb);
		vBar.setPageIncrement (thumb);
	}
	vBar.setVisible (pageSize < maximum);

	/* reclaim any space now left on the bottom */
	if (maximum < (this.topIndex + thumb)) {
		this.topIndex = maximum - thumb;
		vBar.setSelection (this.topIndex);
		this.redraw ();
	} else {
		final int selection = vBar.getSelection ();
		if (selection != this.topIndex) {
			this.topIndex = selection;
			this.redraw ();
		}
	}
}
}
