/**
 * The Logic Lab
 * @author jpk
 * @since Feb 11, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.tll.client.model.ModelChangeEvent;
import com.tll.client.mvc.ViewManager;
import com.tll.client.mvc.view.IView;

/**
 * This is the parent panel for {@link IView}s in the app.
 * <p>The sole dispatcher of {@link ModelChangeEvent}s in the app.
 * @author jpk
 */
public class Portal extends FlowPanel {

	@Override
	protected void onDetach() {
		super.onDetach();
		ViewManager.shutdown();
	}
}
