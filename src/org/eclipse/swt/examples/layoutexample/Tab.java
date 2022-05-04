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


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * <code>Tab</code> is the abstract superclass of every page
 * in the example's tab folder.  Each page in the tab folder
 * displays a layout, and allows the user to manipulate the
 * layout.
 *
 * A typical page in a Tab contains a two column composite.
 * The left column contains the layout group, which contains
 * the "layout composite" (the one that has the example layout).
 * The right column contains the "control" group. The "control"
 * group allows the user to interact with the example. Typical
 * operations are modifying layout parameters, adding children
 * to the "layout composite", and modifying child layout data.
 * The "Code" button in the "control" group opens a new window
 * containing code that will regenerate the layout. This code
 * (or parts of it) can be selected and copied to the clipboard.
 */
abstract class Tab {
	Shell shell;
	Display display;
	/* Common groups and composites */
	Composite tabFolderPage;
	SashForm sash;
	Group layoutGroup, controlGroup, childGroup;
	/* The composite that contains the example layout */
	Composite layoutComposite;
	/* Common controls for modifying the example layout */
	String [] names;
	Control [] children;
	ToolItem add, delete, clear, code;
	int prevSelected = 0;
	/* Common values for working with TableEditors */
	Table table;
	int index;
	boolean comboReset = false;
	final String[] OPTIONS = {"Button", "Canvas", "Combo", "Composite",	"CoolBar",
			"Group", "Label", "Link", "List", "ProgressBar", "Scale", "Slider", "StyledText",
			"Table", "Text", "ToolBar", "Tree"};
	TableItem newItem, lastSelected;
	List<String[]> data = new ArrayList<> ();
	/* Controlling instance */
	final LayoutExample instance;

	/* Listeners */
	SelectionListener selectionListener = widgetSelectedAdapter(e -> this.resetEditors ());

	TraverseListener traverseListener = e -> {
		if (e.detail == SWT.TRAVERSE_RETURN) {
			e.doit = false;
			this.resetEditors ();
		}
	};

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	Tab(final LayoutExample instance) {
		this.instance = instance;
	}

