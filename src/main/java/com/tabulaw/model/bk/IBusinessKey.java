/**
 * The Logic Lab
 * @author jpk
 * Feb 1, 2009
 */
package com.tabulaw.model.bk;

import java.io.Serializable;

import com.tabulaw.IDescriptorProvider;
import com.tabulaw.IMarshalable;

/**
 * IBusinessKey
 * @param <E> The entity type (not necessarily an IEntity)
 * @author jpk
 */
public interface IBusinessKey<E> extends IBusinessKeyDefinition<E>, IDescriptorProvider, IMarshalable, Serializable {

	/**
	 * @return The type of object to which this key refers.
	 */
	Class<E> getType();

	/**
	 * @return <code>true</code> if the defining key properties have been set.
	 */
	boolean isSet();

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
