package com.tabulaw.listhandler;

import java.util.ArrayList;
import java.util.List;

import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.SearchResult;
import com.tabulaw.dao.Sorting;

/**
 * Search supporting list handler implementation based on an id list.
 * @author jpk
 */
public final class PrimaryKeyListHandler extends SearchListHandler {

	/**
	 * The id list - list of entity pks matching the search criteria.
	 */
	private List<?> pks;

	/**
	 * Constructor
	 * @param dataProvider The data provider used to fetch the list elements with
	 *        the given criteria.
	 * @param criteria The criteria used to generate the underlying list
	 * @param sorting
	 */
	PrimaryKeyListHandler(IListingDataProvider dataProvider, Criteria<?> criteria, Sorting sorting) {
		super(dataProvider, criteria, sorting);
	}

	public ListHandlerType getListHandlerType() {
		return ListHandlerType.IDLIST;
	}

	public int size() {
		return (pks == null) ? 0 : pks.size();
	}

	@Override
	public List<SearchResult> getElements(int offset, int pageSize, Sorting sort) throws IndexOutOfBoundsException,
	EmptyListException, ListHandlerException {

		assert this.sorting != null;

		// if sorting differs, re-execute search
		if(sort != null && !sort.equals(this.sorting) || (sort == null && this.sorting != null)) {
			try {
				pks = dataProvider.getPrimaryKeys(criteria, sort);
			}
			catch(final InvalidCriteriaException e) {
				throw new ListHandlerException(e.getMessage());
			}
		}

		if(pks == null || pks.size() < 1) {
			throw new EmptyListException("No list elements exist");
		}

		final int size = pks.size();
		int ei = offset + pageSize;

		// adjust the end index if it exceeds the bounds of the id list
		if(ei > size - 1) ei = size - 1;

		final List<?> subids = pks.subList(offset, ei);

		final List<?> list = dataProvider.getEntitiesFromIds(criteria.getEntityClass(), subids, sort);
		if(list == null || list.size() != subids.size()) {
			throw new ListHandlerException("id and entity count mismatch");
		}
		final List<SearchResult> slist = new ArrayList<SearchResult>(list.size());
		for(final Object e : list) {
			slist.add(new SearchResult(e));
		}
		return slist;
	}
}
