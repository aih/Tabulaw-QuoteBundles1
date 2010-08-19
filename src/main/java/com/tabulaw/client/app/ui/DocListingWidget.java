/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.Resources;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.app.ui.view.DocViewInitializer;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.listing.AbstractListingConfig;
import com.tabulaw.client.ui.listing.Column;
import com.tabulaw.client.ui.listing.DataListingOperator;
import com.tabulaw.client.ui.listing.IListingConfig;
import com.tabulaw.client.ui.listing.ITableCellRenderer;
import com.tabulaw.client.ui.listing.ListingNavBar;
import com.tabulaw.client.ui.listing.ModelListingTable;
import com.tabulaw.client.ui.listing.ModelListingWidget;
import com.tabulaw.client.util.Fmt;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.rpc.DocListingPayload;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.User;
import com.tabulaw.dao.Sorting;
import com.tabulaw.listhandler.InMemoryListHandler;

/**
 * Lists documents for a given user.
 * @author jpk
 */
public class DocListingWidget extends AbstractModelChangeAwareWidget {

	static class ListingConfig extends AbstractListingConfig {

		static final Sorting defaultSorting = new Sorting("title");

		static final Column[] cols = new Column[] {
			new Column("Title", null, "title", null, "title"),
			new Column("Date", GlobalFormat.DATE, "date", null, "date"), 
			new Column("", null, null, null, "opcol"),
		};

		public ListingConfig() {
			super("Document", null, cols, defaultSorting, 10);
		}

		@Override
		public boolean isShowRefreshBtn() {
			return false;
		}
	} // ListingConfig

	static class DocListing extends ModelListingWidget<DocRef, DocListingWidget.Table> /*implements IRpcHandler*/ {

		// don't show as it gets offset from table due to disclosure panel animation above in ui
		//final RpcUiHandler rpcui;

		public DocListing() {
			super(config.getListingId(), config.getListingElementName(), new Table(config), new ListingNavBar<DocRef>(config,
					null));
			getElement().setId("docListing");
			//rpcui = new RpcUiHandler(this);
			//addHandler(this, RpcEvent.TYPE);
		}

		Table getTable() {
			return table;
		}

		@Override
		protected Widget createNoDataRowsWidget() {
			return new Label("Currently, no Documents cached.");
		}

		@Override
		public void onModelChangeEvent(ModelChangeEvent event) {
			if(event.getModelKey() != null && event.getModelKey().getEntityType().equals(EntityType.DOCUMENT.name())) {
				// NOTE: we don't call super since we are retrieving the entire listing
				// super.onModelChangeEvent(event);
				getOperator().refresh();
			}
		}

		/*
		@Override
		public void onRpcEvent(RpcEvent event) {
			rpcui.onRpcEvent(event);
		}
		*/
	}

	static class Table extends ModelListingTable<DocRef> {

		public Table(IListingConfig config) {
			super(config);
		}

		@Override
		protected void onCellClick(int colIndex, int rowIndex) {
			if(rowIndex > 0 && colIndex < 2)
				ViewManager.get().dispatch(new ShowViewRequest(new DocViewInitializer(getRowData(rowIndex).getModelKey())));
		}
	}

	class CellRenderer implements ITableCellRenderer<DocRef> {

		@Override
		public void renderCell(int rowIndex, final int cellIndex, final DocRef rowData, Column column, final HTMLTable table) {
			switch(cellIndex) {
				case 0:
					table.setText(rowIndex, cellIndex, rowData.getTitle());
					break;
				case 1:
					String sdate = Fmt.format(rowData.getDate(), GlobalFormat.DATE);
					table.setText(rowIndex, cellIndex, sdate);
					break;
				case 2: {
					FlowPanel fpanel = new FlowPanel();
					fpanel.setStyleName("buttonPanel");
					
					// delete button
					final User liu = ClientModelCache.get().getUser();
					final boolean isAdmin = liu.isAdministrator();
					final String title = isAdmin ? "Permanantly delete document" : "Remove document";
					Image img = new Image(Resources.INSTANCE.trash());
					img.setTitle(title);
					img.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							event.stopPropagation();
							String docref = rowData.getTitle();
							final String confirm =
									isAdmin ? "Permanantly delete document '" + docref + "'?"
											: "Remove document '" + docref + "'?";
							if(Window.confirm(confirm)) {

								// client
								// remove quotes bound to the doc
								List<Quote> quotesToRemove = ClientModelCache.get().getQuotesByDoc(rowData.getId());
								for(Quote q : quotesToRemove) {
									ClientModelCache.get().remove(q.getModelKey(), table);
								}
								// remove the doc
								ClientModelCache.get().remove(rowData.getModelKey(), table);

								operator.refresh();

								// server
								if(isAdmin) {
									Poc.getUserDataService().deleteDoc(rowData.getId(), new AsyncCallback<Payload>() {
										
										@Override
										public void onSuccess(Payload result) {
											Notifier.get().showFor(result);
										}
										
										@Override
										public void onFailure(Throwable caught) {
											Notifier.get().showFor(caught);
										}
									});
								}
								else {
									ServerPersistApi.get().removeDocUserBinding(rowData.getId());
								}
							}
						}
					});
					fpanel.add(img);
					
					// export to word icon
					img = new Image(Resources.INSTANCE.permalink());
					img.setTitle("Export to MS Word");
					img.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							event.stopPropagation();
							Window.open("docdownload?mimeType=application/vnd.openxmlformats-officedocument.wordprocessingml.document&docId=" + rowData.getId(), "_top", null);
						}
					});
					fpanel.add(img);

					table.setWidget(rowIndex, cellIndex, fpanel);
					break;
				}
			}
		}
	} // CellRenderer

	public static class Operator extends DataListingOperator<DocRef, InMemoryListHandler<DocRef>> {
	
		public Operator() {
			super(config.getPageSize(), new InMemoryListHandler<DocRef>(), config.getDefaultSorting());
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public void refresh() {
			getDataProvider().setList((List<DocRef>) ClientModelCache.get().getAll(EntityType.DOCUMENT));
			super.refresh();
		}
	}

	static final IListingConfig config = new ListingConfig();

	private final Operator operator;

	private final DocListing listingWidget;

	private final FlowPanel pnl = new FlowPanel();

	/**
	 * Constructor
	 */
	public DocListingWidget() {
		super();

		listingWidget = new DocListing();
		operator = new Operator();
		listingWidget.getTable().setCellRenderer(new CellRenderer());
		listingWidget.setOperator(operator);

		pnl.add(listingWidget);

		initWidget(pnl);
	}

	public Operator getOperator() {
		return operator;
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		listingWidget.onModelChangeEvent(event);
	}

	public void refresh() {
		// get docs from server
		new RpcCommand<DocListingPayload>() {

			@Override
			protected void doExecute() {
				setSource(listingWidget);
				User user = ClientModelCache.get().getUser();
				if(user.isAdministrator()) {
					Poc.getUserDataService().getAllDocs(this);	
				}
				else {
					Poc.getUserDataService().getDocsForUser(user.getId(), this);
				}
			}

			@Override
			protected void handleSuccess(DocListingPayload result) {
				super.handleSuccess(result);
				Notifier.get().showFor(result);
				if(!result.hasErrors()) {
					ClientModelCache.get().persistAll(result.getDocList());
					operator.refresh();
				}
			}
		}.execute();
	}
}
