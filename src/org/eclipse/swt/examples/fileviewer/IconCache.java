/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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
package org.eclipse.swt.examples.fileviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 * Manages icons for the application.
 * This is necessary as we could easily end up creating thousands of icons
 * bearing the same image.
 */
class IconCache {
	// Stock images
	public final int
		shellIcon = 0,
		iconClosedDrive = 1,
		iconClosedFolder = 2,
		iconFile = 3,
		iconOpenDrive = 4,
		iconOpenFolder = 5,
		cmdCopy = 6,
		cmdCut = 7,
		cmdDelete = 8,
		cmdParent = 9,
		cmdPaste = 10,
		cmdPrint = 11,
		cmdRefresh = 12,
		cmdRename = 13,
		cmdSearch = 14;
	public final String[] stockImageLocations = {
		"generic_example.gif",
		"icon_ClosedDrive.gif",
		"icon_ClosedFolder.gif",
		"icon_File.gif",
		"icon_OpenDrive.gif",
		"icon_OpenFolder.gif",
		"cmd_Copy.gif",
		"cmd_Cut.gif",
		"cmd_Delete.gif",
		"cmd_Parent.gif",
		"cmd_Paste.gif",
		"cmd_Print.gif",
		"cmd_Refresh.gif",
		"cmd_Rename.gif",
		"cmd_Search.gif"
	};
	public Image stockImages[];

	// Stock cursors
	public final int
		cursorDefault = 0,
		cursorWait = 1;
	public Cursor stockCursors[];
	// Cached icons
	private Map<Program, Image> iconCache; /* map Program to Image */

	public IconCache() {
	}
	/**
	 * Loads the resources
	 *
	 * @param display the display
	 */
	public void initResources(final Display display) {
		if (this.stockImages == null) {
			this.stockImages = new Image[this.stockImageLocations.length];

			for (int i = 0; i < this.stockImageLocations.length; ++i) {
				final Image image = this.createStockImage(display, this.stockImageLocations[i]);
				if (image == null) {
					this.freeResources();
					throw new IllegalStateException(
						FileViewer.getResourceString("error.CouldNotLoadResources"));
				}
				this.stockImages[i] = image;
			}
		}
		if (this.stockCursors == null) {
			this.stockCursors = new Cursor[] {
				null,
				display.getSystemCursor(SWT.CURSOR_WAIT)
			};
		}
		this.iconCache = new HashMap<>();
	}
	/**
	 * Frees the resources
	 */
	public void freeResources() {
		if (this.stockImages != null) {
			for (final Image image : this.stockImages) {
				if (image != null) {
          image.dispose();
        }
			}
			this.stockImages = null;
		}
		if (this.iconCache != null) {
			for (final Image image : this.iconCache.values()) {
				image.dispose();
			}
			this.iconCache = null;
		}
		this.stockCursors = null;
	}
	/**
	 * Creates a stock image
	 *
	 * @param display the display
	 * @param path the relative path to the icon
	 */
	private Image createStockImage(final Display display, final String path) {
		final InputStream stream = IconCache.class.getResourceAsStream (path);
		final ImageData imageData = new ImageData (stream);
		final ImageData mask = imageData.getTransparencyMask ();
		final Image result = new Image (display, imageData, mask);
		try {
			stream.close ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
		return result;
	}
	/**
	 * Gets an image for a file associated with a given program
	 *
	 * @param program the Program
	 */
	public Image getIconFromProgram(final Program program) {
		Image image = this.iconCache.get(program);
		if (image == null) {
			final ImageData imageData = program.getImageData();
			if (imageData != null) {
				image = new Image(null, imageData, imageData.getTransparencyMask());
				this.iconCache.put(program, image);
			} else {
				image = this.stockImages[this.iconFile];
			}
		}
		return image;
	}
}
