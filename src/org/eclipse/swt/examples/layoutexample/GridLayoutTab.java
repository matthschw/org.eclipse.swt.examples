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
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class GridLayoutTab extends Tab {
	/* Controls for setting layout parameters */
	Spinner numColumns;
	Button makeColumnsEqualWidth;
	Spinner marginWidth, marginHeight, marginLeft, marginRight, marginTop, marginBottom, horizontalSpacing, verticalSpacing;
	/* The example layout instance */
	GridLayout gridLayout;
	/* TableEditors and related controls*/
	TableEditor nameEditor, comboEditor, widthEditor, heightEditor;
	TableEditor vAlignEditor, hAlignEditor, hGrabEditor, vGrabEditor, hSpanEditor, vSpanEditor;
	TableEditor hIndentEditor, vIndentEditor, minWidthEditor, minHeightEditor, excludeEditor;
	CCombo combo, vAlign, hAlign, hGrab, vGrab, exclude;
	Text nameText, widthText, heightText, hSpan, vSpan, hIndent, vIndent, minWidthText, minHeightText;
	/* Constants */
	static final int NAME_COL = 0;
	static final int COMBO_COL = 1;
	static final int WIDTH_COL = 2;
	static final int HEIGHT_COL = 3;
	static final int HALIGN_COL = 4;
	static final int VALIGN_COL = 5;
	static final int HGRAB_COL = 6;
	static final int VGRAB_COL = 7;
	static final int HSPAN_COL = 8;
	static final int VSPAN_COL = 9;
	static final int HINDENT_COL = 10;
	static final int VINDENT_COL = 11;
	static final int MINWIDTH_COL = 12;
	static final int MINHEIGHT_COL = 13;
	static final int EXCLUDE_COL = 14;

	static final int TOTAL_COLS = 15;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	GridLayoutTab(final LayoutExample instance) {
		super(instance);
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	@Override
	void createChildWidgets () {
		/* Create the TraverseListener */
		final TraverseListener traverseListener = e -> {
			if ((e.detail == SWT.TRAVERSE_RETURN) || (e.detail == SWT.TRAVERSE_TAB_NEXT)) {
        this.resetEditors ();
      }
			if (e.detail == SWT.TRAVERSE_ESCAPE) {
        this.disposeEditors ();
      }
		};

		/* Add common controls */
		super.createChildWidgets ();

		/* Add hovers to the column headers whose field names have been shortened to save space */
		this.table.getColumn (HALIGN_COL).setToolTipText ("horizontalAlignment");
		this.table.getColumn (VALIGN_COL).setToolTipText ("verticalAlignment");
		this.table.getColumn (HGRAB_COL).setToolTipText ("grabExcessHorizontalSpace");
		this.table.getColumn (VGRAB_COL).setToolTipText ("grabExcessVerticalSpace");
		this.table.getColumn (HSPAN_COL).setToolTipText ("horizontalSpan");
		this.table.getColumn (VSPAN_COL).setToolTipText ("verticalSpan");
		this.table.getColumn (HINDENT_COL).setToolTipText ("horizontalIndent");
		this.table.getColumn (VINDENT_COL).setToolTipText ("verticalIndent");
		this.table.getColumn (MINWIDTH_COL).setToolTipText ("minimumWidth");
		this.table.getColumn (MINHEIGHT_COL).setToolTipText ("minimumHeight");

		/* Add TableEditors */
		this.nameEditor = new TableEditor (this.table);
		this.comboEditor = new TableEditor (this.table);
		this.widthEditor = new TableEditor (this.table);
		this.heightEditor = new TableEditor (this.table);
		this.vAlignEditor = new TableEditor (this.table);
		this.hAlignEditor = new TableEditor (this.table);
		this.hGrabEditor = new TableEditor (this.table);
		this.vGrabEditor = new TableEditor (this.table);
		this.hSpanEditor = new TableEditor (this.table);
		this.vSpanEditor = new TableEditor (this.table);
		this.hIndentEditor = new TableEditor (this.table);
		this.vIndentEditor = new TableEditor (this.table);
		this.minWidthEditor = new TableEditor (this.table);
		this.minHeightEditor = new TableEditor (this.table);
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

			this.nameText = new Text(this.table, SWT.SINGLE);
			this.nameText.setText(this.data.get(this.index)[NAME_COL]);
			this.createTextEditor(this.nameText, this.nameEditor, NAME_COL);

			this.combo = new CCombo(this.table, SWT.READ_ONLY);
			this.createComboEditor(this.combo, this.comboEditor);

			this.widthText = new Text(this.table, SWT.SINGLE);
			this.widthText.setText(this.data.get(this.index)[WIDTH_COL]);
			this.createTextEditor(this.widthText, this.widthEditor, WIDTH_COL);

			this.heightText = new Text(this.table, SWT.SINGLE);
			this.heightText.setText(this.data.get(this.index)[HEIGHT_COL]);
			this.createTextEditor(this.heightText, this.heightEditor, HEIGHT_COL);

			final String[] alignValues = new String[] { "BEGINNING", "CENTER", "END", "FILL" };
			this.hAlign = new CCombo(this.table, SWT.NONE);
			this.hAlign.setItems(alignValues);
			this.hAlign.setText(this.newItem.getText(HALIGN_COL));
			this.hAlignEditor.horizontalAlignment = SWT.LEFT;
			this.hAlignEditor.grabHorizontal = true;
			this.hAlignEditor.minimumWidth = 50;
			this.hAlignEditor.setEditor(this.hAlign, this.newItem, HALIGN_COL);
			this.hAlign.addTraverseListener(traverseListener);

			this.vAlign = new CCombo(this.table, SWT.NONE);
			this.vAlign.setItems(alignValues);
			this.vAlign.setText(this.newItem.getText(VALIGN_COL));
			this.vAlignEditor.horizontalAlignment = SWT.LEFT;
			this.vAlignEditor.grabHorizontal = true;
			this.vAlignEditor.minimumWidth = 50;
			this.vAlignEditor.setEditor(this.vAlign, this.newItem, VALIGN_COL);
			this.vAlign.addTraverseListener(traverseListener);

			final String[] boolValues = new String[] { "false", "true" };
			this.hGrab = new CCombo(this.table, SWT.NONE);
			this.hGrab.setItems(boolValues);
			this.hGrab.setText(this.newItem.getText(HGRAB_COL));
			this.hGrabEditor.horizontalAlignment = SWT.LEFT;
			this.hGrabEditor.grabHorizontal = true;
			this.hGrabEditor.minimumWidth = 50;
			this.hGrabEditor.setEditor(this.hGrab, this.newItem, HGRAB_COL);
			this.hGrab.addTraverseListener(traverseListener);

			this.vGrab = new CCombo(this.table, SWT.NONE);
			this.vGrab.setItems(boolValues);
			this.vGrab.setText(this.newItem.getText(VGRAB_COL));
			this.vGrabEditor.horizontalAlignment = SWT.LEFT;
			this.vGrabEditor.grabHorizontal = true;
			this.vGrabEditor.minimumWidth = 50;
			this.vGrabEditor.setEditor(this.vGrab, this.newItem, VGRAB_COL);
			this.vGrab.addTraverseListener(traverseListener);

			this.hSpan = new Text(this.table, SWT.SINGLE);
			this.hSpan.setText(this.data.get(this.index)[HSPAN_COL]);
			this.createTextEditor(this.hSpan, this.hSpanEditor, HSPAN_COL);

			this.vSpan = new Text(this.table, SWT.SINGLE);
			this.vSpan.setText(this.data.get(this.index)[VSPAN_COL]);
			this.createTextEditor(this.vSpan, this.vSpanEditor, VSPAN_COL);

			this.hIndent = new Text(this.table, SWT.SINGLE);
			this.hIndent.setText(this.data.get(this.index)[HINDENT_COL]);
			this.createTextEditor(this.hIndent, this.hIndentEditor, HINDENT_COL);

			this.vIndent = new Text(this.table, SWT.SINGLE);
			this.vIndent.setText(this.data.get(this.index)[VINDENT_COL]);
			this.createTextEditor(this.vIndent, this.vIndentEditor, VINDENT_COL);

			this.minWidthText = new Text(this.table, SWT.SINGLE);
			this.minWidthText.setText(this.data.get(this.index)[MINWIDTH_COL]);
			this.createTextEditor(this.minWidthText, this.minWidthEditor, MINWIDTH_COL);

			this.minHeightText = new Text(this.table, SWT.SINGLE);
			this.minHeightText.setText(this.data.get(this.index)[MINHEIGHT_COL]);
			this.createTextEditor(this.minHeightText, this.minHeightEditor, MINHEIGHT_COL);

			this.exclude = new CCombo(this.table, SWT.NONE);
			this.exclude.setItems(boolValues);
			this.exclude.setText(this.newItem.getText(EXCLUDE_COL));
			this.excludeEditor.horizontalAlignment = SWT.LEFT;
			this.excludeEditor.grabHorizontal = true;
			this.excludeEditor.minimumWidth = 50;
			this.excludeEditor.setEditor(this.exclude, this.newItem, EXCLUDE_COL);
			this.exclude.addTraverseListener(traverseListener);

			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final Rectangle rect = this.newItem.getBounds(i);
				if (rect.contains(pt)) {
					switch (i) {
					case NAME_COL:
						this.nameText.setFocus();
						break;
					case COMBO_COL:
						this.combo.setFocus();
						break;
					case WIDTH_COL:
						this.widthText.setFocus();
						break;
					case HEIGHT_COL:
						this.heightText.setFocus();
						break;
					case HALIGN_COL:
						this.hAlign.setFocus();
						break;
					case VALIGN_COL:
						this.vAlign.setFocus();
						break;
					case HGRAB_COL:
						this.hGrab.setFocus();
						break;
					case VGRAB_COL:
						this.vGrab.setFocus();
						break;
					case HSPAN_COL:
						this.hSpan.setFocus();
						break;
					case VSPAN_COL:
						this.vSpan.setFocus();
						break;
					case HINDENT_COL:
						this.hIndent.setFocus();
						break;
					case VINDENT_COL:
						this.vIndent.setFocus();
						break;
					case MINWIDTH_COL:
						this.minWidthText.setFocus();
						break;
					case MINHEIGHT_COL:
						this.minHeightText.setFocus();
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
		/* Controls the columns in the GridLayout */
		final Group columnGroup = new Group (this.controlGroup, SWT.NONE);
		columnGroup.setText (LayoutExample.getResourceString ("Columns"));
		columnGroup.setLayout(new GridLayout(2, false));
		columnGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, false));
		new Label(columnGroup, SWT.NONE).setText ("numColumns");
		this.numColumns = new Spinner (columnGroup, SWT.BORDER);
		this.numColumns.setMinimum (1);
		this.numColumns.addSelectionListener (this.selectionListener);
		this.makeColumnsEqualWidth = new Button (columnGroup, SWT.CHECK);
		this.makeColumnsEqualWidth.setText ("makeColumnsEqualWidth");
		this.makeColumnsEqualWidth.addSelectionListener (this.selectionListener);
		this.makeColumnsEqualWidth.setEnabled (false);
		this.makeColumnsEqualWidth.setLayoutData (new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		/* Controls the margins and spacing of the GridLayout */
		final Group marginGroup = new Group(this.controlGroup, SWT.NONE);
		marginGroup.setText (LayoutExample.getResourceString("Margins_Spacing"));
		marginGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		marginGroup.setLayout(new GridLayout(2, false));
		new Label (marginGroup, SWT.NONE).setText("marginWidth");
		this.marginWidth = new Spinner (marginGroup, SWT.BORDER);
		this.marginWidth.setSelection(5);
		this.marginWidth.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginHeight");
		this.marginHeight = new Spinner(marginGroup, SWT.BORDER);
		this.marginHeight.setSelection(5);
		this.marginHeight.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginLeft");
		this.marginLeft = new Spinner(marginGroup, SWT.BORDER);
		this.marginLeft.setSelection(0);
		this.marginLeft.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginRight");
		this.marginRight = new Spinner(marginGroup, SWT.BORDER);
		this.marginRight.setSelection(0);
		this.marginRight.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginTop");
		this.marginTop = new Spinner(marginGroup, SWT.BORDER);
		this.marginTop.setSelection(0);
		this.marginTop.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginBottom");
		this.marginBottom = new Spinner(marginGroup, SWT.BORDER);
		this.marginBottom.setSelection(0);
		this.marginBottom.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("horizontalSpacing");
		this.horizontalSpacing = new Spinner(marginGroup, SWT.BORDER);
		this.horizontalSpacing.setSelection(5);
		this.horizontalSpacing.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("verticalSpacing");
		this.verticalSpacing = new Spinner(marginGroup, SWT.BORDER);
		this.verticalSpacing.setSelection(5);
		this.verticalSpacing.addSelectionListener(this.selectionListener);

		/* Add common controls */
		super.createControlWidgets ();
		this.controlGroup.pack();
	}

	/**
	 * Creates the example layout.
	 */
	@Override
	void createLayout () {
		this.gridLayout = new GridLayout ();
		this.layoutComposite.setLayout (this.gridLayout);
	}

	/**
	 * Disposes the editors without placing their contents
	 * into the table.
	 */
	@Override
	void disposeEditors () {
		this.comboEditor.setEditor (null, null, -1);
		this.combo.dispose ();
		this.nameText.dispose ();
		this.widthText.dispose ();
		this.heightText.dispose ();
		this.hAlign.dispose ();
		this.vAlign.dispose ();
		this.hGrab.dispose ();
		this.vGrab.dispose ();
		this.hSpan.dispose ();
		this.vSpan.dispose ();
		this.hIndent.dispose ();
		this.vIndent.dispose ();
		this.minWidthText.dispose ();
		this.minHeightText.dispose ();
		this.exclude.dispose ();
	}

	/**
	 * Generates code for the example layout.
	 */
	@Override
	StringBuilder generateLayoutCode () {
		final StringBuilder code = new StringBuilder ();
		code.append ("\t\tGridLayout gridLayout = new GridLayout (");
		if ((this.gridLayout.numColumns != 1) || this.gridLayout.makeColumnsEqualWidth) {
			code.append (this.gridLayout.numColumns + ", " + this.gridLayout.makeColumnsEqualWidth);
		}
		code.append(");\n");
		if (this.gridLayout.marginWidth != 5) {
			code.append("\t\tgridLayout.marginWidth = " + this.gridLayout.marginWidth + ";\n");
		}
		if (this.gridLayout.marginHeight != 5) {
			code.append ("\t\tgridLayout.marginHeight = " + this.gridLayout.marginHeight + ";\n");
		}
		if (this.gridLayout.marginLeft != 0) {
			code.append ("\t\tgridLayout.marginLeft = " + this.gridLayout.marginLeft + ";\n");
		}
		if (this.gridLayout.marginRight != 0) {
			code.append ("\t\tgridLayout.marginRight = " + this.gridLayout.marginRight + ";\n");
		}
		if (this.gridLayout.marginTop != 0) {
			code.append ("\t\tgridLayout.marginTop = " + this.gridLayout.marginTop + ";\n");
		}
		if (this.gridLayout.marginBottom != 0) {
			code.append ("\t\tgridLayout.marginBottom = " + this.gridLayout.marginBottom + ";\n");
		}
		if (this.gridLayout.horizontalSpacing != 5) {
			code.append ("\t\tgridLayout.horizontalSpacing = " + this.gridLayout.horizontalSpacing + ";\n");
		}
		if (this.gridLayout.verticalSpacing != 5) {
			code.append ("\t\tgridLayout.verticalSpacing = " + this.gridLayout.verticalSpacing + ";\n");
		}
		code.append ("\t\tshell.setLayout (gridLayout);\n");

		boolean first = true;
		boolean bounds, align, grab, span;
		for (int i = 0; i < this.children.length; i++) {
			final Control control = this.children [i];
			code.append (this.getChildCode (control, i));
			final GridData data = (GridData) control.getLayoutData ();
			if (data != null) {
				/* Use the most efficient constructor */
				bounds = (data.widthHint != SWT.DEFAULT) || (data.heightHint != SWT.DEFAULT);
				align = (data.horizontalAlignment != SWT.BEGINNING) || (data.verticalAlignment != SWT.CENTER);
				grab = data.grabExcessHorizontalSpace || data.grabExcessVerticalSpace;
				span = (data.horizontalSpan != 1) || (data.verticalSpan != 1);

				code.append ("\t\t");
				if (first) {
					code.append ("GridData ");
					first = false;
				}
				if (align || grab || span) {
					code.append ("data = new GridData (");
					code.append (this.alignmentString(data.horizontalAlignment) + ", ");
					code.append (this.alignmentString(data.verticalAlignment) + ", ");
					code.append (data.grabExcessHorizontalSpace + ", ");
					code.append (data.grabExcessVerticalSpace);
					if (span) {
						code.append (", " + data.horizontalSpan);
						code.append (", " + data.verticalSpan);
					}
					code.append(");\n");
					if (data.widthHint != SWT.DEFAULT) {
						code.append ("\t\tdata.widthHint = " + data.widthHint + ";\n");
					}
					if (data.heightHint != SWT.DEFAULT) {
						code.append ("\t\tdata.heightHint = " + data.heightHint + ";\n");
					}
				} else if (bounds) {
        	code.append ("data = new GridData (");
        	code.append (data.widthHint == SWT.DEFAULT ? "SWT.DEFAULT" : String.valueOf(data.widthHint) + ", ");
        	code.append (data.heightHint == SWT.DEFAULT ? "SWT.DEFAULT" : String.valueOf(data.heightHint));
        	code.append(");\n");
        } else {
        	code.append ("data = new GridData ();\n");
        }
				if (data.horizontalIndent != 0) {
					code.append ("\t\tdata.horizontalIndent = " + data.horizontalIndent + ";\n");
				}
				if (data.verticalIndent != 0) {
					code.append ("\t\tdata.verticalIndent = " + data.verticalIndent + ";\n");
				}
				if (data.minimumWidth != 0) {
					code.append ("\t\tdata.minimumWidth = " + data.minimumWidth + ";\n");
				}
				if (data.minimumHeight != 0) {
					code.append ("\t\tdata.minimumHeight = " + data.minimumHeight + ";\n");
				}
				if (data.exclude) {
					code.append ("\t\tdata.exclude = true;\n");
				}
				if (code.substring (code.length () - 33).equals ("GridData data = new GridData ();\n")) {
					code.delete (code.length () - 33, code.length ());
					first = true;
				} else if (code.substring (code.length () - 24).equals ("data = new GridData ();\n")) {
					code.delete (code.length () - 24, code.length ());
				} else {
					code.append ("\t\t" + this.names [i] + ".setLayoutData (data);\n");
				}
			}
		}
		return code;
	}

	String alignmentString(final int alignment) {
		switch (alignment) {
    case SWT.BEGINNING:
      return "SWT.BEGINNING";
    case SWT.CENTER:
      return "SWT.CENTER";
    case SWT.END:
      return "SWT.END";
    default:
      break;
    }
		return "SWT.FILL";
	}

	/**
	 * Returns the string to insert when a new child control is added to the table.
	 */
	@Override
	String[] getInsertString (final String name, final String controlType) {
		return new String [] {name, controlType,
				"-1","-1","BEGINNING","CENTER",
				"false","false","1","1","0","0",
				"0","0","false"};
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
			"hAlignment", //"horizontalAlignment",
			"vAlignment", //"verticalAlignment",
			"grabH", //"grabExcessHorizontalSpace",
			"grabV", //"grabExcessVerticalSpace",
			"hSpan", //"horizontalSpan",
			"vSpan", //"verticalSpan",
			"hIndent", //"horizontalIndent",
			"vIndent", //"verticalIndent",
			"minWidth", //"minimumWidth",
			"minHeight", //"minimumHeight",
			"exclude"
		};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "GridLayout";
	}

	/**
	 * Takes information from TableEditors and stores it.
	 */
	@Override
	void resetEditors (final boolean tab) {
		final TableItem oldItem = this.comboEditor.getItem ();
		if (oldItem != null) {
			final int row = this.table.indexOf (oldItem);
			/** Make sure user enters a valid data*/
			try {
				new String (this.nameText.getText ());
			} catch (final NumberFormatException e) {
				this.nameText.setText (oldItem.getText (NAME_COL));
			}
			try {
				Integer.parseInt(this.widthText.getText ());
			} catch (final NumberFormatException e) {
				this.widthText.setText (oldItem.getText (WIDTH_COL));
			}
			try {
				Integer.parseInt(this.heightText.getText ());
			} catch (final NumberFormatException e) {
				this.heightText.setText (oldItem.getText (HEIGHT_COL));
			}
			try {
				Integer.parseInt(this.hSpan.getText ());
			} catch (final NumberFormatException e) {
				this.hSpan.setText (oldItem.getText (HSPAN_COL));
			}
			try {
				Integer.parseInt(this.vSpan.getText ());
			} catch (final NumberFormatException e) {
				this.vSpan.setText (oldItem.getText (VSPAN_COL));
			}
			try {
				Integer.parseInt(this.hIndent.getText ());
			} catch (final NumberFormatException e) {
				this.hIndent.setText (oldItem.getText (HINDENT_COL));
			}
			try {
				Integer.parseInt(this.vIndent.getText ());
			} catch (final NumberFormatException e) {
				this.vIndent.setText (oldItem.getText (VINDENT_COL));
			}
			try {
				Integer.parseInt(this.minWidthText.getText ());
			} catch (final NumberFormatException e) {
				this.minWidthText.setText (oldItem.getText (MINWIDTH_COL));
			}
			try {
				Integer.parseInt(this.minHeightText.getText ());
			} catch (final NumberFormatException e) {
				this.minHeightText.setText (oldItem.getText (MINHEIGHT_COL));
			}
			final String [] insert = new String [] {
				this.nameText.getText (), this.combo.getText (), this.widthText.getText (), this.heightText.getText (),
				this.hAlign.getText (), this.vAlign.getText (), this.hGrab.getText (), this.vGrab.getText (),
				this.hSpan.getText (), this.vSpan.getText (), this.hIndent.getText (), this.vIndent.getText (),
				this.minWidthText.getText (), this.minHeightText.getText (), this.exclude.getText ()
			};
			this.data.set (row, insert);
			for (int i = 0; i < TOTAL_COLS; i++) {
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
	 * Return the initial weight of the layout and control groups within the SashForm.
	 * @return the desired sash weights for the tab page
	 */
	@Override
	int[] sashWeights () {
		return new int[] {35, 65};
	}

	/**
	 * Sets the layout data for the children of the layout.
	 */
	@Override
	void setLayoutData () {
		final Control [] children = this.layoutComposite.getChildren ();
		final TableItem [] items = this.table.getItems ();
		GridData data;
		int hSpan, vSpan, hIndent, vIndent;
		String vAlign, hAlign, vGrab, hGrab, exclude;
		for (int i = 0; i < children.length; i++) {
			data = new GridData ();
			/* Set widthHint and heightHint */
			data.widthHint = Integer.parseInt(items [i].getText (WIDTH_COL));
			data.heightHint = Integer.parseInt(items [i].getText (HEIGHT_COL));
			/* Set vertical alignment and horizontal alignment */
			hAlign = items [i].getText (HALIGN_COL);
			if (hAlign.equals ("CENTER")) {
				data.horizontalAlignment = SWT.CENTER;
			} else if (hAlign.equals ("END")) {
				data.horizontalAlignment = SWT.END;
			} else if (hAlign.equals ("FILL")) {
				data.horizontalAlignment = SWT.FILL;
			} else {
				data.horizontalAlignment = SWT.BEGINNING;
			}
			vAlign = items [i].getText (VALIGN_COL);
			if (vAlign.equals ("BEGINNING")) {
				data.verticalAlignment = SWT.BEGINNING;
			} else if (vAlign.equals ("END")) {
				data.verticalAlignment = SWT.END;
			} else if (vAlign.equals ("FILL")) {
				data.verticalAlignment = SWT.FILL;
			} else {
				data.verticalAlignment = SWT.CENTER;
			}
			/* Set spans and indents */
			hSpan = Integer.parseInt (items [i].getText (HSPAN_COL));
			data.horizontalSpan = hSpan;
			vSpan = Integer.parseInt(items [i].getText (VSPAN_COL));
			data.verticalSpan = vSpan;
			hIndent = Integer.parseInt(items [i].getText (HINDENT_COL));
			data.horizontalIndent = hIndent;
			vIndent = Integer.parseInt(items [i].getText (VINDENT_COL));
			data.verticalIndent = vIndent;
			/* Set grabs */
			hGrab = items [i].getText (HGRAB_COL);
			data.grabExcessHorizontalSpace = hGrab.equals ("true");
			vGrab = items [i].getText (VGRAB_COL);
			data.grabExcessVerticalSpace = vGrab.equals ("true");
			/* Set minimum width and height */
			data.minimumWidth = Integer.parseInt(items [i].getText (MINWIDTH_COL));
			data.minimumHeight = Integer.parseInt(items [i].getText (MINHEIGHT_COL));
			/* Set exclude boolean */
			exclude = items [i].getText (EXCLUDE_COL);
			data.exclude = exclude.equals ("true");

			children [i].setLayoutData (data);
		}
	}

	/**
	 * Sets the state of the layout.
	 */
	@Override
	void setLayoutState () {
		/* Set the columns for the layout */
		this.gridLayout.numColumns = this.numColumns.getSelection ();
		this.gridLayout.makeColumnsEqualWidth = this.makeColumnsEqualWidth.getSelection ();
		this.makeColumnsEqualWidth.setEnabled (this.numColumns.getSelection () > 1);

		/* Set the margins and spacing */
		this.gridLayout.marginWidth = this.marginWidth.getSelection ();
		this.gridLayout.marginHeight = this.marginHeight.getSelection ();
		this.gridLayout.marginLeft = this.marginLeft.getSelection ();
		this.gridLayout.marginRight = this.marginRight.getSelection ();
		this.gridLayout.marginTop = this.marginTop.getSelection ();
		this.gridLayout.marginBottom = this.marginBottom.getSelection ();
		this.gridLayout.horizontalSpacing = this.horizontalSpacing.getSelection ();
		this.gridLayout.verticalSpacing = this.verticalSpacing.getSelection ();
	}
}
