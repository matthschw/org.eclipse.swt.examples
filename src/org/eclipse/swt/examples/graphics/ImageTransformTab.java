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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * This tab demonstrates transformations, such as scaling, rotation, and
 * invert.  It allows the user to specify values for scaling and rotation.
 */
public class ImageTransformTab extends GraphicsTab {

	private Spinner rotateSpinner, translateSpinnerX, translateSpinnerY, scaleSpinnerX, scaleSpinnerY;
	private Button invertButton;

/**
 * Constructor
 * @param example A GraphicsExample
 */
public ImageTransformTab(final GraphicsExample example) {
	super(example);
}

@Override
public String getCategory() {
	return GraphicsExample.getResourceString("Transform"); //$NON-NLS-1$
}

@Override
public String getText() {
	return GraphicsExample.getResourceString("Image"); //$NON-NLS-1$
}

@Override
public String getDescription() {
	return GraphicsExample.getResourceString("TransformImgDescription"); //$NON-NLS-1$
}

/**
 * This method creates the controls specific to the tab. The call to the
 * createControlPanel method in the super class create the controls that are
 * defined in the super class.
 *
 * @param parent The parent composite
 */
@Override
public void createControlPanel(final Composite parent) {

	Composite comp;
	final GridLayout gridLayout = new GridLayout(2, false);

	// create spinner for the rotation angle
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(gridLayout);

	new Label(comp, SWT.CENTER).setText(GraphicsExample.getResourceString("Rotate")); //$NON-NLS-1$
	this.rotateSpinner = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	final GC gc = new GC(this.rotateSpinner);
	final int width = (int) (gc.getFontMetrics().getAverageCharacterWidth() * 5);
	gc.dispose();
	this.rotateSpinner.setLayoutData(new GridData(width, SWT.DEFAULT));
	this.rotateSpinner.setSelection(0);
	this.rotateSpinner.setMinimum(-720);
	this.rotateSpinner.setMaximum(720);
	this.rotateSpinner.setIncrement(30);
	this.rotateSpinner.addListener(SWT.Selection, event -> this.example.redraw());

	// create a spinner for translating along the x axis
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(gridLayout);

	new Label(comp, SWT.CENTER).setText(GraphicsExample.getResourceString("xtranslate")); //$NON-NLS-1$
	this.translateSpinnerX = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.translateSpinnerX.setLayoutData(new GridData(width, SWT.DEFAULT));
	this.translateSpinnerX.setMinimum(-100);
	this.translateSpinnerX.setMaximum(500);
	this.translateSpinnerX.setSelection(0);
	this.translateSpinnerX.setIncrement(10);
	this.translateSpinnerX.addListener(SWT.Selection, event -> this.example.redraw());

	// create a spinner for translating along the y axis
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(gridLayout);

	new Label(comp, SWT.CENTER).setText(GraphicsExample.getResourceString("ytranslate")); //$NON-NLS-1$
	this.translateSpinnerY = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.translateSpinnerY.setLayoutData(new GridData(width, SWT.DEFAULT));
	this.translateSpinnerY.setMinimum(-100);
	this.translateSpinnerY.setMaximum(500);
	this.translateSpinnerY.setSelection(0);
	this.translateSpinnerY.setIncrement(10);
	this.translateSpinnerY.addListener(SWT.Selection, event -> this.example.redraw());

	// create a spinner for scaling along the x axis
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(gridLayout);

	new Label(comp, SWT.CENTER).setText(GraphicsExample.getResourceString("xscale")); //$NON-NLS-1$
	this.scaleSpinnerX = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.scaleSpinnerX.setLayoutData(new GridData(width, SWT.DEFAULT));
	this.scaleSpinnerX.setDigits(2);
	this.scaleSpinnerX.setMinimum(1);
	this.scaleSpinnerX.setMaximum(400);
	this.scaleSpinnerX.setSelection(100);
	this.scaleSpinnerX.setIncrement(10);
	this.scaleSpinnerX.addListener(SWT.Selection, event -> this.example.redraw());

	// create a spinner for scaling along the y axis
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(gridLayout);

	new Label(comp, SWT.CENTER).setText(GraphicsExample.getResourceString("yscale")); //$NON-NLS-1$
	this.scaleSpinnerY = new Spinner(comp, SWT.BORDER | SWT.WRAP);
	this.scaleSpinnerY.setLayoutData(new GridData(width, SWT.DEFAULT));
	this.scaleSpinnerY.setDigits(2);
	this.scaleSpinnerY.setMinimum(1);
	this.scaleSpinnerY.setMaximum(400);
	this.scaleSpinnerY.setSelection(100);
	this.scaleSpinnerY.setIncrement(10);
	this.scaleSpinnerY.addListener(SWT.Selection, event -> this.example.redraw());

	// create a button for inverting the transform matrix
	comp = new Composite(parent, SWT.NONE);
	comp.setLayout(new GridLayout());
	this.invertButton = new Button(comp, SWT.TOGGLE);
	this.invertButton.setText(GraphicsExample.getResourceString("Invert")); //$NON-NLS-1$
	this.invertButton.addListener(SWT.Selection, event -> this.example.redraw());
}

@Override
public void paint(final GC gc, final int width, final int height) {
	if (!this.example.checkAdvancedGraphics()) {
    return;
  }
	final Device device = gc.getDevice();

	final Image image = GraphicsExample.loadImage(device, GraphicsExample.class, "ace_club.jpg");

	final Transform transform = new Transform(device);

	// scale image
	transform.scale(this.scaleSpinnerX.getSelection()/100f, this.scaleSpinnerY.getSelection()/100f);

	// translate image
	transform.translate(this.translateSpinnerX.getSelection(), this.translateSpinnerY.getSelection());

	// rotate on center of image
	final Rectangle rect = image.getBounds();
	transform.translate(rect.width/2, rect.height/2);
	transform.rotate(this.rotateSpinner.getSelection());
	transform.translate(-rect.width/2, -rect.height/2);

	if(this.invertButton.getSelection()){
		transform.invert();
	}

	gc.setTransform(transform);
	gc.drawImage(image, 0, 0);

	transform.dispose();
	image.dispose();
}

}
