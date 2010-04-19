package com.tabulaw.schema;

import com.tabulaw.IMarshalable;

/**
 * QueryParam - Needed to resolve query param types.
 * @author jpk
 */
public interface IQueryParam extends IPropertyNameProvider, IMarshalable {

	/**
	 * @return The property type of the query param necessary for server side type
	 *         coercion.
	 */
	PropertyType getType();

	/**
	 * @return The property value.
	 */
	Object getValue();
}