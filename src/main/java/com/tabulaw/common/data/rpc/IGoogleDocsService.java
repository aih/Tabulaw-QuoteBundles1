package com.tabulaw.common.data.rpc;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.common.data.GoogleDocument;
import com.tabulaw.model.DocRef;

@RemoteServiceRelativePath(value = "googledoc")
public interface IGoogleDocsService extends RemoteService {

	List<GoogleDocument> getDocuments();

	List<DocRef> download(Collection<GoogleDocument> resourceId);
}
