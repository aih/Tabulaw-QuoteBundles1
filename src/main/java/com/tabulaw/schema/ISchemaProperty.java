/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Feb 20, 2008
 */
package com.tabulaw.schema;

import com.tabulaw.IMarshalable;

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