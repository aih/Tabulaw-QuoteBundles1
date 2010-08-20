/**
 * The Logic Lab
 * @author jpk
 * @since Feb 16, 2010
 */
package com.tabulaw.client.app.ui;

import java.util.Date;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.app.view.DocViewInitializer;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.RpcCommand;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.view.ShowViewRequest;
import com.tabulaw.client.view.ViewManager;
import com.tabulaw.common.data.rpc.DocPayload;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityFactory;

/**
 * Dialog for adding docs.
 * @author jpk
 */
public class DocCreateDialog extends Dialog implements IEditHandler<FieldGroup> {

	private final DocCreateEditPanel editPanel = new DocCreateEditPanel();

	/**
	 * Constructor
	 */
	public DocCreateDialog() {
		super(null, false);

		setText("Create Document");
		setAnimationEnabled(true);

		editPanel.addEditHandler(this);

		add(editPanel);
	}

	@Override
	public void show() {
		super.show();
		editPanel.setMode(false);
	}

	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		// persist the quote bundle
		if(event.getOp() == EditOp.SAVE) {
			FieldGroup fg = event.getContent();

			boolean isCaseDoc = fg.getFieldWidget("docTypeCase").getValue().equals(Boolean.TRUE);

			if(isCaseDoc) {
				final String caseUrl = fg.getFieldWidget("caseUrl").getValue().toString();

				new RpcCommand<DocPayload>() {

					@Override
					protected void doExecute() {
						Poc.getDocService().fetch(caseUrl, this);
					}

					@Override
					protected void handleSuccess(DocPayload result) {
						super.handleSuccess(result);
						Notifier.get().showFor(result);
						if(!result.hasErrors()) {
							// persist the new doc and propagate through app
							final DocRef persistedDoc = result.getDocRef();
							ClientModelCache.get().persist(persistedDoc, DocCreateDialog.this);

							DeferredCommand.addCommand(new Command() {

								@Override
								public void execute() {
									// show the doc (letting the model change event finish first)
									final DocViewInitializer dvi = new DocViewInitializer(persistedDoc.getModelKey());
									ViewManager.get().dispatch(new ShowViewRequest(dvi));
								}
							});
						}
					}

				}.execute();
			}
			else {
				String docTitle = fg.getFieldWidget("docTitle").getValue().toString();
				Date docDate = (Date) fg.getFieldWidget("docDate").getValue();
				final DocRef newDoc = EntityFactory.get().buildDoc(docTitle, docDate);

				new RpcCommand<DocPayload>() {

					@Override
					protected void doExecute() {
						setSource(DocCreateDialog.this);
						Poc.getUserDataService().createDoc(newDoc, null, this);
					}

					@Override
					protected void handleSuccess(DocPayload result) {
						super.handleSuccess(result);
						Notifier.get().showFor(result);
						if(!result.hasErrors()) {
							// persist the new doc and propagate through app
							final DocRef persistedDoc = result.getDocRef();
							ClientModelCache.get().persist(persistedDoc, DocCreateDialog.this);

							DeferredCommand.addCommand(new Command() {

								@Override
								public void execute() {
									// show the doc (letting the model change event finish first)
									final DocViewInitializer dvi = new DocViewInitializer(persistedDoc.getModelKey());
									ViewManager.get().dispatch(new ShowViewRequest(dvi));
								}
							});
						}
					}
				}.execute();
			}

			hide();
		}
		else if(event.getOp() == EditOp.CANCEL) {
			hide();
		}
	}
}
