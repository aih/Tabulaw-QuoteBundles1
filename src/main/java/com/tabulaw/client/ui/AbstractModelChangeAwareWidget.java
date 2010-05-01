/**
 * The Logic Lab
 * @author jpk
 * @since Mar 3, 2010
 */
package com.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.tabulaw.client.Poc;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;

/**
 * Base class for widgets that source and/or accept {@link ModelChangeEvent}s.
 * Implementations are free to ignore {@link ModelChangeEvent}s.
 * @author jpk
 */
public abstract class AbstractModelChangeAwareWidget extends Composite implements IModelChangeHandler {

	private HandlerRegistration hrModelChange;

	/**
	 * Makes this widget see model change events.
	 */
	public void makeModelChangeAware() {
		if(hrModelChange == null)
			hrModelChange = Poc.getPortal().addModelChangeHandler(this);
	}

	/**
	 * Makes this widget NOT see model changes events.
	 */
	public void unmakeModelChangeAware() {
		if(hrModelChange != null) {
			hrModelChange.removeHandler();
			hrModelChange = null;
		}
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		Log.debug("Handling model change: " + event);
	}
}
