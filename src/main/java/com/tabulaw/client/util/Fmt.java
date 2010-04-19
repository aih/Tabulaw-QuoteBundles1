/**
 * The Logic Lab
 * @author jpk Sep 2, 2007
 */
package com.tabulaw.client.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * Fmt - String formatting constants and methods.
 * <P>
 * NOTE: We prescribe pre-baked global format directives to avoid having to
 * dynamically construct either DateFormats or NumberFormats which are
 * expensive. The downside is a limited set of available date and number formats
 * available to the client.
 * @see DateTimeFormat
 * @see NumberFormat
 * @author jpk
 */
public abstract class Fmt {

	private static final Map<GlobalFormat, DateTimeFormat> dateFormatBindings =
			new HashMap<GlobalFormat, DateTimeFormat>();

	private static final Map<GlobalFormat, NumberFormat> decimalFormatBindings =
			new HashMap<GlobalFormat, NumberFormat>();

	static {
		dateFormatBindings.put(GlobalFormat.DATE, DateTimeFormat.getShortDateFormat());
		dateFormatBindings.put(GlobalFormat.TIME, DateTimeFormat.getShortTimeFormat());
		dateFormatBindings.put(GlobalFormat.TIMESTAMP, DateTimeFormat.getShortDateTimeFormat());
		// default is timestamp
		dateFormatBindings.put(null, DateTimeFormat.getShortDateTimeFormat());

		decimalFormatBindings.put(GlobalFormat.CURRENCY, NumberFormat.getCurrencyFormat());
		decimalFormatBindings.put(GlobalFormat.PERCENT, NumberFormat.getPercentFormat());
		decimalFormatBindings.put(GlobalFormat.DECIMAL, NumberFormat.getDecimalFormat());
		// default is local dependant decimal format
		decimalFormatBindings.put(null, NumberFormat.getDecimalFormat());
	}

	/**
	 * Translates a {@link GlobalFormat} instance to a {@link DateTimeFormat}
	 * instance.
	 * @param format The {@link GlobalFormat} instance
	 * @return The translated {@link DateTimeFormat} instance
	 * @throws IllegalArgumentException When the given global format instance does
	 *         not represent a date format.
	 */
	public static DateTimeFormat getDateTimeFormat(GlobalFormat format) throws IllegalArgumentException {
		if(!format.isDateFormat()) throw new IllegalArgumentException("Not a date format");
		return dateFormatBindings.get(format);
	}

	/**
	 * Translates a {@link GlobalFormat} instance to a {@link NumberFormat}
	 * instance.
	 * @param format The {@link GlobalFormat} instance
	 * @return The translated {@link NumberFormat} instance
	 * @throws IllegalArgumentException When the given global format instance does
	 *         not represent a decimal format.
	 */
	public static NumberFormat getDecimalFormat(GlobalFormat format) throws IllegalArgumentException {
		if(!format.isNumericFormat()) throw new IllegalArgumentException("Not a number format");
		return decimalFormatBindings.get(format);
	}

	/**
	 * Generic formatting utility method.
	 * @param value
	 * @param format
	 * @return Never <code>null</code> String.
	 */
	public static String format(Object value, GlobalFormat format) {
		if(value == null) return "";
		if(format != null) {
			switch(format) {
				case DATE:
					return date((Date) value, GlobalFormat.DATE);
				case TIME:
					return date((Date) value, GlobalFormat.TIME);
				case TIMESTAMP:
					return date((Date) value, GlobalFormat.TIMESTAMP);

				case CURRENCY:
					return decimal(((Double) value).doubleValue(), GlobalFormat.CURRENCY);

				case PERCENT:
					return decimal(((Double) value).doubleValue(), GlobalFormat.PERCENT);

				case DECIMAL:
					return decimal(((Double) value).doubleValue(), GlobalFormat.DECIMAL);

				case BOOL_TRUEFALSE:
					return bool(((Boolean) value).booleanValue(), GlobalFormat.BOOL_TRUEFALSE);
				case BOOL_YESNO:
					return bool(((Boolean) value).booleanValue(), GlobalFormat.BOOL_YESNO);
			}
		}
		// resort to toString
		return value.toString();
	}

	/**
	 * Formats a {@link Date} to a String given a format directive.
	 * @param date
	 * @param format May be <code>null</code> in which case
	 *        {@link GlobalFormat#TIMESTAMP} formatting is used.
	 * @return Formatted date String (never <code>null</code>).
	 */
	private static String date(Date date, GlobalFormat format) {
		return date == null ? "" : dateFormatBindings.get(format).format(date);
	}

	/**
	 * Formats a decimal to a local dependant decimal formatted String.
	 * @param decimal
	 * @param format The decimal format. If <code>null</code>, default local
	 *        dependant decimal formatting is applied.
	 * @return A decimal formatted String.
	 */
	private static String decimal(double decimal, GlobalFormat format) {
		return decimalFormatBindings.get(format).format(decimal);
	}

	/**
	 * Formats a decimal to a local dependant decimal formatted String.
	 * @param decimal
	 * @return A decimal formatted String.
	 */
	public static String decimal(double decimal) {
		return decimalFormatBindings.get(GlobalFormat.DECIMAL).format(decimal);
	}

	/**
	 * Formats a decimal to a currency formatted String
	 * @param decimal
	 * @return Currency formatted String
	 */
	public static String currency(double decimal) {
		return decimalFormatBindings.get(GlobalFormat.CURRENCY).format(decimal);
	}

	/**
	 * Formats a decimal to a percent formatted String
	 * @param decimal
	 * @return Percent formatted String
	 */
	public static String percent(double decimal) {
		return decimalFormatBindings.get(GlobalFormat.PERCENT).format(decimal);
	}

	/**
	 * Formats a boolean to a UI friendly string.
	 * @param b
	 * @param format
	 * @return Presentation worthy string
	 */
	private static String bool(boolean b, GlobalFormat format) {
		switch(format) {
			case BOOL_YESNO:
				return b ? "Yes" : "No";
			default:
			case BOOL_TRUEFALSE:
				return b ? "True" : "False";
		}
	}
}
