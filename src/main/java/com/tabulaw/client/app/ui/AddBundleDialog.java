/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.user.client.DeferredCommand;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.model.ServerPersistApi;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.FocusCommand;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.FieldGroupEditPanel;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.QuoteBundle;

/**
 * Dialog for handling quote bundles to the app.
 * @author jpk
 */
public class AddBundleDialog extends Dialog implements IEditHandler<FieldGroup> {

	private final AddBundlePanel fieldPanel = new AddBundlePanel();

	private final FieldGroupEditPanel editPanel = new FieldGroupEditPanel("Add", null, "Cancel", null, fieldPanel);

	/**
	 * Constructor
	 */
	public AddBundleDialog() {
		super(null, false);

		setText("Add Quote Bundle");
		setAnimationEnabled(true);

		// set error hander for edit panel
		editPanel.setErrorHandler(ErrorHandlerBuilder.build(false, true, null), true);

		editPanel.addEditHandler(this);

		add(editPanel);
	}

	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		// persist the quote bundle
		if(event.getOp() == EditOp.SAVE) {
			FieldGroup fieldGroup = event.getContent();

			String qbName = (String) fieldGroup.getFieldWidget("qbName").getValue();
			String qbDesc = (String) fieldGroup.getFieldWidget("qbDesc").getValue();

			QuoteBundle qb = EntityFactory.get().buildBundle(qbName, qbDesc);
			qb.setId(ClientModelCache.get().getNextId(EntityType.QUOTE_BUNDLE.name()));

			// default set the current quote bundle if not set yet
			if(ClientModelCache.get().getUserState().getCurrentQuoteBundleId() == null) {
				ClientModelCache.get().getUserState().setCurrentQuoteBundleId(qb.getId());
			}

			// client side persist and model change propagation
			ClientModelCache.get().persist(qb, Poc.getPortal());

			// server side persist
			ServerPersistApi.get().addBundle(qb);

			hide();
		}
		else if(event.getOp() == EditOp.CANCEL) {
			hide();
		}
	}

	@Override
	public void show() {
		super.show();
		editPanel.getErrorHandler().clear();
		DeferredCommand.addCommand(new FocusCommand(fieldPanel.getFocusable(), true));
	}
}
