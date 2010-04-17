/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.Poc;
import com.tabulaw.client.model.PocModelCache;
import com.tabulaw.client.ui.listing.AbstractListingConfig;
import com.tabulaw.client.ui.listing.Column;
import com.tabulaw.client.ui.listing.DataListingOperator;
import com.tabulaw.client.ui.listing.IListingConfig;
import com.tabulaw.client.ui.listing.ModelCellRenderer;
import com.tabulaw.client.ui.listing.ModelPropertyFormatter;
import com.tabulaw.client.view.DocumentViewInitializer;
import com.tabulaw.common.data.rpc.DocListingPayload;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.listhandler.InMemoryListHandler;
import com.tll.client.data.rpc.RpcCommand;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ShowViewRequest;
import com.tll.client.util.GlobalFormat;
import com.tll.common.model.Model;
import com.tll.dao.Sorting;

/**
 * Lists documents for a given user.
 * @author jpk
 */
public class DocumentsListingWidget extends AbstractModelChangeAwareWidget {

	static class DocListing extends ModelListingWidget<DocumentsListingWidget.Table> /*implements IRpcHandler*/ {

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
			if(event.getModelKey() != null && event.getModelKey().getEntityType() == EntityType.DOCUMENT) {
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

	static class Table extends ModelListingTable {

		public Table(IListingConfig<Model> config) {
			super(config);
		}

		@Override
		protected void onCellClick(int colIndex, int rowIndex) {
			if(rowIndex > 0 && colIndex < 2)
				ViewManager.get().dispatch(new ShowViewRequest(new DocumentViewInitializer(getRowKey(rowIndex))));
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

	class CellRenderer extends ModelCellRenderer {
		
		@Override
		public void renderCell(int rowIndex, final int cellIndex, final Model rowData, Column column,
				final HTMLTable table) {
			if(cellIndex == 2) {
				Image img = new Image("images/trash.gif", 0, 0, 10, 11);
				img.setTitle("Delete document..");
				img.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						event.stopPropagation();
						String docref = rowData.asString("title");
						if(Window.confirm("Delete document '" + docref + "'?")) {
							Model deleted = PocModelCache.get().remove(rowData.getKey(), table);
							if(deleted != null) {
								operator.refresh();
							}
						}
					}
				});
				table.setWidget(rowIndex, cellIndex, img);
			}
			else {
				String cv = ModelPropertyFormatter.pformat(rowData, column.getPropertyName(), column.getFormat());
				table.setHTML(rowIndex, cellIndex, cv == null ? getValueForNull() : cv);
			}
		}
	} // CellRenderer
	
	static class ListingConfig extends AbstractListingConfig<Model> {

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

	static final IListingConfig<Model> config = new ListingConfig();

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
	
	public static class Operator extends DataListingOperator<Model, InMemoryListHandler<Model>> {

		public Operator() {
			super(config.getPageSize(), new InMemoryListHandler<Model>(), config.getDefaultSorting());
		}

		@Override
		public void refresh() {
			getDataProvider().setList(PocModelCache.get().getAll(EntityType.DOCUMENT));
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
					PocModelCache.get().persistAll(result.getCachedDocs());
					operator = new Operator();
					listingWidget.getTable().setCellRenderer(new CellRenderer());
					listingWidget.setOperator(operator);
					operator.refresh();
				}
			}.execute();
		}
	}
}
