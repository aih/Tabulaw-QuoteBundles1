package com.tabulaw.common.data.rpc;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.common.data.GoogleDocument;

@RemoteServiceRelativePath(value = "googledoc")
public interface IGoogleDocsService extends RemoteService {

	String getAuthKey();

	List<GoogleDocument> getDocuments(String authKey);
	
	void download(String authKey, Collection<GoogleDocument> resourceId);
}
