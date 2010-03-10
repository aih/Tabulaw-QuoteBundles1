/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.tll.client.listing.AbstractListingConfig;
import com.tll.client.listing.Column;
import com.tll.client.listing.DataListingOperator;
import com.tll.client.listing.IListingConfig;
import com.tll.client.listing.ITableCellRenderer;
import com.tll.client.listing.ModelCellRenderer;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.ShowViewRequest;
import com.tll.client.ui.listing.ModelListingTable;
import com.tll.client.ui.listing.ModelListingWidget;
import com.tll.client.util.GlobalFormat;
import com.tll.common.model.Model;
import com.tll.dao.Sorting;
import com.tll.listhandler.InMemoryListHandler;
import com.tll.tabulaw.client.model.PocModelStore;
import com.tll.tabulaw.client.view.DocumentViewInitializer;
import com.tll.tabulaw.common.model.PocEntityType;

/**
 * Lists documents for a given user.
 * @author jpk
 */
public class DocumentsListingWidget extends AbstractModelChangingWidget {

	static class DocListing extends ModelListingWidget<DocumentsListingWidget.Table> {

		public DocListing() {
			super(new Table(config), null);
		}
		
		@Override
		public void onModelChangeEvent(ModelChangeEvent event) {
			if(event.getModelKey() != null && event.getModelKey().getEntityType() == PocEntityType.DOCUMENT) {
				super.onModelChangeEvent(event);
			}
		}
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
	}

	static class ListingConfig extends AbstractListingConfig<Model> {

		static class CellRenderer extends ModelCellRenderer {

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
								Model deleted = PocModelStore.get().remove(rowData.getKey(), table);
								if(deleted != null) {
									operator.refresh();
								}
							}
						}
					});
					table.setWidget(rowIndex, cellIndex, img);
				}
				else {
					super.renderCell(rowIndex, cellIndex, rowData, column, table);
				}
			}
		} // CellRenderer

		static final Sorting defaultSorting = new Sorting("title");

		static final String[] modelProps = new String[] {
			"title", "case.date" };

		static final Column[] cols =
				new Column[] {
					new Column("Title", null, "title", null, "title"),
					new Column("Date", GlobalFormat.DATE, "case.date", null, "date"), new Column("", null, null, null, "delete"), };

		public ListingConfig() {
			super("Document", modelProps, cols, defaultSorting, 10);
		}

		@Override
		public boolean isShowRefreshBtn() {
			return false;
		}

		@Override
		public boolean isShowNavBar() {
			return false;
		}

		@Override
		public ITableCellRenderer<Model> getCellRenderer() {
			return new CellRenderer();
		}
	} // ListingConfig

	static final IListingConfig<Model> config = new ListingConfig();

	static class ListingOperator extends DataListingOperator<Model, InMemoryListHandler<Model>> {

		public ListingOperator() {
			super(config.getPageSize(), new InMemoryListHandler<Model>(), config.getDefaultSorting());
		}

		@Override
		public void refresh() {
			getDataProvider().setList(PocModelStore.get().getAll(PocEntityType.DOCUMENT));
			super.sort(config.getDefaultSorting());
		}
	} // ListingOperator

	static final ListingOperator operator = new ListingOperator();

	private final DocListing listingWidget;

	/**
	 * Constructor
	 */
	public DocumentsListingWidget() {
		super();
		listingWidget = new DocListing();
		listingWidget.setOperator(operator);
		initWidget(listingWidget);
	}
	
	public void refresh() {
		listingWidget.refresh();
	}
	
	public void clear() {
		listingWidget.clear();
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		listingWidget.onModelChangeEvent(event);
	}
}
