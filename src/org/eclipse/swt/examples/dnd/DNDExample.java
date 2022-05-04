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
package org.eclipse.swt.examples.dnd;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class DNDExample {

	private int dragOperation = 0;
	private Transfer[] dragTypes = new Transfer[0];
	private Control dragControl;
	private int dragControlType = 0;
	private DragSource dragSource;
	private String dragDataText;
	private String dragDataRTF;
	private String dragDataHTML;
	private String dragDataURL;
	private String[] dragDataFiles;
	private List fileList;
	private boolean dragEnabled = false;

	private int dropOperation = 0;
	private int dropFeedback = 0;
	private int dropDefaultOperation = 0;
	private Transfer[] dropTypes = new Transfer[0];
	private DropTarget dropTarget;
	private Control dropControl;
	private int dropControlType = 0;
	private Composite defaultParent;
	private boolean dropEnabled = false;

	private Text dragConsole;
	private boolean dragEventDetail = false;
	private Text dropConsole;
	private boolean dropEventDetail = false;

	private Image itemImage;

	private static final int BUTTON_TOGGLE = 0;
	private static final int BUTTON_RADIO = 1;
	private static final int BUTTON_CHECK = 2;
	private static final int CANVAS = 3;
	private static final int LABEL = 4;
	private static final int LIST = 5;
	private static final int TABLE = 6;
	private static final int TREE = 7;
	private static final int TEXT = 8;
	private static final int STYLED_TEXT = 9;
	private static final int COMBO = 10;

public static void main(final String[] args) {
	final Display display = new Display();
	final DNDExample example = new DNDExample();
	example.open(display);
	display.dispose();
}

private void addDragTransfer(final Transfer transfer){
	final Transfer[] newTypes = new Transfer[this.dragTypes.length + 1];
	System.arraycopy(this.dragTypes, 0, newTypes, 0, this.dragTypes.length);
	newTypes[this.dragTypes.length] = transfer;
	this.dragTypes = newTypes;
	if (this.dragSource != null) {
		this.dragSource.setTransfer(this.dragTypes);
	}
}

private void addDropTransfer(final Transfer transfer){
	final Transfer[] newTypes = new Transfer[this.dropTypes.length + 1];
	System.arraycopy(this.dropTypes, 0, newTypes, 0, this.dropTypes.length);
	newTypes[this.dropTypes.length] = transfer;
	this.dropTypes = newTypes;
	if (this.dropTarget != null) {
		this.dropTarget.setTransfer(this.dropTypes);
	}
}

private void createDragOperations(final Composite parent) {
	parent.setLayout(new RowLayout(SWT.VERTICAL));
	final Button moveButton = new Button(parent, SWT.CHECK);
	moveButton.setText("DND.DROP_MOVE");
	moveButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b = (Button) e.widget;
		if (b.getSelection()) {
			this.dragOperation |= DND.DROP_MOVE;
		} else {
			this.dragOperation = this.dragOperation & ~DND.DROP_MOVE;
			if (this.dragOperation == 0) {
				this.dragOperation = DND.DROP_MOVE;
				moveButton.setSelection(true);
			}
		}
		if (this.dragEnabled) {
			this.createDragSource();
		}
	}));


	final Button copyButton = new Button(parent, SWT.CHECK);
	copyButton.setText("DND.DROP_COPY");
	copyButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b = (Button) e.widget;
		if (b.getSelection()) {
			this.dragOperation |= DND.DROP_COPY;
		} else {
			this.dragOperation = this.dragOperation & ~DND.DROP_COPY;
			if (this.dragOperation == 0) {
				this.dragOperation = DND.DROP_MOVE;
				moveButton.setSelection(true);
			}
		}
		if (this.dragEnabled) {
			this.createDragSource();
		}
	}));

	final Button linkButton = new Button(parent, SWT.CHECK);
	linkButton.setText("DND.DROP_LINK");
	linkButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b = (Button) e.widget;
		if (b.getSelection()) {
			this.dragOperation |= DND.DROP_LINK;
		} else {
			this.dragOperation = this.dragOperation & ~DND.DROP_LINK;
			if (this.dragOperation == 0) {
				this.dragOperation = DND.DROP_MOVE;
				moveButton.setSelection(true);
			}
		}
		if (this.dragEnabled) {
			this.createDragSource();
		}
	}));

	//initialize state
	moveButton.setSelection(true);
	copyButton.setSelection(true);
	linkButton.setSelection(true);
	this.dragOperation |= DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
}

