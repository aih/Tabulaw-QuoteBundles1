/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.model.User;

/**
 * Shows a listing of all registered users in the system and a edit section for
 * editing user records.
 * @author jpk
 */
public class ManageUsersWidget extends Composite {

	private final UsersListingWidget listing;

	private final UserEditPanel editPanel;

	private final HorizontalPanel hp = new HorizontalPanel();

	/**
	 * Constructor
	 */
	public ManageUsersWidget() {
		super();

		listing = new UsersListingWidget();
		editPanel = new UserEditPanel();

		hp.add(listing);
		hp.add(editPanel);

		initWidget(hp);

		listing.addSelectionHandler(editPanel);

		editPanel.addEditHandler(new IEditHandler<User>() {

			@Override
			public void onEdit(EditEvent<User> event) {
				final User updatedUser = event.getContent();
				new RpcCommand<ModelPayload<User>>() {

					@Override
					protected void doExecute() {
						setSource(listing);
						Poc.getUserAdminService().updateUser(updatedUser, this);
					}

					@Override
					protected void handleSuccess(ModelPayload<User> result) {
						super.handleSuccess(result);
						User user = result.getModel();
						// hack - fire a manually created model change event on the user listing widget
						// in order to get the target row updated
						listing.getListingWidget().onModelChangeEvent(
								new ModelChangeEvent(ManageUsersWidget.this, ModelChangeOp.UPDATED, user, null));
						Notifier.get().showFor(result);
					}
				}.execute();
			}
		});
	}

	public void refresh() {
		listing.getOperator().refresh();
	}
}
