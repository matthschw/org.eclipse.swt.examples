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
package org.eclipse.swt.examples.addressbook;

import static org.eclipse.swt.events.MenuListener.menuShownAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetDefaultSelectedAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * AddressBookExample is an example that uses <code>org.eclipse.swt</code>
 * libraries to implement a simple address book.  This application has
 * save, load, sorting, and searching functions common
 * to basic address books.
 */
public class AddressBook {

	private static ResourceBundle resAddressBook = ResourceBundle.getBundle("examples_addressbook");
	private Shell shell;

	private Table table;
	private SearchDialog searchDialog;

	private File file;
	private boolean isModified;

	private String[] copyBuffer;

	private int lastSortColumn= -1;

	private static final String DELIMITER = "\t";
	private static final String[] columnNames = {resAddressBook.getString("Last_name"),
												 resAddressBook.getString("First_name"),
												 resAddressBook.getString("Business_phone"),
												 resAddressBook.getString("Home_phone"),
												 resAddressBook.getString("Email"),
												 resAddressBook.getString("Fax")};

public static void main(final String[] args) {
	final Display display = new Display();
	final AddressBook application = new AddressBook();
	final Shell shell = application.open(display);
	while(!shell.isDisposed()){
		if(!display.readAndDispatch()) {
      display.sleep();
    }
	}
	display.dispose();
}
public Shell open(final Display display) {
	this.shell = new Shell(display);
	this.shell.setLayout(new FillLayout());
	this.shell.addShellListener(ShellListener.shellClosedAdapter(e -> e.doit = this.closeAddressBook()));

	this.createMenuBar();

	this.searchDialog = new SearchDialog(this.shell);
	this.searchDialog.setSearchAreaNames(columnNames);
	this.searchDialog.setSearchAreaLabel(resAddressBook.getString("Column"));
	this.searchDialog.addFindListener(() -> this.findEntry());

	this.table = new Table(this.shell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
	this.table.setHeaderVisible(true);
	this.table.setMenu(this.createPopUpMenu());
	this.table.addSelectionListener(widgetDefaultSelectedAdapter(e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length > 0) {
      this.editEntry(items[0]);
    }
	}));
	for(int i = 0; i < columnNames.length; i++) {
		final TableColumn column = new TableColumn(this.table, SWT.NONE);
		column.setText(columnNames[i]);
		column.setWidth(150);
		final int columnIndex = i;
		column.addSelectionListener(widgetSelectedAdapter(e -> this.sort(columnIndex)));
	}

	this.newAddressBook();

	this.shell.setSize(this.table.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, 300);
	this.shell.open();
	return this.shell;
}

