/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.user.client.DeferredCommand;
import com.tll.client.ui.Dialog;
import com.tll.client.ui.FocusCommand;
import com.tll.client.ui.edit.EditEvent;
import com.tll.client.ui.edit.IEditHandler;
import com.tll.client.ui.edit.ModelEditPanel;
import com.tll.client.validate.ErrorHandlerBuilder;
import com.tll.client.validate.ErrorHandlerDelegate;
import com.tll.common.model.Model;


/**
 * Dialog for handling quote bundles to the app.
 * @author jpk
 */
public class AddQuoteBundleDialog extends Dialog implements IEditHandler<Model> {
	
	private final AddQuoteBundlePanel fieldPanel = new AddQuoteBundlePanel();
	
	private final ModelEditPanel editPanel = new ModelEditPanel(fieldPanel, true, false, false);
	
	/**
	 * Constructor
	 * @param editHandler required: handles add quote bundle edit events
	 */
	public AddQuoteBundleDialog(IEditHandler<Model> editHandler) {
		super(null, false);
		
		setText("Add Quote Bundle");
		setAnimationEnabled(true);
		
		// set error hander for edit panel
		//GlobalMsgPanel msgPanel = new GlobalMsgPanel();
		//BillboardValidationFeedback billboard = new BillboardValidationFeedback(msgPanel);
		ErrorHandlerDelegate ehd = ErrorHandlerBuilder.build(false, true, null);
		editPanel.setErrorHandler(ehd, true);
		
		editPanel.addEditHandler(this);
		editPanel.addEditHandler(editHandler);

		add(editPanel);
	}

	@Override
	public void onEdit(EditEvent<Model> event) {
		hide();
	}

	@Override
	public void show() {
		super.show();
		DeferredCommand.addCommand(new FocusCommand(fieldPanel.getFocusable(), true));
	}
}
