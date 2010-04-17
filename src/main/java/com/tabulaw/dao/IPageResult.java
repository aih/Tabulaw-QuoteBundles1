/**
 * The Logic Lab
 * @author jpk
 * Jun 14, 2008
 */
package com.tabulaw.dao;

import java.util.List;

/**
 * IPageResult - Definition for providing search results based on a paged result
 * set.
 * @author jpk
 * @param <T>
 */
public interface IPageResult<T> {

	/**
	 * @return The "paged" sub-set of results.
	 */
	List<T> getPageList();

	/**
	 * @return The total number of records in the underlying recordset.
	 */
	int getResultCount();
}
