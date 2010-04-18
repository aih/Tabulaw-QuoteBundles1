/**
 * The Logic Lab
 * @author jpk
 * Jun 13, 2008
 */
package com.tabulaw.client.ui.listing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;

import com.tabulaw.common.model.IModelKeyProvider;
import com.tabulaw.common.model.ModelKey;

/**
 * A table whose rows are {@link Model}s and, consequently, identifiable by a
 * unique key: {@link ModelKey}.
 * @param <T> model key provider type
 * @author jpk
 */
public class ModelListingTable<T extends IModelKeyProvider> extends ListingTable<T> {

	/**
	 * The row data comprised of {@link Model} instances for each listing element
	 * row.
	 */
	protected final List<T> rowDataList = new ArrayList<T>();

	/**
	 * Constructor
	 */
	public ModelListingTable() {
		super();
	}

	/**
	 * Constructor
	 * @param config
	 * @param cellRenderer
	 */
	public ModelListingTable(IListingConfig config, ITableCellRenderer<T> cellRenderer) {
		super(config, cellRenderer);
	}

	/**
	 * Get the row ref for a given row.
	 * @param row 0-based table row num (considers the header row).
	 * @return ModelKey
	 */
	protected ModelKey getRowKey(int row) {
		return getRowData(row).getModelKey();
	}

	/**
	 * Get the underlying model data for a row.
	 * @param row 0-based table row num (considers the header row).
	 * @return the model row data
	 */
	protected T getRowData(int row) {
		return rowDataList.get(row - 1);
	}

	/**
	 * Get the row index given a {@link ModelKey}.
	 * @param rowKey The row key for which to find the associated row index.
	 * @return The resolved row index or <code>-1</code> if no row matching the
	 *         given row key is present in the table.
	 */
	protected int getRowIndex(ModelKey rowKey) {
		for(int i = 0; i < rowDataList.size(); i++) {
			final ModelKey rdlKey = rowDataList.get(i).getModelKey();
			if(rdlKey.equals(rowKey)) return i + 1; // account for header row
		}
		// can't find
		return -1;
	}

	@Override
	protected void setRowData(int rowIndex, int rowNum, T rowData, boolean overwriteOnNull) {
		assert rowIndex >= 1; // min index is one after the header row!
		super.setRowData(rowIndex, rowNum, rowData, overwriteOnNull);
		final int dindex = rowIndex - 1;
		if(dindex == rowDataList.size()) {
			rowDataList.add(rowData);
		}
		else {
			assert dindex < rowDataList.size();
			rowDataList.set(dindex, rowData);
		}
	}

	@Override
	protected int addRow(T rowData) {
		rowDataList.add(rowData);
		return super.addRow(rowData);
	}

	@Override
	protected void updateRow(int rowIndex, T rowData) {
		rowDataList.set(rowIndex - 1, rowData);
		super.updateRow(rowIndex, rowData);
	}

	@Override
	protected void deleteRow(int rowIndex) {
		rowDataList.remove(rowIndex - 1);
		super.deleteRow(rowIndex);
	}

	/**
	 * Applies model data to a given row in the UI only and does not alter the
	 * underlying row data.
	 */
	protected void applyModeltoUi(int rowIndex, T mdata) {
		super.setRowData(rowIndex, -1, mdata, true);
		getRowFormatter().addStyleName(rowIndex, Styles.UPDATED);
	}
}
