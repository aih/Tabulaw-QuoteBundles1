/**
 * 
 */
package com.tabulaw.dao;

import java.io.Serializable;
import java.util.Comparator;

import com.tabulaw.IPropertyValueProvider;

/**
 * A {@link Comparator} based on a {@link SortColumn}.
 * <p>
 * Threadsafe.
 * @param <T> The {@link Comparator} type.
 * @author jpk
 */
@SuppressWarnings("unchecked")
public final class SortColumnComparator<T> implements Comparator<T>, Serializable {

	static final long serialVersionUID = 5681155061446144523L;

	/**
	 * The {@link SortColumn} dictating the ordinality when comparing.
	 */
	private final SortColumn sortColumn;

	/**
	 * Constructor
	 * @param sortColumn
	 */
	public SortColumnComparator(final SortColumn sortColumn) {
		super();
		if(sortColumn == null) throw new NullPointerException();
		this.sortColumn = sortColumn;
	}

	public int compare(final T o1, final T o2) {

		int rval = 0;

		String propPath = sortColumn.getPropertyName();
		Object v1, v2;
		if(o1 instanceof IPropertyValueProvider && o2 instanceof IPropertyValueProvider) {
				v1 = ((IPropertyValueProvider) o1).getPropertyValue(propPath);
				v2 = ((IPropertyValueProvider) o2).getPropertyValue(propPath);
		}
		else throw new IllegalStateException();

		if(v1 == null && v2 == null) {
			return 0;
		}
		if(v1 == null) {
			return sortColumn.isAscending() ? -1 : 1;
		}
		if(v2 == null) {
			return sortColumn.isAscending() ? 1 : -1;
		}

		if(v1 instanceof String && v2 instanceof String) {
			if(Boolean.TRUE.equals(this.sortColumn.getIgnoreCase())) {
				rval = ((String) v1).compareToIgnoreCase((String) v2);
			}
			else {
				rval = ((String) v1).compareTo((String) v2);
			}
		}
		else if(v1 instanceof Comparable) {
			rval = ((Comparable) v1).compareTo(v2);
		}
		else if(v1 instanceof Number) {
			final Double d1 = new Double(((Number) v1).doubleValue());
			final Double d2 = new Double(((Number) v2).doubleValue());
			rval = d1.compareTo(d2);
		}
		else if(v1 instanceof Boolean) {
			final Boolean b1 = (Boolean) v1;
			final Boolean b2 = (Boolean) v2;

			if(b1.booleanValue() == b2.booleanValue())
				rval = 0;
			else {
				rval = (b1.booleanValue() == true) ? 1 : -1;
			}
		}

		else
			throw new IllegalStateException("Unhandled bean property type: " + v1.getClass());

		return sortColumn.isAscending() ? rval : (rval == 0) ? 0 : (rval > 0) ? -1 : +1;
	}
}