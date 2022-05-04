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
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 497575
 *******************************************************************************/
package org.eclipse.swt.examples.controlexample;


import static org.eclipse.swt.events.SelectionListener.widgetDefaultSelectedAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * <code>Tab</code> is the abstract superclass of every page
 * in the example's tab folder.  Each page in the tab folder
 * describes a control.
 *
 * A Tab itself is not a control but instead provides a
 * hierarchy with which to share code that is common to
 * every page in the folder.
 *
 * A typical page in a Tab contains a two column composite.
 * The left column contains the "Example" group.  The right
 * column contains "Control" group.  The "Control" group
 * contains controls that allow the user to interact with
 * the example control.  The "Control" group typically
 * contains a "Style", "Other" and "Size" group.  Subclasses
 * can override these defaults to augment a group or stop
 * a group from being created.
 */
abstract class Tab {
	Shell shell;
	Display display;

	/* Common control buttons */
	Button borderButton, enabledButton, visibleButton, backgroundImageButton, popupMenuButton;
	Button preferredButton, tooSmallButton, smallButton, largeButton, rectangleButton, fillHButton, fillVButton;

	/* Common groups and composites */
	Composite tabFolderPage;
	Group exampleGroup, controlGroup, listenersGroup, otherGroup, sizeGroup, styleGroup, colorGroup, backgroundModeGroup;

	/* Controlling instance */
	final ControlExample instance;

	/* Sizing constants for the "Size" group */
	static final int TOO_SMALL_SIZE	= 10;
	static final int RETANGLE_SIZE_WIDTH	= 140;
	static final int RETANGLE_SIZE_HEIGHT	= 30;
	static final int SMALL_SIZE		= 50;
	static final int LARGE_SIZE		= 100;

	/* Right-to-left support */
	static final boolean RTL_SUPPORT_ENABLE = "win32".equals(SWT.getPlatform()) || "gtk".equals(SWT.getPlatform());
	Group orientationGroup;
	Button rtlButton, ltrButton, defaultOrietationButton;
	Group directionGroup;
	Button rtlDirectionButton, ltrDirectionButton, autoDirectionButton, defaultDirectionButton;

	/* Controls and resources for the "Colors & Fonts" group */
	static final int IMAGE_SIZE = 12;
	static final int FOREGROUND_COLOR = 0;
	static final int BACKGROUND_COLOR = 1;
	static final int FONT = 2;
	Table colorAndFontTable;
	ColorDialog colorDialog;
	FontDialog fontDialog;
	Color foregroundColor, backgroundColor;
	Font font;

	/* Controls and resources for the "Background Mode" group */
	Combo backgroundModeCombo;
	Button backgroundModeImageButton, backgroundModeColorButton;

	boolean samplePopup = false;

	/* Set/Get API controls */
	Combo nameCombo;
	Label returnTypeLabel;
	Button getButton, setButton;
	Text setText, getText;
	Shell setGetDialog;

	/* Event logging variables and controls */
	Text eventConsole;
	boolean logging = false;
	boolean untypedEvents = false;
	boolean [] eventsFilter;
	int setFieldsMask = 0;
	Event setFieldsEvent = new Event ();
	boolean ignore = false;

	/* Event logging constants */
	static final int DOIT	= 0x0100;
	static final int DETAIL	= 0x0200;
	static final int TEXT	= 0x0400;
	static final int X		= 0x0800;
	static final int Y		= 0x1000;
	static final int WIDTH	= 0x2000;
	static final int HEIGHT	= 0x4000;

	static final int DETAIL_IME			= 0;
	static final int DETAIL_ERASE_ITEM	= 1;
	static final int DETAIL_TRAVERSE	= 2;

	static class EventInfo {
		String name;
		int type;
		int settableFields;
		int setFields;
		Event event;
		EventInfo (final String name, final int type, final int settableFields, final int setFields, final Event event) {
			this.name = name;
			this.type = type;
			this.settableFields = settableFields;
			this.setFields = setFields;
			this.event = event;
		}
	}

	final EventInfo [] EVENT_INFO = {
		new EventInfo ("Activate", SWT.Activate, 0, 0, new Event()),
		new EventInfo ("Arm", SWT.Arm, 0, 0, new Event()),
		new EventInfo ("Close", SWT.Close, DOIT, 0, new Event()),
		new EventInfo ("Collapse", SWT.Collapse, 0, 0, new Event()),
		new EventInfo ("Deactivate", SWT.Deactivate, 0, 0, new Event()),
		new EventInfo ("DefaultSelection", SWT.DefaultSelection, 0, 0, new Event()),
		new EventInfo ("Deiconify", SWT.Deiconify, 0, 0, new Event()),
		new EventInfo ("Dispose", SWT.Dispose, 0, 0, new Event()),
		new EventInfo ("DragDetect", SWT.DragDetect, 0, 0, new Event()),
		new EventInfo ("EraseItem", SWT.EraseItem, DETAIL | DETAIL_ERASE_ITEM, 0, new Event()),
		new EventInfo ("Expand", SWT.Expand, 0, 0, new Event()),
		new EventInfo ("FocusIn", SWT.FocusIn, 0, 0, new Event()),
		new EventInfo ("FocusOut", SWT.FocusOut, 0, 0, new Event()),
		new EventInfo ("HardKeyDown", SWT.HardKeyDown, 0, 0, new Event()),
		new EventInfo ("HardKeyUp", SWT.HardKeyUp, 0, 0, new Event()),
		new EventInfo ("Help", SWT.Help, 0, 0, new Event()),
		new EventInfo ("Hide", SWT.Hide, 0, 0, new Event()),
		new EventInfo ("Iconify", SWT.Iconify, 0, 0, new Event()),
		new EventInfo ("KeyDown", SWT.KeyDown, DOIT, 0, new Event()),
		new EventInfo ("KeyUp", SWT.KeyUp, DOIT, 0, new Event()),
		new EventInfo ("MeasureItem", SWT.MeasureItem, 0, 0, new Event()),
		new EventInfo ("MenuDetect", SWT.MenuDetect, X | Y | DOIT, 0, new Event()),
		new EventInfo ("Modify", SWT.Modify, 0, 0, new Event()),
		new EventInfo ("MouseDoubleClick", SWT.MouseDoubleClick, 0, 0, new Event()),
		new EventInfo ("MouseDown", SWT.MouseDown, 0, 0, new Event()),
		new EventInfo ("MouseEnter", SWT.MouseEnter, 0, 0, new Event()),
		new EventInfo ("MouseExit", SWT.MouseExit, 0, 0, new Event()),
		new EventInfo ("MouseHorizontalWheel", SWT.MouseHorizontalWheel, 0, 0, new Event()),
		new EventInfo ("MouseHover", SWT.MouseHover, 0, 0, new Event()),
		new EventInfo ("MouseMove", SWT.MouseMove, 0, 0, new Event()),
		new EventInfo ("MouseUp", SWT.MouseUp, 0, 0, new Event()),
		new EventInfo ("MouseVerticalWheel", SWT.MouseVerticalWheel, 0, 0, new Event()),
		new EventInfo ("Move", SWT.Move, 0, 0, new Event()),
		new EventInfo ("Paint", SWT.Paint, 0, 0, new Event()),
		new EventInfo ("PaintItem", SWT.PaintItem, 0, 0, new Event()),
		new EventInfo ("Resize", SWT.Resize, 0, 0, new Event()),
		new EventInfo ("Selection", SWT.Selection, X | Y | DOIT, 0, new Event()), // sash
		new EventInfo ("SetData", SWT.SetData, 0, 0, new Event()),
//		new EventInfo ("Settings", SWT.Settings, 0, 0, new Event()),  // note: this event only goes to Display
		new EventInfo ("Show", SWT.Show, 0, 0, new Event()),
		new EventInfo ("Traverse", SWT.Traverse, DETAIL | DETAIL_TRAVERSE | DOIT, 0, new Event()),
		new EventInfo ("Verify", SWT.Verify, TEXT | DOIT, 0, new Event()),
		new EventInfo ("ImeComposition", SWT.ImeComposition, DETAIL | DETAIL_IME | TEXT | DOIT, 0, new Event()),
	};

	static final String [][] DETAIL_CONSTANTS = {
		{ // DETAIL_IME = 0
			"SWT.COMPOSITION_CHANGED",
			"SWT.COMPOSITION_OFFSET",
			"SWT.COMPOSITION_SELECTION",
		},
		{ // DETAIL_ERASE_ITEM = 1
			"SWT.SELECTED",
			"SWT.FOCUSED",
			"SWT.BACKGROUND",
			"SWT.FOREGROUND",
			"SWT.HOT",
		},
		{ // DETAIL_TRAVERSE = 2
			"SWT.TRAVERSE_NONE",
			"SWT.TRAVERSE_ESCAPE",
			"SWT.TRAVERSE_RETURN",
			"SWT.TRAVERSE_TAB_PREVIOUS",
			"SWT.TRAVERSE_TAB_NEXT",
			"SWT.TRAVERSE_ARROW_PREVIOUS",
			"SWT.TRAVERSE_ARROW_NEXT",
			"SWT.TRAVERSE_MNEMONIC",
			"SWT.TRAVERSE_PAGE_PREVIOUS",
			"SWT.TRAVERSE_PAGE_NEXT",
		},
	};