private boolean closeAddressBook() {
	if(this.isModified) {
		//ask user if they want to save current address book
		final MessageBox box = new MessageBox(this.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		box.setText(this.shell.getText());
		box.setMessage(resAddressBook.getString("Close_save"));

		final int choice = box.open();
		if(choice == SWT.CANCEL) {
			return false;
		} else if(choice == SWT.YES) {
			if (!this.save()) {
        return false;
      }
		}
	}

	final TableItem[] items = this.table.getItems();
	for (final TableItem item : items) {
		item.dispose();
	}

	return true;
}
/**
 * Creates the menu at the top of the shell where most
 * of the programs functionality is accessed.
 *
 * @return		The <code>Menu</code> widget that was created
 */
private Menu createMenuBar() {
	final Menu menuBar = new Menu(this.shell, SWT.BAR);
	this.shell.setMenuBar(menuBar);

	//create each header and subMenu for the menuBar
	this.createFileMenu(menuBar);
	this.createEditMenu(menuBar);
	this.createSearchMenu(menuBar);
	this.createHelpMenu(menuBar);

	return menuBar;
}

/**
 * Converts an encoded <code>String</code> to a String array representing a table entry.
 */
private String[] decodeLine(String line) {
	if(line == null) {
    return null;
  }

	final String[] parsedLine = new String[this.table.getColumnCount()];
	for(int i = 0; i < (parsedLine.length - 1); i++) {
		final int index = line.indexOf(DELIMITER);
		if (index > -1) {
			parsedLine[i] = line.substring(0, index);
			line = line.substring(index + DELIMITER.length());
		} else {
			return null;
		}
	}

	if (line.contains(DELIMITER)) {
    return null;
  }

	parsedLine[parsedLine.length - 1] = line;

	return parsedLine;
}
private void displayError(final String msg) {
	final MessageBox box = new MessageBox(this.shell, SWT.ICON_ERROR);
	box.setMessage(msg);
	box.open();
}
private void editEntry(final TableItem item) {
	final DataEntryDialog dialog = new DataEntryDialog(this.shell);
	dialog.setLabels(columnNames);
	String[] values = new String[this.table.getColumnCount()];
	for (int i = 0; i < values.length; i++) {
		values[i] = item.getText(i);
	}
	dialog.setValues(values);
	values = dialog.open();
	if (values != null) {
		item.setText(values);
		this.isModified = true;
	}
}
private String encodeLine(final String[] tableItems) {
	StringBuilder line = new StringBuilder();
	for (int i = 0; i < (tableItems.length - 1); i++) {
		line.append(tableItems[i]).append(DELIMITER);
	}
	line.append(tableItems[tableItems.length - 1]).append("\n");

	return line.toString();
}
private boolean findEntry() {
	final Cursor waitCursor = this.shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
	this.shell.setCursor(waitCursor);

	final boolean matchCase = this.searchDialog.getMatchCase();
	final boolean matchWord = this.searchDialog.getMatchWord();
	String searchString = this.searchDialog.getSearchString();
	final int column = this.searchDialog.getSelectedSearchArea();

	searchString = matchCase ? searchString : searchString.toLowerCase();

	boolean found = false;
	if (this.searchDialog.getSearchDown()) {
		for(int i = this.table.getSelectionIndex() + 1; i < this.table.getItemCount(); i++) {
			if (found = this.findMatch(searchString, this.table.getItem(i), column, matchWord, matchCase)){
				this.table.setSelection(i);
				break;
			}
		}
	} else {
		for(int i = this.table.getSelectionIndex() - 1; i > -1; i--) {
			if (found = this.findMatch(searchString, this.table.getItem(i), column, matchWord, matchCase)){
				this.table.setSelection(i);
				break;
			}
		}
	}

	this.shell.setCursor(null);

	return found;
}
private boolean findMatch(final String searchString, final TableItem item, final int column, final boolean matchWord, final boolean matchCase) {

	final String tableText = matchCase ? item.getText(column) : item.getText(column).toLowerCase();
	if (matchWord) {
		if ((tableText != null) && tableText.equals(searchString)) {
			return true;
		}

	} else if((tableText!= null) && tableText.contains(searchString)) {
  	return true;
  }
	return false;
}
private void newAddressBook() {
	this.shell.setText(resAddressBook.getString("Title_bar") + resAddressBook.getString("New_title"));
	this.file = null;
	this.isModified = false;
}
private void newEntry() {
	final DataEntryDialog dialog = new DataEntryDialog(this.shell);
	dialog.setLabels(columnNames);
	final String[] data = dialog.open();
	if (data != null) {
		final TableItem item = new TableItem(this.table, SWT.NONE);
		item.setText(data);
		this.isModified = true;
	}
}

private void openAddressBook() {
	final FileDialog fileDialog = new FileDialog(this.shell, SWT.OPEN);

	fileDialog.setFilterExtensions(new String[] {"*.adr;", "*.*"});
	fileDialog.setFilterNames(new String[] {resAddressBook.getString("Book_filter_name") + " (*.adr)",
											resAddressBook.getString("All_filter_name") + " (*.*)"});
	final String name = fileDialog.open();

	if(name == null) {
    return;
  }
	final File file = new File(name);
	if (!file.exists()) {
		this.displayError(resAddressBook.getString("File")+file.getName()+" "+resAddressBook.getString("Does_not_exist"));
		return;
	}

	final Cursor waitCursor = this.shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
	this.shell.setCursor(waitCursor);

	String[] data = new String[0];
	try (FileReader fileReader = new FileReader(file.getAbsolutePath());
			BufferedReader bufferedReader = new BufferedReader(fileReader);){

		String nextLine = bufferedReader.readLine();
		while (nextLine != null){
			final String[] newData = new String[data.length + 1];
			System.arraycopy(data, 0, newData, 0, data.length);
			newData[data.length] = nextLine;
			data = newData;
			nextLine = bufferedReader.readLine();
		}
	} catch(final FileNotFoundException e) {
		this.displayError(resAddressBook.getString("File_not_found") + "\n" + file.getName());
		return;
	} catch (final IOException e ) {
		this.displayError(resAddressBook.getString("IO_error_read") + "\n" + file.getName());
		return;
	} finally {
		this.shell.setCursor(null);
	}

	String[][] tableInfo = new String[data.length][this.table.getColumnCount()];
	int writeIndex = 0;
	for (final String element : data) {
		final String[] line = this.decodeLine(element);
		if (line != null) {
      tableInfo[writeIndex++] = line;
    }
	}
	if (writeIndex != data.length) {
		final String[][] result = new String[writeIndex][this.table.getColumnCount()];
		System.arraycopy(tableInfo, 0, result, 0, writeIndex);
		tableInfo = result;
	}
	Arrays.sort(tableInfo, new RowComparator(0));

	for (final String[] element : tableInfo) {
		final TableItem item = new TableItem(this.table, SWT.NONE);
		item.setText(element);
	}
	this.shell.setText(resAddressBook.getString("Title_bar")+fileDialog.getFileName());
	this.isModified = false;
	this.file = file;
}
private boolean save() {
	if(this.file == null) {
    return this.saveAs();
  }

	final Cursor waitCursor = new Cursor(this.shell.getDisplay(), SWT.CURSOR_WAIT);
	this.shell.setCursor(waitCursor);

	final TableItem[] items = this.table.getItems();
	final String[] lines = new String[items.length];
	for(int i = 0; i < items.length; i++) {
		final String[] itemText = new String[this.table.getColumnCount()];
		for (int j = 0; j < itemText.length; j++) {
			itemText[j] = items[i].getText(j);
		}
		lines[i] = this.encodeLine(itemText);
	}

	try (FileWriter fileWriter = new FileWriter(this.file.getAbsolutePath(), false);){
		for (final String line : lines) {
			fileWriter.write(line);
		}
	} catch(final FileNotFoundException e) {
		this.displayError(resAddressBook.getString("File_not_found") + "\n" + this.file.getName());
		return false;
	} catch(final IOException e ) {
		this.displayError(resAddressBook.getString("IO_error_write") + "\n" + this.file.getName());
		return false;
	} finally {
		this.shell.setCursor(null);
	}

	this.shell.setText(resAddressBook.getString("Title_bar")+this.file.getName());
	this.isModified = false;
	return true;
}
private boolean saveAs() {

	final FileDialog saveDialog = new FileDialog(this.shell, SWT.SAVE);
	saveDialog.setFilterExtensions(new String[] {"*.adr;",  "*.*"});
	saveDialog.setFilterNames(new String[] {"Address Books (*.adr)", "All Files "});

	saveDialog.open();
	String name = saveDialog.getFileName();

	if(name.isEmpty()) {
    return false;
  }

	if(name.indexOf(".adr") != (name.length() - 4)) {
		name += ".adr";
	}

	final File file = new File(saveDialog.getFilterPath(), name);
	if(file.exists()) {
		final MessageBox box = new MessageBox(this.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
		box.setText(resAddressBook.getString("Save_as_title"));
		box.setMessage(resAddressBook.getString("File") + file.getName()+" "+resAddressBook.getString("Query_overwrite"));
		if(box.open() != SWT.YES) {
			return false;
		}
	}
	this.file = file;
	return this.save();
}
private void sort(final int column) {
	if(this.table.getItemCount() <= 1) {
    return;
  }

	final TableItem[] items = this.table.getItems();
	final String[][] data = new String[items.length][this.table.getColumnCount()];
	for(int i = 0; i < items.length; i++) {
		for(int j = 0; j < this.table.getColumnCount(); j++) {
			data[i][j] = items[i].getText(j);
		}
	}

	Arrays.sort(data, new RowComparator(column));

	if (this.lastSortColumn != column) {
		this.table.setSortColumn(this.table.getColumn(column));
		this.table.setSortDirection(SWT.DOWN);
		for (int i = 0; i < data.length; i++) {
			items[i].setText(data[i]);
		}
		this.lastSortColumn = column;
	} else {
		// reverse order if the current column is selected again
		this.table.setSortDirection(SWT.UP);
		int j = data.length -1;
		for (int i = 0; i < data.length; i++) {
			items[i].setText(data[j--]);
		}
		this.lastSortColumn = -1;
	}

}
/**
 * Creates all the items located in the File submenu and
 * associate all the menu items with their appropriate
 * functions.
 *
 * @param	menuBar Menu
 *				the <code>Menu</code> that file contain
 *				the File submenu.
 */
private void createFileMenu(final Menu menuBar) {
	//File menu.
	final MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
	item.setText(resAddressBook.getString("File_menu_title"));
	final Menu menu = new Menu(this.shell, SWT.DROP_DOWN);
	item.setMenu(menu);
	/**
	 * Adds a listener to handle enabling and disabling
	 * some items in the Edit submenu.
	 */
	menu.addMenuListener(menuShownAdapter(e -> {
		final Menu menu1 = (Menu) e.widget;
		final MenuItem[] items = menu1.getItems();
		items[1].setEnabled(this.table.getSelectionCount() != 0); // edit contact
		items[5].setEnabled((this.file != null) && this.isModified); // save
		items[6].setEnabled(this.table.getItemCount() != 0); // save as
	}));


	//File -> New Contact
	MenuItem subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("New_contact"));
	subItem.setAccelerator(SWT.MOD1 + 'N');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> this.newEntry()));
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Edit_contact"));
	subItem.setAccelerator(SWT.MOD1 + 'E');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length == 0) {
      return;
    }
		this.editEntry(items[0]);
	}));


	new MenuItem(menu, SWT.SEPARATOR);

	//File -> New Address Book
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("New_address_book"));
	subItem.setAccelerator(SWT.MOD1 + 'B');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		if (this.closeAddressBook()) {
			this.newAddressBook();
		}
	}));

	//File -> Open
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Open_address_book"));
	subItem.setAccelerator(SWT.MOD1 + 'O');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		if (this.closeAddressBook()) {
			this.openAddressBook();
		}
	}));

	//File -> Save.
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Save_address_book"));
	subItem.setAccelerator(SWT.MOD1 + 'S');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> this.save()));

	//File -> Save As.
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Save_book_as"));
	subItem.setAccelerator(SWT.MOD1 + 'A');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> this.saveAs()));


	new MenuItem(menu, SWT.SEPARATOR);

	//File -> Exit.
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Exit"));
	subItem.addSelectionListener(widgetSelectedAdapter( e -> this.shell.close()));
}

