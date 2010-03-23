/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.tll.client.listing.AbstractListingConfig;
import com.tll.client.listing.AbstractListingOperator;
import com.tll.client.listing.Column;
import com.tll.client.listing.IListingConfig;
import com.tll.client.listing.ITableCellRenderer;
import com.tll.client.listing.ListingEvent;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.ui.listing.ListingNavBar;
import com.tll.client.ui.listing.ListingTable;
import com.tll.client.ui.listing.ListingWidget;
import com.tll.common.data.ListingOp;
import com.tll.dao.Sorting;
import com.tll.tabulaw.common.data.rpc.DocSearchPayload;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest;
import com.tll.tabulaw.common.data.rpc.DocSearchResult;
import com.tll.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;

/**
 * Lists live search results via server proxy.
 * @author jpk
 */
public class DocSearchResultsListingWidget extends Composite implements SelectionHandler<Suggestion> {

	static class DocListing extends ListingWidget<DocSearchResult, DocSearchResultsListingWidget.Table> {

		public DocListing() {
			super(config.getListingId(), config.getListingElementName(), new Table(config),
					new ListingNavBar<DocSearchResult>(config, null));
		}
	}

	static class Table extends ListingTable<DocSearchResult> {

		public Table(IListingConfig<DocSearchResult> config) {
			super(config);
		}

		@Override
		protected void onCellClick(int colIndex, int rowIndex) {
			if(rowIndex > 0 && colIndex < 2) {
				// TODO load the doc!
			}
		}

		private HandlerRegistration hrModelChange;

		@Override
		protected void onLoad() {
			super.onLoad();
			hrModelChange = addHandler(ModelChangeDispatcher.get(), ModelChangeEvent.TYPE);
		}

		@Override
		protected void onUnload() {
			hrModelChange.removeHandler();
			super.onUnload();
		}
	}

	static class CellRenderer implements ITableCellRenderer<DocSearchResult> {

		@Override
		public void renderCell(int rowIndex, final int cellIndex, final DocSearchResult rowData, Column column,
				final HTMLTable table) {
			String html = rowData.getDocTitleHtml();
			table.setHTML(rowIndex, cellIndex, html);
		}
	} // CellRenderer

	static class ListingConfig extends AbstractListingConfig<DocSearchResult> {

		static final Sorting defaultSorting = new Sorting("title");

		static final String[] modelProps = new String[] {
			"docTitleHtml", "citationText", "docSummaryHtml"
		};

		static final Column[] cols = new Column[] { new Column("Document"),
		};

		public ListingConfig() {
			super("Document", modelProps, cols, defaultSorting, 10);
		}

		@Override
		public boolean isShowRefreshBtn() {
			return false;
		}

		@Override
		public boolean isShowNavBar() {
			return true;
		}

		@Override
		public ITableCellRenderer<DocSearchResult> getCellRenderer() {
			return new CellRenderer();
		}
	} // ListingConfig

	static final IListingConfig<DocSearchResult> config = new ListingConfig();

	static class ListingOperator extends AbstractListingOperator<DocSearchResult> {

		String query;

		public ListingOperator() {
			super();
			this.listSize = 9999;
		}

		@Override
		protected void doFetch(final int ofst, Sorting srtg) {
			DocSearchRequest dsr = new DocSearchRequest(DocDataProvider.GOOGLE_SCHOLAR, query, ofst, getPageSize());
			DocSearchWidget.svc.search(dsr, new AsyncCallback<DocSearchPayload>() {

				@Override
				public void onSuccess(DocSearchPayload result) {
					if(!result.getStatus().hasErrors()) {
						offset = ofst;
						// fire the listing event
						sourcingWidget.fireEvent(new ListingEvent<DocSearchResult>(getListingId(), ListingOp.FETCH, listSize,
								result.getResults(), offset, sorting, getPageSize()));
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Unable to get doc search results: " + caught.getMessage());
				}
			});
		}

		@Override
		protected String getListingId() {
			return "1";
		}

		@Override
		protected int getPageSize() {
			return config.getPageSize();
		}

		@Override
		public void clear() {
			// no-op
		}

		@Override
		public void refresh() {
			doFetch(0, null);
		}

	} // ListingOperator

	private final ListingOperator operator;

	private final DocListing listingWidget;

	private final FlowPanel pnl = new FlowPanel();

	/**
	 * Constructor
	 */
	public DocSearchResultsListingWidget() {
		super();
		operator = new ListingOperator();

		listingWidget = new DocListing();
		listingWidget.setOperator(operator);

		pnl.add(listingWidget);

		initWidget(pnl);
	}

	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		String query = event.getSelectedItem().getReplacementString();
		operator.query = query;
		operator.refresh();
	}
}
