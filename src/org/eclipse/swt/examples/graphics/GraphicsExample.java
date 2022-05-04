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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class is the main class of the graphics application. Various "tabs" are
 * created and made visible by this class.
 */
public class GraphicsExample {

  Composite parent;
  GraphicsTab[] tabs; // tabs to be found in the application
  GraphicsTab tab; // the current tab
  GraphicsBackground background; // used to store information about the
                                 // background

  ToolBar toolBar; // toolbar that contains backItem and dbItem
  Tree tabList; // tree structure of tabs
  Text tabDesc; // multi-line text widget that displays a tab description
  Sash hSash, vSash;
  Canvas canvas;
  Composite tabControlPanel;
  ToolItem backItem, dbItem; // background, double buffer items
  Menu backMenu; // background menu item

  List<Image> resources; // stores resources that will be disposed
  List<GraphicsTab> tabs_in_order; // stores GraphicsTabs in the order that they
                                   // appear in the tree
  boolean animate = true; // whether animation should happen

  static boolean advanceGraphics, advanceGraphicsInit;

  static final int MARGIN = 5;
  static final int SASH_SPACING = 1;
  static final int TIMER = 30;
  static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle("examples_graphics"); //$NON-NLS-1$

  /*
   * Default constructor is needed so that example launcher can create an
   * instance.
   */
  public GraphicsExample() {
    super();
  }

  public GraphicsExample(final Composite parent) {
    this.parent = parent;
    this.resources = new ArrayList<>();
    this.createControls(parent);
    this.setTab(this.tab);
    this.startAnimationTimer();
  }

  boolean checkAdvancedGraphics() {
    if (advanceGraphicsInit) {
      return advanceGraphics;
    }
    advanceGraphicsInit = true;
    final Display display = this.parent.getDisplay();
    try {
      final Path path = new Path(display);
      path.dispose();
    } catch (final SWTException e) {
      Shell shell = display.getActiveShell(), newShell = null;
      if (shell == null) {
        shell = newShell = new Shell(display);
      }
      final MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
      dialog.setText(RESOURCE_BUNDLE.getString("Warning")); //$NON-NLS-1$
      dialog.setMessage(RESOURCE_BUNDLE.getString("LibNotFound")); //$NON-NLS-1$
      dialog.open();
      if (newShell != null) {
        newShell.dispose();
      }
      return false;
    }
    return advanceGraphics = true;
  }

  void createControls(final Composite parent) {
    this.tabs = this.createTabs();
    this.createToolBar(parent);
    this.createTabList(parent);
    this.hSash = new Sash(parent, SWT.HORIZONTAL);
    this.createTabDesc(parent);
    this.vSash = new Sash(parent, SWT.VERTICAL);
    this.createCanvas(parent);
    this.createControlPanel(parent);

    FormData data;
    final FormLayout layout = new FormLayout();
    parent.setLayout(layout);

    data = new FormData();
    data.left = new FormAttachment(0, MARGIN);
    data.top = new FormAttachment(0, MARGIN);
    data.right = new FormAttachment(100, -MARGIN);
    this.toolBar.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(0, MARGIN);
    data.top = new FormAttachment(this.toolBar, MARGIN);
    data.right = new FormAttachment(this.vSash, -SASH_SPACING);
    data.bottom = new FormAttachment(this.hSash, -SASH_SPACING);
    this.tabList.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(0, MARGIN);
    final int offset = parent.getBounds().height
        - this.tabDesc.computeSize(SWT.DEFAULT, this.tabDesc.getLineHeight() * 10).y;
    data.top = new FormAttachment(null, offset);
    data.right = new FormAttachment(this.vSash, -SASH_SPACING);
    this.hSash.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(0, MARGIN);
    data.top = new FormAttachment(this.hSash, SASH_SPACING);
    data.right = new FormAttachment(this.vSash, -SASH_SPACING);
    data.bottom = new FormAttachment(100, -MARGIN);
    this.tabDesc.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(null,
        this.tabList.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 50);
    data.top = new FormAttachment(this.toolBar, MARGIN);
    data.bottom = new FormAttachment(100, -MARGIN);
    this.vSash.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(this.vSash, SASH_SPACING);
    data.top = new FormAttachment(this.toolBar, MARGIN);
    data.right = new FormAttachment(100, -MARGIN);
    data.bottom = new FormAttachment(this.tabControlPanel);
    this.canvas.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(this.vSash, SASH_SPACING);
    data.right = new FormAttachment(100, -MARGIN);
    data.bottom = new FormAttachment(100, -MARGIN);
    this.tabControlPanel.setLayoutData(data);

    this.vSash.addListener(SWT.Selection, event -> {
      final Rectangle rect = this.hSash.getParent().getClientArea();
      event.x = Math.min(Math.max(event.x, 60), rect.width - 60);
      if (event.detail != SWT.DRAG) {
        final FormData data1 = (FormData) this.vSash.getLayoutData();
        data1.left.offset = event.x;
        this.vSash.requestLayout();
        this.animate = true;
      } else {
        this.animate = false;
      }
    });
    this.hSash.addListener(SWT.Selection, event -> {
      final Rectangle rect = this.vSash.getParent().getClientArea();
      event.y = Math.min(Math.max(event.y, this.tabList.getLocation().y + 60),
          rect.height - 60);
      if (event.detail != SWT.DRAG) {
        final FormData data1 = (FormData) this.hSash.getLayoutData();
        data1.top.offset = event.y;
        this.hSash.requestLayout();
      }
    });
  }