/**
 * Creates all the items located in the Edit submenu and
 * associate all the menu items with their appropriate
 * functions.
 *
 * @param	menuBar Menu
 *				the <code>Menu</code> that file contain
 *				the Edit submenu.
 *
 * @see	#createSortMenu()
 */
private MenuItem createEditMenu(final Menu menuBar) {
	//Edit menu.
	final MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
	item.setText(resAddressBook.getString("Edit_menu_title"));
	final Menu menu = new Menu(this.shell, SWT.DROP_DOWN);
	item.setMenu(menu);

	/**
	 * Add a listener to handle enabling and disabling
	 * some items in the Edit submenu.
	 */
	menu.addMenuListener(menuShownAdapter(e -> {
		final Menu menu1 = (Menu) e.widget;
		final MenuItem[] items = menu1.getItems();
		final int count = this.table.getSelectionCount();
		items[0].setEnabled(count != 0); // edit
		items[1].setEnabled(count != 0); // copy
		items[2].setEnabled(this.copyBuffer != null); // paste
		items[3].setEnabled(count != 0); // delete
		items[5].setEnabled(this.table.getItemCount() != 0); // sort
	}));

	//Edit -> Edit
	MenuItem subItem = new MenuItem(menu, SWT.PUSH);
	subItem.setText(resAddressBook.getString("Edit"));
	subItem.setAccelerator(SWT.MOD1 + 'E');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length == 0) {
      return;
    }
		this.editEntry(items[0]);
	}));

	//Edit -> Copy
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Copy"));
	subItem.setAccelerator(SWT.MOD1 + 'C');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length == 0) {
      return;
    }
		this.copyBuffer = new String[this.table.getColumnCount()];
		for (int i = 0; i < this.copyBuffer.length; i++) {
			this.copyBuffer[i] = items[0].getText(i);
		}
	}));

	//Edit -> Paste
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Paste"));
	subItem.setAccelerator(SWT.MOD1 + 'V');
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		if (this.copyBuffer == null) {
      return;
    }
		final TableItem tableItem = new TableItem(this.table, SWT.NONE);
		tableItem.setText(this.copyBuffer);
		this.isModified = true;
	}));

	//Edit -> Delete
	subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("Delete"));
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length == 0) {
      return;
    }
		items[0].dispose();
		this.isModified = true;
	}));

	new MenuItem(menu, SWT.SEPARATOR);

	//Edit -> Sort(Cascade)
	subItem = new MenuItem(menu, SWT.CASCADE);
	subItem.setText(resAddressBook.getString("Sort"));
	final Menu submenu = this.createSortMenu();
	subItem.setMenu(submenu);

	return item;

}

