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
package org.eclipse.swt.examples.javaviewer;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class JavaViewer {
	Shell shell;
	StyledText text;
	JavaLineStyler lineStyler = new JavaLineStyler();
	FileDialog fileDialog;
	static ResourceBundle resources = ResourceBundle.getBundle("examples_javaviewer");

Menu createFileMenu() {
	final Menu bar = this.shell.getMenuBar ();
	final Menu menu = new Menu (bar);
	MenuItem item;

	// Open
	item = new MenuItem (menu, SWT.PUSH);
	item.setText (resources.getString("Open_menuitem"));
	item.setAccelerator(SWT.MOD1 + 'O');
	item.addSelectionListener(widgetSelectedAdapter(event -> this.openFile()));

	// Exit
	item = new MenuItem (menu, SWT.PUSH);
	item.setText (resources.getString("Exit_menuitem"));
	item.addSelectionListener (widgetSelectedAdapter(e -> this.menuFileExit ()));
	return menu;
}

void createMenuBar () {
	final Menu bar = new Menu (this.shell, SWT.BAR);
	this.shell.setMenuBar (bar);

	final MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
	fileItem.setText (resources.getString("File_menuitem"));
	fileItem.setMenu (this.createFileMenu ());

}

void createShell (final Display display) {
	this.shell = new Shell (display);
	this.shell.setText (resources.getString("Window_title"));
	final GridLayout layout = new GridLayout();
	layout.numColumns = 1;
	this.shell.setLayout(layout);
	this.shell.addShellListener(ShellListener.shellClosedAdapter(e -> {
		this.text.removeLineStyleListener(this.lineStyler);
	}));
}
void createStyledText() {
	this.text = new StyledText (this.shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	final GridData spec = new GridData();
	spec.horizontalAlignment = GridData.FILL;
	spec.grabExcessHorizontalSpace = true;
	spec.verticalAlignment = GridData.FILL;
	spec.grabExcessVerticalSpace = true;
	this.text.setLayoutData(spec);
	this.text.addLineStyleListener(this.lineStyler);
	this.text.setEditable(false);
	final Color bg = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	this.text.setBackground(bg);
}

void displayError(final String msg) {
	final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR);
	box.setMessage(msg);
	box.open();
}

public static void main (final String [] args) {
	final Display display = new Display();
	final JavaViewer example = new JavaViewer ();
	final Shell shell = example.open (display);
	while (!shell.isDisposed ()) {
    if (!display.readAndDispatch ()) {
      display.sleep ();
    }
  }
	display.dispose ();
}

public Shell open (final Display display) {
	this.createShell (display);
	this.createMenuBar ();
	this.createStyledText ();
	this.shell.setSize(500, 400);
	this.shell.open ();
	return this.shell;
}

void openFile() {
	if (this.fileDialog == null) {
		this.fileDialog = new FileDialog(this.shell, SWT.OPEN);
	}

	this.fileDialog.setFilterExtensions(new String[] {"*.java", "*.*"});
	final String name = this.fileDialog.open();

	this.open(name);
}

void open(final String name) {
	final String textString;

	if ((name == null) || (name.length() == 0)) {
    return;
  }

	final File file = new File(name);
	if (!file.exists()) {
		final String message = MessageFormat.format(resources.getString("Err_file_no_exist"), file.getName());
		this.displayError(message);
		return;
	}

	try {
		final FileInputStream stream= new FileInputStream(file.getPath());
		try (Reader in = new BufferedReader(new InputStreamReader(stream))) {

			final char[] readBuffer= new char[2048];
			final StringBuilder buffer= new StringBuilder((int) file.length());
			int n;
			while ((n = in.read(readBuffer)) > 0) {
				buffer.append(readBuffer, 0, n);
			}
			textString = buffer.toString();
			stream.close();
		} catch (final IOException e) {
			// Err_file_io
			final String message = MessageFormat.format(resources.getString("Err_file_io"), file.getName());
			this.displayError(message);
			return;
		}
	}
	catch (final FileNotFoundException e) {
		final String message = MessageFormat.format(resources.getString("Err_not_found"), file.getName());
		this.displayError(message);
		return;
	}
	// Guard against superfluous mouse move events -- defer action until later
	final Display display = this.text.getDisplay();
	display.asyncExec(() -> this.text.setText(textString));

	// parse the block comments up front since block comments can go across
	// lines - inefficient way of doing this
	this.lineStyler.parseBlockComments(textString);
}

void menuFileExit () {
	this.shell.close ();
}
}