private void createDragSource() {
	if (this.dragSource != null) {
    this.dragSource.dispose();
  }
	this.dragSource = new DragSource(this.dragControl, this.dragOperation);
	this.dragSource.setTransfer(this.dragTypes);
	this.dragSource.addDragListener(new DragSourceListener() {
		@Override
		public void dragFinished(final org.eclipse.swt.dnd.DragSourceEvent event) {
			DNDExample.this.dragConsole.append(">>dragFinished\n");
			DNDExample.this.printEvent(event);
			DNDExample.this.dragDataText = DNDExample.this.dragDataRTF = DNDExample.this.dragDataHTML = DNDExample.this.dragDataURL = null;
			DNDExample.this.dragDataFiles = null;
			if (event.detail == DND.DROP_MOVE) {
				switch(DNDExample.this.dragControlType) {
					case BUTTON_CHECK:
					case BUTTON_TOGGLE:
					case BUTTON_RADIO: {
						final Button b = (Button)DNDExample.this.dragControl;
						b.setText("");
						break;
					}
					case STYLED_TEXT: {
						final StyledText text = (StyledText)DNDExample.this.dragControl;
						text.insert("");
						break;
					}
					case TABLE: {
						final Table table = (Table)DNDExample.this.dragControl;
						final TableItem[] items = table.getSelection();
						for (final TableItem item : items) {
							item.dispose();
						}
						break;
					}
					case TEXT: {
						final Text text = (Text)DNDExample.this.dragControl;
						text.insert("");
						break;
					}
					case TREE: {
						final Tree tree = (Tree)DNDExample.this.dragControl;
						final TreeItem[] items = tree.getSelection();
						for (final TreeItem item : items) {
							item.dispose();
						}
						break;
					}
					case CANVAS: {
						DNDExample.this.dragControl.setData("STRINGS", null);
						DNDExample.this.dragControl.redraw();
						break;
					}
					case LABEL: {
						final Label label = (Label)DNDExample.this.dragControl;
						label.setText("");
						break;
					}
					case LIST: {
						final List list = (List)DNDExample.this.dragControl;
						final int[] indices = list.getSelectionIndices();
						list.remove(indices);
						break;
					}
					case COMBO:{
						final Combo combo = (Combo)DNDExample.this.dragControl;
						combo.setText("");
						break;
					}
				}
			}
		}
		@Override
		public void dragSetData(final org.eclipse.swt.dnd.DragSourceEvent event) {
			DNDExample.this.dragConsole.append(">>dragSetData\n");
			DNDExample.this.printEvent(event);
			if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = DNDExample.this.dragDataText;
			}
			if (RTFTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = DNDExample.this.dragDataRTF;
			}
			if (HTMLTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = DNDExample.this.dragDataHTML;
			}
			if (URLTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = DNDExample.this.dragDataURL;
			}
			if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = DNDExample.this.dragDataFiles;
			}
		}
		@Override
		public void dragStart(final org.eclipse.swt.dnd.DragSourceEvent event) {
			DNDExample.this.dragConsole.append(">>dragStart\n");
			DNDExample.this.printEvent(event);
			DNDExample.this.dragDataFiles = DNDExample.this.fileList.getItems();
			switch(DNDExample.this.dragControlType) {
				case BUTTON_CHECK:
				case BUTTON_TOGGLE:
				case BUTTON_RADIO: {
					final Button b = (Button)DNDExample.this.dragControl;
					DNDExample.this.dragDataText = b.getSelection() ? "true" : "false";
					break;
				}
				case STYLED_TEXT: {
					final StyledText text = (StyledText)DNDExample.this.dragControl;
					final String s = text.getSelectionText();
					if (s.length() == 0) {
						event.doit = false;
					} else {
						DNDExample.this.dragDataText = s;
					}
					break;
				}
				case TABLE: {
					final Table table = (Table)DNDExample.this.dragControl;
					final TableItem[] items = table.getSelection();
					if (items.length == 0) {
						event.doit = false;
					} else {
						final StringBuilder buffer = new StringBuilder();
						for (int i = 0; i < items.length; i++) {
							buffer.append(items[i].getText());
							if ((items.length > 1) && (i < (items.length - 1))) {
								buffer.append("\n");
							}
						}
						DNDExample.this.dragDataText = buffer.toString();
					}
					break;
				}
				case TEXT: {
					final Text text = (Text)DNDExample.this.dragControl;
					final String s = text.getSelectionText();
					if (s.length() == 0) {
						event.doit = false;
					} else {
						DNDExample.this.dragDataText = s;
					}
					break;
				}
				case TREE: {
					final Tree tree = (Tree)DNDExample.this.dragControl;
					final TreeItem[] items = tree.getSelection();
					if (items.length == 0) {
						event.doit = false;
					} else {
						final StringBuilder buffer = new StringBuilder();
						for (int i = 0; i < items.length; i++) {
							buffer.append(items[i].getText());
							if ((items.length > 1) && (i < (items.length - 1))) {
								buffer.append("\n");
							}
						}
						DNDExample.this.dragDataText = buffer.toString();
					}
					break;
				}
				case CANVAS: {
					final String[] strings = (String[])DNDExample.this.dragControl.getData("STRINGS");
					if ((strings == null) || (strings.length == 0)) {
						event.doit = false;
					} else {
						final StringBuilder buffer = new StringBuilder();
						for (int i = 0; i < strings.length; i++) {
							buffer.append(strings[i]);
							if ((strings.length > 1) && (i < (strings.length - 1))) {
								buffer.append("\n");
							}
						}
						DNDExample.this.dragDataText = buffer.toString();
					}
					break;
				}
				case LABEL: {
					final Label label = (Label)DNDExample.this.dragControl;
					final String string = label.getText();
					if (string.length() == 0) {
						event.doit = false;
					} else {
						DNDExample.this.dragDataText = string;
					}
					break;
				}
				case LIST: {
					final List list = (List)DNDExample.this.dragControl;
					final String[] selection = list.getSelection();
					if (selection.length == 0) {
						event.doit = false;
					} else {
						final StringBuilder buffer = new StringBuilder();
						for (int i = 0; i < selection.length; i++) {
							buffer.append(selection[i]);
							if ((selection.length > 1) && (i < (selection.length - 1))) {
								buffer.append("\n");
							}
						}
						DNDExample.this.dragDataText = buffer.toString();
					}
					break;
				}
				case COMBO: {
					final Combo combo = (Combo) DNDExample.this.dragControl;
					final String string = combo.getText();
					final Point selection = combo.getSelection();
					if (selection.x == selection.y) {
						event.doit = false;
					} else {
						DNDExample.this.dragDataText = string.substring(selection.x, selection.y);
					}
					break;
				}
				default:
					throw new SWTError(SWT.ERROR_NOT_IMPLEMENTED);
			}
			if (DNDExample.this.dragDataText != null) {
				DNDExample.this.dragDataRTF = "{\\rtf1{\\colortbl;\\red255\\green0\\blue0;}\\cf1\\b "+DNDExample.this.dragDataText+"}";
				DNDExample.this.dragDataHTML = "<b>"+DNDExample.this.dragDataText+"</b>";
				DNDExample.this.dragDataURL = "http://" + DNDExample.this.dragDataText.replace(' ', '.');
				try {
					new URL(DNDExample.this.dragDataURL);
				} catch (final MalformedURLException e) {
					DNDExample.this.dragDataURL = null;
				}
			}

			for (final Transfer dragType : DNDExample.this.dragTypes) {
				if ((dragType instanceof TextTransfer) && (DNDExample.this.dragDataText == null)) {
					event.doit = false;
				}
				if ((dragType instanceof RTFTransfer) && (DNDExample.this.dragDataRTF == null)) {
					event.doit = false;
				}
				if ((dragType instanceof HTMLTransfer) && (DNDExample.this.dragDataHTML == null)) {
					event.doit = false;
				}
				if ((dragType instanceof URLTransfer) && (DNDExample.this.dragDataURL == null)) {
					event.doit = false;
				}
				if ((dragType instanceof FileTransfer) && ((DNDExample.this.dragDataFiles == null) || (DNDExample.this.dragDataFiles.length == 0))) {
					event.doit = false;
				}
			}
		}
	});
}

