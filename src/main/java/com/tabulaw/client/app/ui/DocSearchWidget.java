/**
 * The Logic Lab
 * @author jpk
 * @since May 4, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author jpk
 */
public class DocSearchWidget extends Composite {
	
	static class Styles {
		public static final String DOC_SEARCH = "docSearch";
	}

	private final DocSearchListingWidget docSearchListing = new DocSearchListingWidget();

	private final DocSuggestWidget docSuggest = new DocSuggestWidget();
	
	private final FlowPanel panel = new FlowPanel();

	/**
	 * Constructor
	 */
	public DocSearchWidget() {
		super();
		
		panel.add(docSuggest);
		panel.add(docSearchListing);
		
		panel.setStyleName(Styles.DOC_SEARCH);
		initWidget(panel);
		
		docSuggest.addSelectionHandler(docSearchListing);
	}

}
