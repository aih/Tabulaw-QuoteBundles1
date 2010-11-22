/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Sep 1, 2007
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.shared.EventHandler;

/**
 * IRpcHandler - Listens to RPC related events.
 * @author jpk
 */
public interface IRpcHandler extends EventHandler {

	/**
	 * Fired when an RPC event occurrs.
	 * <p>
	 * <strong>NOTE: </strong>The rpc event conveys whether or not the RPC was
	 * successfull or not.
	 * @param event The rpc event
	 */
	void onRpcEvent(RpcEvent event);
}