private void createDragTypes(final Composite parent) {
	parent.setLayout(new GridLayout());
	final Button textButton = new Button(parent, SWT.CHECK);
	textButton.setText("Text Transfer");
	textButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b = (Button) e.widget;
		if (b.getSelection()) {
			this.addDragTransfer(TextTransfer.getInstance());
		} else {
			this.removeDragTransfer(TextTransfer.getInstance());
		}
	}));

	Button b = new Button(parent, SWT.CHECK);
	b.setText("RTF Transfer");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b1 = (Button) e.widget;
		if (b1.getSelection()) {
			this.addDragTransfer(RTFTransfer.getInstance());
		} else {
			this.removeDragTransfer(RTFTransfer.getInstance());
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("HTML Transfer");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b2 = (Button) e.widget;
		if (b2.getSelection()) {
			this.addDragTransfer(HTMLTransfer.getInstance());
		} else {
			this.removeDragTransfer(HTMLTransfer.getInstance());
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("URL Transfer");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b3 = (Button) e.widget;
		if (b3.getSelection()) {
			this.addDragTransfer(URLTransfer.getInstance());
		} else {
			this.removeDragTransfer(URLTransfer.getInstance());
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("File Transfer");
	b.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b4 = (Button) e.widget;
		if (b4.getSelection()) {
			this.addDragTransfer(FileTransfer.getInstance());
		} else {
			this.removeDragTransfer(FileTransfer.getInstance());
		}
	}));
	b = new Button(parent, SWT.PUSH);
	b.setText("Select File(s)");
	b.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final FileDialog dialog = new FileDialog(this.fileList.getShell(), SWT.OPEN | SWT.MULTI);
		final String result = dialog.open();
		if ((result != null) && (result.length() > 0)) {
			this.fileList.removeAll();
			final String path = dialog.getFilterPath();
			final String[] names = dialog.getFileNames();
			for (final String name : names) {
				this.fileList.add(path + File.separatorChar + name);
			}
		}
	}));
	this.fileList = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	final GridData data = new GridData();
	data.grabExcessHorizontalSpace = true;
	data.horizontalAlignment = GridData.FILL;
	data.verticalAlignment = GridData.BEGINNING;
	this.fileList.setLayoutData(data);

	// initialize state
	textButton.setSelection(true);
	this.addDragTransfer(TextTransfer.getInstance());
}

private void createDragWidget(final Composite parent) {
	parent.setLayout(new FormLayout());
	final Combo combo = new Combo(parent, SWT.READ_ONLY);
	combo.setItems("Toggle Button", "Radio Button", "Checkbox", "Canvas", "Label", "List", "Table", "Tree", "Text", "StyledText", "Combo");
	combo.select(LABEL);
	this.dragControlType = combo.getSelectionIndex();
	this.dragControl = this.createWidget(this.dragControlType, parent, "Drag Source");

	combo.addSelectionListener(widgetSelectedAdapter(e -> {
		final Object data = this.dragControl.getLayoutData();
		final Composite dragParent = this.dragControl.getParent();
		this.dragControl.dispose();
		final Combo c = (Combo) e.widget;
		this.dragControlType = c.getSelectionIndex();
		this.dragControl = this.createWidget(this.dragControlType, dragParent, "Drag Source");
		this.dragControl.setLayoutData(data);
		if (this.dragEnabled) {
      this.createDragSource();
    }
		dragParent.layout();
	}));

	final Button b = new Button(parent, SWT.CHECK);
	b.setText("DragSource");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b1 = (Button) e.widget;
		this.dragEnabled = b1.getSelection();
		if (this.dragEnabled) {
			this.createDragSource();
		} else {
			if (this.dragSource != null) {
				this.dragSource.dispose();
			}
			this.dragSource = null;
		}
	}));
	b.setSelection(true);
	this.dragEnabled = true;

	FormData data = new FormData();
	data.top = new FormAttachment(0, 10);
	data.bottom = new FormAttachment(combo, -10);
	data.left = new FormAttachment(0, 10);
	data.right = new FormAttachment(100, -10);
	this.dragControl.setLayoutData(data);

	data = new FormData();
	data.bottom = new FormAttachment(100, -10);
	data.left = new FormAttachment(0, 10);
	combo.setLayoutData(data);

	data = new FormData();
	data.bottom = new FormAttachment(100, -10);
	data.left = new FormAttachment(combo, 10);
	b.setLayoutData(data);
}

