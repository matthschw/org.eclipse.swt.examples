/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;

/**
 * This tab demonstrates various text fonts. It allows the user to specify font
 * parameters such as face, style and size.
 */
public class CustomFontTab extends GraphicsTab {

	String text = GraphicsExample.getResourceString("SWT");
	GraphicsBackground fontForeground;
	Combo fontFaceCb, fontStyleCb;
	Spinner fontPointSpinner;
	Button colorButton;
	List<String> fontNames;
	int [] styleValues;
	String [] fontStyles;
	Menu menu;

public CustomFontTab(final GraphicsExample example) {
	super(example);

	// create list of fonts for this platform
	final FontData [] fontData = Display.getCurrent().getFontList(null, true);
	this.fontNames = new ArrayList<>();
	for (final FontData element : fontData) {
		// remove duplicates and sort
		final String nextName = element.getName();
		if (!this.fontNames.contains(nextName)) {
			int j = 0;
			while((j < this.fontNames.size()) && (nextName.compareTo(this.fontNames.get(j)) > 0)) {
				j++;
			}
			this.fontNames.add(j, nextName);
		}
	}
	this.fontStyles = new String [] {
			GraphicsExample.getResourceString("Regular"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Italic"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Bold"), //$NON-NLS-1$
			GraphicsExample.getResourceString("BoldItalic") //$NON-NLS-1$
	};
	this.styleValues = new int [] {SWT.NORMAL, SWT.ITALIC, SWT.BOLD, SWT.BOLD | SWT.ITALIC};
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Font"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("CustomFont"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("CustomFontDescription"); //$NON-NLS-1$
}

@Override
public void dispose() {
	if (this.menu != null) {
		this.menu.dispose();
		this.menu = null;
	}
}

@Override
public void createControlPanel(final Composite parent) {

	final Composite mainComp = new Composite(parent, SWT.NONE);
	mainComp.setLayout(new RowLayout());

	// create combo for font face
	Composite comp = new Composite(mainComp, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	new Label(comp, SWT.LEFT).setText(GraphicsExample.getResourceString("FontFace")); //$NON-NLS-1$
	this.fontFaceCb = new Combo(comp, SWT.DROP_DOWN);
	for (final String name : this.fontNames) {
		this.fontFaceCb.add(name);
	}
	this.fontFaceCb.select(0);
	this.fontFaceCb.addListener(SWT.Selection, event -> this.example.redraw());

	// create combo for font style
	comp = new Composite(mainComp, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	new Label(comp, SWT.LEFT).setText(GraphicsExample.getResourceString("FontStyle")); //$NON-NLS-1$
	this.fontStyleCb = new Combo(comp, SWT.DROP_DOWN);
	for (final String fontStyle : this.fontStyles) {
		this.fontStyleCb.add(fontStyle);
	}
	this.fontStyleCb.select(0);
	this.fontStyleCb.addListener(SWT.Selection, event -> this.example.redraw());

	// create spinner for font size (points)
	comp = new Composite(mainComp, SWT.NONE);
	comp.setLayout(new GridLayout(2, false));

	new Label(comp, SWT.LEFT).setText(GraphicsExample.getResourceString("FontSize")); //$NON-NLS-1$
	this.fontPointSpinner = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.fontPointSpinner.setMinimum(1);
	this.fontPointSpinner.setMaximum(1000);
	this.fontPointSpinner.setSelection(200);
	this.fontPointSpinner.addListener(SWT.Selection, event -> this.example.redraw());

	final ColorMenu cm = new ColorMenu();
	cm.setColorItems(true);
	cm.setPatternItems(this.example.checkAdvancedGraphics());
	this.menu = cm.createMenu(parent.getParent(), gb -> {
		this.fontForeground = gb;
		this.colorButton.setImage(gb.getThumbNail());
		this.example.redraw();
	});

	// initialize the background to the 2nd item in the menu (black)
	this.fontForeground = (GraphicsBackground)this.menu.getItem(1).getData();

	// create color button
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());

	this.colorButton = new Button(comp, SWT.PUSH);
	this.colorButton.setText(GraphicsExample.getResourceString("Color")); //$NON-NLS-1$
	this.colorButton.setImage(this.fontForeground.getThumbNail());
	this.colorButton.addListener(SWT.Selection, event -> {
		final Button button = (Button) event.widget;
		final Composite parent1 = button.getParent();
		final Rectangle bounds = button.getBounds();
		final Point point = parent1.toDisplay(new Point(bounds.x, bounds.y));
		this.menu.setLocation(point.x, point.y + bounds.height);
		this.menu.setVisible(true);
	});
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	final String fontFace = this.fontNames.get(this.fontFaceCb.getSelectionIndex());
	final int points = this.fontPointSpinner.getSelection();
	final int style = this.styleValues[this.fontStyleCb.getSelectionIndex()];

	final Font font = new Font(device, fontFace, points, style);
	gc.setFont(font);
	gc.setTextAntialias(SWT.ON);

	final Point size = gc.stringExtent(this.text);
	final int textWidth = size.x;
	final int textHeight = size.y;

	Pattern pattern = null;
	if (this.fontForeground.getBgColor1() != null) {
		gc.setForeground(this.fontForeground.getBgColor1());
	} else if (this.fontForeground.getBgImage() != null) {
		pattern = new Pattern(device, this.fontForeground.getBgImage());
		gc.setForegroundPattern(pattern);
	}

	gc.drawString(this.text, (width-textWidth)/2, (height-textHeight)/2, true);

	font.dispose();
	if (pattern != null) {
    pattern.dispose();
  }
}

}

