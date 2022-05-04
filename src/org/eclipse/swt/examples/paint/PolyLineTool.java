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
 * A polyline drawing tool.
 */
public class PolyLineTool extends SegmentedPaintSession implements PaintTool {
	private ToolSettings settings;

	/**
	 * Constructs a PolyLineTool.
	 *
	 * @param toolSettings the new tool settings
	 * @param paintSurface the PaintSurface we will render on.
	 */
	public PolyLineTool(final ToolSettings toolSettings, final PaintSurface paintSurface) {
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
	 * Returns the name associated with this tool.
	 *
	 * @return the localized name of this tool
	 */
	@Override
	public String getDisplayName() {
		return PaintExample.getResourceString("tool.PolyLine.label");
	}

	/*
	 * Template methods for drawing
	 */
	@Override
	protected Figure createFigure(final Point[] points, final int numPoints, final boolean closed) {
		final ContainerFigure container = new ContainerFigure();
		if (closed && (this.settings.commonFillType != ToolSettings.ftNone) && (numPoints >= 3)) {
			container.add(new SolidPolygonFigure(this.settings.commonBackgroundColor, points, numPoints));
		}
		if (! closed || (this.settings.commonFillType != ToolSettings.ftSolid) || (numPoints < 3)) {
			for (int i = 0; i < (numPoints - 1); ++i) {
				final Point a = points[i];
				final Point b = points[i + 1];
				container.add(new LineFigure(this.settings.commonForegroundColor, this.settings.commonBackgroundColor, this.settings.commonLineStyle,
					a.x, a.y, b.x, b.y));
			}
			if (closed) {
				final Point a = points[points.length - 1];
				final Point b = points[0];
				container.add(new LineFigure(this.settings.commonForegroundColor, this.settings.commonBackgroundColor, this.settings.commonLineStyle,
					a.x, a.y, b.x, b.y));
			}
		}
		return container;
	}
}
