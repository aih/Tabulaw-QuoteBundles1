/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 4, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;

/**
 * @author jpk
 */
public class DocSearchWidget extends Composite {

	
	private final DocSuggestWidget docSuggest = new DocSuggestWidget();

	private final DocSearchListingWidget docSearchListing = new DocSearchListingWidget();

	private final DisclosurePanel dpSearchResults;
	
	private final FlowPanel panel = new FlowPanel();

	/**
	 * Constructor
	 */
	public DocSearchWidget() {
		super();

		panel.setStyleName("docSearch");
		
		Label lblSearch = new Label("Search");
		lblSearch.setStyleName("docSearchTitle");
		panel.add(lblSearch);
		
		panel.add(docSuggest);

		dpSearchResults = new DisclosurePanel("Search Results");
		dpSearchResults.setAnimationEnabled(true);
		dpSearchResults.addStyleName("searchDp");
		dpSearchResults.setContent(docSearchListing);
		dpSearchResults.setOpen(true); // initially open
		dpSearchResults.addOpenHandler(new OpenHandler<DisclosurePanel>() {

			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				// docListing.setVisible(false);
			}
		});
		dpSearchResults.addCloseHandler(new CloseHandler<DisclosurePanel>() {

			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				// docListing.setVisible(true);
			}
		});
		dpSearchResults.setVisible(false); // initially hidden
		panel.add(dpSearchResults);
		
		initWidget(panel);

		docSuggest.addSelectionHandler(new SelectionHandler<String>() {
			
			@Override
			public void onSelection(SelectionEvent<String> event) {
				((HasText)dpSearchResults.getHeader()).setText("Search Results for '" + event.getSelectedItem() + "'");
				dpSearchResults.setVisible(true);
				docSearchListing.onSelection(event);
			}
		});
	}

	public void reset() {
		docSearchListing.clearData();
		//docSearchListing.setVisible(false);
	}
}
