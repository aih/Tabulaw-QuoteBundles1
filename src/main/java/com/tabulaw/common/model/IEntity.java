package com.tabulaw.common.model;

import java.io.Serializable;

import com.tabulaw.IDescriptorProvider;
import com.tabulaw.IMarshalable;
import com.tabulaw.IPropertyValueProvider;
import com.tabulaw.ITypeDescriptorProvider;

/**
 * Contract for a particular model type usable for server and client side.
 * @author jpk
 */
public interface IEntity 
extends Serializable, Cloneable, IMarshalable, IModelKeyProvider, IDescriptorProvider, ITypeDescriptorProvider, IPropertyValueProvider {

	static final String ID_FIELDNAME = "id";

	/**
	 * @return The entity type.
	 */
	String getEntityType();

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
	 * @return A distinct deep copy of all properties that are managed by this
	 *         entity (those that are not referenced).
	 */
	IEntity clone();
}
