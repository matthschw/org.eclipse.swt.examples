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


import java.io.InputStream;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class ControlExample {
	private static ResourceBundle resourceBundle =
		ResourceBundle.getBundle("examples_control"); //$NON-NLS-1$
	private ShellTab shellTab;
	private TabFolder tabFolder;
	private final Tab [] tabs;
	Image[] images;

	static final int ciClosedFolder = 0, ciOpenFolder = 1, ciTarget = 2, ciBackground = 3, ciParentBackground = 4;
	static final String[] imageLocations = {
		"closedFolder.gif", 			//$NON-NLS-1$
		"openFolder.gif", 				//$NON-NLS-1$
		"target.gif", 					//$NON-NLS-1$
		"backgroundImage.png", 			//$NON-NLS-1$
		"parentBackgroundImage.png"}; 	//$NON-NLS-1$
	static final int[] imageTypes = {
		SWT.ICON,
		SWT.ICON,
		SWT.ICON,
		SWT.BITMAP,
		SWT.BITMAP};

	boolean startup = true;

	/**
	 * Creates an instance of a ControlExample embedded inside
	 * the supplied parent Composite.
	 *
	 * @param parent the container of the example
	 */
	public ControlExample(final Composite parent) {
		this.initResources();
		this.tabFolder = new TabFolder (parent, SWT.NONE);
		this.tabs = this.createTabs();
		for (final Tab tab : this.tabs) {
			final TabItem item = new TabItem (this.tabFolder, SWT.NONE);
			item.setText (tab.getTabText ());
			item.setControl (tab.createTabFolderPage (this.tabFolder));
			item.setData (tab);
		}

		/* Workaround: if the tab folder is wider than the screen,
		 * Mac platforms clip instead of somehow scrolling the tab items.
		 * We try to recover some width by using shorter tab names. */
		final Point size = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		final Rectangle monitorArea = parent.getMonitor().getClientArea();
		final boolean isMac = SWT.getPlatform().equals("cocoa");
		if ((size.x > monitorArea.width) && isMac) {
			final TabItem [] tabItems = this.tabFolder.getItems();
			for (int i=0; i<tabItems.length; i++) {
				tabItems[i].setText (this.tabs [i].getShortTabText ());
			}
		}
		this.startup = false;
	}

	/**
	 * Answers the set of example Tabs
	 */
	Tab[] createTabs() {
		return new Tab [] {
			new ButtonTab (this),
			new CanvasTab (this),
			new ColorTab(this),
			new ComboTab (this),
			new CoolBarTab (this),
			new DateTimeTab (this),
			new DialogTab (this),
			new ExpandBarTab (this),
			new GroupTab (this),
			new LabelTab (this),
			new LinkTab (this),
			new ListTab (this),
			new MenuTab (this),
			new ProgressBarTab (this),
			new SashTab (this),
			new ScaleTab (this),
			this.shellTab = new ShellTab(this),
			new SliderTab (this),
			new SpinnerTab (this),
			new TabFolderTab (this),
			new TableTab (this),
			new TextTab (this),
			new ToolBarTab (this),
			new ToolTipTab (this),
			new TreeTab (this),
			new BrowserTab (this),
		};
	}

	/**
	 * Disposes of all resources associated with a particular
	 * instance of the ControlExample.
	 */
	public void dispose() {
		/*
		 * Destroy any shells that may have been created
		 * by the Shells tab.  When a shell is disposed,
		 * all child shells are also disposed.  Therefore
		 * it is necessary to check for disposed shells
		 * in the shells list to avoid disposing a shell
		 * twice.
		 */
		if (this.shellTab != null) {
      this.shellTab.closeAllShells ();
    }
		this.shellTab = null;
		this.tabFolder = null;
		this.freeResources();
	}

	/**
	 * Frees the resources
	 */
	void freeResources() {
		if (this.images != null) {
			for (final Image image : this.images) {
				if (image != null) {
          image.dispose();
        }
			}
			this.images = null;
		}
	}

	/**
	 * Gets a string from the resource bundle.
	 * We don't want to crash because of a missing String.
	 * Returns the key if not found.
	 */
	static String getResourceString(final String key) {
		try {
			return resourceBundle.getString(key);
		} catch (final MissingResourceException e) {
			return key;
		} catch (final NullPointerException e) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Gets a string from the resource bundle and binds it
	 * with the given arguments. If the key is not found,
	 * return the key.
	 */
	static String getResourceString(final String key, final Object... args) {
		try {
			return MessageFormat.format(getResourceString(key), args);
		} catch (final MissingResourceException e) {
			return key;
		} catch (final NullPointerException e) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Loads the resources
	 */
	void initResources() {
		final Class<ControlExample> clazz = ControlExample.class;
		if (resourceBundle != null) {
			try {
				if (this.images == null) {
					this.images = new Image[imageLocations.length];

					for (int i = 0; i < imageLocations.length; ++i) {
						try (InputStream sourceStream = clazz.getResourceAsStream(imageLocations[i])) {
							final ImageData source = new ImageData(sourceStream);
							if (imageTypes[i] == SWT.ICON) {
								final ImageData mask = source.getTransparencyMask();
								this.images[i] = new Image(null, source, mask);
							} else {
								this.images[i] = new Image(null, source);
							}
						}
					}
				}
				return;
			} catch (final Throwable t) {
			}
		}
		final String error = (resourceBundle != null) ?
			getResourceString("error.CouldNotLoadResources") :
			"Unable to load resources"; //$NON-NLS-1$
		this.freeResources();
		throw new RuntimeException(error);
	}

	/**
	 * Invokes as a standalone program.
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		final ControlExample instance = new ControlExample(shell);
		shell.setText(getResourceString("window.title"));
		setShellSize(shell);
		shell.open();
		while (! shell.isDisposed()) {
			if (! display.readAndDispatch()) {
        display.sleep();
      }
		}
		instance.dispose();
		display.dispose();
	}

	/**
	 * Grabs input focus.
	 */
	public void setFocus() {
		this.tabFolder.setFocus();
	}

	/**
	 * Sets the size of the shell to it's "packed" size,
	 * unless that makes it larger than the monitor it is being displayed on,
	 * in which case just set the shell size to be slightly smaller than the monitor.
	 */
	static void setShellSize(final Shell shell) {
		final Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		final Rectangle monitorArea = shell.getMonitor().getClientArea();
		shell.setSize(Math.min(size.x, monitorArea.width), Math.min(size.y, monitorArea.height));
	}
}

