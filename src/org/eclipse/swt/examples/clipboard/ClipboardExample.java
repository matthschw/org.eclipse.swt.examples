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
package org.eclipse.swt.examples.clipboard;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ClipboardExample {

	Clipboard clipboard;
	Shell shell;
	Text text;
	Combo combo;
	StyledText styledText;
	Label status;
	static final int HSIZE = 100, VSIZE = 60;

static final class AutoScaleImageDataProvider implements ImageDataProvider {
	ImageData imageData;
	int currentZoom;
	public AutoScaleImageDataProvider (final ImageData data) {
		this.imageData = data;
		this.currentZoom = getDeviceZoom ();
	}

	@Override
	public ImageData getImageData (final int zoom) {
		return autoScaleImageData(this.imageData, zoom, this.currentZoom);
	}

	static ImageData autoScaleImageData (final ImageData imageData, final int targetZoom, final int currentZoom) {
		if ((imageData == null) || (targetZoom == currentZoom)) {
      return imageData;
    }
		final float scaleFactor = ((float) targetZoom)/((float) currentZoom);
		return imageData.scaledTo (Math.round (imageData.width * scaleFactor), Math.round (imageData.height * scaleFactor));
	}

	static int getDeviceZoom () {
		int zoom = 100;
		final String value = System.getProperty ("org.eclipse.swt.internal.deviceZoom");
		if (value != null) {
			try {
				zoom = Integer.parseInt(value);
			} catch (final NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return zoom;
	}
}

public static void main( final String[] args) {
	final Display display = new Display();
	new ClipboardExample().open(display);
	display.dispose();
}
public void open(final Display display) {
	this.clipboard = new Clipboard(display);
	this.shell = new Shell (display);
	this.shell.setText("SWT Clipboard");
	this.shell.setLayout(new FillLayout());

	final ScrolledComposite sc = new ScrolledComposite(this.shell, SWT.H_SCROLL | SWT.V_SCROLL);
	final Composite parent = new Composite(sc, SWT.NONE);
	sc.setContent(parent);
	parent.setLayout(new GridLayout(2, true));

	final Group copyGroup = new Group(parent, SWT.NONE);
	copyGroup.setText("Copy From:");
	GridData data = new GridData(GridData.FILL_BOTH);
	copyGroup.setLayoutData(data);
	copyGroup.setLayout(new GridLayout(3, false));

	final Group pasteGroup = new Group(parent, SWT.NONE);
	pasteGroup.setText("Paste To:");
	data = new GridData(GridData.FILL_BOTH);
	pasteGroup.setLayoutData(data);
	pasteGroup.setLayout(new GridLayout(3, false));

	final Group controlGroup = new Group(parent, SWT.NONE);
	controlGroup.setText("Control API:");
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.horizontalSpan = 2;
	controlGroup.setLayoutData(data);
	controlGroup.setLayout(new GridLayout(5, false));

	final Group typesGroup = new Group(parent, SWT.NONE);
	typesGroup.setText("Available Types");
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.horizontalSpan = 2;
	typesGroup.setLayoutData(data);
	typesGroup.setLayout(new GridLayout(2, false));

	this.status = new Label(parent, SWT.NONE);
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.horizontalSpan = 2;
	this.status.setLayoutData(data);

	this.createTextTransfer(copyGroup, pasteGroup);
	this.createRTFTransfer(copyGroup, pasteGroup);
	this.createHTMLTransfer(copyGroup, pasteGroup);
	this.createFileTransfer(copyGroup, pasteGroup);
	this.createImageTransfer(copyGroup, pasteGroup);
	this.createMyTransfer(copyGroup, pasteGroup);
	this.createControlTransfer(controlGroup);
	this.createAvailableTypes(typesGroup);

	sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	sc.setExpandHorizontal(true);
	sc.setExpandVertical(true);

	final Point size = this.shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	final Rectangle monitorArea = this.shell.getMonitor().getClientArea();
	this.shell.setSize(Math.min(size.x, monitorArea.width - 20), Math.min(size.y, monitorArea.height - 20));
	this.shell.open();
	while (!this.shell.isDisposed ()) {
		if (!display.readAndDispatch ()) {
      display.sleep ();
    }
	}
	this.clipboard.dispose();
}
void createTextTransfer(final Composite copyParent, final Composite pasteParent) {

	// TextTransfer
	Label l = new Label(copyParent, SWT.NONE);
	l.setText("TextTransfer:"); //$NON-NLS-1$
	final Text copyText = new Text(copyParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	copyText.setText("some\nplain\ntext");
	GridData data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	copyText.setLayoutData(data);
	Button b = new Button(copyParent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final String textData = copyText.getText();
		if (textData.length() > 0) {
			this.status.setText("");
			this.clipboard.setContents(new Object[] {textData}, new Transfer[] {TextTransfer.getInstance()});
		} else {
			this.status.setText("No text to copy");
		}
	}));

	l = new Label(pasteParent, SWT.NONE);
	l.setText("TextTransfer:"); //$NON-NLS-1$
	final Text pasteText = new Text(pasteParent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	pasteText.setLayoutData(data);
	b = new Button(pasteParent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final String textData = (String)this.clipboard.getContents(TextTransfer.getInstance());
		if ((textData != null) && (textData.length() > 0)) {
			this.status.setText("");
			pasteText.setText("begin paste>"+textData+"<end paste");
		} else {
			this.status.setText("No text to paste");
		}
	}));
}
void createRTFTransfer(final Composite copyParent, final Composite pasteParent){
	//	RTF Transfer
	Label l = new Label(copyParent, SWT.NONE);
	l.setText("RTFTransfer:"); //$NON-NLS-1$
	final Text copyRtfText = new Text(copyParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	copyRtfText.setText("some\nrtf\ntext");
	GridData data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	copyRtfText.setLayoutData(data);
	Button b = new Button(copyParent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final String textData = copyRtfText.getText();
		if (textData.length() > 0) {
			this.status.setText("");
			final StringBuilder buffer = new StringBuilder();
			buffer.append("{\\rtf1\\ansi\\uc1{\\colortbl;\\red255\\green0\\blue0;}\\uc1\\b\\i ");
			for (int i = 0; i < textData.length(); i++) {
				final char ch = textData.charAt(i);
				if (ch > 0xFF) {
					buffer.append("\\u");
					buffer.append(Integer.toString((short) ch));
					buffer.append('?');
				} else {
					if ((ch == '}') || (ch == '{') || (ch == '\\')) {
						buffer.append('\\');
					}
					buffer.append(ch);
					if (ch == '\n') {
            buffer.append("\\par ");
          }
					if ((ch == '\r') && (((i - 1) == textData.length()) || (textData.charAt(i + 1) != '\n'))) {
						buffer.append("\\par ");
					}
				}
			}
			buffer.append("}");
			this.clipboard.setContents(new Object[] {buffer.toString()}, new Transfer[] {RTFTransfer.getInstance()});
		} else {
			this.status.setText("No RTF to copy");
		}
	}));

	l = new Label(pasteParent, SWT.NONE);
	l.setText("RTFTransfer:"); //$NON-NLS-1$
	final Text pasteRtfText = new Text(pasteParent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	pasteRtfText.setLayoutData(data);
	b = new Button(pasteParent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final String textData = (String)this.clipboard.getContents(RTFTransfer.getInstance());
		if ((textData != null) && (textData.length() > 0)) {
			this.status.setText("");
			pasteRtfText.setText("start paste>"+textData+"<end paste");
		} else {
			this.status.setText("No RTF to paste");
		}
	}));
}
void createHTMLTransfer(final Composite copyParent, final Composite pasteParent){
	//	HTML Transfer
	Label l = new Label(copyParent, SWT.NONE);
	l.setText("HTMLTransfer:"); //$NON-NLS-1$
	final Text copyHtmlText = new Text(copyParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	copyHtmlText.setText("<b>Hello World</b>");
	GridData data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	copyHtmlText.setLayoutData(data);
	Button b = new Button(copyParent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final String textData = copyHtmlText.getText();
		if (textData.length() > 0) {
			this.status.setText("");
			this.clipboard.setContents(new Object[] {textData}, new Transfer[] {HTMLTransfer.getInstance()});
		} else {
			this.status.setText("No HTML to copy");
		}
	}));

	l = new Label(pasteParent, SWT.NONE);
	l.setText("HTMLTransfer:"); //$NON-NLS-1$
	final Text pasteHtmlText = new Text(pasteParent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	pasteHtmlText.setLayoutData(data);
	b = new Button(pasteParent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final String textData = (String)this.clipboard.getContents(HTMLTransfer.getInstance());
		if ((textData != null) && (textData.length() > 0)) {
			this.status.setText("");
			pasteHtmlText.setText("start paste>"+textData+"<end paste");
		} else {
			this.status.setText("No HTML to paste");
		}
	}));
}
void createFileTransfer(final Composite copyParent, final Composite pasteParent){
	//File Transfer
	Label l = new Label(copyParent, SWT.NONE);
	l.setText("FileTransfer:"); //$NON-NLS-1$
	GridData data = new GridData();
	data.verticalSpan = 3;
	l.setLayoutData(data);

	final Table copyFileTable = new Table(copyParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	data.verticalSpan = 3;
	copyFileTable.setLayoutData(data);

	Button b = new Button(copyParent, SWT.PUSH);
	b.setText("Select file(s)");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final FileDialog dialog = new FileDialog(this.shell, SWT.OPEN | SWT.MULTI);
		final String result = dialog.open();
		if ((result != null) && (result.length() > 0)){
			final String path = dialog.getFilterPath();
			final String[] names = dialog.getFileNames();
			for (final String name : names) {
				final TableItem item = new TableItem(copyFileTable, SWT.NONE);
				item.setText(path+File.separator+name);
			}
		}
	}));
	b = new Button(copyParent, SWT.PUSH);
	b.setText("Select directory");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final DirectoryDialog dialog = new DirectoryDialog(this.shell, SWT.OPEN);
		final String result = dialog.open();
		if ((result != null) && (result.length() > 0)){
			//copyFileTable.removeAll();
			final TableItem item = new TableItem(copyFileTable, SWT.NONE);
			item.setText(result);
		}
	}));

	b = new Button(copyParent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final TableItem[] items = copyFileTable.getItems();
		if (items.length > 0){
			this.status.setText("");
			final String[] itemsData = new String[items.length];
			for (int i = 0; i < itemsData.length; i++) {
				itemsData[i] = items[i].getText();
			}
			this.clipboard.setContents(new Object[] {itemsData}, new Transfer[] {FileTransfer.getInstance()});
		} else {
			this.status.setText("No file to copy");
		}
	}));

	l = new Label(pasteParent, SWT.NONE);
	l.setText("FileTransfer:"); //$NON-NLS-1$
	final Table pasteFileTable = new Table(pasteParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	pasteFileTable.setLayoutData(data);
	b = new Button(pasteParent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final String[] textData = (String[])this.clipboard.getContents(FileTransfer.getInstance());
		if ((textData != null) && (textData.length > 0)) {
			this.status.setText("");
			pasteFileTable.removeAll();
			for (final String element : textData) {
				final TableItem item = new TableItem(pasteFileTable, SWT.NONE);
				item.setText(element);
			}
		} else {
			this.status.setText("No file to paste");
		}
	}));
}

