/**
 * The Logic Lab
 * @author jpk
 * @since Mar 14, 2010
 */
package com.tabulaw.model;

import com.tabulaw.common.model.IEntity;
import com.tabulaw.schema.Extended;
import com.tabulaw.schema.Root;

/**
 * IEntity's metadata definition.
 * @author jpk
 */
public class EntityMetadata implements IEntityMetadata {

	@Override
	public Class<?> getRootEntityClass(Class<?> entityClass) {
		if(entityClass.getAnnotation(Extended.class) != null) {
			Class<?> ec = entityClass;
			do {
				ec = ec.getSuperclass();
			} while(ec != null && ec.getAnnotation(Root.class) == null);
			if(ec != null) return ec;
		}
		return entityClass;
	}

	@Override
	public Class<?> getEntityClass(Object entity) {
		if(entity instanceof IEntity) {
			//return ((IEntity) entity).entityClass();
			return entity.getClass();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getEntityInstanceDescriptor(Object entity) {
		if(entity instanceof IEntity) {
			return ((IEntity) entity).descriptor();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getEntityTypeDescriptor(Object entity) {
		if(entity instanceof IEntity) {
			return ((IEntity) entity).typeDesc();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public boolean isEntityType(Class<?> claz) {
		return IEntity.class.isAssignableFrom(claz);
	}
}
