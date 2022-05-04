/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
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
package org.eclipse.swt.examples.browserexample;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class BrowserExample {
	static ResourceBundle resourceBundle = ResourceBundle.getBundle("examples_browser");
	int index;
	boolean busy;
	Image images[];
	Image icon = null;
	boolean title = false;
	Composite parent;
	Text locationBar;
	Browser browser;
	ToolBar toolbar;
	Canvas canvas;
	ToolItem itemBack, itemForward;
	Label status;
	ProgressBar progressBar;
	SWTError error = null;

	static final String[] imageLocations = {
			"eclipse01.bmp", "eclipse02.bmp", "eclipse03.bmp", "eclipse04.bmp", "eclipse05.bmp",
			"eclipse06.bmp", "eclipse07.bmp", "eclipse08.bmp", "eclipse09.bmp", "eclipse10.bmp",
			"eclipse11.bmp", "eclipse12.bmp",};
	static final String iconLocation = "document.gif";

	public BrowserExample(final Composite parent, final boolean top) {
		this.parent = parent;
		try {
			this.browser = new Browser(parent, SWT.BORDER);
		} catch (final SWTError e) {
			this.error = e;
			/* Browser widget could not be instantiated */
			parent.setLayout(new FillLayout());
			final Label label = new Label(parent, SWT.CENTER | SWT.WRAP);
			label.setText(getResourceString("BrowserNotCreated"));
			label.requestLayout();
			return;
		}
		this.initResources();
		final Display display = parent.getDisplay();
		this.browser.setData("org.eclipse.swt.examples.browserexample.BrowserApplication", this);
		this.browser.addOpenWindowListener(event -> {
			final Shell shell = new Shell(display);
			if (this.icon != null) {
        shell.setImage(this.icon);
      }
			shell.setLayout(new FillLayout());
			final BrowserExample app = new BrowserExample(shell, false);
			app.setShellDecoration(this.icon, true);
			event.browser = app.getBrowser();
		});
		if (top) {
			this.browser.setUrl(getResourceString("Startup"));
			this.show(false, null, null, true, true, true, true);
		} else {
			this.browser.addVisibilityWindowListener(VisibilityWindowListener.showAdapter(e -> {
				final Browser browser = (Browser) e.widget;
				final BrowserExample app = (BrowserExample) browser
						.getData("org.eclipse.swt.examples.browserexample.BrowserApplication");
				app.show(true, e.location, e.size, e.addressBar, e.menuBar, e.statusBar, e.toolBar);
			}));
			this.browser.addCloseWindowListener(event -> {
				final Browser browser = (Browser)event.widget;
				final Shell shell = browser.getShell();
				shell.close();
			});
		}
	}

	/**
	 * Disposes of all resources associated with a particular
	 * instance of the BrowserApplication.
	 */
	public void dispose() {
		this.freeResources();
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
			return "!" + key + "!";
		}
	}

	public SWTError getError() { return this.error; }

	public Browser getBrowser() { return this.browser; }

	public void setShellDecoration(final Image icon, final boolean title) {
		this.icon = icon;
		this.title = title;
	}

	void show(final boolean owned, final Point location, final Point size, final boolean addressBar, final boolean menuBar, final boolean statusBar, final boolean toolBar) {
		final Shell shell = this.browser.getShell();
		if (owned) {
			if (location != null) {
        shell.setLocation(location);
      }
			if (size != null) {
        shell.setSize(shell.computeSize(size.x, size.y));
      }
		}
		FormData data = null;
		if (toolBar) {
			this.toolbar = new ToolBar(this.parent, SWT.NONE);
			data = new FormData();
			data.top = new FormAttachment(0, 5);
			this.toolbar.setLayoutData(data);
			this.itemBack = new ToolItem(this.toolbar, SWT.PUSH);
			this.itemBack.setText(getResourceString("Back"));
			this.itemForward = new ToolItem(this.toolbar, SWT.PUSH);
			this.itemForward.setText(getResourceString("Forward"));
			final ToolItem itemStop = new ToolItem(this.toolbar, SWT.PUSH);
			itemStop.setText(getResourceString("Stop"));
			final ToolItem itemRefresh = new ToolItem(this.toolbar, SWT.PUSH);
			itemRefresh.setText(getResourceString("Refresh"));
			final ToolItem itemGo = new ToolItem(this.toolbar, SWT.PUSH);
			itemGo.setText(getResourceString("Go"));

			this.itemBack.setEnabled(this.browser.isBackEnabled());
			this.itemForward.setEnabled(this.browser.isForwardEnabled());
			final Listener listener = event -> {
				final ToolItem item = (ToolItem)event.widget;
				if (item == this.itemBack) {
          this.browser.back();
        } else if (item == this.itemForward) {
          this.browser.forward();
        } else if (item == itemStop) {
          this.browser.stop();
        } else if (item == itemRefresh) {
          this.browser.refresh();
        } else if (item == itemGo) {
          this.browser.setUrl(this.locationBar.getText());
        }
			};
			this.itemBack.addListener(SWT.Selection, listener);
			this.itemForward.addListener(SWT.Selection, listener);
			itemStop.addListener(SWT.Selection, listener);
			itemRefresh.addListener(SWT.Selection, listener);
			itemGo.addListener(SWT.Selection, listener);

			this.canvas = new Canvas(this.parent, SWT.NO_BACKGROUND);
			data = new FormData();
			data.width = 24;
			data.height = 24;
			data.top = new FormAttachment(0, 5);
			data.right = new FormAttachment(100, -5);
			this.canvas.setLayoutData(data);

			final Rectangle rect = this.images[0].getBounds();
			this.canvas.addListener(SWT.Paint, e -> {
				final Point pt = ((Canvas)e.widget).getSize();
				e.gc.drawImage(this.images[this.index], 0, 0, rect.width, rect.height, 0, 0, pt.x, pt.y);
			});
			this.canvas.addListener(SWT.MouseDown, e -> this.browser.setUrl(getResourceString("Startup")));

			final Display display = this.parent.getDisplay();
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (BrowserExample.this.canvas.isDisposed()) {
            return;
          }
					if (BrowserExample.this.busy) {
						BrowserExample.this.index++;
						if (BrowserExample.this.index == BrowserExample.this.images.length) {
              BrowserExample.this.index = 0;
            }
						BrowserExample.this.canvas.redraw();
					}
					display.timerExec(150, this);
				}
			});
		}
		if (addressBar) {
			this.locationBar = new Text(this.parent, SWT.BORDER);
			data = new FormData();
			if (this.toolbar != null) {
				data.top = new FormAttachment(this.toolbar, 0, SWT.TOP);
				data.left = new FormAttachment(this.toolbar, 5, SWT.RIGHT);
				data.right = new FormAttachment(this.canvas, -5, SWT.DEFAULT);
			} else {
				data.top = new FormAttachment(0, 0);
				data.left = new FormAttachment(0, 0);
				data.right = new FormAttachment(100, 0);
			}
			this.locationBar.setLayoutData(data);
			this.locationBar.addListener(SWT.DefaultSelection, e -> this.browser.setUrl(this.locationBar.getText()));
		}
		if (statusBar) {
			this.status = new Label(this.parent, SWT.NONE);
			this.progressBar = new ProgressBar(this.parent, SWT.NONE);

			data = new FormData();
			data.left = new FormAttachment(0, 5);
			data.right = new FormAttachment(this.progressBar, 0, SWT.DEFAULT);
			data.bottom = new FormAttachment(100, -5);
			this.status.setLayoutData(data);

			data = new FormData();
			data.right = new FormAttachment(100, -5);
			data.bottom = new FormAttachment(100, -5);
			this.progressBar.setLayoutData(data);

			this.browser.addStatusTextListener(event -> this.status.setText(event.text));
		}
		this.parent.setLayout(new FormLayout());

		final Control aboveBrowser = toolBar ? (Control)this.canvas : (addressBar ? (Control)this.locationBar : null);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = aboveBrowser != null ? new FormAttachment(aboveBrowser, 5, SWT.DEFAULT) : new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = this.status != null ? new FormAttachment(this.status, -5, SWT.DEFAULT) : new FormAttachment(100, 0);
		this.browser.setLayoutData(data);

		if (statusBar || toolBar) {
			this.browser.addProgressListener(new ProgressListener() {
				@Override
				public void changed(final ProgressEvent event) {
					if (event.total == 0) {
            return;
          }
					final int ratio = (event.current * 100) / event.total;
					if (BrowserExample.this.progressBar != null) {
            BrowserExample.this.progressBar.setSelection(ratio);
          }
					BrowserExample.this.busy = event.current != event.total;
					if (!BrowserExample.this.busy) {
						BrowserExample.this.index = 0;
						if (BrowserExample.this.canvas != null) {
              BrowserExample.this.canvas.redraw();
            }
					}
				}
				@Override
				public void completed(final ProgressEvent event) {
					if (BrowserExample.this.progressBar != null) {
            BrowserExample.this.progressBar.setSelection(0);
          }
					BrowserExample.this.busy = false;
					BrowserExample.this.index = 0;
					if (BrowserExample.this.canvas != null) {
						BrowserExample.this.itemBack.setEnabled(BrowserExample.this.browser.isBackEnabled());
						BrowserExample.this.itemForward.setEnabled(BrowserExample.this.browser.isForwardEnabled());
						BrowserExample.this.canvas.redraw();
					}
				}
			});
		}
		if (addressBar || statusBar || toolBar) {
			this.browser.addLocationListener(LocationListener.changedAdapter(event -> {
					this.busy = true;
					if (event.top && (this.locationBar != null)) {
            this.locationBar.setText(event.location);
          }
				}
			));
		}
		if (this.title) {
			this.browser.addTitleListener(event -> shell.setText(event.title+" - "+getResourceString("window.title")));
		}
		this.parent.layout(true);
		if (owned) {
      shell.open();
    }
	}

	/**
	 * Grabs input focus
	 */
	public void focus() {
		if (this.locationBar != null) {
      this.locationBar.setFocus();
    } else if (this.browser != null) {
      this.browser.setFocus();
    } else {
      this.parent.setFocus();
    }
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
	 * Loads the resources
	 */
	void initResources() {
		final Class<? extends BrowserExample> clazz = this.getClass();
		if (resourceBundle != null) {
			try {
				if (this.images == null) {
					this.images = new Image[imageLocations.length];
					for (int i = 0; i < imageLocations.length; ++i) {
						try (InputStream sourceStream = clazz.getResourceAsStream(imageLocations[i])) {
						final ImageData source = new ImageData(sourceStream);
						final ImageData mask = source.getTransparencyMask();
						this.images[i] = new Image(null, source, mask);
						}
					}
				}
				return;
			} catch (final Throwable t) {
			}
		}
		final String error = (resourceBundle != null) ? getResourceString("error.CouldNotLoadResources") : "Unable to load resources";
		this.freeResources();
		throw new RuntimeException(error);
	}

	public static void main(final String [] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText(getResourceString("window.title"));
		final InputStream stream = BrowserExample.class.getResourceAsStream(iconLocation);
		final Image icon = new Image(display, stream);
		shell.setImage(icon);
		try {
			stream.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final BrowserExample app = new BrowserExample(shell, true);
		app.setShellDecoration(icon, true);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
        display.sleep();
      }
		}
		icon.dispose();
		app.dispose();
		display.dispose();
	}
}
