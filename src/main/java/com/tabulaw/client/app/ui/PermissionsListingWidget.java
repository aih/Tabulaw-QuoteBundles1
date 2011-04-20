/**
 * Copyright (C) Tabulaw, Inc. 2009-2011 All Rights Reserved
 * @author Andrey Levchenko
 * @since Apr 15, 2011
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.ui.RpcCommand;
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
import com.tabulaw.common.data.rpc.UserListPayload;
import com.tabulaw.dao.Sorting;
import com.tabulaw.listhandler.InMemoryListHandler;
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

	static final IListingConfig config = new ListingConfig();

	private final Operator operator;

	private final ModelListingWidget<User, ModelListingTable<User>> listingWidget;

	private final FlowPanel pnl = new FlowPanel();
	private String bundleId;

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
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

		listingWidget = new ModelListingWidget<User, ModelListingTable<User>>(config.getListingId(), config.getListingElementName(),table, null);
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
			new RpcCommand<UserListPayload>() {

				@Override
				protected void doExecute() {
					setSource(listingWidget);
					Poc.getUserDataService().getBundleUsers(bundleId, this);
				}

				@Override
				protected void handleSuccess(UserListPayload result) {
					super.handleSuccess(result);
					getDataProvider().setList(result.getUsers());
					firstPage();
				}
			}.execute();
		}
	}

	public void addListingHandler(IListingHandler<User> handler) {
		listingWidget.addHandler(handler, ListingEvent.TYPE);
	}
}
