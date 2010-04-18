/**
 * The Logic Lab
 * @author jpk
 * Feb 26, 2009
 */
package com.tll.client.ui.field;

import java.util.Map;

/**
 * AbstractDataField - A field that limits its value to a collection of values.
 * @param <E> the element (atomic) value type
 * @param <V> the field value type
 * @author jpk
 */
public abstract class AbstractDataField<E, V> extends AbstractField<V> {

	/**
	 * The data map of presentation worthy tokens keyed by the value.
	 */
	private Map<E, String> data;

	/**
	 * Constructor
	 * @param name
	 * @param propName
	 * @param labelText
	 * @param helpText
	 */
	public AbstractDataField(String name, String propName, String labelText, String helpText) {
		super(name, propName, labelText, helpText);
	}

	/**
	 * Set or reset the field data. <br>
	 * @param data Map of value/name pairs keyed by <em>value</em> where each key
	 *        holds the token for use in the ui.
	 */
	public void setData(Map<E, String> data) {
		this.data = data;
	}
	
	/**
	 * @return the data map
	 */
	protected final Map<E, String> getData() {
		return data;
	}

	/**
	 * Adds a single value/name data item.
	 * @param name the presentation name
	 * @param value the field value
	 */
	public void addDataItem(String name, E value) {
		data.put(value, name);
	}

	/**
	 * Removes a single data item
	 * @param value the field value data item value to remove
	 */
	public void removeDataItem(E value) {
		data.remove(value);
	}

	public final String getToken(E value) {
		return data.get(value);
	}

	public final E getDataValue(String key) {
		for(final E val : data.keySet()) {
			if(data.get(val).equals(key)) {
				return val;
			}
		}
		throw new IllegalArgumentException();
	}
}
