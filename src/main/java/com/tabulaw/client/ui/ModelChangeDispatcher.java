/**
 * The Logic Lab
 * @author jpk
 * @since Mar 2, 2010
 */
package com.tabulaw.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.Poc;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IView;

/**
 * Responsible for dispatching model change events to the appropriate widgets.
 * @author jpk
 */
public class ModelChangeDispatcher implements IModelChangeHandler {

	private static final ModelChangeDispatcher instance = new ModelChangeDispatcher();

	public static ModelChangeDispatcher get() {
		return instance;
	}

	private ModelChangeDispatcher() {
	}

	@Override
	public void onModelChangeEvent(ModelChangeEvent event) {
		// apply the model change to the nav row
		Poc.getNavRow().onModelChangeEvent(event);
		
		// apply the model change to the currently cached views
		Widget sourceWidget = (Widget) event.getSource();
		IView<?>[] views = ViewManager.get().getCachedViews();
		for(IView<?> view : views) {
			// we *DONT* notify the view the sourced the event!
			if(!DOM.isOrHasChild(view.getViewWidget().getElement(), sourceWidget.getElement())) {
				((IModelChangeHandler)view).onModelChangeEvent(event);
			}
			else {
				Log.debug("View ( " + view + " ) -sourced- : " + event);
			}
		}
	}
}
