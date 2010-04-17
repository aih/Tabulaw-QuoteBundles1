package com.tabulaw.common.model;

import com.tll.schema.Managed;

/**
 * EntityBase - Base class for all entities.
 * @author jpk
 */
public abstract class EntityBase implements IEntity {

	private static final long serialVersionUID = -4641847785797486723L;

	private Long id;

	private boolean generated;

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

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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
	public final boolean isGenerated() {
		return generated;
	}

	@Override
	public void setGenerated(Object id) {
		if(id == null) throw new IllegalArgumentException("Generated primary keys can't be null.");
		if(id instanceof Long == false) throw new IllegalStateException();
		setId((Long)id);
		this.generated = true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		final Object sid = getId();
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
		result = prime * result + (int) (version ^ (version >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final EntityBase other = (EntityBase) obj;
		final Object sid = getId(), otherId = other.getId();
		if(sid == null) {
			if(otherId != null) return false;
		}
		else if(!sid.equals(otherId)) return false;
		if(version != other.version) return false;
		return true;
	}

	@Override
	public String toString() {
		return typeDesc() + ", id: " + getId() + ", version: " + getVersion();
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
		return typeDesc() + " (Id: " + getId() + ")";
	}
}
