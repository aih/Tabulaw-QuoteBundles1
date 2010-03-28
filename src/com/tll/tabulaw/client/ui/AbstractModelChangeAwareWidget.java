/**
 * The Logic Lab
 * @author jpk
 * @since Mar 3, 2010
 */
package com.tll.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.tll.client.model.IHasModelChangeHandlers;
import com.tll.client.model.IModelChangeHandler;
import com.tll.client.model.ModelChangeEvent;

/**
 * Base class for widgets that source and/or accept {@link ModelChangeEvent}s.
 * Implementations are free to ignore {@link ModelChangeEvent}s.
 * @author jpk
 */
public abstract class AbstractModelChangeAwareWidget extends Composite implements IHasModelChangeHandlers, IModelChangeHandler {

	private HandlerRegistration hrModelChange;

	@Override
	protected void onLoad() {
		super.onLoad();
		hrModelChange = addModelChangeHandler(ModelChangeDispatcher.get());
	}

	@Override
	protected void onUnload() {
		hrModelChange.removeHandler();
		super.onUnload();
	}

	@Override
	public HandlerRegistration addModelChangeHandler(IModelChangeHandler handler) {
		return addHandler(handler, ModelChangeEvent.TYPE);
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		Log.debug("Handling model change: " + event);
	}
}
