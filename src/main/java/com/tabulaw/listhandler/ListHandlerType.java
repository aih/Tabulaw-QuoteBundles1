package com.tabulaw.listhandler;

/**
 * Enumerates the defined {@link com.tabulaw.listhandler.IListHandler} types
 * (implementations). See the com.tll.listhandler package.
 * @author jpk
 */
public enum ListHandlerType {
	IN_MEMORY,
	IDLIST,
	PAGE;

	/**
	 * Does the {@link ListHandlerType} employ search criteria?
	 * @return true/false
	 */
	public boolean isSearchBased() {
		return this == ListHandlerType.IDLIST || this == ListHandlerType.PAGE;
	}

}