  void createCanvas(final Composite parent) {
    int style = SWT.NO_BACKGROUND;
    if (this.dbItem.getSelection()) {
      style |= SWT.DOUBLE_BUFFERED;
    }
    this.canvas = new Canvas(parent, style);
    this.canvas.addListener(SWT.Paint, event -> {
      final GC gc = event.gc;
      final Rectangle rect = this.canvas.getClientArea();
      final Device device = gc.getDevice();
      Pattern pattern = null;
      if (this.background.getBgColor1() != null) {
        if (this.background.getBgColor2() != null) { // gradient
          pattern = new Pattern(device, 0, 0, rect.width, rect.height,
              this.background.getBgColor1(), this.background.getBgColor2());
          gc.setBackgroundPattern(pattern);
        } else { // solid color
          gc.setBackground(this.background.getBgColor1());
        }
      } else if (this.background.getBgImage() != null) { // image
        pattern = new Pattern(device, this.background.getBgImage());
        gc.setBackgroundPattern(pattern);
      }
      gc.fillRectangle(rect);
      final GraphicsTab tab = this.getTab();
      if (tab != null) {
        tab.paint(gc, rect.width, rect.height);
      }
      if (pattern != null) {
        pattern.dispose();
      }
    });
  }

  void recreateCanvas() {
    if (this.dbItem
        .getSelection() == ((this.canvas.getStyle() & SWT.DOUBLE_BUFFERED) != 0)) {
      return;
    }
    final Object data = this.canvas.getLayoutData();
    if (this.canvas != null) {
      this.canvas.dispose();
    }
    this.createCanvas(this.parent);
    this.canvas.setLayoutData(data);
    this.parent.layout(true, true);
  }

  /**
   * Creates the control panel
   *
   * @param parent
   */
  void createControlPanel(final Composite parent) {
    Group group;
    this.tabControlPanel = group = new Group(parent, SWT.NONE);
    group.setText(getResourceString("Settings")); //$NON-NLS-1$
    this.tabControlPanel.setLayout(new RowLayout());
  }

