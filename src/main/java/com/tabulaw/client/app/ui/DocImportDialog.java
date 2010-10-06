package com.tabulaw.client.app.ui;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.ui.Dialog;
import com.tabulaw.common.data.GoogleDocument;

public class DocImportDialog extends Dialog {

	// https://www.google.com/accounts/ClientLogin?Email=gtabulaw@olesiak.biz&Passwd=tabulaw&accountType=HOSTED_OR_GOOGLE&service=cl
	// https://www.google.com/accounts/AuthSubRequest?scope=https%3A%2F%2Fdocs.google.com%2Ffeeds%2F&session=1&secure=0&next=http://127.0.0.1:8888/subauth.jsp

	public DocImportDialog() {
		super(null, false);
		Poc.getGoogledocsService().getAuthKey(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
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
		System.out.println(documents);
	}
}
