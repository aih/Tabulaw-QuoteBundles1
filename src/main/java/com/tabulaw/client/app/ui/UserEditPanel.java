/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.Date;

import com.tabulaw.client.ui.edit.AbstractEditPanel;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.client.validate.ErrorHandlerDelegate;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.User.Role;

/**
 * @author jpk
 */
public class UserEditPanel extends AbstractEditPanel<User, UserFieldPanel> {

	User user;

	/**
	 * Constructor
	 */
	public UserEditPanel() {
		super("Save", "Delete", null, "Reset", new UserFieldPanel());
		getFieldPanel().getFieldGroup().setEnabled(false);
		ErrorHandlerDelegate errorHandler = ErrorHandlerBuilder.build(false, true, null);
		getFieldPanel().getFieldGroup().setErrorHandler(errorHandler);
	}
	
	@SuppressWarnings("unchecked")
	public void setUser(User user) {
		FieldGroup fg = getFieldPanel().getFieldGroup();
		fg.clearValue();
		
		setSaveButtonText(user.isNew()? "Create" : "Update");

		// set fields
		fg.getFieldWidget("userName").setValue(user.getName());
		fg.getFieldWidget("userEmail").setValue(user.getEmailAddress());
		fg.getFieldWidget("userLocked").setValue(user.isLocked());
		fg.getFieldWidget("userEnabled").setValue(user.isEnabled());
		fg.getFieldWidget("userExpires").setValue(user.getExpires());

		// role
		if(user.getNumRoles() == 0) {
			// presume user role
			user.addRole(Role.USER);
		}
		Role role = user.getRoles().get(0);
		fg.getFieldWidget("userRoles").setValue(role);
		
		getFieldPanel().getFieldGroup().setEnabled(true);
	}

	@Override
	protected User getEditContent() {
		FieldGroup fg = getFieldPanel().getFieldGroup();

		if(user == null)
		// i.e. a new user
			user = new User();

		user.setName(fg.getFieldWidget("userName").getFieldValue());
		user.setEmailAddress(fg.getFieldWidget("userEmail").getFieldValue());
		user.setLocked((Boolean) fg.getFieldWidget("userLocked").getValue());
		user.setEnabled((Boolean) fg.getFieldWidget("userEnabled").getValue());
		user.setExpires((Date) fg.getFieldWidget("userExpires").getValue());

		String role = fg.getFieldWidget("userRoles").getFieldValue();
		user.getRoles().clear();
		user.addRole(Enum.valueOf(Role.class, role));

		return user;
	}
}
