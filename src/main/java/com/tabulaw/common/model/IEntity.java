package com.tabulaw.common.model;

import java.io.Serializable;

import com.tll.IDescriptorProvider;
import com.tll.IMarshalable;
import com.tll.IPropertyValueProvider;
import com.tll.ITypeDescriptorProvider;

/**
 * IEntity - Fundamental type for all [server side] entities.
 * @author jpk
 */
public interface IEntity 
extends Serializable, Cloneable, IMarshalable, IModelKeyProvider, 
IDescriptorProvider, ITypeDescriptorProvider, IPropertyValueProvider {

	static final String ID_FIELDNAME = "id";

	/**
	 * @return The entity type.
	 */
	EntityType getEntityType();

	/**
	 * @return the id.
	 */
	String getId();
	
	/**
	 * Set the id.
	 * @param id
	 */
	void setId(String id);

	/**
	 * @return <code>true</code> if the entity has not yet been persisted.
	 */
	boolean isNew();

	/**
	 * @return the version
	 */
	int getVersion();

	/**
	 * @param version the version to set
	 */
	void setVersion(int version);

	/**
	 * @return A distinct partially deep copy.
	 */
	IEntity clone();
}
