/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.ui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.Poc;
import com.tabulaw.client.Resources;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.mvc.ViewManager;
import com.tabulaw.client.mvc.view.ShowViewRequest;
import com.tabulaw.client.ui.listing.AbstractListingConfig;
import com.tabulaw.client.ui.listing.Column;
import com.tabulaw.client.ui.listing.DataListingOperator;
import com.tabulaw.client.ui.listing.IListingConfig;
import com.tabulaw.client.ui.listing.ITableCellRenderer;
import com.tabulaw.client.ui.listing.ModelListingTable;
import com.tabulaw.client.ui.listing.ModelListingWidget;
import com.tabulaw.client.util.Fmt;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.client.view.DocumentViewInitializer;
import com.tabulaw.common.data.rpc.DocListingPayload;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.dao.Sorting;
import com.tabulaw.listhandler.InMemoryListHandler;

/**
 * Lists documents for a given user.
 * @author jpk
 */
public class DocumentsListingWidget extends AbstractModelChangeAwareWidget {

	static class DocListing extends ModelListingWidget<DocRef, DocumentsListingWidget.Table> /*implements IRpcHandler*/ {

		//final RpcUiHandler rpcui;
		
		public DocListing() {
			super(config.getListingId(), config.getListingElementName(), new Table(config), null);
			//rpcui = new RpcUiHandler(this);
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
				//super.onModelChangeEvent(event);
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
			super(config, null);
		}

		@Override
		protected void onCellClick(int colIndex, int rowIndex) {
			if(rowIndex > 0 && colIndex < 2)
				ViewManager.get().dispatch(new ShowViewRequest(new DocumentViewInitializer(getRowKey(rowIndex))));
		}
	}

	class CellRenderer implements ITableCellRenderer<DocRef> {
		
		@Override
		public void renderCell(int rowIndex, final int cellIndex, final DocRef rowData, Column column,
				final HTMLTable table) {
			switch(cellIndex) {
				case 0:
					table.setText(rowIndex, cellIndex, rowData.getTitle());
					break;
				case 1:
					String sdate = Fmt.format(rowData.getDate(), GlobalFormat.DATE);
					table.setText(rowIndex, cellIndex, sdate);
					break;
				case 2: {
					Image img = new Image(Resources.INSTANCE.delete());
					img.setTitle("Delete document..");
					img.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							event.stopPropagation();
							String docref = rowData.getTitle();
							if(Window.confirm("Delete document '" + docref + "'?")) {
								DocRef deleted = (DocRef) ClientModelCache.get().remove(rowData.getModelKey(), table);
								if(deleted != null) {
									operator.refresh();
								}
							}
						}
					});
					table.setWidget(rowIndex, cellIndex, img);
					break;
				}
			}
		}
	} // CellRenderer
	
	static class ListingConfig extends AbstractListingConfig {

		static final Sorting defaultSorting = new Sorting("title");

		static final String[] modelProps = new String[] { "title", "date" };

		static final Column[] cols = new Column[] {
			new Column("Title", null, "title", null, "title"),
			new Column("Date", GlobalFormat.DATE, "date", null, "date"), 
			new Column("", null, null, null, "delete"), 
		};

		public ListingConfig() {
			super("Document", modelProps, cols, defaultSorting, 1000);
		}

		@Override
		public boolean isShowRefreshBtn() {
			return false;
		}

		@Override
		public boolean isShowNavBar() {
			return false;
		}
	} // ListingConfig

	static final IListingConfig config = new ListingConfig();

	private Operator operator;

	private final DocListing listingWidget;

	private final FlowPanel pnl = new FlowPanel();
	
	/**
	 * Constructor
	 */
	public DocumentsListingWidget() {
		super();
		
		listingWidget = new DocListing();
		
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
	
	public static class Operator extends DataListingOperator<DocRef, InMemoryListHandler<DocRef>> {

		public Operator() {
			super(config.getPageSize(), new InMemoryListHandler<DocRef>(), config.getDefaultSorting());
		}

		@SuppressWarnings("unchecked")
		@Override
		public void refresh() {
			getDataProvider().setList((List<DocRef>)ClientModelCache.get().getAll(EntityType.DOCUMENT));
			super.refresh();
		}
	}
	
	public void loadData() {
		// get docs from server
		if(operator == null) {
			new RpcCommand<DocListingPayload>() {
				
				@Override
				protected void doExecute() {
					setSource(listingWidget);
					Poc.getDocService().getCachedDocs(this);
				}

				@Override
				protected void handleSuccess(DocListingPayload result) {
					super.handleSuccess(result);
					ClientModelCache.get().persistAll(result.getCachedDocs());
					operator = new Operator();
					listingWidget.getTable().setCellRenderer(new CellRenderer());
					listingWidget.setOperator(operator);
					operator.refresh();
				}
			}.execute();
		}
	}
}
