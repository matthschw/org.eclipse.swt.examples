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
package org.eclipse.swt.examples.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StarPolyTab extends GraphicsTab {
	int[] radial;
	static final int POINTS  = 11;

	Combo fillRuleCb;

public StarPolyTab(final GraphicsExample example) {
	super(example);
	this.radial = new int[POINTS * 2];
}

@Override
public void createControlPanel(final Composite parent) {
	new Label(parent, SWT.NONE).setText(GraphicsExample.getResourceString("FillRule")); //$NON-NLS-1$
	this.fillRuleCb = new Combo(parent, SWT.DROP_DOWN);
	this.fillRuleCb.add("FILL_EVEN_ODD");
	this.fillRuleCb.add("FILL_WINDING");
	this.fillRuleCb.select(0);
	this.fillRuleCb.addListener(SWT.Selection, event -> this.example.redraw());
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Polygons"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("StarPolygon"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("StarPolygonDescription"); //$NON-NLS-1$
}

@Override
public void paint(final GC gc, final int width, final int height) {
	final int centerX = width / 2;
	final int centerY = height / 2;
	int pos = 0;
	for (int i = 0; i < POINTS; ++i) {
		final double r = (Math.PI*2 * pos)/POINTS;
		this.radial[i*2] = (int)((1+Math.cos(r))*centerX);
		this.radial[(i*2)+1] = (int)((1+Math.sin(r))*centerY);
		pos = (pos + (POINTS/2)) % POINTS;
	}
	gc.setFillRule(this.fillRuleCb.getSelectionIndex() != 0 ? SWT.FILL_WINDING : SWT.FILL_EVEN_ODD);
	gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_YELLOW));
	gc.fillPolygon(this.radial);
	gc.drawPolygon(this.radial);
}
}
