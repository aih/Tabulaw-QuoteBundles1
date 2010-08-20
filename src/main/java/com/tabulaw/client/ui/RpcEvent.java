/**
 * The Logic Lab
 * @author jpk Feb 23, 2008
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.shared.GwtEvent;

/**
 * RpcEvent - Conveys RPC send/recv/error events without additional info.
 * @author jpk
 */
public final class RpcEvent extends GwtEvent<IRpcHandler> {

	public static final com.google.gwt.event.shared.GwtEvent.Type<IRpcHandler> TYPE =
		new com.google.gwt.event.shared.GwtEvent.Type<IRpcHandler>();

	/**
	 * Type
	 * @author jpk
	 */
	public static enum Type {
		/**
		 * An RPC command was just sent.
		 */
		SENT,
		/**
		 * An RPC command was successfull and was just received.
		 */
		RECEIVED,
		/**
		 * An error occurred while trying to send the rpc request.
		 */
		SEND_ERROR,
		/**
		 * An RPC processing related error occurred.
		 */
		ERROR;
	}

	private final Type type;

	/**
	 * Constructor - Use for RPC send calls.
	 * @param type
	 */
	public RpcEvent(Type type) {
		this.type = type;
	}

	@Override
	protected void dispatch(IRpcHandler handler) {
		handler.onRpcEvent(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IRpcHandler> getAssociatedType() {
		return TYPE;
	}

	public Type getType() {
		return type;
	}
}