/**
 * Creates all the items located in the Sort cascading submenu and
 * associate all the menu items with their appropriate
 * functions.
 *
 * @return	Menu
 *			The cascading menu with all the sort menu items on it.
 */
private Menu createSortMenu() {
	final Menu submenu = new Menu(this.shell, SWT.DROP_DOWN);
	MenuItem subitem;
	for(int i = 0; i < columnNames.length; i++) {
		subitem = new MenuItem (submenu, SWT.NONE);
		subitem.setText(columnNames [i]);
		final int column = i;
		subitem.addSelectionListener(widgetSelectedAdapter( e -> this.sort(column)));

	}

	return submenu;
}

/**
 * Creates all the items located in the Search submenu and
 * associate all the menu items with their appropriate
 * functions.
 *
 * @param	menuBar	Menu
 *				the <code>Menu</code> that file contain
 *				the Search submenu.
 */
private void createSearchMenu(final Menu menuBar) {
	//Search menu.
	MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
	item.setText(resAddressBook.getString("Search_menu_title"));
	final Menu searchMenu = new Menu(this.shell, SWT.DROP_DOWN);
	item.setMenu(searchMenu);

	//Search -> Find...
	item = new MenuItem(searchMenu, SWT.NONE);
	item.setText(resAddressBook.getString("Find"));
	item.setAccelerator(SWT.MOD1 + 'F');
	item.addSelectionListener(widgetSelectedAdapter( e -> {
		this.searchDialog.setMatchCase(false);
		this.searchDialog.setMatchWord(false);
		this.searchDialog.setSearchDown(true);
		this.searchDialog.setSearchString("");
		this.searchDialog.setSelectedSearchArea(0);
		this.searchDialog.open();
	}));

	//Search -> Find Next
	item = new MenuItem(searchMenu, SWT.NONE);
	item.setText(resAddressBook.getString("Find_next"));
	item.setAccelerator(SWT.F3);
	item.addSelectionListener(widgetSelectedAdapter( e -> this.searchDialog.open()));
}

