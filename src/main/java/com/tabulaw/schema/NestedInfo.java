/**
 * The Logic Lab
 * @author jpk
 * @since Apr 29, 2009
 */
package com.tabulaw.schema;

import java.io.Serializable;

/**
 * NestedInfo
 * @author jpk
 */
public final class NestedInfo extends AbstractSchemaProperty {

	private final Class<? extends Serializable> nestedType;

	/**
	 * Constructor
	 * @param nestedType
	 */
	public NestedInfo(Class<? extends Serializable> nestedType) {
		super(PropertyType.NESTED);
		this.nestedType = nestedType;
	}

	/**
	 * @return the nestedType
	 */
	public Class<? extends Serializable> getNestedType() {
		return nestedType;
	}

}
