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


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A text drawing tool.
 */
public class TextTool extends BasicPaintSession implements PaintTool {
	private ToolSettings settings;
	private String drawText = PaintExample.getResourceString("tool.Text.settings.defaulttext");

	/**
	 * Constructs a PaintTool.
	 *
	 * @param toolSettings the new tool settings
	 * @param paintSurface the PaintSurface we will render on.
	 */
	public TextTool(final ToolSettings toolSettings, final PaintSurface paintSurface) {
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
		return PaintExample.getResourceString("tool.Text.label");
	}

	/**
	 * Activates the tool.
	 */
	@Override
	public void beginSession() {
		this.getPaintSurface().setStatusMessage(PaintExample.getResourceString(
			"session.Text.message"));
	}

	/**
	 * Deactivates the tool.
	 */
	@Override
	public void endSession() {
		this.getPaintSurface().clearRubberbandSelection();
	}

	/**
	 * Aborts the current operation.
	 */
	@Override
	public void resetSession() {
		this.getPaintSurface().clearRubberbandSelection();
	}

	/**
	 * Handles a mouseDown event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseDown(final MouseEvent event) {
		if (event.button == 1) {
			// draw with left mouse button
			this.getPaintSurface().commitRubberbandSelection();
		} else {
			// set text with right mouse button
			this.getPaintSurface().clearRubberbandSelection();
			final Shell shell = this.getPaintSurface().getShell();
			final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setText(PaintExample.getResourceString("tool.Text.dialog.title"));
			dialog.setLayout(new GridLayout());
			final Label label = new Label(dialog, SWT.NONE);
			label.setText(PaintExample.getResourceString("tool.Text.dialog.message"));
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			final Text field = new Text(dialog, SWT.SINGLE | SWT.BORDER);
			field.setText(this.drawText);
			field.selectAll();
			field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			final Composite buttons = new Composite(dialog, SWT.NONE);
			final GridLayout layout = new GridLayout(2, true);
			layout.marginWidth = 0;
			buttons.setLayout(layout);
			buttons.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
			final Button ok = new Button(buttons, SWT.PUSH);
			ok.setText(PaintExample.getResourceString("OK"));
			ok.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			ok.addSelectionListener(widgetSelectedAdapter(e -> {
				this.drawText = field.getText();
				dialog.dispose();
			}));
			final Button cancel = new Button(buttons, SWT.PUSH);
			cancel.setText(PaintExample.getResourceString("Cancel"));
			cancel.addSelectionListener(widgetSelectedAdapter(e -> dialog.dispose()));
			dialog.setDefaultButton(ok);
			dialog.pack();
			dialog.open();
			final Display display = dialog.getDisplay();
			while (! shell.isDisposed() && ! dialog.isDisposed()) {
				if (! display.readAndDispatch()) {
          display.sleep();
        }
			}
		}
	}

	/**
	 * Handles a mouseDoubleClick event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent event) {
	}

	/**
	 * Handles a mouseUp event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseUp(final MouseEvent event) {
	}

	/**
	 * Handles a mouseMove event.
	 *
	 * @param event the mouse event detail information
	 */
	@Override
	public void mouseMove(final MouseEvent event) {
		final PaintSurface ps = this.getPaintSurface();
		ps.setStatusCoord(ps.getCurrentPosition());
		ps.clearRubberbandSelection();
		ps.addRubberbandSelection(
			new TextFigure(this.settings.commonForegroundColor, this.settings.commonFont,
				this.drawText, event.x, event.y));
	}
}
