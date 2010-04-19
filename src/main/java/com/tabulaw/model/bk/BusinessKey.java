package com.tabulaw.model.bk;

import java.util.Arrays;

/**
 * Abstract base class for all business keys in the application.
 * @author jpk
 * @param <E>
 */
final class BusinessKey<E> implements IBusinessKey<E> {

	private static final long serialVersionUID = 2415120120614040086L;

	private final Class<E> type;
	private final String businessKeyName;
	private final String[] propertyNames;
	private Object[] propertyValues;

	/**
	 * Constructor
	 * @param def The business key definition
	 */
	public BusinessKey(IBusinessKeyDefinition<E> def) {
		if(def.getType() == null) throw new IllegalArgumentException("A key type must be specified.");
		this.type = def.getType();
		this.businessKeyName = def.getBusinessKeyName();
		this.propertyNames = def.getPropertyNames();
		clear();
	}

	/**
	 * Constructor
	 * @param def The business key definition
	 * @param propertyValues The propertyValues array
	 */
	public BusinessKey(IBusinessKeyDefinition<E> def, Object[] propertyValues) {
		this(def);
		copyValues(propertyValues);
	}
	
	@Override
	public Class<E> getType() {
		return type;
	}

	@Override
	public String getBusinessKeyName() {
		return businessKeyName;
	}

	@Override
	public String descriptor() {
		return getType().getName() + " " + getBusinessKeyName();
	}

	/**
	 * @return The field names
	 */
	@Override
	public String[] getPropertyNames() {
		return propertyNames;
	}

	private void copyValues(Object[] values) {
		for(int i = 0; i < this.propertyValues.length; ++i) {
			this.propertyValues[i] = values[i];
		}
	}

	private int propertyIndex(String propertyName) {
		for(int i = 0; i < propertyNames.length; ++i) {
			final String fname = propertyNames[i];
			if(fname != null && fname.equals(propertyName)) return i;
		}
		return -1;
	}

	@Override
	public Object getPropertyValue(String propertyName) {
		final int index = propertyIndex(propertyName);
		return (index == -1) ? null : propertyValues[index];
	}

	@Override
	public Object getPropertyValue(int index) {
		return propertyValues[index];
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) {
		final int index = propertyIndex(propertyName);
		if(index != -1) {
			propertyValues[index] = value;
		}
	}

	@Override
	public void setPropertyValue(int index, Object value) {
		propertyValues[index] = value;
	}

	@Override
	public void clear() {
		this.propertyValues = new Object[propertyNames.length];
	}

	@Override
	public boolean isSet() {
		for(final Object obj : propertyValues) {
			if(obj == null) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.toString().hashCode());
		result = prime * result + ((businessKeyName == null) ? 0 : businessKeyName.hashCode());
		result = prime * result + Arrays.hashCode(propertyNames);
		result = prime * result + Arrays.hashCode(propertyValues);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(getClass() != obj.getClass()) return false;
		BusinessKey<?> other = (BusinessKey<?>) obj;
		if(type == null) {
			if(other.type != null) return false;
		}
		else if(!type.equals(other.type)) return false;
		if(businessKeyName == null) {
			if(other.businessKeyName != null) return false;
		}
		else if(!businessKeyName.equals(other.businessKeyName)) return false;
		if(!Arrays.equals(propertyNames, other.propertyNames)) return false;
		if(!Arrays.equals(propertyValues, other.propertyValues)) return false;
		return true;
	}

	@Override
	public String toString() {
		return getBusinessKeyName();
	}
}
