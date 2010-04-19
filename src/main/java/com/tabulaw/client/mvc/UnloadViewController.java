/**
 * The Logic Lab
 * @author jpk Jan 19, 2008
 */
package com.tabulaw.client.mvc;

import com.tabulaw.client.mvc.view.IViewRequest;
import com.tabulaw.client.mvc.view.UnloadViewRequest;

/**
 * UnloadViewController - Handles {@link UnloadViewRequest}s.
 * @author jpk
 */
class UnloadViewController implements IController {

	public boolean canHandle(IViewRequest request) {
		return request instanceof UnloadViewRequest;
	}

	@Override
	public void handle(IViewRequest request) {
		final UnloadViewRequest r = (UnloadViewRequest) request;
		ViewManager.get().unloadView(r.getViewKey(), r.isDestroy(), r.isErradicate());
	}
}
