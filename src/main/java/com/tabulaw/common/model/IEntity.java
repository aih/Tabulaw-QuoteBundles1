package com.tabulaw.common.model;

import java.io.Serializable;

import com.tll.IDescriptorProvider;
import com.tll.IMarshalable;
import com.tll.ITypeDescriptorProvider;

/**
 * IEntity - Fundamental type for all [server side] entities.
 * @author jpk
 */
public interface IEntity extends Serializable, Cloneable, IMarshalable, IDescriptorProvider, ITypeDescriptorProvider {

	static final String ID_FIELDNAME = "id";
	
	/**
	 * @return The entity type.
	 */
	EntityType getEntityType();
	
	/**
	 * @return The key uniquely identifying the entity.
	 */
	ModelKey getKey();

	/**
	 * @return <code>true</code> if the entity has not yet been persisted.
	 */
	boolean isNew();
	
	/**
	 * @return the version
	 */
	long getVersion();

	/**
	 * @param version the version to set
	 */
	void setVersion(long version);
	
	/**
	 * @return A distinct partially deep copy.
	 */
	IEntity clone();
}
