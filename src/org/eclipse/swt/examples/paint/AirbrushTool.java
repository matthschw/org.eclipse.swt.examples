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


import java.util.Random;

import org.eclipse.swt.graphics.Point;

/**
 * An airbrush tool.
 */
public class AirbrushTool extends ContinuousPaintSession implements PaintTool {
	private ToolSettings settings;
	private final Random random;
	private int cachedRadiusSquared;
	private int cachedNumPoints;

	/**
	 * Constructs a Tool.
	 *
	 * @param toolSettings the new tool settings
	 * @param paintSurface the PaintSurface we will render on.
	 */
	public AirbrushTool(final ToolSettings toolSettings, final PaintSurface paintSurface) {
		super(paintSurface);
		this.random = new Random();
		this.setRetriggerTimer(10);
		this.set(toolSettings);
	}

	/**
	 * Sets the tool's settings.
	 *
	 * @param toolSettings the new tool settings
	 */
	@Override
	public void set(final ToolSettings toolSettings) {
		// compute things we need to know for drawing
		this.settings = toolSettings;
		this.cachedRadiusSquared = this.settings.airbrushRadius * this.settings.airbrushRadius;
		this.cachedNumPoints = (314 * this.settings.airbrushIntensity * this.cachedRadiusSquared) / 250000;
		if ((this.cachedNumPoints == 0) && (this.settings.airbrushIntensity != 0)) {
      this.cachedNumPoints = 1;
    }
	}

	/**
	 * Returns the name associated with this tool.
	 *
	 * @return the localized name of this tool
	 */
	@Override
	public String getDisplayName() {
		return PaintExample.getResourceString("tool.Airbrush.label");
	}

	/*
	 * Template method for drawing
	 */
	@Override
	protected void render(final Point point) {
		// Draws a bunch (cachedNumPoints) of random pixels within a specified circle (cachedRadiusSquared).
		final ContainerFigure cfig = new ContainerFigure();

		for (int i = 0; i < this.cachedNumPoints; ++i) {
			int randX, randY;
			do {
				randX = (int) ((this.random.nextDouble() - 0.5) * this.settings.airbrushRadius * 2.0);
				randY = (int) ((this.random.nextDouble() - 0.5) * this.settings.airbrushRadius * 2.0);
			} while (((randX * randX) + (randY * randY)) > this.cachedRadiusSquared);
			cfig.add(new PointFigure(this.settings.commonForegroundColor, point.x + randX, point.y + randY));
		}
		this.getPaintSurface().drawFigure(cfig);
	}
}
