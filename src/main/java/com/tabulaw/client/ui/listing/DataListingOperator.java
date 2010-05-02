/**
 * The Logic Lab
 * @author jpk
 * Mar 30, 2008
 */
package com.tabulaw.client.ui.listing;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.tabulaw.common.data.ListingOp;
import com.tabulaw.dao.Sorting;
import com.tabulaw.listhandler.EmptyListException;
import com.tabulaw.listhandler.IListHandler;
import com.tabulaw.listhandler.ListHandlerException;

/**
 * DataListingOperator - {@link IListingOperator} based on an existing
 * collection of data elements.
 * @author jpk
 * @param <R> list element type
 * @param <H> list <em>handler</em> type
 */
public class DataListingOperator<R, H extends IListHandler<R>> extends AbstractListingOperator<R> {

	/**
	 * The data provider.
	 */
	private final H dataProvider;

	private final int pageSize;

	/**
	 * The current chunk of listing data.
	 */
	private transient List<R> current;

	/**
	 * Constructor
	 * @param pageSize
	 * @param dataProvider
	 * @param sorting
	 */
	public DataListingOperator(int pageSize, H dataProvider, Sorting sorting) {
		this.pageSize = pageSize;
		this.dataProvider = dataProvider;
		this.sorting = sorting;
	}
	
	protected final H getDataProvider() {
		return dataProvider;
	}

	@Override
	protected void doFetch(int ofst, Sorting srtg) {
		try {
			current = dataProvider.getElements(ofst, pageSize, srtg);
			sorting = srtg;
			listSize = current.size();
			offset = ofst;
		}
		catch(final EmptyListException e) {
			if(current != null) current.clear();
		}
		catch(final IndexOutOfBoundsException e) {
			throw new IllegalStateException(e);
		}
		catch(final ListHandlerException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected String getListingId() {
		return null; // local listings don't need an id
	}

	@Override
	protected int getPageSize() {
		return pageSize;
	}

	@Override
	public void refresh() {
		doFetch(0, sorting);
		fireListingEvent(ListingOp.REFRESH);
	}
	
	private void fireListingEvent(ListingOp listingOp) {
		fireListingEvent(listingOp, current);
	}

	@Override
	public void firstPage() {
		super.firstPage();
		fireListingEvent(ListingOp.FETCH);
	}

	@Override
	public void lastPage() {
		super.lastPage();
		fireListingEvent(ListingOp.FETCH);
	}

	@Override
	public void nextPage() {
		super.nextPage();
		fireListingEvent(ListingOp.FETCH);
	}

	@Override
	public void previousPage() {
		super.previousPage();
		fireListingEvent(ListingOp.FETCH);
	}

	@Override
	public void gotoPage(int pageNum) {
		super.gotoPage(pageNum);
		fireListingEvent(ListingOp.FETCH);
	}

	@Override
	public void sort(Sorting srtg) {
		super.sort(srtg);
		fireListingEvent(ListingOp.FETCH);
	}

	@Override
	public void clear() {
		offset = 0;
		sorting = null;
		fireListingEvent(ListingOp.CLEAR);
		if(current != null) {
			DeferredCommand.addCommand(new Command() {
				
				@Override
				public void execute() {
					current.clear();
				}
			});
		}
	}
}
