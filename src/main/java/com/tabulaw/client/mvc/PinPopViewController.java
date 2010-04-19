/**
 * The Logic Lab
 * @author jpk Jan 19, 2008
 */
package com.tabulaw.client.mvc;

import com.tabulaw.client.mvc.view.IViewRequest;
import com.tabulaw.client.mvc.view.PinPopViewRequest;

/**
 * PinPopViewController - Handles {@link PinPopViewRequest}s.
 * @author jpk
 */
class PinPopViewController implements IController {

	public boolean canHandle(IViewRequest request) {
		return request instanceof PinPopViewRequest;
	}

	@Override
	public void handle(IViewRequest request) {
		final PinPopViewRequest r = (PinPopViewRequest) request;
		if(r.isPop()) {
			// pop the view...
			ViewManager.get().popCurrentView();
		}
		else {
			// pin the view (deferring it to ensure we are clear of the history "pump" since we subsequently fire a view change event)...
			ViewManager.get().pinPoppedView(r.getViewKey());
		}
	}

}
