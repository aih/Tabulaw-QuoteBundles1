package com.tll.client.validate;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * DecimalValidator
 * @author jpk
 */
public class DecimalValidator implements IValidator {

	private final NumberFormat numberFormat;

	/**
	 * Constructor
	 * @param numberFormat
	 */
	public DecimalValidator(NumberFormat numberFormat) {
		if(numberFormat == null) {
			throw new IllegalArgumentException("A number format must be specified.");
		}
		this.numberFormat = numberFormat;
	}

	/**
	 * Constructor
	 * @param pattern
	 */
	public DecimalValidator(String pattern) {
		if(pattern == null) {
			throw new IllegalArgumentException("A number pattern must be specified.");
		}
		numberFormat = NumberFormat.getFormat(pattern);
	}

	public Object validate(Object value) throws ValidationException {
		if(value == null) return null;
		final double d;
		try {
			d = numberFormat.parse(value.toString());
		}
		catch(final NumberFormatException nfe) {
			throw new ValidationException("Value must be a decimal of format: '" + numberFormat.getPattern() + "'.");
		}
		return new Double(d);
	}
}
