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
package org.eclipse.swt.examples.addressbook;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

/* Imports */
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * SearchDialog is a simple class that uses <code>org.eclipse.swt</code>
 * libraries to implement a basic search dialog.
 */
public class SearchDialog {

	private static ResourceBundle resAddressBook = ResourceBundle.getBundle("examples_addressbook");

	Shell shell;
	Text searchText;
	Combo searchArea;
	Label searchAreaLabel;
	Button matchCase;
	Button matchWord;
	Button findButton;
	Button down;
	FindListener findHandler;

/**
 * Class constructor that sets the parent shell and the table widget that
 * the dialog will search.
 *
 * @param parent	Shell
 *			The shell that is the parent of the dialog.
 */
public SearchDialog(final Shell parent) {
	this.shell = new Shell(parent, SWT.CLOSE | SWT.BORDER | SWT.TITLE);
	GridLayout layout = new GridLayout();
	layout.numColumns = 2;
	this.shell.setLayout(layout);
	this.shell.setText(resAddressBook.getString("Search_dialog_title"));
	this.shell.addShellListener(ShellListener.shellClosedAdapter(e -> {
		// don't dispose of the shell, just hide it for later use
		e.doit = false;
		this.shell.setVisible(false);
	}));

	final Label label = new Label(this.shell, SWT.LEFT);
	label.setText(resAddressBook.getString("Dialog_find_what"));
	this.searchText = new Text(this.shell, SWT.BORDER);
	GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	gridData.widthHint = 200;
	this.searchText.setLayoutData(gridData);
	this.searchText.addModifyListener(e -> {
		final boolean enableFind = (this.searchText.getCharCount() != 0);
		this.findButton.setEnabled(enableFind);
	});

	this.searchAreaLabel = new Label(this.shell, SWT.LEFT);
	this.searchArea = new Combo(this.shell, SWT.DROP_DOWN | SWT.READ_ONLY);
	gridData = new GridData(GridData.FILL_HORIZONTAL);
	gridData.widthHint = 200;
	this.searchArea.setLayoutData(gridData);

	this.matchCase = new Button(this.shell, SWT.CHECK);
	this.matchCase.setText(resAddressBook.getString("Dialog_match_case"));
	gridData = new GridData();
	gridData.horizontalSpan = 2;
	this.matchCase.setLayoutData(gridData);

	this.matchWord = new Button(this.shell, SWT.CHECK);
	this.matchWord.setText(resAddressBook.getString("Dialog_match_word"));
	gridData = new GridData();
	gridData.horizontalSpan = 2;
	this.matchWord.setLayoutData(gridData);

	final Group direction = new Group(this.shell, SWT.NONE);
	gridData = new GridData();
	gridData.horizontalSpan = 2;
	direction.setLayoutData(gridData);
	direction.setLayout (new FillLayout ());
	direction.setText(resAddressBook.getString("Dialog_direction"));

	final Button up = new Button(direction, SWT.RADIO);
	up.setText(resAddressBook.getString("Dialog_dir_up"));
	up.setSelection(false);

	this.down = new Button(direction, SWT.RADIO);
	this.down.setText(resAddressBook.getString("Dialog_dir_down"));
	this.down.setSelection(true);

	final Composite composite = new Composite(this.shell, SWT.NONE);
	gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gridData.horizontalSpan = 2;
	composite.setLayoutData(gridData);
	layout = new GridLayout();
	layout.numColumns = 2;
	layout.makeColumnsEqualWidth = true;
	composite.setLayout(layout);

	this.findButton = new Button(composite, SWT.PUSH);
	this.findButton.setText(resAddressBook.getString("Dialog_find"));
	this.findButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	this.findButton.setEnabled(false);
	this.findButton.addSelectionListener(widgetSelectedAdapter(e -> {
		if (!this.findHandler.find()) {
			final MessageBox box = new MessageBox(this.shell, SWT.ICON_INFORMATION | SWT.OK | SWT.PRIMARY_MODAL);
			box.setText(this.shell.getText());
			box.setMessage(resAddressBook.getString("Cannot_find") + "\"" + this.searchText.getText() + "\"");
			box.open();
		}
	}));

	final Button cancelButton = new Button(composite, SWT.PUSH);
	cancelButton.setText(resAddressBook.getString("Cancel"));
	cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
	cancelButton.addSelectionListener(widgetSelectedAdapter(e -> this.shell.setVisible(false)));
	this.shell.pack();
}
public String getSearchAreaLabel(final String label) {
	return this.searchAreaLabel.getText();
}

public String[] getsearchAreaNames() {
	return this.searchArea.getItems();
}
public boolean getMatchCase() {
	return this.matchCase.getSelection();
}
public boolean getMatchWord() {
	return this.matchWord.getSelection();
}
public String getSearchString() {
	return this.searchText.getText();
}
public boolean getSearchDown(){
	return this.down.getSelection();
}
public int getSelectedSearchArea() {
	return this.searchArea.getSelectionIndex();
}
public void open() {
	if (this.shell.isVisible()) {
		this.shell.setFocus();
	} else {
		this.shell.open();
	}
	this.searchText.setFocus();
}
public void setSearchAreaNames(final String[] names) {
	for (final String name : names) {
		this.searchArea.add(name);
	}
	this.searchArea.select(0);
}
public void setSearchAreaLabel(final String label) {
	this.searchAreaLabel.setText(label);
}
public void setMatchCase(final boolean match) {
	this.matchCase.setSelection(match);
}
public void setMatchWord(final boolean match) {
	this.matchWord.setSelection(match);
}
public void setSearchDown(final boolean searchDown){
	this.down.setSelection(searchDown);
}
public void setSearchString(final String searchString) {
	this.searchText.setText(searchString);
}

public void setSelectedSearchArea(final int index) {
	this.searchArea.select(index);
}
public void addFindListener(final FindListener listener) {
	this.findHandler = listener;
}
public void removeFindListener(final FindListener listener) {
	this.findHandler = null;
}
}