	/**
	 * Creates the "children" group. This is the group that allows
	 * you to add children to the layout. It exists within the
	 * controlGroup.
	 */
	void createChildGroup () {
		this.childGroup = new Group (this.controlGroup, SWT.NONE);
		this.childGroup.setText (LayoutExample.getResourceString("Children"));
		this.childGroup.setLayout(new GridLayout ());
		this.childGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true, 2, 1));

		final ToolBar toolBar = new ToolBar(this.childGroup, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		this.add = new ToolItem(toolBar, SWT.DROP_DOWN);
		this.add.setText(LayoutExample.getResourceString("Add"));
		this.add.addSelectionListener (widgetSelectedAdapter(event -> {
			if (event.detail == SWT.ARROW) {
				final ToolItem item = (ToolItem)event.widget;
				final ToolBar bar = item.getParent ();
				final Menu menu = new Menu (this.shell, SWT.POP_UP);
				for (int i = 0; i < this.OPTIONS.length; i++) {
					final MenuItem newItem = new MenuItem (menu, SWT.RADIO);
					newItem.setText (this.OPTIONS [i]);
					newItem.addSelectionListener (widgetSelectedAdapter(e -> {
						final MenuItem menuItem = (MenuItem)e.widget;
						if (menuItem.getSelection ()) {
							final Menu menuParent  = menuItem.getParent ();
							this.prevSelected = menuParent.indexOf (menuItem);
							final String controlType = menuItem.getText ();
							final String name = controlType.toLowerCase () + String.valueOf (this.table.getItemCount ());
							final String [] insert = this.getInsertString (name, controlType);
							if (insert != null) {
								final TableItem tableItem = new TableItem (this.table, SWT.NONE);
								tableItem.setText (insert);
								this.data.add (insert);
							}
							this.resetEditors ();
						}
					}));
					newItem.setSelection (i == this.prevSelected);
				}
				final Point pt = this.display.map (bar, null, event.x, event.y);
				menu.setLocation (pt.x, pt.y);
				menu.setVisible (true);

				while ((menu != null) && !menu.isDisposed () && menu.isVisible ()) {
					if (!this.display.readAndDispatch ()) {
						this.display.sleep ();
					}
				}
				menu.dispose ();
			} else {
				final String controlType = this.OPTIONS [this.prevSelected];
				final String name = controlType.toLowerCase () + String.valueOf (this.table.getItemCount ());
				final String [] insert = this.getInsertString (name, controlType);
				if (insert != null) {
					final TableItem item = new TableItem (this.table, 0);
					item.setText (insert);
					this.data.add (insert);
				}
				this.resetEditors ();
			}
		}));

		new ToolItem(toolBar,SWT.SEPARATOR);

		this.delete = new ToolItem(toolBar, SWT.PUSH);
		this.delete.setText (LayoutExample.getResourceString ("Delete"));
		this.delete.addSelectionListener (widgetSelectedAdapter(e -> {
			this.resetEditors ();
			final int [] selected = this.table.getSelectionIndices ();
			this.table.remove (selected);
			/* Refresh the control indices of the table */
			for (int i = 0; i < this.table.getItemCount(); i++) {
				final TableItem item = this.table.getItem (i);
				item.setText (0, item.getText (0));
			}
			this.refreshLayoutComposite ();
			this.layoutComposite.layout (true);
			this.layoutGroup.layout (true);
		}));

		new ToolItem(toolBar,SWT.SEPARATOR);
		this.clear = new ToolItem(toolBar, SWT.PUSH);
		this.clear.setText (LayoutExample.getResourceString ("Clear"));
		this.clear.addSelectionListener (widgetSelectedAdapter(e -> {
			this.resetEditors ();
			this.children = this.layoutComposite.getChildren ();
			for (final Control child : this.children) {
				child.dispose ();
			}
			this.table.removeAll ();
			this.data.clear ();
			this.children = new Control [0];
			this.layoutGroup.layout (true);
		}));
		toolBar.pack();

		new ToolItem (toolBar,SWT.SEPARATOR);
		this.code = new ToolItem (toolBar, SWT.PUSH);
		this.code.setText (LayoutExample.getResourceString ("Generate_Code"));
		this.code.addSelectionListener(widgetSelectedAdapter(e -> {
			final Shell shell = new Shell();
			shell.setText(LayoutExample.getResourceString("Generated_Code"));
			shell.setLayout(new FillLayout());
			final Text text = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			final String layoutCode = this.generateCode().toString ();
			if (layoutCode.length() == 0) {
        return;
      }
			text.setText(layoutCode);

			final Menu bar = new Menu(shell, SWT.BAR);
			shell.setMenuBar(bar);
			final MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
			editItem.setText(LayoutExample.getResourceString("Edit"));
			final Menu menu = new Menu(bar);
			final MenuItem select = new MenuItem(menu, SWT.PUSH);
			select.setText(LayoutExample.getResourceString("Select_All"));
			select.setAccelerator(SWT.MOD1 + 'A');
			select.addSelectionListener(widgetSelectedAdapter(event -> text.selectAll()));
			final MenuItem copy = new MenuItem(menu, SWT.PUSH);
			copy.setText(LayoutExample.getResourceString("Copy"));
			copy.setAccelerator(SWT.MOD1 + 'C');
			copy.addSelectionListener(widgetSelectedAdapter(event -> text.copy()));
			final MenuItem exit = new MenuItem(menu, SWT.PUSH);
			exit.setText(LayoutExample.getResourceString("Exit"));
			exit.addSelectionListener(widgetSelectedAdapter(event -> shell.close()));
			editItem.setMenu(menu);

			shell.pack();
			shell.setSize(500, 600);
			shell.open();
			while(!shell.isDisposed()) {
        if (!this.display.readAndDispatch()) {
          this.display.sleep();
        }
      }
		}));

		this.createChildWidgets();
	}

	/**
	 * Creates the controls for modifying the "children"
	 * table, and the table itself.
	 * Subclasses override this method to augment the
	 * standard table.
	 */
	void createChildWidgets() {
		/* Create the "children" table */
		this.table = new Table (this.childGroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		this.table.setLinesVisible (true);
		this.table.setHeaderVisible (true);
		final FontData def[] = this.display.getSystemFont().getFontData();
		this.table.setFont(new Font(this.display, def[0].getName(), 10, SWT.NONE));
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gridData.heightHint = 150;
		this.table.setLayoutData (gridData);
		this.table.addTraverseListener (this.traverseListener);

		/* Add columns to the table */
		final String [] columnHeaders = this.getLayoutDataFieldNames ();
		for (int i = 0; i < columnHeaders.length; i++) {
			final TableColumn column = new TableColumn(this.table, SWT.NONE);
			column.setText (columnHeaders [i]);
			if (i == 0) {
        column.setWidth (100);
      } else if (i == 1) {
        column.setWidth (90);
      } else {
        column.pack ();
      }
		}
	}

	/**
	 * Creates the TableEditor with a CCombo in the first column
	 * of the table. This CCombo lists all the controls that
	 * the user can select to place on their layout.
	 */
	void createComboEditor (final CCombo combo, final TableEditor comboEditor) {
		combo.setItems (this.OPTIONS);
		combo.setText (this.newItem.getText (1));

		/* Set up editor */
		comboEditor.horizontalAlignment = SWT.LEFT;
		comboEditor.grabHorizontal = true;
		comboEditor.minimumWidth = 50;
		comboEditor.setEditor (combo, this.newItem, 1);

		/* Add listener */
		combo.addTraverseListener(e -> {
			if ((e.detail == SWT.TRAVERSE_TAB_NEXT) || (e.detail == SWT.TRAVERSE_RETURN)) {
				this.comboReset = true;
				this.resetEditors ();
			}
			if (e.detail == SWT.TRAVERSE_ESCAPE) {
				this.disposeEditors ();
			}
		});
	}

	/**
	 * Creates the "control" group. This is the group on the
	 * right half of each example tab. It contains controls
	 * for adding new children to the layoutComposite, and
	 * for modifying the children's layout data.
	 */
	void createControlGroup () {
		this.controlGroup = new Group (this.sash, SWT.NONE);
		this.controlGroup.setText (LayoutExample.getResourceString("Parameters"));
		final GridLayout layout = new GridLayout (2, true);
		layout.horizontalSpacing = 10;
		this.controlGroup.setLayout (layout);
		final Button preferredButton = new Button (this.controlGroup, SWT.CHECK);
		preferredButton.setText (LayoutExample.getResourceString ("Preferred_Size"));
		preferredButton.setSelection (false);
		preferredButton.addSelectionListener (widgetSelectedAdapter(e -> {
			this.resetEditors ();
			final GridData data = (GridData)this.layoutComposite.getLayoutData();
			if (preferredButton.getSelection ()) {
				data.heightHint = data.widthHint = SWT.DEFAULT;
				data.verticalAlignment = data.horizontalAlignment = 0;
				data.grabExcessVerticalSpace = data.grabExcessHorizontalSpace = false;
			} else {
				data.verticalAlignment = data.horizontalAlignment = SWT.FILL;
				data.grabExcessVerticalSpace = data.grabExcessHorizontalSpace = true;
			}
			this.layoutComposite.setLayoutData (data);
			this.layoutGroup.layout (true);
		}));
		preferredButton.setLayoutData (new GridData (SWT.FILL, SWT.CENTER, true, false, 2, 1));
		this.createControlWidgets ();
	}

	/**
	 * Creates the "control" widget children.
	 * Subclasses override this method to augment
	 * the standard controls created.
	 */
	void createControlWidgets () {
		this.createChildGroup ();
	}

	/**
	 * Creates the example layout.
	 * Subclasses override this method.
	 */
	void createLayout () {
	}

	/**
	 * Creates the composite that contains the example layout.
	 */
	void createLayoutComposite () {
		this.layoutComposite = new Composite (this.layoutGroup, SWT.BORDER);
		this.layoutComposite.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		this.createLayout ();
	}

	/**
	 * Creates the layout group. This is the group on the
	 * left half of each example tab.
	 */
	void createLayoutGroup () {
		this.layoutGroup = new Group (this.sash, SWT.NONE);
		this.layoutGroup.setText (LayoutExample.getResourceString("Layout"));
		this.layoutGroup.setLayout (new GridLayout ());
		this.createLayoutComposite ();
	}

	/**
	 * Creates the tab folder page.
	 *
	 * @param tabFolder org.eclipse.swt.widgets.TabFolder
	 * @return the new page for the tab folder
	 */
	Composite createTabFolderPage (final TabFolder tabFolder) {
		/* Cache the shell and display. */
		this.shell = tabFolder.getShell ();
		this.display = this.shell.getDisplay ();

		/* Create a two column page with a SashForm*/
		this.tabFolderPage = new Composite (tabFolder, SWT.NONE);
		this.tabFolderPage.setLayoutData (new GridData(SWT.FILL, SWT.FILL, true, true));
		this.tabFolderPage.setLayout (new FillLayout ());
		this.sash = new SashForm (this.tabFolderPage, SWT.HORIZONTAL);

		/* Create the "layout" and "control" columns */
		this.createLayoutGroup ();
		this.createControlGroup ();

		this.sash.setWeights(this.sashWeights ());
		return this.tabFolderPage;
	}

	/**
	 * Return the initial weight of the layout and control groups within the SashForm.
	 * Subclasses may override to provide tab-specific weights.
	 * @return the desired sash weights for the tab page
	 */
	int[] sashWeights () {
		return new int[] {50, 50};
	}

	/**
	 * Creates the TableEditor with a Text in the given column
	 * of the table.
	 */
	void createTextEditor (final Text text, final TableEditor textEditor, final int column) {
		text.setFont (this.table.getFont ());
		text.selectAll ();
		textEditor.horizontalAlignment = SWT.LEFT;
		textEditor.grabHorizontal = true;
		textEditor.setEditor (text, this.newItem, column);

		text.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
				this.resetEditors (true);
			}
			if (e.detail == SWT.TRAVERSE_ESCAPE) {
				this.disposeEditors ();
			}
		});
	}

	/**
	 * Disposes the editors without placing their contents
	 * into the table.
	 * Subclasses override this method.
	 */
	void disposeEditors () {
	}

	/**
	 * Generates the code needed to produce the example layout.
	 */
	StringBuilder generateCode () {
		/* Make sure all information being entered is stored in the table */
		this.resetEditors ();

		/* Get names for controls in the layout */
		this.names = new String [this.children.length];
		for (int i = 0; i < this.children.length; i++) {
			final TableItem myItem = this.table.getItem(i);
			final String name = myItem.getText(0);
			if (name.matches("\\d")) {
				final Control control = this.children [i];
				final String controlClass = control.getClass ().toString ();
				final String controlType = controlClass.substring (controlClass.lastIndexOf ('.') + 1);
				this.names [i] = controlType.toLowerCase () + i;
			} else {
				this.names [i] = myItem.getText(0);
			}
		}

		/* Create StringBuilder containing the code */
		final StringBuilder code = new StringBuilder ();
		code.append ("import org.eclipse.swt.*;\n");
		code.append ("import org.eclipse.swt.layout.*;\n");
		code.append ("import org.eclipse.swt.widgets.*;\n");
		if (this.needsCustom ()) {
      code.append ("import org.eclipse.swt.custom.*;\n");
    }
		if (this.needsGraphics ()) {
      code.append ("import org.eclipse.swt.graphics.*;\n");
    }
		code.append ("\n");
		code.append ("public class MyLayout {\n");
		code.append ("\tpublic static void main (String [] args) {\n");
		code.append ("\t\tDisplay display = new Display ();\n");
		code.append ("\t\tShell shell = new Shell (display);\n");

		/* Get layout specific code */
		code.append (this.generateLayoutCode ());

		code.append ("\n\t\tshell.pack ();\n\t\tshell.open ();\n\n");
		code.append ("\t\twhile (!shell.isDisposed ()) {\n");
		code.append ("\t\t\tif (!display.readAndDispatch ())\n");
		code.append ("\t\t\t\tdisplay.sleep ();\n\t\t}\n\t\tdisplay.dispose ();\n\t}\n}");

		return code;
	}

	boolean needsGraphics() {
		return false;
	}

	boolean needsCustom() {
		return false;
	}

	/**
	 * Generates layout specific code for the example layout.
	 * Subclasses override this method.
	 */
	StringBuilder generateLayoutCode () {
		return new StringBuilder ();
	}

	/**
	 * Returns the StringBuilder for the code which will
	 * create a child control.
	 */
	StringBuilder getChildCode (final Control control, final int i) {
		final StringBuilder code = new StringBuilder ();
		/* Find the type of control */
		final String controlClass = control.getClass().toString ();
		final String controlType = controlClass.substring (controlClass.lastIndexOf ('.') + 1);
		/* Find the style of the control */
		String styleString;
		if (controlType.equals ("Button")) {
			styleString = "SWT.PUSH";
		} else if (controlType.equals ("StyledText")) {
			styleString = "SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL";
		} else if (controlType.equals ("Canvas") || controlType.equals ("Composite") ||
					controlType.equals ("Table") || controlType.equals ("StyledText") ||
					controlType.equals ("ToolBar") || controlType.equals ("Tree") ||
					controlType.equals ("List") || controlType.equals ("Text")) {
			styleString = "SWT.BORDER";
		} else {
      styleString = "SWT.NONE";
    }
		/* Write out the control being declared */
		code.append ("\n\t\t" + controlType + " " + this.names [i] +
					 " = new " + controlType + " (shell, " + styleString + ");\n");
		/* Add items to those controls that need items */
		if (controlType.equals ("Combo") || controlType.equals ("List")) {
			code.append ("\t\t" + this.names [i] + ".setItems (new String [] {\"Item 1\", \"Item 2\", \"Item 2\"});\n");
		} else if (controlType.equals ("Table")) {
			code.append ("\t\t" + this.names [i] + ".setLinesVisible (true);\n");
			code.append ("\t\tfor (int i = 0; i < 2; i++) {\n");
			code.append ("\t\tTableItem tableItem = new TableItem (" + this.names [i] + ", SWT.NONE);\n");
			code.append ("\t\t\ttableItem.setText (\"Item\" + i);\n\t\t}\n");
		} else if (controlType.equals ("Tree")) {
			code.append ("\t\tfor (int i = 0; i < 2; i++) {\n");
			code.append ("\t\tTreeItem treeItem = new TreeItem (" + this.names [i] + ", SWT.NONE);\n");
			code.append ("\t\t\ttreeItem.setText (\"Item\" + i);\n\t\t}\n");
		} else if (controlType.equals ("ToolBar")) {
			code.append ("\t\tfor (int i = 0; i < 2; i++) {\n");
			code.append ("\t\tToolItem toolItem = new ToolItem (" + this.names [i] + ", SWT.NONE);\n");
			code.append ("\t\t\ttoolItem.setText (\"Item\" + i);\n\t\t}\n");
		} else if (controlType.equals ("CoolBar")) {
			code.append ("\t\tToolBar coolToolBar = new ToolBar (" + this.names [i] + ", SWT.BORDER);\n");
			code.append ("\t\tToolItem coolToolItem = new ToolItem (coolToolBar, SWT.NONE);\n");
			code.append ("\t\tcoolToolItem.setText (\"Item 1\");\n");
			code.append ("\t\tcoolToolItem = new ToolItem (coolToolBar, SWT.NONE);\n");
			code.append ("\t\tcoolToolItem.setText (\"Item 2\");\n");
			code.append ("\t\tCoolItem coolItem1 = new CoolItem (" + this.names [i] + ", SWT.NONE);\n");
			code.append ("\t\tcoolItem1.setControl (coolToolBar);\n");
			code.append ("\t\tPoint size = coolToolBar.computeSize (SWT.DEFAULT, SWT.DEFAULT);\n");
			code.append ("\t\tcoolItem1.setSize (coolItem1.computeSize (size.x, size.y));\n");
			code.append ("\t\tcoolToolBar = new ToolBar (" + this.names [i] + ", SWT.BORDER);\n");
			code.append ("\t\tcoolToolItem = new ToolItem (coolToolBar, SWT.NONE);\n");
			code.append ("\t\tcoolToolItem.setText (\"Item 3\");\n");
			code.append ("\t\tcoolToolItem = new ToolItem (coolToolBar, SWT.NONE);\n");
			code.append ("\t\tcoolToolItem.setText (\"Item 4\");\n");
			code.append ("\t\tCoolItem coolItem2 = new CoolItem (" + this.names [i] + ", SWT.NONE);\n");
			code.append ("\t\tcoolItem2.setControl (coolToolBar);\n");
			code.append ("\t\tsize = coolToolBar.computeSize (SWT.DEFAULT, SWT.DEFAULT);\n");
			code.append ("\t\tcoolItem2.setSize (coolItem2.computeSize (size.x, size.y));\n");
			code.append ("\t\t" + this.names [i] + ".setSize (" + this.names [i] + ".computeSize (SWT.DEFAULT, SWT.DEFAULT));\n");
		} else if (controlType.equals ("ProgressBar")) {
			code.append ("\t\t" + this.names [i] + ".setSelection (50);\n");
		}
		/* Set text for those controls that support it */
		if (controlType.equals ("Button") ||
			controlType.equals ("Combo") ||
			controlType.equals ("Group") ||
			controlType.equals ("Label") ||
			controlType.equals ("Link") ||
			controlType.equals ("StyledText") ||
			controlType.equals ("Text")) {
			code.append ("\t\t" + this.names [i] + ".setText (\"" + this.names [i] + "\");\n");
		}
		return code;
	}

	/**
	 * Returns the string to insert when a new child control is added to the table.
	 * Subclasses override this method.
	 */
	String[] getInsertString (final String name, final String controlType) {
		return null;
	}

	/**
	 * Returns the layout data field names.
	 * Subclasses override this method.
	 */
	String [] getLayoutDataFieldNames () {
		return new String [] {};
	}

	/**
	 * Gets the text for the tab folder item.
	 * Subclasses override this method.
	 */
	String getTabText () {
		return "";
	}

	/**
	 * Refreshes the composite and draws all controls
	 * in the layout example.
	 */
	void refreshLayoutComposite () {
		/* Remove children that are already laid out */
		this.children = this.layoutComposite.getChildren ();
		for (final Control child : this.children) {
			child.dispose ();
		}
		/* Add all children listed in the table */
		final TableItem [] items = this.table.getItems ();
		this.children = new Control [items.length];
		final String [] itemValues = new String [] {
			LayoutExample.getResourceString ("Item", new String [] {"1"}),
			LayoutExample.getResourceString ("Item", new String [] {"2"}),
			LayoutExample.getResourceString ("Item", new String [] {"3"})};
		for (int i = 0; i < items.length; i++) {
			final String control = items [i].getText (1);
			final String controlName = items [i].getText (0);
			if (control.equals ("Button")) {
				final Button button = new Button (this.layoutComposite, SWT.PUSH);
				button.setText (controlName);
				this.children [i] = button;
			} else if (control.equals ("Canvas")) {
				final Canvas canvas = new Canvas (this.layoutComposite, SWT.BORDER);
				this.children [i] = canvas;
			} else if (control.equals ("Combo")) {
				final Combo combo = new Combo (this.layoutComposite, SWT.NONE);
				combo.setItems (itemValues);
				combo.setText (controlName);
				this.children [i] = combo;
			} else if (control.equals ("Composite")) {
				final Composite composite = new Composite (this.layoutComposite, SWT.BORDER);
				this.children [i] = composite;
			} else if (control.equals ("CoolBar")) {
				final CoolBar coolBar = new CoolBar (this.layoutComposite, SWT.NONE);
				ToolBar toolBar = new ToolBar (coolBar, SWT.BORDER);
				ToolItem item = new ToolItem (toolBar, 0);
				item.setText (LayoutExample.getResourceString ("Item",new String [] {"1"}));
				item = new ToolItem (toolBar, 0);
				item.setText (LayoutExample.getResourceString ("Item",new String [] {"2"}));
				final CoolItem coolItem1 = new CoolItem (coolBar, 0);
				coolItem1.setControl (toolBar);
				toolBar = new ToolBar (coolBar, SWT.BORDER);
				item = new ToolItem (toolBar, 0);
				item.setText (LayoutExample.getResourceString ("Item",new String [] {"3"}));
				item = new ToolItem (toolBar, 0);
				item.setText (LayoutExample.getResourceString ("Item",new String [] {"4"}));
				final CoolItem coolItem2 = new CoolItem (coolBar, 0);
				coolItem2.setControl (toolBar);
				final Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				coolItem1.setSize(coolItem1.computeSize (size.x, size.y));
				coolItem2.setSize(coolItem2.computeSize (size.x, size.y));
				coolBar.setSize(coolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				this.children [i] = coolBar;
			} else if (control.equals ("Group")) {
				final Group group = new Group (this.layoutComposite, SWT.NONE);
				group.setText (controlName);
				this.children [i] = group;
			} else if (control.equals ("Label")) {
				final Label label = new Label (this.layoutComposite, SWT.NONE);
				label.setText (controlName);
				this.children [i] = label;
			} else if (control.equals ("Link")) {
				final Link link = new Link (this.layoutComposite, SWT.NONE);
				link.setText (controlName);
				this.children [i] = link;
			} else if (control.equals ("List")) {
				final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List (this.layoutComposite, SWT.BORDER);
				list.setItems (itemValues);
				this.children [i] = list;
			} else if (control.equals ("ProgressBar")) {
				final ProgressBar progress = new ProgressBar (this.layoutComposite, SWT.NONE);
				progress.setSelection (50);
				this.children [i] = progress;
			} else if (control.equals ("Scale")) {
				final Scale scale = new Scale (this.layoutComposite, SWT.NONE);
				this.children [i] = scale;
			} else if (control.equals ("Slider")) {
				final Slider slider = new Slider (this.layoutComposite, SWT.NONE);
				this.children [i] = slider;
			} else if (control.equals ("StyledText")) {
				final StyledText styledText = new StyledText (this.layoutComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
				styledText.setText (controlName);
				this.children [i] = styledText;
			} else if (control.equals ("Table")) {
				final Table table = new Table (this.layoutComposite, SWT.BORDER);
				table.setLinesVisible (true);
				final TableItem item1 = new TableItem (table, 0);
				item1.setText (LayoutExample.getResourceString ("Item",new String [] {"1"}));
				final TableItem item2 = new TableItem (table, 0);
				item2.setText (LayoutExample.getResourceString ("Item",new String [] {"2"}));
				this.children [i] = table;
			} else if (control.equals ("Text")) {
				final Text text = new Text (this.layoutComposite, SWT.BORDER);
				text.setText (controlName);
				this.children [i] = text;
			} else if (control.equals ("ToolBar")) {
				final ToolBar toolBar = new ToolBar (this.layoutComposite, SWT.BORDER);
				final ToolItem item1 = new ToolItem (toolBar, 0);
				item1.setText (LayoutExample.getResourceString ("Item",new String [] {"1"}));
				final ToolItem item2 = new ToolItem (toolBar, 0);
				item2.setText (LayoutExample.getResourceString ("Item",new String [] {"2"}));
				this.children [i] = toolBar;
			} else {
				final Tree tree = new Tree (this.layoutComposite, SWT.BORDER);
				final TreeItem item1 = new TreeItem (tree, 0);
				item1.setText (LayoutExample.getResourceString ("Item",new String [] {"1"}));
				final TreeItem item2 = new TreeItem (tree, 0);
				item2.setText (LayoutExample.getResourceString ("Item",new String [] {"2"}));
				this.children [i] = tree;
			}
		}
	}

	void resetEditors () {
		this.resetEditors (false);
	}

	/**
	 * Takes information from TableEditors and stores it.
	 * Subclasses override this method.
	 */
	void resetEditors (final boolean tab) {
	}

	/**
	 * Sets the layout data for the children of the layout.
	 * Subclasses override this method.
	 */
	void setLayoutData () {
	}

	/**
	 * Sets the state of the layout.
	 * Subclasses override this method.
	 */
	void setLayoutState () {
	}
}
