/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * This class utilizes the factory design pattern to create menus that may
 * contain color items, pattern items and gradient items. To create a menu:
 * first set the menu items that you wish to have appear in the menu by calling
 * the setters (setColorItems(), setPatternItems(), setGradientItems()), and
 * then call createMenu() to get an instance of a menu. By default, the menu
 * will contain color items.
 */
public class ColorMenu {

	boolean enableColorItems, enablePatternItems, enableGradientItems;

	public ColorMenu() {
		this.enableColorItems = true;
	}

	/**
	 * Method used to specify whether or not the color menu items will appear in
	 * the menu.
	 *
	 * @param enable
	 *            A boolean flag - true to make the color menu items visible in
	 *            the menu; false otherwise.
	 */
	public void setColorItems(final boolean enable) {
		this.enableColorItems = enable;
	}

	/**
	 * @return true if color menu items are contained in the menu; false otherwise.
	 * */
	public boolean getColorItems() {
		return this.enableColorItems;
	}

	/**
	 * Method used to specify whether or not the pattern menu items will appear
	 * in the menu.
	 *
	 * @param enable
	 *            A boolean flag - true to make the pattern menu items visible
	 *            in the menu; false otherwise.
	 */
	public void setPatternItems(final boolean enable) {
		this.enablePatternItems = enable;
	}

	/**
	 * @return true if pattern menu items are contained in the menu; false otherwise.
	 * */
	public boolean getPatternItems() {
		return this.enablePatternItems;
	}

	/**
	 * Method used to specify whether or not the gradient menu items will appear
	 * in the menu.
	 *
	 * @param enable
	 *            A boolean flag - true to make the gradient menu items visible
	 *            in the menu; false otherwise.
	 */
	public void setGradientItems(final boolean enable) {
		this.enableGradientItems = enable;
	}

	/**
	 * @return true if gradient menu items are contained in the menu; false otherwise.
	 */
	public boolean getGradientItems() {
		return this.enableGradientItems;
	}

	/**
	 * Creates and returns the menu based on the settings provided via
	 * setColorItems(), setPatternItems() and setGradientItems()
	 *
	 * @return A menu based on the settings
	 */
	public Menu createMenu(final Control parent, final ColorListener cl) {
		final Menu menu = new Menu(parent);

		final MenuItemListener menuItemListener = this.createMenuItemListener(parent);
		menu.addListener(SWT.Selection, menuItemListener);
		menu.addListener(SWT.Dispose, menuItemListener);
		menuItemListener.setColorListener(cl);

		if (this.enableColorItems) {
			this.addColorItems(menu, menuItemListener, menuItemListener.getMenuResources());
		}
		if (this.enablePatternItems) {
			this.addPatternItems(menu, menuItemListener, menuItemListener.getMenuResources());
		}
		if (this.enableGradientItems) {
			this.addGradientItems(menu, menuItemListener);
		}
		return menu;
	}

	/** Adds the colors items to the menu. */
	private void addColorItems(final Menu menu, final MenuItemListener menuListener,
			final List<Image> menuResources) {
		final Display display = menu.getDisplay();

		if (menu.getItemCount() != 0) {
			new MenuItem(menu, SWT.SEPARATOR);
		}

		// color names
		final String[] names = new String[]{
			GraphicsExample.getResourceString("White"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Black"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Red"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Green"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Blue"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Yellow"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Cyan"), //$NON-NLS-1$
		};

		// colors needed for the background menu
		final Color[] colors = new Color[]{
			display.getSystemColor(SWT.COLOR_WHITE),
			display.getSystemColor(SWT.COLOR_BLACK),
			display.getSystemColor(SWT.COLOR_RED),
			display.getSystemColor(SWT.COLOR_GREEN),
			display.getSystemColor(SWT.COLOR_BLUE),
			display.getSystemColor(SWT.COLOR_YELLOW),
			display.getSystemColor(SWT.COLOR_CYAN),
		};

		// add standard color items to menu
		for (int i = 0; i < names.length; i++) {
			final MenuItem item = new MenuItem(menu, SWT.NONE);
			item.setText(names[i]);
			item.addListener(SWT.Selection, menuListener);
			final Color color = colors[i];
			final GraphicsBackground gb = new GraphicsBackground();
			final Image image = GraphicsExample.createImage(display, color);
			gb.setBgColor1(color);
			gb.setBgImage(image);
			gb.setThumbNail(image);
			menuResources.add(image);
			item.setImage(image);
			item.setData(gb);
		}

		// add custom color item to menu
		menuListener.customColorMI = new MenuItem(menu, SWT.NONE);
		menuListener.customColorMI.setText(GraphicsExample.getResourceString("CustomColor")); //$NON-NLS-1$
		menuListener.customColorMI.addListener(SWT.Selection, menuListener);
		final GraphicsBackground gb = new GraphicsBackground();
		menuListener.customColorMI.setData(gb);
	}

