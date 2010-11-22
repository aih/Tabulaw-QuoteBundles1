/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jkirton
 * Jun 17, 2008
 */
package com.tabulaw.client.ui.listing;

/**
 * PagingUtil - Performs necessary client-side paging calculations.
 * @author jpk
 */
public abstract class PagingUtil {

	/**
	 * Calculates the page number from a list index and a page size.
	 * @param offset The list index
	 * @param pageSize The page size
	 * @return The calculated page number
	 */
	public static int pageNumFromListIndex(int offset, int pageSize) {
		return ((int) Math.round(offset / (double) pageSize + 0.5d)) - 1;
	}

	/**
	 * Calculates the list index from the page number and page size.
	 * @param pageNum 0-based page number
	 * @param pageSize The page size
	 * @return The calculated list index.
	 */
	public static int listIndexFromPageNum(int pageNum, int pageSize) {
		return pageNum == 0 ? 0 : pageNum * pageSize;
	}

	/**
	 * Calculates the number of pages in a list subject to paging.
	 * @param listSize The list size
	 * @param pageSize The max number of elements per page
	 * @return The calculated number of pages.
	 */
	public static int numPages(int listSize, int pageSize) {
		return (listSize % pageSize == 0) ? (int) (listSize / (double) pageSize) : (int) Math.round(listSize
				/ (double) pageSize
				+ 0.5d);
	}
}
