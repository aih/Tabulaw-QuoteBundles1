/**
 * The Logic Lab
 * @author jpk
 * Jan 21, 2009
 */
package com.tabulaw.criteria;

import com.tll.schema.IQueryParam;
import com.tll.schema.PropertyType;


/**
 * QueryParam
 * @author jpk
 */
public class QueryParam implements IQueryParam {

	private final String propertyName;
	private final PropertyType type;
	private final Object value;

	/**
	 * Constructor
	 * @param propertyName
	 * @param type
	 * @param value
	 */
	public QueryParam(String propertyName, PropertyType type, Object value) {
		super();
		this.propertyName = propertyName;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}
	
	@Override
	public PropertyType getType() {
		return type;
	}

	@Override
	public Object getValue() {
		return value;
	}
}
