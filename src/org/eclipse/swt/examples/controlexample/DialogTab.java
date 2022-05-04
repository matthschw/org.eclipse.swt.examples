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
package org.eclipse.swt.examples.controlexample;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

class DialogTab extends Tab {
	/* Example widgets and groups that contain them */
	Group dialogStyleGroup, resultGroup;
	Text textWidget;

	/* Style widgets added to the "Style" group */
	Combo dialogCombo;
	Button createButton;
	Button okButton, cancelButton;
	Button yesButton, noButton;
	Button retryButton;
	Button abortButton, ignoreButton;
	Button iconErrorButton, iconInformationButton, iconQuestionButton;
	Button iconWarningButton, iconWorkingButton, noIconButton;
	Button primaryModalButton, applicationModalButton, systemModalButton;
	Button sheetButton;
	Button effectsVisibleButton, usePreviousResultButton;
	Button saveButton, openButton, multiButton;
	RGB colorDialogResult, fontDialogColorResult;
	RGB[] colorDialogCustomColors;
	String directoryDialogResult;
	String fileDialogResult;
	int fileDialogIndexResult;
	FontData[] fontDialogFontListResult;
	PrinterData printDialogResult;

	static String [] FilterExtensions	= {"*.txt", "*.bat", "*.doc;*.rtf", "*"};
	static String [] FilterNames		= {ControlExample.getResourceString("FilterName_0"),
										   ControlExample.getResourceString("FilterName_1"),
										   ControlExample.getResourceString("FilterName_2"),
										   ControlExample.getResourceString("FilterName_3")};

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	DialogTab(final ControlExample instance) {
		super(instance);
	}

	/**
	 * Handle a button style selection event.
	 *
	 * @param event the selection event
	 */
	void buttonStyleSelected(final SelectionEvent event) {
		/*
		 * Only certain combinations of button styles are
		 * supported for various dialogs.  Make sure the
		 * control widget reflects only valid combinations.
		 */
		final boolean ok = this.okButton.getSelection ();
		final boolean cancel = this.cancelButton.getSelection ();
		final boolean yes = this.yesButton.getSelection ();
		final boolean no = this.noButton.getSelection ();
		final boolean abort = this.abortButton.getSelection ();
		final boolean retry = this.retryButton.getSelection ();
		final boolean ignore = this.ignoreButton.getSelection ();

		this.okButton.setEnabled (!(yes || no || retry || abort || ignore));
		this.cancelButton.setEnabled (!(abort || ignore || (yes != no)));
		this.yesButton.setEnabled (!(ok || retry || abort || ignore || (cancel && !yes && !no)));
		this.noButton.setEnabled (!(ok || retry || abort || ignore || (cancel && !yes && !no)));
		this.retryButton.setEnabled (!(ok || yes || no));
		this.abortButton.setEnabled (!(ok || cancel || yes || no));
		this.ignoreButton.setEnabled (!(ok || cancel || yes || no));

		this.createButton.setEnabled (
				!(ok || cancel || yes || no || retry || abort || ignore) ||
				ok ||
				(ok && cancel) ||
				(yes && no) ||
				(yes && no && cancel) ||
				(retry && cancel) ||
				(abort && retry && ignore));


	}

