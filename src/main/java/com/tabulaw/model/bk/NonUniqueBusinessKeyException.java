/**
 * The Logic Lab
 * @author jpk
 * Feb 1, 2009
 */
package com.tabulaw.model.bk;

/**
 * NonUniqueBusinessKeyException - Indicates an entity is non-unique by business
 * key.
 * @author jpk
 */
public class NonUniqueBusinessKeyException extends Exception {

	private static final long serialVersionUID = 706689060398655969L;

	/**
	 * Constructor
	 * @param bk The non-unique business key
	 */
	public NonUniqueBusinessKeyException(IBusinessKey<?> bk) {
		super("Non unique: " + bk.descriptor());
	}
}
