/**
 * 
 */
package com.tll.util;

import com.google.gwt.i18n.client.NumberFormat;
import com.tll.IMarshalable;

/**
 * Encapsulates a numerical value with a string-wise formatting directive used
 * to format the number for display.
 * @author jpk
 */
public class FNum implements IMarshalable {

	/**
	 * The numerical value
	 */
	private Double value;
	/**
	 * The string-wise formatting directive by which the <code>value</code> is
	 * formatted for display. The "format" of this property is consistent with the
	 * formatting rules for Java's decimal formatting.
	 */
	private String format;

	public FNum() {
	}

	public FNum(Double value) {
		this(value, null);
	}

	public FNum(Double value, String format) {
		this();
		setValue(value);
		setFormat(format);
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return NumberFormat.getDecimalFormat().format(getValue().doubleValue());
	}
}
