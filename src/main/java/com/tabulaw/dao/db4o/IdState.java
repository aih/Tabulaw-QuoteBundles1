/**
 * The Logic Lab
 * @author jpk
 * @since Jan 24, 2010
 */
package com.tabulaw.dao.db4o;

import java.util.HashMap;
import java.util.Map;

import com.tabulaw.common.model.EntityBase;
import com.tabulaw.common.model.EntityType;

/**
 * IdState - An entity solely for holding the state of the current primary keys
 * by entity type.
 * @author jpk
 */
public class IdState extends EntityBase {

	private static final long serialVersionUID = 5319169153141045247L;
	
	/**
	 * Holds the next primary key for root entity types.
	 */
	private final Map<Class<?>, Long> idMap = new HashMap<Class<?>, Long>();

	@Override
	public EntityType getEntityType() {
		throw new UnsupportedOperationException();
	}

	public Long getCurrentId(Class<?> entityType) {
		return idMap.get(entityType);
	}

	public void setCurrentId(Class<?> entityType, Long currentId) {
		idMap.put(entityType, currentId);
	}
}
