/**
 * The Logic Lab
 * @author jpk
 * @since Jan 24, 2010
 */
package com.tabulaw.model;

import com.tabulaw.common.model.IEntity;

/**
 * IEntityFactory - Contract for creating new entities.
 * @param <PK> the primary key type
 * @author jpk
 */
public interface IEntityFactory<PK> {

	/**
	 * Are primary keys generatable? I.e.: are they able to be created in their
	 * entirety <em>before</em> the associated entity is first persisted?
	 * @return true/false
	 */
	boolean isPrimaryKeyGeneratable();

	/**
	 * Creates a new entity instance of the given type.
	 * @param <E>
	 * @param entityClass entity type to create
	 * @param generate set the primary key of the created entity?
	 * @return newly created entity
	 * @throws IllegalStateException Upon error instantiating the entity
	 */
	<E extends IEntity> E createEntity(Class<E> entityClass, boolean generate) throws IllegalStateException;

	/**
	 * Creates a new primary key. This primary key is expected to be set on the
	 * given entity instance and the generated key is returned as a convenience.
	 * <p>
	 * The primary key created by this method is <em>not</em> required to be
	 * "complete" meaning the datastore may need to alter it to establish its
	 * completeness.
	 * <p>
	 * Determination of whether a given enties' primary key was "generated" is
	 * achieved by calling {@link IEntity#isGenerated()}.
	 * @param entity The required entity instance
	 * @return the newly created primary key that was set in the given entity
	 *         returned as a convenience
	 */
	PK generatePrimaryKey(IEntity entity);

	/**
	 * Converts an entity primary key to a String with the added provision that
	 * the resultant string is able to be converted back to the original primary
	 * key behaving exactly like the one given.
	 * @param pk the primary key to convert
	 * @return A full representation of the primary key as a string.
	 */
	String primaryKeyToString(PK pk);

	/**
	 * The reverse of {@link #primaryKeyToString(Object)}
	 * @param s the string to convert to a primary key
	 * @return A primary key
	 */
	PK stringToPrimaryKey(String s);
}
