/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.ModelKey;
import com.tabulaw.common.model.User;

/**
 * Shows a listing of all registered users in the system and a edit section for
 * editing user records.
 * @author jpk
 */
public class UsersWidget extends Composite {

	static final class Styles {

		public static final String MANAGE_USER = "manageUserWidget";
		public static final String CREATE_USER = "createUser";
		public static final String EDIT_CONTAINER = "editContainer";
		public static final String TITLE = "title";
		public static final String RIGHT = "right";
	}

	private final UsersListingWidget listing;

	private final Label lblTitle = new Label();

	private final UserEditPanel editPanel;

	private final FlowPanel panel, editContainer, rightPanel;

	/**
	 * Constructor
	 */
	public UsersWidget() {
		super();

		panel = new FlowPanel();
		panel.setStyleName(Styles.MANAGE_USER);

		editContainer = new FlowPanel();
		editContainer.setStyleName(Styles.EDIT_CONTAINER);

		lblTitle.setStyleName(Styles.TITLE);
		editContainer.add(lblTitle);

		rightPanel = new FlowPanel();
		rightPanel.setStyleName(Styles.RIGHT);

		listing = new UsersListingWidget();
		panel.add(listing);
		panel.add(rightPanel);

		editPanel = new UserEditPanel();
		editContainer.add(editPanel);
		rightPanel.add(editContainer);
		editContainer.setVisible(false); // hide initially

		initWidget(panel);

		listing.addSelectionHandler(new SelectionHandler<User>() {

			@Override
			public void onSelection(SelectionEvent<User> event) {
				User user = event.getSelectedItem();
				assert user != null;

				editPanel.setEditable(!user.isSuperuser());
				editPanel.setUser(user);
				lblTitle.setText(user.isSuperuser() ? user.getName() : "Edit " + user.getName());
				editContainer.setVisible(true);
			}
		});

		editPanel.addEditHandler(new IEditHandler<User>() {

			@Override
			public void onEdit(EditEvent<User> event) {
				switch(event.getOp()) {
					default:
					case CANCEL:
						listing.setVisible(true);
						break;
					case SAVE: {
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
								Notifier.get().showFor(result);
								Poc.fireModelChangeEvent(new ModelChangeEvent(UsersWidget.this, ModelChangeOp.UPDATED, result.getModel(), null));
							}
						}.execute();
						break;
					}
					case DELETE: {
						final ModelKey key = editPanel.user.getModelKey();
						new RpcCommand<Payload>() {

							@Override
							protected void doExecute() {
								setSource(listing);
								Poc.getUserAdminService().deleteUser(key.getId(), this);
							}

							@Override
							protected void handleSuccess(Payload result) {
								super.handleSuccess(result);
								Notifier.get().showFor(result);
								Poc.fireModelChangeEvent(new ModelChangeEvent(UsersWidget.this, ModelChangeOp.DELETED, null, key));
							}
						}.execute();
						break;
					}
				}
			}
		});
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		listing.makeModelChangeAware();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		listing.unmakeModelChangeAware();
	}

	public void refresh() {
		listing.getOperator().refresh();
	}

	public void newUserMode() {
		listing.setVisible(false);
		editContainer.setVisible(true);
		lblTitle.setText("Create User");
		User user = new User();
		editPanel.setUser(user);
	}
}
