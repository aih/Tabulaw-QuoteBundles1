/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.Date;

import com.tabulaw.client.ui.edit.AbstractEditPanel;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.common.model.Authority;
import com.tabulaw.common.model.User;

/**
 * @author jpk
 */
public class UserEditPanel extends AbstractEditPanel<User, UserFieldPanel> {

	private User user;

	/**
	 * Constructor
	 */
	public UserEditPanel() {
		super("Save", "Delete", null, "Reset", new UserFieldPanel());
		getFieldPanel().getFieldGroup().setEnabled(false);
	}
	
	@SuppressWarnings("unchecked")
	public void setUser(User user) {
		if(user.isNew()) {
			getFieldPanel().getFieldGroup().clearValue();
			setSaveButtonText("Create");
		}
		else {
			// set title
			setSaveButtonText("Update");
		}
		// set fields
		FieldGroup fg = getFieldPanel().getFieldGroup();
		fg.getFieldWidget("userName").setValue(user.getName());
		fg.getFieldWidget("userEmail").setValue(user.getEmailAddress());
		fg.getFieldWidget("userLocked").setValue(user.isLocked());
		fg.getFieldWidget("userEnabled").setValue(user.isEnabled());
		fg.getFieldWidget("userExpires").setValue(user.getExpires());

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
		user.getAuthorities().clear();
		user.addAuthority(new Authority(role));

		return user;
	}

}