private void createDropOperations(final Composite parent) {
	parent.setLayout(new RowLayout(SWT.VERTICAL));
	final Button moveButton = new Button(parent, SWT.CHECK);
	moveButton.setText("DND.DROP_MOVE");
	moveButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b = (Button) e.widget;
		if (b.getSelection()) {
			this.dropOperation |= DND.DROP_MOVE;
		} else {
			this.dropOperation = this.dropOperation & ~DND.DROP_MOVE;
			if ((this.dropOperation == 0) || ((this.dropDefaultOperation & DND.DROP_MOVE) != 0)) {
				this.dropOperation |= DND.DROP_MOVE;
				moveButton.setSelection(true);
			}
		}
		if (this.dropEnabled) {
			this.createDropTarget();
		}
	}));


	final Button copyButton = new Button(parent, SWT.CHECK);
	copyButton.setText("DND.DROP_COPY");
	copyButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b = (Button) e.widget;
		if (b.getSelection()) {
			this.dropOperation |= DND.DROP_COPY;
		} else {
			this.dropOperation = this.dropOperation & ~DND.DROP_COPY;
			if ((this.dropOperation == 0) || ((this.dropDefaultOperation & DND.DROP_COPY) != 0)) {
				this.dropOperation = DND.DROP_COPY;
				copyButton.setSelection(true);
			}
		}
		if (this.dropEnabled) {
			this.createDropTarget();
		}
	}));

	final Button linkButton = new Button(parent, SWT.CHECK);
	linkButton.setText("DND.DROP_LINK");
	linkButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropOperation |= DND.DROP_LINK;
		} else {
			this.dropOperation = this.dropOperation & ~DND.DROP_LINK;
			if ((this.dropOperation == 0) || ((this.dropDefaultOperation & DND.DROP_LINK) != 0)) {
				this.dropOperation = DND.DROP_LINK;
				linkButton.setSelection(true);
			}
		}
		if (this.dropEnabled) {
			this.createDropTarget();
		}
	}));

	Button b = new Button(parent, SWT.CHECK);
	b.setText("DND.DROP_DEFAULT");
	this.defaultParent = new Composite(parent, SWT.NONE);
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropOperation |= DND.DROP_DEFAULT;
			this.defaultParent.setVisible(true);
		} else {
			this.dropOperation = this.dropOperation & ~DND.DROP_DEFAULT;
			this.defaultParent.setVisible(false);
		}
		if (this.dropEnabled) {
			this.createDropTarget();
		}
	}));

	this.defaultParent.setVisible(false);
	final GridLayout layout = new GridLayout();
	layout.marginWidth = 20;
	this.defaultParent.setLayout(layout);
	final Label label = new Label(this.defaultParent, SWT.NONE);
	label.setText("Value for default operation is:");
	b = new Button(this.defaultParent, SWT.RADIO);
	b.setText("DND.DROP_MOVE");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropDefaultOperation = DND.DROP_MOVE;
			this.dropOperation |= DND.DROP_MOVE;
			moveButton.setSelection(true);
			if (this.dropEnabled) {
				this.createDropTarget();
			}
		}
	}));

	b = new Button(this.defaultParent, SWT.RADIO);
	b.setText("DND.DROP_COPY");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropDefaultOperation = DND.DROP_COPY;
			this.dropOperation |= DND.DROP_COPY;
			copyButton.setSelection(true);
			if (this.dropEnabled) {
				this.createDropTarget();
			}
		}
	}));

	b = new Button(this.defaultParent, SWT.RADIO);
	b.setText("DND.DROP_LINK");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropDefaultOperation = DND.DROP_LINK;
			this.dropOperation |= DND.DROP_LINK;
			linkButton.setSelection(true);
			if (this.dropEnabled) {
				this.createDropTarget();
			}
		}
	}));

	b = new Button(this.defaultParent, SWT.RADIO);
	b.setText("DND.DROP_NONE");
	b.setSelection(true);
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropDefaultOperation = DND.DROP_NONE;
			this.dropOperation &= ~DND.DROP_DEFAULT;
			if (this.dropEnabled) {
				this.createDropTarget();
			}
		}
	}));

	// initialize state
	moveButton.setSelection(true);
	copyButton.setSelection(true);
	linkButton.setSelection(true);
	this.dropOperation = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
}

