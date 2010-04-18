/**
 * The Logic Lab
 * @author jpk
 * Feb 20, 2008
 */
package com.tll.schema;

import com.tll.IMarshalable;

/**
 * ISchemaProperty
 * @author jpk
 */
public interface ISchemaProperty extends IMarshalable {

	/**
	 * @return The property type
	 */
	PropertyType getPropertyType();
}