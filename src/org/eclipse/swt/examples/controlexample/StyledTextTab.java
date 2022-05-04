/*******************************************************************************
 * Copyright (c) 2000, 2019 IBM Corporation and others.
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
package org.eclipse.swt.examples.controlexample;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.MovementEvent;
import org.eclipse.swt.custom.MovementListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Widget;

class StyledTextTab extends ScrollableTab {
	/* Example widgets and groups that contain them */
	StyledText styledText;
	Group styledTextGroup, styledTextStyleGroup;

	/* Style widgets added to the "Style" group */
	Button wrapButton, readOnlyButton, fullSelectionButton;

	/* Buttons for adding StyleRanges to StyledText */
	Button boldButton, italicButton, redButton, yellowButton, underlineButton, strikeoutButton, resetButton;
	Image boldImage, italicImage, redImage, yellowImage, underlineImage, strikeoutImage;

	/* Other widgets added to the "Other" group */
	Button mouseNavigatorButton;

	/* Variables for saving state. */
	String text;
	StyleRange[] styleRanges;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	StyledTextTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates a bitmap image.
	 */
	Image createBitmapImage (final Display display, final String name) {
		final InputStream sourceStream = ControlExample.class.getResourceAsStream (name + ".bmp");
		final InputStream maskStream = ControlExample.class.getResourceAsStream (name + "_mask.bmp");
		final ImageData source = new ImageData (sourceStream);
		final ImageData mask = new ImageData (maskStream);
		final Image result = new Image (display, source, mask);
		try {
			sourceStream.close ();
			maskStream.close ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
		return result;
	}

	/**
	 * Creates the "Control" widget children.
	 */
	@Override
	void createControlWidgets () {
		super.createControlWidgets ();

		/* Add a group for modifying the StyledText widget */
		this.createStyledTextStyleGroup ();
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the styled text widget */
		this.styledTextGroup = new Group (this.exampleGroup, SWT.NONE);
		this.styledTextGroup.setLayout (new GridLayout ());
		this.styledTextGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.styledTextGroup.setText ("StyledText");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.singleButton.getSelection ()) {
      style |= SWT.SINGLE;
    }
		if (this.multiButton.getSelection ()) {
      style |= SWT.MULTI;
    }
		if (this.horizontalButton.getSelection ()) {
      style |= SWT.H_SCROLL;
    }
		if (this.verticalButton.getSelection ()) {
      style |= SWT.V_SCROLL;
    }
		if (this.wrapButton.getSelection ()) {
      style |= SWT.WRAP;
    }
		if (this.readOnlyButton.getSelection ()) {
      style |= SWT.READ_ONLY;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }
		if (this.fullSelectionButton.getSelection ()) {
      style |= SWT.FULL_SELECTION;
    }

		/* Create the example widgets */
		this.styledText = new StyledText (this.styledTextGroup, style);
		this.styledText.setText (ControlExample.getResourceString("Example_string"));
		this.styledText.append ("\n");
		this.styledText.append (ControlExample.getResourceString("One_Two_Three"));

		if (this.text != null) {
			this.styledText.setText(this.text);
			this.text = null;
		}
		if (this.styleRanges != null) {
			this.styledText.setStyleRanges(this.styleRanges);
			this.styleRanges = null;
		}
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup();

		/* Create the extra widgets */
		this.wrapButton = new Button (this.styleGroup, SWT.CHECK);
		this.wrapButton.setText ("SWT.WRAP");
		this.readOnlyButton = new Button (this.styleGroup, SWT.CHECK);
		this.readOnlyButton.setText ("SWT.READ_ONLY");
		this.fullSelectionButton = new Button (this.styleGroup, SWT.CHECK);
		this.fullSelectionButton.setText ("SWT.FULL_SELECTION");
	}

	/**
	 * Creates the "StyledText Style" group.
	 */
	void createStyledTextStyleGroup () {
		this.styledTextStyleGroup = new Group (this.controlGroup, SWT.NONE);
		this.styledTextStyleGroup.setText (ControlExample.getResourceString ("StyledText_Styles"));
		this.styledTextStyleGroup.setLayout (new GridLayout(6, false));
		final GridData data = new GridData (GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		this.styledTextStyleGroup.setLayoutData (data);

		/* Get images */
		this.boldImage = this.createBitmapImage (this.display, "bold");
		this.italicImage = this.createBitmapImage (this.display, "italic");
		this.redImage = this.createBitmapImage (this.display, "red");
		this.yellowImage = this.createBitmapImage (this.display, "yellow");
		this.underlineImage = this.createBitmapImage (this.display, "underline");
		this.strikeoutImage = this.createBitmapImage (this.display, "strikeout");

		/* Create controls to modify the StyledText */
		Label label = new Label (this.styledTextStyleGroup, SWT.NONE);
		label.setText (ControlExample.getResourceString ("StyledText_Style_Instructions"));
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		this.resetButton = new Button (this.styledTextStyleGroup, SWT.PUSH);
		this.resetButton.setText(ControlExample.getResourceString ("Clear"));
		this.resetButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
		label = new Label (this.styledTextStyleGroup, SWT.NONE);
		label.setText (ControlExample.getResourceString ("Bold"));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		this.boldButton = new Button (this.styledTextStyleGroup, SWT.PUSH);
		this.boldButton.setImage (this.boldImage);
		label = new Label (this.styledTextStyleGroup, SWT.NONE);
		label.setText (ControlExample.getResourceString ("Underline"));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		this.underlineButton = new Button (this.styledTextStyleGroup, SWT.PUSH);
		this.underlineButton.setImage (this.underlineImage);
		label = new Label (this.styledTextStyleGroup, SWT.NONE);
		label.setText (ControlExample.getResourceString ("Foreground_Style"));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		this.redButton = new Button (this.styledTextStyleGroup, SWT.PUSH);
		this.redButton.setImage (this.redImage);
		label = new Label (this.styledTextStyleGroup, SWT.NONE);
		label.setText (ControlExample.getResourceString ("Italic"));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		this.italicButton = new Button (this.styledTextStyleGroup, SWT.PUSH);
		this.italicButton.setImage (this.italicImage);
		label = new Label (this.styledTextStyleGroup, SWT.NONE);
		label.setText (ControlExample.getResourceString ("Strikeout"));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		this.strikeoutButton = new Button (this.styledTextStyleGroup, SWT.PUSH);
		this.strikeoutButton.setImage (this.strikeoutImage);
		label = new Label (this.styledTextStyleGroup, SWT.NONE);
		label.setText (ControlExample.getResourceString ("Background_Style"));
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		this.yellowButton = new Button (this.styledTextStyleGroup, SWT.PUSH);
		this.yellowButton.setImage (this.yellowImage);
		final SelectionListener styleListener = widgetSelectedAdapter(e -> {
			final Point sel = this.styledText.getSelectionRange();
			if ((sel == null) || (sel.y == 0)) {
        return;
      }
			StyleRange style;
			for (int i = sel.x; i<(sel.x+sel.y); i++) {
				final StyleRange range = this.styledText.getStyleRangeAtOffset(i);
				if ((range != null) && (e.widget != this.resetButton)) {
					style = (StyleRange)range.clone();
					style.start = i;
					style.length = 1;
				} else {
					style = new StyleRange(i, 1, null, null, SWT.NORMAL);
				}
				if (e.widget == this.boldButton) {
					style.fontStyle ^= SWT.BOLD;
				} else if (e.widget == this.italicButton) {
					style.fontStyle ^= SWT.ITALIC;
				} else if (e.widget == this.underlineButton) {
					style.underline = !style.underline;
				} else if (e.widget == this.strikeoutButton) {
					style.strikeout = !style.strikeout;
				}
				this.styledText.setStyleRange(style);
			}
			this.styledText.setSelectionRange(sel.x + sel.y, 0);
		});
		final SelectionListener colorListener = widgetSelectedAdapter(e -> {
			final Point sel = this.styledText.getSelectionRange();
			if ((sel == null) || (sel.y == 0)) {
        return;
      }
			Color fg = null, bg = null;
			if (e.widget == this.redButton) {
				fg = this.display.getSystemColor (SWT.COLOR_RED);
			} else if (e.widget == this.yellowButton) {
				bg = this.display.getSystemColor (SWT.COLOR_YELLOW);
			}
			StyleRange style;
			for (int i = sel.x; i<(sel.x+sel.y); i++) {
				final StyleRange range = this.styledText.getStyleRangeAtOffset(i);
				if (range != null) {
					style = (StyleRange)range.clone();
					style.start = i;
					style.length = 1;
					if (fg != null) {
            style.foreground = style.foreground != null ? null : fg;
          }
					if (bg != null) {
            style.background = style.background != null ? null : bg;
          }
				} else {
					style = new StyleRange (i, 1, fg, bg, SWT.NORMAL);
				}
				this.styledText.setStyleRange(style);
			}
			this.styledText.setSelectionRange(sel.x + sel.y, 0);
		});
		this.resetButton.addSelectionListener(styleListener);
		this.boldButton.addSelectionListener(styleListener);
		this.italicButton.addSelectionListener(styleListener);
		this.underlineButton.addSelectionListener(styleListener);
		this.strikeoutButton.addSelectionListener(styleListener);
		this.redButton.addSelectionListener(colorListener);
		this.yellowButton.addSelectionListener(colorListener);
		this.yellowButton.addDisposeListener(e -> {
			this.boldImage.dispose();
			this.italicImage.dispose();
			this.redImage.dispose();
			this.yellowImage.dispose();
			this.underlineImage.dispose();
			this.strikeoutImage.dispose();
		});
	}

	/**
	 * Creates the tab folder page.
	 *
	 * @param tabFolder org.eclipse.swt.widgets.TabFolder
	 * @return the new page for the tab folder
	 */
	@Override
	Composite createTabFolderPage (final TabFolder tabFolder) {
		super.createTabFolderPage (tabFolder);

		/*
		 * Add a resize listener to the tabFolderPage so that
		 * if the user types into the example widget to change
		 * its preferred size, and then resizes the shell, we
		 * recalculate the preferred size correctly.
		 */
		this.tabFolderPage.addControlListener(ControlListener.controlResizedAdapter(e ->	this.setExampleWidgetSize ()));

		return this.tabFolderPage;
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		this.mouseNavigatorButton = new Button (this.otherGroup, SWT.CHECK);
		this.mouseNavigatorButton.setText (ControlExample.getResourceString("Mouse_Nav"));
		this.mouseNavigatorButton.addSelectionListener (widgetSelectedAdapter(event -> this.styledText.setMouseNavigatorEnabled(this.mouseNavigatorButton.getSelection())));
	}

	/**
	 * Disposes the "Example" widgets.
	 */
	@Override
	void disposeExampleWidgets () {
		/* store the state of the styledText if applicable */
		if (this.styledText != null) {
			this.styleRanges = this.styledText.getStyleRanges();
			this.text = this.styledText.getText();
		}
		super.disposeExampleWidgets();
	}

	/**
	 * Gets the list of custom event names.
	 *
	 * @return an array containing custom event names
	 */
	@Override
	String [] getCustomEventNames () {
		return new String [] {
				"ExtendedModifyListener", "BidiSegmentListener", "LineBackgroundListener",
				"LineStyleListener", "PaintObjectListener", "TextChangeListener",
				"VerifyKeyListener", "WordMovementListener"};
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.styledText};
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"Alignment", "AlwaysShowScrollBars", "BlockSelection", "BottomMargin", "CaretOffset", "DoubleClickEnabled", "Editable", "HorizontalIndex", "HorizontalPixel", "Indent", "Justify", "LeftMargin", "LineSpacing", "Orientation", "RightMargin", "Selection", "Tabs", "TabStops", "Text", "TextLimit", "ToolTipText", "TopIndex", "TopMargin", "TopPixel", "WrapIndent", "WordWrap"};
	}


	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "StyledText";
	}

	/**
	 * Hooks the custom listener specified by eventName.
	 */
	@Override
	void hookCustomListener (final String eventName) {
		if (eventName == "ExtendedModifyListener") {
			this.styledText.addExtendedModifyListener (event -> this.log (eventName, event));
		}
		if (eventName == "BidiSegmentListener") {
			this.styledText.addBidiSegmentListener (event -> this.log (eventName, event));
		}
		if (eventName == "LineBackgroundListener") {
			this.styledText.addLineBackgroundListener (event -> this.log (eventName, event));
		}
		if (eventName == "LineStyleListener") {
			this.styledText.addLineStyleListener (event -> this.log (eventName, event));
		}
		if (eventName == "PaintObjectListener") {
			this.styledText.addPaintObjectListener (event -> this.log (eventName, event));
		}
		if (eventName == "TextChangeListener") {
			this.styledText.getContent().addTextChangeListener (new TextChangeListener() {
				@Override
				public void textChanged(final TextChangedEvent event) {
					StyledTextTab.this.log (eventName + ".textChanged", event);
				}
				@Override
				public void textChanging(final TextChangingEvent event) {
					StyledTextTab.this.log (eventName + ".textChanging", event);
				}
				@Override
				public void textSet(final TextChangedEvent event) {
					StyledTextTab.this.log (eventName + ".textSet", event);
				}
			});
		}
		if (eventName == "VerifyKeyListener") {
			this.styledText.addVerifyKeyListener (event -> this.log (eventName, event));
		}
		if (eventName == "WordMovementListener") {
			this.styledText.addWordMovementListener (new MovementListener() {
				@Override
				public void getNextOffset(final MovementEvent event) {
					StyledTextTab.this.log (eventName + ".getNextOffset", event);
				}
				@Override
				public void getPreviousOffset(final MovementEvent event) {
					StyledTextTab.this.log (eventName + ".getPreviousOffset", event);
				}
			});
		}
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState ();
		this.wrapButton.setSelection ((this.styledText.getStyle () & SWT.WRAP) != 0);
		this.readOnlyButton.setSelection ((this.styledText.getStyle () & SWT.READ_ONLY) != 0);
		this.fullSelectionButton.setSelection ((this.styledText.getStyle () & SWT.FULL_SELECTION) != 0);
		this.horizontalButton.setEnabled ((this.styledText.getStyle () & SWT.MULTI) != 0);
		this.verticalButton.setEnabled ((this.styledText.getStyle () & SWT.MULTI) != 0);
		this.wrapButton.setEnabled ((this.styledText.getStyle () & SWT.MULTI) != 0);
	}
}
