package com.tabulaw.client.app.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.app.model.ClientModelCache;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.model.DocRef;

public class DocImportDialog extends Dialog implements IEditHandler<FieldGroup> {

	// https://www.google.com/accounts/ClientLogin?Email=gtabulaw@olesiak.biz&Passwd=tabulaw&accountType=HOSTED_OR_GOOGLE&service=cl
	// https://www.google.com/accounts/AuthSubRequest?scope=https%3A%2F%2Fdocs.google.com%2Ffeeds%2F&session=1&secure=0&next=http://127.0.0.1:8888/subauth.jsp

	private final DocImportEditPanel importPanel = new DocImportEditPanel();

	private String authKey;
	private int importId = 0;

	public DocImportDialog() {
		super(null, false);
		setText("Import from Google Docs");
		setAnimationEnabled(true);
		importPanel.addEditHandler(this);
		add(importPanel);
	}

	public void setGoogleDocs(List<GoogleDocument> list) {
		importPanel.setGoogleDocs(list);
	}

	@Override
	public void onEdit(EditEvent<FieldGroup> event) {
		if (event.getOp() == EditOp.SAVE) {
			Set<GoogleDocument> docs = importPanel.getValue();
			if (!docs.isEmpty()
					&& Window
							.confirm("Importing will take some time. Do you want to continue?")) {
				importPanel.setEnabled(false);
				doImport(authKey, docs);
			} else {
				hide();
			}
		} else if (event.getOp() == EditOp.CANCEL) {
			hide();
		}
	}

	@Override
	public void show() {
		super.show();
		importPanel.setEnabled(true);
		setGoogleDocs(null);
		loadDocuments();
	}

	private void loadDocuments() {
		Poc.getGoogledocsService().getAuthKey(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				DocImportDialog.this.authKey = result;
				getDocuments(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	private void getDocuments(String authKey) {
		Poc.getGoogledocsService().getDocuments(authKey,
				new AsyncCallback<List<GoogleDocument>>() {
					@Override
					public void onSuccess(List<GoogleDocument> result) {
						showDocuments(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
	}

	private void showDocuments(List<GoogleDocument> documents) {
		setGoogleDocs(documents);
	}

	private void doImport(String authKey, Collection<GoogleDocument> resourceId) {
		final int currentImportId = ++importId;
		Poc.getGoogledocsService().download(authKey, resourceId,
				new AsyncCallback<List<DocRef>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(List<DocRef> downloaded) {
						if (currentImportId == importId) {
							refreshList(downloaded);
							hide();
						}
					}
				});
	}

	private void refreshList(List<DocRef> downloaded) {
		final ArrayList<Msg> msgs = new ArrayList<Msg>(downloaded.size());
		for (DocRef doc : downloaded) {
			msgs.add(new Msg("Document: '" + doc.getTitle() + "' uploaded.",
					MsgLevel.INFO));
		}
		// persist and propagate
		ClientModelCache.get().persistAll(downloaded, this);
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				Notifier.get().post(msgs);
			}
		});
	}
}
