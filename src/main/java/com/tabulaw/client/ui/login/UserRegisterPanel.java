/**
 * The Logic Lab
 * @author jpk
 * @since Apr 19, 2010
 */
package com.tabulaw.client.ui.login;

import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.login.LoginTopPanel.Mode;

/**
 * @author jpk
 */
public class UserRegisterPanel extends FieldGroupEditPanel /*implements IEditHandler<FieldGroup>*/ {

	/**
	 * Constructor
	 */
	public UserRegisterPanel() {
		super("Register", null, "Cancel", null, new FieldPanel(Mode.REGISTER));
	}

	/*
	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		if(event.getOp() == EditEvent.EditOp.SAVE) {
			FieldGroup fg = event.getContent();
			String userEmail = (String) fg.getFieldWidget("userEmail").getValue();
			String userPswd = (String) fg.getFieldWidget("userPswd").getValue();
			UserRegistrationRequest request = new UserRegistrationRequest(userEmail, userPswd);
			Poc.getUserRegisterService().registerUser(request, new AsyncCallback<Payload>() {

				@Override
				public void onSuccess(Payload result) {
					// TODO handle user registration success
				}

				@Override
				public void onFailure(Throwable caught) {
					String emsg = "Unable to perform user registration.";
					Notifier.get().error(emsg);
				}
			});
		}
		else {
			// TODO hide
		}
	}
	*/

}
