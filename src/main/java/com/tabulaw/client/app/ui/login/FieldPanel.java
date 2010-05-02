/**
 * The Logic Lab
 * @author jpk
 * @since Apr 21, 2010
 */
package com.tabulaw.client.app.ui.login;

import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.app.ui.login.LoginTopPanel.Mode;
import com.tabulaw.client.ui.field.AbstractFieldGroupProvider;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.GridFieldComposer;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.PasswordField;
import com.tabulaw.client.ui.field.TextField;
import com.tabulaw.client.validate.IValidator;
import com.tabulaw.client.validate.ValidationException;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;

/**
 * Common field panel for use in login and user registration.
 * <p>
 * package level visibility only
 * @author jpk
 */
class FieldPanel extends AbstractFieldPanel<FlowPanel> {

	static final PropertyMetadata userNameMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 50);
	static final PropertyMetadata userEmailMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 128);
	static final PropertyMetadata userPasswordMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 30);

	/**
	 * Provides login or user register fields depending on the flag provided upon
	 * construction.
	 * @author jpk
	 */
	class UserFieldProvider extends AbstractFieldGroupProvider {

		@Override
		protected String getFieldGroupName() {
			return mode == Mode.LOGIN ? "Login" : "User Registration";
		}

		@Override
		protected void populateFieldGroup(final FieldGroup fg) {

			int visibleFieldLen = 30;
			
			TextField email = FieldFactory.femail("userEmail", "emailAddress", "Email Address", "Your email address", visibleFieldLen);
			email.setPropertyMetadata(userEmailMetadata);
			fg.addField(email);

			PasswordField password = FieldFactory.fpassword("userPswd", "password", "Password", "Specify a password", visibleFieldLen);
			password.setPropertyMetadata(userPasswordMetadata);
			fg.addField(password);

			if(mode == Mode.REGISTER) {
				TextField fname = FieldFactory.ftext("userName", "name", "Name", "Your name", visibleFieldLen);
				fname.setPropertyMetadata(userNameMetadata);
				fg.addField(fname);
				
				PasswordField passwordConfirm =
						FieldFactory.fpassword("userPswdConfirm", "passwordConfirm", "Confirm Password", "Confirm your password",
								visibleFieldLen);
				passwordConfirm.setPropertyMetadata(userPasswordMetadata);
				fg.addField(passwordConfirm);

				fg.addValidator(new IValidator() {

					@Override
					public Object validate(Object value) throws ValidationException {
						// password/confirm password match
						String p = (String) fg.getFieldWidget("userPswd").getValue();
						String pc = (String) fg.getFieldWidget("userPswdConfirm").getValue();
						if(p != null && pc != null && !p.equals(pc)) {
							throw new ValidationException("Password and Confirm Password do not match.");
						}
						return null;
					}
				});
			}

			fg.validateIncrementally(false);
		}
	}

	private final FlowPanel panel = new FlowPanel();

	private final Mode mode;

	/**
	 * Constructor
	 * @param mode
	 */
	public FieldPanel(Mode mode) {
		super();
		if(mode == null) throw new NullPointerException();
		this.mode = mode;
		initWidget(panel);
	}

	@Override
	protected FieldGroup generateFieldGroup() {
		return new UserFieldProvider().getFieldGroup();
	}

	@Override
	protected IFieldRenderer<FlowPanel> getRenderer() {
		return new IFieldRenderer<FlowPanel>() {

			@Override
			public void render(FlowPanel widget, FieldGroup fg) {
				final GridFieldComposer cmpsr = new GridFieldComposer();
				cmpsr.setCanvas(panel);
				
				if(mode == Mode.LOGIN) {
					// login mode
					cmpsr.addField(fg.getFieldWidget("userEmail"));
					cmpsr.addField(fg.getFieldWidget("userPswd"));
				}
				else {
					// register mode
					cmpsr.addField(fg.getFieldWidget("userName"));
					cmpsr.addField(fg.getFieldWidget("userEmail"));
					cmpsr.addField(fg.getFieldWidget("userPswd"));
					cmpsr.addField(fg.getFieldWidget("userPswdConfirm"));
				}
			}
		};
	}
}