private void createDropTarget() {
	if (this.dropTarget != null) {
    this.dropTarget.dispose();
  }
	this.dropTarget = new DropTarget(this.dropControl, this.dropOperation);
	this.dropTarget.setTransfer(this.dropTypes);
	this.dropTarget.addDropListener(new DropTargetListener() {
		@Override
		public void dragEnter(final DropTargetEvent event) {
			DNDExample.this.dropConsole.append(">>dragEnter\n");
			DNDExample.this.printEvent(event);
			if (event.detail == DND.DROP_DEFAULT) {
				event.detail = DNDExample.this.dropDefaultOperation;
			}
			event.feedback = DNDExample.this.dropFeedback;
		}
		@Override
		public void dragLeave(final DropTargetEvent event) {
			DNDExample.this.dropConsole.append(">>dragLeave\n");
			DNDExample.this.printEvent(event);
		}
		@Override
		public void dragOperationChanged(final DropTargetEvent event) {
			DNDExample.this.dropConsole.append(">>dragOperationChanged\n");
			DNDExample.this.printEvent(event);
			if (event.detail == DND.DROP_DEFAULT) {
				event.detail = DNDExample.this.dropDefaultOperation;
			}
			event.feedback = DNDExample.this.dropFeedback;
		}
		@Override
		public void dragOver(final DropTargetEvent event) {
			DNDExample.this.dropConsole.append(">>dragOver\n");
			DNDExample.this.printEvent(event);
			event.feedback = DNDExample.this.dropFeedback;
		}
		@Override
		public void drop(final DropTargetEvent event) {
			DNDExample.this.dropConsole.append(">>drop\n");
			DNDExample.this.printEvent(event);
			String[] strings = null;
			if (TextTransfer.getInstance().isSupportedType(event.currentDataType) ||
				RTFTransfer.getInstance().isSupportedType(event.currentDataType) ||
				HTMLTransfer.getInstance().isSupportedType(event.currentDataType) ||
				URLTransfer.getInstance().isSupportedType(event.currentDataType)) {
				strings = new String[] {(String)event.data};
			}
			if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
				strings = (String[])event.data;
			}
			if ((strings == null) || (strings.length == 0)) {
				DNDExample.this.dropConsole.append("!!Invalid data dropped");
				return;
			}

			if ((strings.length == 1) && ((DNDExample.this.dropControlType == TABLE) ||
										(DNDExample.this.dropControlType == TREE) ||
										(DNDExample.this.dropControlType == LIST))) {
				// convert string separated by "\n" into an array of strings
				final String string = strings[0];
				int count = 0;
				int offset = string.indexOf("\n", 0);
				while (offset > 0) {
					count++;
					offset = string.indexOf("\n", offset + 1);
				}
				if (count > 0) {
					strings = new String[count + 1];
					int start = 0;
					int end = string.indexOf("\n");
					int index = 0;
					while (start < end) {
						strings[index++] = string.substring(start, end);
						start = end + 1;
						end = string.indexOf("\n", start);
						if (end == -1) {
              end = string.length();
            }
					}
				}
			}
			switch(DNDExample.this.dropControlType) {
				case BUTTON_CHECK:
				case BUTTON_TOGGLE:
				case BUTTON_RADIO: {
					final Button b = (Button)DNDExample.this.dropControl;
					b.setText(strings[0]);
					break;
				}
				case STYLED_TEXT: {
					final StyledText text = (StyledText)DNDExample.this.dropControl;
					for (final String string : strings) {
						text.insert(string);
					}
					break;
				}
				case TABLE: {
					final Table table = (Table)DNDExample.this.dropControl;
					final Point p = event.display.map(null, table, event.x, event.y);
					final TableItem dropItem = table.getItem(p);
					final int index = dropItem == null ? table.getItemCount() : table.indexOf(dropItem);
					for (final String string : strings) {
						final TableItem item = new TableItem(table, SWT.NONE, index);
						item.setText(0, string);
						item.setText(1, "dropped item");
					}
					final TableColumn[] columns = table.getColumns();
					for (final TableColumn column : columns) {
						column.pack();
					}
					break;
				}
				case TEXT: {
					final Text text = (Text)DNDExample.this.dropControl;
					for (final String string : strings) {
						text.append(string+"\n");
					}
					break;
				}
				case TREE: {
					final Tree tree = (Tree)DNDExample.this.dropControl;
					final Point p = event.display.map(null, tree, event.x, event.y);
					final TreeItem parentItem = tree.getItem(p);
					for (final String string : strings) {
						final TreeItem item = parentItem != null ? new TreeItem(parentItem, SWT.NONE) : new TreeItem(tree, SWT.NONE);
						item.setText(string);
					}
					break;
				}
				case CANVAS: {
					DNDExample.this.dropControl.setData("STRINGS", strings);
					DNDExample.this.dropControl.redraw();
					break;
				}
				case LABEL: {
					final Label label = (Label)DNDExample.this.dropControl;
					label.setText(strings[0]);
					break;
				}
				case LIST: {
					final List list = (List)DNDExample.this.dropControl;
					for (final String string : strings) {
						list.add(string);
					}
					break;
				}
				case COMBO: {
					final Combo combo = (Combo)DNDExample.this.dropControl;
					combo.setText(strings[0]);
					break;
				}
				default:
					throw new SWTError(SWT.ERROR_NOT_IMPLEMENTED);
			}
		}
		@Override
		public void dropAccept(final DropTargetEvent event) {
			DNDExample.this.dropConsole.append(">>dropAccept\n");
			DNDExample.this.printEvent(event);
		}
	});
}

