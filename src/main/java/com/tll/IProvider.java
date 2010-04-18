/**
 * The Logic Lab
 * @author jpk
 * Mar 9, 2009
 */
package com.tll;

/**
 * IProvider
 * @author jpk
 * @param <T> the type
 */
public interface IProvider<T> {

	/**
	 * @return the type instance.
	 */
	T get();
}
