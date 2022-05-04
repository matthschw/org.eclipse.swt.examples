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
package org.eclipse.swt.examples.paint;


import org.eclipse.swt.graphics.Point;

/**
 * A drawing tool.
 */
public class RectangleTool extends DragPaintSession implements PaintTool {
	private ToolSettings settings;

	/**
	 * Constructs a RectangleTool.
	 *
	 * @param toolSettings the new tool settings
	 * @param paintSurface the PaintSurface we will render on.
	 */
	public RectangleTool(final ToolSettings toolSettings, final PaintSurface paintSurface) {
		super(paintSurface);
		this.set(toolSettings);
	}

	/**
	 * Sets the tool's settings.
	 *
	 * @param toolSettings the new tool settings
	 */
	@Override
	public void set(final ToolSettings toolSettings) {
		this.settings = toolSettings;
	}

	/**
	 * Returns name associated with this tool.
	 *
	 * @return the localized name of this tool
	 */
	@Override
	public String getDisplayName() {
		return PaintExample.getResourceString("tool.Rectangle.label");
	}

	/*
	 * Template method for drawing
	 */
	@Override
	protected Figure createFigure(final Point a, final Point b) {
		switch (this.settings.commonFillType) {
			default:
			case ToolSettings.ftNone:
				return new RectangleFigure(this.settings.commonForegroundColor, this.settings.commonBackgroundColor, this.settings.commonLineStyle,
					a.x, a.y, b.x, b.y);
			case ToolSettings.ftSolid:
				return new SolidRectangleFigure(this.settings.commonBackgroundColor, a.x, a.y, b.x, b.y);
			case ToolSettings.ftOutline: {
				final ContainerFigure container = new ContainerFigure();
				container.add(new SolidRectangleFigure(this.settings.commonBackgroundColor, a.x, a.y, b.x, b.y));
				container.add(new RectangleFigure(this.settings.commonForegroundColor, this.settings.commonBackgroundColor, this.settings.commonLineStyle,
					a.x, a.y, b.x, b.y));
				return container;
			}
		}
	}
}
