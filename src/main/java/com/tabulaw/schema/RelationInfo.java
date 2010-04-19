/**
 * The Logic Lab
 * @author jpk
 * Feb 20, 2008
 */
package com.tabulaw.schema;


/**
 * RelationInfo - Defines key information about a defined relation (betw. 2
 * entities) in the schema.
 * @author jpk
 */
public final class RelationInfo extends AbstractSchemaProperty {

	private final Class<?> relatedType;

	/**
	 * Is this relation pointing to an entity or collection of entities that
	 * manage their own life-cycle?
	 */
	private final boolean reference;

	/**
	 * Constructor
	 * @param relatedType
	 * @param propertyType
	 * @param reference
	 */
	public RelationInfo(final Class<?> relatedType, final PropertyType propertyType,
			final boolean reference) {
		super(propertyType);
		this.relatedType = relatedType;
		this.reference = reference;
	}

	public Class<?> getRelatedType() {
		return relatedType;
	}

	public boolean isReference() {
		return reference;
	}
}
