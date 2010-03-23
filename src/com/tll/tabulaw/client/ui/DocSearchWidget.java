/**
 * The Logic Lab
 * @author jpk
 * @since Mar 21, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tll.tabulaw.common.data.rpc.IDocSearchService;
import com.tll.tabulaw.common.data.rpc.IDocSearchServiceAsync;

/**
 * 
 * @author jpk
 */
public class DocSearchWidget extends Composite {

	public static final IDocSearchServiceAsync svc;
	static {
		svc = (IDocSearchServiceAsync) GWT.create(IDocSearchService.class);
	}
	
	private final DocSuggestWidget suggest;

	private final DocSearchResultsListingWidget listing;

	private final FlowPanel pnl = new FlowPanel();

	public DocSearchWidget() {
		super();
		suggest = new DocSuggestWidget();
		listing = new DocSearchResultsListingWidget();
		pnl.add(suggest);
		pnl.add(listing);
		initWidget(pnl);
		
		suggest.addSelectionHandler(listing);
	}

}
