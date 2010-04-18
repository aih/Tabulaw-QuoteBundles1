/*
 * The Logic Lab
 */
package com.tll.key;

import java.io.Serializable;

import com.tll.IDescriptorProvider;
import com.tll.IMarshalable;

/**
 * IKey - Abstraction serving as an identifier to a particular instance of a
 * particular type.
 * @param <T> the key type
 * @author jpk
 */
public interface IKey<T> extends IDescriptorProvider, IMarshalable, Serializable {

	/**
	 * @return The type of object to which this key refers.
	 */
	Class<T> getType();

	/**
	 * @return <code>true</code> if the defining key properties have been set.
	 */
	boolean isSet();

	/**
	 * Clear the state of this key resetting all defining properties to their
	 * default values.
	 */
	void clear();
}
