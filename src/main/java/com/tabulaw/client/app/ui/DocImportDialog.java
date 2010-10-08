package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.client.ui.edit.EditEvent;
import com.tabulaw.client.ui.edit.EditEvent.EditOp;
import com.tabulaw.client.ui.edit.IEditHandler;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.common.data.GoogleDocument;

public class DocImportDialog extends Dialog implements IEditHandler<FieldGroup> {

	// https://www.google.com/accounts/ClientLogin?Email=gtabulaw@olesiak.biz&Passwd=tabulaw&accountType=HOSTED_OR_GOOGLE&service=cl
	// https://www.google.com/accounts/AuthSubRequest?scope=https%3A%2F%2Fdocs.google.com%2Ffeeds%2F&session=1&secure=0&next=http://127.0.0.1:8888/subauth.jsp

	private final DocImportEditPanel importPanel = new DocImportEditPanel();

	private String authKey;

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
			doImport(authKey, importPanel.getResourceId());
		} else if (event.getOp() == EditOp.CANCEL) {
			hide();
		}
	}

	@Override
	public void show() {
		super.show();
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

	private void doImport(String authKey, String resourceId) {
		Poc.getGoogledocsService().download(authKey, resourceId,
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(Void result) {
						System.out.println("CLIENT-DOWNLOADED");
					}
				});
	}
}
