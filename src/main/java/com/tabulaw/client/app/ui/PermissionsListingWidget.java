/**
 * Copyright (C) Tabulaw, Inc. 2009-2011 All Rights Reserved
 * @author Andrey Levchenko
 * @since Apr 15, 2011
 */
package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.ui.listing.AbstractListingConfig;
import com.tabulaw.client.ui.listing.Column;
import com.tabulaw.client.ui.listing.DataListingOperator;
import com.tabulaw.client.ui.listing.IListingConfig;
import com.tabulaw.client.ui.listing.IListingHandler;
import com.tabulaw.client.ui.listing.ITableCellRenderer;
import com.tabulaw.client.ui.listing.ListingEvent;
import com.tabulaw.client.ui.listing.ModelListingTable;
import com.tabulaw.client.ui.listing.ModelListingWidget;
import com.tabulaw.client.util.Fmt;
import com.tabulaw.dao.Sorting;
import com.tabulaw.listhandler.InMemoryListHandler;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;

/**
 * Lists bundle permissions
 * @author Andrey Levchenko
 */
public class PermissionsListingWidget extends Composite {

	static class ListingConfig extends AbstractListingConfig {

		static final Sorting defaultSorting = new Sorting("name");

		static final Column[] cols =
				new Column[] {
					new Column("User", "name"), 
					new Column("Email", "emailAddress")
				};

		public ListingConfig() {
			super("Permission", null, cols, defaultSorting, 1000);
		}

		@Override
		public boolean isShowRefreshBtn() {
			return false;
		}
	} // ListingConfig

	static class UserListing extends ModelListingWidget<User, ModelListingTable<User>> {
		private String noDataWarning = ""; 
		public UserListing(IListingConfig config, ModelListingTable<User> table) {
			super(config.getListingId(), config.getListingElementName(), table, null);
		}
		@Override
		protected Widget createNoDataRowsWidget() {
			return new HTML(noDataWarning + "You have not shared this Quote Bundle with other users.", true);
		}
		public void updateText(QuoteBundle bundle){
			if(bundle.getParentBundleId() != null){
				User user = ClientModelCache.get().getQuoteBundleOwner(bundle.getParentBundleId());
				noDataWarning = user.getName() + " shared this with you. <br/>";
			}
		}
		
	}
	
	static final IListingConfig config = new ListingConfig();

	private final Operator operator;

	private final UserListing listingWidget;

	private final FlowPanel pnl = new FlowPanel();
	private QuoteBundle bundle;

	public void setBundleId(QuoteBundle bundle) {
		this.bundle = bundle;
		listingWidget.updateText(bundle);
	}

	/**
	 * Constructor
	 */
	public PermissionsListingWidget() {
		super();
		ModelListingTable<User> table = new ModelListingTable<User>(config);

		table.setCellRenderer(new ITableCellRenderer<User>() {

			@Override
			public void renderCell(int rowIndex, int cellIndex, final User rowData, Column column, HTMLTable table) {
					Object val = rowData.getPropertyValue(column.getPropertyName());
					table.setText(rowIndex, cellIndex, Fmt.format(val, column.getFormat()));
			}

		});
		

		operator = new Operator();

		listingWidget = new UserListing(config, table);
		listingWidget.setOperator(operator);


		pnl.add(listingWidget);

		initWidget(pnl);
	}

	public void refresh() {
		operator.refresh();
	}

	class Operator extends DataListingOperator<User, InMemoryListHandler<User>> {

		public Operator() {
			super(config.getPageSize(), new InMemoryListHandler<User>(), config.getDefaultSorting());
		}

		@Override
		public void refresh() {
			listingGenerated = false; // force the data to re-pop into the table
			List<User> users = ClientModelCache.get().getQuoteBundlesOwners(bundle.getChildQuoteBundles());  
			getDataProvider().setList(users);
			firstPage();
			
		}
	}

	public void addListingHandler(IListingHandler<User> handler) {
		listingWidget.addHandler(handler, ListingEvent.TYPE);
	}
}