void createImageTransfer(final Composite copyParent, final Composite pasteParent){
	final Image[] copyImage = new Image[] {null};
	Label l = new Label(copyParent, SWT.NONE);
	l.setText("ImageTransfer:"); //$NON-NLS-1$
	GridData data = new GridData();
	data.verticalSpan = 2;
	l.setLayoutData(data);

	final Canvas copyImageCanvas = new Canvas(copyParent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	data.verticalSpan = 2;
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	copyImageCanvas.setLayoutData(data);

	final Point copyOrigin = new Point(0, 0);
	final ScrollBar copyHBar = copyImageCanvas.getHorizontalBar();
	copyHBar.setEnabled(false);
	copyHBar.addListener(SWT.Selection, e -> {
		if (copyImage[0] != null) {
			final int hSelection = copyHBar.getSelection();
			final int destX = -hSelection - copyOrigin.x;
			final Rectangle rect = copyImage[0].getBounds();
			copyImageCanvas.scroll(destX, 0, 0, 0, rect.width, rect.height, false);
			copyOrigin.x = -hSelection;
		}
	});
	final ScrollBar copyVBar = copyImageCanvas.getVerticalBar();
	copyVBar.setEnabled(false);
	copyVBar.addListener(SWT.Selection, e -> {
		if (copyImage[0] != null) {
			final int vSelection = copyVBar.getSelection();
			final int destY = -vSelection - copyOrigin.y;
			final Rectangle rect = copyImage[0].getBounds();
			copyImageCanvas.scroll(0, destY, 0, 0, rect.width, rect.height, false);
			copyOrigin.y = -vSelection;
		}
	});
	copyImageCanvas.addListener(SWT.Paint, e -> {
		if(copyImage[0] != null) {
			final GC gc = e.gc;
			gc.drawImage(copyImage[0], copyOrigin.x, copyOrigin.y);
			final Rectangle rect = copyImage[0].getBounds();
			final Rectangle client = copyImageCanvas.getClientArea ();
			final int marginWidth = client.width - rect.width;
			if (marginWidth > 0) {
				gc.fillRectangle (rect.width, 0, marginWidth, client.height);
			}
			final int marginHeight = client.height - rect.height;
			if (marginHeight > 0) {
				gc.fillRectangle(0, rect.height, client.width, marginHeight);
			}
			gc.dispose();
		}
	});
	final Button openButton = new Button(copyParent, SWT.PUSH);
	openButton.setText("Open Image");
	openButton.addSelectionListener(widgetSelectedAdapter(e -> {
		final FileDialog dialog = new FileDialog (this.shell, SWT.OPEN);
		dialog.setText("Open an image file or cancel");
		final String string = dialog.open ();
		if (string != null) {
			if (copyImage[0] != null) {
				System.out.println("CopyImage");
				copyImage[0].dispose();
			}
			copyImage[0] = new Image(e.display, string);
			copyVBar.setEnabled(true);
			copyHBar.setEnabled(true);
			copyOrigin.x = 0; copyOrigin.y = 0;
			final Rectangle rect = copyImage[0].getBounds();
			final Rectangle client = copyImageCanvas.getClientArea();
			copyHBar.setMaximum(rect.width);
			copyVBar.setMaximum(rect.height);
			copyHBar.setThumb(Math.min(rect.width, client.width));
			copyVBar.setThumb(Math.min(rect.height, client.height));
			copyImageCanvas.scroll(0, 0, 0, 0, rect.width, rect.height, true);
			copyVBar.setSelection(0);
			copyHBar.setSelection(0);
			copyImageCanvas.redraw();
		}
	}));
	Button b = new Button(copyParent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		if (copyImage[0] != null) {
			this.status.setText("");
			// Fetch ImageData at current zoom and save in the clip-board.
			this.clipboard.setContents(new Object[] {copyImage[0].getImageDataAtCurrentZoom()}, new Transfer[] {ImageTransfer.getInstance()});
		} else {
			this.status.setText("No image to copy");
		}
	}));

	final Image[] pasteImage = new Image[] {null};
	l = new Label(pasteParent, SWT.NONE);
	l.setText("ImageTransfer:");
	final Canvas pasteImageCanvas = new Canvas(pasteParent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	pasteImageCanvas.setLayoutData(data);
	final Point pasteOrigin = new Point(0, 0);
	final ScrollBar pasteHBar = pasteImageCanvas.getHorizontalBar();
	pasteHBar.setEnabled(false);
	pasteHBar.addListener(SWT.Selection, e -> {
		if (pasteImage[0] != null) {
			final int hSelection = pasteHBar.getSelection();
			final int destX = -hSelection - pasteOrigin.x;
			final Rectangle rect = pasteImage[0].getBounds();
			pasteImageCanvas.scroll(destX, 0, 0, 0, rect.width, rect.height, false);
			pasteOrigin.x = -hSelection;
		}
	});
	final ScrollBar pasteVBar = pasteImageCanvas.getVerticalBar();
	pasteVBar.setEnabled(false);
	pasteVBar.addListener(SWT.Selection, e -> {
		if (pasteImage[0] != null) {
			final int vSelection = pasteVBar.getSelection();
			final int destY = -vSelection - pasteOrigin.y;
			final Rectangle rect = pasteImage[0].getBounds();
			pasteImageCanvas.scroll(0, destY, 0, 0, rect.width, rect.height, false);
			pasteOrigin.y = -vSelection;
		}
	});
	pasteImageCanvas.addListener(SWT.Paint, e -> {
		if(pasteImage[0] != null) {
			final GC gc = e.gc;
			gc.drawImage(pasteImage[0], pasteOrigin.x, pasteOrigin.y);
			final Rectangle rect = pasteImage[0].getBounds();
			final Rectangle client = pasteImageCanvas.getClientArea ();
			final int marginWidth = client.width - rect.width;
			if (marginWidth > 0) {
				gc.fillRectangle(rect.width, 0, marginWidth, client.height);
			}
			final int marginHeight = client.height - rect.height;
			if (marginHeight > 0) {
				gc.fillRectangle(0, rect.height, client.width, marginHeight);
			}
		}
	});
	b = new Button(pasteParent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		final ImageData imageData =(ImageData)this.clipboard.getContents(ImageTransfer.getInstance());
		if (imageData != null) {
			if (pasteImage[0] != null) {
				System.out.println("PasteImage");
				pasteImage[0].dispose();
			}
			this.status.setText("");
			// Consume the ImageData at current zoom as-is.
			pasteImage[0] = new Image(e.display, new AutoScaleImageDataProvider(imageData));
			pasteVBar.setEnabled(true);
			pasteHBar.setEnabled(true);
			pasteOrigin.x = 0; pasteOrigin.y = 0;
			final Rectangle rect = pasteImage[0].getBounds();
			final Rectangle client = pasteImageCanvas.getClientArea();
			pasteHBar.setMaximum(rect.width);
			pasteVBar.setMaximum(rect.height);
			pasteHBar.setThumb(Math.min(rect.width, client.width));
			pasteVBar.setThumb(Math.min(rect.height, client.height));
			pasteImageCanvas.scroll(0, 0, 0, 0, rect.width, rect.height, true);
			pasteVBar.setSelection(0);
			pasteHBar.setSelection(0);
			pasteImageCanvas.redraw();
		} else {
			this.status.setText("No image to paste");
		}
	}));
}
void createMyTransfer(final Composite copyParent, final Composite pasteParent){
	//	MyType Transfer
	// TODO
}
void createControlTransfer(final Composite parent){
	// TODO: CCombo and Spinner also have cut(), copy() and paste() API
	Label l = new Label(parent, SWT.NONE);
	l.setText("Text:");
	Button b = new Button(parent, SWT.PUSH);
	b.setText("Cut");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.text.cut()));
	b = new Button(parent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.text.copy()));
	b = new Button(parent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.text.paste()));
	this.text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	this.text.setLayoutData(data);

	l = new Label(parent, SWT.NONE);
	l.setText("Combo:");
	b = new Button(parent, SWT.PUSH);
	b.setText("Cut");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.combo.cut()));
	b = new Button(parent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.combo.copy()));
	b = new Button(parent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.combo.paste()));
	this.combo = new Combo(parent, SWT.NONE);
	this.combo.setItems("Item 1", "Item 2", "Item 3", "A longer Item");
	this.combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	l = new Label(parent, SWT.NONE);
	l.setText("StyledText:");
	b = new Button(parent, SWT.PUSH);
	b.setText("Cut");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.styledText.cut()));
	b = new Button(parent, SWT.PUSH);
	b.setText("Copy");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.styledText.copy()));
	b = new Button(parent, SWT.PUSH);
	b.setText("Paste");
	b.addSelectionListener(widgetSelectedAdapter(e -> this.styledText.paste()));
	this.styledText = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.widthHint = HSIZE;
	data.heightHint = VSIZE;
	this.styledText.setLayoutData(data);
}
void createAvailableTypes(final Composite parent){
	final List list = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	final GridData data = new GridData(GridData.FILL_BOTH);
	data.heightHint = VSIZE;
	list.setLayoutData(data);
	final Button b = new Button(parent, SWT.PUSH);
	b.setText("Get Available Types");
	b.addSelectionListener(widgetSelectedAdapter(e -> {
		list.removeAll();
		final String[] names = this.clipboard.getAvailableTypeNames();
		for (final String name : names) {
			list.add(name);
		}
	}));
}
}