  void createToolBar(final Composite parent) {
    final Display display = parent.getDisplay();

    this.toolBar = new ToolBar(parent, SWT.FLAT);

    final ToolItem back = new ToolItem(this.toolBar, SWT.PUSH);
    back.setText(getResourceString("Back")); //$NON-NLS-1$
    back.setImage(this.loadImage(display, "back.gif")); //$NON-NLS-1$

    back.addListener(SWT.Selection, event -> {
      int index = this.tabs_in_order.indexOf(this.tab) - 1;
      if (index < 0) {
        index = this.tabs_in_order.size() - 1;
      }
      this.setTab(this.tabs_in_order.get(index));
    });

    final ToolItem next = new ToolItem(this.toolBar, SWT.PUSH);
    next.setText(getResourceString("Next")); //$NON-NLS-1$
    next.setImage(this.loadImage(display, "next.gif")); //$NON-NLS-1$
    next.addListener(SWT.Selection, event -> {
      final int index = (this.tabs_in_order.indexOf(this.tab) + 1) % this.tabs_in_order.size();
      this.setTab(this.tabs_in_order.get(index));
    });

    final ColorMenu colorMenu = new ColorMenu();

    // setup items to be contained in the background menu
    colorMenu.setColorItems(true);
    colorMenu.setPatternItems(this.checkAdvancedGraphics());
    colorMenu.setGradientItems(this.checkAdvancedGraphics());

    // create the background menu
    this.backMenu = colorMenu.createMenu(parent, gb -> {
      this.background = gb;
      this.backItem.setImage(gb.getThumbNail());
      if (this.canvas != null) {
        this.canvas.redraw();
      }
    });

    // initialize the background to the first item in the menu
    this.background = (GraphicsBackground) this.backMenu.getItem(0).getData();

    // background tool item
    this.backItem = new ToolItem(this.toolBar, SWT.PUSH);
    this.backItem.setText(getResourceString("Background")); //$NON-NLS-1$
    this.backItem.setImage(this.background.getThumbNail());
    this.backItem.addListener(SWT.Selection, event -> {
      if (event.widget == this.backItem) {
        final ToolItem toolItem = (ToolItem) event.widget;
        final ToolBar toolBar = toolItem.getParent();
        final Rectangle toolItemBounds = toolItem.getBounds();
        final Point point = toolBar
            .toDisplay(new Point(toolItemBounds.x, toolItemBounds.y));
        this.backMenu.setLocation(point.x, point.y + toolItemBounds.height);
        this.backMenu.setVisible(true);
      }
    });

    // double buffer tool item
    this.dbItem = new ToolItem(this.toolBar, SWT.CHECK);
    this.dbItem.setText(getResourceString("DoubleBuffer")); //$NON-NLS-1$
    this.dbItem.setImage(this.loadImage(display, "db.gif")); //$NON-NLS-1$
    this.dbItem.addListener(SWT.Selection,
        event -> this.setDoubleBuffered(this.dbItem.getSelection()));
  }

  /**
   * Creates and returns a thumbnail image.
   *
   * @param device a device
   * @param name   filename of the image
   */
  static Image createThumbnail(final Device device, final String name) {
    final Image image = new Image(device, name);
    final Rectangle src = image.getBounds();
    Image result = null;
    if ((src.width != 16) || (src.height != 16)) {
      result = new Image(device, 16, 16);
      final GC gc = new GC(result);
      final Rectangle dest = result.getBounds();
      gc.drawImage(image, src.x, src.y, src.width, src.height, dest.x, dest.y,
          dest.width, dest.height);
      gc.dispose();
    }
    if (result != null) {
      image.dispose();
      return result;
    }
    return image;
  }

  /**
   * Creates an image based on a gradient pattern made up of two colors.
   *
   * @param device - The Device
   * @param color1 - The first color used to create the image
   * @param color2 - The second color used to create the image
   *
   */
  static Image createImage(final Device device, final Color color1, final Color color2, final int width,
      final int height) {
    final Image image = new Image(device, width, height);
    final GC gc = new GC(image);
    final Rectangle rect = image.getBounds();
    final Pattern pattern = new Pattern(device, rect.x, rect.y, rect.width - 1,
        rect.height - 1, color1, color2);
    gc.setBackgroundPattern(pattern);
    gc.fillRectangle(rect);
    gc.drawRectangle(rect.x, rect.y, rect.width - 1, rect.height - 1);
    gc.dispose();
    pattern.dispose();
    return image;
  }

