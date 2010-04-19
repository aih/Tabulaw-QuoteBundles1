/**
 * The Logic Lab
 * @author jpk
 * Apr 23, 2008
 */
package com.tabulaw.schema;

/**
 * AbstractSchemaProperty
 * @author jpk
 */
public abstract class AbstractSchemaProperty implements ISchemaProperty {

	private PropertyType propertyType;

	/**
	 * Constructor - Needed for GWT compile
	 */
	protected AbstractSchemaProperty() {
		super();
	}

	/**
	 * Constructor
	 * @param propertyType
	 */
	protected AbstractSchemaProperty(final PropertyType propertyType) {
		super();
		if(propertyType == null) {
			throw new IllegalArgumentException("A property type must be specified.");
		}
		this.propertyType = propertyType;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}
}