	/**
	 * Handle the create button selection event.
	 *
	 * @param event org.eclipse.swt.events.SelectionEvent
	 */
	void createButtonSelected(final SelectionEvent event) {

		/* Compute the appropriate dialog style */
		int style = this.getDefaultStyle();
		if (this.okButton.getEnabled () && this.okButton.getSelection ()) {
      style |= SWT.OK;
    }
		if (this.cancelButton.getEnabled () && this.cancelButton.getSelection ()) {
      style |= SWT.CANCEL;
    }
		if (this.yesButton.getEnabled () && this.yesButton.getSelection ()) {
      style |= SWT.YES;
    }
		if (this.noButton.getEnabled () && this.noButton.getSelection ()) {
      style |= SWT.NO;
    }
		if (this.retryButton.getEnabled () && this.retryButton.getSelection ()) {
      style |= SWT.RETRY;
    }
		if (this.abortButton.getEnabled () && this.abortButton.getSelection ()) {
      style |= SWT.ABORT;
    }
		if (this.ignoreButton.getEnabled () && this.ignoreButton.getSelection ()) {
      style |= SWT.IGNORE;
    }
		if (this.iconErrorButton.getEnabled () && this.iconErrorButton.getSelection ()) {
      style |= SWT.ICON_ERROR;
    }
		if (this.iconInformationButton.getEnabled () && this.iconInformationButton.getSelection ()) {
      style |= SWT.ICON_INFORMATION;
    }
		if (this.iconQuestionButton.getEnabled () && this.iconQuestionButton.getSelection ()) {
      style |= SWT.ICON_QUESTION;
    }
		if (this.iconWarningButton.getEnabled () && this.iconWarningButton.getSelection ()) {
      style |= SWT.ICON_WARNING;
    }
		if (this.iconWorkingButton.getEnabled () && this.iconWorkingButton.getSelection ()) {
      style |= SWT.ICON_WORKING;
    }
		if (this.primaryModalButton.getEnabled () && this.primaryModalButton.getSelection ()) {
      style |= SWT.PRIMARY_MODAL;
    }
		if (this.applicationModalButton.getEnabled () && this.applicationModalButton.getSelection ()) {
      style |= SWT.APPLICATION_MODAL;
    }
		if (this.systemModalButton.getEnabled () && this.systemModalButton.getSelection ()) {
      style |= SWT.SYSTEM_MODAL;
    }
		if (this.sheetButton.getSelection ()) {
      style |= SWT.SHEET;
    }
		if (this.saveButton.getEnabled () && this.saveButton.getSelection ()) {
      style |= SWT.SAVE;
    }
		if (this.openButton.getEnabled () && this.openButton.getSelection ()) {
      style |= SWT.OPEN;
    }
		if (this.multiButton.getEnabled () && this.multiButton.getSelection ()) {
      style |= SWT.MULTI;
    }

		/* Open the appropriate dialog type */
		final String name = this.dialogCombo.getText ();

		if (name.equals (ControlExample.getResourceString("ColorDialog"))) {
			final ColorDialog dialog = new ColorDialog (this.shell ,style);
			if (this.usePreviousResultButton.getSelection()) {
				dialog.setRGB (this.colorDialogResult);
				dialog.setRGBs(this.colorDialogCustomColors);
			}
			dialog.setText (ControlExample.getResourceString("Title"));
			final RGB result = dialog.open ();
			this.textWidget.append (ControlExample.getResourceString("ColorDialog") + Text.DELIMITER);
			this.textWidget.append (ControlExample.getResourceString("Result", "" + result) + Text.DELIMITER);
			this.textWidget.append ("getRGB() = " + dialog.getRGB() + Text.DELIMITER);
			this.textWidget.append ("getRGBs() =" + Text.DELIMITER);
			final RGB[] rgbs = dialog.getRGBs();
			if (rgbs != null) {
				for (final RGB rgbColor : rgbs) {
					this.textWidget.append ("\t" + rgbColor + Text.DELIMITER);
				}
			}
			this.textWidget.append (Text.DELIMITER);
			this.colorDialogResult = result;
			this.colorDialogCustomColors = rgbs;
			return;
		}

		if (name.equals (ControlExample.getResourceString("DirectoryDialog"))) {
			final DirectoryDialog dialog = new DirectoryDialog (this.shell, style);
			if (this.usePreviousResultButton.getSelection()) {
				dialog.setFilterPath (this.directoryDialogResult);
			}
			dialog.setMessage (ControlExample.getResourceString("Example_string"));
			dialog.setText (ControlExample.getResourceString("Title"));
			final String result = dialog.open ();
			this.textWidget.append (ControlExample.getResourceString("DirectoryDialog") + Text.DELIMITER);
			this.textWidget.append (ControlExample.getResourceString("Result", "" + result) + Text.DELIMITER + Text.DELIMITER);
			this.directoryDialogResult = result;
			return;
		}

		if (name.equals (ControlExample.getResourceString("FileDialog"))) {
			final FileDialog dialog = new FileDialog (this.shell, style);
			if (this.usePreviousResultButton.getSelection()) {
				dialog.setFileName (this.fileDialogResult);
				dialog.setFilterIndex(this.fileDialogIndexResult);
			}
			dialog.setFilterNames (FilterNames);
			dialog.setFilterExtensions (FilterExtensions);
			dialog.setText (ControlExample.getResourceString("Title"));
			final String result = dialog.open();
			this.textWidget.append (ControlExample.getResourceString("FileDialog") + Text.DELIMITER);
			this.textWidget.append (ControlExample.getResourceString("Result", "" + result) + Text.DELIMITER);
			this.textWidget.append ("getFilterIndex() =" + dialog.getFilterIndex() + Text.DELIMITER);
			this.textWidget.append ("getFilterPath() =" + dialog.getFilterPath() + Text.DELIMITER);
			this.textWidget.append ("getFileName() =" + dialog.getFileName() + Text.DELIMITER);
			this.textWidget.append ("getFileNames() =" + Text.DELIMITER);
			final String [] files = dialog.getFileNames ();
			for (final String file : files) {
				this.textWidget.append ("\t" + file + Text.DELIMITER);
			}
			this.textWidget.append (Text.DELIMITER);
			this.fileDialogResult = result;
			this.fileDialogIndexResult = dialog.getFilterIndex();
			return;
		}

		if (name.equals (ControlExample.getResourceString("FontDialog"))) {
			final FontDialog dialog = new FontDialog (this.shell, style);
			if (this.usePreviousResultButton.getSelection()) {
				dialog.setFontList (this.fontDialogFontListResult);
				dialog.setRGB(this.fontDialogColorResult);
			}
			dialog.setEffectsVisible(this.effectsVisibleButton.getSelection());
			dialog.setText (ControlExample.getResourceString("Title"));
			final FontData result = dialog.open ();
			this.textWidget.append (ControlExample.getResourceString("FontDialog") + Text.DELIMITER);
			this.textWidget.append (ControlExample.getResourceString("Result", "" + result) + Text.DELIMITER);
			this.textWidget.append ("getFontList() =" + Text.DELIMITER);
			final FontData [] fonts = dialog.getFontList ();
			if (fonts != null) {
				for (final FontData font : fonts) {
					this.textWidget.append ("\t" + font + Text.DELIMITER);
				}
			}
			this.textWidget.append ("getEffectsVisible() = " + dialog.getEffectsVisible() + Text.DELIMITER);
			this.textWidget.append ("getRGB() = " + dialog.getRGB() + Text.DELIMITER + Text.DELIMITER);
			this.fontDialogFontListResult = dialog.getFontList ();
			this.fontDialogColorResult = dialog.getRGB();
			return;
		}

		if (name.equals (ControlExample.getResourceString("PrintDialog"))) {
			final PrintDialog dialog = new PrintDialog (this.shell, style);
			if (this.usePreviousResultButton.getSelection()) {
				dialog.setPrinterData(this.printDialogResult);
			}
			dialog.setText(ControlExample.getResourceString("Title"));
			final PrinterData result = dialog.open ();
			this.textWidget.append (ControlExample.getResourceString("PrintDialog") + Text.DELIMITER);
			this.textWidget.append (ControlExample.getResourceString("Result", "" + result) + Text.DELIMITER);
			if (result != null) {
				this.textWidget.append ("printerData.scope = " + (result.scope == PrinterData.PAGE_RANGE ? "PAGE_RANGE" : result.scope == PrinterData.SELECTION ? "SELECTION" : "ALL_PAGES") + Text.DELIMITER);
				this.textWidget.append ("printerData.startPage = " + result.startPage + Text.DELIMITER);
				this.textWidget.append ("printerData.endPage = " + result.endPage + Text.DELIMITER);
				this.textWidget.append ("printerData.printToFile = " + result.printToFile + Text.DELIMITER);
				this.textWidget.append ("printerData.fileName = " + result.fileName + Text.DELIMITER);
				this.textWidget.append ("printerData.orientation = " + (result.orientation == PrinterData.LANDSCAPE ? "LANDSCAPE" : "PORTRAIT") + Text.DELIMITER);
				this.textWidget.append ("printerData.copyCount = " + result.copyCount + Text.DELIMITER);
				this.textWidget.append ("printerData.collate = " + result.collate + Text.DELIMITER);
				this.textWidget.append ("printerData.duplex = " + (result.duplex == PrinterData.DUPLEX_LONG_EDGE ? "DUPLEX_LONG_EDGE" : result.duplex == PrinterData.DUPLEX_SHORT_EDGE ? "DUPLEX_SHORT_EDGE" : "NONE") + Text.DELIMITER);
			}
			this.textWidget.append (Text.DELIMITER);
			this.printDialogResult = result;
			return;
		}

		if (name.equals(ControlExample.getResourceString("MessageBox"))) {
			final MessageBox dialog = new MessageBox (this.shell, style);
			dialog.setMessage (ControlExample.getResourceString("Example_string"));
			dialog.setText (ControlExample.getResourceString("Title"));
			final int result = dialog.open ();
			this.textWidget.append (ControlExample.getResourceString("MessageBox") + Text.DELIMITER);
			/*
			 * The resulting integer depends on the original
			 * dialog style.  Decode the result and display it.
			 */
			switch (result) {
				case SWT.OK:
					this.textWidget.append (ControlExample.getResourceString("Result", "SWT.OK"));
					break;
				case SWT.YES:
					this.textWidget.append (ControlExample.getResourceString("Result", "SWT.YES"));
					break;
				case SWT.NO:
					this.textWidget.append (ControlExample.getResourceString("Result", "SWT.NO"));
					break;
				case SWT.CANCEL:
					this.textWidget.append (ControlExample.getResourceString("Result", "SWT.CANCEL"));
					break;
				case SWT.ABORT:
					this.textWidget.append (ControlExample.getResourceString("Result", "SWT.ABORT"));
					break;
				case SWT.RETRY:
					this.textWidget.append (ControlExample.getResourceString("Result", "SWT.RETRY"));
					break;
				case SWT.IGNORE:
					this.textWidget.append (ControlExample.getResourceString("Result", "SWT.IGNORE"));
					break;
				default:
					this.textWidget.append(ControlExample.getResourceString("Result", "" + result));
					break;
			}
			this.textWidget.append (Text.DELIMITER + Text.DELIMITER);
		}
	}

