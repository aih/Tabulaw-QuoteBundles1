/**
 * The Logic Lab
 * @author jpk
 * @since Mar 14, 2009
 */
package com.tabulaw.common.model;

import java.io.Serializable;

import com.tabulaw.IDescriptorProvider;
import com.tabulaw.IMarshalable;

/**
 * ModelKey
 * @author jpk
 */
@SuppressWarnings("serial")
public class ModelKey implements IDescriptorProvider, IMarshalable, Serializable {

	/**
	 * The entity type.
	 */
	private String type;

	/**
	 * The entity identifier.
	 */
	private String id;

	/**
	 * The entity name.
	 */
	private String name;

	/**
	 * Constructor
	 */
	public ModelKey() {
		super();
	}

	/**
	 * Constructor
	 * @param type the entity type
	 * @param id the entity identifier
	 */
	public ModelKey(String type, String id) {
		this(type, id, null);
	}

	/**
	 * Constructor
	 * @param type the entity type
	 * @param id the entity identifier
	 * @param name optional name
	 */
	public ModelKey(String type, String id, String name) {
		setEntityType(type);
		setId(id);
		setName(name);
	}

	public String getEntityType() {
		return type;
	}

	public void setEntityType(String type) {
		if(type == null) throw new NullPointerException();
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void clear() {
		id = name = null;
	}

	public boolean isSet() {
		return type != null && id != null;
	}

	@Override
	public String descriptor() {
		return isSet() ? (name != null) ? type + " '" + name + '\'' : type : "-unset-";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final ModelKey other = (ModelKey) obj;
		if(id == null) {
			if(other.id != null) return false;
		}
		else if(!id.equals(other.id)) return false;
		if(type == null) {
			if(other.type != null) return false;
		}
		else if(!type.equals(other.type)) return false;
		return true;
	}

	@Override
	public String toString() {
		return (name != null) ? type + " '" + name + "' (Id: " + id + ")" : type + " (Id: " + id + ")";
	}
}
