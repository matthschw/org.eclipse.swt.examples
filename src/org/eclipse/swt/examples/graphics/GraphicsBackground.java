/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * This class is used for storing data relevant to a background.
 */
public class GraphicsBackground {

	private Image bgImage;
	private Image thumbNail;
	private Color bgColor1;
	private Color bgColor2;

	public GraphicsBackground() {
		this.bgImage = null;
		this.thumbNail = null;
		this.bgColor1 = null;
		this.bgColor2 = null;
	}

	public Image getBgImage() {
		return this.bgImage;
	}

	public void setBgImage(final Image bgImage) {
		this.bgImage = bgImage;
	}

	public Color getBgColor1() {
		return this.bgColor1;
	}

	public void setBgColor1(final Color bgColor1) {
		this.bgColor1 = bgColor1;
	}

	public Color getBgColor2() {
		return this.bgColor2;
	}

	public void setBgColor2(final Color bgColor2) {
		this.bgColor2 = bgColor2;
	}

	public Image getThumbNail() {
		return this.thumbNail;
	}

	public void setThumbNail(final Image thumbNail) {
		this.thumbNail = thumbNail;
	}
}