	/**
	 * Creates the "Control" group.
	 */
	@Override
	void createControlGroup () {
		/*
		 * Create the "Control" group.  This is the group on the
		 * right half of each example tab.  It consists of the
		 * style group, the display group and the size group.
		 */
		this.controlGroup = new Group (this.tabFolderPage, SWT.NONE);
		final GridLayout gridLayout= new GridLayout ();
		this.controlGroup.setLayout(gridLayout);
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		this.controlGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		this.controlGroup.setText (ControlExample.getResourceString("Parameters"));

		/*
		 * Create a group to hold the dialog style combo box and
		 * create dialog button.
		 */
		this.dialogStyleGroup = new Group (this.controlGroup, SWT.NONE);
		this.dialogStyleGroup.setLayout (new GridLayout ());
		final GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 2;
		this.dialogStyleGroup.setLayoutData (gridData);
		this.dialogStyleGroup.setText (ControlExample.getResourceString("Dialog_Type"));
	}

	/**
	 * Creates the "Control" widget children.
	 */
	@Override
	void createControlWidgets () {

		/* Create the combo */
		final String [] strings = {
			ControlExample.getResourceString("ColorDialog"),
			ControlExample.getResourceString("DirectoryDialog"),
			ControlExample.getResourceString("FileDialog"),
			ControlExample.getResourceString("FontDialog"),
			ControlExample.getResourceString("PrintDialog"),
			ControlExample.getResourceString("MessageBox"),
		};
		this.dialogCombo = new Combo (this.dialogStyleGroup, SWT.READ_ONLY);
		this.dialogCombo.setItems (strings);
		this.dialogCombo.setText (strings [0]);
		this.dialogCombo.setVisibleItemCount(strings.length);

		/* Create the create dialog button */
		this.createButton = new Button(this.dialogStyleGroup, SWT.NONE);
		this.createButton.setText (ControlExample.getResourceString("Create_Dialog"));
		this.createButton.setLayoutData (new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		/* Create a group for the various dialog button style controls */
		final Group buttonStyleGroup = new Group (this.controlGroup, SWT.NONE);
		buttonStyleGroup.setLayout (new GridLayout ());
		buttonStyleGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		buttonStyleGroup.setText (ControlExample.getResourceString("Button_Styles"));

		/* Create the button style buttons */
		this.okButton = new Button (buttonStyleGroup, SWT.CHECK);
		this.okButton.setText ("SWT.OK");
		this.cancelButton = new Button (buttonStyleGroup, SWT.CHECK);
		this.cancelButton.setText ("SWT.CANCEL");
		this.yesButton = new Button (buttonStyleGroup, SWT.CHECK);
		this.yesButton.setText ("SWT.YES");
		this.noButton = new Button (buttonStyleGroup, SWT.CHECK);
		this.noButton.setText ("SWT.NO");
		this.retryButton = new Button (buttonStyleGroup, SWT.CHECK);
		this.retryButton.setText ("SWT.RETRY");
		this.abortButton = new Button (buttonStyleGroup, SWT.CHECK);
		this.abortButton.setText ("SWT.ABORT");
		this.ignoreButton = new Button (buttonStyleGroup, SWT.CHECK);
		this.ignoreButton.setText ("SWT.IGNORE");

		/* Create a group for the icon style controls */
		final Group iconStyleGroup = new Group (this.controlGroup, SWT.NONE);
		iconStyleGroup.setLayout (new GridLayout ());
		iconStyleGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		iconStyleGroup.setText (ControlExample.getResourceString("Icon_Styles"));

		/* Create the icon style buttons */
		this.iconErrorButton = new Button (iconStyleGroup, SWT.RADIO);
		this.iconErrorButton.setText ("SWT.ICON_ERROR");
		this.iconInformationButton = new Button (iconStyleGroup, SWT.RADIO);
		this.iconInformationButton.setText ("SWT.ICON_INFORMATION");
		this.iconQuestionButton = new Button (iconStyleGroup, SWT.RADIO);
		this.iconQuestionButton.setText ("SWT.ICON_QUESTION");
		this.iconWarningButton = new Button (iconStyleGroup, SWT.RADIO);
		this.iconWarningButton.setText ("SWT.ICON_WARNING");
		this.iconWorkingButton = new Button (iconStyleGroup, SWT.RADIO);
		this.iconWorkingButton.setText ("SWT.ICON_WORKING");
		this.noIconButton = new Button (iconStyleGroup, SWT.RADIO);
		this.noIconButton.setText (ControlExample.getResourceString("No_Icon"));

		/* Create a group for the modal style controls */
		final Group modalStyleGroup = new Group (this.controlGroup, SWT.NONE);
		modalStyleGroup.setLayout (new GridLayout ());
		modalStyleGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		modalStyleGroup.setText (ControlExample.getResourceString("Modal_Styles"));

		/* Create the modal style buttons */
		this.primaryModalButton = new Button (modalStyleGroup, SWT.RADIO);
		this.primaryModalButton.setText ("SWT.PRIMARY_MODAL");
		this.applicationModalButton = new Button (modalStyleGroup, SWT.RADIO);
		this.applicationModalButton.setText ("SWT.APPLICATION_MODAL");
		this.systemModalButton = new Button (modalStyleGroup, SWT.RADIO);
		this.systemModalButton.setText ("SWT.SYSTEM_MODAL");

		/* Create a group for the file dialog style controls */
		final Group fileDialogStyleGroup = new Group (this.controlGroup, SWT.NONE);
		fileDialogStyleGroup.setLayout (new GridLayout ());
		fileDialogStyleGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		fileDialogStyleGroup.setText (ControlExample.getResourceString("File_Dialog_Styles"));

		/* Create the file dialog style buttons */
		this.openButton = new Button(fileDialogStyleGroup, SWT.RADIO);
		this.openButton.setText("SWT.OPEN");
		this.saveButton = new Button (fileDialogStyleGroup, SWT.RADIO);
		this.saveButton.setText ("SWT.SAVE");
		this.multiButton = new Button(fileDialogStyleGroup, SWT.CHECK);
		this.multiButton.setText("SWT.MULTI");

		/* Create the orientation group */
		if (RTL_SUPPORT_ENABLE) {
			this.createOrientationGroup();
		}

		/* Create a group for other style and setting controls */
		final Group otherGroup = new Group (this.controlGroup, SWT.NONE);
		otherGroup.setLayout (new GridLayout ());
		otherGroup.setLayoutData (new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		otherGroup.setText (ControlExample.getResourceString("Other"));

		/* Create the other style and setting controls */
		this.sheetButton = new Button(otherGroup, SWT.CHECK);
		this.sheetButton.setText("SWT.SHEET");
		this.usePreviousResultButton = new Button(otherGroup, SWT.CHECK);
		this.usePreviousResultButton.setText(ControlExample.getResourceString("Use_Previous_Result"));
		this.effectsVisibleButton = new Button(otherGroup, SWT.CHECK);
		this.effectsVisibleButton.setText("FontDialog.setEffectsVisible");

		/* Add the listeners */
		this.dialogCombo.addSelectionListener (widgetSelectedAdapter(event -> this.dialogSelected (event)));
		this.createButton.addSelectionListener (widgetSelectedAdapter(event -> this.createButtonSelected (event)));
		final SelectionListener buttonStyleListener = widgetSelectedAdapter(event -> this.buttonStyleSelected (event));
		this.okButton.addSelectionListener (buttonStyleListener);
		this.cancelButton.addSelectionListener (buttonStyleListener);
		this.yesButton.addSelectionListener (buttonStyleListener);
		this.noButton.addSelectionListener (buttonStyleListener);
		this.retryButton.addSelectionListener (buttonStyleListener);
		this.abortButton.addSelectionListener (buttonStyleListener);
		this.ignoreButton.addSelectionListener (buttonStyleListener);

		/* Set default values for style buttons */
		this.okButton.setEnabled (false);
		this.cancelButton.setEnabled (false);
		this.yesButton.setEnabled (false);
		this.noButton.setEnabled (false);
		this.retryButton.setEnabled (false);
		this.abortButton.setEnabled (false);
		this.ignoreButton.setEnabled (false);
		this.iconErrorButton.setEnabled (false);
		this.iconInformationButton.setEnabled (false);
		this.iconQuestionButton.setEnabled (false);
		this.iconWarningButton.setEnabled (false);
		this.iconWorkingButton.setEnabled (false);
		this.noIconButton.setEnabled (false);
		this.saveButton.setEnabled (false);
		this.openButton.setEnabled (false);
		this.openButton.setSelection (true);
		this.multiButton.setEnabled (false);
		this.noIconButton.setSelection (true);
		this.effectsVisibleButton.setEnabled(false);
		this.effectsVisibleButton.setSelection(true);
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();
		this.exampleGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));

		/*
		 * Create a group for the text widget to display
		 * the results returned by the example dialogs.
		 */
		this.resultGroup = new Group (this.exampleGroup, SWT.NONE);
		this.resultGroup.setLayout (new GridLayout ());
		this.resultGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.resultGroup.setText (ControlExample.getResourceString("Dialog_Result"));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {
		/*
		 * Create a multi lined, scrolled text widget for output.
		 */
		this.textWidget = new Text(this.resultGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final GridData gridData = new GridData (GridData.FILL_BOTH);
		this.textWidget.setLayoutData (gridData);
	}

	/**
	 * The platform dialogs do not have SWT listeners.
	 */
	@Override
	void createListenersGroup () {
	}

	/**
	 * Handle a dialog type combo selection event.
	 *
	 * @param event the selection event
	 */
	void dialogSelected (final SelectionEvent event) {

		/* Enable/Disable the buttons */
		final String name = this.dialogCombo.getText ();
		final boolean isMessageBox = name.equals (ControlExample.getResourceString("MessageBox"));
		final boolean isFileDialog = name.equals (ControlExample.getResourceString("FileDialog"));
		final boolean isFontDialog = name.equals (ControlExample.getResourceString("FontDialog"));
		this.okButton.setEnabled (isMessageBox);
		this.cancelButton.setEnabled (isMessageBox);
		this.yesButton.setEnabled (isMessageBox);
		this.noButton.setEnabled (isMessageBox);
		this.retryButton.setEnabled (isMessageBox);
		this.abortButton.setEnabled (isMessageBox);
		this.ignoreButton.setEnabled (isMessageBox);
		this.iconErrorButton.setEnabled (isMessageBox);
		this.iconInformationButton.setEnabled (isMessageBox);
		this.iconQuestionButton.setEnabled (isMessageBox);
		this.iconWarningButton.setEnabled (isMessageBox);
		this.iconWorkingButton.setEnabled (isMessageBox);
		this.noIconButton.setEnabled (isMessageBox);
		this.saveButton.setEnabled (isFileDialog);
		this.openButton.setEnabled (isFileDialog);
		this.multiButton.setEnabled (isFileDialog);
		this.effectsVisibleButton.setEnabled (isFontDialog);
		this.usePreviousResultButton.setEnabled (!isMessageBox);

		/* Deselect the buttons */
		if (!isMessageBox) {
			this.okButton.setSelection (false);
			this.cancelButton.setSelection (false);
			this.yesButton.setSelection (false);
			this.noButton.setSelection (false);
			this.retryButton.setSelection (false);
			this.abortButton.setSelection (false);
			this.ignoreButton.setSelection (false);
		}
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		return new Widget [0];
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Dialog";
	}

	/**
	 * Recreates the "Example" widgets.
	 */
	@Override
	void recreateExampleWidgets () {
		if (this.textWidget == null) {
			super.recreateExampleWidgets ();
		}
	}
}
