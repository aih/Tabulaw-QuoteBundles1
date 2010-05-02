/**
 * The Logic Lab
 * @author jpk
 * @since Feb 14, 2010
 */
package com.tabulaw.client.app.ui.user;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.model.ClientModelCache;
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
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.User;
import com.tabulaw.dao.Sorting;
import com.tabulaw.listhandler.InMemoryListHandler;

/**
 * Lists user entities.
 * @author jpk
 */
public class UsersListingWidget extends AbstractModelChangeAwareWidget {

	static class ListingConfig extends AbstractListingConfig {

		static final Sorting defaultSorting = new Sorting("name");

		static final String[] modelProps = new String[] { 
			"name", "dateCreated", "dateModified", "emailAddress", "locked", "enabled", "expires", "authorities",
		};

		static final Column[] cols = new Column[] {
			Column.ROW_COUNT_COLUMN,
			new Column("Name", "name"),
			new Column("Created/Modified"), 
			new Column("Email", "emailAddress"), 
			new Column("Locked?", GlobalFormat.BOOL_YESNO, "locked"), 
			new Column("Enabled?", GlobalFormat.BOOL_YESNO, "enabled"), 
			new Column("Expires", GlobalFormat.DATE, "expires"), 
			new Column("Roles", "authorities"), 
		};

		public ListingConfig() {
			super("User", modelProps, cols, defaultSorting, 1000);
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

	static class UserListing extends ModelListingWidget<User, UsersListingWidget.Table> /*implements IRpcHandler*/ {

		//final RpcUiHandler rpcui;
		
		public UserListing() {
			super(config.getListingId(), config.getListingElementName(), new Table(config), null);
			//rpcui = new RpcUiHandler(this);
		}
		
		Table getTable() {
			return table;
		}
		
		@Override
		protected Widget createNoDataRowsWidget() {
			return new Label("Currently, no Users exist.");
		}

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

		public Table(IListingConfig config) {
			super(config, new ITableCellRenderer<User>() {

				@Override
				public void renderCell(int rowIndex, int cellIndex, User rowData, Column column, HTMLTable table) {
					if("Created/Modified".equals(column.getName())) {
						String created = Fmt.format(rowData.getDateCreated(), GlobalFormat.DATE);
						String modified = Fmt.format(rowData.getDateModified(), GlobalFormat.DATE);
						String html = "<div>" + created + "</div><div>" + modified + "</div>";
						table.setHTML(rowIndex, cellIndex, html);
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
			// TODO impl
			//if(rowIndex > 0 && colIndex < 2)
				//ViewManager.get().dispatch(new ShowViewRequest(new DocumentViewInitializer(getRowKey(rowIndex))));
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
	
	class Operator extends DataListingOperator<User, InMemoryListHandler<User>> {
		
		public Operator() {
			super(config.getPageSize(), new InMemoryListHandler<User>(), config.getDefaultSorting());
		}

		@Override
		public void refresh() {
			new RpcCommand<UserListPayload>() {
				
				@Override
				protected void doExecute() {
					setSource(listingWidget);
					ClientModelCache.getUserDataService().getAllUsers(this);
				}

				@Override
				protected void handleSuccess(UserListPayload result) {
					super.handleSuccess(result);
					listingWidget.setOperator(Operator.this);
					listingWidget.getOperator().firstPage();
				}
			}.execute();
		}
	}
}
