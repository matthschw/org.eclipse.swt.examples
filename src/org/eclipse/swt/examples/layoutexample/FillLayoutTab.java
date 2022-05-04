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
package org.eclipse.swt.examples.layoutexample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class FillLayoutTab extends Tab {
	/* Controls for setting layout parameters */
	Button horizontal, vertical;
	Spinner marginWidth, marginHeight, spacing;
	/* The example layout instance */
	FillLayout fillLayout;
	/* TableEditors and related controls*/
	TableEditor comboEditor, nameEditor;
	CCombo combo;
	Text nameText;
	final int NAME_COL = 0;
	final int TOTAL_COLS = 2;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	FillLayoutTab(final LayoutExample instance) {
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
		this.comboEditor = new TableEditor (this.table);
		this.nameEditor = new TableEditor (this.table);
		this.table.addMouseListener(MouseListener.mouseDownAdapter(e -> {
			this.resetEditors();
			this.index = this.table.getSelectionIndex();
			if (this.index == -1) {
        return;
      }
			final TableItem oldItem = this.comboEditor.getItem();
			this.newItem = this.table.getItem(this.index);
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
		}));
	}

	/**
	 * Creates the control widgets.
	 */
	@Override
	void createControlWidgets () {
		/* Controls the type of FillLayout */
		final Group typeGroup = new Group (this.controlGroup, SWT.NONE);
		typeGroup.setText (LayoutExample.getResourceString ("Type"));
		typeGroup.setLayout (new GridLayout ());
		typeGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, false));
		this.horizontal = new Button (typeGroup, SWT.RADIO);
		this.horizontal.setText ("SWT.HORIZONTAL");
		this.horizontal.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.horizontal.setSelection(true);
		this.horizontal.addSelectionListener (this.selectionListener);
		this.vertical = new Button (typeGroup, SWT.RADIO);
		this.vertical.setText ("SWT.VERTICAL");
		this.vertical.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.vertical.addSelectionListener (this.selectionListener);

		/* Controls the margins and spacing of the FillLayout */
		final Group marginGroup = new Group(this.controlGroup, SWT.NONE);
		marginGroup.setText (LayoutExample.getResourceString("Margins_Spacing"));
		marginGroup.setLayout(new GridLayout(2, false));
		marginGroup.setLayoutData (new GridData(SWT.FILL, SWT.CENTER, false, false));
		new Label(marginGroup, SWT.NONE).setText("marginWidth");
		this.marginWidth = new Spinner(marginGroup, SWT.BORDER);
		this.marginWidth.setSelection(0);
		this.marginWidth.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginHeight");
		this.marginHeight = new Spinner(marginGroup, SWT.BORDER);
		this.marginHeight.setSelection(0);
		this.marginHeight.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		this.marginHeight.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText ("spacing");
		this.spacing = new Spinner(marginGroup, SWT.BORDER);
		this.spacing.setSelection(0);
		this.spacing.addSelectionListener(this.selectionListener);

		/* Add common controls */
		super.createControlWidgets ();
	}

	/**
	 * Creates the example layout.
	 */
	@Override
	void createLayout () {
		this.fillLayout = new FillLayout ();
		this.layoutComposite.setLayout (this.fillLayout);
	}

	/**
	 * Disposes the editors without placing their contents
	 * into the table.
	 */
	@Override
	void disposeEditors () {
		this.comboEditor.setEditor (null, null, -1);
		this.combo.dispose ();
		this.nameText.dispose();
	}


	/**
	 * Generates code for the example layout.
	 */
	@Override
	StringBuilder generateLayoutCode () {
		final StringBuilder code = new StringBuilder ();
		code.append ("\t\tFillLayout fillLayout = new FillLayout ();\n");
		if (this.fillLayout.type == SWT.VERTICAL) {
			code.append ("\t\tfillLayout.type = SWT.VERTICAL;\n");
		}
		if (this.fillLayout.marginWidth != 0) {
			code.append("\t\tfillLayout.marginWidth = " + this.fillLayout.marginWidth + ";\n");
		}
		if (this.fillLayout.marginHeight != 0) {
			code.append("\t\tfillLayout.marginHeight = " + this.fillLayout.marginHeight + ";\n");
		}
		if (this.fillLayout.spacing != 0) {
			code.append("\t\tfillLayout.spacing = " + this.fillLayout.spacing + ";\n");
		}
		code.append("\t\tshell.setLayout (fillLayout);\n");
		for(int i = 0; i < this.children.length; i++) {
			final Control control = this.children[i];
			code.append(this.getChildCode(control, i));
		}
		return code;
	}

	/**
	 * Returns the string to insert when a new child control is added to the table.
	 */
	@Override
	String[] getInsertString (final String name, final String controlType) {
		return new String [] {name, controlType};
	}

	/**
	 * Returns the layout data field names.
	 */
	@Override
	String [] getLayoutDataFieldNames() {
		return new String [] { "Control Name", "Control Type" };
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "FillLayout";
	}

	/**
	 * Takes information from TableEditors and stores it.
	 */
	@Override
	void resetEditors (final boolean tab) {
		final TableItem oldItem = this.comboEditor.getItem ();
		this.comboEditor.setEditor (null, null, -1);
		if (oldItem != null) {
			final int row = this.table.indexOf (oldItem);
			try {
				new String (this.nameText.getText ());
			} catch (final NumberFormatException e) {
				this.nameText.setText (oldItem.getText (this.NAME_COL));
			}
			final String [] insert = new String [] {this.nameText.getText (), this.combo.getText ()};
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
		this.layoutComposite.layout (true);
		this.layoutGroup.layout (true);
	}

	/**
	 * Sets the state of the layout.
	 */
	@Override
	void setLayoutState () {
		if (this.vertical.getSelection()) {
			this.fillLayout.type = SWT.VERTICAL;
		} else {
			this.fillLayout.type = SWT.HORIZONTAL;
		}

		/* Set the margins and spacing */
		this.fillLayout.marginWidth = this.marginWidth.getSelection();
		this.fillLayout.marginHeight = this.marginHeight.getSelection();
		this.fillLayout.spacing = this.spacing.getSelection();
	}
}
