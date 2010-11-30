/*
 * The MIT License
 *
 * Copyright (c) 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 * This file is part of HelenaORM.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tabulaw.cassandra.om;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;


import com.google.common.collect.ImmutableMap;
import com.tabulaw.cassandra.om.factory.exception.HelenaException;
import com.tabulaw.cassandra.om.mappings.AbstractStringBasedTypeMapping;
import com.tabulaw.cassandra.om.mappings.BooleanTypeMapping;
import com.tabulaw.cassandra.om.mappings.DateTypeMapping;
import com.tabulaw.cassandra.om.mappings.IntegerTypeMapping;
import com.tabulaw.cassandra.om.mappings.LongTypeMapping;
import com.tabulaw.cassandra.om.mappings.StringTypeMapping;
import com.tabulaw.cassandra.om.mappings.URITypeMapping;
import com.tabulaw.cassandra.om.mappings.UUIDTypeMapping;

public class TypeConverter {
	public static final TypeConverter INSTANCE = new TypeConverter(
	    ImmutableMap.<Class<?>, TypeMapping<?>>of(
          String.class, new StringTypeMapping(),
          Boolean.class, new BooleanTypeMapping(),
          Long.class, new LongTypeMapping(),
          Integer.class, new IntegerTypeMapping(),
          Date.class, new DateTypeMapping()
	    ),
	    SerializeUnknownClasses.YES
	);
	
	private static final byte[] EMPTY_BYTES = new byte[0];
	private static final ImmutableMap<Class<?>, Class<?>> _typeReplacition = 
		ImmutableMap.<Class<?>, Class<?>>of(
				int.class, Integer.class,
				long.class, Long.class,
				boolean.class, Boolean.class
		);

	private final ImmutableMap<Class<?>, TypeMapping<?>> _typeMappings;
	private final SerializeUnknownClasses _serializationPolicy;

	public TypeConverter(final ImmutableMap<Class<?>, TypeMapping<?>> typeMappings,
			final SerializeUnknownClasses serializationPolicy) {
		_serializationPolicy = serializationPolicy;
		_typeMappings = typeMappings;
	}

	public byte[] asByteArray(final Object propertyValue) {
		if(propertyValue == null) {
			return EMPTY_BYTES;
		}
		if(_typeMappings.containsKey(propertyValue.getClass())) {
			return _typeMappings.get(propertyValue.getClass()).toBytes(propertyValue);
		}
		if(Enum.class.isAssignableFrom(propertyValue.getClass())) {
			return stringToBytes(((Enum<?>) propertyValue).name());
		}
		if((propertyValue instanceof Serializable || propertyValue instanceof Collection<?>) && _serializationPolicy == SerializeUnknownClasses.YES) {
			return serialize(propertyValue);
		}

		throw new HelenaException("Can not map " + propertyValue.getClass()
				+ " instance to byte array, either implement serializable or create a custom type mapping!");
	}

	@SuppressWarnings("unchecked")
	public String asString(final Object propertyValue) {
		if(propertyValue == null) {
			return "";
		}
		if(_typeMappings.containsKey(propertyValue.getClass())) {
			TypeMapping<?> mapping = _typeMappings.get(propertyValue.getClass());
			if(mapping instanceof AbstractStringBasedTypeMapping<?>) {
				return ((AbstractStringBasedTypeMapping) mapping).asString(propertyValue);
			}
			throw new HelenaException("Can not map " + propertyValue.getClass()
					+ " instance to byte array, either implement serializable or create a custom type mapping!");
		}
		if(Enum.class.isAssignableFrom(propertyValue.getClass())) {
			return ((Enum<?>) propertyValue).name();
		}
		throw new HelenaException("Can not map " + propertyValue.getClass()
				+ " instance to byte array, either implement serializable or create a custom type mapping!");
	}
	
	@SuppressWarnings("unchecked")
	public Object asObject(Class<?> returnType, String value) {
		if(value == null || value.isEmpty()) {
			return null;
		}
		if (_typeReplacition.containsKey(returnType)) {
			returnType = _typeReplacition.get(returnType);
		}
		if(_typeMappings.containsKey(returnType)) {
			TypeMapping<?> mapping = _typeMappings.get(returnType);
			if(mapping instanceof AbstractStringBasedTypeMapping<?>) {
				return ((AbstractStringBasedTypeMapping) mapping).fromString(value);
			}
			throw new HelenaException("Can not map " + returnType
					+ " instance to byte array, either implement serializable or create a custom type mapping!");
		}
		if(Enum.class.isAssignableFrom(returnType)) {
			return Enum.valueOf((Class) returnType, value);
		}
		throw new HelenaException("Can not map " + returnType
				+ " instance to byte array, either implement serializable or create a custom type mapping!");
	}	

	private byte[] serialize(final Object propertyValue) {
		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final ObjectOutputStream oout = new ObjectOutputStream(out);

			oout.writeObject(propertyValue);
			oout.close();

			return out.toByteArray();
		}
		catch(final IOException e) {
			throw new HelenaException("Unable to Serialize object of type " + propertyValue.getClass(), e);
		}
	}

	String bytesToString(final byte[] bytes) {
		return (String) _typeMappings.get(String.class).fromBytes(bytes);
	}

	byte[] stringToBytes(final String string) {
		return _typeMappings.get(String.class).toBytes(string);
	}

	public Object asObject(Class<?> returnType, final byte[] value) {
		if (_typeReplacition.containsKey(returnType)) {
			returnType = _typeReplacition.get(returnType);
		}
		if(_typeMappings.containsKey(returnType)) {
			return returnType.cast(_typeMappings.get(returnType).fromBytes(value));
		}
		if(returnType.isEnum()) {
			return makeEnumInstance(returnType, value);
		}
		if(Serializable.class.isAssignableFrom(returnType) || Collection.class.isAssignableFrom(returnType)) {
			return returnType.cast(deserialize(value));
		}
		throw new HelenaException("Can not handle type " + returnType.getClass()
				+ ", maybe you have getters and setters with different Types? Otherwise, add a Type mapping");
	}

	private Enum<?> makeEnumInstance(final Class<?> returnType, final byte[] value) {
		try {
			final Method method = returnType.getMethod("valueOf", String.class);

			return (Enum<?>) method.invoke(returnType, bytesToString(value));
		}
		catch(final SecurityException e) {
			throw new HelenaException(e);
		}
		catch(final NoSuchMethodException e) {
			throw new HelenaException(e);
		}
		catch(final IllegalArgumentException e) {
			throw new HelenaException(e);
		}
		catch(final IllegalAccessException e) {
			throw new HelenaException(e);
		}
		catch(final InvocationTargetException e) {
			throw new HelenaException(e);
		}
	}

	private Object deserialize(final byte[] value) {
		final ByteArrayInputStream in = new ByteArrayInputStream(value);
		try {
			final ObjectInputStream oin = new ObjectInputStream(in);
			final Object retval = oin.readObject();

			oin.close();

			return retval;
		}
		catch(final IOException e) {
			throw new HelenaException(e);
		}
		catch(final ClassNotFoundException e) {
			throw new HelenaException(e);
		}
	}
}
