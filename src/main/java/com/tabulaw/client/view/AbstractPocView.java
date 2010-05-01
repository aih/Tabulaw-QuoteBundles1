/**
 * The Logic Lab
 * @author jpk
 * @since Feb 12, 2010
 */
package com.tabulaw.client.view;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.mvc.view.AbstractView;
import com.tabulaw.client.mvc.view.IViewInitializer;

/**
 * AbstractPocView
 * @author jpk
 * @param <I>
 */
public abstract class AbstractPocView<I extends IViewInitializer> extends AbstractView<I> implements IPocView<I> {

	/**
	 * Constructor
	 */
	public AbstractPocView() {
		super();
	}

	@Override
	protected void doInitialization(I initializer) {
		// base impl no-op
	}

	@Override
	protected void doDestroy() {
		// base impl no-op
	}

	@Override
	public Widget[] getNavColWidgets() {
		// base impl none
		return null;
	}

	protected void handleModelChange(ModelChangeEvent event) {
		// base impl no-op
	}
	
	@Override
	public final void onModelChangeEvent(ModelChangeEvent event) {
		Log.debug("View ( " + this + " ) is handling model change event: " + event.toString() + "..");
		handleModelChange(event);
	}
}