	static final Object [] DETAIL_VALUES = {
		"SWT.COMPOSITION_CHANGED", Integer.valueOf(SWT.COMPOSITION_CHANGED),
		"SWT.COMPOSITION_OFFSET", Integer.valueOf(SWT.COMPOSITION_OFFSET),
		"SWT.COMPOSITION_SELECTION", Integer.valueOf(SWT.COMPOSITION_SELECTION),
		"SWT.SELECTED", Integer.valueOf(SWT.SELECTED),
		"SWT.FOCUSED", Integer.valueOf(SWT.FOCUSED),
		"SWT.BACKGROUND", Integer.valueOf(SWT.BACKGROUND),
		"SWT.FOREGROUND", Integer.valueOf(SWT.FOREGROUND),
		"SWT.HOT", Integer.valueOf(SWT.HOT),
		"SWT.TRAVERSE_NONE", Integer.valueOf(SWT.TRAVERSE_NONE),
		"SWT.TRAVERSE_ESCAPE", Integer.valueOf(SWT.TRAVERSE_ESCAPE),
		"SWT.TRAVERSE_RETURN", Integer.valueOf(SWT.TRAVERSE_RETURN),
		"SWT.TRAVERSE_TAB_PREVIOUS", Integer.valueOf(SWT.TRAVERSE_TAB_PREVIOUS),
		"SWT.TRAVERSE_TAB_NEXT", Integer.valueOf(SWT.TRAVERSE_TAB_NEXT),
		"SWT.TRAVERSE_ARROW_PREVIOUS", Integer.valueOf(SWT.TRAVERSE_ARROW_PREVIOUS),
		"SWT.TRAVERSE_ARROW_NEXT", Integer.valueOf(SWT.TRAVERSE_ARROW_NEXT),
		"SWT.TRAVERSE_MNEMONIC", Integer.valueOf(SWT.TRAVERSE_MNEMONIC),
		"SWT.TRAVERSE_PAGE_PREVIOUS", Integer.valueOf(SWT.TRAVERSE_PAGE_PREVIOUS),
		"SWT.TRAVERSE_PAGE_NEXT", Integer.valueOf(SWT.TRAVERSE_PAGE_NEXT),
	};

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	Tab(final ControlExample instance) {
		this.instance = instance;
	}

	/**
	 * Creates the "Control" group.  The "Control" group
	 * is typically the right hand column in the tab.
	 */
	void createControlGroup () {

		/*
		 * Create the "Control" group.  This is the group on the
		 * right half of each example tab.  It consists of the
		 * "Style" group, the "Other" group and the "Size" group.
		 */
		this.controlGroup = new Group (this.tabFolderPage, SWT.NONE);
		this.controlGroup.setLayout (new GridLayout (2, true));
		this.controlGroup.setLayoutData (new GridData(SWT.FILL, SWT.FILL, false, false));
		this.controlGroup.setText (ControlExample.getResourceString("Parameters"));

		/* Create individual groups inside the "Control" group */
		this.createStyleGroup ();
		this.createOtherGroup ();
		this.createSetGetGroup();
		this.createSizeGroup ();
		this.createColorAndFontGroup ();
		if (this.rtlSupport()) {
			this.createOrientationGroup ();
			this.createDirectionGroup ();
		}
		this.createBackgroundModeGroup ();

		/*
		 * For each Button child in the style group, add a selection
		 * listener that will recreate the example controls.  If the
		 * style group button is a RADIO button, ensure that the radio
		 * button is selected before recreating the example controls.
		 * When the user selects a RADIO button, the current RADIO
		 * button in the group is deselected and the new RADIO button
		 * is selected automatically.  The listeners are notified for
		 * both these operations but typically only do work when a RADIO
		 * button is selected.
		 */
		final SelectionListener selectionListener = widgetSelectedAdapter(event -> {
			if ((event.widget.getStyle () & SWT.RADIO) != 0) {
				if (!((Button) event.widget).getSelection ()) {
          return;
        }
			}
			if (!this.handleTextDirection (event.widget)) {
				this.recreateExampleWidgets ();
				if (this.rtlSupport ()) {
					/* Reflect the base direction falls back to the default (i.e. orientation). */
					this.ltrDirectionButton.setSelection (false);
					this.rtlDirectionButton.setSelection (false);
					this.autoDirectionButton.setSelection (false);
					this.defaultDirectionButton.setSelection (true);
				}
			}
		});
		final Control [] children = this.styleGroup.getChildren ();
		for (final Control child : children) {
			if (child instanceof Button) {
				final Button button = (Button) child;
				button.addSelectionListener (selectionListener);
			} else if (child instanceof Composite) {
      	/* Look down one more level of children in the style group. */
      	final Composite composite = (Composite) child;
      	final Control [] grandchildren = composite.getChildren ();
      	for (final Control grandchild : grandchildren) {
      		if (grandchild instanceof Button) {
      			final Button button = (Button) grandchild;
      			button.addSelectionListener (selectionListener);
      		}
      	}
      }
		}
		if (this.rtlSupport()) {
			this.rtlButton.addSelectionListener (selectionListener);
			this.ltrButton.addSelectionListener (selectionListener);
			this.defaultOrietationButton.addSelectionListener (selectionListener);
			this.rtlDirectionButton.addSelectionListener (selectionListener);
			this.ltrDirectionButton.addSelectionListener (selectionListener);
			this.autoDirectionButton.addSelectionListener (selectionListener);
			this.defaultDirectionButton.addSelectionListener (selectionListener);
		}
	}

	/**
	 * Append the Set/Get API controls to the "Other" group.
	 */
	void createSetGetGroup() {
		/*
		 * Create the button to access set/get API functionality.
		 */
		final String [] methodNames = this.getMethodNames ();
		if (methodNames != null) {
			final Button setGetButton = new Button (this.otherGroup, SWT.PUSH);
			setGetButton.setText (ControlExample.getResourceString ("Set_Get"));
			setGetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			setGetButton.addSelectionListener (widgetSelectedAdapter(e -> {
				if (this.getExampleWidgets().length >  0) {
					if (this.setGetDialog == null) {
						this.setGetDialog = this.createSetGetDialog(methodNames);
					}
					Point pt = setGetButton.getLocation();
					pt = this.display.map(setGetButton.getParent(), null, pt);
					this.setGetDialog.setLocation(pt.x, pt.y);
					this.setGetDialog.open();
				}
			}));
		}
	}

	/**
	 * Creates the "Control" widget children.
	 * Subclasses override this method to augment
	 * the standard controls created in the "Style",
	 * "Other" and "Size" groups.
	 */
	void createControlWidgets () {
	}

	/**
	 * Creates the "Colors and Fonts" group. This is typically
	 * a child of the "Control" group. Subclasses override
	 * this method to customize color and font settings.
	 */
	void createColorAndFontGroup () {
		/* Create the group. */
		this.colorGroup = new Group(this.controlGroup, SWT.NONE);
		this.colorGroup.setLayout (new GridLayout (2, true));
		this.colorGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.colorGroup.setText (ControlExample.getResourceString ("Colors"));
		this.colorAndFontTable = new Table(this.colorGroup, SWT.BORDER | SWT.V_SCROLL);
		this.colorAndFontTable.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		TableItem item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Foreground_Color"));
		this.colorAndFontTable.setSelection(0);
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Background_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Font"));
		final Button changeButton = new Button (this.colorGroup, SWT.PUSH);
		changeButton.setText(ControlExample.getResourceString("Change"));
		changeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final Button defaultsButton = new Button (this.colorGroup, SWT.PUSH);
		defaultsButton.setText(ControlExample.getResourceString("Defaults"));
		defaultsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		/* Add listeners to set/reset colors and fonts. */
		this.colorDialog = new ColorDialog (this.shell);
		this.fontDialog = new FontDialog (this.shell);
		this.colorAndFontTable.addSelectionListener(widgetDefaultSelectedAdapter(event -> this.changeFontOrColor (this.colorAndFontTable.getSelectionIndex())));
		changeButton.addSelectionListener(widgetSelectedAdapter(event -> this.changeFontOrColor (this.colorAndFontTable.getSelectionIndex())));
		defaultsButton.addSelectionListener(widgetSelectedAdapter(e -> this.resetColorsAndFonts ()));
		this.shell.addDisposeListener(event -> {
			if (this.font != null) {
        this.font.dispose();
      }
			this.foregroundColor = null;
			this.backgroundColor = null;
			this.font = null;
			if ((this.colorAndFontTable != null) && !this.colorAndFontTable.isDisposed()) {
				final TableItem [] items = this.colorAndFontTable.getItems();
				for (final TableItem currentItem : items) {
					final Image image = currentItem.getImage();
					if (image != null) {
            image.dispose();
          }
				}
			}
		});
	}

