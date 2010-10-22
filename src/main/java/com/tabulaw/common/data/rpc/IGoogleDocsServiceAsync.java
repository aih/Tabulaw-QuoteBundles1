package com.tabulaw.common.data.rpc;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.model.DocRef;

public interface IGoogleDocsServiceAsync {

	void getDocuments(AsyncCallback<List<GoogleDocument>> callback);

	void download(Collection<GoogleDocument> documents,
			AsyncCallback<List<DocRef>> callback);
}
