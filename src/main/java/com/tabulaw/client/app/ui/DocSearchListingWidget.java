/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.data.rpc.IRpcHandler;
import com.tabulaw.client.data.rpc.RpcEvent;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcUiHandler;
import com.tabulaw.client.ui.listing.AbstractListingConfig;
import com.tabulaw.client.ui.listing.AbstractListingOperator;
import com.tabulaw.client.ui.listing.Column;
import com.tabulaw.client.ui.listing.IListingConfig;
import com.tabulaw.client.ui.listing.ITableCellRenderer;
import com.tabulaw.client.ui.listing.ListingNavBar;
import com.tabulaw.client.ui.listing.ModelListingTable;
import com.tabulaw.client.ui.listing.ModelListingWidget;
import com.tabulaw.common.data.ListingOp;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocSearchPayload;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tabulaw.dao.Sorting;

/**
 * Lists doc search results.
 * @author jpk
 */
public class DocSearchListingWidget extends Composite implements SelectionHandler<String> {

	public static class Styles {
		public static final String DOC_SEARCH_LISTING = "docSearchListing";
	}
	
	static class ListingConfig extends AbstractListingConfig {

		static final Sorting defaultSorting = new Sorting("title");

		static final Column[] cols = new Column[] {
			new Column("Result"),
		};

		public ListingConfig() {
			super("Document", null, cols, defaultSorting, 10);
		}

		@Override
		public boolean isShowRefreshBtn() {
			return false;
		}
	} // ListingConfig

	static class Operator extends AbstractListingOperator<CaseDocSearchResult> {
		
		String query;

		/**
		 * Constructor
		 */
		public Operator() {
			super();
			// declare a workable upper bound for google scholar search results
			listSize = 999;
		}

		@Override
		protected void doFetch(final int ofst, Sorting srtg) {
			DocSearchRequest dsr = new DocSearchRequest(DocDataProvider.GOOGLE_SCHOLAR, query, ofst, getPageSize(), true);
			Poc.getDocService().search(dsr, new AsyncCallback<DocSearchPayload>() {

				@Override
				public void onSuccess(DocSearchPayload result) {
					if(result.hasErrors()) {
						Notifier.get().showFor(result);
					}
					else {
						List<CaseDocSearchResult> searchResults = result.getResults();
						offset = ofst;
						fireListingEvent(ListingOp.FETCH, searchResults);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Notifier.get().showFor(caught);
				}
			});
		}

		@Override
		protected String getListingId() {
			return "docSearch";
		}

		@Override
		protected int getPageSize() {
			return config.getPageSize();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void refresh() {
			doFetch(0, null);
		}
	}

	static class DocListing extends ModelListingWidget<CaseDocSearchResult, DocSearchListingWidget.Table> implements IRpcHandler {

		final RpcUiHandler rpcui;

		public DocListing() {
			super(config.getListingId(), config.getListingElementName(), new Table(config), new ListingNavBar<CaseDocSearchResult>(config,
					null));
			rpcui = new RpcUiHandler(this);
		}

		Table getTable() {
			return table;
		}

		@Override
		protected Widget createNoDataRowsWidget() {
			return new Label("No documents found.");
		}

		@Override
		public void onRpcEvent(RpcEvent event) {
			rpcui.onRpcEvent(event);
		}
	}

	static class Table extends ModelListingTable<CaseDocSearchResult> {

		public Table(IListingConfig config) {
			super(config, null);
		}

		@Override
		protected void onCellClick(int colIndex, int rowIndex) {
			if(rowIndex > 0 && colIndex < 2)
				ViewManager.get().dispatch(new ShowViewRequest(new DocViewInitializer(getRowKey(rowIndex))));
		}
	}

	static class CellRenderer implements ITableCellRenderer<CaseDocSearchResult> {

		@Override
		public void renderCell(int rowIndex, final int cellIndex, final CaseDocSearchResult rowData, Column column, final HTMLTable table) {
			// same as doc suggest (for now)
			String html = "<div class=\"entry\"><div class=\"title\">" + rowData.getTitleHtml() + "</div><div class=\"summary\">"
				+ rowData.getSummary() + "</div></div>";
			table.setHTML(rowIndex, cellIndex, html);
		}
	} // CellRenderer

	static final IListingConfig config = new ListingConfig();

	private final Operator operator;

	private final DocListing listingWidget;

	private final FlowPanel pnl = new FlowPanel();

	/**
	 * Constructor
	 */
	public DocSearchListingWidget() {
		super();

		listingWidget = new DocListing();
		operator = new Operator();
		listingWidget.getTable().setCellRenderer(new CellRenderer());
		listingWidget.setOperator(operator);
		pnl.add(listingWidget);

		pnl.setStyleName(Styles.DOC_SEARCH_LISTING);
		initWidget(pnl);
	}

	/**
	 * This triggers a new full text search request.
	 */
	@Override
	public void onSelection(SelectionEvent<String> event) {
		operator.query = event.getSelectedItem();
		operator.refresh();
	}
}
