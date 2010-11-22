/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Jan 6, 2009
 */
package com.tabulaw.schema;

/**
 * IPropertyMetadataProvider - Indicates the ability to provide
 * {@link PropertyMetadata}.
 * @author jpk
 */
public interface IPropertyMetadataProvider {

	/**
	 * @param propPath The property path identifying the property for which to
	 *        provide metadata.
	 * @return The {@link PropertyMetadata} or <code>null</code> if none found at
	 *         the given path.
	 */
	PropertyMetadata getPropertyMetadata(String propPath);
}
