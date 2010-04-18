/**
 * The Logic Lab
 * @author jpk
 * Feb 1, 2009
 */
package com.tll.model.bk;

import com.tll.key.IKey;

/**
 * IBusinessKey
 * @param <E> The entity type (not necessarily an IEntity)
 * @author jpk
 */
public interface IBusinessKey<E> extends IBusinessKeyDefinition<E>, IKey<E> {

	/**
	 * Get the property value given a property name.
	 * @param propertyName
	 * @return The property value
	 */ 
	Object getPropertyValue(String propertyName);

	/**
	 * Get the property value given a property index.
	 * @param index
	 * @return The property value
	 */
	Object getPropertyValue(int index);

	/**
	 * Set a property given its name and value.
	 * @param propertyName
	 * @param value
	 */
	void setPropertyValue(String propertyName, Object value);

	/**
	 * Set a property value given its index and value.
	 * @param index
	 * @param value
	 */
	void setPropertyValue(int index, Object value);

	/**
	 * Clear the state of this key resetting all defining properties to their
	 * default values.
	 */
	void clear();
}
