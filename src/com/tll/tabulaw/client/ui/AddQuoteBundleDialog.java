/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.ui.Dialog;
import com.tll.client.ui.FocusCommand;
import com.tll.client.ui.edit.EditEvent;
import com.tll.client.ui.edit.IEditHandler;
import com.tll.client.ui.edit.IModelEditContent;
import com.tll.client.ui.edit.ModelEditPanel;
import com.tll.client.ui.edit.EditEvent.EditOp;
import com.tll.client.validate.ErrorHandlerBuilder;
import com.tll.client.validate.ErrorHandlerDelegate;
import com.tll.common.model.Model;
import com.tll.tabulaw.client.model.PocModelStore;
import com.tll.tabulaw.common.model.PocEntityType;


/**
 * Dialog for handling quote bundles to the app.
 * @author jpk
 */
public class AddQuoteBundleDialog extends Dialog implements IEditHandler<IModelEditContent> {
	
	private final AddQuoteBundlePanel fieldPanel = new AddQuoteBundlePanel();
	
	private final ModelEditPanel editPanel = new ModelEditPanel(fieldPanel, true, false, false);
	
	private HandlerRegistration mcr;

	/**
	 * Constructor
	 */
	public AddQuoteBundleDialog() {
		super(null, false);
		
		setText("Add Quote Bundle");
		setAnimationEnabled(true);
		
		// set error hander for edit panel
		//GlobalMsgPanel msgPanel = new GlobalMsgPanel();
		//BillboardValidationFeedback billboard = new BillboardValidationFeedback(msgPanel);
		ErrorHandlerDelegate ehd = ErrorHandlerBuilder.build(false, true, null);
		editPanel.setErrorHandler(ehd, true);
		
		editPanel.addEditHandler(this);

		add(editPanel);
	}

	@Override
	public void onEdit(EditEvent<IModelEditContent> event) {
		// persist the quote bundle
		if(event.getOp() == EditOp.ADD) {
			Model mQuoteBundle = event.getContent().getModel();
			PocModelStore.get().persist(mQuoteBundle, this);
		}
		// defer the hide so the model change event bubble up in the dom since
		// hide() removes the dialog from the dom
		DeferredCommand.addCommand(new Command() {
			
			@Override
			public void execute() {
				hide();
			}
		});
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
		editPanel.setModel(PocModelStore.get().create(PocEntityType.QUOTE_BUNDLE));
		DeferredCommand.addCommand(new FocusCommand(fieldPanel.getFocusable(), true));
	}
}
