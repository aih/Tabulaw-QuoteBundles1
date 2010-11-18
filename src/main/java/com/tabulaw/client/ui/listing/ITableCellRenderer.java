/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk Jan 19, 2008
 */
package com.tabulaw.client.ui.listing;

import com.google.gwt.user.client.ui.HTMLTable;

/**
 * ITableCellRenderer - Responsible for transforming row data to presentation
 * ready columned text.
 * @author jpk
 * @param <R> The row data type
 */
public interface ITableCellRenderer<R> {

	/**
	 * Provides the table cell value given the row data and particular column.
	 * @param rowIndex the 0-based row index
	 * @param cellIndex
	 * @param rowData The row data
	 * @param column The table column
	 * @param table the table in which to render the cell contents
	 */
	void renderCell(int rowIndex, int cellIndex, R rowData, Column column, HTMLTable table);
}
