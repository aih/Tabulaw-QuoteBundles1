package com.tabulaw.common.model;

import com.tabulaw.IDescriptorProvider;

/**
 * NameKey - Simple entity key that holds an entity name and also identifies the
 * field by which that name is retrieved from the entity.
 * @author jpk
 */
public class NameKey implements IDescriptorProvider {

	private static final long serialVersionUID = -3217664978174156618L;

	public static final String DEFAULT_FIELDNAME = "name";

	/**
	 * The name used to identify the field that holds the name.
	 */
	private String nameProperty;

	/**
	 * The actual name value.
	 */
	private String name;

	/**
	 * The entity type.
	 */
	private final String entityType;

	/**
	 * Constructor
	 * @param entityType
	 */
	public NameKey(String entityType) {
		this(entityType, null, DEFAULT_FIELDNAME);
	}

	/**
	 * Constructor
	 * @param entityType
	 * @param name
	 */
	public NameKey(String entityType, String name) {
		this(entityType, name, DEFAULT_FIELDNAME);
	}

	/**
	 * Constructor
	 * @param entityType
	 * @param propertyName
	 * @param name
	 */
	public NameKey(String entityType, String propertyName, String name) {
		if(entityType == null) throw new NullPointerException();
		this.entityType = entityType;
		setNameProperty(propertyName);
		setName(name);
	}

	public String getEntityType() {
		return entityType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The name of the property that identifies the name.
	 */
	public final String getNameProperty() {
		return nameProperty;
	}

	/**
	 * Sets the name of the property that identifies the name.
	 * @param nameProperty
	 */
	public final void setNameProperty(String nameProperty) {
		if(nameProperty == null) throw new IllegalArgumentException("A field name must be specified");
	}

	@Override
	public String descriptor() {
		return getNameProperty() + ": " + getName();
	}

	public void clear() {
		this.name = null;
	}

	public boolean isSet() {
		return name != null;
	}
}
