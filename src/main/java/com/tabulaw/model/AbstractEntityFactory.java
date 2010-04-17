/**
 * The Logic Lab
 * @author jpk
 * @since Jan 24, 2010
 */
package com.tabulaw.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.common.model.IEntity;
import com.tll.util.StringUtil;

/**
 * AbstractEntityFactory
 * @param <PK> the primary key type
 * @author jpk
 */
public abstract class AbstractEntityFactory<PK> implements IEntityFactory<PK> {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * News an entity instance of the given type only.
	 * @param <E>
	 * @param entityClass
	 * @return The newly created entity instance
	 * @throws IllegalStateException When instantiation fails
	 */
	protected final <E extends IEntity> E newEntity(Class<E> entityClass) throws IllegalStateException {
		E entity;
		try {
			entity = entityClass.newInstance();
		}
		catch(final IllegalAccessException iae) {
			throw new IllegalStateException(StringUtil.replaceVariables(
					"Could not access default constructor for entity type: '%1'.", entityClass.getName()), iae);
		}
		catch(final InstantiationException ie) {
			throw new IllegalStateException(StringUtil.replaceVariables("Unable to instantiate the entity: %1", entityClass
					.getName()), ie);
		}

		if(log.isDebugEnabled()) log.debug("Created entity: " + entity);
		return entity;
	}

	@Override
	public <E extends IEntity> E createEntity(Class<E> entityClass, boolean generate) throws IllegalStateException {
		E e = newEntity(entityClass);
		if(generate) generatePrimaryKey(e);
		return e;
	}

}