/**
 * Creates all items located in the popup menu and associates
 * all the menu items with their appropriate functions.
 *
 * @return	Menu
 *			The created popup menu.
 */
private Menu createPopUpMenu() {
	final Menu popUpMenu = new Menu(this.shell, SWT.POP_UP);

	/**
	 * Adds a listener to handle enabling and disabling
	 * some items in the Edit submenu.
	 */
	popUpMenu.addMenuListener(menuShownAdapter(e -> {
		final Menu menu = (Menu) e.widget;
		final MenuItem[] items = menu.getItems();
		final int count = this.table.getSelectionCount();
		items[2].setEnabled(count != 0); // edit
		items[3].setEnabled(count != 0); // copy
		items[4].setEnabled(this.copyBuffer != null); // paste
		items[5].setEnabled(count != 0); // delete
		items[7].setEnabled(this.table.getItemCount() != 0); // find
	}));

	//New
	MenuItem item = new MenuItem(popUpMenu, SWT.PUSH);
	item.setText(resAddressBook.getString("Pop_up_new"));
	item.addSelectionListener(widgetSelectedAdapter( e -> this.newEntry()));

	new MenuItem(popUpMenu, SWT.SEPARATOR);

	//Edit
	item = new MenuItem(popUpMenu, SWT.PUSH);
	item.setText(resAddressBook.getString("Pop_up_edit"));
	item.addSelectionListener(widgetSelectedAdapter( e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length == 0) {
      return;
    }
		this.editEntry(items[0]);
	}));

	//Copy
	item = new MenuItem(popUpMenu, SWT.PUSH);
	item.setText(resAddressBook.getString("Pop_up_copy"));
	item.addSelectionListener(widgetSelectedAdapter( e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length == 0) {
      return;
    }
		this.copyBuffer = new String[this.table.getColumnCount()];
		for (int i = 0; i < this.copyBuffer.length; i++) {
			this.copyBuffer[i] = items[0].getText(i);
		}
	}));

	//Paste
	item = new MenuItem(popUpMenu, SWT.PUSH);
	item.setText(resAddressBook.getString("Pop_up_paste"));
	item.addSelectionListener(widgetSelectedAdapter( e -> {
		if (this.copyBuffer == null) {
      return;
    }
		final TableItem tableItem = new TableItem(this.table, SWT.NONE);
		tableItem.setText(this.copyBuffer);
		this.isModified = true;
	}));

	//Delete
	item = new MenuItem(popUpMenu, SWT.PUSH);
	item.setText(resAddressBook.getString("Pop_up_delete"));
	item.addSelectionListener(widgetSelectedAdapter( e -> {
		final TableItem[] items = this.table.getSelection();
		if (items.length == 0) {
      return;
    }
		items[0].dispose();
		this.isModified = true;
	}));

	new MenuItem(popUpMenu, SWT.SEPARATOR);

	//Find...
	item = new MenuItem(popUpMenu, SWT.PUSH);
	item.setText(resAddressBook.getString("Pop_up_find"));
	item.addSelectionListener(widgetSelectedAdapter( e -> this.searchDialog.open()));

	return popUpMenu;
}

