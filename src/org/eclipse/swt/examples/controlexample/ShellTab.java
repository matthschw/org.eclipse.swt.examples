/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
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


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

class ShellTab extends Tab {
	/* Style widgets added to the "Style" groups, and "Other" group */
	Button noParentButton, parentButton;
	Button noTrimButton, closeButton, titleButton, minButton, maxButton, borderButton, resizeButton, onTopButton, toolButton, sheetButton, shellTrimButton, dialogTrimButton, noMoveButton;
	Button createButton, closeAllButton;
	Button modelessButton, primaryModalButton, applicationModalButton, systemModalButton;
	Button imageButton;
	Group parentStyleGroup, modalStyleGroup;

	/* Variables used to track the open shells */
	int shellCount = 0;
	Shell [] shells = new Shell [4];

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	ShellTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Close all the example shells.
	 */
	void closeAllShells() {
		for (int i = 0; i<this.shellCount; i++) {
			if ((this.shells [i] != null) & !this.shells [i].isDisposed ()) {
				this.shells [i].dispose();
				this.shells [i] = null;
			}
		}
		this.shellCount = 0;
	}

	/**
	 * Handle the Create button selection event.
	 *
	 * @param event org.eclipse.swt.events.SelectionEvent
	 */
	public void createButtonSelected(final SelectionEvent event) {

		/*
		 * Remember the example shells so they
		 * can be disposed by the user.
		 */
		if (this.shellCount >= this.shells.length) {
			final Shell [] newShells = new Shell [this.shells.length + 4];
			System.arraycopy (this.shells, 0, newShells, 0, this.shells.length);
			this.shells = newShells;
		}

		/* Compute the shell style */
		int style = SWT.NONE;
		if (this.noTrimButton.getSelection()) {
      style |= SWT.NO_TRIM;
    }
		if (this.noMoveButton.getSelection()) {
      style |= SWT.NO_MOVE;
    }
		if (this.closeButton.getSelection()) {
      style |= SWT.CLOSE;
    }
		if (this.titleButton.getSelection()) {
      style |= SWT.TITLE;
    }
		if (this.minButton.getSelection()) {
      style |= SWT.MIN;
    }
		if (this.maxButton.getSelection()) {
      style |= SWT.MAX;
    }
		if (this.borderButton.getSelection()) {
      style |= SWT.BORDER;
    }
		if (this.resizeButton.getSelection()) {
      style |= SWT.RESIZE;
    }
		if (this.onTopButton.getSelection()) {
      style |= SWT.ON_TOP;
    }
		if (this.toolButton.getSelection()) {
      style |= SWT.TOOL;
    }
		if (this.sheetButton.getSelection()) {
      style |= SWT.SHEET;
    }
		if (this.modelessButton.getSelection()) {
      style |= SWT.MODELESS;
    }
		if (this.primaryModalButton.getSelection()) {
      style |= SWT.PRIMARY_MODAL;
    }
		if (this.applicationModalButton.getSelection()) {
      style |= SWT.APPLICATION_MODAL;
    }
		if (this.systemModalButton.getSelection()) {
      style |= SWT.SYSTEM_MODAL;
    }

		/* Create the shell with or without a parent */
		if (this.noParentButton.getSelection ()) {
			this.shells [this.shellCount] = new Shell (style);
		} else {
			this.shells [this.shellCount] = new Shell (this.shell, style);
		}
		final Shell currentShell = this.shells [this.shellCount];
		currentShell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		final Button button = new Button(currentShell, SWT.CHECK);
		button.setBounds(20, 20, 120, 30);
		button.setText(ControlExample.getResourceString("FullScreen"));
		button.addSelectionListener(widgetSelectedAdapter(e -> currentShell.setFullScreen(button.getSelection())));
		final Button close = new Button(currentShell, SWT.PUSH);
		close.setBounds(160, 20, 120, 30);
		close.setText(ControlExample.getResourceString("Close"));
		close.addListener(SWT.Selection, event1 -> {currentShell.dispose(); this.shellCount--;});

		/* Set the size, title, and image, and open the shell */
		currentShell.setSize (300, 100);
		currentShell.setText (ControlExample.getResourceString("Title") + this.shellCount);
		if (this.imageButton.getSelection()) {
      currentShell.setImage(this.instance.images[ControlExample.ciTarget]);
    }
		if (this.backgroundImageButton.getSelection()) {
      currentShell.setBackgroundImage(this.instance.images[ControlExample.ciBackground]);
    }
		this.hookListeners (currentShell);
		currentShell.open ();
		this.shellCount++;
	}