	/** Adds the pattern items to the menu. */
	private void addPatternItems(final Menu menu, final MenuItemListener menuListener,
			final List<Image> menuResources) {
		final Display display = menu.getDisplay();

		if (menu.getItemCount() != 0) {
			new MenuItem(menu, SWT.SEPARATOR);
		}

		// pattern names
		final String[] names = new String[]{
			GraphicsExample.getResourceString("Pattern1"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Pattern2"), //$NON-NLS-1$
			GraphicsExample.getResourceString("Pattern3"), //$NON-NLS-1$
		};

		// pattern images
		final Image[] images = new Image[]{
			this.loadImage(display, "pattern1.jpg", menuResources),
			this.loadImage(display, "pattern2.jpg", menuResources),
			this.loadImage(display, "pattern3.jpg", menuResources),
		};

		// add the pre-defined patterns to the menu
		for (int i = 0; i < names.length; i++) {
			final MenuItem item = new MenuItem(menu, SWT.NONE);
			item.setText(names[i]);
			item.addListener(SWT.Selection, menuListener);
			final Image image = images[i];
			final GraphicsBackground gb = new GraphicsBackground();
			gb.setBgImage(image);
			gb.setThumbNail(image);
			item.setImage(image);
			item.setData(gb);
		}

		// add the custom pattern item
		menuListener.customPatternMI = new MenuItem(menu, SWT.NONE);
		menuListener.customPatternMI.setText(GraphicsExample.getResourceString("CustomPattern")); //$NON-NLS-1$
		menuListener.customPatternMI.addListener(SWT.Selection, menuListener);
		final GraphicsBackground gb = new GraphicsBackground();
		menuListener.customPatternMI.setData(gb);
	}

	/** Adds the gradient menu item. */
	private void addGradientItems(final Menu menu, final MenuItemListener menuListener) {
		if (menu.getItemCount() != 0) {
			new MenuItem(menu, SWT.SEPARATOR);
		}
		menuListener.customGradientMI = new MenuItem(menu, SWT.NONE);
		menuListener.customGradientMI.setText(GraphicsExample.getResourceString("Gradient")); //$NON-NLS-1$
		menuListener.customGradientMI.addListener(SWT.Selection, menuListener);
		final GraphicsBackground gb = new GraphicsBackground();
		menuListener.customGradientMI.setData(gb);
	}

	/** Creates and returns the listener for menu items. */
	private MenuItemListener createMenuItemListener(final Control parent) {
		return new MenuItemListener(parent);
	}

	/**
	 * Creates and returns an instance of Image using on the path of an image.
	 *
	 * @param display
	 *            A Display
	 * @param name
	 *            The path of the image file
	 * @param resources
	 *            The list of resources of the menu
	 */
	private Image loadImage(final Display display, final String name, final List<Image> resources) {
		final Image image = GraphicsExample.loadImage(display, GraphicsExample.class, name);
		if (image != null) {
      resources.add(image);
    }
		return image;
	}

	/**
	 * An inner class used as a listener for MenuItems added to the menu.
	 */
	static class MenuItemListener implements Listener {
		MenuItem customColorMI, customPatternMI, customGradientMI;	// custom menu items
		Control parent;
		Image customImage, customImageThumb;
		Color customColor;
		GraphicsBackground background;	// used to store information about the background
		ColorListener colorListener;
		List<Image> resourceImages;

		public MenuItemListener(final Control parent){
			this.parent = parent;
			this.resourceImages = new ArrayList<>();
		}
		/**
		 * Method used to set the ColorListener
		 *
		 * @param cl
		 *            A ColorListener
		 * @see org.eclipse.swt.examples.graphics.ColorListener.java
		 */
		public void setColorListener(final ColorListener cl) {
			this.colorListener = cl;
		}

		public List<Image> getMenuResources() {
			return this.resourceImages;
		}

		@Override
		public void handleEvent(final Event event) {
			switch (event.type) {

			case SWT.Dispose:
				for (final Image image : this.resourceImages) {
					image.dispose();
				}
				this.resourceImages = new ArrayList<>();
				break;
			case SWT.Selection:
				final Display display = event.display;
				final MenuItem item = (MenuItem) event.widget;
				if (this.customColorMI == item) {
					final ColorDialog dialog = new ColorDialog(this.parent.getShell());
					if ((this.customColor != null) && !this.customColor.isDisposed()) {
						dialog.setRGB(this.customColor.getRGB());
					}
					final RGB rgb = dialog.open();
					if (rgb == null) {
            return;
          }
					this.customColor = new Color(rgb);
					if (this.customPatternMI != null) {
            this.customPatternMI.setImage(null);
          }
					if (this.customGradientMI != null) {
            this.customGradientMI.setImage(null);
          }
					if (this.customImage != null) {
            this.customImage.dispose();
          }
					this.customImage = GraphicsExample.createImage(display, this.customColor);
					final GraphicsBackground gb = new GraphicsBackground();
					gb.setBgImage(this.customImage);
					gb.setThumbNail(this.customImage);
					gb.setBgColor1(this.customColor);
					item.setData(gb);
					item.setImage(this.customImage);
					this.resourceImages.add(this.customImage);
				} else if (this.customPatternMI == item) {
					final FileDialog dialog = new FileDialog(this.parent.getShell());
					dialog.setFilterExtensions(new String[] { "*.jpg", "*.gif",	"*.*" });
					final String name = dialog.open();
					if (name == null) {
            return;
          }
					if (this.customColorMI != null) {
            this.customColorMI.setImage(null);
          }
					if (this.customGradientMI != null) {
            this.customGradientMI.setImage(null);
          }
					if (this.customImage != null) {
            this.customImage.dispose();
          }
					if (this.customImageThumb != null) {
            this.customImageThumb.dispose();
          }
					this.customImage = new Image(display, name);
					this.customImageThumb = GraphicsExample.createThumbnail(display, name);
					final GraphicsBackground gb = new GraphicsBackground();
					gb.setBgImage(this.customImage);
					gb.setThumbNail(this.customImageThumb);
					item.setData(gb);
					item.setImage(this.customImageThumb);
					this.resourceImages.add(this.customImageThumb);
				} else if (this.customGradientMI == item) {
					final GradientDialog dialog = new GradientDialog(this.parent.getShell());
					if (this.background != null) {
						if (this.background.getBgColor1() != null) {
              dialog.setFirstRGB(this.background.getBgColor1().getRGB());
            }
						if (this.background.getBgColor2() != null) {
              dialog.setSecondRGB(this.background.getBgColor2().getRGB());
            }
					}
					if (dialog.open() != SWT.OK) {
            return;
          }
					final Color colorA = new Color(dialog.getFirstRGB());
					final Color colorB = new Color(dialog.getSecondRGB());
					if ((colorA == null) || (colorB == null)) {
            return;
          }
					if (this.customColorMI != null) {
            this.customColorMI.setImage(null);
          }
					if (this.customPatternMI != null) {
            this.customPatternMI.setImage(null);
          }
					if (this.customImage != null) {
            this.customImage.dispose();
          }
					this.customImage = GraphicsExample.createImage(display, colorA,
							colorB, 16, 16);
					final GraphicsBackground gb = new GraphicsBackground();
					gb.setBgImage(this.customImage);
					gb.setThumbNail(this.customImage);
					gb.setBgColor1(colorA);
					gb.setBgColor2(colorB);
					item.setData(gb);
					item.setImage(this.customImage);
					this.resourceImages.add(this.customImage);
				} else {
					if (this.customColorMI != null) {
            this.customColorMI.setImage(null);
          }
					if (this.customPatternMI != null) {
            this.customPatternMI.setImage(null);
          }
					if (this.customGradientMI != null) {
            this.customGradientMI.setImage(null);
          }
				}
				this.background = (GraphicsBackground) item.getData();
				this.colorListener.setColor(this.background);
				break;
			}
		}
	}
}