private void createFeedbackTypes(final Group parent) {
	parent.setLayout(new RowLayout(SWT.VERTICAL));
	Button b = new Button(parent, SWT.CHECK);
	b.setText("FEEDBACK_SELECT");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropFeedback |= DND.FEEDBACK_SELECT;
		} else {
			this.dropFeedback &= ~DND.FEEDBACK_SELECT;
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("FEEDBACK_SCROLL");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropFeedback |= DND.FEEDBACK_SCROLL;
		} else {
			this.dropFeedback &= ~DND.FEEDBACK_SCROLL;
		}
	}));


	b = new Button(parent, SWT.CHECK);
	b.setText("FEEDBACK_INSERT_BEFORE");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropFeedback |= DND.FEEDBACK_INSERT_BEFORE;
		} else {
			this.dropFeedback &= ~DND.FEEDBACK_INSERT_BEFORE;
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("FEEDBACK_INSERT_AFTER");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropFeedback |= DND.FEEDBACK_INSERT_AFTER;
		} else {
			this.dropFeedback &= ~DND.FEEDBACK_INSERT_AFTER;
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("FEEDBACK_EXPAND");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.dropFeedback |= DND.FEEDBACK_EXPAND;
		} else {
			this.dropFeedback &= ~DND.FEEDBACK_EXPAND;
		}
	}));
}

private void createDropTypes(final Composite parent) {
	parent.setLayout(new RowLayout(SWT.VERTICAL));
	final Button textButton = new Button(parent, SWT.CHECK);
	textButton.setText("Text Transfer");
	textButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button b = (Button) e.widget;
		if (b.getSelection()) {
			this.addDropTransfer(TextTransfer.getInstance());
		} else {
			this.removeDropTransfer(TextTransfer.getInstance());
		}
	}));

	Button b = new Button(parent, SWT.CHECK);
	b.setText("RTF Transfer");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.addDropTransfer(RTFTransfer.getInstance());
		} else {
			this.removeDropTransfer(RTFTransfer.getInstance());
		}
	}));


	b = new Button(parent, SWT.CHECK);
	b.setText("HTML Transfer");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.addDropTransfer(HTMLTransfer.getInstance());
		} else {
			this.removeDropTransfer(HTMLTransfer.getInstance());
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("URL Transfer");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.addDropTransfer(URLTransfer.getInstance());
		} else {
			this.removeDropTransfer(URLTransfer.getInstance());
		}
	}));

	b = new Button(parent, SWT.CHECK);
	b.setText("File Transfer");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		if (eb.getSelection()) {
			this.addDropTransfer(FileTransfer.getInstance());
		} else {
			this.removeDropTransfer(FileTransfer.getInstance());
		}
	}));

	// initialize state
	textButton.setSelection(true);
	this.addDropTransfer(TextTransfer.getInstance());
}

private void createDropWidget(final Composite parent) {
	parent.setLayout(new FormLayout());
	final Combo combo = new Combo(parent, SWT.READ_ONLY);
	combo.setItems("Toggle Button", "Radio Button", "Checkbox", "Canvas", "Label", "List", "Table", "Tree", "Text", "StyledText", "Combo");
	combo.select(LABEL);
	this.dropControlType = combo.getSelectionIndex();
	this.dropControl = this.createWidget(this.dropControlType, parent, "Drop Target");
	combo.addSelectionListener(widgetSelectedAdapter(e -> {
		final Object data = this.dropControl.getLayoutData();
		final Composite dropParent = this.dropControl.getParent();
		this.dropControl.dispose();
		final Combo c = (Combo) e.widget;
		this.dropControlType = c.getSelectionIndex();
		this.dropControl = this.createWidget(this.dropControlType, dropParent, "Drop Target");
		this.dropControl.setLayoutData(data);
		if (this.dropEnabled) {
      this.createDropTarget();
    }
		dropParent.layout();
	}));

	final Button b = new Button(parent, SWT.CHECK);
	b.setText("DropTarget");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final Button eb = (Button) e.widget;
		this.dropEnabled = eb.getSelection();
		if (this.dropEnabled) {
			this.createDropTarget();
		} else {
			if (this.dropTarget != null) {
				this.dropTarget.dispose();
			}
			this.dropTarget = null;
		}
	}));
	// initialize state
	b.setSelection(true);
	this.dropEnabled = true;

	FormData data = new FormData();
	data.top = new FormAttachment(0, 10);
	data.bottom = new FormAttachment(combo, -10);
	data.left = new FormAttachment(0, 10);
	data.right = new FormAttachment(100, -10);
	this.dropControl.setLayoutData(data);

	data = new FormData();
	data.bottom = new FormAttachment(100, -10);
	data.left = new FormAttachment(0, 10);
	combo.setLayoutData(data);

	data = new FormData();
	data.bottom = new FormAttachment(100, -10);
	data.left = new FormAttachment(combo, 10);
	b.setLayoutData(data);
}

