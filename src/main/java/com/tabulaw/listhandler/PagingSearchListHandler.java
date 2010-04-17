package com.tabulaw.listhandler;

import java.util.List;

import com.tabulaw.dao.IPageResult;
import com.tabulaw.dao.Sorting;

/**
 * Search supporting list handler implementation for pageable result sets.
 * @author jpk
 * @param <T>
 */
public abstract class PagingSearchListHandler<T> implements IListHandler<T> {

	/**
	 * The current page of results
	 */
	protected IPageResult<T> page;

	/**
	 * @param offset
	 * @param pageSize
	 * @param sorting
	 * @return
	 * @throws IndexOutOfBoundsException
	 * @throws ListHandlerException
	 */
	protected abstract IPageResult<T> doGetElements(int offset, int pageSize, Sorting sorting)
			throws IndexOutOfBoundsException, ListHandlerException;

	public final List<T> getElements(int offset, int pageSize, Sorting sort) throws IndexOutOfBoundsException,
			EmptyListException, ListHandlerException {

		page = doGetElements(offset, pageSize, sort);
		if(page.getResultCount() < 1) {
			throw new EmptyListException("No matching page results found.");
		}
		return page.getPageList();
	}

	public int size() {
		return page == null ? 0 : page.getResultCount();
	}
}
