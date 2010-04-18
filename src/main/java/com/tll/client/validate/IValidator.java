/**
 * 
 */
package com.tll.client.validate;

/**
 * IValidator - Contract for validator implementations.
 * @author jpk
 */
public interface IValidator {

	/**
	 * Validate the given value returning the "validated" value.
	 * <p>
	 * E.g.: A numeric validator will return a number when validation passes.
	 * @param value value to validate
	 * @return The validated value possibly of a different type
	 * @throws ValidationException When the value is found invalid.
	 */
	Object validate(Object value) throws ValidationException;
}
