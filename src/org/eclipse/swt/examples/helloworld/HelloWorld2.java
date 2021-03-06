/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
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
package org.eclipse.swt.examples.helloworld;


import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/*
 * This example builds on HelloWorld1 and demonstrates the minimum amount
 * of code required to open an SWT Shell with a Label and process the events.
 */
public class HelloWorld2 {
	private static ResourceBundle resHello = ResourceBundle.getBundle("examples_helloworld");

public static void main (final String [] args) {
	final Display display = new Display ();
	final Shell shell = new HelloWorld2 ().open (display);
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) {
      display.sleep ();
    }
	}
	display.dispose ();
}

public Shell open (final Display display) {
	final Shell shell = new Shell (display);
	final Label label = new Label (shell, SWT.CENTER);
	label.setText (resHello.getString("Hello_world"));
	label.setBounds (shell.getClientArea ());
	shell.open ();
	return shell;
}
}
