package com.tabulaw.listhandler;

import java.util.Collections;
import java.util.List;

import com.tabulaw.dao.SortColumnComparator;
import com.tabulaw.dao.Sorting;

/**
 * InMemoryListHandler - {@link IListHandler} implementation for a
 * {@link java.util.List}.
 * @author jpk
 * @param <T> the row element type
 */
public class InMemoryListHandler<T> extends AbstractListHandler<T> {

	/**
	 * The managed list.
	 */
	private List<T> rows;

	/**
	 * Constructor
	 */
	public InMemoryListHandler() {
		super();
	}

	/**
	 * Constructor
	 * @param rows must not be <code>null</code> but may be empty.
	 * @throws IllegalArgumentException When <code>rows</code> is
	 *         <code>null</code>
	 */
	public InMemoryListHandler(List<T> rows) {
		super();
		setList(rows);
	}

	/**
	 * Sets or resets the managed row list.
	 * @param rows non-<code>null</code>
	 */
	public void setList(List<T> rows) {
		if(rows == null) {
			throw new IllegalArgumentException("Null row list");
		}
		this.rows = rows;
	}

	public final int size() {
		return rows == null ? 0 : rows.size();
	}

	void sort(Sorting sort) throws ListHandlerException {
		if(rows == null) throw new ListHandlerException("Rows not set.");
		if(sort == null || sort.size() < 1) {
			throw new ListHandlerException("No sorting specified.");
		}
		if(size() > 1) {
			try {
				Collections.sort(this.rows, new SortColumnComparator<T>(sort.getPrimarySortColumn()));
			}
			catch(final RuntimeException e) {
				throw new ListHandlerException("Unable to sort list: " + e.getMessage(), e);
			}
		}
		this.sorting = sort;
	}

	public List<T> getElements(int offset, int pageSize, Sorting sort) throws IndexOutOfBoundsException,
			EmptyListException, ListHandlerException {
		final int siz = size();
		if(rows == null) throw new ListHandlerException("No rows set");
		if(siz < 1) throw new EmptyListException("No collection list elements exist");
		
		if(sort != null && !sort.equals(this.sorting) || sorting == null) {
			sort(sort);
		}
		
		// adjust boundaries if necessary
		// TODO determine if we should be rigid instead and throw an IndexOutOfBoundsException..
		int start = offset, end = offset + pageSize;
		if(start >= siz) start = siz - 1;
		if(end > siz) end = siz;
		assert end >= start;
		
		return rows.subList(start, end);
	}
}
