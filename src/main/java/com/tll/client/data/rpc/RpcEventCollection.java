package com.tll.client.data.rpc;

import java.util.ArrayList;

/**
 * StatusHandlerCollection
 * @author jpk
 */
@SuppressWarnings("serial")
final class RpcEventCollection extends ArrayList<IRpcHandler> {

	public void fire(RpcEvent event) {
		for(final IRpcHandler listener : this) {
			listener.onRpcEvent(event);
		}
	}
}