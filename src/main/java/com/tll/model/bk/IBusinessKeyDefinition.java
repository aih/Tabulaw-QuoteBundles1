package com.tll.model.bk;

/**
 * IBusinessKeyDefinition - Defines a business key for given entity type.
 * @author jpk
 * @param <E> the entity type to which this definition applies (not necessarily
 *        an IEntity)
 */
public interface IBusinessKeyDefinition<E> {

	/**
	 * @return The entity type
	 */
	Class<E> getType();

	/**
	 * @return The business key name.
	 */
	String getBusinessKeyName();

	/**
	 * @return The OGNL formatted property names that make up this business key.
	 */
	String[] getPropertyNames();
}