/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.view.DocViewInitializer;
import com.tabulaw.client.ui.IRpcHandler;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcCommand;
import com.tabulaw.client.ui.RpcEvent;
import com.tabulaw.client.ui.RpcUiHandler;
import com.tabulaw.client.ui.listing.AbstractListingConfig;
import com.tabulaw.client.ui.listing.AbstractListingOperator;
import com.tabulaw.client.ui.listing.Column;
import com.tabulaw.client.ui.listing.IListingConfig;
import com.tabulaw.client.ui.listing.ITableCellRenderer;
import com.tabulaw.client.ui.listing.ListingNavBar;
import com.tabulaw.client.ui.listing.ModelListingTable;
import com.tabulaw.client.ui.listing.ModelListingWidget;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.common.data.ListingOp;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocPayload;
import com.tabulaw.common.data.rpc.DocSearchPayload;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.dao.Sorting;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;

/**
 * Lists doc search results.
 * @author jpk
 */
public class DocSearchListingWidget extends Composite implements SelectionHandler<String> {

	static class ListingConfig extends AbstractListingConfig {

		static final Sorting defaultSorting = new Sorting("title");

		static final Column[] cols = new Column[] { new Column("Result"),
		};

		public ListingConfig() {
			super("Document", null, cols, defaultSorting, 5);
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
			new RpcCommand<DocSearchPayload>() {

				@Override
				protected void doExecute() {
					setSource(Operator.this.sourcingWidget);
					DocSearchRequest dsr = new DocSearchRequest("GOOGLE_SCHOLAR", query, ofst, getPageSize(), true);
					Poc.getDocService().search(dsr, this);
				}

				@Override
				protected void handleSuccess(DocSearchPayload result) {
					super.handleSuccess(result);
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
				protected void handleFailure(Throwable caught) {
					super.handleFailure(caught);
					Notifier.get().showFor(caught);
				}
			}.execute();
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
			offset = 0;
			sorting = null;
			fireListingEvent(ListingOp.CLEAR, null);
		}

		@Override
		public void refresh() {
			doFetch(0, null);
		}
	}

	static class DocListing extends ModelListingWidget<CaseDocSearchResult, DocSearchListingWidget.Table> implements IRpcHandler {

		final RpcUiHandler rpcui;

		public DocListing() {
			super(config.getListingId(), config.getListingElementName(), new Table(config),
					new ListingNavBar<CaseDocSearchResult>(config, null));
			rpcui = new RpcUiHandler(this);
			addHandler(this, RpcEvent.TYPE);
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

	static class Table extends ModelListingTable<CaseDocSearchResult> implements IRpcHandler {

		final RpcUiHandler rpcui;

		public Table(IListingConfig config) {
			super(config, new ITableCellRenderer<CaseDocSearchResult>() {

				@Override
				public void renderCell(int rowIndex, int cellIndex, CaseDocSearchResult rowData, Column column, HTMLTable table) {
					// same as doc suggest (for now)
					String html =
							"<div class=\"entry\"><div class=\"title\">" + rowData.getTitleHtml() + "</div><div class=\"summary\">"
									+ rowData.getSummary() + "</div></div>";
					table.setHTML(rowIndex, cellIndex, html);
				}

			});
			rpcui = new RpcUiHandler(this);
		}

		@Override
		public void onRpcEvent(RpcEvent event) {
			rpcui.onRpcEvent(event);
		}

		@Override
		protected void onCellClick(int colIndex, int rowIndex) {
			if(rowIndex == 0) return;
			final CaseDocSearchResult caseDoc = getRowData(rowIndex);
			if(caseDoc == null) return;
			final String docRemoteUrl = caseDoc.getUrl();
			new RpcCommand<DocPayload>() {

				@Override
				protected void doExecute() {
					this.source = Table.this;
					Poc.getDocService().fetch(docRemoteUrl, this);
				}

				@Override
				protected void handleFailure(Throwable caught) {
					super.handleFailure(caught);
					//Log.error("Unable to fetch remote document", caught);
					Notifier.get().showFor(caught);
				}

				@Override
				protected void handleSuccess(DocPayload result) {
					super.handleSuccess(result);
					if(result.hasErrors()) {
						Notifier.get().showFor(result);
						return;
					}
					final DocRef mNewDoc = result.getDocRef();
					final DocContent mNewDocContent = result.getDocContent();

					// persist the new doc and propagate through app
					ClientModelCache.get().persist(mNewDoc, Table.this);
					if(mNewDocContent != null) ClientModelCache.get().persist(mNewDocContent, null);

					DeferredCommand.addCommand(new Command() {

						@Override
						public void execute() {
							// show the doc (letting the model change event finish
							// first)
							final DocViewInitializer dvi = new DocViewInitializer(mNewDoc.getModelKey());
							ViewManager.get().dispatch(new ShowViewRequest(dvi));
						}
					});
				}

			}.execute();
		}
	}

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
		listingWidget.setOperator(operator);
		pnl.add(listingWidget);

		pnl.setStyleName("docSearchListing");
		initWidget(pnl);
	}

	/**
	 * This triggers a new full text search request.
	 */
	@Override
	public void onSelection(SelectionEvent<String> event) {
		operator.query = event.getSelectedItem();
		operator.refresh();
		setVisible(true);
	}

	/**
	 * Removes search results from the listing.
	 */
	public void clearData() {
		operator.clear();
	}
}
