/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Apr 23, 2008
 */
package com.tabulaw.schema;


/**
 * PropertyType - Generic and basic representation of bean properties such that
 * necessary info is captured for client/server entity/model marshaling.
 * @author jpk
 */
public enum PropertyType {

	STRING(1),
	CHAR(1 << 1),
	ENUM(1 << 2),
	BOOL(1 << 3),
	INT(1 << 4),
	LONG(1 << 5),
	FLOAT(1 << 6),
	DOUBLE(1 << 7),
	DATE(1 << 8), // date and time (java.util.Date)
	
	/**
	 * A non-marshalable unknown object reference.
	 */
	OBJECT(1 << 9),

	STRING_MAP(1 << 10),

	/**
	 * {@link #RELATED_ONE} corres. to a related one relation
	 */
	RELATED_ONE(1 << 11),

	/**
	 * {@link #RELATED_MANY} corres. to a related many type relation
	 */
	RELATED_MANY(1 << 12),

	/**
	 * {@link #INDEXED} corres. to an element referenced by index within a related
	 * many relation
	 */
	INDEXED(1 << 13),

	/**
	 * {@link #NESTED} corres. to a nested element.
	 */
	NESTED(1 << 14);

	/**
	 * {@link #VALUE_TYPES} corres. to non-collection and non-relational types
	 */
	private static final int VALUE_TYPES =
		STRING.flag | CHAR.flag | ENUM.flag | BOOL.flag | INT.flag | LONG.flag | FLOAT.flag | DOUBLE.flag | DATE.flag
		| STRING_MAP.flag | OBJECT.flag;

	/**
	 * {@link #RELATIONAL_TYPES} corres. to those types that represent a relation
	 */
	private static final int RELATIONAL_TYPES = RELATED_ONE.flag | RELATED_MANY.flag;

	/**
	 * {@link #MODEL_TYPES} corres. to types that map to a single model ref.
	 */
	private static final int MODEL_TYPES = RELATED_ONE.flag | INDEXED.flag | NESTED.flag;

	/**
	 * Types able to format correctly on their own
	 */
	private static final int SELF_FORMATTING_TYPES = STRING.flag | ENUM.flag | INT.flag | LONG.flag | CHAR.flag;

	/**
	 * The bit flag
	 */
	private final int flag;

	private PropertyType(int flag) {
		this.flag = flag;
	}

	public boolean isValue() {
		return ((flag & VALUE_TYPES) == flag);
	}

	public boolean isRelational() {
		return ((flag & RELATIONAL_TYPES) == flag);
	}

	public boolean isModelRef() {
		return ((flag & MODEL_TYPES) == flag);
	}

	public boolean isSelfFormatting() {
		return ((flag & SELF_FORMATTING_TYPES) == flag);
	}

	public boolean isNested() {
		return ((flag & NESTED.flag) == flag);
	}
}
