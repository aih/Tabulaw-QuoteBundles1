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
	private int version = -1;
	
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

	@Override
	public abstract IEntity clone();

	@Override
	public ModelKey getModelKey() {
		return new ModelKey(getEntityType().name(), getId());
	}

	@Managed
	@Override
	public final int getVersion() {
		return version;
	}

	@Override
	public final void setVersion(int version) {
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
		return typeDesc() + " (" + getId() + ")";
	}

	@Override
	public String toString() {
		return typeDesc() + ", key: " + getId() + ", version: " + getVersion();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		String id = getId();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		EntityBase other = (EntityBase) obj;
		String id = getId(), otherId = other.getId();
		if(id == null) {
			if(otherId != null) return false;
		}
		else if(!id.equals(otherId)) return false;
		return true;
	}
	
}
