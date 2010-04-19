/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tabulaw.client.ui;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.client.Poc;
import com.tabulaw.client.model.ClientModelCache;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.client.validate.ErrorHandlerDelegate;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.msg.Msg;

/**
 * Dialog for handling quote bundles to the app.
 * @author jpk
 */
public class AddQuoteBundleDialog extends Dialog implements IEditHandler<FieldGroup> {

	private final AddQuoteBundlePanel fieldPanel = new AddQuoteBundlePanel();

	private final FieldGroupEditPanel editPanel = new FieldGroupEditPanel(fieldPanel, true, false, false);

	private HandlerRegistration mcr;

	/**
	 * Constructor
	 */
	public AddQuoteBundleDialog() {
		super(null, false);

		setText("Add Quote Bundle");
		setAnimationEnabled(true);

		// set error hander for edit panel
		// GlobalMsgPanel msgPanel = new GlobalMsgPanel();
		// BillboardValidationFeedback billboard = new
		// BillboardValidationFeedback(msgPanel);
		ErrorHandlerDelegate ehd = ErrorHandlerBuilder.build(false, true, null);
		editPanel.setErrorHandler(ehd, true);

		editPanel.addEditHandler(this);

		add(editPanel);
	}

	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		// persist the quote bundle
		if(event.getOp() == EditOp.ADD) {
			FieldGroup fieldGroup = event.getContent();
			
			String qbName = (String) fieldGroup.getFieldWidget("name").getValue();
			String qbDesc = (String) fieldGroup.getFieldWidget("description").getValue();
			
			QuoteBundle qb = new QuoteBundle(qbName, qbDesc, null);
			
			// persist
			//mQuoteBundle.setId(ClientModelCache.get().getNextId(EntityType.QUOTE_BUNDLE));
			// server-side persist
			String userId = ClientModelCache.get().getUser().getId();
			Poc.getUserDataService().addBundleForUser(userId, qb, new AsyncCallback<ModelPayload>() {
				
				@Override
				public void onSuccess(ModelPayload result) {
					if(result.hasErrors()) {
						List<Msg> msgs = result.getStatus().getMsgs();
						Notifier.get().post(msgs);
					}
					else {
						QuoteBundle persistedQuoteBundle = (QuoteBundle) result.getModel();
						ClientModelCache.get().persist(persistedQuoteBundle, AddQuoteBundleDialog.this);
						
						// default set the current quote bundle if not set yet
						Poc.setCurrentQuoteBundle(persistedQuoteBundle);
	
						// defer the hide so the model change event bubble up in the dom since
						// hide() removes the dialog from the dom
						DeferredCommand.addCommand(new Command() {
	
							@Override
							public void execute() {
								hide();
							}
						});
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					String emsg = "Failed to persist Quote Bundle.";
					Log.error(emsg, caught);
					Notifier.get().error(emsg);
				}
			});
		}
	}

	@Override
	public void hide() {
		if(mcr != null) mcr.removeHandler();
		super.hide();
	}

	@Override
	public void show() {
		super.show();
		mcr = addHandler(ModelChangeDispatcher.get(), ModelChangeEvent.TYPE);
		//editPanel.setModel(EntityFactory.get().buildQuoteBundle(null, null));
		DeferredCommand.addCommand(new FocusCommand(fieldPanel.getFocusable(), true));
	}
}
