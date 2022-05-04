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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class StackLayoutTab extends Tab {
	/* Controls for setting layout parameters */
	Button backButton, advanceButton;
	Label topControl;
	Spinner marginWidth, marginHeight;
	/* The example layout instance */
	StackLayout stackLayout;
	int currentLayer = -1;
	/* TableEditors and related controls*/
	TableEditor comboEditor, nameEditor;
	CCombo combo;
	Text nameText;
	final int NAME_COL = 0;
	final int TOTAL_COLS = 2;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	StackLayoutTab(final LayoutExample instance) {
		super(instance);
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	@Override
	void createChildWidgets() {
		/* Add common controls */
		super.createChildWidgets();

		/* Add TableEditors */
		this.comboEditor = new TableEditor(this.table);
		this.nameEditor = new TableEditor(this.table);
		this.table.addMouseListener(MouseListener.mouseDownAdapter(e -> {
			this.resetEditors();
			this.index = this.table.getSelectionIndex();
			if (this.index == -1) {
        return;
      }
			// set top layer of stack to the selected item
			this.setTopControl(this.index);

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
	void createControlWidgets() {
		/* Controls the topControl in the StackLayout */
		final Group columnGroup = new Group (this.controlGroup, SWT.NONE);
		columnGroup.setText ("topControl");//(LayoutExample.getResourceString ("Top_Control"));
		columnGroup.setLayout(new GridLayout(3, false));
		columnGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, false));
		this.backButton = new Button(columnGroup, SWT.PUSH);
		this.backButton.setText("<<");
		this.backButton.setEnabled(false);
		this.backButton.setLayoutData(new GridData (SWT.END, SWT.CENTER, false, false));
		this.backButton.addSelectionListener(SelectionListener.widgetSelectedAdapter( e ->this.setTopControl (this.currentLayer - 1)));
		this.topControl = new Label (columnGroup, SWT.BORDER);
		this.topControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.advanceButton = new Button(columnGroup, SWT.PUSH);
		this.advanceButton.setText(">>");
		this.advanceButton.setEnabled(false);
		this.advanceButton
				.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.setTopControl(this.currentLayer + 1)));

		/* Controls the margins of the StackLayout */
		final Group marginGroup = new Group(this.controlGroup, SWT.NONE);
		marginGroup.setText (LayoutExample.getResourceString("Margins"));
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

		/* Add common controls */
		super.createControlWidgets();
	}

	/**
	 * Creates the example layout.
	 */
	@Override
	void createLayout() {
		this.stackLayout = new StackLayout();
		this.layoutComposite.setLayout(this.stackLayout);
	}

	@Override
	void createLayoutComposite() {
		this.layoutComposite = new Composite(this.layoutGroup, SWT.BORDER);
		this.layoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		this.createLayout();
	}

	/**
	 * Disposes the editors without placing their contents
	 * into the table.
	 */
	@Override
	void disposeEditors() {
		this.comboEditor.setEditor(null, null, -1);
		this.combo.dispose();
		this.nameText.dispose();
	}

	/**
	 * Generates code for the example layout.
	 */
	@Override
	StringBuilder generateLayoutCode() {
		final StringBuilder code = new StringBuilder();
		code.append("\t\tStackLayout stackLayout = new StackLayout ();\n");
		if (this.stackLayout.marginWidth != 0) {
			code.append("\t\tstackLayout.marginWidth = " + this.stackLayout.marginWidth + ";\n");
		}
		if (this.stackLayout.marginHeight != 0) {
			code.append("\t\tstackLayout.marginHeight = " + this.stackLayout.marginHeight + ";\n");
		}
		code.append("\t\tshell.setLayout (stackLayout);\n");
		for(int i = 0; i < this.children.length; i++) {
			final Control control = this.children[i];
			code.append (this.getChildCode(control, i));
		}
		if ((this.children.length > 0) && (this.currentLayer != -1)) {
			code.append("\n\t\tstackLayout.topControl = " + this.names[this.currentLayer] + ";\n");
		}
		return code;
	}

	@Override
	boolean needsCustom() {
		return true;
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
	String[] getLayoutDataFieldNames() {
		return new String[] {"Control Name", "Control Type"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText() {
		return "StackLayout";
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
		this.setTopControl (this.currentLayer);
		this.layoutGroup.layout (true);
	}

	void setTopControl (final int index) {
		if ((index == -1) || (this.children.length == 0)) {
			this.currentLayer = -1;
			this.topControl.setText ("");
		} else {
			this.currentLayer = index;
			this.stackLayout.topControl = this.children [this.currentLayer];
			this.layoutComposite.layout ();
			final TableItem item = this.table.getItem(this.currentLayer);
			this.topControl.setText (item.getText(0));
		}
		this.backButton.setEnabled((this.children.length > 1) && (this.currentLayer > 0));
		this.advanceButton.setEnabled((this.children.length > 1) && (this.currentLayer < (this.children.length - 1)));
	}

	/**
	 * Sets the state of the layout.
	 */
	@Override
	void setLayoutState() {
		/* Set the margins and spacing */
		this.stackLayout.marginWidth = this.marginWidth.getSelection();
		this.stackLayout.marginHeight = this.marginHeight.getSelection();
	}

}

