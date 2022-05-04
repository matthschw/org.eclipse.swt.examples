/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
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
package org.eclipse.swt.examples.layoutexample;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class RowLayoutTab extends Tab {
	/* Controls for setting layout parameters */
	Button horizontal, vertical;
	Button wrap, pack, fill, justify, center;
	Spinner marginWidth, marginHeight, marginLeft, marginRight, marginTop, marginBottom, spacing;
	/* The example layout instance */
	RowLayout rowLayout;
	/* TableEditors and related controls*/
	TableEditor comboEditor, widthEditor, heightEditor, nameEditor, excludeEditor;
	CCombo combo, exclude;
	Text nameText, widthText, heightText;

	/* Constants */
	final int NAME_COL = 0;
	final int COMBO_COL = 1;
	final int WIDTH_COL = 2;
	final int HEIGHT_COL = 3;
	final int EXCLUDE_COL = 4;
	final int TOTAL_COLS = 5;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	RowLayoutTab(final LayoutExample instance) {
		super(instance);
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	@Override
	void createChildWidgets () {
		/* Add common controls */
		super.createChildWidgets ();

		/* Add TableEditors */
		this.nameEditor = new TableEditor (this.table);
		this.comboEditor = new TableEditor (this.table);
		this.widthEditor = new TableEditor (this.table);
		this.heightEditor = new TableEditor (this.table);
		this.excludeEditor = new TableEditor (this.table);
		this.table.addMouseListener(MouseListener.mouseDownAdapter(e -> {
			this.resetEditors();
			this.index = this.table.getSelectionIndex();
			final Point pt = new Point(e.x, e.y);
			this.newItem = this.table.getItem(pt);
			if (this.newItem == null) {
        return;
      }
			final TableItem oldItem = this.comboEditor.getItem();
			if ((this.newItem == oldItem) || (this.newItem != this.lastSelected)) {
				this.lastSelected = this.newItem;
				return;
			}
			this.table.showSelection();

			this.combo = new CCombo(this.table, SWT.READ_ONLY);
			this.createComboEditor(this.combo, this.comboEditor);

			this.nameText = new Text(this.table, SWT.SINGLE);
			this.nameText.setText(this.data.get(this.index)[this.NAME_COL]);
			this.createTextEditor(this.nameText, this.nameEditor, this.NAME_COL);

			this.widthText = new Text(this.table, SWT.SINGLE);
			this.widthText.setText(this.data.get(this.index)[this.WIDTH_COL]);
			this.createTextEditor(this.widthText, this.widthEditor, this.WIDTH_COL);

			this.heightText = new Text(this.table, SWT.SINGLE);
			this.heightText.setText(this.data.get(this.index)[this.HEIGHT_COL]);
			this.createTextEditor(this.heightText, this.heightEditor, this.HEIGHT_COL);

			final String[] boolValues = new String[] { "false", "true" };
			this.exclude = new CCombo(this.table, SWT.NONE);
			this.exclude.setItems(boolValues);
			this.exclude.setText(this.newItem.getText(this.EXCLUDE_COL));
			this.excludeEditor.horizontalAlignment = SWT.LEFT;
			this.excludeEditor.grabHorizontal = true;
			this.excludeEditor.minimumWidth = 50;
			this.excludeEditor.setEditor(this.exclude, this.newItem, this.EXCLUDE_COL);
			this.exclude.addTraverseListener(this.traverseListener);

			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final Rectangle rect = this.newItem.getBounds(i);
				if (rect.contains(pt)) {
					switch (i) {
					case NAME_COL:
						this.nameText.setFocus();
					case COMBO_COL:
						this.combo.setFocus();
						break;
					case WIDTH_COL:
						this.widthText.setFocus();
						break;
					case HEIGHT_COL:
						this.heightText.setFocus();
						break;
					case EXCLUDE_COL:
						this.exclude.setFocus();
						break;
					default:
						this.resetEditors();
						break;
					}
				}
			}
		}));
	}

	/**
	 * Creates the control widgets.
	 */
	@Override
	void createControlWidgets () {
		/* Controls the type of RowLayout */
		final Group typeGroup = new Group (this.controlGroup, SWT.NONE);
		typeGroup.setText (LayoutExample.getResourceString ("Type"));
		typeGroup.setLayout (new GridLayout ());
		typeGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, false));
		this.horizontal = new Button (typeGroup, SWT.RADIO);
		this.horizontal.setText ("SWT.HORIZONTAL");
		this.horizontal.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.horizontal.setSelection(true);
		this.horizontal.addSelectionListener (this.selectionListener);
		this.vertical = new Button (typeGroup, SWT.RADIO);
		this.vertical.setText ("SWT.VERTICAL");
		this.vertical.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.vertical.addSelectionListener (this.selectionListener);

		/* Controls the margins and spacing of the RowLayout */
		final Group marginGroup = new Group (this.controlGroup, SWT.NONE);
		marginGroup.setText (LayoutExample.getResourceString ("Margins_Spacing"));
		marginGroup.setLayout(new GridLayout(2, false));
		marginGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, false, 1, 2));
		new Label(marginGroup, SWT.NONE).setText("marginWidth");
		this.marginWidth = new Spinner(marginGroup, SWT.BORDER);
		this.marginWidth.setSelection(0);
		this.marginWidth.addSelectionListener(this.selectionListener);
		new Label (marginGroup, SWT.NONE).setText ("marginHeight");
		this.marginHeight = new Spinner(marginGroup, SWT.BORDER);
		this.marginHeight.setSelection(0);
		this.marginHeight.setLayoutData (new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		this.marginHeight.addSelectionListener (this.selectionListener);
		new Label (marginGroup, SWT.NONE).setText ("marginLeft");
		this.marginLeft = new Spinner(marginGroup, SWT.BORDER);
		this.marginLeft.setSelection(3);
		this.marginLeft.addSelectionListener (this.selectionListener);
		new Label (marginGroup, SWT.NONE).setText ("marginRight");
		this.marginRight = new Spinner(marginGroup, SWT.BORDER);
		this.marginRight.setSelection(3);
		this.marginRight.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginTop");
		this.marginTop = new Spinner(marginGroup, SWT.BORDER);
		this.marginTop.setSelection(3);
		this.marginTop.addSelectionListener(this.selectionListener);
		new Label (marginGroup, SWT.NONE).setText ("marginBottom");
		this.marginBottom = new Spinner(marginGroup, SWT.BORDER);
		this.marginBottom.setSelection(3);
		this.marginBottom.addSelectionListener (this.selectionListener);
		new Label (marginGroup, SWT.NONE).setText ("spacing");
		this.spacing = new Spinner(marginGroup, SWT.BORDER);
		this.spacing.setSelection(3);
		this.spacing.addSelectionListener (this.selectionListener);

		/* Controls other parameters of the RowLayout */
		final Group specGroup = new Group (this.controlGroup, SWT.NONE);
		specGroup.setText (LayoutExample.getResourceString ("Properties"));
		specGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		specGroup.setLayout (new GridLayout ());
		this.wrap = new Button (specGroup, SWT.CHECK);
		this.wrap.setText ("Wrap");
		this.wrap.setSelection (true);
		this.wrap.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.wrap.addSelectionListener (this.selectionListener);
		this.pack = new Button (specGroup, SWT.CHECK);
		this.pack.setText ("Pack");
		this.pack.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.pack.setSelection (true);
		this.pack.addSelectionListener(this.selectionListener);
		this.fill = new Button(specGroup, SWT.CHECK);
		this.fill.setText("Fill");
		this.fill.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.fill.addSelectionListener(this.selectionListener);
		this.justify = new Button (specGroup, SWT.CHECK);
		this.justify.setText ("Justify");
		this.justify.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.justify.addSelectionListener (this.selectionListener);
		this.center = new Button (specGroup, SWT.CHECK);
		this.center.setText ("Center");
		this.center.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.center.addSelectionListener (this.selectionListener);

		/* Add common controls */
		super.createControlWidgets ();
	}

	/**
	 * Creates the example layout.
	 */
	@Override
	void createLayout () {
		this.rowLayout = new RowLayout ();
		this.layoutComposite.setLayout (this.rowLayout);
	}

	/**
	 * Disposes the editors without placing their contents
	 * into the table.
	 */
	@Override
	void disposeEditors () {
		this.comboEditor.setEditor (null, null, -1);
		this.combo.dispose ();
		this.widthText.dispose ();
		this.heightText.dispose ();
		this.nameText.dispose ();
		this.exclude.dispose ();
	}

	/**
	 * Generates code for the example layout.
	 */
	@Override
	StringBuilder generateLayoutCode () {
		final StringBuilder code = new StringBuilder ();
		code.append ("\t\tRowLayout rowLayout = new RowLayout ();\n");
		if (this.rowLayout.type == SWT.VERTICAL) {
			code.append ("\t\trowLayout.type = SWT.VERTICAL;\n");
		}
		if (!this.rowLayout.wrap) {
			code.append ("\t\trowLayout.wrap = false;\n");
		}
		if (!this.rowLayout.pack) {
			code.append ("\t\trowLayout.pack = false;\n");
		}
		if (this.rowLayout.fill) {
			code.append("\t\trowLayout.fill = true;\n");
		}
		if (this.rowLayout.justify) {
			code.append ("\t\trowLayout.justify = true;\n");
		}
		if (this.rowLayout.center) {
			code.append ("\t\trowLayout.center = true;\n");
		}
		if (this.rowLayout.marginWidth != 0) {
			code.append("\t\trowLayout.marginWidth = " + this.rowLayout.marginWidth + ";\n");
		}
		if (this.rowLayout.marginHeight != 0) {
			code.append("\t\trowLayout.marginHeight = " + this.rowLayout.marginHeight + ";\n");
		}
		if (this.rowLayout.marginLeft != 3) {
			code.append ("\t\trowLayout.marginLeft = " + this.rowLayout.marginLeft + ";\n");
		}
		if (this.rowLayout.marginRight != 3) {
			code.append ("\t\trowLayout.marginRight = " + this.rowLayout.marginRight + ";\n");
		}
		if (this.rowLayout.marginTop != 3) {
			code.append ("\t\trowLayout.marginTop = " + this.rowLayout.marginTop + ";\n");
		}
		if (this.rowLayout.marginBottom != 3) {
			code.append ("\t\trowLayout.marginBottom = " + this.rowLayout.marginBottom + ";\n");
		}
		if (this.rowLayout.spacing != 3) {
			code.append ("\t\trowLayout.spacing = " + this.rowLayout.spacing + ";\n");
		}
		code.append ("\t\tshell.setLayout (rowLayout);\n");

		boolean first = true;
		for (int i = 0; i < this.children.length; i++) {
			final Control control = this.children [i];
			code.append (this.getChildCode (control,i));
			final RowData rowData = (RowData) control.getLayoutData ();
			if (rowData != null) {
				if ((rowData.width != -1) || (rowData.height != -1) || rowData.exclude) {
					code.append ("\t\t");
					if (first) {
						code.append ("RowData ");
						first = false;
					}
					if ((rowData.width == -1) && (rowData.height == -1)) {
						code.append ("rowData = new RowData ();\n");
					} else if (rowData.width == -1) {
						code.append ("rowData = new RowData (SWT.DEFAULT, " + rowData.height + ");\n");
					} else if (rowData.height == -1) {
						code.append ("rowData = new RowData (" + rowData.width + ", SWT.DEFAULT);\n");
					} else {
						code.append ("rowData = new RowData (" + rowData.width + ", " + rowData.height + ");\n");
					}
					if (rowData.exclude) {
						code.append ("\t\trowData.exclude = true;\n");
					}
					code.append ("\t\t" + this.names [i] + ".setLayoutData (rowData);\n");
				}
			}
		}
		return code;
	}

	/**
	 * Returns the string to insert when a new child control is added to the table.
	 */
	@Override
	String[] getInsertString (final String name, final String controlType) {
		return new String [] {name, controlType, "-1", "-1", "false"};
	}

	/**
	 * Returns the layout data field names.
	 */
	@Override
	String [] getLayoutDataFieldNames() {
		return new String [] {
			"Control Name",
			"Control Type",
			"width",
			"height",
			"exclude"
		};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "RowLayout";
	}

	/**
	 * Takes information from TableEditors and stores it.
	 */
	@Override
	void resetEditors (final boolean tab) {
		final TableItem oldItem = this.comboEditor.getItem ();
		if (oldItem != null) {
			final int row = this.table.indexOf (oldItem);
			/* Make sure user has entered valid data */
			try {
				new String(this.nameText.getText());
			} catch (final NumberFormatException e) {
				this.nameText.setText(oldItem.getText(this.NAME_COL));
			}
			try {
				Integer.parseInt(this.widthText.getText ());
			} catch (final NumberFormatException e) {
				this.widthText.setText (oldItem.getText (this.WIDTH_COL));
			}
			try {
				Integer.parseInt(this.heightText.getText ());
			} catch (final NumberFormatException e) {
				this.heightText.setText (oldItem.getText (this.HEIGHT_COL));
			}
			final String [] insert = new String [] {
				this.nameText.getText(), this.combo.getText (), this.widthText.getText (), this.heightText.getText (), this.exclude.getText ()};
			this.data.set (row, insert);
			for (int i = 0 ; i < this.TOTAL_COLS; i++) {
				oldItem.setText (i, this.data.get (row) [i]);
			}
			if (!tab) {
        this.disposeEditors ();
      }
		}
		this.setLayoutState ();
		this.refreshLayoutComposite ();
		this.setLayoutData ();
		this.layoutComposite.layout (true);
		this.layoutGroup.layout (true);
	}

	/**
	 * Sets the layout data for the children of the layout.
	 */
	@Override
	void setLayoutData () {
		final Control [] children = this.layoutComposite.getChildren ();
		final TableItem [] items = this.table.getItems ();
		RowData data;
		int width, height;
		String exclude;
		for (int i = 0; i < children.length; i++) {
			width = Integer.parseInt(items [i].getText (this.WIDTH_COL));
			height = Integer.parseInt(items [i].getText (this.HEIGHT_COL));
			data = new RowData (width, height);
			exclude = items [i].getText (this.EXCLUDE_COL);
			data.exclude = exclude.equals ("true");
			children [i].setLayoutData (data);
		}

	}

	/**
	 * Sets the state of the layout.
	 */
	@Override
	void setLayoutState () {
		/* Set the type of layout */
		this.rowLayout.type = this.vertical.getSelection () ? SWT.VERTICAL : SWT.HORIZONTAL;

		/* Set the margins and spacing */
		this.rowLayout.marginWidth = this.marginWidth.getSelection ();
		this.rowLayout.marginHeight = this.marginHeight.getSelection ();
		this.rowLayout.marginLeft = this.marginLeft.getSelection ();
		this.rowLayout.marginRight = this.marginRight.getSelection ();
		this.rowLayout.marginTop = this.marginTop.getSelection ();
		this.rowLayout.marginBottom = this.marginBottom.getSelection ();
		this.rowLayout.spacing = this.spacing.getSelection ();

		/* Set the other layout properties */
		this.rowLayout.wrap = this.wrap.getSelection ();
		this.rowLayout.pack = this.pack.getSelection ();
		this.rowLayout.fill = this.fill.getSelection ();
		this.rowLayout.justify = this.justify.getSelection ();
		this.rowLayout.center = this.center.getSelection ();
	}
}
