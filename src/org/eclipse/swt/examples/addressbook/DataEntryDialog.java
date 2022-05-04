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
package org.eclipse.swt.examples.addressbook;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

/* Imports */
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * DataEntryDialog class uses <code>org.eclipse.swt</code>
 * libraries to implement a dialog that accepts basic personal information that
 * is added to a <code>Table</code> widget or edits a <code>TableItem</code> entry
 * to represent the entered data.
 */
public class DataEntryDialog {

	private static ResourceBundle resAddressBook = ResourceBundle.getBundle("examples_addressbook");

	Shell shell;
	String[] values;
	String[] labels;

public DataEntryDialog(final Shell parent) {
	this.shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
	this.shell.setLayout(new GridLayout());
}

private void addTextListener(final Text text) {
	text.addModifyListener(e -> {
		final Integer index = (Integer)(text.getData("index"));
		this.values[index.intValue()] = text.getText();
	});
}
private void createControlButtons() {
	final Composite composite = new Composite(this.shell, SWT.NONE);
	composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
	final GridLayout layout = new GridLayout();
	layout.numColumns = 2;
	composite.setLayout(layout);

	final Button okButton = new Button(composite, SWT.PUSH);
	okButton.setText(resAddressBook.getString("OK"));
	okButton.addSelectionListener(widgetSelectedAdapter(e -> this.shell.close()));

	final Button cancelButton = new Button(composite, SWT.PUSH);
	cancelButton.setText(resAddressBook.getString("Cancel"));
	cancelButton.addSelectionListener(widgetSelectedAdapter(e -> {
		this.values = null;
		this.shell.close();
	}));

	this.shell.setDefaultButton(okButton);
}

private void createTextWidgets() {
	if (this.labels == null) {
    return;
  }

	final Composite composite = new Composite(this.shell, SWT.NONE);
	composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	final GridLayout layout= new GridLayout();
	layout.numColumns = 2;
	composite.setLayout(layout);

	if (this.values == null) {
    this.values = new String[this.labels.length];
  }

	for (int i = 0; i < this.labels.length; i++) {
		final Label label = new Label(composite, SWT.RIGHT);
		label.setText(this.labels[i]);
		final Text text = new Text(composite, SWT.BORDER);
		final GridData gridData = new GridData();
		gridData.widthHint = 400;
		text.setLayoutData(gridData);
		if (this.values[i] != null) {
			text.setText(this.values[i]);
		}
		text.setData("index", Integer.valueOf(i));
		this.addTextListener(text);
	}
}

public String[] getLabels() {
	return this.labels;
}
public String getTitle() {
	return this.shell.getText();
}
/**
 * Returns the contents of the <code>Text</code> widgets in the dialog in a
 * <code>String</code> array.
 *
 * @return	String[]
 *			The contents of the text widgets of the dialog.
 *			May return null if all text widgets are empty.
 */
public String[] getValues() {
	return this.values;
}
/**
 * Opens the dialog in the given state.  Sets <code>Text</code> widget contents
 * and dialog behaviour accordingly.
 *
 * @param 	dialogState	int
 *					The state the dialog should be opened in.
 */
public String[] open() {
	this.createTextWidgets();
	this.createControlButtons();
	this.shell.pack();
	this.shell.open();
	final Display display = this.shell.getDisplay();
	while(!this.shell.isDisposed()){
		if(!display.readAndDispatch()) {
      display.sleep();
    }
	}

	return this.getValues();
}
public void setLabels(final String[] labels) {
	this.labels = labels;
}
public void setTitle(final String title) {
	this.shell.setText(title);
}
/**
 * Sets the values of the <code>Text</code> widgets of the dialog to
 * the values supplied in the parameter array.
 *
 * @param	itemInfo	String[]
 * 						The values to which the dialog contents will be set.
 */
public void setValues(final String[] itemInfo) {
	if (this.labels == null) {
    return;
  }

	if (this.values == null) {
    this.values = new String[this.labels.length];
  }

	final int numItems = Math.min(this.values.length, itemInfo.length);
	System.arraycopy(itemInfo, 0, this.values, 0, numItems);
}
}
