/**
 * The Logic Lab
 * @author jpk
 * @since Apr 19, 2010
 */
package com.tabulaw.client.ui.login;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.Poc;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.edit.IEditHandler;
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
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.rpc.UserRegistrationRequest;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;

/**
 * @author jpk
 */
public class UserRegisterPanel extends FieldGroupEditPanel implements IEditHandler<FieldGroup> {

	static class UserRegisterFieldPanel extends AbstractFieldPanel<FlowPanel> {

		static class FieldProvider extends AbstractFieldGroupProvider {

			@Override
			protected String getFieldGroupName() {
				return "User Registration";
			}

			@Override
			protected void populateFieldGroup(final FieldGroup fg) {

				TextField email = FieldFactory.femail("userEmail", "emailAddress", "Email Address", "Your email address", 25);
				PasswordField password = FieldFactory.fpassword("userPswd", "password", "Password", "Specify a password", 12);
				PasswordField passwordConfirm = FieldFactory.fpassword("userPswdConfirm", "passwordConfirm", "Confirm Password", "Confirm your password", 12);
				
				fg.addField(email);
				fg.addField(password);
				fg.addField(passwordConfirm);

				final PropertyMetadata userEmailMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 50);
				final PropertyMetadata userPasswordMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 30);
				
				email.setPropertyMetadata(userEmailMetadata);
				password.setPropertyMetadata(userPasswordMetadata);
				passwordConfirm.setPropertyMetadata(userPasswordMetadata);
				
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
		}

		/**
		 * Renders the add quote bundle fields.
		 * @author jpk
		 */
		static class Renderer implements IFieldRenderer<FlowPanel> {

			public void render(FlowPanel panel, FieldGroup fg) {
				final GridFieldComposer cmpsr = new GridFieldComposer();
				cmpsr.setCanvas(panel);
				cmpsr.addField(fg.getFieldWidget("userEmail"));
				cmpsr.addField(fg.getFieldWidget("userPswd"));
				cmpsr.addField(fg.getFieldWidget("userPswdConfirm"));
			}
		}

		private final FlowPanel panel = new FlowPanel();

		/**
		 * Constructor
		 */
		public UserRegisterFieldPanel() {
			super();

			initWidget(panel);
		}

		@Override
		protected FieldGroup generateFieldGroup() {
			return new FieldProvider().getFieldGroup();
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new Renderer();
		}
	}

	/**
	 * Constructor
	 */
	public UserRegisterPanel() {
		super("Register", null, "Cancel", null, new UserRegisterFieldPanel());
	}

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

}