  /**
   * Creates an image based on the color provided and returns it.
   *
   * @param device - The Device
   * @param color  - The color used to create the image
   *
   */
  static Image createImage(final Device device, final Color color) {
    final Image image = new Image(device, 16, 16);
    final GC gc = new GC(image);
    gc.setBackground(color);
    final Rectangle rect = image.getBounds();
    gc.fillRectangle(rect);
    if (color.equals(device.getSystemColor(SWT.COLOR_BLACK))) {
      gc.setForeground(device.getSystemColor(SWT.COLOR_WHITE));
    }
    gc.drawRectangle(rect.x, rect.y, rect.width - 1, rect.height - 1);
    gc.dispose();
    return image;
  }

  void createTabList(final Composite parent) {
    this.tabList = new Tree(parent,
        SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    Arrays.sort(this.tabs, (tab0, tab1) -> tab0.getText().compareTo(tab1.getText()));
    final HashSet<String> set = new HashSet<>();
    for (final GraphicsTab tab : this.tabs) {
      set.add(tab.getCategory());
    }
    final String[] categories = new String[set.size()];
    set.toArray(categories);
    Arrays.sort(categories);
    for (final String text : categories) {
      final TreeItem item = new TreeItem(this.tabList, SWT.NONE);
      item.setText(text);
    }
    this.tabs_in_order = new ArrayList<>();
    final TreeItem[] items = this.tabList.getItems();
    for (final TreeItem item : items) {
      for (final GraphicsTab tab : this.tabs) {
        if (item.getText().equals(tab.getCategory())) {
          final TreeItem item1 = new TreeItem(item, SWT.NONE);
          item1.setText(tab.getText());
          item1.setData(tab);
          this.tabs_in_order.add(tab);
        }
      }
    }
    this.tabList.addListener(SWT.Selection, event -> {
      final TreeItem item = (TreeItem) event.item;
      if (item != null) {
        final GraphicsTab gt = (GraphicsTab) item.getData();
        if (gt == this.tab) {
          return;
        }
        this.setTab((GraphicsTab) item.getData());
      }
    });
  }

  /**
   * Creates the multi-line text widget that will contain the tab description.
   */
  void createTabDesc(final Composite parent) {
    this.tabDesc = new Text(parent,
        SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP | SWT.BORDER);
    this.tabDesc.setEditable(false);
    this.tabDesc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  /**
   * Initializes the GraphicsTab instances that will be contained in
   * GraphicsExample.
   */
  GraphicsTab[] createTabs() {
    return new GraphicsTab[] { new LineTab(this), new StarPolyTab(this),
        this.tab = new IntroTab(this), new BlackHoleTab(this), new AlphaTab(this),
        new BallTab(this), new CountDownTab(this), new CurvesSWTTab(this),
        new CurvesTab(this), new CustomFontTab(this), new FontBounceTab(this),
        new GradientTab(this), new ImageTransformTab(this), new ShapesTab(this),
        new MazeTab(this), new RGBTab(this), new SpiralTab(this),
        new CardsTab(this), new LineCapTab(this), new InterpolationTab(this),
        new PathClippingTab(this), new PathClippingAnimTab(this),
        new LineStyleTab(this), new LineJoinTab(this),
        new RegionClippingTab(this), new CustomAlphaTab(this),
        new TextAntialiasTab(this), new GraphicAntialiasTab(this),
        new ImageFlipTab(this), new ImageScaleTab(this), new PathTab(this), };
  }

  /**
   * Disposes all resources created by the receiver.
   */
  public void dispose() {
    if (this.tabs != null) {
      for (final GraphicsTab tab : this.tabs) {
        tab.dispose();
      }
    }
    this.tabs = null;
    if (this.resources != null) {
      for (final Image image : this.resources) {
        if (image != null) {
          image.dispose();
        }
      }
    }
    this.resources = null;

    if (this.backMenu != null) {
      this.backMenu.dispose();
      this.backMenu = null;
    }
  }

  TreeItem findItemByData(final TreeItem[] items, final Object data) {
    for (TreeItem item : items) {
      if (item.getData() == data) {
        return item;
      }
      item = this.findItemByData(item.getItems(), data);
      if (item != null) {
        return item;
      }
    }
    return null;
  }

  /**
   * Gets the current tab.
   */
  public GraphicsTab getTab() {
    return this.tab;
  }

  /**
   * Gets a string from the resource bundle. We don't want to crash because of a
   * missing String. Returns the key if not found.
   */
  static String getResourceString(final String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (final MissingResourceException e) {
      return key;
    } catch (final NullPointerException e) {
      return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  static Image loadImage(final Device device, final Class<GraphicsExample> clazz,
      final String string) {
    final InputStream stream = clazz.getResourceAsStream(string);
    if (stream == null) {
      return null;
    }
    Image image = null;
    try {
      image = new Image(device, stream);
    } catch (final SWTException ex) {
    } finally {
      try {
        stream.close();
      } catch (final IOException ex) {
      }
    }
    return image;
  }

  Image loadImage(final Device device, final String name) {
    final Image image = loadImage(device, GraphicsExample.class, name);
    if (image != null) {
      this.resources.add(image);
    }
    return image;
  }

  public Shell open(final Display display) {
    final Shell shell = new Shell(display);
    shell.setText(getResourceString("GraphicsExample")); //$NON-NLS-1$
    final GraphicsExample example = new GraphicsExample(shell);
    shell.addListener(SWT.Close, event -> example.dispose());
    shell.open();
    return shell;
  }

  /**
   * Redraws the current tab.
   */
  public void redraw() {
    this.canvas.redraw();
  }

  /**
   * Sets wheter the canvas is double buffered or not.
   */
  public void setDoubleBuffered(final boolean doubleBuffered) {
    this.dbItem.setSelection(doubleBuffered);
    this.recreateCanvas();
  }

  /**
   * Grabs input focus.
   */
  public void setFocus() {
    this.tabList.setFocus();
  }

  /**
   * Sets the current tab.
   */
  public void setTab(final GraphicsTab tab) {
    Control[] children = this.tabControlPanel.getChildren();
    for (final Control control : children) {
      control.dispose();
    }
    if (this.tab != null) {
      this.tab.dispose();
    }
    this.tab = tab;
    if (tab != null) {
      this.setDoubleBuffered(tab.getDoubleBuffered());
      tab.createControlPanel(this.tabControlPanel);
      this.tabDesc.setText(tab.getDescription());
    } else {
      this.tabDesc.setText("");
    }
    final FormData data = (FormData) this.tabControlPanel.getLayoutData();
    children = this.tabControlPanel.getChildren();
    if (children.length != 0) {
      data.top = null;
    } else {
      data.top = new FormAttachment(100, -MARGIN);
    }
    this.parent.layout(true, true);
    if (tab != null) {
      final TreeItem[] selection = this.tabList.getSelection();
      if ((selection.length == 0) || (selection[0].getData() != tab)) {
        final TreeItem item = this.findItemByData(this.tabList.getItems(), tab);
        if (item != null) {
          this.tabList.setSelection(new TreeItem[] { item });
        }
      }
    }
    this.canvas.redraw();
  }

  /**
   * Starts the animation if the animate flag is set.
   */
  void startAnimationTimer() {
    final Display display = this.parent.getDisplay();
    display.timerExec(TIMER, new Runnable() {
      @Override
      public void run() {
        if (GraphicsExample.this.canvas.isDisposed()) {
          return;
        }
        int timeout = TIMER;
        final GraphicsTab tab = GraphicsExample.this.getTab();
        if (tab instanceof AnimatedGraphicsTab) {
          final AnimatedGraphicsTab animTab = (AnimatedGraphicsTab) tab;
          if (GraphicsExample.this.animate && animTab.getAnimation()) {
            final Rectangle rect = GraphicsExample.this.canvas.getClientArea();
            animTab.next(rect.width, rect.height);
            GraphicsExample.this.canvas.redraw();
            GraphicsExample.this.canvas.update();
          }
          timeout = animTab.getAnimationTime();
        }
        display.timerExec(timeout, this);
      }
    });
  }

  public static void main(final String[] args) {
    final Display display = new Display();
    final Shell shell = new GraphicsExample().open(display);
    while ((shell != null) && !shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
}
