package com.tabulaw.listhandler;

/**
 * Enumerates the defined {@link com.tabulaw.listhandler.IListHandler} types
 * (implementations). See the com.tll.listhandler package.
 * @author jpk
 */
public enum ListHandlerType {
	IN_MEMORY,
	MODELKEY_LIST,
	PAGE;

	/**
	 * Does the {@link ListHandlerType} employ search criteria?
	 * @return true/false
	 */
	public boolean isSearchBased() {
		return this == ListHandlerType.MODELKEY_LIST || this == ListHandlerType.PAGE;
	}

}