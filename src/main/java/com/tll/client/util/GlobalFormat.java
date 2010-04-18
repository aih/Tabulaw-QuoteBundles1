/**
 * 
 */
package com.tll.client.util;

/**
 * GlobalFormat - Used for client side formatting.
 * @author jpk
 */
public enum GlobalFormat {
	TIMESTAMP,
	DATE,
	TIME,
	CURRENCY,
	PERCENT,
	DECIMAL,
	BOOL_YESNO,
	BOOL_TRUEFALSE;

	public boolean isDateFormat() {
		return this == TIMESTAMP || this == DATE || this == TIME;
	}

	public boolean isNumericFormat() {
		return this == CURRENCY || this == PERCENT || this == DECIMAL;
	}

	public boolean isBooleanFormat() {
		return this == BOOL_YESNO || this == BOOL_TRUEFALSE;
	}
}