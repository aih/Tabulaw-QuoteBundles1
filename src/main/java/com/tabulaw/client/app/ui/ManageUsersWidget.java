/**
 * The Logic Lab
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.data.rpc.RpcCommand;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.model.ModelChangeEvent.ModelChangeOp;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.SimpleHyperLink;
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
	
	static final class Styles {
		public static final String MANAGE_USER = "manageUserWidget";
		public static final String CREATE_USER = "createUser";
		public static final String RIGHT = "right";
	}

	private final UsersListingWidget listing;

	private final SimpleHyperLink lnkCreateUser;
	
	private final UserEditPanel editPanel;

	private final FlowPanel panel;

	private final FlowPanel rightPanel;
	
	/**
	 * Constructor
	 */
	public ManageUsersWidget() {
		super();
		
		panel = new FlowPanel();
		panel.setStyleName(Styles.MANAGE_USER);
		
		rightPanel = new FlowPanel();
		rightPanel.setStyleName(Styles.RIGHT);

		listing = new UsersListingWidget();
		
		lnkCreateUser = new SimpleHyperLink("Create User", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				User user = new User();
				editPanel.setUser(user);
			}
		});
		lnkCreateUser.addStyleName(Styles.CREATE_USER);
		
		editPanel = new UserEditPanel();

		panel.add(listing);
		
		rightPanel.add(lnkCreateUser);
		rightPanel.add(editPanel);
		panel.add(rightPanel);

		initWidget(panel);

		listing.addSelectionHandler(new SelectionHandler<User>() {
			
			@Override
			public void onSelection(SelectionEvent<User> event) {
				editPanel.setUser(event.getSelectedItem());
			}
		});

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
