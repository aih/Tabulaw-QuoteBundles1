package com.tll.tabulaw.model;

import com.google.inject.Inject;
import com.tll.model.IEntity;
import com.tll.model.IEntityAssembler;
import com.tll.model.IEntityFactory;
import com.tll.model.IEntityProvider;

/**
 * EntityAssembler
 * @author jpk
 */
public final class EntityAssembler implements IEntityAssembler {

	private final IEntityFactory<?> entityFactory;

	/**
	 * Constructor
	 * @param entityFactory required
	 */
	@Inject
	public EntityAssembler(IEntityFactory<?> entityFactory) {
		super();
		this.entityFactory = entityFactory;
	}

	private <E extends IEntity> E createEntity(Class<E> entityClass) {
		return entityFactory.createEntity(entityClass, true);
	}

	public <E extends IEntity> E assembleEntity(Class<E> entityType, IEntityProvider entityProvider) {
		/*
		E e = null;
		if(User.class.equals(entityType)) {
			final User ae = createEntity(User.class);
			e = (E) ae;
		}

		else
			throw new IllegalArgumentException("Unsupported entity type '" + entityType + "' for assembly");
		*/
		return createEntity(entityType);
	}
}