	/**
	 * Creates the "Control" group.
	 */
	@Override
	void createControlGroup () {
		/*
		 * Create the "Control" group.  This is the group on the
		 * right half of each example tab.  It consists of the
		 * style group, the 'other' group and the size group.
		 */
		this.controlGroup = new Group (this.tabFolderPage, SWT.NONE);
		this.controlGroup.setLayout (new GridLayout (2, true));
		this.controlGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		this.controlGroup.setText (ControlExample.getResourceString("Parameters"));

		/* Create a group for the decoration style controls */
		this.styleGroup = new Group (this.controlGroup, SWT.NONE);
		this.styleGroup.setLayout (new GridLayout ());
		this.styleGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, false, false, 1, 3));
		this.styleGroup.setText (ControlExample.getResourceString("Decoration_Styles"));

		/* Create a group for the modal style controls */
		this.modalStyleGroup = new Group (this.controlGroup, SWT.NONE);
		this.modalStyleGroup.setLayout (new GridLayout ());
		this.modalStyleGroup.setLayoutData (new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		this.modalStyleGroup.setText (ControlExample.getResourceString("Modal_Styles"));

		/* Create a group for the 'other' controls */
		this.otherGroup = new Group (this.controlGroup, SWT.NONE);
		this.otherGroup.setLayout (new GridLayout ());
		this.otherGroup.setLayoutData (new GridData(SWT.FILL, SWT.FILL, false, false));
		this.otherGroup.setText (ControlExample.getResourceString("Other"));

		/* Create a group for the parent style controls */
		this.parentStyleGroup = new Group (this.controlGroup, SWT.NONE);
		this.parentStyleGroup.setLayout (new GridLayout ());
		final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		this.parentStyleGroup.setLayoutData (gridData);
		this.parentStyleGroup.setText (ControlExample.getResourceString("Parent"));
	}

	/**
	 * Creates the "Control" widget children.
	 */
	@Override
	void createControlWidgets () {

		/* Create the parent style buttons */
		this.noParentButton = new Button (this.parentStyleGroup, SWT.RADIO);
		this.noParentButton.setText (ControlExample.getResourceString("No_Parent"));
		this.parentButton = new Button (this.parentStyleGroup, SWT.RADIO);
		this.parentButton.setText (ControlExample.getResourceString("Parent"));

		/* Create the decoration style buttons */
		this.noTrimButton = new Button (this.styleGroup, SWT.CHECK);
		this.noTrimButton.setText ("SWT.NO_TRIM");
		this.noMoveButton = new Button (this.styleGroup, SWT.CHECK);
		this.noMoveButton.setText ("SWT.NO_MOVE");
		this.closeButton = new Button (this.styleGroup, SWT.CHECK);
		this.closeButton.setText ("SWT.CLOSE");
		this.titleButton = new Button (this.styleGroup, SWT.CHECK);
		this.titleButton.setText ("SWT.TITLE");
		this.minButton = new Button (this.styleGroup, SWT.CHECK);
		this.minButton.setText ("SWT.MIN");
		this.maxButton = new Button (this.styleGroup, SWT.CHECK);
		this.maxButton.setText ("SWT.MAX");
		this.borderButton = new Button (this.styleGroup, SWT.CHECK);
		this.borderButton.setText ("SWT.BORDER");
		this.resizeButton = new Button (this.styleGroup, SWT.CHECK);
		this.resizeButton.setText ("SWT.RESIZE");
		this.onTopButton = new Button (this.styleGroup, SWT.CHECK);
		this.onTopButton.setText ("SWT.ON_TOP");
		this.toolButton = new Button (this.styleGroup, SWT.CHECK);
		this.toolButton.setText ("SWT.TOOL");
		this.sheetButton = new Button (this.styleGroup, SWT.CHECK);
		this.sheetButton.setText ("SWT.SHEET");
		final Label separator = new Label(this.styleGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false));
		this.shellTrimButton = new Button (this.styleGroup, SWT.CHECK);
		this.shellTrimButton.setText ("SWT.SHELL_TRIM");
		this.dialogTrimButton = new Button (this.styleGroup, SWT.CHECK);
		this.dialogTrimButton.setText ("SWT.DIALOG_TRIM");

		/* Create the modal style buttons */
		this.modelessButton = new Button (this.modalStyleGroup, SWT.RADIO);
		this.modelessButton.setText ("SWT.MODELESS");
		this.primaryModalButton = new Button (this.modalStyleGroup, SWT.RADIO);
		this.primaryModalButton.setText ("SWT.PRIMARY_MODAL");
		this.applicationModalButton = new Button (this.modalStyleGroup, SWT.RADIO);
		this.applicationModalButton.setText ("SWT.APPLICATION_MODAL");
		this.systemModalButton = new Button (this.modalStyleGroup, SWT.RADIO);
		this.systemModalButton.setText ("SWT.SYSTEM_MODAL");

		/* Create the 'other' buttons */
		this.imageButton = new Button (this.otherGroup, SWT.CHECK);
		this.imageButton.setText (ControlExample.getResourceString("Image"));
		this.backgroundImageButton = new Button(this.otherGroup, SWT.CHECK);
		this.backgroundImageButton.setText(ControlExample.getResourceString("BackgroundImage"));

		this.createSetGetGroup();

		/* Create the "create" and "closeAll" buttons */
		this.createButton = new Button (this.controlGroup, SWT.NONE);
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
		this.createButton.setLayoutData (gridData);
		this.createButton.setText (ControlExample.getResourceString("Create_Shell"));
		this.closeAllButton = new Button (this.controlGroup, SWT.NONE);
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		this.closeAllButton.setText (ControlExample.getResourceString("Close_All_Shells"));
		this.closeAllButton.setLayoutData (gridData);

		/* Add the listeners */
		this.createButton.addSelectionListener(widgetSelectedAdapter(e -> this.createButtonSelected(e)));
		this.closeAllButton.addSelectionListener(widgetSelectedAdapter(e -> this.closeAllShells ()));
		final SelectionListener decorationButtonListener = widgetSelectedAdapter(event -> this.decorationButtonSelected(event));
		this.noTrimButton.addSelectionListener (decorationButtonListener);
		this.noMoveButton.addSelectionListener(decorationButtonListener);
		this.closeButton.addSelectionListener (decorationButtonListener);
		this.titleButton.addSelectionListener (decorationButtonListener);
		this.minButton.addSelectionListener (decorationButtonListener);
		this.maxButton.addSelectionListener (decorationButtonListener);
		this.borderButton.addSelectionListener (decorationButtonListener);
		this.resizeButton.addSelectionListener (decorationButtonListener);
		this.dialogTrimButton.addSelectionListener (decorationButtonListener);
		this.shellTrimButton.addSelectionListener (decorationButtonListener);
		this.applicationModalButton.addSelectionListener (decorationButtonListener);
		this.systemModalButton.addSelectionListener (decorationButtonListener);

		/* Set the default state */
		this.noParentButton.setSelection (true);
		this.modelessButton.setSelection (true);
	}

	/**
	 * Handle a decoration button selection event.
	 *
	 * @param event org.eclipse.swt.events.SelectionEvent
	 */
	public void decorationButtonSelected(final SelectionEvent event) {
		final Button widget = (Button) event.widget;

		/*
		 * Make sure that if the modal style is SWT.APPLICATION_MODAL
		 * or SWT.SYSTEM_MODAL the style SWT.CLOSE is also selected.
		 * This is to make sure the user can close the shell.
		 */
		if ((widget == this.applicationModalButton) || (widget == this.systemModalButton)) {
			if (widget.getSelection()) {
				this.closeButton.setSelection (true);
				this.noTrimButton.setSelection (false);
			}
			return;
		}
		if (widget == this.closeButton) {
			if (this.applicationModalButton.getSelection() || this.systemModalButton.getSelection()) {
				this.closeButton.setSelection (true);
			}
		}
		/*
		 * Make sure that if the SWT.NO_TRIM button is selected
		 * then all other decoration buttons are deselected.
		 */
		if (widget.getSelection()) {
			if (widget == this.noTrimButton) {
				if (this.applicationModalButton.getSelection() || this.systemModalButton.getSelection()) {
					this.noTrimButton.setSelection (false);
					return;
				}
				this.closeButton.setSelection (false);
				this.titleButton.setSelection (false);
				this.minButton.setSelection (false);
				this.maxButton.setSelection (false);
				this.borderButton.setSelection (false);
				this.resizeButton.setSelection (false);
			} else {
				this.noTrimButton.setSelection (false);
			}
		}

		/*
		 * Make sure that the SWT.DIALOG_TRIM and SWT.SHELL_TRIM buttons
		 * are consistent.
		 */
		if ((widget == this.dialogTrimButton) || (widget == this.shellTrimButton)) {
			if (widget.getSelection() && (widget == this.dialogTrimButton)) {
				this.shellTrimButton.setSelection(false);
			} else {
				this.dialogTrimButton.setSelection(false);
			}
			//SHELL_TRIM = CLOSE | TITLE | MIN | MAX | RESIZE;
			//DIALOG_TRIM = TITLE | CLOSE | BORDER;
			this.closeButton.setSelection (widget.getSelection ());
			this.titleButton.setSelection (widget.getSelection ());
			this.minButton.setSelection ((widget == this.shellTrimButton) && widget.getSelection( ));
			this.maxButton.setSelection ((widget == this.shellTrimButton) && widget.getSelection ());
			this.borderButton.setSelection ((widget == this.dialogTrimButton) && widget.getSelection ());
			this.resizeButton.setSelection ((widget == this.shellTrimButton) && widget.getSelection ());
		} else {
			final boolean title = this.titleButton.getSelection ();
			final boolean close = this.closeButton.getSelection ();
			final boolean min = this.minButton.getSelection ();
			final boolean max = this.maxButton.getSelection ();
			final boolean border = this.borderButton.getSelection ();
			final boolean resize = this.resizeButton.getSelection ();
			this.dialogTrimButton.setSelection(title && close && border && !min && !max && !resize);
			this.shellTrimButton.setSelection(title && close && min && max && resize && !border);
		}
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Shell";
	}

	@Override
	String[] getMethodNames() {
		return new String[] {"Alpha", "Bounds", "MinimumSize", "MaximumSize", "Modified", "Text"};
	}

	@Override
	Widget[] getExampleWidgets() {
		return this.shellCount == 0 ? new Widget[0] : this.shells;
	}

}
