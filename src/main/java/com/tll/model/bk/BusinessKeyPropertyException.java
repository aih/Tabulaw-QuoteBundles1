package com.tll.model.bk;

import com.tll.ApplicationException;

/**
 * BusinessKeyPropertyException - When a business key is applied to an entity
 * and a particular business key property can't be set.
 * @author jpk
 */
public class BusinessKeyPropertyException extends ApplicationException {
	private static final long serialVersionUID = 7562515596447976158L;

	/**
	 * Constructor
	 * @param entityClass
	 * @param businessKeyName
	 * @param businessKeyPropertyName
	 */
	public BusinessKeyPropertyException(Class<?> entityClass, String businessKeyName,
			String businessKeyPropertyName) {
		super("Unable to access business key property: " + businessKeyPropertyName + " for " + businessKeyName + " of "
				+ entityClass);
	}
}
