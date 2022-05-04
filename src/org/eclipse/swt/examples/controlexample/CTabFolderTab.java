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
package org.eclipse.swt.examples.controlexample;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

class CTabFolderTab extends Tab {
	int lastSelectedTab = 0;

	/* Example widgets and groups that contain them */
	CTabFolder tabFolder1;
	Group tabFolderGroup, itemGroup, tabHeightGroup;

	/* Style widgets added to the "Style" group */
	Button topButton, bottomButton, flatButton, closeButton;
	Button rightButton, fillButton, wrapButton;

	static String [] CTabItems1 = {ControlExample.getResourceString("CTabItem1_0"),
								  ControlExample.getResourceString("CTabItem1_1"),
								  ControlExample.getResourceString("CTabItem1_2")};

	/* Controls and resources added to the "Fonts" group */
	static final int SELECTION_FOREGROUND_COLOR = 3;
	static final int SELECTION_BACKGROUND_COLOR = 4;
	static final int ITEM_FONT = 5;
	static final int ITEM_FOREGROUND_COLOR = 6;
	static final int ITEM_BACKGROUND_COLOR = 7;
	Color selectionForegroundColor, selectionBackgroundColor, itemForegroundColor, itemBackgroundColor;
	Font itemFont;

	/* Other widgets added to the "Other" group */
	Button simpleTabButton, singleTabButton, imageButton, showMinButton, showMaxButton,
	topRightButton, unselectedCloseButton, unselectedImageButton;

	ToolBar topRightControl;

	Button tabHeightDefault, tabHeightSmall, tabHeightMedium, tabHeightLarge;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	CTabFolderTab(final ControlExample instance) {
		super(instance);
	}

	@Override
	void createControlGroup() {
		super.createControlGroup();

		/* Create a group for the CTabFolder */
		this.tabHeightGroup = new Group (this.controlGroup, SWT.NONE);
		this.tabHeightGroup.setLayout (new GridLayout ());
		this.tabHeightGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.tabHeightGroup.setText (ControlExample.getResourceString ("Tab_Height"));

		this.tabHeightDefault = new Button(this.tabHeightGroup, SWT.RADIO);
		this.tabHeightDefault.setText(ControlExample.getResourceString("Preferred"));
		this.tabHeightDefault.setSelection(true);
		this.tabHeightDefault.addSelectionListener (widgetSelectedAdapter(event -> this.setTabHeight(SWT.DEFAULT)));

		this.tabHeightSmall = new Button(this.tabHeightGroup, SWT.RADIO);
		this.tabHeightSmall.setText("8");
		this.tabHeightSmall.addSelectionListener (widgetSelectedAdapter(event -> this.setTabHeight(Integer.parseInt(this.tabHeightSmall.getText()))));

		this.tabHeightMedium = new Button(this.tabHeightGroup, SWT.RADIO);
		this.tabHeightMedium.setText("20");
		this.tabHeightMedium.addSelectionListener (widgetSelectedAdapter(event -> this.setTabHeight(Integer.parseInt(this.tabHeightMedium.getText()))));

		this.tabHeightLarge = new Button(this.tabHeightGroup, SWT.RADIO);
		this.tabHeightLarge.setText("45");
		this.tabHeightLarge.addSelectionListener (widgetSelectedAdapter(event -> this.setTabHeight(Integer.parseInt(this.tabHeightLarge.getText()))));
	}

