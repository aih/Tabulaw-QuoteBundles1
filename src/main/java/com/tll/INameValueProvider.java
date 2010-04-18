package com.tll;

/**
 * INameValueProvider - Definition for providing a name and an associated Object
 * value.
 * @author jpk
 * @param <V> The value type.
 */
public interface INameValueProvider<V> {

	/**
	 * @return The ascribed name.
	 */
	String getName();

	/**
	 * @return The value.
	 */
	V getValue();

}