private Control createWidget(final int type, final Composite parent, final String prefix){
	switch (type) {
		case BUTTON_CHECK: {
			final Button button = new Button(parent, SWT.CHECK);
			button.setText(prefix+" Check box");
			return button;
		}
		case BUTTON_TOGGLE: {
			final Button button = new Button(parent, SWT.TOGGLE);
			button.setText(prefix+" Toggle button");
			return button;
		}
		case BUTTON_RADIO: {
			final Button button = new Button(parent, SWT.RADIO);
			button.setText(prefix+" Radio button");
			return button;
		}
		case STYLED_TEXT: {
			final StyledText text = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			text.setText(prefix+" Styled Text");
			return text;
		}
		case TABLE: {
			final Table table = new Table(parent, SWT.BORDER | SWT.MULTI);
			table.setHeaderVisible(true);
			final TableColumn column0 = new TableColumn(table, SWT.LEFT);
			column0.setText("Name");
			final TableColumn column1 = new TableColumn(table, SWT.RIGHT);
			column1.setText("Value");
			final TableColumn column2 = new TableColumn(table, SWT.CENTER);
			column2.setText("Description");
			for (int i = 0; i < 10; i++) {
				final TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, prefix+" name "+i);
				item.setText(1, prefix+" value "+i);
				item.setText(2, prefix+" description "+i);
				item.setImage(this.itemImage);
			}
			column0.pack();
			column1.pack();
			column2.pack();
			return table;
		}
		case TEXT: {
			final Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			text.setText(prefix+" Text");
			return text;
		}
		case TREE: {
			final Tree tree = new Tree(parent, SWT.BORDER | SWT.MULTI);
			tree.setHeaderVisible(true);
			final TreeColumn column0 = new TreeColumn(tree, SWT.LEFT);
			column0.setText("Name");
			final TreeColumn column1 = new TreeColumn(tree, SWT.RIGHT);
			column1.setText("Value");
			final TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
			column2.setText("Description");
			for (int i = 0; i < 3; i++) {
				final TreeItem item = new TreeItem(tree, SWT.NONE);
				item.setText(0, prefix+" name "+i);
				item.setText(1, prefix+" value "+i);
				item.setText(2, prefix+" description "+i);
				item.setImage(this.itemImage);
				for (int j = 0; j < 3; j++) {
					final TreeItem subItem = new TreeItem(item, SWT.NONE);
					subItem.setText(0, prefix+" name "+i+" "+j);
					subItem.setText(1, prefix+" value "+i+" "+j);
					subItem.setText(2, prefix+" description "+i+" "+j);
					subItem.setImage(this.itemImage);
					for (int k = 0; k < 3; k++) {
						final TreeItem subsubItem = new TreeItem(subItem, SWT.NONE);
						subsubItem.setText(0, prefix+" name "+i+" "+j+" "+k);
						subsubItem.setText(1, prefix+" value "+i+" "+j+" "+k);
						subsubItem.setText(2, prefix+" description "+i+" "+j+" "+k);
						subsubItem.setImage(this.itemImage);
					}
				}
			}
			column0.pack();
			column1.pack();
			column2.pack();
			return tree;
		}
		case CANVAS: {
			final Canvas canvas = new Canvas(parent, SWT.BORDER);
			canvas.setData("STRINGS", new String[] {prefix+" Canvas widget"});
			canvas.addPaintListener(e -> {
				final Canvas c = (Canvas)e.widget;
				final Image image = (Image)c.getData("IMAGE");
				if (image != null) {
					e.gc.drawImage(image, 5, 5);
				} else {
					final String[] strings = (String[])c.getData("STRINGS");
					if (strings != null) {
						final FontMetrics metrics = e.gc.getFontMetrics();
						final int height = metrics.getHeight();
						int y = 5;
						for (final String string : strings) {
							e.gc.drawString(string, 5, y);
							y += height + 5;
						}
					}
				}
			});
			return canvas;
		}
		case LABEL: {
			final Label label = new Label(parent, SWT.BORDER);
			label.setText(prefix+" Label");
			return label;
		}
		case LIST: {
			final List list = new List(parent, SWT.BORDER|SWT.MULTI);
			list.setItems(prefix+" Item a", prefix+" Item b",  prefix+" Item c",  prefix+" Item d");
			return list;
		}
		case COMBO:{
			final Combo combo = new Combo(parent, SWT.BORDER);
			combo.setItems("Item a", "Item b", "Item c", "Item d");
			return combo;
		}
		default:
			throw new SWTError(SWT.ERROR_NOT_IMPLEMENTED);
	}
}

