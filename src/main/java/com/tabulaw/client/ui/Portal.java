/**
 * The Logic Lab
 * @author jpk
 * @since Feb 11, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IView;

/**
 * This is the parent panel for {@link IView}s in the app.
 * @author jpk
 */
public class Portal extends AbstractModelChangeAwareWidget {

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
}
