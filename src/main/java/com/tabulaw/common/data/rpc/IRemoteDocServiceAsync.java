/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jpk
 */
public interface IRemoteDocServiceAsync {
	
	void search(DocSearchRequest request, AsyncCallback<DocSearchPayload> callback);
	
	void fetch(String url, AsyncCallback<DocPayload> callback);
}
