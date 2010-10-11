package com.tabulaw.common.data.rpc;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.data.GoogleDocument;

public interface IGoogleDocsServiceAsync {
	
	void getAuthKey(AsyncCallback<String> callback);

	void getDocuments(String authKey, AsyncCallback<List<GoogleDocument>> callback);

	void download(String authKey, Collection<GoogleDocument> documents, AsyncCallback<Void> callback);
}
