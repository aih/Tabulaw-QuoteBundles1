/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tabulaw;

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
