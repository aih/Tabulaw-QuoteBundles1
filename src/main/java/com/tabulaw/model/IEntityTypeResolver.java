/**
 * The Logic Lab
 * @author jpk
 * @since May 15, 2009
 */
package com.tabulaw.model;

import com.tabulaw.common.model.IEntity;

/**
 * IEntityTypeResolver
 * @author jpk
 */
public interface IEntityTypeResolver {

	/**
	 * Resolves the given {@link Class} to a token.
	 * @param clz the entity class type
	 * @return the resolved token
	 * @throws IllegalArgumentException when the entity class type can't be
	 *         resolved.
	 */
	String resolveEntityType(Class<? extends IEntity> clz) throws IllegalArgumentException;

	/**
	 * Resolves the given token to a corres. entity class.
	 * @param entityType the entity type
	 * @return the resolved entity class type
	 * @throws IllegalArgumentException when the entity type can't be resolved.
	 */
	Class<? extends IEntity> resolveEntityClass(String entityType) throws IllegalArgumentException;
}