	void changeFontOrColor(final int index) {
		switch (index) {
			case FOREGROUND_COLOR: {
				Color oldColor = this.foregroundColor;
				if (oldColor == null) {
					final Control [] controls = this.getExampleControls ();
					if (controls.length > 0) {
            oldColor = controls [0].getForeground ();
          }
				}
				if (oldColor != null)
         {
          this.colorDialog.setRGB(oldColor.getRGB()); // seed dialog with current color
        }
				final RGB rgb = this.colorDialog.open();
				if (rgb == null) {
          return;
        }
				this.foregroundColor = new Color (rgb);
				this.setExampleWidgetForeground ();
			}
			break;
			case BACKGROUND_COLOR: {
				Color oldColor = this.backgroundColor;
				if (oldColor == null) {
					final Control [] controls = this.getExampleControls ();
					if (controls.length > 0)
           {
            oldColor = controls [0].getBackground (); // seed dialog with current color
          }
				}
				if (oldColor != null) {
          this.colorDialog.setRGB(oldColor.getRGB());
        }
				final RGB rgb = this.colorDialog.open();
				if (rgb == null) {
          return;
        }
				this.backgroundColor = new Color (rgb);
				this.setExampleWidgetBackground ();
			}
			break;
			case FONT: {
				Font oldFont = this.font;
				if (oldFont == null) {
					final Control [] controls = this.getExampleControls ();
					if (controls.length > 0) {
            oldFont = controls [0].getFont ();
          }
				}
				if (oldFont != null)
         {
          this.fontDialog.setFontList(oldFont.getFontData()); // seed dialog with current font
        }
				final FontData fontData = this.fontDialog.open ();
				if (fontData == null) {
          return;
        }
				oldFont = this.font; // dispose old font when done
				this.font = new Font (this.display, fontData);
				this.setExampleWidgetFont ();
				this.setExampleWidgetSize ();
				if (oldFont != null) {
          oldFont.dispose ();
        }
			}
			break;
		}
	}

