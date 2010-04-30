/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DeferredCommand;
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
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.QuoteBundle;

/**
 * Dialog for handling quote bundles to the app.
 * @author jpk
 */
public class AddQuoteBundleDialog extends Dialog implements IEditHandler<FieldGroup> {

	private final AddQuoteBundlePanel fieldPanel = new AddQuoteBundlePanel();

	private final FieldGroupEditPanel editPanel = new FieldGroupEditPanel("Add", null, "Cancel", null, fieldPanel);

	private HandlerRegistration mcr;

	/**
	 * Constructor
	 */
	public AddQuoteBundleDialog() {
		super(null, false);

		setText("Add Quote Bundle");
		setAnimationEnabled(true);

		editPanel.addEditHandler(this);

		add(editPanel);
	}

	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		// persist the quote bundle
		if(event.getOp() == EditOp.SAVE) {
			FieldGroup fieldGroup = event.getContent();

			// validate
			if(!fieldGroup.isValid()) return;

			String qbName = (String) fieldGroup.getFieldWidget("qbName").getValue();
			String qbDesc = (String) fieldGroup.getFieldWidget("qbDesc").getValue();

			QuoteBundle qb = EntityFactory.get().buildQuoteBundle(qbName, qbDesc);
			qb.setId(ClientModelCache.get().getNextId(EntityType.QUOTE_BUNDLE.name()));

			// default set the current quote bundle if not set yet
			if(ClientModelCache.get().getUserState().getCurrentQuoteBundleId() == null) {
				ClientModelCache.get().getUserState().setCurrentQuoteBundleId(qb.getId());
			}

			// client side persist and model change propagation
			ClientModelCache.get().persist(qb, Poc.getPortal());

			// server side persist
			ClientModelCache.get().addBundle(qb);

			hide();
		}
		else if(event.getOp() == EditOp.CANCEL) {
			hide();
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

		// set error hander for edit panel
		ErrorHandlerDelegate ehd = ErrorHandlerBuilder.build(false, true, null);
		editPanel.setErrorHandler(ehd, true);

		mcr = addHandler(ModelChangeDispatcher.get(), ModelChangeEvent.TYPE);
		// editPanel.setModel(EntityFactory.get().buildQuoteBundle(null, null));
		DeferredCommand.addCommand(new FocusCommand(fieldPanel.getFocusable(), true));
	}
}
