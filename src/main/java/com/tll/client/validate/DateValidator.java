package com.tll.client.validate;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.tll.client.util.Fmt;
import com.tll.client.util.GlobalFormat;
import com.tll.util.StringUtil;

/**
 * DateValidator
 * @author jpk
 */
public class DateValidator implements IValidator {

	private static final DateValidator TIMESTAMP_VALIDATOR =
			new DateValidator(Fmt.getDateTimeFormat(GlobalFormat.TIMESTAMP));

	private static final DateValidator DATE_VALIDATOR = new DateValidator(Fmt.getDateTimeFormat(GlobalFormat.DATE));

	private static final DateValidator TIME_VALIDATOR = new DateValidator(Fmt.getDateTimeFormat(GlobalFormat.TIME));

	/**
	 * Factory method for obtaining a pre-baked {@link DateValidator}.
	 * @param dateFormat
	 * @return The appropriate {@link DateValidator}
	 * @throws IllegalArgumentException When the given date format is
	 *         <code>null</code> or invalid.
	 */
	public static final DateValidator get(GlobalFormat dateFormat) {
		switch(dateFormat) {
			case DATE:
				return DATE_VALIDATOR;
			case TIME:
				return TIME_VALIDATOR;
			case TIMESTAMP:
				return TIMESTAMP_VALIDATOR;
		}
		throw new IllegalArgumentException("A valid date format must be specified.");
	}

	private final DateTimeFormat dateFormat;

	/**
	 * Constructor
	 * @param pattern
	 */
	public DateValidator(String pattern) {
		if(pattern == null) {
			throw new IllegalArgumentException("A date format must be specified.");
		}
		dateFormat = DateTimeFormat.getFormat(pattern);
	}

	/**
	 * Constructor
	 * @param dateFormat
	 */
	public DateValidator(DateTimeFormat dateFormat) {
		super();
		if(dateFormat == null) {
			throw new IllegalArgumentException("A date pattern must be specified.");
		}
		this.dateFormat = dateFormat;
	}

	public Object validate(Object value) throws ValidationException {
		if(value == null || value instanceof Date) return value;
		final String s = value.toString();
		if(StringUtil.isEmpty(s)) return value;
		try {
			return dateFormat.parse(s);
		}
		catch(final Throwable e) {
			throw new ValidationException("Invalid date.");
		}
	}
}
