/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 13, 2009
 */
package com.tabulaw.client.ui.listing;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.common.data.ListingOp;
import com.tabulaw.dao.Sorting;

/**
 * AbstractListingOperator
 * @author jpk
 * @param <R> the row element type
 */
public abstract class AbstractListingOperator<R> implements IListingOperator<R> {

	/**
	 * The Widget that will be passed in dispatched {@link ListingEvent}s.
	 */
	protected Widget sourcingWidget;

	/**
	 * The current list index offset.
	 */
	protected int offset = 0;

	/**
	 * The current sorting directive.
	 */
	protected Sorting sorting;

	/**
	 * The size of the underlying result set.
	 */
	protected int listSize = -1;

	/**
	 * Constructor
	 */
	public AbstractListingOperator() {
	}

	/**
	 * Sets the source of rpc events
	 * @param sourcingWidget the optional widget that will serve as the rpc event
	 *        source
	 */
	public final void setSourcingWidget(Widget sourcingWidget) {
		this.sourcingWidget = sourcingWidget;
	}

	private void fetch(int ofst, Sorting srtg) {
		doFetch(ofst, srtg);
		listingGenerated = true;
	}

	protected void fireListingEvent(ListingOp listingOp, List<R> pageElements) {
		// fire the listing event
		sourcingWidget.fireEvent(new ListingEvent<R>(getListingId(), listingOp, listSize, pageElements, offset, sorting,
				getPageSize()));
	}

	/**
	 * Responsible for fetching the data.
	 * @param ofst
	 * @param srtg
	 */
	protected abstract void doFetch(int ofst, Sorting srtg);

	protected abstract String getListingId();

	protected abstract int getPageSize();

	protected boolean listingGenerated;

	public void sort(Sorting srtg) {
		if(!listingGenerated || (this.sorting != null && !this.sorting.equals(srtg))) {
			fetch(offset, srtg);
		}
	}

	public void firstPage() {
		if(!listingGenerated || offset != 0) fetch(0, sorting);
	}

	public void gotoPage(int pageNum) {
		final int ofst = PagingUtil.listIndexFromPageNum(pageNum, getPageSize());
		if(!listingGenerated || this.offset != ofst) fetch(ofst, sorting);
	}

	public void lastPage() {
		final int pageSize = getPageSize();
		final int numPages = PagingUtil.numPages(listSize, pageSize);
		final int ofst = PagingUtil.listIndexFromPageNum(numPages - 1, pageSize);
		if(!listingGenerated || this.offset != ofst) {
			fetch(ofst, sorting);
		}
	}

	public void nextPage() {
		final int ofst = this.offset + getPageSize();
		if(ofst < listSize) fetch(ofst, sorting);
	}

	public void previousPage() {
		final int ofst = this.offset - getPageSize();
		if(ofst >= 0) fetch(ofst, sorting);
	}
}
