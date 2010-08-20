/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcCommand;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.common.data.rpc.ModelPayload;
import com.tabulaw.common.data.rpc.Payload;
import com.tabulaw.model.ModelKey;
import com.tabulaw.model.User;

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
	}

	private final UsersListingWidget listing;

	private final Label lblTitle = new Label();

	private final UserEditPanel editPanel;

	private final FlowPanel panel, editContainer, userFloatPanel;

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

		userFloatPanel = new FlowPanel();
		
		listing = new UsersListingWidget();
		panel.add(listing);
		panel.add(userFloatPanel);

		editPanel = new UserEditPanel();
		editContainer.add(editPanel);
		userFloatPanel.add(editContainer);
		editContainer.setVisible(false); // hide initially

		initWidget(panel);

		listing.addSelectionHandler(new SelectionHandler<User>() {

			@Override
			public void onSelection(SelectionEvent<User> event) {
				userFloatPanel.setStyleName("right");
				User user = event.getSelectedItem();
				assert user != null;
				editPanel.setUser(user);
				lblTitle.setText(user.isSuperuser() ? user.getName() : "Edit " + user.getName());
				editContainer.setVisible(true);
			}
		});

		editPanel.addEditHandler(new IEditHandler<User>() {

			@Override
			public void onEdit(EditEvent<User> event) {
				final User editedUser = event.getContent();
				switch(event.getOp()) {
					default:
					case CANCEL:
						listing.setVisible(true);
						editContainer.setVisible(false);
						break;
					case SAVE: {
						new RpcCommand<ModelPayload<User>>() {

							@Override
							protected void doExecute() {
								setSource(listing);
								if(editedUser.isNew()) {
									Poc.getUserAdminService().createUser(editedUser, this);
									editContainer.setVisible(false);
								}
								else {
									Poc.getUserAdminService().updateUser(editedUser, this);
								}
								listing.setVisible(true);
							}

							@Override
							protected void handleSuccess(ModelPayload<User> result) {
								super.handleSuccess(result);
								Notifier.get().showFor(result);
								User updatedUser = result.getModel();
								// update the user in the edit panel since it remains visible 
								if(!editedUser.isNew()) editPanel.setUser(updatedUser);
								Poc.fireModelChangeEvent(new ModelChangeEvent(UsersWidget.this, ModelChangeOp.UPDATED, updatedUser, null));
							}
						}.execute();
						break;
					}
					case DELETE: {
						final ModelKey key = editPanel.getUser().getModelKey();
						if(Window.confirm("Delete user: " + key.descriptor() + "?")) {
							new RpcCommand<Payload>() {
	
								@Override
								protected void doExecute() {
									setSource(listing);
									Poc.getUserAdminService().deleteUser(key.getId(), this);
									editContainer.setVisible(false);
								}
	
								@Override
								protected void handleSuccess(Payload result) {
									super.handleSuccess(result);
									Notifier.get().showFor(result);
									Poc.fireModelChangeEvent(new ModelChangeEvent(UsersWidget.this, ModelChangeOp.DELETED, null, key));
								}
							}.execute();
						}
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
		userFloatPanel.setStyleName("left");
		listing.setVisible(false);
		editContainer.setVisible(true);
		lblTitle.setText("Create User");
		User user = new User();
		editPanel.setUser(user);
	}
}
