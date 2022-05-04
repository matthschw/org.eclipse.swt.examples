/*******************************************************************************
 * Copyright (c) 2000, 2021 IBM Corporation and others.
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
package org.eclipse.swt.examples.imageanalyzer;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.ImageLoaderEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ImageAnalyzer {
	static ResourceBundle bundle = ResourceBundle.getBundle("examples_images");
	Display display;
	Shell shell;
	Canvas imageCanvas, paletteCanvas;
	Label typeLabel, sizeLabel, depthLabel, transparentPixelLabel,
		timeToLoadLabel, screenSizeLabel, backgroundPixelLabel,
		locationLabel, disposalMethodLabel, delayTimeLabel,
		repeatCountLabel, compressionRatioLabel, paletteLabel, dataLabel, statusLabel;
	Combo backgroundCombo, imageTypeCombo, compressionCombo, scaleXCombo, scaleYCombo, alphaCombo;
	Button incrementalCheck, transparentCheck, maskCheck, backgroundCheck;
	Button previousButton, nextButton, animateButton;
	StyledText dataText;
	Sash sash;
	Color whiteColor, blackColor, redColor, greenColor, blueColor, canvasBackground;
	Font fixedWidthFont;
	Cursor crossCursor;
	GC imageCanvasGC;
	PrinterData printerData;

	int paletteWidth = 140; // recalculated and used as a width hint
	int ix = 0, iy = 0, py = 0; // used to scroll the image and palette
	int compression; // used to modify the compression ratio of the image
	float xscale = 1, yscale = 1; // used to scale the image
	int alpha = 255; // used to modify the alpha value of the image
	boolean incremental = false; // used to incrementally display an image
	boolean transparent = true; // used to display an image with transparency
	boolean showMask = false; // used to display an icon mask or transparent image mask
	boolean showBackground = false; // used to display the background of an animated image
	boolean animate = false; // used to animate a multi-image file
	Thread animateThread; // draws animated images
	Thread incrementalThread; // draws incremental images
	String lastPath; // used to seed the file dialog
	String currentName; // the current image file or URL name
	String fileName; // the current image file
	ImageLoader loader; // the loader for the current image file
	ImageData[] imageDataArray; // all image data read from the current file
	int imageDataIndex; // the index of the current image data
	ImageData imageData; // the currently-displayed image data
	Image image; // the currently-displayed image
	List<ImageLoaderEvent> incrementalEvents; // incremental image events
	long loadTime = 0; // the time it took to load the current image

	static final int INDEX_DIGITS = 4;
	static final int ALPHA_CHARS = 5;
	static final int ALPHA_CONSTANT = 0;
	static final int ALPHA_X = 1;
	static final int ALPHA_Y = 2;
	static final String[] OPEN_FILTER_EXTENSIONS = new String[] {
			"*.bmp;*.gif;*.ico;*.jfif;*.jpeg;*.jpg;*.png;*.tif;*.tiff",
			"*.bmp", "*.gif", "*.ico", "*.jpg;*.jpeg;*.jfif", "*.png", "*.tif;*.tiff" };
	static final String[] OPEN_FILTER_NAMES = new String[] {
			bundle.getString("All_images") + " (bmp, gif, ico, jfif, jpeg, jpg, png, tif, tiff)",
			"BMP (*.bmp)", "GIF (*.gif)", "ICO (*.ico)", "JPEG (*.jpg, *.jpeg, *.jfif)",
			"PNG (*.png)", "TIFF (*.tif, *.tiff)" };
	static final String[] SAVE_FILTER_EXTENSIONS = new String[] {
			"*.bmp", "*.bmp", "*.gif", "*.ico", "*.jpg", "*.png", "*.tif", "*.bmp" };
	static final String[] SAVE_FILTER_NAMES = new String[] {
			"Uncompressed BMP (*.bmp)", "RLE Compressed BMP (*.bmp)", "GIF (*.gif)",
			"ICO (*.ico)", "JPEG (*.jpg)", "PNG (*.png)",
			"TIFF (*.tif)", "OS/2 BMP (*.bmp)" };
	class TextPrompter extends Dialog {
		String message = "";
		String result = null;
		Shell dialog;
		Text text;
		public TextPrompter (final Shell parent, final int style) {
			super (parent, style);
		}
		public TextPrompter (final Shell parent) {
			this (parent, SWT.APPLICATION_MODAL);
		}
		public String getMessage () {
			return this.message;
		}
		public void setMessage (final String string) {
			this.message = string;
		}
		public String open () {
			this.dialog = new Shell(this.getParent(), this.getStyle());
			this.dialog.setText(this.getText());
			this.dialog.setLayout(new GridLayout());
			final Label label = new Label(this.dialog, SWT.NONE);
			label.setText(this.message);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.text = new Text(this.dialog, SWT.SINGLE | SWT.BORDER);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.widthHint = 300;
			this.text.setLayoutData(data);
			final Composite buttons = new Composite(this.dialog, SWT.NONE);
			final GridLayout grid = new GridLayout();
			grid.numColumns = 2;
			buttons.setLayout(grid);
			buttons.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			final Button ok = new Button(buttons, SWT.PUSH);
			ok.setText(bundle.getString("OK"));
			data = new GridData();
			data.widthHint = 75;
			ok.setLayoutData(data);
			ok.addSelectionListener(widgetSelectedAdapter(e -> {
				this.result = this.text.getText();
				this.dialog.dispose();
			}));
			final Button cancel = new Button(buttons, SWT.PUSH);
			cancel.setText(bundle.getString("Cancel"));
			data = new GridData();
			data.widthHint = 75;
			cancel.setLayoutData(data);
			cancel.addSelectionListener(widgetSelectedAdapter(e -> this.dialog.dispose()));
			this.dialog.setDefaultButton(ok);
			this.dialog.pack();
			this.dialog.open();
			while (!this.dialog.isDisposed()) {
				if (!ImageAnalyzer.this.display.readAndDispatch()) {
          ImageAnalyzer.this.display.sleep();
        }
			}
			return this.result;
		}
	}

	public static void main(final String [] args) {
		final Display display = new Display();
		final ImageAnalyzer imageAnalyzer = new ImageAnalyzer();
		final Shell shell = imageAnalyzer.open(display);

		while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
		display.dispose();
	}

	public Shell open(final Display dpy) {
		// Create a window and set its title.
		this.display = dpy;
		this.shell = new Shell(this.display);
		this.shell.setText(bundle.getString("Image_analyzer"));

		// Hook resize and dispose listeners.
		this.shell.addControlListener(ControlListener.controlResizedAdapter(this::resizeShell));
		this.shell.addShellListener(ShellListener.shellClosedAdapter(e -> {
			this.animate = false; // stop any animation in progress
			if (this.animateThread != null) {
				// wait for the thread to die before disposing the shell.
				while (this.animateThread.isAlive()) {
					if (!this.display.readAndDispatch()) {
            this.display.sleep();
          }
				}
			}
			e.doit = true;
		}));
		this.shell.addDisposeListener(e -> {
			// Clean up.
			if (this.image != null) {
        this.image.dispose();
      }
			this.fixedWidthFont.dispose();
		});

		// Create colors and fonts.
		this.whiteColor = new Color(255, 255, 255);
		this.blackColor = new Color(0, 0, 0);
		this.redColor = new Color(255, 0, 0);
		this.greenColor = new Color(0, 255, 0);
		this.blueColor = new Color(0, 0, 255);
		this.fixedWidthFont = new Font(this.display, "courier", 10, 0);
		this.crossCursor = this.display.getSystemCursor(SWT.CURSOR_CROSS);

		// Add a menu bar and widgets.
		this.createMenuBar();
		this.createWidgets();
		this.shell.pack();

		// Create a GC for drawing, and hook the listener to dispose it.
		this.imageCanvasGC = new GC(this.imageCanvas);
		this.imageCanvas.addDisposeListener(e -> this.imageCanvasGC.dispose());

		// Open the window
		this.shell.open();
		return this.shell;
	}

	void createWidgets() {
		// Add the widgets to the shell in a grid layout.
		final GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.numColumns = 2;
		this.shell.setLayout(layout);

		// Add a composite to contain some control widgets across the top.
		final Composite controls = new Composite(this.shell, SWT.NONE);
		final RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 8;
		controls.setLayout(rowLayout);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		controls.setLayoutData(gridData);

		// Combo to change the background.
		Group group = new Group(controls, SWT.NONE);
		group.setLayout(new RowLayout());
		group.setText(bundle.getString("Background"));
		this.backgroundCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.backgroundCombo.setItems(bundle.getString("None"),
			bundle.getString("White"),
			bundle.getString("Black"),
			bundle.getString("Red"),
			bundle.getString("Green"),
			bundle.getString("Blue"));
		this.backgroundCombo.select(this.backgroundCombo.indexOf(bundle.getString("White")));
		this.backgroundCombo.addSelectionListener(widgetSelectedAdapter(event -> this.changeBackground()));

		// Combo to change the compression ratio.
		group = new Group(controls, SWT.NONE);
		group.setLayout(new GridLayout(3, true));
		group.setText(bundle.getString("Save_group"));
		this.imageTypeCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		final String[] types = {"JPEG", "PNG", "GIF", "ICO", "TIFF", "BMP"};
		for (final String type : types) {
			this.imageTypeCombo.add(type);
		}
		this.imageTypeCombo.select(this.imageTypeCombo.indexOf("JPEG"));
		this.imageTypeCombo.addSelectionListener(widgetSelectedAdapter(event -> {
			final int index = this.imageTypeCombo.getSelectionIndex();
			switch(index) {
			case 0:
				this.compressionCombo.setEnabled(true);
				this.compressionRatioLabel.setEnabled(true);
				if (this.compressionCombo.getItemCount() == 100) {
          break;
        }
				this.compressionCombo.removeAll();
				for (int i = 0; i < 100; i++) {
					this.compressionCombo.add(String.valueOf(i + 1));
				}
				this.compressionCombo.select(this.compressionCombo.indexOf("75"));
				break;
			case 1:
				this.compressionCombo.setEnabled(true);
				this.compressionRatioLabel.setEnabled(true);
				if (this.compressionCombo.getItemCount() == 10) {
          break;
        }
				this.compressionCombo.removeAll();
				for (int i = 0; i < 4; i++) {
					this.compressionCombo.add(String.valueOf(i));
				}
				this.compressionCombo.select(0);
				break;
			case 2:
			case 3:
			case 4:
			case 5:
				this.compressionCombo.setEnabled(false);
				this.compressionRatioLabel.setEnabled(false);
				break;
			}
		}));
		this.imageTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.compressionRatioLabel = new Label(group, SWT.NONE);
		this.compressionRatioLabel.setText(bundle.getString("Compression"));
		this.compressionRatioLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		this.compressionCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i < 100; i++) {
			this.compressionCombo.add(String.valueOf(i + 1));
		}
		this.compressionCombo.select(this.compressionCombo.indexOf("75"));
		this.compressionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// Combo to change the x scale.
		final String[] values = {
			"0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1",
			"1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2",
			"3", "4", "5", "6", "7", "8", "9", "10",};
		group = new Group(controls, SWT.NONE);
		group.setLayout(new RowLayout());
		group.setText(bundle.getString("X_scale"));
		this.scaleXCombo = new Combo(group, SWT.DROP_DOWN);
		for (final String value : values) {
			this.scaleXCombo.add(value);
		}
		this.scaleXCombo.select(this.scaleXCombo.indexOf("1"));
		this.scaleXCombo.addSelectionListener(widgetSelectedAdapter(event -> this.scaleX()));

		// Combo to change the y scale.
		group = new Group(controls, SWT.NONE);
		group.setLayout(new RowLayout());
		group.setText(bundle.getString("Y_scale"));
		this.scaleYCombo = new Combo(group, SWT.DROP_DOWN);
		for (final String value : values) {
			this.scaleYCombo.add(value);
		}
		this.scaleYCombo.select(this.scaleYCombo.indexOf("1"));
		this.scaleYCombo.addSelectionListener(widgetSelectedAdapter(event -> this.scaleY()));

		// Combo to change the alpha value.
		group = new Group(controls, SWT.NONE);
		group.setLayout(new RowLayout());
		group.setText(bundle.getString("Alpha_K"));
		this.alphaCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i <= 255; i += 5) {
			this.alphaCombo.add(String.valueOf(i));
		}
		this.alphaCombo.select(this.alphaCombo.indexOf("255"));
		this.alphaCombo.addSelectionListener(widgetSelectedAdapter(event -> this.alpha()));

		// Check box to request incremental display.
		group = new Group(controls, SWT.NONE);
		group.setLayout(new RowLayout());
		group.setText(bundle.getString("Display"));
		this.incrementalCheck = new Button(group, SWT.CHECK);
		this.incrementalCheck.setText(bundle.getString("Incremental"));
		this.incrementalCheck.setSelection(this.incremental);
		this.incrementalCheck.addSelectionListener(widgetSelectedAdapter(event -> this.incremental = ((Button)event.widget).getSelection()));

		// Check box to request transparent display.
		this.transparentCheck = new Button(group, SWT.CHECK);
		this.transparentCheck.setText(bundle.getString("Transparent"));
		this.transparentCheck.setSelection(this.transparent);
		this.transparentCheck.addSelectionListener(widgetSelectedAdapter(event -> {
			this.transparent = ((Button)event.widget).getSelection();
			if (this.image != null) {
				this.imageCanvas.redraw();
			}
		}));

		// Check box to request mask display.
		this.maskCheck = new Button(group, SWT.CHECK);
		this.maskCheck.setText(bundle.getString("Mask"));
		this.maskCheck.setSelection(this.showMask);
		this.maskCheck.addSelectionListener(widgetSelectedAdapter(event -> {
			this.showMask = ((Button)event.widget).getSelection();
			if (this.image != null) {
				this.imageCanvas.redraw();
			}
		}));

		// Check box to request background display.
		this.backgroundCheck = new Button(group, SWT.CHECK);
		this.backgroundCheck.setText(bundle.getString("Background"));
		this.backgroundCheck.setSelection(this.showBackground);
		this.backgroundCheck.addSelectionListener(widgetSelectedAdapter(event -> this.showBackground = ((Button)event.widget).getSelection()));

		// Group the animation buttons.
		group = new Group(controls, SWT.NONE);
		group.setLayout(new RowLayout());
		group.setText(bundle.getString("Animation"));

		// Push button to display the previous image in a multi-image file.
		this.previousButton = new Button(group, SWT.PUSH);
		this.previousButton.setText(bundle.getString("Previous"));
		this.previousButton.setEnabled(false);
		this.previousButton.addSelectionListener(widgetSelectedAdapter(event -> this.previous()));

		// Push button to display the next image in a multi-image file.
		this.nextButton = new Button(group, SWT.PUSH);
		this.nextButton.setText(bundle.getString("Next"));
		this.nextButton.setEnabled(false);
		this.nextButton.addSelectionListener(widgetSelectedAdapter(event -> this.next()));

		// Push button to toggle animation of a multi-image file.
		this.animateButton = new Button(group, SWT.PUSH);
		this.animateButton.setText(bundle.getString("Animate"));
		this.animateButton.setEnabled(false);
		this.animateButton.addSelectionListener(widgetSelectedAdapter(event -> this.animate()));

		// Label to show the image file type.
		this.typeLabel = new Label(this.shell, SWT.NONE);
		this.typeLabel.setText(bundle.getString("Type_initial"));
		this.typeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Canvas to show the image.
		this.imageCanvas = new Canvas(this.shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
		this.imageCanvas.setBackground(this.whiteColor);
		this.imageCanvas.setCursor(this.crossCursor);
		gridData = new GridData();
		gridData.verticalSpan = 15;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.imageCanvas.setLayoutData(gridData);
		this.imageCanvas.addPaintListener(event -> {
			if (this.image == null) {
				final Rectangle bounds = this.imageCanvas.getBounds();
				event.gc.fillRectangle(0, 0, bounds.width, bounds.height);
			} else {
				this.paintImage(event);
			}
		});
		this.imageCanvas.addMouseMoveListener(event -> {
			if (this.image != null) {
				this.showColorAt(event.x, event.y);
			}
		});

		// Set up the image canvas scroll bars.
		final ScrollBar horizontal = this.imageCanvas.getHorizontalBar();
		horizontal.setVisible(true);
		horizontal.setMinimum(0);
		horizontal.setEnabled(false);
		horizontal.addSelectionListener(widgetSelectedAdapter(event -> this.scrollHorizontally((ScrollBar)event.widget)));
		ScrollBar vertical = this.imageCanvas.getVerticalBar();
		vertical.setVisible(true);
		vertical.setMinimum(0);
		vertical.setEnabled(false);
		vertical.addSelectionListener(widgetSelectedAdapter(event -> this.scrollVertically((ScrollBar)event.widget)));

		// Label to show the image size.
		this.sizeLabel = new Label(this.shell, SWT.NONE);
		this.sizeLabel.setText(bundle.getString("Size_initial"));
		this.sizeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the image depth.
		this.depthLabel = new Label(this.shell, SWT.NONE);
		this.depthLabel.setText(bundle.getString("Depth_initial"));
		this.depthLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the transparent pixel.
		this.transparentPixelLabel = new Label(this.shell, SWT.NONE);
		this.transparentPixelLabel.setText(bundle.getString("Transparent_pixel_initial"));
		this.transparentPixelLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the time to load.
		this.timeToLoadLabel = new Label(this.shell, SWT.NONE);
		this.timeToLoadLabel.setText(bundle.getString("Time_to_load_initial"));
		this.timeToLoadLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Separate the animation fields from the rest of the fields.
		Label separator = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the logical screen size for animation.
		this.screenSizeLabel = new Label(this.shell, SWT.NONE);
		this.screenSizeLabel.setText(bundle.getString("Animation_size_initial"));
		this.screenSizeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the background pixel.
		this.backgroundPixelLabel = new Label(this.shell, SWT.NONE);
		this.backgroundPixelLabel.setText(bundle.getString("Background_pixel_initial"));
		this.backgroundPixelLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the image location (x, y).
		this.locationLabel = new Label(this.shell, SWT.NONE);
		this.locationLabel.setText(bundle.getString("Image_location_initial"));
		this.locationLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the image disposal method.
		this.disposalMethodLabel = new Label(this.shell, SWT.NONE);
		this.disposalMethodLabel.setText(bundle.getString("Disposal_initial"));
		this.disposalMethodLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the image delay time.
		this.delayTimeLabel = new Label(this.shell, SWT.NONE);
		this.delayTimeLabel.setText(bundle.getString("Delay_initial"));
		this.delayTimeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show the background pixel.
		this.repeatCountLabel = new Label(this.shell, SWT.NONE);
		this.repeatCountLabel.setText(bundle.getString("Repeats_initial"));
		this.repeatCountLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Separate the animation fields from the palette.
		separator = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Label to show if the image has a direct or indexed palette.
		this.paletteLabel = new Label(this.shell, SWT.NONE);
		this.paletteLabel.setText(bundle.getString("Palette_initial"));
		this.paletteLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// Canvas to show the image's palette.
		this.paletteCanvas = new Canvas(this.shell, SWT.BORDER | SWT.V_SCROLL | SWT.NO_REDRAW_RESIZE);
		this.paletteCanvas.setFont(this.fixedWidthFont);
		this.paletteCanvas.getVerticalBar().setVisible(true);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		final GC gc = new GC(this.paletteLabel);
		this.paletteWidth = gc.stringExtent(bundle.getString("Max_length_string")).x;
		gc.dispose();
		gridData.widthHint = this.paletteWidth;
		gridData.heightHint = 16 * 11; // show at least 16 colors
		this.paletteCanvas.setLayoutData(gridData);
		this.paletteCanvas.addPaintListener(event -> {
			if (this.image != null) {
        this.paintPalette(event);
      }
		});

		// Set up the palette canvas scroll bar.
		vertical = this.paletteCanvas.getVerticalBar();
		vertical.setVisible(true);
		vertical.setMinimum(0);
		vertical.setIncrement(10);
		vertical.setEnabled(false);
		vertical.addSelectionListener(widgetSelectedAdapter(event -> this.scrollPalette((ScrollBar)event.widget)));

		// Sash to see more of image or image data.
		this.sash = new Sash(this.shell, SWT.HORIZONTAL);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		this.sash.setLayoutData(gridData);
		this.sash.addSelectionListener (widgetSelectedAdapter(event -> {
			if (event.detail != SWT.DRAG) {
				((GridData)this.paletteCanvas.getLayoutData()).heightHint = SWT.DEFAULT;
				final Rectangle paletteCanvasBounds = this.paletteCanvas.getBounds();
				final int minY = paletteCanvasBounds.y + 20;
				final Rectangle dataLabelBounds = this.dataLabel.getBounds();
				final int maxY = this.statusLabel.getBounds().y - dataLabelBounds.height - 20;
				if ((event.y > minY) && (event.y < maxY)) {
					final Rectangle oldSash = this.sash.getBounds();
					this.sash.setBounds(event.x, event.y, event.width, event.height);
					final int diff = event.y - oldSash.y;
					Rectangle bounds = this.imageCanvas.getBounds();
					this.imageCanvas.setBounds(bounds.x, bounds.y, bounds.width, bounds.height + diff);
					bounds = paletteCanvasBounds;
					this.paletteCanvas.setBounds(bounds.x, bounds.y, bounds.width, bounds.height + diff);
					bounds = dataLabelBounds;
					this.dataLabel.setBounds(bounds.x, bounds.y + diff, bounds.width, bounds.height);
					bounds = this.dataText.getBounds();
					this.dataText.setBounds(bounds.x, bounds.y + diff, bounds.width, bounds.height - diff);
					//shell.layout(true);
				}
			}
		}));

		// Label to show data-specific fields.
		this.dataLabel = new Label(this.shell, SWT.NONE);
		this.dataLabel.setText(bundle.getString("Pixel_data_initial"));
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		this.dataLabel.setLayoutData(gridData);

		// Text to show a dump of the data.
		this.dataText = new StyledText(this.shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		this.dataText.setBackground(this.display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		this.dataText.setFont(this.fixedWidthFont);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.heightHint = 128;
		gridData.grabExcessVerticalSpace = true;
		this.dataText.setLayoutData(gridData);
		this.dataText.addMouseListener(MouseListener.mouseDownAdapter(event -> {
			if ((this.image != null) && (event.button == 1)) {
				this.showColorForData();
			}
		}));
		this.dataText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent event) {
				if (ImageAnalyzer.this.image != null) {
					ImageAnalyzer.this.showColorForData();
				}
			}
		});

		// Label to show status and cursor location in image.
		this.statusLabel = new Label(this.shell, SWT.NONE);
		this.statusLabel.setText("");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		this.statusLabel.setLayoutData(gridData);
	}

	Menu createMenuBar() {
		// Menu bar.
		final Menu menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(menuBar);
		this.createFileMenu(menuBar);
		this.createAlphaMenu(menuBar);
		return menuBar;
	}

	void createFileMenu(final Menu menuBar) {
		// File menu
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText(bundle.getString("File"));
		final Menu fileMenu = new Menu(this.shell, SWT.DROP_DOWN);
		item.setMenu(fileMenu);

		// File -> Open File...
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("OpenFile"));
		item.setAccelerator(SWT.MOD1 + 'O');
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuOpenFile()));

		// File -> Open URL...
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("OpenURL"));
		item.setAccelerator(SWT.MOD1 + 'U');
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuOpenURL()));

		// File -> Reopen
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("Reopen"));
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuReopen()));

		new MenuItem(fileMenu, SWT.SEPARATOR);

		// File -> Load File... (natively)
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("LoadFile"));
		item.setAccelerator(SWT.MOD1 + 'L');
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuLoad()));

		new MenuItem(fileMenu, SWT.SEPARATOR);

		// File -> Save
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("Save"));
		item.setAccelerator(SWT.MOD1 + 'S');
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuSave()));

		// File -> Save As...
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("Save_as"));
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuSaveAs()));

		// File -> Save Mask As...
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("Save_mask_as"));
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuSaveMaskAs()));

		new MenuItem(fileMenu, SWT.SEPARATOR);

		// File -> Print
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("Print"));
		item.setAccelerator(SWT.MOD1 + 'P');
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuPrint()));

		new MenuItem(fileMenu, SWT.SEPARATOR);

		// File -> Exit
		item = new MenuItem(fileMenu, SWT.PUSH);
		item.setText(bundle.getString("Exit"));
		item.addSelectionListener(widgetSelectedAdapter(event -> this.shell.close()));

	}

	void createAlphaMenu(final Menu menuBar) {
		// Alpha menu
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText(bundle.getString("Alpha"));
		final Menu alphaMenu = new Menu(this.shell, SWT.DROP_DOWN);
		item.setMenu(alphaMenu);

		// Alpha -> K
		item = new MenuItem(alphaMenu, SWT.PUSH);
		item.setText("K");
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuComposeAlpha(ALPHA_CONSTANT)));

		// Alpha -> (K + x) % 256
		item = new MenuItem(alphaMenu, SWT.PUSH);
		item.setText("(K + x) % 256");
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuComposeAlpha(ALPHA_X)));

		// Alpha -> (K + y) % 256
		item = new MenuItem(alphaMenu, SWT.PUSH);
		item.setText("(K + y) % 256");
		item.addSelectionListener(widgetSelectedAdapter(event -> this.menuComposeAlpha(ALPHA_Y)));
	}

	void menuComposeAlpha(final int alpha_op) {
		if (this.image == null) {
      return;
    }
		this.animate = false; // stop any animation in progress
		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		try {
			if (alpha_op == ALPHA_CONSTANT) {
				this.imageData.alpha = this.alpha;
			} else {
				this.imageData.alpha = -1;
				switch (alpha_op) {
					case ALPHA_X:
						for (int y = 0; y < this.imageData.height; y++) {
						for (int x = 0; x < this.imageData.width; x++) {
							this.imageData.setAlpha(x, y, (x + this.alpha) % 256);
						}
						}
						break;
					case ALPHA_Y:
						for (int y = 0; y < this.imageData.height; y++) {
						for (int x = 0; x < this.imageData.width; x++) {
							this.imageData.setAlpha(x, y, (y + this.alpha) % 256);
						}
						}
						break;
					default: break;
				}
			}
			this.displayImage(this.imageData);
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	/* Just use Image(device, filename) to load an image file. */
	void menuLoad() {
		this.animate = false; // stop any animation in progress

		// Get the user to choose an image file.
		final FileDialog fileChooser = new FileDialog(this.shell, SWT.OPEN);
		if (this.lastPath != null) {
      fileChooser.setFilterPath(this.lastPath);
    }
		fileChooser.setFilterExtensions(OPEN_FILTER_EXTENSIONS);
		fileChooser.setFilterNames(OPEN_FILTER_NAMES);
		final String filename = fileChooser.open();
		this.lastPath = fileChooser.getFilterPath();
		if (filename == null) {
      return;
    }

		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		try {
			// Read the new image from the chosen file.
			final long startTime = System.currentTimeMillis();
			final Image newImage = new Image(this.display, filename);
			this.loadTime = System.currentTimeMillis() - startTime; // don't include getImageData in load time
			this.imageData = newImage.getImageData();

			// Cache the filename.
			this.currentName = filename;
			this.fileName = filename;

			// Fill in array and loader data.
			this.loader = new ImageLoader();
			this.imageDataArray = new ImageData[] {this.imageData};
			this.loader.data = this.imageDataArray;

			// Display the image.
			this.imageDataIndex = 0;
			this.displayImage(this.imageData);
		} catch (SWTException | SWTError | OutOfMemoryError e) {
			this.showErrorDialog(bundle.getString("Loading_lc"), filename, e);
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	void menuOpenFile() {
		this.animate = false; // stop any animation in progress

		// Get the user to choose an image file.
		final FileDialog fileChooser = new FileDialog(this.shell, SWT.OPEN);
		if (this.lastPath != null) {
      fileChooser.setFilterPath(this.lastPath);
    }
		fileChooser.setFilterExtensions(OPEN_FILTER_EXTENSIONS);
		fileChooser.setFilterNames(OPEN_FILTER_NAMES);
		final String filename = fileChooser.open();
		this.lastPath = fileChooser.getFilterPath();
		if (filename == null) {
      return;
    }
		this.showFileType(filename);
		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		final ImageLoader oldLoader = this.loader;
		try {
			this.loader = new ImageLoader();
			if (this.incremental) {
				// Prepare to handle incremental events.
				this.loader.addImageLoaderListener(this::incrementalDataLoaded);
				this.incrementalThreadStart();
			}
			// Read the new image(s) from the chosen file.
			final long startTime = System.currentTimeMillis();
			this.imageDataArray = this.loader.load(filename);
			this.loadTime = System.currentTimeMillis() - startTime;
			if (this.imageDataArray.length > 0) {
				// Cache the filename.
				this.currentName = filename;
				this.fileName = filename;

				// If there are multiple images in the file (typically GIF)
				// then enable the Previous, Next and Animate buttons.
				this.previousButton.setEnabled(this.imageDataArray.length > 1);
				this.nextButton.setEnabled(this.imageDataArray.length > 1);
				this.animateButton.setEnabled((this.imageDataArray.length > 1) && (this.loader.logicalScreenWidth > 0) && (this.loader.logicalScreenHeight > 0));

				// Display the first image in the file.
				this.imageDataIndex = 0;
				this.displayImage(this.imageDataArray[this.imageDataIndex]);
			}
		} catch (SWTException | SWTError | OutOfMemoryError e) {
			this.showErrorDialog(bundle.getString("Loading_lc"), filename, e);
			this.loader = oldLoader;
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	void menuOpenURL() {
		this.animate = false; // stop any animation in progress

		// Get the user to choose an image URL.
		final TextPrompter textPrompter = new TextPrompter(this.shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		textPrompter.setText(bundle.getString("OpenURLDialog"));
		textPrompter.setMessage(bundle.getString("EnterURL"));
		final String urlname = textPrompter.open();
		if (urlname == null) {
      return;
    }

		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		final ImageLoader oldLoader = this.loader;
		try {
			final URL url = new URL(urlname);
			try (InputStream stream = url.openStream()) {
				this.loader = new ImageLoader();
				if (this.incremental) {
					// Prepare to handle incremental events.
					this.loader.addImageLoaderListener(this::incrementalDataLoaded);
					this.incrementalThreadStart();
				}
				// Read the new image(s) from the chosen URL.
				final long startTime = System.currentTimeMillis();
				this.imageDataArray = this.loader.load(stream);
				this.loadTime = System.currentTimeMillis() - startTime;
			}
			if (this.imageDataArray.length > 0) {
				this.currentName = urlname;
				this.fileName = null;

				// If there are multiple images (typically GIF)
				// then enable the Previous, Next and Animate buttons.
				this.previousButton.setEnabled(this.imageDataArray.length > 1);
				this.nextButton.setEnabled(this.imageDataArray.length > 1);
				this.animateButton.setEnabled((this.imageDataArray.length > 1) && (this.loader.logicalScreenWidth > 0) && (this.loader.logicalScreenHeight > 0));

				// Display the first image.
				this.imageDataIndex = 0;
				this.displayImage(this.imageDataArray[this.imageDataIndex]);
			}
		} catch (Exception | OutOfMemoryError e) {
			this.showErrorDialog(bundle.getString("Loading_lc"), urlname, e);
			this.loader = oldLoader;
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	/*
	 * Called to start a thread that draws incremental images
	 * as they are loaded.
	 */
	void incrementalThreadStart() {
		this.incrementalEvents = new ArrayList<>();
		this.incrementalThread = new Thread("Incremental") {
			@Override
			public void run() {
				// Draw the first ImageData increment.
				while (ImageAnalyzer.this.incrementalEvents != null) {
					// Synchronize so we don't try to remove when the vector is null.
					synchronized (ImageAnalyzer.this) {
						if (ImageAnalyzer.this.incrementalEvents != null) {
							if (!ImageAnalyzer.this.incrementalEvents.isEmpty()) {
								final ImageLoaderEvent event = ImageAnalyzer.this.incrementalEvents.remove(0);
								if (ImageAnalyzer.this.image != null) {
                  ImageAnalyzer.this.image.dispose();
                }
								ImageAnalyzer.this.image = new Image(ImageAnalyzer.this.display, event.imageData);
								ImageAnalyzer.this.imageData = event.imageData;
								ImageAnalyzer.this.imageCanvasGC.drawImage(
									ImageAnalyzer.this.image,
									0,
									0,
									ImageAnalyzer.this.imageData.width,
									ImageAnalyzer.this.imageData.height,
									ImageAnalyzer.this.imageData.x,
									ImageAnalyzer.this.imageData.y,
									ImageAnalyzer.this.imageData.width,
									ImageAnalyzer.this.imageData.height);
							} else {
								yield();
							}
						}
					}
				}
				ImageAnalyzer.this.display.wake();
			}
		};
		this.incrementalThread.setDaemon(true);
		this.incrementalThread.start();
	}

	/*
	 * Called when incremental image data has been loaded,
	 * for example, for interlaced GIF/PNG or progressive JPEG.
	 */
	void incrementalDataLoaded(final ImageLoaderEvent event) {
		// Synchronize so that we do not try to add while
		// the incremental drawing thread is removing.
		synchronized (this) {
			this.incrementalEvents.add(event);
		}
	}

	void menuSave() {
		if (this.image == null) {
      return;
    }
		this.animate = false; // stop any animation in progress

		// If the image file type is unknown, we can't 'Save',
		// so we have to use 'Save As...'.
		if ((this.imageData.type == SWT.IMAGE_UNDEFINED) || (this.fileName == null)) {
			this.menuSaveAs();
			return;
		}

		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		try {
			// Save the current image to the current file.
			this.loader.data = new ImageData[] {this.imageData};
			if (this.imageData.type == SWT.IMAGE_JPEG) {
        this.loader.compression = this.compressionCombo.indexOf(this.compressionCombo.getText()) + 1;
      }
			if (this.imageData.type == SWT.IMAGE_PNG) {
        this.loader.compression = this.compressionCombo.indexOf(this.compressionCombo.getText());
      }
			this.loader.save(this.fileName, this.imageData.type);
		} catch (SWTException | SWTError e) {
			this.showErrorDialog(bundle.getString("Saving_lc"), this.fileName, e);
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	void menuSaveAs() {
		if (this.image == null) {
      return;
    }
		this.animate = false; // stop any animation in progress

		// Get the user to choose a file name and type to save.
		final FileDialog fileChooser = new FileDialog(this.shell, SWT.SAVE);
		fileChooser.setFilterPath(this.lastPath);
		if (this.fileName != null) {
			String name = this.fileName;
			final int nameStart = name.lastIndexOf(java.io.File.separatorChar);
			if (nameStart > -1) {
				name = name.substring(nameStart + 1);
			}
			fileChooser.setFileName(name.substring(0, name.indexOf(".")) + "." + this.imageTypeCombo.getText().toLowerCase());
		}
		fileChooser.setFilterExtensions(SAVE_FILTER_EXTENSIONS);
		fileChooser.setFilterNames(SAVE_FILTER_NAMES);
		switch (this.imageTypeCombo.getSelectionIndex()) {
		case 0:
			fileChooser.setFilterIndex(4);
			break;
		case 1:
			fileChooser.setFilterIndex(5);
			break;
		case 2:
			fileChooser.setFilterIndex(2);
			break;
		case 3:
			fileChooser.setFilterIndex(3);
			break;
		case 4:
			fileChooser.setFilterIndex(6);
			break;
		case 5:
			fileChooser.setFilterIndex(0);
			break;
		}
		final String filename = fileChooser.open();
		this.lastPath = fileChooser.getFilterPath();
		if (filename == null) {
      return;
    }

		// Figure out what file type the user wants saved.
		int filetype = fileChooser.getFilterIndex();
		if (filetype == -1) {
			/* The platform file dialog does not support user-selectable file filters.
			 * Determine the desired type by looking at the file extension.
			 */
			filetype = this.determineFileType(filename);
			if (filetype == SWT.IMAGE_UNDEFINED) {
				final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR);
				box.setMessage(createMsg(bundle.getString("Unknown_extension"),
					filename.substring(filename.lastIndexOf('.') + 1)));
				box.open();
				return;
			}
		}

		if (new java.io.File(filename).exists()) {
			final MessageBox box = new MessageBox(this.shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
			box.setMessage(createMsg(bundle.getString("Overwrite"), filename));
			if (box.open() == SWT.CANCEL) {
        return;
      }
		}

		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		try {
			// Save the current image to the specified file.
			boolean multi = false;
			if (this.loader.data.length > 1) {
				final MessageBox box = new MessageBox(this.shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				box.setMessage(createMsg(bundle.getString("Save_all"), Integer.valueOf(this.loader.data.length)));
				final int result = box.open();
				if (result == SWT.CANCEL) {
          return;
        }
				if (result == SWT.YES) {
          multi = true;
        }
			}
			/* If the image has transparency but the user has transparency turned off,
			 * turn it off in the saved image. */
			final int transparentPixel = this.imageData.transparentPixel;
			if (!multi && (transparentPixel != -1) && !this.transparent) {
				this.imageData.transparentPixel = -1;
			}

			if (!multi) {
        this.loader.data = new ImageData[] {this.imageData};
      }
			this.loader.compression = this.compressionCombo.indexOf(this.compressionCombo.getText());
			this.loader.save(filename, filetype);

			/* Restore the previous transparency setting. */
			if (!multi && (transparentPixel != -1) && !this.transparent) {
				this.imageData.transparentPixel = transparentPixel;
			}

			// Update the shell title and file type label,
			// and use the new file.
			this.fileName = filename;
			this.shell.setText(createMsg(bundle.getString("Analyzer_on"), filename));
			this.typeLabel.setText(createMsg(bundle.getString("Type_string"), this.fileTypeString(filetype)));

		} catch (SWTException | SWTError e) {
			this.showErrorDialog(bundle.getString("Saving_lc"), filename, e);
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	void menuSaveMaskAs() {
		if ((this.image == null) || !this.showMask || (this.imageData.getTransparencyType() == SWT.TRANSPARENCY_NONE)) {
      return;
    }
		this.animate = false; // stop any animation in progress

		// Get the user to choose a file name and type to save.
		final FileDialog fileChooser = new FileDialog(this.shell, SWT.SAVE);
		fileChooser.setFilterPath(this.lastPath);
		if (this.fileName != null) {
      fileChooser.setFileName(this.fileName);
    }
		fileChooser.setFilterExtensions(SAVE_FILTER_EXTENSIONS);
		fileChooser.setFilterNames(SAVE_FILTER_NAMES);
		final String filename = fileChooser.open();
		this.lastPath = fileChooser.getFilterPath();
		if (filename == null) {
      return;
    }

		// Figure out what file type the user wants saved.
		int filetype = fileChooser.getFilterIndex();
		if (filetype == -1) {
			/* The platform file dialog does not support user-selectable file filters.
			 * Determine the desired type by looking at the file extension.
			 */
			filetype = this.determineFileType(filename);
			if (filetype == SWT.IMAGE_UNDEFINED) {
				final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR);
				box.setMessage(createMsg(bundle.getString("Unknown_extension"),
					filename.substring(filename.lastIndexOf('.') + 1)));
				box.open();
				return;
			}
		}

		if (new java.io.File(filename).exists()) {
			final MessageBox box = new MessageBox(this.shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
			box.setMessage(createMsg(bundle.getString("Overwrite"), filename));
			if (box.open() == SWT.CANCEL) {
        return;
      }
		}

		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		try {
			// Save the mask of the current image to the specified file.
			final ImageData maskImageData = this.imageData.getTransparencyMask();
			this.loader.data = new ImageData[] {maskImageData};
			this.loader.save(filename, filetype);

		} catch (SWTException | SWTError e) {
			this.showErrorDialog(bundle.getString("Saving_lc"), filename, e);
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	void menuPrint() {
		if (this.image == null) {
      return;
    }

		try {
			// Ask the user to specify the printer.
			final PrintDialog dialog = new PrintDialog(this.shell, SWT.NONE);
			if (this.printerData != null) {
        dialog.setPrinterData(this.printerData);
      }
			this.printerData = dialog.open();
			if (this.printerData == null) {
        return;
      }

			final Printer printer = new Printer(this.printerData);
			final Point screenDPI = this.display.getDPI();
			final Point printerDPI = printer.getDPI();
			final int scaleFactor = printerDPI.x / screenDPI.x;
			final Rectangle trim = printer.computeTrim(0, 0, 0, 0);
			if (printer.startJob(this.currentName)) {
				if (printer.startPage()) {
					final GC gc = new GC(printer);
					final int transparentPixel = this.imageData.transparentPixel;
					if ((transparentPixel != -1) && !this.transparent) {
						this.imageData.transparentPixel = -1;
					}
					final Image printerImage = new Image(printer, this.imageData);
					gc.drawImage(
						printerImage,
						0,
						0,
						this.imageData.width,
						this.imageData.height,
						-trim.x,
						-trim.y,
						scaleFactor * this.imageData.width,
						scaleFactor * this.imageData.height);
					if ((transparentPixel != -1) && !this.transparent) {
						this.imageData.transparentPixel = transparentPixel;
					}
					printerImage.dispose();
					gc.dispose();
					printer.endPage();
				}
				printer.endJob();
			}
			printer.dispose();
		} catch (final SWTError e) {
			final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR);
			box.setMessage(bundle.getString("Printing_error") + e.getMessage());
			box.open();
		}
	}

	void menuReopen() {
		if (this.currentName == null) {
      return;
    }
		this.animate = false; // stop any animation in progress
		final Cursor waitCursor = this.display.getSystemCursor(SWT.CURSOR_WAIT);
		this.shell.setCursor(waitCursor);
		this.imageCanvas.setCursor(waitCursor);
		try {
			this.loader = new ImageLoader();
			ImageData[] newImageData;
			if (this.fileName == null) {
				final URL url = new URL(this.currentName);
				try (InputStream stream = url.openStream()) {
					final long startTime = System.currentTimeMillis();
					newImageData = this.loader.load(stream);
					this.loadTime = System.currentTimeMillis() - startTime;
				}
			} else {
				final long startTime = System.currentTimeMillis();
				newImageData = this.loader.load(this.fileName);
				this.loadTime = System.currentTimeMillis() - startTime;
			}
			this.imageDataIndex = 0;
			this.displayImage(newImageData[this.imageDataIndex]);

		} catch (Exception | OutOfMemoryError e) {
			this.showErrorDialog(bundle.getString("Reloading_lc"), this.currentName, e);
		} finally {
			this.shell.setCursor(null);
			this.imageCanvas.setCursor(this.crossCursor);
		}
	}

	void changeBackground() {
		final String background = this.backgroundCombo.getText();
		if (background.equals(bundle.getString("White"))) {
			this.imageCanvas.setBackground(this.whiteColor);
		} else if (background.equals(bundle.getString("Black"))) {
			this.imageCanvas.setBackground(this.blackColor);
		} else if (background.equals(bundle.getString("Red"))) {
			this.imageCanvas.setBackground(this.redColor);
		} else if (background.equals(bundle.getString("Green"))) {
			this.imageCanvas.setBackground(this.greenColor);
		} else if (background.equals(bundle.getString("Blue"))) {
			this.imageCanvas.setBackground(this.blueColor);
		} else {
			this.imageCanvas.setBackground(null);
		}
	}
	/*
	 * Called when the ScaleX combo selection changes.
	 */
	void scaleX() {
		try {
			this.xscale = Float.parseFloat(this.scaleXCombo.getText());
		} catch (final NumberFormatException e) {
			this.xscale = 1;
			this.scaleXCombo.select(this.scaleXCombo.indexOf("1"));
		}
		if (this.image != null) {
			this.resizeScrollBars();
			this.imageCanvas.redraw();
		}
	}

	/*
	 * Called when the ScaleY combo selection changes.
	 */
	void scaleY() {
		try {
			this.yscale = Float.parseFloat(this.scaleYCombo.getText());
		} catch (final NumberFormatException e) {
			this.yscale = 1;
			this.scaleYCombo.select(this.scaleYCombo.indexOf("1"));
		}
		if (this.image != null) {
			this.resizeScrollBars();
			this.imageCanvas.redraw();
		}
	}

	/*
	 * Called when the Alpha combo selection changes.
	 */
	void alpha() {
		try {
			this.alpha = Integer.parseInt(this.alphaCombo.getText());
		} catch (final NumberFormatException e) {
			this.alphaCombo.select(this.alphaCombo.indexOf("255"));
			this.alpha = 255;
		}
	}

	/*
	 * Called when the mouse moves in the image canvas.
	 * Show the color of the image at the point under the mouse.
	 */
	void showColorAt(final int mx, final int my) {
		final int x = mx - this.imageData.x - this.ix;
		final int y = my - this.imageData.y - this.iy;
		this.showColorForPixel(x, y);
	}

	/*
	 * Called when a mouse down or key press is detected
	 * in the data text. Show the color of the pixel at
	 * the caret position in the data text.
	 */
	void showColorForData() {
		final int delimiterLength = this.dataText.getLineDelimiter().length();
		final int charactersPerLine = 6 + (3 * this.imageData.bytesPerLine) + delimiterLength;
		final int position = this.dataText.getCaretOffset();
		final int y = position / charactersPerLine;
		if (((position - (y * charactersPerLine)) < 6) || ((((y + 1) * charactersPerLine) - position) <= delimiterLength)) {
			this.statusLabel.setText("");
			return;
		}
		final int dataPosition = position - (6 * (y + 1)) - (delimiterLength * y);
		final int byteNumber = dataPosition / 3;
		final int where = dataPosition - (byteNumber * 3);
		final int xByte = byteNumber % this.imageData.bytesPerLine;
		int x = -1;
		final int depth = this.imageData.depth;
		if (depth == 1) { // 8 pixels per byte (can only show 3 of 8)
			if (where == 0) {
        x = xByte * 8;
      }
			if (where == 1) {
        x = (xByte * 8) + 3;
      }
			if (where == 2) {
        x = (xByte * 8) + 7;
      }
		}
		if (depth == 2) { // 4 pixels per byte (can only show 3 of 4)
			if (where == 0) {
        x = xByte * 4;
      }
			if (where == 1) {
        x = (xByte * 4) + 1;
      }
			if (where == 2) {
        x = (xByte * 4) + 3;
      }
		}
		if (depth == 4) { // 2 pixels per byte
			if (where == 0) {
        x = xByte * 2;
      }
			if (where == 1) {
        x = xByte * 2;
      }
			if (where == 2) {
        x = (xByte * 2) + 1;
      }
		}
		if (depth == 8) { // 1 byte per pixel
			x = xByte;
		}
		if (depth == 16) { // 2 bytes per pixel
			x = xByte / 2;
		}
		if (depth == 24) { // 3 bytes per pixel
			x = xByte / 3;
		}
		if (depth == 32) { // 4 bytes per pixel
			x = xByte / 4;
		}
		if (x != -1) {
			this.showColorForPixel(x, y);
		} else {
			this.statusLabel.setText("");
		}
	}

	/*
	 * Set the status label to show color information
	 * for the specified pixel in the image.
	 */
	void showColorForPixel(final int x, final int y) {
		if ((x >= 0) && (x < this.imageData.width) && (y >= 0) && (y < this.imageData.height)) {
			final int pixel = this.imageData.getPixel(x, y);
			final RGB rgb = this.imageData.palette.getRGB(pixel);
			boolean hasAlpha = false;
			int alphaValue = 0;
			if ((this.imageData.alphaData != null) && (this.imageData.alphaData.length > 0)) {
				hasAlpha = true;
				alphaValue = this.imageData.getAlpha(x, y);
			}
			final String rgbMessageFormat = bundle.getString(hasAlpha ? "RGBA" : "RGB");
			final Object[] rgbArgs = {
					Integer.toString(rgb.red),
					Integer.toString(rgb.green),
					Integer.toString(rgb.blue),
					Integer.toString(alphaValue)
			};
			final Object[] rgbHexArgs = {
					Integer.toHexString(rgb.red),
					Integer.toHexString(rgb.green),
					Integer.toHexString(rgb.blue),
					Integer.toHexString(alphaValue)
			};
			final Object[] args = {
					Integer.valueOf(x),
					Integer.valueOf(y),
					Integer.valueOf(pixel),
					Integer.toHexString(pixel),
					createMsg(rgbMessageFormat, rgbArgs),
					createMsg(rgbMessageFormat, rgbHexArgs),
					(pixel == this.imageData.transparentPixel) ? bundle.getString("Color_at_transparent") : ""};
			this.statusLabel.setText(createMsg(bundle.getString("Color_at"), args));
		} else {
			this.statusLabel.setText("");
		}
	}

	/*
	 * Called when the Animate button is pressed.
	 */
	void animate() {
		this.animate = !this.animate;
		if (this.animate && (this.image != null) && (this.imageDataArray.length > 1)) {
			this.animateThread = new Thread(bundle.getString("Animation")) {
				@Override
				public void run() {
					// Pre-animation widget setup.
					ImageAnalyzer.this.preAnimation();

					// Animate.
					try {
						ImageAnalyzer.this.animateLoop();
					} catch (final SWTException e) {
						ImageAnalyzer.this.display.syncExec(() -> ImageAnalyzer.this.showErrorDialog(createMsg(bundle.getString("Creating_image"),
									Integer.valueOf(ImageAnalyzer.this.imageDataIndex+1)),
									ImageAnalyzer.this.currentName, e));
					}

					// Post animation widget reset.
					ImageAnalyzer.this.postAnimation();
				}
			};
			this.animateThread.setDaemon(true);
			this.animateThread.start();
		}
	}

	/*
	 * Loop through all of the images in a multi-image file
	 * and display them one after another.
	 */
	void animateLoop() {
		// Create an off-screen image to draw on, and a GC to draw with.
		// Both are disposed after the animation.
		final Image offScreenImage = new Image(this.display, this.loader.logicalScreenWidth, this.loader.logicalScreenHeight);
		final GC offScreenImageGC = new GC(offScreenImage);

		try {
			// Use syncExec to get the background color of the imageCanvas.
			this.display.syncExec(() -> this.canvasBackground = this.imageCanvas.getBackground());

			// Fill the off-screen image with the background color of the canvas.
			offScreenImageGC.setBackground(this.canvasBackground);
			offScreenImageGC.fillRectangle(
				0,
				0,
				this.loader.logicalScreenWidth,
				this.loader.logicalScreenHeight);

			// Draw the current image onto the off-screen image.
			offScreenImageGC.drawImage(
				this.image,
				0,
				0,
				this.imageData.width,
				this.imageData.height,
				this.imageData.x,
				this.imageData.y,
				this.imageData.width,
				this.imageData.height);

			int repeatCount = this.loader.repeatCount;
			while (this.animate && ((this.loader.repeatCount == 0) || (repeatCount > 0))) {
				if (this.imageData.disposalMethod == SWT.DM_FILL_BACKGROUND) {
					// Fill with the background color before drawing.
					Color bgColor = null;
					final int backgroundPixel = this.loader.backgroundPixel;
					if (this.showBackground && (backgroundPixel != -1)) {
						// Fill with the background color.
						final RGB backgroundRGB = this.imageData.palette.getRGB(backgroundPixel);
						bgColor = new Color(backgroundRGB);
					}
					offScreenImageGC.setBackground(bgColor != null ? bgColor : this.canvasBackground);
					offScreenImageGC.fillRectangle(
						this.imageData.x,
						this.imageData.y,
						this.imageData.width,
						this.imageData.height);
				} else if (this.imageData.disposalMethod == SWT.DM_FILL_PREVIOUS) {
					// Restore the previous image before drawing.
					offScreenImageGC.drawImage(
						this.image,
						0,
						0,
						this.imageData.width,
						this.imageData.height,
						this.imageData.x,
						this.imageData.y,
						this.imageData.width,
						this.imageData.height);
				}

				// Get the next image data.
				this.imageDataIndex = (this.imageDataIndex + 1) % this.imageDataArray.length;
				this.imageData = this.imageDataArray[this.imageDataIndex];
				this.image.dispose();
				this.image = new Image(this.display, this.imageData);

				// Draw the new image data.
				offScreenImageGC.drawImage(
					this.image,
					0,
					0,
					this.imageData.width,
					this.imageData.height,
					this.imageData.x,
					this.imageData.y,
					this.imageData.width,
					this.imageData.height);

				// Draw the off-screen image to the screen.
				this.imageCanvasGC.drawImage(offScreenImage, 0, 0);

				// Sleep for the specified delay time before drawing again.
				try {
					Thread.sleep(visibleDelay(this.imageData.delayTime * 10));
				} catch (final InterruptedException e) {
				}

				// If we have just drawn the last image in the set,
				// then decrement the repeat count.
				if (this.imageDataIndex == (this.imageDataArray.length - 1)) {
          repeatCount--;
        }
			}
		} finally {
			offScreenImage.dispose();
			offScreenImageGC.dispose();
		}
	}

	/*
	 * Pre animation setup.
	 */
	void preAnimation() {
		this.display.syncExec(() -> {
			// Change the label of the Animate button to 'Stop'.
			this.animateButton.setText(bundle.getString("Stop"));

			// Disable anything we don't want the user
			// to select during the animation.
			this.previousButton.setEnabled(false);
			this.nextButton.setEnabled(false);
			this.backgroundCombo.setEnabled(false);
			this.scaleXCombo.setEnabled(false);
			this.scaleYCombo.setEnabled(false);
			this.alphaCombo.setEnabled(false);
			this.incrementalCheck.setEnabled(false);
			this.transparentCheck.setEnabled(false);
			this.maskCheck.setEnabled(false);
			// leave backgroundCheck enabled

			// Reset the scale combos and scrollbars.
			this.resetScaleCombos();
			this.resetScrollBars();
		});
	}

	/*
	 * Post animation reset.
	 */
	void postAnimation() {
		this.display.syncExec(() -> {
			// Enable anything we disabled before the animation.
			this.previousButton.setEnabled(true);
			this.nextButton.setEnabled(true);
			this.backgroundCombo.setEnabled(true);
			this.scaleXCombo.setEnabled(true);
			this.scaleYCombo.setEnabled(true);
			this.alphaCombo.setEnabled(true);
			this.incrementalCheck.setEnabled(true);
			this.transparentCheck.setEnabled(true);
			this.maskCheck.setEnabled(true);

			// Reset the label of the Animate button.
			this.animateButton.setText(bundle.getString("Animate"));

			if (this.animate) {
				// If animate is still true, we finished the
				// full number of repeats. Leave the image as-is.
				this.animate = false;
			} else {
				// Redisplay the current image and its palette.
				this.displayImage(this.imageDataArray[this.imageDataIndex]);
			}
		});
	}

	/*
	 * Called when the Previous button is pressed.
	 * Display the previous image in a multi-image file.
	 */
	void previous() {
		if ((this.image != null) && (this.imageDataArray.length > 1)) {
			if (this.imageDataIndex == 0) {
				this.imageDataIndex = this.imageDataArray.length;
			}
			this.imageDataIndex = this.imageDataIndex - 1;
			this.displayImage(this.imageDataArray[this.imageDataIndex]);
		}
	}

	/*
	 * Called when the Next button is pressed.
	 * Display the next image in a multi-image file.
	 */
	void next() {
		if ((this.image != null) && (this.imageDataArray.length > 1)) {
			this.imageDataIndex = (this.imageDataIndex + 1) % this.imageDataArray.length;
			this.displayImage(this.imageDataArray[this.imageDataIndex]);
		}
	}

	void displayImage(final ImageData newImageData) {
		this.resetScaleCombos();
		if (this.incremental && (this.incrementalThread != null)) {
			// Tell the incremental thread to stop drawing.
			synchronized (this) {
				this.incrementalEvents = null;
			}

			// Wait until the incremental thread is done.
			while (this.incrementalThread.isAlive()) {
				if (!this.display.readAndDispatch()) {
          this.display.sleep();
        }
			}
		}

		// Dispose of the old image, if there was one.
		if (this.image != null) {
      this.image.dispose();
    }

		try {
			// Cache the new image and imageData.
			this.image = new Image(this.display, newImageData);
			this.imageData = newImageData;

		} catch (final SWTException e) {
			this.showErrorDialog(bundle.getString("Creating_from") + " ", this.currentName, e);
			this.image = null;
			return;
		}

		// Update the widgets with the new image info.
		String string = createMsg(bundle.getString("Analyzer_on"), this.currentName);
		this.shell.setText(string);

		if (this.imageDataArray.length > 1) {
			string = createMsg(bundle.getString("Type_index"), this.fileTypeString(this.imageData.type),
					Integer.valueOf(this.imageDataIndex + 1), Integer.valueOf(this.imageDataArray.length));
		} else {
			string = createMsg(bundle.getString("Type_string"), this.fileTypeString(this.imageData.type));
		}
		this.typeLabel.setText(string);

		string = createMsg(bundle.getString("Size_value"),
					 Integer.valueOf(this.imageData.width), Integer.valueOf(this.imageData.height));
		this.sizeLabel.setText(string);

		string = createMsg(bundle.getString("Depth_value"),
				Integer.valueOf(this.imageData.depth), Integer.valueOf(this.display.getDepth()));
		this.depthLabel.setText(string);

		string = createMsg(bundle.getString("Transparent_pixel_value"), pixelInfo(this.imageData.transparentPixel));
		this.transparentPixelLabel.setText(string);

		string = createMsg(bundle.getString("Time_to_load_value"), Long.valueOf(this.loadTime));
		this.timeToLoadLabel.setText(string);

		string = createMsg(bundle.getString("Animation_size_value"),
		                      Integer.valueOf(this.loader.logicalScreenWidth), Integer.valueOf(this.loader.logicalScreenHeight));
		this.screenSizeLabel.setText(string);

		string = createMsg(bundle.getString("Background_pixel_value"), pixelInfo(this.loader.backgroundPixel));
		this.backgroundPixelLabel.setText(string);

		string = createMsg(bundle.getString("Image_location_value"),
		                      Integer.valueOf(this.imageData.x), Integer.valueOf(this.imageData.y));
		this.locationLabel.setText(string);

		string = createMsg(bundle.getString("Disposal_value"),
		                      Integer.valueOf(this.imageData.disposalMethod), disposalString(this.imageData.disposalMethod));
		this.disposalMethodLabel.setText(string);

		final int delay = this.imageData.delayTime * 10;
		final int delayUsed = visibleDelay(delay);
		if (delay != delayUsed) {
			string = createMsg(bundle.getString("Delay_value"),
			                   Integer.valueOf(delay), Integer.valueOf(delayUsed));
		} else {
			string = createMsg(bundle.getString("Delay_used"), Integer.valueOf(delay));
		}
		this.delayTimeLabel.setText(string);

		if (this.loader.repeatCount == 0) {
			string = createMsg( bundle.getString("Repeats_forever"), Integer.valueOf(this.loader.repeatCount));
		} else {
			string = createMsg(bundle.getString("Repeats_value"), Integer.valueOf(this.loader.repeatCount));
		}
		this.repeatCountLabel.setText(string);

		if (this.imageData.palette.isDirect) {
			string = bundle.getString("Palette_direct");
		} else {
			string = createMsg(bundle.getString("Palette_value"), Integer.valueOf(this.imageData.palette.getRGBs().length));
		}
		this.paletteLabel.setText(string);

		string = createMsg(
				bundle.getString("Pixel_data_value"),
						Integer.valueOf(this.imageData.bytesPerLine),
						Integer.valueOf(this.imageData.scanlinePad),
						depthInfo(this.imageData.depth),
						((this.imageData.alphaData != null) && (this.imageData.alphaData.length > 0)) ?
								bundle.getString("Scroll_for_alpha") : "");
		this.dataLabel.setText(string);

		final String data = this.dataHexDump(this.dataText.getLineDelimiter());
		this.dataText.setText(data);

		final ArrayList<StyleRange> ranges = new ArrayList<>();
		// bold the first column all the way down
		int index = 0;
		while((index = data.indexOf(':', index+1)) != -1) {
			int start = index - INDEX_DIGITS;
			int length = INDEX_DIGITS;
			if (Character.isLetter(data.charAt(index-1))) {
				start = index - ALPHA_CHARS;
				length = ALPHA_CHARS;
			}
			ranges.add(new StyleRange(start, length, this.dataText.getForeground(), this.dataText.getBackground(), SWT.BOLD));
		}
		if(!ranges.isEmpty()) {
      this.dataText.setStyleRanges(ranges.toArray(new StyleRange[0]));
    }

		this.statusLabel.setText("");

		// Redraw both canvases.
		this.resetScrollBars();
		this.paletteCanvas.redraw();
		this.imageCanvas.redraw();
	}

	void paintImage(final PaintEvent event) {
		final GC gc = event.gc;
		Image paintImage = this.image;

		/* If the user wants to see the transparent pixel in its actual color,
		 * then temporarily turn off transparency.
		 */
		final int transparentPixel = this.imageData.transparentPixel;
		if ((transparentPixel != -1) && !this.transparent) {
			this.imageData.transparentPixel = -1;
			paintImage = new Image(this.display, this.imageData);
		}

		/* Scale the image when drawing, using the user's selected scaling factor. */
		final int w = Math.round(this.imageData.width * this.xscale);
		final int h = Math.round(this.imageData.height * this.yscale);

		/* If any of the background is visible, fill it with the background color. */
		final Rectangle bounds = this.imageCanvas.getBounds();
		if (this.imageData.getTransparencyType() != SWT.TRANSPARENCY_NONE) {
			/* If there is any transparency at all, fill the whole background. */
			gc.fillRectangle(0, 0, bounds.width, bounds.height);
		} else {
			/* Otherwise, just fill in the backwards L. */
			if ((this.ix + w) < bounds.width) {
        gc.fillRectangle(this.ix + w, 0, bounds.width - (this.ix + w), bounds.height);
      }
			if ((this.iy + h) < bounds.height) {
        gc.fillRectangle(0, this.iy + h, this.ix + w, bounds.height - (this.iy + h));
      }
		}

		/* Draw the image */
		gc.drawImage(
			paintImage,
			0,
			0,
			this.imageData.width,
			this.imageData.height,
			this.ix + this.imageData.x,
			this.iy + this.imageData.y,
			w,
			h);

		/* If there is a mask and the user wants to see it, draw it. */
		if (this.showMask && (this.imageData.getTransparencyType() != SWT.TRANSPARENCY_NONE)) {
			final ImageData maskImageData = this.imageData.getTransparencyMask();
			final Image maskImage = new Image(this.display, maskImageData);
			gc.drawImage(
				maskImage,
				0,
				0,
				this.imageData.width,
				this.imageData.height,
				w + 10 + this.ix + this.imageData.x,
				this.iy + this.imageData.y,
				w,
				h);
			maskImage.dispose();
		}

		/* If transparency was temporarily disabled, restore it. */
		if ((transparentPixel != -1) && !this.transparent) {
			this.imageData.transparentPixel = transparentPixel;
			paintImage.dispose();
		}
	}

	void paintPalette(final PaintEvent event) {
		final GC gc = event.gc;
		gc.fillRectangle(this.paletteCanvas.getClientArea());
		if (this.imageData.palette.isDirect) {
			// For a direct palette, display the masks.
			int y = this.py + 10;
			final int xTab = 50;
			gc.drawString("rMsk", 10, y, true);
			gc.drawString(toHex4ByteString(this.imageData.palette.redMask), xTab, y, true);
			gc.drawString("gMsk", 10, y+=12, true);
			gc.drawString(toHex4ByteString(this.imageData.palette.greenMask), xTab, y, true);
			gc.drawString("bMsk", 10, y+=12, true);
			gc.drawString(toHex4ByteString(this.imageData.palette.blueMask), xTab, y, true);
			gc.drawString("rShf", 10, y+=12, true);
			gc.drawString(Integer.toString(this.imageData.palette.redShift), xTab, y, true);
			gc.drawString("gShf", 10, y+=12, true);
			gc.drawString(Integer.toString(this.imageData.palette.greenShift), xTab, y, true);
			gc.drawString("bShf", 10, y+=12, true);
			gc.drawString(Integer.toString(this.imageData.palette.blueShift), xTab, y, true);
		} else {
			// For an indexed palette, display the palette colors and indices.
			final RGB[] rgbs = this.imageData.palette.getRGBs();
			if (rgbs != null) {
				final int xTab1 = 40, xTab2 = 100;
				for (int i = 0; i < rgbs.length; i++) {
					final int y = ((i+1) * 10) + this.py;
					gc.drawString(String.valueOf(i), 10, y, true);
					gc.drawString(toHexByteString(rgbs[i].red) + toHexByteString(rgbs[i].green) + toHexByteString(rgbs[i].blue), xTab1, y, true);
					final Color color = new Color(rgbs[i]);
					gc.setBackground(color);
					gc.fillRectangle(xTab2, y+2, 10, 10);
				}
			}
		}
	}

	void resizeShell(final ControlEvent event) {
		if ((this.image == null) || this.shell.isDisposed()) {
      return;
    }
		this.resizeScrollBars();
	}

	// Reset the scale combos to 1.
	void resetScaleCombos() {
		this.xscale = 1; this.yscale = 1;
		this.scaleXCombo.select(this.scaleXCombo.indexOf("1"));
		this.scaleYCombo.select(this.scaleYCombo.indexOf("1"));
	}

	// Reset the scroll bars to 0.
	void resetScrollBars() {
		if (this.image == null) {
      return;
    }
		this.ix = 0; this.iy = 0; this.py = 0;
		this.resizeScrollBars();
		this.imageCanvas.getHorizontalBar().setSelection(0);
		this.imageCanvas.getVerticalBar().setSelection(0);
		this.paletteCanvas.getVerticalBar().setSelection(0);
	}

	void resizeScrollBars() {
		// Set the max and thumb for the image canvas scroll bars.
		final ScrollBar horizontal = this.imageCanvas.getHorizontalBar();
		ScrollBar vertical = this.imageCanvas.getVerticalBar();
		Rectangle canvasBounds = this.imageCanvas.getClientArea();
		final int width = Math.round(this.imageData.width * this.xscale);
		if (width > canvasBounds.width) {
			// The image is wider than the canvas.
			horizontal.setEnabled(true);
			horizontal.setMaximum(width);
			horizontal.setThumb(canvasBounds.width);
			horizontal.setPageIncrement(canvasBounds.width);
		} else {
			// The canvas is wider than the image.
			horizontal.setEnabled(false);
			if (this.ix != 0) {
				// Make sure the image is completely visible.
				this.ix = 0;
				this.imageCanvas.redraw();
			}
		}
		final int height = Math.round(this.imageData.height * this.yscale);
		if (height > canvasBounds.height) {
			// The image is taller than the canvas.
			vertical.setEnabled(true);
			vertical.setMaximum(height);
			vertical.setThumb(canvasBounds.height);
			vertical.setPageIncrement(canvasBounds.height);
		} else {
			// The canvas is taller than the image.
			vertical.setEnabled(false);
			if (this.iy != 0) {
				// Make sure the image is completely visible.
				this.iy = 0;
				this.imageCanvas.redraw();
			}
		}

		// Set the max and thumb for the palette canvas scroll bar.
		vertical = this.paletteCanvas.getVerticalBar();
		if (this.imageData.palette.isDirect) {
			vertical.setEnabled(false);
		} else { // indexed palette
			canvasBounds = this.paletteCanvas.getClientArea();
			final int paletteHeight = (this.imageData.palette.getRGBs().length * 10) + 20; // 10 pixels each index + 20 for margins.
			vertical.setEnabled(true);
			vertical.setMaximum(paletteHeight);
			vertical.setThumb(canvasBounds.height);
			vertical.setPageIncrement(canvasBounds.height);
		}
	}

	/*
	 * Called when the image canvas' horizontal scrollbar is selected.
	 */
	void scrollHorizontally(final ScrollBar scrollBar) {
		if (this.image == null) {
      return;
    }
		final Rectangle canvasBounds = this.imageCanvas.getClientArea();
		final int width = Math.round(this.imageData.width * this.xscale);
		final int height = Math.round(this.imageData.height * this.yscale);
		if (width > canvasBounds.width) {
			// Only scroll if the image is bigger than the canvas.
			int x = -scrollBar.getSelection();
			if ((x + width) < canvasBounds.width) {
				// Don't scroll past the end of the image.
				x = canvasBounds.width - width;
			}
			this.imageCanvas.scroll(x, this.iy, this.ix, this.iy, width, height, false);
			this.ix = x;
		}
	}

	/*
	 * Called when the image canvas' vertical scrollbar is selected.
	 */
	void scrollVertically(final ScrollBar scrollBar) {
		if (this.image == null) {
      return;
    }
		final Rectangle canvasBounds = this.imageCanvas.getClientArea();
		final int width = Math.round(this.imageData.width * this.xscale);
		final int height = Math.round(this.imageData.height * this.yscale);
		if (height > canvasBounds.height) {
			// Only scroll if the image is bigger than the canvas.
			int y = -scrollBar.getSelection();
			if ((y + height) < canvasBounds.height) {
				// Don't scroll past the end of the image.
				y = canvasBounds.height - height;
			}
			this.imageCanvas.scroll(this.ix, y, this.ix, this.iy, width, height, false);
			this.iy = y;
		}
	}

	/*
	 * Called when the palette canvas' vertical scrollbar is selected.
	 */
	void scrollPalette(final ScrollBar scrollBar) {
		if (this.image == null) {
      return;
    }
		final Rectangle canvasBounds = this.paletteCanvas.getClientArea();
		final int paletteHeight = (this.imageData.palette.getRGBs().length * 10) + 20;
		if (paletteHeight > canvasBounds.height) {
			// Only scroll if the palette is bigger than the canvas.
			int y = -scrollBar.getSelection();
			if ((y + paletteHeight) < canvasBounds.height) {
				// Don't scroll past the end of the palette.
				y = canvasBounds.height - paletteHeight;
			}
			this.paletteCanvas.scroll(0, y, 0, this.py, this.paletteWidth, paletteHeight, false);
			this.py = y;
		}
	}

	/*
	 * Return a String containing a line-by-line dump of
	 * the data in the current imageData. The lineDelimiter
	 * parameter must be a string of length 1 or 2.
	 */
	String dataHexDump(final String lineDelimiter) {
		final int MAX_DUMP = 1024 * 1024;
		if (this.image == null) {
      return "";
    }
		boolean truncated = false;
		char[] dump = null;
		final byte[] alphas = this.imageData.alphaData;
		try {
			int length = this.imageData.height * (6 + (3 * this.imageData.bytesPerLine) + lineDelimiter.length());
			if ((alphas != null) && (alphas.length > 0)) {
				length += (this.imageData.height * (6 + (3 * this.imageData.width) + lineDelimiter.length())) + 6 + lineDelimiter.length();
			}
			dump = new char[length];
		} catch (final OutOfMemoryError e) {
			/* Too much data to dump - truncate. */
			dump = new char[MAX_DUMP];
			truncated = true;
		}
		int index = 0;
		try {
			for (int i = 0; i < this.imageData.data.length; i++) {
				if ((i % this.imageData.bytesPerLine) == 0) {
					final int line = i / this.imageData.bytesPerLine;
					dump[index++] = Character.forDigit((line / 1000) % 10, 10);
					dump[index++] = Character.forDigit((line / 100) % 10, 10);
					dump[index++] = Character.forDigit((line / 10) % 10, 10);
					dump[index++] = Character.forDigit(line % 10, 10);
					dump[index++] = ':';
					dump[index++] = ' ';
				}
				final byte b = this.imageData.data[i];
				dump[index++] = Character.forDigit((b & 0xF0) >> 4, 16);
				dump[index++] = Character.forDigit(b & 0x0F, 16);
				dump[index++] = ' ';
				if (((i + 1) % this.imageData.bytesPerLine) == 0) {
					dump[index++] = lineDelimiter.charAt(0);
					if (lineDelimiter.length() > 1) {
						dump[index++] = lineDelimiter.charAt(1);
					}
				}
			}
			if ((alphas != null) && (alphas.length > 0)) {
				dump[index++] = lineDelimiter.charAt(0);
				if (lineDelimiter.length() > 1) {
					dump[index++] = lineDelimiter.charAt(1);
				}
				System.arraycopy(new char[]{'A','l','p','h','a',':'}, 0, dump, index, 6);
				index +=6;
				dump[index++] = lineDelimiter.charAt(0);
				if (lineDelimiter.length() > 1) {
					dump[index++] = lineDelimiter.charAt(1);
				}
				for (int i = 0; i < alphas.length; i++) {
					if ((i % this.imageData.width) == 0) {
						final int line = i / this.imageData.width;
						dump[index++] = Character.forDigit((line / 1000) % 10, 10);
						dump[index++] = Character.forDigit((line / 100) % 10, 10);
						dump[index++] = Character.forDigit((line / 10) % 10, 10);
						dump[index++] = Character.forDigit(line % 10, 10);
						dump[index++] = ':';
						dump[index++] = ' ';
					}
					final byte b = alphas[i];
					dump[index++] = Character.forDigit((b & 0xF0) >> 4, 16);
					dump[index++] = Character.forDigit(b & 0x0F, 16);
					dump[index++] = ' ';
					if (((i + 1) % this.imageData.width) == 0) {
						dump[index++] = lineDelimiter.charAt(0);
						if (lineDelimiter.length() > 1) {
							dump[index++] = lineDelimiter.charAt(1);
						}
					}
				}
			}
		} catch (final IndexOutOfBoundsException e) {}
		String result = "";
		try {
			result = new String(dump);
		} catch (final OutOfMemoryError e) {
			/* Too much data to display in the text widget - truncate. */
			result = new String(dump, 0, MAX_DUMP);
			truncated = true;
		}
		if (truncated) {
      result += "\n ...data dump truncated at " + MAX_DUMP + "bytes...";
    }
		return result;
	}

	/*
	 * Open an error dialog displaying the specified information.
	 */
	void showErrorDialog(final String operation, final String filename, final Throwable e) {
		final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR);
		final String message = createMsg(bundle.getString("Error"), operation, filename);
		String errorMessage = "";
		if (e != null) {
			if (e instanceof SWTException) {
				final SWTException swte = (SWTException) e;
				errorMessage = swte.getMessage();
				if (swte.throwable != null) {
					errorMessage += ":\n" + swte.throwable.toString();
				}
			} else if (e instanceof SWTError) {
				final SWTError swte = (SWTError) e;
				errorMessage = swte.getMessage();
				if (swte.throwable != null) {
					errorMessage += ":\n" + swte.throwable.toString();
				}
			} else {
				errorMessage = e.toString();
			}
		}
		box.setMessage(message + errorMessage);
		box.open();
	}

	/*
	 * Open a dialog asking the user for more information on the type of BMP file to save.
	 */
	int showBMPDialog() {
		final int [] bmpType = new int[1];
		bmpType[0] = SWT.IMAGE_BMP;
		final SelectionListener radioSelected = widgetSelectedAdapter(event -> {
			final Button radio = (Button) event.widget;
			if (radio.getSelection()) {
        bmpType[0] = ((Integer)radio.getData()).intValue();
      }
		});
		// need to externalize strings
		final Shell dialog = new Shell(this.shell, SWT.DIALOG_TRIM);

		dialog.setText(bundle.getString("Save_as_type"));
		dialog.setLayout(new GridLayout());

		Label label = new Label(dialog, SWT.NONE);
		label.setText(bundle.getString("Save_as_type_label"));

		Button radio = new Button(dialog, SWT.RADIO);
		radio.setText(bundle.getString("Save_as_type_no_compress"));
		radio.setSelection(true);
		radio.setData(Integer.valueOf(SWT.IMAGE_BMP));
		radio.addSelectionListener(radioSelected);

		radio = new Button(dialog, SWT.RADIO);
		radio.setText(bundle.getString("Save_as_type_rle_compress"));
		radio.setData(Integer.valueOf(SWT.IMAGE_BMP_RLE));
		radio.addSelectionListener(radioSelected);

		radio = new Button(dialog, SWT.RADIO);
		radio.setText(bundle.getString("Save_as_type_os2"));
		radio.setData(Integer.valueOf(SWT.IMAGE_OS2_BMP));
		radio.addSelectionListener(radioSelected);

		label = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button ok = new Button(dialog, SWT.PUSH);
		ok.setText(bundle.getString("OK"));
		final GridData data = new GridData();
		data.horizontalAlignment = SWT.CENTER;
		data.widthHint = 75;
		ok.setLayoutData(data);
		ok.addSelectionListener(widgetSelectedAdapter(e -> dialog.close()));

		dialog.pack();
		dialog.open();
		while (!dialog.isDisposed()) {
			if (!this.display.readAndDispatch()) {
        this.display.sleep();
      }
		}
		return bmpType[0];
	}

	/*
	 * Return a String describing how to analyze the bytes
	 * in the hex dump.
	 */
	static String depthInfo(final int depth) {
		final Object[] args = {Integer.valueOf(depth), ""};
		switch (depth) {
			case 1:
				args[1] = createMsg(bundle.getString("Multi_pixels"), Integer.valueOf(8), " [01234567]");
				break;
			case 2:
				args[1] = createMsg(bundle.getString("Multi_pixels"), Integer.valueOf(4), "[00112233]");
				break;
			case 4:
				args[1] = createMsg(bundle.getString("Multi_pixels"), Integer.valueOf(2), "[00001111]");
				break;
			case 8:
				args[1] = bundle.getString("One_byte");
				break;
			case 16:
				args[1] = createMsg(bundle.getString("Multi_bytes"), Integer.valueOf(2));
				break;
			case 24:
				args[1] = createMsg(bundle.getString("Multi_bytes"), Integer.valueOf(3));
				break;
			case 32:
				args[1] = createMsg(bundle.getString("Multi_bytes"), Integer.valueOf(4));
				break;
			default:
				args[1] = bundle.getString("Unsupported_lc");
		}
		return createMsg(bundle.getString("Depth_info"), args);
	}

	/*
	 * Return the specified number of milliseconds.
	 * If the specified number of milliseconds is too small
	 * to see a visual change, then return a higher number.
	 */
	static int visibleDelay(final int ms) {
		if (ms < 20) {
      return ms + 30;
    }
		if (ms < 30) {
      return ms + 10;
    }
		return ms;
	}

	/*
	 * Return the specified byte value as a hex string,
	 * preserving leading 0's.
	 */
	static String toHexByteString(final int i) {
		if (i <= 0x0f) {
      return "0" + Integer.toHexString(i);
    }
		return Integer.toHexString(i & 0xff);
	}

	/*
	 * Return the specified 4-byte value as a hex string,
	 * preserving leading 0's.
	 * (a bit 'brute force'... should probably use a loop...)
	 */
	static String toHex4ByteString(final int i) {
		final String hex = Integer.toHexString(i);
		if (hex.length() == 1) {
      return "0000000" + hex;
    }
		if (hex.length() == 2) {
      return "000000" + hex;
    }
		if (hex.length() == 3) {
      return "00000" + hex;
    }
		if (hex.length() == 4) {
      return "0000" + hex;
    }
		if (hex.length() == 5) {
      return "000" + hex;
    }
		if (hex.length() == 6) {
      return "00" + hex;
    }
		if (hex.length() == 7) {
      return "0" + hex;
    }
		return hex;
	}

	/*
	 * Return a String describing the specified
	 * transparent or background pixel.
	 */
	static String pixelInfo(final int pixel) {
		if (pixel == -1) {
			return pixel + " (" + bundle.getString("None_lc") + ")";
		}
		return pixel + " (0x" + Integer.toHexString(pixel) + ")";
	}

	/*
	 * Return a String describing the specified disposal method.
	 */
	static String disposalString(final int disposalMethod) {
		switch (disposalMethod) {
			case SWT.DM_FILL_NONE: return bundle.getString("None_lc");
			case SWT.DM_FILL_BACKGROUND: return bundle.getString("Background_lc");
			case SWT.DM_FILL_PREVIOUS: return bundle.getString("Previous_lc");
		}
		return bundle.getString("Unspecified_lc");
	}

	/*
	 * Return a String describing the specified image file type.
	 */
	String fileTypeString(final int filetype) {
		switch (filetype) {
    case SWT.IMAGE_BMP:
      return "BMP";
    case SWT.IMAGE_BMP_RLE:
      return "RLE" + this.imageData.depth + " BMP";
    case SWT.IMAGE_OS2_BMP:
      return "OS/2 BMP";
    case SWT.IMAGE_GIF:
      return "GIF";
    case SWT.IMAGE_ICO:
      return "ICO";
    case SWT.IMAGE_JPEG:
      return "JPEG";
    case SWT.IMAGE_PNG:
      return "PNG";
    case SWT.IMAGE_TIFF:
      return "TIFF";
    default:
      break;
    }
		return bundle.getString("Unknown_ac");
	}

	/*
	 * Return the specified file's image type, based on its extension.
	 * Note that this is not a very robust way to determine image type,
	 * and it is only to be used in the absence of any better method.
	 */
	int determineFileType(final String filename) {
		final String ext = filename.substring(filename.lastIndexOf('.') + 1);
		if (ext.equalsIgnoreCase("bmp")) {
			return this.showBMPDialog();
		}
		if (ext.equalsIgnoreCase("gif")) {
      return SWT.IMAGE_GIF;
    }
		if (ext.equalsIgnoreCase("ico")) {
      return SWT.IMAGE_ICO;
    }
		if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jfif")) {
      return SWT.IMAGE_JPEG;
    }
		if (ext.equalsIgnoreCase("png")) {
      return SWT.IMAGE_PNG;
    }
		if (ext.equalsIgnoreCase("tif") || ext.equalsIgnoreCase("tiff")) {
      return SWT.IMAGE_TIFF;
    }
		return SWT.IMAGE_UNDEFINED;
	}

	void showFileType(final String filename) {
		final String ext = filename.substring(filename.lastIndexOf('.') + 1);
		if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jfif")) {
			this.imageTypeCombo.select(0);
			this.compressionCombo.setEnabled(true);
			this.compressionRatioLabel.setEnabled(true);
			if (this.compressionCombo.getItemCount() == 100) {
        return;
      }
			this.compressionCombo.removeAll();
			for (int i = 0; i < 100; i++) {
				this.compressionCombo.add(String.valueOf(i + 1));
			}
			this.compressionCombo.select(this.compressionCombo.indexOf("75"));
			return;
		}
		if (ext.equalsIgnoreCase("png")) {
			this.imageTypeCombo.select(1);
			this.compressionCombo.setEnabled(true);
			this.compressionRatioLabel.setEnabled(true);
			if (this.compressionCombo.getItemCount() == 10) {
        return;
      }
			this.compressionCombo.removeAll();
			for (int i = 0; i < 4; i++) {
				this.compressionCombo.add(String.valueOf(i));
			}
			this.compressionCombo.select(0);
			return;
		}
		if (ext.equalsIgnoreCase("bmp")) {
			this.imageTypeCombo.select(5);
		}
		if (ext.equalsIgnoreCase("gif")) {
			this.imageTypeCombo.select(2);
		}
		if (ext.equalsIgnoreCase("ico")) {
			this.imageTypeCombo.select(3);
		}
		if (ext.equalsIgnoreCase("tif") || ext.equalsIgnoreCase("tiff")) {
			this.imageTypeCombo.select(4);
		}
		this.compressionCombo.setEnabled(false);
		this.compressionRatioLabel.setEnabled(false);
	}

	static String createMsg(final String msg, final Object... args) {
		final MessageFormat formatter = new MessageFormat(msg);
		return formatter.format(args);
	}

}
