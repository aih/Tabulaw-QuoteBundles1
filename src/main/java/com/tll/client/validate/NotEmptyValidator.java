package com.tll.client.validate;

import java.util.Collection;

/**
 * NotEmptyValidator
 * @author jkirton
 */
public class NotEmptyValidator implements IValidator {

	public static final NotEmptyValidator INSTANCE = new NotEmptyValidator();

	/**
	 * Constructor
	 */
	private NotEmptyValidator() {
	}

	public Object validate(Object value) throws ValidationException {
		if(value instanceof Collection<?>) {
			final Collection<?> clc = (Collection<?>) value;
			if(clc.size() < 1) {
				throw new ValidationException("At least one item must be selected.");
			}
		}
		else {
			final String s = value == null ? null : value.toString();
			if(s == null || s.length() < 1) {
				throw new ValidationException("Value cannot be empty.");
			}
		}
		return value;
	}
}
