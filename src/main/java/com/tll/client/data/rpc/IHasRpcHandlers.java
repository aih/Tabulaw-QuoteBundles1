/**
 * The Logic Lab
 * @author jpk
 * Feb 23, 2008
 */
package com.tll.client.data.rpc;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * IHasRpcHandlers
 * @author jpk
 */
public interface IHasRpcHandlers extends HasHandlers {

	/**
	 * Adds an rpc handler.
	 * @param handler the handler
	 * @return the handler registration
	 */
	HandlerRegistration addRpcHandler(IRpcHandler handler);
}
