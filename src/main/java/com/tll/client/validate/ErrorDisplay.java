package com.tll.client.validate;

/**
 * ErrorDisplay - Error display permutations.
 * @author jpk
 */
public enum ErrorDisplay {
	/**
	 * Indicates errors should be displayed "globally".
	 */
	GLOBAL(1),
	/**
	 * Indicates errors should be displayed "locally".
	 */
	LOCAL(1 << 1);

	/**
	 * Indicates no flags.
	 */
	public static final int NO_FLAGS = -1;

	/**
	 * ORd composite of all defined enum flags.
	 */
	public static final int ALL_FLAGS = GLOBAL.flag | LOCAL.flag;

	/**
	 * The bit flag.
	 */
	private int flag;

	/**
	 * Constructor
	 */
	private ErrorDisplay(int flag) {
		this.flag = flag;
	}

	public int flag() {
		return flag;
	}
}