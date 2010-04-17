/**
 * The Logic Lab
 * @author jpk Nov 29, 2007
 */
package com.tabulaw.listhandler;

import com.tabulaw.dao.Sorting;

/**
 * AbstractListHandler - Common base class to all {@link IListHandler}
 * implementations.
 * @param <T> the list element type
 * @author jpk
 */
public abstract class AbstractListHandler<T> implements IListHandler<T> {

	/**
	 * The sorting directive.
	 */
	protected Sorting sorting;
}