/**
 * Creates all the items located in the Help submenu and
 * associate all the menu items with their appropriate
 * functions.
 *
 * @param	menuBar	Menu
 *				the <code>Menu</code> that file contain
 *				the Help submenu.
 */
private void createHelpMenu(final Menu menuBar) {

	//Help Menu
	final MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
	item.setText(resAddressBook.getString("Help_menu_title"));
	final Menu menu = new Menu(this.shell, SWT.DROP_DOWN);
	item.setMenu(menu);

	//Help -> About Text Editor
	final MenuItem subItem = new MenuItem(menu, SWT.NONE);
	subItem.setText(resAddressBook.getString("About"));
	subItem.addSelectionListener(widgetSelectedAdapter( e -> {
		final MessageBox box = new MessageBox(this.shell, SWT.NONE);
		box.setText(resAddressBook.getString("About_1") + this.shell.getText());
		box.setMessage(this.shell.getText() + resAddressBook.getString("About_2"));
		box.open();
	}));
}

/**
 * To compare entries (rows) by the given column
 */
private static class RowComparator implements Comparator<String[]> {
	private final int column;

	/**
	 * Constructs a RowComparator given the column index
	 * @param col The index (starting at zero) of the column
	 */
	public RowComparator(final int col) {
		this.column = col;
	}

	/**
	 * Compares two rows (type String[]) using the specified
	 * column entry.
	 * @param row1 First row to compare
	 * @param row2 Second row to compare
	 * @return negative if row1 less than row2, positive if
	 * 			row1 greater than row2, and zero if equal.
	 */
	@Override
	public int compare(final String[] row1, final String[] row2) {
		return row1[this.column].compareTo(row2[this.column]);
	}
}
}
