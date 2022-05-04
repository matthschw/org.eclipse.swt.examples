/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
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


import org.eclipse.swt.graphics.Region;

/**
 * Container for Figure objects with stacking preview mechanism.
 */
public class ContainerFigure extends Figure {
	private static final int INITIAL_ARRAY_SIZE = 16;

	Figure[]   objectStack = null;
	int      nextIndex = 0;

	/**
	 * Constructs an empty Container
	 */
	public ContainerFigure() {
	}
	/**
	 * Adds an object to the container for later drawing.
	 *
	 * @param object the object to add to the drawing list
	 */
	public void add(final Figure object) {
		if (this.objectStack == null) {
			this.objectStack = new Figure[INITIAL_ARRAY_SIZE];
		} else if (this.objectStack.length <= this.nextIndex) {
			final Figure[] newObjectStack = new Figure[this.objectStack.length * 2];
			System.arraycopy(this.objectStack, 0, newObjectStack, 0, this.objectStack.length);
			this.objectStack = newObjectStack;
		}
		this.objectStack[this.nextIndex] = object;
		++this.nextIndex;
	}
	/**
	 * Determines if the container is empty.
	 * @return true if the container is empty
	 */
	public boolean isEmpty() {
		return this.nextIndex == 0;
	}
	/**
	 * Adds an object to the container and draws its preview then updates the supplied preview state.
	 *
	 * @param object the object to add to the drawing list
	 * @param gc the GC to draw on
	 * @param offset the offset to add to virtual coordinates to get display coordinates
	 * @param rememberedState the state returned by a previous drawPreview() or addAndPreview()
	 *        using this Container, may be null if there was no such previous call
	 * @return object state that must be passed to erasePreview() later to erase this object
	 */
//	public Object addAndPreview(Figure object, GC gc, Point offset, Object rememberedState) {
//		Object[] stateStack = (Object[]) rememberedState;
//		if (stateStack == null) {
//			stateStack = new Object[INITIAL_ARRAY_SIZE];
//		} else if (stateStack.length <= nextIndex) {
//			Object[] newStateStack = new Object[stateStack.length * 2];
//			System.arraycopy(stateStack, 0, newStateStack, 0, stateStack.length);
//			stateStack = newStateStack;
//		}
//		add(object);
//		stateStack[nextIndex - 1] = object.drawPreview(gc, offset);
//		return stateStack;
//	}
	/**
	 * Clears the container.
	 * <p>
	 * Note that erasePreview() cannot be called after this point to erase any previous
	 * drawPreview()'s.
	 * </p>
	 */
	public void clear() {
		while (--this.nextIndex > 0) {
      this.objectStack[this.nextIndex] = null;
    }
		this.nextIndex = 0;
	}
	@Override
	public void draw(final FigureDrawContext fdc) {
		for (int i = 0; i < this.nextIndex; ++i) {
      this.objectStack[i].draw(fdc);
    }
	}
	@Override
	public void addDamagedRegion(final FigureDrawContext fdc, final Region region) {
		for (int i = 0; i < this.nextIndex; ++i) {
      this.objectStack[i].addDamagedRegion(fdc, region);
    }
	}
}
