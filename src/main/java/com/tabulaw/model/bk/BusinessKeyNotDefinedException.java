package com.tabulaw.model.bk;

/**
 * BusinessKeyNotDefinedException
 * @author jpk
 */
public class BusinessKeyNotDefinedException extends Exception {

	private static final long serialVersionUID = -695847771421395166L;

	/**
	 * Constructor - Use when <em>no</em> business keys are defined for the given
	 * entity type.
	 * @param entityClass The entity type
	 */
	public BusinessKeyNotDefinedException(Class<?> entityClass) {
		super("No business keys exist for  " + entityClass.getName());
	}

	/**
	 * Constructor - Use when no business key is found for the given entity type
	 * and business key name.
	 * @param entityClass The entity type
	 * @param businessKeyName The business key name
	 */
	public BusinessKeyNotDefinedException(Class<?> entityClass, String businessKeyName) {
		super("No business key of name '" + businessKeyName + "' is defined for " + entityClass.getName());
	}
}
