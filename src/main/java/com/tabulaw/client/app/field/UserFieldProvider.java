/**
 * The Logic Lab
 * @author jpk
 * @since May 7, 2010
 */
package com.tabulaw.client.app.field;

import java.util.HashMap;
import java.util.Map;

import com.tabulaw.client.app.model.EntityMetadataProvider;
import com.tabulaw.client.ui.GridRenderer;
import com.tabulaw.client.ui.field.AbstractFieldGroupProvider;
import com.tabulaw.client.ui.field.CheckboxField;
import com.tabulaw.client.ui.field.DateField;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.PasswordField;
import com.tabulaw.client.ui.field.RadioGroupField;
import com.tabulaw.client.ui.field.TextField;
import com.tabulaw.client.validate.IValidator;
import com.tabulaw.client.validate.ValidationException;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.User.Role;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.util.StringUtil;

/**
 * Generates user related fields according to user case.
 * @author jpk
 */
public class UserFieldProvider extends AbstractFieldGroupProvider {

	public static enum UserUseCase {
		LOGIN,
		REGISTER,
		UPDATE,
		CREATE;
	}

	private final UserUseCase useCase;

	/**
	 * Constructor
	 * @param useCase
	 */
	public UserFieldProvider(UserUseCase useCase) {
		super();
		if(useCase == null) throw new NullPointerException();
		this.useCase = useCase;
	}

	@Override
	protected String getFieldGroupName() {
		return "User";
	}

	@Override
	protected void populateFieldGroup(final FieldGroup fg) {
		Map<String, PropertyMetadata> metamap = EntityMetadataProvider.get().getEntityMetadata(EntityType.USER);
		int visibleLen = 30;

		if(useCase == UserUseCase.CREATE || useCase == UserUseCase.UPDATE || useCase == UserUseCase.REGISTER) {
			// name
			TextField fname = FieldFactory.ftext("userName", "name", "Name", "Name", visibleLen);
			fname.setPropertyMetadata(metamap.get("name"));
			fg.addField(fname);
		}

		// email
		TextField femail = femail("userEmail", "emailAddress", "Email Address", "Your email address", visibleLen);
		femail.setPropertyMetadata(metamap.get("emailAddress"));
		fg.addField(femail);

		if(useCase == UserUseCase.UPDATE) {
			// locked
			CheckboxField flocked = fcheckbox("userLocked", "locked", "Locked?", "User locked?");
			flocked.setPropertyMetadata(metamap.get("locked"));
			fg.addField(flocked);
	
			// enabled
			CheckboxField fenabled = fcheckbox("userEnabled", "enabled", "Enabled?", "User enabled?");
			fenabled.setPropertyMetadata(metamap.get("enabled"));
			fg.addField(fenabled);
		}
		
		// expires
		if(useCase == UserUseCase.CREATE || useCase == UserUseCase.UPDATE) {
			DateField fexpires = fdate("userExpires", "expires", "Expiry Date", "Date user account expires");
			fexpires.setPropertyMetadata(metamap.get("expires"));
			fg.addField(fexpires);
	
			HashMap<Role, String> dataMap = new HashMap<Role, String>();
			for(Role role : Role.values()) {
				dataMap.put(role, StringUtil.enumStyleToPresentation(role.name()));
			}
	
			GridRenderer userRolesRenderer = new GridRenderer(Role.values().length, null);
			RadioGroupField<Role> fuserRoles =
					FieldFactory.fradiogroup("userRoles", "roles", null, "The user roles", dataMap, userRolesRenderer);
			fg.addField(fuserRoles);
		}

		// password
		if(useCase == UserUseCase.LOGIN || useCase == UserUseCase.REGISTER || useCase == UserUseCase.CREATE) {
			PasswordField password = FieldFactory.fpassword("userPswd", "password", "Password", "Specify a password", visibleLen);
			password.setPropertyMetadata(metamap.get("password"));
			fg.addField(password);
		}
		
		// confirm password
		if(useCase == UserUseCase.REGISTER || useCase == UserUseCase.CREATE) {
			PasswordField passwordConfirm =
					FieldFactory.fpassword("userPswdConfirm", "passwordConfirm", "Confirm Password", "Confirm your password", visibleLen);
			passwordConfirm.setPropertyMetadata(metamap.get("password"));
			fg.addField(passwordConfirm);
		
			passwordConfirm.addValidator(new IValidator() {
	
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