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
package org.eclipse.swt.examples.hoverhelp;


import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
/**
 * This example demonstrates how to implement hover help feedback
 * using the MouseTrackListener.
 */
public class HoverHelp {
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("examples_hoverhelp");

	static final int
		hhiInformation = 0,
		hhiWarning = 1;
	static final String[] imageLocations = {
		"information.gif",
		"warning.gif"
	};
	Image images[];

	/**
	 * Runs main program.
	 */
	public static void main (final String [] args) {
		final Display display = new Display();
		final Shell shell = new HoverHelp().open(display);
		// Event loop
		while ((shell != null) && ! shell.isDisposed()) {
			if (! display.readAndDispatch()) {
        display.sleep();
      }
		}
		// Cleanup
		display.dispose();

	}

	/**
	 * Opens the main program.
	 */
	public Shell open(final Display display) {
		// Load the images
		final Class<HoverHelp> clazz = HoverHelp.class;
		try {
			if (this.images == null) {
				this.images = new Image[imageLocations.length];

				for (int i = 0; i < imageLocations.length; ++i) {
					try (InputStream stream = clazz.getResourceAsStream(imageLocations[i])) {
						final ImageData source = new ImageData(stream);
						final ImageData mask = source.getTransparencyMask();
						this.images[i] = new Image(display, source, mask);
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (final Exception ex) {
			System.err.println(this.getResourceString("error.CouldNotLoadResources", ex.getMessage()));
			return null;
		}

		// Create the window
		final Shell shell = new Shell();
		this.createPartControl(shell);
		shell.addDisposeListener(e -> {
			/* Free resources */
			if (this.images != null) {
				for (final Image image : this.images) {
					if (image != null) {
            image.dispose();
          }
				}
				this.images = null;
			}
		});
		shell.pack();
		shell.open();
		return shell;
	}

	/**
	 * Gets a string from the resource bundle.
	 * We don't want to crash because of a missing String.
	 * Returns the key if not found.
	 */
	public String getResourceString(final String key) {
		try {
			return resourceBundle.getString(key);
		} catch (final MissingResourceException e) {
			return key;
		} catch (final NullPointerException e) {
			return "!" + key + "!";
		}
	}

	/**
	 * Gets a string from the resource bundle and binds it
	 * with the given arguments. If the key is not found,
	 * return the key.
	 */
	public String getResourceString(final String key, final Object... args) {
		try {
			return MessageFormat.format(this.getResourceString(key), args);
		} catch (final MissingResourceException e) {
			return key;
		} catch (final NullPointerException e) {
			return "!" + key + "!";
		}
	}

	/**
	 * Creates the example
	 */
	public void createPartControl(final Composite frame) {
		final ToolTipHandler tooltip = new ToolTipHandler(frame.getShell());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		frame.setLayout(layout);

		final String platform = SWT.getPlatform();
		String helpKey = "F1";
		if (platform.equals("gtk")) {
      helpKey = "Ctrl+F1";
    }
		if (platform.equals("cocoa")) {
      helpKey = "Help";
    }

		final ToolBar bar = new ToolBar (frame, SWT.BORDER);
		for (int i=0; i<5; i++) {
			final ToolItem item = new ToolItem (bar, SWT.PUSH);
			item.setText (this.getResourceString("ToolItem.text", new Object[] { Integer.valueOf(i) }));
			item.setData ("TIP_TEXT", this.getResourceString("ToolItem.tooltip",
				new Object[] { item.getText(), helpKey }));
			item.setData ("TIP_HELPTEXTHANDLER", (ToolTipHelpTextHandler) widget -> {
				final Item item1 = (Item) widget;
				return this.getResourceString("ToolItem.help", new Object[] { item1.getText() });
			});
		}
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		bar.setLayoutData(gridData);
		tooltip.activateHoverHelp(bar);

		final Table table = new Table (frame, SWT.BORDER);
		for (int i=0; i<4; i++) {
			final TableItem item = new TableItem (table, SWT.PUSH);
			item.setText (this.getResourceString("Item", new Object[] { Integer.valueOf(i) }));
			item.setData ("TIP_IMAGE", this.images[hhiInformation]);
			item.setText (this.getResourceString("TableItem.text", new Object[] { Integer.valueOf(i) }));
			item.setData ("TIP_TEXT", this.getResourceString("TableItem.tooltip",
				new Object[] { item.getText(), helpKey }));
			item.setData ("TIP_HELPTEXTHANDLER", (ToolTipHelpTextHandler) widget -> {
				final Item item1 = (Item) widget;
				return this.getResourceString("TableItem.help", new Object[] { item1.getText() });
			});
		}
		table.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));
		tooltip.activateHoverHelp(table);

		final Tree tree = new Tree (frame, SWT.BORDER);
		for (int i=0; i<4; i++) {
			final TreeItem item = new TreeItem (tree, SWT.PUSH);
			item.setText (this.getResourceString("Item", new Object[] { Integer.valueOf(i) }));
			item.setData ("TIP_IMAGE", this.images[hhiWarning]);
			item.setText (this.getResourceString("TreeItem.text", new Object[] { Integer.valueOf(i) }));
			item.setData ("TIP_TEXT", this.getResourceString("TreeItem.tooltip",
				new Object[] { item.getText(), helpKey}));
			item.setData ("TIP_HELPTEXTHANDLER", (ToolTipHelpTextHandler) widget -> {
				final Item item1 = (Item) widget;
				return this.getResourceString("TreeItem.help", new Object[] { item1.getText() });
			});
		}
		tree.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));
		tooltip.activateHoverHelp(tree);

		final Button button = new Button (frame, SWT.PUSH);
		button.setText (this.getResourceString("Hello.text"));
		button.setData ("TIP_TEXT", this.getResourceString("Hello.tooltip"));
		tooltip.activateHoverHelp(button);
	}

