package com.tabulaw.common.model;

/**
 * Interface for entities that have names.
 * @author jpk
 */
public interface INamedEntity extends IEntity {

	/**
	 * The name of the property holding the name.
	 */
	public static final String NAME = "name";

	/**
	 * Returns the name for this entity.
	 * @return the name of this entity
	 */
	String getName();

	/**
	 * Sets the name for this entity.
	 * @param name the name of this entity
	 */
	void setName(String name);
}