	/**
	 * Creates the "Other" group.  This is typically
	 * a child of the "Control" group.
	 */
	void createOtherGroup () {
		/* Create the group */
		this.otherGroup = new Group (this.controlGroup, SWT.NONE);
		this.otherGroup.setLayout (new GridLayout ());
		this.otherGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.otherGroup.setText (ControlExample.getResourceString("Other"));

		/* Create the controls */
		this.enabledButton = new Button(this.otherGroup, SWT.CHECK);
		this.enabledButton.setText(ControlExample.getResourceString("Enabled"));
		this.visibleButton = new Button(this.otherGroup, SWT.CHECK);
		this.visibleButton.setText(ControlExample.getResourceString("Visible"));
		this.backgroundImageButton = new Button(this.otherGroup, SWT.CHECK);
		this.backgroundImageButton.setText(ControlExample.getResourceString("BackgroundImage"));
		this.popupMenuButton = new Button(this.otherGroup, SWT.CHECK);
		this.popupMenuButton.setText(ControlExample.getResourceString("PopupMenu"));

		/* Add the listeners */
		this.enabledButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleWidgetEnabled ()));
		this.visibleButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleWidgetVisibility ()));
		this.backgroundImageButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleWidgetBackgroundImage ()));
		this.popupMenuButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleWidgetPopupMenu ()));

		/* Set the default state */
		this.enabledButton.setSelection(true);
		this.visibleButton.setSelection(true);
		this.backgroundImageButton.setSelection(false);
		this.popupMenuButton.setSelection(false);
	}

	/**
	 * Creates the "Background Mode" group.
	 */
	void createBackgroundModeGroup () {
		/* Create the group */
		this.backgroundModeGroup = new Group (this.controlGroup, SWT.NONE);
		this.backgroundModeGroup.setLayout (new GridLayout ());
		this.backgroundModeGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.backgroundModeGroup.setText (ControlExample.getResourceString("Background_Mode"));

		/* Create the controls */
		this.backgroundModeCombo = new Combo(this.backgroundModeGroup, SWT.READ_ONLY);
		this.backgroundModeCombo.setItems("SWT.INHERIT_NONE", "SWT.INHERIT_DEFAULT", "SWT.INHERIT_FORCE");
		this.backgroundModeImageButton = new Button(this.backgroundModeGroup, SWT.CHECK);
		this.backgroundModeImageButton.setText(ControlExample.getResourceString("BackgroundImage"));
		this.backgroundModeColorButton = new Button(this.backgroundModeGroup, SWT.CHECK);
		this.backgroundModeColorButton.setText(ControlExample.getResourceString("BackgroundColor"));

		/* Add the listeners */
		this.backgroundModeCombo.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleGroupBackgroundMode ()));
		this.backgroundModeImageButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleGroupBackgroundImage ()));
		this.backgroundModeColorButton.addSelectionListener (widgetSelectedAdapter(event -> this.setExampleGroupBackgroundColor ()));

		/* Set the default state */
		this.backgroundModeCombo.setText(this.backgroundModeCombo.getItem(0));
		this.backgroundModeImageButton.setSelection(false);
		this.backgroundModeColorButton.setSelection(false);
	}

	void createEditEventDialog(final Shell parent, final int x, final int y, final int index) {
		final Shell dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(ControlExample.getResourceString ("Edit_Event"));
		final Label label = new Label (dialog, SWT.NONE);
		label.setText (ControlExample.getResourceString ("Edit_Event_Fields", this.EVENT_INFO[index].name));

		final Group group = new Group (dialog, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));

		final int fields = this.EVENT_INFO[index].settableFields;
		final int eventType = this.EVENT_INFO[index].type;
		this.setFieldsMask = this.EVENT_INFO[index].setFields;
		this.setFieldsEvent = this.EVENT_INFO[index].event;

		if ((fields & DOIT) != 0) {
			new Label (group, SWT.NONE).setText ("doit");
			final Combo doitCombo = new Combo (group, SWT.READ_ONLY);
			doitCombo.setItems ("", "true", "false");
			if ((this.setFieldsMask & DOIT) != 0) {
        doitCombo.setText(Boolean.toString(this.setFieldsEvent.doit));
      }
			doitCombo.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
			doitCombo.addSelectionListener(widgetSelectedAdapter(e -> {
				final String newValue = doitCombo.getText();
				if (newValue.length() == 0) {
					this.setFieldsMask &= ~DOIT;
				} else {
					this.setFieldsEvent.type = eventType;
					this.setFieldsEvent.doit = newValue.equals("true");
					this.setFieldsMask |= DOIT;
				}
			}));
		}

		if ((fields & DETAIL) != 0) {
			new Label (group, SWT.NONE).setText ("detail");
			final int detailType = fields & 0xFF;
			final Combo detailCombo = new Combo (group, SWT.READ_ONLY);
			detailCombo.setItems (DETAIL_CONSTANTS[detailType]);
			detailCombo.add ("", 0);
			detailCombo.setVisibleItemCount(detailCombo.getItemCount());
			if ((this.setFieldsMask & DETAIL) != 0) {
        detailCombo.setText (DETAIL_CONSTANTS[detailType][this.setFieldsEvent.detail]);
      }
			detailCombo.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
			detailCombo.addSelectionListener(widgetSelectedAdapter(e -> {
				final String newValue = detailCombo.getText();
				if (newValue.length() == 0) {
					this.setFieldsMask &= ~DETAIL;
				} else {
					this.setFieldsEvent.type = eventType;
					for (int i = 0; i < DETAIL_VALUES.length; i += 2) {
						if (newValue.equals (DETAIL_VALUES [i])) {
							this.setFieldsEvent.detail = ((Integer) DETAIL_VALUES [i + 1]).intValue();
							break;
						}
					}
					this.setFieldsMask |= DETAIL;
				}
			}));
		}

		if ((fields & TEXT) != 0) {
			new Label (group, SWT.NONE).setText ("text");
			final Text textText = new Text (group, SWT.BORDER);
			if ((this.setFieldsMask & TEXT) != 0) {
        textText.setText(this.setFieldsEvent.text);
      }
			textText.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
			textText.addModifyListener(e -> {
				final String newValue = textText.getText();
				if (newValue.length() == 0) {
					this.setFieldsMask &= ~TEXT;
				} else {
					this.setFieldsEvent.type = eventType;
					this.setFieldsEvent.text = newValue;
					this.setFieldsMask |= TEXT;
				}
			});
		}

		if ((fields & X) != 0) {
			new Label (group, SWT.NONE).setText ("x");
			final Text xText = new Text (group, SWT.BORDER);
			if ((this.setFieldsMask & X) != 0) {
        xText.setText(Integer.toString(this.setFieldsEvent.x));
      }
			xText.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
			xText.addModifyListener(e -> {
				final String newValue = xText.getText ();
				try {
					final int newIntValue = Integer.parseInt (newValue);
					this.setFieldsEvent.type = eventType;
					this.setFieldsEvent.x = newIntValue;
					this.setFieldsMask |= X;
				} catch (final NumberFormatException ex) {
					this.setFieldsMask &= ~X;
				}
			});
		}

		if ((fields & Y) != 0) {
			new Label (group, SWT.NONE).setText ("y");
			final Text yText = new Text (group, SWT.BORDER);
			if ((this.setFieldsMask & Y) != 0) {
        yText.setText(Integer.toString(this.setFieldsEvent.y));
      }
			yText.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
			yText.addModifyListener(e -> {
				final String newValue = yText.getText ();
				try {
					final int newIntValue = Integer.parseInt (newValue);
					this.setFieldsEvent.type = eventType;
					this.setFieldsEvent.y = newIntValue;
					this.setFieldsMask |= Y;
				} catch (final NumberFormatException ex) {
					this.setFieldsMask &= ~Y;
				}
			});
		}

		if ((fields & WIDTH) != 0) {
			new Label (group, SWT.NONE).setText ("width");
			final Text widthText = new Text (group, SWT.BORDER);
			if ((this.setFieldsMask & WIDTH) != 0) {
        widthText.setText(Integer.toString(this.setFieldsEvent.width));
      }
			widthText.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
			widthText.addModifyListener(e -> {
				final String newValue = widthText.getText ();
				try {
					final int newIntValue = Integer.parseInt (newValue);
					this.setFieldsEvent.type = eventType;
					this.setFieldsEvent.width = newIntValue;
					this.setFieldsMask |= WIDTH;
				} catch (final NumberFormatException ex) {
					this.setFieldsMask &= ~WIDTH;
				}
			});
		}

		if ((fields & HEIGHT) != 0) {
			new Label (group, SWT.NONE).setText ("height");
			final Text heightText = new Text (group, SWT.BORDER);
			if ((this.setFieldsMask & HEIGHT) != 0) {
        heightText.setText(Integer.toString(this.setFieldsEvent.height));
      }
			heightText.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false));
			heightText.addModifyListener(e -> {
				final String newValue = heightText.getText ();
				try {
					final int newIntValue = Integer.parseInt (newValue);
					this.setFieldsEvent.type = eventType;
					this.setFieldsEvent.height = newIntValue;
					this.setFieldsMask |= HEIGHT;
				} catch (final NumberFormatException ex) {
					this.setFieldsMask &= ~HEIGHT;
				}
			});
		}

		final Button ok = new Button (dialog, SWT.PUSH);
		ok.setText (ControlExample.getResourceString("OK"));
		final GridData data = new GridData (70, SWT.DEFAULT);
		data.horizontalAlignment = SWT.RIGHT;
		ok.setLayoutData (data);
		ok.addSelectionListener (widgetSelectedAdapter(e -> {
			this.EVENT_INFO[index].setFields = this.setFieldsMask;
			this.EVENT_INFO[index].event = this.setFieldsEvent;
			dialog.dispose();
		}));

		dialog.setDefaultButton(ok);
		dialog.pack();
		dialog.setLocation(x, y);
		dialog.open();
	}

	/**
	 * Create the event console popup menu.
	 */
	void createEventConsolePopup () {
		final Menu popup = new Menu (this.shell, SWT.POP_UP);
		this.eventConsole.setMenu (popup);

		final MenuItem cut = new MenuItem (popup, SWT.PUSH);
		cut.setText (ControlExample.getResourceString("MenuItem_Cut"));
		cut.addListener (SWT.Selection, event -> this.eventConsole.cut ());
		final MenuItem copy = new MenuItem (popup, SWT.PUSH);
		copy.setText (ControlExample.getResourceString("MenuItem_Copy"));
		copy.addListener (SWT.Selection, event -> this.eventConsole.copy ());
		final MenuItem paste = new MenuItem (popup, SWT.PUSH);
		paste.setText (ControlExample.getResourceString("MenuItem_Paste"));
		paste.addListener (SWT.Selection, event -> this.eventConsole.paste ());
		new MenuItem (popup, SWT.SEPARATOR);
		final MenuItem selectAll = new MenuItem (popup, SWT.PUSH);
		selectAll.setText(ControlExample.getResourceString("MenuItem_SelectAll"));
		selectAll.addListener (SWT.Selection, event -> this.eventConsole.selectAll ());
	}

	/**
	 * Creates the "Example" group.  The "Example" group
	 * is typically the left hand column in the tab.
	 */
	void createExampleGroup () {
		this.exampleGroup = new Group (this.tabFolderPage, SWT.NONE);
		this.exampleGroup.setLayout (new GridLayout ());
		this.exampleGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
	}

	/**
	 * Creates the "Example" widget children of the "Example" group.
	 * Subclasses override this method to create the particular
	 * example control.
	 */
	void createExampleWidgets () {
		/* Do nothing */
	}

	/**
	 * Creates and opens the "Listener selection" dialog.
	 */
	void createListenerSelectionDialog () {
		final Shell dialog = new Shell (this.shell, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		dialog.setText (ControlExample.getResourceString ("Select_Listeners"));
		dialog.setLayout (new GridLayout (2, false));
		final Table table = new Table (dialog, SWT.BORDER | SWT.V_SCROLL | SWT.CHECK);
		final GridData data = new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 3;
		table.setLayoutData(data);
		for (int i = 0; i < this.EVENT_INFO.length; i++) {
			final TableItem item = new TableItem (table, SWT.NONE);
			item.setText (this.EVENT_INFO[i].name);
			item.setChecked (this.eventsFilter[i]);
		}
		final String [] customNames = this.getCustomEventNames ();
		for (int i = 0; i < customNames.length; i++) {
			final TableItem item = new TableItem (table, SWT.NONE);
			item.setText (customNames[i]);
			item.setChecked (this.eventsFilter[this.EVENT_INFO.length + i]);
		}
		final Button selectAll = new Button (dialog, SWT.PUSH);
		selectAll.setText(ControlExample.getResourceString ("Select_All"));
		selectAll.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		selectAll.addSelectionListener (widgetSelectedAdapter(e -> {
			final TableItem [] items = table.getItems();
			for (int i = 0; i < this.EVENT_INFO.length; i++) {
				items[i].setChecked(true);
			}
			for (int i = 0; i < customNames.length; i++) {
				items[this.EVENT_INFO.length + i].setChecked(true);
			}
		}));
		final Button deselectAll = new Button (dialog, SWT.PUSH);
		deselectAll.setText(ControlExample.getResourceString ("Deselect_All"));
		deselectAll.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		deselectAll.addSelectionListener (widgetSelectedAdapter(e -> {
			final TableItem [] items = table.getItems();
			for (int i = 0; i < this.EVENT_INFO.length; i++) {
				items[i].setChecked(false);
			}
			for (int i = 0; i < customNames.length; i++) {
				items[this.EVENT_INFO.length + i].setChecked(false);
			}
		}));
		final Button editEvent = new Button (dialog, SWT.PUSH);
		editEvent.setText (ControlExample.getResourceString ("Edit_Event"));
		editEvent.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		editEvent.addSelectionListener (widgetSelectedAdapter(e -> {
			Point pt = editEvent.getLocation();
			pt = e.display.map(editEvent, null, pt);
			final int index = table.getSelectionIndex();
			if ((this.getExampleWidgets().length > 0) && (index != -1)) {
				this.createEditEventDialog(dialog, pt.x, pt.y, index);
			}
		}));
		editEvent.setEnabled(false);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				int fields = 0;
				final int index = table.getSelectionIndex();
				if ((index != -1) && (index < Tab.this.EVENT_INFO.length)) {  // TODO: Allow custom widgets to specify event info
					fields = (Tab.this.EVENT_INFO[index].settableFields);
				}
				editEvent.setEnabled(fields != 0);
			}
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				if (editEvent.getEnabled()) {
					Point pt = editEvent.getLocation();
					pt = e.display.map(editEvent, null, pt);
					final int index = table.getSelectionIndex();
					if ((Tab.this.getExampleWidgets().length > 0) && (index != -1) && (index < Tab.this.EVENT_INFO.length)) {
						Tab.this.createEditEventDialog(dialog, pt.x, pt.y, index);
					}
				}
			}
		});

		new Label(dialog, SWT.NONE); /* Filler */
		final Button ok = new Button (dialog, SWT.PUSH);
		ok.setText(ControlExample.getResourceString ("OK"));
		dialog.setDefaultButton(ok);
		ok.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		ok.addSelectionListener (widgetSelectedAdapter(e -> {
			final TableItem [] items = table.getItems();
			for (int i = 0; i < this.EVENT_INFO.length; i++) {
				this.eventsFilter[i] = items[i].getChecked();
			}
			for (int i = 0; i < customNames.length; i++) {
				this.eventsFilter[this.EVENT_INFO.length + i] = items[this.EVENT_INFO.length + i].getChecked();
			}
			dialog.dispose();
		}));
		dialog.pack ();
		/*
		 * If the preferred size of the dialog is too tall for the display,
		 * then reduce the height, so that the vertical scrollbar will appear.
		 */
		final Rectangle bounds = dialog.getBounds();
		final Rectangle trim = dialog.computeTrim(0, 0, 0, 0);
		final Rectangle clientArea = this.display.getClientArea();
		if (bounds.height > clientArea.height) {
			dialog.setSize(bounds.width, clientArea.height - trim.height);
		}
		dialog.setLocation(bounds.x, clientArea.y);
		dialog.open ();
		while (! dialog.isDisposed()) {
			if (! this.display.readAndDispatch()) {
        this.display.sleep();
      }
		}
	}

	/**
	 * Creates the "Listeners" group.  The "Listeners" group
	 * goes below the "Example" and "Control" groups.
	 */
	void createListenersGroup () {
		this.listenersGroup = new Group (this.tabFolderPage, SWT.NONE);
		this.listenersGroup.setLayout (new GridLayout (4, false));
		this.listenersGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true, 2, 1));
		this.listenersGroup.setText (ControlExample.getResourceString ("Listeners"));

		/*
		 * Create the button to access the 'Listeners' dialog.
		 */
		final Button listenersButton = new Button (this.listenersGroup, SWT.PUSH);
		listenersButton.setText (ControlExample.getResourceString ("Select_Listeners"));
		listenersButton.addSelectionListener (widgetSelectedAdapter(e -> {
			this.createListenerSelectionDialog ();
			this.recreateExampleWidgets ();
		}));

		/*
		 * Create the checkbox to specify whether typed or untyped events are displayed in the log.
		 */
		final Button untypedEventsCheckbox = new Button (this.listenersGroup, SWT.CHECK);
		untypedEventsCheckbox.setText (ControlExample.getResourceString ("UntypedEvents"));
		untypedEventsCheckbox.addSelectionListener (widgetSelectedAdapter(e -> this.untypedEvents = untypedEventsCheckbox.getSelection ()));

		/*
		 * Create the checkbox to add/remove listeners to/from the example widgets.
		 */
		final Button listenCheckbox = new Button (this.listenersGroup, SWT.CHECK);
		listenCheckbox.setText (ControlExample.getResourceString ("Listen"));
		listenCheckbox.addSelectionListener (widgetSelectedAdapter(e -> {
			this.logging = listenCheckbox.getSelection ();
			this.recreateExampleWidgets ();
		}));

		/*
		 * Create the button to clear the text.
		 */
		final Button clearButton = new Button (this.listenersGroup, SWT.PUSH);
		clearButton.setText (ControlExample.getResourceString ("Clear"));
		clearButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		clearButton.addSelectionListener (widgetSelectedAdapter(e -> this.eventConsole.setText ("")));

		/* Initialize the eventsFilter to log all events. */
		final int customEventCount = this.getCustomEventNames ().length;
		this.eventsFilter = new boolean [this.EVENT_INFO.length + customEventCount];
		for (int i = 0; i < (this.EVENT_INFO.length + customEventCount); i++) {
			this.eventsFilter [i] = true;
		}

		/* Create the event console Text. */
		this.eventConsole = new Text (this.listenersGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		final GridData data = new GridData (GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		data.heightHint = 80;
		this.eventConsole.setLayoutData (data);
		this.createEventConsolePopup ();
		this.eventConsole.addKeyListener (new KeyAdapter () {
			@Override
			public void keyPressed (final KeyEvent e) {
				if (((e.keyCode == 'A') || (e.keyCode == 'a')) && ((e.stateMask & SWT.MOD1) != 0)) {
					Tab.this.eventConsole.selectAll ();
					e.doit = false;
				}
			}
		});
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	String[] getMethodNames() {
		return null;
	}

	Shell createSetGetDialog(final String[] methodNames) {
		final Shell dialog = new Shell(this.shell, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MODELESS);
		dialog.setLayout(new GridLayout(2, false));
		dialog.setText(this.getTabText() + " " + ControlExample.getResourceString ("Set_Get"));
		this.nameCombo = new Combo(dialog, SWT.READ_ONLY);
		this.nameCombo.setItems(methodNames);
		this.nameCombo.setText(methodNames[0]);
		this.nameCombo.setVisibleItemCount(methodNames.length);
		this.nameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		this.nameCombo.addSelectionListener(widgetSelectedAdapter(e -> this.resetLabels()));
		this.returnTypeLabel = new Label(dialog, SWT.NONE);
		this.returnTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		this.setButton = new Button(dialog, SWT.PUSH);
		this.setButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		this.setButton.addSelectionListener(widgetSelectedAdapter(e -> {
			this.setValue();
			this.setText.selectAll();
			this.setText.setFocus();
		}));
		this.setText = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		this.setText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		this.getButton = new Button(dialog, SWT.PUSH);
		this.getButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		this.getButton.addSelectionListener(widgetSelectedAdapter(e -> this.getValue()));
		this.getText = new Text(dialog, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 240;
		data.heightHint = 200;
		this.getText.setLayoutData(data);
		this.resetLabels();
		dialog.setDefaultButton(this.setButton);
		dialog.pack();
		dialog.addDisposeListener(e -> this.setGetDialog = null);
		return dialog;
	}

	void resetLabels() {
		final String methodRoot = this.nameCombo.getText();
		this.returnTypeLabel.setText(this.parameterInfo(methodRoot));
		this.setButton.setText(this.setMethodName(methodRoot));
		this.getButton.setText("get" + methodRoot);
		this.setText.setText("");
		this.getText.setText("");
		this.getValue();
		this.setText.setFocus();
	}

	String setMethodName(final String methodRoot) {
		return "set" + methodRoot;
	}

	String parameterInfo(final String methodRoot) {
		String typeName = null;
		final Class<?> returnType = this.getReturnType(methodRoot);
		final boolean isArray = returnType.isArray();
		if (isArray) {
			typeName = returnType.getComponentType().getName();
		} else {
			typeName = returnType.getName();
		}
		String typeNameString = typeName;
		final int index = typeName.lastIndexOf('.');
		if ((index != -1) && ((index+1) < typeName.length())) {
      typeNameString = typeName.substring(index+1);
    }
		final String info = ControlExample.getResourceString("Info_" + typeNameString + (isArray ? "A" : ""));
		if (isArray) {
			typeNameString += "[]";
		}
		return ControlExample.getResourceString("Parameter_Info", new Object[] {typeNameString, info});
	}

	void getValue() {
		final String methodName = "get" + this.nameCombo.getText();
		this.getText.setText("");
		final Widget[] widgets = this.getExampleWidgets();
		for (int i = 0; i < widgets.length; i++) {
			try {
				if (widgets[i] == null) {
					continue;
				}
				final Method method = widgets[i].getClass().getMethod(methodName);
				final Object result = method.invoke(widgets[i]);
				if (result == null) {
					this.getText.append("null");
				} else if (result.getClass().isArray()) {
					final int length = java.lang.reflect.Array.getLength(result);
					if (length == 0) {
						this.getText.append(result.getClass().getComponentType() + "[0]");
					}
					for (int j = 0; j < length; j++) {
						this.getText.append(java.lang.reflect.Array.get(result,j).toString() + "\n");
					}
				} else {
					this.getText.append(result.toString());
				}
			} catch (final Exception e) {
				this.getText.append(e.toString());
			}
			if ((i + 1) < widgets.length) {
				this.getText.append("\n\n");
			}
		}
	}

	Class<?> getReturnType(final String methodRoot) {
		Class<?> returnType = null;
		final String methodName = "get" + methodRoot;
		final Widget[] widgets = this.getExampleWidgets();
		try {
			final Method method = widgets[0].getClass().getMethod(methodName);
			returnType = method.getReturnType();
		} catch (final Exception e) {
		}
		return returnType;
	}

	void setValue() {
		/* The parameter type must be the same as the get method's return type */
		final String methodRoot = this.nameCombo.getText();
		final Class<?> returnType = this.getReturnType(methodRoot);
		final String methodName = this.setMethodName(methodRoot);
		final String value = this.setText.getText();
		final Widget[] widgets = this.getExampleWidgets();
		for (final Widget widget : widgets) {
			try {
				if (widget == null) {
					continue;
				}
				final java.lang.reflect.Method method = widget.getClass().getMethod(methodName, returnType);
				final String typeName = returnType.getName();
				Object[] parameter = null;
				if (value.equals("null")) {
					parameter = new Object[] {null};
				} else if (typeName.equals("int")) {
					parameter = new Object[] {Integer.valueOf(value)};
				} else if (typeName.equals("long")) {
					parameter = new Object[] {Long.valueOf(value)};
				} else if (typeName.equals("char")) {
					parameter = new Object[] {value.length() == 1 ? Character.valueOf(value.charAt(0)) : Character.valueOf('\0')};
				} else if (typeName.equals("boolean")) {
					parameter = new Object[] {Boolean.valueOf(value)};
				} else if (typeName.equals("java.lang.String")) {
					parameter = new Object[] {value};
				} else if (typeName.equals("org.eclipse.swt.graphics.Point")) {
					final String xy[] = this.split(value, ',');
					parameter = new Object[] {new Point(Integer.parseInt(xy[0]),Integer.parseInt(xy[1]))};
				} else if (typeName.equals("org.eclipse.swt.graphics.Rectangle")) {
					final String xywh[] = this.split(value, ',');
					parameter = new Object[] {new Rectangle(Integer.parseInt(xywh[0]),Integer.parseInt(xywh[1]),Integer.parseInt(xywh[2]),Integer.parseInt(xywh[3]))};
				} else if (typeName.equals("[I")) {
					final String strings[] = this.split(value, ',');
					final int[] ints = new int[strings.length];
					for (int j = 0; j < strings.length; j++) {
						ints[j] = Integer.parseInt(strings[j]);
					}
					parameter = new Object[] {ints};
				} else if (typeName.equals("[C")) {
					final String strings[] = this.split(value, ',');
					final char[] chars = new char[strings.length];
					for (int j = 0; j < strings.length; j++) {
						chars[j] = strings[j].charAt(0);
					}
					parameter = new Object[] {chars};
				} else if (typeName.equals("[Ljava.lang.String;")) {
					parameter = new Object[] {this.split(value, ',')};
				} else {
					parameter = this.parameterForType(typeName, value, widget);
				}
				method.invoke(widget, parameter);
			} catch (final Exception e) {
				final Throwable cause = e.getCause();
				final String message = e.getMessage();
				this.getText.setText(e.toString());
				if (cause != null) {
          this.getText.append(", cause=\n" + cause.toString());
        }
				if (message != null) {
          this.getText.append(", message=\n" + message);
        }
			}
		}
	}

	Object[] parameterForType(final String typeName, final String value, final Widget widget) {
		return new Object[] {value};
	}

	void createOrientationGroup () {
		/* Create Orientation group*/
		this.orientationGroup = new Group (this.controlGroup, SWT.NONE);
		this.orientationGroup.setLayout (new GridLayout());
		this.orientationGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.orientationGroup.setText (ControlExample.getResourceString("Orientation"));
		this.defaultOrietationButton = new Button (this.orientationGroup, SWT.RADIO);
		this.defaultOrietationButton.setText (ControlExample.getResourceString("Default"));
		this.defaultOrietationButton.setSelection (true);
		this.ltrButton = new Button (this.orientationGroup, SWT.RADIO);
		this.ltrButton.setText ("SWT.LEFT_TO_RIGHT");
		this.rtlButton = new Button (this.orientationGroup, SWT.RADIO);
		this.rtlButton.setText ("SWT.RIGHT_TO_LEFT");
	}

	void createDirectionGroup () {
		/* Create Text Direction group*/
		this.directionGroup = new Group (this.controlGroup, SWT.NONE);
		this.directionGroup.setLayout (new GridLayout());
		this.directionGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.directionGroup.setText (ControlExample.getResourceString("Text_Direction"));
		this.defaultDirectionButton = new Button (this.directionGroup, SWT.RADIO);
		this.defaultDirectionButton.setText (ControlExample.getResourceString("Default"));
		this.defaultDirectionButton.setSelection (true);
		this.ltrDirectionButton = new Button (this.directionGroup, SWT.RADIO);
		this.ltrDirectionButton.setText ("SWT.LEFT_TO_RIGHT");
		this.rtlDirectionButton = new Button (this.directionGroup, SWT.RADIO);
		this.rtlDirectionButton.setText ("SWT.RIGHT_TO_LEFT");
		this.autoDirectionButton = new Button (this.directionGroup, SWT.RADIO);
		this.autoDirectionButton.setText ("AUTO direction");
	}

	/**
	 * Creates the "Size" group.  The "Size" group contains
	 * controls that allow the user to change the size of
	 * the example widgets.
	 */
	void createSizeGroup () {
		/* Create the group */
		this.sizeGroup = new Group (this.controlGroup, SWT.NONE);
		this.sizeGroup.setLayout (new GridLayout());
		this.sizeGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.sizeGroup.setText (ControlExample.getResourceString("Size"));

		/* Create the controls */

		/*
		 * The preferred size of a widget is the size returned
		 * by widget.computeSize (SWT.DEFAULT, SWT.DEFAULT).
		 * This size is defined on a widget by widget basis.
		 * Many widgets will attempt to display their contents.
		 */
		this.preferredButton = new Button (this.sizeGroup, SWT.RADIO);
		this.preferredButton.setText (ControlExample.getResourceString("Preferred"));
		this.tooSmallButton = new Button (this.sizeGroup, SWT.RADIO);
		this.tooSmallButton.setText (TOO_SMALL_SIZE + " X " + TOO_SMALL_SIZE);
		this.smallButton = new Button(this.sizeGroup, SWT.RADIO);
		this.smallButton.setText (SMALL_SIZE + " X " + SMALL_SIZE);
		this.largeButton = new Button (this.sizeGroup, SWT.RADIO);
		this.largeButton.setText (LARGE_SIZE + " X " + LARGE_SIZE);
		this.rectangleButton = new Button (this.sizeGroup, SWT.RADIO);
		this.rectangleButton.setText (RETANGLE_SIZE_WIDTH + " X " + RETANGLE_SIZE_HEIGHT);
		this.fillHButton = new Button (this.sizeGroup, SWT.CHECK);
		this.fillHButton.setText (ControlExample.getResourceString("Fill_X"));
		this.fillVButton = new Button (this.sizeGroup, SWT.CHECK);
		this.fillVButton.setText (ControlExample.getResourceString("Fill_Y"));

		/* Add the listeners */
		final SelectionListener selectionListener = widgetSelectedAdapter(event -> this.setExampleWidgetSize ());
		this.preferredButton.addSelectionListener(selectionListener);
		this.tooSmallButton.addSelectionListener(selectionListener);
		this.smallButton.addSelectionListener(selectionListener);
		this.largeButton.addSelectionListener(selectionListener);
		this.rectangleButton.addSelectionListener(selectionListener);
		this.fillHButton.addSelectionListener(selectionListener);
		this.fillVButton.addSelectionListener(selectionListener);

		/* Set the default state */
		this.preferredButton.setSelection (true);
	}

	/**
	 * Creates the "Style" group.  The "Style" group contains
	 * controls that allow the user to change the style of
	 * the example widgets.  Changing a widget "Style" causes
	 * the widget to be destroyed and recreated.
	 */
	void createStyleGroup () {
		this.styleGroup = new Group (this.controlGroup, SWT.NONE);
		this.styleGroup.setLayout (new GridLayout ());
		this.styleGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		this.styleGroup.setText (ControlExample.getResourceString("Styles"));
	}

	/**
	 * Creates the tab folder page.
	 *
	 * @param tabFolder org.eclipse.swt.widgets.TabFolder
	 * @return the new page for the tab folder
	 */
	Composite createTabFolderPage (final TabFolder tabFolder) {
		/* Cache the shell and display. */
		this.shell = tabFolder.getShell ();
		this.display = this.shell.getDisplay ();

		/* Create a two column page. */
		this.tabFolderPage = new Composite (tabFolder, SWT.NONE);
		this.tabFolderPage.setLayout (new GridLayout (2, false));

		/* Create the "Example" and "Control" groups. */
		this.createExampleGroup ();
		this.createControlGroup ();

		/* Create the "Listeners" group under the "Control" group. */
		this.createListenersGroup ();

		/* Create and initialize the example and control widgets. */
		this.createExampleWidgets ();
		this.hookExampleWidgetListeners ();
		this.createControlWidgets ();
		this.setExampleWidgetState ();

		return this.tabFolderPage;
	}

	void setExampleWidgetPopupMenu() {
		final Control[] controls = this.getExampleControls();
		for (final Control control : controls) {
			control.addListener(SWT.MenuDetect, event -> {
				Menu menu = control.getMenu();
				if ((menu != null) && this.samplePopup) {
					menu.dispose();
					menu = null;
				}
				if ((menu == null) && this.popupMenuButton.getSelection()) {
					menu = new Menu(this.shell, SWT.POP_UP | (control.getStyle() & (SWT.RIGHT_TO_LEFT | SWT.LEFT_TO_RIGHT)));
					final MenuItem item = new MenuItem(menu, SWT.PUSH);
					item.setText("Sample popup menu item");
					this.specialPopupMenuItems(menu, event);
					control.setMenu(menu);
					this.samplePopup = true;
				}
			});
		}
	}

	protected void specialPopupMenuItems(final Menu menu, final Event event) {
	}

	/**
	 * Disposes the "Example" widgets.
	 */
	void disposeExampleWidgets () {
		final Widget [] widgets = this.getExampleWidgets ();
		for (final Widget widget : widgets) {
			widget.dispose ();
		}
	}

	Image colorImage (final Color color) {
		final Image image = new Image (this.display, IMAGE_SIZE, IMAGE_SIZE);
		final GC gc = new GC(image);
		gc.setBackground(color);
		final Rectangle bounds = image.getBounds();
		gc.fillRectangle(0, 0, bounds.width, bounds.height);
		gc.setBackground(this.display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
		gc.dispose();
		return image;
	}

	Image fontImage (final Font font) {
		final Image image = new Image (this.display, IMAGE_SIZE, IMAGE_SIZE);
		final GC gc = new GC(image);
		final Rectangle bounds = image.getBounds();
		gc.setBackground(this.display.getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle(0, 0, bounds.width, bounds.height);
		gc.setBackground(this.display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
		final FontData data[] = font.getFontData();
		final int style = data[0].getStyle();
		switch (style) {
		case SWT.NORMAL:
			gc.drawLine(3, 3, 3, 8);
			gc.drawLine(4, 3, 7, 8);
			gc.drawLine(8, 3, 8, 8);
			break;
		case SWT.BOLD:
			gc.drawLine(3, 2, 3, 9);
			gc.drawLine(4, 2, 4, 9);
			gc.drawLine(5, 2, 7, 2);
			gc.drawLine(5, 3, 8, 3);
			gc.drawLine(5, 5, 7, 5);
			gc.drawLine(5, 6, 7, 6);
			gc.drawLine(5, 8, 8, 8);
			gc.drawLine(5, 9, 7, 9);
			gc.drawLine(7, 4, 8, 4);
			gc.drawLine(7, 7, 8, 7);
			break;
		case SWT.ITALIC:
			gc.drawLine(6, 2, 8, 2);
			gc.drawLine(7, 3, 4, 8);
			gc.drawLine(3, 9, 5, 9);
			break;
		case SWT.BOLD | SWT.ITALIC:
			gc.drawLine(5, 2, 8, 2);
			gc.drawLine(5, 3, 8, 3);
			gc.drawLine(6, 4, 4, 7);
			gc.drawLine(7, 4, 5, 7);
			gc.drawLine(3, 8, 6, 8);
			gc.drawLine(3, 9, 6, 9);
			break;
		}
		gc.dispose();
		return image;
	}

	/**
	 * Gets the list of custom event names.
	 * Subclasses override this method to allow adding of custom events.
	 *
	 * @return an array containing custom event names
	 * @see hookCustomListener
	 */
	String [] getCustomEventNames () {
		return new String [0];
	}

	/**
	 * Gets the default style for a widget
	 *
	 * @return the default style bit
	 */
	int getDefaultStyle () {
		if ((this.ltrButton != null) && this.ltrButton.getSelection()) {
			return SWT.LEFT_TO_RIGHT;
		}
		if ((this.rtlButton != null) && this.rtlButton.getSelection()) {
			return SWT.RIGHT_TO_LEFT;
		}
		return SWT.NONE;
	}

	/**
	 * Gets the "Example" widgets.
	 *
	 * @return an array containing the example widgets
	 */
	Widget [] getExampleWidgets () {
		return new Widget [0];
	}

	/**
	 * Gets the "Example" controls.
	 * This is the subset of "Example" widgets that are controls.
	 *
	 * @return an array containing the example controls
	 */
	Control [] getExampleControls () {
		final Widget [] widgets = this.getExampleWidgets ();
		Control [] controls = new Control [0];
		for (final Widget widget : widgets) {
			if (widget instanceof Control) {
				final Control[] newControls = new Control[controls.length + 1];
				System.arraycopy(controls, 0, newControls, 0, controls.length);
				controls = newControls;
				controls[controls.length - 1] = (Control)widget;
			}
		}
		return controls;
	}

	/**
	 * Gets the "Example" widget's items, if any.
	 *
	 * @return an array containing the example widget's items
	 */
	Item [] getExampleWidgetItems () {
		return new Item [0];
	}

	/**
	 * Gets the short text for the tab folder item.
	 *
	 * @return the short text for the tab item
	 */
	String getShortTabText() {
		return this.getTabText();
	}

	/**
	 * Gets the text for the tab folder item.
	 *
	 * @return the text for the tab item
	 */
	String getTabText () {
		return "";
	}

	/**
	 * In case one of the buttons that control text direction was selected,
	 * apply the text direction on the controls in the client area.
	 *
	 * @return false iff example widgets must be re-created
	 */
	boolean handleTextDirection (final Widget widget) {
		if (!this.rtlSupport ()) {
			return false;
		}
		int textDirection = SWT.NONE;
		if (this.ltrDirectionButton.equals (widget)) {
			textDirection = SWT.LEFT_TO_RIGHT;
		} else if (this.rtlDirectionButton.equals (widget)) {
			textDirection = SWT.RIGHT_TO_LEFT;
		} else if (this.autoDirectionButton.equals (widget)) {
			textDirection = SWT.AUTO_TEXT_DIRECTION;
		} else if (!this.defaultDirectionButton.equals (widget)) {
			return false;
		}
		final Control [] children = this.getExampleControls ();
		if (children.length > 0) {
			if (SWT.NONE == textDirection) {
				textDirection = children [0].getOrientation ();
			}
			for (final Control child : children) {
				child.setTextDirection (textDirection);
			}
		}
		return true;
	}

	/**
	 * Hooks all listeners to all example controls
	 * and example control items.
	 */
	void hookExampleWidgetListeners () {
		if (this.logging) {
			final Widget[] widgets = this.getExampleWidgets ();
			for (final Widget widget : widgets) {
				this.hookListeners (widget);
			}
			final Item[] exampleItems = this.getExampleWidgetItems ();
			for (final Item exampleItem : exampleItems) {
				this.hookListeners (exampleItem);
			}
			final String [] customNames = this.getCustomEventNames ();
			for (int i = 0; i < customNames.length; i++) {
				if (this.eventsFilter [this.EVENT_INFO.length + i]) {
					this.hookCustomListener (customNames[i]);
				}
			}
		}
	}

	/**
	 * Hooks the custom listener specified by eventName.
	 * Subclasses override this method to add custom listeners.
	 * @see getCustomEventNames
	 */
	void hookCustomListener (final String eventName) {
	}

	/**
	 * Hooks all listeners to the specified widget.
	 */
	void hookListeners (final Widget widget) {
		if (this.logging) {
			final Listener listener = event -> this.log (event);
			for (int i = 0; i < this.EVENT_INFO.length; i++) {
				if (this.eventsFilter [i]) {
					widget.addListener (this.EVENT_INFO[i].type, listener);
				}
			}
		}
	}

	/**
	 * Logs an untyped event to the event console.
	 */
	void log(final Event event) {
		int i = 0;
		while (i < this.EVENT_INFO.length) {
			if (this.EVENT_INFO[i].type == event.type) {
        break;
      }
			i++;
		}
		StringBuilder toString = new StringBuilder().append(this.EVENT_INFO[i].name).append(" [")
        .append(event.type).append("]: ");
		if (!this.untypedEvents) {
			switch (event.type) {
				case SWT.KeyDown:
				case SWT.KeyUp: toString.append(new KeyEvent (event).toString ()); break;
				case SWT.MouseDown:
				case SWT.MouseUp:
				case SWT.MouseMove:
				case SWT.MouseEnter:
				case SWT.MouseExit:
				case SWT.MouseDoubleClick:
				case SWT.MouseWheel:
				case SWT.MouseHover: toString.append(new MouseEvent (event).toString ()); break;
				case SWT.Paint: toString.append(new PaintEvent (event).toString ()); break;
				case SWT.Move:
				case SWT.Resize: toString.append(new ControlEvent (event).toString ()); break;
				case SWT.Dispose: toString.append(new DisposeEvent (event).toString ()); break;
				case SWT.Selection:
				case SWT.DefaultSelection: toString.append(new SelectionEvent (event).toString ()); break;
				case SWT.FocusIn:
				case SWT.FocusOut: toString.append(new FocusEvent (event).toString ()); break;
				case SWT.Expand:
				case SWT.Collapse: toString.append(new TreeEvent (event).toString ()); break;
				case SWT.Iconify:
				case SWT.Deiconify:
				case SWT.Close:
				case SWT.Activate:
				case SWT.Deactivate: toString.append(new ShellEvent (event).toString ()); break;
				case SWT.Show:
				case SWT.Hide: toString.append((event.widget instanceof Menu) ? new MenuEvent (event).toString () : event.toString()); break;
				case SWT.Modify: toString.append(new ModifyEvent (event).toString ()); break;
				case SWT.Verify: toString.append(new VerifyEvent (event).toString ()); break;
				case SWT.Help: toString.append(new HelpEvent (event).toString ()); break;
				case SWT.Arm: toString.append(new ArmEvent (event).toString ()); break;
				case SWT.Traverse: toString.append(new TraverseEvent (event).toString ()); break;
				case SWT.HardKeyDown:
				case SWT.HardKeyUp:
				case SWT.DragDetect:
				case SWT.MenuDetect:
				case SWT.SetData:
				default: toString.append(event.toString ());
			}
		} else {
			toString.append(event.toString());
		}
		this.log (toString.toString());

		/* Return values for event fields. */
		final int mask = this.EVENT_INFO[i].setFields;
		if (!this.ignore && (mask != 0)) {
			final Event setFieldsEvent = this.EVENT_INFO[i].event;
			if ((mask & DOIT) != 0) {
        event.doit = setFieldsEvent.doit;
      }
			if ((mask & DETAIL) != 0) {
        event.detail = setFieldsEvent.detail;
      }
			if ((mask & TEXT) != 0) {
        event.text = setFieldsEvent.text;
      }
			if ((mask & X) != 0) {
        event.x = setFieldsEvent.x;
      }
			if ((mask & Y) != 0) {
        event.y = setFieldsEvent.y;
      }
			if ((mask & WIDTH) != 0) {
        event.width = setFieldsEvent.width;
      }
			if ((mask & HEIGHT) != 0) {
        event.height = setFieldsEvent.height;
      }
			this.eventConsole.append (ControlExample.getResourceString("Returning"));
			this.ignore = true;
			this.log (event);
			this.ignore = false;
		}
	}

	/**
	 * Logs a string to the event console.
	 */
	void log (final String string) {
		if (!this.eventConsole.isDisposed()) {
			this.eventConsole.append (string);
			this.eventConsole.append ("\n");
		}
	}

	/**
	 * Logs a typed event to the event console.
	 */
	void log (final String eventName, final TypedEvent event) {
		this.log (eventName + ": " + event.toString ());
	}

	/**
	 * Recreates the "Example" widgets.
	 */
	void recreateExampleWidgets () {
		this.disposeExampleWidgets ();
		this.createExampleWidgets ();
		this.hookExampleWidgetListeners ();
		this.setExampleWidgetState ();
	}

	/**
	 * Sets the foreground color, background color, and font
	 * of the "Example" widgets to their default settings.
	 * Subclasses may extend in order to reset other colors
	 * and fonts to default settings as well.
	 */
	void resetColorsAndFonts () {
		this.foregroundColor = null;
		this.setExampleWidgetForeground ();
		this.backgroundColor = null;
		this.setExampleWidgetBackground ();
		final Font oldFont = this.font;
		this.font = null;
		this.setExampleWidgetFont ();
		this.setExampleWidgetSize ();
		if (oldFont != null) {
      oldFont.dispose();
    }
	}

	boolean rtlSupport() {
		return RTL_SUPPORT_ENABLE;
	}

	/**
	 * Sets the background color of the "Example" widgets' parent.
	 */
	void setExampleGroupBackgroundColor () {
		if (this.backgroundModeGroup == null) {
      return;
    }
		this.exampleGroup.setBackground (this.backgroundModeColorButton.getSelection () ? this.display.getSystemColor(SWT.COLOR_BLUE) : null);
	}
	/**
	 * Sets the background image of the "Example" widgets' parent.
	 */
	void setExampleGroupBackgroundImage () {
		if (this.backgroundModeGroup == null) {
      return;
    }
		this.exampleGroup.setBackgroundImage (this.backgroundModeImageButton.getSelection () ? this.instance.images[ControlExample.ciParentBackground] : null);
	}

	/**
	 * Sets the background mode of the "Example" widgets' parent.
	 */
	void setExampleGroupBackgroundMode () {
		if (this.backgroundModeGroup == null) {
      return;
    }
		final String modeString = this.backgroundModeCombo.getText ();
		int mode = SWT.INHERIT_NONE;
		if (modeString.equals("SWT.INHERIT_DEFAULT")) {
      mode = SWT.INHERIT_DEFAULT;
    }
		if (modeString.equals("SWT.INHERIT_FORCE")) {
      mode = SWT.INHERIT_FORCE;
    }
		this.exampleGroup.setBackgroundMode (mode);
	}

	/**
	 * Sets the background color of the "Example" widgets.
	 */
	void setExampleWidgetBackground () {
		if (this.colorAndFontTable == null)
     {
      return; // user cannot change color/font on this tab
    }
		final Control [] controls = this.getExampleControls ();
		if (!this.instance.startup) {
			for (final Control control : controls) {
				control.setBackground (this.backgroundColor);
			}
		}
		// Set the background color item's image to match the background color of the example widget(s).
		Color color = this.backgroundColor;
		if (controls.length == 0) {
      return;
    }
		if (color == null) {
      color = controls [0].getBackground ();
    }
		final TableItem item = this.colorAndFontTable.getItem(BACKGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage (color));
	}

	/**
	 * Sets the enabled state of the "Example" widgets.
	 */
	void setExampleWidgetEnabled () {
		final Control [] controls = this.getExampleControls ();
		for (final Control control : controls) {
			control.setEnabled (this.enabledButton.getSelection ());
		}
	}

	/**
	 * Sets the font of the "Example" widgets.
	 */
	void setExampleWidgetFont () {
		if (this.colorAndFontTable == null)
     {
      return; // user cannot change color/font on this tab
    }
		final Control [] controls = this.getExampleControls ();
		if (!this.instance.startup) {
			for (final Control control : controls) {
				control.setFont(this.font);
			}
		}
		/* Set the font item's image and font to match the font of the example widget(s). */
		Font ft = this.font;
		if (controls.length == 0) {
      return;
    }
		if (ft == null) {
      ft = controls [0].getFont ();
    }
		final TableItem item = this.colorAndFontTable.getItem(FONT);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.fontImage (ft));
		item.setFont(ft);
		this.colorAndFontTable.layout ();
	}

	/**
	 * Sets the foreground color of the "Example" widgets.
	 */
	void setExampleWidgetForeground () {
		if (this.colorAndFontTable == null)
     {
      return; // user cannot change color/font on this tab
    }
		final Control [] controls = this.getExampleControls ();
		if (!this.instance.startup) {
			for (final Control control : controls) {
				control.setForeground (this.foregroundColor);
			}
		}
		/* Set the foreground color item's image to match the foreground color of the example widget(s). */
		Color color = this.foregroundColor;
		if (controls.length == 0) {
      return;
    }
		if (color == null) {
      color = controls [0].getForeground ();
    }
		final TableItem item = this.colorAndFontTable.getItem(FOREGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the size of the "Example" widgets.
	 */
	void setExampleWidgetSize () {
		int size = SWT.DEFAULT;
		if (this.preferredButton == null) {
      return;
    }
		if (this.preferredButton.getSelection()) {
      size = SWT.DEFAULT;
    }
		if (this.tooSmallButton.getSelection()) {
      size = TOO_SMALL_SIZE;
    }
		if (this.smallButton.getSelection()) {
      size = SMALL_SIZE;
    }
		if (this.largeButton.getSelection()) {
      size = LARGE_SIZE;
    }
		final Control [] controls = this.getExampleControls ();
		for (final Control control : controls) {
			GridData gridData = new GridData(size, size);
			if (this.rectangleButton.getSelection()) {
				gridData = new GridData(RETANGLE_SIZE_WIDTH, RETANGLE_SIZE_HEIGHT);
			}
			gridData.grabExcessHorizontalSpace = this.fillHButton.getSelection();
			gridData.grabExcessVerticalSpace = this.fillVButton.getSelection();
			gridData.horizontalAlignment = this.fillHButton.getSelection() ? SWT.FILL : SWT.LEFT;
			gridData.verticalAlignment = this.fillVButton.getSelection() ? SWT.FILL : SWT.TOP;
			control.setLayoutData (gridData);
		}
		this.tabFolderPage.layout (controls);
	}

	/**
	 * Sets the state of the "Example" widgets.  Subclasses
	 * may extend this method to set "Example" widget state
	 * that is specific to the widget.
	 */
	void setExampleWidgetState () {
		this.setExampleWidgetBackground ();
		this.setExampleWidgetForeground ();
		this.setExampleWidgetFont ();
		if (!this.instance.startup) {
			this.setExampleWidgetEnabled ();
			this.setExampleWidgetVisibility ();
			this.setExampleGroupBackgroundMode ();
			this.setExampleGroupBackgroundColor ();
			this.setExampleGroupBackgroundImage ();
			this.setExampleWidgetBackgroundImage ();
			this.setExampleWidgetPopupMenu ();
			this.setExampleWidgetSize ();
		}
		//TEMPORARY CODE
//		Control [] controls = getExampleControls ();
//		for (int i=0; i<controls.length; i++) {
//			log ("Control=" + controls [i] + ", border width=" + controls [i].getBorderWidth ());
//		}
	}

	/**
	 * Sets the visibility of the "Example" widgets.
	 */
	void setExampleWidgetVisibility () {
		final Control [] controls = this.getExampleControls ();
		for (final Control control : controls) {
			control.setVisible (this.visibleButton.getSelection ());
		}
	}

	/**
	 * Sets the background image of the "Example" widgets.
	 */
	void setExampleWidgetBackgroundImage () {
		if ((this.backgroundImageButton != null) && this.backgroundImageButton.isDisposed()) {
      return;
    }
		final Control [] controls = this.getExampleControls ();
		for (final Control control : controls) {
			control.setBackgroundImage (this.backgroundImageButton.getSelection () ? this.instance.images[ControlExample.ciBackground] : null);
		}
	}

	/**
	 * Splits the given string around matches of the given character.
	 *
	 * This subset of java.lang.String.split(String regex)
	 * uses only code that can be run on CLDC platforms.
	 */
	String [] split (final String string, final char ch) {
		String [] result = new String[0];
		int start = 0;
		final int length = string.length();
		while (start < length) {
			int end = string.indexOf(ch, start);
			if (end == -1) {
        end = length;
      }
			final String substr = string.substring(start, end);
			final String [] newResult = new String[result.length + 1];
			System.arraycopy(result, 0, newResult, 0, result.length);
			newResult [result.length] = substr;
			result = newResult;
			start = end + 1;
		}
		return result;
	}
}
