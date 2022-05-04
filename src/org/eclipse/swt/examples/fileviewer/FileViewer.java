/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
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
package org.eclipse.swt.examples.fileviewer;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * File Viewer example
 */
public class FileViewer {
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("examples_fileviewer");

	private final static String DRIVE_A = "a:" + File.separator;
	private final static String DRIVE_B = "b:" + File.separator;

	/* UI elements */
	private Display display;
	private Shell shell;
	private ToolBar toolBar;

	private Label numObjectsLabel;
	private Label diskSpaceLabel;

	private File currentDirectory = null;
	private boolean initial = true;

	/* Drag and drop optimizations */
	private boolean isDragging = false; // if this app is dragging
	private boolean isDropping = false; // if this app is dropping

	private File[]  processedDropFiles = null; // so Drag only deletes what it needs to
	private File[]  deferredRefreshFiles = null;      // to defer notifyRefreshFiles while we do DND
	private boolean deferredRefreshRequested = false; // to defer notifyRefreshFiles while we do DND
	private ProgressDialog progressDialog = null; // progress dialog for locally-initiated operations

	/* Combo view */
	private static final String COMBODATA_ROOTS = "Combo.roots";
		// File[]: Array of files whose paths are currently displayed in the combo
	private static final String COMBODATA_LASTTEXT = "Combo.lastText";
		// String: Previous selection text string

	private Combo combo;

	/* Tree view */
	private final IconCache iconCache = new IconCache();
	private static final String TREEITEMDATA_FILE = "TreeItem.file";
		// File: File associated with tree item
	private static final String TREEITEMDATA_IMAGEEXPANDED = "TreeItem.imageExpanded";
		// Image: shown when item is expanded
	private static final String TREEITEMDATA_IMAGECOLLAPSED = "TreeItem.imageCollapsed";
		// Image: shown when item is collapsed
	private static final String TREEITEMDATA_STUB = "TreeItem.stub";
		// Object: if not present or null then the item has not been populated

	private Tree tree;
	private Label treeScopeLabel;

	/* Table view */
	private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(
		DateFormat.MEDIUM, DateFormat.MEDIUM);
	private static final String TABLEITEMDATA_FILE = "TableItem.file";
		// File: File associated with table row
	private static final String TABLEDATA_DIR = "Table.dir";
		// File: Currently visible directory
	private static final int[] tableWidths = new int[] {150, 60, 75, 150};
	private final String[] tableTitles = new String [] {
		FileViewer.getResourceString("table.Name.title"),
		FileViewer.getResourceString("table.Size.title"),
		FileViewer.getResourceString("table.Type.title"),
		FileViewer.getResourceString("table.Modified.title")
	};
	private Table table;
	private Label tableContentsOfLabel;

	/* Table update worker */
	// Control data
	private final Object workerLock = new Object();
		// Lock for all worker control data and state
	private volatile Thread  workerThread = null;
		// The worker's thread
	private volatile boolean workerStopped = false;
		// True if the worker must exit on completion of the current cycle
	private volatile boolean workerCancelled = false;
		// True if the worker must cancel its operations prematurely perhaps due to a state update

	// Worker state information -- this is what gets synchronized by an update
	private volatile File workerStateDir = null;

	// State information to use for the next cycle
	private volatile File workerNextDir = null;

	/* Simulate only flag */
	// when true, disables actual filesystem manipulations and outputs results to standard out
	private boolean simulateOnly = true;

	/**
	 * Runs main program.
	 */
	public static void main (final String [] args) {
		final Display display = new Display ();
		final FileViewer application = new FileViewer();
		final Shell shell = application.open(display);
		while (! shell.isDisposed()) {
			if (! display.readAndDispatch()) {
        display.sleep();
      }
		}
		application.close();
		display.dispose();
	}

	/**
	 * Opens the main program.
	 */
	public Shell open(final Display display) {
		// Create the window
		this.display = display;
		this.iconCache.initResources(display);
		this.shell = new Shell();
		this.createShellContents();
		this.notifyRefreshFiles(null);
		this.shell.open();
		return this.shell;
	}

	/**
	 * Closes the main program.
	 */
	void close() {
		this.workerStop();
		this.iconCache.freeResources();
	}

	/**
	 * Returns a string from the resource bundle.
	 * We don't want to crash because of a missing String.
	 * Returns the key if not found.
	 */
	static String getResourceString(final String key) {
		try {
			return resourceBundle.getString(key);
		} catch (final MissingResourceException e) {
			return key;
		} catch (final NullPointerException e) {
			return "!" + key + "!";
		}
	}

	/**
	 * Returns a string from the resource bundle and binds it
	 * with the given arguments. If the key is not found,
	 * return the key.
	 */
	static String getResourceString(final String key, final Object[] args) {
		try {
			return MessageFormat.format(getResourceString(key), args);
		} catch (final MissingResourceException e) {
			return key;
		} catch (final NullPointerException e) {
			return "!" + key + "!";
		}
	}

