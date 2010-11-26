package com.tabulaw.common.data.rpc;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.data.GoogleDocument;

public interface IGoogleDocsServiceAsync {

	void getDocuments(AsyncCallback<GoogleDocumentListPayload> callback);

	void download(Collection<GoogleDocument> documents,
			AsyncCallback<DocRefListPayload> callback);
}
