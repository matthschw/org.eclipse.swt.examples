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
package org.eclipse.swt.examples.layoutexample;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class FormLayoutTab extends Tab {
	/* Controls for setting layout parameters */
	Spinner marginWidth, marginHeight, marginLeft, marginRight, marginTop, marginBottom, spacing;
	/* The example layout instance */
	FormLayout formLayout;
	/* TableEditors and related controls*/
	TableEditor nameEditor, comboEditor, widthEditor, heightEditor;
	TableEditor leftEditor, rightEditor, topEditor, bottomEditor;
	CCombo combo;
	Text nameText, widthText, heightText;
	Button leftAttach, rightAttach, topAttach, bottomAttach;

	/* Constants */
	final int NAME_COL = 0;
	final int COMBO_COL = 1;
	final int WIDTH_COL = 2;
	final int HEIGHT_COL = 3;
	final int LEFT_COL = 4;
	final int RIGHT_COL = 5;
	final int TOP_COL = 6;
	final int BOTTOM_COL = 7;

	final int MODIFY_COLS = 4;	// The number of columns with combo or text editors
	final int TOTAL_COLS = 8;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	FormLayoutTab(final LayoutExample instance) {
		super(instance);
	}

	/**
	 * Returns the constant for the alignment for an
	 * attachment given a string.
	 */
	int alignmentConstant (final String align) {
		if (align.equals("LEFT")) {
      return SWT.LEFT;
    }
		if (align.equals("RIGHT")) {
      return SWT.RIGHT;
    }
		if (align.equals("TOP")) {
      return SWT.TOP;
    }
		if (align.equals("BOTTOM")) {
      return SWT.BOTTOM;
    }
		if (align.equals("CENTER")) {
      return SWT.CENTER;
    }
		return SWT.DEFAULT;
	}

	/**
	 * Returns a string representing the alignment for an
	 * attachment given a constant.
	 */
	String alignmentString (final int align) {
		switch (align) {
			case SWT.LEFT: return "LEFT";
			case SWT.RIGHT: return "RIGHT";
			case SWT.TOP: return "TOP";
			case SWT.BOTTOM: return "BOTTOM";
			case SWT.CENTER: return "CENTER";
		}
		return "DEFAULT";
	}

	/**
	 * Update the attachment field in case the type of control
	 * has changed.
	 */
	String checkAttachment (final String oldAttach, final FormAttachment newAttach) {
		final String controlClass = newAttach.control.getClass().toString ();
		final String controlType = controlClass.substring (controlClass.lastIndexOf ('.') + 1);
		int i = 0;
		while ((i < oldAttach.length ()) && !Character.isDigit(oldAttach.charAt (i))) {
			i++;
		}
		final String index = oldAttach.substring (i, oldAttach.indexOf (','));
		return controlType + index + "," + newAttach.offset + ":" + this.alignmentString (newAttach.alignment);
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	@Override
	void createChildWidgets () {
		/* Add common controls */
		super.createChildWidgets ();

		/* Resize the columns */
		this.table.getColumn (this.LEFT_COL).setWidth (90);
		this.table.getColumn (this.RIGHT_COL).setWidth (90);
		this.table.getColumn (this.TOP_COL).setWidth (90);
		this.table.getColumn (this.BOTTOM_COL).setWidth (90);

		/* Add TableEditors */
		this.nameEditor = new TableEditor (this.table);
		this.comboEditor = new TableEditor (this.table);
		this.widthEditor = new TableEditor (this.table);
		this.heightEditor = new TableEditor (this.table);
		this.leftEditor = new TableEditor (this.table);
		this.rightEditor = new TableEditor (this.table);
		this.topEditor = new TableEditor (this.table);
		this.bottomEditor = new TableEditor (this.table);
		this.table.addMouseListener(MouseListener.mouseDownAdapter(e -> {
			this.resetEditors();
			this.index = this.table.getSelectionIndex();
			final Point pt = new Point(e.x, e.y);
			this.newItem = this.table.getItem(pt);
			if (this.newItem == null) {
        return;
      }
			final TableItem oldItem = this.comboEditor.getItem();
			if ((this.newItem == oldItem) || (this.newItem != this.lastSelected)) {
				this.lastSelected = this.newItem;
				return;
			}
			this.table.showSelection();

			this.combo = new CCombo(this.table, SWT.READ_ONLY);
			this.createComboEditor(this.combo, this.comboEditor);

			this.nameText = new Text(this.table, SWT.SINGLE);
			this.nameText.setText(this.data.get(this.index)[this.NAME_COL]);
			this.createTextEditor(this.nameText, this.nameEditor, this.NAME_COL);

			this.widthText = new Text(this.table, SWT.SINGLE);
			this.widthText.setText(this.data.get(this.index)[this.WIDTH_COL]);
			this.createTextEditor(this.widthText, this.widthEditor, this.WIDTH_COL);

			this.heightText = new Text(this.table, SWT.SINGLE);
			this.heightText.setText(this.data.get(this.index)[this.HEIGHT_COL]);
			this.createTextEditor(this.heightText, this.heightEditor, this.HEIGHT_COL);

			this.leftAttach = new Button(this.table, SWT.PUSH);
			this.leftAttach.setText(LayoutExample.getResourceString("Attach_Edit"));
			this.leftEditor.horizontalAlignment = SWT.LEFT;
			this.leftEditor.grabHorizontal = true;
			this.leftEditor.minimumWidth = this.leftAttach.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			this.leftEditor.setEditor(this.leftAttach, this.newItem, this.LEFT_COL);
			this.leftAttach.addSelectionListener(SelectionListener.widgetSelectedAdapter(e1 -> {
				final Shell shell = this.tabFolderPage.getShell();
				final AttachDialog dialog = new AttachDialog(shell);
				dialog.setText(LayoutExample.getResourceString("Left_Attachment"));
				dialog.setColumn(this.LEFT_COL);
				final String attach = dialog.open();
				this.newItem.setText(this.LEFT_COL, attach);
				this.resetEditors();
			}));

			this.rightAttach = new Button(this.table, SWT.PUSH);
			this.rightAttach.setText(LayoutExample.getResourceString("Attach_Edit"));
			this.rightEditor.horizontalAlignment = SWT.LEFT;
			this.rightEditor.grabHorizontal = true;
			this.rightEditor.minimumWidth = this.rightAttach.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			this.rightEditor.setEditor(this.rightAttach, this.newItem, this.RIGHT_COL);
			this.rightAttach.addSelectionListener(SelectionListener.widgetSelectedAdapter(e1 -> {
				final Shell shell = this.tabFolderPage.getShell();
				final AttachDialog dialog = new AttachDialog(shell);
				dialog.setText(LayoutExample.getResourceString("Right_Attachment"));
				dialog.setColumn(this.RIGHT_COL);
				final String attach = dialog.open();
				this.newItem.setText(this.RIGHT_COL, attach);
				if (this.newItem.getText(this.LEFT_COL).endsWith(")")) {
          this.newItem.setText(this.LEFT_COL, "");
        }
				this.resetEditors();
			}));

			this.topAttach = new Button(this.table, SWT.PUSH);
			this.topAttach.setText(LayoutExample.getResourceString("Attach_Edit"));
			this.topEditor.horizontalAlignment = SWT.LEFT;
			this.topEditor.grabHorizontal = true;
			this.topEditor.minimumWidth = this.topAttach.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			this.topEditor.setEditor(this.topAttach, this.newItem, this.TOP_COL);
			this.topAttach.addSelectionListener(SelectionListener.widgetSelectedAdapter(e1 -> {
				final Shell shell = this.tabFolderPage.getShell();
				final AttachDialog dialog = new AttachDialog(shell);
				dialog.setText(LayoutExample.getResourceString("Top_Attachment"));
				dialog.setColumn(this.TOP_COL);
				final String attach = dialog.open();
				this.newItem.setText(this.TOP_COL, attach);
				this.resetEditors();
			}));
			this.bottomAttach = new Button(this.table, SWT.PUSH);
			this.bottomAttach.setText(LayoutExample.getResourceString("Attach_Edit"));
			this.bottomEditor.horizontalAlignment = SWT.LEFT;
			this.bottomEditor.grabHorizontal = true;
			this.bottomEditor.minimumWidth = this.bottomAttach.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			this.bottomEditor.setEditor(this.bottomAttach, this.newItem, this.BOTTOM_COL);
			this.bottomAttach.addSelectionListener(SelectionListener.widgetSelectedAdapter(e1 -> {
				final Shell shell = this.tabFolderPage.getShell();
				final AttachDialog dialog = new AttachDialog(shell);
				dialog.setText(LayoutExample.getResourceString("Bottom_Attachment"));
				dialog.setColumn(this.BOTTOM_COL);
				final String attach = dialog.open();
				this.newItem.setText(this.BOTTOM_COL, attach);
				if (this.newItem.getText(this.TOP_COL).endsWith(")")) {
          this.newItem.setText(this.TOP_COL, "");
        }
				this.resetEditors();
			}));

			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final Rectangle rect = this.newItem.getBounds(i);
				if (rect.contains(pt)) {
					switch (i) {
					case 0:
						this.resetEditors();
						break;
					case COMBO_COL:
						this.combo.setFocus();
						break;
					case WIDTH_COL:
						this.widthText.setFocus();
						break;
					case HEIGHT_COL:
						this.heightText.setFocus();
						break;
					default:
						break;
					}
				}
			}
		}));
	}

	/**
	 * Creates the control widgets.
	 */
	@Override
	void createControlWidgets () {
		/* Controls the margins and spacing of the FormLayout */
		final Group marginGroup = new Group (this.controlGroup, SWT.NONE);
		marginGroup.setText (LayoutExample.getResourceString ("Margins_Spacing"));
		marginGroup.setLayout(new GridLayout (2, false));
		marginGroup.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, false, false));
		new Label (marginGroup, SWT.NONE).setText ("marginWidth");
		this.marginWidth = new Spinner(marginGroup, SWT.BORDER);
		this.marginWidth.setSelection(0);
		this.marginWidth.addSelectionListener(this.selectionListener);
		new Label (marginGroup, SWT.NONE).setText("marginHeight");
		this.marginHeight = new Spinner(marginGroup, SWT.BORDER);
		this.marginHeight.setSelection(0);
		this.marginHeight.addSelectionListener (this.selectionListener);
		new Label (marginGroup, SWT.NONE).setText("marginLeft");
		this.marginLeft = new Spinner(marginGroup, SWT.BORDER);
		this.marginLeft.setSelection(0);
		this.marginLeft.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginRight");
		this.marginRight = new Spinner(marginGroup, SWT.BORDER);
		this.marginRight.setSelection(0);
		this.marginRight.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginTop");
		this.marginTop = new Spinner(marginGroup, SWT.BORDER);
		this.marginTop.setSelection(0);
		this.marginTop.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText("marginBottom");
		this.marginBottom = new Spinner(marginGroup, SWT.BORDER);
		this.marginBottom.setSelection(0);
		this.marginBottom.addSelectionListener(this.selectionListener);
		new Label(marginGroup, SWT.NONE).setText ("spacing");
		this.spacing = new Spinner(marginGroup, SWT.BORDER);
		this.spacing.setSelection(0);
		this.spacing.addSelectionListener(this.selectionListener);

		/* Add common controls */
		super.createControlWidgets ();
	}

	/**
	 * Creates the example layout.
	 */
	@Override
	void createLayout () {
		this.formLayout = new FormLayout ();
		this.layoutComposite.setLayout (this.formLayout);
	}

	/**
	 * Disposes the editors without placing their contents
	 * into the table.
	 */
	@Override
	void disposeEditors () {
		this.comboEditor.setEditor (null, null, -1);
		this.combo.dispose ();
		this.nameText.dispose ();
		this.widthText.dispose ();
		this.heightText.dispose ();
		this.leftAttach.dispose ();
		this.rightAttach.dispose ();
		this.topAttach.dispose ();
		this.bottomAttach.dispose ();
	}

	/**
	 * Generates code for the example layout.
	 */
	@Override
	StringBuilder generateLayoutCode () {
		final StringBuilder code = new StringBuilder ();
		code.append ("\t\tFormLayout formLayout = new FormLayout ();\n");
		if (this.formLayout.marginWidth != 0) {
			code.append ("\t\tformLayout.marginWidth = " + this.formLayout.marginWidth + ";\n");
		}
		if (this.formLayout.marginHeight != 0) {
			code.append ("\t\tformLayout.marginHeight = " + this.formLayout.marginHeight + ";\n");
		}
		if(this.formLayout.marginLeft != 0) {
			code.append ("\t\tformLayout.marginLeft = " + this.formLayout.marginLeft + ";\n");
		}
		if(this.formLayout.marginRight != 0) {
			code.append ("\t\tformLayout.marginRight = " + this.formLayout.marginRight + ";\n");
		}
		if(this.formLayout.marginTop != 0) {
			code.append ("\t\tformLayout.marginTop = " + this.formLayout.marginTop + ";\n");
		}
		if(this.formLayout.marginBottom != 0) {
			code.append ("\t\tformLayout.marginBottom = " + this.formLayout.marginBottom + ";\n");
		}
		if (this.formLayout.spacing != 0) {
			code.append ("\t\tformLayout.spacing = " + this.formLayout.spacing + ";\n");
		}
		code.append ("\t\tshell.setLayout (formLayout);\n");

		boolean first = true;
		for (int i = 0; i < this.children.length; i++) {
			final Control control = this.children [i];
			code.append (this.getChildCode (control, i));
			final FormData data = (FormData) control.getLayoutData ();
			if (data != null) {
				code.append ("\t\t");
				if (first) {
					code.append ("FormData ");
					first = false;
				}
				code.append ("data = new FormData ();\n");
				if (data.width != SWT.DEFAULT) {
					code.append ("\t\tdata.width = " + data.width + ";\n");
				}
				if (data.height != SWT.DEFAULT) {
					code.append ("\t\tdata.height = " + data.height + ";\n");
				}
				if (data.left != null) {
					if (data.left.control != null) {
						final TableItem item = this.table.getItem (i);
						final String controlString = item.getText (this.LEFT_COL);
						final int index = Integer.parseInt(controlString.substring (controlString.indexOf (',') - 1, controlString.indexOf (',')));
						code.append ("\t\tdata.left = new FormAttachment (" + this.names [index] + ", " + data.left.offset + ", SWT." + this.alignmentString (data.left.alignment) + ");\n");
					} else if ((data.right != null) || ((data.left.numerator != 0) ||(data.left.offset != 0))) {
          	code.append ("\t\tdata.left = new FormAttachment (" + data.left.numerator + ", " + data.left.offset + ");\n");
          }
				}
				if (data.right != null) {
					if (data.right.control != null) {
						final TableItem item = this.table.getItem (i);
						final String controlString = item.getText (this.RIGHT_COL);
						final int index = Integer.parseInt (controlString.substring (controlString.indexOf (',') - 1, controlString.indexOf (',')));
						code.append ("\t\tdata.right = new FormAttachment (" + this.names [index] + ", " + data.right.offset + ", SWT." + this.alignmentString (data.right.alignment) + ");\n");
					} else {
						code.append ("\t\tdata.right = new FormAttachment (" + data.right.numerator + ", " + data.right.offset + ");\n");
					}
				}
				if (data.top != null) {
					if (data.top.control != null) {
						final TableItem item = this.table.getItem (i);
						final String controlString = item.getText (this.TOP_COL);
						final int index = Integer.parseInt(controlString.substring (controlString.indexOf (',') - 1, controlString.indexOf (',')));
						code.append ("\t\tdata.top = new FormAttachment (" + this.names [index] + ", " + data.top.offset + ", SWT." + this.alignmentString (data.top.alignment) + ");\n");
					} else if ((data.bottom != null) || ((data.top.numerator != 0) ||(data.top.offset != 0))) {
          	code.append ("\t\tdata.top = new FormAttachment (" + data.top.numerator + ", " + data.top.offset + ");\n");
          }
				}
				if (data.bottom != null) {
					if (data.bottom.control != null) {
						final TableItem item = this.table.getItem (i);
						final String controlString = item.getText (this.BOTTOM_COL);
						final int index = Integer.parseInt(controlString.substring (controlString.indexOf (',') - 1, controlString.indexOf (',')));
						code.append ("\t\tdata.bottom = new FormAttachment (" + this.names [index] + ", " + data.bottom.offset + ", SWT." + this.alignmentString (data.bottom.alignment) + ");\n");
					} else {
						code.append ("\t\tdata.bottom = new FormAttachment (" + data.bottom.numerator + ", " + data.bottom.offset + ");\n");
					}
				}
				code.append ("\t\t" + this.names [i] + ".setLayoutData (data);\n");
			}
		}
		return code;
	}

	/**
	 * Returns the string to insert when a new child control is added to the table.
	 */
	@Override
	String[] getInsertString (final String name, final String controlType) {
		return new String [] {name, controlType, "-1", "-1",
				"0,0 (" + LayoutExample.getResourceString ("Default") + ")", "",
				"0,0 (" + LayoutExample.getResourceString ("Default") + ")", ""};
	}

	/**
	 * Returns the layout data field names.
	 */
	@Override
	String [] getLayoutDataFieldNames() {
		return new String [] {
			"Control Name",
			"Control Type",
			"width",
			"height",
			"left",
			"right",
			"top",
			"bottom"
		};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "FormLayout";
	}

	/**
	 * Takes information from TableEditors and stores it.
	 */
	@Override
	void resetEditors (final boolean tab) {
		final TableItem oldItem = this.comboEditor.getItem ();
		if (oldItem != null) {
			final int row = this.table.indexOf (oldItem);
			try {
				new String (this.nameText.getText ());
			} catch (final NumberFormatException e) {
				this.nameText.setText (oldItem.getText (this.NAME_COL));
			}
			try {
				Integer.parseInt(this.widthText.getText ());
			} catch (final NumberFormatException e) {
				this.widthText.setText (oldItem.getText (this.WIDTH_COL));
			}
			try {
				Integer.parseInt(this.heightText.getText());
			} catch(final NumberFormatException e) {
				this.heightText.setText (oldItem.getText(this.HEIGHT_COL));
			}
			final String[] insert = new String [] {this.nameText.getText(), this.combo.getText (), this.widthText.getText (), this.heightText.getText ()};
			this.data.set (row, insert);
			for (int i = 0 ; i < this.MODIFY_COLS; i++) {
				oldItem.setText (i, this.data.get (row) [i]);
			}
			if (!tab) {
        this.disposeEditors ();
      }
		}
		this.setLayoutState ();
		this.refreshLayoutComposite ();
		this.setLayoutData ();
		this.layoutComposite.layout (true);
		this.layoutGroup.layout (true);
	}

	/**
	 * Return the initial weight of the layout and control groups within the SashForm.
	 * @return the desired sash weights for the tab page
	 */
	@Override
	int[] sashWeights () {
		return new int[] {40, 60};
	}

	/**
	 * Sets an attachment to the edge of a widget using the
	 * information in the table.
	 */
	FormAttachment setAttachment (final String attachment) {
		String control, align;
		int position, offset;
		final int comma = attachment.indexOf (',');
		final char first = attachment.charAt (0);
		if (Character.isLetter(first)) {
			/* Case where there is a control */
			control = attachment.substring (0, comma);
			int i = 0;
			while ((i < control.length ()) && !Character.isDigit (control.charAt (i))) {
				i++;
			}
			final String end = control.substring (i);
			final int index = Integer.parseInt(end);
			final Control attachControl = this.children [index];
			final int colon = attachment.indexOf (':');
			try {
				offset = Integer.parseInt(attachment.substring (comma + 1, colon));
			} catch (final NumberFormatException e) {
				offset = 0;
			}
			align = attachment.substring (colon + 1);
			return new FormAttachment (attachControl, offset, this.alignmentConstant (align));
		} else {
			/* Case where there is a position */
			try {
				position = Integer.parseInt(attachment.substring (0,comma));
			} catch (final NumberFormatException e) {
				position = 0;
			}
			try {
				offset = Integer.parseInt(attachment.substring (comma + 1));
			} catch (final NumberFormatException e) {
				offset = 0;
			}
			return new FormAttachment (position, offset);
		}
	}

	/**
	 * Sets the layout data for the children of the layout.
	 */
	@Override
	void setLayoutData () {
		final Control [] children = this.layoutComposite.getChildren ();
		final TableItem [] items = this.table.getItems ();
		FormData data;
		int width, height;
		String left, right, top, bottom;
		for (int i = 0; i < children.length; i++) {
			width = Integer.parseInt(items [i].getText (this.WIDTH_COL));
			height = Integer.parseInt(items [i].getText (this.HEIGHT_COL));
			data = new FormData ();
			if (width > 0) {
        data.width = width;
      }
			if (height > 0) {
        data.height = height;
      }

			left = items [i].getText (this.LEFT_COL);
			if (left.length () > 0) {
				data.left = this.setAttachment (left);
				if (data.left.control != null) {
					final String attachment = this.checkAttachment (left, data.left);
					items [i].setText (this.LEFT_COL, attachment);
				}
			}
			right = items [i].getText (this.RIGHT_COL);
			if (right.length () > 0) {
				data.right = this.setAttachment (right);
				if (data.right.control != null) {
					final String attachment = this.checkAttachment (right, data.right);
					items [i].setText (this.RIGHT_COL, attachment);
				}
			}
			top = items [i].getText (this.TOP_COL);
			if (top.length () > 0 ) {
				data.top = this.setAttachment (top);
				if (data.top.control != null) {
					final String attachment = this.checkAttachment (top, data.top);
					items [i].setText (this.TOP_COL, attachment);
				}
			}
			bottom = items [i].getText (this.BOTTOM_COL);
			if (bottom.length () > 0) {
				data.bottom = this.setAttachment (bottom);
				if (data.bottom.control != null) {
					final String attachment = this.checkAttachment (bottom, data.bottom);
					items [i].setText (this.BOTTOM_COL, attachment);
				}
			}
			children [i].setLayoutData (data);
		}
	}

	/**
	 * Sets the state of the layout.
	 */
	@Override
	void setLayoutState () {
		/* Set the margins and spacing */
		this.formLayout.marginWidth = this.marginWidth.getSelection ();
		this.formLayout.marginHeight = this.marginHeight.getSelection ();
		this.formLayout.marginLeft = this.marginLeft.getSelection ();
		this.formLayout.marginRight = this.marginRight.getSelection ();
		this.formLayout.marginTop = this.marginTop.getSelection ();
		this.formLayout.marginBottom = this.marginBottom.getSelection ();
		this.formLayout.spacing = this.spacing.getSelection ();
	}

	/**
	 * <code>AttachDialog</code> is the class that creates a
	 * dialog specific for this example. It creates a dialog
	 * with controls to set the values in a FormAttachment.
	 */
	public class AttachDialog extends Dialog {
		String result = "";
		String controlInput, positionInput, alignmentInput, offsetInput;
		int col = 0;

		public AttachDialog (final Shell parent, final int style) {
			super (parent, style);
		}

		public AttachDialog (final Shell parent) {
			this (parent, 0);
		}

		public void setColumn (final int col) {
			this.col = col;
		}

		public String open () {
			final Shell parent = this.getParent ();
			final Shell shell = new Shell (parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			shell.setText (this.getText ());
			shell.setLayout (new GridLayout (3, true));

			/* Find out what was previously set as an attachment */
			final TableItem newItem = FormLayoutTab.this.leftEditor.getItem ();
			this.result = newItem.getText (this.col);
			final String oldAttach = this.result;
			String oldPos = "0", oldControl = "", oldAlign = "DEFAULT", oldOffset = "0";
			boolean isControl = false;
			if (oldAttach.length () != 0) {
				final char first = oldAttach.charAt (0);
				if (Character.isLetter(first)) {
					/* We have a control */
					isControl = true;
					oldControl = oldAttach.substring (0, oldAttach.indexOf (','));
					oldAlign = oldAttach.substring (oldAttach.indexOf (':') + 1);
					oldOffset = oldAttach.substring (oldAttach.indexOf (',') + 1, oldAttach.indexOf (':'));
				} else {
					/* We have a position */
					oldPos = oldAttach.substring (0, oldAttach.indexOf (','));
					oldOffset = oldAttach.substring (oldAttach.indexOf (',') + 1);
					if (oldOffset.endsWith (")")) { // i.e. (Default)
						oldOffset = oldOffset.substring (0, oldOffset.indexOf (' '));
					}
				}
			}

			/* Add position field */
			final Button posButton = new Button (shell, SWT.RADIO);
			posButton.setText (LayoutExample.getResourceString ("Position"));
			posButton.setSelection (!isControl);
			final Combo position = new Combo (shell, SWT.NONE);
			position.setItems ("0","25","33","50","67","75","100");
			position.setVisibleItemCount (7);
			position.setText (oldPos);
			position.setEnabled (!isControl);
			position.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false, 2, 1));

			/* Add control field */
			final Button contButton = new Button (shell, SWT.RADIO);
			contButton.setText (LayoutExample.getResourceString ("Control"));
			contButton.setSelection (isControl);
			final Combo control = new Combo (shell, SWT.READ_ONLY);
			final TableItem [] items = FormLayoutTab.this.table.getItems ();
			final TableItem currentItem = FormLayoutTab.this.leftEditor.getItem ();
			for (int i = 0; i < FormLayoutTab.this.table.getItemCount (); i++) {
				if (items [i].getText (0).length() > 0) {
					if (items [i] != currentItem) {
						control.add (items [i].getText (FormLayoutTab.this.COMBO_COL) + i);
					}
				}
			}
			if (oldControl.length () != 0) {
        control.setText (oldControl);
      } else {
        control.select (0);
      }
			control.setEnabled (isControl);
			control.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false, 2, 1));

			/* Add alignment field */
			new Label (shell, SWT.NONE).setText (LayoutExample.getResourceString ("Alignment"));
			final Combo alignment = new Combo (shell, SWT.NONE);
			String[] alignmentValues;
			if ((this.col == FormLayoutTab.this.LEFT_COL) || (this.col == FormLayoutTab.this.RIGHT_COL)) {
				alignmentValues = new String [] {"SWT.LEFT", "SWT.RIGHT", "SWT.CENTER", "SWT.DEFAULT"};
			} else {
				// col == TOP_COL || col == BOTTOM_COL
				alignmentValues = new String [] {"SWT.TOP", "SWT.BOTTOM", "SWT.CENTER", "SWT.DEFAULT"};
			}
			alignment.setItems (alignmentValues);
			alignment.setText ("SWT." + oldAlign);
			alignment.setEnabled (isControl);
			alignment.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false, 2, 1));

			/* Add offset field */
			new Label (shell, SWT.NONE).setText (LayoutExample.getResourceString ("Offset"));
			final Text offset = new Text (shell, SWT.SINGLE | SWT.BORDER);
			offset.setText (oldOffset);
			offset.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false, 2, 1));

			/* Add listeners for choosing between position and control */
			posButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
				position.setEnabled(true);
				control.setEnabled(false);
				alignment.setEnabled(false);
			}));
			contButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
				position.setEnabled(false);
				control.setEnabled(true);
				alignment.setEnabled(true);
			}));

			final Button clear = new Button (shell, SWT.PUSH);
			clear.setText (LayoutExample.getResourceString ("Clear"));
			clear.setLayoutData (new GridData (SWT.END, SWT.CENTER, false, false));
			clear.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
				this.result = "";
				shell.close();
			}));
			/* OK button sets data into table */
			final Button ok = new Button (shell, SWT.PUSH);
			ok.setText (LayoutExample.getResourceString ("OK"));
			ok.setLayoutData (new GridData (SWT.CENTER, SWT.CENTER, false, false));
			ok.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
				this.controlInput = control.getText();
				this.alignmentInput = alignment.getText().substring(4);
				this.positionInput = position.getText();
				if (this.positionInput.length() == 0) {
          this.positionInput = "0";
        }
				try {
					Integer.parseInt(this.positionInput);
				} catch (final NumberFormatException except) {
					this.positionInput = "0";
				}
				this.offsetInput = offset.getText();
				if (this.offsetInput.length() == 0) {
          this.offsetInput = "0";
        }
				try {
					Integer.parseInt(this.offsetInput);
				} catch (final NumberFormatException except) {
					this.offsetInput = "0";
				}
				if (posButton.getSelection() || (this.controlInput.length() == 0)) {
					this.result = this.positionInput + "," + this.offsetInput;
				} else {
					this.result = this.controlInput + "," + this.offsetInput + ":" + this.alignmentInput;
				}
				shell.close();
			}));
			final Button cancel = new Button (shell, SWT.PUSH);
			cancel.setText (LayoutExample.getResourceString ("Cancel"));
			cancel.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> shell.close()));

			shell.setDefaultButton (ok);
			shell.pack ();
			/* Center the dialog */
			final Point center = parent.getLocation ();
			center.x = (center.x + (parent.getBounds ().width / 2)) - (shell.getBounds ().width / 2);
			center.y = (center.y + (parent.getBounds ().height / 2)) - (shell.getBounds ().height / 2);
			shell.setLocation (center);
			shell.open ();
			while (!shell.isDisposed ()) {
				if (FormLayoutTab.this.display.readAndDispatch ()) {
          FormLayoutTab.this.display.sleep ();
        }
			}

			return this.result;
		}
	}
}
