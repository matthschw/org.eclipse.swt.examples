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


import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * This example demonstrates the minimum amount of code required
 * to open an SWT Shell and process the events.
 */
public class HelloWorld1 {

public static void main (final String [] args) {
	final Display display = new Display ();
	final Shell shell = new HelloWorld1 ().open (display);
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) {
      display.sleep ();
    }
	}
	display.dispose ();
}

public Shell open (final Display display) {
	final Shell shell = new Shell (display);
	shell.open ();
	return shell;
}
}
