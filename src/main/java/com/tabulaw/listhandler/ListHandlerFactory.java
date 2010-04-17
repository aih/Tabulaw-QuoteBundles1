package com.tabulaw.listhandler;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.tabulaw.common.model.IEntity;
import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.Sorting;
import com.tll.IPropertyValueProvider;
import com.tll.util.CollectionUtil;

/**
 * Factory for creating concrete {@link IListHandler}s.
 * @author jpk
 */
public abstract class ListHandlerFactory {

	/**
	 * Creates an {@link InMemoryListHandler} given a {@link Collection} and
	 * {@link Sorting} directive.
	 * @param <T> the collection element type
	 * @param c an arbitrary collection. If the collection isn't a {@link List}, a
	 *        newly created {@link List} is created containing all collection
	 *        elements whose element order is dicated by the collection's
	 *        {@link Iterator}.
	 * @param sorting the sorting directive. May be <code>null</code>.
	 * @return IListHandler instance
	 * @throws ListHandlerException When a sorting related occurs.
	 * @throws IllegalArgumentException When the given collection is
	 *         <code>null</code>
	 */
	public static <T extends IPropertyValueProvider> IListHandler<T> create(Collection<T> c, Sorting sorting) throws ListHandlerException,
	IllegalArgumentException {
		try {
			final InMemoryListHandler<T> listHandler = new InMemoryListHandler<T>(CollectionUtil.listFromCollection(c));
			if(sorting != null) {
				listHandler.sort(sorting);
			}
			return listHandler;
		}
		catch(final EmptyListException ele) {
			throw ele;
		}
	}

	/**
	 * Creates a criteria based list handler.
	 * @param criteria
	 * @param sorting
	 * @param type
	 * @param dataProvider
	 * @return The generated search based {@link IListHandler}
	 * @throws InvalidCriteriaException When the criteria or the sorting directive
	 *         is not specified.
	 * @throws EmptyListException When the list handler type is
	 *         {@link ListHandlerType#IN_MEMORY} and no matching results exist.
	 * @throws ListHandlerException When the list handler type is
	 *         {@link ListHandlerType#IN_MEMORY} and the sorting directive is
	 *         specified but mal-formed.
	 * @throws IllegalStateException when the list handler type is un-supported.
	 */
	public static IListHandler<?> create(Criteria<?> criteria, Sorting sorting,
			ListHandlerType type, IListingDataProvider dataProvider) throws InvalidCriteriaException, EmptyListException,
			ListHandlerException, IllegalStateException {

		SearchListHandler<?> slh = null;

		switch(type) {

		case IN_MEMORY:
			//return create(dataProvider.find(criteria, null), sorting);
			throw new UnsupportedOperationException();

		case MODELKEY_LIST:
			if(criteria.getCriteriaType().isQuery()) {
				throw new InvalidCriteriaException("Id list handling does not support query based criteria");
			}
			slh = new ModelKeyListHandler(dataProvider, criteria, sorting);
			break;

		case PAGE:
			slh = new PagingSearchListHandler<IEntity>(dataProvider, criteria, sorting);
			break;

		default:
			throw new IllegalStateException("Unhandled list handler type: " + type);
		}

		return slh;
	}
}