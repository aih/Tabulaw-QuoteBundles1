/**
 * The Logic Lab
 * @author jpk Aug 28, 2007
 */
package com.tabulaw.client.ui.listing;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ListingWidget - Base class for all listing {@link Widget}s in the app.
 * @author jpk
 * @param <R> The row data type.
 * @param <T> the table widget type
 */
public class ListingWidget<R, T extends ListingTable<R>> extends Composite implements Focusable, KeyDownHandler, IListingHandler<R> {

	/**
	 * Styles - (tableview.css)
	 * @author jpk
	 */
	protected static class Styles {

		/**
		 * The css class the top-most containing div gets.
		 */
		public static final String TABLE_VIEW = "tableView";

		/**
		 * The css class for table view captions.
		 */
		public static final String CAPTION = "caption";

		/**
		 * The css class for the label that is displayed when no data rows exist.
		 */
		public static final String NODATA = "nodata";

		/**
		 * The listing portal style.
		 */
		public static final String PORTAL = "portal";
	} // Styles

	protected final String listingId, listingElementName;

	/**
	 * The main "listing" panel containing all widgets comprising this widget.
	 */
	protected final FocusPanel focusPanel = new FocusPanel();

	/**
	 * The sole chile of the focus panel enabling multiple widgets to exist under
	 * the focus panel.
	 */
	protected final FlowPanel tableViewPanel = new FlowPanel();

	/**
	 * Wrapped around the listing table enabling vertical scrolling.
	 */
	protected final ScrollPanel portal = new ScrollPanel();

	/**
	 * The listing table.
	 */
	protected final T table;

	/**
	 * The listing navigation bar.
	 */
	protected final ListingNavBar<R> navBar;

	/**
	 * Displayed in place of the table when no data rows exist.
	 */
	private Widget noDataRowsWidget;

	/**
	 * The listing operator
	 */
	private IListingOperator<R> operator;

	/**
	 * The optional row popup.
	 */
	// protected RowContextPopup rowPopup;

	/**
	 * Constructor
	 * @param listingId
	 * @param listingElementName
	 * @param table listing table widget
	 * @param navBar optional nav bar
	 */
	public ListingWidget(String listingId, String listingElementName, T table, ListingNavBar<R> navBar) {
		super();

		this.listingId = listingId;
		this.listingElementName = listingElementName;
		tableViewPanel.setStylePrimaryName(Styles.TABLE_VIEW);
		focusPanel.add(tableViewPanel);
		focusPanel.addKeyDownHandler(this);
		initWidget(focusPanel);
		portal.setStyleName(Styles.PORTAL);
		tableViewPanel.add(portal);

		// table
		portal.add(table);
		this.table = table;
		
		// nav bar
		if(navBar != null) {
			this.navBar = navBar;
			tableViewPanel.add(navBar.getWidget());
		}
		else {
			this.navBar = null;
		}
	}

	/**
	 * @return the unique listing id
	 */
	public final String getListingId() {
		return listingId;
	}

	/**
	 * @return the presentation worthy listing elment data type name.
	 */
	public final String getListingElementName() {
		return listingElementName;
	}

	/**
	 * Sets the operator which is delegated to on behalf of this Widget for
	 * performing listing ops.
	 * @param operator The listing operator
	 */
	public final void setOperator(IListingOperator<R> operator) {
		if(operator == null) throw new IllegalArgumentException();
		if(this.operator != null) throw new IllegalStateException();
		this.operator = operator;
		operator.setSourcingWidget(this);
		this.table.setListingOperator(operator);
		if(navBar != null) navBar.setListingOperator(operator);

		// trap listing events fired by the operator that is being set
		addHandler(this, ListingEvent.TYPE);
	}

	/**
	 * @return The listing operator.
	 */
	public final IListingOperator<R> getOperator() {
		return operator;
	}

	/**
	 * @return The number of rows <em>shown</em> in the listing.
	 */
	public final int getNumRows() {
		return table.getRowCount();
	}

	/**
	 * Physically adds a row.
	 * @param rowData The row data to add
	 */
	public final void addRow(R rowData) {
		table.addRow(rowData);
		if(navBar != null) navBar.increment();
		handleTableVisibility();
	}

	/**
	 * Updates a row at the given rowIndex.
	 * @param rowIndex 0-based row index
	 * @param rowData
	 */
	public final void updateRow(int rowIndex, R rowData) {
		table.updateRow(rowIndex, rowData);
	}

	/**
	 * Physically deletes a row from the listing.
	 * @param rowIndex the 0-based index
	 */
	public final void deleteRow(int rowIndex) {
		table.deleteRow(rowIndex);
		if(navBar != null) navBar.decrement();
		handleTableVisibility();
	}

	/**
	 * Marks a row as deleted or un-deleted.
	 * @param rowIndex the 0-based index
	 * @param markDeleted true/false
	 */
	public final void markRowDeleted(int rowIndex, boolean markDeleted) {
		table.markRowDeleted(rowIndex, markDeleted);
	}

	public final boolean isRowMarkedDeleted(int rowIndex) {
		return table.isRowMarkedDeleted(rowIndex);
	}

	public final int getTabIndex() {
		return focusPanel.getTabIndex();
	}

	public final void setAccessKey(char key) {
		focusPanel.setAccessKey(key);
	}

	public final void setFocus(boolean focused) {
		focusPanel.setFocus(focused);
	}

	public final void setTabIndex(int index) {
		focusPanel.setTabIndex(index);
	}

	public void onKeyDown(KeyDownEvent event) {
		delegateEvent(table, event);
	}

	public final void setPortalHeight(String height) {
		portal.setHeight(height);
	}

	protected Widget createNoDataRowsWidget() {
		return new Label("Currently, no " + listingElementName + "s exist.");
	}

	private void handleTableVisibility() {
		// handle no data rows case
		boolean noDataRows = table.getRowCount() <= 1;
		portal.setVisible(!noDataRows);
		if(navBar != null) navBar.setVisible(!noDataRows);
		if(noDataRowsWidget == null) {
			// no data rows widget
			noDataRowsWidget = createNoDataRowsWidget();
			noDataRowsWidget.setStyleName(Styles.NODATA);
			noDataRowsWidget.setVisible(false);
			tableViewPanel.add(noDataRowsWidget);
		}
		if(noDataRowsWidget != null) noDataRowsWidget.setVisible(noDataRows);
	}

	public final void onListingEvent(ListingEvent<R> event) {
		table.onListingEvent(event);
		if(navBar != null) navBar.onListingEvent(event);
		handleTableVisibility();
	}
}
