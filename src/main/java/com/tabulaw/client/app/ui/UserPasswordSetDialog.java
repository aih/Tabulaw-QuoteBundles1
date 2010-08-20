/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.field.UserFieldProvider;
import com.tabulaw.client.app.field.UserFieldProvider.UserUseCase;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.FocusCommand;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.GridFieldComposer;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.IFieldWidget;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.client.validate.ErrorHandlerDelegate;
import com.tabulaw.common.data.rpc.Payload;

/**
 * Dialog for handling user password rest.
 * @author jpk
 */
public class UserPasswordSetDialog extends Dialog implements IEditHandler<FieldGroup> {

	static class UserSetPasswordFieldPanel extends AbstractFieldPanel {
		
		@Override
		protected FieldGroup generateFieldGroup() {
			return new UserFieldProvider(UserUseCase.PASSWORD_SET).getFieldGroup();
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {

				@Override
				public void render(FlowPanel widget, FieldGroup fg) {
					final GridFieldComposer cmpsr = new GridFieldComposer();
					cmpsr.setCanvas(widget);
					cmpsr.addField(fg.getFieldWidget("userPswd"));
					cmpsr.addField(fg.getFieldWidget("userPswdConfirm"));
				}
			};
		}
	}
	
	private final UserSetPasswordFieldPanel fieldPanel;
	
	private final FieldGroupEditPanel editPanel;
	
	private String userId;

	/**
	 * Constructor
	 */
	public UserPasswordSetDialog() {
		super(null, false);

		fieldPanel = new UserSetPasswordFieldPanel();
		
		editPanel = new FieldGroupEditPanel("Set Password", null, "Cancel", null, fieldPanel);
		editPanel.addEditHandler(this);
		editPanel.setFieldPanel(new UserSetPasswordFieldPanel());

		add(editPanel);
	}
	
	/**
	 * Readys UI state making it display and process worthy.
	 * @param userId
	 * @param userName
	 * @param userEmail
	 */
	public void set(String userId, String userName, String userEmail) {
		if(userId == null || userName == null || userEmail == null)
			throw new NullPointerException();
		this.userId = userId;
		setText("Set password for " + userName + " (" + userEmail + ")");
	}

	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		// persist the quote bundle
		if(event.getOp() == EditOp.SAVE) {
			FieldGroup fieldGroup = event.getContent();
			hide();

			String userPswd = (String) fieldGroup.getFieldWidget("userPswd").getValue();
			Poc.getUserAdminService().setUserPassword(userId, userPswd, new AsyncCallback<Payload>() {
				
				@Override
				public void onSuccess(Payload result) {
					Notifier.get().showFor(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Notifier.get().showFor(caught);
				}
			});
		}
		else if(event.getOp() == EditOp.CANCEL) {
			hide();
		}
	}

	@Override
	public void show() {
		super.show();

		// set error hander for edit panel
		ErrorHandlerDelegate ehd = ErrorHandlerBuilder.build(false, true, null);
		editPanel.setErrorHandler(ehd, true);
		
		final IFieldWidget<?> fpassword = fieldPanel.getFieldGroup().getFieldWidget("userPswd");
		
		// TODO fix this doesn't work for lord knows why
		DeferredCommand.addCommand(new FocusCommand(fpassword.getEditable(), true));
	}
}
