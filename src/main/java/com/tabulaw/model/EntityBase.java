package com.tabulaw.model;

import javax.xml.bind.annotation.XmlTransient;

import com.tabulaw.cassandra.om.annotations.HelenaSuperclass;
import com.tabulaw.schema.Managed;
import com.tabulaw.util.StringUtil;

/**
 * EntityBase - Base class for all entities.
 * @author jpk
 */
@HelenaSuperclass
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
		return StringUtil.enumStyleToPresentation(getEntityType());
	}

	/**
	 * @return A new empty instance via the default constructor.
	 */
	protected abstract IEntity newInstance();

	/**
	 * Sets the properties on the given clone instance
	 * @param cln the clone instance
	 */
	protected void doClone(IEntity cln) {
		((EntityBase) cln).version = version;
	}

	@Override
	public final IEntity clone() {
		IEntity cln = newInstance();
		doClone(cln);
		return cln;
	}

	@Override
	public ModelKey getModelKey() {
		return new ModelKey(getEntityType(), getId());
	}

	@Managed
	@Override
	@XmlTransient
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
	public final String toString() {
		return typeDesc() + ", id: " + getId() + ", version: " + getVersion();
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		String id = getId();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		EntityBase other = (EntityBase) obj;
		String id = getId(), otherId = other.getId();
		if(id == null || otherId == null) throw new IllegalStateException();
		if(!id.equals(otherId)) return false;
		return true;
	}

	/*
	 * Sub-classes may override.
	 */
	@Override
	public Object getPropertyValue(String propertyPath) {
		throw new UnsupportedOperationException();
	}
}
