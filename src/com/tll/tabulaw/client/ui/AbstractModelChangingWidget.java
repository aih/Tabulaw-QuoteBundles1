/**
 * The Logic Lab
 * @author jpk
 * @since Mar 3, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.tll.client.model.IModelChangeHandler;
import com.tll.client.model.ModelChangeEvent;

/**
 * Base class for widgets that generate {@link ModelChangeEvent}s. Additionally,
 * this widget class implements {@link IModelChangeHandler} for convenience.
 * Implementations are free to ignore {@link ModelChangeEvent}s.
 * @author jpk
 */
public abstract class AbstractModelChangingWidget extends Composite implements IModelChangeHandler {

	private HandlerRegistration hrModelChange;

	@Override
	protected void onLoad() {
		super.onLoad();
		hrModelChange = addHandler(ModelChangeDispatcher.get(), ModelChangeEvent.TYPE);
	}

	@Override
	protected void onUnload() {
		hrModelChange.removeHandler();
		super.onUnload();
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		// base impl no-op
	}
}
