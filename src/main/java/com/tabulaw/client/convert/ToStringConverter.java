package com.tabulaw.client.convert;

/**
 * ToStringConverter - Converts an arbitrary {@link Object} to a {@link String}.
 * <p>
 * <em><b>IMPT NOTE: </b>This code was originally derived from the <a href="http://gwittir.googlecode.com/">gwittir</a> project.</em>
 * @author jpk
 * @param <I> The input type
 */
public class ToStringConverter<I> implements IConverter<String, I> {

	public static final ToStringConverter<Object> INSTANCE = new ToStringConverter<Object>();

	/**
	 * Constructor
	 */
	private ToStringConverter() {
	}

	/**
	 * Translates an arbitrary {@link Object} instance to a non-<code>null</code>
	 * {@link String} instance.
	 * @param o the object to convert
	 * @return A never <code>null</code> {@link String}.
	 */
	public String convert(Object o) {
		return o == null ? "" : o.toString();
	}
}