	/**
	 * Construct the UI
	 *
	 * @param container the ShellContainer managing the Shell we are rendering inside
	 */
	private void createShellContents() {
		this.shell.setText(getResourceString("Title", new Object[] { "" }));
		this.shell.setImage(this.iconCache.stockImages[this.iconCache.shellIcon]);
		final Menu bar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(bar);
		this.createFileMenu(bar);
		this.createHelpMenu(bar);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		this.shell.setLayout(gridLayout);

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.widthHint = 185;
		this.createComboView(this.shell, gridData);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		this.createToolBar(this.shell, gridData);

		final SashForm sashForm = new SashForm(this.shell, SWT.NONE);
		sashForm.setOrientation(SWT.HORIZONTAL);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gridData.horizontalSpan = 3;
		sashForm.setLayoutData(gridData);
		this.createTreeView(sashForm);
		this.createTableView(sashForm);
		sashForm.setWeights(new int[] { 2, 5 });

		this.numObjectsLabel = new Label(this.shell, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		gridData.widthHint = 185;
		this.numObjectsLabel.setLayoutData(gridData);

		this.diskSpaceLabel = new Label(this.shell, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		this.diskSpaceLabel.setLayoutData(gridData);
	}

	/**
	 * Creates the File Menu.
	 *
	 * @param parent the parent menu
	 */
	private void createFileMenu(final Menu parent) {
		final Menu menu = new Menu(parent);
		final MenuItem header = new MenuItem(parent, SWT.CASCADE);
		header.setText(getResourceString("menu.File.text"));
		header.setMenu(menu);

		final MenuItem simulateItem = new MenuItem(menu, SWT.CHECK);
		simulateItem.setText(getResourceString("menu.File.SimulateOnly.text"));
		simulateItem.setSelection(this.simulateOnly);
		simulateItem.addSelectionListener(widgetSelectedAdapter(e -> this.simulateOnly = simulateItem.getSelection()));

		final MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(getResourceString("menu.File.Close.text"));
		item.addSelectionListener(widgetSelectedAdapter(e -> this.shell.close()));
	}

	/**
	 * Creates the Help Menu.
	 *
	 * @param parent the parent menu
	 */
	private void createHelpMenu(final Menu parent) {
		final Menu menu = new Menu(parent);
		final MenuItem header = new MenuItem(parent, SWT.CASCADE);
		header.setText(getResourceString("menu.Help.text"));
		header.setMenu(menu);

		final MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(getResourceString("menu.Help.About.text"));
		item.addSelectionListener(widgetSelectedAdapter(e -> {
			final MessageBox box = new MessageBox(this.shell, SWT.ICON_INFORMATION | SWT.OK);
			box.setText(getResourceString("dialog.About.title"));
			box.setMessage(getResourceString("dialog.About.description",
				new Object[] { System.getProperty("os.name") }));
			box.open();
		}));
	}

	/**
	 * Creates the toolbar
	 *
	 * @param shell the shell on which to attach the toolbar
	 * @param layoutData the layout data
	 */
	private void createToolBar(final Shell shell, final Object layoutData) {
		this.toolBar = new ToolBar(shell, SWT.NONE);
		this.toolBar.setLayoutData(layoutData);
		ToolItem item = new ToolItem(this.toolBar, SWT.SEPARATOR);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdParent]);
		item.setToolTipText(getResourceString("tool.Parent.tiptext"));
		item.addSelectionListener(widgetSelectedAdapter(e -> this.doParent()));
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdRefresh]);
		item.setToolTipText(getResourceString("tool.Refresh.tiptext"));
		item.addSelectionListener(widgetSelectedAdapter(e -> this.doRefresh()));
		final SelectionListener unimplementedListener = widgetSelectedAdapter(e -> {
			final MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			box.setText(getResourceString("dialog.NotImplemented.title"));
			box.setMessage(getResourceString("dialog.ActionNotImplemented.description"));
			box.open();
		});

		item = new ToolItem(this.toolBar, SWT.SEPARATOR);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdCut]);
		item.setToolTipText(getResourceString("tool.Cut.tiptext"));
		item.addSelectionListener(unimplementedListener);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdCopy]);
		item.setToolTipText(getResourceString("tool.Copy.tiptext"));
		item.addSelectionListener(unimplementedListener);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdPaste]);
		item.setToolTipText(getResourceString("tool.Paste.tiptext"));
		item.addSelectionListener(unimplementedListener);

		item = new ToolItem(this.toolBar, SWT.SEPARATOR);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdDelete]);
		item.setToolTipText(getResourceString("tool.Delete.tiptext"));
		item.addSelectionListener(unimplementedListener);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdRename]);
		item.setToolTipText(getResourceString("tool.Rename.tiptext"));
		item.addSelectionListener(unimplementedListener);

		item = new ToolItem(this.toolBar, SWT.SEPARATOR);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdSearch]);
		item.setToolTipText(getResourceString("tool.Search.tiptext"));
		item.addSelectionListener(unimplementedListener);
		item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setImage(this.iconCache.stockImages[this.iconCache.cmdPrint]);
		item.setToolTipText(getResourceString("tool.Print.tiptext"));
		item.addSelectionListener(unimplementedListener);
	}

	/**
	 * Creates the combo box view.
	 *
	 * @param parent the parent control
	 */
	private void createComboView(final Composite parent, final Object layoutData) {
		this.combo = new Combo(parent, SWT.NONE);
		this.combo.setLayoutData(layoutData);
		this.combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final File[] roots = (File[]) FileViewer.this.combo.getData(COMBODATA_ROOTS);
				if (roots == null) {
          return;
        }
				final int selection = FileViewer.this.combo.getSelectionIndex();
				if ((selection >= 0) && (selection < roots.length)) {
					FileViewer.this.notifySelectedDirectory(roots[selection]);
				}
			}
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				final String lastText = (String) FileViewer.this.combo.getData(COMBODATA_LASTTEXT);
				final String text = FileViewer.this.combo.getText();
				if ((text == null) || ((lastText != null) && lastText.equals(text))) {
          return;
        }
				FileViewer.this.combo.setData(COMBODATA_LASTTEXT, text);
				FileViewer.this.notifySelectedDirectory(new File(text));
			}
		});
	}

	/**
	 * Creates the file tree view.
	 *
	 * @param parent the parent control
	 */
	private void createTreeView(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = gridLayout.marginWidth = 2;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		this.treeScopeLabel = new Label(composite, SWT.BORDER);
		this.treeScopeLabel.setText(FileViewer.getResourceString("details.AllFolders.text"));
		this.treeScopeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		this.tree = new Tree(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		this.tree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

		this.tree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final TreeItem[] selection = FileViewer.this.tree.getSelection();
				if ((selection != null) && (selection.length != 0)) {
					final TreeItem item = selection[0];
					final File file = (File) item.getData(TREEITEMDATA_FILE);

					FileViewer.this.notifySelectedDirectory(file);
				}
			}
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				final TreeItem[] selection = FileViewer.this.tree.getSelection();
				if ((selection != null) && (selection.length != 0)) {
					final TreeItem item = selection[0];
					item.setExpanded(true);
					FileViewer.this.treeExpandItem(item);
				}
			}
		});
		this.tree.addTreeListener(new TreeAdapter() {
			@Override
			public void treeExpanded(final TreeEvent event) {
				final TreeItem item = (TreeItem) event.item;
				final Image image = (Image) item.getData(TREEITEMDATA_IMAGEEXPANDED);
				if (image != null) {
          item.setImage(image);
        }
				FileViewer.this.treeExpandItem(item);
			}
			@Override
			public void treeCollapsed(final TreeEvent event) {
				final TreeItem item = (TreeItem) event.item;
				final Image image = (Image) item.getData(TREEITEMDATA_IMAGECOLLAPSED);
				if (image != null) {
          item.setImage(image);
        }
			}
		});
		this.createTreeDragSource(this.tree);
		this.createTreeDropTarget(this.tree);
	}

	/**
	 * Creates the Drag & Drop DragSource for items being dragged from the tree.
	 *
	 * @return the DragSource for the tree
	 */
	private DragSource createTreeDragSource(final Tree tree){
		final DragSource dragSource = new DragSource(tree, DND.DROP_MOVE | DND.DROP_COPY);
		dragSource.setTransfer(FileTransfer.getInstance());
		dragSource.addDragListener(new DragSourceListener() {
			TreeItem[] dndSelection = null;
			String[] sourceNames = null;
			@Override
			public void dragStart(final DragSourceEvent event){
				this.dndSelection = tree.getSelection();
				this.sourceNames = null;
				event.doit = this.dndSelection.length > 0;
				FileViewer.this.isDragging = true;
				FileViewer.this.processedDropFiles = null;
			}
			@Override
			public void dragFinished(final DragSourceEvent event){
				FileViewer.this.dragSourceHandleDragFinished(event, this.sourceNames);
				this.dndSelection = null;
				this.sourceNames = null;
				FileViewer.this.isDragging = false;
				FileViewer.this.processedDropFiles = null;
				FileViewer.this.handleDeferredRefresh();
			}
			@Override
			public void dragSetData(final DragSourceEvent event){
				if ((this.dndSelection == null) || (this.dndSelection.length == 0) || ! FileTransfer.getInstance().isSupportedType(event.dataType)) {
          return;
        }

				this.sourceNames  = new String[this.dndSelection.length];
				for (int i = 0; i < this.dndSelection.length; i++) {
					final File file = (File) this.dndSelection[i].getData(TREEITEMDATA_FILE);
					this.sourceNames[i] = file.getAbsolutePath();
				}
				event.data = this.sourceNames;
			}
		});
		return dragSource;
	}

	/**
	 * Creates the Drag & Drop DropTarget for items being dropped onto the tree.
	 *
	 * @return the DropTarget for the tree
	 */
	private DropTarget createTreeDropTarget(final Tree tree) {
		final DropTarget dropTarget = new DropTarget(tree, DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(FileTransfer.getInstance());
		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragEnter(final DropTargetEvent event) {
				FileViewer.this.isDropping = true;
			}
			@Override
			public void dragLeave(final DropTargetEvent event) {
				FileViewer.this.isDropping = false;
				FileViewer.this.handleDeferredRefresh();
			}
			@Override
			public void dragOver(final DropTargetEvent event) {
				FileViewer.this.dropTargetValidate(event, this.getTargetFile(event));
				event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
			}
			@Override
			public void drop(final DropTargetEvent event) {
				final File targetFile = this.getTargetFile(event);
				if (FileViewer.this.dropTargetValidate(event, targetFile)) {
          FileViewer.this.dropTargetHandleDrop(event, targetFile);
        }
			}
			private File getTargetFile(final DropTargetEvent event) {
				// Determine the target File for the drop
				final TreeItem item = tree.getItem(tree.toControl(new Point(event.x, event.y)));
				File targetFile = null;
				if (item != null) {
					// We are over a particular item in the tree, use the item's file
					targetFile = (File) item.getData(TREEITEMDATA_FILE);
				}
				return targetFile;
			}
		});
		return dropTarget;
	}

	/**
	 * Handles expand events on a tree item.
	 *
	 * @param item the TreeItem to fill in
	 */
	private void treeExpandItem(final TreeItem item) {
		this.shell.setCursor(this.iconCache.stockCursors[this.iconCache.cursorWait]);
		final Object stub = item.getData(TREEITEMDATA_STUB);
		if (stub == null) {
      this.treeRefreshItem(item, true);
    }
		this.shell.setCursor(this.iconCache.stockCursors[this.iconCache.cursorDefault]);
	}

	/**
	 * Traverse the entire tree and update only what has changed.
	 *
	 * @param roots the root directory listing
	 */
	private void treeRefresh(final File[] masterFiles) {
		final TreeItem[] items = this.tree.getItems();
		int masterIndex = 0;
		int itemIndex = 0;
		for (int i = 0; i < items.length; ++i) {
			final TreeItem item = items[i];
			final File itemFile = (File) item.getData(TREEITEMDATA_FILE);
			if ((itemFile == null) || (masterIndex == masterFiles.length)) {
				// remove bad item or placeholder
				item.dispose();
				continue;
			}
			final File masterFile = masterFiles[masterIndex];
			final int compare = compareFiles(masterFile, itemFile);
			if (compare == 0) {
				// same file, update it
				this.treeRefreshItem(item, false);
				++itemIndex;
				++masterIndex;
			} else if (compare < 0) {
				// should appear before file, insert it
				final TreeItem newItem = new TreeItem(this.tree, SWT.NONE, itemIndex);
				this.treeInitVolume(newItem, masterFile);
				new TreeItem(newItem, SWT.NONE); // placeholder child item to get "expand" button
				++itemIndex;
				++masterIndex;
				--i;
			} else {
				// should appear after file, delete stale item
				item.dispose();
			}
		}
		for (;masterIndex < masterFiles.length; ++masterIndex) {
			final File masterFile = masterFiles[masterIndex];
			final TreeItem newItem = new TreeItem(this.tree, SWT.NONE);
			this.treeInitVolume(newItem, masterFile);
			new TreeItem(newItem, SWT.NONE); // placeholder child item to get "expand" button
		}
	}

	/**
	 * Traverse an item in the tree and update only what has changed.
	 *
	 * @param dirItem the tree item of the directory
	 * @param forcePopulate true iff we should populate non-expanded items as well
	 */
	private void treeRefreshItem(final TreeItem dirItem, final boolean forcePopulate) {
		final File dir = (File) dirItem.getData(TREEITEMDATA_FILE);

		if (! forcePopulate && ! dirItem.getExpanded()) {
			// Refresh non-expanded item
			if (dirItem.getData(TREEITEMDATA_STUB) != null) {
				treeItemRemoveAll(dirItem);
				new TreeItem(dirItem, SWT.NONE); // placeholder child item to get "expand" button
				dirItem.setData(TREEITEMDATA_STUB, null);
			}
			return;
		}
		// Refresh expanded item
		dirItem.setData(TREEITEMDATA_STUB, this); // clear stub flag

		/* Get directory listing */
		final File[] subFiles = (dir != null) ? FileViewer.getDirectoryList(dir) : null;
		if ((subFiles == null) || (subFiles.length == 0)) {
			/* Error or no contents */
			treeItemRemoveAll(dirItem);
			dirItem.setExpanded(false);
			return;
		}

		/* Refresh sub-items */
		final TreeItem[] items = dirItem.getItems();
		final File[] masterFiles = subFiles;
		int masterIndex = 0;
		int itemIndex = 0;
		File masterFile = null;
		for (int i = 0; i < items.length; ++i) {
			while ((masterFile == null) && (masterIndex < masterFiles.length)) {
				masterFile = masterFiles[masterIndex++];
				if (! masterFile.isDirectory()) {
          masterFile = null;
        }
			}

			final TreeItem item = items[i];
			final File itemFile = (File) item.getData(TREEITEMDATA_FILE);
			if ((itemFile == null) || (masterFile == null)) {
				// remove bad item or placeholder
				item.dispose();
				continue;
			}
			final int compare = compareFiles(masterFile, itemFile);
			if (compare == 0) {
				// same file, update it
				this.treeRefreshItem(item, false);
				masterFile = null;
				++itemIndex;
			} else if (compare < 0) {
				// should appear before file, insert it
				final TreeItem newItem = new TreeItem(dirItem, SWT.NONE, itemIndex);
				this.treeInitFolder(newItem, masterFile);
				new TreeItem(newItem, SWT.NONE); // add a placeholder child item so we get the "expand" button
				masterFile = null;
				++itemIndex;
				--i;
			} else {
				// should appear after file, delete stale item
				item.dispose();
			}
		}
		while ((masterFile != null) || (masterIndex < masterFiles.length)) {
			if (masterFile != null) {
				final TreeItem newItem = new TreeItem(dirItem, SWT.NONE);
				this.treeInitFolder(newItem, masterFile);
				new TreeItem(newItem, SWT.NONE); // add a placeholder child item so we get the "expand" button
				if (masterIndex == masterFiles.length) {
          break;
        }
			}
			masterFile = masterFiles[masterIndex++];
			if (! masterFile.isDirectory()) {
        masterFile = null;
      }
		}
	}

	/**
	 * Foreign method: removes all children of a TreeItem.
	 * @param treeItem the TreeItem
	 */
	private static void treeItemRemoveAll(final TreeItem treeItem) {
		final TreeItem[] children = treeItem.getItems();
		for (final TreeItem child : children) {
			child.dispose();
		}
	}

	/**
	 * Initializes a folder item.
	 *
	 * @param item the TreeItem to initialize
	 * @param folder the File associated with this TreeItem
	 */
	private void treeInitFolder(final TreeItem item, final File folder) {
		item.setText(folder.getName());
		item.setImage(this.iconCache.stockImages[this.iconCache.iconClosedFolder]);
		item.setData(TREEITEMDATA_FILE, folder);
		item.setData(TREEITEMDATA_IMAGEEXPANDED, this.iconCache.stockImages[this.iconCache.iconOpenFolder]);
		item.setData(TREEITEMDATA_IMAGECOLLAPSED, this.iconCache.stockImages[this.iconCache.iconClosedFolder]);
	}

	/**
	 * Initializes a volume item.
	 *
	 * @param item the TreeItem to initialize
	 * @param volume the File associated with this TreeItem
	 */
	private void treeInitVolume(final TreeItem item, final File volume) {
		item.setText(volume.getPath());
		item.setImage(this.iconCache.stockImages[this.iconCache.iconClosedDrive]);
		item.setData(TREEITEMDATA_FILE, volume);
		item.setData(TREEITEMDATA_IMAGEEXPANDED, this.iconCache.stockImages[this.iconCache.iconOpenDrive]);
		item.setData(TREEITEMDATA_IMAGECOLLAPSED, this.iconCache.stockImages[this.iconCache.iconClosedDrive]);
	}

	/**
	 * Creates the file details table.
	 *
	 * @param parent the parent control
	 */
	private void createTableView(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = gridLayout.marginWidth = 2;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);
		this.tableContentsOfLabel = new Label(composite, SWT.BORDER);
		this.tableContentsOfLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		this.table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

		for (int i = 0; i < this.tableTitles.length; ++i) {
			final TableColumn column = new TableColumn(this.table, SWT.NONE);
			column.setText(this.tableTitles[i]);
			column.setWidth(tableWidths[i]);
		}
		this.table.setHeaderVisible(true);
		this.table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				FileViewer.this.notifySelectedFiles(this.getSelectedFiles());
			}
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				FileViewer.this.doDefaultFileAction(this.getSelectedFiles());
			}
			private File[] getSelectedFiles() {
				final TableItem[] items = FileViewer.this.table.getSelection();
				final File[] files = new File[items.length];

				for (int i = 0; i < items.length; ++i) {
					files[i] = (File) items[i].getData(TABLEITEMDATA_FILE);
				}
				return files;
			}
		});

		this.createTableDragSource(this.table);
		this.createTableDropTarget(this.table);
	}

	/**
	 * Creates the Drag & Drop DragSource for items being dragged from the table.
	 *
	 * @return the DragSource for the table
	 */
	private DragSource createTableDragSource(final Table table) {
		final DragSource dragSource = new DragSource(table, DND.DROP_MOVE | DND.DROP_COPY);
		dragSource.setTransfer(FileTransfer.getInstance());
		dragSource.addDragListener(new DragSourceListener() {
			TableItem[] dndSelection = null;
			String[] sourceNames = null;
			@Override
			public void dragStart(final DragSourceEvent event){
				this.dndSelection = table.getSelection();
				this.sourceNames = null;
				event.doit = this.dndSelection.length > 0;
				FileViewer.this.isDragging = true;
			}
			@Override
			public void dragFinished(final DragSourceEvent event){
				FileViewer.this.dragSourceHandleDragFinished(event, this.sourceNames);
				this.dndSelection = null;
				this.sourceNames = null;
				FileViewer.this.isDragging = false;
				FileViewer.this.handleDeferredRefresh();
			}
			@Override
			public void dragSetData(final DragSourceEvent event){
				if ((this.dndSelection == null) || (this.dndSelection.length == 0) || ! FileTransfer.getInstance().isSupportedType(event.dataType)) {
          return;
        }

				this.sourceNames  = new String[this.dndSelection.length];
				for (int i = 0; i < this.dndSelection.length; i++) {
					final File file = (File) this.dndSelection[i].getData(TABLEITEMDATA_FILE);
					this.sourceNames[i] = file.getAbsolutePath();
				}
				event.data = this.sourceNames;
			}
		});
		return dragSource;
	}

	/**
	 * Creates the Drag & Drop DropTarget for items being dropped onto the table.
	 *
	 * @return the DropTarget for the table
	 */
	private DropTarget createTableDropTarget(final Table table){
		final DropTarget dropTarget = new DropTarget(table, DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(FileTransfer.getInstance() );
		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragEnter(final DropTargetEvent event) {
				FileViewer.this.isDropping = true;
			}
			@Override
			public void dragLeave(final DropTargetEvent event) {
				FileViewer.this.isDropping = false;
				FileViewer.this.handleDeferredRefresh();
			}
			@Override
			public void dragOver(final DropTargetEvent event) {
				FileViewer.this.dropTargetValidate(event, this.getTargetFile(event));
				event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
			}
			@Override
			public void drop(final DropTargetEvent event) {
				final File targetFile = this.getTargetFile(event);
				if (FileViewer.this.dropTargetValidate(event, targetFile)) {
          FileViewer.this.dropTargetHandleDrop(event, targetFile);
        }
			}
			private File getTargetFile(final DropTargetEvent event) {
				// Determine the target File for the drop
				final TableItem item = table.getItem(table.toControl(new Point(event.x, event.y)));
				File targetFile = null;
				if (item == null) {
					// We are over an unoccupied area of the table.
					// If it is a COPY, we can use the table's root file.
					if (event.detail == DND.DROP_COPY) {
						targetFile = (File) table.getData(TABLEDATA_DIR);
					}
				} else {
					// We are over a particular item in the table, use the item's file
					targetFile = (File) item.getData(TABLEITEMDATA_FILE);
				}
				return targetFile;
			}
		});
		return dropTarget;
	}

	/**
	 * Notifies the application components that a new current directory has been selected
	 *
	 * @param dir the directory that was selected, null is ignored
	 */
	void notifySelectedDirectory(File dir) {
		if ((dir == null) || ((this.currentDirectory != null) && dir.equals(this.currentDirectory))) {
      return;
    }
		this.currentDirectory = dir;
		this.notifySelectedFiles(null);

		/* Shell:
		 * Sets the title to indicate the selected directory
		 */
		this.shell.setText(getResourceString("Title", new Object[] { this.currentDirectory.getPath() }));

		/* Table view:
		 * Displays the contents of the selected directory.
		 */
		this.workerUpdate(dir, false);

		/* Combo view:
		 * Sets the combo box to point to the selected directory.
		 */
		final File[] comboRoots = (File[]) this.combo.getData(COMBODATA_ROOTS);
		int comboEntry = -1;
		if (comboRoots != null) {
			for (int i = 0; i < comboRoots.length; ++i) {
				if (dir.equals(comboRoots[i])) {
					comboEntry = i;
					break;
				}
			}
		}
		if (comboEntry == -1) {
      this.combo.setText(dir.getPath());
    } else {
      this.combo.select(comboEntry);
    }

		/* Tree view:
		 * If not already expanded, recursively expands the parents of the specified
		 * directory until it is visible.
		 */
		final List <File> path = new ArrayList<>();
		// Build a stack of paths from the root of the tree
		while (dir != null) {
			path.add(dir);
			dir = dir.getParentFile();
		}
		// Recursively expand the tree to get to the specified directory
		TreeItem[] items = this.tree.getItems();
		TreeItem lastItem = null;
		for (int i = path.size() - 1; i >= 0; --i) {
			final File pathElement = path.get(i);

			// Search for a particular File in the array of tree items
			// No guarantee that the items are sorted in any recognizable fashion, so we'll
			// just sequential scan.  There shouldn't be more than a few thousand entries.
			TreeItem item = null;
			for (final TreeItem currentItem : items) {
				item = currentItem;
				if (item.isDisposed()) {
          continue;
        }
				final File itemFile = (File) item.getData(TREEITEMDATA_FILE);
				if ((itemFile != null) && itemFile.equals(pathElement)) {
          break;
        }
			}
			if (item == null) {
        break;
      }
			lastItem = item;
			if ((i != 0) && !item.getExpanded()) {
				this.treeExpandItem(item);
				item.setExpanded(true);
			}
			items = item.getItems();
		}
		this.tree.setSelection((lastItem != null) ? new TreeItem[] { lastItem } : new TreeItem[0]);
	}

	/**
	 * Notifies the application components that files have been selected
	 *
	 * @param files the files that were selected, null or empty array indicates no active selection
	 */
	void notifySelectedFiles(final File[] files) {
		/* Details:
		 * Update the details that are visible on screen.
		 */
		if ((files != null) && (files.length != 0)) {
			this.numObjectsLabel.setText(getResourceString("details.NumberOfSelectedFiles.text",
				new Object[] { Integer.valueOf(files.length) }));
			long fileSize = 0L;
			for (final File file : files) {
				fileSize += file.length();
			}
			this.diskSpaceLabel.setText(getResourceString("details.FileSize.text",
				new Object[] { Long.valueOf(fileSize) }));
		} else {
			// No files selected
			this.diskSpaceLabel.setText("");
			if (this.currentDirectory != null) {
				final int numObjects = getDirectoryList(this.currentDirectory).length;
				this.numObjectsLabel.setText(getResourceString("details.DirNumberOfObjects.text",
					new Object[] { Integer.valueOf(numObjects) }));
			} else {
				this.numObjectsLabel.setText("");
			}
		}
	}

	/**
	 * Notifies the application components that files must be refreshed
	 *
	 * @param files the files that need refreshing, empty array is a no-op, null refreshes all
	 */
	void notifyRefreshFiles(final File[] files) {
		if ((files != null) && (files.length == 0)) {
      return;
    }

		if ((this.deferredRefreshRequested) && (this.deferredRefreshFiles != null) && (files != null)) {
			// merge requests
			final File[] newRequest = new File[this.deferredRefreshFiles.length + files.length];
			System.arraycopy(this.deferredRefreshFiles, 0, newRequest, 0, this.deferredRefreshFiles.length);
			System.arraycopy(files, 0, newRequest, this.deferredRefreshFiles.length, files.length);
			this.deferredRefreshFiles = newRequest;
		} else {
			this.deferredRefreshFiles = files;
			this.deferredRefreshRequested = true;
		}
		this.handleDeferredRefresh();
	}

	/**
	 * Handles deferred Refresh notifications (due to Drag & Drop)
	 */
	void handleDeferredRefresh() {
		if (this.isDragging || this.isDropping || ! this.deferredRefreshRequested) {
      return;
    }
		if (this.progressDialog != null) {
			this.progressDialog.close();
			this.progressDialog = null;
		}

		this.deferredRefreshRequested = false;
		final File[] files = this.deferredRefreshFiles;
		this.deferredRefreshFiles = null;

		this.shell.setCursor(this.iconCache.stockCursors[this.iconCache.cursorWait]);

		/* Table view:
		 * Refreshes information about any files in the list and their children.
		 */
		boolean refreshTable = false;
		if (files != null) {
			for (final File file : files) {
				if (file.equals(this.currentDirectory)) {
					refreshTable = true;
					break;
				}
				final File parentFile = file.getParentFile();
				if ((parentFile != null) && (parentFile.equals(this.currentDirectory))) {
					refreshTable = true;
					break;
				}
			}
		} else {
      refreshTable = true;
    }
		if (refreshTable) {
      this.workerUpdate(this.currentDirectory, true);
    }

		/* Combo view:
		 * Refreshes the list of roots
		 */
		final File[] roots = this.getRoots();

		if (files == null) {
			boolean refreshCombo = false;
			final File[] comboRoots = (File[]) this.combo.getData(COMBODATA_ROOTS);

			if ((comboRoots != null) && (comboRoots.length == roots.length)) {
				for (int i = 0; i < roots.length; ++i) {
					if (! roots[i].equals(comboRoots[i])) {
						refreshCombo = true;
						break;
					}
				}
			} else {
        refreshCombo = true;
      }

			if (refreshCombo) {
				this.combo.removeAll();
				this.combo.setData(COMBODATA_ROOTS, roots);
				for (final File file : roots) {
					this.combo.add(file.getPath());
				}
			}
		}

		/* Tree view:
		 * Refreshes information about any files in the list and their children.
		 */
		this.treeRefresh(roots);

		// Remind everyone where we are in the filesystem
		final File dir = this.currentDirectory;
		this.currentDirectory = null;
		this.notifySelectedDirectory(dir);

		this.shell.setCursor(this.iconCache.stockCursors[this.iconCache.cursorDefault]);
	}

	/**
	 * Performs the default action on a set of files.
	 *
	 * @param files the array of files to process
	 */
	void doDefaultFileAction(final File[] files) {
		// only uses the 1st file (for now)
		if (files.length == 0) {
      return;
    }
		final File file = files[0];

		if (file.isDirectory()) {
			this.notifySelectedDirectory(file);
		} else {
			final String fileName = file.getAbsolutePath();
			if (! Program.launch(fileName)) {
				final MessageBox dialog = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.OK);
				dialog.setMessage(getResourceString("error.FailedLaunch.message", new Object[] { fileName }));
				dialog.setText(this.shell.getText ());
				dialog.open();
			}
		}
	}

	/**
	 * Navigates to the parent directory
	 */
	void doParent() {
		if (this.currentDirectory == null) {
      return;
    }
		final File parentDirectory = this.currentDirectory.getParentFile();
		this.notifySelectedDirectory(parentDirectory);
	}

	/**
	 * Performs a refresh
	 */
	void doRefresh() {
		this.notifyRefreshFiles(null);
	}

	/**
	 * Validates a drop target as a candidate for a drop operation.
	 * <p>
	 * Used in dragOver() and dropAccept().<br>
	 * Note event.detail is set to DND.DROP_NONE by this method if the target is not valid.
	 * </p>
	 * @param event the DropTargetEvent to validate
	 * @param targetFile the File representing the drop target location
	 *        under inspection, or null if none
	 */
	private boolean dropTargetValidate(final DropTargetEvent event, final File targetFile) {
		if ((targetFile != null) && targetFile.isDirectory()) {
			if ((event.detail != DND.DROP_COPY) && (event.detail != DND.DROP_MOVE)) {
				event.detail = DND.DROP_MOVE;
			}
		} else {
			event.detail = DND.DROP_NONE;
		}
		return event.detail != DND.DROP_NONE;
	}

	/**
	 * Handles a drop on a dropTarget.
	 * <p>
	 * Used in drop().<br>
	 * Note event.detail is modified by this method.
	 * </p>
	 * @param event the DropTargetEvent passed as parameter to the drop() method
	 * @param targetFile the File representing the drop target location
	 *        under inspection, or null if none
	 */
	private void dropTargetHandleDrop(final DropTargetEvent event, final File targetFile) {
		// Get dropped data (an array of filenames)
		if (! this.dropTargetValidate(event, targetFile)) {
      return;
    }
		final String[] sourceNames = (String[]) event.data;
		if (sourceNames == null) {
      event.detail = DND.DROP_NONE;
    }
		if (event.detail == DND.DROP_NONE) {
      return;
    }

		// Open progress dialog
		this.progressDialog = new ProgressDialog(this.shell,
			(event.detail == DND.DROP_MOVE) ? ProgressDialog.MOVE : ProgressDialog.COPY);
		this.progressDialog.setTotalWorkUnits(sourceNames.length);
		this.progressDialog.open();

		// Copy each file
		final List<File> processedFiles = new ArrayList<>();
		for (int i = 0; (i < sourceNames.length) && (! this.progressDialog.isCancelled()); i++){
			final File source = new File(sourceNames[i]);
			final File dest = new File(targetFile, source.getName());
			if (source.equals(dest))
       {
        continue; // ignore if in same location
      }

			this.progressDialog.setDetailFile(source, ProgressDialog.COPY);
			while (! this.progressDialog.isCancelled()) {
				if (this.copyFileStructure(source, dest)) {
					processedFiles.add(source);
					break;
				} else if (! this.progressDialog.isCancelled()) {
					if ((event.detail == DND.DROP_MOVE) && (!this.isDragging)) {
						// It is not possible to notify an external drag source that a drop
						// operation was only partially successful.  This is particularly a
						// problem for DROP_MOVE operations since unless the source gets
						// DROP_NONE, it will delete the original data including bits that
						// may not have been transferred successfully.
						final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.RETRY | SWT.CANCEL);
						box.setText(getResourceString("dialog.FailedCopy.title"));
						box.setMessage(getResourceString("dialog.FailedCopy.description",
							new Object[] { source, dest }));
						final int button = box.open();
						if (button == SWT.CANCEL) {
							i = sourceNames.length;
							event.detail = DND.DROP_NONE;
							break;
						}
					} else {
						// We can recover gracefully from errors if the drag source belongs
						// to this application since it will look at processedDropFiles.
						final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
						box.setText(getResourceString("dialog.FailedCopy.title"));
						box.setMessage(getResourceString("dialog.FailedCopy.description",
							new Object[] { source, dest }));
						final int button = box.open();
						if (button == SWT.ABORT) {
              i = sourceNames.length;
            }
						if (button != SWT.RETRY) {
              break;
            }
					}
				}
				this.progressDialog.addProgress(1);
			}
		}
		if (this.isDragging) {
			// Remember exactly which files we processed
			this.processedDropFiles = processedFiles.toArray(new File[processedFiles.size()]);
		} else {
			this.progressDialog.close();
			this.progressDialog = null;
		}
		this.notifyRefreshFiles(new File[] { targetFile });
	}

	/**
	 * Handles the completion of a drag on a dragSource.
	 * <p>
	 * Used in dragFinished().<br>
	 * </p>
	 * @param event the DragSourceEvent passed as parameter to the dragFinished() method
	 * @param sourceNames the names of the files that were dragged (event.data is invalid)
	 */
	private void dragSourceHandleDragFinished(final DragSourceEvent event, final String[] sourceNames) {
		if ((sourceNames == null) || (event.detail != DND.DROP_MOVE)) {
      return;
    }

		// Get array of files that were actually transferred
		final File[] sourceFiles;
		if (this.processedDropFiles != null) {
			sourceFiles = this.processedDropFiles;
		} else {
			sourceFiles = new File[sourceNames.length];
			for (int i = 0; i < sourceNames.length; ++i) {
        sourceFiles[i] = new File(sourceNames[i]);
      }
		}
		if (this.progressDialog == null) {
      this.progressDialog = new ProgressDialog(this.shell, ProgressDialog.MOVE);
    }
		this.progressDialog.setTotalWorkUnits(sourceFiles.length);
		this.progressDialog.setProgress(0);
		this.progressDialog.open();

		// Delete each file
		for (int i = 0; (i < sourceFiles.length) && (! this.progressDialog.isCancelled()); i++){
			final File source = sourceFiles[i];
			this.progressDialog.setDetailFile(source, ProgressDialog.DELETE);
			while (! this.progressDialog.isCancelled()) {
				if (this.deleteFileStructure(source)) {
					break;
				} else if (! this.progressDialog.isCancelled()) {
					final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
					box.setText(getResourceString("dialog.FailedDelete.title"));
					box.setMessage(getResourceString("dialog.FailedDelete.description",
						new Object[] { source }));
					final int button = box.open();
					if (button == SWT.ABORT) {
            i = sourceNames.length;
          }
					if (button == SWT.RETRY) {
            break;
          }
				}
			}
			this.progressDialog.addProgress(1);
		}
		this.notifyRefreshFiles(sourceFiles);
		this.progressDialog.close();
		this.progressDialog = null;
	}

	/**
	 * Gets filesystem root entries
	 *
	 * @return an array of Files corresponding to the root directories on the platform,
	 *         may be empty but not null
	 */
	File[] getRoots() {
		/*
		 * On JDK 1.22 only...
		 */
		// return File.listRoots();

		/*
		 * On JDK 1.1.7 and beyond...
		 * -- PORTABILITY ISSUES HERE --
		 */
		if (System.getProperty ("os.name").contains ("Windows")) {
			final List<File> list = new ArrayList<>();
			list.add(new File(DRIVE_A));
			list.add(new File(DRIVE_B));
			for (char i = 'c'; i <= 'z'; ++i) {
				final File drive = new File(i + ":" + File.separator);
				if (drive.isDirectory() && drive.exists()) {
					list.add(drive);
					if (this.initial && (i == 'c')) {
						this.currentDirectory = drive;
						this.initial = false;
					}
				}
			}
			final File[] roots = list.toArray(new File[list.size()]);
			sortFiles(roots);
			return roots;
		}
		final File root = new File(File.separator);
		if (this.initial) {
			this.currentDirectory = root;
			this.initial = false;
		}
		return new File[] { root };
	}

	/**
	 * Gets a directory listing
	 *
	 * @param file the directory to be listed
	 * @return an array of files this directory contains, may be empty but not null
	 */
	static File[] getDirectoryList(final File file) {
		final File[] list = file.listFiles();
		if (list == null) {
      return new File[0];
    }
		sortFiles(list);
		return list;
	}

	/**
	 * Copies a file or entire directory structure.
	 *
	 * @param oldFile the location of the old file or directory
	 * @param newFile the location of the new file or directory
	 * @return true iff the operation succeeds without errors
	 */
	boolean copyFileStructure(final File oldFile, final File newFile) {
		if ((oldFile == null) || (newFile == null)) {
      return false;
    }

		// ensure that newFile is not a child of oldFile or a dupe
		File searchFile = newFile;
		do {
			if (oldFile.equals(searchFile)) {
        return false;
      }
			searchFile = searchFile.getParentFile();
		} while (searchFile != null);

		if (oldFile.isDirectory()) {
			/*
			 * Copy a directory
			 */
			if (this.progressDialog != null) {
				this.progressDialog.setDetailFile(oldFile, ProgressDialog.COPY);
			}
			if (this.simulateOnly) {
				//System.out.println(getResourceString("simulate.DirectoriesCreated.text",
				//	new Object[] { newFile.getPath() }));
			} else {
				if (! newFile.mkdirs()) {
          return false;
        }
			}
			final File[] subFiles = oldFile.listFiles();
			if (subFiles != null) {
				if (this.progressDialog != null) {
					this.progressDialog.addWorkUnits(subFiles.length);
				}
				for (final File subFile : subFiles) {
					final File oldSubFile = subFile;
					final File newSubFile = new File(newFile, oldSubFile.getName());
					if (! this.copyFileStructure(oldSubFile, newSubFile)) {
            return false;
          }
					if (this.progressDialog != null) {
						this.progressDialog.addProgress(1);
						if (this.progressDialog.isCancelled()) {
              return false;
            }
					}
				}
			}
		} else /*
     * Copy a file
     */
    if (this.simulateOnly) {
    	//System.out.println(getResourceString("simulate.CopyFromTo.text",
    	//	new Object[] { oldFile.getPath(), newFile.getPath() }));
    } else {
    	try (FileReader in = new FileReader(oldFile);
    			FileWriter out = new FileWriter(newFile);){
    		int count;
    		while ((count = in.read()) != -1) {
          out.write(count);
        }
    	} catch (final IOException e) {
    		return false;
    	}
    }
		return true;
	}

	/**
	 * Deletes a file or entire directory structure.
	 *
	 * @param oldFile the location of the old file or directory
	 * @return true iff the operation succeeds without errors
	 */
	boolean deleteFileStructure(final File oldFile) {
		if (oldFile == null) {
      return false;
    }
		if (oldFile.isDirectory()) {
			/*
			 * Delete a directory
			 */
			if (this.progressDialog != null) {
				this.progressDialog.setDetailFile(oldFile, ProgressDialog.DELETE);
			}
			final File[] subFiles = oldFile.listFiles();
			if (subFiles != null) {
				if (this.progressDialog != null) {
					this.progressDialog.addWorkUnits(subFiles.length);
				}
				for (final File subFile : subFiles) {
					final File oldSubFile = subFile;
					if (! this.deleteFileStructure(oldSubFile)) {
            return false;
          }
					if (this.progressDialog != null) {
						this.progressDialog.addProgress(1);
						if (this.progressDialog.isCancelled()) {
              return false;
            }
					}
				}
			}
		}
		if (this.simulateOnly) {
			//System.out.println(getResourceString("simulate.Delete.text",
			//	new Object[] { oldFile.getPath(), oldFile.getPath() }));
			return true;
		}
		return oldFile.delete();
	}

	/**
	 * Sorts files lexicographically by name.
	 *
	 * @param files the array of Files to be sorted
	 */
	static void sortFiles(final File[] files) {
		/* Very lazy merge sort algorithm */
		sortBlock(files, 0, files.length - 1, new File[files.length]);
	}
	private static void sortBlock(final File[] files, final int start, final int end, final File[] mergeTemp) {
		final int length = (end - start) + 1;
		if (length < 8) {
			for (int i = end; i > start; --i) {
				for (int j = end; j > start; --j)  {
					if (compareFiles(files[j - 1], files[j]) > 0) {
						final File temp = files[j];
						files[j] = files[j-1];
						files[j-1] = temp;
					}
				}
			}
			return;
		}
		final int mid = (start + end) / 2;
		sortBlock(files, start, mid, mergeTemp);
		sortBlock(files, mid + 1, end, mergeTemp);
		int x = start;
		int y = mid + 1;
		for (int i = 0; i < length; ++i) {
			if ((x > mid) || ((y <= end) && (compareFiles(files[x], files[y]) > 0))) {
				mergeTemp[i] = files[y++];
			} else {
				mergeTemp[i] = files[x++];
			}
		}
		for (int i = 0; i < length; ++i) {
      files[i + start] = mergeTemp[i];
    }
	}
	private static int compareFiles(final File a, final File b) {
//		boolean aIsDir = a.isDirectory();
//		boolean bIsDir = b.isDirectory();
//		if (aIsDir && ! bIsDir) return -1;
//		if (bIsDir && ! aIsDir) return 1;

		// sort case-sensitive files in a case-insensitive manner
		int compare = a.getName().compareToIgnoreCase(b.getName());
		if (compare == 0) {
      compare = a.getName().compareTo(b.getName());
    }
		return compare;
	}

	/*
	 * This worker updates the table with file information in the background.
	 * <p>
	 * Implementation notes:
	 * <ul>
	 * <li> It is designed such that it can be interrupted cleanly.
	 * <li> It uses asyncExec() in some places to ensure that SWT Widgets are manipulated in the
	 *      right thread.  Exclusive use of syncExec() would be inappropriate as it would require a pair
	 *      of context switches between each table update operation.
	 * </ul>
	 * </p>
	 */

	/**
	 * Stops the worker and waits for it to terminate.
	 */
	void workerStop() {
		if (this.workerThread == null) {
      return;
    }
		synchronized(this.workerLock) {
			this.workerCancelled = true;
			this.workerStopped = true;
			this.workerLock.notifyAll();
		}
		while (this.workerThread != null) {
			if (! this.display.readAndDispatch()) {
        this.display.sleep();
      }
		}
	}

	/**
	 * Notifies the worker that it should update itself with new data.
	 * Cancels any previous operation and begins a new one.
	 *
	 * @param dir the new base directory for the table, null is ignored
	 * @param force if true causes a refresh even if the data is the same
	 */
	void workerUpdate(final File dir, final boolean force) {
		if ((dir == null) || ((!force) && (this.workerNextDir != null) && (this.workerNextDir.equals(dir)))) {
      return;
    }

		synchronized(this.workerLock) {
			this.workerNextDir = dir;
			this.workerStopped = false;
			this.workerCancelled = true;
			this.workerLock.notifyAll();
		}
		if (this.workerThread == null) {
			this.workerThread = new Thread(this.workerRunnable);
			this.workerThread.start();
		}
	}

	/**
	 * Manages the worker's thread
	 */
	private final Runnable workerRunnable = () -> {
		while (! this.workerStopped) {
			synchronized(this.workerLock) {
				this.workerCancelled = false;
				this.workerStateDir = this.workerNextDir;
			}
			this.workerExecute();
			synchronized(this.workerLock) {
				try {
					if ((!this.workerCancelled) && (this.workerStateDir == this.workerNextDir)) {
            this.workerLock.wait();
          }
				} catch (final InterruptedException e) {
				}
			}
		}
		this.workerThread = null;
		// wake up UI thread in case it is in a modal loop awaiting thread termination
		// (see workerStop())
		this.display.wake();
	};

	/**
	 * Updates the table's contents
	 */
	private void workerExecute() {
		File[] dirList;
		// Clear existing information
		this.display.syncExec(() -> {
			this.tableContentsOfLabel.setText(FileViewer.getResourceString("details.ContentsOf.text",
				new Object[] { this.workerStateDir.getPath() }));
			this.table.removeAll();
			this.table.setData(TABLEDATA_DIR, this.workerStateDir);
		});
		dirList = getDirectoryList(this.workerStateDir);

		for (int i = 0; (! this.workerCancelled) && (i < dirList.length); i++) {
			this.workerAddFileDetails(dirList[i]);
		}

	}

	/**
	 * Adds a file's detail information to the directory list
	 */
	private void workerAddFileDetails(final File file) {
		final String nameString = file.getName();
		final String dateString = dateFormat.format(new Date(file.lastModified()));
		final String sizeString;
		final String typeString;
		final Image iconImage;

		if (file.isDirectory()) {
			typeString = getResourceString("filetype.Folder");
			sizeString = "";
			iconImage = this.iconCache.stockImages[this.iconCache.iconClosedFolder];
		} else {
			sizeString = getResourceString("filesize.KB",
				new Object[] { Long.valueOf((file.length() + 512) / 1024) });

			final int dot = nameString.lastIndexOf('.');
			if (dot != -1) {
				final String extension = nameString.substring(dot);
				final Program program = Program.findProgram(extension);
				if (program != null) {
					typeString = program.getName();
					iconImage = this.iconCache.getIconFromProgram(program);
				} else {
					typeString = getResourceString("filetype.Unknown", new Object[] { extension.toUpperCase() });
					iconImage = this.iconCache.stockImages[this.iconCache.iconFile];
				}
			} else {
				typeString = getResourceString("filetype.None");
				iconImage = this.iconCache.stockImages[this.iconCache.iconFile];
			}
		}
		final String[] strings = new String[] { nameString, sizeString, typeString, dateString };

		this.display.syncExec(() -> {
			// guard against the shell being closed before this runs
			if (this.shell.isDisposed()) {
        return;
      }
			final TableItem tableItem = new TableItem(this.table, 0);
			tableItem.setText(strings);
			tableItem.setImage(iconImage);
			tableItem.setData(TABLEITEMDATA_FILE, file);
		});
	}

	/**
	 * Instances of this class manage a progress dialog for file operations.
	 */
	class ProgressDialog {
		public final static int COPY = 0;
		public final static int DELETE = 1;
		public final static int MOVE = 2;

		Shell shell;
		Label messageLabel, detailLabel;
		ProgressBar progressBar;
		Button cancelButton;
		boolean isCancelled = false;

		final String operationKeyName[] = {
			"Copy",
			"Delete",
			"Move"
		};

		/**
		 * Creates a progress dialog but does not open it immediately.
		 *
		 * @param parent the parent Shell
		 * @param style one of COPY, MOVE
		 */
		public ProgressDialog(final Shell parent, final int style) {
			this.shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
			final GridLayout gridLayout = new GridLayout();
			this.shell.setLayout(gridLayout);
			this.shell.setText(getResourceString("progressDialog." + this.operationKeyName[style] + ".title"));
			this.shell.addShellListener(ShellListener.shellClosedAdapter(e -> this.isCancelled = true));

			this.messageLabel = new Label(this.shell, SWT.HORIZONTAL);
			this.messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
			this.messageLabel.setText(getResourceString("progressDialog." + this.operationKeyName[style] + ".description"));

			this.progressBar = new ProgressBar(this.shell, SWT.HORIZONTAL | SWT.WRAP);
			this.progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
			this.progressBar.setMinimum(0);
			this.progressBar.setMaximum(0);

			this.detailLabel = new Label(this.shell, SWT.HORIZONTAL);
			final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
			gridData.widthHint = 400;
			this.detailLabel.setLayoutData(gridData);

			this.cancelButton = new Button(this.shell, SWT.PUSH);
			this.cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL));
			this.cancelButton.setText(getResourceString("progressDialog.cancelButton.text"));
			this.cancelButton.addSelectionListener(widgetSelectedAdapter(e -> {
				this.isCancelled = true;
				this.cancelButton.setEnabled(false);
			}));
		}
		/**
		 * Sets the detail text to show the filename along with a string
		 * representing the operation being performed on that file.
		 *
		 * @param file the file to be detailed
		 * @param operation one of COPY, DELETE
		 */
		public void setDetailFile(final File file, final int operation) {
			this.detailLabel.setText(getResourceString("progressDialog." + this.operationKeyName[operation] + ".operation",
				new Object[] { file }));
		}
		/**
		 * Returns true if the Cancel button was been clicked.
		 *
		 * @return true if the Cancel button was clicked.
		 */
		public boolean isCancelled() {
			return this.isCancelled;
		}
		/**
		 * Sets the total number of work units to be performed.
		 *
		 * @param work the total number of work units
		 */
		public void setTotalWorkUnits(final int work) {
			this.progressBar.setMaximum(work);
		}
		/**
		 * Adds to the total number of work units to be performed.
		 *
		 * @param work the number of work units to add
		 */
		public void addWorkUnits(final int work) {
			this.setTotalWorkUnits(this.progressBar.getMaximum() + work);
		}
		/**
		 * Sets the progress of completion of the total work units.
		 *
		 * @param work the total number of work units completed
		 */
		public void setProgress(final int work) {
			this.progressBar.setSelection(work);
			while (FileViewer.this.display.readAndDispatch()) {} // enable event processing
		}
		/**
		 * Adds to the progress of completion of the total work units.
		 *
		 * @param work the number of work units completed to add
		 */
		public void addProgress(final int work) {
			this.setProgress(this.progressBar.getSelection() + work);
		}
		/**
		 * Opens the dialog.
		 */
		public void open() {
			this.shell.pack();
			final Shell parentShell = (Shell) this.shell.getParent();
			final Rectangle rect = parentShell.getBounds();
			final Rectangle bounds = this.shell.getBounds();
			bounds.x = rect.x + ((rect.width - bounds.width) / 2);
			bounds.y = rect.y + ((rect.height - bounds.height) / 2);
			this.shell.setBounds(bounds);
			this.shell.open();
		}
		/**
		 * Closes the dialog and disposes its resources.
		 */
		public void close() {
			this.shell.close();
			this.shell.dispose();
			this.shell = null;
			this.messageLabel = null;
			this.detailLabel = null;
			this.progressBar = null;
			this.cancelButton = null;
		}
	}
}