	/**
	 * Creates the "Colors and Fonts" group.
	 */
	@Override
	void createColorAndFontGroup () {
		super.createColorAndFontGroup();

		TableItem item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Selection_Foreground_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Selection_Background_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Font"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Foreground_Color"));
		item = new TableItem(this.colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Background_Color"));

		this.shell.addDisposeListener(event -> {
			if (this.itemFont != null) {
        this.itemFont.dispose();
      }
			this.selectionBackgroundColor = null;
			this.selectionForegroundColor = null;
			this.itemFont = null;
			this.itemBackgroundColor = null;
			this.itemForegroundColor = null;
		});
	}

	@Override
	void changeFontOrColor(final int index) {
		switch (index) {
			case SELECTION_FOREGROUND_COLOR: {
				Color oldColor = this.selectionForegroundColor;
				if (oldColor == null) {
          oldColor = this.tabFolder1.getSelectionForeground();
        }
				this.colorDialog.setRGB(oldColor.getRGB());
				final RGB rgb = this.colorDialog.open();
				if (rgb == null) {
          return;
        }
				this.selectionForegroundColor = new Color (rgb);
				this.setSelectionForeground ();
			}
			break;
			case SELECTION_BACKGROUND_COLOR: {
				Color oldColor = this.selectionBackgroundColor;
				if (oldColor == null) {
          oldColor = this.tabFolder1.getSelectionBackground();
        }
				this.colorDialog.setRGB(oldColor.getRGB());
				final RGB rgb = this.colorDialog.open();
				if (rgb == null) {
          return;
        }
				this.selectionBackgroundColor = new Color (rgb);
				this.setSelectionBackground ();
			}
			break;
			case ITEM_FONT: {
				Font oldFont = this.itemFont;
				if (oldFont == null) {
          oldFont = this.tabFolder1.getItem (0).getFont ();
        }
				this.fontDialog.setFontList(oldFont.getFontData());
				final FontData fontData = this.fontDialog.open ();
				if (fontData == null) {
          return;
        }
				oldFont = this.itemFont;
				this.itemFont = new Font (this.display, fontData);
				this.setItemFont ();
				this.setExampleWidgetSize ();
				if (oldFont != null) {
          oldFont.dispose ();
        }
			}
			break;
			case ITEM_FOREGROUND_COLOR: {
				Color oldColor = this.itemForegroundColor;
				if (oldColor == null) {
          oldColor = this.tabFolder1.getItem(0).getControl().getForeground();
        }
				this.colorDialog.setRGB(oldColor.getRGB());
				final RGB rgb = this.colorDialog.open();
				if (rgb == null) {
          return;
        }
				this.itemForegroundColor = new Color (rgb);
				this.setItemForeground ();
			}
			break;
			case ITEM_BACKGROUND_COLOR: {
				Color oldColor = this.itemBackgroundColor;
				if (oldColor == null) {
          oldColor = this.tabFolder1.getItem(0).getControl().getBackground();
        }
				this.colorDialog.setRGB(oldColor.getRGB());
				final RGB rgb = this.colorDialog.open();
				if (rgb == null) {
          return;
        }
				this.itemBackgroundColor = new Color (rgb);
				this.setItemBackground ();
			}
			break;
			default:
				super.changeFontOrColor(index);
		}
	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		this.simpleTabButton = new Button (this.otherGroup, SWT.CHECK);
		this.simpleTabButton.setText (ControlExample.getResourceString("Set_Simple_Tabs"));
		this.simpleTabButton.setSelection(true);
		this.simpleTabButton.addSelectionListener (widgetSelectedAdapter(event -> this.setSimpleTabs()));

		this.singleTabButton = new Button (this.otherGroup, SWT.CHECK);
		this.singleTabButton.setText (ControlExample.getResourceString("Set_Single_Tabs"));
		this.singleTabButton.setSelection(false);
		this.singleTabButton.addSelectionListener (widgetSelectedAdapter(event -> this.setSingleTabs()));

		this.showMinButton = new Button (this.otherGroup, SWT.CHECK);
		this.showMinButton.setText (ControlExample.getResourceString("Set_Min_Visible"));
		this.showMinButton.setSelection(false);
		this.showMinButton.addSelectionListener (widgetSelectedAdapter(event -> this.setMinimizeVisible()));

		this.showMaxButton = new Button (this.otherGroup, SWT.CHECK);
		this.showMaxButton.setText (ControlExample.getResourceString("Set_Max_Visible"));
		this.showMaxButton.setSelection(false);
		this.showMaxButton.addSelectionListener (widgetSelectedAdapter(event -> this.setMaximizeVisible()));

		this.topRightButton = new Button (this.otherGroup, SWT.CHECK);
		this.topRightButton.setText (ControlExample.getResourceString("Set_Top_Right"));
		this.topRightButton.setSelection(false);
		this.topRightButton.addSelectionListener (widgetSelectedAdapter(event -> this.setTopRight()));

		this.imageButton = new Button (this.otherGroup, SWT.CHECK);
		this.imageButton.setText (ControlExample.getResourceString("Set_Image"));
		this.imageButton.addSelectionListener (widgetSelectedAdapter(event -> this.setImages()));

		this.unselectedImageButton = new Button (this.otherGroup, SWT.CHECK);
		this.unselectedImageButton.setText (ControlExample.getResourceString("Set_Unselected_Image_Visible"));
		this.unselectedImageButton.setSelection(true);
		this.unselectedImageButton.addSelectionListener (widgetSelectedAdapter(event -> this.setUnselectedImageVisible()));
		this.unselectedCloseButton = new Button (this.otherGroup, SWT.CHECK);
		this.unselectedCloseButton.setText (ControlExample.getResourceString("Set_Unselected_Close_Visible"));
		this.unselectedCloseButton.setSelection(true);
		this.unselectedCloseButton.addSelectionListener (widgetSelectedAdapter(event -> this.setUnselectedCloseVisible()));
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the CTabFolder */
		this.tabFolderGroup = new Group (this.exampleGroup, SWT.NONE);
		this.tabFolderGroup.setLayout (new GridLayout ());
		this.tabFolderGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.tabFolderGroup.setText ("CTabFolder");
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {

		/* Compute the widget style */
		int style = this.getDefaultStyle();
		if (this.topButton.getSelection ()) {
      style |= SWT.TOP;
    }
		if (this.bottomButton.getSelection ()) {
      style |= SWT.BOTTOM;
    }
		if (this.borderButton.getSelection ()) {
      style |= SWT.BORDER;
    }
		if (this.flatButton.getSelection ()) {
      style |= SWT.FLAT;
    }
		if (this.closeButton.getSelection ()) {
      style |= SWT.CLOSE;
    }

		/* Create the example widgets */
		this.tabFolder1 = new CTabFolder (this.tabFolderGroup, style);
		for (int i = 0; i < CTabItems1.length; i++) {
			final CTabItem item = new CTabItem(this.tabFolder1, SWT.NONE);
			item.setText(CTabItems1[i]);
			final Text text = new Text(this.tabFolder1, SWT.WRAP | SWT.MULTI);
			text.setText(ControlExample.getResourceString("CTabItem_content") + ": " + i);
			item.setControl(text);
		}
		this.tabFolder1.addListener(SWT.Selection, event -> this.lastSelectedTab = this.tabFolder1.getSelectionIndex());

		/* If we have saved state, restore it */
		this.tabFolder1.setSelection(this.lastSelectedTab);
		this.setTopRight ();
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup ();

		/* Create the extra widgets */
		this.topButton = new Button (this.styleGroup, SWT.RADIO);
		this.topButton.setText ("SWT.TOP");
		this.topButton.setSelection(true);
		this.bottomButton = new Button (this.styleGroup, SWT.RADIO);
		this.bottomButton.setText ("SWT.BOTTOM");
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
		this.flatButton = new Button (this.styleGroup, SWT.CHECK);
		this.flatButton.setText ("SWT.FLAT");
		this.closeButton = new Button (this.styleGroup, SWT.CHECK);
		this.closeButton.setText ("SWT.CLOSE");

		final Group topRightGroup = new Group(this.styleGroup, SWT.NONE);
		topRightGroup.setLayout (new GridLayout ());
		topRightGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false));
		topRightGroup.setText (ControlExample.getResourceString("Top_Right_Styles"));
		this.rightButton = new Button (topRightGroup, SWT.RADIO);
		this.rightButton.setText ("SWT.RIGHT");
		this.rightButton.setSelection(true);
		this.fillButton = new Button (topRightGroup, SWT.RADIO);
		this.fillButton.setText ("SWT.FILL");
		this.wrapButton = new Button (topRightGroup, SWT.RADIO);
		this.wrapButton.setText ("SWT.RIGHT | SWT.WRAP");
	}

	/**
	 * Gets the list of custom event names.
	 *
	 * @return an array containing custom event names
	 */
	@Override
	String [] getCustomEventNames () {
		return new String [] {"CTabFolderEvent"};
	}

	/**
	 * Gets the "Example" widget children's items, if any.
	 *
	 * @return an array containing the example widget children's items
	 */
	@Override
	Item [] getExampleWidgetItems () {
		return this.tabFolder1.getItems();
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [] {this.tabFolder1};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "CTabFolder";
	}

	/**
	 * Hooks the custom listener specified by eventName.
	 */
	@Override
	void hookCustomListener (final String eventName) {
		if (eventName == "CTabFolderEvent") {
			this.tabFolder1.addCTabFolder2Listener (new CTabFolder2Listener () {
				@Override
				public void close (final CTabFolderEvent event) {
					CTabFolderTab.this.log (eventName, event);
				}
				@Override
				public void minimize(final CTabFolderEvent event) {
					CTabFolderTab.this.log (eventName, event);
				}
				@Override
				public void maximize(final CTabFolderEvent event) {
					CTabFolderTab.this.log (eventName, event);
				}
				@Override
				public void restore(final CTabFolderEvent event) {
					CTabFolderTab.this.log (eventName, event);
				}
				@Override
				public void showList(final CTabFolderEvent event) {
					CTabFolderTab.this.log (eventName, event);
				}
			});
		}
	}

	/**
	 * Sets the foreground color, background color, and font
	 * of the "Example" widgets to their default settings.
	 * Also sets foreground and background color of the Node 1
	 * TreeItems to default settings.
	 */
	@Override
	void resetColorsAndFonts () {
		super.resetColorsAndFonts ();
		this.selectionForegroundColor = null;
		this.setSelectionForeground ();
		this.selectionBackgroundColor = null;
		this.setSelectionBackground ();
		final Font oldFont = this.itemFont;
		this.itemFont = null;
		this.setItemFont ();
		if (oldFont != null) {
      oldFont.dispose();
    }
		this.itemForegroundColor = null;
		this.setItemForeground ();
		this.itemBackgroundColor = null;
		this.setItemBackground ();
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		super.setExampleWidgetState();
		this.setSimpleTabs();
		this.setSingleTabs();
		this.setImages();
		this.setMinimizeVisible();
		this.setMaximizeVisible();
		this.setUnselectedCloseVisible();
		this.setUnselectedImageVisible();
		this.setSelectionBackground ();
		this.setSelectionForeground ();
		this.setItemFont ();
		this.setItemBackground();
		this.setItemForeground();
		this.setExampleWidgetSize();
	}

	/**
	 * Sets the shape that the CTabFolder will use to render itself.
	 */
	void setSimpleTabs () {
		this.tabFolder1.setSimple (this.simpleTabButton.getSelection ());
		this.setExampleWidgetSize();
	}

	/**
	 * Sets the number of tabs that the CTabFolder should display.
	 */
	void setSingleTabs () {
		this.tabFolder1.setSingle (this.singleTabButton.getSelection ());
		this.setExampleWidgetSize();
	}
	/**
	 * Sets an image into each item of the "Example" widgets.
	 */
	void setImages () {
		final boolean setImage = this.imageButton.getSelection ();
		final CTabItem items[] = this.tabFolder1.getItems ();
		for (final CTabItem item : items) {
			if (setImage) {
				item.setImage (this.instance.images[ControlExample.ciClosedFolder]);
			} else {
				item.setImage (null);
			}
		}
		this.setExampleWidgetSize ();
	}
	/**
	 * Sets the visibility of the minimize button
	 */
	void setMinimizeVisible () {
		this.tabFolder1.setMinimizeVisible(this.showMinButton.getSelection ());
		this.setExampleWidgetSize();
	}
	/**
	 * Sets the visibility of the maximize button
	 */
	void setMaximizeVisible () {
		this.tabFolder1.setMaximizeVisible(this.showMaxButton.getSelection ());
		this.setExampleWidgetSize();
	}
	/**
	 * Sets the top right control to a toolbar
	 */
	void setTopRight () {
		if (this.topRightButton.getSelection ()) {
			this.topRightControl = new ToolBar(this.tabFolder1, SWT.FLAT);
			ToolItem item = new ToolItem(this.topRightControl, SWT.PUSH);
			item.setImage(this.instance.images[ControlExample.ciClosedFolder]);
			item = new ToolItem(this.topRightControl, SWT.PUSH);
			item.setImage(this.instance.images[ControlExample.ciOpenFolder]);
			int topRightStyle = 0;
			if (this.rightButton.getSelection ()) {
        topRightStyle |= SWT.RIGHT;
      }
			if (this.fillButton.getSelection ()) {
        topRightStyle |= SWT.FILL;
      }
			if (this.wrapButton.getSelection ()) {
        topRightStyle |= SWT.RIGHT | SWT.WRAP;
      }
			this.tabFolder1.setTopRight(this.topRightControl, topRightStyle);
		} else if (this.topRightControl != null) {
    	this.tabFolder1.setTopRight(null);
    	this.topRightControl.dispose();
    }
		this.setExampleWidgetSize();
	}
	/**
	 * Sets the visibility of the close button on unselected tabs
	 */
	void setUnselectedCloseVisible () {
		this.tabFolder1.setUnselectedCloseVisible(this.unselectedCloseButton.getSelection ());
		this.setExampleWidgetSize();
	}
	/**
	 * Sets the visibility of the image on unselected tabs
	 */
	void setUnselectedImageVisible () {
		this.tabFolder1.setUnselectedImageVisible(this.unselectedImageButton.getSelection ());
		this.setExampleWidgetSize();
	}
	/**
	 * Sets the background color of CTabItem 0.
	 */
	void setSelectionBackground () {
		if (!this.instance.startup) {
			this.tabFolder1.setSelectionBackground(this.selectionBackgroundColor);
		}
		// Set the selection background item's image to match the background color of the selection.
		Color color = this.selectionBackgroundColor;
		if (color == null) {
      color = this.tabFolder1.getSelectionBackground ();
    }
		final TableItem item = this.colorAndFontTable.getItem(SELECTION_BACKGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the foreground color of CTabItem 0.
	 */
	void setSelectionForeground () {
		if (!this.instance.startup) {
			this.tabFolder1.setSelectionForeground(this.selectionForegroundColor);
		}
		// Set the selection foreground item's image to match the foreground color of the selection.
		Color color = this.selectionForegroundColor;
		if (color == null) {
      color = this.tabFolder1.getSelectionForeground ();
    }
		final TableItem item = this.colorAndFontTable.getItem(SELECTION_FOREGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the font of CTabItem 0.
	 */
	void setItemFont () {
		if (!this.instance.startup) {
			this.tabFolder1.getItem (0).setFont (this.itemFont);
			this.setExampleWidgetSize();
		}
		/* Set the font item's image to match the font of the item. */
		Font ft = this.itemFont;
		if (ft == null) {
      ft = this.tabFolder1.getItem (0).getFont ();
    }
		final TableItem item = this.colorAndFontTable.getItem(ITEM_FONT);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.fontImage(ft));
		item.setFont(ft);
		this.colorAndFontTable.layout ();
	}

	/**
	 * Sets the background color of the CTabItem 0 content element.
	 */
	void setItemBackground () {
		if (!this.instance.startup) {
			this.tabFolder1.getItem(0).getControl().setBackground(this.itemBackgroundColor);
		}
		/* Set the background color item's image to match the background color of the content. */
		Color color = this.itemBackgroundColor;
		if (color == null) {
      color = this.tabFolder1.getItem(0).getControl().getBackground();
    }
		final TableItem item = this.colorAndFontTable.getItem(ITEM_BACKGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Sets the foreground color of the CTabItem 0  content element.
	 */
	void setItemForeground () {
		if (!this.instance.startup) {
			this.tabFolder1.getItem(0).getControl().setForeground(this.itemForegroundColor);
		}
		/* Set the foreground color item's image to match the foreground color of the content. */
		Color color = this.itemForegroundColor;
		if (color == null) {
      color = this.tabFolder1.getItem(0).getControl().getForeground();
    }
		final TableItem item = this.colorAndFontTable.getItem(ITEM_FOREGROUND_COLOR);
		final Image oldImage = item.getImage();
		if (oldImage != null) {
      oldImage.dispose();
    }
		item.setImage (this.colorImage(color));
	}

	/**
	 * Set the fixed item height for the CTabFolder
	 *
	 * @param height new fixed header height
	 */
	void setTabHeight(final int height) {
		this.tabFolder1.setTabHeight(height);
	}
}
