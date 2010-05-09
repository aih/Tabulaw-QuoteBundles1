/**
 * The Logic Lab
 * @author jpk
 * @since May 4, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;

/**
 * Single widget containing:
 * <ul>
 * <li>User's documents (doc listing)
 * <li>Document Search
 * </ul>
 * @author jpk
 */
public class DocsWidget extends AbstractModelChangeAwareWidget {

	private static class Styles {
		public static final String DOC_LISTING_HEADER = "docListingHeader";
		public static final String DOC_LISTING_HEADER_TITLE = "title";
		public static final String DOC_LISTING_HEADER_SUMMARY = "summary";
		public static final String DP_SEARCH = "searchDp";
	}

	private final FlowPanel panel = new FlowPanel();

	private final DisclosurePanel dpSearch;
	private final DocSearchWidget docSearch = new DocSearchWidget();

	private final FlowPanel docListingHeader = new FlowPanel();
	private final DocListingWidget docListing = new DocListingWidget();

	/**
	 * Constructor
	 */
	public DocsWidget() {
		super();

		dpSearch = new DisclosurePanel("Search");
		dpSearch.setAnimationEnabled(true);
		dpSearch.addStyleName(Styles.DP_SEARCH);
		dpSearch.setContent(docSearch);
		dpSearch.setOpen(true); // initially open
		dpSearch.addOpenHandler(new OpenHandler<DisclosurePanel>() {

			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				// docListing.setVisible(false);
			}
		});
		dpSearch.addCloseHandler(new CloseHandler<DisclosurePanel>() {

			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				// docListing.setVisible(true);
			}
		});

		panel.add(dpSearch);

		docListingHeader.setStyleName(Styles.DOC_LISTING_HEADER);
		Label lblDocuments = new Label("Documents");
		lblDocuments.setStyleName(Styles.DOC_LISTING_HEADER_TITLE);
		Label lblSummary = new Label("Includes your written documents and reading history.");
		lblSummary.setStyleName(Styles.DOC_LISTING_HEADER_SUMMARY);
		docListingHeader.add(lblDocuments);
		docListingHeader.add(lblSummary);

		panel.add(docListingHeader);
		panel.add(docListing);

		initWidget(panel);
	}

	/**
	 * Resets the state to that of initial load.
	 */
	public void refresh() {
		docSearch.reset();
		docListing.refresh();
	}

	/**
	 * Clears out all data but retains the structure.
	 */
	public void clearState() {
		if(docListing.getOperator() != null) docListing.getOperator().clear();
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		docListing.onModelChangeEvent(event);
	}
}
