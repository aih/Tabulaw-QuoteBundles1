/**
 * The Logic Lab
 * @author jpk Mar 1, 2009
 */
package com.tll.client.convert;

/**
 * ToBooleanConverter
 * @author jpk
 */
public class ToBooleanConverter implements IConverter<Boolean, Object> {
	
	public static final ToBooleanConverter DEFAULT = new ToBooleanConverter();

	private static final String DEFAULT_TRUE_STRING = "true";

	/**
	 * The compare string for determining if the value resolves to true.
	 */
	private final String trueStr;

	/**
	 * Constructor
	 */
	public ToBooleanConverter() {
		trueStr = DEFAULT_TRUE_STRING;
	}

	/**
	 * Constructor
	 * @param trueStr
	 */
	public ToBooleanConverter(String trueStr) {
		this.trueStr = trueStr;
	}

	@Override
	public Boolean convert(Object value) throws IllegalArgumentException {
		if(value == null || value instanceof Boolean) {
			return (Boolean) value;
		}
		return (trueStr.equals(value.toString())) ? Boolean.TRUE : Boolean.FALSE;
	}

}
