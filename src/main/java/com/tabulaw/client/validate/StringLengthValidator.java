package com.tabulaw.client.validate;

/**
 * StringLengthValidator
 * @author jkirton
 */
public class StringLengthValidator implements IValidator {

	private final int min, max;

	/**
	 * Validates a String for min/max length.
	 * @param value The String to validate
	 * @param min The min allowed length or <code>-1</code> if not bounded by a
	 *        minimum length constraint.
	 * @param max The max allowed length or <code>-1</code> if not bounded by a
	 *        maximum length constraint.
	 * @return The validated Object
	 * @throws ValidationException When the String's length is out of bounds.
	 */
	public static Object validate(Object value, int min, int max) throws ValidationException {
		final int len = (value == null ? 0 : value.toString().length());
		if(min == -1 && max != -1) {
			if(len > max) throw new ValidationException("Max value length is " + max + " characters.");
		}
		else if(min != -1 && max == -1) {
			if(len < min) throw new ValidationException("Min value length is " + min + " characters.");
		}
		else if((len < min) || (len > max)) {
			throw new ValidationException("Value length must be between " + min + " - " + max + " characters.");
		}
		return value;
	}

	/**
	 * Constructor
	 * @param minCharacters
	 * @param maxCharacters
	 */
	public StringLengthValidator(int minCharacters, int maxCharacters) {
		if(maxCharacters <= minCharacters) {
			throw new IllegalArgumentException("Invalid min/max lengths");
		}
		this.min = minCharacters;
		this.max = maxCharacters;
	}

	public Object validate(Object value) throws ValidationException {
		return validate(value, min, max);
	}
}
