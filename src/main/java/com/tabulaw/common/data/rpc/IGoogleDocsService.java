package com.tabulaw.common.data.rpc;

import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.common.data.GoogleDocument;

@RemoteServiceRelativePath(value = "googledoc.rpc")
public interface IGoogleDocsService extends RemoteService {

	GoogleDocumentListPayload getDocuments();

	DocRefListPayload download(Collection<GoogleDocument> resourceId);
}