public void open(final Display display) {
	final Shell shell = new Shell(display);
	shell.setText("Drag and Drop Example");
	shell.setLayout(new FillLayout());

	this.itemImage = new Image (display, DNDExample.class.getResourceAsStream("openFolder.gif"));

	final ScrolledComposite sc = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
	final Composite parent = new Composite(sc, SWT.NONE);
	sc.setContent(parent);
	parent.setLayout(new FormLayout());

	final Label dragLabel = new Label(parent, SWT.LEFT);
	dragLabel.setText("Drag Source:");

	final Group dragWidgetGroup = new Group(parent, SWT.NONE);
	dragWidgetGroup.setText("Widget");
	this.createDragWidget(dragWidgetGroup);

	final Composite cLeft = new Composite(parent, SWT.NONE);
	cLeft.setLayout(new GridLayout(2, false));

	final Group dragOperationsGroup = new Group(cLeft, SWT.NONE);
	dragOperationsGroup.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
	dragOperationsGroup.setText("Allowed Operation(s):");
	this.createDragOperations(dragOperationsGroup);

	final Group dragTypesGroup = new Group(cLeft, SWT.NONE);
	dragTypesGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
	dragTypesGroup.setText("Transfer Type(s):");
	this.createDragTypes(dragTypesGroup);

	this.dragConsole = new Text(cLeft, SWT.READ_ONLY | SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
	this.dragConsole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	Menu menu = new Menu (shell, SWT.POP_UP);
	MenuItem item = new MenuItem (menu, SWT.PUSH);
	item.setText ("Clear");
	item.addSelectionListener(widgetSelectedAdapter(e -> this.dragConsole.setText("")));
	item = new MenuItem (menu, SWT.CHECK);
	item.setText ("Show Event detail");
	item.addSelectionListener(widgetSelectedAdapter(e -> {
		final MenuItem eItem = (MenuItem) e.widget;
		this.dragEventDetail = eItem.getSelection();
	}));
	this.dragConsole.setMenu(menu);

	final Label dropLabel = new Label(parent, SWT.LEFT);
	dropLabel.setText("Drop Target:");

	final Group dropWidgetGroup = new Group(parent, SWT.NONE);
	dropWidgetGroup.setText("Widget");
	this.createDropWidget(dropWidgetGroup);

	final Composite cRight = new Composite(parent, SWT.NONE);
	cRight.setLayout(new GridLayout(2, false));

	final Group dropOperationsGroup = new Group(cRight, SWT.NONE);
	dropOperationsGroup.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 2));
	dropOperationsGroup.setText("Allowed Operation(s):");
	this.createDropOperations(dropOperationsGroup);

	final Group dropTypesGroup = new Group(cRight, SWT.NONE);
	dropTypesGroup.setText("Transfer Type(s):");
	this.createDropTypes(dropTypesGroup);

	final Group feedbackTypesGroup = new Group(cRight, SWT.NONE);
	feedbackTypesGroup.setText("Feedback Type(s):");
	this.createFeedbackTypes(feedbackTypesGroup);

	this.dropConsole = new Text(cRight, SWT.READ_ONLY | SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
	this.dropConsole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	menu = new Menu (shell, SWT.POP_UP);
	item = new MenuItem (menu, SWT.PUSH);
	item.setText ("Clear");
	item.addSelectionListener(widgetSelectedAdapter(e -> this.dropConsole.setText("")));
	item = new MenuItem (menu, SWT.CHECK);
	item.setText ("Show Event detail");
	item.addSelectionListener(widgetSelectedAdapter(e -> {
			final MenuItem eItem = (MenuItem)e.widget;
			this.dropEventDetail = eItem.getSelection();
		}
	));
	this.dropConsole.setMenu(menu);

	if (this.dragEnabled) {
    this.createDragSource();
  }
	if (this.dropEnabled) {
    this.createDropTarget();
  }

	final int height = 200;
	FormData data = new FormData();
	data.top = new FormAttachment(0, 10);
	data.left = new FormAttachment(0, 10);
	dragLabel.setLayoutData(data);

	data = new FormData();
	data.top = new FormAttachment(dragLabel, 10);
	data.left = new FormAttachment(0, 10);
	data.right = new FormAttachment(50, -10);
	data.height = height;
	dragWidgetGroup.setLayoutData(data);

	data = new FormData();
	data.top = new FormAttachment(dragWidgetGroup, 10);
	data.left = new FormAttachment(0, 10);
	data.right = new FormAttachment(50, -10);
	data.bottom = new FormAttachment(100, -10);
	cLeft.setLayoutData(data);

	data = new FormData();
	data.top = new FormAttachment(0, 10);
	data.left = new FormAttachment(cLeft, 10);
	dropLabel.setLayoutData(data);

	data = new FormData();
	data.top = new FormAttachment(dropLabel, 10);
	data.left = new FormAttachment(cLeft, 10);
	data.right = new FormAttachment(100, -10);
	data.height = height;
	dropWidgetGroup.setLayoutData(data);

	data = new FormData();
	data.top = new FormAttachment(dropWidgetGroup, 10);
	data.left = new FormAttachment(cLeft, 10);
	data.right = new FormAttachment(100, -10);
	data.bottom = new FormAttachment(100, -10);
	cRight.setLayoutData(data);

	sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	sc.setExpandHorizontal(true);
	sc.setExpandVertical(true);

	final Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	final Rectangle monitorArea = shell.getMonitor().getClientArea();
	shell.setSize(Math.min(size.x, monitorArea.width - 20), Math.min(size.y, monitorArea.height - 20));
	shell.open();

	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) {
      display.sleep();
    }
	}
	this.itemImage.dispose();
}

private void printEvent(final DragSourceEvent e) {
	if (!this.dragEventDetail) {
    return;
  }
	this.dragConsole.append(e.toString() + "\n");
}

private void printEvent(final DropTargetEvent e) {
	if (!this.dropEventDetail) {
    return;
  }
	this.dropConsole.append(e.toString() + "\n");
}

private void removeDragTransfer(final Transfer transfer){
	if (this.dragTypes.length == 1) {
		this.dragTypes = new Transfer[0];
	} else {
		int index = -1;
		for(int i = 0; i < this.dragTypes.length; i++) {
			if (this.dragTypes[i] == transfer) {
				index = i;
				break;
			}
		}
		if (index == -1) {
      return;
    }
		final Transfer[] newTypes = new Transfer[this.dragTypes.length - 1];
		System.arraycopy(this.dragTypes, 0, newTypes, 0, index);
		System.arraycopy(this.dragTypes, index + 1, newTypes, index, this.dragTypes.length - index - 1);
		this.dragTypes = newTypes;
	}
	if (this.dragSource != null) {
		this.dragSource.setTransfer(this.dragTypes);
	}
}

private void removeDropTransfer(final Transfer transfer){
	if (this.dropTypes.length == 1) {
		this.dropTypes = new Transfer[0];
	} else {
		int index = -1;
		for(int i = 0; i < this.dropTypes.length; i++) {
			if (this.dropTypes[i] == transfer) {
				index = i;
				break;
			}
		}
		if (index == -1) {
      return;
    }
		final Transfer[] newTypes = new Transfer[this.dropTypes.length - 1];
		System.arraycopy(this.dropTypes, 0, newTypes, 0, index);
		System.arraycopy(this.dropTypes, index + 1, newTypes, index, this.dropTypes.length - index - 1);
		this.dropTypes = newTypes;
	}
	if (this.dropTarget != null) {
		this.dropTarget.setTransfer(this.dropTypes);
	}
}
}
