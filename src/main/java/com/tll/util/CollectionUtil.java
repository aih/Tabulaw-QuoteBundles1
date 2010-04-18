package com.tll.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities related to array manipulation and management.
 * @author jpk
 */
public abstract class CollectionUtil {

	/**
	 * Returns true if the list has no elements or is null; false otherwise.
	 * @param list the list to check
	 * @return true if the list is empty, false otherwise.
	 */
	public static boolean isEmpty(Collection<?> list) {
		if(list == null || list.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Converts a collection to a list by either casting or creating a concrete
	 * list depending on the type of collection given.
	 * @param <T> The type of the elements in the collection.
	 * @param c the collection. If <code>null</code>, <code>null</code> is
	 *        returned.
	 * @return {@link List} containing the same elements as the given
	 *         {@link Collection} param.
	 */
	public static <T> List<T> listFromCollection(Collection<T> c) {
		if(c == null) return null;
		if(c.size() < 1) {
			return new ArrayList<T>(0);
		}
		return (c instanceof List<?>) ? (List<T>) c : new ArrayList<T>(c);
	}

}
