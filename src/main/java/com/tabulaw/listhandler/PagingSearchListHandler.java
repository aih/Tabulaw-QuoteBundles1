package com.tabulaw.listhandler;

import java.util.List;

import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.IPageResult;
import com.tabulaw.dao.SearchResult;
import com.tabulaw.dao.Sorting;

/**
 * Search supporting list handler implementation for pageable result sets.
 * @author jpk
 */
public final class PagingSearchListHandler extends SearchListHandler {

	/**
	 * The current page of results
	 */
	private IPageResult<SearchResult> page;

	/**
	 * Constructor
	 * @param dataProvider The data provider used to fetch the list elements with
	 *        the given criteria.
	 * @param criteria The criteria used to generate the underlying list
	 * @param sorting The required sorting directive.
	 */
	PagingSearchListHandler(IListingDataProvider dataProvider, Criteria<?> criteria, Sorting sorting) {
		super(dataProvider, criteria, sorting);
	}

	public ListHandlerType getListHandlerType() {
		return ListHandlerType.PAGE;
	}

	public List<SearchResult> getElements(int offset, int pageSize, Sorting sort) throws IndexOutOfBoundsException,
	EmptyListException, ListHandlerException {

		try {
			page = dataProvider.getPage(criteria, sort, offset, pageSize);
			if(page.getResultCount() < 1) {
				throw new EmptyListException("No matching page results found.");
			}
		}
		catch(final InvalidCriteriaException e) {
			throw new ListHandlerException(e.getMessage());
		}
		return page.getPageList();
	}

	public int size() {
		return page == null ? 0 : page.getResultCount();
	}
}
