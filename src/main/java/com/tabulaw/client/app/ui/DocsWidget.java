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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.app.Resources;
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

	static class Styles {

		public static final String DP_SEARCH = "searchDp";
		public static final String DP_HEADER = "sheader";
		public static final String DP_SEARCH_HEADER_IMAGE = "simage";
		public static final String DP_SEARCH_HEADER_TEXT = "stext";
	}

	private final FlowPanel panel = new FlowPanel();

	private final DisclosurePanel dp;

	private final DocSearchWidget docSearch = new DocSearchWidget();

	private final DocListingWidget docListing = new DocListingWidget();

	/**
	 * Constructor
	 */
	public DocsWidget() {
		super();

		Image searchImage = new Image(Resources.INSTANCE.searchImage());
		searchImage.setStyleName(Styles.DP_SEARCH_HEADER_IMAGE);
		Label searchText = new Label("Search");
		searchText.setStyleName(Styles.DP_SEARCH_HEADER_TEXT);
		FlowPanel dpHeaderWidget = new FlowPanel();
		dpHeaderWidget.setStyleName(Styles.DP_HEADER);
		dpHeaderWidget.add(searchImage);
		dpHeaderWidget.add(searchText);

		dp = new DisclosurePanel();
		dp.setAnimationEnabled(true);
		dp.addStyleName(Styles.DP_SEARCH);
		dp.setHeader(dpHeaderWidget);
		dp.add(docSearch);
		dp.addOpenHandler(new OpenHandler<DisclosurePanel>() {

			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				//docListing.setVisible(false);
			}
		});
		dp.addCloseHandler(new CloseHandler<DisclosurePanel>() {

			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				//docListing.setVisible(true);
			}
		});

		panel.add(dp);
		panel.add(docListing);

		initWidget(panel);
	}

	/**
	 * Resets the state to that of initial load.
	 */
	public void refresh() {
		// TODO
	}

	/**
	 * Clears out all data but retains the structure.
	 */
	public void clearState() {
		// TODO finish
		if(docListing.getOperator() != null) docListing.getOperator().clear();
	}
}
