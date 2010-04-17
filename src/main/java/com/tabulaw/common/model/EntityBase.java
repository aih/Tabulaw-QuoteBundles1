package com.tabulaw.common.model;

import com.tll.schema.Managed;

/**
 * EntityBase - Base class for all entities.
 * @author jpk
 */
public abstract class EntityBase implements IEntity {

	private static final long serialVersionUID = -4641847785797486723L;

	/**
	 * At object creation, a version of <code>-1</code> is assigined indicating a
	 * <em>transient</em> (not persisted yet) entity.
	 */
	private long version = -1;

	/**
	 * Constructor
	 */
	public EntityBase() {
		super();
	}

	@Override
	public String typeDesc() {
		return getEntityType().descriptor();
	}

	/**
	 * @return The entity impl dependent identifier token.
	 */
	protected abstract String getId();

	@Override
	public abstract IEntity clone();

	@Override
	public final ModelKey getKey() {
		return new ModelKey(getEntityType().name(), getId());
	}

	@Managed
	@Override
	public final long getVersion() {
		return version;
	}

	@Override
	public final void setVersion(long version) {
		this.version = version;
	}

	@Override
	public final boolean isNew() {
		return version == -1;
	}

	/*
	 * May be overridden by sub-classes for a better descriptor.
	 */
	@Override
	public String descriptor() {
		return typeDesc() + " (" + getKey() + ")";
	}

	@Override
	public String toString() {
		return typeDesc() + ", key: " + getKey() + ", version: " + getVersion();
	}
}
