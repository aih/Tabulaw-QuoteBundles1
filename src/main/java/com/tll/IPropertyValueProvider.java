/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tll;

/**
 * Provides values for property paths.
 * @author jpk
 */
public interface IPropertyValueProvider {

	/**
	 * @param propertyPath
	 * @return
	 */
	Object getPropertyValue(String propertyPath);
}
