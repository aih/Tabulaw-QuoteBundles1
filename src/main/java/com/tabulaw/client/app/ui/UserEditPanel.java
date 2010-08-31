/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.app.field.UserFieldProvider;
import com.tabulaw.client.app.field.UserFieldProvider.UserUseCase;
import com.tabulaw.client.ui.FocusCommand;
import com.tabulaw.client.ui.GridRenderer;
import com.tabulaw.client.ui.SimpleHyperLink;
import com.tabulaw.client.ui.edit.AbstractEditPanel;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.CellFieldComposer;
import com.tabulaw.client.ui.field.CheckboxField;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.IFieldWidget;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.model.AppFeature;
import com.tabulaw.model.User;
import com.tabulaw.model.User.Role;

/**
 * @author jpk
 */
public class UserEditPanel extends AbstractEditPanel<User> {

	static class CreateUserFieldPanel extends AbstractFieldPanel {

		@Override
		protected FieldGroup generateFieldGroup() {
			return new UserFieldProvider(UserUseCase.CREATE).getFieldGroup();
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {

				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					final CellFieldComposer cmpsr = new CellFieldComposer();
					cmpsr.setCanvas(widget);
					cmpsr.addField(fg.getFieldWidget("userName"));
					cmpsr.addField(fg.getFieldWidget("userEmail"));
					cmpsr.newRow();
					cmpsr.addField(fg.getFieldWidget("userPswd"));
					cmpsr.addField(fg.getFieldWidget("userPswdConfirm"));
					
					cmpsr.newRow();
					cmpsr.addField(fg.getFieldWidget("userRoles"), true);
					
					cmpsr.newRow();
					FieldGroup appFeaturesGroup = (FieldGroup) fg.getFieldByName("App Features");
					Set<IFieldWidget<?>> fappFeatures = appFeaturesGroup.getFieldWidgets(null);
					HashSet<Widget> fwidgets = new HashSet<Widget>(fappFeatures.size());
					for(IFieldWidget<?> fw : fappFeatures) {
						fwidgets.add(fw.getWidget());
					}
					GridRenderer gr = new GridRenderer(3, null);
					cmpsr.addWidget("App Features", gr.render(fwidgets));
				}
			};
		}
	}

	static class UpdateUserFieldPanel extends AbstractFieldPanel {
		
		private final SimpleHyperLink lnkSetPassword;

		/**
		 * Constructor
		 * @param lnkSetPassword optional
		 */
		public UpdateUserFieldPanel(SimpleHyperLink lnkSetPassword) {
			super();
			this.lnkSetPassword = lnkSetPassword;
		}

		@Override
		protected FieldGroup generateFieldGroup() {
			FieldGroup fg = new UserFieldProvider(UserUseCase.UPDATE).getFieldGroup();
			fg.setEnabled(false); // initial state
			fg.validateIncrementally(true);
			return fg;
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {

				@Override
				public void render(FlowPanel widget, FieldGroup fg) {

					final CellFieldComposer cmpsr = new CellFieldComposer();
					cmpsr.setCanvas(widget);
					cmpsr.addField(fg.getFieldWidget("userName"));
					cmpsr.newRow();
					IFieldWidget<?> femail = fg.getFieldWidget("userEmail");
					femail.setReadOnly(true);
					cmpsr.addField(femail);
					
					if(lnkSetPassword != null) cmpsr.addWidget("&nbsp;", lnkSetPassword);
					
					cmpsr.newRow();
					cmpsr.addField(fg.getFieldWidget("userEnabled"));
					cmpsr.addField(fg.getFieldWidget("userLocked"));
					cmpsr.newRow();
					cmpsr.addField(fg.getFieldWidget("userExpires"));
					
					cmpsr.newRow();
					cmpsr.addField(fg.getFieldWidget("userRoles"), true);
					
					cmpsr.newRow();
					FieldGroup appFeaturesGroup = (FieldGroup) fg.getFieldByName("App Features");
					Set<IFieldWidget<?>> fappFeatures = appFeaturesGroup.getFieldWidgets(null);
					HashSet<Widget> fwidgets = new HashSet<Widget>(fappFeatures.size());
					for(IFieldWidget<?> fw : fappFeatures) {
						fwidgets.add(fw.getWidget());
					}
					GridRenderer gr = new GridRenderer(3, null);
					cmpsr.addWidget("App Features", gr.render(fwidgets));
				}
			};
		}
	}

	/**
	 * user under edit.
	 */
	private User user;
	
	private UserUseCase mode;

	private AbstractFieldPanel fpCreate, fpUpdate;
	
	private SimpleHyperLink lnkSetPassword;

	private final UserPasswordSetDialog dlgResetPassword = new UserPasswordSetDialog();

	/**
	 * Constructor
	 */
	public UserEditPanel() {
		super();
		// set field-only error handler (no msg display)
		setErrorHandler(ErrorHandlerBuilder.build(false, true, null), false);
	}

	public UserUseCase getMode() {
		return mode;
	}

	public User getUser() {
		return user;
	}

	@SuppressWarnings("unchecked")
	public void setUser(User user) {
		// first set mode
		setMode(user.isNew() ? UserUseCase.CREATE : UserUseCase.UPDATE);
		
		FieldGroup fg = getFieldPanel().getFieldGroup();
		fg.clearValue();

		setSaveButtonText(user.isNew() ? "Create" : "Update");

		// set fields
		fg.getFieldWidget("userName").setValue(user.getName());
		fg.getFieldWidget("userEmail").setValue(user.getEmailAddress());
		if(!user.isNew()) {
			fg.getFieldWidget("userLocked").setValue(user.isLocked());
			fg.getFieldWidget("userEnabled").setValue(user.isEnabled());
			fg.getFieldWidget("userExpires").setValue(user.getExpires());
		}

		// role
		if(user.getNumRoles() == 0) {
			// presume user role
			user.addRole(Role.USER);
		}
		Role role = user.getRoles().get(0);
		fg.getFieldWidget("userRoles").setValue(role);
		
		// app features
		Set<AppFeature> userFeatures = user.getAppFeatures();
		for(AppFeature af : AppFeature.values()) {
			CheckboxField f = (CheckboxField) fg.getFieldWidget("user" + af.name());
			boolean hasFeature = userFeatures == null ? false : userFeatures.contains(af);
			f.setValue(hasFeature);
		}

		getFieldPanel().getFieldGroup().setEnabled(true);

		// don't allow superuser editing - this is their id!
		setEditable(!user.isSuperuser());

		// can't edit email once user exists
		if(!user.isSuperuser()) fg.getFieldWidget("userEmail").setReadOnly(!user.isNew());

		this.user = user;
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getEditContent() {
		FieldGroup fg = getFieldPanel().getFieldGroup();

		assert user != null;
		user.setName(fg.getFieldWidget("userName").getFieldValue());
		user.setEmailAddress(fg.getFieldWidget("userEmail").getFieldValue());
		if(!user.isNew()) {
			user.setLocked((Boolean) fg.getFieldWidget("userLocked").getValue());
			user.setEnabled((Boolean) fg.getFieldWidget("userEnabled").getValue());
			user.setExpires((Date) fg.getFieldWidget("userExpires").getValue());
		}
		else {
			user.setPassword(fg.getFieldWidget("userPswd").getFieldValue());
		}

		// roles
		String role = fg.getFieldWidget("userRoles").getFieldValue();
		user.getRoles().clear();
		user.addRole(Enum.valueOf(Role.class, role));

		// app features
		for(AppFeature af : AppFeature.values()) {
			String fname = "user" + af.name();
			IFieldWidget<Boolean> f = fg.getFieldWidget(fname);
			if(f.getValue() == Boolean.TRUE) {
				user.addAppFeature(af);
			}
			else {
				user.removeAppFeature(af);
			}
		}
		
		return user;
	}

	/**
	 * Swaps out or sets the contained field panel based on the given mode.
	 * @param mode user use case mode to set
	 */
	private void setMode(UserUseCase mode) {
		switch(mode) {
			case CREATE:
				if(fpCreate == null) fpCreate = new CreateUserFieldPanel();
				setFieldPanel(fpCreate);
				showCancelButton(true);
				showResetButton(false);
				showDeleteButton(false);
				addStyleName("createUser");
				removeStyleName("updateUser");
				break;
			case UPDATE:
				if(fpUpdate == null) {
					lnkSetPassword = new SimpleHyperLink("Set password", new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							event.getNativeEvent().stopPropagation();
							dlgResetPassword.set(user.getId(), user.getName(), user.getEmailAddress());
							dlgResetPassword.showRelativeTo((UIObject) event.getSource());
						}
					});
					lnkSetPassword.getElement().setId("spw");
					lnkSetPassword.setTitle("Set password..");
					fpUpdate = new UpdateUserFieldPanel(lnkSetPassword);
				}
				setFieldPanel(fpUpdate);
				showCancelButton(false);
				showResetButton(true);
				showDeleteButton(true);
				removeStyleName("createUser");
				addStyleName("updateUser");
				break;
			default:
				throw new IllegalStateException();
		}
		
		// set focus to user name field
		DeferredCommand.addCommand(new FocusCommand(getFieldPanel().getFieldGroup().getFieldWidget("userName"), true));
		
		this.mode = mode;
	}
}
