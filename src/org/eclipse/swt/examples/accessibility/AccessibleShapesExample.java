/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This example creates some Shapes in a Shell and uses a FillLayout to position them.
 * The Shape class is a simple example of the use of the SWT Accessibility API.
 * A Shape is accessible, but it does not have any accessible children.
 */
public class AccessibleShapesExample {
	static Display display;
	static Shell shell;

	public static void main(final String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final Shape redSquare = new Shape(shell, SWT.NONE);
		redSquare.setColor(SWT.COLOR_RED);
		redSquare.setShape(Shape.SQUARE);

		final Shape blueCircle = new Shape(shell, SWT.NONE);
		blueCircle.setColor(SWT.COLOR_BLUE);
		blueCircle.setShape(Shape.CIRCLE);

		final Shape greenSquare = new Shape(shell, SWT.NONE);
		greenSquare.setColor(SWT.COLOR_GREEN);
		greenSquare.setShape(Shape.SQUARE);

		shell.pack();
		shell.open();
		redSquare.setFocus();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
        display.sleep();
      }
		}
	}
}
