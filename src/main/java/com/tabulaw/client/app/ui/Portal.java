/**
 * The Logic Lab
 * @author jpk
 * @since Feb 11, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tabulaw.client.app.search.IHasSearchHandlers;
import com.tabulaw.client.app.search.ISearchHandler;
import com.tabulaw.client.app.search.SearchEvent;
import com.tabulaw.client.model.IHasModelChangeHandlers;
import com.tabulaw.client.model.IModelChangeHandler;
import com.tabulaw.client.model.ModelChangeEvent;
import com.tabulaw.client.view.IView;
import com.tabulaw.client.view.ViewManager;

/**
 * This is the parent panel for {@link IView}s in the app.
 * <p>
 * This widget sources {@link ModelChangeEvent}s for the entire app.
 * @author jpk
 */
public class Portal extends Composite implements IHasModelChangeHandlers ,IHasSearchHandlers {

	private final FlowPanel pnl = new FlowPanel();

	public Portal() {
		super();
		initWidget(pnl);
	}

	public FlowPanel getPanel() {
		return pnl;
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		ViewManager.shutdown();
	}

	@Override
	public HandlerRegistration addModelChangeHandler(IModelChangeHandler handler) {
		return addHandler(handler, ModelChangeEvent.TYPE);
	}

	@Override
	public HandlerRegistration addSearchHandler(ISearchHandler handler) {
		return addHandler(handler, SearchEvent.TYPE);
	}
}
