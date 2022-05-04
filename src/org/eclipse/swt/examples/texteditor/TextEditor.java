/*****************************************************************************
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
package org.eclipse.swt.examples.texteditor;

import static org.eclipse.swt.events.MenuListener.menuShownAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class TextEditor {
  Display display;
  Shell shell;
  CoolBar coolBar;
  StyledText styledText;
  Label statusBar;
  ToolItem boldControl, italicControl, leftAlignmentItem, centerAlignmentItem,
      rightAlignmentItem, justifyAlignmentItem, blockSelectionItem;
  Combo fontNameControl, fontSizeControl;
  MenuItem underlineSingleItem, underlineDoubleItem, underlineErrorItem,
      underlineSquiggleItem, borderSolidItem, borderDashItem, borderDotItem;

  boolean insert = true;
  StyleRange[] selectedRanges;
  int newCharCount, start;
  String fileName = null;
  int styleState;
  String link;

  // Resources
  Image iBold, iItalic, iUnderline, iStrikeout, iLeftAlignment, iRightAlignment,
      iCenterAlignment, iJustifyAlignment, iCopy, iCut, iLink;
  Image iPaste, iSpacing, iIndent, iTextForeground, iTextBackground,
      iBaselineUp, iBaselineDown, iBulletList, iNumberedList, iBlockSelection,
      iBorderStyle;
  Font font, textFont;
  Color textForeground, textBackground, strikeoutColor, underlineColor,
      borderColor;

  static final int BULLET_WIDTH = 40;
  static final int MARGIN = 5;
  static final int BOLD = SWT.BOLD;
  static final int ITALIC = SWT.ITALIC;
  static final int FONT_STYLE = BOLD | ITALIC;
  static final int STRIKEOUT = 1 << 3;
  static final int FOREGROUND = 1 << 4;
  static final int BACKGROUND = 1 << 5;
  static final int FONT = 1 << 6;
  static final int BASELINE_UP = 1 << 7;
  static final int BASELINE_DOWN = 1 << 8;
  static final int UNDERLINE_SINGLE = 1 << 9;
  static final int UNDERLINE_DOUBLE = 1 << 10;
  static final int UNDERLINE_ERROR = 1 << 11;
  static final int UNDERLINE_SQUIGGLE = 1 << 12;
  static final int UNDERLINE_LINK = 1 << 13;
  static final int UNDERLINE = UNDERLINE_SINGLE | UNDERLINE_DOUBLE
      | UNDERLINE_SQUIGGLE | UNDERLINE_ERROR | UNDERLINE_LINK;
  static final int BORDER_SOLID = 1 << 23;
  static final int BORDER_DASH = 1 << 24;
  static final int BORDER_DOT = 1 << 25;
  static final int BORDER = BORDER_SOLID | BORDER_DASH | BORDER_DOT;

  static final boolean SAMPLE_TEXT = false;
  static final boolean USE_BASELINE = false;

  static final String[] FONT_SIZES = new String[] { "6", //$NON-NLS-1$
      "8", //$NON-NLS-1$
      "9", //$NON-NLS-1$
      "10", //$NON-NLS-1$
      "11", //$NON-NLS-1$
      "12", //$NON-NLS-1$
      "14", //$NON-NLS-1$
      "24", //$NON-NLS-1$
      "36", //$NON-NLS-1$
      "48" //$NON-NLS-1$
  };

  static final ResourceBundle resources = ResourceBundle
      .getBundle("examples_texteditor"); //$NON-NLS-1$

  static String getResourceString(final String key) {
    try {
      return resources.getString(key);
    } catch (final MissingResourceException e) {
      return key;
    } catch (final NullPointerException e) {
      return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static void main(final String[] args) {
    final Display display = new Display();
    final TextEditor editor = new TextEditor();
    final Shell shell = editor.open(display);
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    editor.releaseResources();
    display.dispose();
  }

  /*
   * Default constructor is needed so that example launcher can create an
   * instance.
   */
  public TextEditor() {
  }

  public TextEditor(final Display display) {
    this.open(display);
  }

  public Shell open(final Display display) {
    this.display = display;
    this.initResources();
    this.shell = new Shell(display);
    this.shell.setText(getResourceString("Window_title")); //$NON-NLS-1$
    this.styledText = new StyledText(this.shell,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    this.createMenuBar();
    this.createToolBar();
    this.createPopup();
    this.statusBar = new Label(this.shell, SWT.NONE);
    this.installListeners();
    this.updateToolBar();
    this.updateStatusBar();
    this.shell.setSize(1000, 700);
    this.shell.open();
    return this.shell;
  }

  void addControl(final Control control) {
    final int offset = this.styledText.getCaretOffset();
    this.styledText.replaceTextRange(offset, 0, "\uFFFC"); //$NON-NLS-1$
    final StyleRange style = new StyleRange();
    final Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    final int ascent = (2 * size.y) / 3;
    final int descent = size.y - ascent;
    style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
        size.x + (2 * MARGIN));
    style.data = control;
    final int[] ranges = { offset, 1 };
    final StyleRange[] styles = { style };
    this.styledText.setStyleRanges(0, 0, ranges, styles);
    control.setSize(size);
  }

  void addImage(final Image image) {
    final int offset = this.styledText.getCaretOffset();
    this.styledText.replaceTextRange(offset, 0, "\uFFFC"); //$NON-NLS-1$
    final StyleRange style = new StyleRange();
    final Rectangle rect = image.getBounds();
    style.metrics = new GlyphMetrics(rect.height, 0, rect.width);
    style.data = image;
    final int[] ranges = { offset, 1 };
    final StyleRange[] styles = { style };
    this.styledText.setStyleRanges(0, 0, ranges, styles);
  }

  void adjustFontSize(final int increment) {
    final int newIndex = this.fontSizeControl.getSelectionIndex() + increment;
    if ((0 <= newIndex) && (newIndex < this.fontSizeControl.getItemCount())) {
      final String name = this.fontNameControl.getText();
      final int size = Integer.parseInt(this.fontSizeControl.getItem(newIndex));
      this.disposeResource(this.textFont);
      this.textFont = new Font(this.display, name, size, SWT.NORMAL);
      this.setStyle(FONT);
      this.updateToolBar();
    }
  }

  void createMenuBar() {
    final Menu menu = new Menu(this.shell, SWT.BAR);
    this.shell.setMenuBar(menu);

    final MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
    final Menu fileMenu = new Menu(this.shell, SWT.DROP_DOWN);
    fileItem.setText(getResourceString("File_menuitem")); //$NON-NLS-1$
    fileItem.setMenu(fileMenu);

    final MenuItem openItem = new MenuItem(fileMenu, SWT.PUSH);
    openItem.setText(getResourceString("Open_menuitem")); //$NON-NLS-1$
    openItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final FileDialog dialog = new FileDialog(this.shell, SWT.OPEN);
      dialog
          .setFilterNames(new String[] { getResourceString("Text_Documents") }); //$NON-NLS-1$
      dialog.setFilterExtensions(new String[] { "*.txt" }); //$NON-NLS-1$
      final String name = dialog.open();
      if (name == null) {
        return;
      }
      this.fileName = name;
      try (FileInputStream file = new FileInputStream(name);) {
        this.styledText.setText(this.openFile(file));
      } catch (final IOException e) {
        this.showError(getResourceString("Error"), e.getMessage()); //$NON-NLS-1$
      }
    }));

    final MenuItem saveItem = new MenuItem(fileMenu, SWT.PUSH);
    saveItem.setText(getResourceString("Save_menuitem")); //$NON-NLS-1$
    saveItem.addSelectionListener(widgetSelectedAdapter(event -> this.saveFile()));

    fileMenu.addMenuListener(new MenuAdapter() {
      @Override
      public void menuShown(final MenuEvent event) {
        saveItem.setEnabled(TextEditor.this.fileName != null);
      }
    });

    final MenuItem saveAsItem = new MenuItem(fileMenu, SWT.PUSH);
    saveAsItem.setText(getResourceString("SaveAs_menuitem")); //$NON-NLS-1$
    saveAsItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final FileDialog dialog = new FileDialog(this.shell, SWT.SAVE);
      dialog
          .setFilterNames(new String[] { getResourceString("Text_Documents") }); //$NON-NLS-1$
      dialog.setFilterExtensions(new String[] { "*.txt" }); //$NON-NLS-1$
      if (this.fileName != null) {
        dialog.setFileName(this.fileName);
      }
      final String name = dialog.open();
      if (name != null) {
        this.fileName = name;
        this.saveFile();
      }
    }));

    new MenuItem(fileMenu, SWT.SEPARATOR);

    final MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
    exitItem.setText(getResourceString("Exit_menuitem")); //$NON-NLS-1$
    exitItem
        .addSelectionListener(widgetSelectedAdapter(event -> this.shell.dispose()));

    final MenuItem editItem = new MenuItem(menu, SWT.CASCADE);
    final Menu editMenu = new Menu(this.shell, SWT.DROP_DOWN);
    editItem.setText(getResourceString("Edit_menuitem")); //$NON-NLS-1$
    editItem.setMenu(editMenu);
    final MenuItem cutItem = new MenuItem(editMenu, SWT.PUSH);
    cutItem.setText(getResourceString("Cut_menuitem")); //$NON-NLS-1$
    cutItem.setImage(this.iCut);
    cutItem.setAccelerator(SWT.MOD1 | 'x');
    cutItem
        .addSelectionListener(widgetSelectedAdapter(event -> this.styledText.cut()));

    final MenuItem copyItem = new MenuItem(editMenu, SWT.PUSH);
    copyItem.setText(getResourceString("Copy_menuitem")); //$NON-NLS-1$
    copyItem.setImage(this.iCopy);
    copyItem.setAccelerator(SWT.MOD1 | 'c');
    copyItem.addSelectionListener(
        widgetSelectedAdapter(event -> this.styledText.copy()));

    final MenuItem pasteItem = new MenuItem(editMenu, SWT.PUSH);
    pasteItem.setText(getResourceString("Paste_menuitem")); //$NON-NLS-1$
    pasteItem.setImage(this.iPaste);
    pasteItem.setAccelerator(SWT.MOD1 | 'v');
    pasteItem.addSelectionListener(
        widgetSelectedAdapter(event -> this.styledText.paste()));

    new MenuItem(editMenu, SWT.SEPARATOR);
    final MenuItem selectAllItem = new MenuItem(editMenu, SWT.PUSH);
    selectAllItem.setText(getResourceString("SelectAll_menuitem")); //$NON-NLS-1$
    selectAllItem.setAccelerator(SWT.MOD1 | 'a');
    selectAllItem.addSelectionListener(
        widgetSelectedAdapter(event -> this.styledText.selectAll()));

    editMenu.addMenuListener(menuShownAdapter(event -> {
      final int selectionCount = this.styledText.getSelectionCount();
      cutItem.setEnabled(selectionCount > 0);
      copyItem.setEnabled(selectionCount > 0);
      selectAllItem.setEnabled(selectionCount < this.styledText.getCharCount());
    }));

    final MenuItem wrapItem = new MenuItem(editMenu, SWT.CHECK);
    wrapItem.setText(getResourceString("Wrap_menuitem")); //$NON-NLS-1$
    wrapItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final MenuItem item = (MenuItem) event.widget;
      final boolean enabled = item.getSelection();
      this.styledText.setWordWrap(enabled);
      editMenu.getItem(6).setEnabled(enabled);
      editMenu.getItem(8).setEnabled(enabled);
      this.leftAlignmentItem.setEnabled(enabled);
      this.centerAlignmentItem.setEnabled(enabled);
      this.rightAlignmentItem.setEnabled(enabled);
      this.justifyAlignmentItem.setEnabled(enabled);
      this.blockSelectionItem.setEnabled(!enabled);
    }));

    final MenuItem justifyItem = new MenuItem(editMenu, SWT.CHECK);
    justifyItem.setText(getResourceString("Justify_menuitem")); //$NON-NLS-1$
    justifyItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final MenuItem item = (MenuItem) event.widget;
      this.styledText.setJustify(item.getSelection());
      this.updateToolBar();
    }));
    justifyItem.setEnabled(false);

    final MenuItem setFontItem = new MenuItem(editMenu, SWT.PUSH);
    setFontItem.setText(getResourceString("SetFont_menuitem")); //$NON-NLS-1$
    setFontItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final FontDialog fontDialog = new FontDialog(this.shell);
      fontDialog.setFontList(this.styledText.getFont().getFontData());
      final FontData data = fontDialog.open();
      if (data != null) {
        final Font newFont = new Font(this.display, data);
        this.styledText.setFont(newFont);
        if (this.font != null) {
          this.font.dispose();
        }
        this.font = newFont;
        this.updateToolBar();
      }
    }));

    final MenuItem alignmentItem = new MenuItem(editMenu, SWT.CASCADE);
    alignmentItem.setText(getResourceString("Alignment_menuitem")); //$NON-NLS-1$
    final Menu alignmentMenu = new Menu(this.shell, SWT.DROP_DOWN);
    alignmentItem.setMenu(alignmentMenu);
    final MenuItem leftAlignmentItem = new MenuItem(alignmentMenu, SWT.RADIO);
    leftAlignmentItem.setText(getResourceString("Left_menuitem")); //$NON-NLS-1$
    leftAlignmentItem.setSelection(true);
    leftAlignmentItem.addSelectionListener(widgetSelectedAdapter(event -> {
      this.styledText.setAlignment(SWT.LEFT);
      this.updateToolBar();
    }));
    alignmentItem.setEnabled(false);

    final MenuItem centerAlignmentItem = new MenuItem(alignmentMenu, SWT.RADIO);
    centerAlignmentItem.setText(getResourceString("Center_menuitem")); //$NON-NLS-1$
    centerAlignmentItem.addSelectionListener(widgetSelectedAdapter(event -> {
      this.styledText.setAlignment(SWT.CENTER);
      this.updateToolBar();
    }));

    final MenuItem rightAlignmentItem = new MenuItem(alignmentMenu, SWT.RADIO);
    rightAlignmentItem.setText(getResourceString("Right_menuitem")); //$NON-NLS-1$
    rightAlignmentItem.addSelectionListener(widgetSelectedAdapter(event -> {
      this.styledText.setAlignment(SWT.RIGHT);
      this.updateToolBar();
    }));

    final MenuItem editOrientationItem = new MenuItem(editMenu, SWT.CASCADE);
    editOrientationItem.setText(getResourceString("Orientation_menuitem")); //$NON-NLS-1$
    final Menu editOrientationMenu = new Menu(this.shell, SWT.DROP_DOWN);
    editOrientationItem.setMenu(editOrientationMenu);

    final MenuItem leftToRightItem = new MenuItem(editOrientationMenu, SWT.RADIO);
    leftToRightItem.setText(getResourceString("LeftToRight_menuitem")); //$NON-NLS-1$
    leftToRightItem.addSelectionListener(widgetSelectedAdapter(
        event -> this.styledText.setOrientation(SWT.LEFT_TO_RIGHT)));
    leftToRightItem.setSelection(true);

    final MenuItem rightToLeftItem = new MenuItem(editOrientationMenu, SWT.RADIO);
    rightToLeftItem.setText(getResourceString("RightToLeft_menuitem")); //$NON-NLS-1$
    rightToLeftItem.addSelectionListener(widgetSelectedAdapter(
        event -> this.styledText.setOrientation(SWT.RIGHT_TO_LEFT)));

    new MenuItem(editMenu, SWT.SEPARATOR);
    final MenuItem insertObjectItem = new MenuItem(editMenu, SWT.CASCADE);
    insertObjectItem.setText(getResourceString("InsertObject_menuitem")); //$NON-NLS-1$
    final Menu insertObjectMenu = new Menu(this.shell, SWT.DROP_DOWN);
    insertObjectItem.setMenu(insertObjectMenu);

    final MenuItem insertControlItem = new MenuItem(insertObjectMenu, SWT.CASCADE);
    insertControlItem.setText(getResourceString("Controls_menuitem")); //$NON-NLS-1$
    final Menu controlChoice = new Menu(this.shell, SWT.DROP_DOWN);
    insertControlItem.setMenu(controlChoice);

    final MenuItem buttonItem = new MenuItem(controlChoice, SWT.PUSH);
    buttonItem.setText(getResourceString("Button_menuitem")); //$NON-NLS-1$
    final MenuItem comboItem = new MenuItem(controlChoice, SWT.PUSH);
    comboItem.setText(getResourceString("Combo_menuitem")); //$NON-NLS-1$

    buttonItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final Button button = new Button(this.styledText, SWT.PUSH);
      button.setText(getResourceString("Button_menuitem")); //$NON-NLS-1$
      this.addControl(button);
    }));

    comboItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final Combo combo = new Combo(this.styledText, SWT.NONE);
      combo.setText(getResourceString("Combo_menuitem")); //$NON-NLS-1$
      this.addControl(combo);
    }));

    final MenuItem insertImageItem = new MenuItem(insertObjectMenu, SWT.PUSH);
    insertImageItem.setText(getResourceString("Image_menuitem")); //$NON-NLS-1$

    insertImageItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final FileDialog fileDialog = new FileDialog(this.shell, SWT.OPEN);
      final String fileName = fileDialog.open();
      if (fileName != null) {
        try {
          final Image image = new Image(this.display, fileName);
          this.addImage(image);
        } catch (final Exception e) {
          this.showError(getResourceString("Bad_image"), e.getMessage()); //$NON-NLS-1$
        }
      }
    }));

    if (SAMPLE_TEXT) {
      new MenuItem(editMenu, SWT.SEPARATOR);
      final MenuItem loadProfileItem = new MenuItem(editMenu, SWT.CASCADE);
      loadProfileItem.setText(getResourceString("LoadProfile_menuitem")); //$NON-NLS-1$
      final Menu loadProfileMenu = new Menu(this.shell, SWT.DROP_DOWN);
      loadProfileItem.setMenu(loadProfileMenu);
      final SelectionListener adapter = widgetSelectedAdapter(event -> {
        final int profile = Integer.parseInt((String) event.widget.getData());
        this.loadProfile(profile);
      });

      MenuItem profileItem = new MenuItem(loadProfileMenu, SWT.PUSH);
      profileItem.setText(getResourceString("Profile1_menuitem")); //$NON-NLS-1$
      profileItem.setData("1"); //$NON-NLS-1$
      profileItem.addSelectionListener(adapter);
      profileItem = new MenuItem(loadProfileMenu, SWT.PUSH);
      profileItem.setText(getResourceString("Profile2_menuitem")); //$NON-NLS-1$
      profileItem.setData("2"); //$NON-NLS-1$
      profileItem.addSelectionListener(adapter);
      profileItem = new MenuItem(loadProfileMenu, SWT.PUSH);
      profileItem.setText(getResourceString("Profile3_menuitem")); //$NON-NLS-1$
      profileItem.setData("3"); //$NON-NLS-1$
      profileItem.addSelectionListener(adapter);
      profileItem = new MenuItem(loadProfileMenu, SWT.PUSH);
      profileItem.setText(getResourceString("Profile4_menuitem")); //$NON-NLS-1$
      profileItem.setData("4"); //$NON-NLS-1$
      profileItem.addSelectionListener(adapter);
    }
  }

  void createPopup() {
    final Menu menu = new Menu(this.styledText);
    final MenuItem cutItem = new MenuItem(menu, SWT.PUSH);
    cutItem.setText(getResourceString("Cut_menuitem")); //$NON-NLS-1$
    cutItem.setImage(this.iCut);
    cutItem.addListener(SWT.Selection, event -> this.styledText.cut());
    final MenuItem copyItem = new MenuItem(menu, SWT.PUSH);
    copyItem.setText(getResourceString("Copy_menuitem")); //$NON-NLS-1$
    copyItem.setImage(this.iCopy);
    copyItem.addListener(SWT.Selection, event -> this.styledText.copy());
    final MenuItem pasteItem = new MenuItem(menu, SWT.PUSH);
    pasteItem.setText(getResourceString("Paste_menuitem")); //$NON-NLS-1$
    pasteItem.setImage(this.iPaste);
    pasteItem.addListener(SWT.Selection, event -> this.styledText.paste());
    new MenuItem(menu, SWT.SEPARATOR);
    final MenuItem selectAllItem = new MenuItem(menu, SWT.PUSH);
    selectAllItem.setText(getResourceString("SelectAll_menuitem")); //$NON-NLS-1$
    selectAllItem.addListener(SWT.Selection, event -> this.styledText.selectAll());
    menu.addMenuListener(menuShownAdapter(event -> {
      final int selectionCount = this.styledText.getSelectionCount();
      cutItem.setEnabled(selectionCount > 0);
      copyItem.setEnabled(selectionCount > 0);
      selectAllItem.setEnabled(selectionCount < this.styledText.getCharCount());
    }));
    this.styledText.setMenu(menu);
  }

  void createToolBar() {
    this.coolBar = new CoolBar(this.shell, SWT.FLAT);
    final ToolBar styleToolBar = new ToolBar(this.coolBar, SWT.FLAT);
    this.boldControl = new ToolItem(styleToolBar, SWT.CHECK);
    this.boldControl.setImage(this.iBold);
    this.boldControl.setToolTipText(getResourceString("Bold")); //$NON-NLS-1$
    this.boldControl
        .addSelectionListener(widgetSelectedAdapter(event -> this.setStyle(BOLD)));

    this.italicControl = new ToolItem(styleToolBar, SWT.CHECK);
    this.italicControl.setImage(this.iItalic);
    this.italicControl.setToolTipText(getResourceString("Italic")); //$NON-NLS-1$
    this.italicControl
        .addSelectionListener(widgetSelectedAdapter(event -> this.setStyle(ITALIC)));

    final Menu underlineMenu = new Menu(this.shell, SWT.POP_UP);
    this.underlineSingleItem = new MenuItem(underlineMenu, SWT.RADIO);
    this.underlineSingleItem.setText(getResourceString("Single_menuitem")); //$NON-NLS-1$
    this.underlineSingleItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (this.underlineSingleItem.getSelection()) {
        this.setStyle(UNDERLINE_SINGLE);
      }
    }));
    this.underlineSingleItem.setSelection(true);

    this.underlineDoubleItem = new MenuItem(underlineMenu, SWT.RADIO);
    this.underlineDoubleItem.setText(getResourceString("Double_menuitem")); //$NON-NLS-1$
    this.underlineDoubleItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (this.underlineDoubleItem.getSelection()) {
        this.setStyle(UNDERLINE_DOUBLE);
      }
    }));

    this.underlineSquiggleItem = new MenuItem(underlineMenu, SWT.RADIO);
    this.underlineSquiggleItem.setText(getResourceString("Squiggle_menuitem")); //$NON-NLS-1$
    this.underlineSquiggleItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (this.underlineSquiggleItem.getSelection()) {
        this.setStyle(UNDERLINE_SQUIGGLE);
      }
    }));

    this.underlineErrorItem = new MenuItem(underlineMenu, SWT.RADIO);
    this.underlineErrorItem.setText(getResourceString("Error_menuitem")); //$NON-NLS-1$
    this.underlineErrorItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (this.underlineErrorItem.getSelection()) {
        this.setStyle(UNDERLINE_ERROR);
      }
    }));

    final MenuItem underlineColorItem = new MenuItem(underlineMenu, SWT.PUSH);
    underlineColorItem.setText(getResourceString("Color_menuitem")); //$NON-NLS-1$
    underlineColorItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final ColorDialog dialog = new ColorDialog(this.shell);
      final RGB rgb = this.underlineColor != null ? this.underlineColor.getRGB() : null;
      dialog.setRGB(rgb);
      final RGB newRgb = dialog.open();
      if (newRgb != null) {
        if (!newRgb.equals(rgb)) {
          this.underlineColor = new Color(newRgb);
        }
        if (this.underlineSingleItem.getSelection()) {
          this.setStyle(UNDERLINE_SINGLE);
        } else if (this.underlineDoubleItem.getSelection()) {
          this.setStyle(UNDERLINE_DOUBLE);
        } else if (this.underlineErrorItem.getSelection()) {
          this.setStyle(UNDERLINE_ERROR);
        } else if (this.underlineSquiggleItem.getSelection()) {
          this.setStyle(UNDERLINE_SQUIGGLE);
        }
      }
    }));

    final ToolItem underlineControl = new ToolItem(styleToolBar, SWT.DROP_DOWN);
    underlineControl.setImage(this.iUnderline);
    underlineControl.setToolTipText(getResourceString("Underline")); //$NON-NLS-1$
    underlineControl.addSelectionListener(widgetSelectedAdapter(event -> {
      if (event.detail == SWT.ARROW) {
        final Rectangle rect = underlineControl.getBounds();
        final Point pt = new Point(rect.x, rect.y + rect.height);
        underlineMenu
            .setLocation(this.display.map(underlineControl.getParent(), null, pt));
        underlineMenu.setVisible(true);
      } else if (this.underlineSingleItem.getSelection()) {
        this.setStyle(UNDERLINE_SINGLE);
      } else if (this.underlineDoubleItem.getSelection()) {
        this.setStyle(UNDERLINE_DOUBLE);
      } else if (this.underlineErrorItem.getSelection()) {
        this.setStyle(UNDERLINE_ERROR);
      } else if (this.underlineSquiggleItem.getSelection()) {
        this.setStyle(UNDERLINE_SQUIGGLE);
      }
    }));

    final ToolItem strikeoutControl = new ToolItem(styleToolBar, SWT.DROP_DOWN);
    strikeoutControl.setImage(this.iStrikeout);
    strikeoutControl.setToolTipText(getResourceString("Strikeout")); //$NON-NLS-1$
    strikeoutControl.addSelectionListener(widgetSelectedAdapter(event -> {
      if (event.detail == SWT.ARROW) {
        final ColorDialog dialog = new ColorDialog(this.shell);
        final RGB rgb = this.strikeoutColor != null ? this.strikeoutColor.getRGB() : null;
        dialog.setRGB(rgb);
        final RGB newRgb = dialog.open();
        if (newRgb == null) {
          return;
        }
        if (!newRgb.equals(rgb)) {
          this.strikeoutColor = new Color(newRgb);
        }
      }
      this.setStyle(STRIKEOUT);
    }));

    final Menu borderMenu = new Menu(this.shell, SWT.POP_UP);
    this.borderSolidItem = new MenuItem(borderMenu, SWT.RADIO);
    this.borderSolidItem.setText(getResourceString("Solid")); //$NON-NLS-1$
    this.borderSolidItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (this.borderSolidItem.getSelection()) {
        this.setStyle(BORDER_SOLID);
      }
    }));
    this.borderSolidItem.setSelection(true);

    this.borderDashItem = new MenuItem(borderMenu, SWT.RADIO);
    this.borderDashItem.setText(getResourceString("Dash")); //$NON-NLS-1$
    this.borderDashItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (this.borderDashItem.getSelection()) {
        this.setStyle(BORDER_DASH);
      }
    }));

    this.borderDotItem = new MenuItem(borderMenu, SWT.RADIO);
    this.borderDotItem.setText(getResourceString("Dot")); //$NON-NLS-1$
    this.borderDotItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (this.borderDotItem.getSelection()) {
        this.setStyle(BORDER_DOT);
      }
    }));

    final MenuItem borderColorItem = new MenuItem(borderMenu, SWT.PUSH);
    borderColorItem.setText(getResourceString("Color_menuitem")); //$NON-NLS-1$
    borderColorItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final ColorDialog dialog = new ColorDialog(this.shell);
      final RGB rgb = this.borderColor != null ? this.borderColor.getRGB() : null;
      dialog.setRGB(rgb);
      final RGB newRgb = dialog.open();
      if (newRgb != null) {
        if (!newRgb.equals(rgb)) {
          this.borderColor = new Color(newRgb);
        }
        if (this.borderDashItem.getSelection()) {
          this.setStyle(BORDER_DASH);
        } else if (this.borderDotItem.getSelection()) {
          this.setStyle(BORDER_DOT);
        } else if (this.borderSolidItem.getSelection()) {
          this.setStyle(BORDER_SOLID);
        }
      }
    }));

    final ToolItem borderControl = new ToolItem(styleToolBar, SWT.DROP_DOWN);
    borderControl.setImage(this.iBorderStyle);
    borderControl.setToolTipText(getResourceString("Box")); //$NON-NLS-1$
    borderControl.addSelectionListener(widgetSelectedAdapter(event -> {
      if (event.detail == SWT.ARROW) {
        final Rectangle rect = borderControl.getBounds();
        final Point pt = new Point(rect.x, rect.y + rect.height);
        borderMenu
            .setLocation(this.display.map(borderControl.getParent(), null, pt));
        borderMenu.setVisible(true);
      } else if (this.borderDashItem.getSelection()) {
        this.setStyle(BORDER_DASH);
      } else if (this.borderDotItem.getSelection()) {
        this.setStyle(BORDER_DOT);
      } else if (this.borderSolidItem.getSelection()) {
        this.setStyle(BORDER_SOLID);
      }
    }));

    final ToolItem foregroundItem = new ToolItem(styleToolBar, SWT.DROP_DOWN);
    foregroundItem.setImage(this.iTextForeground);
    foregroundItem.setToolTipText(getResourceString("TextForeground")); //$NON-NLS-1$
    foregroundItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if ((event.detail == SWT.ARROW) || (this.textForeground == null)) {
        final ColorDialog dialog = new ColorDialog(this.shell);
        final RGB rgb = this.textForeground != null ? this.textForeground.getRGB() : null;
        dialog.setRGB(rgb);
        final RGB newRgb = dialog.open();
        if (newRgb == null) {
          return;
        }
        if (!newRgb.equals(rgb)) {
          this.textForeground = new Color(newRgb);
        }
      }
      this.setStyle(FOREGROUND);
    }));

    final ToolItem backgroundItem = new ToolItem(styleToolBar, SWT.DROP_DOWN);
    backgroundItem.setImage(this.iTextBackground);
    backgroundItem.setToolTipText(getResourceString("TextBackground")); //$NON-NLS-1$
    backgroundItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if ((event.detail == SWT.ARROW) || (this.textBackground == null)) {
        final ColorDialog dialog = new ColorDialog(this.shell);
        final RGB rgb = this.textBackground != null ? this.textBackground.getRGB() : null;
        dialog.setRGB(rgb);
        final RGB newRgb = dialog.open();
        if (newRgb == null) {
          return;
        }
        if (!newRgb.equals(rgb)) {
          this.textBackground = new Color(newRgb);
        }
      }
      this.setStyle(BACKGROUND);
    }));

    final ToolItem baselineUpItem = new ToolItem(styleToolBar, SWT.PUSH);
    baselineUpItem.setImage(this.iBaselineUp);
    String tooltip = "IncreaseFont"; //$NON-NLS-1$
    if (USE_BASELINE)
     {
      tooltip = "IncreaseBaseline"; //$NON-NLS-1$
    }
    baselineUpItem.setToolTipText(getResourceString(tooltip));
    baselineUpItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (USE_BASELINE) {
        this.setStyle(BASELINE_UP);
      } else {
        this.adjustFontSize(1);
      }
    }));

    final ToolItem baselineDownItem = new ToolItem(styleToolBar, SWT.PUSH);
    baselineDownItem.setImage(this.iBaselineDown);
    tooltip = "DecreaseFont"; //$NON-NLS-1$
    if (USE_BASELINE)
     {
      tooltip = "DecreaseBaseline"; //$NON-NLS-1$
    }
    baselineDownItem.setToolTipText(getResourceString(tooltip));
    baselineDownItem.addSelectionListener(widgetSelectedAdapter(event -> {
      if (USE_BASELINE) {
        this.setStyle(BASELINE_DOWN);
      } else {
        this.adjustFontSize(-1);
      }
    }));
    final ToolItem linkItem = new ToolItem(styleToolBar, SWT.PUSH);
    linkItem.setImage(this.iLink);
    linkItem.setToolTipText(getResourceString("Link")); //$NON-NLS-1$
    linkItem.addSelectionListener(widgetSelectedAdapter(event -> this.setLink()));

    CoolItem coolItem = new CoolItem(this.coolBar, SWT.NONE);
    coolItem.setControl(styleToolBar);

    Composite composite = new Composite(this.coolBar, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 1;
    composite.setLayout(layout);
    this.fontNameControl = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
    this.fontNameControl.setItems(this.getFontNames());
    this.fontNameControl.setVisibleItemCount(12);
    this.fontSizeControl = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
    this.fontSizeControl.setItems(FONT_SIZES);
    this.fontSizeControl.setVisibleItemCount(8);
    final SelectionListener adapter = widgetSelectedAdapter(event -> {
      final String name = this.fontNameControl.getText();
      final int size = Integer.parseInt(this.fontSizeControl.getText());
      this.disposeResource(this.textFont);
      this.textFont = new Font(this.display, name, size, SWT.NORMAL);
      this.setStyle(FONT);
    });
    this.fontSizeControl.addSelectionListener(adapter);
    this.fontNameControl.addSelectionListener(adapter);
    coolItem = new CoolItem(this.coolBar, SWT.NONE);
    coolItem.setControl(composite);

    final ToolBar alignmentToolBar = new ToolBar(this.coolBar, SWT.FLAT);
    this.blockSelectionItem = new ToolItem(alignmentToolBar, SWT.CHECK);
    this.blockSelectionItem.setImage(this.iBlockSelection);
    this.blockSelectionItem.setToolTipText(getResourceString("BlockSelection")); //$NON-NLS-1$
    this.blockSelectionItem.addSelectionListener(widgetSelectedAdapter(
        event -> this.styledText.invokeAction(ST.TOGGLE_BLOCKSELECTION)));

    this.leftAlignmentItem = new ToolItem(alignmentToolBar, SWT.RADIO);
    this.leftAlignmentItem.setImage(this.iLeftAlignment);
    this.leftAlignmentItem.setToolTipText(getResourceString("AlignLeft")); //$NON-NLS-1$
    this.leftAlignmentItem.setSelection(true);
    this.leftAlignmentItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final Point selection = this.styledText.getSelection();
      final int lineStart = this.styledText.getLineAtOffset(selection.x);
      final int lineEnd = this.styledText.getLineAtOffset(selection.y);
      this.styledText.setLineAlignment(lineStart, (lineEnd - lineStart) + 1, SWT.LEFT);
    }));
    this.leftAlignmentItem.setEnabled(false);

    this.centerAlignmentItem = new ToolItem(alignmentToolBar, SWT.RADIO);
    this.centerAlignmentItem.setImage(this.iCenterAlignment);
    this.centerAlignmentItem.setToolTipText(getResourceString("Center_menuitem")); //$NON-NLS-1$
    this.centerAlignmentItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final Point selection = this.styledText.getSelection();
      final int lineStart = this.styledText.getLineAtOffset(selection.x);
      final int lineEnd = this.styledText.getLineAtOffset(selection.y);
      this.styledText.setLineAlignment(lineStart, (lineEnd - lineStart) + 1,
          SWT.CENTER);
    }));
    this.centerAlignmentItem.setEnabled(false);

    this.rightAlignmentItem = new ToolItem(alignmentToolBar, SWT.RADIO);
    this.rightAlignmentItem.setImage(this.iRightAlignment);
    this.rightAlignmentItem.setToolTipText(getResourceString("AlignRight")); //$NON-NLS-1$
    this.rightAlignmentItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final Point selection = this.styledText.getSelection();
      final int lineStart = this.styledText.getLineAtOffset(selection.x);
      final int lineEnd = this.styledText.getLineAtOffset(selection.y);
      this.styledText.setLineAlignment(lineStart, (lineEnd - lineStart) + 1,
          SWT.RIGHT);
    }));
    this.rightAlignmentItem.setEnabled(false);

    this.justifyAlignmentItem = new ToolItem(alignmentToolBar, SWT.CHECK);
    this.justifyAlignmentItem.setImage(this.iJustifyAlignment);
    this.justifyAlignmentItem.setToolTipText(getResourceString("Justify")); //$NON-NLS-1$
    this.justifyAlignmentItem.addSelectionListener(widgetSelectedAdapter(event -> {
      final Point selection = this.styledText.getSelection();
      final int lineStart = this.styledText.getLineAtOffset(selection.x);
      final int lineEnd = this.styledText.getLineAtOffset(selection.y);
      this.styledText.setLineJustify(lineStart, (lineEnd - lineStart) + 1,
          this.justifyAlignmentItem.getSelection());
    }));
    this.justifyAlignmentItem.setEnabled(false);

    final ToolItem bulletListItem = new ToolItem(alignmentToolBar, SWT.PUSH);
    bulletListItem.setImage(this.iBulletList);
    bulletListItem.setToolTipText(getResourceString("BulletList")); //$NON-NLS-1$
    bulletListItem.addSelectionListener(
        widgetSelectedAdapter(event -> this.setBullet(ST.BULLET_DOT)));

    final ToolItem numberedListItem = new ToolItem(alignmentToolBar, SWT.PUSH);
    numberedListItem.setImage(this.iNumberedList);
    numberedListItem.setToolTipText(getResourceString("NumberedList")); //$NON-NLS-1$
    numberedListItem.addSelectionListener(widgetSelectedAdapter(
        event -> this.setBullet(ST.BULLET_NUMBER | ST.BULLET_TEXT)));

    coolItem = new CoolItem(this.coolBar, SWT.NONE);
    coolItem.setControl(alignmentToolBar);
    composite = new Composite(this.coolBar, SWT.NONE);
    layout = new GridLayout(4, false);
    layout.marginHeight = 1;
    composite.setLayout(layout);
    Label label = new Label(composite, SWT.NONE);
    label.setText(getResourceString("Indent")); //$NON-NLS-1$
    final Spinner indent = new Spinner(composite, SWT.BORDER);
    indent.addSelectionListener(widgetSelectedAdapter(event -> {
      final Spinner spinner = (Spinner) event.widget;
      this.styledText.setIndent(spinner.getSelection());
    }));
    label = new Label(composite, SWT.NONE);
    label.setText(getResourceString("Spacing")); //$NON-NLS-1$
    final Spinner spacing = new Spinner(composite, SWT.BORDER);
    spacing.addSelectionListener(widgetSelectedAdapter(event -> {
      final Spinner spinner = (Spinner) event.widget;
      this.styledText.setLineSpacing(spinner.getSelection());
    }));

    coolItem = new CoolItem(this.coolBar, SWT.NONE);
    coolItem.setControl(composite);

    // Button to toggle Mouse Navigator in StyledText
    composite = new Composite(this.coolBar, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    final Button mouseNavigator = new Button(composite, SWT.CHECK);
    mouseNavigator.setText(getResourceString("MouseNav"));
    mouseNavigator
        .addSelectionListener(widgetSelectedAdapter(event -> this.styledText
            .setMouseNavigatorEnabled(mouseNavigator.getSelection())));
    coolItem = new CoolItem(this.coolBar, SWT.NONE);
    coolItem.setControl(composite);

    // Compute Size for various CoolItems
    final CoolItem[] coolItems = this.coolBar.getItems();
    for (final CoolItem item : coolItems) {
      final Control control = item.getControl();
      Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
      item.setMinimumSize(size);
      size = item.computeSize(size.x, size.y);
      item.setPreferredSize(size);
      item.setSize(size);
    }

    this.coolBar.addControlListener(
        ControlListener.controlResizedAdapter(event -> this.handleResize(event)));
  }

  void disposeRanges(final StyleRange[] ranges) {
    final StyleRange[] allRanges = this.styledText.getStyleRanges(0,
        this.styledText.getCharCount(), false);
    for (final StyleRange rangeToDispose : ranges) {
      boolean disposeFont = true;
      for (final StyleRange range : allRanges) {
        if (disposeFont && (rangeToDispose.font == range.font)) {
          disposeFont = false;
          break;
        }
      }
      if (disposeFont && (rangeToDispose.font != this.textFont)
          && (rangeToDispose.font != null)) {
        rangeToDispose.font.dispose();
      }

      final Object data = rangeToDispose.data;
      if (data != null) {
        if (data instanceof Image) {
          ((Image) data).dispose();
        }
        if (data instanceof Control) {
          ((Control) data).dispose();
        }
      }
    }
  }

  void disposeResource(final Font font) {
    if (font == null) {
      return;
    }
    final StyleRange[] styles = this.styledText.getStyleRanges(0,
        this.styledText.getCharCount(), false);
    int index = 0;
    while (index < styles.length) {
      if (styles[index].font == font) {
        break;
      }
      index++;
    }
    if (index == styles.length) {
      font.dispose();
    }
  }

  String[] getFontNames() {
    final FontData[] fontNames = this.display.getFontList(null, true);
    String[] names = new String[fontNames.length];
    int count = 0;
    mainfor: for (final FontData fontData : fontNames) {
      final String fontName = fontData.getName();
      if (fontName.startsWith("@")) {
        continue;
      }
      for (int j = 0; j < count; j++) {
        if (names[j].equals(fontName)) {
          continue mainfor;
        }
      }
      names[count++] = fontName;
    }
    if (count < names.length) {
      final String[] newNames = new String[count];
      System.arraycopy(names, 0, newNames, 0, count);
      names = newNames;
    }
    return names;
  }

  StyleRange[] getStyles(final InputStream stream) {
    try {
      StyleRange[] styles = new StyleRange[256];
      int count = 0;
      final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      String line;
      while ((line = reader.readLine()) != null) {
        final StringTokenizer tokenizer = new StringTokenizer(line, ";", false); //$NON-NLS-1$
        final StyleRange range = new StyleRange();
        range.start = Integer.parseInt(tokenizer.nextToken());
        range.length = Integer.parseInt(tokenizer.nextToken());
        range.fontStyle = Integer.parseInt(tokenizer.nextToken());
        range.strikeout = tokenizer.nextToken().equals("true"); //$NON-NLS-1$
        range.underline = tokenizer.nextToken().equals("true"); //$NON-NLS-1$
        if (tokenizer.hasMoreTokens()) {
          final int red = Integer.parseInt(tokenizer.nextToken());
          final int green = Integer.parseInt(tokenizer.nextToken());
          final int blue = Integer.parseInt(tokenizer.nextToken());
          range.foreground = new Color(red, green, blue);
        }
        if (tokenizer.hasMoreTokens()) {
          final int red = Integer.parseInt(tokenizer.nextToken());
          final int green = Integer.parseInt(tokenizer.nextToken());
          final int blue = Integer.parseInt(tokenizer.nextToken());
          range.background = new Color(red, green, blue);
        }
        if (count >= styles.length) {
          final StyleRange[] newStyles = new StyleRange[styles.length + 256];
          System.arraycopy(styles, 0, newStyles, 0, styles.length);
          styles = newStyles;
        }
        styles[count++] = range;
      }
      if (count < styles.length) {
        final StyleRange[] newStyles = new StyleRange[count];
        System.arraycopy(styles, 0, newStyles, 0, count);
        styles = newStyles;
      }
      return styles;
    } catch (final IOException e) {
      this.showError(getResourceString("Error"), e.getMessage()); //$NON-NLS-1$
    }
    return null;
  }

  void handleKeyDown(final Event event) {
    if (event.keyCode == SWT.INSERT) {
      this.insert = !this.insert;
    }
  }

  void handleModify(final ModifyEvent event) {
    if ((this.newCharCount > 0) && (this.start >= 0)) {
      final StyleRange style = new StyleRange();
      if ((this.textFont != null) && !this.textFont.equals(this.styledText.getFont())) {
        style.font = this.textFont;
      } else {
        style.fontStyle = SWT.NONE;
        if (this.boldControl.getSelection()) {
          style.fontStyle |= SWT.BOLD;
        }
        if (this.italicControl.getSelection()) {
          style.fontStyle |= SWT.ITALIC;
        }
      }
      if ((this.styleState & FOREGROUND) != 0) {
        style.foreground = this.textForeground;
      }
      if ((this.styleState & BACKGROUND) != 0) {
        style.background = this.textBackground;
      }
      final int underlineStyle = this.styleState & UNDERLINE;
      if (underlineStyle != 0) {
        style.underline = true;
        style.underlineColor = this.underlineColor;
        switch (underlineStyle) {
        case UNDERLINE_SINGLE:
          style.underlineStyle = SWT.UNDERLINE_SINGLE;
          break;
        case UNDERLINE_DOUBLE:
          style.underlineStyle = SWT.UNDERLINE_DOUBLE;
          break;
        case UNDERLINE_SQUIGGLE:
          style.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
          break;
        case UNDERLINE_ERROR:
          style.underlineStyle = SWT.UNDERLINE_ERROR;
          break;
        case UNDERLINE_LINK: {
          style.underlineColor = null;
          if ((this.link != null) && (this.link.length() > 0)) {
            style.underlineStyle = SWT.UNDERLINE_LINK;
            style.data = this.link;
          } else {
            style.underline = false;
          }
          break;
        }
        }
      }
      if ((this.styleState & STRIKEOUT) != 0) {
        style.strikeout = true;
        style.strikeoutColor = this.strikeoutColor;
      }
      final int borderStyle = this.styleState & BORDER;
      if (borderStyle != 0) {
        style.borderColor = this.borderColor;
        switch (borderStyle) {
        case BORDER_DASH:
          style.borderStyle = SWT.BORDER_DASH;
          break;
        case BORDER_DOT:
          style.borderStyle = SWT.BORDER_DOT;
          break;
        case BORDER_SOLID:
          style.borderStyle = SWT.BORDER_SOLID;
          break;
        }
      }
      final int[] ranges = { this.start, this.newCharCount };
      final StyleRange[] styles = { style };
      this.styledText.setStyleRanges(this.start, this.newCharCount, ranges, styles);
    }
    this.disposeRanges(this.selectedRanges);
  }

  void handleMouseUp(final Event event) {
    if (this.link != null) {
      final int offset = this.styledText.getCaretOffset();
      final StyleRange range = offset > 0
          ? this.styledText.getStyleRangeAtOffset(offset - 1)
          : null;
      if (range != null) {
        if (this.link == range.data) {
          final Shell dialog = new Shell(this.shell);
          dialog.setLayout(new FillLayout());
          dialog.setText(getResourceString("Browser")); //$NON-NLS-1$
          final Browser browser = new Browser(dialog, SWT.NONE);
          browser.setUrl(this.link);
          dialog.open();
        }
      }
    }
  }

  void handlePaintObject(final PaintObjectEvent event) {
    final GC gc = event.gc;
    final StyleRange style = event.style;
    final Object data = style.data;
    if (data instanceof Image) {
      final Image image = (Image) data;
      final int x = event.x;
      final int y = (event.y + event.ascent) - style.metrics.ascent;
      gc.drawImage(image, x, y);
    }
    if (data instanceof Control) {
      final Control control = (Control) data;
      final Point pt = control.getSize();
      final int x = event.x + MARGIN;
      final int y = (event.y + event.ascent) - ((2 * pt.y) / 3);
      control.setLocation(x, y);
    }
  }

  void handleResize(final ControlEvent event) {
    final Rectangle rect = this.shell.getClientArea();
    final Point cSize = this.coolBar.computeSize(rect.width, SWT.DEFAULT);
    final Point sSize = this.statusBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    final int statusMargin = 2;
    this.coolBar.setBounds(rect.x, rect.y, cSize.x, cSize.y);
    this.styledText.setBounds(rect.x, rect.y + cSize.y, rect.width,
        rect.height - cSize.y - (sSize.y + (2 * statusMargin)));
    this.statusBar.setBounds(rect.x + statusMargin,
        (rect.y + rect.height) - sSize.y - statusMargin,
        rect.width - (2 * statusMargin), sSize.y);
  }

  void handleVerifyText(final VerifyEvent event) {
    this.start = event.start;
    this.newCharCount = event.text.length();
    final int replaceCharCount = event.end - this.start;

    // mark styles to be disposed
    this.selectedRanges = this.styledText.getStyleRanges(this.start, replaceCharCount, false);
  }

  void initResources() {
    this.iBold = this.loadImage(this.display, "bold"); //$NON-NLS-1$
    this.iItalic = this.loadImage(this.display, "italic"); //$NON-NLS-1$
    this.iUnderline = this.loadImage(this.display, "underline"); //$NON-NLS-1$
    this.iStrikeout = this.loadImage(this.display, "strikeout"); //$NON-NLS-1$
    this.iBlockSelection = this.loadImage(this.display, "fullscrn"); //$NON-NLS-1$
    this.iBorderStyle = this.loadImage(this.display, "resize"); //$NON-NLS-1$
    this.iLeftAlignment = this.loadImage(this.display, "left"); //$NON-NLS-1$
    this.iRightAlignment = this.loadImage(this.display, "right"); //$NON-NLS-1$
    this.iCenterAlignment = this.loadImage(this.display, "center"); //$NON-NLS-1$
    this.iJustifyAlignment = this.loadImage(this.display, "justify"); //$NON-NLS-1$
    this.iCut = this.loadImage(this.display, "cut"); //$NON-NLS-1$
    this.iCopy = this.loadImage(this.display, "copy"); //$NON-NLS-1$
    this.iPaste = this.loadImage(this.display, "paste"); //$NON-NLS-1$
    this.iTextForeground = this.loadImage(this.display, "textForeground"); //$NON-NLS-1$
    this.iTextBackground = this.loadImage(this.display, "textBackground"); //$NON-NLS-1$
    this.iBaselineUp = this.loadImage(this.display, "font_big"); //$NON-NLS-1$
    this.iBaselineDown = this.loadImage(this.display, "font_sml"); //$NON-NLS-1$
    this.iBulletList = this.loadImage(this.display, "para_bul"); //$NON-NLS-1$
    this.iNumberedList = this.loadImage(this.display, "para_num"); //$NON-NLS-1$
    this.iLink = new Image(this.display, this.getClass().getResourceAsStream("link_obj.gif")); //$NON-NLS-1$
  }

  void installListeners() {
    this.styledText.addCaretListener(event -> {
      this.updateStatusBar();
      this.updateToolBar();
    });
    this.styledText.addListener(SWT.MouseUp, event -> this.handleMouseUp(event));
    this.styledText.addListener(SWT.KeyDown, event -> this.handleKeyDown(event));
    this.styledText.addVerifyListener(event -> this.handleVerifyText(event));
    this.styledText.addModifyListener(event -> this.handleModify(event));
    this.styledText.addPaintObjectListener(event -> this.handlePaintObject(event));
    this.styledText.addListener(SWT.Dispose, event -> {
      final StyleRange[] styles = this.styledText.getStyleRanges(0,
          this.styledText.getCharCount(), false);
      for (final StyleRange style : styles) {
        final Object data = style.data;
        if (data != null) {
          if (data instanceof Image) {
            ((Image) data).dispose();
          }
          if (data instanceof Control) {
            ((Control) data).dispose();
          }
        }
      }
    });
    this.shell.addControlListener(
        ControlListener.controlResizedAdapter(event -> this.handleResize(event)));
  }

  Image loadImage(final Display display, final String fileName) {
    Image image = null;
    try (InputStream sourceStream = this.getClass()
        .getResourceAsStream(fileName + ".ico")) { //$NON-NLS-1$
      final ImageData source = new ImageData(sourceStream);
      final ImageData mask = source.getTransparencyMask();
      image = new Image(display, source, mask);
    } catch (final IOException e) {
      this.showError(getResourceString("Error"), e.getMessage()); //$NON-NLS-1$
    }
    return image;
  }

  void loadProfile(final int profile) {
    try {
      switch (profile) {
      case 1: {
        final String text = this.openFile(
            TextEditor.class.getResourceAsStream("text.txt")); //$NON-NLS-1$
        final StyleRange[] styles = this.getStyles(
            TextEditor.class.getResourceAsStream("styles.txt")); //$NON-NLS-1$
        this.styledText.setText(text);
        if (styles != null) {
          this.styledText.setStyleRanges(styles);
        }
        break;
      }
      case 2: {
        this.styledText.setText(getResourceString("Profile2")); //$NON-NLS-1$
        break;
      }
      case 3: {
        final String text = this.openFile(
            TextEditor.class.getResourceAsStream("text4.txt")); //$NON-NLS-1$
        this.styledText.setText(text);
        break;
      }
      case 4: {
        this.styledText.setText(getResourceString("Profile4")); //$NON-NLS-1$
        break;
      }
      }
      this.updateToolBar();
    } catch (final Exception e) {
      this.showError(getResourceString("Error"), e.getMessage()); //$NON-NLS-1$
    }
  }

  String openFile(final InputStream stream) throws IOException {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    final StringBuilder buffer = new StringBuilder();
    String line;
    final String lineDelimiter = this.styledText.getLineDelimiter();
    while ((line = reader.readLine()) != null) {
      buffer.append(line);
      buffer.append(lineDelimiter);
    }
    return buffer.toString();
  }

  void releaseResources() {
    this.iBold.dispose();
    this.iBold = null;
    this.iItalic.dispose();
    this.iItalic = null;
    this.iUnderline.dispose();
    this.iUnderline = null;
    this.iStrikeout.dispose();
    this.iStrikeout = null;
    this.iBorderStyle.dispose();
    this.iBorderStyle = null;
    this.iBlockSelection.dispose();
    this.iBlockSelection = null;
    this.iLeftAlignment.dispose();
    this.iLeftAlignment = null;
    this.iRightAlignment.dispose();
    this.iRightAlignment = null;
    this.iCenterAlignment.dispose();
    this.iCenterAlignment = null;
    this.iJustifyAlignment.dispose();
    this.iJustifyAlignment = null;
    this.iCut.dispose();
    this.iCut = null;
    this.iCopy.dispose();
    this.iCopy = null;
    this.iPaste.dispose();
    this.iPaste = null;
    this.iTextForeground.dispose();
    this.iTextForeground = null;
    this.iTextBackground.dispose();
    this.iTextBackground = null;
    this.iBaselineUp.dispose();
    this.iBaselineUp = null;
    this.iBaselineDown.dispose();
    this.iBaselineDown = null;
    this.iBulletList.dispose();
    this.iBulletList = null;
    this.iNumberedList.dispose();
    this.iNumberedList = null;
    this.iLink.dispose();
    this.iLink = null;

    if (this.textFont != null) {
      this.textFont.dispose();
    }
    this.textFont = null;
    this.textForeground = null;
    this.textBackground = null;
    this.strikeoutColor = null;
    this.underlineColor = null;
    this.borderColor = null;

    if (this.font != null) {
      this.font.dispose();
    }
    this.font = null;
  }

  void saveFile() {
    if (this.fileName != null) {
      try (FileWriter file = new FileWriter(this.fileName);) {
        file.write(this.styledText.getText());
      } catch (final IOException e) {
        this.showError(getResourceString("Error"), e.getMessage());
      }
    }
  }

  void setBullet(final int type) {
    final Point selection = this.styledText.getSelection();
    final int lineStart = this.styledText.getLineAtOffset(selection.x);
    final int lineEnd = this.styledText.getLineAtOffset(selection.y);
    final StyleRange styleRange = new StyleRange();
    styleRange.metrics = new GlyphMetrics(0, 0, BULLET_WIDTH);
    final Bullet bullet = new Bullet(type, styleRange);
    bullet.text = ".";
    for (int lineIndex = lineStart; lineIndex <= lineEnd; lineIndex++) {
      final Bullet oldBullet = this.styledText.getLineBullet(lineIndex);
      this.styledText.setLineBullet(lineIndex, 1, oldBullet != null ? null : bullet);
    }
  }

  void setLink() {
    final Shell dialog = new Shell(this.shell,
        SWT.APPLICATION_MODAL | SWT.SHELL_TRIM);
    dialog.setLayout(new GridLayout(2, false));
    dialog.setText(getResourceString("SetLink")); //$NON-NLS-1$
    final Label label = new Label(dialog, SWT.NONE);
    label.setText(getResourceString("URL")); //$NON-NLS-1$
    final Text text = new Text(dialog, SWT.SINGLE);
    text.setLayoutData(new GridData(200, SWT.DEFAULT));
    if (this.link != null) {
      text.setText(this.link);
      text.selectAll();
    }
    final Button okButton = new Button(dialog, SWT.PUSH);
    okButton.setText(getResourceString("Ok")); //$NON-NLS-1$
    final Button cancelButton = new Button(dialog, SWT.PUSH);
    cancelButton.setText(getResourceString("Cancel")); //$NON-NLS-1$
    final Listener listener = event -> {
      if (event.widget == okButton) {
        this.link = text.getText();
        this.setStyle(UNDERLINE_LINK);
      }
      dialog.dispose();
    };
    okButton.addListener(SWT.Selection, listener);
    cancelButton.addListener(SWT.Selection, listener);
    dialog.setDefaultButton(okButton);
    dialog.pack();
    dialog.open();
    while (!dialog.isDisposed()) {
      if (!this.display.readAndDispatch()) {
        this.display.sleep();
      }
    }
  }

  void setStyle(final int style) {
    final int[] ranges = this.styledText.getSelectionRanges();
    int i = 0;
    while (i < ranges.length) {
      this.setStyle(style, ranges[i++], ranges[i++]);
    }
    this.updateStyleState(style, FOREGROUND);
    this.updateStyleState(style, BACKGROUND);
    this.updateStyleState(style, UNDERLINE);
    this.updateStyleState(style, STRIKEOUT);
    this.updateStyleState(style, BORDER);
  }

  void setStyle(final int style, final int start, final int length) {
    if (length == 0) {
      return;
    }

    /* Create new style range */
    final StyleRange newRange = new StyleRange();
    if ((style & FONT) != 0) {
      newRange.font = this.textFont;
    }
    if ((style & FONT_STYLE) != 0) {
      newRange.fontStyle = style & FONT_STYLE;
    }
    if ((style & FOREGROUND) != 0) {
      newRange.foreground = this.textForeground;
    }
    if ((style & BACKGROUND) != 0) {
      newRange.background = this.textBackground;
    }
    if ((style & BASELINE_UP) != 0) {
      newRange.rise++;
    }
    if ((style & BASELINE_DOWN) != 0) {
      newRange.rise--;
    }
    if ((style & STRIKEOUT) != 0) {
      newRange.strikeout = true;
      newRange.strikeoutColor = this.strikeoutColor;
    }
    if ((style & UNDERLINE) != 0) {
      newRange.underline = true;
      newRange.underlineColor = this.underlineColor;
      switch (style & UNDERLINE) {
      case UNDERLINE_SINGLE:
        newRange.underlineStyle = SWT.UNDERLINE_SINGLE;
        break;
      case UNDERLINE_DOUBLE:
        newRange.underlineStyle = SWT.UNDERLINE_DOUBLE;
        break;
      case UNDERLINE_ERROR:
        newRange.underlineStyle = SWT.UNDERLINE_ERROR;
        break;
      case UNDERLINE_SQUIGGLE:
        newRange.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
        break;
      case UNDERLINE_LINK:
        newRange.underlineColor = null;
        if ((this.link != null) && (this.link.length() > 0)) {
          newRange.underlineStyle = SWT.UNDERLINE_LINK;
          newRange.data = this.link;
        } else {
          newRange.underline = false;
        }
        break;
      }
    }
    if ((style & BORDER) != 0) {
      switch (style & BORDER) {
      case BORDER_DASH:
        newRange.borderStyle = SWT.BORDER_DASH;
        break;
      case BORDER_DOT:
        newRange.borderStyle = SWT.BORDER_DOT;
        break;
      case BORDER_SOLID:
        newRange.borderStyle = SWT.BORDER_SOLID;
        break;
      }
      newRange.borderColor = this.borderColor;
    }

    int newRangeStart = start;
    int newRangeLength = length;
    final int[] ranges = this.styledText.getRanges(start, length);
    final StyleRange[] styles = this.styledText.getStyleRanges(start, length, false);
    final int maxCount = (ranges.length * 2) + 2;
    int[] newRanges = new int[maxCount];
    StyleRange[] newStyles = new StyleRange[maxCount / 2];
    int count = 0;
    for (int i = 0; i < ranges.length; i += 2) {
      final int rangeStart = ranges[i];
      final int rangeLength = ranges[i + 1];
      final StyleRange range = styles[i / 2];
      if (rangeStart > newRangeStart) {
        newRangeLength = rangeStart - newRangeStart;
        newRanges[count] = newRangeStart;
        newRanges[count + 1] = newRangeLength;
        newStyles[count / 2] = newRange;
        count += 2;
      }
      newRangeStart = rangeStart + rangeLength;
      newRangeLength = (start + length) - newRangeStart;

      /* Create merged style range */
      final StyleRange mergedRange = new StyleRange(range);
      // Note: fontStyle is not copied by the constructor
      mergedRange.fontStyle = range.fontStyle;
      if ((style & FONT) != 0) {
        mergedRange.font = newRange.font;
      }
      if ((style & FONT_STYLE) != 0) {
        mergedRange.fontStyle = range.fontStyle ^ newRange.fontStyle;
      }
      if ((mergedRange.font != null)
          && (((style & FONT) != 0) || ((style & FONT_STYLE) != 0))) {
        boolean change = false;
        final FontData[] fds = mergedRange.font.getFontData();
        for (final FontData fd : fds) {
          if (fd.getStyle() != mergedRange.fontStyle) {
            fd.setStyle(mergedRange.fontStyle);
            change = true;
          }
        }
        if (change) {
          mergedRange.font = new Font(this.display, fds);
        }
      }
      if ((style & FOREGROUND) != 0) {
        mergedRange.foreground = newRange.foreground != range.foreground
            ? newRange.foreground
            : null;
      }
      if ((style & BACKGROUND) != 0) {
        mergedRange.background = newRange.background != range.background
            ? newRange.background
            : null;
      }
      if ((style & BASELINE_UP) != 0) {
        mergedRange.rise++;
      }
      if ((style & BASELINE_DOWN) != 0) {
        mergedRange.rise--;
      }
      if ((style & STRIKEOUT) != 0) {
        mergedRange.strikeout = !range.strikeout
            || (range.strikeoutColor != newRange.strikeoutColor);
        mergedRange.strikeoutColor = mergedRange.strikeout
            ? newRange.strikeoutColor
            : null;
      }
      if ((style & UNDERLINE) != 0) {
        if ((style & UNDERLINE_LINK) != 0) {
          if ((this.link != null) && (this.link.length() > 0)) {
            mergedRange.underline = !range.underline
                || (range.underlineStyle != newRange.underlineStyle)
                || (range.data != newRange.data);
          } else {
            mergedRange.underline = false;
          }
          mergedRange.underlineColor = null;
        } else {
          mergedRange.underline = !range.underline
              || (range.underlineStyle != newRange.underlineStyle)
              || (range.underlineColor != newRange.underlineColor);
          mergedRange.underlineColor = mergedRange.underline
              ? newRange.underlineColor
              : null;
        }
        mergedRange.underlineStyle = mergedRange.underline
            ? newRange.underlineStyle
            : SWT.NONE;
        mergedRange.data = mergedRange.underline ? newRange.data : null;
      }
      if ((style & BORDER) != 0) {
        if ((range.borderStyle != newRange.borderStyle)
            || (range.borderColor != newRange.borderColor)) {
          mergedRange.borderStyle = newRange.borderStyle;
          mergedRange.borderColor = newRange.borderColor;
        } else {
          mergedRange.borderStyle = SWT.NONE;
          mergedRange.borderColor = null;
        }
      }

      newRanges[count] = rangeStart;
      newRanges[count + 1] = rangeLength;
      newStyles[count / 2] = mergedRange;
      count += 2;
    }
    if (newRangeLength > 0) {
      newRanges[count] = newRangeStart;
      newRanges[count + 1] = newRangeLength;
      newStyles[count / 2] = newRange;
      count += 2;
    }
    if ((0 < count) && (count < maxCount)) {
      final int[] tmpRanges = new int[count];
      final StyleRange[] tmpStyles = new StyleRange[count / 2];
      System.arraycopy(newRanges, 0, tmpRanges, 0, count);
      System.arraycopy(newStyles, 0, tmpStyles, 0, count / 2);
      newRanges = tmpRanges;
      newStyles = tmpStyles;
    }
    this.styledText.setStyleRanges(start, length, newRanges, newStyles);
    this.disposeRanges(styles);
  }

  void showError(final String title, final String message) {
    final MessageBox messageBox = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.CLOSE);
    messageBox.setText(title);
    messageBox.setMessage(message);
    messageBox.open();
  }

  void updateStatusBar() {
    final int offset = this.styledText.getCaretOffset();
    final int lineIndex = this.styledText.getLineAtOffset(offset);
    final String insertLabel = getResourceString(this.insert ? "Insert" : "Overwrite"); //$NON-NLS-1$ //$NON-NLS-2$
    this.statusBar.setText(getResourceString("Offset") //$NON-NLS-1$
        + offset + " " //$NON-NLS-1$
        + getResourceString("Line") //$NON-NLS-1$
        + lineIndex + "\t" //$NON-NLS-1$
        + insertLabel);
  }

  void updateStyleState(final int style, final int changingStyle) {
    if ((style & changingStyle) != 0) {
      if ((style & changingStyle) == (this.styleState & changingStyle)) {
        this.styleState &= ~changingStyle;
      } else {
        this.styleState &= ~changingStyle;
        this.styleState |= style;
      }
    }
  }

  void updateToolBar() {
    this.styleState = 0;
    this.link = null;
    boolean bold = false, italic = false;
    Font font = null;

    final int offset = this.styledText.getCaretOffset();
    final StyleRange range = offset > 0 ? this.styledText.getStyleRangeAtOffset(offset - 1)
        : null;
    if (range != null) {
      if (range.font != null) {
        font = range.font;
        final FontData[] fds = font.getFontData();
        for (final FontData fd : fds) {
          final int fontStyle = fd.getStyle();
          if (!bold && ((fontStyle & SWT.BOLD) != 0)) {
            bold = true;
          }
          if (!italic && ((fontStyle & SWT.ITALIC) != 0)) {
            italic = true;
          }
        }
      } else {
        bold = (range.fontStyle & SWT.BOLD) != 0;
        italic = (range.fontStyle & SWT.ITALIC) != 0;
      }
      if (range.foreground != null) {
        this.styleState |= FOREGROUND;
        if (this.textForeground != range.foreground) {
          this.textForeground = range.foreground;
        }
      }
      if (range.background != null) {
        this.styleState |= BACKGROUND;
        if (this.textBackground != range.background) {
          this.textBackground = range.background;
        }
      }
      if (range.underline) {
        switch (range.underlineStyle) {
        case SWT.UNDERLINE_SINGLE:
          this.styleState |= UNDERLINE_SINGLE;
          break;
        case SWT.UNDERLINE_DOUBLE:
          this.styleState |= UNDERLINE_DOUBLE;
          break;
        case SWT.UNDERLINE_SQUIGGLE:
          this.styleState |= UNDERLINE_SQUIGGLE;
          break;
        case SWT.UNDERLINE_ERROR:
          this.styleState |= UNDERLINE_ERROR;
          break;
        case SWT.UNDERLINE_LINK:
          this.styleState |= UNDERLINE_LINK;
          this.link = (String) range.data;
          break;
        }
        if (range.underlineStyle != SWT.UNDERLINE_LINK) {
          this.underlineSingleItem
              .setSelection((this.styleState & UNDERLINE_SINGLE) != 0);
          this.underlineDoubleItem
              .setSelection((this.styleState & UNDERLINE_DOUBLE) != 0);
          this.underlineErrorItem.setSelection((this.styleState & UNDERLINE_ERROR) != 0);
          this.underlineSquiggleItem
              .setSelection((this.styleState & UNDERLINE_SQUIGGLE) != 0);
          this.underlineColor = range.underlineColor;
        }
      }
      if (range.strikeout) {
        this.styleState |= STRIKEOUT;
        this.strikeoutColor = range.strikeoutColor;
      }
      if (range.borderStyle != SWT.NONE) {
        switch (range.borderStyle) {
        case SWT.BORDER_SOLID:
          this.styleState |= BORDER_SOLID;
          break;
        case SWT.BORDER_DASH:
          this.styleState |= BORDER_DASH;
          break;
        case SWT.BORDER_DOT:
          this.styleState |= BORDER_DOT;
          break;
        }
        this.borderSolidItem.setSelection((this.styleState & BORDER_SOLID) != 0);
        this.borderDashItem.setSelection((this.styleState & BORDER_DASH) != 0);
        this.borderDotItem.setSelection((this.styleState & BORDER_DOT) != 0);
        this.borderColor = range.borderColor;
      }
    }

    this.boldControl.setSelection(bold);
    this.italicControl.setSelection(italic);
    final FontData fontData = font != null ? font.getFontData()[0]
        : this.styledText.getFont().getFontData()[0];
    int index = 0;
    int count = this.fontNameControl.getItemCount();
    final String fontName = fontData.getName();
    while (index < count) {
      if (this.fontNameControl.getItem(index).equals(fontName)) {
        this.fontNameControl.select(index);
        break;
      }
      index++;
    }
    index = 0;
    count = this.fontSizeControl.getItemCount();
    final int fontSize = fontData.getHeight();
    while (index < count) {
      final int size = Integer.parseInt(this.fontSizeControl.getItem(index));
      if (fontSize == size) {
        this.fontSizeControl.select(index);
        break;
      }
      if (size > fontSize) {
        this.fontSizeControl.add(String.valueOf(fontSize), index);
        this.fontSizeControl.select(index);
        break;
      }
      index++;
    }

    this.disposeResource(this.textFont);
    this.textFont = font;
    final int lineIndex = this.styledText.getLineAtOffset(offset);
    final int alignment = this.styledText.getLineAlignment(lineIndex);
    this.leftAlignmentItem.setSelection((alignment & SWT.LEFT) != 0);
    this.centerAlignmentItem.setSelection((alignment & SWT.CENTER) != 0);
    this.rightAlignmentItem.setSelection((alignment & SWT.RIGHT) != 0);
    final boolean justify = this.styledText.getLineJustify(lineIndex);
    this.justifyAlignmentItem.setSelection(justify);
  }
}