/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.ui.Composite;

/**
 * Shows a listing of all registered users in the system and a edit section for
 * editing user records.
 * @author jpk
 */
public class ManageUsersWidget extends Composite {

	private final UsersListingWidget listing;

	/**
	 * Constructor
	 */
	public ManageUsersWidget() {
		super();

		listing = new UsersListingWidget();

		initWidget(listing);
	}

	public void refresh() {
		listing.getOperator().refresh();
	}
}
