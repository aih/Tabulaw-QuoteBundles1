/**
 * The Logic Lab
 * @author jpk Aug 28, 2007
 */
package com.tabulaw.client.data.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.common.data.rpc.Payload;

/**
 * RpcCommand - Intended base class for all client-side RPC based requests.
 * <p>
 * Fires {@link RpcEvent}s on the source widget if non-<code>null</code>.
 * @author jpk
 * @param <P> payload type
 */
public abstract class RpcCommand<P extends Payload> implements AsyncCallback<P>, Command {

	/**
	 * The sourcing widget which may be <code>null</code>.
	 */
	protected Widget source;

	/**
	 * The declared ref is necessary in order to chain rpc commands.
	 */
	private AsyncCallback<P> callback = this;

	public final void setSource(Widget source) {
		this.source = source;
	}

	protected final AsyncCallback<P> getAsyncCallback() {
		return callback;
	}

	final void setAsyncCallback(AsyncCallback<P> callback) {
		this.callback = callback;
	}

	/**
	 * Does the actual RPC execution.
	 */
	protected abstract void doExecute();

	@Override
	public final void execute() {
		try {
			doExecute();
			// fire an RPC send event
			if(source != null) source.fireEvent(new RpcEvent(RpcEvent.Type.SENT));
		}
		catch(final Throwable t) {
			if(source != null) source.fireEvent(new RpcEvent(RpcEvent.Type.SEND_ERROR));
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public final void onSuccess(P result) {
		handleSuccess(result);
	}

	@Override
	public final void onFailure(Throwable caught) {
		handleFailure(caught);
	}

	/**
	 * May be overridden by sub-classes.
	 * @param result
	 */
	protected void handleSuccess(P result) {
		// fire RPC event
		if(source != null) source.fireEvent(new RpcEvent(RpcEvent.Type.RECEIVED));
		// fire status event
		//StatusEventDispatcher.get().fireEvent(new StatusEvent(result.getStatus()));
	}

	/**
	 * May be overridden by sub-classes.
	 * @param caught
	 */
	protected void handleFailure(Throwable caught) {
		GWT.log("Error in rpc payload retrieval", caught);
		// fire RPC event
		if(source != null) source.fireEvent(new RpcEvent(RpcEvent.Type.ERROR));

		// fire status event
		String msg = caught.getMessage();
		if(msg == null) msg = "An unknown RPC error occurred";
		//final Status status = new Status(msg, MsgLevel.ERROR, (Msg.MsgAttr.STATUS.flag | Msg.MsgAttr.EXCEPTION.flag));
		//StatusEventDispatcher.get().fireEvent(new StatusEvent(status));
	}

}
