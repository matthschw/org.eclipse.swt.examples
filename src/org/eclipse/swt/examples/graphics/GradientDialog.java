/*******************************************************************************
 * Copyright (c) 2006, 2020 IBM Corporation and others.
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * This dialog is used for prompting the user to select two colors for the
 * creation of a gradient.
 */
public class GradientDialog extends Dialog {

	Canvas canvas;

	Button colorButton1, colorButton2;		// color buttons

	Button okButton, cancelButton;

	Menu menu1, menu2;

	RGB rgb1, rgb2;			// first and second color used in gradient
	int returnVal; 			// value to be returned by open(), set to SWT.OK
							// if the ok button has been pressed
	List<Image> resources;

	public GradientDialog(final Shell parent) {
		this (parent, SWT.PRIMARY_MODAL);
	}

	public GradientDialog(final Shell parent, final int style) {
		super(parent, style);
		this.rgb1 = this.rgb2 = null;
		this.returnVal = SWT.CANCEL;
		this.resources = new ArrayList<>();
	}

	/**
	 * Sets up the dialog and opens it.
	 * */
	public int open() {
		final Shell dialog = new Shell(this.getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | this.getStyle());
		dialog.setText(GraphicsExample.getResourceString("Gradient")); //$NON-NLS-1$

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		dialog.setLayout(gridLayout);

		// create the controls in the dialog
		this.createDialogControls(dialog);

		dialog.addListener(SWT.Close, event -> {
			for (final Image obj : this.resources) {
				if (obj != null) {
					obj.dispose();
				}
			}
			dialog.dispose();
		});

		dialog.setDefaultButton (this.okButton);
		dialog.pack ();
		final Rectangle rect = this.getParent().getMonitor().getBounds();
		final Rectangle bounds = dialog.getBounds();
		dialog.setLocation(rect.x + ((rect.width - bounds.width) / 2), rect.y + ((rect.height - bounds.height) / 2));
		dialog.setMinimumSize(bounds.width, bounds.height);

		dialog.open ();

		final Display display = this.getParent().getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
        display.sleep();
      }
		}

		if (this.menu1 != null) {
			this.menu1.dispose();
			this.menu1 = null;
		}

		if (this.menu2 != null) {
			this.menu2.dispose();
			this.menu2 = null;
		}

		return this.returnVal;
	}

	/**
	 * Creates the controls of the dialog.
	 * */
	public void createDialogControls(final Shell parent) {
		final Display display = parent.getDisplay();

		// message
		final Label message = new Label(parent, SWT.NONE);
		message.setText(GraphicsExample.getResourceString("GradientDlgMsg"));
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		message.setLayoutData(gridData);

		// default colors are white and black
		if ((this.rgb1 == null) || (this.rgb2 == null)) {
			this.rgb1 = display.getSystemColor(SWT.COLOR_WHITE).getRGB();
			this.rgb2 = display.getSystemColor(SWT.COLOR_BLACK).getRGB();
		}

		// canvas
		this.canvas = new Canvas(parent, SWT.NONE);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = 200;
		gridData.heightHint = 100;
		this.canvas.setLayoutData(gridData);
		this.canvas.addListener (SWT.Paint, e -> {
			Image preview = null;
			final Point size = this.canvas.getSize();
			final Color color1 = new Color(this.rgb1);
			final Color color2 = new Color(this.rgb2);
			preview = GraphicsExample.createImage(display, color1, color2, size.x, size.y);
			if (preview != null) {
				e.gc.drawImage (preview, 0, 0);
			}
			preview.dispose();
		});

		// composite used for both color buttons
		final Composite colorButtonComp = new Composite(parent, SWT.NONE);

		// layout buttons
		final RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.pack = false;
		colorButtonComp.setLayout(layout);

		// position composite
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		colorButtonComp.setLayoutData(gridData);

		final ColorMenu colorMenu = new ColorMenu();

		// color controls: first color
		this.colorButton1 = new Button(colorButtonComp, SWT.PUSH);
		this.colorButton1.setText(GraphicsExample.getResourceString("GradientDlgButton1"));
		final Color color1 = new Color(this.rgb1);
		final Image img1 = GraphicsExample.createImage(display, color1);
		this.colorButton1.setImage(img1);
		this.resources.add(img1);
		this.menu1 = colorMenu.createMenu(parent.getParent(), gb -> {
			this.rgb1 = gb.getBgColor1().getRGB();
			this.colorButton1.setImage(gb.getThumbNail());
			if (this.canvas != null) {
        this.canvas.redraw();
      }
		});
		this.colorButton1.addListener(SWT.Selection, event -> {
			final Button button = (Button) event.widget;
			final Composite parent1 = button.getParent();
			final Rectangle bounds = button.getBounds();
			final Point point = parent1.toDisplay(new Point(bounds.x, bounds.y));
			this.menu1.setLocation(point.x, point.y + bounds.height);
			this.menu1.setVisible(true);
		});

		// color controls: second color
		this.colorButton2 = new Button(colorButtonComp, SWT.PUSH);
		this.colorButton2.setText(GraphicsExample.getResourceString("GradientDlgButton2"));
		final Color color2 = new Color(this.rgb2);
		final Image img2 = GraphicsExample.createImage(display, color2);
		this.colorButton2.setImage(img2);
		this.resources.add(img2);
		this.menu2 = colorMenu.createMenu(parent.getParent(), gb -> {
			this.rgb2 = gb.getBgColor1().getRGB();
			this.colorButton2.setImage(gb.getThumbNail());
			if (this.canvas != null) {
        this.canvas.redraw();
      }
		});
		this.colorButton2.addListener(SWT.Selection, event -> {
			final Button button = (Button) event.widget;
			final Composite parent1 = button.getParent();
			final Rectangle bounds = button.getBounds();
			final Point point = parent1.toDisplay(new Point(bounds.x, bounds.y));
			this.menu2.setLocation(point.x, point.y + bounds.height);
			this.menu2.setVisible(true);
		});

		// composite used for ok and cancel buttons
		final Composite okCancelComp = new Composite(parent, SWT.NONE);

		// layout buttons
		final RowLayout rowLayout = new RowLayout();
		rowLayout.pack = false;
		rowLayout.marginTop = 5;
		okCancelComp.setLayout(rowLayout);

		// position composite
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.horizontalSpan = 2;
		okCancelComp.setLayoutData(gridData);

		// OK button
		this.okButton = new Button (okCancelComp, SWT.PUSH);
		this.okButton.setText("&OK");
		this.okButton.addListener(SWT.Selection, event -> {
			this.returnVal = SWT.OK;
			parent.close();
		});

		// cancel button
		this.cancelButton = new Button (okCancelComp, SWT.PUSH);
		this.cancelButton.setText("&Cancel");
		this.cancelButton.addListener(SWT.Selection, event -> parent.close());
	}

	/**
	 * Returns the first RGB selected by the user.
	 * */
	public RGB getFirstRGB() {
		return this.rgb1;
	}

	/**
	 * Sets the first RGB.
	 * @param rgb
	 */
	public void setFirstRGB(final RGB rgb) {
		this.rgb1 = rgb;
	}

	/**
	 * Returns the second RGB selected by the user.
	 * */
	public RGB getSecondRGB() {
		return this.rgb2;
	}

	/**
	 * Sets the second RGB.
	 * @param rgb
	 */
	public void setSecondRGB(final RGB rgb) {
		this.rgb2 = rgb;
	}
}
