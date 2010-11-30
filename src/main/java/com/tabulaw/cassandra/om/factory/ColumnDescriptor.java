package com.tabulaw.cassandra.om.factory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.tabulaw.cassandra.om.factory.exception.HelenaException;


public class ColumnDescriptor {
	private String name;
	
	private PropertyDescriptor property;
	private AccessibleObject annotated;	

	ColumnDescriptor(String name, PropertyDescriptor property, AccessibleObject annotated) {
		this.name = name;
		this.annotated = annotated;
		this.property = property;
	}
	
	public Object getValue(Object object) {
		if (object == null) {
			return null;
		}
		if (property != null) {
			try {
				return property.getReadMethod().invoke(object);
			} catch (IllegalAccessException ex) {
				throw new HelenaException("Bad config!", ex);
			} catch (InvocationTargetException ex) {
				throw new HelenaException("Bad config!", ex);
			}
		}
		if (annotated instanceof Field) {
			Field field = (Field) annotated;
			try {
				return field.get(object);
			} catch (IllegalAccessException ex) {
				throw new HelenaException("Bad config!", ex);
			}
		}
		throw new HelenaException("Bad config!");
	}
	
	public void setValue(Object object, Object value) {
		if (annotated instanceof Field) {
			if (((Field) annotated).getType().isPrimitive() && value == null) {
				return; 
			}
		} else {
			if (((Method) annotated).getReturnType().isPrimitive() && value == null) {
				return;
			}
		}
		if (property != null) {
			try {
				property.getWriteMethod().invoke(object, value);
				return;
			} catch (IllegalAccessException ex) {
				throw new HelenaException("Bad config!", ex);
			} catch (InvocationTargetException ex) {
				throw new HelenaException("Bad config!", ex);
			}
		}
		if (annotated instanceof Field) {
			Field field = (Field) annotated;
			try {
				field.set(object, value);
			} catch (IllegalAccessException ex) {
				throw new HelenaException("Bad config!", ex);
			}
		}
		throw new HelenaException("Bad config!");
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> getType() {
		if (annotated instanceof Field) {
			return ((Field) annotated).getType();
		} else {
			return ((Method) annotated).getReturnType();
		}
	}
	
	public AccessibleObject getAnnotatedObject() {
		return annotated;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object paramObject) {
		if (paramObject == null) {
			return false;
		}
		ColumnDescriptor other = (ColumnDescriptor) paramObject;
		return name.equals(other.name);
	}

}
