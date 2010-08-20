/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.AbstractModelChangeAwareWidget;
import com.tabulaw.client.ui.listing.AbstractListingConfig;
import com.tabulaw.client.ui.listing.Column;
import com.tabulaw.client.ui.listing.DataListingOperator;
import com.tabulaw.client.ui.listing.IListingConfig;
import com.tabulaw.client.ui.listing.ITableCellRenderer;
import com.tabulaw.client.ui.listing.ModelListingTable;
import com.tabulaw.client.ui.listing.ModelListingWidget;
import com.tabulaw.client.util.Fmt;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.common.data.rpc.UserListPayload;
import com.tabulaw.dao.Sorting;
import com.tabulaw.listhandler.InMemoryListHandler;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.User;

/**
 * Lists user entities that fires
 * @author jpk
 */
public class UsersListingWidget extends AbstractModelChangeAwareWidget implements HasSelectionHandlers<User> {

	static class ListingConfig extends AbstractListingConfig {

		static final Sorting defaultSorting = new Sorting("name");

		static final Column[] cols =
				new Column[] {
					Column.ROW_COUNT_COLUMN, 
					new Column("User", "name"), 
					new Column("Email", "emailAddress"),
					new Column("Created", GlobalFormat.DATE, "dateCreated"),
					new Column("Modified", GlobalFormat.DATE, "dateModified"), 
					new Column("Roles", "roles"),
				};

		public ListingConfig() {
			super("User", null, cols, defaultSorting, 1000);
		}

		@Override
		public boolean isShowRefreshBtn() {
			return false;
		}
	} // ListingConfig

	static class UserListing extends ModelListingWidget<User, UsersListingWidget.Table> /*implements IRpcHandler*/{

		// final RpcUiHandler rpcui;

		public UserListing() {
			super(config.getListingId(), config.getListingElementName(), new Table(config), null);
			// rpcui = new RpcUiHandler(this);
		}

		Table getTable() {
			return table;
		}

		@Override
		protected Widget createNoDataRowsWidget() {
			return new Label("Currently, no Users exist.");
		}

		/*
		 * We override to re-fetch all from server 
		 * as we don't track user entities client-side save for the logged in user
		 */
		@Override
		public void onModelChangeEvent(ModelChangeEvent event) {
			if(event.getModelKey() != null && event.getModelKey().getEntityType().equals(EntityType.USER.name())) {
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

	static class Table extends ModelListingTable<User> {

		private HasSelectionHandlers<User> selectionDispatcher;
		
		public void setSelectionDispatcher(HasSelectionHandlers<User> selectionHandler) {
			this.selectionDispatcher = selectionHandler;
		}

		public Table(IListingConfig config) {
			super(config);
			setCellRenderer(new ITableCellRenderer<User>() {

				@Override
				public void renderCell(int rowIndex, int cellIndex, final User rowData, Column column, HTMLTable table) {
					if("Roles".equals(column.getName())) {
						String val = (String) rowData.getPropertyValue(column.getPropertyName());
						boolean isAdmin = val.indexOf(User.Role.ADMINISTRATOR.name()) >= 0;
						val = isAdmin ? "Administrator" : "User";
						table.setText(rowIndex, cellIndex, val);
					}
					else {
						Object val = rowData.getPropertyValue(column.getPropertyName());
						table.setText(rowIndex, cellIndex, Fmt.format(val, column.getFormat()));
					}
				}

			});
		}

		@Override
		protected void onCellClick(int colIndex, int rowIndex) {
			User user = getRowData(rowIndex);
			if(user != null) SelectionEvent.fire(selectionDispatcher, user);
		}
	}

	static final IListingConfig config = new ListingConfig();

	private final Operator operator;

	private final UserListing listingWidget;

	private final FlowPanel pnl = new FlowPanel();

	/**
	 * Constructor
	 */
	public UsersListingWidget() {
		super();

		operator = new Operator();

		listingWidget = new UserListing();
		listingWidget.setOperator(operator);

		// hack
		listingWidget.getTable().setSelectionDispatcher(this);

		pnl.add(listingWidget);

		initWidget(pnl);
	}

	public UserListing getListingWidget() {
		return listingWidget;
	}

	public Operator getOperator() {
		return operator;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<User> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		super.onModelChangeEvent(event);
		listingWidget.onModelChangeEvent(event);
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
					Poc.getUserAdminService().getAllUsers(this);
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
}
