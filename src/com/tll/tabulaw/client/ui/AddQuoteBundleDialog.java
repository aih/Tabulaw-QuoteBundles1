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
import com.tll.client.ui.edit.EditPanel;
import com.tll.client.ui.edit.IEditHandler;
import com.tll.client.validate.ErrorHandlerBuilder;
import com.tll.client.validate.ErrorHandlerDelegate;
import com.tll.tabulaw.client.model.PocModelStore;
import com.tll.tabulaw.common.model.PocEntityType;


/**
 * Dialog for handling quote bundles to the app.
 * @author jpk
 */
public class AddQuoteBundleDialog extends Dialog implements IEditHandler {
	
	private final AddQuoteBundlePanel fieldPanel = new AddQuoteBundlePanel();
	
	private final EditPanel editPanel = new EditPanel(fieldPanel, true, false, false);
	
	/**
	 * Constructor
	 * @param editHandler required: handles add quote bundle edit events
	 */
	public AddQuoteBundleDialog(IEditHandler editHandler) {
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
	public void onEdit(EditEvent event) {
		hide();
	}

	@Override
	public void show() {
		super.show();
		editPanel.setModel(PocModelStore.get().create(PocEntityType.QUOTE_BUNDLE));
		DeferredCommand.addCommand(new FocusCommand(fieldPanel.getFocusable(), true));
	}
}
