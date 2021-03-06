/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
package org.eclipse.swt.examples.graphics;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

/**
 * This class is the one used for creating a graphic. Override the paint()
 * method to create the drawing. To create an animated graphic, use the
 * AnimatedGraphicsTab class.
 */
public abstract class GraphicsTab {

GraphicsExample example;

public GraphicsTab(final GraphicsExample example) {
	this.example = example;
}

/**
 * Creates the widgets used to control the drawing.
 */
public void createControlPanel(final Composite parent) {
}

/**
 * Disposes resources created by the receiver.
 */
public void dispose() {
}

/**
 * Answer the receiver's name.
 */
public abstract String getText();

/**
 * Answer the receiver's category.
 */
public String getCategory() {
	return GraphicsExample.getResourceString("Misc"); //$NON-NLS-1$
}

/**
 *  Answer the receiver's description.
 * */
public String getDescription() {
	return "";
}

/**
 *  Answer whether the receiver's drawing should be double bufferer.
 */
public boolean getDoubleBuffered() {
	return false;
}

/**
 * Paint the receiver into the specified GC.
 */
public void paint(final GC gc, final int width, final int height) {
}

}