	/**
	 * Emulated tooltip handler
	 * Notice that we could display anything in a tooltip besides text and images.
	 * For instance, it might make sense to embed large tables of data or buttons linking
	 * data under inspection to material elsewhere, or perform dynamic lookup for creating
	 * tooltip text on the fly.
	 */
	protected static class ToolTipHandler {
		private final Shell  parentShell;
		private final Shell  tipShell;
		private final Label  tipLabelImage, tipLabelText;
		private Widget tipWidget; // widget this tooltip is hovering over
		private Point  tipPosition; // the position being hovered over

		/**
		 * Creates a new tooltip handler
		 *
		 * @param parent the parent Shell
		 */
		public ToolTipHandler(final Shell parent) {
			final Display display = parent.getDisplay();
			this.parentShell = parent;

			this.tipShell = new Shell(parent, SWT.ON_TOP | SWT.TOOL);
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.marginWidth = 2;
			gridLayout.marginHeight = 2;
			this.tipShell.setLayout(gridLayout);

			this.tipShell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

			this.tipLabelImage = new Label(this.tipShell, SWT.NONE);
			this.tipLabelImage.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			this.tipLabelImage.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			this.tipLabelImage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL |
				GridData.VERTICAL_ALIGN_CENTER));

			this.tipLabelText = new Label(this.tipShell, SWT.NONE);
			this.tipLabelText.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			this.tipLabelText.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			this.tipLabelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL |
				GridData.VERTICAL_ALIGN_CENTER));
		}

		/**
		 * Enables customized hover help for a specified control
		 *
		 * @control the control on which to enable hoverhelp
		 */
		public void activateHoverHelp(final Control control) {
			/*
			 * Get out of the way if we attempt to activate the control underneath the tooltip
			 */
			control.addMouseListener(MouseListener.mouseDownAdapter(e -> {
				if (this.tipShell.isVisible()) {
          this.tipShell.setVisible(false);
        }
			}));

			/*
			 * Trap hover events to pop-up tooltip
			 */
			control.addMouseTrackListener(new MouseTrackAdapter () {
				@Override
				public void mouseExit(final MouseEvent e) {
					if (ToolTipHandler.this.tipShell.isVisible()) {
            ToolTipHandler.this.tipShell.setVisible(false);
          }
					ToolTipHandler.this.tipWidget = null;
				}
				@Override
				public void mouseHover (final MouseEvent event) {
					final Point pt = new Point (event.x, event.y);
					Widget widget = event.widget;
					if (widget instanceof ToolBar) {
						final ToolBar w = (ToolBar) widget;
						widget = w.getItem (pt);
					}
					if (widget instanceof Table) {
						final Table w = (Table) widget;
						widget = w.getItem (pt);
					}
					if (widget instanceof Tree) {
						final Tree w = (Tree) widget;
						widget = w.getItem (pt);
					}
					if (widget == null) {
						ToolTipHandler.this.tipShell.setVisible(false);
						ToolTipHandler.this.tipWidget = null;
						return;
					}
					if (widget == ToolTipHandler.this.tipWidget) {
            return;
          }
					ToolTipHandler.this.tipWidget = widget;
					ToolTipHandler.this.tipPosition = control.toDisplay(pt);
					final String text = (String) widget.getData("TIP_TEXT");
					final Image image = (Image) widget.getData("TIP_IMAGE");
					ToolTipHandler.this.tipLabelText.setText(text != null ? text : "");
					ToolTipHandler.this.tipLabelImage.setImage(image); // accepts null
					ToolTipHandler.this.tipShell.pack();
					ToolTipHandler.this.setHoverLocation(ToolTipHandler.this.tipShell, ToolTipHandler.this.tipPosition);
					ToolTipHandler.this.tipShell.setVisible(true);
				}
			});

			/*
			 * Trap F1 Help to pop up a custom help box
			 */
			control.addHelpListener(event -> {
				if (this.tipWidget == null) {
          return;
        }
				final ToolTipHelpTextHandler handler = (ToolTipHelpTextHandler)
					this.tipWidget.getData("TIP_HELPTEXTHANDLER");
				if (handler == null) {
          return;
        }
				final String text = handler.getHelpText(this.tipWidget);
				if (text == null) {
          return;
        }

				if (this.tipShell.isVisible()) {
					this.tipShell.setVisible(false);
					final Shell helpShell = new Shell(this.parentShell, SWT.SHELL_TRIM);
					helpShell.setLayout(new FillLayout());
					final Label label = new Label(helpShell, SWT.NONE);
					label.setText(text);
					helpShell.pack();
					this.setHoverLocation(helpShell, this.tipPosition);
					helpShell.open();
				}
			});
		}

		/**
		 * Sets the location for a hovering shell
		 * @param shell the object that is to hover
		 * @param position the position of a widget to hover over
		 * @return the top-left location for a hovering box
		 */
		private void setHoverLocation(final Shell shell, final Point position) {
			final Rectangle displayBounds = shell.getDisplay().getBounds();
			final Rectangle shellBounds = shell.getBounds();
			shellBounds.x = Math.max(Math.min(position.x, displayBounds.width - shellBounds.width), 0);
			shellBounds.y = Math.max(Math.min(position.y + 16, displayBounds.height - shellBounds.height), 0);
			shell.setBounds(shellBounds);
		}
	}

	/**
	 * ToolTip help handler
	 */
	protected interface ToolTipHelpTextHandler {
		/**
		 * Get help text
		 * @param widget the widget that is under help
		 * @return a help text string
		 */
		public String getHelpText(Widget widget);
	}
}
