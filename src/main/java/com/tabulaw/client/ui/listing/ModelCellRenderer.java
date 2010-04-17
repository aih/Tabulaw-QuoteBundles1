/**
 * The Logic Lab
 * @author jpk
 * @since Mar 19, 2009
 */
package com.tabulaw.client.ui.listing;

import com.google.gwt.user.client.ui.HTMLTable;
import com.tll.common.model.Model;

/**
 * Core impl for cell rendering handling model property formatting.
 * @author jpk
 */
public class ModelCellRenderer implements ITableCellRenderer<Model> {

	/**
	 * @return The value to put in the cell when the "resolved" model value is
	 *         <code>null</code>.
	 */
	protected String getValueForNull() {
		return "-";
	}

	@Override
	public void renderCell(int rowIndex, int cellIndex, Model rowData, Column column, HTMLTable table) {
		String cv = ModelPropertyFormatter.pformat(rowData, column.getPropertyName(), column.getFormat());
		table.setText(rowIndex, cellIndex, cv == null ? getValueForNull() : cv);
	}

}
