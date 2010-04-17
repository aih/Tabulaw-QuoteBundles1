/**
 * The Logic Lab
 * @author jpk
 * @since Mar 21, 2010
 */
package com.tabulaw.common.data;

import java.util.List;

import com.tabulaw.dao.Sorting;
import com.tll.IMarshalable;

/**
 * Encapsulates page data for an associated listing.
 * @param <R> The row data type.
 * @author jpk
 */
public class Page<R extends IMarshalable> implements IMarshalable {

	private int totalSize, pageSize, offset;

	/**
	 * The sorting directive.
	 */
	private Sorting sorting;

	/**
	 * The current list of elements.
	 */
	private List<R> elements;

	/**
	 * Constructor
	 */
	public Page() {
		super();
	}

	/**
	 * Constructor
	 * @param totalSize
	 * @param pageSize
	 * @param offset
	 * @param sorting
	 * @param elements
	 */
	public Page(int totalSize, int pageSize, int offset, Sorting sorting, List<R> elements) {
		super();
		this.totalSize = totalSize;
		this.pageSize = pageSize;
		this.offset = offset;
		this.sorting = sorting;
		this.elements = elements;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Sorting getSorting() {
		return sorting;
	}

	public void setSorting(Sorting sorting) {
		this.sorting = sorting;
	}

	public List<R> getElements() {
		return elements;
	}

	public void setElements(List<R> elements) {
		this.elements = elements;
	}
}
